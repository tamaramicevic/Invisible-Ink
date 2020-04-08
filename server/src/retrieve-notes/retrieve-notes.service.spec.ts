import { Test, TestingModule } from '@nestjs/testing';
import { Note } from 'src/shared/models/note';
import { Filter, PreBuiltFilter } from './models/retrieve-notes-request';
import { RetrieveNotesService } from './retrieve-notes.service';

const notes: Note[] = [
  {
    NoteId: 'noteA',
    Title: 'noteA',
    Body: 'noteA body',
    Expiration: '234234',
    Score: 5,
    Lat: 25,
    Lon: 58,
    ImageId: 'image1.jpg',
  },
  {
    NoteId: 'noteB',
    Title: 'noteB',
    Body: 'noteB body',
    Expiration: '234234',
    Score: 1,
    Lat: 25,
    Lon: 58,
    ImageId: null,
  },
  {
    NoteId: 'noteC',
    Title: 'noteC',
    Body: 'noteC body',
    Expiration: '234234',
    Score: 25,
    Lat: 25,
    Lon: 58,
    ImageId: null,
  },
];

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

  const filter: Filter = {
    keywords: null,
    options: PreBuiltFilter.BEST_RATED,
    limit: 2,
    withImage: null,
  };

  it('should be sorted by score', async () => {
    expect(await service.ApplyFilters(null, notes, filter)).toBeDefined();
  });
});
