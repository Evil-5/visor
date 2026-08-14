package com.vindicator.security.controller;

import com.vindicator.security.dto.IngestScanRequest;
import com.vindicator.security.dto.TriggerScanRequest;
import com.vindicator.security.dto.UpdateScannerStatusRequest;
import com.vindicator.security.model.ScanJob;
import com.vindicator.security.service.ScanIngestionService;
import com.vindicator.security.service.ScanRunnerService;
import com.vindicator.security.repository.ScanJobRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/scans")
public class ScanController {

    private final ScanIngestionService ingestionService;
    private final ScanRunnerService scanRunnerService;
    private final ScanJobRepository scanJobRepository;

    public ScanController(ScanIngestionService ingestionService, ScanRunnerService scanRunnerService, ScanJobRepository scanJobRepository) {
        this.ingestionService = ingestionService;
        this.scanRunnerService = scanRunnerService;
        this.scanJobRepository = scanJobRepository;
    }

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingestScan(@Valid @RequestBody IngestScanRequest request) {
        ScanJob job = ingestionService.ingest(request);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("jobId", job.getJobId());
        response.put("asset", job.getAsset());
        response.put("findingsIngested", job.getFindingsCount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerScan(@Valid @RequestBody TriggerScanRequest request) {
        ScanJob job = scanRunnerService.initializeScanJob(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ACCEPTED");
        response.put("jobId", job.getJobId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/jobs/active")
    public ResponseEntity<List<ScanJob>> getActiveJobs() {
        return ResponseEntity.ok(scanJobRepository.findByStatusOrderByStartedAtDesc("IN_PROGRESS"));
    }

    @GetMapping("/jobs/pending")
    public ResponseEntity<ScanJob> getPendingJob() {
        Optional<ScanJob> jobOpt = scanJobRepository.findFirstByStatusOrderByStartedAtAsc("PENDING");
        if (jobOpt.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ScanJob job = jobOpt.get();
        job.setStatus("IN_PROGRESS");
        scanJobRepository.save(job);
        return ResponseEntity.ok(job);
    }

    @PatchMapping("/jobs/{jobId}/status")
    public ResponseEntity<Map<String, String>> updateScannerStatus(
            @PathVariable String jobId,
            @Valid @RequestBody UpdateScannerStatusRequest request) {
        
        Optional<ScanJob> jobOpt = scanJobRepository.findByJobId(jobId);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ScanJob job = jobOpt.get();
        job.getScannerStatuses().put(request.getScannerName(), request.getStatus());
        scanJobRepository.save(job);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "UPDATED");
        return ResponseEntity.ok(response);
    }
}
