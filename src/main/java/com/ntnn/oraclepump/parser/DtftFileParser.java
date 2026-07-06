package com.ntnn.oraclepump.parser;

import com.ntnn.oraclepump.domain.ParsedRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Component
public class DtftFileParser {

    private static final int FIELD_COUNT = 6;

    public boolean isHeader(String line) {
        return line != null && line.startsWith("MachineID");
    }

    public ParseResult parseLine(int lineNo, String line) {
        if (line == null || line.isBlank()) {
            return new ParseResult.Failure(lineNo, line, "Empty line");
        }

        var fields = line.split("\\|", -1);
        if (fields.length != FIELD_COUNT) {
            return new ParseResult.Failure(lineNo, line,
                "Expected %d pipe-delimited fields, got %d".formatted(FIELD_COUNT, fields.length));
        }

        try {
            return new ParseResult.Success(new ParsedRecord(
                Long.parseLong(fields[0].trim()),
                LocalDateTime.parse(fields[1].trim()),
                Double.parseDouble(fields[2].trim()),
                Double.parseDouble(fields[3].trim()),
                fields[4].trim(),
                fields[5].trim(),
                lineNo
            ));
        } catch (NumberFormatException e) {
            return new ParseResult.Failure(lineNo, line, "Invalid numeric field: " + e.getMessage());
        } catch (DateTimeParseException e) {
            return new ParseResult.Failure(lineNo, line, "Invalid timestamp (ISO-8601 required): " + e.getMessage());
        }
    }
}
