import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardSummaryResponse } from '../../models/security.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  @Input() summary: DashboardSummaryResponse | null = null;
  @Output() navigateToTab = new EventEmitter<string>();

  get totalCount(): number {
    if (!this.summary) return 1;
    return (
      this.summary.criticalCount +
      this.summary.highCount +
      this.summary.mediumCount +
      this.summary.lowCount
    ) || 1;
  }

  getSeverityPercentage(count: number): number {
    return Math.round((count / this.totalCount) * 100);
  }

  onCardClick(tab: string) {
    this.navigateToTab.emit(tab);
  }
}
