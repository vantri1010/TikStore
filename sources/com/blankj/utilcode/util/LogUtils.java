package com.blankj.utilcode.util;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import androidx.collection.SimpleArrayMap;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class LogUtils {
    public static final int A = 7;
    private static final String ARGS = "args";
    private static final String BOTTOM_BORDER = "└────────────────────────────────────────────────────────────────────────────────────────────────────────────────";
    private static final String BOTTOM_CORNER = "└";
    private static final Config CONFIG = new Config();
    public static final int D = 3;
    public static final int E = 6;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final int FILE = 16;
    /* access modifiers changed from: private */
    public static final String FILE_SEP = System.getProperty("file.separator");
    public static final int I = 4;
    /* access modifiers changed from: private */
    public static final SimpleArrayMap<Class, IFormatter> I_FORMATTER_MAP = new SimpleArrayMap<>();
    private static final int JSON = 32;
    private static final String LEFT_BORDER = "│ ";
    /* access modifiers changed from: private */
    public static final String LINE_SEP = System.getProperty("line.separator");
    private static final int MAX_LEN = 1100;
    private static final String MIDDLE_BORDER = "├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String MIDDLE_CORNER = "├";
    private static final String MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String NOTHING = "log nothing";
    private static final String NULL = "null";
    private static final String PLACEHOLDER = " ";
    private static final String SIDE_DIVIDER = "────────────────────────────────────────────────────────";
    /* access modifiers changed from: private */
    public static final char[] T = {'V', 'D', 'I', 'W', 'E', 'A'};
    private static final String TOP_BORDER = "┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────";
    private static final String TOP_CORNER = "┌";
    public static final int V = 2;
    public static final int W = 5;
    private static final int XML = 48;
    private static SimpleDateFormat simpleDateFormat;

    public interface IFileWriter {
        void write(String str, String str2);
    }

    public static abstract class IFormatter<T> {
        public abstract String format(T t);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {
    }

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Config getConfig() {
        return CONFIG;
    }

    public static void v(Object... contents) {
        log(2, CONFIG.getGlobalTag(), contents);
    }

    public static void vTag(String tag, Object... contents) {
        log(2, tag, contents);
    }

    public static void d(Object... contents) {
        log(3, CONFIG.getGlobalTag(), contents);
    }

    public static void dTag(String tag, Object... contents) {
        log(3, tag, contents);
    }

    public static void i(Object... contents) {
        log(4, CONFIG.getGlobalTag(), contents);
    }

    public static void iTag(String tag, Object... contents) {
        log(4, tag, contents);
    }

    public static void w(Object... contents) {
        log(5, CONFIG.getGlobalTag(), contents);
    }

    public static void wTag(String tag, Object... contents) {
        log(5, tag, contents);
    }

    public static void e(Object... contents) {
        log(6, CONFIG.getGlobalTag(), contents);
    }

    public static void eTag(String tag, Object... contents) {
        log(6, tag, contents);
    }

    public static void a(Object... contents) {
        log(7, CONFIG.getGlobalTag(), contents);
    }

    public static void aTag(String tag, Object... contents) {
        log(7, tag, contents);
    }

    public static void file(Object content) {
        log(19, CONFIG.getGlobalTag(), content);
    }

    public static void file(int type, Object content) {
        log(type | 16, CONFIG.getGlobalTag(), content);
    }

    public static void file(String tag, Object content) {
        log(19, tag, content);
    }

    public static void file(int type, String tag, Object content) {
        log(type | 16, tag, content);
    }

    public static void json(Object content) {
        log(35, CONFIG.getGlobalTag(), content);
    }

    public static void json(int type, Object content) {
        log(type | 32, CONFIG.getGlobalTag(), content);
    }

    public static void json(String tag, Object content) {
        log(35, tag, content);
    }

    public static void json(int type, String tag, Object content) {
        log(type | 32, tag, content);
    }

    public static void xml(String content) {
        log(51, CONFIG.getGlobalTag(), content);
    }

    public static void xml(int type, String content) {
        log(type | 48, CONFIG.getGlobalTag(), content);
    }

    public static void xml(String tag, String content) {
        log(51, tag, content);
    }

    public static void xml(int type, String tag, String content) {
        log(type | 48, tag, content);
    }

    public static void log(int type, String tag, Object... contents) {
        if (CONFIG.isLogSwitch()) {
            final int type_low = type & 15;
            int type_high = type & PsExtractor.VIDEO_STREAM_MASK;
            if (!CONFIG.isLog2ConsoleSwitch() && !CONFIG.isLog2FileSwitch() && type_high != 16) {
                return;
            }
            if (type_low >= CONFIG.mConsoleFilter || type_low >= CONFIG.mFileFilter) {
                final TagHead tagHead = processTagAndHead(tag);
                final String body = processBody(type_high, contents);
                if (CONFIG.isLog2ConsoleSwitch() && type_high != 16 && type_low >= CONFIG.mConsoleFilter) {
                    print2Console(type_low, tagHead.tag, tagHead.consoleHead, body);
                }
                if ((CONFIG.isLog2FileSwitch() || type_high == 16) && type_low >= CONFIG.mFileFilter) {
                    EXECUTOR.execute(new Runnable() {
                        public void run() {
                            int i = type_low;
                            String str = tagHead.tag;
                            LogUtils.print2File(i, str, tagHead.fileHead + body);
                        }
                    });
                }
            }
        }
    }

    public static List<File> getLogFiles() {
        File logDir = new File(CONFIG.getDir());
        if (!logDir.exists()) {
            return new ArrayList();
        }
        File[] files = logDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return LogUtils.isMatchLogFileName(name);
            }
        });
        List<File> list = new ArrayList<>();
        Collections.addAll(list, files);
        return list;
    }

    private static TagHead processTagAndHead(String tag) {
        String tag2;
        String tag3;
        String tag4;
        if (CONFIG.mTagIsSpace || CONFIG.isLogHeadSwitch()) {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            int stackIndex = CONFIG.getStackOffset() + 3;
            if (stackIndex >= stackTrace.length) {
                String fileName = getFileName(stackTrace[3]);
                if (!CONFIG.mTagIsSpace || !isSpace(tag)) {
                    tag4 = tag;
                } else {
                    int index = fileName.indexOf(46);
                    tag4 = index == -1 ? fileName : fileName.substring(0, index);
                }
                return new TagHead(tag4, (String[]) null, ": ");
            }
            StackTraceElement targetElement = stackTrace[stackIndex];
            String fileName2 = getFileName(targetElement);
            if (!CONFIG.mTagIsSpace || !isSpace(tag)) {
                tag3 = tag;
            } else {
                int index2 = fileName2.indexOf(46);
                tag3 = index2 == -1 ? fileName2 : fileName2.substring(0, index2);
            }
            if (CONFIG.isLogHeadSwitch()) {
                String tName = Thread.currentThread().getName();
                int i = 5;
                String head = new Formatter().format("%s, %s.%s(%s:%d)", new Object[]{tName, targetElement.getClassName(), targetElement.getMethodName(), fileName2, Integer.valueOf(targetElement.getLineNumber())}).toString();
                String fileHead = " [" + head + "]: ";
                if (CONFIG.getStackDeep() <= 1) {
                    return new TagHead(tag3, new String[]{head}, fileHead);
                }
                String[] consoleHead = new String[Math.min(CONFIG.getStackDeep(), stackTrace.length - stackIndex)];
                consoleHead[0] = head;
                String space = new Formatter().format("%" + (tName.length() + 2) + "s", new Object[]{""}).toString();
                int i2 = 1;
                int len = consoleHead.length;
                while (i2 < len) {
                    StackTraceElement targetElement2 = stackTrace[i2 + stackIndex];
                    Formatter formatter = new Formatter();
                    StackTraceElement[] stackTrace2 = stackTrace;
                    Object[] objArr = new Object[i];
                    objArr[0] = space;
                    objArr[1] = targetElement2.getClassName();
                    objArr[2] = targetElement2.getMethodName();
                    objArr[3] = getFileName(targetElement2);
                    objArr[4] = Integer.valueOf(targetElement2.getLineNumber());
                    consoleHead[i2] = formatter.format("%s%s.%s(%s:%d)", objArr).toString();
                    i2++;
                    stackTrace = stackTrace2;
                    i = 5;
                }
                return new TagHead(tag3, consoleHead, fileHead);
            }
            tag2 = tag3;
        } else {
            tag2 = CONFIG.getGlobalTag();
        }
        return new TagHead(tag2, (String[]) null, ": ");
    }

    private static String getFileName(StackTraceElement targetElement) {
        String fileName = targetElement.getFileName();
        if (fileName != null) {
            return fileName;
        }
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1];
        }
        int index = className.indexOf(36);
        if (index != -1) {
            className = className.substring(0, index);
        }
        return className + ".java";
    }

    private static String processBody(int type, Object... contents) {
        String body = NULL;
        if (contents != null) {
            if (contents.length == 1) {
                body = formatObject(type, contents[0]);
            } else {
                StringBuilder sb = new StringBuilder();
                int len = contents.length;
                for (int i = 0; i < len; i++) {
                    Object content = contents[i];
                    sb.append(ARGS);
                    sb.append("[");
                    sb.append(i);
                    sb.append("]");
                    sb.append(" = ");
                    sb.append(formatObject(content));
                    sb.append(LINE_SEP);
                }
                body = sb.toString();
            }
        }
        return body.length() == 0 ? NOTHING : body;
    }

    private static String formatObject(int type, Object object) {
        if (object == null) {
            return NULL;
        }
        if (type == 32) {
            return LogFormatter.object2String(object, 32);
        }
        if (type == 48) {
            return LogFormatter.object2String(object, 48);
        }
        return formatObject(object);
    }

    /* access modifiers changed from: private */
    public static String formatObject(Object object) {
        IFormatter iFormatter;
        if (object == null) {
            return NULL;
        }
        if (I_FORMATTER_MAP.isEmpty() || (iFormatter = I_FORMATTER_MAP.get(getClassFromObject(object))) == null) {
            return LogFormatter.object2String(object);
        }
        return iFormatter.format(object);
    }

    private static void print2Console(int type, String tag, String[] head, String msg) {
        if (CONFIG.isSingleTagSwitch()) {
            printSingleTagMsg(type, tag, processSingleTagMsg(type, tag, head, msg));
            return;
        }
        printBorder(type, tag, true);
        printHead(type, tag, head);
        printMsg(type, tag, msg);
        printBorder(type, tag, false);
    }

    private static void printBorder(int type, String tag, boolean isTop) {
        if (CONFIG.isLogBorderSwitch()) {
            Log.println(type, tag, isTop ? TOP_BORDER : BOTTOM_BORDER);
        }
    }

    private static void printHead(int type, String tag, String[] head) {
        String str;
        if (head != null) {
            for (String aHead : head) {
                if (CONFIG.isLogBorderSwitch()) {
                    str = LEFT_BORDER + aHead;
                } else {
                    str = aHead;
                }
                Log.println(type, tag, str);
            }
            if (CONFIG.isLogBorderSwitch()) {
                Log.println(type, tag, MIDDLE_BORDER);
            }
        }
    }

    private static void printMsg(int type, String tag, String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len));
                return;
            }
            return;
        }
        printSubMsg(type, tag, msg);
    }

    private static void printSubMsg(int type, String tag, String msg) {
        if (!CONFIG.isLogBorderSwitch()) {
            Log.println(type, tag, msg);
            return;
        }
        new StringBuilder();
        for (String line : msg.split(LINE_SEP)) {
            Log.println(type, tag, LEFT_BORDER + line);
        }
    }

    private static String processSingleTagMsg(int type, String tag, String[] head, String msg) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        if (CONFIG.isLogBorderSwitch()) {
            sb.append(" ");
            sb.append(LINE_SEP);
            sb.append(TOP_BORDER);
            sb.append(LINE_SEP);
            if (head != null) {
                for (String aHead : head) {
                    sb.append(LEFT_BORDER);
                    sb.append(aHead);
                    sb.append(LINE_SEP);
                }
                sb.append(MIDDLE_BORDER);
                sb.append(LINE_SEP);
            }
            String[] split = msg.split(LINE_SEP);
            int length = split.length;
            while (i < length) {
                String line = split[i];
                sb.append(LEFT_BORDER);
                sb.append(line);
                sb.append(LINE_SEP);
                i++;
            }
            sb.append(BOTTOM_BORDER);
        } else {
            if (head != null) {
                sb.append(" ");
                sb.append(LINE_SEP);
                int length2 = head.length;
                while (i < length2) {
                    sb.append(head[i]);
                    sb.append(LINE_SEP);
                    i++;
                }
            }
            sb.append(msg);
        }
        return sb.toString();
    }

    private static void printSingleTagMsg(int type, String tag, String msg) {
        int len = msg.length();
        int countOfSub = CONFIG.isLogBorderSwitch() ? (len - BOTTOM_BORDER.length()) / MAX_LEN : len / MAX_LEN;
        if (countOfSub <= 0) {
            Log.println(type, tag, msg);
        } else if (CONFIG.isLogBorderSwitch()) {
            Log.println(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER);
            int index = MAX_LEN;
            for (int i = 1; i < countOfSub; i++) {
                Log.println(type, tag, " " + LINE_SEP + TOP_BORDER + LINE_SEP + LEFT_BORDER + msg.substring(index, index + MAX_LEN) + LINE_SEP + BOTTOM_BORDER);
                index += MAX_LEN;
            }
            if (index != len - BOTTOM_BORDER.length()) {
                Log.println(type, tag, " " + LINE_SEP + TOP_BORDER + LINE_SEP + LEFT_BORDER + msg.substring(index, len));
            }
        } else {
            Log.println(type, tag, msg.substring(0, MAX_LEN));
            int index2 = MAX_LEN;
            for (int i2 = 1; i2 < countOfSub; i2++) {
                Log.println(type, tag, " " + LINE_SEP + msg.substring(index2, index2 + MAX_LEN));
                index2 += MAX_LEN;
            }
            if (index2 != len) {
                Log.println(type, tag, " " + LINE_SEP + msg.substring(index2, len));
            }
        }
    }

    /* access modifiers changed from: private */
    public static void print2File(int type, String tag, String msg) {
        String format = getSdf().format(new Date());
        String date = format.substring(0, 10);
        String time = format.substring(11);
        String fullPath = CONFIG.getDir() + CONFIG.getFilePrefix() + "_" + date + "_" + CONFIG.getProcessName() + CONFIG.getFileExtension();
        if (!createOrExistsFile(fullPath, date)) {
            Log.e("LogUtils", "create " + fullPath + " failed!");
            return;
        }
        input2File(time + T[type - 2] + "/" + tag + msg + LINE_SEP, fullPath);
    }

    private static SimpleDateFormat getSdf() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss.SSS ", Locale.getDefault());
        }
        return simpleDateFormat;
    }

    private static boolean createOrExistsFile(String filePath, String date) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            deleteDueLogs(filePath, date);
            boolean isCreate = file.createNewFile();
            if (isCreate) {
                printDeviceInfo(filePath, date);
            }
            return isCreate;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void deleteDueLogs(String filePath, String date) {
        if (CONFIG.getSaveDays() > 0) {
            File[] files = new File(filePath).getParentFile().listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return LogUtils.isMatchLogFileName(name);
                }
            });
            if (files == null) {
                String str = date;
            } else if (files.length <= 0) {
                String str2 = date;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
                try {
                    long dueMillis = sdf.parse(date).getTime() - (((long) CONFIG.getSaveDays()) * 86400000);
                    for (final File aFile : files) {
                        String name = aFile.getName();
                        int length = name.length();
                        if (sdf.parse(findDate(name)).getTime() <= dueMillis) {
                            EXECUTOR.execute(new Runnable() {
                                public void run() {
                                    if (!aFile.delete()) {
                                        Log.e("LogUtils", "delete " + aFile + " failed!");
                                    }
                                }
                            });
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean isMatchLogFileName(String name) {
        return name.matches("^" + CONFIG.getFilePrefix() + "_[0-9]{4}_[0-9]{2}_[0-9]{2}_.*$");
    }

    private static String findDate(String str) {
        Matcher matcher = Pattern.compile("[0-9]{4}_[0-9]{2}_[0-9]{2}").matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private static void printDeviceInfo(String filePath, String date) {
        String versionName = "";
        int versionCode = 0;
        try {
            PackageInfo pi = Utils.getApp().getPackageManager().getPackageInfo(Utils.getApp().getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        input2File("************* Log Head ****************\nDate of Log        : " + date + "\nDevice Manufacturer: " + Build.MANUFACTURER + "\nDevice Model       : " + Build.MODEL + "\nAndroid Version    : " + Build.VERSION.RELEASE + "\nAndroid SDK        : " + Build.VERSION.SDK_INT + "\nApp VersionName    : " + versionName + "\nApp VersionCode    : " + versionCode + "\n************* Log Head ****************\n\n", filePath);
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? file.mkdirs() : file.isDirectory());
    }

    /* access modifiers changed from: private */
    public static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static void input2File(String input, String filePath) {
        if (CONFIG.mFileWriter == null) {
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
                bw.write(input);
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                Log.e("LogUtils", "log to " + filePath + " failed!");
                if (bw != null) {
                    bw.close();
                }
            } catch (Throwable th) {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        } else {
            CONFIG.mFileWriter.write(filePath, input);
        }
    }

    public static final class Config {
        /* access modifiers changed from: private */
        public int mConsoleFilter;
        private String mDefaultDir;
        private String mDir;
        private String mFileExtension;
        /* access modifiers changed from: private */
        public int mFileFilter;
        private String mFilePrefix;
        /* access modifiers changed from: private */
        public IFileWriter mFileWriter;
        private String mGlobalTag;
        private boolean mLog2ConsoleSwitch;
        private boolean mLog2FileSwitch;
        private boolean mLogBorderSwitch;
        private boolean mLogHeadSwitch;
        private boolean mLogSwitch;
        private String mProcessName;
        private int mSaveDays;
        private boolean mSingleTagSwitch;
        private int mStackDeep;
        private int mStackOffset;
        /* access modifiers changed from: private */
        public boolean mTagIsSpace;

        private Config() {
            this.mFilePrefix = "util";
            this.mFileExtension = ".txt";
            this.mLogSwitch = true;
            this.mLog2ConsoleSwitch = true;
            this.mGlobalTag = "";
            this.mTagIsSpace = true;
            this.mLogHeadSwitch = true;
            this.mLog2FileSwitch = false;
            this.mLogBorderSwitch = true;
            this.mSingleTagSwitch = true;
            this.mConsoleFilter = 2;
            this.mFileFilter = 2;
            this.mStackDeep = 1;
            this.mStackOffset = 0;
            this.mSaveDays = -1;
            this.mProcessName = Utils.getCurrentProcessName();
            if (this.mDefaultDir == null) {
                if (!"mounted".equals(Environment.getExternalStorageState()) || Utils.getApp().getExternalCacheDir() == null) {
                    this.mDefaultDir = Utils.getApp().getCacheDir() + LogUtils.FILE_SEP + "log" + LogUtils.FILE_SEP;
                    return;
                }
                this.mDefaultDir = Utils.getApp().getExternalCacheDir() + LogUtils.FILE_SEP + "log" + LogUtils.FILE_SEP;
            }
        }

        public final Config setLogSwitch(boolean logSwitch) {
            this.mLogSwitch = logSwitch;
            return this;
        }

        public final Config setConsoleSwitch(boolean consoleSwitch) {
            this.mLog2ConsoleSwitch = consoleSwitch;
            return this;
        }

        public final Config setGlobalTag(String tag) {
            if (LogUtils.isSpace(tag)) {
                this.mGlobalTag = "";
                this.mTagIsSpace = true;
            } else {
                this.mGlobalTag = tag;
                this.mTagIsSpace = false;
            }
            return this;
        }

        public final Config setLogHeadSwitch(boolean logHeadSwitch) {
            this.mLogHeadSwitch = logHeadSwitch;
            return this;
        }

        public final Config setLog2FileSwitch(boolean log2FileSwitch) {
            this.mLog2FileSwitch = log2FileSwitch;
            return this;
        }

        public final Config setDir(String dir) {
            String str;
            if (LogUtils.isSpace(dir)) {
                this.mDir = null;
            } else {
                if (dir.endsWith(LogUtils.FILE_SEP)) {
                    str = dir;
                } else {
                    str = dir + LogUtils.FILE_SEP;
                }
                this.mDir = str;
            }
            return this;
        }

        public final Config setDir(File dir) {
            String str;
            if (dir == null) {
                str = null;
            } else {
                str = dir.getAbsolutePath() + LogUtils.FILE_SEP;
            }
            this.mDir = str;
            return this;
        }

        public final Config setFilePrefix(String filePrefix) {
            if (LogUtils.isSpace(filePrefix)) {
                this.mFilePrefix = "util";
            } else {
                this.mFilePrefix = filePrefix;
            }
            return this;
        }

        public final Config setFileExtension(String fileExtension) {
            if (LogUtils.isSpace(fileExtension)) {
                this.mFileExtension = ".txt";
            } else if (fileExtension.startsWith(".")) {
                this.mFileExtension = fileExtension;
            } else {
                this.mFileExtension = "." + fileExtension;
            }
            return this;
        }

        public final Config setBorderSwitch(boolean borderSwitch) {
            this.mLogBorderSwitch = borderSwitch;
            return this;
        }

        public final Config setSingleTagSwitch(boolean singleTagSwitch) {
            this.mSingleTagSwitch = singleTagSwitch;
            return this;
        }

        public final Config setConsoleFilter(int consoleFilter) {
            this.mConsoleFilter = consoleFilter;
            return this;
        }

        public final Config setFileFilter(int fileFilter) {
            this.mFileFilter = fileFilter;
            return this;
        }

        public final Config setStackDeep(int stackDeep) {
            this.mStackDeep = stackDeep;
            return this;
        }

        public final Config setStackOffset(int stackOffset) {
            this.mStackOffset = stackOffset;
            return this;
        }

        public final Config setSaveDays(int saveDays) {
            this.mSaveDays = saveDays;
            return this;
        }

        public final <T> Config addFormatter(IFormatter<T> iFormatter) {
            if (iFormatter != null) {
                LogUtils.I_FORMATTER_MAP.put(LogUtils.getTypeClassFromParadigm(iFormatter), iFormatter);
            }
            return this;
        }

        public final Config setFileWriter(IFileWriter fileWriter) {
            this.mFileWriter = fileWriter;
            return this;
        }

        public final String getProcessName() {
            String str = this.mProcessName;
            if (str == null) {
                return "";
            }
            return str.replace(com.king.zxing.util.LogUtils.COLON, "_");
        }

        public final String getDefaultDir() {
            return this.mDefaultDir;
        }

        public final String getDir() {
            String str = this.mDir;
            return str == null ? this.mDefaultDir : str;
        }

        public final String getFilePrefix() {
            return this.mFilePrefix;
        }

        public final String getFileExtension() {
            return this.mFileExtension;
        }

        public final boolean isLogSwitch() {
            return this.mLogSwitch;
        }

        public final boolean isLog2ConsoleSwitch() {
            return this.mLog2ConsoleSwitch;
        }

        public final String getGlobalTag() {
            if (LogUtils.isSpace(this.mGlobalTag)) {
                return "";
            }
            return this.mGlobalTag;
        }

        public final boolean isLogHeadSwitch() {
            return this.mLogHeadSwitch;
        }

        public final boolean isLog2FileSwitch() {
            return this.mLog2FileSwitch;
        }

        public final boolean isLogBorderSwitch() {
            return this.mLogBorderSwitch;
        }

        public final boolean isSingleTagSwitch() {
            return this.mSingleTagSwitch;
        }

        public final char getConsoleFilter() {
            return LogUtils.T[this.mConsoleFilter - 2];
        }

        public final char getFileFilter() {
            return LogUtils.T[this.mFileFilter - 2];
        }

        public final int getStackDeep() {
            return this.mStackDeep;
        }

        public final int getStackOffset() {
            return this.mStackOffset;
        }

        public final int getSaveDays() {
            return this.mSaveDays;
        }

        public String toString() {
            return "process: " + getProcessName() + LogUtils.LINE_SEP + "switch: " + isLogSwitch() + LogUtils.LINE_SEP + "console: " + isLog2ConsoleSwitch() + LogUtils.LINE_SEP + "tag: " + getGlobalTag() + LogUtils.LINE_SEP + "head: " + isLogHeadSwitch() + LogUtils.LINE_SEP + "file: " + isLog2FileSwitch() + LogUtils.LINE_SEP + "dir: " + getDir() + LogUtils.LINE_SEP + "filePrefix: " + getFilePrefix() + LogUtils.LINE_SEP + "border: " + isLogBorderSwitch() + LogUtils.LINE_SEP + "singleTag: " + isSingleTagSwitch() + LogUtils.LINE_SEP + "consoleFilter: " + getConsoleFilter() + LogUtils.LINE_SEP + "fileFilter: " + getFileFilter() + LogUtils.LINE_SEP + "stackDeep: " + getStackDeep() + LogUtils.LINE_SEP + "stackOffset: " + getStackOffset() + LogUtils.LINE_SEP + "saveDays: " + getSaveDays() + LogUtils.LINE_SEP + "formatter: " + LogUtils.I_FORMATTER_MAP;
        }
    }

    private static final class TagHead {
        String[] consoleHead;
        String fileHead;
        String tag;

        TagHead(String tag2, String[] consoleHead2, String fileHead2) {
            this.tag = tag2;
            this.consoleHead = consoleHead2;
            this.fileHead = fileHead2;
        }
    }

    private static final class LogFormatter {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

        private LogFormatter() {
        }

        static String object2String(Object object) {
            return object2String(object, -1);
        }

        static String object2String(Object object, int type) {
            if (object.getClass().isArray()) {
                return array2String(object);
            }
            if (object instanceof Throwable) {
                return throwable2String((Throwable) object);
            }
            if (object instanceof Bundle) {
                return bundle2String((Bundle) object);
            }
            if (object instanceof Intent) {
                return intent2String((Intent) object);
            }
            if (type == 32) {
                return object2Json(object);
            }
            if (type == 48) {
                return formatXml(object.toString());
            }
            return object.toString();
        }

        private static String throwable2String(Throwable e) {
            return ThrowableUtils.getFullStackTrace(e);
        }

        private static String bundle2String(Bundle bundle) {
            Iterator<String> iterator = bundle.keySet().iterator();
            if (!iterator.hasNext()) {
                return "Bundle {}";
            }
            StringBuilder sb = new StringBuilder(128);
            sb.append("Bundle { ");
            while (true) {
                String key = iterator.next();
                Object value = bundle.get(key);
                sb.append(key);
                sb.append('=');
                if (value instanceof Bundle) {
                    sb.append(value == bundle ? "(this Bundle)" : bundle2String((Bundle) value));
                } else {
                    sb.append(LogUtils.formatObject(value));
                }
                if (!iterator.hasNext()) {
                    sb.append(" }");
                    return sb.toString();
                }
                sb.append(',');
                sb.append(' ');
            }
        }

        private static String intent2String(Intent intent) {
            Intent mSelector;
            ClipData mClipData;
            StringBuilder sb = new StringBuilder(128);
            sb.append("Intent { ");
            boolean first = true;
            String mAction = intent.getAction();
            if (mAction != null) {
                sb.append("act=");
                sb.append(mAction);
                first = false;
            }
            Set<String> mCategories = intent.getCategories();
            if (mCategories != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("cat=[");
                boolean firstCategory = true;
                for (String c : mCategories) {
                    if (!firstCategory) {
                        sb.append(',');
                    }
                    sb.append(c);
                    firstCategory = false;
                }
                sb.append("]");
            }
            Uri mData = intent.getData();
            if (mData != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("dat=");
                sb.append(mData);
            }
            String mType = intent.getType();
            if (mType != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("typ=");
                sb.append(mType);
            }
            int mFlags = intent.getFlags();
            if (mFlags != 0) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("flg=0x");
                sb.append(Integer.toHexString(mFlags));
            }
            String mPackage = intent.getPackage();
            if (mPackage != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("pkg=");
                sb.append(mPackage);
            }
            ComponentName mComponent = intent.getComponent();
            if (mComponent != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("cmp=");
                sb.append(mComponent.flattenToShortString());
            }
            Rect mSourceBounds = intent.getSourceBounds();
            if (mSourceBounds != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("bnds=");
                sb.append(mSourceBounds.toShortString());
            }
            if (Build.VERSION.SDK_INT >= 16 && (mClipData = intent.getClipData()) != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                clipData2String(mClipData, sb);
            }
            Bundle mExtras = intent.getExtras();
            if (mExtras != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append("extras={");
                sb.append(bundle2String(mExtras));
                sb.append('}');
            }
            if (Build.VERSION.SDK_INT >= 15 && (mSelector = intent.getSelector()) != null) {
                if (!first) {
                    sb.append(' ');
                }
                sb.append("sel={");
                sb.append(mSelector == intent ? "(this Intent)" : intent2String(mSelector));
                sb.append("}");
            }
            sb.append(" }");
            return sb.toString();
        }

        private static void clipData2String(ClipData clipData, StringBuilder sb) {
            ClipData.Item item = clipData.getItemAt(0);
            if (item == null) {
                sb.append("ClipData.Item {}");
                return;
            }
            sb.append("ClipData.Item { ");
            String mHtmlText = item.getHtmlText();
            if (mHtmlText != null) {
                sb.append("H:");
                sb.append(mHtmlText);
                sb.append("}");
                return;
            }
            CharSequence mText = item.getText();
            if (mText != null) {
                sb.append("T:");
                sb.append(mText);
                sb.append("}");
                return;
            }
            Uri uri = item.getUri();
            if (uri != null) {
                sb.append("U:");
                sb.append(uri);
                sb.append("}");
                return;
            }
            Intent intent = item.getIntent();
            if (intent != null) {
                sb.append("I:");
                sb.append(intent2String(intent));
                sb.append("}");
                return;
            }
            sb.append("NULL");
            sb.append("}");
        }

        private static String object2Json(Object object) {
            if (object instanceof CharSequence) {
                return formatJson(object.toString());
            }
            try {
                return GSON.toJson(object);
            } catch (Throwable th) {
                return object.toString();
            }
        }

        private static String formatJson(String json) {
            try {
                int len = json.length();
                for (int i = 0; i < len; i++) {
                    char c = json.charAt(i);
                    if (c == '{') {
                        return new JSONObject(json).toString(2);
                    }
                    if (c == '[') {
                        return new JSONArray(json).toString(2);
                    }
                    if (!Character.isWhitespace(c)) {
                        return json;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

        private static String formatXml(String xml) {
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("indent", "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                String obj = xmlOutput.getWriter().toString();
                return obj.replaceFirst(">", ">" + LogUtils.LINE_SEP);
            } catch (Exception e) {
                e.printStackTrace();
                return xml;
            }
        }

        private static String array2String(Object object) {
            if (object instanceof Object[]) {
                return Arrays.deepToString((Object[]) object);
            }
            if (object instanceof boolean[]) {
                return Arrays.toString((boolean[]) object);
            }
            if (object instanceof byte[]) {
                return Arrays.toString((byte[]) object);
            }
            if (object instanceof char[]) {
                return Arrays.toString((char[]) object);
            }
            if (object instanceof double[]) {
                return Arrays.toString((double[]) object);
            }
            if (object instanceof float[]) {
                return Arrays.toString((float[]) object);
            }
            if (object instanceof int[]) {
                return Arrays.toString((int[]) object);
            }
            if (object instanceof long[]) {
                return Arrays.toString((long[]) object);
            }
            if (object instanceof short[]) {
                return Arrays.toString((short[]) object);
            }
            throw new IllegalArgumentException("Array has incompatible type: " + object.getClass());
        }
    }

    /* access modifiers changed from: private */
    public static <T> Class getTypeClassFromParadigm(IFormatter<T> formatter) {
        Type type;
        Type[] genericInterfaces = formatter.getClass().getGenericInterfaces();
        if (genericInterfaces.length == 1) {
            type = genericInterfaces[0];
        } else {
            type = formatter.getClass().getGenericSuperclass();
        }
        Type type2 = ((ParameterizedType) type).getActualTypeArguments()[0];
        while (type2 instanceof ParameterizedType) {
            type2 = ((ParameterizedType) type2).getRawType();
        }
        String className = type2.toString();
        if (className.startsWith("class ")) {
            className = className.substring(6);
        } else if (className.startsWith("interface ")) {
            className = className.substring(10);
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class getClassFromObject(Object obj) {
        String className;
        Class objClass = obj.getClass();
        if (objClass.isAnonymousClass() || objClass.isSynthetic()) {
            Type[] genericInterfaces = objClass.getGenericInterfaces();
            if (genericInterfaces.length == 1) {
                Type type = genericInterfaces[0];
                while (type instanceof ParameterizedType) {
                    type = ((ParameterizedType) type).getRawType();
                }
                className = type.toString();
            } else {
                Type type2 = objClass.getGenericSuperclass();
                while (type2 instanceof ParameterizedType) {
                    type2 = ((ParameterizedType) type2).getRawType();
                }
                className = type2.toString();
            }
            if (className.startsWith("class ")) {
                className = className.substring(6);
            } else if (className.startsWith("interface ")) {
                className = className.substring(10);
            }
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return objClass;
    }
}
