import { CosmosClient, Item } from '@azure/cosmos';
import { Injectable, OnApplicationBootstrap } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Point } from 'geojson';

import { NoteSearchParams } from 'src/shared/models/note-search-params';
import { Vote } from 'src/shared/models/vote';
import { Note } from '../shared/models/note';
import { NoteSchema } from './models/note-schema';
import { ReportedNoteSchema } from './models/reported-note-schema';

@Injectable()
export class AzureCosmosDbService implements OnApplicationBootstrap {
    private readonly mCosmosDbClient: CosmosClient;

    // Database IDs
    private readonly mDBId: string;
    private readonly mNoteContainerId: string;
    private readonly mReportedNotesContainerId: string;
    
    constructor(private readonly configService: ConfigService) {
        const endpoint: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-END-POINT') || '<database services endpoint>';
        const authKey: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-AUTH-KEY') || '<auth key>';
        this.mDBId = this.configService.get<string>('AZURE-COSMOS-DATABASE-ID') || '<database id>';
        this.mNoteContainerId = this.configService.get<string>('AZURE-COSMOS-NOTESCONTAINER-ID') || '<notes container id>';
        this.mReportedNotesContainerId = this.configService.get<string>('AZURE-COSMOS-REPORTSCONTAINER-ID') || '<reported notes container id>';
        
        this.mCosmosDbClient = new CosmosClient({
            endpoint,
            key: authKey,
          });
    }

    async onApplicationBootstrap() {
        // Create DB if it doesn't exist
        try {
            await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error creating database:\n', error);
        }
  
        // Create the containers if they don't exist
        try {
            await this.mCosmosDbClient.database(this.mDBId).containers.createIfNotExists({ id: this.mNoteContainerId });
            await this.mCosmosDbClient.database(this.mDBId).containers.createIfNotExists({ id: this.mReportedNotesContainerId });
  
            const iterator = this.mCosmosDbClient.database(this.mDBId).containers.readAll();
            const { resources: containersList } = await iterator.fetchAll();
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error creating containers:\n', error);
        }
        return;
      }

    async UploadNote(note: Note): Promise<void> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container: noteContainer } = await database.containers.createIfNotExists({ id: this.mNoteContainerId });

            // TODO: might need to verify coordinates are correctly entered
            const loc: Point = { type: 'Point', coordinates: [note.Lon, note.Lat]};

            /*
            // Create timestamp
            const expiryDate: Date = new Date(note.TimeStamp);
            expiryDate.setHours(expiryDate.getHours() + note.ExpiresInHours);
            */

            const dbNote: NoteSchema = {
                title: note.Title,
                body: note.Body,
                expiryTime: note.Expiration,
                score: note.Score,
                location: loc,
            };
    
            const { item } = await noteContainer.items.create(dbNote);
            
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error uploading note:\n', error);
        }

        return;
    }

    async RetrieveNotes(searchParams: NoteSearchParams): Promise<Note[]> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container: noteContainer } = await database.containers.createIfNotExists({ id: this.mNoteContainerId });
            const querySpec = {
                query: 'SELECT * FROM Notes note WHERE ST_DISTANCE(note.location, {\'type\': \'Point\', \'coordinates\':[@lon, @lat]}) < @range',
                parameters: [
                    {
                        name: '@lon',
                        value: searchParams.UserLocation.coordinates[0],
                    },
                    {
                        name: '@lat',
                        value: searchParams.UserLocation.coordinates[1],
                    },
                    {
                        name: '@range',
                        value: searchParams.Range,
                    },
                ],
            }; 
            
            const { resources: retreivedNotes } = await noteContainer.items.query(querySpec).fetchAll();
            const result = retreivedNotes.map(
                item => {
                    const note = item as NoteSchema;
                    return {
                        NoteId: item.id,
                        Title: note.title,
                        Body: note.body,
                        Expiration: note.expiryTime,
                        Score: note.score,
                        Lat: note.location.coordinates[0], // double check this conversion
                        Lon: note.location.coordinates[1],
                        } as Note;
                    },
                ).filter(item => !!item);

            return this.FilterNotesBySearchTerms(searchParams.Keywords, result);
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error retrieving note:\n', error);
        }

        return;
    }

    private FilterNotesBySearchTerms(keywords: string[], notes: Note[]): Note[] {
        if (!Array.isArray(keywords) || !keywords.length) { return notes; }

        return notes.filter(note => {
            return keywords.some(keyword => {
                return note.Title.search(new RegExp(keyword, 'i')) !== -1 ||
                note.Body.search(new RegExp(keyword, 'i')) !== -1;
            });
        });
    }

    async GetNoteReportByNoteId(noteId: string): Promise<ReportedNoteSchema> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container: reportContainer } = await database.containers.createIfNotExists({ id: this.mReportedNotesContainerId });
            const querySpec = {
            query: 'SELECT * FROM c WHERE c.NoteId = @noteId',
            parameters: [
                {
                    name: '@noteId',
                    value: noteId,
                },
            ],
            };
            const { resources: retrievedNote } = await reportContainer.items.query(querySpec).fetchAll();
            return retrievedNote.length === 0 ? null : retrievedNote[0];
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error retrieving note: ', error);
        }
    }

    async GetNoteById(noteId: string): Promise<Note> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container: noteContainer } = await database.containers.createIfNotExists({ id: this.mNoteContainerId });
            const querySpec = {
            query: 'SELECT * FROM c WHERE c.id = @noteId',
            parameters: [
                {
                    name: '@noteId',
                    value: noteId,
                },
            ],
            };
            const { resources: retrievedNote } = await noteContainer.items.query(querySpec).fetchAll();
            return retrievedNote.length === 0 ? null : retrievedNote[0];
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error retrieving note: ', error);
        }
    }

    async ReportNote(report: ReportedNoteSchema): Promise<void> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container: reportedNotesContainer } = await database.containers.createIfNotExists({ id: this.mReportedNotesContainerId });
    
            const { item } = await reportedNotesContainer.items.create(report);

        } catch (error) {
            // tslint:disable-next-line
            console.log('Error reporting note:\n', error);
        }

        return;
    }

    async VoteNote(vote: Vote): Promise<void> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container: noteContainer } = await database.containers.createIfNotExists({ id: this.mNoteContainerId });

            const item = noteContainer.item(vote.NoteId, undefined);
            const { resource: note } = await item.read();

            // vote.Rate ? true => upvote; false => downvote
            note.Score = vote.Rate ? (note.Score + 1) : (note.Score - 1);

            const { resource: updatedNote } = await noteContainer.items.upsert(note);

        } catch (error) {
            // tslint:disable-next-line
            console.log('Error applying vote to note:\n', error);
        }
        return;
    }
}
