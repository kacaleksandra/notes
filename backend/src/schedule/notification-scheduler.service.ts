import { Injectable } from '@nestjs/common';
import { Cron, CronExpression } from '@nestjs/schedule';
import { NotificationService } from 'src/notification/notification.service';

@Injectable()
export class NotificationSchedulerService {
  constructor(private readonly notificationService: NotificationService) {}

  @Cron(CronExpression.EVERY_MINUTE)
  async handleCron() {
    console.log('Running every minute');
    await this.notificationService.checkAndSendNotifications();
  }
}
