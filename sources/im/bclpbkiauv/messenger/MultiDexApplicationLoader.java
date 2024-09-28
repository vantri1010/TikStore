package im.bclpbkiauv.messenger;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.Utils;

public class MultiDexApplicationLoader extends ApplicationLoader {
    /* access modifiers changed from: protected */
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        Utils.init((Application) this);
        SDKInitializer.initialize(this);
    }
}
