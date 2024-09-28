package com.baidu.mapsdkplatform.comapi.map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

class m extends Handler {
    final /* synthetic */ l a;

    m(l lVar) {
        this.a = lVar;
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (this.a.g != null && ((Long) message.obj).longValue() == this.a.g.j) {
            boolean z = true;
            int i = 0;
            if (message.what == 4000) {
                if (this.a.g.h != null) {
                    for (n next : this.a.g.h) {
                        Bitmap bitmap = null;
                        if (message.arg2 == 1) {
                            int[] iArr = new int[(this.a.d * this.a.e)];
                            int[] iArr2 = new int[(this.a.d * this.a.e)];
                            if (this.a.g.i != null) {
                                int[] a2 = this.a.g.i.a(iArr, this.a.d, this.a.e);
                                for (int i2 = 0; i2 < this.a.e; i2++) {
                                    for (int i3 = 0; i3 < this.a.d; i3++) {
                                        int i4 = a2[(this.a.d * i2) + i3];
                                        iArr2[(((this.a.e - i2) - 1) * this.a.d) + i3] = (i4 & -16711936) | ((i4 << 16) & 16711680) | ((i4 >> 16) & 255);
                                    }
                                }
                                bitmap = Bitmap.createBitmap(iArr2, this.a.d, this.a.e, Bitmap.Config.RGB_565);
                            } else {
                                return;
                            }
                        }
                        if (next != null) {
                            next.a(bitmap);
                        }
                    }
                }
            } else if (message.what == 39) {
                if (this.a.g != null && this.a.g.h != null) {
                    if (message.arg1 == 100) {
                        this.a.g.B();
                    } else if (message.arg1 == 200) {
                        this.a.g.L();
                    } else if (message.arg1 == 1) {
                        this.a.requestRender();
                    } else if (message.arg1 == 0) {
                        this.a.requestRender();
                        if (!this.a.g.b() && this.a.getRenderMode() != 0) {
                            this.a.setRenderMode(0);
                        }
                    } else if (message.arg1 == 2) {
                        if (this.a.g.h != null) {
                            for (n next2 : this.a.g.h) {
                                if (next2 != null) {
                                    next2.c();
                                }
                            }
                        } else {
                            return;
                        }
                    }
                    if (!this.a.g.k && this.a.e > 0 && this.a.d > 0 && this.a.g.b(0, 0) != null) {
                        this.a.g.k = true;
                        for (n next3 : this.a.g.h) {
                            if (next3 != null) {
                                next3.b();
                            }
                        }
                    }
                    for (n next4 : this.a.g.h) {
                        if (next4 != null) {
                            next4.a();
                        }
                    }
                }
            } else if (message.what == 41) {
                if (this.a.g != null && this.a.g.h != null) {
                    if (this.a.g.n || this.a.g.o) {
                        for (n next5 : this.a.g.h) {
                            if (next5 != null) {
                                next5.b(this.a.g.E());
                            }
                        }
                    }
                }
            } else if (message.what == 999) {
                if (this.a.g.h != null) {
                    for (n next6 : this.a.g.h) {
                        if (next6 != null) {
                            next6.e();
                        }
                    }
                }
            } else if (message.what == 50) {
                if (this.a.g.h != null) {
                    for (n next7 : this.a.g.h) {
                        if (next7 != null) {
                            if (message.arg1 != 0) {
                                if (message.arg1 == 1) {
                                    if (this.a.g.E().a >= 18.0f) {
                                        next7.a(true);
                                    }
                                }
                            }
                            next7.a(false);
                        }
                    }
                }
            } else if (message.what == 65289) {
                int i5 = message.arg2;
                if (message.arg1 == 300) {
                    if (message.arg2 != 1003) {
                        if (message.arg2 >= 1004) {
                            int i6 = message.arg2;
                        }
                        i = i5;
                        z = false;
                    }
                    for (n next8 : this.a.g.h) {
                        if (next8 != null) {
                            next8.a(z, i);
                        }
                    }
                }
            }
        }
    }
}
