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
public class GitleaksParser implements ScannerReportParser {

    private static final Logger log = LoggerFactory.getLogger(GitleaksParser.class);

    private final ObjectMapper objectMapper;

    public GitleaksParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String scannerName) {
        return "GITLEAKS".equalsIgnoreCase(scannerName);
    }

    @Override
    public List<SecurityFinding> parse(String asset, String rawReportJson) {
        List<SecurityFinding> findings = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(rawReportJson);
            if (!root.isArray()) {
                return findings;
            }
            for (JsonNode leak : root) {
                String ruleId = leak.path("RuleID").asText("secret-leak");
                String file = leak.path("File").asText("unknown-file");
                int line = leak.path("StartLine").asInt(1);
                String desc = "Secret detected by rule: " + ruleId + " at line " + line;

                findings.add(SecurityFinding.builder()
                        .findingId(UUID.randomUUID().toString())
                        .asset(asset)
                        .scanner("GITLEAKS")
                        .severity("CRITICAL")
                        .cveOrRuleId(ruleId)
                        .packageOrFile(file + ":" + line)
                        .installedVersion("")
                        .fixedVersion("")
                        .title("Hardcoded Secret: " + ruleId)
                        .description(desc)
                        .status("OPEN")
                        .remediationType("AI_ASSISTED")
                        .ossRemediationReference("AI Remediation Agent: Revoke secret and use environment variable")
                        .firstSeenAt(Instant.now())
                        .lastSeenAt(Instant.now())
                        .build());
            }
        } catch (Exception e) {
            log.warn("Failed to parse Gitleaks report for asset {}: {}", asset, e.getMessage());
        }
        return findings;
    }
}
