package im.bclpbkiauv.ui.hui.wallet_public.bean;

import android.text.TextUtils;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class WalletAccountInfo {
    private static final int AUTH_CHECKING = 0;
    private static final int AUTH_FAILED = 2;
    private static final int AUTH_NONCE = -1;
    private static final int AUTH_SUCCESS = 1;
    private String basicsStatus;
    private String bindBankStatus;
    private String cashAmount;
    private double freezeOthers;
    private String frozenCash;
    private String isSetPayWord;
    private double otherAmount;
    private List<WalletConfigBean.Bean> riskList;
    private String seniorStatus;
    private int status;
    private Integer type;
    private String userName;

    @Retention(RetentionPolicy.SOURCE)
    private @interface CheckStatus {
    }

    public double getFreezeOthers() {
        return this.freezeOthers;
    }

    public String getIsSetPayWord() {
        return this.isSetPayWord;
    }

    public void setIsSetPayWord(String isSetPayWord2) {
        this.isSetPayWord = isSetPayWord2;
    }

    @Deprecated
    public double getOtherAmount() {
        return this.otherAmount;
    }

    public double getCashAmount() {
        if (NumberUtil.isNumber(this.cashAmount)) {
            return Double.parseDouble(this.cashAmount);
        }
        return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    }

    public double getFrozenCash() {
        if (NumberUtil.isNumber(this.frozenCash)) {
            return Double.parseDouble(this.frozenCash);
        }
        return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    }

    public int getStatus() {
        return this.status;
    }

    public String getUserName() {
        return this.userName;
    }

    public int getAuthNormalStatus() {
        if (!TextUtils.isEmpty(this.basicsStatus)) {
            return Integer.valueOf(this.basicsStatus).intValue();
        }
        return -1;
    }

    public int getAuthVipStatus() {
        if (!TextUtils.isEmpty(this.seniorStatus)) {
            return Integer.valueOf(this.seniorStatus).intValue();
        }
        return -1;
    }

    public int getBindBankStatus() {
        if (!TextUtils.isEmpty(this.bindBankStatus)) {
            return Integer.valueOf(this.bindBankStatus).intValue();
        }
        return -1;
    }

    public int getUserRollType() {
        Integer num = this.type;
        if (num == null) {
            num = 0;
            this.type = num;
        }
        return num.intValue();
    }

    public boolean isLocked() {
        return this.status == 0;
    }

    public boolean hasPaypassword() {
        return "1".equals(this.isSetPayWord);
    }

    public boolean hasBindBank() {
        return "1".equals(this.bindBankStatus);
    }

    public boolean hasNormalAuth() {
        return "1".equals(this.basicsStatus);
    }

    public List<WalletConfigBean.Bean> getRiskList() {
        return this.riskList;
    }
}
