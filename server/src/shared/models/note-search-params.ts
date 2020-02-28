import { Point } from 'geojson';

export interface NoteSearchParams {
    UserLocation: Point;
    Range: number; // this is the distance in meters
    Keywords: string[];
}
