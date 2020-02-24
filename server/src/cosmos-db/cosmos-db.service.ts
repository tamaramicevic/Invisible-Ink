import { CosmosClient } from '@azure/cosmos';
import { Injectable } from '@nestjs/common';
import { ConfigService} from '@nestjs/config';
// import { Client } from 'documentdb-typescript';

@Injectable()
export class CosmosDbService {
    // private readonly mCosmosDbClient: Client;
    private readonly mCosmosDbClient: CosmosClient;
    private readonly mDatabaseID: string;

    constructor(private readonly configService: ConfigService) {
        const endpoint: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-END-POINT') || '<database services endpoint>';
        const authKey: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-AUTH-KEY') || '<auth key>';
        const containerID: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-CONTAINERID') || '<container id>';

        this.mDatabaseID = this.configService.get<string>('AZURE-COSMOS-DATABASE-DATABASEID') || '<database id>';

        // this.mCosmosDbClient = new Client(endpoint, authKey);
        // this.mCosmosDbClient.enableConsoleLog = true;

        this.mCosmosDbClient = new CosmosClient({
            endpoint,
            key: authKey,
          });

        this.initialize();
    }

    async initialize() {
        try {
            const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDatabaseID });
            console.log(`Created database:\n${database.id}\n`);

            // await this.mCosmosDbClient.openAsync();
            // var dbs = await this.mCosmosDbClient.listDatabasesAsync();
            // console.log(dbs.map(db => db.id));
        } catch (error) {
            console.debug('Error creating database: ', error);
        }

        this.readContainers();
    }

    /**
     * Read the database definition
     */
    async readContainers() {
        try {
            const iterator = this.mCosmosDbClient.database(this.mDatabaseID).containers.readAll();
            const { resources: containersList } = await iterator.fetchAll();
            console.log(' --- Priting via iterator.fetchAll()');
            console.log(containersList);
        } catch (error) {
            console.debug('Error reading database: ', error);
        }
        
    }
    
}
