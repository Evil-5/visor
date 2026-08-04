package com.vindicator.security.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "remediation_rules")
public class RemediationRule {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ruleId;

    @Indexed
    private String cveOrRulePattern;
    private String remediationType;
    private String ossTool;
    private String recipeName;
    private boolean autoPrEnabled;

    @Version
    private Long version;

    public RemediationRule() {}

    public RemediationRule(String id, String ruleId, String cveOrRulePattern, String remediationType, String ossTool,
                           String recipeName, boolean autoPrEnabled, Long version) {
        this.id = id;
        this.ruleId = ruleId;
        this.cveOrRulePattern = cveOrRulePattern;
        this.remediationType = remediationType;
        this.ossTool = ossTool;
        this.recipeName = recipeName;
        this.autoPrEnabled = autoPrEnabled;
        this.version = version;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public String getCveOrRulePattern() { return cveOrRulePattern; }
    public void setCveOrRulePattern(String cveOrRulePattern) { this.cveOrRulePattern = cveOrRulePattern; }

    public String getRemediationType() { return remediationType; }
    public void setRemediationType(String remediationType) { this.remediationType = remediationType; }

    public String getOssTool() { return ossTool; }
    public void setOssTool(String ossTool) { this.ossTool = ossTool; }

    public String getRecipeName() { return recipeName; }
    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }

    public boolean isAutoPrEnabled() { return autoPrEnabled; }
    public void setAutoPrEnabled(boolean autoPrEnabled) { this.autoPrEnabled = autoPrEnabled; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String ruleId;
        private String cveOrRulePattern;
        private String remediationType;
        private String ossTool;
        private String recipeName;
        private boolean autoPrEnabled;
        private Long version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder ruleId(String ruleId) { this.ruleId = ruleId; return this; }
        public Builder cveOrRulePattern(String cveOrRulePattern) { this.cveOrRulePattern = cveOrRulePattern; return this; }
        public Builder remediationType(String remediationType) { this.remediationType = remediationType; return this; }
        public Builder ossTool(String ossTool) { this.ossTool = ossTool; return this; }
        public Builder recipeName(String recipeName) { this.recipeName = recipeName; return this; }
        public Builder autoPrEnabled(boolean autoPrEnabled) { this.autoPrEnabled = autoPrEnabled; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        public RemediationRule build() {
            return new RemediationRule(id, ruleId, cveOrRulePattern, remediationType, ossTool, recipeName, autoPrEnabled, version);
        }
    }
}
