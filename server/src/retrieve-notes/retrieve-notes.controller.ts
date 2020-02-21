import { Controller, Get, Req } from '@nestjs/common';
import { RetrieveNotesRequest } from './models/retrieve-notes-request';

@Controller('retrieve-notes')
export class RetrieveNotesController {
    @Get()
    async RetrieveNotes(@Req() request: RetrieveNotesRequest) {
        return null;
    }
}
