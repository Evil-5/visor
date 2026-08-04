package com.vindicator.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "assets")
public class Asset {

    @Id
    private String id;

    @Indexed(unique = true)
    private String assetId;

    private String name;
    private String type;
    private String repositoryUrl;
    private String ownerId;

    private int openCriticals;
    private int openHighs;
    private int openMediums;
    private int openLows;

    private Instant lastScannedAt;

    @Version
    private Long version;

    public Asset() {}

    public Asset(String id, String assetId, String name, String type, String repositoryUrl, String ownerId,
                 int openCriticals, int openHighs, int openMediums, int openLows, Instant lastScannedAt, Long version) {
        this.id = id;
        this.assetId = assetId;
        this.name = name;
        this.type = type;
        this.repositoryUrl = repositoryUrl;
        this.ownerId = ownerId;
        this.openCriticals = openCriticals;
        this.openHighs = openHighs;
        this.openMediums = openMediums;
        this.openLows = openLows;
        this.lastScannedAt = lastScannedAt;
        this.version = version;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRepositoryUrl() { return repositoryUrl; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public int getOpenCriticals() { return openCriticals; }
    public void setOpenCriticals(int openCriticals) { this.openCriticals = openCriticals; }

    public int getOpenHighs() { return openHighs; }
    public void setOpenHighs(int openHighs) { this.openHighs = openHighs; }

    public int getOpenMediums() { return openMediums; }
    public void setOpenMediums(int openMediums) { this.openMediums = openMediums; }

    public int getOpenLows() { return openLows; }
    public void setOpenLows(int openLows) { this.openLows = openLows; }

    public Instant getLastScannedAt() { return lastScannedAt; }
    public void setLastScannedAt(Instant lastScannedAt) { this.lastScannedAt = lastScannedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String assetId;
        private String name;
        private String type;
        private String repositoryUrl;
        private String ownerId;
        private int openCriticals;
        private int openHighs;
        private int openMediums;
        private int openLows;
        private Instant lastScannedAt;
        private Long version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder assetId(String assetId) { this.assetId = assetId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder repositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; return this; }
        public Builder ownerId(String ownerId) { this.ownerId = ownerId; return this; }
        public Builder openCriticals(int openCriticals) { this.openCriticals = openCriticals; return this; }
        public Builder openHighs(int openHighs) { this.openHighs = openHighs; return this; }
        public Builder openMediums(int openMediums) { this.openMediums = openMediums; return this; }
        public Builder openLows(int openLows) { this.openLows = openLows; return this; }
        public Builder lastScannedAt(Instant lastScannedAt) { this.lastScannedAt = lastScannedAt; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public Asset build() {
            return new Asset(id, assetId, name, type, repositoryUrl, ownerId, openCriticals, openHighs, openMediums, openLows, lastScannedAt, version);
        }
    }
}
