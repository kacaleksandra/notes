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
  ApiOperation,
  ApiResponse,
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
  @ApiOperation({
    summary: 'Create category',
    description: 'Create a new category.',
  })
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
  @ApiOperation({
    summary: 'Delete category by id',
    description: 'Delete a category by id.',
  })
  @ApiResponse({ status: 204, description: 'Category deleted successfully' })
  @ApiResponse({ status: 404, description: 'Category not found' })
  @ApiResponse({
    status: 403,
    description: 'Forbidden: Category does not belong to the user',
  })
  async remove(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    await this.categoriesService.remove(user.id, id);
  }

  @Get()
  @ApiOkResponse({ type: [CategoryEntity] })
  @ApiResponse({ status: 200, description: 'OK', type: [CategoryEntity] })
  @ApiOperation({
    summary: 'Get all categories',
    description: 'Get all categories.',
  })
  async findAll(@User() user: Users) {
    return this.categoriesService.findAll(user.id);
  }

  @Get(':id')
  @ApiOkResponse({ type: CategoryEntity })
  @ApiOperation({
    summary: 'Get category by id',
    description: 'Get category by specific id.',
  })
  @ApiResponse({ status: 200, description: 'OK', type: CategoryEntity })
  @ApiResponse({ status: 404, description: 'Category not found' })
  async findOne(@User() user: Users, @Param('id', ParseIntPipe) id: number) {
    return this.categoriesService.findOne(user.id, id);
  }
}
