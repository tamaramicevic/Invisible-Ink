import { Test, TestingModule } from '@nestjs/testing';
import { CosmosDbController } from './cosmos-db.controller';

describe('CosmosDb Controller', () => {
  let controller: CosmosDbController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [CosmosDbController],
    }).compile();

    controller = module.get<CosmosDbController>(CosmosDbController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
