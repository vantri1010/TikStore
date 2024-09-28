package com.blankj.utilcode.util;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class IntentUtils {
    private IntentUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isIntentAvailable(Intent intent) {
        return Utils.getApp().getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
    }

    public static Intent getInstallAppIntent(String filePath) {
        return getInstallAppIntent(getFileByPath(filePath), false);
    }

    public static Intent getInstallAppIntent(File file) {
        return getInstallAppIntent(file, false);
    }

    public static Intent getInstallAppIntent(String filePath, boolean isNewTask) {
        return getInstallAppIntent(getFileByPath(filePath), isNewTask);
    }

    public static Intent getInstallAppIntent(File file, boolean isNewTask) {
        Uri data;
        if (file == null) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT < 24) {
            data = Uri.fromFile(file);
        } else {
            intent.setFlags(1);
            data = FileProvider.getUriForFile(Utils.getApp(), Utils.getApp().getPackageName() + ".utilcode.provider", file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        return getIntent(intent, isNewTask);
    }

    public static Intent getUninstallAppIntent(String packageName) {
        return getUninstallAppIntent(packageName, false);
    }

    public static Intent getUninstallAppIntent(String packageName, boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.DELETE");
        intent.setData(Uri.parse("package:" + packageName));
        return getIntent(intent, isNewTask);
    }

    public static Intent getLaunchAppIntent(String packageName) {
        return getLaunchAppIntent(packageName, false);
    }

    public static Intent getLaunchAppIntent(String packageName, boolean isNewTask) {
        Intent intent = Utils.getApp().getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return null;
        }
        return getIntent(intent, isNewTask);
    }

    public static Intent getLaunchAppDetailsSettingsIntent(String packageName) {
        return getLaunchAppDetailsSettingsIntent(packageName, false);
    }

    public static Intent getLaunchAppDetailsSettingsIntent(String packageName, boolean isNewTask) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return getIntent(intent, isNewTask);
    }

    public static Intent getShareTextIntent(String content) {
        return getShareTextIntent(content, false);
    }

    public static Intent getShareTextIntent(String content, boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", content);
        return getIntent(intent, isNewTask);
    }

    public static Intent getShareImageIntent(String content, String imagePath) {
        return getShareImageIntent(content, imagePath, false);
    }

    public static Intent getShareImageIntent(String content, String imagePath, boolean isNewTask) {
        if (imagePath == null || imagePath.length() == 0) {
            return null;
        }
        return getShareImageIntent(content, new File(imagePath), isNewTask);
    }

    public static Intent getShareImageIntent(String content, File image) {
        return getShareImageIntent(content, image, false);
    }

    public static Intent getShareImageIntent(String content, File image, boolean isNewTask) {
        if (image == null || !image.isFile()) {
            return null;
        }
        return getShareImageIntent(content, file2Uri(image), isNewTask);
    }

    public static Intent getShareImageIntent(String content, Uri uri) {
        return getShareImageIntent(content, uri, false);
    }

    public static Intent getShareImageIntent(String content, Uri uri, boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", content);
        intent.putExtra("android.intent.extra.STREAM", uri);
        intent.setType("image/*");
        return getIntent(intent, isNewTask);
    }

    public static Intent getShareImageIntent(String content, LinkedList<String> imagePaths) {
        return getShareImageIntent(content, imagePaths, false);
    }

    public static Intent getShareImageIntent(String content, LinkedList<String> imagePaths, boolean isNewTask) {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return null;
        }
        List<File> files = new ArrayList<>();
        Iterator it = imagePaths.iterator();
        while (it.hasNext()) {
            files.add(new File((String) it.next()));
        }
        return getShareImageIntent(content, files, isNewTask);
    }

    public static Intent getShareImageIntent(String content, List<File> images) {
        return getShareImageIntent(content, images, false);
    }

    public static Intent getShareImageIntent(String content, List<File> images, boolean isNewTask) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        ArrayList<Uri> uris = new ArrayList<>();
        for (File image : images) {
            if (image.isFile()) {
                uris.add(file2Uri(image));
            }
        }
        return getShareImageIntent(content, uris, isNewTask);
    }

    public static Intent getShareImageIntent(String content, ArrayList<Uri> uris) {
        return getShareImageIntent(content, uris, false);
    }

    public static Intent getShareImageIntent(String content, ArrayList<Uri> uris, boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.putExtra("android.intent.extra.TEXT", content);
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", uris);
        intent.setType("image/*");
        return getIntent(intent, isNewTask);
    }

    public static Intent getComponentIntent(String packageName, String className) {
        return getComponentIntent(packageName, className, (Bundle) null, false);
    }

    public static Intent getComponentIntent(String packageName, String className, boolean isNewTask) {
        return getComponentIntent(packageName, className, (Bundle) null, isNewTask);
    }

    public static Intent getComponentIntent(String packageName, String className, Bundle bundle) {
        return getComponentIntent(packageName, className, bundle, false);
    }

    public static Intent getComponentIntent(String packageName, String className, Bundle bundle, boolean isNewTask) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setComponent(new ComponentName(packageName, className));
        return getIntent(intent, isNewTask);
    }

    public static Intent getShutdownIntent() {
        return getShutdownIntent(false);
    }

    public static Intent getShutdownIntent(boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        return getIntent(intent, isNewTask);
    }

    public static Intent getDialIntent(String phoneNumber) {
        return getDialIntent(phoneNumber, false);
    }

    public static Intent getDialIntent(String phoneNumber, boolean isNewTask) {
        return getIntent(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phoneNumber)), isNewTask);
    }

    public static Intent getCallIntent(String phoneNumber) {
        return getCallIntent(phoneNumber, false);
    }

    public static Intent getCallIntent(String phoneNumber, boolean isNewTask) {
        return getIntent(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)), isNewTask);
    }

    public static Intent getSendSmsIntent(String phoneNumber, String content) {
        return getSendSmsIntent(phoneNumber, content, false);
    }

    public static Intent getSendSmsIntent(String phoneNumber, String content, boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", content);
        return getIntent(intent, isNewTask);
    }

    public static Intent getCaptureIntent(Uri outUri) {
        return getCaptureIntent(outUri, false);
    }

    public static Intent getCaptureIntent(Uri outUri, boolean isNewTask) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", outUri);
        intent.addFlags(1);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(Intent intent, boolean isNewTask) {
        return isNewTask ? intent.addFlags(C.ENCODING_PCM_MU_LAW) : intent;
    }

    private static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
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

    private static Uri file2Uri(File file) {
        if (file == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT < 24) {
            return Uri.fromFile(file);
        }
        return FileProvider.getUriForFile(Utils.getApp(), Utils.getApp().getPackageName() + ".utilcode.provider", file);
    }
}
