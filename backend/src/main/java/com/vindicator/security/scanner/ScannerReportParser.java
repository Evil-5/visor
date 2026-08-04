package com.vindicator.security.scanner;

import com.vindicator.security.model.SecurityFinding;

import java.util.List;

public interface ScannerReportParser {
    boolean supports(String scannerName);
    List<SecurityFinding> parse(String asset, String rawReportJson);
}
