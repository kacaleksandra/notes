import { Module } from '@nestjs/common';
import { NotificationService } from './notification.service';
import { NotificationController } from './notification.controller';
import { PrismaClient } from '@prisma/client';

@Module({
  controllers: [NotificationController],
  providers: [NotificationService, PrismaClient],
})
export class NotificationModule {}
