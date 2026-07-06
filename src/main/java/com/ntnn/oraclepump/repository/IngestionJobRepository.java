package com.ntnn.oraclepump.repository;

import com.ntnn.oraclepump.domain.IngestionJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionJobRepository extends JpaRepository<IngestionJob, Long> {}
