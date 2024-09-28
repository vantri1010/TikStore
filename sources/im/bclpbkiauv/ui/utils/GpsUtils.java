package im.bclpbkiauv.ui.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.ui.components.toast.ToastUtils;

public class GpsUtils {
    public static boolean isOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService("location");
        boolean gps = locationManager.isProviderEnabled("gps");
        boolean network = locationManager.isProviderEnabled("network");
        if (gps || network) {
            return true;
        }
        return false;
    }

    public static void toGpsSettingActivity(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.settings.LOCATION_SOURCE_SETTINGS");
        intent.setFlags(C.ENCODING_PCM_MU_LAW);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.show((CharSequence) "跳转失败");
        }
    }
}
