package com.ntnn.oraclepump.parser;

import com.ntnn.oraclepump.domain.ParsedRecord;
import lombok.Getter;

@Getter
public class ParseResult {

    private final ParsedRecord record;
    private final String errorReason;
    private final int lineNo;
    private final String rawLine;

    private ParseResult(ParsedRecord record, String errorReason, int lineNo, String rawLine) {
        this.record = record;
        this.errorReason = errorReason;
        this.lineNo = lineNo;
        this.rawLine = rawLine;
    }

    public static ParseResult success(ParsedRecord record) {
        return new ParseResult(record, null, record.getLineNo(), null);
    }

    public static ParseResult failure(int lineNo, String rawLine, String reason) {
        return new ParseResult(null, reason, lineNo, rawLine);
    }

    public boolean isSuccess() {
        return record != null;
    }
}