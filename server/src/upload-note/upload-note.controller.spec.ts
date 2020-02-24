import { Test, TestingModule } from '@nestjs/testing';
import { UploadNoteController } from './upload-note.controller';

describe('UploadNote Controller', () => {
  let controller: UploadNoteController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UploadNoteController],
    }).compile();

    controller = module.get<UploadNoteController>(UploadNoteController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
