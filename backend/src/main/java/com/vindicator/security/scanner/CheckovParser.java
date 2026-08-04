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
public class CheckovParser implements ScannerReportParser {

    private static final Logger log = LoggerFactory.getLogger(CheckovParser.class);

    private final ObjectMapper objectMapper;

    public CheckovParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String scannerName) {
        return "CHECKOV".equalsIgnoreCase(scannerName);
    }

    @Override
    public List<SecurityFinding> parse(String asset, String rawReportJson) {
        List<SecurityFinding> findings = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(rawReportJson);
            List<JsonNode> resultNodes = new ArrayList<>();
            if (root.isArray()) {
                root.forEach(resultNodes::add);
            } else {
                resultNodes.add(root);
            }
            for (JsonNode resNode : resultNodes) {
                JsonNode failedChecks = resNode.path("results").path("failed_checks");
                if (failedChecks.isArray()) {
                    for (JsonNode check : failedChecks) {
                        String checkId = check.path("check_id").asText("CKV_UNKNOWN");
                        String checkName = check.path("check_name").asText(checkId);
                        String filePath = check.path("file_path").asText("unknown-file");
                        String resource = check.path("resource").asText(filePath);

                        findings.add(SecurityFinding.builder()
                                .findingId(UUID.randomUUID().toString())
                                .asset(asset)
                                .scanner("CHECKOV")
                                .severity("HIGH")
                                .cveOrRuleId(checkId)
                                .packageOrFile(filePath + " (" + resource + ")")
                                .installedVersion("")
                                .fixedVersion("")
                                .title("IaC Misconfiguration: " + checkId)
                                .description(checkName)
                                .status("OPEN")
                                .remediationType("AI_ASSISTED")
                                .ossRemediationReference("AI Remediation Agent: IaC Terraform/Dockerfile fix required")
                                .firstSeenAt(Instant.now())
                                .lastSeenAt(Instant.now())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse Checkov report for asset {}: {}", asset, e.getMessage());
        }
        return findings;
    }
}
