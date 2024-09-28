package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.king.zxing.util.CodeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.PhotoEditRadioCell;
import im.bclpbkiauv.ui.cells.PhotoEditToolCell;
import im.bclpbkiauv.ui.components.PhotoFilterBlurControl;
import im.bclpbkiauv.ui.components.PhotoFilterCurvesControl;
import im.bclpbkiauv.ui.components.PhotoFilterView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

public class PhotoFilterView extends FrameLayout {
    private static final int curveDataStep = 2;
    private static final int curveGranularity = 100;
    /* access modifiers changed from: private */
    public Bitmap bitmapToEdit;
    /* access modifiers changed from: private */
    public float blurAngle;
    private PhotoFilterBlurControl blurControl;
    /* access modifiers changed from: private */
    public float blurExcludeBlurSize;
    /* access modifiers changed from: private */
    public Point blurExcludePoint;
    /* access modifiers changed from: private */
    public float blurExcludeSize;
    private ImageView blurItem;
    private FrameLayout blurLayout;
    private TextView blurLinearButton;
    private TextView blurOffButton;
    private TextView blurRadialButton;
    /* access modifiers changed from: private */
    public int blurType;
    private TextView cancelTextView;
    /* access modifiers changed from: private */
    public int contrastTool = 2;
    /* access modifiers changed from: private */
    public float contrastValue;
    private ImageView curveItem;
    private FrameLayout curveLayout;
    private RadioButton[] curveRadioButton = new RadioButton[4];
    private PhotoFilterCurvesControl curvesControl;
    /* access modifiers changed from: private */
    public CurvesToolValue curvesToolValue;
    private TextView doneTextView;
    /* access modifiers changed from: private */
    public EGLThread eglThread;
    /* access modifiers changed from: private */
    public int enhanceTool = 0;
    /* access modifiers changed from: private */
    public float enhanceValue;
    /* access modifiers changed from: private */
    public int exposureTool = 1;
    /* access modifiers changed from: private */
    public float exposureValue;
    /* access modifiers changed from: private */
    public int fadeTool = 5;
    /* access modifiers changed from: private */
    public float fadeValue;
    /* access modifiers changed from: private */
    public int grainTool = 9;
    /* access modifiers changed from: private */
    public float grainValue;
    /* access modifiers changed from: private */
    public int highlightsTool = 6;
    /* access modifiers changed from: private */
    public float highlightsValue;
    private MediaController.SavedFilterState lastState;
    /* access modifiers changed from: private */
    public int orientation;
    private RecyclerListView recyclerListView;
    /* access modifiers changed from: private */
    public int saturationTool = 3;
    /* access modifiers changed from: private */
    public float saturationValue;
    private int selectedTool;
    /* access modifiers changed from: private */
    public int shadowsTool = 7;
    /* access modifiers changed from: private */
    public float shadowsValue;
    /* access modifiers changed from: private */
    public int sharpenTool = 10;
    /* access modifiers changed from: private */
    public float sharpenValue;
    /* access modifiers changed from: private */
    public boolean showOriginal;
    private TextureView textureView;
    /* access modifiers changed from: private */
    public int tintHighlightsColor;
    /* access modifiers changed from: private */
    public int tintHighlightsTool = 12;
    /* access modifiers changed from: private */
    public int tintShadowsColor;
    /* access modifiers changed from: private */
    public int tintShadowsTool = 11;
    private FrameLayout toolsView;
    private ImageView tuneItem;
    /* access modifiers changed from: private */
    public int vignetteTool = 8;
    /* access modifiers changed from: private */
    public float vignetteValue;
    /* access modifiers changed from: private */
    public int warmthTool = 4;
    /* access modifiers changed from: private */
    public float warmthValue;

    public static class CurvesValue {
        public float blacksLevel = 0.0f;
        public float[] cachedDataPoints;
        public float highlightsLevel = 75.0f;
        public float midtonesLevel = 50.0f;
        public float previousBlacksLevel = 0.0f;
        public float previousHighlightsLevel = 75.0f;
        public float previousMidtonesLevel = 50.0f;
        public float previousShadowsLevel = 25.0f;
        public float previousWhitesLevel = 100.0f;
        public float shadowsLevel = 25.0f;
        public float whitesLevel = 100.0f;

        public float[] getDataPoints() {
            if (this.cachedDataPoints == null) {
                interpolateCurve();
            }
            return this.cachedDataPoints;
        }

        public void saveValues() {
            this.previousBlacksLevel = this.blacksLevel;
            this.previousShadowsLevel = this.shadowsLevel;
            this.previousMidtonesLevel = this.midtonesLevel;
            this.previousHighlightsLevel = this.highlightsLevel;
            this.previousWhitesLevel = this.whitesLevel;
        }

        public void restoreValues() {
            this.blacksLevel = this.previousBlacksLevel;
            this.shadowsLevel = this.previousShadowsLevel;
            this.midtonesLevel = this.previousMidtonesLevel;
            this.highlightsLevel = this.previousHighlightsLevel;
            this.whitesLevel = this.previousWhitesLevel;
            interpolateCurve();
        }

        public float[] interpolateCurve() {
            float f = this.blacksLevel;
            int i = 1;
            float f2 = 0.5f;
            float f3 = this.whitesLevel;
            float[] points = {-0.001f, f / 100.0f, 0.0f, f / 100.0f, 0.25f, this.shadowsLevel / 100.0f, 0.5f, this.midtonesLevel / 100.0f, 0.75f, this.highlightsLevel / 100.0f, 1.0f, f3 / 100.0f, 1.001f, f3 / 100.0f};
            int i2 = 100;
            ArrayList<Float> dataPoints = new ArrayList<>(100);
            ArrayList<Float> interpolatedPoints = new ArrayList<>(100);
            interpolatedPoints.add(Float.valueOf(points[0]));
            interpolatedPoints.add(Float.valueOf(points[1]));
            int index = 1;
            while (index < (points.length / 2) - 2) {
                float point0x = points[(index - 1) * 2];
                float point0y = points[((index - 1) * 2) + i];
                float point1x = points[index * 2];
                float point1y = points[(index * 2) + 1];
                float point2x = points[(index + 1) * 2];
                float point2y = points[((index + 1) * 2) + 1];
                float point3x = points[(index + 2) * 2];
                float point3y = points[((index + 2) * 2) + 1];
                int i3 = 1;
                while (i3 < i2) {
                    float t = ((float) i3) * 0.01f;
                    float tt = t * t;
                    float ttt = tt * t;
                    float pix = ((point1x * 2.0f) + ((point2x - point0x) * t) + (((((point0x * 2.0f) - (point1x * 5.0f)) + (point2x * 4.0f)) - point3x) * tt) + (((((point1x * 3.0f) - point0x) - (point2x * 3.0f)) + point3x) * ttt)) * f2;
                    float piy = Math.max(0.0f, Math.min(1.0f, ((point1y * 2.0f) + ((point2y - point0y) * t) + (((((2.0f * point0y) - (5.0f * point1y)) + (4.0f * point2y)) - point3y) * tt) + (((((point1y * 3.0f) - point0y) - (3.0f * point2y)) + point3y) * ttt)) * f2));
                    if (pix > point0x) {
                        interpolatedPoints.add(Float.valueOf(pix));
                        interpolatedPoints.add(Float.valueOf(piy));
                    }
                    if ((i3 - 1) % 2 == 0) {
                        dataPoints.add(Float.valueOf(piy));
                    }
                    i3++;
                    f2 = 0.5f;
                    i2 = 100;
                }
                interpolatedPoints.add(Float.valueOf(point2x));
                interpolatedPoints.add(Float.valueOf(point2y));
                index++;
                f2 = 0.5f;
                i = 1;
                i2 = 100;
            }
            interpolatedPoints.add(Float.valueOf(points[12]));
            interpolatedPoints.add(Float.valueOf(points[13]));
            this.cachedDataPoints = new float[dataPoints.size()];
            int a = 0;
            while (true) {
                float[] fArr = this.cachedDataPoints;
                if (a >= fArr.length) {
                    break;
                }
                fArr[a] = dataPoints.get(a).floatValue();
                a++;
            }
            float[] retValue = new float[interpolatedPoints.size()];
            for (int a2 = 0; a2 < retValue.length; a2++) {
                retValue[a2] = interpolatedPoints.get(a2).floatValue();
            }
            return retValue;
        }

        public boolean isDefault() {
            return ((double) Math.abs(this.blacksLevel - 0.0f)) < 1.0E-5d && ((double) Math.abs(this.shadowsLevel - 25.0f)) < 1.0E-5d && ((double) Math.abs(this.midtonesLevel - 50.0f)) < 1.0E-5d && ((double) Math.abs(this.highlightsLevel - 75.0f)) < 1.0E-5d && ((double) Math.abs(this.whitesLevel - 100.0f)) < 1.0E-5d;
        }
    }

    public static class CurvesToolValue {
        public static final int CurvesTypeBlue = 3;
        public static final int CurvesTypeGreen = 2;
        public static final int CurvesTypeLuminance = 0;
        public static final int CurvesTypeRed = 1;
        public int activeType;
        public CurvesValue blueCurve = new CurvesValue();
        public ByteBuffer curveBuffer;
        public CurvesValue greenCurve = new CurvesValue();
        public CurvesValue luminanceCurve = new CurvesValue();
        public CurvesValue redCurve = new CurvesValue();

        public CurvesToolValue() {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(CodeUtils.DEFAULT_REQ_HEIGHT);
            this.curveBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.LITTLE_ENDIAN);
        }

