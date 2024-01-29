import { Notes } from '@prisma/client';
import { Exclude } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

export class NoteEntity implements Notes {
  constructor(partial: Partial<NoteEntity>) {
    Object.assign(this, partial);
  }
  @ApiProperty()
  id: number;
  @ApiProperty()
  title: string;
  @ApiProperty()
  content: string;
  @ApiProperty()
  created_at: Date;
  @Exclude()
  userId: number;
}
