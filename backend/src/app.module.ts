import { Module } from '@nestjs/common';
import { AuthModule } from './auth/auth.module';
import { CategoriesModule } from './categories/categories.module';
import { NotesModule } from './notes/notes.module';

@Module({
  imports: [AuthModule, CategoriesModule, NotesModule],
})
export class AppModule {}
