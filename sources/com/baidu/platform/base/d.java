package com.baidu.platform.base;

import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.search.core.SearchResult;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class d {
    protected SearchType a;

    public abstract SearchResult a(String str);

    public SearchType a() {
        return this.a;
    }

    public abstract void a(SearchResult searchResult, Object obj);

    public void a(SearchType searchType) {
        this.a = searchType;
    }

    /* access modifiers changed from: protected */
    public boolean a(String str, SearchResult searchResult, boolean z) {
        SearchResult.ERRORNO errorno;
        if (str != null) {
            try {
                if (str.length() > 0) {
                    int optInt = new JSONObject(str).optInt(z ? NotificationCompat.CATEGORY_STATUS : "status_sp");
                    if (optInt == 0) {
                        return false;
                    }
                    if (optInt != 200 && optInt != 230) {
                        switch (optInt) {
                            case 104:
                            case 105:
                            case 106:
                            case 107:
                            case 108:
                                errorno = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                                break;
                            default:
                                errorno = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                                break;
                        }
                    } else {
                        errorno = SearchResult.ERRORNO.KEY_ERROR;
                    }
                    searchResult.error = errorno;
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                searchResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                return true;
            }
        }
        searchResult.error = SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
        return true;
    }
}
