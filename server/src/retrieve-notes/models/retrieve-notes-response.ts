import { Note } from '../../shared/models/note';
import { NoteLocation } from '../../shared/models/note-location';

export interface NoteResponse {
    id: string;
    title: string;
    body: string;
    expiration: string;
    imageUrl: string;
    location: NoteLocation;
    score: number;
}

export interface RetrieveNotesResponsePayload {
    notes: NoteResponse[];
}
