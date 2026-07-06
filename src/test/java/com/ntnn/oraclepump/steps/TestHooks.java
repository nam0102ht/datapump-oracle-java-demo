package com.ntnn.oraclepump.steps;

import com.ntnn.oraclepump.repository.IngestionErrorRepository;
import com.ntnn.oraclepump.repository.IngestionJobRepository;
import com.ntnn.oraclepump.routing.ShardRouter;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class TestHooks {

    private final JdbcTemplate jdbcTemplate;
    private final IngestionJobRepository jobRepository;
    private final IngestionErrorRepository errorRepository;

    @Before
    public void cleanDatabase() {
        for (int i = 0; i < ShardRouter.SHARD_COUNT; i++) {
            jdbcTemplate.execute(
                "DELETE FROM machine_log_shard_" + String.format("%02d", i));
        }
        errorRepository.deleteAll();
        jobRepository.deleteAll();
    }
}
