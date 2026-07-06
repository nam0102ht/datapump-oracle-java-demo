package com.ntnn.oraclepump.pipeline;

import com.ntnn.oraclepump.domain.ParsedRecord;
import com.ntnn.oraclepump.routing.ShardRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShardWriterService {

    private final JdbcTemplate jdbcTemplate;
    private final ShardRouter shardRouter;

    @Transactional
    public int batchInsert(int shard, List<ParsedRecord> records) {
        var sql = """
            INSERT INTO %s (machine_id, log_timestamp, temperature, pressure, status, operator)
            VALUES (?, ?, ?, ?, ?, ?)
            """.formatted(shardRouter.tableName(shard));

        var args = records.stream()
            .map(r -> new Object[]{
                r.machineId(),
                Timestamp.valueOf(r.timestamp()),
                r.temperature(),
                r.pressure(),
                r.status(),
                r.operator()
            })
            .toList();

        return Arrays.stream(jdbcTemplate.batchUpdate(sql, args)).sum();
    }
}
