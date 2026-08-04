package com.vindicator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vindicator.security.model.SecurityFinding;
import com.vindicator.security.scanner.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScannerParserTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testTrivyParserNormalization() {
        TrivyParser parser = new TrivyParser(objectMapper);
        String sampleJson = "{\"Results\": [{\"Target\": \"pom.xml\", \"Vulnerabilities\": [{\"VulnerabilityID\": \"CVE-2023-6378\", \"PkgName\": \"logback-classic\", \"InstalledVersion\": \"1.4.11\", \"FixedVersion\": \"1.4.14\", \"Severity\": \"HIGH\", \"Title\": \"Logback RCE\"}]}]}";
        List<SecurityFinding> findings = parser.parse("docfolio", sampleJson);

        assertEquals(1, findings.size());
        SecurityFinding finding = findings.get(0);
        assertEquals("CVE-2023-6378", finding.getCveOrRuleId());
        assertEquals("logback-classic", finding.getPackageOrFile());
        assertEquals("RENOVATE_AUTO", finding.getRemediationType());
        assertTrue(finding.getOssRemediationReference().contains("1.4.14"));
    }

    @Test
    void testGitleaksParserNormalization() {
        GitleaksParser parser = new GitleaksParser(objectMapper);
        String sampleJson = "[{\"RuleID\": \"generic-api-key\", \"File\": \"src/config.ts\", \"StartLine\": 12}]";
        List<SecurityFinding> findings = parser.parse("docfolio", sampleJson);

        assertEquals(1, findings.size());
        SecurityFinding finding = findings.get(0);
        assertEquals("generic-api-key", finding.getCveOrRuleId());
        assertEquals("CRITICAL", finding.getSeverity());
        assertEquals("AI_ASSISTED", finding.getRemediationType());
    }

    @Test
    void testSemgrepParserNormalization() {
        SemgrepParser parser = new SemgrepParser(objectMapper);
        String sampleJson = "{\"results\": [{\"check_id\": \"java.spring.security.csrf\", \"path\": \"src/main/java/App.java\", \"extra\": {\"severity\": \"ERROR\", \"message\": \"Disabled CSRF\"}}]}";
        List<SecurityFinding> findings = parser.parse("docfolio", sampleJson);

        assertEquals(1, findings.size());
        SecurityFinding finding = findings.get(0);
        assertEquals("java.spring.security.csrf", finding.getCveOrRuleId());
        assertEquals("HIGH", finding.getSeverity());
        assertEquals("OPENREWRITE_AUTO", finding.getRemediationType());
    }
}
