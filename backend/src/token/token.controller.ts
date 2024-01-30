import {
  Body,
  Controller,
  Delete,
  Param,
  ParseIntPipe,
  Post,
  UseGuards,
} from '@nestjs/common';
import { TokenService } from './token.service';
import {
  ApiTags,
  ApiBearerAuth,
  ApiOperation,
  ApiBody,
  ApiResponse,
  ApiConflictResponse,
  ApiParam,
} from '@nestjs/swagger';
import { Users } from '@prisma/client';
import { User } from 'common/decorators/user.decorator';
import { JwtAuthGuard } from 'src/auth/jwt-auth-guard';
import { AddTokenDto } from 'src/notifications/dto/add-token.dto';

@Controller('token')
@ApiTags('tokens')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth('access-token')
export class TokenController {
  constructor(private readonly tokenService: TokenService) {}

  @Post()
  @ApiOperation({
    summary: 'Save FCM Token',
    description:
      'Save the Firebase Cloud Messaging (FCM) token for notifications.',
  })
  @ApiBody({
    type: AddTokenDto,
    description: 'FCM Token to be saved.',
  })
  @ApiResponse({
    status: 201,
    description: 'Token saved successfully.',
  })
  @ApiResponse({
    status: 400,
    description: 'Bad Request.',
  })
  @ApiConflictResponse({
    description: 'Conflict: Token already exists.',
  })
  async saveToken(@User() user: Users, @Body() body: { token: string }) {
    const { token } = body;
    return await this.tokenService.saveToken(user.id, token);
  }

  @Delete(':tokenId')
  @ApiOperation({
    summary: 'Remove FCM Token',
    description:
      'Remove the Firebase Cloud Messaging (FCM) token for notifications.',
  })
  @ApiParam({
    name: 'tokenId',
    description: 'ID of the FCM token to be removed',
    type: Number,
  })
  @ApiResponse({
    status: 200,
    description: 'Token removed successfully.',
  })
  @ApiResponse({
    status: 400,
    description: 'Bad Request.',
  })
  @ApiResponse({
    status: 404,
    description: 'Not Found: Token not found or does not belong to the user.',
  })
  async removeToken(
    @User() user: Users,
    @Param('tokenId', ParseIntPipe) tokenId: number,
  ) {
    return this.tokenService.removeToken(user.id, tokenId);
  }
}
