package com.alivc.rtc.device;

import android.content.Context;
import android.os.Binder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.alivc.rtc.device.core.persistent.PersistentConfiguration;
import com.alivc.rtc.device.utils.Base64;
import com.alivc.rtc.device.utils.IntUtils;
import com.alivc.rtc.device.utils.PhoneInfoUtils;
import com.alivc.rtc.device.utils.RC4;
import com.alivc.rtc.device.utils.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UTUtdid {
    private static final Object CREATE_LOCK = new Object();
    private static final String S_GLOBAL_PERSISTENT_CONFIG_DIR = (".UTAliRtc" + File.separator + "Global");
    private static final String S_GLOBAL_PERSISTENT_CONFIG_KEY = "AlRtc";
    private static final String S_LOCAL_STORAGE_KEY = "ContextData";
    private static final String S_LOCAL_STORAGE_NAME = ".RtcDataStorage";
    private static final String UM_SETTINGS_STORAGE = "dxCRMxhQkdGePGnp";
    private static final String UM_SETTINGS_STORAGE_NEW = "mqBRboGZkQPcAkyk";
    private static UTUtdid s_umutdid = null;
    private String mCBDomain = "xx_utdid_domain";
    private String mCBKey = "xx_utdid_key";
    private Context mContext = null;
    private PersistentConfiguration mPC = null;
    private Pattern mPattern = Pattern.compile("[^0-9a-zA-Z=/+]+");
    private PersistentConfiguration mTaoPC = null;
    private String mUtdid = null;
    private UTUtdidHelper mUtdidHelper = null;

    private UTUtdid(Context context) {
        this.mContext = context;
        this.mTaoPC = new PersistentConfiguration(context, S_GLOBAL_PERSISTENT_CONFIG_DIR, S_GLOBAL_PERSISTENT_CONFIG_KEY, false, true);
        this.mPC = new PersistentConfiguration(context, S_LOCAL_STORAGE_NAME, S_LOCAL_STORAGE_KEY, false, true);
        this.mUtdidHelper = new UTUtdidHelper();
        this.mCBKey = String.format("K_%d", new Object[]{Integer.valueOf(StringUtils.hashCode(this.mCBKey))});
        this.mCBDomain = String.format("D_%d", new Object[]{Integer.valueOf(StringUtils.hashCode(this.mCBDomain))});
    }

    private void removeIllegalKeys() {
        PersistentConfiguration persistentConfiguration = this.mTaoPC;
        if (persistentConfiguration != null) {
            if (StringUtils.isEmpty(persistentConfiguration.getString("UTDID2"))) {
                String lUtdid = this.mTaoPC.getString("UTDID");
                if (!StringUtils.isEmpty(lUtdid)) {
                    saveUtdidToTaoPPC(lUtdid);
                }
            }
            boolean lNeedSync = false;
            if (!StringUtils.isEmpty(this.mTaoPC.getString("DID"))) {
                this.mTaoPC.remove("DID");
                lNeedSync = true;
            }
            if (!StringUtils.isEmpty(this.mTaoPC.getString("EI"))) {
                this.mTaoPC.remove("EI");
                lNeedSync = true;
            }
            if (!StringUtils.isEmpty(this.mTaoPC.getString("SI"))) {
                this.mTaoPC.remove("SI");
                lNeedSync = true;
            }
            if (lNeedSync) {
                this.mTaoPC.commit();
            }
        }
    }

    public static UTUtdid instance(Context context) {
        if (context != null && s_umutdid == null) {
            synchronized (CREATE_LOCK) {
                if (s_umutdid == null) {
                    UTUtdid uTUtdid = new UTUtdid(context);
                    s_umutdid = uTUtdid;
                    uTUtdid.removeIllegalKeys();
                }
            }
        }
        return s_umutdid;
    }

    private void saveUtdidToTaoPPC(String pUtdid) {
        PersistentConfiguration persistentConfiguration;
        if (isValidUtdid(pUtdid)) {
            if (pUtdid.endsWith("\n")) {
                pUtdid = pUtdid.substring(0, pUtdid.length() - 1);
            }
            if (pUtdid.length() == 24 && (persistentConfiguration = this.mTaoPC) != null) {
                persistentConfiguration.putString("UTDID2", pUtdid);
                this.mTaoPC.commit();
            }
        }
    }

    private void saveUtdidToLocalStorage(String pPackedUtdid) {
        PersistentConfiguration persistentConfiguration;
        if (pPackedUtdid != null && (persistentConfiguration = this.mPC) != null && !pPackedUtdid.equals(persistentConfiguration.getString(this.mCBKey))) {
            this.mPC.putString(this.mCBKey, pPackedUtdid);
            this.mPC.commit();
        }
    }

    private void saveUtdidToNewSettings(String lUtdid) {
        if (checkSettingsPermission() && isValidUtdid(lUtdid)) {
            if (lUtdid.endsWith("\n")) {
                lUtdid = lUtdid.substring(0, lUtdid.length() - 1);
            }
            if (24 == lUtdid.length()) {
                String data = null;
                try {
                    data = Settings.System.getString(this.mContext.getContentResolver(), UM_SETTINGS_STORAGE_NEW);
                    Log.e("ydsyds", "saveUtdidToNewSettings,getString uuid:" + data);
                } catch (Exception var5) {
                    Log.e("ydsyds", "saveUtdidToNewSettings,getString Exception" + var5.getMessage());
                }
                if (!isValidUtdid(data)) {
                    try {
                        Settings.System.putString(this.mContext.getContentResolver(), UM_SETTINGS_STORAGE_NEW, lUtdid);
                        Log.e("ydsyds", "saveUtdidToNewSettings,is not ValidUtdid putString uuid:" + data);
                    } catch (Exception var4) {
                        Log.e("ydsyds", "saveUtdidToNewSettings,putString Exception" + var4.getMessage());
                    }
                }
            }
        }
    }

    private void syncUtdidToSettings(String pPackedUtdid) {
        String data = null;
        try {
            data = Settings.System.getString(this.mContext.getContentResolver(), UM_SETTINGS_STORAGE);
            Log.e("ydsyds", "syncUtdidToSettings,getString uuid:" + data);
        } catch (Exception var5) {
            Log.e("ydsyds", "syncUtdidToSettings,putString Exception" + var5.getMessage());
        }
        if (!pPackedUtdid.equals(data)) {
            try {
                Settings.System.putString(this.mContext.getContentResolver(), UM_SETTINGS_STORAGE, pPackedUtdid);
                Log.e("ydsyds", "syncUtdidToSettings,getString uuid:" + data);
            } catch (Exception var4) {
                Log.e("ydsyds", "syncUtdidToSettings,putString Exception" + var4.getMessage());
            }
        }
    }

    private void saveUtdidToSettings(String lPackedUtdid) {
        if (checkSettingsPermission() && lPackedUtdid != null) {
            syncUtdidToSettings(lPackedUtdid);
        }
    }

    private String getUtdidFromTaoPPC() {
        PersistentConfiguration persistentConfiguration = this.mTaoPC;
        if (persistentConfiguration == null) {
            return null;
        }
        String lUtdid = persistentConfiguration.getString("UTDID2");
        if (StringUtils.isEmpty(lUtdid) || this.mUtdidHelper.packUtdidStr(lUtdid) == null) {
            return null;
        }
        return lUtdid;
    }

    private boolean isValidUtdid(String pUtdid) {
        if (pUtdid != null) {
            if (pUtdid.endsWith("\n")) {
                pUtdid = pUtdid.substring(0, pUtdid.length() - 1);
            }
            if (24 != pUtdid.length() || this.mPattern.matcher(pUtdid).find()) {
                return false;
            }
            return true;
        }
        return false;
    }

    public synchronized String getValue() {
        StringBuilder sb = new StringBuilder();
        sb.append("mUtdid.getValue,mUtdid is null = ");
        sb.append(this.mUtdid == null);
        Log.e("ydsyds", sb.toString());
        return this.mUtdid != null ? this.mUtdid : getValueForUpdate();
    }

    public synchronized String getValueForUpdate() {
        Log.e("ydsyds", "mUtdid.getValueForUpdate");
        String readUtdid = readUtdid();
        this.mUtdid = readUtdid;
        if (!TextUtils.isEmpty(readUtdid)) {
            return this.mUtdid;
        }
        try {
            byte[] lUtdid = generateUtdid();
            if (lUtdid != null) {
                String encodeToString = Base64.encodeToString(lUtdid, 2);
                this.mUtdid = encodeToString;
                saveUtdidToTaoPPC(encodeToString);
                String lPackedUtdid = this.mUtdidHelper.pack(lUtdid);
                if (lPackedUtdid != null) {
                    saveUtdidToSettings(lPackedUtdid);
                    saveUtdidToLocalStorage(lPackedUtdid);
                }
                return this.mUtdid;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        return null;
    }

    public synchronized String readUtdid() {
        Log.e("ydsyds", "mUtdid.readUtdid");
        String lNewSettingsUtdid = "";
        try {
            Log.e("ydsyds", "mUtdid.readUtdid lNewSettingsUtdidsetting name is = UM_SETTINGS_STORAGE_NEW");
            lNewSettingsUtdid = Settings.System.getString(this.mContext.getContentResolver(), UM_SETTINGS_STORAGE_NEW);
        } catch (Exception e) {
        }
        if (isValidUtdid(lNewSettingsUtdid)) {
            Log.e("ydsyds", "mUtdid.readUtdid lNewSettingsUtdidisValidUtdid");
            return lNewSettingsUtdid;
        }
        Log.e("ydsyds", "mUtdid.readUtdid lNewSettingsUtdidis not Valid Utdid");
        UTUtdidHelper2 lHelper2 = new UTUtdidHelper2();
        boolean lNeedUpdateSettings = false;
        String data = null;
        try {
            data = Settings.System.getString(this.mContext.getContentResolver(), UM_SETTINGS_STORAGE);
        } catch (Exception e2) {
        }
        if (!StringUtils.isEmpty(data)) {
            String lSUtdid = lHelper2.dePackWithBase64(data);
            if (isValidUtdid(lSUtdid)) {
                saveUtdidToNewSettings(lSUtdid);
                return lSUtdid;
            }
            String lContent = lHelper2.dePack(data);
            if (isValidUtdid(lContent)) {
                String lUtdid = this.mUtdidHelper.packUtdidStr(lContent);
                if (!StringUtils.isEmpty(lUtdid)) {
                    saveUtdidToSettings(lUtdid);
                    try {
                        data = Settings.System.getString(this.mContext.getContentResolver(), UM_SETTINGS_STORAGE);
                    } catch (Exception e3) {
                    }
                }
            }
            String lUtdid2 = this.mUtdidHelper.dePack(data);
            if (isValidUtdid(lUtdid2)) {
                this.mUtdid = lUtdid2;
                saveUtdidToTaoPPC(lUtdid2);
                saveUtdidToLocalStorage(data);
                saveUtdidToNewSettings(this.mUtdid);
                return this.mUtdid;
            }
        } else {
            lNeedUpdateSettings = true;
        }
        String lSUtdid2 = getUtdidFromTaoPPC();
        if (isValidUtdid(lSUtdid2)) {
            String lContent2 = this.mUtdidHelper.packUtdidStr(lSUtdid2);
            if (lNeedUpdateSettings) {
                saveUtdidToSettings(lContent2);
            }
            saveUtdidToNewSettings(lSUtdid2);
            saveUtdidToLocalStorage(lContent2);
            this.mUtdid = lSUtdid2;
            return lSUtdid2;
        }
        String lContent3 = this.mPC.getString(this.mCBKey);
        if (!StringUtils.isEmpty(lContent3)) {
            String lUtdid3 = lHelper2.dePack(lContent3);
            if (!isValidUtdid(lUtdid3)) {
                lUtdid3 = this.mUtdidHelper.dePack(lContent3);
            }
            if (isValidUtdid(lUtdid3)) {
                String lBUtdid = this.mUtdidHelper.packUtdidStr(lUtdid3);
                if (!StringUtils.isEmpty(lUtdid3)) {
                    this.mUtdid = lUtdid3;
                    if (lNeedUpdateSettings) {
                        saveUtdidToSettings(lBUtdid);
                    }
                    saveUtdidToTaoPPC(this.mUtdid);
                    return this.mUtdid;
                }
            }
        }
        return null;
    }

    private byte[] generateUtdid() throws Exception {
        String imei;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        int uniqueID = new Random().nextInt();
        byte[] bTimestamp = IntUtils.getBytes(timestamp);
        byte[] bUniqueID = IntUtils.getBytes(uniqueID);
        baos.write(bTimestamp, 0, 4);
        baos.write(bUniqueID, 0, 4);
        baos.write(3);
        baos.write(0);
        try {
            imei = PhoneInfoUtils.getImei(this.mContext);
        } catch (Exception e) {
            imei = "" + new Random().nextInt();
        }
        baos.write(IntUtils.getBytes(StringUtils.hashCode(imei)), 0, 4);
        baos.write(IntUtils.getBytes(StringUtils.hashCode(calcHmac(baos.toByteArray()))));
        return baos.toByteArray();
    }

    public static String calcHmac(byte[] src) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(RC4.rc4(new byte[]{69, 114, 116, -33, 125, -54, -31, 86, -11, 11, -78, -96, -17, -99, 64, 23, -95, -126, -82, -64, 113, 116, -16, -103, 49, -30, 9, -39, 33, -80, -68, -78, -117, 53, 30, -122, 64, -104, 74, -49, 106, 85, -38, -93}), mac.getAlgorithm()));
        return Base64.encodeToString(mac.doFinal(src), 2);
    }

    private boolean checkSettingsPermission() {
        return this.mContext.checkPermission("android.permission.WRITE_SETTINGS", Binder.getCallingPid(), Binder.getCallingUid()) == 0;
    }
}
