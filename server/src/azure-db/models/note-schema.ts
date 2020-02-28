import { Point } from 'geojson';

export interface NoteSchema {
    Title: string;
    Body: string;
    ImageId?: string;
    TimeStamp: string;
    Score: number;
    Location: Point;
}
