import { AnalyzeSentimentSuccessResult, CognitiveServicesCredential, TextAnalyticsClient} from '@azure/ai-text-analytics';
import { Injectable } from '@nestjs/common';
import { ConfigService} from '@nestjs/config';
import { SentimentAnalysisResult } from './models/sentiment-analysis-result';

@Injectable()
export class TextAnalyticsService {
    private readonly mTextAnalyticsClient: TextAnalyticsClient;
    constructor(private readonly configService: ConfigService) {

        const endpoint: string = this.configService.get<string>('AZURE-TEXT-ANALYTICS-API-END-POINT') || '<cognitive services endpoint>';
        const apiKey: string = this.configService.get<string>('AZURE-TEXT-ANALYTICS-API-KEY') || '<api key>';

        this.mTextAnalyticsClient = new TextAnalyticsClient(endpoint, new CognitiveServicesCredential(apiKey));
    }
    // TODO: all calls to console should be replaced by logger
    async AnalyzeSentimentScores(documents: string[]): Promise<SentimentAnalysisResult[]> {
        try {
            const sentimentAnalysisResult = await this.mTextAnalyticsClient.analyzeSentiment(documents);
            const result = sentimentAnalysisResult.map(
                item => {
                    const successResult = item as AnalyzeSentimentSuccessResult;
                    return {
                        sentiment: successResult.sentiment,
                        positive: successResult.documentScores.positive,
                        negative: successResult.documentScores.negative,
                        neutral: successResult.documentScores.neutral,
                    } as SentimentAnalysisResult;
                },
            ).filter(item => !!item);
            return result;
        } catch (error) {
            // tslint:disable-next-line
            console.debug('Error analyzing sentiment: ', error);
        }
    }
}
