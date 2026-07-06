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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
        LocalDateTime start = LocalDateTime.now();
        String fileName = file.getOriginalFilename();
        IngestionJob job = jobService.create(fileName);

        long rowsRead = 0;
        long rowsFailed = 0;
        List<IngestionError> errorBatch = new ArrayList<>(errorBatchSize);
        ShardBuffer buffer = new ShardBuffer(batchSize, shardWriter);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;

                if (lineNo == 1 && parser.isHeader(line)) {
                    continue;
                }

                rowsRead++;
                ParseResult result = parser.parseLine(lineNo, line);

                if (result.isSuccess()) {
                    int shard = router.route(result.getRecord().getMachineId());
                    buffer.add(shard, result.getRecord());
                } else {
                    rowsFailed++;
                    errorBatch.add(buildError(fileName, result));
                    if (errorBatch.size() >= errorBatchSize) {
                        errorRepository.saveAll(errorBatch);
                        errorBatch.clear();
                    }
                }
            }

            buffer.flushAll();
            if (!errorBatch.isEmpty()) {
                errorRepository.saveAll(errorBatch);
            }

        } catch (Exception e) {
            log.error("Ingestion failed for file {}: {}", fileName, e.getMessage(), e);
            jobService.fail(job.getId());
            throw new RuntimeException("Ingestion failed: " + e.getMessage(), e);
        }

        long elapsedMs = ChronoUnit.MILLIS.between(start, LocalDateTime.now());
        jobService.complete(job.getId(), rowsRead, buffer.getTotalInserted(), rowsFailed, elapsedMs);

        log.info("Ingest done — file={} read={} inserted={} failed={} elapsed={}ms",
            fileName, rowsRead, buffer.getTotalInserted(), rowsFailed, elapsedMs);

        return jobService.findById(job.getId());
    }

    private IngestionError buildError(String fileName, ParseResult result) {
        IngestionError error = new IngestionError();
        error.setFileName(fileName);
        error.setLineNo(result.getLineNo());
        error.setReason(result.getErrorReason());
        String raw = result.getRawLine();
        error.setRawData(raw != null && raw.length() > 4000 ? raw.substring(0, 4000) : raw);
        return error;
    }
}
