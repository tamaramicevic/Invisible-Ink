import {
    Feature, FeatureCollection, GeoJsonGeometryTypes, GeoJsonTypes,
    Point,
} from 'geojson';

import { NoteLocation } from '../../shared/models/note-location';

export enum PreBuiltFilter {
    BEST_RATED = 'BEST_RATED',
    NEWEST = 'NEWEST',
    WORST_RATED = 'WORST_RATED',
}

export interface Filter {
    keywords?: string;
    options?: PreBuiltFilter;
    limit?: number;
    withImage?: boolean;
}

export interface RetrieveNotesRequest {
    location: NoteLocation; // [lon, lat]
    filter?: Filter;
    // TODO: preferences???
}

/*
{
  "filter": {
	"keywords": "covid-19",
	"limit": null,
	"withImage": true,
	"options": “BEST”,
  },
  "location": {
	"latitude": 53.5321527,
	"longitude": -113.2424553
  }
}

*/
