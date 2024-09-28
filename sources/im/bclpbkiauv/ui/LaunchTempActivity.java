package im.bclpbkiauv.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.C;

public class LaunchTempActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent remoteIntent = getIntent();
        Intent target = new Intent();
        target.setFlags(C.ENCODING_PCM_MU_LAW);
        target.setClass(this, LaunchActivity.class);
        target.setAction(remoteIntent.getAction());
        target.setData(remoteIntent.getData());
        startActivity(target);
        finish();
    }
}
