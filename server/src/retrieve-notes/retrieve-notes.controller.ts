import { Controller, Get, Req } from '@nestjs/common';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { RetrieveNotesRequest } from './models/retrieve-notes-request';
import { RetrieveNotesResponse } from './models/retrieve-notes-response';

const notesResponse: RetrieveNotesResponse = {
    notes: 
    [
        {
            NoteId: '21',
            Title: 'Sample Title 1',
            Body: 'Sample Body 1',
            TimeStamp: '2020-02-26T23:02:06Z',
            Score: 32,
            Lat: 53.527381,
            Lon: -113.527821,
            ExpiresInHours: 24,
        },
        {
            NoteId: '42',
            Title: 'Sample Title 2',
            Body: 'Sample Body 2',
            TimeStamp: '2020-02-26T23:02:06Z',
            Score: 64,
            Lat: 53.527481, 
            Lon: -113.527821,
            ExpiresInHours: 24,
        },
        {
            NoteId: '661',
            Title: 'Do long titles look good in the UI? We should use this opportunity to find out don\'t you think? I think it might look weird',
            Body: 'Some random body, how big should we let the body be do you think?',
            TimeStamp: '2020-02-26T23:02:06Z',
            Score: 128,
            Lat: 53.527381, 
            Lon: 113.527851,
            ExpiresInHours: 24,
        },
        {
            NoteId: '133',
            Title: 'Sample Title 3',
            Body: 'Sample Body 3',
            TimeStamp: '2020-02-26T23:02:06Z',
            Score: 256,
            Lat: 53.527351, 
            Lon: -113.527421,
            ExpiresInHours: 24,
        },
        {
            NoteId: '252341',
            Title: 'Sample Title 4',
            Body: 'Sample Body 4',
            TimeStamp: '2020-02-26T23:02:06Z',
            Score: 512,
            Lat: 53.527381, 
            Lon: -113.527826,
            ExpiresInHours: 24,
        },

    ],
};

@Controller('retrieve-notes')
export class RetrieveNotesController {

    constructor(private readonly azureCosmosDbService: AzureCosmosDbService) {}

    @Get()
    async RetrieveNotes(@Req() request): Promise<RetrieveNotesResponse> {
        const requestBody: RetrieveNotesRequest = JSON.parse(JSON.stringify(request.body));
        // tslint:disable-next-line
        console.log(`GET notes request received with following params:`);
        // tslint:disable-next-line
        console.dir(requestBody);

        // TODO: For now hard-coding range: 1km
        // Keywords: null
        try {
            const result: RetrieveNotesResponse = {
                notes: await this.azureCosmosDbService.RetrieveNotes({UserLocation: requestBody.Location, Range: 1000, Keywords: []}),
            };
            return result;
        } catch (error) { 
            return null; 
        }
    }
}
