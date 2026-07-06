package com.ntnn.oraclepump.controller;

import com.ntnn.oraclepump.dto.IngestionResponse;
import com.ntnn.oraclepump.service.IngestionJobService;
import com.ntnn.oraclepump.service.IngestionPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ingest")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionPipelineService pipelineService;
    private final IngestionJobService jobService;

    @PostMapping
    public ResponseEntity<IngestionResponse> ingest(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(IngestionResponse.from(pipelineService.ingest(file)));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<IngestionResponse> status(@PathVariable Long jobId) {
        return ResponseEntity.ok(IngestionResponse.from(jobService.findById(jobId)));
    }
}
