export interface UploadNoteRequest {
    title: string;
    body: string;
    expiration: string;
    lat: number;
    long: number;
    // no images for now look into https://docs.nestjs.com/techniques/file-upload
    // for documentation on how to do it
}
