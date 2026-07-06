package com.ntnn.oraclepump.dto;

import com.ntnn.oraclepump.domain.IngestionJob;

public record IngestionResponse(
    Long jobId,
    String fileName,
    String status,
    long rowsRead,
    long rowsInserted,
    long rowsFailed,
    Long elapsedMs
) {
    public static IngestionResponse from(IngestionJob job) {
        return new IngestionResponse(
            job.getId(),
            job.getFileName(),
            job.getStatus().name(),
            job.getRowsRead(),
            job.getRowsInserted(),
            job.getRowsFailed(),
            job.getElapsedMs()
        );
    }
}
