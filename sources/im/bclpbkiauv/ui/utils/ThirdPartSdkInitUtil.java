package im.bclpbkiauv.ui.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.Process;
import com.aliyun.security.yunceng.android.sdk.YunCeng;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.bjz.comm.net.factory.SSLSocketClient;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.constants.Constants;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.config.VideoPlayerConfig;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.factory.ExoPlayerFactory;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class ThirdPartSdkInitUtil implements Constants {
    private static final String TAG = "ThirdPartSdkInit";
    private static volatile boolean sdkIsInit;

    public static void initOtherSdk(Context applicationContext) {
        if (applicationContext != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("SDKINIT  ===> ThirdPartSdkInitUtil initOtherSdk ===> start , sdkIsInit = " + sdkIsInit);
            }
            if (!sdkIsInit) {
                sdkIsInit = true;
                SmartRefreshLayout.setDefaultRefreshHeaderCreator($$Lambda$ThirdPartSdkInitUtil$YySnwPfh111coomuGvLAWOMTQDM.INSTANCE);
                SmartRefreshLayout.setDefaultRefreshFooterCreator($$Lambda$ThirdPartSdkInitUtil$MU3L29cqMr8vJEAULp46iz2seKI.INSTANCE);
                OkHttpUtils.initClient(new OkHttpClient.Builder().addInterceptor(new LoggerInterceptor("fcokhttp", true)).connectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS).readTimeout(20000, TimeUnit.MILLISECONDS).sslSocketFactory(SSLSocketClient.getSSLSocketFactory()).hostnameVerifier(SSLSocketClient.getHostnameVerifier()).build());
                SDKInitializer.initialize(applicationContext);
                SDKInitializer.setCoordType(CoordType.GCJ02);
                isMainProcess(applicationContext);
                VideoPlayerManager.loadConfig(new VideoPlayerConfig.Builder(applicationContext).buildPlayerFactory(new ExoPlayerFactory(applicationContext)).enableSmallWindowPlay().enableCache(true).enableLog(true).build());
                try {
                    HttpResponseCache.install(new File(applicationContext.getCacheDir(), "http"), 268435456);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("SDKINIT  ===> ThirdPartSdkInitUtil initOtherSdk ===> end , sdkIsInit = " + sdkIsInit);
                }
            }
        }
    }

    private static boolean isMainProcess(Context applicationContext) {
        int pid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo appProcess : ((ActivityManager) applicationContext.getSystemService("activity")).getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return applicationContext.getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    private static void initYunCeng(Context applicationContext) {
        int ret = YunCeng.initEx(applicationContext.getResources().getString(R.string.yuncheng_app_key), "Default");
        if (ret != 0) {
            FileLog.e("ApplicationLoader ---> initYunCeng YunCeng sdk init failed " + ret);
            return;
        }
        FileLog.d("ApplicationLoader ---> initYunCeng YunCeng sdk init success");
    }
}
