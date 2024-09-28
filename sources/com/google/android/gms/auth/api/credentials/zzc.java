package com.google.android.gms.auth.api.credentials;

import android.os.Parcelable;

public final class zzc implements Parcelable.Creator<Credential> {
    public final /* synthetic */ Object[] newArray(int i) {
        return new Credential[i];
    }

    /* JADX WARNING: type inference failed for: r1v3, types: [android.os.Parcelable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final /* synthetic */ java.lang.Object createFromParcel(android.os.Parcel r12) {
        /*
            r11 = this;
            int r0 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.validateObjectHeader(r12)
            r1 = 0
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r7 = r6
            r8 = r7
            r9 = r8
            r10 = r9
        L_0x0015:
            int r1 = r12.dataPosition()
            if (r1 >= r0) goto L_0x005f
            int r1 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readHeader(r12)
            int r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.getFieldId(r1)
            switch(r2) {
                case 1: goto L_0x0059;
                case 2: goto L_0x0053;
                case 3: goto L_0x0049;
                case 4: goto L_0x0042;
                case 5: goto L_0x003c;
                case 6: goto L_0x0036;
                case 7: goto L_0x0026;
                case 8: goto L_0x0026;
                case 9: goto L_0x0030;
                case 10: goto L_0x002a;
                default: goto L_0x0026;
            }
        L_0x0026:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.skipUnknownField(r12, r1)
            goto L_0x0015
        L_0x002a:
            java.lang.String r10 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r12, r1)
            goto L_0x0015
        L_0x0030:
            java.lang.String r9 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r12, r1)
            goto L_0x0015
        L_0x0036:
            java.lang.String r8 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r12, r1)
            goto L_0x0015
        L_0x003c:
            java.lang.String r7 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r12, r1)
            goto L_0x0015
        L_0x0042:
            android.os.Parcelable$Creator<com.google.android.gms.auth.api.credentials.IdToken> r2 = com.google.android.gms.auth.api.credentials.IdToken.CREATOR
            java.util.ArrayList r6 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createTypedList(r12, r1, r2)
            goto L_0x0015
        L_0x0049:
            android.os.Parcelable$Creator r2 = android.net.Uri.CREATOR
            android.os.Parcelable r1 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createParcelable(r12, r1, r2)
            r5 = r1
            android.net.Uri r5 = (android.net.Uri) r5
            goto L_0x0015
        L_0x0053:
            java.lang.String r4 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r12, r1)
            goto L_0x0015
        L_0x0059:
            java.lang.String r3 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createString(r12, r1)
            goto L_0x0015
        L_0x005f:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ensureAtEnd(r12, r0)
            com.google.android.gms.auth.api.credentials.Credential r12 = new com.google.android.gms.auth.api.credentials.Credential
            r2 = r12
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.auth.api.credentials.zzc.createFromParcel(android.os.Parcel):java.lang.Object");
    }
}
