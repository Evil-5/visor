package com.vindicator.security.scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vindicator.security.model.SecurityFinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TrufflehogParser implements ScannerReportParser {

    private static final Logger log = LoggerFactory.getLogger(TrufflehogParser.class);

    private final ObjectMapper objectMapper;

    public TrufflehogParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String scannerName) {
        return "TRUFFLEHOG".equalsIgnoreCase(scannerName);
    }

    @Override
    public List<SecurityFinding> parse(String asset, String rawReportJson) {
        List<SecurityFinding> findings = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new StringReader(rawReportJson));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || !line.startsWith("{")) {
                    continue;
                }
                JsonNode res = objectMapper.readTree(line);
                String detector = res.path("DetectorName").asText("secret-detector");
                String file = res.path("SourceMetadata").path("Data").path("Filesystem").path("file").asText("unknown-file");

                findings.add(SecurityFinding.builder()
                        .findingId(UUID.randomUUID().toString())
                        .asset(asset)
                        .scanner("TRUFFLEHOG")
                        .severity("CRITICAL")
                        .cveOrRuleId(detector)
                        .packageOrFile(file)
                        .installedVersion("")
                        .fixedVersion("")
                        .title("Verified High-Entropy Secret: " + detector)
                        .description("Verified credential leak detected in " + file)
                        .status("OPEN")
                        .remediationType("AI_ASSISTED")
                        .ossRemediationReference("AI Remediation Agent: Rotate secret immediately")
                        .firstSeenAt(Instant.now())
                        .lastSeenAt(Instant.now())
                        .build());
            }
        } catch (Exception e) {
            log.warn("Failed to parse TruffleHog report for asset {}: {}", asset, e.getMessage());
        }
        return findings;
    }
}
