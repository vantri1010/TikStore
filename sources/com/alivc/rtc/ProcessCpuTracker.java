package com.alivc.rtc;

import android.content.Context;
import android.os.Build;
import android.os.CpuUsageInfo;
import android.os.HardwarePropertiesManager;
import android.os.Process;
import java.io.Serializable;
import java.lang.reflect.Method;

public class ProcessCpuTracker implements Serializable {
    static final int[] PROCESS_STATS_FORMAT = {32, 544, 32, 32, 32, 32, 32, 32, 32, 8224, 32, 8224, 32, 8224, 8224, 32, 32, 32, 32, 8224, 32, 8224, 32};
    private static final int PROCESS_STAT_STIME = 3;
    private static final int PROCESS_STAT_UTIME = 2;
    private static final int PROC_COMBINE = 256;
    private static final int PROC_OUT_LONG = 8192;
    private static final int PROC_PARENS = 512;
    private static final int PROC_SPACE_TERM = 32;
    static final int[] SYSTEM_CPU_FORMAT = {288, 8224, 8224, 8224, 8224, 8224, 8224, 8224};
    private long mBaseIdleTime;
    private long mBaseIoWaitTime;
    private long mBaseIrqTime;
    private long mBaseSoftIrqTime;
    private long mBaseSystemTime;
    private long mBaseUserTime;
    private int mMyPidPercent;
    private String mPidStatFile;
    private long mProcessBaseSystemTime;
    private long mProcessBaseUserTime;
    private int mProcessRelSystemTime;
    private int mProcessRelUserTime;
    private long mProcessSystemTime;
    private long mProcessUserTime;
    private Method mReadProcFile;
    private int mRelIdleTime;
    private int mRelIoWaitTime;
    private int mRelIrqTime;
    private int mRelSoftIrqTime;
    private int mRelSystemTime;
    private int mRelUserTime;
    private final long[] mStatsData = new long[6];
    private final long[] mSysCpu = new long[7];
    private int mTotalSysPercent;

