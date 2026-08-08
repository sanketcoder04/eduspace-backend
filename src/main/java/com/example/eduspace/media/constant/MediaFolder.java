package com.example.eduspace.media.constant;

/**
 * Whitelisted upload destinations. Keeping this closed (rather than a free-text
 * folder param) stops callers from writing arbitrary paths into the bucket.
 */

public enum MediaFolder {
    AVATAR,
    COVER,
    RESUME,
    CERTIFICATE,
    SELFIE
}