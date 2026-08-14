import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet, Router } from '@angular/router';
import { trigger, transition, style, query, animateChild, group, animate } from '@angular/animations';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ScanModalComponent } from './components/scan-modal/scan-modal';
import { AppStateService } from './services/state.service';
import { ScanJob } from './models/security.model';

export const routeTransitionAnimations = trigger('routeAnimations', [
  transition('* <=> *', [
    style({ position: 'relative' }),
    query(':enter, :leave', [
      style({
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        opacity: 0
      })
    ], { optional: true }),
    query(':enter', [
      style({ opacity: 0, transform: 'translateY(10px)' })
    ], { optional: true }),
    group([
      query(':leave', [
        animate('200ms ease-out', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ], { optional: true }),
      query(':enter', [
        animate('300ms 100ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ], { optional: true })
    ])
  ])
]);

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    RouterOutlet,
    NavbarComponent,
    ScanModalComponent
  ],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
  animations: [routeTransitionAnimations]
})
export class App implements OnInit, OnDestroy {
  state = inject(AppStateService);
  private router = inject(Router);

  showScanModal = false;
  toastMessage: string | null = null;

  ngOnInit() {
    this.state.loadAllData();
    this.state.startPolling();
  }

  ngOnDestroy() {
    this.state.stopPolling();
  }

  get activeJobs() {
    return this.state.activeJobs;
  }

  getRouteAnimationData(outlet: RouterOutlet) {
    return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
  }

  onTabChange(tab: string) {
    this.router.navigate([`/${tab}`]);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onOpenScanModal() {
    this.showScanModal = true;
  }

  onTriggerScan(targetDir: string) {
    if (!targetDir) return;
    this.showScanModal = false;
    this.state.triggerScan(targetDir);
  }
}
