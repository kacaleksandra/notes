import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaClient } from '@prisma/client';

@Injectable()
export class TokenService {
  constructor(private readonly prisma: PrismaClient) {}
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
}
