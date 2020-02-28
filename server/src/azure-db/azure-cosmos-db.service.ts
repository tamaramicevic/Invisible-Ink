import { CosmosClient, Item } from '@azure/cosmos';
import { Injectable, OnApplicationBootstrap } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Point } from 'geojson';

import { NoteSearchParams } from 'src/shared/models/note-search-params';
import { Note } from '../shared/models/note';
import { NoteLifetimeLogSchema } from './models/note-lifetime-log-schema';
import { NoteSchema } from './models/note-schema';
import { ReportedNoteSchema } from './models/reported-note-schema';

@Injectable()
export class AzureCosmosDbService implements OnApplicationBootstrap {
    private readonly mCosmosDbClient: CosmosClient;

    // Database IDs
    private readonly mDBId: string;
    private readonly mNoteContainerId: string;
    private readonly mReportedNotesContainerId: string;
    private readonly mNoteLifetimeLogContainerId: string;
    
    constructor(private readonly configService: ConfigService) {
        const endpoint: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-END-POINT') || '<database services endpoint>';
        const authKey: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-AUTH-KEY') || '<auth key>';
        this.mDBId = this.configService.get<string>('AZURE-COSMOS-DATABASE-ID') || '<database id>';
        this.mNoteContainerId = this.configService.get<string>('AZURE-COSMOS-NOTESCONTAINER-ID') || '<notes container id>';
        this.mReportedNotesContainerId = this.configService.get<string>('AZURE-COSMOS-REPORTSCONTAINER-ID') || '<reported notes container id>';
        this.mNoteLifetimeLogContainerId = this.configService.get<string>('AZURE-COSMOS-LIFETIMECONTAINER-ID') || '<note lifetime log container id>';

        this.mCosmosDbClient = new CosmosClient({
            endpoint,
            key: authKey,
          });

        /* Note:    There should be no logic like this in constructors
        // dummy values
        const note: Note = {
            NoteId: '22',
            Title: 'TestNote',
            Body: 'This is a test note',
            TimeStamp: Date.now().toString(),
            Score: 9,
            Lat: 98.4569839,
            Lon: 114.2364167,
            ImageId: '22a',
            ExpiresInHours: 24,
        };

        this.UploadNote(note);
        */
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
            await this.mCosmosDbClient.database(this.mDBId).containers.createIfNotExists({ id: this.mNoteLifetimeLogContainerId });
            await this.mCosmosDbClient.database(this.mDBId).containers.createIfNotExists({ id: this.mReportedNotesContainerId });
  
            const iterator = this.mCosmosDbClient.database(this.mDBId).containers.readAll();
            const { resources: containersList } = await iterator.fetchAll();
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error creating containers:\n', error);
        }
        console.log('Azure CosmosDb successfully initialized');
        return;
      }

    async UploadNote(note: Note): Promise<void> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container: noteLifetimeLogContainer } = await database.containers.createIfNotExists({ id: this.mNoteLifetimeLogContainerId });
            const { container: noteContainer } = await database.containers.createIfNotExists({ id: this.mNoteContainerId });

            // TODO: might need to verify coordinates are correctly entered
            const loc: Point = { type: 'Point', coordinates: [note.Lat, note.Lon]};

            // Create timestamp
            const expiryDate: Date = new Date(note.TimeStamp);
            expiryDate.setHours(expiryDate.getHours() + note.ExpiresInHours);

            const dbNote: NoteSchema = {
                Title: note.Title,
                Body: note.Body,
                ImageId: note.ImageId,
                TimeStamp: note.TimeStamp,
                ExpiryTime: expiryDate.toISOString(),
                Score: note.Score,
                Location: loc,
            };
    
            const { item } = await noteContainer.items.create(dbNote);

            // TODO: Evaluate if this needs to be removed
            /*const dbNoteLifetimeLog: NoteLifetimeLogSchema = {
                NoteId: item.id,
                Timestamp: note.TimeStamp,
                Lifetime: note.ExpiresInHours,
            };

            await noteLifetimeLogContainer.items.create(dbNoteLifetimeLog);

            const { resources: results } = await noteLifetimeLogContainer.items.readAll().fetchAll();*/

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
                query: 'SELECT * FROM Notes n WHERE ST_DISTANCE(n.Location, @userLocation) <= @range',
                parameters: [
                    {
                        name: '@userLocation',
                        value: JSON.stringify(searchParams.UserLocation),
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
                        Title: note.Title,
                        Body: note.Body,
                        ImageId: note.ImageId,
                        TimeStamp: note.TimeStamp,
                        Score: note.Score,
                        Lat: note.Location.coordinates[0], // double check this conversion
                        Lon: note.Location.coordinates[1],
                        } as Note;
                    },
                ).filter(item => !!item);

            return result;
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error retrieving note:\n', error);
        }
    }

    async ReportNote(): Promise<void> {
        // TODO : needs implementation
        return;
    }

    async VoteNote(): Promise<void> {
        // TODO : needs implementation
        return;
    }
}
