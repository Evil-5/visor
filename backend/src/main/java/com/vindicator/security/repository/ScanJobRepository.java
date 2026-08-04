package com.vindicator.security.repository;

import com.vindicator.security.model.ScanJob;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ScanJobRepository extends MongoRepository<ScanJob, String> {
    Optional<ScanJob> findByJobId(String jobId);
}
