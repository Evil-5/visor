import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScanModal } from './scan-modal';

describe('ScanModal', () => {
  let component: ScanModal;
  let fixture: ComponentFixture<ScanModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScanModal],
    }).compileComponents();

    fixture = TestBed.createComponent(ScanModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
