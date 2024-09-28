package com.alivc.component.capture;

public class VideoParam {
    private int cameraId;
    private int currentZoom = 1;
    private int fps;
    private int height;
    private int maxZoom;
    private int minZoom = 0;
    private int pushHeight;
    private int pushWidth;
    private int rotation;
    private int width;

    public VideoParam(int width2, int height2, int fps2, int cameraId2, int rotation2) {
        this.width = width2;
        this.height = height2;
        this.fps = fps2;
        this.cameraId = cameraId2;
        this.rotation = rotation2;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width2) {
        this.width = width2;
    }

    public int getHeight() {
        return this.height;
    }

    public int getPushWidth() {
        return this.pushWidth;
    }

    public void setPushWidth(int pushWidth2) {
        this.pushWidth = pushWidth2;
    }

    public int getPushHeight() {
        return this.pushHeight;
    }

    public void setPushHeight(int pushHeight2) {
        this.pushHeight = pushHeight2;
    }

    public void setHeight(int height2) {
        this.height = height2;
    }

    public int getCameraId() {
        return this.cameraId;
    }

    public void setCameraId(int cameraId2) {
        this.cameraId = cameraId2;
    }

    public int getFps() {
        return this.fps;
    }

    public void setFps(int fps2) {
        this.fps = fps2;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int rotation2) {
        this.rotation = rotation2;
    }

    public int getMaxZoom() {
        return this.maxZoom;
    }

    public void setMaxZoom(int maxZoom2) {
        this.maxZoom = maxZoom2;
    }

    public int getMinZoom() {
        return this.minZoom;
    }

    public void setMinZoom(int minZoom2) {
        this.minZoom = minZoom2;
    }

    public int getCurrentZoom() {
        return this.currentZoom;
    }

    public void setCurrentZoom(int currentZoom2) {
        this.currentZoom = currentZoom2;
    }
}
