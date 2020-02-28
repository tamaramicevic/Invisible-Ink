export interface UploadNoteRequest {
    Title: string;
    Body: string;
    TimeStamp: string;
    LifetimeInHours: number;
    Lat: number;
    Lon: number;
    // no images for now look into https://docs.nestjs.com/techniques/file-upload
    // for documentation on how to do it
}
