import { Module } from '@nestjs/common';
import { ScheduleModule } from '@nestjs/schedule';
import { NotificationsService } from 'src/notifications/notifications.service';
import { NotificationSchedulerService } from './notification-scheduler.service';
import { PrismaClient } from '@prisma/client';

@Module({
  imports: [ScheduleModule.forRoot()],
  providers: [NotificationSchedulerService, NotificationsService, PrismaClient],
  exports: [ScheduleAppModule],
})
export class ScheduleAppModule {}
