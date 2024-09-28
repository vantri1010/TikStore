package com.google.android.gms.internal.vision;

final class zzce {
    private static final Class<?> zzhn = zzbd();

    private static Class<?> zzbd() {
        try {
            return Class.forName("com.google.protobuf.ExtensionRegistry");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static zzcf zzbe() {
        Class<?> cls = zzhn;
        if (cls != null) {
            try {
                return (zzcf) cls.getDeclaredMethod("getEmptyRegistry", new Class[0]).invoke((Object) null, new Object[0]);
            } catch (Exception e) {
            }
        }
        return zzcf.zzhq;
    }
}
