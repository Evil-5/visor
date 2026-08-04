import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  @Input() activeTab = 'dashboard';
  @Output() tabChange = new EventEmitter<string>();
  @Output() triggerScan = new EventEmitter<void>();

  selectTab(tab: string) {
    this.tabChange.emit(tab);
  }

  onRunScan() {
    this.triggerScan.emit();
  }
}
