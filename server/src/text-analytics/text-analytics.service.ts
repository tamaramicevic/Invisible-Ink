import { CognitiveServicesCredential, TextAnalyticsClient} from '@azure/ai-text-analytics';
import { Injectable } from '@nestjs/common';
import { ConfigService} from '@nestjs/config';


@Injectable()
export class TextAnalyticsService {
    constructor(private readonly configService: ConfigService) {
        console.debug('Initializing TextAnalyticsService\n');
        console.debug('API Key: ', this.configService.get<string>('AZURE-TEXT-ANALYTICS-API-KEY'), '\n');
        console.debug('Endpoint: ', this.configService.get<string>('AZURE-TEXT-ANALYTICS-API-END-POINT'), '\n');
    }
}
