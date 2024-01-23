// user.decorator.ts
import { createParamDecorator, ExecutionContext } from '@nestjs/common';
import { Users } from '@prisma/client';

export const User = createParamDecorator(
  (data: unknown, context: ExecutionContext): Users => {
    const request = context.switchToHttp().getRequest();
    return request.user;
  },
);
