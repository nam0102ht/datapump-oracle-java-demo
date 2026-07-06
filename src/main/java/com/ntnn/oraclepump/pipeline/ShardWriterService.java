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
        String sql = "INSERT INTO " + shardRouter.tableName(shard) +
            " (machine_id, log_timestamp, temperature, pressure, status, operator)" +
            " VALUES (?, ?, ?, ?, ?, ?)";

        List<Object[]> args = records.stream()
            .map(r -> new Object[]{
                r.getMachineId(),
                Timestamp.valueOf(r.getTimestamp()),
                r.getTemperature(),
                r.getPressure(),
                r.getStatus(),
                r.getOperator()
            })
            .toList();

        int[] counts = jdbcTemplate.batchUpdate(sql, args);
        return Arrays.stream(counts).sum();
    }
}
