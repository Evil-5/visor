# Roadmap

# Security Control Plane - Simplified Flow

```text
Code Push
    ↓
Security Scans
    ↓
Normalize Findings
    ↓
Store Findings
    ↓
Dashboard
    ↓
Remediation Engine
    ↓
Patch Generation
    ↓
Validation
    ↓
Pull Request
```


---

# 1. Security Scans

### Purpose

Run all security tools and generate reports.

### Tools

#### Source Code

* Semgrep (SAST)
* Trivy Code
* OSV Scanner

#### Secrets

* Gitleaks
* TruffleHog

#### Infrastructure

* Checkov

#### Containers

* Trivy Image

#### Supply Chain

* Syft (SBOM)

#### Runtime (Future)

* Docker Bench
* Lynis

### Output

```text
semgrep-report.json
trivy-report.json
osv-report.json
...
```

No AI involved.


---

# 2. Normalize Findings

### Purpose

Convert different scanner formats into one common format.

Example:

```text
Trivy
Semgrep
Checkov
    ↓
Common Finding Model
```

Output:

```json
{
  "asset":"outline",
  "severity":"HIGH",
  "package":"openssl",
  "cve":"CVE-XXXX"
}
```

No AI involved.


---

# 3. Store Findings

### Purpose

Maintain historical vulnerability database.

Store:

* Assets
* Findings
* CVEs
* Packages
* Status

Benefits:

* history
* deduplication
* reporting
* AI-ready data

No AI involved.


---

# 4. Dashboard

### Purpose

Visualize security posture.

Views:

* Open Criticals
* Open Highs
* Secrets
* Infrastructure Risks
* Production Risks

No AI involved.


---

# 5. Remediation Engine

### Purpose

Determine how a finding should be fixed.

### Rule-Based Fixes (No AI)

Example:

```text
openssl 3.0.2
    ↓
fixed in
    ↓
openssl 3.0.15
```

Recommendation:

```text
Upgrade openssl to 3.0.15
```

Most vulnerabilities fall into this category.


---

# 6. Renovate Bot

### Purpose

Automatically update dependencies.

Examples:

```text
Spring 3.5.0 → 3.5.1
Angular 20.1 → 20.2
```

Renovate creates PRs automatically.

### Handles

* Maven
* npm
* Docker images
* GitHub Actions

No AI needed.


---

# 7. AI Remediation Agent

### Purpose

Handle cases Renovate cannot solve.

### AI SHOULD DO

#### Dockerfile Fixes

Example:

```dockerfile
FROM ubuntu:22.04
```

AI decides:

```dockerfile
FROM ubuntu:24.04
```


---

#### Dependency Conflicts

Example:

```text
Library A requires Spring 5
Library B requires Spring 6
```

AI determines upgrade path.


---

#### Multi-Step Remediation

Example:

```text
Upgrade package
Update config
Change code
```

AI generates solution.


---

#### Root Cause Analysis

Example:

```text
Why does this vulnerability keep returning?
```

AI investigates.


---

### AI SHOULD NOT DO

* Parse scan reports
* Upgrade simple package versions
* Replace Renovate
* Handle routine fixes


---

# 8. Patch Generation

### Rule-Based

Renovate creates PR automatically.


---

### AI-Based

Agent creates:

* Dockerfile changes
* Config updates
* Code fixes

Outputs:

```text
Git Commit
Pull Request
```


---

# 9. Validation

### Purpose

Ensure fix actually works.

Pipeline:

```text
Apply Fix
    ↓
Build
    ↓
Run Tests
    ↓
Run Security Scans Again
```

If vulnerability disappears:

```text
PASS
```

Otherwise:

```text
FAIL
```

No AI needed.


---

# 10. Pull Request

### Automated

Create PR containing:

* vulnerabilities fixed
* dependencies upgraded
* validation results


---

# What Needs To Be Build First

### Phase 1

```text
Run Scanners
    ↓
Normalize Findings
    ↓
Store in DB
    ↓
Simple Dashboard
```


---

### Phase 2

```text
Add Syft
    ↓
Add Renovate
    ↓
Auto Dependency Updates
```


---

### Phase 3

```text
AI Remediation Agent
    ↓
Patch Generation
    ↓
Validation
```


---

# Golden Rule

```text
Simple Fix?
    → Renovate / Rules

Complex Fix?
    → AI Agent
```

The AI should only touch the 10-20% of security issues that require reasoning. The other 80-90% should be solved deterministically using scanners, normalization, Renovate, and rule-based remediation.