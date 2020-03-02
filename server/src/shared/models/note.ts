export interface Note {
    NoteId: string;
    Title: string;
    Body: string;
    TimeStamp: string;
    Score: number;
    Lat: number;
    Lon: number;
    ImageId?: string;
    ExpiresInHours: number;
}
