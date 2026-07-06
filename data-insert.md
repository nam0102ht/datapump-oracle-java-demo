This is a good system design and backend engineering exercise. However, I would not recommend using Oracle Pump (Data Pump) for this use case.

Oracle Data Pump (expdp/impdp) is designed for Oracle-to-Oracle database migration, not for importing arbitrary manufacturing log files (DTFT, CSV, TXT, XML, etc.).

For a 100 MB manufacturing export log, a production-grade pipeline would look like this.

⸻

Scenario

Input

dtft_export_20260705.dtft
Size: 100 MB
Contains
MachineID
Timestamp
Temperature
Pressure
Status
Operator
...

Destination

Oracle Sharding
shard_01
shard_02
...
shard_32

⸻

Architecture

                 DTFT File
                      │
                      ▼
             File Upload Service
                      │
                      ▼
             Validation Service
          checksum/schema/version
                      │
                      ▼
             File Parser
          stream line-by-line
                      │
        ┌─────────────┴─────────────┐
        ▼                           ▼
Batch Buffer                Bad Records
│                           │
▼                           ▼
determine shard            Error Table
│
▼
Oracle Direct Path Loader
│
▼
Oracle Sharding

⸻

Step 1 Parse the file

Never load the whole 100 MB file into memory.

Instead

BufferedReader
while(readLine()!=null){
parse()
}

or

InputStream
↓
BufferedInputStream
↓
Parser

Memory stays around

20 MB
instead of
100 MB+

⸻

Step 2 Batch

Don’t insert

1 row
1 insert

Instead

1000 rows
↓
batch insert

Example

Batch Size = 1000
100000 rows
↓
100 batches

⸻

Step 3 Determine the shard

Suppose sharding key is

MachineID
Shard
MachineID % 32

Example

MachineID
12345
↓
12345 % 32
↓
Shard 9

Maintain one buffer per shard.

Shard1
1000 rows
↓
insert
Shard2
1000 rows
↓
insert

⸻

Step 4 Oracle loading

There are several options.

Option 1 JDBC Batch (most common)

PreparedStatement
addBatch()
executeBatch()

Pros

* simple
* transactional
* good for application servers

⸻

Option 2 SQL*Loader (recommended for files)

sqlldr
control.ctl
data=logs.dtft

SQL*Loader uses Oracle Direct Path and is much faster than individual inserts.

⸻

Option 3 External Table

CREATE TABLE ext_logs
(
...
)
ORGANIZATION EXTERNAL

Oracle reads the file as if it were a table.

INSERT INTO production_table
SELECT *
FROM ext_logs;

This is extremely common for ETL.

⸻

Option 4 Direct Path API

Oracle supports Direct Path Insert.

INSERT /*+ APPEND */

This bypasses much of the buffer cache.

Much faster.

⸻

Step 5 Partition + Sharding

Suppose

32 shards

Each shard contains

Daily partitions
2026-07-01
2026-07-02
2026-07-03

Routing

MachineID
↓
Shard
↓
Date
↓
Partition

⸻

Handling bad records

Suppose

temperature = ABC

Do not stop the whole file.

Instead

good rows
↓
production table
bad rows
↓
error table

Error table

filename
line
reason
raw_data
created_at

⸻

Performance improvements

Instead of

Read
↓
Insert
↓
Read
↓
Insert

Use a producer-consumer pipeline.

Reader Thread
↓
Blocking Queue
↓
Parser Pool
↓
Shard Dispatcher
↓
Writer Pool
↓
Oracle

This allows CPU and database work to overlap.

⸻

Transaction strategy

Avoid one transaction for the entire 100 MB file.

Instead

1000 rows
↓
Commit

Benefits:

* lower undo/redo pressure
* easier recovery
* reduced lock duration

If batch 52 fails, only that batch is retried.

⸻

Monitoring

Track metrics such as:

File Name
Rows Read
Rows Inserted
Rows Failed
Rows/sec
Current Batch
Current Shard
Elapsed Time

⸻

When to use Oracle Data Pump

Use Oracle Data Pump only when importing or exporting Oracle database objects:

Oracle DB A
│
expdp
│
dump.dmp
│
impdp
▼
Oracle DB B

Examples include:

* Database migration
* Schema migration
* Table migration
* Full database backup/restore
* Moving data between Oracle environments

It is not intended for ingesting flat files such as DTFT, CSV, or manufacturing logs.

Interview discussion

If an interviewer asks how you would ingest a 100 MB manufacturing log into Oracle Sharding, a strong answer is:

I would stream the file rather than loading it into memory, validate each record, compute the shard based on the sharding key (such as MachineID), buffer records into batches (e.g., 1,000 rows), and use JDBC batch inserts, SQL*Loader, or Oracle External Tables with Direct Path inserts. I would isolate invalid records into an error table, commit per batch for recoverability, and parallelize parsing and writing with a producer-consumer pipeline. I would not use Oracle Data Pump because it is designed for Oracle database migration, not for importing flat files.