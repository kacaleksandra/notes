// categories.controller.ts (przykład użycia w kontrolerze)
import {
  Controller,
  Post,
  Body,
  UseGuards,
  Get,
  Param,
  ParseIntPipe,
  Delete,
  HttpCode,
} from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth-guard';
import { CategoriesService } from './categories.service';
import { Users } from '@prisma/client';
import {
  ApiBearerAuth,
  ApiBody,
  ApiCreatedResponse,
  ApiOkResponse,
  ApiTags,
} from '@nestjs/swagger';
import { CategoryEntity } from './entities/category.entity';
import { CreateCategoryDto } from './dto/create-category.dto';
import { User } from 'common/decorators/user.decorator';

@Controller('categories')
@ApiTags('categories')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth('access-token')
export class CategoriesController {
  constructor(private readonly categoriesService: CategoriesService) {}

  @Post()
  @ApiCreatedResponse({ type: CategoryEntity })
  @ApiBody({
    type: CreateCategoryDto,
    description: 'Example of creating a category',
  })
  async create(
    @Body() createCategoryDto: CreateCategoryDto,
    @User() user: Users,
  ) {
    return new CategoryEntity(
      await this.categoriesService.create(user.id, createCategoryDto),
    );
  }

  @Delete(':id')
  @HttpCode(204)
  async remove(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    await this.categoriesService.remove(user.id, id);
  }

  @Get()
  @ApiOkResponse({ type: [CategoryEntity] })
  async findAll(@User() user: Users) {
    return this.categoriesService.findAll(user.id);
  }

  @Get(':id')
  @ApiOkResponse({ type: CategoryEntity })
  async findOne(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    return this.categoriesService.findOne(user.id, id);
  }
}
