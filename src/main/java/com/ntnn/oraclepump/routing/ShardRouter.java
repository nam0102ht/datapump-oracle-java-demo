package com.ntnn.oraclepump.routing;

import org.springframework.stereotype.Component;

@Component
public class ShardRouter {

    public static final int SHARD_COUNT = 32;

    public int route(long machineId) {
        return (int) (Math.abs(machineId) % SHARD_COUNT);
    }

    public String tableName(int shard) {
        return String.format("machine_log_shard_%02d", shard);
    }
}