package im.bclpbkiauv.messenger;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$LocationController$n0Bacs9RivvwlnqeEHaHLj8vN-g  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$n0Bacs9RivvwlnqeEHaHLj8vNg implements Runnable {
    public static final /* synthetic */ $$Lambda$LocationController$n0Bacs9RivvwlnqeEHaHLj8vNg INSTANCE = new $$Lambda$LocationController$n0Bacs9RivvwlnqeEHaHLj8vNg();

    private /* synthetic */ $$Lambda$LocationController$n0Bacs9RivvwlnqeEHaHLj8vNg() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.newLocationAvailable, new Object[0]);
    }
}
