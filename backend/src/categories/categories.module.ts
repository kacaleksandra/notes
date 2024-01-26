import { Module } from '@nestjs/common';
import { CategoriesController } from './categories.controller';
import { CategoriesService } from './categories.service';
import { PrismaClient } from '@prisma/client';

@Module({
  controllers: [CategoriesController],
  providers: [CategoriesService, PrismaClient],
  exports: [CategoriesService],
})
export class CategoriesModule {}
