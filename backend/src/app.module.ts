import { Module } from '@nestjs/common';
import { AuthModule } from './auth/auth.module';
import { CategoriesModule } from './categories/categories.module';
import { NotesModule } from './notes/notes.module';
import { NotificationModule } from './notification/notification.module';
import { ScheduleAppModule } from './schedule/schedule.module';
import { TokenModule } from './token/token.module';

@Module({
  imports: [
    AuthModule,
    CategoriesModule,
    NotesModule,
    NotificationModule,
    ScheduleAppModule,
    TokenModule,
  ],
})
export class AppModule {}
