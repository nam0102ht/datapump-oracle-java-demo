package com.ntnn.oraclepump.domain;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ParsedRecord {
    private long machineId;
    private LocalDateTime timestamp;
    private Double temperature;
    private Double pressure;
    private String status;
    private String operator;
    private int lineNo;
}