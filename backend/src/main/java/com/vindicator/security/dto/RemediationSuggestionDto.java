package com.vindicator.security.dto;

public class RemediationSuggestionDto {
    private String findingId;
    private String cveOrRuleId;
    private String packageOrFile;
    private String remediationType;
    private String ossTool;
    private String recommendationText;
    private String aiPromptContext;

    public RemediationSuggestionDto() {}

    public RemediationSuggestionDto(String findingId, String cveOrRuleId, String packageOrFile, String remediationType,
                                    String ossTool, String recommendationText, String aiPromptContext) {
        this.findingId = findingId;
        this.cveOrRuleId = cveOrRuleId;
        this.packageOrFile = packageOrFile;
        this.remediationType = remediationType;
        this.ossTool = ossTool;
        this.recommendationText = recommendationText;
        this.aiPromptContext = aiPromptContext;
    }

    public String getFindingId() { return findingId; }
    public void setFindingId(String findingId) { this.findingId = findingId; }

    public String getCveOrRuleId() { return cveOrRuleId; }
    public void setCveOrRuleId(String cveOrRuleId) { this.cveOrRuleId = cveOrRuleId; }

    public String getPackageOrFile() { return packageOrFile; }
    public void setPackageOrFile(String packageOrFile) { this.packageOrFile = packageOrFile; }

    public String getRemediationType() { return remediationType; }
    public void setRemediationType(String remediationType) { this.remediationType = remediationType; }

    public String getOssTool() { return ossTool; }
    public void setOssTool(String ossTool) { this.ossTool = ossTool; }

    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }

    public String getAiPromptContext() { return aiPromptContext; }
    public void setAiPromptContext(String aiPromptContext) { this.aiPromptContext = aiPromptContext; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String findingId;
        private String cveOrRuleId;
        private String packageOrFile;
        private String remediationType;
        private String ossTool;
        private String recommendationText;
        private String aiPromptContext;

        public Builder findingId(String findingId) { this.findingId = findingId; return this; }
        public Builder cveOrRuleId(String cveOrRuleId) { this.cveOrRuleId = cveOrRuleId; return this; }
        public Builder packageOrFile(String packageOrFile) { this.packageOrFile = packageOrFile; return this; }
        public Builder remediationType(String remediationType) { this.remediationType = remediationType; return this; }
        public Builder ossTool(String ossTool) { this.ossTool = ossTool; return this; }
        public Builder recommendationText(String recommendationText) { this.recommendationText = recommendationText; return this; }
        public Builder aiPromptContext(String aiPromptContext) { this.aiPromptContext = aiPromptContext; return this; }

        public RemediationSuggestionDto build() {
            return new RemediationSuggestionDto(findingId, cveOrRuleId, packageOrFile, remediationType, ossTool,
                    recommendationText, aiPromptContext);
        }
    }
}
