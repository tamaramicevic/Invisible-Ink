import { Controller, Post, Req } from '@nestjs/common';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { Note } from 'src/shared/models/note';
import { Vote } from 'src/shared/models/vote';
import { VoteErrorCondition, VoteResponse } from './models/vote-response';

@Controller('vote')
export class VoteController {

    constructor(private readonly azureCosmosService: AzureCosmosDbService) {}

    @Post()
    async VoteOnNote(@Req() request): Promise<VoteResponse> {
        const voteRequest: Vote = JSON.parse(JSON.stringify(request.body));

        // tslint:disable-next-line
        console.log('Received Report request with the following params:');
        // tslint:disable-next-line
        console.dir(voteRequest);

        try {
            const note: Note = await this.azureCosmosService.GetNoteById(voteRequest.NoteId);

            if (note === null) {
                return { success: false, error: VoteErrorCondition.NOTE_NON_EXISTENT };
            }
        } catch (error) {
            return { success: false, error: VoteErrorCondition.UPLOAD_FAILED };
        }

        try {
            await this.azureCosmosService.VoteNote(voteRequest);
            return { success: true };
        } catch (error) {
            return { success: false, error: VoteErrorCondition.UPLOAD_FAILED };
        }
    }
}
