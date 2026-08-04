package com.vindicator.security.dto;

import jakarta.validation.constraints.NotBlank;

public class IngestScanRequest {
    @NotBlank(message = "asset name is required")
    private String asset;

    @NotBlank(message = "scanner name is required (e.g. TRIVY, SEMGREP, GITLEAKS, CHECKOV, OSV, TRUFFLEHOG)")
    private String scanner;

    @NotBlank(message = "rawReportJson payload is required")
    private String rawReportJson;

    public IngestScanRequest() {}

    public IngestScanRequest(String asset, String scanner, String rawReportJson) {
        this.asset = asset;
        this.scanner = scanner;
        this.rawReportJson = rawReportJson;
    }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getScanner() { return scanner; }
    public void setScanner(String scanner) { this.scanner = scanner; }

    public String getRawReportJson() { return rawReportJson; }
    public void setRawReportJson(String rawReportJson) { this.rawReportJson = rawReportJson; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String asset;
        private String scanner;
        private String rawReportJson;

        public Builder asset(String asset) { this.asset = asset; return this; }
        public Builder scanner(String scanner) { this.scanner = scanner; return this; }
        public Builder rawReportJson(String rawReportJson) { this.rawReportJson = rawReportJson; return this; }
        public IngestScanRequest build() { return new IngestScanRequest(asset, scanner, rawReportJson); }
    }
}
