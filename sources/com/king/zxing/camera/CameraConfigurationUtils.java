package com.king.zxing.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import com.king.zxing.util.LogUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class CameraConfigurationUtils {
    private static final int AREA_PER_1000 = 400;
    private static final double MAX_ASPECT_DISTORTION = 0.05d;
    private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
    private static final int MAX_FPS = 20;
    private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
    private static final int MIN_FPS = 10;
    private static final int MIN_PREVIEW_PIXELS = 153600;
    private static final Pattern SEMICOLON = Pattern.compile(";");

    private CameraConfigurationUtils() {
    }

    public static void setFocus(Camera.Parameters parameters, boolean autoFocus, boolean disableContinuous, boolean safeMode) {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        String focusMode = null;
        if (autoFocus) {
            if (safeMode || disableContinuous) {
                focusMode = findSettableValue("focus mode", supportedFocusModes, "auto");
            } else {
                focusMode = findSettableValue("focus mode", supportedFocusModes, "continuous-picture", "continuous-video", "auto");
            }
        }
        if (!safeMode && focusMode == null) {
            focusMode = findSettableValue("focus mode", supportedFocusModes, "macro", "edof");
        }
        if (focusMode == null) {
            return;
        }
        if (focusMode.equals(parameters.getFocusMode())) {
            LogUtils.d("Focus mode already set to " + focusMode);
            return;
        }
        parameters.setFocusMode(focusMode);
    }

    public static void setTorch(Camera.Parameters parameters, boolean on) {
        String flashMode;
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (on) {
            flashMode = findSettableValue("flash mode", supportedFlashModes, "torch", "on");
        } else {
            flashMode = findSettableValue("flash mode", supportedFlashModes, "off");
        }
        if (flashMode == null) {
            return;
        }
        if (flashMode.equals(parameters.getFlashMode())) {
            LogUtils.d("Flash mode already set to " + flashMode);
            return;
        }
        LogUtils.d("Setting flash mode to " + flashMode);
        parameters.setFlashMode(flashMode);
    }

    public static void setBestExposure(Camera.Parameters parameters, boolean lightOn) {
        int minExposure = parameters.getMinExposureCompensation();
        int maxExposure = parameters.getMaxExposureCompensation();
        float step = parameters.getExposureCompensationStep();
        if (!(minExposure == 0 && maxExposure == 0)) {
            float targetCompensation = 0.0f;
            if (step > 0.0f) {
                if (!lightOn) {
                    targetCompensation = MAX_EXPOSURE_COMPENSATION;
                }
                int compensationSteps = Math.round(targetCompensation / step);
                float actualCompensation = ((float) compensationSteps) * step;
                int compensationSteps2 = Math.max(Math.min(compensationSteps, maxExposure), minExposure);
                if (parameters.getExposureCompensation() == compensationSteps2) {
                    LogUtils.d("Exposure compensation already set to " + compensationSteps2 + " / " + actualCompensation);
                    return;
                }
                LogUtils.d("Setting exposure compensation to " + compensationSteps2 + " / " + actualCompensation);
                parameters.setExposureCompensation(compensationSteps2);
                return;
            }
        }
        LogUtils.d("Camera does not support exposure compensation");
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters) {
        setBestPreviewFPS(parameters, 10, 20);
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters, int minFPS, int maxFPS) {
        List<int[]> supportedPreviewFpsRanges = parameters.getSupportedPreviewFpsRange();
        LogUtils.d("Supported FPS ranges: " + toString((Collection<int[]>) supportedPreviewFpsRanges));
        if (supportedPreviewFpsRanges != null && !supportedPreviewFpsRanges.isEmpty()) {
            int[] suitableFPSRange = null;
            Iterator<int[]> it = supportedPreviewFpsRanges.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                int[] fpsRange = it.next();
                int thisMin = fpsRange[0];
                int thisMax = fpsRange[1];
                if (thisMin >= minFPS * 1000 && thisMax <= maxFPS * 1000) {
                    suitableFPSRange = fpsRange;
                    break;
                }
            }
            if (suitableFPSRange == null) {
                LogUtils.d("No suitable FPS range?");
                return;
            }
            int[] currentFpsRange = new int[2];
            parameters.getPreviewFpsRange(currentFpsRange);
            if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
                LogUtils.d("FPS range already set to " + Arrays.toString(suitableFPSRange));
                return;
            }
            LogUtils.d("Setting FPS range to " + Arrays.toString(suitableFPSRange));
            parameters.setPreviewFpsRange(suitableFPSRange[0], suitableFPSRange[1]);
        }
    }

    public static void setFocusArea(Camera.Parameters parameters) {
        if (parameters.getMaxNumFocusAreas() > 0) {
            LogUtils.d("Old focus areas: " + toString((Iterable<Camera.Area>) parameters.getFocusAreas()));
            List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
            LogUtils.d("Setting focus area to : " + toString((Iterable<Camera.Area>) middleArea));
            parameters.setFocusAreas(middleArea);
            return;
        }
        LogUtils.d("Device does not support focus areas");
    }

    public static void setMetering(Camera.Parameters parameters) {
        if (parameters.getMaxNumMeteringAreas() > 0) {
            LogUtils.d("Old metering areas: " + parameters.getMeteringAreas());
            List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
            LogUtils.d("Setting metering area to : " + toString((Iterable<Camera.Area>) middleArea));
            parameters.setMeteringAreas(middleArea);
            return;
        }
        LogUtils.d("Device does not support metering areas");
    }

    private static List<Camera.Area> buildMiddleArea(int areaPer1000) {
        return Collections.singletonList(new Camera.Area(new Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1));
    }

    public static void setVideoStabilization(Camera.Parameters parameters) {
        if (!parameters.isVideoStabilizationSupported()) {
            LogUtils.d("This device does not support video stabilization");
        } else if (parameters.getVideoStabilization()) {
            LogUtils.d("Video stabilization already enabled");
        } else {
            LogUtils.d("Enabling video stabilization...");
            parameters.setVideoStabilization(true);
        }
    }

    public static void setBarcodeSceneMode(Camera.Parameters parameters) {
        if ("barcode".equals(parameters.getSceneMode())) {
            LogUtils.d("Barcode scene mode already set");
            return;
        }
        String sceneMode = findSettableValue("scene mode", parameters.getSupportedSceneModes(), "barcode");
        if (sceneMode != null) {
            parameters.setSceneMode(sceneMode);
        }
    }

    public static void setZoom(Camera.Parameters parameters, double targetZoomRatio) {
        if (parameters.isZoomSupported()) {
            Integer zoom = indexOfClosestZoom(parameters, targetZoomRatio);
            if (zoom != null) {
                if (parameters.getZoom() == zoom.intValue()) {
                    LogUtils.d("Zoom is already set to " + zoom);
                    return;
                }
                LogUtils.d("Setting zoom to " + zoom);
                parameters.setZoom(zoom.intValue());
                return;
            }
            return;
        }
        LogUtils.d("Zoom is not supported");
    }

    private static Integer indexOfClosestZoom(Camera.Parameters parameters, double targetZoomRatio) {
        List<Integer> ratios = parameters.getZoomRatios();
        LogUtils.d("Zoom ratios: " + ratios);
        int maxZoom = parameters.getMaxZoom();
        if (ratios == null || ratios.isEmpty() || ratios.size() != maxZoom + 1) {
            LogUtils.w("Invalid zoom ratios!");
            return null;
        }
        double target100 = targetZoomRatio * 100.0d;
        double smallestDiff = Double.POSITIVE_INFINITY;
        int closestIndex = 0;
        for (int i = 0; i < ratios.size(); i++) {
            double diff = Math.abs(((double) ratios.get(i).intValue()) - target100);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                closestIndex = i;
            }
        }
        LogUtils.d("Chose zoom ratio of " + (((double) ratios.get(closestIndex).intValue()) / 100.0d));
        return Integer.valueOf(closestIndex);
    }

    public static void setInvertColor(Camera.Parameters parameters) {
        if ("negative".equals(parameters.getColorEffect())) {
            LogUtils.d("Negative effect already set");
            return;
        }
        String colorMode = findSettableValue("color effect", parameters.getSupportedColorEffects(), "negative");
        if (colorMode != null) {
            parameters.setColorEffect(colorMode);
        }
    }

    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        double screenAspectRatio;
        double screenAspectRatio2;
        Camera.Size maxResPreviewSize;
        Iterator<Camera.Size> it;
        List<Camera.Size> rawSupportedSizes;
        double screenAspectRatio3;
        Point point = screenResolution;
        List<Camera.Size> rawSupportedSizes2 = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes2 == null) {
            LogUtils.w("Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize != null) {
                return new Point(defaultSize.width, defaultSize.height);
            }
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        if (LogUtils.isShowLog()) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size size : rawSupportedSizes2) {
                previewSizesString.append(size.width);
                previewSizesString.append('x');
                previewSizesString.append(size.height);
                previewSizesString.append(' ');
            }
            LogUtils.d("Supported preview sizes: " + previewSizesString);
        }
        if (point.x < point.y) {
            screenAspectRatio = ((double) point.x) / ((double) point.y);
        } else {
            screenAspectRatio = ((double) point.y) / ((double) point.x);
        }
        LogUtils.d("screenAspectRatio: " + screenAspectRatio);
        int maxResolution = 0;
        Camera.Size maxResPreviewSize2 = null;
        Iterator<Camera.Size> it2 = rawSupportedSizes2.iterator();
        while (it2.hasNext()) {
            Camera.Size size2 = it2.next();
            int realWidth = size2.width;
            int realHeight = size2.height;
            int resolution = realWidth * realHeight;
            if (resolution < MIN_PREVIEW_PIXELS) {
                rawSupportedSizes = rawSupportedSizes2;
                screenAspectRatio2 = screenAspectRatio;
                maxResPreviewSize = maxResPreviewSize2;
                it = it2;
            } else {
                boolean isCandidatePortrait = realWidth < realHeight;
                int maybeFlippedWidth = isCandidatePortrait ? realWidth : realHeight;
                int maybeFlippedHeight = isCandidatePortrait ? realHeight : realWidth;
                LogUtils.d(String.format("maybeFlipped:%d * %d", new Object[]{Integer.valueOf(maybeFlippedWidth), Integer.valueOf(maybeFlippedHeight)}));
                rawSupportedSizes = rawSupportedSizes2;
                maxResPreviewSize = maxResPreviewSize2;
                int maybeFlippedHeight2 = maybeFlippedHeight;
                it = it2;
                double aspectRatio = ((double) maybeFlippedWidth) / ((double) maybeFlippedHeight2);
                LogUtils.d("aspectRatio: " + aspectRatio);
                double distortion = Math.abs(aspectRatio - screenAspectRatio);
                screenAspectRatio2 = screenAspectRatio;
                LogUtils.d("distortion: " + distortion);
                if (distortion <= MAX_ASPECT_DISTORTION) {
                    if (maybeFlippedWidth == point.x && maybeFlippedHeight2 == point.y) {
                        Point exactPoint = new Point(realWidth, realHeight);
                        LogUtils.d("Found preview size exactly matching screen size: " + exactPoint);
                        return exactPoint;
                    }
                    if (resolution > maxResolution) {
                        maxResolution = resolution;
                        maxResPreviewSize2 = size2;
                    } else {
                        maxResPreviewSize2 = maxResPreviewSize;
                    }
                    rawSupportedSizes2 = rawSupportedSizes;
                    it2 = it;
                    screenAspectRatio3 = screenAspectRatio2;
                }
            }
            rawSupportedSizes2 = rawSupportedSizes;
            it2 = it;
            maxResPreviewSize2 = maxResPreviewSize;
            screenAspectRatio3 = screenAspectRatio2;
        }
        double d = screenAspectRatio;
        Camera.Size maxResPreviewSize3 = maxResPreviewSize2;
        if (maxResPreviewSize3 != null) {
            Camera.Size maxResPreviewSize4 = maxResPreviewSize3;
            Point largestSize = new Point(maxResPreviewSize4.width, maxResPreviewSize4.height);
            LogUtils.d("Using largest suitable preview size: " + largestSize);
            return largestSize;
        }
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview != null) {
            Point defaultSize2 = new Point(defaultPreview.width, defaultPreview.height);
            LogUtils.d("No suitable preview sizes, using default: " + defaultSize2);
            return defaultSize2;
        }
        throw new IllegalStateException("Parameters contained no preview size!");
    }

    private static String findSettableValue(String name, Collection<String> supportedValues, String... desiredValues) {
        LogUtils.d("Requesting " + name + " value from among: " + Arrays.toString(desiredValues));
        LogUtils.d("Supported " + name + " values: " + supportedValues);
        if (supportedValues != null) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    LogUtils.d("Can set " + name + " to: " + desiredValue);
                    return desiredValue;
                }
            }
        }
        LogUtils.d("No supported values match");
        return null;
    }

    private static String toString(Collection<int[]> arrays) {
        if (arrays == null || arrays.isEmpty()) {
            return "[]";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        Iterator<int[]> it = arrays.iterator();
        while (it.hasNext()) {
            buffer.append(Arrays.toString(it.next()));
            if (it.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append(']');
        return buffer.toString();
    }

    private static String toString(Iterable<Camera.Area> areas) {
        if (areas == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Camera.Area area : areas) {
            result.append(area.rect);
            result.append(':');
            result.append(area.weight);
            result.append(' ');
        }
        return result.toString();
    }

    public static String collectStats(Camera.Parameters parameters) {
        return collectStats((CharSequence) parameters.flatten());
    }

    public static String collectStats(CharSequence flattenedParams) {
        StringBuilder result = new StringBuilder(1000);
        result.append("BOARD=");
        result.append(Build.BOARD);
        result.append(10);
        result.append("BRAND=");
        result.append(Build.BRAND);
        result.append(10);
        result.append("CPU_ABI=");
        result.append(Build.CPU_ABI);
        result.append(10);
        result.append("DEVICE=");
        result.append(Build.DEVICE);
        result.append(10);
        result.append("DISPLAY=");
        result.append(Build.DISPLAY);
        result.append(10);
        result.append("FINGERPRINT=");
        result.append(Build.FINGERPRINT);
        result.append(10);
        result.append("HOST=");
        result.append(Build.HOST);
        result.append(10);
        result.append("ID=");
        result.append(Build.ID);
        result.append(10);
        result.append("MANUFACTURER=");
        result.append(Build.MANUFACTURER);
        result.append(10);
        result.append("MODEL=");
        result.append(Build.MODEL);
        result.append(10);
        result.append("PRODUCT=");
        result.append(Build.PRODUCT);
        result.append(10);
        result.append("TAGS=");
        result.append(Build.TAGS);
        result.append(10);
        result.append("TIME=");
        result.append(Build.TIME);
        result.append(10);
        result.append("TYPE=");
        result.append(Build.TYPE);
        result.append(10);
        result.append("USER=");
        result.append(Build.USER);
        result.append(10);
        result.append("VERSION.CODENAME=");
        result.append(Build.VERSION.CODENAME);
        result.append(10);
        result.append("VERSION.INCREMENTAL=");
        result.append(Build.VERSION.INCREMENTAL);
        result.append(10);
        result.append("VERSION.RELEASE=");
        result.append(Build.VERSION.RELEASE);
        result.append(10);
        result.append("VERSION.SDK_INT=");
        result.append(Build.VERSION.SDK_INT);
        result.append(10);
        if (flattenedParams != null) {
            String[] params = SEMICOLON.split(flattenedParams);
            Arrays.sort(params);
            for (String param : params) {
                result.append(param);
                result.append(10);
            }
        }
        return result.toString();
    }
}
