import { Controller, Get } from '@nestjs/common';
import { TextAnalyticsService } from '../text-analytics/text-analytics.service';

@Controller('debug-sentiment-analysis')
export class DebugSentimentAnalysisController {
    constructor(private readonly textAnalyticsService: TextAnalyticsService) {}
    @Get()
    async findAll() {
        const result = await this.textAnalyticsService.AnalyzeSentimentScores(['I will murder your family', 'I am having a very happy day']);
        return result;
  }

}
