import { Test, TestingModule } from '@nestjs/testing';
import { RetrieveNotesController } from './retrieve-notes.controller';

describe('RetrieveNotes Controller', () => {
  let controller: RetrieveNotesController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [RetrieveNotesController],
    }).compile();

    controller = module.get<RetrieveNotesController>(RetrieveNotesController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
