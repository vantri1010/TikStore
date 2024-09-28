package im.bclpbkiauv.ui.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Log;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.io.File;

public class DownloadUtils {
    private static volatile DownloadUtils instance;
    private Context mContext;
    private DownloadListener mDownLoadListener;
    private long mDownloadId;
    private DownloadManager mDownloadManager;
    private BroadcastReceiver mDownloadReceiver;
    private String mDownloadUrl;
    private String mFileName;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public boolean mIsDownloading;
    /* access modifiers changed from: private */
    public final Runnable mQueryProgressRunnable = new Runnable() {
        public void run() {
            DownloadUtils.this.queryProgress();
            if (DownloadUtils.this.mIsDownloading) {
                DownloadUtils.this.mHandler.postDelayed(DownloadUtils.this.mQueryProgressRunnable, 1000);
            }
        }
    };

    public interface DownloadListener {
        void onFailed();

        void onFinish(String str, long j);

        void onProgress(int i, long j, long j2);

        void onStart();
    }

    public static DownloadUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadUtils(context);
            instance.registerReceiver();
        }
        return instance;
    }

    private DownloadUtils(Context context) {
        this.mContext = context;
    }

    private void registerReceiver() {
        this.mDownloadReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED".equals(intent.getAction())) {
                    DownloadUtils.this.showDownloadList();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DOWNLOAD_COMPLETE");
        intentFilter.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED");
        this.mContext.registerReceiver(this.mDownloadReceiver, intentFilter);
    }

    public void startDownload(String downloadUrl, String version) {
        setDownloadUrl(downloadUrl);
        if (this.mFileName == null) {
            if (version != null) {
                this.mFileName = LocaleController.getString(R.string.AppName) + "_v" + version + ".apk";
            } else {
                this.mFileName = LocaleController.getString(R.string.AppName) + ".apk";
            }
        }
        startDownload();
    }

    private void startDownload() {
        ToastUtils.show((int) R.string.StartDownload);
        DownloadListener downloadListener = this.mDownLoadListener;
        if (downloadListener != null) {
            downloadListener.onStart();
        }
        this.mDownloadManager = (DownloadManager) this.mContext.getSystemService("download");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(this.mDownloadUrl));
        request.setAllowedNetworkTypes(3);
        request.setNotificationVisibility(1);
        request.setTitle(LocaleController.getString(R.string.AppName));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, this.mFileName);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        try {
            long enqueue = this.mDownloadManager.enqueue(request);
            this.mDownloadId = enqueue;
            if (enqueue != 0) {
                startQueryProgress();
            }
        } catch (IllegalArgumentException e) {
            DownloadListener downloadListener2 = this.mDownLoadListener;
            if (downloadListener2 != null) {
                downloadListener2.onFailed();
            }
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:com.android.providers.downloads"));
            if (intent.resolveActivity(this.mContext.getPackageManager()) != null) {
                this.mContext.startActivity(intent);
            }
        }
    }

    public void showDownloadList() {
        Intent downloadIntent = new Intent("android.intent.action.VIEW_DOWNLOADS");
        if (downloadIntent.resolveActivity(this.mContext.getPackageManager()) != null) {
            this.mContext.startActivity(downloadIntent);
        }
    }

    /* access modifiers changed from: private */
    public void queryProgress() {
        DownloadListener downloadListener;
        if (this.mIsDownloading) {
            Cursor c = this.mDownloadManager.query(new DownloadManager.Query().setFilterById(new long[]{this.mDownloadId}));
            if (c == null || !c.moveToFirst()) {
                stopQueryProgress();
                DownloadListener downloadListener2 = this.mDownLoadListener;
                if (downloadListener2 != null) {
                    downloadListener2.onFailed();
                }
            } else {
                int status = c.getInt(c.getColumnIndex(NotificationCompat.CATEGORY_STATUS));
                logDebug("下载状态：" + status);
                if (status == 2) {
                    long soFarSize = c.getLong(c.getColumnIndex("bytes_so_far"));
                    long totalSize = c.getLong(c.getColumnIndex("total_size"));
                    if (totalSize > 0) {
                        float percent = (((float) soFarSize) * 1.0f) / ((float) totalSize);
                        logDebug(String.format("total:%s kb soFar:%s kb", new Object[]{Long.valueOf(totalSize / 1024), Long.valueOf(soFarSize / 1024)}) + percent);
                        DownloadListener downloadListener3 = this.mDownLoadListener;
                        if (downloadListener3 != null) {
                            downloadListener3.onProgress((int) (100.0f * percent), soFarSize / 1024, totalSize / 1024);
                        }
                    }
                } else if (status == 8) {
                    stopQueryProgress();
                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    long totalSize2 = c.getLong(c.getColumnIndex("total_size"));
                    if (this.mDownLoadListener != null) {
                        String fullName = downloadDir.getPath() + File.separator + this.mFileName;
                        logDebug(fullName);
                        this.mDownLoadListener.onFinish(fullName, totalSize2);
                    }
                    Uri downloadUri = this.mDownloadManager.getUriForDownloadedFile(this.mDownloadId);
                    if (downloadUri != null) {
                        installApp(downloadUri);
                    }
                    onDestroy();
                } else if (status == 16 && (downloadListener = this.mDownLoadListener) != null) {
                    downloadListener.onFailed();
                }
            }
            closeCursor(c);
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    private void logDebug(String msg) {
        if (BuildVars.DEBUG_VERSION) {
            Log.d(getClass().getName(), msg);
        }
    }

    public void removeDownload() {
        this.mDownloadManager.remove(new long[]{this.mDownloadId});
        stopQueryProgress();
    }

    private void startQueryProgress() {
        logDebug("startQueryProgress");
        this.mIsDownloading = true;
        this.mHandler.post(this.mQueryProgressRunnable);
    }

    private void stopQueryProgress() {
        logDebug("stopQueryProgress");
        this.mIsDownloading = false;
        this.mHandler.removeCallbacks(this.mQueryProgressRunnable);
    }

    public boolean isDownloading() {
        return this.mIsDownloading;
    }

    public void onDestroy() {
        try {
            stopQueryProgress();
            this.mContext.unregisterReceiver(this.mDownloadReceiver);
            this.mContext = null;
            logDebug("onDestroy");
            instance = null;
        } catch (Exception e) {
            FileLog.e(getClass().getName() + " ---> " + e.getMessage());
        }
    }

    private void installApp(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(C.ENCODING_PCM_MU_LAW);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(1);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        logDebug("installApp uri = " + uri.toString());
        this.mContext.startActivity(intent);
    }

    public DownloadUtils setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
        return this;
    }

    public DownloadUtils setFileName(String fileName) {
        this.mFileName = fileName;
        return this;
    }

    public DownloadUtils setDownloadListener(DownloadListener listener) {
        this.mDownLoadListener = listener;
        return this;
    }
}
