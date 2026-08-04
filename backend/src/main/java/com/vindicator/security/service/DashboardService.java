package com.vindicator.security.service;

import com.vindicator.security.dto.DashboardSummaryResponse;
import com.vindicator.security.dto.FindingResponse;
import com.vindicator.security.model.SecurityFinding;
import com.vindicator.security.repository.SecurityFindingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final SecurityFindingRepository findingRepository;
    private final RemediationService remediationService;

    public DashboardService(SecurityFindingRepository findingRepository, RemediationService remediationService) {
        this.findingRepository = findingRepository;
        this.remediationService = remediationService;
    }

    public DashboardSummaryResponse getSummary() {
        List<SecurityFinding> openFindings = findingRepository.findByStatus("OPEN");

        long criticals = openFindings.stream().filter(f -> "CRITICAL".equalsIgnoreCase(f.getSeverity())).count();
        long highs = openFindings.stream().filter(f -> "HIGH".equalsIgnoreCase(f.getSeverity())).count();
        long mediums = openFindings.stream().filter(f -> "MEDIUM".equalsIgnoreCase(f.getSeverity())).count();
        long lows = openFindings.stream().filter(f -> "LOW".equalsIgnoreCase(f.getSeverity()) || "INFO".equalsIgnoreCase(f.getSeverity())).count();

        long secretsCount = openFindings.stream()
                .filter(f -> "GITLEAKS".equalsIgnoreCase(f.getScanner()) || "TRUFFLEHOG".equalsIgnoreCase(f.getScanner()))
                .count();

        long iacRisksCount = openFindings.stream()
                .filter(f -> "CHECKOV".equalsIgnoreCase(f.getScanner()))
                .count();

        List<FindingResponse> recentCriticals = openFindings.stream()
                .filter(f -> "CRITICAL".equalsIgnoreCase(f.getSeverity()))
                .limit(5)
                .map(f -> FindingResponse.builder()
                        .findingId(f.getFindingId())
                        .asset(f.getAsset())
                        .scanner(f.getScanner())
                        .severity(f.getSeverity())
                        .cveOrRuleId(f.getCveOrRuleId())
                        .packageOrFile(f.getPackageOrFile())
                        .installedVersion(f.getInstalledVersion())
                        .fixedVersion(f.getFixedVersion())
                        .title(f.getTitle())
                        .description(f.getDescription())
                        .status(f.getStatus())
                        .remediationType(f.getRemediationType())
                        .ossRemediationReference(f.getOssRemediationReference())
                        .firstSeenAt(f.getFirstSeenAt())
                        .lastSeenAt(f.getLastSeenAt())
                        .build())
                .collect(Collectors.toList());

        return DashboardSummaryResponse.builder()
                .totalOpen(openFindings.size())
                .openCriticals(criticals)
                .openHighs(highs)
                .openMediums(mediums)
                .openLows(lows)
                .secretsCount(secretsCount)
                .iacRisksCount(iacRisksCount)
                .recentCriticals(recentCriticals)
                .automatedRemediations(remediationService.getAutomatedRemediations())
                .build();
    }
}
