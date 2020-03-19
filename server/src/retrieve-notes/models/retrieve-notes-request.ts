import {
    Feature, FeatureCollection, GeoJsonGeometryTypes, GeoJsonTypes,
    Point,
} from 'geojson';

export enum PreBuiltFilter {
    BEST_RATED = 'BEST_RATED',
    NEWEST = 'NEWEST',
    WORST_RATED = 'WORST_RATED',
}

export interface Filter {
    Keywords?: string[];
    Filter?: PreBuiltFilter;
    Limit?: number;
    WithImage?: boolean;
}

export interface RetrieveNotesRequest {
    location: Point; // [lon, lat]
    Filter?: Filter;
    // TODO: preferences???
}
