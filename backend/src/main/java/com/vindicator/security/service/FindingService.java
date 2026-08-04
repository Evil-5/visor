package com.vindicator.security.service;

import com.vindicator.security.dto.FindingResponse;
import com.vindicator.security.model.SecurityFinding;
import com.vindicator.security.repository.SecurityFindingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FindingService {

    private final SecurityFindingRepository findingRepository;

    public FindingService(SecurityFindingRepository findingRepository) {
        this.findingRepository = findingRepository;
    }

    public List<FindingResponse> getAllFindings(String asset, String status, String severity) {
        List<SecurityFinding> findings = findingRepository.findAll();
        return findings.stream()
                .filter(f -> asset == null || f.getAsset().equalsIgnoreCase(asset))
                .filter(f -> status == null || f.getStatus().equalsIgnoreCase(status))
                .filter(f -> severity == null || f.getSeverity().equalsIgnoreCase(severity))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FindingResponse getByFindingId(String findingId) {
        SecurityFinding finding = findingRepository.findByFindingId(findingId)
                .orElseThrow(() -> new IllegalArgumentException("Finding not found: " + findingId));
        return toResponse(finding);
    }

    public FindingResponse updateStatus(String findingId, String newStatus) {
        SecurityFinding finding = findingRepository.findByFindingId(findingId)
                .orElseThrow(() -> new IllegalArgumentException("Finding not found: " + findingId));
        finding.setStatus(newStatus.toUpperCase());
        return toResponse(findingRepository.save(finding));
    }

    private FindingResponse toResponse(SecurityFinding finding) {
        return FindingResponse.builder()
                .findingId(finding.getFindingId())
                .asset(finding.getAsset())
                .scanner(finding.getScanner())
                .severity(finding.getSeverity())
                .cveOrRuleId(finding.getCveOrRuleId())
                .packageOrFile(finding.getPackageOrFile())
                .installedVersion(finding.getInstalledVersion())
                .fixedVersion(finding.getFixedVersion())
                .title(finding.getTitle())
                .description(finding.getDescription())
                .status(finding.getStatus())
                .remediationType(finding.getRemediationType())
                .ossRemediationReference(finding.getOssRemediationReference())
                .firstSeenAt(finding.getFirstSeenAt())
                .lastSeenAt(finding.getLastSeenAt())
                .build();
    }
}
