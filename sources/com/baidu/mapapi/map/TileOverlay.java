package com.baidu.mapapi.map;

import android.util.Log;
import com.baidu.mapapi.common.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public final class TileOverlay {
    /* access modifiers changed from: private */
    public static final String b = TileOverlay.class.getSimpleName();
    private static int f = 0;
    BaiduMap a;
    private ExecutorService c = Executors.newFixedThreadPool(1);
    private HashMap<String, Tile> d = new HashMap<>();
    /* access modifiers changed from: private */
    public HashSet<String> e = new HashSet<>();
    /* access modifiers changed from: private */
    public TileProvider g;

    public TileOverlay(BaiduMap baiduMap, TileProvider tileProvider) {
        this.a = baiduMap;
        this.g = tileProvider;
    }

    private synchronized Tile a(String str) {
        if (!this.d.containsKey(str)) {
            return null;
        }
        Tile tile = this.d.get(str);
        this.d.remove(str);
        return tile;
    }

    /* access modifiers changed from: private */
    public synchronized void a(String str, Tile tile) {
        this.d.put(str, tile);
    }

    private synchronized boolean b(String str) {
        return this.e.contains(str);
    }

    private synchronized void c(String str) {
        this.e.add(str);
    }

    /* access modifiers changed from: package-private */
    public Tile a(int i, int i2, int i3) {
        String str;
        String str2;
        String str3 = i + "_" + i2 + "_" + i3;
        Tile a2 = a(str3);
        if (a2 != null) {
            return a2;
        }
        BaiduMap baiduMap = this.a;
        if (baiduMap != null && f == 0) {
            MapStatus mapStatus = baiduMap.getMapStatus();
            f = (((mapStatus.a.j.right - mapStatus.a.j.left) / 256) + 2) * (((mapStatus.a.j.bottom - mapStatus.a.j.top) / 256) + 2);
        }
        if (this.d.size() > f) {
            a();
        }
        if (b(str3) || this.c.isShutdown()) {
            return null;
        }
        try {
            c(str3);
            this.c.execute(new y(this, i, i2, i3, str3));
            return null;
        } catch (RejectedExecutionException e2) {
            str2 = b;
            str = "ThreadPool excepiton";
        } catch (Exception e3) {
            str2 = b;
            str = "fileDir is not legal";
        }
        Log.e(str2, str);
        return null;
    }

    /* access modifiers changed from: package-private */
    public synchronized void a() {
        Logger.logE(b, "clearTaskSet");
        this.e.clear();
        this.d.clear();
    }

    /* access modifiers changed from: package-private */
    public void b() {
        this.c.shutdownNow();
    }

    public boolean clearTileCache() {
        BaiduMap baiduMap = this.a;
        if (baiduMap == null) {
            return false;
        }
        return baiduMap.b();
    }

    public void removeTileOverlay() {
        BaiduMap baiduMap = this.a;
        if (baiduMap != null) {
            baiduMap.a(this);
        }
    }
}
