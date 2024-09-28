package im.bclpbkiauv.ui.wallet.model;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONArray;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WalletWithdrawTemplateBean implements Comparable<WalletWithdrawTemplateBean> {
    private static final String TAG = "WalletWithdrawTemplateBean";
    public static final String TYPE_INPUT = "input";
    public static final String TYPE_PICTURE = "img";
    public static final String TYPE_SELECT = "select";
    public static final String TYPE_TEXTARE = "textare";
    private String count;
    private List<DictItemBean> dictList;
    private boolean dictListDataIsRecreated;
    private String dictType;
    private String displayName;
    private String enable;
    private String explan;
    private int id;
    private String orderNum;
    private String paramName;
    private PictureBean[] pictureArray;
    private DictItemBean selectDictItem;
    private int selectIndex = -1;
    private String templateId;
    private String textInput;
    private String type;

    public static List<WalletWithdrawTemplateBean> recreateData(List<WalletWithdrawTemplateBean> data, WalletPaymentBankCardBean orginCardData) {
        Object orginValue;
        List<WalletWithdrawTemplateBean> newData = new ArrayList<>();
        if (data != null) {
            for (WalletWithdrawTemplateBean b : data) {
                if (b != null && b.isEnable()) {
                    if (orginCardData != null && (orginValue = orginCardData.getInfoMap().get(b.getParamName())) != null) {
                        if (b.isTypeInputText()) {
                            b.setTextInput(orginValue.toString());
                        } else if (b.isTypeSelect()) {
                            int i = 0;
                            while (true) {
                                if (i >= b.getDictList().size()) {
                                    break;
                                } else if (b.getDictList().get(i).getDictValue().equals(orginValue.toString())) {
                                    b.setSelectIndex(i);
                                    break;
                                } else {
                                    i++;
                                }
                            }
                        } else if (b.isTypePicture()) {
                            try {
                                if ((orginValue instanceof JSONArray) && ((JSONArray) orginValue).size() == b.getPictureCount()) {
                                    b.pictureArray = new PictureBean[((JSONArray) orginValue).size()];
                                    for (int i2 = 0; i2 < ((JSONArray) orginValue).size(); i2++) {
                                        Object oj = ((JSONArray) orginValue).get(i2);
                                        if (oj != null) {
                                            b.setPictureOriginUrl(i2, oj.toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e(TAG, "recreat error when picture", e);
                            }
                        }
                    }
                    newData.add(b);
                }
            }
        }
        Collections.sort(newData);
        return newData;
    }

    public static String createInfoJson(List<WalletWithdrawTemplateBean> templateData) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0; i < templateData.size(); i++) {
            WalletWithdrawTemplateBean t = templateData.get(i);
            if (t != null) {
                builder.append("\"");
                builder.append(t.getParamName());
                builder.append("\":");
                if (t.isTypeInputText()) {
                    builder.append("\"");
                    builder.append(t.getTextInput());
                    builder.append("\"");
                } else if (t.isTypeSelect()) {
                    DictItemBean di = t.getSelectDictItem();
                    if (di != null) {
                        builder.append("\"");
                        builder.append(di.getDictValue());
                        builder.append("\"");
                    }
                } else if (t.isTypePicture()) {
                    String[] arr = t.getPictureUrlArray();
                    if (arr.length == t.getPictureCount()) {
                        builder.append("[");
                        for (int j = 0; j < arr.length; j++) {
                            builder.append("\"");
                            builder.append(arr[j]);
                            builder.append("\"");
                            if (j != arr.length - 1) {
                                builder.append(",");
                            }
                        }
                        builder.append("]");
                    }
                }
                if (i != templateData.size() - 1) {
                    builder.append(",");
                }
            }
        }
        builder.append("}");
        return builder.toString();
    }

    public boolean isTypeInputText() {
        return TYPE_INPUT.equals(this.type) || TYPE_TEXTARE.equals(this.type);
    }

    public boolean isTypeSelect() {
        return TYPE_SELECT.equals(this.type);
    }

    public boolean isTypePicture() {
        return TYPE_PICTURE.equals(this.type);
    }

    public String getDisplayName() {
        String str = this.displayName;
        return str == null ? "" : str;
    }

    public String getExplan() {
        if (TextUtils.isEmpty(this.explan)) {
            if (isTypeSelect()) {
                this.explan = "please select";
            } else if (isTypePicture()) {
                this.explan = "please upload picture";
            } else {
                this.explan = "please enter";
            }
        }
        return this.explan;
    }

    public String getParamName() {
        String str = this.paramName;
        return str == null ? "" : str;
    }

    public int getTemplateId() {
        if (NumberUtil.isNumber(this.templateId)) {
            return Integer.parseInt(this.templateId);
        }
        return 0;
    }

    public String getOrderNum() {
        String str = this.orderNum;
        return str == null ? "" : str;
    }

    public List<DictItemBean> getDictList() {
        if (this.dictList != null && !this.dictListDataIsRecreated) {
            List<DictItemBean> co = new ArrayList<>(this.dictList);
            this.dictList.clear();
            for (DictItemBean b : co) {
                if (b != null && b.isEnable()) {
                    this.dictList.add(b);
                }
            }
            this.dictListDataIsRecreated = true;
        }
        List<DictItemBean> co2 = this.dictList;
        return co2 == null ? new ArrayList() : co2;
    }

    public boolean isEnable() {
        return "1".endsWith(this.enable);
    }

    public void setTextInput(String textInput2) {
        if (isTypeInputText()) {
            this.textInput = textInput2;
        }
    }

    public String getTextInput() {
        return this.textInput;
    }

    public DictItemBean getSelectDictItem() {
        int i;
        if (this.selectDictItem == null && (i = this.selectIndex) >= 0 && i < getDictList().size()) {
            this.selectDictItem = getDictList().get(this.selectIndex);
        }
        return this.selectDictItem;
    }

    public void setSelectIndex(int selectIndex2) {
        if (isTypeSelect()) {
            this.selectIndex = selectIndex2;
        }
    }

    public int getPictureCount() {
        if (!isTypePicture() || !NumberUtil.isNumber(this.count)) {
            return 0;
        }
        return Integer.parseInt(this.count);
    }

    public PictureBean[] getPictureArray() {
        return this.pictureArray;
    }

    public PictureBean getPictureBeanIndex(int index) {
        PictureBean[] pictureBeanArr = this.pictureArray;
        if (pictureBeanArr == null || index < 0 || index >= pictureBeanArr.length) {
            return null;
        }
        return pictureBeanArr[index];
    }

    public boolean checkNeedToUploadPictureByIndex(int index, boolean isAddNew) {
        PictureBean pb = getPictureBeanIndex(index);
        if (pb != null) {
            return pb.checkNeedToUploadPictureByIndex(isAddNew);
        }
        return false;
    }

    public void setPicturePath(int index, String path) {
        if (isTypePicture()) {
            if (this.pictureArray == null) {
                this.pictureArray = new PictureBean[getPictureCount()];
            }
            PictureBean pb = getPictureBeanIndex(index);
            if (pb == null) {
                pb = new PictureBean();
                this.pictureArray[index] = pb;
            }
            pb.setPath(path);
        }
    }

    public void setPictureUrl(int index, String url) {
        if (isTypePicture()) {
            if (this.pictureArray == null) {
                this.pictureArray = new PictureBean[getPictureCount()];
            }
            PictureBean pb = getPictureBeanIndex(index);
            if (pb == null) {
                pb = new PictureBean();
                this.pictureArray[index] = pb;
            }
            pb.setUrl(url);
        }
    }

    private void setPictureOriginUrl(int index, String originUrl) {
        if (isTypePicture()) {
            if (this.pictureArray == null) {
                this.pictureArray = new PictureBean[getPictureCount()];
            }
            PictureBean pb = getPictureBeanIndex(index);
            if (pb == null) {
                pb = new PictureBean();
                this.pictureArray[index] = pb;
            }
            pb.setOriginUrl(originUrl);
        }
    }

    public String getPicturePathByIndex(int index) {
        PictureBean pb;
        if (!isTypePicture() || (pb = getPictureBeanIndex(index)) == null) {
            return "";
        }
        return pb.getPath();
    }

    public boolean hasPicturePathInIndex(int index) {
        return !TextUtils.isEmpty(getPicturePathByIndex(index));
    }

    public String getPictureUrlByIndex(int index) {
        PictureBean pb;
        if (!isTypePicture() || (pb = getPictureBeanIndex(index)) == null) {
            return "";
        }
        return pb.getUrl();
    }

    public boolean hasPictureDataInIndex(int index, boolean isAddNewAccount) {
        PictureBean pb = getPictureBeanIndex(index);
        if (pb == null) {
            return false;
        }
        if (isAddNewAccount) {
            return !TextUtils.isEmpty(pb.getPath());
        }
        if (!TextUtils.isEmpty(pb.getPath()) || (!TextUtils.isEmpty(pb.getOriginUrl()) && pb.getOriginUrl().startsWith("http"))) {
            return true;
        }
        return false;
    }

    public String[] getPictureUrlArray() {
        String[] urlArray = new String[getPictureCount()];
        if (this.pictureArray != null) {
            int i = 0;
            while (true) {
                PictureBean[] pictureBeanArr = this.pictureArray;
                if (i >= pictureBeanArr.length) {
                    break;
                }
                PictureBean pb = pictureBeanArr[i];
                if (pb != null) {
                    if (!TextUtils.isEmpty(pb.getUrl())) {
                        urlArray[i] = pb.getUrl();
                    } else {
                        urlArray[i] = pb.getOriginUrl();
                    }
                }
                i++;
            }
        }
        return urlArray;
    }

    public int compareTo(WalletWithdrawTemplateBean o) {
        if (o != null) {
            return getOrderNum().compareTo(o.getOrderNum());
        }
        return 0;
    }

    public static class DictItemBean implements Comparable<DictItemBean> {
        private String dictLabel;
        public String dictSort;
        private String dictType;
        private String dictValue;
        public int isDefualt;
        private int status;

        public String getDictLabel() {
            String str = this.dictLabel;
            return str == null ? "" : str;
        }

        public String getDictValue() {
            String str = this.dictValue;
            return str == null ? "" : str;
        }

        public String getDictType() {
            String str = this.dictType;
            return str == null ? "" : str;
        }

        public String getDictSort() {
            String str = this.dictSort;
            return str == null ? "" : str;
        }

        public boolean isEnable() {
            return this.status == 0;
        }

        public int compareTo(DictItemBean o) {
            if (o != null) {
                return getDictSort().compareTo(o.getDictSort());
            }
            return 0;
        }
    }

    public static class PictureBean {
        private String originUrl;
        private String path;
        private String url;

        public void setPath(String path2) {
            this.path = path2;
        }

        public String getPath() {
            String str = this.path;
            return str == null ? "" : str;
        }

        public void setUrl(String url2) {
            this.url = url2;
        }

        public String getUrl() {
            String str = this.url;
            return str == null ? "" : str;
        }

        public void setOriginUrl(String originUrl2) {
            this.originUrl = originUrl2;
        }

        public String getOriginUrl() {
            String str = this.originUrl;
            return str == null ? "" : str;
        }

        public boolean checkNeedToUploadPictureByIndex(boolean isAddNew) {
            if (isAddNew) {
                return TextUtils.isEmpty(getUrl());
            }
            return TextUtils.isEmpty(getUrl()) && !TextUtils.isEmpty(getPath());
        }
    }
}
