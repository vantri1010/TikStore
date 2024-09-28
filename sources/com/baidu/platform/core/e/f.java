package com.baidu.platform.core.e;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.platform.base.d;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class f extends d {
    public SearchResult a(String str) {
        SearchResult.ERRORNO errorno;
        ShareUrlResult shareUrlResult = new ShareUrlResult();
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        shareUrlResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return shareUrlResult;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        shareUrlResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return shareUrlResult;
                    }
                }
                if (str != null) {
                    try {
                        JSONObject jSONObject2 = new JSONObject(str);
                        if (!jSONObject2.optString(RemoteConfigConstants.ResponseFieldKey.STATE).equals("success")) {
                            errorno = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                        } else {
                            shareUrlResult.setUrl(jSONObject2.optString(ImagesContract.URL));
                            shareUrlResult.setType(a().ordinal());
                            errorno = SearchResult.ERRORNO.NO_ERROR;
                        }
                        shareUrlResult.error = errorno;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return shareUrlResult;
                }
                shareUrlResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                return shareUrlResult;
            } catch (Exception e2) {
            }
        }
        shareUrlResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        return shareUrlResult;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetShareUrlResultListener)) {
            OnGetShareUrlResultListener onGetShareUrlResultListener = (OnGetShareUrlResultListener) obj;
            int i = g.a[a().ordinal()];
            if (i == 1) {
                onGetShareUrlResultListener.onGetPoiDetailShareUrlResult((ShareUrlResult) searchResult);
            } else if (i == 2) {
                onGetShareUrlResultListener.onGetLocationShareUrlResult((ShareUrlResult) searchResult);
            }
        }
    }
}
