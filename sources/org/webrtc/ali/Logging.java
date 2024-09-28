package org.webrtc.ali;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
    private static final Logger fallbackLogger = createFallbackLogger();
    private static volatile boolean loggingEnabled;
    private static volatile NativeLibStatus nativeLibStatus = NativeLibStatus.UNINITIALIZED;
    private static volatile boolean tracingEnabled;

    private enum NativeLibStatus {
        UNINITIALIZED,
        LOADED,
        FAILED
    }

    public enum Severity {
        LS_SENSITIVE,
        LS_VERBOSE,
        LS_INFO,
        LS_WARNING,
        LS_ERROR,
        LS_NONE
    }

    private static native void nativeEnableLogThreads();

    private static native void nativeEnableLogTimeStamps();

    private static native void nativeEnableLogToDebugOutput(int i);

    private static native void nativeEnableTracing(String str, int i);

    private static native void nativeLog(int i, String str, String str2);

    private static Logger createFallbackLogger() {
        Logger fallbackLogger2 = Logger.getLogger("org.webrtc.Logging");
        fallbackLogger2.setLevel(Level.ALL);
        return fallbackLogger2;
    }

    private static boolean loadNativeLibrary() {
        if (nativeLibStatus == NativeLibStatus.UNINITIALIZED) {
            try {
                nativeLibStatus = NativeLibStatus.FAILED;
            } catch (UnsatisfiedLinkError t) {
                nativeLibStatus = NativeLibStatus.FAILED;
                fallbackLogger.log(Level.WARNING, "Failed to load jingle_peerconnection_so: ", t);
            }
        }
        return nativeLibStatus == NativeLibStatus.LOADED;
    }

    public enum TraceLevel {
        TRACE_NONE(0),
        TRACE_STATEINFO(1),
        TRACE_WARNING(2),
        TRACE_ERROR(4),
        TRACE_CRITICAL(8),
        TRACE_APICALL(16),
        TRACE_DEFAULT(255),
        TRACE_MODULECALL(32),
        TRACE_MEMORY(256),
        TRACE_TIMER(512),
        TRACE_STREAM(1024),
        TRACE_DEBUG(2048),
        TRACE_INFO(4096),
        TRACE_TERSEINFO(8192),
        TRACE_ALL(65535);
        
        public final int level;

        private TraceLevel(int level2) {
            this.level = level2;
        }
    }

    public static void enableLogThreads() {
        if (!loadNativeLibrary()) {
            fallbackLogger.log(Level.WARNING, "Cannot enable log thread because native lib not loaded.");
        } else {
            nativeEnableLogThreads();
        }
    }

    public static void enableLogTimeStamps() {
        if (!loadNativeLibrary()) {
            fallbackLogger.log(Level.WARNING, "Cannot enable log timestamps because native lib not loaded.");
        } else {
            nativeEnableLogTimeStamps();
        }
    }

    public static synchronized void enableTracing(String path, EnumSet<TraceLevel> levels) {
        synchronized (Logging.class) {
            if (!loadNativeLibrary()) {
                fallbackLogger.log(Level.WARNING, "Cannot enable tracing because native lib not loaded.");
            } else if (!tracingEnabled) {
                int nativeLevel = 0;
                Iterator it = levels.iterator();
                while (it.hasNext()) {
                    nativeLevel |= ((TraceLevel) it.next()).level;
                }
                nativeEnableTracing(path, nativeLevel);
                tracingEnabled = true;
            }
        }
    }

    public static synchronized void enableLogToDebugOutput(Severity severity) {
        synchronized (Logging.class) {
            if (!loadNativeLibrary()) {
                fallbackLogger.log(Level.WARNING, "Cannot enable logging because native lib not loaded.");
                return;
            }
            nativeEnableLogToDebugOutput(severity.ordinal());
            loggingEnabled = true;
        }
    }

    public static void log(Severity severity, String tag, String message) {
        Level level;
        if (loggingEnabled) {
            nativeLog(severity.ordinal(), tag, message);
            return;
        }
        int i = AnonymousClass1.$SwitchMap$org$webrtc$ali$Logging$Severity[severity.ordinal()];
        if (i == 1) {
            level = Level.SEVERE;
        } else if (i == 2) {
            level = Level.WARNING;
        } else if (i != 3) {
            level = Level.FINE;
        } else {
            level = Level.INFO;
        }
        Logger logger = fallbackLogger;
        logger.log(level, tag + ": " + message);
    }

    /* renamed from: org.webrtc.ali.Logging$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$ali$Logging$Severity;

        static {
            int[] iArr = new int[Severity.values().length];
            $SwitchMap$org$webrtc$ali$Logging$Severity = iArr;
            try {
                iArr[Severity.LS_ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$webrtc$ali$Logging$Severity[Severity.LS_WARNING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$webrtc$ali$Logging$Severity[Severity.LS_INFO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static void d(String tag, String message) {
        log(Severity.LS_INFO, tag, message);
    }

    public static void e(String tag, String message) {
        log(Severity.LS_ERROR, tag, message);
    }

    public static void w(String tag, String message) {
        log(Severity.LS_WARNING, tag, message);
    }

    public static void e(String tag, String message, Throwable e) {
        log(Severity.LS_ERROR, tag, message);
        log(Severity.LS_ERROR, tag, e.toString());
        log(Severity.LS_ERROR, tag, getStackTraceString(e));
    }

    public static void w(String tag, String message, Throwable e) {
        log(Severity.LS_WARNING, tag, message);
        log(Severity.LS_WARNING, tag, e.toString());
        log(Severity.LS_WARNING, tag, getStackTraceString(e));
    }

    public static void v(String tag, String message) {
        log(Severity.LS_VERBOSE, tag, message);
    }

    private static String getStackTraceString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
