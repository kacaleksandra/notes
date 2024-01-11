// src/users/entities/user.entity.ts
import { ApiProperty } from '@nestjs/swagger';
import { Users } from '@prisma/client';
import { Exclude } from 'class-transformer';

export class UserEntity implements Users {
  constructor(partial: Partial<UserEntity>) {
    Object.assign(this, partial);
  }

  @ApiProperty()
  id: number;

  @ApiProperty()
  email: string;

  @Exclude()
  password: string;
}
