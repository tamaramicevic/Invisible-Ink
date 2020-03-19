import { Controller, Get, Req, Post } from '@nestjs/common';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { RetrieveNotesRequest } from './models/retrieve-notes-request';
import { RetrieveNotesResponse } from './models/retrieve-notes-response';

@Controller('retrieve-notes')
export class RetrieveNotesController {

    constructor(private readonly azureCosmosDbService: AzureCosmosDbService) {}

    @Post()
    async RetrieveNotes(@Req() request): Promise<RetrieveNotesResponse> {
        const requestBody: RetrieveNotesRequest = JSON.parse(JSON.stringify(request.body));
        // tslint:disable-next-line
        console.log(`GET notes request received with following params:`);
        // tslint:disable-next-line
        console.dir(requestBody);

        // TODO: For now hard-coding range: 100km
        // Keywords: null
        try {
            const result: RetrieveNotesResponse = {
                notes: await this.azureCosmosDbService.RetrieveNotes({UserLocation: requestBody.location, Range: 100000, Keywords: []}),
            };
            return result;
        } catch (error) { 
            return null; 
        }
    }
}
