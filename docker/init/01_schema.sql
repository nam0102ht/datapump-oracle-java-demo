-- Schema init for oracle-pump (runs as APP_USER in XEPDB1)

-- Sequences for JPA-managed entities
CREATE SEQUENCE ingestion_job_seq   START WITH 1 INCREMENT BY 1  NOCACHE NOCYCLE;
CREATE SEQUENCE ingestion_error_seq START WITH 1 INCREMENT BY 50 NOCACHE NOCYCLE;

-- Job tracking table
CREATE TABLE ingestion_job (
    id            NUMBER(19)   NOT NULL,
    file_name     VARCHAR2(255) NOT NULL,
    status        VARCHAR2(50)  NOT NULL,
    rows_read     NUMBER(15)    DEFAULT 0,
    rows_inserted NUMBER(15)    DEFAULT 0,
    rows_failed   NUMBER(15)    DEFAULT 0,
    current_batch NUMBER(10)    DEFAULT 0,
    current_shard NUMBER(5),
    start_time    TIMESTAMP,
    end_time      TIMESTAMP,
    elapsed_ms    NUMBER(20),
    created_at    TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_ingestion_job PRIMARY KEY (id)
);

-- Bad-record error table
CREATE TABLE ingestion_error (
    id         NUMBER(19)    NOT NULL,
    file_name  VARCHAR2(255) NOT NULL,
    line_no    NUMBER(10)    NOT NULL,
    reason     VARCHAR2(1000),
    raw_data   VARCHAR2(4000),
    created_at TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_ingestion_error PRIMARY KEY (id)
);
CREATE INDEX idx_ing_error_file ON ingestion_error(file_name);

-- Shard tables: machine_log_shard_00 .. machine_log_shard_31
-- All share the same structure; sharding key = MachineID % 32

CREATE TABLE machine_log_shard_00 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_00 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_00_mid ON machine_log_shard_00(machine_id);

CREATE TABLE machine_log_shard_01 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_01 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_01_mid ON machine_log_shard_01(machine_id);

CREATE TABLE machine_log_shard_02 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_02 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_02_mid ON machine_log_shard_02(machine_id);

CREATE TABLE machine_log_shard_03 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_03 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_03_mid ON machine_log_shard_03(machine_id);

CREATE TABLE machine_log_shard_04 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_04 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_04_mid ON machine_log_shard_04(machine_id);

CREATE TABLE machine_log_shard_05 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_05 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_05_mid ON machine_log_shard_05(machine_id);

CREATE TABLE machine_log_shard_06 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_06 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_06_mid ON machine_log_shard_06(machine_id);

CREATE TABLE machine_log_shard_07 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_07 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_07_mid ON machine_log_shard_07(machine_id);

CREATE TABLE machine_log_shard_08 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_08 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_08_mid ON machine_log_shard_08(machine_id);

CREATE TABLE machine_log_shard_09 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_09 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_09_mid ON machine_log_shard_09(machine_id);

CREATE TABLE machine_log_shard_10 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_10 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_10_mid ON machine_log_shard_10(machine_id);

CREATE TABLE machine_log_shard_11 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_11 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_11_mid ON machine_log_shard_11(machine_id);

CREATE TABLE machine_log_shard_12 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_12 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_12_mid ON machine_log_shard_12(machine_id);

CREATE TABLE machine_log_shard_13 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_13 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_13_mid ON machine_log_shard_13(machine_id);

CREATE TABLE machine_log_shard_14 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_14 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_14_mid ON machine_log_shard_14(machine_id);

CREATE TABLE machine_log_shard_15 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_15 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_15_mid ON machine_log_shard_15(machine_id);

CREATE TABLE machine_log_shard_16 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_16 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_16_mid ON machine_log_shard_16(machine_id);

CREATE TABLE machine_log_shard_17 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_17 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_17_mid ON machine_log_shard_17(machine_id);

CREATE TABLE machine_log_shard_18 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_18 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_18_mid ON machine_log_shard_18(machine_id);

CREATE TABLE machine_log_shard_19 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_19 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_19_mid ON machine_log_shard_19(machine_id);

CREATE TABLE machine_log_shard_20 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_20 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_20_mid ON machine_log_shard_20(machine_id);

CREATE TABLE machine_log_shard_21 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_21 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_21_mid ON machine_log_shard_21(machine_id);

CREATE TABLE machine_log_shard_22 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_22 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_22_mid ON machine_log_shard_22(machine_id);

CREATE TABLE machine_log_shard_23 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_23 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_23_mid ON machine_log_shard_23(machine_id);

CREATE TABLE machine_log_shard_24 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_24 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_24_mid ON machine_log_shard_24(machine_id);

CREATE TABLE machine_log_shard_25 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_25 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_25_mid ON machine_log_shard_25(machine_id);

CREATE TABLE machine_log_shard_26 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_26 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_26_mid ON machine_log_shard_26(machine_id);

CREATE TABLE machine_log_shard_27 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_27 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_27_mid ON machine_log_shard_27(machine_id);

CREATE TABLE machine_log_shard_28 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_28 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_28_mid ON machine_log_shard_28(machine_id);

CREATE TABLE machine_log_shard_29 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_29 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_29_mid ON machine_log_shard_29(machine_id);

CREATE TABLE machine_log_shard_30 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_30 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_30_mid ON machine_log_shard_30(machine_id);

CREATE TABLE machine_log_shard_31 (id NUMBER GENERATED ALWAYS AS IDENTITY CONSTRAINT pk_shard_31 PRIMARY KEY, machine_id NUMBER(12) NOT NULL, log_timestamp TIMESTAMP NOT NULL, temperature BINARY_DOUBLE, pressure BINARY_DOUBLE, status VARCHAR2(50), operator VARCHAR2(100), created_at TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL);
CREATE INDEX idx_shard_31_mid ON machine_log_shard_31(machine_id);