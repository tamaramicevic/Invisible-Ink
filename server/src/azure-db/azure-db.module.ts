import { Module } from '@nestjs/common';
import { AzureBlobStorageService } from './azure-blob-storage.service';
import { AzureCosmosDbService } from './azure-cosmos-db.service';

@Module({
    providers: [AzureCosmosDbService, AzureBlobStorageService],
    exports: [AzureCosmosDbService, AzureBlobStorageService],
})
export class AzureDbModule {}
