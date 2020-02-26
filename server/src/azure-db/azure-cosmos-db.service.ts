import { CosmosClient } from '@azure/cosmos';
import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class AzureCosmosDbService {
    private readonly mCosmosDbClient: CosmosClient;

    // Database IDs
    private readonly mDBId: string;
    private readonly mNoteContainerId: string;
    private readonly mNoteLifetimeLogContainerId: string;
    private readonly mReportedNotesContainerId: string;
    
    constructor(private readonly configService: ConfigService) {
        const endpoint: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-END-POINT') || '<database services endpoint>';
        const authKey: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-AUTH-KEY') || '<auth key>';
        this.mDBId = this.configService.get<string>('AZURE-COSMOS-DATABASE-ID') || '<database id>';
        console.log(`DBID: `, this.mDBId);

        this.mCosmosDbClient = new CosmosClient({
            endpoint,
            key: authKey,
          });

        this.Initialize();
    }

    async Initialize(): Promise<void> {
        // Create DB if it doesn't exist
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDBId });
        } catch (error) {
            console.log('Error creating/ reading database:\n', error);
        }

        // Create the containers if they don't exist
        try {
            const iterator = this.mCosmosDbClient.database(this.mDBId).containers.readAll();
            const { resources: containersList } = await iterator.fetchAll();
            console.log('List of active containers:\n')
            console.log(containersList);
        } catch (error) {
            console.log('Error reading containers');
        }
        return;
    }
}
