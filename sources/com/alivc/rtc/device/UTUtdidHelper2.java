package com.alivc.rtc.device;

import com.alivc.rtc.device.utils.AESUtils;
import com.alivc.rtc.device.utils.Base64;
import com.alivc.rtc.device.utils.StringUtils;

public class UTUtdidHelper2 {
    public String dePack(String pPackedUtdid) {
        return AESUtils.decrypt(pPackedUtdid);
    }

    public String dePackWithBase64(String pUtdidWithBase64) {
        String lEResult = AESUtils.decrypt(pUtdidWithBase64);
        if (StringUtils.isEmpty(lEResult)) {
            return null;
        }
        try {
            return new String(Base64.decode(lEResult, 0));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
