import { Module } from '@nestjs/common';
import { NotificationsService } from './notifications.service';
import { NotificationsController } from './notifications.controller';
import { PrismaClient } from '@prisma/client';

@Module({
  controllers: [NotificationsController],
  providers: [NotificationsService, PrismaClient],
})
export class NotificationsModule {}
