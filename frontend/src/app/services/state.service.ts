import { Injectable, computed, signal, inject } from '@angular/core';
import { timer, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SecurityService } from './security.service';
import { DashboardSummaryResponse, FindingResponse, RemediationSuggestionDto, ScanJob } from '../models/security.model';

@Injectable({
  providedIn: 'root'
})
export class AppStateService {
  private securityService = inject(SecurityService);

  summary = signal<DashboardSummaryResponse | null>(null);
  findings = signal<FindingResponse[]>([]);
  automatedList = signal<RemediationSuggestionDto[]>([]);
  aiTaskList = signal<RemediationSuggestionDto[]>([]);
  activeJobs = signal<ScanJob[]>([]);

  private pollingSub?: Subscription;

  loadAllData() {
    this.securityService.getDashboardSummary().subscribe(data => this.summary.set(data));
    this.securityService.getFindings().subscribe(data => this.findings.set(data));
    this.securityService.getAutomatedRemediations().subscribe(data => this.automatedList.set(data));
    this.securityService.getAiTasks().subscribe(data => this.aiTaskList.set(data));
  }

  startPolling() {
    if (this.pollingSub) return;
    this.pollingSub = timer(0, 3000).pipe(
      switchMap(() => this.securityService.getActiveJobs())
    ).subscribe(jobs => {
      this.activeJobs.set(jobs);
      if (jobs.some(j => j.status === 'COMPLETED')) {
        this.loadAllData();
      }
    });
  }

  stopPolling() {
    if (this.pollingSub) {
      this.pollingSub.unsubscribe();
      this.pollingSub = undefined;
    }
  }

  triggerScan(targetDir: string) {
    this.securityService.triggerScan(targetDir).subscribe({
      next: (res) => {
        console.log('Scan triggered:', res);
        this.securityService.getActiveJobs().subscribe(jobs => this.activeJobs.set(jobs));
      },
      error: (err) => console.error('Error triggering scan:', err)
    });
  }

  updateFindingStatus(findingId: string, status: string) {
    return this.securityService.updateFindingStatus(findingId, status);
  }
}
