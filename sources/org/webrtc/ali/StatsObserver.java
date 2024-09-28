package org.webrtc.ali;

public interface StatsObserver {
    void onComplete(StatsReport[] statsReportArr);
}
