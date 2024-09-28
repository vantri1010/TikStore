package org.webrtc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CpuMonitor {
    private static final int CPU_STAT_LOG_PERIOD_MS = 6000;
    private static final int CPU_STAT_SAMPLE_PERIOD_MS = 2000;
    private static final int MOVING_AVERAGE_SAMPLES = 5;
    private static final String TAG = "CpuMonitor";
    private int actualCpusPresent;
    private final Context appContext;
    private long[] cpuFreqMax;
    private boolean cpuOveruse;
    private int cpusPresent;
    private double[] curFreqScales;
    private String[] curPath;
    private ScheduledExecutorService executor;
    private final MovingAverage frequencyScale = new MovingAverage(5);
    private boolean initialized;
    private ProcStat lastProcStat;
    private long lastStatLogTimeMs = SystemClock.elapsedRealtime();
    private String[] maxPath;
    private final MovingAverage systemCpuUsage = new MovingAverage(5);
    private final MovingAverage totalCpuUsage = new MovingAverage(5);
    private final MovingAverage userCpuUsage = new MovingAverage(5);

    private static class ProcStat {
        final long idleTime;
        final long systemTime;
        final long userTime;

        ProcStat(long userTime2, long systemTime2, long idleTime2) {
            this.userTime = userTime2;
            this.systemTime = systemTime2;
            this.idleTime = idleTime2;
        }
    }

    private static class MovingAverage {
        private double[] circBuffer;
        private int circBufferIndex;
        private double currentValue;
        private final int size;
        private double sum;

        public MovingAverage(int size2) {
            if (size2 > 0) {
                this.size = size2;
                this.circBuffer = new double[size2];
                return;
            }
            throw new AssertionError("Size value in MovingAverage ctor should be positive.");
        }

        public void reset() {
            Arrays.fill(this.circBuffer, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            this.circBufferIndex = 0;
            this.sum = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            this.currentValue = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }

        public void addValue(double value) {
            double d = this.sum;
            double[] dArr = this.circBuffer;
            int i = this.circBufferIndex;
            double d2 = d - dArr[i];
            this.sum = d2;
            int i2 = i + 1;
            this.circBufferIndex = i2;
            dArr[i] = value;
            this.currentValue = value;
            this.sum = d2 + value;
            if (i2 >= this.size) {
                this.circBufferIndex = 0;
            }
        }

        public double getCurrent() {
            return this.currentValue;
        }

        public double getAverage() {
            return this.sum / ((double) this.size);
        }
    }

    public CpuMonitor(Context context) {
        Log.d(TAG, "CpuMonitor ctor.");
        this.appContext = context.getApplicationContext();
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
        resetStat();
        scheduleCpuUtilizationTask();
    }

    public synchronized void reset() {
        if (this.executor != null) {
            Log.d(TAG, "reset");
            resetStat();
            this.cpuOveruse = false;
        }
    }

    public synchronized int getCpuUsageCurrent() {
        return doubleToPercent(this.userCpuUsage.getCurrent() + this.systemCpuUsage.getCurrent());
    }

    public synchronized int getCpuUsageAverage() {
        return doubleToPercent(this.userCpuUsage.getAverage() + this.systemCpuUsage.getAverage());
    }

    public synchronized int getFrequencyScaleAverage() {
        return doubleToPercent(this.frequencyScale.getAverage());
    }

    private void scheduleCpuUtilizationTask() {
        ScheduledExecutorService scheduledExecutorService = this.executor;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            this.executor = null;
        }
        ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.executor = newSingleThreadScheduledExecutor;
        ScheduledFuture<?> scheduleAtFixedRate = newSingleThreadScheduledExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                CpuMonitor.this.cpuUtilizationTask();
            }
        }, 0, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, TimeUnit.MILLISECONDS);
    }

    /* access modifiers changed from: private */
    public void cpuUtilizationTask() {
        if (sampleCpuUtilization() && SystemClock.elapsedRealtime() - this.lastStatLogTimeMs >= 6000) {
            this.lastStatLogTimeMs = SystemClock.elapsedRealtime();
            Log.d(TAG, getStatString());
        }
    }

    private void init() {
        FileReader fin;
        try {
            fin = new FileReader("/sys/devices/system/cpu/present");
            try {
                Scanner scanner = new Scanner(new BufferedReader(fin)).useDelimiter("[-\n]");
                scanner.nextInt();
                this.cpusPresent = scanner.nextInt() + 1;
                scanner.close();
                fin.close();
            } catch (Exception e) {
                Log.e(TAG, "Cannot do CPU stats due to /sys/devices/system/cpu/present parsing problem");
                fin.close();
            }
        } catch (FileNotFoundException e2) {
            Log.e(TAG, "Cannot do CPU stats since /sys/devices/system/cpu/present is missing");
        } catch (IOException e3) {
            Log.e(TAG, "Error closing file");
        } catch (Throwable th) {
            fin.close();
            throw th;
        }
        int i = this.cpusPresent;
        this.cpuFreqMax = new long[i];
        this.maxPath = new String[i];
        this.curPath = new String[i];
        this.curFreqScales = new double[i];
        for (int i2 = 0; i2 < this.cpusPresent; i2++) {
            this.cpuFreqMax[i2] = 0;
            this.curFreqScales[i2] = 0.0d;
            String[] strArr = this.maxPath;
            strArr[i2] = "/sys/devices/system/cpu/cpu" + i2 + "/cpufreq/cpuinfo_max_freq";
            String[] strArr2 = this.curPath;
            strArr2[i2] = "/sys/devices/system/cpu/cpu" + i2 + "/cpufreq/scaling_cur_freq";
        }
        this.lastProcStat = new ProcStat(0, 0, 0);
        resetStat();
        this.initialized = true;
    }

    private synchronized void resetStat() {
        this.userCpuUsage.reset();
        this.systemCpuUsage.reset();
        this.totalCpuUsage.reset();
        this.frequencyScale.reset();
        this.lastStatLogTimeMs = SystemClock.elapsedRealtime();
    }

    private int getBatteryLevel() {
        Intent intent = this.appContext.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int batteryScale = intent.getIntExtra("scale", 100);
        if (batteryScale > 0) {
            return (int) ((((float) intent.getIntExtra("level", 0)) * 100.0f) / ((float) batteryScale));
        }
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0126, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean sampleCpuUtilization() {
        /*
            r26 = this;
            r1 = r26
            monitor-enter(r26)
            r2 = 0
            r4 = 0
            r6 = 0
            boolean r0 = r1.initialized     // Catch:{ all -> 0x0138 }
            if (r0 != 0) goto L_0x0010
            r26.init()     // Catch:{ all -> 0x0138 }
        L_0x0010:
            int r0 = r1.cpusPresent     // Catch:{ all -> 0x0138 }
            r8 = 0
            if (r0 != 0) goto L_0x0017
            monitor-exit(r26)
            return r8
        L_0x0017:
            r1.actualCpusPresent = r8     // Catch:{ all -> 0x0138 }
            r0 = 0
        L_0x001a:
            int r9 = r1.cpusPresent     // Catch:{ all -> 0x0138 }
            r10 = 1
            r11 = 0
            r13 = 0
            if (r0 >= r9) goto L_0x0094
            double[] r9 = r1.curFreqScales     // Catch:{ all -> 0x0138 }
            r9[r0] = r11     // Catch:{ all -> 0x0138 }
            long[] r9 = r1.cpuFreqMax     // Catch:{ all -> 0x0138 }
            r11 = r9[r0]     // Catch:{ all -> 0x0138 }
            int r9 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r9 != 0) goto L_0x0064
            java.lang.String[] r9 = r1.maxPath     // Catch:{ all -> 0x0138 }
            r9 = r9[r0]     // Catch:{ all -> 0x0138 }
            long r11 = r1.readFreqFromFile(r9)     // Catch:{ all -> 0x0138 }
            int r9 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r9 <= 0) goto L_0x0063
            java.lang.String r9 = "CpuMonitor"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0138 }
            r15.<init>()     // Catch:{ all -> 0x0138 }
            java.lang.String r8 = "Core "
            r15.append(r8)     // Catch:{ all -> 0x0138 }
            r15.append(r0)     // Catch:{ all -> 0x0138 }
            java.lang.String r8 = ". Max frequency: "
            r15.append(r8)     // Catch:{ all -> 0x0138 }
            r15.append(r11)     // Catch:{ all -> 0x0138 }
            java.lang.String r8 = r15.toString()     // Catch:{ all -> 0x0138 }
            android.util.Log.d(r9, r8)     // Catch:{ all -> 0x0138 }
            r2 = r11
            long[] r8 = r1.cpuFreqMax     // Catch:{ all -> 0x0138 }
            r8[r0] = r11     // Catch:{ all -> 0x0138 }
            java.lang.String[] r8 = r1.maxPath     // Catch:{ all -> 0x0138 }
            r9 = 0
            r8[r0] = r9     // Catch:{ all -> 0x0138 }
        L_0x0063:
            goto L_0x0069
        L_0x0064:
            long[] r8 = r1.cpuFreqMax     // Catch:{ all -> 0x0138 }
            r11 = r8[r0]     // Catch:{ all -> 0x0138 }
            r2 = r11
        L_0x0069:
            java.lang.String[] r8 = r1.curPath     // Catch:{ all -> 0x0138 }
            r8 = r8[r0]     // Catch:{ all -> 0x0138 }
            long r8 = r1.readFreqFromFile(r8)     // Catch:{ all -> 0x0138 }
            int r11 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r11 != 0) goto L_0x007a
            int r11 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r11 != 0) goto L_0x007a
            goto L_0x0090
        L_0x007a:
            int r11 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r11 <= 0) goto L_0x0083
            int r11 = r1.actualCpusPresent     // Catch:{ all -> 0x0138 }
            int r11 = r11 + r10
            r1.actualCpusPresent = r11     // Catch:{ all -> 0x0138 }
        L_0x0083:
            long r4 = r4 + r8
            long r6 = r6 + r2
            int r10 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r10 <= 0) goto L_0x0090
            double[] r10 = r1.curFreqScales     // Catch:{ all -> 0x0138 }
            double r11 = (double) r8     // Catch:{ all -> 0x0138 }
            double r13 = (double) r2     // Catch:{ all -> 0x0138 }
            double r11 = r11 / r13
            r10[r0] = r11     // Catch:{ all -> 0x0138 }
        L_0x0090:
            int r0 = r0 + 1
            r8 = 0
            goto L_0x001a
        L_0x0094:
            int r0 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x0128
            int r0 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x00a4
            r16 = r2
            r18 = r4
            r22 = r6
            goto L_0x012e
        L_0x00a4:
            double r8 = (double) r4     // Catch:{ all -> 0x0138 }
            double r13 = (double) r6     // Catch:{ all -> 0x0138 }
            double r8 = r8 / r13
            org.webrtc.utils.CpuMonitor$MovingAverage r0 = r1.frequencyScale     // Catch:{ all -> 0x0138 }
            double r13 = r0.getCurrent()     // Catch:{ all -> 0x0138 }
            int r0 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r0 <= 0) goto L_0x00bc
            org.webrtc.utils.CpuMonitor$MovingAverage r0 = r1.frequencyScale     // Catch:{ all -> 0x0138 }
            double r13 = r0.getCurrent()     // Catch:{ all -> 0x0138 }
            double r13 = r13 + r8
            r18 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            double r8 = r13 * r18
        L_0x00bc:
            org.webrtc.utils.CpuMonitor$ProcStat r0 = r26.readProcStat()     // Catch:{ all -> 0x0138 }
            if (r0 != 0) goto L_0x00c5
            monitor-exit(r26)
            r10 = 0
            return r10
        L_0x00c5:
            long r13 = r0.userTime     // Catch:{ all -> 0x0138 }
            org.webrtc.utils.CpuMonitor$ProcStat r15 = r1.lastProcStat     // Catch:{ all -> 0x0138 }
            long r10 = r15.userTime     // Catch:{ all -> 0x0138 }
            long r13 = r13 - r10
            long r10 = r0.systemTime     // Catch:{ all -> 0x0138 }
            org.webrtc.utils.CpuMonitor$ProcStat r15 = r1.lastProcStat     // Catch:{ all -> 0x0138 }
            r20 = r13
            long r12 = r15.systemTime     // Catch:{ all -> 0x0138 }
            long r10 = r10 - r12
            long r12 = r0.idleTime     // Catch:{ all -> 0x0138 }
            org.webrtc.utils.CpuMonitor$ProcStat r14 = r1.lastProcStat     // Catch:{ all -> 0x0138 }
            long r14 = r14.idleTime     // Catch:{ all -> 0x0138 }
            long r12 = r12 - r14
            long r14 = r20 + r10
            long r14 = r14 + r12
            r18 = 0
            int r22 = (r8 > r18 ? 1 : (r8 == r18 ? 0 : -1))
            if (r22 == 0) goto L_0x011f
            r16 = 0
            int r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x00f2
            r16 = r2
            r18 = r4
            r22 = r6
            goto L_0x0125
        L_0x00f2:
            r16 = r2
            org.webrtc.utils.CpuMonitor$MovingAverage r2 = r1.frequencyScale     // Catch:{ all -> 0x0138 }
            r2.addValue(r8)     // Catch:{ all -> 0x0138 }
            r18 = r4
            r2 = r20
            double r4 = (double) r2     // Catch:{ all -> 0x0138 }
            double r2 = (double) r14     // Catch:{ all -> 0x0138 }
            double r4 = r4 / r2
            org.webrtc.utils.CpuMonitor$MovingAverage r2 = r1.userCpuUsage     // Catch:{ all -> 0x0138 }
            r2.addValue(r4)     // Catch:{ all -> 0x0138 }
            double r2 = (double) r10     // Catch:{ all -> 0x0138 }
            r22 = r6
            double r6 = (double) r14     // Catch:{ all -> 0x0138 }
            double r2 = r2 / r6
            org.webrtc.utils.CpuMonitor$MovingAverage r6 = r1.systemCpuUsage     // Catch:{ all -> 0x0138 }
            r6.addValue(r2)     // Catch:{ all -> 0x0138 }
            double r6 = r4 + r2
            double r6 = r6 * r8
            r24 = r2
            org.webrtc.utils.CpuMonitor$MovingAverage r2 = r1.totalCpuUsage     // Catch:{ all -> 0x0138 }
            r2.addValue(r6)     // Catch:{ all -> 0x0138 }
            r1.lastProcStat = r0     // Catch:{ all -> 0x0138 }
            monitor-exit(r26)
            r2 = 1
            return r2
        L_0x011f:
            r16 = r2
            r18 = r4
            r22 = r6
        L_0x0125:
            monitor-exit(r26)
            r2 = 0
            return r2
        L_0x0128:
            r16 = r2
            r18 = r4
            r22 = r6
        L_0x012e:
            java.lang.String r0 = "CpuMonitor"
            java.lang.String r2 = "Could not read max or current frequency for any CPU"
            android.util.Log.e(r0, r2)     // Catch:{ all -> 0x0138 }
            monitor-exit(r26)
            r0 = 0
            return r0
        L_0x0138:
            r0 = move-exception
            monitor-exit(r26)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.utils.CpuMonitor.sampleCpuUtilization():boolean");
    }

    private int doubleToPercent(double d) {
        return (int) ((100.0d * d) + 0.5d);
    }

    public synchronized String getStatString() {
        return "CPU \nUser: " + doubleToPercent(this.userCpuUsage.getCurrent()) + "/" + doubleToPercent(this.userCpuUsage.getAverage()) + "\nSystem: " + doubleToPercent(this.systemCpuUsage.getCurrent()) + "/" + doubleToPercent(this.systemCpuUsage.getAverage()) + "\nFreq: " + doubleToPercent(this.frequencyScale.getCurrent()) + "/" + doubleToPercent(this.frequencyScale.getAverage()) + "\nTotal usage: " + doubleToPercent(this.totalCpuUsage.getCurrent()) + "/" + doubleToPercent(this.totalCpuUsage.getAverage());
    }

    private long readFreqFromFile(String fileName) {
        BufferedReader reader;
        long number = 0;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            number = parseLong(reader.readLine());
            reader.close();
        } catch (FileNotFoundException | IOException e) {
        } catch (Throwable th) {
            reader.close();
            throw th;
        }
        return number;
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "parseLong error.", e);
            return 0;
        }
    }

    private ProcStat readProcStat() {
        BufferedReader reader;
        long userTime = 0;
        long systemTime = 0;
        long idleTime = 0;
        try {
            reader = new BufferedReader(new FileReader("/proc/stat"));
            try {
                String[] lines = reader.readLine().split("\\s+");
                int length = lines.length;
                if (length >= 5) {
                    userTime = parseLong(lines[1]) + parseLong(lines[2]);
                    systemTime = parseLong(lines[3]);
                    idleTime = parseLong(lines[4]);
                }
                if (length >= 8) {
                    userTime += parseLong(lines[5]);
                    systemTime = systemTime + parseLong(lines[6]) + parseLong(lines[7]);
                }
                reader.close();
                return new ProcStat(userTime, systemTime, idleTime);
            } catch (Exception e) {
                Log.e(TAG, "Problems parsing /proc/stat", e);
                reader.close();
                return null;
            }
        } catch (FileNotFoundException e2) {
            Log.e(TAG, "Cannot open /proc/stat for reading");
            return null;
        } catch (IOException e3) {
            Log.e(TAG, "Problems reading /proc/stat");
            return null;
        } catch (Throwable th) {
            Log.e(TAG, "Unknown error");
            return null;
        }
    }
}
