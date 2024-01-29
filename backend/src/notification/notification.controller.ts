import {
  Body,
  Controller,
  Delete,
  Param,
  ParseIntPipe,
  Post,
  UseGuards,
} from '@nestjs/common';
import { NotificationService } from './notification.service';
import {
  ApiTags,
  ApiBearerAuth,
  ApiOperation,
  ApiResponse,
  ApiBody,
  ApiConflictResponse,
  ApiParam,
} from '@nestjs/swagger';
import { JwtAuthGuard } from 'src/auth/jwt-auth-guard';
import { User } from 'common/decorators/user.decorator';
import { Users } from '@prisma/client';
import { AddReminderDto } from './dto/add-reminder.dto';

@Controller('notification')
@ApiTags('notifications')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth('access-token')
export class NotificationController {
  constructor(private readonly notificationService: NotificationService) {}

  @Post()
  @ApiOperation({
    summary: 'Add notification',
    description: 'Add notification for note',
  })
  @ApiBody({
    type: AddReminderDto,
  })
  @ApiResponse({
    status: 201,
    description: 'Reminder saved successfully.',
  })
  @ApiResponse({
    status: 400,
    description: 'Bad Request.',
  })
  @ApiConflictResponse({
    description: 'Conflict: Reminder already exists.',
  })
  async addReminder(@User() user: Users, @Body() body: AddReminderDto) {
    return await this.notificationService.addReminder(user.id, body);
  }

  @Delete(':reminderId')
  @ApiOperation({
    summary: 'Remove notification',
    description: 'Remove notification for note',
  })
  @ApiParam({
    name: 'reminderId',
    description: 'ID of the reminder to be removed',
    type: Number,
  })
  @ApiResponse({
    status: 200,
    description: 'Reminder removed successfully.',
  })
  @ApiResponse({
    status: 400,
    description: 'Bad Request.',
  })
  @ApiResponse({
    status: 404,
    description:
      'Not Found: Reminder not found or does not belong to the user.',
  })
  async removeReminder(
    @User() user: Users,
    @Param('reminderId', ParseIntPipe) reminderId: number,
  ) {
    return this.notificationService.removeReminder(user.id, reminderId);
  }

  // async sendingNotificationOneUser(@Body() body: { token: string }) {
  //   const { token } = body;
  //   return this.notificationService.sendingNotificationOneUser(token);
  // }
}
