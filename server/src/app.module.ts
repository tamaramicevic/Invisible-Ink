import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { TextAnalyticsModule } from './text-analytics/text-analytics.module';

@Module({
  imports: [TextAnalyticsModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
