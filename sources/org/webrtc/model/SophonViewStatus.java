package org.webrtc.model;

import android.view.SurfaceView;
import java.lang.ref.WeakReference;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;

public class SophonViewStatus {
    public String callId;
    public boolean flip;
    public int height;
    public boolean isAddDisplayWindow;
    public int renderMode = ALI_RTC_INTERFACE.AliDisplayMode.AliRTCSdk_Auto_Mode.ordinal();
    public WeakReference<SurfaceView> surfaceView;
    public ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type videoType;
    public ViewMode viewMode;
    public int width;

    public enum ViewMode {
        LOACALVIEW,
        REMOTEVIEW
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height2) {
        this.height = height2;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width2) {
        this.width = width2;
    }

    public String getCallId() {
        return this.callId;
    }

    public void setCallId(String callId2) {
        this.callId = callId2;
    }

    public ViewMode getViewMode() {
        return this.viewMode;
    }

    public void setViewMode(ViewMode viewMode2) {
        this.viewMode = viewMode2;
    }

    public boolean isAddDisplayWindow() {
        return this.isAddDisplayWindow;
    }

    public void setAddDisplayWindow(boolean addDisplayWindow) {
        this.isAddDisplayWindow = addDisplayWindow;
    }

    public ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type getVideoType() {
        return this.videoType;
    }

    public void setVideoType(ALI_RTC_INTERFACE.AliRTCSdk_VideSource_Type videoType2) {
        this.videoType = videoType2;
    }

    public int getRenderMode() {
        return this.renderMode;
    }

    public void setRenderMode(int renderMode2) {
        this.renderMode = renderMode2;
    }
}
