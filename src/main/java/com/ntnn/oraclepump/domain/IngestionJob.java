package com.ntnn.oraclepump.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "INGESTION_JOB")
@Data
public class IngestionJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingestion_job_seq")
    @SequenceGenerator(name = "ingestion_job_seq", sequenceName = "INGESTION_JOB_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "FILE_NAME", nullable = false, length = 255)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 50)
    private IngestionJobStatus status;

    @Column(name = "ROWS_READ")
    private long rowsRead;

    @Column(name = "ROWS_INSERTED")
    private long rowsInserted;

    @Column(name = "ROWS_FAILED")
    private long rowsFailed;

    @Column(name = "CURRENT_BATCH")
    private int currentBatch;

    @Column(name = "CURRENT_SHARD")
    private Integer currentShard;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "ELAPSED_MS")
    private Long elapsedMs;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}