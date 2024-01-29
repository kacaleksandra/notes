import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  ParseIntPipe,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBadRequestResponse,
  ApiBearerAuth,
  ApiConflictResponse,
  ApiForbiddenResponse,
  ApiInternalServerErrorResponse,
  ApiNoContentResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { JwtAuthGuard } from 'src/auth/jwt-auth-guard';
import { NotesService } from './notes.service';
import { User } from 'common/decorators/user.decorator';
import { CreateNoteDto } from './dto/create-note.dto';
import { NoteEntity } from './entities/note.entity';
import { Users } from '@prisma/client';

@Controller('notes')
@ApiTags('notes')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth('access-token')
export class NotesController {
  constructor(private readonly notesService: NotesService) {}

  @Post()
  @ApiOperation({
    summary: 'Create note',
    description: 'Create a new note. CategoryIDs are optional.',
  })
  @ApiOkResponse({ type: NoteEntity })
  @ApiBadRequestResponse({ description: 'Bad Request: Invalid input data' })
  @ApiConflictResponse({ description: 'Conflict: Note exists' })
  async create(@Body() createNoteDto: CreateNoteDto, @User() user: Users) {
    return new NoteEntity(
      await this.notesService.create(user.id, createNoteDto),
    );
  }

  @Patch(':id')
  @ApiOperation({
    summary: 'Update note by id',
    description:
      'It is necessary to send all fields. If categoryIds are not provided, all category associations will be removed.',
  })
  @ApiOkResponse({ type: NoteEntity })
  @ApiNotFoundResponse({ description: 'Not Found: Note not found' })
  @ApiForbiddenResponse({
    description: 'Forbidden: Note does not belong to the user',
  })
  @ApiBadRequestResponse({ description: 'Bad Request: Invalid input data' })
  @ApiInternalServerErrorResponse({
    description: 'Internal Server Error: Failed to update note',
  })
  async update(
    @User() user: Users,
    @Param('id', ParseIntPipe) id: number,
    @Body() updateNoteDto: CreateNoteDto,
  ) {
    return new NoteEntity(
      await this.notesService.update(user.id, id, updateNoteDto),
    );
  }

  @Delete(':id')
  @ApiOperation({
    summary: 'Delete note by id',
    description: 'Delete a note by id.',
  })
  @ApiNoContentResponse({
    description: 'No Content: Note deleted successfully',
  })
  @ApiNotFoundResponse({ description: 'Not Found: Note not found' })
  @ApiForbiddenResponse({
    description: 'Forbidden: Note does not belong to the user',
  })
  @ApiInternalServerErrorResponse({
    description: 'Internal Server Error: Failed to delete note',
  })
  async remove(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    return await this.notesService.remove(user.id, id);
  }

  @Get()
  @ApiOperation({
    summary: 'Get all notes',
    description: 'Retrieve a list of all notes for logged in user.',
  })
  async findAll(@User() user: Users) {
    return this.notesService.findAll(user.id);
  }

  @Get(':id')
  @ApiOperation({
    summary: 'Get note by id',
    description: 'Retrieve a list of all notes.',
  })
  @ApiOkResponse({ status: 401, description: 'Unauthorized' })
  @ApiNotFoundResponse({ description: 'Not Found: Note not found' })
  @ApiOkResponse({ status: 401, description: 'Unauthorized' })
  async findOne(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    return this.notesService.findOne(user.id, id);
  }

  @Get('by-category/:categoryId')
  @ApiOperation({
    summary: 'Get notes by category id',
    description: 'Retrieve a list of all notes by category id.',
  })
  @ApiOkResponse({
    type: [NoteEntity],
    description: 'List of notes by category id',
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiNotFoundResponse({
    description: 'Category not found or does not belong to the user',
  })
  @ApiInternalServerErrorResponse({
    description: 'Failed to fetch notes by category',
  })
  async findAllByCategory(
    @User() user: Users,
    @Param('categoryId', ParseIntPipe) categoryId: number,
  ) {
    return this.notesService.findAllByCategory(user.id, categoryId);
  }
}
