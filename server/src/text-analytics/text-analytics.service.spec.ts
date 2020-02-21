import { ConfigService } from '@nestjs/config';
import { Test, TestingModule } from '@nestjs/testing';
import { TextAnalyticsService } from './text-analytics.service';

describe('TextAnalyticsService', () => {
  let service: TextAnalyticsService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [TextAnalyticsService, ConfigService],
    }).compile();

    service = module.get<TextAnalyticsService>(TextAnalyticsService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
