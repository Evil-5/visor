import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RemediationSuggestionDto } from '../../models/security.model';
import { AppStateService } from '../../services/state.service';

@Component({
  selector: 'app-remediation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './remediation.component.html',
  styleUrls: ['./remediation.component.css']
})
export class RemediationComponent {
  state = inject(AppStateService);

  get automatedList(): RemediationSuggestionDto[] {
    return this.state.automatedList();
  }

  get aiTaskList(): RemediationSuggestionDto[] {
    return this.state.aiTaskList();
  }

  activeSubTab: 'renovate' | 'openrewrite' | 'ai' = 'renovate';
  copiedItem: string | null = null;

  get renovateTasks(): RemediationSuggestionDto[] {
    return this.automatedList.filter(r => r.remediationType === 'RENOVATE_AUTO');
  }

  get openRewriteTasks(): RemediationSuggestionDto[] {
    return this.automatedList.filter(r => r.remediationType === 'OPENREWRITE_AUTO');
  }

  copyCommand(text: string, id: string) {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(text);
      this.copiedItem = id;
      setTimeout(() => {
        this.copiedItem = null;
      }, 2500);
    }
  }
}
