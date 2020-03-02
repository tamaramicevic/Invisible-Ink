import { Test, TestingModule } from '@nestjs/testing';
import { AzureDbModule } from '../azure-db/azure-db.module';
import { RetrieveNotesController } from './retrieve-notes.controller';

describe('RetrieveNotes Controller', () => {
  let controller: RetrieveNotesController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [RetrieveNotesController],
      imports: [AzureDbModule],
    }).compile();

    controller = module.get<RetrieveNotesController>(RetrieveNotesController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
