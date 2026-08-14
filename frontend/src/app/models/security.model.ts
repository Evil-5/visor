export interface FindingResponse {
  findingId: string;
  asset: string;
  scanner: string;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | string;
  cveOrRuleId: string;
  packageOrFile: string;
  installedVersion?: string;
  fixedVersion?: string;
  title: string;
  description: string;
  status: 'OPEN' | 'RESOLVED' | 'FALSE_POSITIVE' | string;
  remediationType: 'RENOVATE_AUTO' | 'OPENREWRITE_AUTO' | 'AI_ASSISTED' | string;
  ossRemediationReference: string;
  firstSeenAt: string;
  lastSeenAt: string;
}

export interface DashboardSummaryResponse {
  totalOpenFindings: number;
  criticalCount: number;
  highCount: number;
  mediumCount: number;
  lowCount: number;
  renovateAutoPrCount: number;
  openRewriteCount: number;
  aiAssistedCount: number;
  findingsByScanner: { [scannerName: string]: number };
}

export interface RemediationSuggestionDto {
  remediationType: string;
  cveOrRuleId: string;
  packageOrFile: string;
  installedVersion?: string;
  fixedVersion?: string;
  suggestedAction: string;
  findingIds: string[];
}

export interface ScanJob {
  jobId: string;
  asset: string;
  scannersRun?: string[];
  scannerStatuses?: { [scannerName: string]: string };
  status: 'QUEUED' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED' | string;
  findingsCount?: number;
  startedAt: string;
  completedAt?: string;
  errorMessage?: string;
}
