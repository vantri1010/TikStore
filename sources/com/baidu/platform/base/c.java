package com.baidu.platform.base;

import com.baidu.mapapi.search.core.SearchResult;

class c implements Runnable {
    final /* synthetic */ d a;
    final /* synthetic */ SearchResult b;
    final /* synthetic */ Object c;
    final /* synthetic */ a d;

    c(a aVar, d dVar, SearchResult searchResult, Object obj) {
        this.d = aVar;
        this.a = dVar;
        this.b = searchResult;
        this.c = obj;
    }

    public void run() {
        if (this.a != null) {
            this.d.a.lock();
            try {
                this.a.a(this.b, this.c);
            } finally {
                this.d.a.unlock();
            }
        }
    }
}
