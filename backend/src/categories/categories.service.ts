import {
  ConflictException,
  ForbiddenException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaClient } from '@prisma/client';
import { CreateCategoryDto } from './dto/create-category.dto';
import { isUserExist } from 'common/validation/isUserExist';
import { plainToClass } from 'class-transformer';
import { CategoryEntity } from './entities/category.entity';

@Injectable()
export class CategoriesService {
  constructor(private prisma: PrismaClient) {}

  async create(userId: number, createCategoryDto: CreateCategoryDto) {
    isUserExist(this.prisma, userId);

    const existingCategory = await this.prisma.categories.findFirst({
      where: { title: createCategoryDto.title, userId },
    });

    if (existingCategory) {
      throw new ConflictException('Category exists');
    }

    const createdCategory = await this.prisma.categories.create({
      data: { ...createCategoryDto, userId },
    });

    const categoryWithoutUserId = plainToClass(CategoryEntity, {
      id: createdCategory.id,
      title: createdCategory.title,
    });

    return categoryWithoutUserId;
  }

  async remove(userId: number, id: number) {
    const category = await this.prisma.categories.findUnique({
      where: { id, userId },
    });

    if (!category) {
      throw new NotFoundException('Category not found');
    }

    if (category.userId !== userId) {
      throw new ForbiddenException('Category does not belong to the user');
    }

    try {
      await this.prisma.categories.delete({ where: { id } });
    } catch (error) {
      throw new InternalServerErrorException('Failed to delete category');
    }
  }

  findAll(userId: number) {
    return this.prisma.categories.findMany({
      where: { userId },
      select: { id: true, title: true },
    });
  }

  async findOne(userId: number, id: number) {
    const category = await this.prisma.categories.findUnique({
      where: { id, userId },
      select: { id: true, title: true },
    });

    if (!category) {
      // Kategoria o podanym id nie istnieje
      throw new NotFoundException('Category not found');
    }

    return category;
  }
}
