package im.bclpbkiauv.tgnet;

import com.blankj.utilcode.util.GsonUtils;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.utils.apache.StringEscapeUtils;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class TLJsonResolve {
    public static String TAG = TLJsonResolve.class.getName();

    public static String getData(TLObject response) {
        if (response == null) {
            return null;
        }
        if (response instanceof TLRPC.TL_dataJSON) {
            return ((TLRPC.TL_dataJSON) response).data;
        }
        Field[] fields = response.getClass().getDeclaredFields();
        int length = fields.length;
        int i = 0;
        while (i < length) {
            Field f = fields[i];
            if (TLRPC.TL_dataJSON.class.getName().equals(f.getType().getName())) {
                f.setAccessible(true);
                try {
                    Object o = f.get(response);
                    if (o != null) {
                        return ((TLRPC.TL_dataJSON) o).data;
                    }
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                i++;
            }
        }
        return null;
    }

    public static <T> TLApiModel<T> parse(String jsonData, Class<?> clz) {
        TLRPC.TL_dataJSON dataJSON = new TLRPC.TL_dataJSON();
        dataJSON.data = jsonData;
        return parse((TLObject) dataJSON, clz);
    }

    public static <T> TLApiModel<T> parse(TLObject response, Class<?> clz) {
        TLApiModel model = null;
        String errorMsg = null;
        try {
            model = (TLApiModel) GsonUtils.fromJson(getData(response), TLApiModel.class);
            if (model.data != null) {
                model.data = StringEscapeUtils.unescapeJava(model.data);
                if (model.data != null) {
                    try {
                        model.data = model.data.replaceAll("\"\\{", "{");
                    } catch (Exception e) {
                    }
                    try {
                        model.data = model.data.replaceAll("}\"", "}");
                    } catch (Exception e2) {
                    }
                    try {
                        model.data = model.data.replaceAll("\"\\[", "[");
                    } catch (Exception e3) {
                    }
                    try {
                        model.data = model.data.replaceAll("]\"", "]");
                    } catch (Exception e4) {
                    }
                    if (model.data.startsWith("{")) {
                        model.model = GsonUtils.fromJson(model.data, clz);
                    } else if (model.data.startsWith("[")) {
                        model.modelList = (List) GsonUtils.fromJson(model.data, (Type) new ParameterizedTypeImpl(clz));
                    }
                }
            }
        } catch (Exception e5) {
            errorMsg = e5.getMessage();
            FileLog.e(TAG + " =====> " + e5.getMessage());
        }
        if (model == null) {
            model = new TLApiModel();
            model.code = "-9999";
            if (errorMsg != null) {
                model.message = errorMsg;
            } else {
                model.message = LocaleController.getString("TLJsonResolveParseFaild", R.string.TLJsonResolveParseFaild);
            }
        }
        return model;
    }

    public static <T> TLApiModel<T> parse3(TLObject response, Class<?> clz) {
        TLApiModel model = null;
        String errorMsg = null;
        try {
            model = (TLApiModel) GsonUtils.fromJson(getData(response), TLApiModel.class);
            if (!(model.data == null || model.data == null)) {
                if (model.data.startsWith("{")) {
                    model.model = GsonUtils.fromJson(model.data, clz);
                } else if (model.data.startsWith("[")) {
                    model.modelList = (List) GsonUtils.fromJson(model.data, (Type) new ParameterizedTypeImpl(clz));
                }
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            FileLog.e(TAG + " =====> " + e.getMessage());
        }
        if (model == null) {
            model = new TLApiModel();
            model.code = "-9999";
            if (errorMsg != null) {
                model.message = errorMsg;
            } else {
                model.message = LocaleController.getString("TLJsonResolveParseFaild", R.string.TLJsonResolveParseFaild);
            }
        }
        return model;
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            this.clazz = clz;
        }

        public Type[] getActualTypeArguments() {
            return new Type[]{this.clazz};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    }
}
