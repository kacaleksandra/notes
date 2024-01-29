import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class AddReminderDto {
  @ApiProperty({
    description: 'Reminder date',
    type: String,
  })
  @IsNotEmpty()
  @IsString()
  date: string;

  @ApiProperty({
    description: 'Note ID',
    type: Number,
  })
  @IsNotEmpty()
  @IsNumber()
  noteId: number;
}
