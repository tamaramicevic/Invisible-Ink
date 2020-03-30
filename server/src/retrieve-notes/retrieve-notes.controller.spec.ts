import { ConfigService } from '@nestjs/config';
import { Test, TestingModule } from '@nestjs/testing';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { RetrieveNotesController } from './retrieve-notes.controller';
import { RetrieveNotesService } from './retrieve-notes.service';

describe('RetrieveNotes Controller', () => {
  let controller: RetrieveNotesController;
  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [RetrieveNotesController],
      providers: [AzureCosmosDbService, ConfigService, RetrieveNotesService],
    }).compile();

    controller = module.get<RetrieveNotesController>(RetrieveNotesController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
