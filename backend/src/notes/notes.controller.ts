import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  Param,
  ParseIntPipe,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
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
  @HttpCode(204)
  @ApiOperation({
    summary: 'Delete note by id',
    description: 'Delete a note by id.',
  })
  async remove(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    await this.notesService.remove(user.id, id);
  }

  @Get()
  @ApiOkResponse({ type: [NoteEntity] })
  @ApiOperation({
    summary: 'Get all notes',
    description: 'Retrieve a list of all notes for logged in user.',
  })
  async findAll(@User() user: Users) {
    return this.notesService.findAll(user.id);
  }

  @Get(':id')
  @ApiOkResponse({ type: NoteEntity })
  @ApiOperation({
    summary: 'Get note by id',
    description: 'Retrieve a list of all notes.',
  })
  async findOne(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    return this.notesService.findOne(user.id, id);
  }

  @Get('by-category/:categoryId')
  @ApiOperation({
    summary: 'Get notes by category id',
    description: 'Retrieve a list of all notes by category id.',
  })
  async findAllByCategory(
    @User() user: Users,
    @Param('categoryId', ParseIntPipe) categoryId: number,
  ) {
    return this.notesService.findAllByCategory(user.id, categoryId);
  }
}
