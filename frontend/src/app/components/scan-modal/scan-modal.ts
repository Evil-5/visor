import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-scan-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './scan-modal.html',
  styleUrls: ['./scan-modal.css']
})
export class ScanModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() runScan = new EventEmitter<string>();

  onClose() {
    this.close.emit();
  }

  onRun(targetDir: string) {
    if (targetDir && targetDir.trim() !== '') {
      this.runScan.emit(targetDir);
    }
  }
}
