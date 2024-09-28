package com.google.android.exoplayer2.drm;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class DrmInitData implements Comparator<SchemeData>, Parcelable {
    public static final Parcelable.Creator<DrmInitData> CREATOR = new Parcelable.Creator<DrmInitData>() {
        public DrmInitData createFromParcel(Parcel in) {
            return new DrmInitData(in);
        }

        public DrmInitData[] newArray(int size) {
            return new DrmInitData[size];
        }
    };
    private int hashCode;
    public final int schemeDataCount;
    private final SchemeData[] schemeDatas;
    public final String schemeType;

    public static DrmInitData createSessionCreationData(DrmInitData manifestData, DrmInitData mediaData) {
        ArrayList<SchemeData> result = new ArrayList<>();
        String schemeType2 = null;
        if (manifestData != null) {
            schemeType2 = manifestData.schemeType;
            for (SchemeData data : manifestData.schemeDatas) {
                if (data.hasData()) {
                    result.add(data);
                }
            }
        }
        if (mediaData != null) {
            if (schemeType2 == null) {
                schemeType2 = mediaData.schemeType;
            }
            int manifestDatasCount = result.size();
            for (SchemeData data2 : mediaData.schemeDatas) {
                if (data2.hasData() && !containsSchemeDataWithUuid(result, manifestDatasCount, data2.uuid)) {
                    result.add(data2);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return new DrmInitData(schemeType2, (List<SchemeData>) result);
    }

    public DrmInitData(List<SchemeData> schemeDatas2) {
        this((String) null, false, (SchemeData[]) schemeDatas2.toArray(new SchemeData[0]));
    }

    public DrmInitData(String schemeType2, List<SchemeData> schemeDatas2) {
        this(schemeType2, false, (SchemeData[]) schemeDatas2.toArray(new SchemeData[0]));
    }

    public DrmInitData(SchemeData... schemeDatas2) {
        this((String) null, schemeDatas2);
    }

    public DrmInitData(String schemeType2, SchemeData... schemeDatas2) {
        this(schemeType2, true, schemeDatas2);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: com.google.android.exoplayer2.drm.DrmInitData$SchemeData[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private DrmInitData(java.lang.String r2, boolean r3, com.google.android.exoplayer2.drm.DrmInitData.SchemeData... r4) {
        /*
            r1 = this;
            r1.<init>()
            r1.schemeType = r2
            if (r3 == 0) goto L_0x000e
            java.lang.Object r0 = r4.clone()
            r4 = r0
            com.google.android.exoplayer2.drm.DrmInitData$SchemeData[] r4 = (com.google.android.exoplayer2.drm.DrmInitData.SchemeData[]) r4
        L_0x000e:
            r1.schemeDatas = r4
            int r0 = r4.length
            r1.schemeDataCount = r0
            java.util.Arrays.sort(r4, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.drm.DrmInitData.<init>(java.lang.String, boolean, com.google.android.exoplayer2.drm.DrmInitData$SchemeData[]):void");
    }

    DrmInitData(Parcel in) {
        this.schemeType = in.readString();
        SchemeData[] schemeDataArr = (SchemeData[]) in.createTypedArray(SchemeData.CREATOR);
        this.schemeDatas = schemeDataArr;
        this.schemeDataCount = schemeDataArr.length;
    }

    @Deprecated
    public SchemeData get(UUID uuid) {
        for (SchemeData schemeData : this.schemeDatas) {
            if (schemeData.matches(uuid)) {
                return schemeData;
            }
        }
        return null;
    }

    public SchemeData get(int index) {
        return this.schemeDatas[index];
    }

    public DrmInitData copyWithSchemeType(String schemeType2) {
        if (Util.areEqual(this.schemeType, schemeType2)) {
            return this;
        }
        return new DrmInitData(schemeType2, false, this.schemeDatas);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            String str = this.schemeType;
            this.hashCode = ((str == null ? 0 : str.hashCode()) * 31) + Arrays.hashCode(this.schemeDatas);
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DrmInitData other = (DrmInitData) obj;
        if (!Util.areEqual(this.schemeType, other.schemeType) || !Arrays.equals(this.schemeDatas, other.schemeDatas)) {
            return false;
        }
        return true;
    }

    public int compare(SchemeData first, SchemeData second) {
        if (C.UUID_NIL.equals(first.uuid)) {
            return C.UUID_NIL.equals(second.uuid) ? 0 : 1;
        }
        return first.uuid.compareTo(second.uuid);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.schemeType);
        dest.writeTypedArray(this.schemeDatas, 0);
    }

    private static boolean containsSchemeDataWithUuid(ArrayList<SchemeData> datas, int limit, UUID uuid) {
        for (int i = 0; i < limit; i++) {
            if (datas.get(i).uuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static final class SchemeData implements Parcelable {
        public static final Parcelable.Creator<SchemeData> CREATOR = new Parcelable.Creator<SchemeData>() {
            public SchemeData createFromParcel(Parcel in) {
                return new SchemeData(in);
            }

            public SchemeData[] newArray(int size) {
                return new SchemeData[size];
            }
        };
        public final byte[] data;
        private int hashCode;
        public final String licenseServerUrl;
        public final String mimeType;
        public final boolean requiresSecureDecryption;
        /* access modifiers changed from: private */
        public final UUID uuid;

        public SchemeData(UUID uuid2, String mimeType2, byte[] data2) {
            this(uuid2, mimeType2, data2, false);
        }

        public SchemeData(UUID uuid2, String mimeType2, byte[] data2, boolean requiresSecureDecryption2) {
            this(uuid2, (String) null, mimeType2, data2, requiresSecureDecryption2);
        }

        public SchemeData(UUID uuid2, String licenseServerUrl2, String mimeType2, byte[] data2, boolean requiresSecureDecryption2) {
            this.uuid = (UUID) Assertions.checkNotNull(uuid2);
            this.licenseServerUrl = licenseServerUrl2;
            this.mimeType = (String) Assertions.checkNotNull(mimeType2);
            this.data = data2;
            this.requiresSecureDecryption = requiresSecureDecryption2;
        }

        SchemeData(Parcel in) {
            this.uuid = new UUID(in.readLong(), in.readLong());
            this.licenseServerUrl = in.readString();
            this.mimeType = (String) Util.castNonNull(in.readString());
            this.data = in.createByteArray();
            this.requiresSecureDecryption = in.readByte() != 0;
        }

        public boolean matches(UUID schemeUuid) {
            return C.UUID_NIL.equals(this.uuid) || schemeUuid.equals(this.uuid);
        }

        public boolean canReplace(SchemeData other) {
            return hasData() && !other.hasData() && matches(other.uuid);
        }

        public boolean hasData() {
            return this.data != null;
        }

        public SchemeData copyWithData(byte[] data2) {
            return new SchemeData(this.uuid, this.licenseServerUrl, this.mimeType, data2, this.requiresSecureDecryption);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SchemeData)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            SchemeData other = (SchemeData) obj;
            if (!Util.areEqual(this.licenseServerUrl, other.licenseServerUrl) || !Util.areEqual(this.mimeType, other.mimeType) || !Util.areEqual(this.uuid, other.uuid) || !Arrays.equals(this.data, other.data)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                int hashCode2 = this.uuid.hashCode() * 31;
                String str = this.licenseServerUrl;
                this.hashCode = ((((hashCode2 + (str == null ? 0 : str.hashCode())) * 31) + this.mimeType.hashCode()) * 31) + Arrays.hashCode(this.data);
            }
            return this.hashCode;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.uuid.getMostSignificantBits());
            dest.writeLong(this.uuid.getLeastSignificantBits());
            dest.writeString(this.licenseServerUrl);
            dest.writeString(this.mimeType);
            dest.writeByteArray(this.data);
            dest.writeByte(this.requiresSecureDecryption ? (byte) 1 : 0);
        }
    }
}
