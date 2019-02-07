import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { DashBoardComponent } from './dashboard/dashboard.component';

@NgModule({
  declarations: [DashBoardComponent],
  imports: [CommonModule, DashboardRoutingModule, FormsModule, ReactiveFormsModule, SharedModule]
})
export class DashboardModule {}
