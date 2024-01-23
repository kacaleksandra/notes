import {
  ConflictException,
  ForbiddenException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { CreateNoteDto } from './dto/create-note.dto';
import { isUserExist } from 'common/validation/isUserExist';
import { plainToClass } from 'class-transformer';
import { NoteEntity } from './entities/note.entity';

@Injectable()
export class NotesService {
  constructor(private prisma: PrismaClient) {}

  async create(userId: number, createNoteDto: CreateNoteDto) {
    isUserExist(this.prisma, userId);

    const category = await this.prisma.categories.findUnique({
      where: { id: createNoteDto.categoryId, userId },
    });

    if (!category) {
      throw new NotFoundException(
        'Category not found or does not belong to the user',
      );
    }

    const existingNote = await this.prisma.notes.findFirst({
      where: { title: createNoteDto.title, userId },
    });

    if (existingNote) {
      throw new ConflictException('Note exists');
    }

    try {
      const createdNote = await this.prisma.notes.create({
        data: { ...createNoteDto, userId },
      });

      const noteWithoutInfo = plainToClass(NoteEntity, {
        id: createdNote.id,
        title: createdNote.title,
      });

      return noteWithoutInfo;
    } catch (error) {
      throw new InternalServerErrorException('Failed to create note');
    }
  }

  async update(userId: number, id: number, updateNoteDto: CreateNoteDto) {
    const note = await this.prisma.notes.findUnique({
      where: { id, userId },
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    if (note.userId !== userId) {
      throw new ForbiddenException('Note does not belong to the user');
    }

    try {
      const updatedNote = await this.prisma.notes.update({
        where: { id },
        data: updateNoteDto,
      });

      return plainToClass(NoteEntity, {
        id: updatedNote.id,
        title: updatedNote.title,
        content: updatedNote.content,
        categoryId: updatedNote.categoryId,
      });
    } catch (error) {
      throw new InternalServerErrorException('Failed to update note');
    }
  }

  async remove(userId: number, id: number) {
    const note = await this.prisma.notes.findUnique({
      where: { id, userId },
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    if (note.userId !== userId) {
      throw new ForbiddenException('Note does not belong to the user');
    }

    try {
      await this.prisma.notes.delete({ where: { id } });
    } catch (error) {
      throw new InternalServerErrorException('Failed to delete note');
    }
  }

  findAll(userId: number) {
    return this.prisma.notes.findMany({
      where: { userId },
      select: { id: true, title: true, content: true, categoryId: true },
    });
  }

  async findOne(userId: number, id: number) {
    const note = await this.prisma.notes.findUnique({
      where: { id, userId },
      select: { id: true, title: true, content: true, categoryId: true },
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    return note;
  }
}
