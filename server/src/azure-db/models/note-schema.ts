import { Point } from 'geojson';

export interface NoteSchema {
    NoteId: string;
    Title: string;
    Body: string;
    ImageId?: string;
    Timestamp: string;
    Score: number;
    Location: Point;
}
