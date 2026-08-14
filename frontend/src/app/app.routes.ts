import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { FindingsComponent } from './components/findings/findings.component';
import { RemediationComponent } from './components/remediation/remediation.component';
import { ScannersConfigComponent } from './components/scanners-config/scanners-config.component';

export const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent, data: { animation: 'Dashboard' } },
  { path: 'findings', component: FindingsComponent, data: { animation: 'Findings' } },
  { path: 'remediation', component: RemediationComponent, data: { animation: 'Remediation' } },
  { path: 'config', component: ScannersConfigComponent, data: { animation: 'Config' } },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
];
