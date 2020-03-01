import { Point } from 'geojson';

// Unfortunately, CosmosDb wants to only accept
// variable names starting with lower case
export interface NoteSchema {
    title: string;
    body: string;
    imageId?: string;
    timeStamp: string;
    expiryTime: string;
    score: number;
    location: Point;
}
