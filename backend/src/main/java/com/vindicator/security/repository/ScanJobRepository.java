package com.vindicator.security.repository;

import com.vindicator.security.model.ScanJob;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ScanJobRepository extends MongoRepository<ScanJob, String> {
    Optional<ScanJob> findByJobId(String jobId);
    java.util.List<ScanJob> findByStatusOrderByStartedAtDesc(String status);
    Optional<ScanJob> findFirstByStatusOrderByStartedAtAsc(String status);
}
