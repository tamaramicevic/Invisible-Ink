import { Controller, Post, Req } from '@nestjs/common';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { ReportedNoteSchema } from 'src/azure-db/models/reported-note-schema';
import { Note } from 'src/shared/models/note';
import { ReportNoteRequest } from './models/report-note-request';
import { ReportNoteErrorCondition, ReportNoteResponse } from './models/report-note-response';

@Controller('report')
export class ReportController {
    constructor(private readonly azureCosmosService: AzureCosmosDbService) { }

    @Post()
    async ReportNote(@Req() request): Promise<ReportNoteResponse> {

        const reportNoteRequest: ReportNoteRequest = JSON.parse(JSON.stringify(request.body));

        // tslint:disable-next-line
        console.log('Received Report request with the following params:');
        // tslint:disable-next-line
        console.dir(reportNoteRequest);

        try {
            const note: Note = await this.azureCosmosService.GetNoteById(reportNoteRequest.NoteId);

            if (note === null) {
                return { success: false, error: ReportNoteErrorCondition.NOTE_NON_EXISTENT };
            }
        } catch (error) {
            return { success: false, error: ReportNoteErrorCondition.UPLOAD_FAILED };
        }
        
        try {
            await this.azureCosmosService.ReportNote(reportNoteRequest as ReportedNoteSchema);
            return { success: true };
            
        } catch (error) {
            return { success: false, error: ReportNoteErrorCondition.UPLOAD_FAILED };
        }
    }
}
