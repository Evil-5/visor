import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  DashboardSummaryResponse,
  FindingResponse,
  RemediationSuggestionDto,
  ScanJob
} from '../models/security.model';

@Injectable({
  providedIn: 'root'
})
export class SecurityService {

  private apiUrl = '/api/v1';

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<DashboardSummaryResponse> {
    return this.http.get<DashboardSummaryResponse>(`${this.apiUrl}/dashboard/summary`).pipe(
      catchError(() => of(this.getMockDashboardSummary()))
    );
  }

  getFindings(asset?: string, status?: string, severity?: string): Observable<FindingResponse[]> {
    let params = new HttpParams();
    if (asset && asset !== 'ALL') params = params.set('asset', asset);
    if (status && status !== 'ALL') params = params.set('status', status);
    if (severity && severity !== 'ALL') params = params.set('severity', severity);

    return this.http.get<FindingResponse[]>(`${this.apiUrl}/findings`, { params }).pipe(
      catchError(() => of(this.getMockFindings().filter(f => {
        if (asset && asset !== 'ALL' && f.asset !== asset) return false;
        if (status && status !== 'ALL' && f.status !== status) return false;
        if (severity && severity !== 'ALL' && f.severity !== severity) return false;
        return true;
      })))
    );
  }

  updateFindingStatus(findingId: string, status: string): Observable<FindingResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<FindingResponse>(`${this.apiUrl}/findings/${findingId}/status`, null, { params });
  }

  getAutomatedRemediations(): Observable<RemediationSuggestionDto[]> {
    return this.http.get<RemediationSuggestionDto[]>(`${this.apiUrl}/remediation/automated`).pipe(
      catchError(() => of(this.getMockAutomatedRemediations()))
    );
  }

  getAiTasks(): Observable<RemediationSuggestionDto[]> {
    return this.http.get<RemediationSuggestionDto[]>(`${this.apiUrl}/remediation/ai-tasks`).pipe(
      catchError(() => of(this.getMockAiTasks()))
    );
  }

  getRecentScans(): Observable<ScanJob[]> {
    return this.http.get<ScanJob[]>(`${this.apiUrl}/scans`).pipe(
      catchError(() => of(this.getMockScans()))
    );
  }

  // --- Rich Mock Data for Offline Demo / Instant Wow Factor ---

  private getMockDashboardSummary(): DashboardSummaryResponse {
    return {
      totalOpenFindings: 18,
      criticalCount: 2,
      highCount: 6,
      mediumCount: 7,
      lowCount: 3,
      renovateAutoPrCount: 7,
      openRewriteCount: 4,
      aiAssistedCount: 7,
      findingsByScanner: {
        'TRIVY': 5,
        'SEMGREP': 4,
        'GITLEAKS': 2,
        'CHECKOV': 3,
        'OSV-SCANNER': 3,
        'TRUFFLEHOG': 1
      }
    };
  }

  private getMockFindings(): FindingResponse[] {
    return [
      {
        findingId: 'f-101',
        asset: 'docfolio',
        scanner: 'GITLEAKS',
        severity: 'CRITICAL',
        cveOrRuleId: 'generic-api-key',
        packageOrFile: 'src/config/jwt.ts:18',
        title: 'Hardcoded Secret: generic-api-key',
        description: 'Hardcoded JWT signing secret discovered in source file. Excluded from sample .venv files.',
        status: 'OPEN',
        remediationType: 'AI_ASSISTED',
        ossRemediationReference: 'AI Remediation Agent: Revoke secret and delegate to OAuth2 OIDC Resource Server',
        firstSeenAt: new Date().toISOString(),
        lastSeenAt: new Date().toISOString()
      },
      {
        findingId: 'f-102',
        asset: 'docfolio',
        scanner: 'TRUFFLEHOG',
        severity: 'CRITICAL',
        cveOrRuleId: 'aws-secret-key',
        packageOrFile: 'configs/aws_config.yml',
        title: 'Verified High-Entropy Secret: aws-secret-key',
        description: 'Verified active AWS Access Key ID found in config YAML.',
        status: 'OPEN',
        remediationType: 'AI_ASSISTED',
        ossRemediationReference: 'AI Remediation Agent: Rotate key via AWS KMS and inject via Kubernetes Secret',
        firstSeenAt: new Date().toISOString(),
        lastSeenAt: new Date().toISOString()
      },
      {
        findingId: 'f-103',
        asset: 'docfolio',
        scanner: 'TRIVY',
        severity: 'HIGH',
        cveOrRuleId: 'CVE-2023-6378',
        packageOrFile: 'logback-classic',
        installedVersion: '1.4.11',
        fixedVersion: '1.4.14',
        title: 'CVE-2023-6378 in logback-classic',
        description: 'Logback receiver serialization vulnerability allows remote code execution.',
        status: 'OPEN',
        remediationType: 'RENOVATE_AUTO',
        ossRemediationReference: 'Renovate Auto-PR: upgrade logback-classic to 1.4.14',
        firstSeenAt: new Date().toISOString(),
        lastSeenAt: new Date().toISOString()
      },
      {
        findingId: 'f-104',
        asset: 'nexus',
        scanner: 'SEMGREP',
        severity: 'HIGH',
        cveOrRuleId: 'java.spring.security.csrf-disabled',
        packageOrFile: 'src/main/java/com/vindicator/nexus/config/SecurityConfig.java',
        title: 'java.spring.security.csrf-disabled in SecurityConfig.java',
        description: 'CSRF protection explicitly disabled without stateless JWT bearer token verification.',
        status: 'OPEN',
        remediationType: 'OPENREWRITE_AUTO',
        ossRemediationReference: 'OpenRewrite recipe org.openrewrite.java.security.OwaspTopTen',
        firstSeenAt: new Date().toISOString(),
        lastSeenAt: new Date().toISOString()
      },
      {
        findingId: 'f-105',
        asset: 'nexus',
        scanner: 'CHECKOV',
        severity: 'HIGH',
        cveOrRuleId: 'CKV_DOCKER_3',
        packageOrFile: 'Dockerfile (root-user)',
        title: 'IaC Misconfiguration: CKV_DOCKER_3',
        description: 'Container runs as root user instead of creating an unprivileged appuser.',
        status: 'OPEN',
        remediationType: 'AI_ASSISTED',
        ossRemediationReference: 'AI Remediation Agent: Add non-root USER directive to Dockerfile multi-stage build',
        firstSeenAt: new Date().toISOString(),
        lastSeenAt: new Date().toISOString()
      },
      {
        findingId: 'f-106',
        asset: 'docfolio',
        scanner: 'OSV-SCANNER',
        severity: 'MEDIUM',
        cveOrRuleId: 'GHSA-2jwj-cqf9-8ccj',
        packageOrFile: 'org.springframework:spring-web',
        installedVersion: '6.0.12',
        fixedVersion: '6.0.15',
        title: 'GHSA-2jwj-cqf9-8ccj in org.springframework:spring-web',
        description: 'URL pattern matching bypass vulnerability in Spring Web controllers.',
        status: 'OPEN',
        remediationType: 'RENOVATE_AUTO',
        ossRemediationReference: 'Renovate Auto-PR: upgrade dependency org.springframework:spring-web',
        firstSeenAt: new Date().toISOString(),
        lastSeenAt: new Date().toISOString()
      },
      {
        findingId: 'f-107',
        asset: 'vindicator-gaming',
        scanner: 'TRIVY',
        severity: 'LOW',
        cveOrRuleId: 'CVE-2024-22233',
        packageOrFile: 'org.springframework.boot:spring-boot-starter-web',
        installedVersion: '3.2.0',
        fixedVersion: '3.2.3',
        title: 'CVE-2024-22233 in spring-boot-starter-web',
        description: 'Minor denial of service risk under high concurrent connection load.',
        status: 'RESOLVED',
        remediationType: 'RENOVATE_AUTO',
        ossRemediationReference: 'Renovate Auto-PR: upgrade spring-boot-starter-web to 3.2.3',
        firstSeenAt: new Date().toISOString(),
        lastSeenAt: new Date().toISOString()
      }
    ];
  }

  private getMockAutomatedRemediations(): RemediationSuggestionDto[] {
    return [
      {
        remediationType: 'RENOVATE_AUTO',
        cveOrRuleId: 'CVE-2023-6378',
        packageOrFile: 'logback-classic',
        installedVersion: '1.4.11',
        fixedVersion: '1.4.14',
        suggestedAction: 'Renovate Auto-PR: upgrade logback-classic to 1.4.14',
        findingIds: ['f-103']
      },
      {
        remediationType: 'OPENREWRITE_AUTO',
        cveOrRuleId: 'java.spring.security.csrf-disabled',
        packageOrFile: 'src/main/java/com/vindicator/nexus/config/SecurityConfig.java',
        suggestedAction: 'mvn rewrite:run -Drewrite.activeRecipes=org.openrewrite.java.security.OwaspTopTen',
        findingIds: ['f-104']
      },
      {
        remediationType: 'RENOVATE_AUTO',
        cveOrRuleId: 'GHSA-2jwj-cqf9-8ccj',
        packageOrFile: 'org.springframework:spring-web',
        installedVersion: '6.0.12',
        fixedVersion: '6.0.15',
        suggestedAction: 'Renovate Auto-PR: upgrade dependency org.springframework:spring-web',
        findingIds: ['f-106']
      }
    ];
  }

  private getMockAiTasks(): RemediationSuggestionDto[] {
    return [
      {
        remediationType: 'AI_ASSISTED',
        cveOrRuleId: 'generic-api-key',
        packageOrFile: 'src/config/jwt.ts:18',
        suggestedAction: 'AI Remediation Agent: Revoke secret and delegate to OAuth2 OIDC Resource Server',
        findingIds: ['f-101']
      },
      {
        remediationType: 'AI_ASSISTED',
        cveOrRuleId: 'CKV_DOCKER_3',
        packageOrFile: 'Dockerfile (root-user)',
        suggestedAction: 'AI Remediation Agent: Add non-root USER directive to Dockerfile multi-stage build',
        findingIds: ['f-105']
      }
    ];
  }

  private getMockScans(): ScanJob[] {
    return [
      {
        jobId: 'job-501',
        asset: 'docfolio',
        scanner: 'TRIVY',
        status: 'COMPLETED',
        totalFindingsCount: 5,
        startedAt: new Date(Date.now() - 3600000).toISOString(),
        completedAt: new Date(Date.now() - 3550000).toISOString()
      },
      {
        jobId: 'job-502',
        asset: 'docfolio',
        scanner: 'GITLEAKS',
        status: 'COMPLETED',
        totalFindingsCount: 2,
        startedAt: new Date(Date.now() - 3600000).toISOString(),
        completedAt: new Date(Date.now() - 3590000).toISOString()
      },
      {
        jobId: 'job-503',
        asset: 'nexus',
        scanner: 'SEMGREP',
        status: 'COMPLETED',
        totalFindingsCount: 4,
        startedAt: new Date(Date.now() - 7200000).toISOString(),
        completedAt: new Date(Date.now() - 7100000).toISOString()
      },
      {
        jobId: 'job-504',
        asset: 'nexus',
        scanner: 'CHECKOV',
        status: 'COMPLETED',
        totalFindingsCount: 3,
        startedAt: new Date(Date.now() - 7200000).toISOString(),
        completedAt: new Date(Date.now() - 7150000).toISOString()
      }
    ];
  }
}
