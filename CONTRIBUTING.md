# Contributing to Vindicator Security Control Plane

Thank you for your interest in contributing to the **Vindicator Security Control Plane**! We welcome contributions from the open-source security and developer community—whether you are adding new scanner integrations, improving remediation rules, enhancing UI aesthetics, or reporting bugs.

---

## 1. Code of Conduct & Engineering Philosophy

When contributing, please follow Vindicator's core engineering rules:
- **Simplicity over complexity**: Prefer straightforward, readable code over clever abstractions.
- **Consistency over novelty**: Extend existing architectural patterns (e.g., our `ScannerReportParser` adapter interface) rather than introducing conflicting frameworks.
- **Minimize dependencies**: Avoid unnecessary third-party libraries. In Java, **do not use Lombok** to ensure zero JDK 17/21/25 annotation processor compatibility issues.
- **No reinventing remediation**: For automated fixes, leverage existing deterministic open-source tools (**Renovate Bot**, **OpenRewrite**) rather than writing custom AST parsers in Spring Boot.

---

## 2. Development Setup

### Prerequisites
- **Docker & Docker Compose** (for MongoDB and local container orchestration)
- **Java 17 or higher** (JDK 21 or 25 recommended)
- **Node.js 20+ & npm 10+** (for Angular 19 frontend development)
- **PowerShell 7+ or Bash** (for executing `security_scan.ps1` / `security_scan.sh`)

### Running Locally

1. **Start MongoDB via Docker**:
   ```bash
   docker compose up mongodb -d
   ```

2. **Start the Spring Boot Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   *The REST API will be available at `http://localhost:8080/api/v1`.*

3. **Start the Angular Frontend**:
   ```bash
   cd frontend
   npm ci
   npm run start
   ```
   *The Control Panel UI will be available at `http://localhost:4200`.*

---

## 3. How to Add a New Scanner Integration

The control plane uses a clean Adapter Pattern to normalize diverse scanner outputs into unified `SecurityFinding` MongoDB documents.

To integrate a new CLI scanner (e.g., Snyk, Grype, SonarQube, Bandit):
1. **Create the Parser**: Add a new class in `com.vindicator.security.scanner` implementing `ScannerReportParser`:
   ```java
   @Component
   public class GrypeReportParser implements ScannerReportParser {
       @Override
       public String getSupportedScannerName() {
           return "GRYPE";
       }

       @Override
       public List<SecurityFinding> parseReport(String jsonReport, ScanJob scanJob) {
           // Parse tool JSON and return normalized SecurityFinding list
       }
   }
   ```
2. **Add Default Ignore Config**: If the tool supports exclusion files, place standard default ignore configs under `configs/` (e.g., `configs/grype.yaml`).
3. **Write Unit Tests**: Add test cases to `ScannerParserTests.java` verifying that sample reports normalize correctly without throwing exceptions.

---

## 4. Submitting a Pull Request

1. Fork the repository and create a feature branch (`git checkout -b feature/my-new-scanner`).
2. Verify all backend unit tests pass:
   ```bash
   cd backend
   mvn test
   ```
3. Verify frontend unit tests and production bundle build:
   ```bash
   cd frontend
   npm test -- --watch=false
   npm run build
   ```
4. Commit your changes with clear, descriptive commit messages.
5. Open a Pull Request referencing any relevant issues or feature requests.
