package com.socks.library.klog;

import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class FileLog {
    public static void printFile(String tag, File targetDirectory, String fileName, String headString, String msg) {
        String fileName2 = fileName == null ? getFileName() : fileName;
        if (save(targetDirectory, fileName2, msg)) {
            Log.d(tag, headString + " save log success ! location is >>>" + targetDirectory.getAbsolutePath() + "/" + fileName2);
            return;
        }
        Log.e(tag, headString + "save log fails !");
    }

    private static boolean save(File dic, String fileName, String msg) {
        try {
            OutputStream outputStream = new FileOutputStream(new File(dic, fileName));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            outputStreamWriter.write(msg);
            outputStreamWriter.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
            return false;
        } catch (IOException e3) {
            e3.printStackTrace();
            return false;
        } catch (Exception e4) {
            e4.printStackTrace();
            return false;
        }
    }

    private static String getFileName() {
        Random random = new Random();
        return "KLog_" + Long.toString(System.currentTimeMillis() + ((long) random.nextInt(10000))).substring(4) + ".txt";
    }
}
