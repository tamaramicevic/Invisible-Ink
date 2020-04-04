import { ConfigService } from '@nestjs/config';
import { Test, TestingModule } from '@nestjs/testing';
import { ContentModerationService } from './content-moderation.service';

describe('ContentModerationService', () => {
  let service: ContentModerationService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [ContentModerationService, ConfigService],
    }).compile();

    service = module.get<ContentModerationService>(ContentModerationService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
