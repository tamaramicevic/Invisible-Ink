import { Test, TestingModule } from '@nestjs/testing';
import { UploadImageController } from './upload-image.controller';

describe('UploadImage Controller', () => {
  let controller: UploadImageController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UploadImageController],
    }).compile();

    controller = module.get<UploadImageController>(UploadImageController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
