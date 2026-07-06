package com.ntnn.oraclepump.service;

import com.ntnn.oraclepump.domain.IngestionError;
import com.ntnn.oraclepump.domain.IngestionJob;
import com.ntnn.oraclepump.parser.DtftFileParser;
import com.ntnn.oraclepump.parser.ParseResult;
import com.ntnn.oraclepump.pipeline.ShardBuffer;
import com.ntnn.oraclepump.pipeline.ShardWriterService;
import com.ntnn.oraclepump.repository.IngestionErrorRepository;
import com.ntnn.oraclepump.routing.ShardRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionPipelineService {

    private final DtftFileParser parser;
    private final ShardRouter router;
    private final ShardWriterService shardWriter;
    private final IngestionErrorRepository errorRepository;
    private final IngestionJobService jobService;

    @Value("${app.ingest.batch-size:1000}")
    private int batchSize;

    @Value("${app.ingest.error-batch-size:100}")
    private int errorBatchSize;

    public IngestionJob ingest(MultipartFile file) {
        var start    = Instant.now();
        var fileName = file.getOriginalFilename();
        var job      = jobService.create(fileName);

        long rowsRead   = 0;
        long rowsFailed = 0;
        var  errorBatch = new ArrayList<IngestionError>(errorBatchSize);
        var  buffer     = new ShardBuffer(batchSize, shardWriter);

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (lineNo == 1 && parser.isHeader(line)) continue;

                rowsRead++;

                // Record-pattern switch — compiler enforces exhaustiveness over the sealed hierarchy
                switch (parser.parseLine(lineNo, line)) {
                    case ParseResult.Success(var record) ->
                        buffer.add(router.route(record.machineId()), record);

                    case ParseResult.Failure f -> {
                        rowsFailed++;
                        errorBatch.add(toError(fileName, f));
                        if (errorBatch.size() >= errorBatchSize) {
                            errorRepository.saveAll(errorBatch);
                            errorBatch.clear();
                        }
                    }
                }
            }

            buffer.flushAll();
            if (!errorBatch.isEmpty()) errorRepository.saveAll(errorBatch);

        } catch (Exception e) {
            log.error("Ingestion failed — file={} error={}", fileName, e.getMessage(), e);
            jobService.fail(job.getId());
            throw new RuntimeException("Ingestion failed: " + e.getMessage(), e);
        }

        var elapsedMs = Instant.now().toEpochMilli() - start.toEpochMilli();
        jobService.complete(job.getId(), rowsRead, buffer.getTotalInserted(), rowsFailed, elapsedMs);

        log.info("Ingest done — file={} read={} inserted={} failed={} elapsed={}ms",
            fileName, rowsRead, buffer.getTotalInserted(), rowsFailed, elapsedMs);

        return jobService.findById(job.getId());
    }

    private IngestionError toError(String fileName, ParseResult.Failure f) {
        var error = new IngestionError();
        error.setFileName(fileName);
        error.setLineNo(f.lineNo());
        error.setReason(f.reason());
        var raw = f.rawLine();
        error.setRawData(raw != null && raw.length() > 4000 ? raw.substring(0, 4000) : raw);
        return error;
    }
}
