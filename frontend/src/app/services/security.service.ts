import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
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
    return this.http.get<DashboardSummaryResponse>(`${this.apiUrl}/dashboard/summary`);
  }

  getFindings(asset?: string, status?: string, severity?: string): Observable<FindingResponse[]> {
    let params = new HttpParams();
    if (asset && asset !== 'ALL') params = params.set('asset', asset);
    if (status && status !== 'ALL') params = params.set('status', status);
    if (severity && severity !== 'ALL') params = params.set('severity', severity);

    return this.http.get<FindingResponse[]>(`${this.apiUrl}/findings`, { params });
  }

  updateFindingStatus(findingId: string, status: string): Observable<FindingResponse> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<FindingResponse>(`${this.apiUrl}/findings/${findingId}/status`, null, { params });
  }

  getAutomatedRemediations(): Observable<RemediationSuggestionDto[]> {
    return this.http.get<RemediationSuggestionDto[]>(`${this.apiUrl}/remediation/automated`);
  }

  getAiTasks(): Observable<RemediationSuggestionDto[]> {
    return this.http.get<RemediationSuggestionDto[]>(`${this.apiUrl}/remediation/ai-tasks`);
  }

  getRecentScans(): Observable<ScanJob[]> {
    return this.http.get<ScanJob[]>(`${this.apiUrl}/scans`);
  }

  triggerScan(targetDir: string): Observable<any> {
    const body = { targetDir: targetDir, assetName: 'local-scan' };
    return this.http.post<any>(`${this.apiUrl}/scans/trigger`, body);
  }

  getActiveJobs(): Observable<ScanJob[]> {
    return this.http.get<ScanJob[]>(`${this.apiUrl}/scans/jobs/active`);
  }
}
