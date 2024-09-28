package com.baidu.mapsdkplatform.comapi.b.a;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.net.URLEncoder;

public class a implements Thread.UncaughtExceptionHandler {
    private static volatile boolean b = false;
    private String a;
    private Thread.UncaughtExceptionHandler c;

    /* renamed from: com.baidu.mapsdkplatform.comapi.b.a.a$a  reason: collision with other inner class name */
    private static class C0018a {
        /* access modifiers changed from: private */
        public static final a a = new a();
    }

    private a() {
        this.a = "";
        this.c = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static a a() {
        return C0018a.a;
    }

    private void a(Throwable th) {
        if (th != null) {
            String th2 = th.toString();
            if (!th2.isEmpty() && !th2.contains("BDMapSDKException")) {
                if (th2.contains("com.baidu.platform") || th2.contains("com.baidu.mapsdkplatform") || th2.contains("com.baidu.mapsdkvi")) {
                    try {
                        StringWriter stringWriter = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(stringWriter);
                        th.printStackTrace(printWriter);
                        Throwable cause = th.getCause();
                        if (cause != null) {
                            cause.printStackTrace(printWriter);
                        }
                        printWriter.close();
                        String obj = stringWriter.toString();
                        if (obj.isEmpty() || this.a == null) {
                            return;
                        }
                        if (!this.a.isEmpty()) {
                            File file = new File(URLEncoder.encode(this.a + (System.currentTimeMillis() / 1000) + ".txt", "UTF-8"));
                            if (file.exists() || file.createNewFile()) {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(obj.getBytes());
                                fileOutputStream.close();
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a(String str) {
        this.a = str;
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof a)) {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    public void uncaughtException(Thread thread, Throwable th) {
        if (!b) {
            b = true;
            a(th);
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.c;
            if (uncaughtExceptionHandler != null) {
                uncaughtExceptionHandler.uncaughtException(thread, th);
            }
        }
    }
}
