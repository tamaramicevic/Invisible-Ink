export enum ReportType {
    HARASSMENT = 'HARASSMENT',
    VIOLENCE = 'VIOLENCE',
    SEXUAL_CONTENT = 'SEXUAL_CONTENT',
}

export interface ReportNoteRequest {
    Type: ReportType;
    NoteId: string;
    Comment?: string;
}
