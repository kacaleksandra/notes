// src/users/users.service.ts
import { ConflictException, Injectable } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { PrismaClient } from '@prisma/client';
import * as bcrypt from 'bcryptjs';
import { UserEntity } from './entities/user.entity';
import { plainToClass } from 'class-transformer';

export const roundsOfHashing = 10;

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaClient) {}

  async create(createUserDto: CreateUserDto) {
    const existingUser = await this.prisma.users.findUnique({
      where: { email: createUserDto.email },
    });

    if (existingUser) {
      throw new ConflictException('User exists');
    }

    const hashedPassword = await bcrypt.hash(
      createUserDto.password,
      roundsOfHashing,
    );

    createUserDto.password = hashedPassword;

    const createdUser = await this.prisma.users.create({
      data: createUserDto,
    });

    const userWithoutPassword = plainToClass(UserEntity, {
      id: createdUser.id,
      email: createdUser.email,
    });

    return userWithoutPassword;
  }

  findAll() {
    return this.prisma.users.findMany();
  }

  findOne(id: number) {
    return this.prisma.users.findUnique({ where: { id } });
  }
}
