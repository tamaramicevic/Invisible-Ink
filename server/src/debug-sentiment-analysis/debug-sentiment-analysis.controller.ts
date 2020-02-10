import { Controller, Get, Req } from '@nestjs/common';
import { Request } from 'express';
import { TextAnalyticsService } from '../text-analytics/text-analytics.service';

@Controller('debug-sentiment-analysis')
export class DebugSentimentAnalysisController {
    constructor(private readonly textAnalyticsService: TextAnalyticsService) {}
    @Get()
    async AnalyzeSentiment(@Req() request: Request) {
        // This will need to be typed in real code
        // tslint:disable-next-line
        const tokens: string[] = request.body['tokens']; 
        return await this.textAnalyticsService.AnalyzeSentimentScores(tokens);
  }

}
