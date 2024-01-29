import { Module } from '@nestjs/common';
import { TokenService } from './token.service';
import { TokenController } from './token.controller';
import { PrismaClient } from '@prisma/client';

@Module({
  providers: [PrismaClient, TokenService],
  controllers: [TokenController],
})
export class TokenModule {}
