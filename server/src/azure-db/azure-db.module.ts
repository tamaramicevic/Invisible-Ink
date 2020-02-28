import { Module } from '@nestjs/common';
import { AzureCosmosDbService } from './azure-cosmos-db.service';

@Module({
    providers: [AzureCosmosDbService],
    exports: [AzureCosmosDbService],
})
export class AzureDbModule {}
