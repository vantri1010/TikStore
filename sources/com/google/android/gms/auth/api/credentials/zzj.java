package com.google.android.gms.auth.api.credentials;

import android.os.Parcelable;

public final class zzj implements Parcelable.Creator<HintRequest> {
    public final /* synthetic */ Object[] newArray(int i) {
        return new HintRequest[i];
    }

    /* JADX WARNING: type inference failed for: r1v3, types: [android.os.Parcelable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final /* synthetic */ java.lang.Object createFromParcel(android.os.Parcel r13) {
        /*
            r12 = this;
            int r0 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.validateObjectHeader(r13)
            r1 = 0
            r2 = 0
            r5 = r1
            r8 = r5
            r10 = r8
            r11 = r10
            r4 = 0
            r6 = 0
            r7 = 0
            r9 = 0
        L_0x0016:
            int r1 = r13.dataPosition()
            if (r1 >= r0) goto L_0x0063
            int r1 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readHeader(r13)
            int r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.getFieldId(r1)
            r3 = 1000(0x3e8, float:1.401E-42)
            if (r2 == r3) goto L_0x005d
            switch(r2) {
                case 1: goto L_0x0053;
                case 2: goto L_0x004d;
                case 3: goto L_0x0047;
                case 4: goto L_0x0041;
                case 5: goto L_0x003b;
                case 6: goto L_0x0035;
                case 7: goto L_0x002f;
                default: goto L_0x002b;
            }
        L_0x002b:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.skipUnknownField(r13, r1)
            goto L_0x0016
        L_0x002f:
            java.lang.String r11 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r13, r1)
            goto L_0x0016
        L_0x0035:
            java.lang.String r10 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r13, r1)
            goto L_0x0016
        L_0x003b:
            boolean r9 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r13, r1)
            goto L_0x0016
        L_0x0041:
            java.lang.String[] r8 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createStringArray(r13, r1)
            goto L_0x0016
        L_0x0047:
            boolean r7 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r13, r1)
            goto L_0x0016
        L_0x004d:
            boolean r6 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r13, r1)
            goto L_0x0016
        L_0x0053:
            android.os.Parcelable$Creator<com.google.android.gms.auth.api.credentials.CredentialPickerConfig> r2 = com.google.android.gms.auth.api.credentials.CredentialPickerConfig.CREATOR
            android.os.Parcelable r1 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createParcelable(r13, r1, r2)
            r5 = r1
            com.google.android.gms.auth.api.credentials.CredentialPickerConfig r5 = (com.google.android.gms.auth.api.credentials.CredentialPickerConfig) r5
            goto L_0x0016
        L_0x005d:
            int r4 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readInt(r13, r1)
            goto L_0x0016
        L_0x0063:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ensureAtEnd(r13, r0)
            com.google.android.gms.auth.api.credentials.HintRequest r13 = new com.google.android.gms.auth.api.credentials.HintRequest
            r3 = r13
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.auth.api.credentials.zzj.createFromParcel(android.os.Parcel):java.lang.Object");
    }
}
