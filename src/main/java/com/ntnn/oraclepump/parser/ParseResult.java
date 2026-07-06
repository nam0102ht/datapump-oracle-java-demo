package com.ntnn.oraclepump.parser;

import com.ntnn.oraclepump.domain.ParsedRecord;

/**
 * Sealed hierarchy so the compiler enforces exhaustive handling at every call site.
 * Switch on ParseResult without a default — adding a new subtype causes a compile error.
 */
public sealed interface ParseResult permits ParseResult.Success, ParseResult.Failure {

    record Success(ParsedRecord record) implements ParseResult {}

    record Failure(int lineNo, String rawLine, String reason) implements ParseResult {}
}
