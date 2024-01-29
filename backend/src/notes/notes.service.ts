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

    if (createNoteDto.categoryIds) {
      for (const categoryId of createNoteDto.categoryIds) {
        const category = await this.prisma.categories.findUnique({
          where: { id: categoryId, userId },
        });

        if (!category) {
          throw new NotFoundException(
            `Category with ID ${categoryId} not found or does not belong to the user`,
          );
        }
      }
    }

    const existingNote = await this.prisma.notes.findFirst({
      where: { title: createNoteDto.title, userId },
    });

    if (existingNote) {
      throw new ConflictException('Note exists');
    }

    try {
      const createdNote = await this.prisma.notes.create({
        data: {
          title: createNoteDto.title,
          content: createNoteDto.content,
          userId,
        },
      });

      // Create NoteCategories records for each category
      if (createNoteDto.categoryIds && createNoteDto.categoryIds.length > 0) {
        console.log('test');
        for (const categoryId of createNoteDto.categoryIds) {
          await this.prisma.noteCategories.create({
            data: {
              noteId: createdNote.id,
              categoryId,
            },
          });
        }
      }

      const noteWithoutInfo = plainToClass(NoteEntity, {
        id: createdNote.id,
        title: createdNote.title,
        content: createdNote.content,
        created_at: createdNote.created_at,
      });

      return noteWithoutInfo;
    } catch (error) {
      throw new InternalServerErrorException('Failed to create note');
    }
  }

  async update(userId: number, id: number, updateNoteDto: CreateNoteDto) {
    const note = await this.prisma.notes.findUnique({
      where: { id, userId },
      include: { NoteCategories: true }, // Pobieramy powiązane kategorie
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    if (note.userId !== userId) {
      throw new ForbiddenException('Note does not belong to the user');
    }

    try {
      // Aktualizujemy notatkę (bez uwzględniania categoryId)
      const updatedNote = await this.prisma.notes.update({
        where: { id },
        data: {
          title: updateNoteDto.title,
          content: updateNoteDto.content,
        },
      });

      // Jeśli przesłano categoryId, to aktualizujemy wpis w tabeli pośredniczącej
      if (
        updateNoteDto.categoryIds !== undefined &&
        updateNoteDto.categoryIds.length > 0
      ) {
        // Usuwamy poprzednie wpisy w tabeli pośredniczącej
        await this.prisma.noteCategories.deleteMany({
          where: { noteId: id },
        });

        // Dodajemy nowy wpis w tabeli pośredniczącej
        for (const categoryId of updateNoteDto.categoryIds) {
          await this.prisma.noteCategories.create({
            data: {
              noteId: id,
              categoryId,
            },
          });
        }
      } else {
        // Jeśli categoryId nie jest przesłane, usuwamy wszelkie powiązania w tabeli pośredniczącej
        await this.prisma.noteCategories.deleteMany({
          where: { noteId: id },
        });
      }

      return plainToClass(NoteEntity, {
        id: updatedNote.id,
        title: updatedNote.title,
        content: updatedNote.content,
        created_at: updatedNote.created_at,
      });
    } catch (error) {
      throw new InternalServerErrorException('Failed to update note');
    }
  }

  async remove(
    userId: number,
    id: number,
  ): Promise<{ success: boolean; message?: string }> {
    const note = await this.prisma.notes.findUnique({
      where: { id, userId },
      include: { NoteCategories: true, Reminders: true }, // Pobieramy powiązane kategorie
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    if (note.userId !== userId) {
      throw new ForbiddenException('Note does not belong to the user');
    }

    try {
      // Usuwamy powiązania z tabeli pośredniczącej, jeśli istnieją
      if (note.NoteCategories.length > 0) {
        await this.prisma.noteCategories.deleteMany({
          where: { noteId: id },
        });
      }

      if (note.Reminders.length > 0) {
        await this.prisma.reminders.deleteMany({
          where: { noteId: id },
        });
      }

      // Usuwamy notatkę
      await this.prisma.notes.delete({ where: { id } });

      return { success: true, message: 'Note removed successfully.' };
    } catch (error) {
      throw new InternalServerErrorException('Failed to delete note');
    }
  }

  async findAll(userId: number) {
    return this.prisma.notes.findMany({
      where: { userId },
      include: { NoteCategories: true },
    });
  }

  async findOne(userId: number, id: number) {
    const note = await this.prisma.notes.findUnique({
      where: { id, userId },
      include: { NoteCategories: true },
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    return note;
  }

  async findAllByCategory(userId: number, categoryId: number) {
    const category = await this.prisma.categories.findUnique({
      where: { id: categoryId, userId },
    });

    console.log(category);

    if (!category) {
      throw new NotFoundException(
        `Category with ID ${categoryId} not found or does not belong to the user`,
      );
    }
    try {
      const notes = await this.prisma.notes.findMany({
        where: {
          userId,
          NoteCategories: {
            some: {
              categoryId,
            },
          },
        },
      });

      return notes.map((note) => plainToClass(NoteEntity, note));
    } catch (error) {
      throw new InternalServerErrorException(
        'Failed to fetch notes by category',
      );
    }
  }
}
