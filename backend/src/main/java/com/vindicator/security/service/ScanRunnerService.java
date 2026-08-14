package com.vindicator.security.service;

import com.vindicator.security.dto.TriggerScanRequest;
import com.vindicator.security.model.ScanJob;
import com.vindicator.security.repository.ScanJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ScanRunnerService {

    private static final Logger log = LoggerFactory.getLogger(ScanRunnerService.class);

    private final ScanJobRepository scanJobRepository;

    @Value("${server.port:8080}")
    private String serverPort;

    public ScanRunnerService(ScanJobRepository scanJobRepository) {
        this.scanJobRepository = scanJobRepository;
    }

    public ScanJob initializeScanJob(TriggerScanRequest request) {
        String jobId = "job-" + UUID.randomUUID().toString().substring(0, 8);
        ScanJob job = ScanJob.builder()
                .jobId(jobId)
                .asset(request.getAssetName())
                .targetDir(request.getTargetDir())
                .scannersRun(Arrays.asList("CHECKOV", "GITLEAKS", "OSV-SCANNER", "SEMGREP", "TRIVY", "TRUFFLEHOG"))
                .findingsCount(0)
                .status("PENDING")
                .startedAt(Instant.now())
                .createdBy("system")
                .build();
        return scanJobRepository.save(job);
    }
}
