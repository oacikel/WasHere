package ocul.longestlovestoryever.washere.models;

public enum eUploadingState {
    UPLOADING_TO_STORAGE,
    STORAGE_UPLOAD_COMPLETE,
    UPLOADING_TO_DATABASE,
    DATABASE_UPLOAD_COMPLETE,
    NO_ACTION,
    UPLOAD_CANCELED,
    ERROR
}
