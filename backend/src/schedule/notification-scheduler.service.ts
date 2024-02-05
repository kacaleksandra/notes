import { Injectable } from '@nestjs/common';
import { Cron, CronExpression } from '@nestjs/schedule';
import { NotificationsService } from 'src/notifications/notifications.service';

@Injectable()
export class NotificationSchedulerService {
  constructor(private readonly notificationsService: NotificationsService) {}

  @Cron(CronExpression.EVERY_10_SECONDS)
  async handleCron() {
    console.log('Running every 10 seconds');
    await this.notificationsService.checkAndSendNotifications();
  }
}
