package com.king.zxing.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.king.zxing.DecodeFormatManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public final class CodeUtils {
    public static final int DEFAULT_REQ_HEIGHT = 800;
    public static final int DEFAULT_REQ_WIDTH = 450;

    private CodeUtils() {
        throw new AssertionError();
    }

    public static Bitmap createQRCode(String content, int heightPix) {
        return createQRCode(content, heightPix, (Bitmap) null);
    }

    public static Bitmap createQRCode(String content, int heightPix, int codeColor) {
        return createQRCode(content, heightPix, (Bitmap) null, codeColor);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo) {
        return createQRCode(content, heightPix, logo, -16777216);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo, int codeColor) {
        return createQRCode(content, heightPix, logo, 0.2f, codeColor);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo, float ratio) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        return createQRCode(content, heightPix, logo, ratio, (Map<EncodeHintType, ?>) hints);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo, float ratio, int codeColor) {
        HashMap hashMap = new HashMap();
        hashMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hashMap.put(EncodeHintType.MARGIN, 1);
        return createQRCode(content, heightPix, logo, ratio, hashMap, codeColor);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo, float ratio, Map<EncodeHintType, ?> hints) {
        return createQRCode(content, heightPix, logo, ratio, hints, -16777216);
    }

    public static Bitmap createQRCode(String content, int heightPix, Bitmap logo, float ratio, Map<EncodeHintType, ?> hints, int codeColor) {
        int i = heightPix;
        Bitmap bitmap = logo;
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, heightPix, heightPix, hints);
            int[] pixels = new int[(i * i)];
            for (int y = 0; y < i; y++) {
                for (int x = 0; x < i; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[(y * i) + x] = codeColor;
                    } else {
                        pixels[(y * i) + x] = -1;
                    }
                }
            }
            Bitmap createBitmap = Bitmap.createBitmap(heightPix, heightPix, Bitmap.Config.ARGB_8888);
            Bitmap bitmap2 = createBitmap;
            createBitmap.setPixels(pixels, 0, heightPix, 0, 0, heightPix, heightPix);
            if (bitmap != null) {
                try {
                    return addLogo(bitmap2, logo, ratio);
                } catch (WriterException e) {
                    e = e;
                    LogUtils.w(e.getMessage());
                    return null;
                }
            } else {
                float f = ratio;
                return bitmap2;
            }
        } catch (WriterException e2) {
            e = e2;
            float f2 = ratio;
            LogUtils.w(e.getMessage());
            return null;
        }
    }

    private static Bitmap addLogo(Bitmap src, Bitmap logo, float ratio) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        float scaleFactor = (((float) srcWidth) * ratio) / ((float) logoWidth);
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0.0f, 0.0f, (Paint) null);
            canvas.scale(scaleFactor, scaleFactor, (float) (srcWidth / 2), (float) (srcHeight / 2));
            canvas.drawBitmap(logo, (float) ((srcWidth - logoWidth) / 2), (float) ((srcHeight - logoHeight) / 2), (Paint) null);
            canvas.save();
            canvas.restore();
            return bitmap;
        } catch (Exception e) {
            LogUtils.w(e.getMessage());
            return null;
        }
    }

    public static String parseQRCode(String bitmapPath) {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        return parseQRCode(bitmapPath, hints);
    }

    public static String parseQRCode(String bitmapPath, Map<DecodeHintType, ?> hints) {
        Result result = parseQRCodeResult(bitmapPath, hints);
        if (result != null) {
            return result.getText();
        }
        return null;
    }

    public static Result parseQRCodeResult(String bitmapPath, Map<DecodeHintType, ?> hints) {
        return parseQRCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT, hints);
    }

    public static Result parseQRCodeResult(String bitmapPath, int reqWidth, int reqHeight, Map<DecodeHintType, ?> hints) {
        boolean isReDecode;
        Result result = null;
        try {
            QRCodeReader reader = new QRCodeReader();
            RGBLuminanceSource source = getRGBLuminanceSource(compressBitmap(bitmapPath, reqWidth, reqHeight));
            if (source != null) {
                try {
                    result = reader.decode(new BinaryBitmap(new HybridBinarizer(source)), hints);
                    isReDecode = false;
                } catch (Exception e) {
                    isReDecode = true;
                }
                if (isReDecode) {
                    try {
                        result = reader.decode(new BinaryBitmap(new HybridBinarizer(source.invert())), hints);
                        isReDecode = false;
                    } catch (Exception e2) {
                        isReDecode = true;
                    }
                }
                if (isReDecode) {
                    try {
                        result = reader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)), hints);
                        isReDecode = false;
                    } catch (Exception e3) {
                        isReDecode = true;
                    }
                }
                if (isReDecode) {
                    if (source.isRotateSupported()) {
                        try {
                            result = reader.decode(new BinaryBitmap(new HybridBinarizer(source.rotateCounterClockwise())), hints);
                        } catch (Exception e4) {
                        }
                    }
                }
                reader.reset();
            }
        } catch (Exception e5) {
            LogUtils.w(e5.getMessage());
        }
        return result;
    }

    public static String parseCode(String bitmapPath) {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        return parseCode(bitmapPath, hints);
    }

    public static String parseCode(String bitmapPath, Map<DecodeHintType, Object> hints) {
        Result result = parseCodeResult(bitmapPath, hints);
        if (result != null) {
            return result.getText();
        }
        return null;
    }

    public static Result parseCodeResult(String bitmapPath, Map<DecodeHintType, Object> hints) {
        return parseCodeResult(bitmapPath, DEFAULT_REQ_WIDTH, DEFAULT_REQ_HEIGHT, hints);
    }

    public static Result parseCodeResult(String bitmapPath, int reqWidth, int reqHeight, Map<DecodeHintType, Object> hints) {
        boolean isReDecode;
        Result result = null;
        try {
            MultiFormatReader reader = new MultiFormatReader();
            reader.setHints(hints);
            RGBLuminanceSource source = getRGBLuminanceSource(compressBitmap(bitmapPath, reqWidth, reqHeight));
            if (source != null) {
                try {
                    result = reader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
                    isReDecode = false;
                } catch (Exception e) {
                    isReDecode = true;
                }
                if (isReDecode) {
                    try {
                        result = reader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source.invert())));
                        isReDecode = false;
                    } catch (Exception e2) {
                        isReDecode = true;
                    }
                }
                if (isReDecode) {
                    try {
                        result = reader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
                        isReDecode = false;
                    } catch (Exception e3) {
                        isReDecode = true;
                    }
                }
                if (isReDecode) {
                    if (source.isRotateSupported()) {
                        try {
                            result = reader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source.rotateCounterClockwise())));
                        } catch (Exception e4) {
                        }
                    }
                }
                reader.reset();
            }
        } catch (Exception e5) {
            LogUtils.w(e5.getMessage());
        }
        return result;
    }

    private static Bitmap compressBitmap(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, newOpts);
        float width = (float) newOpts.outWidth;
        float height = (float) newOpts.outHeight;
        int wSize = 1;
        if (width > ((float) reqWidth)) {
            wSize = (int) (width / ((float) reqWidth));
        }
        int hSize = 1;
        if (height > ((float) reqHeight)) {
            hSize = (int) (height / ((float) reqHeight));
        }
        int size = Math.max(wSize, hSize);
        if (size <= 0) {
            size = 1;
        }
        newOpts.inSampleSize = size;
        newOpts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, newOpts);
    }

    private static RGBLuminanceSource getRGBLuminanceSource(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[(width * height)];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return new RGBLuminanceSource(width, height, pixels);
    }

    public static Bitmap createBarCode(String content, int desiredWidth, int desiredHeight) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, (Map<EncodeHintType, ?>) null);
    }

    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight) {
        return createBarCode(content, format, desiredWidth, desiredHeight, (Map<EncodeHintType, ?>) null);
    }

    public static Bitmap createBarCode(String content, int desiredWidth, int desiredHeight, boolean isShowText) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, (Map<EncodeHintType, ?>) null, isShowText, 40, -16777216);
    }

    public static Bitmap createBarCode(String content, int desiredWidth, int desiredHeight, boolean isShowText, int codeColor) {
        return createBarCode(content, BarcodeFormat.CODE_128, desiredWidth, desiredHeight, (Map<EncodeHintType, ?>) null, isShowText, 40, codeColor);
    }

    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, Map<EncodeHintType, ?> hints) {
        return createBarCode(content, format, desiredWidth, desiredHeight, hints, false, 40, -16777216);
    }

    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, Map<EncodeHintType, ?> hints, boolean isShowText) {
        return createBarCode(content, format, desiredWidth, desiredHeight, hints, isShowText, 40, -16777216);
    }

    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, boolean isShowText, int codeColor) {
        return createBarCode(content, format, desiredWidth, desiredHeight, (Map<EncodeHintType, ?>) null, isShowText, 40, codeColor);
    }

    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, Map<EncodeHintType, ?> hints, boolean isShowText, int codeColor) {
        return createBarCode(content, format, desiredWidth, desiredHeight, hints, isShowText, 40, codeColor);
    }

    public static Bitmap createBarCode(String content, BarcodeFormat format, int desiredWidth, int desiredHeight, Map<EncodeHintType, ?> hints, boolean isShowText, int textSize, int codeColor) {
        int i = textSize;
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        int BLACK = codeColor;
        try {
            BitMatrix result = new MultiFormatWriter().encode(content, format, desiredWidth, desiredHeight, hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[(width * height)];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : -1;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Bitmap bitmap2 = bitmap;
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (isShowText) {
                try {
                    return addCode(bitmap2, content, i, codeColor, i / 2);
                } catch (WriterException e) {
                    e = e;
                    LogUtils.w(e.getMessage());
                    return null;
                }
            } else {
                String str = content;
                int i2 = codeColor;
                return bitmap2;
            }
        } catch (WriterException e2) {
            e = e2;
            String str2 = content;
            int i3 = codeColor;
            LogUtils.w(e.getMessage());
            return null;
        }
    }

    private static Bitmap addCode(Bitmap src, String code, int textSize, int textColor, int offset) {
        if (src == null) {
            return null;
        }
        if (TextUtils.isEmpty(code)) {
            return src;
        }
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        if (srcWidth <= 0 || srcHeight <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight + textSize + (offset * 2), Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0.0f, 0.0f, (Paint) null);
            TextPaint paint = new TextPaint();
            paint.setTextSize((float) textSize);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(code, (float) (srcWidth / 2), (float) ((textSize / 2) + srcHeight + offset), paint);
            canvas.save();
            canvas.restore();
            return bitmap;
        } catch (Exception e) {
            LogUtils.w(e.getMessage());
            return null;
        }
    }
}
