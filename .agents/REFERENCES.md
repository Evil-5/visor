# Vindicator Security Control Plane - References

## Supported Scanners & Formats
1. **Checkov**: IaC & Dockerfile scanner (`bridgecrew/checkov:latest`). Outputs JSON report.
2. **Gitleaks**: Secrets detection (`zricethezav/gitleaks:latest`). Outputs JSON array of detected leaks.
3. **OSV-Scanner**: Dependency vulnerability scanner (`ghcr.io/google/osv-scanner:latest`). Outputs OSV JSON schema.
4. **Semgrep**: Static Analysis SAST (`returntocorp/semgrep:latest`). Outputs Semgrep JSON schema with rule IDs and severity.
5. **Trivy Code & Image**: Source code, config, and container image scanner (`aquasec/trivy:latest`). Outputs Trivy JSON report (`Results` array containing `Vulnerabilities` and `Misconfigurations`).
6. **TruffleHog**: High-entropy secret scanner (`trufflesecurity/trufflehog:latest`). Outputs JSONL or JSON report.

## Canonical Finding Schema
Each finding is normalized to:
```json
{
  "findingId": "external-uuid",
  "asset": "docfolio-service",
  "scanner": "TRIVY",
  "severity": "CRITICAL",
  "cveOrRuleId": "CVE-2024-XXXX",
  "packageOrFile": "org.springframework.boot:spring-boot-starter-web",
  "installedVersion": "3.2.0",
  "fixedVersion": "3.2.4",
  "title": "Remote Code Execution in Spring Web",
  "description": "...",
  "status": "OPEN",
  "remediationType": "RENOVATE_AUTO",
  "firstSeenAt": "2026-08-04T10:00:00Z",
  "lastSeenAt": "2026-08-04T10:00:00Z"
}
```
