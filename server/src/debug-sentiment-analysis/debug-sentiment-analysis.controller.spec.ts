import { ConfigService } from '@nestjs/config';
import { Test, TestingModule } from '@nestjs/testing';
import { TextAnalyticsService } from '../text-analytics/text-analytics.service';
import { DebugSentimentAnalysisController } from './debug-sentiment-analysis.controller';

describe('DebugSentimentAnalysis Controller', () => {
  let controller: DebugSentimentAnalysisController;
  let service: TextAnalyticsService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [DebugSentimentAnalysisController],
      providers: [TextAnalyticsService, ConfigService],
    }).compile();

    controller = module.get<DebugSentimentAnalysisController>(DebugSentimentAnalysisController);
    service = module.get<TextAnalyticsService>(TextAnalyticsService);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
