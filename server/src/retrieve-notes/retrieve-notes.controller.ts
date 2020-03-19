import { Controller, Get, Req, Post } from '@nestjs/common';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { RetrieveNotesRequest } from './models/retrieve-notes-request';
import { RetrieveNotesResponsePayload, NoteResponse } from './models/retrieve-notes-response';
import { NoteLocation } from 'src/shared/models/note-location';
import { Note } from 'src/shared/models/note';
import { Point } from 'geojson';

@Controller('retrieve-notes')
export class RetrieveNotesController {

    constructor(private readonly azureCosmosDbService: AzureCosmosDbService) {}

    @Post()
    async RetrieveNotes(@Req() request): Promise<RetrieveNotesResponsePayload> {
        const requestBody: RetrieveNotesRequest = JSON.parse(JSON.stringify(request.body));
        // tslint:disable-next-line
        console.log(`GET notes request received with following params:`);
        // tslint:disable-next-line
        console.dir(requestBody);

        // TODO: For now hard-coding range: 100km
        // Keywords: null
        const geoLocation: Point = { type: 'Point', coordinates: [requestBody.location.longitude, requestBody.location.latitude] };
        const keywords: string[] = requestBody.filter?.keywords?.split(' ');
        try {
            const resultNotes: Note[] = await this.azureCosmosDbService.RetrieveNotes({
                UserLocation: geoLocation, Range: 100000, Keywords: keywords,
            });
            const response: RetrieveNotesResponsePayload = {
                notes: resultNotes.map(item => {
                    return {
                        id: item.NoteId,
                        title: item.Title,
                        body: item.Body,
                        expiration: item.Expiration,
                        imageUrl: null, // TODO: implement images
                        location: { latitude: item.Lat, longitude: item.Lon } as NoteLocation,
                        score: item.Score,
                    } as NoteResponse;
                }),
            };
            return response;
        } catch (error) { 
            return null; 
        }
    }
}
