import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RemediationSuggestionDto } from '../../models/security.model';

@Component({
  selector: 'app-remediation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './remediation.component.html',
  styleUrls: ['./remediation.component.css']
})
export class RemediationComponent {
  @Input() automatedList: RemediationSuggestionDto[] = [];
  @Input() aiTaskList: RemediationSuggestionDto[] = [];

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
