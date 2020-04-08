/**
 * Software Requirements Specification Coverage
 *
 *  - Covers System Feature 4.2, Functional Requirement: AR-Server-Note-Logic
 *  - Covers System Feature 4.2, Functional Requirement: AR-Server-Note-Retrieval
 *  - Covers System Feature 4.3, Functional Requirement: Map-Client-Note-Serve
 *  - Covers System Feature 4.3, Functional Requirement: Client-Search-Params
 *  - Covers System Feature 4.5, Functional Requirement: Client-Search-Filter
 * 
 */

import { Controller, Get, Post, Req } from '@nestjs/common';
import { Point } from 'geojson';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { Note } from 'src/shared/models/note';
import { NoteLocation } from 'src/shared/models/note-location';
import { RetrieveNotesRequest } from './models/retrieve-notes-request';
import { NoteResponse, RetrieveNotesResponsePayload } from './models/retrieve-notes-response';
import { RetrieveNotesService } from './retrieve-notes.service';

@Controller('retrieve-notes')
export class RetrieveNotesController {

    constructor(
        private readonly azureCosmosDbService: AzureCosmosDbService,
        private readonly retrieveNotesService: RetrieveNotesService) {}

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
            let resultNotes: Note[] = await this.azureCosmosDbService.RetrieveNotes({
                UserLocation: geoLocation, Range: 100000, Keywords: keywords,
            });

            resultNotes = await this.retrieveNotesService.ApplyFilters(geoLocation, resultNotes, requestBody.filter);
            
            const response: RetrieveNotesResponsePayload = {
                notes: resultNotes.map(item => {
                    return {
                        id: item.NoteId,
                        title: item.Title,
                        body: item.Body,
                        expiration: item.Expiration,
                        imageUrl: item.ImageId, // TODO: implement images
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
