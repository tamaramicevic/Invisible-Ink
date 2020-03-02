import { ConfigService } from '@nestjs/config';
import { Test, TestingModule } from '@nestjs/testing';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { VoteController } from './vote.controller';

describe('Vote Controller', () => {
  let controller: VoteController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [VoteController],
      providers: [AzureCosmosDbService, ConfigService],
    }).compile();

    controller = module.get<VoteController>(VoteController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
