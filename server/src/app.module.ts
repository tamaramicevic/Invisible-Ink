import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DebugSentimentAnalysisController } from './debug-sentiment-analysis/debug-sentiment-analysis.controller';
import { TextAnalyticsModule } from './text-analytics/text-analytics.module';
import { TextAnalyticsService } from './text-analytics/text-analytics.service';
import { CosmosDbModule } from './cosmos-db/cosmos-db.module';

@Module({
  imports: [
    TextAnalyticsModule,
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    CosmosDbModule,
  ],
  controllers: [AppController, DebugSentimentAnalysisController],
  providers: [AppService, TextAnalyticsService],
})
export class AppModule {}
