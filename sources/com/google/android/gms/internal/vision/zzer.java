package com.google.android.gms.internal.vision;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class zzer extends zzeq<FieldDescriptorType, Object> {
    zzer(int i) {
        super(i, (zzer) null);
    }

    public final void zzao() {
        if (!isImmutable()) {
            for (int i = 0; i < zzdl(); i++) {
                Map.Entry zzan = zzan(i);
                if (((zzcl) zzan.getKey()).zzbq()) {
                    zzan.setValue(Collections.unmodifiableList((List) zzan.getValue()));
                }
            }
            for (Map.Entry entry : zzdm()) {
                if (((zzcl) entry.getKey()).zzbq()) {
                    entry.setValue(Collections.unmodifiableList((List) entry.getValue()));
                }
            }
        }
        super.zzao();
    }
}
