import { Controller, Post, Req } from '@nestjs/common';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { ContentModerationService } from 'src/content-moderation/content-moderation.service';
import { TextAnalyticsService } from 'src/text-analytics/text-analytics.service';
import { Note } from '../shared/models/note';
import { UploadNoteRequest } from './models/upload-note-request';
import { ErrorCondition, UploadNoteResponse } from './models/upload-note-response';

@Controller('upload')
export class UploadNoteController {
    // TODO: Add interceptor for ErrorCondition.BAD_SENTIMENT_DETECTED
    // TODO: Add interceptor for ErrorCondition.PII_DETECTED

    constructor(
        private readonly azureCosmosDbService: AzureCosmosDbService,
        private readonly textAnalyticsService: TextAnalyticsService, 
        private readonly contentModeratorService: ContentModerationService) {}
    @Post()
    async UploadNote(@Req() request): Promise<UploadNoteResponse> {
        // tslint:disable-next-line
        console.log('Request received with following parameters\n');
        // tslint:disable-next-line
        console.dir(request.body);
        const requestBody: UploadNoteRequest = JSON.parse(JSON.stringify(request.body));
        
        // Check for bad sentiment
        if (await this.textAnalyticsService.ScanForBadSentiment([requestBody.title, requestBody.body])) {
            return { success: false, error: ErrorCondition.BAD_SENTIMENT_DETECTED };
        }

        // Check for Personally Identifiable Information
        const scanString: string = [requestBody.title, requestBody.body].join(', ');
        if (await this.contentModeratorService.ScanForPersonalInformation(scanString)) {
            return { success: false, error: ErrorCondition.PII_DETECTED };
        }
        
        const note: Note = {
            NoteId: null, // this will get generated on the DB
            Title: requestBody.title,
            Body: requestBody.body,
            Expiration: requestBody.expiration,
            Score: 0,
            Lat: requestBody.location.latitude,
            Lon: requestBody.location.longitude,
        };
        try {
            const noteId: string = await this.azureCosmosDbService.UploadNote(note);
            return {success: true, noteId } as UploadNoteResponse;
        } catch (error) {
            return {success: false, error: ErrorCondition.UPLOAD_FAILED };
        }

    }
}
