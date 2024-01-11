import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.enableCors(); //public api, we open for everyone
  app.setGlobalPrefix('api');
  const config = new DocumentBuilder()
    .setTitle('Notes app')
    .setDescription('The notes API description')
    .setVersion('1.0')
    .addBearerAuth()
    .addTag('notes')
    .build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api', app, document);

  await app.listen(3000);
}
bootstrap();
