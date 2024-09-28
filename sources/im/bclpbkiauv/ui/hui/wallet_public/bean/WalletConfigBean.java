package im.bclpbkiauv.ui.hui.wallet_public.bean;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.utils.number.MoneyUtil;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import java.util.List;

public class WalletConfigBean {
    private static volatile WalletConfigBean[] Instance = new WalletConfigBean[3];
    private int basicsStatus;
    private int bindBankStatus;
    private double buyMaxMoneyOneDay;
    private int buyMinCountOneTrade;
    private int buyPayTermTime;
    private int cancelMaxAllowCountsOneDay;
    private double cashAmount;
    private int currentAccount;
    private int forzenPayPasswordInputWrongTimes;
    private int forzenTime;
    private double freezeOthers;
    private double frozenCash;
    private String isSetPayWord;
    private double otherAmount;
    private double payRate;
    private int putCoinsTermTime;
    private int redPacketMaxCountOneDay;
    private double redPacketMaxMoneyOneDay;
    private double redPacketMaxMoneySingleTime;
    private int sellMaxCountOneDay;
    private double sellMaxMoneyOneDay;
    private int sellMinCountOneTrade;
    private int seniorStatus;
    private int status;
    private int tradeLimitTime;
    private int tradePayPasswordInputWrongTimes;
    private double transUnitPrice;
    private double transferMaxMoneySingleTime;
    private int type;
    private String userName;

    public static WalletConfigBean getInstance() {
        return getInstance(UserConfig.selectedAccount);
    }

