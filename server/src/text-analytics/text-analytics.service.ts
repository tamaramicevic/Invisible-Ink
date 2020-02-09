import { AnalyzeSentimentResultCollection, CognitiveServicesCredential, TextAnalyticsClient} from '@azure/ai-text-analytics';
import { Injectable } from '@nestjs/common';
import { ConfigService} from '@nestjs/config';

@Injectable()
export class TextAnalyticsService {
    private readonly mTextAnalyticsClient: TextAnalyticsClient;
    constructor(private readonly configService: ConfigService) {

        const endpoint: string = this.configService.get<string>('AZURE-TEXT-ANALYTICS-API-END-POINT') || '<cognitive services endpoint>';
        const apiKey: string = this.configService.get<string>('AZURE-TEXT-ANALYTICS-API-KEY') || '<api key>';

        this.mTextAnalyticsClient = new TextAnalyticsClient(endpoint, new CognitiveServicesCredential(apiKey));
    }
    // TODO: all calls to console should be replaced by logger
    async AnalyzeSentimentScores(documents: string[]): Promise<AnalyzeSentimentResultCollection> {
        try {
            const sentimentAnalysisResult = this.mTextAnalyticsClient.analyzeSentiment(documents);
            return sentimentAnalysisResult;

        } catch (error) {
            console.debug('Error analyzing sentiment: ', error);
        }
    }
}
