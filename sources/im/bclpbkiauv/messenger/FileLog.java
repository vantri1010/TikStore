package im.bclpbkiauv.messenger;

import android.util.Log;
import im.bclpbkiauv.messenger.time.FastDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class FileLog {
    private static volatile FileLog Instance = null;
    private static final String tag = "tmessages";
    private File currentFile = null;
    private FastDateFormat dateFormat = null;
    private boolean initied;
    private DispatchQueue logQueue = null;
    private File networkFile = null;
    private OutputStreamWriter streamWriter = null;

    public static FileLog getInstance() {
        FileLog localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileLog.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    FileLog fileLog = new FileLog();
                    localInstance = fileLog;
                    Instance = fileLog;
                }
            }
        }
        return localInstance;
    }

    private FileLog() {
        if (BuildVars.LOGS_ENABLED) {
            init();
        }
    }

    public void init() {
        if (!this.initied) {
            this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
            try {
                File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
                if (sdCard != null) {
                    File dir = new File(sdCard.getAbsolutePath() + "/logs");
                    dir.mkdirs();
                    this.currentFile = new File(dir, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
                    try {
                        this.logQueue = new DispatchQueue("logQueue");
                        this.currentFile.createNewFile();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
                        this.streamWriter = outputStreamWriter;
                        outputStreamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
                        this.streamWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.initied = true;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void ensureInitied() {
        getInstance().init();
    }

    public static String getNetworkLogPath() {
        if (!BuildVars.LOGS_ENABLED) {
            return "";
        }
        try {
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
            if (sdCard == null) {
                return "";
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            FileLog instance = getInstance();
            instance.networkFile = new File(dir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void e(String message, Throwable exception) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, message, exception);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(message, exception) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ Throwable f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        FileLog.lambda$e$0(this.f$0, this.f$1);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$e$0(String message, Throwable exception) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
            getInstance().streamWriter.write(exception.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(message) {
                    private final /* synthetic */ String f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        FileLog.lambda$e$1(this.f$0);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$e$1(String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String tag2, String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag2, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(tag2, message) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ String f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        FileLog.lambda$e$2(this.f$0, this.f$1);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$e$2(String tag2, String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + tag2 + " :" + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String tag2, String message, Throwable exception) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.e(tag2, message, exception);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(tag2, message, exception) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ Throwable f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FileLog.lambda$e$3(this.f$0, this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$e$3(String tag2, String message, Throwable exception) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + tag2 + " :" + message + "\n");
            getInstance().streamWriter.write(exception.toString());
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(Throwable e) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            e.printStackTrace();
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(e) {
                    private final /* synthetic */ Throwable f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        FileLog.lambda$e$4(this.f$0);
                    }
                });
            } else {
                e.printStackTrace();
            }
        }
    }

    static /* synthetic */ void lambda$e$4(Throwable e) {
        try {
            getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + e + "\n");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
                outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + stackTraceElement + "\n");
            }
            getInstance().streamWriter.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void d(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(message) {
                    private final /* synthetic */ String f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        FileLog.lambda$d$5(this.f$0);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$d$5(String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void d(String tag2, String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.d(tag2, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(tag2, message) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ String f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        FileLog.lambda$d$6(this.f$0, this.f$1);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$d$6(String tag2, String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " D/" + tag2 + " :" + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w(tag, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(message) {
                    private final /* synthetic */ String f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        FileLog.lambda$w$7(this.f$0);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$w$7(String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(String tag2, String message) {
        if (BuildVars.LOGS_ENABLED) {
            ensureInitied();
            Log.w(tag2, message);
            if (getInstance().streamWriter != null) {
                getInstance().logQueue.postRunnable(new Runnable(tag2, message) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ String f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        FileLog.lambda$w$8(this.f$0, this.f$1);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$w$8(String tag2, String message) {
        try {
            OutputStreamWriter outputStreamWriter = getInstance().streamWriter;
            outputStreamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " W/" + tag2 + ": " + message + "\n");
            getInstance().streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanupLogs() {
        ensureInitied();
        File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
        if (sdCard != null) {
            File[] files = new File(sdCard.getAbsolutePath() + "/logs").listFiles();
            if (files != null) {
                for (File file : files) {
                    if ((getInstance().currentFile == null || !file.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())) && (getInstance().networkFile == null || !file.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath()))) {
                        file.delete();
                    }
                }
            }
        }
    }
}
