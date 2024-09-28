package com.bjz.comm.net.utils;

import android.text.TextUtils;
import android.webkit.URLUtil;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.UrlConstant;

public class HttpUtils {
    private static String remoteGameUrl = "";
    private static String remoteMPUrl = "";
    private static String remoteUrl = "";
    private static String requestToken = "";

    public static HttpUtils getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        /* access modifiers changed from: private */
        public static final HttpUtils INSTANCE = new HttpUtils();

        private Holder() {
        }
    }

    public String getUserAgentMP() {
        return !BuildVars.DEBUG_VERSION ? UrlConstant.USER_AGENT_MINI_PROGRAM : UrlConstant.USER_AGENT_GRAY_TEST;
    }

    public String getUserAgentFC() {
        return !BuildVars.DEBUG_VERSION ? UrlConstant.USER_AGENT_FC : UrlConstant.USER_AGENT_GRAY_TEST;
    }

    public String getUserAgentGame() {
        return !BuildVars.DEBUG_VERSION ? UrlConstant.USER_AGENT_GAME : UrlConstant.USER_AGENT_GRAY_TEST;
    }

    public String getAuthorization() {
        if (TextUtils.isEmpty(requestToken)) {
            requestToken = AppPreferenceUtil.getString("authorization", "");
        }
        return requestToken;
    }

    public void setAuthorization(String authorization) {
        AppPreferenceUtil.putString("authorization", authorization);
        requestToken = authorization;
    }

    public void clearCache() {
        setAuthorization("");
    }

    public String getFcBaseUrl() {
        return BuildVars.DEBUG_VERSION ? UrlConstant.BASE_FC_LOCAL_TEST_URL : getFcRemoteUrl();
    }

    public String getFcBaseVideoUrl() {
        return BuildVars.DEBUG_VERSION ? UrlConstant.BASE_FC_LOCAL_TEST_URL : "https://impyq.gz.bcebos.com/";
    }

    public String getBaseFileUrl() {
        return getFcBaseUrl() + UrlConstant.PICTURE_FILE_URI_PATH;
    }

    private String getFcRemoteUrl() {
        if (TextUtils.isEmpty(remoteUrl)) {
            String remoteUrl2 = AppPreferenceUtil.getString("LocalFcUrl", "");
            if (!TextUtils.isEmpty(remoteUrl2) && !URLUtil.isNetworkUrl(remoteUrl2)) {
                remoteUrl2 = "https://" + remoteUrl2;
                setFcRemoteUrl(remoteUrl2);
            }
            if (!TextUtils.isEmpty(remoteUrl2)) {
                remoteUrl = remoteUrl2;
            } else {
                remoteUrl = UrlConstant.BASE_FC_URL;
            }
        }
        return remoteUrl;
    }

    public void setFcRemoteUrl(String remoteUrl2) {
        AppPreferenceUtil.putString("LocalFcUrl", remoteUrl2);
        remoteUrl = remoteUrl2;
    }

    public String getMPBaseUrl() {
        return BuildVars.DEBUG_VERSION ? UrlConstant.BASE_MINI_PROGRAM_TEST_URL : getMPRemoteUrl();
    }

    private String getMPRemoteUrl() {
        if (TextUtils.isEmpty(remoteMPUrl)) {
            String localMPUrl = AppPreferenceUtil.getString("LocalMPUrl", "");
            if (!TextUtils.isEmpty(localMPUrl) && !URLUtil.isNetworkUrl(localMPUrl)) {
                localMPUrl = "https://" + localMPUrl;
                setFcRemoteUrl(localMPUrl);
            }
            if (!TextUtils.isEmpty(localMPUrl)) {
                remoteMPUrl = localMPUrl;
            } else {
                remoteMPUrl = UrlConstant.BASE_MINI_PROGRAM_URL;
            }
        }
        return remoteMPUrl;
    }

    public void setMPRemoteUrl(String remoteMPUrl2) {
        AppPreferenceUtil.putString("LocalMPUrl", remoteMPUrl2);
        remoteMPUrl = remoteMPUrl2;
    }

    public String getGameBaseUrl() {
        return BuildVars.DEBUG_VERSION ? UrlConstant.BASE_MINI_GAME_TEST_URL : getGameRemoteUrl();
    }

    public String getDownloadFileUrl() {
        return "https://impyq.gz.bcebos.com/";
    }

    public String getDownloadVideoFileUrl() {
        return "https://impyq.gz.bcebos.com/";
    }

    private String getGameRemoteUrl() {
        if (TextUtils.isEmpty(remoteGameUrl)) {
            String localMPUrl = AppPreferenceUtil.getString("LocalGameUrl", "");
            if (!TextUtils.isEmpty(localMPUrl) && !URLUtil.isNetworkUrl(localMPUrl)) {
                localMPUrl = "https://" + localMPUrl;
                setFcRemoteUrl(localMPUrl);
            }
            if (!TextUtils.isEmpty(localMPUrl)) {
                remoteGameUrl = localMPUrl;
            } else {
                remoteGameUrl = UrlConstant.BASE_MINI_GAME_URL;
            }
        }
        return remoteGameUrl;
    }

    public void setGameRemoteUrl(String remoteMPUrl2) {
        AppPreferenceUtil.putString("LocalGameUrl", remoteMPUrl2);
        remoteGameUrl = remoteMPUrl2;
    }

    public String getHuanHuiHostUrl() {
        return UrlConstant.URL_HUAN_HUI_HOST;
    }
}
