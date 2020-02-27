import { ConfigService } from '@nestjs/config';
import { Test, TestingModule } from '@nestjs/testing';
import { AzureCosmosDbService } from './azure-cosmos-db.service';

describe('AzureCosmosDbService', () => {
  let service: AzureCosmosDbService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [AzureCosmosDbService, ConfigService],
    }).compile();

    service = module.get<AzureCosmosDbService>(AzureCosmosDbService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
