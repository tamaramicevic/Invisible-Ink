import { Injectable } from '@nestjs/common';
import { ConfigService} from '@nestjs/config';
import { CosmosClient } from '@azure/cosmos';
import { CosmosDbController } from './cosmos-db.controller';
// import { DbInteractor } from './models/db-controller';

const config = require('./config')
// const CosmosDbController = require('./cosmos-db.controller')
const DbInteractor = require("./models/db-interactor");


@Injectable()
export class CosmosDbService {
    private readonly mCosmosDbClient: CosmosClient;
    constructor (private readonly configService: ConfigService) {
        const endpoint: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-END-POINT') || '<database services endpoint>';
        const authKey: string = this.configService.get<string>('AZURE-COSMOS-DATABASE-AUTH-KEY') || '<auth key>';

        this.mCosmosDbClient = new CosmosClient({
            endpoint: endpoint,
            key: authKey
          })
          
        const dbInteractor = new DbInteractor(this.mCosmosDbClient, config.databaseId, config.containerId)
        const dbController = new CosmosDbController(dbInteractor)
        dbInteractor
            .init(err => {
                console.error(err)
            })
            .catch(err => {
                console.error(err)
                console.error(
                    'Shutting down because there was an error settinig up the database.'
                )
                process.exit(1)
            })
    }
    
}


