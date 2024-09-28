package im.bclpbkiauv.ui.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class SimulatorUtil {
    public static boolean isSimulator(Context context) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("tel:123456"));
        intent.setAction("android.intent.action.DIAL");
        boolean canCallPhone = intent.resolveActivity(context.getPackageManager()) != null;
        boolean isSimulator = Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.toLowerCase().contains("vbox") || Build.FINGERPRINT.toLowerCase().contains("test-keys") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("MuMu") || Build.MODEL.contains("virtual") || Build.SERIAL.equalsIgnoreCase("android") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk".equals(Build.PRODUCT) || ((TelephonyManager) context.getSystemService("phone")).getNetworkOperatorName().toLowerCase().equals("android") || !canCallPhone;
        if (!isSimulator && !hasLightSensor(context).booleanValue()) {
            return true;
        }
        if (!isSimulator || !canCallPhone || !Build.BRAND.equalsIgnoreCase("HUAWEI") || !hasLightSensor(context).booleanValue()) {
            return isSimulator;
        }
        return false;
    }

    public boolean notHasBlueTooth() {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba != null && !TextUtils.isEmpty(ba.getName())) {
            return false;
        }
        return true;
    }

    public static Boolean hasLightSensor(Context context) {
        if (((SensorManager) context.getSystemService("sensor")).getDefaultSensor(5) == null) {
            return false;
        }
        return true;
    }
}
