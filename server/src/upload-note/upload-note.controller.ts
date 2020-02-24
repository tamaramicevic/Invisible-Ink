import { Controller, Post, Req } from '@nestjs/common';
import { UploadNoteRequest } from './models/upload-note-request';
import { ErrorCondition, UploadNoteResponse } from './models/upload-note-response';

@Controller('upload-note')
export class UploadNoteController {
    @Post()
    async UploadNote(@Req() request: UploadNoteRequest): Promise<UploadNoteResponse> {
        // tslint:disable-next-line
        console.log('Request received with following parameters\n');
        // tslint:disable-next-line
        console.dir(request.body);
        const requestBody: UploadNoteRequest = JSON.parse(JSON.stringify(request.body));

        if (requestBody.lat > 1000 && requestBody.long > 1000) {
            return {success: false, error: ErrorCondition.UPLOAD_FAILED };
        }
        if (requestBody.lat > 1000) {
            return {success: false, error: ErrorCondition.BAD_SENTIMENT_DETECTED };
        }
        if (requestBody.long > 1000) {
            return {success: false, error: ErrorCondition.PII_DETECTED};
        }

        return {success: true} as UploadNoteResponse;
    }
}
