package com.baidu.location;

class c extends Thread {
    final /* synthetic */ LocationClient a;

    c(LocationClient locationClient) {
        this.a = locationClient;
    }

    public void run() {
        try {
            if (this.a.E == null) {
                com.baidu.location.b.c unused = this.a.E = new com.baidu.location.b.c(this.a.f, this.a.d, this.a);
            }
            if (this.a.E != null) {
                this.a.E.a();
                this.a.E.c();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
