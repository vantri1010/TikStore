package im.bclpbkiauv.ui.wallet.model;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import im.bclpbkiauv.messenger.FileLog;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class BankCardListResBean implements Serializable {
    private String createTime;
    private int id;
    private String info;
    private Map<String, Object> infoMap;
    private String reactType;
    private int supportId;
    private int templateId;
    private int userId;

    public String getReactType() {
        return this.reactType;
    }

    public void setReactType(String reactType2) {
        this.reactType = reactType2;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime2) {
        this.createTime = createTime2;
    }

    public int getSupportId() {
        return this.supportId;
    }

    public void setSupportId(int supportId2) {
        this.supportId = supportId2;
    }

    public int getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(int templateId2) {
        this.templateId = templateId2;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId2) {
        this.userId = userId2;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info2) {
        this.info = info2;
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

    public String getShortCardNumber() {
        String card = getCardNumber() + "";
        if (TextUtils.isEmpty(card)) {
            return "";
        }
        if (card.length() > 4) {
            return card.substring(card.length() - 4);
        }
        return card;
    }
}
