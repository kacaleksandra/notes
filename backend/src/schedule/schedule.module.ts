import { Module } from '@nestjs/common';
import { ScheduleModule } from '@nestjs/schedule';
import { NotificationService } from 'src/notification/notification.service';
import { NotificationSchedulerService } from './notification-scheduler.service';
import { PrismaClient } from '@prisma/client';

@Module({
  imports: [ScheduleModule.forRoot()],
  providers: [NotificationSchedulerService, NotificationService, PrismaClient],
  exports: [ScheduleAppModule],
})
export class ScheduleAppModule {}
