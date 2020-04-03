import { Injectable, Logger } from '@nestjs/common';
import { Point } from 'geojson';
import { Note } from 'src/shared/models/note';
import {Coordinate} from 'tsgeo/Coordinate';
import {Vincenty} from 'tsgeo/Distance/Vincenty';
import { Filter, PreBuiltFilter } from './models/retrieve-notes-request';

@Injectable()
export class RetrieveNotesService {

    async ApplyFilters(userLocation: Point, notes: Note[], filter: Filter): Promise<Note[]> {
        
        // Note: keywords are handled in the AzureCosmosDbService
        
        // options filter
        switch (filter.options) {
            case PreBuiltFilter.BEST_RATED:
                notes = notes.sort( (noteA, noteB) => noteB.Score - noteA.Score );
                Logger.log('Sorting notes by BEST_RATED', 'RetrieveNotesService');
                break;
            case PreBuiltFilter.NEWEST:
                notes = notes.sort((noteA, noteB) => {
                    const noteADate = Date.parse(noteA.Expiration);
                    const noteBDate = Date.parse(noteB.Expiration);

                    return noteBDate - noteADate;
                });
                Logger.log('Sorting notes by NEWEST', 'RetrieveNotesService');
                break;
            case PreBuiltFilter.WORST_RATED:
                notes = notes.sort( (noteA, noteB) => noteA.Score - noteB.Score );
                Logger.log('Sorting notes by WORST_RATED', 'RetrieveNotesService');
                break;
            default:
                Logger.log(`Sorting by proximity as default`, 'RetrieveNotesService');
                notes = notes.sort((noteA, noteB) => {
                    const noteACoord = new Coordinate(noteA.Lat, noteA.Lon); // Mauna Kea Summit
                    const noteBCoord = new Coordinate(noteB.Lat, noteB.Lon); // Haleakala Summit
                    const userCoord = new Coordinate(userLocation.coordinates[1], userLocation.coordinates[0]);
                    
                    const noteADist = userCoord.getDistance(noteACoord, new Vincenty());
                    const noteBDist = userCoord.getDistance(noteBCoord, new Vincenty());
                    return noteADist - noteBDist;
                });
                break;
        }
        
        if (filter.withImage != null) {
            if (filter.withImage) {
                // Return only notes with images
                notes = notes.filter(item => {
                    return item.ImageId;
                });
            } else if (!filter.withImage) {
                // Return only notes without images
                notes = notes.filter(item => item.ImageId == null);
            }
        }

        filter.limit = filter.limit || 50;

        if (filter.limit && notes.length > filter.limit) { 
            // Return only upto limit notes
            notes = notes.slice(0, filter.limit); 
        }
        return notes;
    }
}
