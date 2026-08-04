package com.vindicator.security.controller;

import com.vindicator.security.dto.RemediationSuggestionDto;
import com.vindicator.security.service.RemediationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/remediation")
public class RemediationController {

    private final RemediationService remediationService;

    public RemediationController(RemediationService remediationService) {
        this.remediationService = remediationService;
    }

    @GetMapping("/automated")
    public ResponseEntity<List<RemediationSuggestionDto>> getAutomated() {
        return ResponseEntity.ok(remediationService.getAutomatedRemediations());
    }

    @GetMapping("/ai-tasks")
    public ResponseEntity<List<RemediationSuggestionDto>> getAiTasks() {
        return ResponseEntity.ok(remediationService.getAiTasks());
    }
}
