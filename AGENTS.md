# Vindicator Security Control Plane (AGENTS.md)

This repository contains the **Vindicator Security Control Plane**, a centralized security orchestration and intelligence platform for the Vindicator software portfolio (DocFolio, Nexus, Vindicator Gaming Platform, and future products).

## Repository Structure

- `configs/`: Standardized scanner configuration and ignore files (`.gitleaks.toml`, `.semgrepignore`, `.trivyignore`, etc.) to eliminate false positives (.venv, sample API keys, test fixtures).
- `backend/`: Spring Boot 3 + Java 17 + MongoDB microservice (`com.vindicator.security`) that ingests raw scanner reports, normalizes findings into a canonical schema, deduplicates records, and orchestrates OSS remediation (Renovate Bot, OpenRewrite).
- `frontend/`: Clean, premium Angular 19 control panel UI for visualizing security posture, exploring normalized findings, and viewing remediation suggestions.
- `security_scan.ps1` / `security_scan.sh`: Modular scan runners that execute Checkov, Gitleaks, OSV-Scanner, Semgrep, Trivy Code, TruffleHog, and Trivy Image in containers, mounting custom exclusions from `configs/`.

## Engineering Philosophy & Standards

- **Simplicity over complexity**: Do not reinvent commodity tools. Use existing OSS scanners as black-box workers and Renovate/OpenRewrite for deterministic remediation.
- **Backend**: Spring Boot 3 with MongoDB. Follow Vindicator patterns (`spdx-be-custom-architecture`, `spdx-be-custom-persistence`, `spdx-be-custom-config-and-deploy`, `spdx-be-custom-security`).
- **Frontend**: Angular 19 with rich, responsive dark-mode aesthetics and strict REST DTO consumption.
- **Authentication**: Casdoor OAuth2/OIDC. Services trust user identity from APISIX gateway headers (`x-user-id`).

## AI Agent Instructions
1. Read `.agents/CONTEXT.md` for architectural context.
2. Read `.agents/OWNERSHIP.md` for domain boundaries.
3. Read `.agents/REFERENCES.md` for API and scanner schema references.
4. Always test scan runners and backend ingestion when modifying parser logic.
