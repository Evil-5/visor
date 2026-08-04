import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface ScannerPolicy {
  name: string;
  category: string;
  status: 'ONLINE' | 'ACTIVE';
  policyFile: string;
  exclusionSummary: string;
  rulesConfig: string;
}

@Component({
  selector: 'app-scanners-config',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './scanners-config.component.html',
  styleUrls: ['./scanners-config.component.css']
})
export class ScannersConfigComponent {
  scanners: ScannerPolicy[] = [
    {
      name: 'GITLEAKS',
      category: 'Secret & Credential Scanner',
      status: 'ONLINE',
      policyFile: 'configs/gitleaks.toml & .gitleaksignore',
      exclusionSummary: 'Excludes all sample tokens under **/.venv/**, **/tests/**, and demo API keys matching regex prefix sample_api_key_*.',
      rulesConfig: '[[rules]]\n  id = "vindicator-custom-token"\n  regex = \'\'\'(?i)(vntk_[a-z0-9]{32})\'\'\'\n  tags = ["key", "vindicator"]'
    },
    {
      name: 'TRUFFLEHOG',
      category: 'Verified High-Entropy Secrets',
      status: 'ONLINE',
      policyFile: 'configs/trufflehog-ignore.txt',
      exclusionSummary: 'Excludes local virtualenvs (.venv/), test mock fixtures, and public demo certificates.',
      rulesConfig: '--no-verification=false --exclude-paths=configs/trufflehog-ignore.txt'
    },
    {
      name: 'SEMGREP',
      category: 'SAST & AST Vulnerability Scanner',
      status: 'ONLINE',
      policyFile: 'configs/semgrep.yml',
      exclusionSummary: 'Excludes generated JavaScript bundles, target/ build folders, and vendor libraries.',
      rulesConfig: 'rules:\n  - id: java.spring.security.csrf-disabled\n    severity: ERROR\n    message: "CSRF disabled in Spring Security"'
    },
    {
      name: 'TRIVY',
      category: 'Container Image & Dependency Scanner',
      status: 'ONLINE',
      policyFile: 'configs/trivy.yaml',
      exclusionSummary: 'Excludes base OS layers marked as won’t-fix by vendor linux distributions.',
      rulesConfig: 'severity: "CRITICAL,HIGH"\nignore-unfixed: true\nscanners: "vuln,secret"'
    },
    {
      name: 'CHECKOV',
      category: 'IaC & Container Dockerfile Policy',
      status: 'ONLINE',
      policyFile: 'configs/checkov.yaml',
      exclusionSummary: 'Excludes test kubernetes deployment manifests from production TLS checks.',
      rulesConfig: '--framework dockerfile,kubernetes,terraform --compact'
    },
    {
      name: 'OSV-SCANNER',
      category: 'Open Source Vulnerability Dependency Check',
      status: 'ONLINE',
      policyFile: 'configs/osv-scanner.toml',
      exclusionSummary: 'Excludes transitive devDependencies not packaged in production JAR/Docker artifact.',
      rulesConfig: '[[IgnoredVulns]]\n  id = "GHSA-xxxx-xxxx-xxxx"\n  reason = "Not reachable in Vindicator backend execution"'
    }
  ];

  selectedScanner: ScannerPolicy = this.scanners[0];

  selectPolicy(policy: ScannerPolicy) {
    this.selectedScanner = policy;
  }
}
