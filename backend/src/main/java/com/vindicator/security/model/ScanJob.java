package com.vindicator.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "scan_jobs")
public class ScanJob {

    @Id
    private String id;

    @Indexed(unique = true)
    private String jobId;

    private String asset;
    private List<String> scannersRun;
    private int findingsCount;
    private String status;

    private Instant startedAt;
    private Instant completedAt;
    private String createdBy;

    @Version
    private Long version;

    public ScanJob() {}

    public ScanJob(String id, String jobId, String asset, List<String> scannersRun, int findingsCount, String status,
                   Instant startedAt, Instant completedAt, String createdBy, Long version) {
        this.id = id;
        this.jobId = jobId;
        this.asset = asset;
        this.scannersRun = scannersRun;
        this.findingsCount = findingsCount;
        this.status = status;
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

    public List<String> getScannersRun() { return scannersRun; }
    public void setScannersRun(List<String> scannersRun) { this.scannersRun = scannersRun; }

    public int getFindingsCount() { return findingsCount; }
    public void setFindingsCount(int findingsCount) { this.findingsCount = findingsCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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
        private List<String> scannersRun;
        private int findingsCount;
        private String status;
        private Instant startedAt;
        private Instant completedAt;
        private String createdBy;
        private Long version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder jobId(String jobId) { this.jobId = jobId; return this; }
        public Builder asset(String asset) { this.asset = asset; return this; }
        public Builder scannersRun(List<String> scannersRun) { this.scannersRun = scannersRun; return this; }
        public Builder findingsCount(int findingsCount) { this.findingsCount = findingsCount; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder startedAt(Instant startedAt) { this.startedAt = startedAt; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public ScanJob build() {
            return new ScanJob(id, jobId, asset, scannersRun, findingsCount, status, startedAt, completedAt, createdBy, version);
        }
    }
}
