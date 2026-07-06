package com.ntnn.oraclepump.pipeline;

import com.ntnn.oraclepump.domain.ParsedRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * Per-ingestion-job buffer; not a Spring bean — one instance per request.
 * Flushes each shard's buffer as an independent @Transactional commit when
 * the threshold is reached, bounding undo/redo pressure to batchSize rows.
 */
public class ShardBuffer {

    private final int batchSize;
    private final ShardWriterService writer;
    private final Map<Integer, List<ParsedRecord>> buffers = new HashMap<>();
    private final LongAdder totalInserted = new LongAdder();

    public ShardBuffer(int batchSize, ShardWriterService writer) {
        this.batchSize = batchSize;
        this.writer    = writer;
    }

    public void add(int shard, ParsedRecord record) {
        var buf = buffers.computeIfAbsent(shard, _ -> new ArrayList<>(batchSize));
        buf.add(record);
        if (buf.size() >= batchSize) {
            flush(shard, buf);
        }
    }

    public void flushAll() {
        buffers.forEach((shard, buf) -> {
            if (!buf.isEmpty()) flush(shard, buf);
        });
    }

    public long getTotalInserted() {
        return totalInserted.sum();
    }

    private void flush(int shard, List<ParsedRecord> buf) {
        totalInserted.add(writer.batchInsert(shard, List.copyOf(buf)));
        buf.clear();
    }
}
