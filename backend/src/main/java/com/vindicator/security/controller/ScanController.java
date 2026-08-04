package com.vindicator.security.controller;

import com.vindicator.security.dto.IngestScanRequest;
import com.vindicator.security.model.ScanJob;
import com.vindicator.security.service.ScanIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scans")
public class ScanController {

    private final ScanIngestionService ingestionService;

    public ScanController(ScanIngestionService ingestionService) {
        this.ingestionService = ingestionService;
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
}
