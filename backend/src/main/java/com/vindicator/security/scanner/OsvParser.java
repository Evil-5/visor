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
public class OsvParser implements ScannerReportParser {

    private static final Logger log = LoggerFactory.getLogger(OsvParser.class);

    private final ObjectMapper objectMapper;

    public OsvParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String scannerName) {
        return "OSV-SCANNER".equalsIgnoreCase(scannerName) || "OSV".equalsIgnoreCase(scannerName);
    }

    @Override
    public List<SecurityFinding> parse(String asset, String rawReportJson) {
        List<SecurityFinding> findings = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(rawReportJson);
            JsonNode results = root.path("results");
            if (!results.isArray()) {
                return findings;
            }
            for (JsonNode res : results) {
                JsonNode packages = res.path("packages");
                if (packages.isArray()) {
                    for (JsonNode pkg : packages) {
                        String name = pkg.path("package").path("name").asText("unknown-pkg");
                        String version = pkg.path("package").path("version").asText("");
                        JsonNode vulns = pkg.path("vulnerabilities");
                        if (vulns.isArray()) {
                            for (JsonNode vuln : vulns) {
                                String id = vuln.path("id").asText("CVE-UNKNOWN");
                                String summary = vuln.path("summary").asText(id + " in " + name);

                                String remediationType = "RENOVATE_AUTO";
                                String ossRef = "Renovate Auto-PR: upgrade dependency " + name;

                                findings.add(SecurityFinding.builder()
                                        .findingId(UUID.randomUUID().toString())
                                        .asset(asset)
                                        .scanner("OSV-SCANNER")
                                        .severity("HIGH")
                                        .cveOrRuleId(id)
                                        .packageOrFile(name)
                                        .installedVersion(version)
                                        .fixedVersion("latest")
                                        .title(summary)
                                        .description(summary)
                                        .status("OPEN")
                                        .remediationType(remediationType)
                                        .ossRemediationReference(ossRef)
                                        .firstSeenAt(Instant.now())
                                        .lastSeenAt(Instant.now())
                                        .build());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse OSV report for asset {}: {}", asset, e.getMessage());
        }
        return findings;
    }
}
