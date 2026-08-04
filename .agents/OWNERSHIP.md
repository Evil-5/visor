# Vindicator Security Control Plane - Domain Ownership

## Component Ownership
- `configs/`: Security Platform Engineering team. Defines standard exclusion paths and mock credential allowlists.
- `backend/`: Core Java/Spring Boot Backend team (`com.vindicator.security`). Owns canonical finding schema, scanner parsers, MongoDB repositories, and remediation orchestration.
- `frontend/`: Angular Frontend team. Owns Executive Dashboard, Findings Explorer, and Remediation Center screens.
- `security_scan.ps1` / `security_scan.sh`: DevSecOps & CI/CD Pipeline team. Owns container execution and volume mounting.

## Security Boundaries
- Backend services trust user identity only from trusted API Gateway headers (`x-user-id`).
- All controllers expose DTOs only; MongoDB entity classes and `_id` fields are never exposed externally.
