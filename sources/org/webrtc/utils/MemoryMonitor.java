package org.webrtc.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.king.zxing.util.LogUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MemoryMonitor {
    private static final int CPU_STAT_SAMPLE_PERIOD_MS = 2000;
    private static final String TAG = MemoryMonitor.class.getSimpleName();
    private WeakReference<Context> contextWeakReference;
    private ScheduledExecutorService executor;
    private long free_memory;
    private long total_memory;
    private long userMemory;

    public MemoryMonitor(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

    public void pause() {
        if (this.executor != null) {
            Log.d(TAG, "pause");
            this.executor.shutdownNow();
            this.executor = null;
        }
    }

    public void resume() {
        Log.d(TAG, "resume");
        this.total_memory = 0;
        this.free_memory = 0;
        this.userMemory = 0;
        scheduleMemoryUtilizationTask();
    }

    private long getTotalMemory() {
        String memTotal = "";
        try {
            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            while (true) {
                String readLine = localBufferedReader.readLine();
                String readTemp = readLine;
                if (readLine == null) {
                    localBufferedReader.close();
                    String memTotal2 = memTotal.split(" ")[0].trim();
                    String str = TAG;
                    Log.d(str, "memTotal: " + memTotal2);
                    return Long.parseLong(memTotal2);
                } else if (readTemp.contains("MemTotal")) {
                    memTotal = readTemp.split(LogUtils.COLON)[1].trim();
                }
            }
        } catch (IOException e) {
            String str2 = TAG;
            Log.e(str2, "IOException: " + e.getMessage());
            return 0;
        }
    }

    private long getFreeMemorySize(Context context) {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(outInfo);
        return outInfo.availMem / 1024;
    }

    private int getUserMemorySize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
        int processid = 0;
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningAppProcessInfo info = pids.get(i);
            if (info.processName.equalsIgnoreCase("com.aliyun.sophon.demo")) {
                processid = info.pid;
            }
        }
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{processid});
        memoryInfo[0].getTotalSharedDirty();
        return memoryInfo[0].getTotalPss();
    }

    public synchronized String getMemoryUsageCurrent() {
        return "Memory\nTotal_Memory:" + this.total_memory + "\nFree_Memory" + this.free_memory + "\nUserMemoryByPid" + this.userMemory;
    }

    public synchronized long getMemoryUsageCurrentByPid() {
        return this.userMemory;
    }

    private void scheduleMemoryUtilizationTask() {
        ScheduledExecutorService scheduledExecutorService = this.executor;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            this.executor = null;
        }
        ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.executor = newSingleThreadScheduledExecutor;
        ScheduledFuture<?> scheduleAtFixedRate = newSingleThreadScheduledExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                MemoryMonitor.this.memoryUtilization();
            }
        }, 0, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, TimeUnit.MILLISECONDS);
    }

    /* access modifiers changed from: private */
    public void memoryUtilization() {
        this.total_memory = getTotalMemory();
        this.free_memory = getFreeMemorySize((Context) this.contextWeakReference.get());
        this.userMemory = (long) getUserMemorySize((Context) this.contextWeakReference.get());
    }
}
