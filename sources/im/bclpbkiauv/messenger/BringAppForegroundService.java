package im.bclpbkiauv.messenger;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.ui.LaunchActivity;

public class BringAppForegroundService extends IntentService {
    public BringAppForegroundService() {
        super("BringAppForegroundService");
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        Intent intent2 = new Intent(this, LaunchActivity.class);
        intent2.setFlags(C.ENCODING_PCM_MU_LAW);
        intent2.setAction("android.intent.action.MAIN");
        startActivity(intent2);
    }
}
