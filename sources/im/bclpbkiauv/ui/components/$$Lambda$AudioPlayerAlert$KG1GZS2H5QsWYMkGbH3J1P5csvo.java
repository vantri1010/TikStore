package im.bclpbkiauv.ui.components;

import android.view.View;
import im.bclpbkiauv.messenger.MediaController;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$KG1GZS2H5QsWYMkGbH3J1P5csvo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AudioPlayerAlert$KG1GZS2H5QsWYMkGbH3J1P5csvo implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$AudioPlayerAlert$KG1GZS2H5QsWYMkGbH3J1P5csvo INSTANCE = new $$Lambda$AudioPlayerAlert$KG1GZS2H5QsWYMkGbH3J1P5csvo();

    private /* synthetic */ $$Lambda$AudioPlayerAlert$KG1GZS2H5QsWYMkGbH3J1P5csvo() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playNextMessage();
    }
}
