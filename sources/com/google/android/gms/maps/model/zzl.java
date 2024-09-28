package com.google.android.gms.maps.model;

import android.os.Parcelable;

public final class zzl implements Parcelable.Creator<PolylineOptions> {
    public final /* synthetic */ Object[] newArray(int i) {
        return new PolylineOptions[i];
    }

    /* JADX WARNING: type inference failed for: r2v3, types: [android.os.Parcelable] */
    /* JADX WARNING: type inference failed for: r2v4, types: [android.os.Parcelable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final /* synthetic */ java.lang.Object createFromParcel(android.os.Parcel r18) {
        /*
            r17 = this;
            r0 = r18
            int r1 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.validateObjectHeader(r18)
            r2 = 0
            r3 = 0
            r4 = 0
            r6 = r3
            r13 = r6
            r14 = r13
            r16 = r14
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r15 = 0
        L_0x001f:
            int r2 = r18.dataPosition()
            if (r2 >= r1) goto L_0x0080
            int r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readHeader(r18)
            int r3 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.getFieldId(r2)
            switch(r3) {
                case 2: goto L_0x0079;
                case 3: goto L_0x0073;
                case 4: goto L_0x006d;
                case 5: goto L_0x0067;
                case 6: goto L_0x0061;
                case 7: goto L_0x005b;
                case 8: goto L_0x0055;
                case 9: goto L_0x004b;
                case 10: goto L_0x0041;
                case 11: goto L_0x003b;
                case 12: goto L_0x0034;
                default: goto L_0x0030;
            }
        L_0x0030:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.skipUnknownField(r0, r2)
            goto L_0x001f
        L_0x0034:
            android.os.Parcelable$Creator<com.google.android.gms.maps.model.PatternItem> r3 = com.google.android.gms.maps.model.PatternItem.CREATOR
            java.util.ArrayList r16 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createTypedList(r0, r2, r3)
            goto L_0x001f
        L_0x003b:
            int r15 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readInt(r0, r2)
            goto L_0x001f
        L_0x0041:
            android.os.Parcelable$Creator<com.google.android.gms.maps.model.Cap> r3 = com.google.android.gms.maps.model.Cap.CREATOR
            android.os.Parcelable r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createParcelable(r0, r2, r3)
            r14 = r2
            com.google.android.gms.maps.model.Cap r14 = (com.google.android.gms.maps.model.Cap) r14
            goto L_0x001f
        L_0x004b:
            android.os.Parcelable$Creator<com.google.android.gms.maps.model.Cap> r3 = com.google.android.gms.maps.model.Cap.CREATOR
            android.os.Parcelable r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createParcelable(r0, r2, r3)
            r13 = r2
            com.google.android.gms.maps.model.Cap r13 = (com.google.android.gms.maps.model.Cap) r13
            goto L_0x001f
        L_0x0055:
            boolean r12 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r0, r2)
            goto L_0x001f
        L_0x005b:
            boolean r11 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r0, r2)
            goto L_0x001f
        L_0x0061:
            boolean r10 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r0, r2)
            goto L_0x001f
        L_0x0067:
            float r9 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x001f
        L_0x006d:
            int r8 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readInt(r0, r2)
            goto L_0x001f
        L_0x0073:
            float r7 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x001f
        L_0x0079:
            android.os.Parcelable$Creator<com.google.android.gms.maps.model.LatLng> r3 = com.google.android.gms.maps.model.LatLng.CREATOR
            java.util.ArrayList r6 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createTypedList(r0, r2, r3)
            goto L_0x001f
        L_0x0080:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ensureAtEnd(r0, r1)
            com.google.android.gms.maps.model.PolylineOptions r0 = new com.google.android.gms.maps.model.PolylineOptions
            r5 = r0
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.maps.model.zzl.createFromParcel(android.os.Parcel):java.lang.Object");
    }
}
