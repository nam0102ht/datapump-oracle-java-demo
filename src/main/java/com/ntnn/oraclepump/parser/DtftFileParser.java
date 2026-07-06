package com.ntnn.oraclepump.parser;

import com.ntnn.oraclepump.domain.ParsedRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Component
public class DtftFileParser {

    private static final int FIELD_COUNT = 6;

    // Expected header prefix — used to detect and skip the header line
    public boolean isHeader(String line) {
        return line != null && line.startsWith("MachineID");
    }

    public ParseResult parseLine(int lineNo, String line) {
        if (line == null || line.isBlank()) {
            return ParseResult.failure(lineNo, line, "Empty line");
        }

        String[] f = line.split("\\|", -1);
        if (f.length != FIELD_COUNT) {
            return ParseResult.failure(lineNo, line,
                "Expected " + FIELD_COUNT + " pipe-delimited fields, got " + f.length);
        }

        try {
            long machineId    = Long.parseLong(f[0].trim());
            LocalDateTime ts  = LocalDateTime.parse(f[1].trim());
            double temperature = Double.parseDouble(f[2].trim());
            double pressure    = Double.parseDouble(f[3].trim());
            String status      = f[4].trim();
            String operator    = f[5].trim();

            return ParseResult.success(ParsedRecord.builder()
                .machineId(machineId)
                .timestamp(ts)
                .temperature(temperature)
                .pressure(pressure)
                .status(status)
                .operator(operator)
                .lineNo(lineNo)
                .build());

        } catch (NumberFormatException e) {
            return ParseResult.failure(lineNo, line, "Invalid numeric field: " + e.getMessage());
        } catch (DateTimeParseException e) {
            return ParseResult.failure(lineNo, line, "Invalid timestamp (expected ISO-8601): " + e.getMessage());
        }
    }
}