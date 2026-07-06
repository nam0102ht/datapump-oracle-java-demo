package com.ntnn.oraclepump.dto;

import com.ntnn.oraclepump.domain.IngestionJob;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngestionResponse {

    private Long jobId;
    private String fileName;
    private String status;
    private long rowsRead;
    private long rowsInserted;
    private long rowsFailed;
    private Long elapsedMs;

    public static IngestionResponse from(IngestionJob job) {
        return IngestionResponse.builder()
            .jobId(job.getId())
            .fileName(job.getFileName())
            .status(job.getStatus().name())
            .rowsRead(job.getRowsRead())
            .rowsInserted(job.getRowsInserted())
            .rowsFailed(job.getRowsFailed())
            .elapsedMs(job.getElapsedMs())
            .build();
    }
}
