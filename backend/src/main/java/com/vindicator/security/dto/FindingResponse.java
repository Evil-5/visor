package com.vindicator.security.dto;

import java.time.Instant;

public class FindingResponse {
    private String findingId;
    private String asset;
    private String scanner;
    private String severity;
    private String cveOrRuleId;
    private String packageOrFile;
    private String installedVersion;
    private String fixedVersion;
    private String title;
    private String description;
    private String status;
    private String remediationType;
    private String ossRemediationReference;
    private Instant firstSeenAt;
    private Instant lastSeenAt;

    public FindingResponse() {}

    public FindingResponse(String findingId, String asset, String scanner, String severity, String cveOrRuleId,
                           String packageOrFile, String installedVersion, String fixedVersion, String title,
                           String description, String status, String remediationType, String ossRemediationReference,
                           Instant firstSeenAt, Instant lastSeenAt) {
        this.findingId = findingId;
        this.asset = asset;
        this.scanner = scanner;
        this.severity = severity;
        this.cveOrRuleId = cveOrRuleId;
        this.packageOrFile = packageOrFile;
        this.installedVersion = installedVersion;
        this.fixedVersion = fixedVersion;
        this.title = title;
        this.description = description;
        this.status = status;
        this.remediationType = remediationType;
        this.ossRemediationReference = ossRemediationReference;
        this.firstSeenAt = firstSeenAt;
        this.lastSeenAt = lastSeenAt;
    }

    public String getFindingId() { return findingId; }
    public void setFindingId(String findingId) { this.findingId = findingId; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getScanner() { return scanner; }
    public void setScanner(String scanner) { this.scanner = scanner; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getCveOrRuleId() { return cveOrRuleId; }
    public void setCveOrRuleId(String cveOrRuleId) { this.cveOrRuleId = cveOrRuleId; }

    public String getPackageOrFile() { return packageOrFile; }
    public void setPackageOrFile(String packageOrFile) { this.packageOrFile = packageOrFile; }

    public String getInstalledVersion() { return installedVersion; }
    public void setInstalledVersion(String installedVersion) { this.installedVersion = installedVersion; }

    public String getFixedVersion() { return fixedVersion; }
    public void setFixedVersion(String fixedVersion) { this.fixedVersion = fixedVersion; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemediationType() { return remediationType; }
    public void setRemediationType(String remediationType) { this.remediationType = remediationType; }

    public String getOssRemediationReference() { return ossRemediationReference; }
    public void setOssRemediationReference(String ossRemediationReference) { this.ossRemediationReference = ossRemediationReference; }

    public Instant getFirstSeenAt() { return firstSeenAt; }
    public void setFirstSeenAt(Instant firstSeenAt) { this.firstSeenAt = firstSeenAt; }

    public Instant getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(Instant lastSeenAt) { this.lastSeenAt = lastSeenAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String findingId;
        private String asset;
        private String scanner;
        private String severity;
        private String cveOrRuleId;
        private String packageOrFile;
        private String installedVersion;
        private String fixedVersion;
        private String title;
        private String description;
        private String status;
        private String remediationType;
        private String ossRemediationReference;
        private Instant firstSeenAt;
        private Instant lastSeenAt;

        public Builder findingId(String findingId) { this.findingId = findingId; return this; }
        public Builder asset(String asset) { this.asset = asset; return this; }
        public Builder scanner(String scanner) { this.scanner = scanner; return this; }
        public Builder severity(String severity) { this.severity = severity; return this; }
        public Builder cveOrRuleId(String cveOrRuleId) { this.cveOrRuleId = cveOrRuleId; return this; }
        public Builder packageOrFile(String packageOrFile) { this.packageOrFile = packageOrFile; return this; }
        public Builder installedVersion(String installedVersion) { this.installedVersion = installedVersion; return this; }
        public Builder fixedVersion(String fixedVersion) { this.fixedVersion = fixedVersion; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder remediationType(String remediationType) { this.remediationType = remediationType; return this; }
        public Builder ossRemediationReference(String ossRemediationReference) { this.ossRemediationReference = ossRemediationReference; return this; }
        public Builder firstSeenAt(Instant firstSeenAt) { this.firstSeenAt = firstSeenAt; return this; }
        public Builder lastSeenAt(Instant lastSeenAt) { this.lastSeenAt = lastSeenAt; return this; }

        public FindingResponse build() {
            return new FindingResponse(findingId, asset, scanner, severity, cveOrRuleId, packageOrFile,
                    installedVersion, fixedVersion, title, description, status, remediationType,
                    ossRemediationReference, firstSeenAt, lastSeenAt);
        }
    }
}
