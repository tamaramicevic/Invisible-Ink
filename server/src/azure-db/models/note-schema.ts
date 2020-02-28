import { Point } from 'geojson';

export interface NoteSchema {
    Title: string;
    Body: string;
    ImageId?: string;
    TimeStamp: string;
    ExpiryTime: string;
    Score: number;
    Location: Point;
}
