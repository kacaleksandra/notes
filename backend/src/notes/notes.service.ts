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

  private formatNote(note: any) {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { NoteCategories, userId: noteUserId, ...rest } = note;
    return {
      ...rest,
      categories: NoteCategories.map((category) => category.categoryId),
    };
  }

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
      include: { NoteCategories: true },
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
        data: {
          title: updateNoteDto.title,
          content: updateNoteDto.content,
        },
      });

      if (
        updateNoteDto.categoryIds !== undefined &&
        updateNoteDto.categoryIds.length > 0
      ) {
        await this.prisma.noteCategories.deleteMany({
          where: { noteId: id },
        });

        for (const categoryId of updateNoteDto.categoryIds) {
          await this.prisma.noteCategories.create({
            data: {
              noteId: id,
              categoryId,
            },
          });
        }
      } else {
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
      include: { NoteCategories: true, Reminders: true },
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    if (note.userId !== userId) {
      throw new ForbiddenException('Note does not belong to the user');
    }

    try {
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

      await this.prisma.notes.delete({ where: { id } });

      return { success: true, message: 'Note removed successfully.' };
    } catch (error) {
      throw new InternalServerErrorException('Failed to delete note');
    }
  }

  async findAll(userId: number) {
    const notes = await this.prisma.notes.findMany({
      where: { userId },
      include: { NoteCategories: { select: { categoryId: true } } },
    });

    const formattedNotes = notes.map((note) => this.formatNote(note));

    return formattedNotes;
  }

  async findOne(userId: number, id: number) {
    const note = await this.prisma.notes.findUnique({
      where: { id, userId },
      include: { NoteCategories: true },
    });

    if (!note) {
      throw new NotFoundException('Note not found');
    }

    const formattedNote = this.formatNote(note);
    return formattedNote;
  }

  async findAllByCategory(userId: number, categoryId: number) {
    const category = await this.prisma.categories.findUnique({
      where: { id: categoryId, userId },
    });

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
