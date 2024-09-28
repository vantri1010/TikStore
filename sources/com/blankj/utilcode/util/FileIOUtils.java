package com.blankj.utilcode.util;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public final class FileIOUtils {
    private static int sBufferSize = 524288;

    public interface OnProgressUpdateListener {
        void onProgressUpdate(double d);
    }

    private FileIOUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean writeFileFromIS(String filePath, InputStream is) {
        return writeFileFromIS(getFileByPath(filePath), is, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(String filePath, InputStream is, boolean append) {
        return writeFileFromIS(getFileByPath(filePath), is, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(File file, InputStream is) {
        return writeFileFromIS(file, is, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(File file, InputStream is, boolean append) {
        return writeFileFromIS(file, is, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromIS(String filePath, InputStream is, OnProgressUpdateListener listener) {
        return writeFileFromIS(getFileByPath(filePath), is, false, listener);
    }

    public static boolean writeFileFromIS(String filePath, InputStream is, boolean append, OnProgressUpdateListener listener) {
        return writeFileFromIS(getFileByPath(filePath), is, append, listener);
    }

    public static boolean writeFileFromIS(File file, InputStream is, OnProgressUpdateListener listener) {
        return writeFileFromIS(file, is, false, listener);
    }

    public static boolean writeFileFromIS(File file, InputStream is, boolean append, OnProgressUpdateListener listener) {
        if (is == null || !createOrExistsFile(file)) {
            return false;
        }
        OutputStream os = null;
        try {
            OutputStream os2 = new BufferedOutputStream(new FileOutputStream(file, append), sBufferSize);
            if (listener != null) {
                double totalSize = (double) is.available();
                int curSize = 0;
                listener.onProgressUpdate(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                byte[] data = new byte[sBufferSize];
                while (true) {
                    int read = is.read(data);
                    int len = read;
                    if (read == -1) {
                        break;
                    }
                    os2.write(data, 0, len);
                    curSize += len;
                    listener.onProgressUpdate(((double) curSize) / totalSize);
                }
            } else {
                byte[] data2 = new byte[sBufferSize];
                while (true) {
                    int read2 = is.read(data2);
                    int len2 = read2;
                    if (read2 == -1) {
                        break;
                    }
                    os2.write(data2, 0, len2);
                }
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return true;
        } catch (IOException e3) {
            e3.printStackTrace();
            try {
                is.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            return false;
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e6) {
                e6.printStackTrace();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes, boolean append) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes) {
        return writeFileFromBytesByStream(file, bytes, false, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes, boolean append) {
        return writeFileFromBytesByStream(file, bytes, append, (OnProgressUpdateListener) null);
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes, OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, false, listener);
    }

    public static boolean writeFileFromBytesByStream(String filePath, byte[] bytes, boolean append, OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(getFileByPath(filePath), bytes, append, listener);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes, OnProgressUpdateListener listener) {
        return writeFileFromBytesByStream(file, bytes, false, listener);
    }

    public static boolean writeFileFromBytesByStream(File file, byte[] bytes, boolean append, OnProgressUpdateListener listener) {
        if (bytes == null) {
            return false;
        }
        return writeFileFromIS(file, (InputStream) new ByteArrayInputStream(bytes), append, listener);
    }

    public static boolean writeFileFromBytesByChannel(String filePath, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByChannel(getFileByPath(filePath), bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByChannel(String filePath, byte[] bytes, boolean append, boolean isForce) {
        return writeFileFromBytesByChannel(getFileByPath(filePath), bytes, append, isForce);
    }

    public static boolean writeFileFromBytesByChannel(File file, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByChannel(file, bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByChannel(File file, byte[] bytes, boolean append, boolean isForce) {
        if (bytes == null || !createOrExistsFile(file)) {
            return false;
        }
        FileChannel fc = null;
        try {
            FileChannel fc2 = new FileOutputStream(file, append).getChannel();
            fc2.position(fc2.size());
            fc2.write(ByteBuffer.wrap(bytes));
            if (isForce) {
                fc2.force(true);
            }
            if (fc2 != null) {
                try {
                    fc2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e2) {
            e2.printStackTrace();
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return false;
        } catch (Throwable th) {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean writeFileFromBytesByMap(String filePath, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByMap(filePath, bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByMap(String filePath, byte[] bytes, boolean append, boolean isForce) {
        return writeFileFromBytesByMap(getFileByPath(filePath), bytes, append, isForce);
    }

    public static boolean writeFileFromBytesByMap(File file, byte[] bytes, boolean isForce) {
        return writeFileFromBytesByMap(file, bytes, false, isForce);
    }

    public static boolean writeFileFromBytesByMap(File file, byte[] bytes, boolean append, boolean isForce) {
        if (bytes == null || !createOrExistsFile(file)) {
            return false;
        }
        FileChannel fc = null;
        try {
            FileChannel fc2 = new FileOutputStream(file, append).getChannel();
            MappedByteBuffer mbb = fc2.map(FileChannel.MapMode.READ_WRITE, fc2.size(), (long) bytes.length);
            mbb.put(bytes);
            if (isForce) {
                mbb.force();
            }
            if (fc2 != null) {
                try {
                    fc2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e2) {
            e2.printStackTrace();
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return false;
        } catch (Throwable th) {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static boolean writeFileFromString(String filePath, String content) {
        return writeFileFromString(getFileByPath(filePath), content, false);
    }

    public static boolean writeFileFromString(String filePath, String content, boolean append) {
        return writeFileFromString(getFileByPath(filePath), content, append);
    }

    public static boolean writeFileFromString(File file, String content) {
        return writeFileFromString(file, content, false);
    }

    public static boolean writeFileFromString(File file, String content, boolean append) {
        if (file == null || content == null || !createOrExistsFile(file)) {
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } catch (IOException e2) {
            e2.printStackTrace();
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return false;
        } catch (Throwable th) {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static List<String> readFile2List(String filePath) {
        return readFile2List(getFileByPath(filePath), (String) null);
    }

    public static List<String> readFile2List(String filePath, String charsetName) {
        return readFile2List(getFileByPath(filePath), charsetName);
    }

    public static List<String> readFile2List(File file) {
        return readFile2List(file, 0, Integer.MAX_VALUE, (String) null);
    }

    public static List<String> readFile2List(File file, String charsetName) {
        return readFile2List(file, 0, Integer.MAX_VALUE, charsetName);
    }

    public static List<String> readFile2List(String filePath, int st, int end) {
        return readFile2List(getFileByPath(filePath), st, end, (String) null);
    }

    public static List<String> readFile2List(String filePath, int st, int end, String charsetName) {
        return readFile2List(getFileByPath(filePath), st, end, charsetName);
    }

    public static List<String> readFile2List(File file, int st, int end) {
        return readFile2List(file, st, end, (String) null);
    }

    public static List<String> readFile2List(File file, int st, int end, String charsetName) {
        BufferedReader reader;
        if (!isFileExists(file) || st > end) {
            return null;
        }
        BufferedReader reader2 = null;
        int curLine = 1;
        try {
            List<String> list = new ArrayList<>();
            if (isSpace(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine != null) {
                    if (curLine > end) {
                        break;
                    }
                    if (st <= curLine && curLine <= end) {
                        list.add(line);
                    }
                    curLine++;
                }
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        } catch (IOException e2) {
            e2.printStackTrace();
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static String readFile2String(String filePath) {
        return readFile2String(getFileByPath(filePath), (String) null);
    }

    public static String readFile2String(String filePath, String charsetName) {
        return readFile2String(getFileByPath(filePath), charsetName);
    }

    public static String readFile2String(File file) {
        return readFile2String(file, (String) null);
    }

    public static String readFile2String(File file, String charsetName) {
        byte[] bytes = readFile2BytesByStream(file);
        if (bytes == null) {
            return null;
        }
        if (isSpace(charsetName)) {
            return new String(bytes);
        }
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] readFile2BytesByStream(String filePath) {
        return readFile2BytesByStream(getFileByPath(filePath), (OnProgressUpdateListener) null);
    }

    public static byte[] readFile2BytesByStream(File file) {
        return readFile2BytesByStream(file, (OnProgressUpdateListener) null);
    }

    public static byte[] readFile2BytesByStream(String filePath, OnProgressUpdateListener listener) {
        return readFile2BytesByStream(getFileByPath(filePath));
    }

    public static byte[] readFile2BytesByStream(File file, OnProgressUpdateListener listener) {
        InputStream is;
        int len;
        if (!isFileExists(file)) {
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file), sBufferSize);
            try {
                ByteArrayOutputStream os2 = new ByteArrayOutputStream();
                byte[] b = new byte[sBufferSize];
                if (listener == null) {
                    while (true) {
                        int read = is.read(b, 0, sBufferSize);
                        int len2 = read;
                        if (read == -1) {
                            break;
                        }
                        os2.write(b, 0, len2);
                    }
                } else {
                    double totalSize = (double) is.available();
                    int curSize = 0;
                    listener.onProgressUpdate(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                    while (true) {
                        int read2 = is.read(b, 0, sBufferSize);
                        len = read2;
                        if (read2 == -1) {
                            break;
                        }
                        os2.write(b, 0, len);
                        curSize += len;
                        listener.onProgressUpdate(((double) curSize) / totalSize);
                    }
                    int i = len;
                }
                byte[] byteArray = os2.toByteArray();
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os2.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return byteArray;
            } catch (IOException e3) {
                e3.printStackTrace();
                try {
                    is.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
                return null;
            }
        } catch (FileNotFoundException e6) {
            e6.printStackTrace();
            return null;
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e7) {
                e7.printStackTrace();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static byte[] readFile2BytesByChannel(String filePath) {
        return readFile2BytesByChannel(getFileByPath(filePath));
    }

    public static byte[] readFile2BytesByChannel(File file) {
        if (!isFileExists(file)) {
            return null;
        }
        FileChannel fc = null;
        try {
            FileChannel fc2 = new RandomAccessFile(file, "r").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc2.size());
            do {
            } while (fc2.read(byteBuffer) > 0);
            byte[] array = byteBuffer.array();
            if (fc2 != null) {
                try {
                    fc2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return array;
        } catch (IOException e2) {
            e2.printStackTrace();
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static byte[] readFile2BytesByMap(String filePath) {
        return readFile2BytesByMap(getFileByPath(filePath));
    }

    public static byte[] readFile2BytesByMap(File file) {
        if (!isFileExists(file)) {
            return null;
        }
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            int size = (int) fc.size();
            byte[] result = new byte[size];
            fc.map(FileChannel.MapMode.READ_ONLY, 0, (long) size).load().get(result, 0, size);
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        } catch (IOException e2) {
            e2.printStackTrace();
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static void setBufferSize(int bufferSize) {
        sBufferSize = bufferSize;
    }

    private static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
    }

    private static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    private static boolean createOrExistsFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? file.mkdirs() : file.isDirectory());
    }

    private static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    private static boolean isSpace(String s) {
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

    private static byte[] is2Bytes(InputStream is) {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            ByteArrayOutputStream os2 = new ByteArrayOutputStream();
            byte[] b = new byte[sBufferSize];
            while (true) {
                int read = is.read(b, 0, sBufferSize);
                int len = read;
                if (read == -1) {
                    break;
                }
                os2.write(b, 0, len);
            }
            byte[] byteArray = os2.toByteArray();
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return byteArray;
        } catch (IOException e3) {
            e3.printStackTrace();
            try {
                is.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e6) {
                e6.printStackTrace();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
            throw th;
        }
    }
}
