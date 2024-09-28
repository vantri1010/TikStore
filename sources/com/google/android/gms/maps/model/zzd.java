package com.google.android.gms.maps.model;

import android.os.Parcelable;

public final class zzd implements Parcelable.Creator<GroundOverlayOptions> {
    public final /* synthetic */ Object[] newArray(int i) {
        return new GroundOverlayOptions[i];
    }

    /* JADX WARNING: type inference failed for: r2v3, types: [android.os.Parcelable] */
    /* JADX WARNING: type inference failed for: r2v4, types: [android.os.Parcelable] */
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
            r6 = r3
            r7 = r6
            r10 = r7
            r8 = 0
            r9 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x0022:
            int r2 = r19.dataPosition()
            if (r2 >= r1) goto L_0x0087
            int r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readHeader(r19)
            int r3 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.getFieldId(r2)
            switch(r3) {
                case 2: goto L_0x0081;
                case 3: goto L_0x0077;
                case 4: goto L_0x0071;
                case 5: goto L_0x006b;
                case 6: goto L_0x0061;
                case 7: goto L_0x005b;
                case 8: goto L_0x0055;
                case 9: goto L_0x004f;
                case 10: goto L_0x0049;
                case 11: goto L_0x0043;
                case 12: goto L_0x003d;
                case 13: goto L_0x0037;
                default: goto L_0x0033;
            }
        L_0x0033:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.skipUnknownField(r0, r2)
            goto L_0x0022
        L_0x0037:
            boolean r17 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r0, r2)
            goto L_0x0022
        L_0x003d:
            float r16 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0022
        L_0x0043:
            float r15 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0022
        L_0x0049:
            float r14 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0022
        L_0x004f:
            boolean r13 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readBoolean(r0, r2)
            goto L_0x0022
        L_0x0055:
            float r12 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0022
        L_0x005b:
            float r11 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0022
        L_0x0061:
            android.os.Parcelable$Creator<com.google.android.gms.maps.model.LatLngBounds> r3 = com.google.android.gms.maps.model.LatLngBounds.CREATOR
            android.os.Parcelable r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createParcelable(r0, r2, r3)
            r10 = r2
            com.google.android.gms.maps.model.LatLngBounds r10 = (com.google.android.gms.maps.model.LatLngBounds) r10
            goto L_0x0022
        L_0x006b:
            float r9 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0022
        L_0x0071:
            float r8 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readFloat(r0, r2)
            goto L_0x0022
        L_0x0077:
            android.os.Parcelable$Creator<com.google.android.gms.maps.model.LatLng> r3 = com.google.android.gms.maps.model.LatLng.CREATOR
            android.os.Parcelable r2 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.createParcelable(r0, r2, r3)
            r7 = r2
            com.google.android.gms.maps.model.LatLng r7 = (com.google.android.gms.maps.model.LatLng) r7
            goto L_0x0022
        L_0x0081:
            android.os.IBinder r6 = com.google.android.gms.common.internal.safeparcel.SafeParcelReader.readIBinder(r0, r2)
            goto L_0x0022
        L_0x0087:
            com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ensureAtEnd(r0, r1)
            com.google.android.gms.maps.model.GroundOverlayOptions r0 = new com.google.android.gms.maps.model.GroundOverlayOptions
            r5 = r0
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.maps.model.zzd.createFromParcel(android.os.Parcel):java.lang.Object");
    }
}
