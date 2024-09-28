package im.bclpbkiauv.ui.wallet.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import im.bclpbkiauv.messenger.FileLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WalletPaymentBankCardBean implements Parcelable {
    public static final Parcelable.Creator<WalletPaymentBankCardBean> CREATOR = new Parcelable.Creator<WalletPaymentBankCardBean>() {
        public WalletPaymentBankCardBean createFromParcel(Parcel in) {
            return new WalletPaymentBankCardBean(in);
        }

        public WalletPaymentBankCardBean[] newArray(int size) {
            return new WalletPaymentBankCardBean[size];
        }
    };
    private String createTime;
    public int id;
    private String info;
    private JSONObject infoJson;
    private Map<String, Object> infoMap;
    public int supportId;
    public int templateId;

    public void setInfo(String info2) {
        this.info = info2;
    }

    public WalletPaymentBankCardBean() {
    }

    public static List<WalletPaymentBankCardBean> createByJson(JSONArray array) {
        List<WalletPaymentBankCardBean> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject on = array.getJSONObject(i);
            WalletPaymentBankCardBean b = new WalletPaymentBankCardBean();
            b.createTime = on.getString("createTime");
            b.id = on.getInteger(TtmlNode.ATTR_ID).intValue();
            b.supportId = on.getInteger("supportId").intValue();
            b.templateId = on.getInteger("templateId").intValue();
            b.info = on.getString("info");
            list.add(b);
        }
        return list;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime2) {
        this.createTime = createTime2;
    }

    public String getInfo() {
        String str = this.info;
        return str == null ? "" : str;
    }

    public JSONObject getInfoJson() {
        if (this.infoJson == null && !TextUtils.isEmpty(getInfo())) {
            try {
                this.infoJson = JSON.parseObject(getInfo());
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        JSONObject jSONObject = this.infoJson;
        return jSONObject == null ? new JSONObject() : jSONObject;
    }

    public Map<String, Object> getInfoMap() {
        if (this.infoMap == null && !TextUtils.isEmpty(getInfo())) {
            try {
                this.infoMap = (Map) JSON.parseObject(getInfo(), LinkedHashMap.class);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        Map<String, Object> map = this.infoMap;
        return map == null ? new HashMap() : map;
    }

    public Object getCardNumber() {
        Object va;
        Iterator<Map.Entry<String, Object>> it = getInfoMap().entrySet().iterator();
        if (!it.hasNext() || (va = it.next().getValue()) == null) {
            return "";
        }
        return va;
    }

    protected WalletPaymentBankCardBean(Parcel in) {
        this.createTime = in.readString();
        this.id = in.readInt();
        this.supportId = in.readInt();
        this.templateId = in.readInt();
        this.info = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createTime);
        dest.writeInt(this.id);
        dest.writeInt(this.supportId);
        dest.writeInt(this.templateId);
        dest.writeString(this.info);
    }
}
