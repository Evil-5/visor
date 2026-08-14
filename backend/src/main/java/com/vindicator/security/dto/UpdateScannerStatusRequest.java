package com.vindicator.security.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateScannerStatusRequest {

    @NotBlank
    private String scannerName;

    @NotBlank
    private String status;

    public UpdateScannerStatusRequest() {}

    public UpdateScannerStatusRequest(String scannerName, String status) {
        this.scannerName = scannerName;
        this.status = status;
    }

    public String getScannerName() { return scannerName; }
    public void setScannerName(String scannerName) { this.scannerName = scannerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
