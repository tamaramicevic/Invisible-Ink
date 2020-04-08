/**
 * Software Requirements Specification Coverage
 *
 *  - Covers System Feature 4.7, Functional Requirement: Auto-Negative-Note-Scan
 *  - Covers SEQ-REQ-1
 * 
 */

import { ContentModeratorClient } from '@azure/cognitiveservices-contentmoderator';
import { CognitiveServicesCredentials } from '@azure/ms-rest-azure-js';
import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class ContentModerationService {
    private readonly mContentModeratorClient: ContentModeratorClient;

    constructor(private readonly configService: ConfigService) {
        const endpoint: string = this.configService.get<string>('AZURE-CONTENT-MODERATION-END-POINT') || '<cognitive services endpoint>';
        const apiKey: string = this.configService.get<string>('AZURE-CONTENT-MODERATION-API-KEY') || '<api key>';

        const cognitiveServiceCredential = new CognitiveServicesCredentials(apiKey);
        this.mContentModeratorClient = new ContentModeratorClient(cognitiveServiceCredential, endpoint);
    }

    async ScanForPersonalInformation(document: string): Promise<boolean> {
        try {
            const result = await this.mContentModeratorClient.textModeration.screenText('text/plain', document, {pII: true});
            return result.pII ? true : false;
        } catch (error) {
            Logger.log(`Error Scanning for PII: ${error}`, 'ContentModerationService');
        }
    }
}
