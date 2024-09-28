package com.alivc.rtc.device;

import com.alivc.rtc.device.utils.AESUtils;
import com.alivc.rtc.device.utils.Base64;

public class UTUtdidHelper {
    public String pack(byte[] pUtdid) {
        return AESUtils.encrypt(Base64.encodeToString(pUtdid, 2));
    }

    public String packUtdidStr(String pUtdid) {
        return AESUtils.encrypt(pUtdid);
    }

    public String dePack(String pPackedUtdid) {
        return AESUtils.decrypt(pPackedUtdid);
    }
}
