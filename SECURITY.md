# Security Policy — Vindicator Security Control Plane

The **Vindicator Security Control Plane** is built to secure modern software ecosystems. We take the security of our own code and infrastructure seriously.

---

## 1. Supported Versions

We provide security updates and patches for the following release branches:

| Version | Supported          |
| ------- | ------------------ |
| 1.x     | :white_check_mark: |
| < 1.0   | :x:                |

---

## 2. Reporting a Vulnerability

If you discover a potential security vulnerability in the **Vindicator Security Control Plane** (e.g., in the Spring Boot backend, REST API endpoints, MongoDB ingestion pipeline, or Angular frontend):

1. **Do not create a public GitHub Issue.** Publicly disclosure of unpatched vulnerabilities puts existing deployments at risk.
2. Send a report via email to **security@vindicator.internal** (or the repository owner Archit Singh directly via GitHub private vulnerability reporting).
3. Include:
   - A brief summary of the vulnerability and impact.
   - Steps to reproduce (proof of concept scripts or HTTP requests are highly appreciated).
   - Any relevant logs or screenshots.
4. **Response Time**: Our team will acknowledge receipt of your vulnerability report within **48 hours** and provide an estimated timeline for validation and remediation.

---

## 3. False Positives & Scanner Exclusion Rules

If a security scanner (Trivy, Semgrep, Gitleaks, Checkov, OSV-Scanner, TruffleHog) reports a false positive in *your own* repository when using our scan runner:
- Please do not submit a security vulnerability report.
- Instead, open a standard Pull Request or Issue to propose improvements to our centralized ignore configs under `configs/` (`gitleaks.toml`, `.gitleaksignore`, `trufflehog-ignore.txt`, etc.).
