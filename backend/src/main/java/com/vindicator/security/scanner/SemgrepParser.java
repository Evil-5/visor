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
public class SemgrepParser implements ScannerReportParser {

    private static final Logger log = LoggerFactory.getLogger(SemgrepParser.class);

    private final ObjectMapper objectMapper;

    public SemgrepParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String scannerName) {
        return "SEMGREP".equalsIgnoreCase(scannerName);
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
                String checkId = res.path("check_id").asText("semgrep.rule");
                String path = res.path("path").asText("unknown-file");
                String message = res.path("extra").path("message").asText(checkId);
                String rawSev = res.path("extra").path("severity").asText("WARNING");
                String severity = "MEDIUM";
                if ("ERROR".equalsIgnoreCase(rawSev)) {
                    severity = "HIGH";
                } else if ("INFO".equalsIgnoreCase(rawSev)) {
                    severity = "LOW";
                }

                String remediationType = "AI_ASSISTED";
                String ossRef = "AI Remediation Agent Analysis Needed";
                if (checkId.contains("java") || checkId.contains("spring") || checkId.contains("owasp")) {
                    remediationType = "OPENREWRITE_AUTO";
                    ossRef = "OpenRewrite recipe org.openrewrite.java.security.OwaspTopTen";
                }

                findings.add(SecurityFinding.builder()
                        .findingId(UUID.randomUUID().toString())
                        .asset(asset)
                        .scanner("SEMGREP")
                        .severity(severity)
                        .cveOrRuleId(checkId)
                        .packageOrFile(path)
                        .installedVersion("")
                        .fixedVersion("")
                        .title(checkId + " in " + path)
                        .description(message)
                        .status("OPEN")
                        .remediationType(remediationType)
                        .ossRemediationReference(ossRef)
                        .firstSeenAt(Instant.now())
                        .lastSeenAt(Instant.now())
                        .build());
            }
        } catch (Exception e) {
            log.warn("Failed to parse Semgrep report for asset {}: {}", asset, e.getMessage());
        }
        return findings;
    }
}
