import { ApiProperty } from '@nestjs/swagger';
import { Categories } from '@prisma/client';
import { Exclude } from 'class-transformer';

export class CategoryEntity implements Categories {
  constructor(partial: Partial<CategoryEntity>) {
    Object.assign(this, partial);
  }

  @ApiProperty()
  id: number;

  @ApiProperty()
  title: string;

  @Exclude()
  userId: number;
}
