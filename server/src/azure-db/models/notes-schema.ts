export interface NotesSchema {
    note_id: number;
    title: string;
    body: string;
    image_id: number;
    date_posted: Date;
    score: number;
    latitude: number;
    longitude: number;
}
