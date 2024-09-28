package im.bclpbkiauv.ui.hui.friendscircle.okhttphelper;

import com.bjz.comm.net.UrlConstant;
import com.bjz.comm.net.utils.HttpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.UserConfig;
import java.io.IOException;
import okhttp3.Response;

public class LiveOkHttpUtils {
    public static String REMOTE_URL = "";

    public static Response doGetSyn(String url) {
        try {
            return ((GetBuilder) ((GetBuilder) ((GetBuilder) ((GetBuilder) OkHttpUtils.get().addHeader("User-Agent", UrlConstant.USER_AGENT_LIVE)).addHeader("authorization", getTokenFromLocal())).addHeader("user-id", String.valueOf(AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id))).url(url)).build().execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getTokenFromLocal() {
        return HttpUtils.getInstance().getAuthorization();
    }
}
