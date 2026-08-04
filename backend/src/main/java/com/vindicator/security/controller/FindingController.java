package com.vindicator.security.controller;

import com.vindicator.security.dto.FindingResponse;
import com.vindicator.security.service.FindingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/findings")
public class FindingController {

    private final FindingService findingService;

    public FindingController(FindingService findingService) {
        this.findingService = findingService;
    }

    @GetMapping
    public ResponseEntity<List<FindingResponse>> getFindings(
            @RequestParam(required = false) String asset,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity) {
        return ResponseEntity.ok(findingService.getAllFindings(asset, status, severity));
    }

    @GetMapping("/{findingId}")
    public ResponseEntity<FindingResponse> getFinding(@PathVariable String findingId) {
        return ResponseEntity.ok(findingService.getByFindingId(findingId));
    }

    @PatchMapping("/{findingId}/status")
    public ResponseEntity<FindingResponse> updateStatus(@PathVariable String findingId, @RequestParam String status) {
        return ResponseEntity.ok(findingService.updateStatus(findingId, status));
    }
}
