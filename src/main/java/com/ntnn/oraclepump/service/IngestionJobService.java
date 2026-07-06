package com.ntnn.oraclepump.service;

import com.ntnn.oraclepump.domain.IngestionJob;
import com.ntnn.oraclepump.domain.IngestionJobStatus;
import com.ntnn.oraclepump.repository.IngestionJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IngestionJobService {

    private final IngestionJobRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public IngestionJob create(String fileName) {
        IngestionJob job = new IngestionJob();
        job.setFileName(fileName);
        job.setStatus(IngestionJobStatus.RUNNING);
        job.setStartTime(LocalDateTime.now());
        return repository.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(Long jobId, long rowsRead, long rowsInserted, long rowsFailed, long elapsedMs) {
        repository.findById(jobId).ifPresent(job -> {
            job.setStatus(IngestionJobStatus.COMPLETED);
            job.setRowsRead(rowsRead);
            job.setRowsInserted(rowsInserted);
            job.setRowsFailed(rowsFailed);
            job.setElapsedMs(elapsedMs);
            job.setEndTime(LocalDateTime.now());
            repository.save(job);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(Long jobId) {
        repository.findById(jobId).ifPresent(job -> {
            job.setStatus(IngestionJobStatus.FAILED);
            job.setEndTime(LocalDateTime.now());
            repository.save(job);
        });
    }

    @Transactional(readOnly = true)
    public IngestionJob findById(Long jobId) {
        return repository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
    }
}
