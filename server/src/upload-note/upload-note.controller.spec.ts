import { ConfigService } from '@nestjs/config';
import { Test, TestingModule } from '@nestjs/testing';
import { AzureCosmosDbService } from 'src/azure-db/azure-cosmos-db.service';
import { UploadNoteController } from './upload-note.controller';

describe('UploadNote Controller', () => {
  let controller: UploadNoteController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UploadNoteController],
      providers: [AzureCosmosDbService, ConfigService],
    }).compile();

    controller = module.get<UploadNoteController>(UploadNoteController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
