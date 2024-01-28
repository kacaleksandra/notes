import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsNotEmpty, IsNumber, IsOptional, IsString } from 'class-validator';

export class CreateNoteDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  title: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  content: string;

  @ApiPropertyOptional({
    type: [Number],
    example: [1, 2], // Przykładowe dane dla categoryIds
  })
  @IsOptional()
  @IsNumber({}, { each: true })
  categoryIds?: number[];
}
