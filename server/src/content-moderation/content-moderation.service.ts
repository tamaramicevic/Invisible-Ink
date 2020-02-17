import { CognitiveServicesCredentials } from '@azure/ms-rest-azure-js';
import { Injectable } from '@nestjs/common';
import { ConfigService} from '@nestjs/config';
import { ContentModeratorClient } from 'azure-cognitiveservices-contentmoderator';

@Injectable()
export class ContentModerationService {
    private readonly mContentModeratorClient: ContentModeratorClient;
    constructor(private readonly configService: ConfigService) {

        const endpoint: string = this.configService.get<string>('AZURE-CONTENT-MODERATOR-API-END-POINT') || '<content moderator endpoint>';
        const apiKey: string = this.configService.get<string>('AZURE-CONTENT-MODERATOR-API-KEY') || '<api key>';

        this.mContentModeratorClient = new ContentModeratorClient(new CognitiveServicesCredentials(apiKey), endpoint);
    }
}
