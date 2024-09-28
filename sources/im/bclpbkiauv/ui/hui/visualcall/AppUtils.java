package im.bclpbkiauv.ui.hui.visualcall;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import androidx.core.view.MotionEventCompat;
import java.lang.reflect.Array;
import java.util.List;

public class AppUtils {
    private static final int VALUE = 100;

    public static boolean isServiceRunning(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = ((ActivityManager) context.getSystemService("activity")).getRunningServices(100);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap blur(Bitmap bitmap, float radius, Context context) {
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 1, bitmap.getHeight() / 1, false);
        if (Build.VERSION.SDK_INT >= 17) {
            RenderScript rs = RenderScript.create(context);
            Allocation allocFromBmp = Allocation.createFromBitmap(rs, bitmap2);
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, allocFromBmp.getElement());
            blur.setInput(allocFromBmp);
            blur.setRadius(radius);
            blur.forEach(allocFromBmp);
            allocFromBmp.copyTo(bitmap2);
            rs.destroy();
        }
        return bitmap2;
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        int i;
        int h;
        int i2;
        int p = radius;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
            Bitmap bitmap2 = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
        if (p < 1) {
            return null;
        }
        int w = bitmap.getWidth();
        int rbs = bitmap.getHeight();
        int[] pix = new int[(w * rbs)];
        bitmap.getPixels(pix, 0, w, 0, 0, w, rbs);
        int wm = w - 1;
        int p2 = rbs - 1;
        int bsum = w * rbs;
        int div = p + p + 1;
        int[] r = new int[bsum];
        int[] g = new int[bsum];
        int[] b = new int[bsum];
        int[] vmin = new int[Math.max(w, rbs)];
        int divsum = (div + 1) >> 1;
        int divsum2 = divsum * divsum;
        int[] dv = new int[(divsum2 * 256)];
        int i3 = 0;
        while (i3 < divsum2 * 256) {
            dv[i3] = i3 / divsum2;
            i3++;
            Bitmap bitmap3 = sentBitmap;
        }
        int yi = 0;
        int yw = 0;
        int i4 = i3;
        int[] iArr = new int[2];
        iArr[1] = 3;
        iArr[0] = div;
        int[][] stack = (int[][]) Array.newInstance(int.class, iArr);
        int r1 = p + 1;
        int divsum3 = divsum2;
        int y = 0;
        while (y < rbs) {
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            int wh = bsum;
            int gsum = 0;
            Bitmap bitmap4 = bitmap;
            int i5 = -p;
            int bsum2 = 0;
            while (i5 <= p) {
                int hm = p2;
                int h2 = rbs;
                int p3 = pix[yi + Math.min(wm, Math.max(i5, 0))];
                int[] sir = stack[i5 + p];
                sir[0] = (p3 & 16711680) >> 16;
                sir[1] = (p3 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                sir[2] = p3 & 255;
                int rbs2 = r1 - Math.abs(i5);
                rsum += sir[0] * rbs2;
                gsum += sir[1] * rbs2;
                bsum2 += sir[2] * rbs2;
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
                p2 = hm;
                rbs = h2;
            }
            int hm2 = p2;
            int h3 = rbs;
            int stackpointer = radius;
            int x = 0;
            while (x < w) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum2];
                int rsum2 = rsum - routsum;
                int gsum2 = gsum - goutsum;
                int bsum3 = bsum2 - boutsum;
                int[] sir2 = stack[((stackpointer - p) + div) % div];
                int routsum2 = routsum - sir2[0];
                int goutsum2 = goutsum - sir2[1];
                int boutsum2 = boutsum - sir2[2];
                if (y == 0) {
                    i2 = i5;
                    vmin[x] = Math.min(x + p + 1, wm);
                } else {
                    i2 = i5;
                }
                int p4 = pix[yw + vmin[x]];
                sir2[0] = (p4 & 16711680) >> 16;
                sir2[1] = (p4 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                int wm2 = wm;
                sir2[2] = p4 & 255;
                int rinsum2 = rinsum + sir2[0];
                int ginsum2 = ginsum + sir2[1];
                int binsum2 = binsum + sir2[2];
                rsum = rsum2 + rinsum2;
                gsum = gsum2 + ginsum2;
                bsum2 = bsum3 + binsum2;
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
            p2 = hm2;
            bitmap = bitmap4;
            bsum = wh;
            rbs = h3;
            int gsum3 = i6;
        }
        Bitmap bitmap5 = bitmap;
        int wh2 = bsum;
        int hm3 = p2;
        int h4 = rbs;
        int i8 = wm;
        int x2 = 0;
        int h5 = y;
        while (x2 < w) {
            int bsum4 = 0;
            int gsum4 = 0;
            int rsum3 = 0;
            int boutsum3 = 0;
            int i9 = h5;
            int i10 = -p;
            int yp = (-p) * w;
            int rinsum3 = 0;
            int ginsum3 = 0;
            int binsum3 = 0;
            int routsum3 = 0;
            int goutsum3 = 0;
            int goutsum4 = i9;
            while (i10 <= p) {
                int[] vmin2 = vmin;
                int yi2 = Math.max(0, yp) + x2;
                int[] sir4 = stack[i10 + p];
                sir4[0] = r[yi2];
                sir4[1] = g[yi2];
                sir4[2] = b[yi2];
                int rbs3 = r1 - Math.abs(i10);
                rsum3 += r[yi2] * rbs3;
                gsum4 += g[yi2] * rbs3;
                bsum4 += b[yi2] * rbs3;
                if (i10 > 0) {
                    rinsum3 += sir4[0];
                    ginsum3 += sir4[1];
                    binsum3 += sir4[2];
                } else {
                    routsum3 += sir4[0];
                    goutsum3 += sir4[1];
                    boutsum3 += sir4[2];
                }
                int i11 = rbs3;
                int hm4 = hm3;
                if (i10 < hm4) {
                    yp += w;
                }
                i10++;
                hm3 = hm4;
                vmin = vmin2;
            }
            int[] vmin3 = vmin;
            int hm5 = hm3;
            int rsum4 = rsum3;
            int y2 = 0;
            int stackpointer2 = radius;
            int boutsum4 = boutsum3;
            int yi3 = x2;
            while (true) {
                i = i10;
                h = h4;
                if (y2 >= h) {
                    break;
                }
                pix[yi3] = (pix[yi3] & -16777216) | (dv[rsum4] << 16) | (dv[gsum4] << 8) | dv[bsum4];
                int rsum5 = rsum4 - routsum3;
                int gsum5 = gsum4 - goutsum3;
                int bsum5 = bsum4 - boutsum4;
                int[] sir5 = stack[((stackpointer2 - p) + div) % div];
                int routsum4 = routsum3 - sir5[0];
                int goutsum5 = goutsum3 - sir5[1];
                int boutsum5 = boutsum4 - sir5[2];
                if (x2 == 0) {
                    vmin3[y2] = Math.min(y2 + r1, hm5) * w;
                }
                int p5 = vmin3[y2] + x2;
                sir5[0] = r[p5];
                sir5[1] = g[p5];
                sir5[2] = b[p5];
                int rinsum4 = rinsum3 + sir5[0];
                int ginsum4 = ginsum3 + sir5[1];
                int binsum4 = binsum3 + sir5[2];
                rsum4 = rsum5 + rinsum4;
                gsum4 = gsum5 + ginsum4;
                bsum4 = bsum5 + binsum4;
                stackpointer2 = (stackpointer2 + 1) % div;
                int[] sir6 = stack[stackpointer2];
                routsum3 = routsum4 + sir6[0];
                goutsum3 = goutsum5 + sir6[1];
                boutsum4 = boutsum5 + sir6[2];
                rinsum3 = rinsum4 - sir6[0];
                ginsum3 = ginsum4 - sir6[1];
                binsum3 = binsum4 - sir6[2];
                yi3 += w;
                y2++;
                p = radius;
                h4 = h;
                i10 = i;
            }
            x2++;
            p = radius;
            hm3 = hm5;
            h4 = h;
            h5 = y2;
            int rsum6 = yi3;
            int yi4 = i;
            vmin = vmin3;
        }
        int i12 = h5;
        int[] iArr2 = dv;
        int i13 = divsum3;
        int i14 = hm3;
        int[] iArr3 = vmin;
        int[] iArr4 = b;
        int[] iArr5 = g;
        int[] iArr6 = r;
        int i15 = wh2;
        bitmap5.setPixels(pix, 0, w, 0, 0, w, h4);
        return bitmap5;
    }
}
