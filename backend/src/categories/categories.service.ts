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

    try {
      const createdCategory = await this.prisma.categories.create({
        data: { ...createCategoryDto, userId },
      });

      const categoryWithoutUserId = plainToClass(CategoryEntity, {
        id: createdCategory.id,
        title: createdCategory.title,
      });

      return categoryWithoutUserId;
    } catch (error) {
      throw new InternalServerErrorException('Failed to create category');
    }
  }

  async remove(userId: number, categoryId: number) {
    return this.prisma.$transaction(async (prisma) => {
      const category = await prisma.categories.findUnique({
        where: { id: categoryId, userId },
      });

      if (!category) {
        throw new NotFoundException('Category not found');
      }

      if (category.userId !== userId) {
        throw new ForbiddenException('Category does not belong to the user');
      }

      try {
        await prisma.noteCategories.deleteMany({ where: { categoryId } });
        await prisma.categories.delete({ where: { id: categoryId } });
      } catch (error) {
        throw new InternalServerErrorException('Failed to delete category');
      }
    });
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
      throw new NotFoundException('Category not found');
    }

    return category;
  }
}
