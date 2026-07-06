package com.ntnn.oraclepump.domain;

import java.time.LocalDateTime;

public record ParsedRecord(
    long machineId,
    LocalDateTime timestamp,
    double temperature,
    double pressure,
    String status,
    String operator,
    int lineNo
) {}
