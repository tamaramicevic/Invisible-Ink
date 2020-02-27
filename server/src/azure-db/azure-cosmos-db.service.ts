import { CosmosClient } from '@azure/cosmos';
import { Injectable, OnApplicationBootstrap } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

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
            // tslint:disable-next-line
            console.log('List of active containers:\n')
            // tslint:disable-next-line
            console.log(containersList);
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error creating containers:\n', error);
        }
  
        return;
      }

    async UploadNote(note: Note): Promise<void> {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
            const { container } = await database.containers.createIfNotExists({ id: this.mNoteLifetimeLogContainerId });

            const NoteLifetimeLog: NoteLifetimeLogSchema = {
                NoteId: note.NoteId,
                Timestamp: note.NoteId,
                Lifetime: note.ExpiresInHours,
            };
    
            await container.items.create(NoteLifetimeLog);

            const { resources: results } = await container.items.readAll().fetchAll();
            for (const item of results) {
                const resultString = JSON.stringify(item);
                // tslint:disable-next-line
                console.log(`Item returned ${resultString}\n`)
            }
        } catch (error) {
            // tslint:disable-next-line
            console.log('Error uploading note:\n', error);
        }

        return;
    }

    async RetrieveNotes(): Promise<void> {
        // TODO : needs implementation
        return;
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
