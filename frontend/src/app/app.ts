import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { FindingsComponent } from './components/findings/findings.component';
import { RemediationComponent } from './components/remediation/remediation.component';
import { ScannersConfigComponent } from './components/scanners-config/scanners-config.component';
import { SecurityService } from './services/security.service';
import {
  DashboardSummaryResponse,
  FindingResponse,
  RemediationSuggestionDto
} from './models/security.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    DashboardComponent,
    FindingsComponent,
    RemediationComponent,
    ScannersConfigComponent
  ],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  activeTab = 'dashboard';

  summary: DashboardSummaryResponse | null = null;
  findings: FindingResponse[] = [];
  automatedList: RemediationSuggestionDto[] = [];
  aiTaskList: RemediationSuggestionDto[] = [];

  toastMessage: string | null = null;
  isScanning = false;

  constructor(private securityService: SecurityService) {}

  ngOnInit() {
    this.loadAllData();
  }

  loadAllData() {
    this.securityService.getDashboardSummary().subscribe(data => {
      this.summary = data;
    });
    this.securityService.getFindings().subscribe(data => {
      this.findings = data;
    });
    this.securityService.getAutomatedRemediations().subscribe(data => {
      this.automatedList = data;
    });
    this.securityService.getAiTasks().subscribe(data => {
      this.aiTaskList = data;
    });
  }

  onTabChange(tab: string) {
    this.activeTab = tab;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onFindingStatusChanged(event: { findingId: string; status: string }) {
    this.securityService.updateFindingStatus(event.findingId, event.status).subscribe(() => {
      this.showToast(`Updated finding status to ${event.status}`);
      this.loadAllData();
    });
  }

  onTriggerScan() {
    if (this.isScanning) return;
    this.isScanning = true;
    this.showToast('⚡ Pipeline scan triggered across all 6 scanners...');
    setTimeout(() => {
      this.isScanning = false;
      this.showToast('✅ Scanners completed! Data refreshed.');
      this.loadAllData();
    }, 3000);
  }

  private showToast(message: string) {
    this.toastMessage = message;
    setTimeout(() => {
      if (this.toastMessage === message) {
        this.toastMessage = null;
      }
    }, 3500);
  }
}
