import { Module } from '@nestjs/common';
import { CosmosDbService } from './cosmos-db.service';

@Module({
  providers: [CosmosDbService],
})
export class CosmosDbModule {}
