# Vindicator Security Control Plane - Architecture Context

## Overview
The Vindicator Security Control Plane decouples security scanning from vulnerability management by treating scanners as stateless workers and providing an intelligence layer above them.

```
Code / Image / IaC
       ↓ (security_scan.ps1 / security_scan.sh + configs/)
   Raw Reports (JSON/SARIF)
       ↓ (POST /api/v1/scans/ingest)
 Spring Boot Ingestion & Normalization Layer (TrivyParser, SemgrepParser, GitleaksParser, CheckovParser, OsvParser, TruffleHogParser)
       ↓ (MongoTemplate.findAndModify Lock-Free Deduplication)
 MongoDB Canonical Storage (security_findings, scan_jobs, assets)
       ↓
 Angular Control Panel & Remediation Orchestrator (Renovate Bot PRs, OpenRewrite Recipes, AI Remediation)
```

## Why Not DefectDojo?
While DefectDojo provides traditional vulnerability management, its monolithic Django architecture and cluttered UI are optimized for manual penetration test tracking rather than modern automated CI/CD pipelines. Building our own lightweight Spring Boot + MongoDB normalization service and Angular UI provides clean REST DTO contracts, better performance, and zero UI clutter.
