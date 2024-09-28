package com.google.firebase.platforminfo;

import javax.annotation.Nonnull;

/* compiled from: com.google.firebase:firebase-common@@19.3.0 */
abstract class LibraryVersion {
    @Nonnull
    public abstract String getLibraryName();

    @Nonnull
    public abstract String getVersion();

    LibraryVersion() {
    }

    static LibraryVersion create(String name, String version) {
        return new AutoValue_LibraryVersion(name, version);
    }
}
