import { Module } from '@nestjs/common';
import { CosmosDbController } from './cosmos-db.controller';
import { CosmosDbService } from './cosmos-db.service';

@Module({
  controllers: [CosmosDbController],
  providers: [CosmosDbService]
})
export class CosmosDbModule {}
