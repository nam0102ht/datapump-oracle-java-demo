package com.ntnn.oraclepump.repository;

import com.ntnn.oraclepump.domain.IngestionError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionErrorRepository extends JpaRepository<IngestionError, Long> {}
