package im.bclpbkiauv.tgnet;

import com.google.gson.Gson;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.constants.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class ParamsUtil implements Constants {
    public static String toUserIdJson(String[] keyArray, Object... valueArray) {
        if (keyArray == null || valueArray == null || keyArray.length != valueArray.length) {
            return "";
        }
        List<String> keyList = new ArrayList<>(Arrays.asList(keyArray));
        List<Object> valueList = new ArrayList<>(Arrays.asList(valueArray));
        keyList.add("userId");
        valueList.add(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()));
        keyList.add("version");
        valueList.add("0.0.1");
        return toJson((String[]) keyList.toArray(), valueList.toArray());
    }

    public static String toUserIdJson(String businessKey, String[] keyArray, Object... valueArray) {
        if (keyArray == null || valueArray == null || keyArray.length != valueArray.length) {
            return "";
        }
        List<String> keyList = new ArrayList<>(Arrays.asList(keyArray));
        List<Object> valueList = new ArrayList<>(Arrays.asList(valueArray));
        keyList.add("userId");
        valueList.add(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()));
        keyList.add("version");
        valueList.add("0.0.1");
        if (businessKey != null) {
            keyList.add("businessKey");
            valueList.add(businessKey);
        }
        return toJson((String[]) keyList.toArray(new String[0]), valueList.toArray());
    }

    public static String toJson(String[] keyArray, Object... valueArray) {
        if (keyArray == null || valueArray == null || keyArray.length != valueArray.length) {
            return "";
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyArray.length; i++) {
            if (!(keyArray[i] == null || valueArray[i] == null)) {
                map.put(keyArray[i], valueArray[i]);
            }
        }
        return toJson(map);
    }

    public static String toJson(Map<String, Object> map) {
        if (map != null) {
            return new JSONObject(map).toString();
        }
        return "";
    }

    public static com.alibaba.fastjson.JSONObject toJsonObj(String[] arg4, Object... arg5) {
        if (arg4 == null || arg5 == null || arg4.length != arg5.length) {
            return null;
        }
        com.alibaba.fastjson.JSONObject v0 = new com.alibaba.fastjson.JSONObject();
        for (int v1 = 0; v1 < arg4.length; v1++) {
            if (!(arg4[v1] == null || arg5[v1] == null)) {
                v0.put(arg4[v1], arg5[v1]);
            }
        }
        return v0;
    }

    public static String toJson(Object classBean) {
        return new Gson().toJson(classBean);
    }
}
