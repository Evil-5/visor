import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FindingResponse } from '../../models/security.model';
import { AppStateService } from '../../services/state.service';

@Component({
  selector: 'app-findings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './findings.component.html',
  styleUrls: ['./findings.component.css']
})
export class FindingsComponent {
  state = inject(AppStateService);

  get findings(): FindingResponse[] {
    return this.state.findings();
  }

  selectedAsset = 'ALL';
  selectedScanner = 'ALL';
  selectedSeverity = 'ALL';
  selectedStatus = 'ALL';
  searchQuery = '';

  get filteredFindings(): FindingResponse[] {
    return this.findings.filter(f => {
      if (this.selectedAsset !== 'ALL' && f.asset !== this.selectedAsset) return false;
      if (this.selectedScanner !== 'ALL' && f.scanner !== this.selectedScanner) return false;
      if (this.selectedSeverity !== 'ALL' && f.severity !== this.selectedSeverity) return false;
      if (this.selectedStatus !== 'ALL' && f.status !== this.selectedStatus) return false;
      if (this.searchQuery.trim()) {
        const q = this.searchQuery.toLowerCase();
        const matchTitle = f.title.toLowerCase().includes(q);
        const matchCve = f.cveOrRuleId.toLowerCase().includes(q);
        const matchPkg = f.packageOrFile.toLowerCase().includes(q);
        const matchDesc = f.description.toLowerCase().includes(q);
        if (!matchTitle && !matchCve && !matchPkg && !matchDesc) return false;
      }
      return true;
    });
  }

  onStatusChange(findingId: string, newStatus: string) {
    this.state.updateFindingStatus(findingId, newStatus).subscribe(() => {
      this.state.loadAllData();
    });
  }

  resetFilters() {
    this.selectedAsset = 'ALL';
    this.selectedScanner = 'ALL';
    this.selectedSeverity = 'ALL';
    this.selectedStatus = 'ALL';
    this.searchQuery = '';
  }
}
