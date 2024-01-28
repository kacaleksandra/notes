import { Module } from '@nestjs/common';
import { NotesController } from './notes.controller';
import { NotesService } from './notes.service';
import { PrismaClient } from '@prisma/client';

@Module({
  controllers: [NotesController],
  providers: [NotesService, PrismaClient],
  exports: [NotesService],
})
export class NotesModule {}
