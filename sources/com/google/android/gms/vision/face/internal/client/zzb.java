package com.google.android.gms.vision.face.internal.client;

import android.os.Parcelable;

public final class zzb implements Parcelable.Creator<FaceParcel> {
    /* JADX WARNING: type inference failed for: r2v3, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final /* synthetic */ java.lang.Object createFromParcel(android.os.Parcel r19) {
        /*
            r18 = this;
            r0 = r19
            int r1 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.validateObjectHeader(r19)
            r2 = 0
            r3 = 0
            r4 = 0
            r14 = r4
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x0017:
            int r2 = r19.dataPosition()
            if (r2 >= r1) goto L_0x006d
            int r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readHeader(r19)
            int r3 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.getFieldId(r2)
            switch(r3) {
                case 1: goto L_0x0068;
                case 2: goto L_0x0063;
                case 3: goto L_0x005e;
                case 4: goto L_0x0059;
                case 5: goto L_0x0054;
                case 6: goto L_0x004f;
                case 7: goto L_0x004a;
                case 8: goto L_0x0045;
                case 9: goto L_0x003b;
                case 10: goto L_0x0036;
                case 11: goto L_0x0031;
                case 12: goto L_0x002c;
                default: goto L_0x0028;
            }
        L_0x0028:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.skipUnknownField(r0, r2)
            goto L_0x0017
        L_0x002c:
            float r17 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x0031:
            float r16 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x0036:
            float r15 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x003b:
            android.os.Parcelable$Creator<com.google.android.gms.vision.face.internal.client.LandmarkParcel> r3 = com.google.android.gms.vision.face.internal.client.LandmarkParcel.CREATOR
            java.lang.Object[] r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createTypedArray(r0, r2, r3)
            r14 = r2
            com.google.android.gms.vision.face.internal.client.LandmarkParcel[] r14 = (com.google.android.gms.vision.face.internal.client.LandmarkParcel[]) r14
            goto L_0x0017
        L_0x0045:
            float r13 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x004a:
            float r12 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x004f:
            float r11 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x0054:
            float r10 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x0059:
            float r9 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x005e:
            float r8 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0017
        L_0x0063:
            int r7 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readInt(r0, r2)
            goto L_0x0017
        L_0x0068:
            int r6 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readInt(r0, r2)
            goto L_0x0017
        L_0x006d:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ensureAtEnd(r0, r1)
            com.google.android.gms.vision.face.internal.client.FaceParcel r0 = new com.google.android.gms.vision.face.internal.client.FaceParcel
            r5 = r0
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.vision.face.internal.client.zzb.createFromParcel(android.os.Parcel):java.lang.Object");
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new FaceParcel[i];
    }
}