        public void fillBuffer() {
            this.curveBuffer.position(0);
            float[] luminanceCurveData = this.luminanceCurve.getDataPoints();
            float[] redCurveData = this.redCurve.getDataPoints();
            float[] greenCurveData = this.greenCurve.getDataPoints();
            float[] blueCurveData = this.blueCurve.getDataPoints();
            for (int a = 0; a < 200; a++) {
                this.curveBuffer.put((byte) ((int) (redCurveData[a] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (greenCurveData[a] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (blueCurveData[a] * 255.0f)));
                this.curveBuffer.put((byte) ((int) (luminanceCurveData[a] * 255.0f)));
            }
            this.curveBuffer.position(0);
        }

        public boolean shouldBeSkipped() {
            return this.luminanceCurve.isDefault() && this.redCurve.isDefault() && this.greenCurve.isDefault() && this.blueCurve.isDefault();
        }
    }

    public class EGLThread extends DispatchQueue {
        private static final int PGPhotoEnhanceHistogramBins = 256;
        private static final int PGPhotoEnhanceSegments = 4;
        private static final String blurFragmentShaderCode = "uniform sampler2D sourceImage;varying highp vec2 blurCoordinates[9];void main() {lowp vec4 sum = vec4(0.0);sum += texture2D(sourceImage, blurCoordinates[0]) * 0.133571;sum += texture2D(sourceImage, blurCoordinates[1]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[2]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[3]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[4]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[5]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[6]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[7]) * 0.012595;sum += texture2D(sourceImage, blurCoordinates[8]) * 0.012595;gl_FragColor = sum;}";
        private static final String blurVertexShaderCode = "attribute vec4 position;attribute vec4 inputTexCoord;uniform highp float texelWidthOffset;uniform highp float texelHeightOffset;varying vec2 blurCoordinates[9];void main() {gl_Position = position;vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);blurCoordinates[0] = inputTexCoord.xy;blurCoordinates[1] = inputTexCoord.xy + singleStepOffset * 1.458430;blurCoordinates[2] = inputTexCoord.xy - singleStepOffset * 1.458430;blurCoordinates[3] = inputTexCoord.xy + singleStepOffset * 3.403985;blurCoordinates[4] = inputTexCoord.xy - singleStepOffset * 3.403985;blurCoordinates[5] = inputTexCoord.xy + singleStepOffset * 5.351806;blurCoordinates[6] = inputTexCoord.xy - singleStepOffset * 5.351806;blurCoordinates[7] = inputTexCoord.xy + singleStepOffset * 7.302940;blurCoordinates[8] = inputTexCoord.xy - singleStepOffset * 7.302940;}";
        private static final String enhanceFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.001953125, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 c00 = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 c01 = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 c10 = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 c11 = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((c00.r - c00.g) / (c00.b - c00.g));float c2 = ((c01.r - c01.g) / (c01.b - c01.g));float c3 = ((c10.r - c10.g) / (c10.b - c10.g));float c4 = ((c11.r - c11.g) / (c11.b - c11.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}";
        private static final String linearBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
        private static final String radialBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
        private static final String rgbToHsvFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}";
        private static final String sharpenFragmentShaderCode = "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}";
        private static final String sharpenVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}";
        private static final String simpleFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}";
        private static final String simpleVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}";
        private static final String toolsFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;} return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}";
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        private int blurHeightHandle;
        private int blurInputTexCoordHandle;
        private int blurPositionHandle;
        private int blurShaderProgram;
        private int blurSourceImageHandle;
        private int blurWidthHandle;
        /* access modifiers changed from: private */
        public boolean blured;
        private int contrastHandle;
        private Bitmap currentBitmap;
        private int[] curveTextures = new int[1];
        private int curvesImageHandle;
        private Runnable drawRunnable = new Runnable() {
            public void run() {
                if (EGLThread.this.initied) {
                    if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                        GLES20.glViewport(0, 0, EGLThread.this.renderBufferWidth, EGLThread.this.renderBufferHeight);
                        EGLThread.this.drawEnhancePass();
                        EGLThread.this.drawSharpenPass();
                        EGLThread.this.drawCustomParamsPass();
                        EGLThread eGLThread = EGLThread.this;
                        boolean unused = eGLThread.blured = eGLThread.drawBlurPass();
                        GLES20.glViewport(0, 0, EGLThread.this.surfaceWidth, EGLThread.this.surfaceHeight);
                        GLES20.glBindFramebuffer(36160, 0);
                        GLES20.glClear(0);
                        GLES20.glUseProgram(EGLThread.this.simpleShaderProgram);
                        GLES20.glActiveTexture(33984);
                        GLES20.glBindTexture(3553, EGLThread.this.renderTexture[!EGLThread.this.blured]);
                        GLES20.glUniform1i(EGLThread.this.simpleSourceImageHandle, 0);
                        GLES20.glEnableVertexAttribArray(EGLThread.this.simpleInputTexCoordHandle);
                        GLES20.glVertexAttribPointer(EGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, EGLThread.this.textureBuffer);
                        GLES20.glEnableVertexAttribArray(EGLThread.this.simplePositionHandle);
                        GLES20.glVertexAttribPointer(EGLThread.this.simplePositionHandle, 2, 5126, false, 8, EGLThread.this.vertexBuffer);
                        GLES20.glDrawArrays(5, 0, 4);
                        EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public EGL10 egl10;
        private EGLConfig eglConfig;
        /* access modifiers changed from: private */
        public EGLContext eglContext;
        /* access modifiers changed from: private */
        public EGLDisplay eglDisplay;
        /* access modifiers changed from: private */
        public EGLSurface eglSurface;
        private int enhanceInputImageTexture2Handle;
        private int enhanceInputTexCoordHandle;
        private int enhanceIntensityHandle;
        private int enhancePositionHandle;
        private int enhanceShaderProgram;
        private int enhanceSourceImageHandle;
        private int[] enhanceTextures = new int[2];
        private int exposureHandle;
        private int fadeAmountHandle;
        private GL gl;
        private int grainHandle;
        private int heightHandle;
        private int highlightsHandle;
        private int highlightsTintColorHandle;
        private int highlightsTintIntensityHandle;
        private boolean hsvGenerated;
        /* access modifiers changed from: private */
        public boolean initied;
        private int inputTexCoordHandle;
        private long lastRenderCallTime;
        private int linearBlurAngleHandle;
        private int linearBlurAspectRatioHandle;
        private int linearBlurExcludeBlurSizeHandle;
        private int linearBlurExcludePointHandle;
        private int linearBlurExcludeSizeHandle;
        private int linearBlurInputTexCoordHandle;
        private int linearBlurPositionHandle;
        private int linearBlurShaderProgram;
        private int linearBlurSourceImage2Handle;
        private int linearBlurSourceImageHandle;
        private boolean needUpdateBlurTexture = true;
        private int positionHandle;
        private int radialBlurAspectRatioHandle;
        private int radialBlurExcludeBlurSizeHandle;
        private int radialBlurExcludePointHandle;
        private int radialBlurExcludeSizeHandle;
        private int radialBlurInputTexCoordHandle;
        private int radialBlurPositionHandle;
        private int radialBlurShaderProgram;
        private int radialBlurSourceImage2Handle;
        private int radialBlurSourceImageHandle;
        /* access modifiers changed from: private */
        public int renderBufferHeight;
        /* access modifiers changed from: private */
        public int renderBufferWidth;
        private int[] renderFrameBuffer = new int[3];
        /* access modifiers changed from: private */
        public int[] renderTexture = new int[3];
        private int rgbToHsvInputTexCoordHandle;
        private int rgbToHsvPositionHandle;
        private int rgbToHsvShaderProgram;
        private int rgbToHsvSourceImageHandle;
        private int saturationHandle;
        private int shadowsHandle;
        private int shadowsTintColorHandle;
        private int shadowsTintIntensityHandle;
        private int sharpenHandle;
        private int sharpenHeightHandle;
        private int sharpenInputTexCoordHandle;
        private int sharpenPositionHandle;
        private int sharpenShaderProgram;
        private int sharpenSourceImageHandle;
        private int sharpenWidthHandle;
        /* access modifiers changed from: private */
        public int simpleInputTexCoordHandle;
        /* access modifiers changed from: private */
        public int simplePositionHandle;
        /* access modifiers changed from: private */
        public int simpleShaderProgram;
        /* access modifiers changed from: private */
        public int simpleSourceImageHandle;
        private int skipToneHandle;
        private int sourceImageHandle;
        /* access modifiers changed from: private */
        public volatile int surfaceHeight;
        private SurfaceTexture surfaceTexture;
        /* access modifiers changed from: private */
        public volatile int surfaceWidth;
        /* access modifiers changed from: private */
        public FloatBuffer textureBuffer;
        private int toolsShaderProgram;
        /* access modifiers changed from: private */
        public FloatBuffer vertexBuffer;
        private FloatBuffer vertexInvertBuffer;
        private int vignetteHandle;
        private int warmthHandle;
        private int widthHandle;

        public EGLThread(SurfaceTexture surface, Bitmap bitmap) {
            super("EGLThread");
            this.surfaceTexture = surface;
            this.currentBitmap = bitmap;
        }

        private int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
            if (compileStatus[0] != 0) {
                return shader;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(GLES20.glGetShaderInfoLog(shader));
            }
            GLES20.glDeleteShader(shader);
            return 0;
        }

        private boolean initGL() {
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] version = new int[2];
            if (!this.egl10.eglInitialize(this.eglDisplay, version)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            int[] configSpec = {12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344};
            if (!this.egl10.eglChooseConfig(this.eglDisplay, configSpec, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eGLConfig = configs[0];
                this.eglConfig = eGLConfig;
                int[] attrib_list = {12440, 2, 12344};
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null) {
                        int[] iArr = attrib_list;
                        int[] iArr2 = configSpec;
                    } else if (eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        int[] iArr3 = version;
                        int[] iArr4 = attrib_list;
                        int[] iArr5 = configSpec;
                    } else {
                        EGL10 egl103 = this.egl10;
                        EGLDisplay eGLDisplay = this.eglDisplay;
                        EGLSurface eGLSurface = this.eglSurface;
                        if (!egl103.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            }
                            finish();
                            return false;
                        }
                        this.gl = this.eglContext.getGL();
                        float[] squareCoordinates = {-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f};
                        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoordinates.length * 4);
                        bb.order(ByteOrder.nativeOrder());
                        FloatBuffer asFloatBuffer = bb.asFloatBuffer();
                        this.vertexBuffer = asFloatBuffer;
                        asFloatBuffer.put(squareCoordinates);
                        this.vertexBuffer.position(0);
                        float[] squareCoordinates2 = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
                        ByteBuffer bb2 = ByteBuffer.allocateDirect(squareCoordinates2.length * 4);
                        bb2.order(ByteOrder.nativeOrder());
                        FloatBuffer asFloatBuffer2 = bb2.asFloatBuffer();
                        this.vertexInvertBuffer = asFloatBuffer2;
                        asFloatBuffer2.put(squareCoordinates2);
                        this.vertexInvertBuffer.position(0);
                        float[] textureCoordinates = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
                        ByteBuffer bb3 = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
                        bb3.order(ByteOrder.nativeOrder());
                        FloatBuffer asFloatBuffer3 = bb3.asFloatBuffer();
                        this.textureBuffer = asFloatBuffer3;
                        asFloatBuffer3.put(textureCoordinates);
                        this.textureBuffer.position(0);
                        GLES20.glGenTextures(1, this.curveTextures, 0);
                        GLES20.glGenTextures(2, this.enhanceTextures, 0);
                        int vertexShader = loadShader(35633, simpleVertexShaderCode);
                        int fragmentShader = loadShader(35632, toolsFragmentShaderCode);
                        if (vertexShader == 0 || fragmentShader == 0) {
                            float[] fArr = textureCoordinates;
                            int[] iArr6 = attrib_list;
                            int[] iArr7 = configSpec;
                            float[] fArr2 = squareCoordinates;
                            ByteBuffer byteBuffer = bb3;
                            float[] fArr3 = squareCoordinates2;
                            finish();
                            return false;
                        }
                        int glCreateProgram = GLES20.glCreateProgram();
                        this.toolsShaderProgram = glCreateProgram;
                        GLES20.glAttachShader(glCreateProgram, vertexShader);
                        GLES20.glAttachShader(this.toolsShaderProgram, fragmentShader);
                        GLES20.glBindAttribLocation(this.toolsShaderProgram, 0, "position");
                        int[] iArr8 = version;
                        GLES20.glBindAttribLocation(this.toolsShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.toolsShaderProgram);
                        int[] linkStatus = new int[1];
                        float[] fArr4 = textureCoordinates;
                        int[] iArr9 = attrib_list;
                        GLES20.glGetProgramiv(this.toolsShaderProgram, 35714, linkStatus, 0);
                        if (linkStatus[0] == 0) {
                            GLES20.glDeleteProgram(this.toolsShaderProgram);
                            this.toolsShaderProgram = 0;
                        } else {
                            this.positionHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "position");
                            this.inputTexCoordHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "inputTexCoord");
                            this.sourceImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "sourceImage");
                            this.shadowsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadows");
                            this.highlightsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlights");
                            this.exposureHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "exposure");
                            this.contrastHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "contrast");
                            this.saturationHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "saturation");
                            this.warmthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "warmth");
                            this.vignetteHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "vignette");
                            this.grainHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "grain");
                            this.widthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "width");
                            this.heightHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "height");
                            this.curvesImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "curvesImage");
                            this.skipToneHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "skipTone");
                            this.fadeAmountHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "fadeAmount");
                            this.shadowsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintIntensity");
                            this.highlightsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintIntensity");
                            this.shadowsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintColor");
                            this.highlightsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintColor");
                        }
                        int vertexShader2 = loadShader(35633, sharpenVertexShaderCode);
                        int fragmentShader2 = loadShader(35632, sharpenFragmentShaderCode);
                        if (vertexShader2 == 0 || fragmentShader2 == 0) {
                            int[] iArr10 = configSpec;
                            float[] fArr5 = squareCoordinates;
                            ByteBuffer byteBuffer2 = bb3;
                            float[] fArr6 = squareCoordinates2;
                            finish();
                            return false;
                        }
                        int glCreateProgram2 = GLES20.glCreateProgram();
                        this.sharpenShaderProgram = glCreateProgram2;
                        GLES20.glAttachShader(glCreateProgram2, vertexShader2);
                        GLES20.glAttachShader(this.sharpenShaderProgram, fragmentShader2);
                        GLES20.glBindAttribLocation(this.sharpenShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.sharpenShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.sharpenShaderProgram);
                        int[] linkStatus2 = new int[1];
                        int i = vertexShader2;
                        GLES20.glGetProgramiv(this.sharpenShaderProgram, 35714, linkStatus2, 0);
                        if (linkStatus2[0] == 0) {
                            GLES20.glDeleteProgram(this.sharpenShaderProgram);
                            this.sharpenShaderProgram = 0;
                        } else {
                            this.sharpenPositionHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "position");
                            this.sharpenInputTexCoordHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "inputTexCoord");
                            this.sharpenSourceImageHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sourceImage");
                            this.sharpenWidthHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputWidth");
                            this.sharpenHeightHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputHeight");
                            this.sharpenHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sharpen");
                        }
                        int vertexShader3 = loadShader(35633, blurVertexShaderCode);
                        int fragmentShader3 = loadShader(35632, blurFragmentShaderCode);
                        if (vertexShader3 == 0 || fragmentShader3 == 0) {
                            int fragmentShader4 = vertexShader3;
                            int[] iArr11 = configSpec;
                            float[] fArr7 = squareCoordinates;
                            ByteBuffer byteBuffer3 = bb3;
                            float[] fArr8 = squareCoordinates2;
                            finish();
                            return false;
                        }
                        int glCreateProgram3 = GLES20.glCreateProgram();
                        this.blurShaderProgram = glCreateProgram3;
                        GLES20.glAttachShader(glCreateProgram3, vertexShader3);
                        GLES20.glAttachShader(this.blurShaderProgram, fragmentShader3);
                        GLES20.glBindAttribLocation(this.blurShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.blurShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.blurShaderProgram);
                        int[] linkStatus3 = new int[1];
                        int i2 = vertexShader3;
                        GLES20.glGetProgramiv(this.blurShaderProgram, 35714, linkStatus3, 0);
                        if (linkStatus3[0] == 0) {
                            GLES20.glDeleteProgram(this.blurShaderProgram);
                            this.blurShaderProgram = 0;
                        } else {
                            this.blurPositionHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "position");
                            this.blurInputTexCoordHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "inputTexCoord");
                            this.blurSourceImageHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "sourceImage");
                            this.blurWidthHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelWidthOffset");
                            this.blurHeightHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelHeightOffset");
                        }
                        int vertexShader4 = loadShader(35633, simpleVertexShaderCode);
                        int fragmentShader5 = loadShader(35632, linearBlurFragmentShaderCode);
                        if (vertexShader4 == 0 || fragmentShader5 == 0) {
                            int[] iArr12 = configSpec;
                            float[] fArr9 = squareCoordinates;
                            ByteBuffer byteBuffer4 = bb3;
                            float[] fArr10 = squareCoordinates2;
                            finish();
                            return false;
                        }
                        int glCreateProgram4 = GLES20.glCreateProgram();
                        this.linearBlurShaderProgram = glCreateProgram4;
                        GLES20.glAttachShader(glCreateProgram4, vertexShader4);
                        GLES20.glAttachShader(this.linearBlurShaderProgram, fragmentShader5);
                        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.linearBlurShaderProgram);
                        int[] linkStatus4 = new int[1];
                        int i3 = fragmentShader5;
                        GLES20.glGetProgramiv(this.linearBlurShaderProgram, 35714, linkStatus4, 0);
                        int[] iArr13 = linkStatus4;
                        int[] iArr14 = configSpec;
                        float[] fArr11 = squareCoordinates;
                        if (linkStatus4[0] == 0) {
                            GLES20.glDeleteProgram(this.linearBlurShaderProgram);
                            this.linearBlurShaderProgram = 0;
                            ByteBuffer byteBuffer5 = bb3;
                        } else {
                            this.linearBlurPositionHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "position");
                            this.linearBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "inputTexCoord");
                            this.linearBlurSourceImageHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "sourceImage");
                            this.linearBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "inputImageTexture2");
                            this.linearBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeSize");
                            this.linearBlurExcludePointHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludePoint");
                            this.linearBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeBlurSize");
                            ByteBuffer byteBuffer6 = bb3;
                            this.linearBlurAngleHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "angle");
                            this.linearBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "aspectRatio");
                        }
                        int vertexShader5 = loadShader(35633, simpleVertexShaderCode);
                        int fragmentShader6 = loadShader(35632, radialBlurFragmentShaderCode);
                        if (vertexShader5 == 0 || fragmentShader6 == 0) {
                            int i4 = fragmentShader6;
                            int i5 = vertexShader5;
                            float[] fArr12 = squareCoordinates2;
                            finish();
                            return false;
                        }
                        int glCreateProgram5 = GLES20.glCreateProgram();
                        this.radialBlurShaderProgram = glCreateProgram5;
                        GLES20.glAttachShader(glCreateProgram5, vertexShader5);
                        GLES20.glAttachShader(this.radialBlurShaderProgram, fragmentShader6);
                        int i6 = fragmentShader6;
                        GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.radialBlurShaderProgram);
                        int[] linkStatus5 = new int[1];
                        int i7 = vertexShader5;
                        float[] fArr13 = squareCoordinates2;
                        GLES20.glGetProgramiv(this.radialBlurShaderProgram, 35714, linkStatus5, 0);
                        if (linkStatus5[0] == 0) {
                            GLES20.glDeleteProgram(this.radialBlurShaderProgram);
                            this.radialBlurShaderProgram = 0;
                        } else {
                            this.radialBlurPositionHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "position");
                            this.radialBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "inputTexCoord");
                            this.radialBlurSourceImageHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "sourceImage");
                            this.radialBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "inputImageTexture2");
                            this.radialBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeSize");
                            this.radialBlurExcludePointHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludePoint");
                            this.radialBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeBlurSize");
                            this.radialBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "aspectRatio");
                        }
                        int vertexShader6 = loadShader(35633, simpleVertexShaderCode);
                        int fragmentShader7 = loadShader(35632, rgbToHsvFragmentShaderCode);
                        if (vertexShader6 == 0 || fragmentShader7 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram6 = GLES20.glCreateProgram();
                        this.rgbToHsvShaderProgram = glCreateProgram6;
                        GLES20.glAttachShader(glCreateProgram6, vertexShader6);
                        GLES20.glAttachShader(this.rgbToHsvShaderProgram, fragmentShader7);
                        GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.rgbToHsvShaderProgram);
                        int[] linkStatus6 = new int[1];
                        GLES20.glGetProgramiv(this.rgbToHsvShaderProgram, 35714, linkStatus6, 0);
                        if (linkStatus6[0] == 0) {
                            GLES20.glDeleteProgram(this.rgbToHsvShaderProgram);
                            this.rgbToHsvShaderProgram = 0;
                        } else {
                            this.rgbToHsvPositionHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "position");
                            this.rgbToHsvInputTexCoordHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "inputTexCoord");
                            this.rgbToHsvSourceImageHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram, "sourceImage");
                        }
                        int vertexShader7 = loadShader(35633, simpleVertexShaderCode);
                        int fragmentShader8 = loadShader(35632, enhanceFragmentShaderCode);
                        if (vertexShader7 == 0 || fragmentShader8 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram7 = GLES20.glCreateProgram();
                        this.enhanceShaderProgram = glCreateProgram7;
                        GLES20.glAttachShader(glCreateProgram7, vertexShader7);
                        GLES20.glAttachShader(this.enhanceShaderProgram, fragmentShader8);
                        GLES20.glBindAttribLocation(this.enhanceShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.enhanceShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.enhanceShaderProgram);
                        int[] linkStatus7 = new int[1];
                        GLES20.glGetProgramiv(this.enhanceShaderProgram, 35714, linkStatus7, 0);
                        if (linkStatus7[0] == 0) {
                            GLES20.glDeleteProgram(this.enhanceShaderProgram);
                            this.enhanceShaderProgram = 0;
                        } else {
                            this.enhancePositionHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "position");
                            this.enhanceInputTexCoordHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "inputTexCoord");
                            this.enhanceSourceImageHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "sourceImage");
                            this.enhanceIntensityHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "intensity");
                            this.enhanceInputImageTexture2Handle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "inputImageTexture2");
                        }
                        int vertexShader8 = loadShader(35633, simpleVertexShaderCode);
                        int fragmentShader9 = loadShader(35632, simpleFragmentShaderCode);
                        if (vertexShader8 == 0 || fragmentShader9 == 0) {
                            finish();
                            return false;
                        }
                        int glCreateProgram8 = GLES20.glCreateProgram();
                        this.simpleShaderProgram = glCreateProgram8;
                        GLES20.glAttachShader(glCreateProgram8, vertexShader8);
                        GLES20.glAttachShader(this.simpleShaderProgram, fragmentShader9);
                        GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                        GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                        GLES20.glLinkProgram(this.simpleShaderProgram);
                        int[] linkStatus8 = new int[1];
                        GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, linkStatus8, 0);
                        if (linkStatus8[0] == 0) {
                            GLES20.glDeleteProgram(this.simpleShaderProgram);
                            this.simpleShaderProgram = 0;
                        } else {
                            this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                            this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                            this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                        }
                        Bitmap bitmap = this.currentBitmap;
                        if (bitmap == null || bitmap.isRecycled()) {
                            return true;
                        }
                        loadTexture(this.currentBitmap);
                        return true;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                finish();
                return false;
            } else {
                int[] iArr15 = version;
                int[] iArr16 = configSpec;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x0153  */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x015a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void drawEnhancePass() {
            /*
                r32 = this;
                r1 = r32
                boolean r0 = r1.hsvGenerated
                r2 = 5
                r3 = 33984(0x84c0, float:4.7622E-41)
                r4 = 36064(0x8ce0, float:5.0536E-41)
                r5 = 4
                r6 = 36160(0x8d40, float:5.0671E-41)
                r7 = 1
                r8 = 0
                r9 = 3553(0xde1, float:4.979E-42)
                if (r0 != 0) goto L_0x010a
                int[] r0 = r1.renderFrameBuffer
                r0 = r0[r8]
                android.opengl.GLES20.glBindFramebuffer(r6, r0)
                int[] r0 = r1.renderTexture
                r0 = r0[r8]
                android.opengl.GLES20.glFramebufferTexture2D(r6, r4, r9, r0, r8)
                android.opengl.GLES20.glClear(r8)
                int r0 = r1.rgbToHsvShaderProgram
                android.opengl.GLES20.glUseProgram(r0)
                android.opengl.GLES20.glActiveTexture(r3)
                int[] r0 = r1.renderTexture
                r0 = r0[r7]
                android.opengl.GLES20.glBindTexture(r9, r0)
                int r0 = r1.rgbToHsvSourceImageHandle
                android.opengl.GLES20.glUniform1i(r0, r8)
                int r0 = r1.rgbToHsvInputTexCoordHandle
                android.opengl.GLES20.glEnableVertexAttribArray(r0)
                int r10 = r1.rgbToHsvInputTexCoordHandle
                r11 = 2
                r12 = 5126(0x1406, float:7.183E-42)
                r13 = 0
                r14 = 8
                java.nio.FloatBuffer r15 = r1.textureBuffer
                android.opengl.GLES20.glVertexAttribPointer(r10, r11, r12, r13, r14, r15)
                int r0 = r1.rgbToHsvPositionHandle
                android.opengl.GLES20.glEnableVertexAttribArray(r0)
                int r10 = r1.rgbToHsvPositionHandle
                java.nio.FloatBuffer r15 = r1.vertexBuffer
                android.opengl.GLES20.glVertexAttribPointer(r10, r11, r12, r13, r14, r15)
                android.opengl.GLES20.glDrawArrays(r2, r8, r5)
                int r0 = r1.renderBufferWidth
                int r10 = r1.renderBufferHeight
                int r0 = r0 * r10
                int r0 = r0 * 4
                java.nio.ByteBuffer r15 = java.nio.ByteBuffer.allocateDirect(r0)
                r10 = 0
                r11 = 0
                int r12 = r1.renderBufferWidth
                int r13 = r1.renderBufferHeight
                r14 = 6408(0x1908, float:8.98E-42)
                r0 = 5121(0x1401, float:7.176E-42)
                r19 = r15
                r15 = r0
                r16 = r19
                android.opengl.GLES20.glReadPixels(r10, r11, r12, r13, r14, r15, r16)
                int[] r0 = r1.enhanceTextures
                r0 = r0[r8]
                android.opengl.GLES20.glBindTexture(r9, r0)
                r15 = 10241(0x2801, float:1.435E-41)
                r14 = 9729(0x2601, float:1.3633E-41)
                android.opengl.GLES20.glTexParameteri(r9, r15, r14)
                r13 = 10240(0x2800, float:1.4349E-41)
                android.opengl.GLES20.glTexParameteri(r9, r13, r14)
                r12 = 10242(0x2802, float:1.4352E-41)
                r11 = 33071(0x812f, float:4.6342E-41)
                android.opengl.GLES20.glTexParameteri(r9, r12, r11)
                r10 = 10243(0x2803, float:1.4354E-41)
                android.opengl.GLES20.glTexParameteri(r9, r10, r11)
                r0 = 3553(0xde1, float:4.979E-42)
                r16 = 0
                r17 = 6408(0x1908, float:8.98E-42)
                int r13 = r1.renderBufferWidth
                int r14 = r1.renderBufferHeight
                r20 = 0
                r21 = 6408(0x1908, float:8.98E-42)
                r22 = 5121(0x1401, float:7.176E-42)
                r2 = 10243(0x2803, float:1.4354E-41)
                r10 = r0
                r5 = 33071(0x812f, float:4.6342E-41)
                r11 = r16
                r3 = 10242(0x2802, float:1.4352E-41)
                r12 = r17
                r4 = 10240(0x2800, float:1.4349E-41)
                r8 = 9729(0x2601, float:1.3633E-41)
                r6 = 10241(0x2801, float:1.435E-41)
                r15 = r20
                r16 = r21
                r17 = r22
                r18 = r19
                android.opengl.GLES20.glTexImage2D(r10, r11, r12, r13, r14, r15, r16, r17, r18)
                r10 = 0
                r0 = 16384(0x4000, float:2.2959E-41)
                java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r0)     // Catch:{ Exception -> 0x00da }
                r10 = r0
                int r0 = r1.renderBufferWidth     // Catch:{ Exception -> 0x00da }
                int r11 = r1.renderBufferHeight     // Catch:{ Exception -> 0x00da }
                r12 = r19
                im.bclpbkiauv.messenger.Utilities.calcCDT(r12, r0, r11, r10)     // Catch:{ Exception -> 0x00d8 }
                goto L_0x00e0
            L_0x00d8:
                r0 = move-exception
                goto L_0x00dd
            L_0x00da:
                r0 = move-exception
                r12 = r19
            L_0x00dd:
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00e0:
                int[] r0 = r1.enhanceTextures
                r0 = r0[r7]
                android.opengl.GLES20.glBindTexture(r9, r0)
                android.opengl.GLES20.glTexParameteri(r9, r6, r8)
                android.opengl.GLES20.glTexParameteri(r9, r4, r8)
                android.opengl.GLES20.glTexParameteri(r9, r3, r5)
                android.opengl.GLES20.glTexParameteri(r9, r2, r5)
                r23 = 3553(0xde1, float:4.979E-42)
                r24 = 0
                r25 = 6408(0x1908, float:8.98E-42)
                r26 = 256(0x100, float:3.59E-43)
                r27 = 16
                r28 = 0
                r29 = 6408(0x1908, float:8.98E-42)
                r30 = 5121(0x1401, float:7.176E-42)
                r31 = r10
                android.opengl.GLES20.glTexImage2D(r23, r24, r25, r26, r27, r28, r29, r30, r31)
                r1.hsvGenerated = r7
            L_0x010a:
                int[] r0 = r1.renderFrameBuffer
                r0 = r0[r7]
                r2 = 36160(0x8d40, float:5.0671E-41)
                android.opengl.GLES20.glBindFramebuffer(r2, r0)
                int[] r0 = r1.renderTexture
                r0 = r0[r7]
                r3 = 36064(0x8ce0, float:5.0536E-41)
                r4 = 0
                android.opengl.GLES20.glFramebufferTexture2D(r2, r3, r9, r0, r4)
                android.opengl.GLES20.glClear(r4)
                int r0 = r1.enhanceShaderProgram
                android.opengl.GLES20.glUseProgram(r0)
                r2 = 33984(0x84c0, float:4.7622E-41)
                android.opengl.GLES20.glActiveTexture(r2)
                int[] r0 = r1.enhanceTextures
                r0 = r0[r4]
                android.opengl.GLES20.glBindTexture(r9, r0)
                int r0 = r1.enhanceSourceImageHandle
                android.opengl.GLES20.glUniform1i(r0, r4)
                r0 = 33985(0x84c1, float:4.7623E-41)
                android.opengl.GLES20.glActiveTexture(r0)
                int[] r0 = r1.enhanceTextures
                r0 = r0[r7]
                android.opengl.GLES20.glBindTexture(r9, r0)
                int r0 = r1.enhanceInputImageTexture2Handle
                android.opengl.GLES20.glUniform1i(r0, r7)
                im.bclpbkiauv.ui.components.PhotoFilterView r0 = im.bclpbkiauv.ui.components.PhotoFilterView.this
                boolean r0 = r0.showOriginal
                if (r0 == 0) goto L_0x015a
                int r0 = r1.enhanceIntensityHandle
                r2 = 0
                android.opengl.GLES20.glUniform1f(r0, r2)
                goto L_0x0165
            L_0x015a:
                int r0 = r1.enhanceIntensityHandle
                im.bclpbkiauv.ui.components.PhotoFilterView r2 = im.bclpbkiauv.ui.components.PhotoFilterView.this
                float r2 = r2.getEnhanceValue()
                android.opengl.GLES20.glUniform1f(r0, r2)
            L_0x0165:
                int r0 = r1.enhanceInputTexCoordHandle
                android.opengl.GLES20.glEnableVertexAttribArray(r0)
                int r2 = r1.enhanceInputTexCoordHandle
                r3 = 2
                r4 = 5126(0x1406, float:7.183E-42)
                r5 = 0
                r6 = 8
                java.nio.FloatBuffer r7 = r1.textureBuffer
                android.opengl.GLES20.glVertexAttribPointer(r2, r3, r4, r5, r6, r7)
                int r0 = r1.enhancePositionHandle
                android.opengl.GLES20.glEnableVertexAttribArray(r0)
                int r2 = r1.enhancePositionHandle
                java.nio.FloatBuffer r7 = r1.vertexBuffer
                android.opengl.GLES20.glVertexAttribPointer(r2, r3, r4, r5, r6, r7)
                r2 = 5
                r3 = 4
                r4 = 0
                android.opengl.GLES20.glDrawArrays(r2, r4, r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.PhotoFilterView.EGLThread.drawEnhancePass():void");
        }

        /* access modifiers changed from: private */
        public void drawSharpenPass() {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.sharpenShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glUniform1i(this.sharpenSourceImageHandle, 0);
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.sharpenHandle, 0.0f);
            } else {
                GLES20.glUniform1f(this.sharpenHandle, PhotoFilterView.this.getSharpenValue());
            }
            GLES20.glUniform1f(this.sharpenWidthHandle, (float) this.renderBufferWidth);
            GLES20.glUniform1f(this.sharpenHeightHandle, (float) this.renderBufferHeight);
            GLES20.glEnableVertexAttribArray(this.sharpenInputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.sharpenInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.sharpenPositionHandle);
            GLES20.glVertexAttribPointer(this.sharpenPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glDrawArrays(5, 0, 4);
        }

        /* access modifiers changed from: private */
        public void drawCustomParamsPass() {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
            GLES20.glClear(0);
            GLES20.glUseProgram(this.toolsShaderProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glUniform1i(this.sourceImageHandle, 0);
            float f = 1.0f;
            if (PhotoFilterView.this.showOriginal) {
                GLES20.glUniform1f(this.shadowsHandle, 1.0f);
                GLES20.glUniform1f(this.highlightsHandle, 1.0f);
                GLES20.glUniform1f(this.exposureHandle, 0.0f);
                GLES20.glUniform1f(this.contrastHandle, 1.0f);
                GLES20.glUniform1f(this.saturationHandle, 1.0f);
                GLES20.glUniform1f(this.warmthHandle, 0.0f);
                GLES20.glUniform1f(this.vignetteHandle, 0.0f);
                GLES20.glUniform1f(this.grainHandle, 0.0f);
                GLES20.glUniform1f(this.fadeAmountHandle, 0.0f);
                GLES20.glUniform3f(this.highlightsTintColorHandle, 0.0f, 0.0f, 0.0f);
                GLES20.glUniform1f(this.highlightsTintIntensityHandle, 0.0f);
                GLES20.glUniform3f(this.shadowsTintColorHandle, 0.0f, 0.0f, 0.0f);
                GLES20.glUniform1f(this.shadowsTintIntensityHandle, 0.0f);
                GLES20.glUniform1f(this.skipToneHandle, 1.0f);
            } else {
                GLES20.glUniform1f(this.shadowsHandle, PhotoFilterView.this.getShadowsValue());
                GLES20.glUniform1f(this.highlightsHandle, PhotoFilterView.this.getHighlightsValue());
                GLES20.glUniform1f(this.exposureHandle, PhotoFilterView.this.getExposureValue());
                GLES20.glUniform1f(this.contrastHandle, PhotoFilterView.this.getContrastValue());
                GLES20.glUniform1f(this.saturationHandle, PhotoFilterView.this.getSaturationValue());
                GLES20.glUniform1f(this.warmthHandle, PhotoFilterView.this.getWarmthValue());
                GLES20.glUniform1f(this.vignetteHandle, PhotoFilterView.this.getVignetteValue());
                GLES20.glUniform1f(this.grainHandle, PhotoFilterView.this.getGrainValue());
                GLES20.glUniform1f(this.fadeAmountHandle, PhotoFilterView.this.getFadeValue());
                GLES20.glUniform3f(this.highlightsTintColorHandle, ((float) ((PhotoFilterView.this.tintHighlightsColor >> 16) & 255)) / 255.0f, ((float) ((PhotoFilterView.this.tintHighlightsColor >> 8) & 255)) / 255.0f, ((float) (PhotoFilterView.this.tintHighlightsColor & 255)) / 255.0f);
                GLES20.glUniform1f(this.highlightsTintIntensityHandle, PhotoFilterView.this.getTintHighlightsIntensityValue());
                GLES20.glUniform3f(this.shadowsTintColorHandle, ((float) ((PhotoFilterView.this.tintShadowsColor >> 16) & 255)) / 255.0f, ((float) ((PhotoFilterView.this.tintShadowsColor >> 8) & 255)) / 255.0f, ((float) (PhotoFilterView.this.tintShadowsColor & 255)) / 255.0f);
                GLES20.glUniform1f(this.shadowsTintIntensityHandle, PhotoFilterView.this.getTintShadowsIntensityValue());
                boolean skipTone = PhotoFilterView.this.curvesToolValue.shouldBeSkipped();
                int i = this.skipToneHandle;
                if (!skipTone) {
                    f = 0.0f;
                }
                GLES20.glUniform1f(i, f);
                if (!skipTone) {
                    PhotoFilterView.this.curvesToolValue.fillBuffer();
                    GLES20.glActiveTexture(33985);
                    GLES20.glBindTexture(3553, this.curveTextures[0]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, 10240, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLES20.glTexImage2D(3553, 0, 6408, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 1, 0, 6408, 5121, PhotoFilterView.this.curvesToolValue.curveBuffer);
                    GLES20.glUniform1i(this.curvesImageHandle, 1);
                }
            }
            GLES20.glUniform1f(this.widthHandle, (float) this.renderBufferWidth);
            GLES20.glUniform1f(this.heightHandle, (float) this.renderBufferHeight);
            GLES20.glEnableVertexAttribArray(this.inputTexCoordHandle);
            GLES20.glVertexAttribPointer(this.inputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.positionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            GLES20.glDrawArrays(5, 0, 4);
        }

        /* access modifiers changed from: private */
        public boolean drawBlurPass() {
            if (PhotoFilterView.this.showOriginal || PhotoFilterView.this.blurType == 0) {
                return false;
            }
            if (this.needUpdateBlurTexture) {
                GLES20.glUseProgram(this.blurShaderProgram);
                GLES20.glUniform1i(this.blurSourceImageHandle, 0);
                GLES20.glEnableVertexAttribArray(this.blurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.blurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.blurPositionHandle);
                GLES20.glVertexAttribPointer(this.blurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
                GLES20.glClear(0);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[1]);
                GLES20.glUniform1f(this.blurWidthHandle, 0.0f);
                GLES20.glUniform1f(this.blurHeightHandle, 1.0f / ((float) this.renderBufferHeight));
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[2]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[2], 0);
                GLES20.glClear(0);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.renderTexture[0]);
                GLES20.glUniform1f(this.blurWidthHandle, 1.0f / ((float) this.renderBufferWidth));
                GLES20.glUniform1f(this.blurHeightHandle, 0.0f);
                GLES20.glDrawArrays(5, 0, 4);
                this.needUpdateBlurTexture = false;
            }
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
            GLES20.glClear(0);
            if (PhotoFilterView.this.blurType == 1) {
                GLES20.glUseProgram(this.radialBlurShaderProgram);
                GLES20.glUniform1i(this.radialBlurSourceImageHandle, 0);
                GLES20.glUniform1i(this.radialBlurSourceImage2Handle, 1);
                GLES20.glUniform1f(this.radialBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                GLES20.glUniform1f(this.radialBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                GLES20.glUniform2f(this.radialBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.x, PhotoFilterView.this.blurExcludePoint.y);
                GLES20.glUniform1f(this.radialBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
                GLES20.glEnableVertexAttribArray(this.radialBlurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.radialBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.radialBlurPositionHandle);
                GLES20.glVertexAttribPointer(this.radialBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            } else if (PhotoFilterView.this.blurType == 2) {
                GLES20.glUseProgram(this.linearBlurShaderProgram);
                GLES20.glUniform1i(this.linearBlurSourceImageHandle, 0);
                GLES20.glUniform1i(this.linearBlurSourceImage2Handle, 1);
                GLES20.glUniform1f(this.linearBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
                GLES20.glUniform1f(this.linearBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
                GLES20.glUniform1f(this.linearBlurAngleHandle, PhotoFilterView.this.blurAngle);
                GLES20.glUniform2f(this.linearBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.x, PhotoFilterView.this.blurExcludePoint.y);
                GLES20.glUniform1f(this.linearBlurAspectRatioHandle, ((float) this.renderBufferHeight) / ((float) this.renderBufferWidth));
                GLES20.glEnableVertexAttribArray(this.linearBlurInputTexCoordHandle);
                GLES20.glVertexAttribPointer(this.linearBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.linearBlurPositionHandle);
                GLES20.glVertexAttribPointer(this.linearBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
            }
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, this.renderTexture[2]);
            GLES20.glDrawArrays(5, 0, 4);
            return true;
        }

        private Bitmap getRenderBufferBitmap() {
            ByteBuffer buffer = ByteBuffer.allocateDirect(this.renderBufferWidth * this.renderBufferHeight * 4);
            GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, buffer);
            Bitmap bitmap = Bitmap.createBitmap(this.renderBufferWidth, this.renderBufferHeight, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            return bitmap;
        }

        public Bitmap getTexture() {
            if (!this.initied) {
                return null;
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Bitmap[] object = new Bitmap[1];
            try {
                postRunnable(new Runnable(object, countDownLatch) {
                    private final /* synthetic */ Bitmap[] f$1;
                    private final /* synthetic */ CountDownLatch f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PhotoFilterView.EGLThread.this.lambda$getTexture$0$PhotoFilterView$EGLThread(this.f$1, this.f$2);
                    }
                });
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            return object[0];
        }

        public /* synthetic */ void lambda$getTexture$0$PhotoFilterView$EGLThread(Bitmap[] object, CountDownLatch countDownLatch) {
            GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[true ^ this.blured], 0);
            GLES20.glClear(0);
            object[0] = getRenderBufferBitmap();
            countDownLatch.countDown();
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glClear(0);
        }

        private Bitmap createBitmap(Bitmap bitmap, int w, int h, float scale) {
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postRotate((float) PhotoFilterView.this.orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        private void loadTexture(Bitmap bitmap) {
            this.renderBufferWidth = bitmap.getWidth();
            this.renderBufferHeight = bitmap.getHeight();
            float maxSize = (float) AndroidUtilities.getPhotoSize();
            if (((float) this.renderBufferWidth) > maxSize || ((float) this.renderBufferHeight) > maxSize || PhotoFilterView.this.orientation % 360 != 0) {
                float scale = 1.0f;
                if (((float) this.renderBufferWidth) > maxSize || ((float) this.renderBufferHeight) > maxSize) {
                    float scaleX = maxSize / ((float) bitmap.getWidth());
                    float scaleY = maxSize / ((float) bitmap.getHeight());
                    if (scaleX < scaleY) {
                        this.renderBufferWidth = (int) maxSize;
                        this.renderBufferHeight = (int) (((float) bitmap.getHeight()) * scaleX);
                        scale = scaleX;
                    } else {
                        this.renderBufferHeight = (int) maxSize;
                        this.renderBufferWidth = (int) (((float) bitmap.getWidth()) * scaleY);
                        scale = scaleY;
                    }
                }
                if (PhotoFilterView.this.orientation % 360 == 90 || PhotoFilterView.this.orientation % 360 == 270) {
                    int temp = this.renderBufferWidth;
                    this.renderBufferWidth = this.renderBufferHeight;
                    this.renderBufferHeight = temp;
                }
                this.currentBitmap = createBitmap(bitmap, this.renderBufferWidth, this.renderBufferHeight, scale);
            } else {
                Bitmap bitmap2 = bitmap;
            }
            GLES20.glGenFramebuffers(3, this.renderFrameBuffer, 0);
            GLES20.glGenTextures(3, this.renderTexture, 0);
            GLES20.glBindTexture(3553, this.renderTexture[0]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
            GLES20.glBindTexture(3553, this.renderTexture[1]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, this.currentBitmap, 0);
            GLES20.glBindTexture(3553, this.renderTexture[2]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, (Buffer) null);
        }

        public void shutdown() {
            postRunnable(new Runnable() {
                public final void run() {
                    PhotoFilterView.EGLThread.this.lambda$shutdown$1$PhotoFilterView$EGLThread();
                }
            });
        }

        public /* synthetic */ void lambda$shutdown$1$PhotoFilterView$EGLThread() {
            finish();
            this.currentBitmap = null;
            Looper looper = Looper.myLooper();
            if (looper != null) {
                looper.quit();
            }
        }

        public void setSurfaceTextureSize(int width, int height) {
            this.surfaceWidth = width;
            this.surfaceHeight = height;
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void requestRender(boolean updateBlur) {
            requestRender(updateBlur, false);
        }

        public void requestRender(boolean updateBlur, boolean force) {
            postRunnable(new Runnable(updateBlur, force) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PhotoFilterView.EGLThread.this.lambda$requestRender$2$PhotoFilterView$EGLThread(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$requestRender$2$PhotoFilterView$EGLThread(boolean updateBlur, boolean force) {
            if (!this.needUpdateBlurTexture) {
                this.needUpdateBlurTexture = updateBlur;
            }
            long newTime = System.currentTimeMillis();
            if (force || Math.abs(this.lastRenderCallTime - newTime) > 30) {
                this.lastRenderCallTime = newTime;
                this.drawRunnable.run();
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoFilterView(Context context, Bitmap bitmap, int rotation, MediaController.SavedFilterState state) {
        super(context);
        Context context2 = context;
        MediaController.SavedFilterState savedFilterState = state;
        if (savedFilterState != null) {
            this.enhanceValue = savedFilterState.enhanceValue;
            this.exposureValue = savedFilterState.exposureValue;
            this.contrastValue = savedFilterState.contrastValue;
            this.warmthValue = savedFilterState.warmthValue;
            this.saturationValue = savedFilterState.saturationValue;
            this.fadeValue = savedFilterState.fadeValue;
            this.tintShadowsColor = savedFilterState.tintShadowsColor;
            this.tintHighlightsColor = savedFilterState.tintHighlightsColor;
            this.highlightsValue = savedFilterState.highlightsValue;
            this.shadowsValue = savedFilterState.shadowsValue;
            this.vignetteValue = savedFilterState.vignetteValue;
            this.grainValue = savedFilterState.grainValue;
            this.blurType = savedFilterState.blurType;
            this.sharpenValue = savedFilterState.sharpenValue;
            this.curvesToolValue = savedFilterState.curvesToolValue;
            this.blurExcludeSize = savedFilterState.blurExcludeSize;
            this.blurExcludePoint = savedFilterState.blurExcludePoint;
            this.blurExcludeBlurSize = savedFilterState.blurExcludeBlurSize;
            this.blurAngle = savedFilterState.blurAngle;
            this.lastState = savedFilterState;
        } else {
            this.curvesToolValue = new CurvesToolValue();
            this.blurExcludeSize = 0.35f;
            this.blurExcludePoint = new Point(0.5f, 0.5f);
            this.blurExcludeBlurSize = 0.15f;
            this.blurAngle = 1.5707964f;
        }
        this.bitmapToEdit = bitmap;
        this.orientation = rotation;
        TextureView textureView2 = new TextureView(context2);
        this.textureView = textureView2;
        addView(textureView2, LayoutHelper.createFrame(-1, -1, 51));
        this.textureView.setVisibility(4);
        this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (PhotoFilterView.this.eglThread == null && surface != null) {
                    PhotoFilterView photoFilterView = PhotoFilterView.this;
                    PhotoFilterView photoFilterView2 = PhotoFilterView.this;
                    EGLThread unused = photoFilterView.eglThread = new EGLThread(surface, photoFilterView2.bitmapToEdit);
                    PhotoFilterView.this.eglThread.setSurfaceTextureSize(width, height);
                    PhotoFilterView.this.eglThread.requestRender(true, true);
                }
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (PhotoFilterView.this.eglThread != null) {
                    PhotoFilterView.this.eglThread.setSurfaceTextureSize(width, height);
                    PhotoFilterView.this.eglThread.requestRender(false, true);
                    PhotoFilterView.this.eglThread.postRunnable(new Runnable() {
                        public final void run() {
                            PhotoFilterView.AnonymousClass1.this.lambda$onSurfaceTextureSizeChanged$0$PhotoFilterView$1();
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$onSurfaceTextureSizeChanged$0$PhotoFilterView$1() {
                if (PhotoFilterView.this.eglThread != null) {
                    PhotoFilterView.this.eglThread.requestRender(false, true);
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (PhotoFilterView.this.eglThread == null) {
                    return true;
                }
                PhotoFilterView.this.eglThread.shutdown();
                EGLThread unused = PhotoFilterView.this.eglThread = null;
                return true;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        PhotoFilterBlurControl photoFilterBlurControl = new PhotoFilterBlurControl(context2);
        this.blurControl = photoFilterBlurControl;
        photoFilterBlurControl.setVisibility(4);
        addView(this.blurControl, LayoutHelper.createFrame(-1, -1, 51));
        this.blurControl.setDelegate(new PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate() {
            public final void valueChanged(Point point, float f, float f2, float f3) {
                PhotoFilterView.this.lambda$new$0$PhotoFilterView(point, f, f2, f3);
            }
        });
        PhotoFilterCurvesControl photoFilterCurvesControl = new PhotoFilterCurvesControl(context2, this.curvesToolValue);
        this.curvesControl = photoFilterCurvesControl;
        photoFilterCurvesControl.setDelegate(new PhotoFilterCurvesControl.PhotoFilterCurvesControlDelegate() {
            public final void valueChanged() {
                PhotoFilterView.this.lambda$new$1$PhotoFilterView();
            }
        });
        this.curvesControl.setVisibility(4);
        addView(this.curvesControl, LayoutHelper.createFrame(-1, -1, 51));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.toolsView = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(-1, 186, 83));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        frameLayout2.setBackgroundColor(-16777216);
        this.toolsView.addView(frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        TextView textView = new TextView(context2);
        this.cancelTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout2.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        TextView textView2 = new TextView(context2);
        this.doneTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(-11420173);
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout2.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        LinearLayout linearLayout = new LinearLayout(context2);
        frameLayout2.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 1));
        ImageView imageView = new ImageView(context2);
        this.tuneItem = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.tuneItem.setImageResource(R.drawable.photo_tools);
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(-9649153, PorterDuff.Mode.MULTIPLY));
        this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.tuneItem, LayoutHelper.createLinear(56, 48));
        this.tuneItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$2$PhotoFilterView(view);
            }
        });
        ImageView imageView2 = new ImageView(context2);
        this.blurItem = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.blurItem.setImageResource(R.drawable.tool_blur);
        this.blurItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.blurItem, LayoutHelper.createLinear(56, 48));
        this.blurItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$3$PhotoFilterView(view);
            }
        });
        ImageView imageView3 = new ImageView(context2);
        this.curveItem = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.curveItem.setImageResource(R.drawable.tool_curve);
        this.curveItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        linearLayout.addView(this.curveItem, LayoutHelper.createLinear(56, 48));
        this.curveItem.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$4$PhotoFilterView(view);
            }
        });
        this.recyclerListView = new RecyclerListView(context2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context2);
        layoutManager.setOrientation(1);
        this.recyclerListView.setLayoutManager(layoutManager);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.setOverScrollMode(2);
        this.recyclerListView.setAdapter(new ToolsAdapter(context2));
        this.toolsView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, 120, 51));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.curveLayout = frameLayout3;
        frameLayout3.setVisibility(4);
        this.toolsView.addView(this.curveLayout, LayoutHelper.createFrame(-1.0f, 78.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        LinearLayout curveTextViewContainer = new LinearLayout(context2);
        curveTextViewContainer.setOrientation(0);
        this.curveLayout.addView(curveTextViewContainer, LayoutHelper.createFrame(-2, -2, 1));
        int a = 0;
        while (a < 4) {
            FrameLayout frameLayout1 = new FrameLayout(context2);
            frameLayout1.setTag(Integer.valueOf(a));
            this.curveRadioButton[a] = new RadioButton(context2);
            this.curveRadioButton[a].setSize(AndroidUtilities.dp(20.0f));
            frameLayout1.addView(this.curveRadioButton[a], LayoutHelper.createFrame(30, 30, 49));
            TextView curveTextView = new TextView(context2);
            curveTextView.setTextSize(1, 12.0f);
            curveTextView.setGravity(16);
            if (a == 0) {
                String str = LocaleController.getString("CurvesAll", R.string.CurvesAll);
                curveTextView.setText(str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase());
                curveTextView.setTextColor(-1);
                this.curveRadioButton[a].setColor(-1, -1);
            } else if (a == 1) {
                String str2 = LocaleController.getString("CurvesRed", R.string.CurvesRed);
                curveTextView.setText(str2.substring(0, 1).toUpperCase() + str2.substring(1).toLowerCase());
                curveTextView.setTextColor(-1684147);
                this.curveRadioButton[a].setColor(-1684147, -1684147);
            } else if (a == 2) {
                String str3 = LocaleController.getString("CurvesGreen", R.string.CurvesGreen);
                curveTextView.setText(str3.substring(0, 1).toUpperCase() + str3.substring(1).toLowerCase());
                curveTextView.setTextColor(-10831009);
                this.curveRadioButton[a].setColor(-10831009, -10831009);
            } else if (a == 3) {
                String str4 = LocaleController.getString("CurvesBlue", R.string.CurvesBlue);
                curveTextView.setText(str4.substring(0, 1).toUpperCase() + str4.substring(1).toLowerCase());
                curveTextView.setTextColor(-12734994);
                this.curveRadioButton[a].setColor(-12734994, -12734994);
            }
            frameLayout1.addView(curveTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 49, 0.0f, 38.0f, 0.0f, 0.0f));
            curveTextViewContainer.addView(frameLayout1, LayoutHelper.createLinear(-2, -2, a == 0 ? 0.0f : 30.0f, 0.0f, 0.0f, 0.0f));
            frameLayout1.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoFilterView.this.lambda$new$5$PhotoFilterView(view);
                }
            });
            a++;
        }
        FrameLayout frameLayout4 = new FrameLayout(context2);
        this.blurLayout = frameLayout4;
        frameLayout4.setVisibility(4);
        this.toolsView.addView(this.blurLayout, LayoutHelper.createFrame(280.0f, 60.0f, 1, 0.0f, 40.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.blurOffButton = textView3;
        textView3.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurOffButton.setTextSize(1, 13.0f);
        this.blurOffButton.setGravity(1);
        this.blurOffButton.setText(LocaleController.getString("BlurOff", R.string.BlurOff));
        this.blurLayout.addView(this.blurOffButton, LayoutHelper.createFrame(80, 60.0f));
        this.blurOffButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$6$PhotoFilterView(view);
            }
        });
        TextView textView4 = new TextView(context2);
        this.blurRadialButton = textView4;
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurRadialButton.setTextSize(1, 13.0f);
        this.blurRadialButton.setGravity(1);
        this.blurRadialButton.setText(LocaleController.getString("BlurRadial", R.string.BlurRadial));
        this.blurLayout.addView(this.blurRadialButton, LayoutHelper.createFrame(80.0f, 80.0f, 51, 100.0f, 0.0f, 0.0f, 0.0f));
        this.blurRadialButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$7$PhotoFilterView(view);
            }
        });
        TextView textView5 = new TextView(context2);
        this.blurLinearButton = textView5;
        textView5.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        this.blurLinearButton.setTextSize(1, 13.0f);
        this.blurLinearButton.setGravity(1);
        this.blurLinearButton.setText(LocaleController.getString("BlurLinear", R.string.BlurLinear));
        this.blurLayout.addView(this.blurLinearButton, LayoutHelper.createFrame(80.0f, 80.0f, 51, 200.0f, 0.0f, 0.0f, 0.0f));
        this.blurLinearButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoFilterView.this.lambda$new$8$PhotoFilterView(view);
            }
        });
        updateSelectedBlurType();
        if (Build.VERSION.SDK_INT >= 21) {
            ((FrameLayout.LayoutParams) this.textureView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
            ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
        }
    }

    public /* synthetic */ void lambda$new$0$PhotoFilterView(Point centerPoint, float falloff, float size, float angle) {
        this.blurExcludeSize = size;
        this.blurExcludePoint = centerPoint;
        this.blurExcludeBlurSize = falloff;
        this.blurAngle = angle;
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$1$PhotoFilterView() {
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$2$PhotoFilterView(View v) {
        this.selectedTool = 0;
        this.tuneItem.setColorFilter(new PorterDuffColorFilter(-9649153, PorterDuff.Mode.MULTIPLY));
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    public /* synthetic */ void lambda$new$3$PhotoFilterView(View v) {
        this.selectedTool = 1;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter(new PorterDuffColorFilter(-9649153, PorterDuff.Mode.MULTIPLY));
        this.curveItem.setColorFilter((ColorFilter) null);
        switchMode();
    }

    public /* synthetic */ void lambda$new$4$PhotoFilterView(View v) {
        this.selectedTool = 2;
        this.tuneItem.setColorFilter((ColorFilter) null);
        this.blurItem.setColorFilter((ColorFilter) null);
        this.curveItem.setColorFilter(new PorterDuffColorFilter(-9649153, PorterDuff.Mode.MULTIPLY));
        switchMode();
    }

    public /* synthetic */ void lambda$new$5$PhotoFilterView(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.curvesToolValue.activeType = num;
        int a1 = 0;
        while (a1 < 4) {
            this.curveRadioButton[a1].setChecked(a1 == num, true);
            a1++;
        }
        this.curvesControl.invalidate();
    }

    public /* synthetic */ void lambda$new$6$PhotoFilterView(View v) {
        this.blurType = 0;
        updateSelectedBlurType();
        this.blurControl.setVisibility(4);
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$7$PhotoFilterView(View v) {
        this.blurType = 1;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(1);
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    public /* synthetic */ void lambda$new$8$PhotoFilterView(View v) {
        this.blurType = 2;
        updateSelectedBlurType();
        this.blurControl.setVisibility(0);
        this.blurControl.setType(0);
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.requestRender(false);
        }
    }

    private void updateSelectedBlurType() {
        int i = this.blurType;
        if (i == 0) {
            Drawable drawable = this.blurOffButton.getContext().getResources().getDrawable(R.drawable.blur_off).mutate();
            drawable.setColorFilter(new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY));
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
            this.blurOffButton.setTextColor(-11420173);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blur_radial, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blur_linear, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 1) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blur_off, 0, 0);
            this.blurOffButton.setTextColor(-1);
            Drawable drawable2 = this.blurOffButton.getContext().getResources().getDrawable(R.drawable.blur_radial).mutate();
            drawable2.setColorFilter(new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY));
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable2, (Drawable) null, (Drawable) null);
            this.blurRadialButton.setTextColor(-11420173);
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blur_linear, 0, 0);
            this.blurLinearButton.setTextColor(-1);
        } else if (i == 2) {
            this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blur_off, 0, 0);
            this.blurOffButton.setTextColor(-1);
            this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blur_radial, 0, 0);
            this.blurRadialButton.setTextColor(-1);
            Drawable drawable3 = this.blurOffButton.getContext().getResources().getDrawable(R.drawable.blur_linear).mutate();
            drawable3.setColorFilter(new PorterDuffColorFilter(-11420173, PorterDuff.Mode.MULTIPLY));
            this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable3, (Drawable) null, (Drawable) null);
            this.blurLinearButton.setTextColor(-11420173);
        }
    }

    public MediaController.SavedFilterState getSavedFilterState() {
        MediaController.SavedFilterState state = new MediaController.SavedFilterState();
        state.enhanceValue = this.enhanceValue;
        state.exposureValue = this.exposureValue;
        state.contrastValue = this.contrastValue;
        state.warmthValue = this.warmthValue;
        state.saturationValue = this.saturationValue;
        state.fadeValue = this.fadeValue;
        state.tintShadowsColor = this.tintShadowsColor;
        state.tintHighlightsColor = this.tintHighlightsColor;
        state.highlightsValue = this.highlightsValue;
        state.shadowsValue = this.shadowsValue;
        state.vignetteValue = this.vignetteValue;
        state.grainValue = this.grainValue;
        state.blurType = this.blurType;
        state.sharpenValue = this.sharpenValue;
        state.curvesToolValue = this.curvesToolValue;
        state.blurExcludeSize = this.blurExcludeSize;
        state.blurExcludePoint = this.blurExcludePoint;
        state.blurExcludeBlurSize = this.blurExcludeBlurSize;
        state.blurAngle = this.blurAngle;
        return state;
    }

    public boolean hasChanges() {
        MediaController.SavedFilterState savedFilterState = this.lastState;
        if (savedFilterState != null) {
            if (this.enhanceValue == savedFilterState.enhanceValue && this.contrastValue == this.lastState.contrastValue && this.highlightsValue == this.lastState.highlightsValue && this.exposureValue == this.lastState.exposureValue && this.warmthValue == this.lastState.warmthValue && this.saturationValue == this.lastState.saturationValue && this.vignetteValue == this.lastState.vignetteValue && this.shadowsValue == this.lastState.shadowsValue && this.grainValue == this.lastState.grainValue && this.sharpenValue == this.lastState.sharpenValue && this.fadeValue == this.lastState.fadeValue && this.tintHighlightsColor == this.lastState.tintHighlightsColor && this.tintShadowsColor == this.lastState.tintShadowsColor && this.curvesToolValue.shouldBeSkipped()) {
                return false;
            }
            return true;
        } else if (this.enhanceValue == 0.0f && this.contrastValue == 0.0f && this.highlightsValue == 0.0f && this.exposureValue == 0.0f && this.warmthValue == 0.0f && this.saturationValue == 0.0f && this.vignetteValue == 0.0f && this.shadowsValue == 0.0f && this.grainValue == 0.0f && this.sharpenValue == 0.0f && this.fadeValue == 0.0f && this.tintHighlightsColor == 0 && this.tintShadowsColor == 0 && this.curvesToolValue.shouldBeSkipped()) {
            return false;
        } else {
            return true;
        }
    }

    public void onTouch(MotionEvent event) {
        if (event.getActionMasked() == 0 || event.getActionMasked() == 5) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            if (layoutParams != null && event.getX() >= ((float) layoutParams.leftMargin) && event.getY() >= ((float) layoutParams.topMargin) && event.getX() <= ((float) (layoutParams.leftMargin + layoutParams.width)) && event.getY() <= ((float) (layoutParams.topMargin + layoutParams.height))) {
                setShowOriginal(true);
            }
        } else if (event.getActionMasked() == 1 || event.getActionMasked() == 6) {
            setShowOriginal(false);
        }
    }

    private void setShowOriginal(boolean value) {
        if (this.showOriginal != value) {
            this.showOriginal = value;
            EGLThread eGLThread = this.eglThread;
            if (eGLThread != null) {
                eGLThread.requestRender(false);
            }
        }
    }

    public void switchMode() {
        int i = this.selectedTool;
        if (i == 0) {
            this.blurControl.setVisibility(4);
            this.blurLayout.setVisibility(4);
            this.curveLayout.setVisibility(4);
            this.curvesControl.setVisibility(4);
            this.recyclerListView.setVisibility(0);
        } else if (i == 1) {
            this.recyclerListView.setVisibility(4);
            this.curveLayout.setVisibility(4);
            this.curvesControl.setVisibility(4);
            this.blurLayout.setVisibility(0);
            if (this.blurType != 0) {
                this.blurControl.setVisibility(0);
            }
            updateSelectedBlurType();
        } else if (i == 2) {
            this.recyclerListView.setVisibility(4);
            this.blurLayout.setVisibility(4);
            this.blurControl.setVisibility(4);
            this.curveLayout.setVisibility(0);
            this.curvesControl.setVisibility(0);
            this.curvesToolValue.activeType = 0;
            int a = 0;
            while (a < 4) {
                this.curveRadioButton[a].setChecked(a == 0, false);
                a++;
            }
        }
    }

    public void shutdown() {
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            eGLThread.shutdown();
            this.eglThread = null;
        }
        this.textureView.setVisibility(8);
    }

    public void init() {
        this.textureView.setVisibility(0);
    }

    public Bitmap getBitmap() {
        EGLThread eGLThread = this.eglThread;
        if (eGLThread != null) {
            return eGLThread.getTexture();
        }
        return null;
    }

    private void fixLayout(int viewWidth, int viewHeight) {
        float bitmapH;
        float bitmapW;
        float bitmapH2;
        float bitmapW2;
        if (this.bitmapToEdit != null) {
            int viewWidth2 = viewWidth - AndroidUtilities.dp(28.0f);
            int viewHeight2 = viewHeight - (AndroidUtilities.dp(214.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
            int i = this.orientation;
            if (i % 360 == 90 || i % 360 == 270) {
                bitmapW = (float) this.bitmapToEdit.getHeight();
                bitmapH = (float) this.bitmapToEdit.getWidth();
            } else {
                bitmapW = (float) this.bitmapToEdit.getWidth();
                bitmapH = (float) this.bitmapToEdit.getHeight();
            }
            float scaleX = ((float) viewWidth2) / bitmapW;
            float scaleY = ((float) viewHeight2) / bitmapH;
            if (scaleX > scaleY) {
                bitmapH2 = (float) viewHeight2;
                bitmapW2 = (float) ((int) Math.ceil((double) (bitmapW * scaleY)));
            } else {
                bitmapW2 = (float) viewWidth2;
                bitmapH2 = (float) ((int) Math.ceil((double) (bitmapH * scaleX)));
            }
            int bitmapX = (int) Math.ceil((double) (((((float) viewWidth2) - bitmapW2) / 2.0f) + ((float) AndroidUtilities.dp(14.0f))));
            int bitmapY = (int) Math.ceil((double) (((((float) viewHeight2) - bitmapH2) / 2.0f) + ((float) AndroidUtilities.dp(14.0f)) + ((float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textureView.getLayoutParams();
            layoutParams.leftMargin = bitmapX;
            layoutParams.topMargin = bitmapY;
            layoutParams.width = (int) bitmapW2;
            layoutParams.height = (int) bitmapH2;
            this.curvesControl.setActualArea((float) bitmapX, (float) (bitmapY - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)), (float) layoutParams.width, (float) layoutParams.height);
            this.blurControl.setActualAreaSize((float) layoutParams.width, (float) layoutParams.height);
            ((FrameLayout.LayoutParams) this.blurControl.getLayoutParams()).height = AndroidUtilities.dp(38.0f) + viewHeight2;
            ((FrameLayout.LayoutParams) this.curvesControl.getLayoutParams()).height = AndroidUtilities.dp(28.0f) + viewHeight2;
            if (AndroidUtilities.isTablet()) {
                int total = AndroidUtilities.dp(86.0f) * 10;
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.recyclerListView.getLayoutParams();
                if (total < viewWidth2) {
                    layoutParams2.width = total;
                    layoutParams2.leftMargin = (viewWidth2 - total) / 2;
                    return;
                }
                layoutParams2.width = -1;
                layoutParams2.leftMargin = 0;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        fixLayout(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* access modifiers changed from: private */
    public float getShadowsValue() {
        return ((this.shadowsValue * 0.55f) + 100.0f) / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getHighlightsValue() {
        return ((this.highlightsValue * 0.75f) + 100.0f) / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getEnhanceValue() {
        return this.enhanceValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getExposureValue() {
        return this.exposureValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getContrastValue() {
        return ((this.contrastValue / 100.0f) * 0.3f) + 1.0f;
    }

    /* access modifiers changed from: private */
    public float getWarmthValue() {
        return this.warmthValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getVignetteValue() {
        return this.vignetteValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getSharpenValue() {
        return ((this.sharpenValue / 100.0f) * 0.6f) + 0.11f;
    }

    /* access modifiers changed from: private */
    public float getGrainValue() {
        return (this.grainValue / 100.0f) * 0.04f;
    }

    /* access modifiers changed from: private */
    public float getFadeValue() {
        return this.fadeValue / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getTintHighlightsIntensityValue() {
        if (this.tintHighlightsColor == 0) {
            return 0.0f;
        }
        return 50.0f / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getTintShadowsIntensityValue() {
        if (this.tintShadowsColor == 0) {
            return 0.0f;
        }
        return 50.0f / 100.0f;
    }

    /* access modifiers changed from: private */
    public float getSaturationValue() {
        float parameterValue = this.saturationValue / 100.0f;
        if (parameterValue > 0.0f) {
            parameterValue *= 1.05f;
        }
        return 1.0f + parameterValue;
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public class ToolsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ToolsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return 13;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: im.bclpbkiauv.ui.cells.PhotoEditRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: im.bclpbkiauv.ui.cells.PhotoEditRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: im.bclpbkiauv.ui.cells.PhotoEditToolCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: im.bclpbkiauv.ui.cells.PhotoEditRadioCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                if (r5 != 0) goto L_0x0013
                im.bclpbkiauv.ui.cells.PhotoEditToolCell r0 = new im.bclpbkiauv.ui.cells.PhotoEditToolCell
                android.content.Context r1 = r3.mContext
                r0.<init>(r1)
                r1 = r0
                im.bclpbkiauv.ui.components.-$$Lambda$PhotoFilterView$ToolsAdapter$fWPUDE8DQghWW6KMo8OYyEwZ9vg r2 = new im.bclpbkiauv.ui.components.-$$Lambda$PhotoFilterView$ToolsAdapter$fWPUDE8DQghWW6KMo8OYyEwZ9vg
                r2.<init>()
                r0.setSeekBarDelegate(r2)
                goto L_0x0023
            L_0x0013:
                im.bclpbkiauv.ui.cells.PhotoEditRadioCell r0 = new im.bclpbkiauv.ui.cells.PhotoEditRadioCell
                android.content.Context r1 = r3.mContext
                r0.<init>(r1)
                r1 = r0
                im.bclpbkiauv.ui.components.-$$Lambda$PhotoFilterView$ToolsAdapter$riqgbuBCeEppl8rcfuRBUPUBCns r0 = new im.bclpbkiauv.ui.components.-$$Lambda$PhotoFilterView$ToolsAdapter$riqgbuBCeEppl8rcfuRBUPUBCns
                r0.<init>()
                r1.setOnClickListener(r0)
            L_0x0023:
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r0 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.PhotoFilterView.ToolsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoFilterView$ToolsAdapter(int i1, int progress) {
            if (i1 == PhotoFilterView.this.enhanceTool) {
                float unused = PhotoFilterView.this.enhanceValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.highlightsTool) {
                float unused2 = PhotoFilterView.this.highlightsValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.contrastTool) {
                float unused3 = PhotoFilterView.this.contrastValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.exposureTool) {
                float unused4 = PhotoFilterView.this.exposureValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.warmthTool) {
                float unused5 = PhotoFilterView.this.warmthValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.saturationTool) {
                float unused6 = PhotoFilterView.this.saturationValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.vignetteTool) {
                float unused7 = PhotoFilterView.this.vignetteValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.shadowsTool) {
                float unused8 = PhotoFilterView.this.shadowsValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.grainTool) {
                float unused9 = PhotoFilterView.this.grainValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.sharpenTool) {
                float unused10 = PhotoFilterView.this.sharpenValue = (float) progress;
            } else if (i1 == PhotoFilterView.this.fadeTool) {
                float unused11 = PhotoFilterView.this.fadeValue = (float) progress;
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(true);
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$PhotoFilterView$ToolsAdapter(View v) {
            PhotoEditRadioCell cell = (PhotoEditRadioCell) v;
            if (((Integer) cell.getTag()).intValue() == PhotoFilterView.this.tintShadowsTool) {
                int unused = PhotoFilterView.this.tintShadowsColor = cell.getCurrentColor();
            } else {
                int unused2 = PhotoFilterView.this.tintHighlightsColor = cell.getCurrentColor();
            }
            if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                PhotoEditToolCell cell = (PhotoEditToolCell) holder.itemView;
                cell.setTag(Integer.valueOf(i));
                if (i == PhotoFilterView.this.enhanceTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Enhance", R.string.Enhance), PhotoFilterView.this.enhanceValue, 0, 100);
                } else if (i == PhotoFilterView.this.highlightsTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Highlights", R.string.Highlights), PhotoFilterView.this.highlightsValue, -100, 100);
                } else if (i == PhotoFilterView.this.contrastTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString(ExifInterface.TAG_CONTRAST, R.string.Contrast), PhotoFilterView.this.contrastValue, -100, 100);
                } else if (i == PhotoFilterView.this.exposureTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Exposure", R.string.Exposure), PhotoFilterView.this.exposureValue, -100, 100);
                } else if (i == PhotoFilterView.this.warmthTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Warmth", R.string.Warmth), PhotoFilterView.this.warmthValue, -100, 100);
                } else if (i == PhotoFilterView.this.saturationTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString(ExifInterface.TAG_SATURATION, R.string.Saturation), PhotoFilterView.this.saturationValue, -100, 100);
                } else if (i == PhotoFilterView.this.vignetteTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Vignette", R.string.Vignette), PhotoFilterView.this.vignetteValue, 0, 100);
                } else if (i == PhotoFilterView.this.shadowsTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Shadows", R.string.Shadows), PhotoFilterView.this.shadowsValue, -100, 100);
                } else if (i == PhotoFilterView.this.grainTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Grain", R.string.Grain), PhotoFilterView.this.grainValue, 0, 100);
                } else if (i == PhotoFilterView.this.sharpenTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Sharpen", R.string.Sharpen), PhotoFilterView.this.sharpenValue, 0, 100);
                } else if (i == PhotoFilterView.this.fadeTool) {
                    cell.setIconAndTextAndValue(LocaleController.getString("Fade", R.string.Fade), PhotoFilterView.this.fadeValue, 0, 100);
                }
            } else if (itemViewType == 1) {
                PhotoEditRadioCell cell2 = (PhotoEditRadioCell) holder.itemView;
                cell2.setTag(Integer.valueOf(i));
                if (i == PhotoFilterView.this.tintShadowsTool) {
                    cell2.setIconAndTextAndValue(LocaleController.getString("TintShadows", R.string.TintShadows), 0, PhotoFilterView.this.tintShadowsColor);
                } else if (i == PhotoFilterView.this.tintHighlightsTool) {
                    cell2.setIconAndTextAndValue(LocaleController.getString("TintHighlights", R.string.TintHighlights), 0, PhotoFilterView.this.tintHighlightsColor);
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == PhotoFilterView.this.tintShadowsTool || position == PhotoFilterView.this.tintHighlightsTool) {
                return 1;
            }
            return 0;
        }
    }
}
