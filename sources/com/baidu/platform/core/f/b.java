package com.baidu.platform.core.f;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.a;
import com.baidu.platform.base.d;
import com.baidu.platform.base.e;

public class b extends a implements a {
    private OnGetSuggestionResultListener b = null;

    public void a() {
        this.a.lock();
        this.b = null;
        this.a.unlock();
    }

    public void a(OnGetSuggestionResultListener onGetSuggestionResultListener) {
        this.a.lock();
        this.b = onGetSuggestionResultListener;
        this.a.unlock();
    }

    public boolean a(SuggestionSearchOption suggestionSearchOption) {
        c cVar = new c();
        cVar.a(SearchType.SUGGESTION_SEARCH_TYPE);
        return a((e) new d(suggestionSearchOption), (Object) this.b, (d) cVar);
    }
}
