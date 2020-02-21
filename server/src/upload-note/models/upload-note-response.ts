export enum ErrorCondition {
    BAD_SENTIMENT_DETECTED = 'BAD_SENTIMENT_DETECTED',
    PII_DETECTED = 'PII_DETECTED',
    UPLOAD_FAILED = 'UPLOAD_FAILED',
}

export interface UploadNoteResponse {
    success: boolean;
    error?: ErrorCondition;
}
