package com.ntnn.oraclepump.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "INGESTION_ERROR")
@Data
public class IngestionError {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingestion_error_seq")
    @SequenceGenerator(name = "ingestion_error_seq", sequenceName = "INGESTION_ERROR_SEQ", allocationSize = 50)
    private Long id;

    @Column(name = "FILE_NAME", nullable = false, length = 255)
    private String fileName;

    @Column(name = "LINE_NO", nullable = false)
    private int lineNo;

    @Column(name = "REASON", length = 1000)
    private String reason;

    @Column(name = "RAW_DATA", length = 4000)
    private String rawData;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}