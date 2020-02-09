import { Module } from '@nestjs/common';
import { TextAnalyticsService } from './text-analytics.service';

@Module({
  providers: [TextAnalyticsService],
})
export class TextAnalyticsModule {}
