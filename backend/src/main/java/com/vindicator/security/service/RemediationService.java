package com.vindicator.security.service;

import com.vindicator.security.dto.RemediationSuggestionDto;
import com.vindicator.security.model.SecurityFinding;
import com.vindicator.security.repository.SecurityFindingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RemediationService {

    private final SecurityFindingRepository findingRepository;

    public RemediationService(SecurityFindingRepository findingRepository) {
        this.findingRepository = findingRepository;
    }

    public List<RemediationSuggestionDto> getAutomatedRemediations() {
        return findingRepository.findByStatus("OPEN").stream()
                .filter(f -> "RENOVATE_AUTO".equals(f.getRemediationType()) || "OPENREWRITE_AUTO".equals(f.getRemediationType()))
                .map(this::toSuggestion)
                .collect(Collectors.toList());
    }

    public List<RemediationSuggestionDto> getAiTasks() {
        return findingRepository.findByStatus("OPEN").stream()
                .filter(f -> "AI_ASSISTED".equals(f.getRemediationType()))
                .map(this::toSuggestion)
                .collect(Collectors.toList());
    }

    private RemediationSuggestionDto toSuggestion(SecurityFinding finding) {
        String tool = "N/A";
        String rec = finding.getOssRemediationReference();
        String promptContext = "";

        if ("RENOVATE_AUTO".equals(finding.getRemediationType())) {
            tool = "RENOVATE";
            rec = "Renovate Bot will automatically generate a pull request updating " + finding.getPackageOrFile() +
                    " from version " + finding.getInstalledVersion() + " to " + finding.getFixedVersion();
        } else if ("OPENREWRITE_AUTO".equals(finding.getRemediationType())) {
            tool = "OPENREWRITE";
            rec = "Run 'mvn rewrite:run' with OpenRewrite recipe org.openrewrite.java.security.OwaspTopTen to refactor code automatically";
        } else {
            tool = "AI_AGENT";
            promptContext = String.format("Asset: %s | Scanner: %s | Severity: %s | ID: %s | Location: %s | Issue: %s | Description: %s",
                    finding.getAsset(), finding.getScanner(), finding.getSeverity(), finding.getCveOrRuleId(),
                    finding.getPackageOrFile(), finding.getTitle(), finding.getDescription());
            rec = "AI Agent Reasoning Required: Analyze vulnerability and generate patch for " + finding.getPackageOrFile();
        }

        return RemediationSuggestionDto.builder()
                .findingId(finding.getFindingId())
                .cveOrRuleId(finding.getCveOrRuleId())
                .packageOrFile(finding.getPackageOrFile())
                .remediationType(finding.getRemediationType())
                .ossTool(tool)
                .recommendationText(rec)
                .aiPromptContext(promptContext)
                .build();
    }
}
