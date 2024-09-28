package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.util.Util;

public final class Descriptor {
    public final String id;
    public final String schemeIdUri;
    public final String value;

    public Descriptor(String schemeIdUri2, String value2, String id2) {
        this.schemeIdUri = schemeIdUri2;
        this.value = value2;
        this.id = id2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Descriptor other = (Descriptor) obj;
        if (!Util.areEqual(this.schemeIdUri, other.schemeIdUri) || !Util.areEqual(this.value, other.value) || !Util.areEqual(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        String str = this.schemeIdUri;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.value;
        int result = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.id;
        if (str3 != null) {
            i = str3.hashCode();
        }
        return result + i;
    }
}
