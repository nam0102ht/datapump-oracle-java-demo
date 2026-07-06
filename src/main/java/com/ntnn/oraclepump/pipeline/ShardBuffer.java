package com.ntnn.oraclepump.pipeline;

import com.ntnn.oraclepump.domain.ParsedRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Per-ingestion-job buffer; not a Spring bean — instantiated per request in the pipeline service.
 * Accumulates records per shard and flushes as individual @Transactional batch inserts
 * once the configured threshold is reached, keeping undo/redo pressure low.
 */
public class ShardBuffer {

    private final int batchSize;
    private final ShardWriterService writer;
    private final Map<Integer, List<ParsedRecord>> buffers = new HashMap<>();
    private final AtomicLong totalInserted = new AtomicLong(0);

    public ShardBuffer(int batchSize, ShardWriterService writer) {
        this.batchSize = batchSize;
        this.writer = writer;
    }

    public void add(int shard, ParsedRecord record) {
        buffers.computeIfAbsent(shard, k -> new ArrayList<>(batchSize)).add(record);
        List<ParsedRecord> buf = buffers.get(shard);
        if (buf.size() >= batchSize) {
            flush(shard, buf);
        }
    }

    public void flushAll() {
        buffers.forEach((shard, buf) -> {
            if (!buf.isEmpty()) {
                flush(shard, buf);
            }
        });
    }

    public long getTotalInserted() {
        return totalInserted.get();
    }

    private void flush(int shard, List<ParsedRecord> buf) {
        int inserted = writer.batchInsert(shard, List.copyOf(buf));
        totalInserted.addAndGet(inserted);
        buf.clear();
    }
}
