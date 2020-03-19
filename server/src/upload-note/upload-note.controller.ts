import { Controller, Post, Req } from '@nestjs/common';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { Note } from '../shared/models/note';
import { UploadNoteRequest } from './models/upload-note-request';
import { ErrorCondition, UploadNoteResponse } from './models/upload-note-response';

@Controller('upload-note')
export class UploadNoteController {
    // TODO: Add interceptor for ErrorCondition.BAD_SENTIMENT_DETECTED
    // TODO: Add interceptor for ErrorCondition.PII_DETECTED

    constructor(private readonly azureCosmosDbService: AzureCosmosDbService) {}
    @Post()
    async UploadNote(@Req() request): Promise<UploadNoteResponse> {
        // tslint:disable-next-line
        console.log('Request received with following parameters\n');
        // tslint:disable-next-line
        console.dir(request.body);
        const requestBody: UploadNoteRequest = JSON.parse(JSON.stringify(request.body));
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
            await this.azureCosmosDbService.UploadNote(note);
        } catch (error) {
            return {success: false, error: ErrorCondition.UPLOAD_FAILED };
        }

        return {success: true} as UploadNoteResponse;
    }
}
