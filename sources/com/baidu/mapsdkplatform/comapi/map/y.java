package com.baidu.mapsdkplatform.comapi.map;

import android.os.Message;

class y {
    private static final String a = y.class.getSimpleName();
    private x b;

    y() {
    }

    /* access modifiers changed from: package-private */
    public void a(Message message) {
        if (message.what == 65289) {
            int i = message.arg1;
            if (!(i == 12 || i == 101 || i == 102)) {
                switch (i) {
                    case -1:
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        break;
                    default:
                        return;
                }
            }
            x xVar = this.b;
            if (xVar != null) {
                xVar.a(message.arg1, message.arg2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(x xVar) {
        this.b = xVar;
    }

    /* access modifiers changed from: package-private */
    public void b(x xVar) {
        this.b = null;
    }
}
