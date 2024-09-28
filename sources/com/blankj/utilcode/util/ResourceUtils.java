package com.blankj.utilcode.util;

import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public final class ResourceUtils {
    private static final int BUFFER_SIZE = 8192;

    private ResourceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Drawable getDrawable(int id) {
        return ContextCompat.getDrawable(Utils.getApp(), id);
    }

    public static int getIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, TtmlNode.ATTR_ID, Utils.getApp().getPackageName());
    }

    public static int getStringIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "string", Utils.getApp().getPackageName());
    }

    public static int getColorIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, TtmlNode.ATTR_TTS_COLOR, Utils.getApp().getPackageName());
    }

    public static int getDimenIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "dimen", Utils.getApp().getPackageName());
    }

    public static int getDrawableIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "drawable", Utils.getApp().getPackageName());
    }

    public static int getMipmapIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "mipmap", Utils.getApp().getPackageName());
    }

    public static int getLayoutIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, TtmlNode.TAG_LAYOUT, Utils.getApp().getPackageName());
    }

    public static int getStyleIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, TtmlNode.TAG_STYLE, Utils.getApp().getPackageName());
    }

    public static int getAnimIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "anim", Utils.getApp().getPackageName());
    }

    public static int getMenuIdByName(String name) {
        return Utils.getApp().getResources().getIdentifier(name, "menu", Utils.getApp().getPackageName());
    }

    public static boolean copyFileFromAssets(String assetsFilePath, String destFilePath) {
        boolean res = true;
        try {
            String[] assets = Utils.getApp().getAssets().list(assetsFilePath);
            if (assets == null || assets.length <= 0) {
                return writeFileFromIS(destFilePath, Utils.getApp().getAssets().open(assetsFilePath), false);
            }
            for (String asset : assets) {
                res &= copyFileFromAssets(assetsFilePath + "/" + asset, destFilePath + "/" + asset);
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readAssets2String(String assetsFilePath) {
        return readAssets2String(assetsFilePath, (String) null);
    }

    public static String readAssets2String(String assetsFilePath, String charsetName) {
        try {
            byte[] bytes = is2Bytes(Utils.getApp().getAssets().open(assetsFilePath));
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
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static List<String> readAssets2List(String assetsPath) {
        return readAssets2List(assetsPath, (String) null);
    }

    public static List<String> readAssets2List(String assetsPath, String charsetName) {
        try {
            return is2List(Utils.getApp().getResources().getAssets().open(assetsPath), charsetName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean copyFileFromRaw(int resId, String destFilePath) {
        return writeFileFromIS(destFilePath, Utils.getApp().getResources().openRawResource(resId), false);
    }

    public static String readRaw2String(int resId) {
        return readRaw2String(resId, (String) null);
    }

    public static String readRaw2String(int resId, String charsetName) {
        byte[] bytes = is2Bytes(Utils.getApp().getResources().openRawResource(resId));
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

    public static List<String> readRaw2List(int resId) {
        return readRaw2List(resId, (String) null);
    }

    public static List<String> readRaw2List(int resId, String charsetName) {
        return is2List(Utils.getApp().getResources().openRawResource(resId), charsetName);
    }

    private static boolean writeFileFromIS(String filePath, InputStream is, boolean append) {
        return writeFileFromIS(getFileByPath(filePath), is, append);
    }

    private static boolean writeFileFromIS(File file, InputStream is, boolean append) {
        if (!createOrExistsFile(file) || is == null) {
            return false;
        }
        OutputStream os = null;
        try {
            OutputStream os2 = new BufferedOutputStream(new FileOutputStream(file, append));
            byte[] data = new byte[8192];
            while (true) {
                int read = is.read(data, 0, 8192);
                int len = read;
                if (read == -1) {
                    break;
                }
                os2.write(data, 0, len);
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

    private static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
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
            byte[] b = new byte[8192];
            while (true) {
                int read = is.read(b, 0, 8192);
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

    private static List<String> is2List(InputStream is, String charsetName) {
        BufferedReader reader;
        BufferedReader reader2 = null;
        try {
            List<String> list = new ArrayList<>();
            if (isSpace(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(is));
            } else {
                reader = new BufferedReader(new InputStreamReader(is, charsetName));
            }
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine != null) {
                    list.add(line);
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            reader.close();
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
}
