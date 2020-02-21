import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DebugSentimentAnalysisController } from './debug-sentiment-analysis/debug-sentiment-analysis.controller';
import { ReportController } from './report/report.controller';
import { RetrieveNotesController } from './retrieve-notes/retrieve-notes.controller';
import { TextAnalyticsModule } from './text-analytics/text-analytics.module';
import { TextAnalyticsService } from './text-analytics/text-analytics.service';
import { UploadImageController } from './upload-image/upload-image.controller';
import { UploadNoteController } from './upload-note/upload-note.controller';
import { VoteController } from './vote/vote.controller';

@Module({
  imports: [
    TextAnalyticsModule,
    ConfigModule.forRoot({
      isGlobal: true,
    }),
  ],
  controllers: [
    AppController, 
    DebugSentimentAnalysisController, 
    UploadNoteController, 
    UploadImageController, 
    RetrieveNotesController, 
    ReportController, VoteController,
  ],
  providers: [AppService, TextAnalyticsService],
})
export class AppModule {}