    public static WalletConfigBean getInstance(int num) {
        WalletConfigBean localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (WalletConfigBean.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    WalletConfigBean[] walletConfigBeanArr = Instance;
                    WalletConfigBean walletConfigBean = new WalletConfigBean(num);
                    localInstance = walletConfigBean;
                    walletConfigBeanArr[num] = walletConfigBean;
                }
            }
        }
        return localInstance;
    }

    private WalletConfigBean(int currentAccount2) {
        this.currentAccount = currentAccount2;
    }

    public int getAuthNormalStatus() {
        return this.basicsStatus;
    }

    public boolean isAuthNormalEnable() {
        return getAuthNormalStatus() == 1;
    }

    public int getAuthVipStatus() {
        return this.seniorStatus;
    }

    public int getBindBankStatus() {
        return this.bindBankStatus;
    }

    public boolean isBindBankCardEnable() {
        return getBindBankStatus() == 1;
    }

    public int getUserRollType() {
        return this.type;
    }

    public boolean hasSetPayPassword() {
        return "1".equals(this.isSetPayWord);
    }

    public double getPayRate() {
        return this.payRate;
    }

    public int getBuyMinCountOneTrade() {
        return this.buyMinCountOneTrade;
    }

    public double getTransUnitPrice() {
        return this.transUnitPrice;
    }

    public double getBuyMaxMoneyOneDay() {
        return this.buyMaxMoneyOneDay;
    }

    public int getSellMaxCountOneDay() {
        return this.sellMaxCountOneDay;
    }

    public double getSellMaxMoneyOneDay() {
        return this.sellMaxMoneyOneDay;
    }

    public int getRedPacketMaxCountOneDay() {
        return this.redPacketMaxCountOneDay;
    }

    public double getRedPacketMaxMoneyOneDay() {
        return this.redPacketMaxMoneyOneDay;
    }

    public double getRedPacketMaxMoneySingleTime() {
        return this.redPacketMaxMoneySingleTime;
    }

    public double getTransferMaxMoneySingleTime() {
        return this.transferMaxMoneySingleTime;
    }

    public int getCancelMaxAllowCountsOneDay() {
        return this.cancelMaxAllowCountsOneDay;
    }

    public int getBuyPayTermTime() {
        return this.buyPayTermTime;
    }

    public int getSellMinCountOneTrade() {
        return this.sellMinCountOneTrade;
    }

    public int getPutCoinsTermTime() {
        return this.putCoinsTermTime;
    }

    @Deprecated
    public double getOtherAmount() {
        return this.otherAmount;
    }

    @Deprecated
    public String getOtherAmountStandard() {
        return MoneyUtil.formatToString(NumberUtil.replacesSientificE(this.otherAmount / 100.0d, 2), 2);
    }

    @Deprecated
    public String getOtherAmountWithoutSientificE() {
        return NumberUtil.replacesSientificE(this.otherAmount / 100.0d, 2);
    }

    public double getCashAmount() {
        return this.cashAmount;
    }

    public String getCashAmountStandard() {
        return MoneyUtil.formatToString(this.cashAmount / 100.0d, 2);
    }

    public double getFrozenCash() {
        return this.frozenCash;
    }

    public String getFrozenCashStandard() {
        return MoneyUtil.formatToString(this.frozenCash / 100.0d, 2);
    }

    public int getStatus() {
        return this.status;
    }

    public String getUserName() {
        return this.userName;
    }

    public double getFreezeOthers() {
        return this.freezeOthers;
    }

    public int getForzenTime() {
        int i = this.forzenTime;
        if (i == 0) {
            return 30;
        }
        return i;
    }

    public int getForzenPayPasswordInputWrongTimes() {
        int i = this.forzenPayPasswordInputWrongTimes;
        if (i == 0) {
            return 5;
        }
        return i;
    }

    public int getTradeLimitTime() {
        int i = this.tradeLimitTime;
        if (i == 0) {
            return 30;
        }
        return i;
    }

    public int getTradePayPasswordInputWrongTimes() {
        int i = this.tradePayPasswordInputWrongTimes;
        if (i == 0) {
            return 5;
        }
        return i;
    }

    public static void setWalletAccountInfo(WalletAccountInfo walletAccountInfo) {
        if (walletAccountInfo != null) {
            getInstance().basicsStatus = walletAccountInfo.getAuthNormalStatus();
            getInstance().seniorStatus = walletAccountInfo.getAuthVipStatus();
            getInstance().bindBankStatus = walletAccountInfo.getBindBankStatus();
            getInstance().isSetPayWord = walletAccountInfo.getIsSetPayWord();
            getInstance().type = walletAccountInfo.getUserRollType();
            getInstance().freezeOthers = walletAccountInfo.getFreezeOthers();
            getInstance().otherAmount = walletAccountInfo.getOtherAmount();
            getInstance().userName = walletAccountInfo.getUserName();
            getInstance().status = walletAccountInfo.getStatus();
            getInstance().cashAmount = walletAccountInfo.getCashAmount();
            getInstance().frozenCash = walletAccountInfo.getFrozenCash();
        }
    }

    public static void setConfigValue(List<Bean> list) {
        Double valueD;
        if (list != null) {
            for (Bean b : list) {
                if (b != null) {
                    try {
                        valueD = Double.valueOf(Double.parseDouble(b.ruleValue));
                    } catch (Exception e) {
                        valueD = Double.valueOf(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                    }
                    String access$100 = b.ruleCode;
                    char c = 65535;
                    switch (access$100.hashCode()) {
                        case -1917215915:
                            if (access$100.equals("red_once_max_money")) {
                                c = 12;
                                break;
                            }
                            break;
                        case -1139994348:
                            if (access$100.equals("basis_issue_time")) {
                                c = 8;
                                break;
                            }
                            break;
                        case -678809999:
                            if (access$100.equals("basis_trans_price")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -483319197:
                            if (access$100.equals("red_day_max_count")) {
                                c = 9;
                                break;
                            }
                            break;
                        case -474090988:
                            if (access$100.equals("red_day_max_money")) {
                                c = 10;
                                break;
                            }
                            break;
                        case -471024453:
                            if (access$100.equals("transfer_once_max_money")) {
                                c = 13;
                                break;
                            }
                            break;
                        case -110154214:
                            if (access$100.equals("buy_min_money")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 301060964:
                            if (access$100.equals("sell_day_max_count")) {
                                c = 6;
                                break;
                            }
                            break;
                        case 310289173:
                            if (access$100.equals("sell_day_max_money")) {
                                c = 7;
                                break;
                            }
                            break;
                        case 1362995017:
                            if (access$100.equals("buy_day_max_money")) {
                                c = 3;
                                break;
                            }
                            break;
                        case 1723723750:
                            if (access$100.equals("sell_min_money")) {
                                c = 5;
                                break;
                            }
                            break;
                        case 1810099102:
                            if (access$100.equals("cancel_day_num")) {
                                c = 11;
                                break;
                            }
                            break;
                        case 1899282680:
                            if (access$100.equals("basis_pay_rate")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 1899349733:
                            if (access$100.equals("basis_pay_time")) {
                                c = 4;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(0.01d);
                            }
                            getInstance().payRate = valueD.doubleValue();
                            break;
                        case 1:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(100.0d);
                            }
                            getInstance().transUnitPrice = valueD.doubleValue();
                            break;
                        case 2:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(50.0d);
                            }
                            getInstance().buyMinCountOneTrade = valueD.intValue();
                            break;
                        case 3:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(50000.0d);
                            }
                            getInstance().buyMaxMoneyOneDay = valueD.doubleValue();
                            break;
                        case 4:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(30.0d);
                            }
                            getInstance().buyPayTermTime = valueD.intValue();
                            break;
                        case 5:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(50.0d);
                            }
                            getInstance().sellMinCountOneTrade = valueD.intValue();
                            break;
                        case 6:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(50.0d);
                            }
                            getInstance().sellMaxCountOneDay = valueD.intValue();
                            break;
                        case 7:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(50000.0d);
                            }
                            getInstance().sellMaxMoneyOneDay = valueD.doubleValue();
                            break;
                        case 8:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(120.0d);
                            }
                            getInstance().putCoinsTermTime = valueD.intValue();
                            break;
                        case 9:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(50.0d);
                            }
                            getInstance().redPacketMaxCountOneDay = valueD.intValue();
                            break;
                        case 10:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(50000.0d);
                            }
                            getInstance().redPacketMaxMoneyOneDay = valueD.doubleValue();
                            break;
                        case 11:
                            if (valueD.doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                                valueD = Double.valueOf(5.0d);
                            }
                            getInstance().cancelMaxAllowCountsOneDay = valueD.intValue();
                            break;
                        case 12:
                            getInstance().redPacketMaxMoneySingleTime = (double) valueD.intValue();
                            break;
                        case 13:
                            getInstance().transferMaxMoneySingleTime = (double) valueD.intValue();
                            break;
                    }
                }
            }
        }
    }

    public static class Bean {
        /* access modifiers changed from: private */
        public String ruleCode;
        /* access modifiers changed from: private */
        public String ruleValue;

        public String getRuleCode() {
            return this.ruleCode;
        }

        public void setRuleCode(String ruleCode2) {
            this.ruleCode = ruleCode2;
        }

        public String getRuleValue() {
            return this.ruleValue;
        }

        public void setRuleValue(String ruleValue2) {
            this.ruleValue = ruleValue2;
        }
    }
}
