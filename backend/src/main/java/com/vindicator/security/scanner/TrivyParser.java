package com.vindicator.security.scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vindicator.security.model.SecurityFinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TrivyParser implements ScannerReportParser {

    private static final Logger log = LoggerFactory.getLogger(TrivyParser.class);

    private final ObjectMapper objectMapper;

    public TrivyParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String scannerName) {
        return "TRIVY".equalsIgnoreCase(scannerName) || "TRIVY_IMAGE".equalsIgnoreCase(scannerName);
    }

    @Override
    public List<SecurityFinding> parse(String asset, String rawReportJson) {
        List<SecurityFinding> findings = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(rawReportJson);
            JsonNode results = root.path("Results");
            if (!results.isArray()) {
                return findings;
            }
            for (JsonNode result : results) {
                String target = result.path("Target").asText("unknown-target");
                JsonNode vulns = result.path("Vulnerabilities");
                if (vulns.isArray()) {
                    for (JsonNode vuln : vulns) {
                        String cveId = vuln.path("VulnerabilityID").asText("CVE-UNKNOWN");
                        String pkgName = vuln.path("PkgName").asText(target);
                        String installedVer = vuln.path("InstalledVersion").asText("");
                        String fixedVer = vuln.path("FixedVersion").asText("");
                        String severity = vuln.path("Severity").asText("MEDIUM").toUpperCase();
                        String title = vuln.path("Title").asText(cveId + " in " + pkgName);
                        String desc = vuln.path("Description").asText(title);

                        String remediationType = "AI_ASSISTED";
                        String ossRef = "AI Remediation Agent Analysis Needed";
                        if (!fixedVer.isEmpty()) {
                            remediationType = "RENOVATE_AUTO";
                            ossRef = "Renovate Auto-PR: upgrade " + pkgName + " to " + fixedVer;
                        } else if (pkgName.contains("spring") || pkgName.contains("java")) {
                            remediationType = "OPENREWRITE_AUTO";
                            ossRef = "OpenRewrite recipe org.openrewrite.java.security.OwaspTopTen";
                        }

                        findings.add(SecurityFinding.builder()
                                .findingId(UUID.randomUUID().toString())
                                .asset(asset)
                                .scanner("TRIVY")
                                .severity(severity)
                                .cveOrRuleId(cveId)
                                .packageOrFile(pkgName)
                                .installedVersion(installedVer)
                                .fixedVersion(fixedVer)
                                .title(title)
                                .description(desc)
                                .status("OPEN")
                                .remediationType(remediationType)
                                .ossRemediationReference(ossRef)
                                .firstSeenAt(Instant.now())
                                .lastSeenAt(Instant.now())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse Trivy report for asset {}: {}", asset, e.getMessage());
        }
        return findings;
    }
}
