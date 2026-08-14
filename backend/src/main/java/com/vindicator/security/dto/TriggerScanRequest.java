package com.vindicator.security.dto;

import jakarta.validation.constraints.NotBlank;

public class TriggerScanRequest {

    @NotBlank
    private String targetDir;

    @NotBlank
    private String assetName;

    public TriggerScanRequest() {}

    public TriggerScanRequest(String targetDir, String assetName) {
        this.targetDir = targetDir;
        this.assetName = assetName;
    }

    public String getTargetDir() { return targetDir; }
    public void setTargetDir(String targetDir) { this.targetDir = targetDir; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
}
