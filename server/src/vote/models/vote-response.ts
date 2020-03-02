export enum VoteErrorCondition {
    NOTE_NON_EXISTENT = 'NOTE_NON_EXISTENT',
    UPLOAD_FAILED = 'UPLOAD_FAILED',
}

export interface VoteResponse {
    success: boolean;
    error?: VoteErrorCondition;
}
