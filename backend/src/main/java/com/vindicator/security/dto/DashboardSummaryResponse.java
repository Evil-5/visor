package com.vindicator.security.dto;

import java.util.List;

public class DashboardSummaryResponse {
    private long totalOpen;
    private long openCriticals;
    private long openHighs;
    private long openMediums;
    private long openLows;
    private long secretsCount;
    private long iacRisksCount;
    private List<FindingResponse> recentCriticals;
    private List<RemediationSuggestionDto> automatedRemediations;

    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(long totalOpen, long openCriticals, long openHighs, long openMediums, long openLows,
                                    long secretsCount, long iacRisksCount, List<FindingResponse> recentCriticals,
                                    List<RemediationSuggestionDto> automatedRemediations) {
        this.totalOpen = totalOpen;
        this.openCriticals = openCriticals;
        this.openHighs = openHighs;
        this.openMediums = openMediums;
        this.openLows = openLows;
        this.secretsCount = secretsCount;
        this.iacRisksCount = iacRisksCount;
        this.recentCriticals = recentCriticals;
        this.automatedRemediations = automatedRemediations;
    }

    public long getTotalOpen() { return totalOpen; }
    public void setTotalOpen(long totalOpen) { this.totalOpen = totalOpen; }

    public long getOpenCriticals() { return openCriticals; }
    public void setOpenCriticals(long openCriticals) { this.openCriticals = openCriticals; }

    public long getOpenHighs() { return openHighs; }
    public void setOpenHighs(long openHighs) { this.openHighs = openHighs; }

    public long getOpenMediums() { return openMediums; }
    public void setOpenMediums(long openMediums) { this.openMediums = openMediums; }

    public long getOpenLows() { return openLows; }
    public void setOpenLows(long openLows) { this.openLows = openLows; }

    public long getSecretsCount() { return secretsCount; }
    public void setSecretsCount(long secretsCount) { this.secretsCount = secretsCount; }

    public long getIacRisksCount() { return iacRisksCount; }
    public void setIacRisksCount(long iacRisksCount) { this.iacRisksCount = iacRisksCount; }

    public List<FindingResponse> getRecentCriticals() { return recentCriticals; }
    public void setRecentCriticals(List<FindingResponse> recentCriticals) { this.recentCriticals = recentCriticals; }

    public List<RemediationSuggestionDto> getAutomatedRemediations() { return automatedRemediations; }
    public void setAutomatedRemediations(List<RemediationSuggestionDto> automatedRemediations) { this.automatedRemediations = automatedRemediations; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalOpen;
        private long openCriticals;
        private long openHighs;
        private long openMediums;
        private long openLows;
        private long secretsCount;
        private long iacRisksCount;
        private List<FindingResponse> recentCriticals;
        private List<RemediationSuggestionDto> automatedRemediations;

        public Builder totalOpen(long totalOpen) { this.totalOpen = totalOpen; return this; }
        public Builder openCriticals(long openCriticals) { this.openCriticals = openCriticals; return this; }
        public Builder openHighs(long openHighs) { this.openHighs = openHighs; return this; }
        public Builder openMediums(long openMediums) { this.openMediums = openMediums; return this; }
        public Builder openLows(long openLows) { this.openLows = openLows; return this; }
        public Builder secretsCount(long secretsCount) { this.secretsCount = secretsCount; return this; }
        public Builder iacRisksCount(long iacRisksCount) { this.iacRisksCount = iacRisksCount; return this; }
        public Builder recentCriticals(List<FindingResponse> recentCriticals) { this.recentCriticals = recentCriticals; return this; }
        public Builder automatedRemediations(List<RemediationSuggestionDto> automatedRemediations) { this.automatedRemediations = automatedRemediations; return this; }

        public DashboardSummaryResponse build() {
            return new DashboardSummaryResponse(totalOpen, openCriticals, openHighs, openMediums, openLows,
                    secretsCount, iacRisksCount, recentCriticals, automatedRemediations);
        }
    }
}
