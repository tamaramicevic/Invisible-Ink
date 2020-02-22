import { Test, TestingModule } from '@nestjs/testing';
import { CosmosDbService } from './cosmos-db.service';

describe('CosmosDbService', () => {
  let service: CosmosDbService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [CosmosDbService],
    }).compile();

    service = module.get<CosmosDbService>(CosmosDbService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
