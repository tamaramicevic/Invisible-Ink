import { Test, TestingModule } from '@nestjs/testing';
import { RetrieveNotesService } from './retrieve-notes.service';

describe('RetrieveNotesService', () => {
  let service: RetrieveNotesService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [RetrieveNotesService],
    }).compile();

    service = module.get<RetrieveNotesService>(RetrieveNotesService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
