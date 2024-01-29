import {
  BadRequestException,
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import * as admin from 'firebase-admin';
import { env } from 'process';
import { AddReminderDto } from './dto/add-reminder.dto';

const firebaseConfig = {
  credential: admin.credential.cert({
    projectId: env.PROJECT_ID,
    privateKey: env.PRIVATE_KEY,
    clientEmail: env.CLIENT_EMAIL,
  }),
};

admin.initializeApp(firebaseConfig);

@Injectable()
export class NotificationService {
  constructor(private readonly prisma: PrismaClient) {}

  async addReminder(
    userId: number,
    reminderDto: AddReminderDto,
  ): Promise<{ success: boolean; message?: string }> {
    const { noteId, date } = reminderDto;

    const user = await this.prisma.users.findUnique({ where: { id: userId } });
    if (!user) {
      throw new NotFoundException(`User with ID ${userId} not found.`);
    }

    const note = await this.prisma.notes.findUnique({ where: { id: noteId } });
    if (!note) {
      throw new NotFoundException(`Note with ID ${noteId} not found.`);
    }

    const existingReminder = await this.prisma.reminders.findFirst({
      where: { userId, noteId, date },
    });
    if (existingReminder) {
      throw new ConflictException('Reminder already exists.');
    }

    try {
      await this.prisma.reminders.create({
        data: { userId, noteId, date },
      });

      return {
        success: true,
        message: 'Reminder saved successfully.',
      };
    } catch (error) {
      return {
        success: false,
        message: error.message,
      };
    }
  }

  async removeReminder(
    userId: number,
    reminderId: number,
  ): Promise<{ success: boolean; message?: string }> {
    const reminder = await this.prisma.reminders.findUnique({
      where: { id: reminderId, userId },
    });

    if (!reminder) {
      throw new NotFoundException(
        `Reminder with ID ${reminderId} not found or does not belong to the user.`,
      );
    }

    try {
      await this.prisma.reminders.delete({ where: { id: reminderId } });
      return { success: true, message: 'Reminder removed successfully.' };
    } catch (error) {
      throw new BadRequestException('Failed to remove reminder.');
    }
  }

  async saveToken(userId: number, token: string) {
    const existingToken = await this.prisma.tokens.findUnique({
      where: { token },
    });

    if (existingToken) {
      throw new BadRequestException('Token already exists.');
    }

    return await this.prisma.tokens.create({
      data: {
        token,
        userId,
      },
    });
  }

  async removeToken(
    userId: number,
    tokenId: number,
  ): Promise<{ success: boolean; message?: string }> {
    const token = await this.prisma.tokens.findUnique({
      where: { id: tokenId, userId },
    });

    if (!token) {
      throw new NotFoundException(
        `Token with ID ${tokenId} not found or does not belong to the user.`,
      );
    }

    try {
      await this.prisma.tokens.delete({ where: { id: tokenId } });
      return { success: true, message: 'Token removed successfully.' };
    } catch (error) {
      throw new BadRequestException('Failed to remove token.');
    }
  }

  async checkAndSendNotifications() {
    const currentDateTime = new Date();
    const warsawTime = currentDateTime.getTime() + 60 * 60 * 1000;

    const reminders = await this.prisma.reminders.findMany({
      where: {
        date: {
          gte: new Date(warsawTime - 60000),
          lte: new Date(warsawTime + 60000),
        },
      },
      include: {
        user: true,
        note: {
          select: {
            title: true,
          },
        },
      },
    });

    for (const reminder of reminders) {
      const { userId, noteId, note } = reminder;

      console.log(reminder);

      // Pobierz tokeny użytkownika z tabeli Tokens
      const tokens = await this.prisma.tokens.findMany({
        where: {
          userId,
        },
      });

      // Wyślij powiadomienie do każdego tokenu
      for (const token of tokens) {
        const payload = {
          token: token.token,
          notification: {
            title: `Reminder: ${note.title}`,
            body: `Pamiętaj o ${note.title}`,
          },
          data: {
            noteId: noteId.toString(),
          },
        };

        try {
          const response = await admin.messaging().send(payload);
          console.log('Firebase Cloud Messaging response:', response);
        } catch (error) {
          console.error('Firebase Cloud Messaging error:', error);
        }
      }
    }
  }

  //   async sendingNotificationOneUser(token: string) {
  //     const payload = {
  //       token: token,
  //       notification: {
  //         title: 'Hi there this is title',
  //         body: 'Hi there this is message',
  //       },
  //       data: {
  //         name: 'Joe',
  //         age: '21',
  //       },
  //     };

  //     try {
  //       const response = await admin.messaging().send(payload);
  //       console.log('Firebase Cloud Messaging response:', response);

  //       return {
  //         success: true,
  //         response,
  //       };
  //     } catch (error) {
  //       console.error('Firebase Cloud Messaging error:', error);
  //       return {
  //         success: false,
  //         error,
  //       };
  //     }
  //   }
}
