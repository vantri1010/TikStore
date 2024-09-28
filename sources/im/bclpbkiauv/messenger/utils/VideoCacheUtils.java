package im.bclpbkiauv.messenger.utils;

import android.content.Context;
import com.bjz.comm.net.utils.HttpUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import java.util.HashMap;
import java.util.Map;

public class VideoCacheUtils {
    private static HttpProxyCacheServer proxy = null;

    public static String getProxyUrl(Context context, String strOrgUrl) {
        if (proxy == null) {
            proxy = new HttpProxyCacheServer.Builder(context.getApplicationContext()).headerInjector($$Lambda$VideoCacheUtils$aLTVROpjPYAmtm8pe6z9oWUMU8.INSTANCE).build();
        }
        return proxy.getProxyUrl(strOrgUrl);
    }

    static /* synthetic */ Map lambda$getProxyUrl$0(String url) {
        Map<String, String> map = new HashMap<>();
        map.put("user-agent", HttpUtils.getInstance().getUserAgentFC());
        return map;
    }
}
