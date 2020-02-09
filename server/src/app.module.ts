import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DebugSentimentAnalysisController } from './debug-sentiment-analysis/debug-sentiment-analysis.controller';
import { TextAnalyticsModule } from './text-analytics/text-analytics.module';
import { TextAnalyticsService } from './text-analytics/text-analytics.service';

@Module({
  imports: [
    TextAnalyticsModule,
    ConfigModule.forRoot({
      isGlobal: true,
    }),
  ],
  controllers: [AppController, DebugSentimentAnalysisController],
  providers: [AppService, TextAnalyticsService],
})
export class AppModule {}
