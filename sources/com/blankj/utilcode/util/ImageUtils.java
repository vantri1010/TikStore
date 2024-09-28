package com.blankj.utilcode.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

public final class ImageUtils {
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ImageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(Utils.getApp().getResources(), bitmap);
    }

    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        if (drawable == null) {
            return null;
        }
        return bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    public static Drawable bytes2Drawable(byte[] bytes) {
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    public static Bitmap view2Bitmap(View view) {
        Bitmap bitmap;
        if (view == null) {
            return null;
        }
        boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
        boolean willNotCacheDrawing = view.willNotCacheDrawing();
        view.setDrawingCacheEnabled(true);
        view.setWillNotCacheDrawing(false);
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
        } else {
            bitmap = Bitmap.createBitmap(drawingCache);
        }
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCacheDrawing);
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        return bitmap;
    }

    public static Bitmap getBitmap(File file) {
        if (file == null) {
            return null;
        }
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static Bitmap getBitmap(File file, int maxWidth, int maxHeight) {
        if (file == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static Bitmap getBitmap(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return BitmapFactory.decodeFile(filePath);
    }

    public static Bitmap getBitmap(String filePath, int maxWidth, int maxHeight) {
        if (isSpace(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getBitmap(InputStream is) {
        if (is == null) {
            return null;
        }
        return BitmapFactory.decodeStream(is);
    }

    public static Bitmap getBitmap(InputStream is, int maxWidth, int maxHeight) {
        if (is == null) {
            return null;
        }
        return getBitmap(input2Byte(is), 0, maxWidth, maxHeight);
    }

    public static Bitmap getBitmap(byte[] data, int offset) {
        if (data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, offset, data.length);
    }

    public static Bitmap getBitmap(byte[] data, int offset, int maxWidth, int maxHeight) {
        if (data.length == 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    public static Bitmap getBitmap(int resId) {
        Drawable drawable = ContextCompat.getDrawable(Utils.getApp(), resId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(int resId, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Resources resources = Utils.getApp().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    public static Bitmap getBitmap(FileDescriptor fd) {
        if (fd == null) {
            return null;
        }
        return BitmapFactory.decodeFileDescriptor(fd);
    }

    public static Bitmap getBitmap(FileDescriptor fd, int maxWidth, int maxHeight) {
        if (fd == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, (Rect) null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, (Rect) null, options);
    }

    public static Bitmap drawColor(Bitmap src, int color) {
        if (src != null) {
            return drawColor(src, color, false);
        }
        throw new NullPointerException("Argument 'src' of type Bitmap (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Bitmap drawColor(Bitmap src, int color, boolean recycle) {
        if (src == null) {
            throw new NullPointerException("Argument 'src' of type Bitmap (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isEmptyBitmap(src)) {
            return null;
        } else {
            Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
            new Canvas(ret).drawColor(color, PorterDuff.Mode.DARKEN);
            return ret;
        }
    }

    public static Bitmap scale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    public static Bitmap scale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap clip(Bitmap src, int x, int y, int width, int height) {
        return clip(src, x, y, width, height, false);
    }

    public static Bitmap clip(Bitmap src, int x, int y, int width, int height, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap skew(Bitmap src, float kx, float ky) {
        return skew(src, kx, ky, 0.0f, 0.0f, false);
    }

    public static Bitmap skew(Bitmap src, float kx, float ky, boolean recycle) {
        return skew(src, kx, ky, 0.0f, 0.0f, recycle);
    }

    public static Bitmap skew(Bitmap src, float kx, float ky, float px, float py) {
        return skew(src, kx, ky, px, py, false);
    }

    public static Bitmap skew(Bitmap src, float kx, float ky, float px, float py, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap rotate(Bitmap src, int degrees, float px, float py) {
        return rotate(src, degrees, px, py, false);
    }

    public static Bitmap rotate(Bitmap src, int degrees, float px, float py, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        if (degrees == 0) {
            return src;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate((float) degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static int getRotateDegree(String filePath) {
        try {
            int orientation = new ExifInterface(filePath).getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 1);
            if (orientation == 3) {
                return 180;
            }
            if (orientation == 6) {
                return 90;
            }
            if (orientation != 8) {
                return 0;
            }
            return 270;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Bitmap toRound(Bitmap src) {
        return toRound(src, 0, 0, false);
    }

    public static Bitmap toRound(Bitmap src, boolean recycle) {
        return toRound(src, 0, 0, recycle);
    }

    public static Bitmap toRound(Bitmap src, int borderSize, int borderColor) {
        return toRound(src, borderSize, borderColor, false);
    }

    public static Bitmap toRound(Bitmap src, int borderSize, int borderColor, boolean recycle) {
        Bitmap bitmap = src;
        int i = borderSize;
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(1);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        float center = ((float) size) / 2.0f;
        RectF rectF = new RectF(0.0f, 0.0f, (float) width, (float) height);
        rectF.inset(((float) (width - size)) / 2.0f, ((float) (height - size)) / 2.0f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        if (width != height) {
            matrix.preScale(((float) size) / ((float) width), ((float) size) / ((float) height));
        }
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (i > 0) {
            paint.setShader((Shader) null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) i);
            canvas.drawCircle(((float) width) / 2.0f, ((float) height) / 2.0f, center - (((float) i) / 2.0f), paint);
        } else {
            int i2 = borderColor;
        }
        if (recycle && !src.isRecycled() && ret != bitmap) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap toRoundCorner(Bitmap src, float radius) {
        return toRoundCorner(src, radius, 0, 0, false);
    }

    public static Bitmap toRoundCorner(Bitmap src, float radius, boolean recycle) {
        return toRoundCorner(src, radius, 0, 0, recycle);
    }

    public static Bitmap toRoundCorner(Bitmap src, float radius, int borderSize, int borderColor) {
        return toRoundCorner(src, radius, borderSize, borderColor, false);
    }

    public static Bitmap toRoundCorner(Bitmap src, float radius, int borderSize, int borderColor, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(1);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        paint.setShader(new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0.0f, 0.0f, (float) width, (float) height);
        float halfBorderSize = ((float) borderSize) / 2.0f;
        rectF.inset(halfBorderSize, halfBorderSize);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        if (borderSize > 0) {
            paint.setShader((Shader) null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) borderSize);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap addCornerBorder(Bitmap src, int borderSize, int color, float cornerRadius) {
        return addBorder(src, borderSize, color, false, cornerRadius, false);
    }

    public static Bitmap addCornerBorder(Bitmap src, int borderSize, int color, float cornerRadius, boolean recycle) {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle);
    }

    public static Bitmap addCircleBorder(Bitmap src, int borderSize, int color) {
        return addBorder(src, borderSize, color, true, 0.0f, false);
    }

    public static Bitmap addCircleBorder(Bitmap src, int borderSize, int color, boolean recycle) {
        return addBorder(src, borderSize, color, true, 0.0f, recycle);
    }

    private static Bitmap addBorder(Bitmap src, int borderSize, int color, boolean isCircle, float cornerRadius, boolean recycle) {
        Bitmap ret;
        int i = borderSize;
        if (isEmptyBitmap(src)) {
            return null;
        }
        if (recycle) {
            ret = src;
            Bitmap bitmap = ret;
        } else {
            Bitmap bitmap2 = src;
            ret = src.copy(src.getConfig(), true);
        }
        int width = ret.getWidth();
        int height = ret.getHeight();
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint(1);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) i);
        if (isCircle) {
            canvas.drawCircle(((float) width) / 2.0f, ((float) height) / 2.0f, (((float) Math.min(width, height)) / 2.0f) - (((float) i) / 2.0f), paint);
            float f = cornerRadius;
        } else {
            int halfBorderSize = i >> 1;
            float f2 = cornerRadius;
            canvas.drawRoundRect(new RectF((float) halfBorderSize, (float) halfBorderSize, (float) (width - halfBorderSize), (float) (height - halfBorderSize)), f2, f2, paint);
        }
        return ret;
    }

    public static Bitmap addReflection(Bitmap src, int reflectionHeight) {
        return addReflection(src, reflectionHeight, false);
    }

    public static Bitmap addReflection(Bitmap src, int reflectionHeight, boolean recycle) {
        Bitmap bitmap = src;
        if (isEmptyBitmap(src)) {
            return null;
        }
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        Bitmap reflectionBitmap = Bitmap.createBitmap(src, 0, srcHeight - reflectionHeight, srcWidth, reflectionHeight, matrix, false);
        Bitmap ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(reflectionBitmap, 0.0f, (float) (srcHeight + 0), (Paint) null);
        Paint paint = new Paint(1);
        paint.setShader(new LinearGradient(0.0f, (float) srcHeight, 0.0f, (float) (ret.getHeight() + 0), 1895825407, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.MIRROR));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0.0f, (float) (srcHeight + 0), (float) srcWidth, (float) ret.getHeight(), paint);
        if (!reflectionBitmap.isRecycled()) {
            reflectionBitmap.recycle();
        }
        if (recycle && !src.isRecycled() && ret != bitmap) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap addTextWatermark(Bitmap src, String content, int textSize, int color, float x, float y) {
        return addTextWatermark(src, content, (float) textSize, color, x, y, false);
    }

    public static Bitmap addTextWatermark(Bitmap src, String content, float textSize, int color, float x, float y, boolean recycle) {
        if (isEmptyBitmap(src) || content == null) {
            return null;
        }
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(1);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.getTextBounds(content, 0, content.length(), new Rect());
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap addImageWatermark(Bitmap src, Bitmap watermark, int x, int y, int alpha) {
        return addImageWatermark(src, watermark, x, y, alpha, false);
    }

    public static Bitmap addImageWatermark(Bitmap src, Bitmap watermark, int x, int y, int alpha, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = src.copy(src.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(1);
            Canvas canvas = new Canvas(ret);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, (float) x, (float) y, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap toAlpha(Bitmap src) {
        return toAlpha(src, false);
    }

    public static Bitmap toAlpha(Bitmap src, Boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = src.extractAlpha();
        if (recycle.booleanValue() && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap toGray(Bitmap src) {
        return toGray(src, false);
    }

    public static Bitmap toGray(Bitmap src, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(src, 0.0f, 0.0f, paint);
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap fastBlur(Bitmap src, float scale, float radius) {
        return fastBlur(src, scale, radius, false, false);
    }

    public static Bitmap fastBlur(Bitmap src, float scale, float radius, boolean recycle) {
        return fastBlur(src, scale, radius, recycle, false);
    }

    public static Bitmap fastBlur(Bitmap src, float scale, float radius, boolean recycle, boolean isReturnScale) {
        Bitmap scaleBitmap;
        if (isEmptyBitmap(src)) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap scaleBitmap2 = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        Paint paint = new Paint(3);
        Canvas canvas = new Canvas();
        paint.setColorFilter(new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_ATOP));
        canvas.scale(scale, scale);
        canvas.drawBitmap(scaleBitmap2, 0.0f, 0.0f, paint);
        if (Build.VERSION.SDK_INT >= 17) {
            scaleBitmap = renderScriptBlur(scaleBitmap2, radius, recycle);
        } else {
            scaleBitmap = stackBlur(scaleBitmap2, (int) radius, recycle);
        }
        if (scale == 1.0f || isReturnScale) {
            if (recycle && !src.isRecycled() && scaleBitmap != src) {
                src.recycle();
            }
            return scaleBitmap;
        }
        Bitmap ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true);
        if (!scaleBitmap.isRecycled()) {
            scaleBitmap.recycle();
        }
        if (recycle && !src.isRecycled() && ret != src) {
            src.recycle();
        }
        return ret;
    }

    public static Bitmap renderScriptBlur(Bitmap src, float radius) {
        return renderScriptBlur(src, radius, false);
    }

    public static Bitmap renderScriptBlur(Bitmap src, float radius, boolean recycle) {
        RenderScript rs = null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        try {
            rs = RenderScript.create(Utils.getApp());
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs, ret, Allocation.MipmapControl.MIPMAP_NONE, 1);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blurScript.setInput(input);
            blurScript.setRadius(radius);
            blurScript.forEach(output);
            output.copyTo(ret);
            return ret;
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }
    }

    public static Bitmap stackBlur(Bitmap src, int radius) {
        return stackBlur(src, radius, false);
    }

    public static Bitmap stackBlur(Bitmap src, int radius, boolean recycle) {
        Bitmap ret;
        int radius2;
        int i;
        int h;
        int[] dv;
        int i2;
        if (recycle) {
            ret = src;
            Bitmap bitmap = ret;
        } else {
            ret = src.copy(src.getConfig(), true);
        }
        int radius3 = radius;
        if (radius3 < 1) {
            radius2 = 1;
        } else {
            radius2 = radius3;
        }
        int w = ret.getWidth();
        int p = ret.getHeight();
        int[] pix = new int[(w * p)];
        ret.getPixels(pix, 0, w, 0, 0, w, p);
        int wm = w - 1;
        int hm = p - 1;
        int wh = w * p;
        int div = radius2 + radius2 + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, p)];
        int divsum = (div + 1) >> 1;
        int divsum2 = divsum * divsum;
        int[] dv2 = new int[(divsum2 * 256)];
        int i3 = 0;
        while (i3 < divsum2 * 256) {
            dv2[i3] = i3 / divsum2;
            i3++;
            Bitmap bitmap2 = src;
        }
        int yi = 0;
        int yw = 0;
        int i4 = i3;
        int[] iArr = new int[2];
        iArr[1] = 3;
        iArr[0] = div;
        int[][] stack = (int[][]) Array.newInstance(int.class, iArr);
        int r1 = radius2 + 1;
        int divsum3 = divsum2;
        int y = 0;
        while (y < p) {
            int bsum = 0;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            int wh2 = wh;
            int i5 = -radius2;
            while (i5 <= radius2) {
                Bitmap ret2 = ret;
                int h2 = p;
                int p2 = pix[yi + Math.min(wm, Math.max(i5, 0))];
                int[] sir = stack[i5 + radius2];
                sir[0] = (p2 & 16711680) >> 16;
                sir[1] = (p2 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                sir[2] = p2 & 255;
                int rbs = r1 - Math.abs(i5);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i5 > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                i5++;
                p = h2;
                ret = ret2;
            }
            Bitmap ret3 = ret;
            int h3 = p;
            int stackpointer = radius2;
            int x = 0;
            while (x < w) {
                r[yi] = dv2[rsum];
                g[yi] = dv2[gsum];
                b[yi] = dv2[bsum];
                int rsum2 = rsum - routsum;
                int gsum2 = gsum - goutsum;
                int bsum2 = bsum - boutsum;
                int[] sir2 = stack[((stackpointer - radius2) + div) % div];
                int routsum2 = routsum - sir2[0];
                int goutsum2 = goutsum - sir2[1];
                int boutsum2 = boutsum - sir2[2];
                if (y == 0) {
                    i2 = i5;
                    vmin[x] = Math.min(x + radius2 + 1, wm);
                } else {
                    i2 = i5;
                }
                int p3 = pix[yw + vmin[x]];
                sir2[0] = (p3 & 16711680) >> 16;
                sir2[1] = (p3 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                int wm2 = wm;
                sir2[2] = p3 & 255;
                int rinsum2 = rinsum + sir2[0];
                int ginsum2 = ginsum + sir2[1];
                int binsum2 = binsum + sir2[2];
                rsum = rsum2 + rinsum2;
                gsum = gsum2 + ginsum2;
                bsum = bsum2 + binsum2;
                stackpointer = (stackpointer + 1) % div;
                int[] sir3 = stack[stackpointer % div];
                routsum = routsum2 + sir3[0];
                goutsum = goutsum2 + sir3[1];
                boutsum = boutsum2 + sir3[2];
                rinsum = rinsum2 - sir3[0];
                ginsum = ginsum2 - sir3[1];
                binsum = binsum2 - sir3[2];
                yi++;
                x++;
                wm = wm2;
                i5 = i2;
            }
            int i6 = i5;
            int i7 = wm;
            yw += w;
            y++;
            p = h3;
            wh = wh2;
            ret = ret3;
            int i8 = i6;
        }
        Bitmap ret4 = ret;
        int wh3 = wh;
        int stackstart = p;
        int i9 = wm;
        int x2 = 0;
        int h4 = y;
        while (x2 < w) {
            int bsum3 = 0;
            int gsum3 = 0;
            int boutsum3 = 0;
            int goutsum3 = 0;
            int routsum3 = 0;
            int binsum3 = 0;
            int ginsum3 = 0;
            int rinsum3 = 0;
            int rsum3 = 0;
            int rsum4 = h4;
            int i10 = -radius2;
            int yp = (-radius2) * w;
            while (i10 <= radius2) {
                int[] vmin2 = vmin;
                int yi2 = Math.max(0, yp) + x2;
                int[] sir4 = stack[i10 + radius2];
                sir4[0] = r[yi2];
                sir4[1] = g[yi2];
                sir4[2] = b[yi2];
                int rbs2 = r1 - Math.abs(i10);
                rsum3 += r[yi2] * rbs2;
                gsum3 += g[yi2] * rbs2;
                bsum3 += b[yi2] * rbs2;
                if (i10 > 0) {
                    rinsum3 += sir4[0];
                    ginsum3 += sir4[1];
                    binsum3 += sir4[2];
                } else {
                    routsum3 += sir4[0];
                    goutsum3 += sir4[1];
                    boutsum3 += sir4[2];
                }
                if (i10 < hm) {
                    yp += w;
                }
                i10++;
                vmin = vmin2;
            }
            int[] vmin3 = vmin;
            int stackpointer2 = radius2;
            int i11 = rsum3;
            int yi3 = x2;
            int y2 = 0;
            int rsum5 = i11;
            while (true) {
                i = i10;
                h = stackstart;
                if (y2 >= h) {
                    break;
                }
                pix[yi3] = (pix[yi3] & -16777216) | (dv2[rsum5] << 16) | (dv2[gsum3] << 8) | dv2[bsum3];
                int rsum6 = rsum5 - routsum3;
                int gsum4 = gsum3 - goutsum3;
                int bsum4 = bsum3 - boutsum3;
                int[] sir5 = stack[((stackpointer2 - radius2) + div) % div];
                int routsum4 = routsum3 - sir5[0];
                int goutsum4 = goutsum3 - sir5[1];
                int boutsum4 = boutsum3 - sir5[2];
                if (x2 == 0) {
                    dv = dv2;
                    vmin3[y2] = Math.min(y2 + r1, hm) * w;
                } else {
                    dv = dv2;
                }
                int p4 = vmin3[y2] + x2;
                sir5[0] = r[p4];
                sir5[1] = g[p4];
                sir5[2] = b[p4];
                int rinsum4 = rinsum3 + sir5[0];
                int ginsum4 = ginsum3 + sir5[1];
                int binsum4 = binsum3 + sir5[2];
                rsum5 = rsum6 + rinsum4;
                gsum3 = gsum4 + ginsum4;
                bsum3 = bsum4 + binsum4;
                stackpointer2 = (stackpointer2 + 1) % div;
                int[] sir6 = stack[stackpointer2];
                routsum3 = routsum4 + sir6[0];
                goutsum3 = goutsum4 + sir6[1];
                boutsum3 = boutsum4 + sir6[2];
                rinsum3 = rinsum4 - sir6[0];
                ginsum3 = ginsum4 - sir6[1];
                binsum3 = binsum4 - sir6[2];
                yi3 += w;
                y2++;
                stackstart = h;
                i10 = i;
                dv2 = dv;
            }
            x2++;
            stackstart = h;
            int stackpointer3 = yi3;
            int yi4 = i;
            h4 = y2;
            vmin = vmin3;
        }
        int i12 = h4;
        int h5 = stackstart;
        int[] iArr2 = dv2;
        int i13 = divsum3;
        int[] iArr3 = vmin;
        int[] iArr4 = b;
        int[] iArr5 = g;
        int[] iArr6 = r;
        int i14 = wh3;
        int i15 = hm;
        ret4.setPixels(pix, 0, w, 0, 0, w, h5);
        return ret4;
    }

    public static boolean save(Bitmap src, String filePath, Bitmap.CompressFormat format) {
        return save(src, getFileByPath(filePath), format, false);
    }

    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format) {
        return save(src, file, format, false);
    }

    public static boolean save(Bitmap src, String filePath, Bitmap.CompressFormat format, boolean recycle) {
        return save(src, getFileByPath(filePath), format, recycle);
    }

    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) {
            return false;
        }
        OutputStream os = null;
        boolean ret = false;
        try {
            OutputStream os2 = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os2);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
            try {
                os2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (os != null) {
                os.close();
            }
        } catch (Throwable th) {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
        return ret;
    }

    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return isImage(file.getPath());
    }

    public static boolean isImage(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap decodeFile = BitmapFactory.decodeFile(filePath, options);
            if (options.outWidth == -1 || options.outHeight == -1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static ImageType getImageType(String filePath) {
        return getImageType(getFileByPath(filePath));
    }

    public static ImageType getImageType(File file) {
        if (file == null) {
            return null;
        }
        InputStream is = null;
        try {
            InputStream is2 = new FileInputStream(file);
            ImageType type = getImageType(is2);
            if (type != null) {
                try {
                    is2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return type;
            }
            try {
                is2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return null;
        } catch (IOException e3) {
            e3.printStackTrace();
            if (is != null) {
                is.close();
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    private static ImageType getImageType(InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[12];
            if (is.read(bytes) != -1) {
                return getImageType(bytes);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ImageType getImageType(byte[] bytes) {
        String type = bytes2HexString(bytes).toUpperCase();
        if (type.contains("FFD8FF")) {
            return ImageType.TYPE_JPG;
        }
        if (type.contains("89504E47")) {
            return ImageType.TYPE_PNG;
        }
        if (type.contains("47494638")) {
            return ImageType.TYPE_GIF;
        }
        if (type.contains("49492A00") || type.contains("4D4D002A")) {
            return ImageType.TYPE_TIFF;
        }
        if (type.contains("424D")) {
            return ImageType.TYPE_BMP;
        }
        if (type.startsWith("52494646") && type.endsWith("57454250")) {
            return ImageType.TYPE_WEBP;
        }
        if (type.contains("00000100") || type.contains("00000200")) {
            return ImageType.TYPE_ICO;
        }
        return ImageType.TYPE_UNKNOWN;
    }

    private static String bytes2HexString(byte[] bytes) {
        int len;
        if (bytes == null || (len = bytes.length) <= 0) {
            return "";
        }
        char[] ret = new char[(len << 1)];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int j2 = j + 1;
            char[] cArr = hexDigits;
            ret[j] = cArr[(bytes[i] >> 4) & 15];
            j = j2 + 1;
            ret[j2] = cArr[bytes[i] & 15];
        }
        return new String(ret);
    }

    private static boolean isJPEG(byte[] b) {
        return b.length >= 2 && b[0] == -1 && b[1] == -40;
    }

    private static boolean isGIF(byte[] b) {
        return b.length >= 6 && b[0] == 71 && b[1] == 73 && b[2] == 70 && b[3] == 56 && (b[4] == 55 || b[4] == 57) && b[5] == 97;
    }

    private static boolean isPNG(byte[] b) {
        return b.length >= 8 && b[0] == -119 && b[1] == 80 && b[2] == 78 && b[3] == 71 && b[4] == 13 && b[5] == 10 && b[6] == 26 && b[7] == 10;
    }

    private static boolean isBMP(byte[] b) {
        return b.length >= 2 && b[0] == 66 && b[1] == 77;
    }

    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }

    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
        return scale(src, scaleWidth, scaleHeight, recycle);
    }

    public static byte[] compressByQuality(Bitmap src, int quality) {
        return compressByQuality(src, quality, false);
    }

    public static byte[] compressByQuality(Bitmap src, int quality, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return bytes;
    }

    public static byte[] compressByQuality(Bitmap src, long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    public static byte[] compressByQuality(Bitmap src, long maxByteSize, boolean recycle) {
        byte[] bytes;
        if (isEmptyBitmap(src) || maxByteSize <= 0) {
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (((long) baos.size()) <= maxByteSize) {
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos);
            if (((long) baos.size()) >= maxByteSize) {
                bytes = baos.toByteArray();
            } else {
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (((long) len) == maxByteSize) {
                        break;
                    } else if (((long) len) > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return bytes;
    }

    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static Bitmap compressBySampleSize(Bitmap src, int maxWidth, int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
    }

    public static Bitmap compressBySampleSize(Bitmap src, int maxWidth, int maxHeight, boolean recycle) {
        if (isEmptyBitmap(src)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) {
            src.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static int[] getSize(String filePath) {
        return getSize(getFileByPath(filePath));
    }

    public static int[] getSize(File file) {
        if (file == null) {
            return new int[]{0, 0};
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        return new int[]{opts.outWidth, opts.outHeight};
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (true) {
            if (height <= maxHeight && width <= maxWidth) {
                return inSampleSize;
            }
            height >>= 1;
            width >>= 1;
            inSampleSize <<= 1;
        }
    }

    private static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
    }

    private static boolean createFileByDeleteOldFile(File file) {
        if (file == null) {
            return false;
        }
        if ((file.exists() && !file.delete()) || !createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? file.mkdirs() : file.isDirectory());
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

    private static byte[] input2Byte(InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            while (true) {
                int read = is.read(b, 0, 1024);
                int len = read;
                if (read == -1) {
                    break;
                }
                os.write(b, 0, len);
            }
            byte[] byteArray = os.toByteArray();
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArray;
        } catch (IOException e2) {
            e2.printStackTrace();
            try {
                is.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            return null;
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }

    public enum ImageType {
        TYPE_JPG("jpg"),
        TYPE_PNG("png"),
        TYPE_GIF("gif"),
        TYPE_TIFF("tiff"),
        TYPE_BMP("bmp"),
        TYPE_WEBP("webp"),
        TYPE_ICO("ico"),
        TYPE_UNKNOWN("unknown");
        
        String value;

        private ImageType(String value2) {
            this.value = value2;
        }

        public String getValue() {
            return this.value;
        }
    }
}
