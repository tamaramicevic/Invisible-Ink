import { Test, TestingModule } from '@nestjs/testing';
import { DebugSentimentAnalysisController } from './debug-sentiment-analysis.controller';

describe('DebugSentimentAnalysis Controller', () => {
  let controller: DebugSentimentAnalysisController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [DebugSentimentAnalysisController],
    }).compile();

    controller = module.get<DebugSentimentAnalysisController>(DebugSentimentAnalysisController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
