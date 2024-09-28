package im.bclpbkiauv.ui.utils.translate.utils;

import android.text.TextUtils;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioFileUtils {
    public static final String SAVE_AUDIO_FOLDER = "/audio_cache";

    public static String getAudioEditStorageDirectory() {
        return ApplicationLoader.applicationContext.getCacheDir().getAbsolutePath() + SAVE_AUDIO_FOLDER;
    }

    public static boolean confirmFolderExist(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return false;
    }

    public static void copyFile(String srcPath, String destPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (!TextUtils.isEmpty(srcPath) && !TextUtils.isEmpty(destPath)) {
                if (!TextUtils.equals(srcPath, destPath)) {
                    FileLog.e("-----------" + new File(destPath).exists());
                    File destFile = new File(destPath);
                    if (!destFile.getParentFile().exists()) {
                        FileLog.e("-----------" + new File(destPath).exists());
                        destFile.getParentFile().mkdirs();
                    }
                    new File(destPath).delete();
                    String tempPath = srcPath + ".temp";
                    if (new File(srcPath).exists()) {
                        fis = new FileInputStream(srcPath);
                        fos = new FileOutputStream(tempPath);
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int read = fis.read(buffer);
                            int length = read;
                            if (read == -1) {
                                break;
                            }
                            fos.write(buffer, 0, length);
                        }
                    }
                    fos.close();
                    fis.close();
                    FileLog.e("-----------" + new File(tempPath).renameTo(new File(destPath)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        File[] files;
        if (file != null && file.exists()) {
            if (file.isDirectory() && (files = file.listFiles()) != null) {
                for (File childFile : files) {
                    deleteFile(childFile);
                }
            }
            deleteFileSafely(file);
        }
    }

    public static boolean checkFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return new File(filePath).exists();
    }

    public static boolean deleteFileSafely(File file) {
        if (file == null) {
            return false;
        }
        File tmp = new File(file.getParent() + File.separator + System.currentTimeMillis());
        file.renameTo(tmp);
        return tmp.delete();
    }

    public static void saveFile(String url, String content) {
        saveFile(url, content, true, false);
    }

    public static void saveFile(String url, String content, boolean cover, boolean append) {
        FileOutputStream out = null;
        File file = new File(url);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else if (cover) {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream out2 = new FileOutputStream(file, append);
            out2.write(content.getBytes());
            out2.close();
            FileLog.e("保存文件" + url + "保存文件成功");
        } catch (Exception e) {
            FileLog.e("保存文件" + url, (Throwable) e);
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static BufferedOutputStream getBufferedOutputStreamFromFile(String fileUrl) {
        try {
            File file = new File(fileUrl);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            return new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            FileLog.e("GetBufferedOutputStreamFromFile异常", (Throwable) e);
            return null;
        }
    }

    public static void renameFile(String oldPath, String newPath) {
        if (!TextUtils.isEmpty(oldPath) && !TextUtils.isEmpty(newPath)) {
            File newFile = new File(newPath);
            if (newFile.exists()) {
                newFile.delete();
            }
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                try {
                    oldFile.renameTo(new File(newPath));
                } catch (Exception e) {
                    FileLog.e("删除本地文件失败", (Throwable) e);
                }
            }
        }
    }
}
