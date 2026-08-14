package com.vindicator.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "scan_jobs")
public class ScanJob {

    @Id
    private String id;

    @Indexed(unique = true)
    private String jobId;

    private String asset;
    private String targetDir;
    private List<String> scannersRun;
    private int findingsCount;
    private String status;
    private Map<String, String> scannerStatuses = new HashMap<>();

    private Instant startedAt;
    private Instant completedAt;
    private String createdBy;

    @Version
    private Long version;

    public ScanJob() {}

    public ScanJob(String id, String jobId, String asset, String targetDir, List<String> scannersRun, int findingsCount, String status, Map<String, String> scannerStatuses,
                   Instant startedAt, Instant completedAt, String createdBy, Long version) {
        this.id = id;
        this.jobId = jobId;
        this.asset = asset;
        this.targetDir = targetDir;
        this.scannersRun = scannersRun;
        this.findingsCount = findingsCount;
        this.status = status;
        this.scannerStatuses = scannerStatuses != null ? scannerStatuses : new HashMap<>();
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.createdBy = createdBy;
        this.version = version;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getTargetDir() { return targetDir; }
    public void setTargetDir(String targetDir) { this.targetDir = targetDir; }

    public List<String> getScannersRun() { return scannersRun; }
    public void setScannersRun(List<String> scannersRun) { this.scannersRun = scannersRun; }

    public int getFindingsCount() { return findingsCount; }
    public void setFindingsCount(int findingsCount) { this.findingsCount = findingsCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, String> getScannerStatuses() { return scannerStatuses; }
    public void setScannerStatuses(Map<String, String> scannerStatuses) { this.scannerStatuses = scannerStatuses; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String jobId;
        private String asset;
        private String targetDir;
        private List<String> scannersRun;
        private int findingsCount;
        private String status;
        private Map<String, String> scannerStatuses;
        private Instant startedAt;
        private Instant completedAt;
        private String createdBy;
        private Long version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder jobId(String jobId) { this.jobId = jobId; return this; }
        public Builder asset(String asset) { this.asset = asset; return this; }
        public Builder targetDir(String targetDir) { this.targetDir = targetDir; return this; }
        public Builder scannersRun(List<String> scannersRun) { this.scannersRun = scannersRun; return this; }
        public Builder findingsCount(int findingsCount) { this.findingsCount = findingsCount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder scannerStatuses(Map<String, String> scannerStatuses) { this.scannerStatuses = scannerStatuses; return this; }
        public Builder startedAt(Instant startedAt) { this.startedAt = startedAt; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public ScanJob build() {
            return new ScanJob(id, jobId, asset, targetDir, scannersRun, findingsCount, status, scannerStatuses, startedAt, completedAt, createdBy, version);
        }
    }
}
