package com.baidu.mapapi.map;

import android.util.Log;

class y implements Runnable {
    final /* synthetic */ int a;
    final /* synthetic */ int b;
    final /* synthetic */ int c;
    final /* synthetic */ String d;
    final /* synthetic */ TileOverlay e;

    y(TileOverlay tileOverlay, int i, int i2, int i3, String str) {
        this.e = tileOverlay;
        this.a = i;
        this.b = i2;
        this.c = i3;
        this.d = str;
    }

    public void run() {
        String str;
        String str2;
        Tile tile = ((FileTileProvider) this.e.g).getTile(this.a, this.b, this.c);
        if (tile == null) {
            str2 = TileOverlay.b;
            str = "FileTile pic is null";
        } else if (tile.width == 256 && tile.height == 256) {
            this.e.a(this.a + "_" + this.b + "_" + this.c, tile);
            this.e.e.remove(this.d);
        } else {
            str2 = TileOverlay.b;
            str = "FileTile pic must be 256 * 256";
        }
        Log.e(str2, str);
        this.e.e.remove(this.d);
    }
}
