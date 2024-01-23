import { PrismaClient } from '@prisma/client';
import { UnauthorizedException } from '@nestjs/common';

export async function isUserExist(prisma: PrismaClient, userId: number) {
  const user = await prisma.users.findUnique({
    where: { id: userId },
  });

  if (!user) {
    throw new UnauthorizedException('User not found');
  }
}
