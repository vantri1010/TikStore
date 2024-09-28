package im.bclpbkiauv.ui.components;

import android.content.DialogInterface;
import im.bclpbkiauv.ui.utils.AppUpdater;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$UpdateAppAlertDialog$NO1jHDQfbwNnvtlbLa-yqMUoHL0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$UpdateAppAlertDialog$NO1jHDQfbwNnvtlbLayqMUoHL0 implements DialogInterface.OnDismissListener {
    public static final /* synthetic */ $$Lambda$UpdateAppAlertDialog$NO1jHDQfbwNnvtlbLayqMUoHL0 INSTANCE = new $$Lambda$UpdateAppAlertDialog$NO1jHDQfbwNnvtlbLayqMUoHL0();

    private /* synthetic */ $$Lambda$UpdateAppAlertDialog$NO1jHDQfbwNnvtlbLayqMUoHL0() {
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        AppUpdater.dismissCheckUpdateTime = System.currentTimeMillis();
    }
}