    ProcessCpuTracker() {
        try {
            this.mPidStatFile = "/proc/" + Process.myPid() + "/stat";
            Method method = Process.class.getMethod("readProcFile", new Class[]{String.class, int[].class, String[].class, long[].class, float[].class});
            this.mReadProcFile = method;
            method.setAccessible(true);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void updateCpuUsages(Context context) {
        long softirqtime;
        long iowaittime;
        long idletime;
        Context context2 = context;
        Method method = this.mReadProcFile;
        if (!(method == null || this.mPidStatFile == null)) {
            try {
                if (((Boolean) method.invoke((Object) null, new Object[]{"/proc/stat", SYSTEM_CPU_FORMAT, null, this.mSysCpu, null})).booleanValue()) {
                    if (((Boolean) this.mReadProcFile.invoke((Object) null, new Object[]{this.mPidStatFile, PROCESS_STATS_FORMAT, null, this.mStatsData, null})).booleanValue()) {
                        long j = this.mStatsData[2];
                        this.mProcessUserTime = j;
                        long j2 = this.mStatsData[3];
                        this.mProcessSystemTime = j2;
                        if (j < this.mProcessBaseUserTime || j2 < this.mProcessBaseSystemTime) {
                            this.mProcessRelUserTime = 0;
                            this.mProcessRelSystemTime = 0;
                        } else {
                            this.mProcessRelUserTime = (int) (j - this.mProcessBaseUserTime);
                            this.mProcessRelSystemTime = (int) (j2 - this.mProcessBaseSystemTime);
                        }
                        long usertime = this.mSysCpu[0] + this.mSysCpu[1];
                        long idletime2 = this.mSysCpu[3];
                        long iowaittime2 = this.mSysCpu[4];
                        long irqtime = this.mSysCpu[5];
                        long softirqtime2 = this.mSysCpu[6];
                        long systemtime = this.mSysCpu[2];
                        if (usertime < this.mBaseUserTime || systemtime < this.mBaseSystemTime || iowaittime2 < this.mBaseIoWaitTime || irqtime < this.mBaseIrqTime) {
                            iowaittime = iowaittime2;
                            softirqtime = softirqtime2;
                            idletime = idletime2;
                        } else {
                            iowaittime = iowaittime2;
                            long softirqtime3 = softirqtime2;
                            if (softirqtime3 < this.mBaseSoftIrqTime || idletime2 < this.mBaseIdleTime) {
                                idletime = idletime2;
                                softirqtime = softirqtime3;
                            } else {
                                int i = (int) (usertime - this.mBaseUserTime);
                                this.mRelUserTime = i;
                                idletime = idletime2;
                                int i2 = (int) (systemtime - this.mBaseSystemTime);
                                this.mRelSystemTime = i2;
                                int i3 = i;
                                int i4 = (int) (iowaittime - this.mBaseIoWaitTime);
                                this.mRelIoWaitTime = i4;
                                softirqtime = softirqtime3;
                                int i5 = (int) (irqtime - this.mBaseIrqTime);
                                this.mRelIrqTime = i5;
                                int i6 = (int) (softirqtime - this.mBaseSoftIrqTime);
                                this.mRelSoftIrqTime = i6;
                                int i7 = (int) (idletime - this.mBaseIdleTime);
                                this.mRelIdleTime = i7;
                                long totalTime = (long) (i3 + i2 + i4 + i5 + i6 + i7);
                                if (totalTime > 1) {
                                    int percent = Math.abs((int) (((long) ((this.mProcessRelUserTime + this.mProcessRelSystemTime) * 100)) / totalTime));
                                    int abs = Math.abs((int) (((long) ((this.mRelUserTime + this.mRelSystemTime) * 100)) / totalTime));
                                    this.mTotalSysPercent = abs;
                                    this.mMyPidPercent = percent;
                                    if (abs > 100) {
                                        this.mTotalSysPercent = 100;
                                    }
                                    if (this.mMyPidPercent > this.mTotalSysPercent) {
                                        this.mMyPidPercent = this.mTotalSysPercent;
                                    }
                                    int i8 = percent;
                                }
                                this.mProcessBaseUserTime = this.mProcessUserTime;
                                this.mProcessBaseSystemTime = this.mProcessSystemTime;
                                this.mBaseUserTime = usertime;
                                this.mBaseSystemTime = systemtime;
                                this.mBaseIoWaitTime = iowaittime;
                                this.mBaseIrqTime = irqtime;
                                this.mBaseSoftIrqTime = softirqtime;
                                long j3 = usertime;
                                this.mBaseIdleTime = idletime;
                            }
                        }
                        this.mRelUserTime = 0;
                        this.mRelSystemTime = 0;
                        this.mRelIoWaitTime = 0;
                        this.mRelIrqTime = 0;
                        this.mRelSoftIrqTime = 0;
                        this.mRelIdleTime = 0;
                        this.mProcessBaseUserTime = this.mProcessUserTime;
                        this.mProcessBaseSystemTime = this.mProcessSystemTime;
                        this.mBaseUserTime = usertime;
                        this.mBaseSystemTime = systemtime;
                        this.mBaseIoWaitTime = iowaittime;
                        this.mBaseIrqTime = irqtime;
                        this.mBaseSoftIrqTime = softirqtime;
                        long j32 = usertime;
                        this.mBaseIdleTime = idletime;
                    }
                } else {
                    return;
                }
            } catch (Exception e) {
            }
        }
        if (Build.VERSION.SDK_INT >= 24 && context2 != null) {
            try {
                HardwarePropertiesManager manager = (HardwarePropertiesManager) context2.getSystemService("hardware_properties");
                if (manager != null) {
                    int cpuPercent = 0;
                    for (CpuUsageInfo usageInfo : manager.getCpuUsages()) {
                        cpuPercent = (int) (((long) cpuPercent) + usageInfo.getActive());
                    }
                    this.mMyPidPercent = cpuPercent;
                }
            } catch (Exception e2) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getMyPicCpuPercent() {
        return this.mMyPidPercent;
    }

    /* access modifiers changed from: package-private */
    public int getTotalSysCpuPercent() {
        return this.mTotalSysPercent;
    }
}
