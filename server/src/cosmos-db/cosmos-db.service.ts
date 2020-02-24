import { Injectable } from '@nestjs/common';
import { ConfigService} from '@nestjs/config';
import { Client } from 'documentdb-typescript';
// import { CosmosClient } from '@azure/cosmos';
// import { DbInteractor } from './models/db-interactor';

// const config = require('./config')
// const CosmosDbController = require('./cosmos-db.controller')
// const DbInteractor = require("./models/db-interactor");


@Injectable()
export class CosmosDbService {
    private readonly mCosmosDbClient: Client;
    private readonly mDatabaseID: string;

    constructor (private readonly configService: ConfigService) {
        const endpoint: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-END-POINT') || '<database services endpoint>';
        const authKey: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-AUTH-KEY') || '<auth key>';
        const containerID: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-CONTAINERID') || '<container id>';

        this.mDatabaseID = this.configService.get<string>('AZURE-COSMOS-DATABASE-DATABASEID') || '<database id>';

        this.mCosmosDbClient = new Client(endpoint, authKey);
        this.mCosmosDbClient.enableConsoleLog = true;


        // this.mCosmosDbClient = new CosmosClient({
        //     endpoint: endpoint,
        //     key: authKey
        //   })

        this.initialize();

        // this.createDatabase();
    }

    async initialize() {
        try{
            await this.mCosmosDbClient.openAsync();
            var dbs = await this.mCosmosDbClient.listDatabasesAsync();
            console.log(dbs.map(db => db.id));
        } catch (error) {

        }
    }

    /**
    * Create the database if it does not exist
    */
    // async createDatabase() {
    //     try {
    //         const { database } = await this.mCosmosDbClient.databases.createIfNotExists({ id: this.mDatabaseID });
    //         console.log(`Created database:\n${database.id}\n`);
    //     } catch(error) {
    //         console.debug('Error creating database: ', error);
    //     }
    // }
    
    // /**
    //  * Read the database definition
    //  */
    // async readDatabase() {
    //     try {
    //         const { resource: databaseDefinition } = await this.mCosmosDbClient.database(this.mDatabaseID).read();
    //         console.log(`Reading database:\n${databaseDefinition.id}\n`);
    //     } catch (error) {
    //         console.debug('Error reading database: ', error);
    //     }
        
    // }

    // /**
    //  * Read the database definition
    //  */
    // async readContainers() {
    //     try {
    //         const iterator = database.containers.readAll();
    //         const { resources: containersList } = await iterator.fetchAll();
    //         console.log(" --- Priting via iterator.fetchAll()");
    //         console.log(containersList);
    //     } catch (error) {
    //         console.debug('Error reading database: ', error);
    //     }
        
    // }
    
}


