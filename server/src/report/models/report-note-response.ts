export enum ReportNoteErrorCondition {
    NOTE_NON_EXISTENT = 'NOTE_NON_EXISTENT',
    UPLOAD_FAILED = 'UPLOAD_FAILED',
    UNKNOWN_REPORT_TYPE = 'UNKNOWN_REPORT_TYPE',
    REPORT_ALREADY_EXISTS = 'REPORT_ALREADY_EXISTS',
}

export interface ReportNoteResponse {
    success: boolean;
    error?: ReportNoteErrorCondition;
}
