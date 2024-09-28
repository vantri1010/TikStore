package im.bclpbkiauv.messenger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.AnimatedFileDrawable;
import im.bclpbkiauv.ui.components.RLottieDrawable;
import im.bclpbkiauv.ui.components.RecyclableDrawable;

public class ImageReceiver implements NotificationCenter.NotificationCenterDelegate {
    private static final int TYPE_CROSSFDADE = 2;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MEDIA = 3;
    public static final int TYPE_THUMB = 1;
    private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, PorterDuff.Mode.MULTIPLY);
    private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, PorterDuff.Mode.MULTIPLY);
    private boolean allowDecodeSingleFrame;
    private boolean allowStartAnimation;
    private boolean animationReadySent;
    private int autoRepeat;
    private RectF bitmapRect;
    private boolean canceledLoading;
    private boolean centerRotation;
    private ColorFilter colorFilter;
    private byte crossfadeAlpha;
    private Drawable crossfadeImage;
    private String crossfadeKey;
    private BitmapShader crossfadeShader;
    private boolean crossfadeWithOldImage;
    private boolean crossfadeWithThumb;
    private boolean crossfadingWithThumb;
    private int currentAccount;
    private float currentAlpha;
    private int currentCacheType;
    private String currentExt;
    private int currentGuid;
    private Drawable currentImageDrawable;
    private String currentImageFilter;
    private String currentImageKey;
    private ImageLocation currentImageLocation;
    private boolean currentKeyQuality;
    private int currentLayerNum;
    private Drawable currentMediaDrawable;
    private String currentMediaFilter;
    private String currentMediaKey;
    private ImageLocation currentMediaLocation;
    private int currentOpenedLayerFlags;
    private Object currentParentObject;
    private int currentSize;
    private Drawable currentThumbDrawable;
    private String currentThumbFilter;
    private String currentThumbKey;
    private ImageLocation currentThumbLocation;
    private ImageReceiverDelegate delegate;
    private RectF drawRegion;
    private boolean forceCrossfade;
    private boolean forceLoding;
    private boolean forcePreview;
    private int imageH;
    private int imageOrientation;
    private BitmapShader imageShader;
    private int imageTag;
    private int imageW;
    private int imageX;
    private int imageY;
    private boolean invalidateAll;
    private boolean isAspectFit;
    private int isPressed;
    private boolean isVisible;
    private long lastUpdateAlphaTime;
    private boolean manualAlphaAnimator;
    private BitmapShader mediaShader;
    private int mediaTag;
    private boolean needsQualityThumb;
    private float overrideAlpha;
    private int param;
    private View parentView;
    private TLRPC.Document qulityThumbDocument;
    private Paint roundPaint;
    private int roundRadius;
    private RectF roundRect;
    private SetImageBackup setImageBackup;
    private Matrix shaderMatrix;
    private boolean shouldGenerateQualityThumb;
    private float sideClip;
    private Drawable staticThumbDrawable;
    private ImageLocation strippedLocation;
    private int thumbOrientation;
    private BitmapShader thumbShader;
    private int thumbTag;
    private boolean useSharedAnimationQueue;

    public interface ImageReceiverDelegate {
        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2);

        void onAnimationReady(ImageReceiver imageReceiver);

        /* renamed from: im.bclpbkiauv.messenger.ImageReceiver$ImageReceiverDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onAnimationReady(ImageReceiverDelegate _this, ImageReceiver imageReceiver) {
            }
        }
    }

    public static class BitmapHolder {
        public Bitmap bitmap;
        private String key;
        private boolean recycleOnRelease;

        public BitmapHolder(Bitmap b, String k) {
            this.bitmap = b;
            this.key = k;
            if (k != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Bitmap b) {
            this.bitmap = b;
            this.recycleOnRelease = true;
        }

        public int getWidth() {
            Bitmap bitmap2 = this.bitmap;
            if (bitmap2 != null) {
                return bitmap2.getWidth();
            }
            return 0;
        }

        public int getHeight() {
            Bitmap bitmap2 = this.bitmap;
            if (bitmap2 != null) {
                return bitmap2.getHeight();
            }
            return 0;
        }

        public boolean isRecycled() {
            Bitmap bitmap2 = this.bitmap;
            return bitmap2 == null || bitmap2.isRecycled();
        }

        public void release() {
            Bitmap bitmap2;
            if (this.key == null) {
                if (this.recycleOnRelease && (bitmap2 = this.bitmap) != null) {
                    bitmap2.recycle();
                }
                this.bitmap = null;
                return;
            }
            boolean canDelete = ImageLoader.getInstance().decrementUseCount(this.key);
            if (!ImageLoader.getInstance().isInMemCache(this.key, false) && canDelete) {
                this.bitmap.recycle();
            }
            this.key = null;
            this.bitmap = null;
        }
    }

    private class SetImageBackup {
        public int cacheType;
        public String ext;
        public String imageFilter;
        public ImageLocation imageLocation;
        public String mediaFilter;
        public ImageLocation mediaLocation;
        public Object parentObject;
        public int size;
        public Drawable thumb;
        public String thumbFilter;
        public ImageLocation thumbLocation;

        private SetImageBackup() {
        }
    }

    public ImageReceiver() {
        this((View) null);
    }

    public ImageReceiver(View view) {
        this.allowStartAnimation = true;
        this.autoRepeat = 1;
        this.drawRegion = new RectF();
        this.isVisible = true;
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.overrideAlpha = 1.0f;
        this.crossfadeAlpha = 1;
        this.parentView = view;
        this.roundPaint = new Paint(3);
        this.currentAccount = UserConfig.selectedAccount;
    }

    public void cancelLoadImage() {
        this.forceLoding = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        this.canceledLoading = true;
    }

    public void setForceLoading(boolean value) {
        this.forceLoding = value;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setStrippedLocation(ImageLocation location) {
        this.strippedLocation = location;
    }

    public ImageLocation getStrippedLocation() {
        return this.strippedLocation;
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, 0, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, int size, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, size, ext, parentObject, cacheType);
    }

    public void setImage(String imagePath, String imageFilter, Drawable thumb, String ext, int size) {
        setImage(ImageLocation.getForPath(imagePath), imageFilter, (ImageLocation) null, (String) null, thumb, size, ext, (Object) null, 1);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, 0, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, int size, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, size, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation fileLocation, String fileFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, int size, String ext, Object parentObject, int cacheType) {
        setImage((ImageLocation) null, (String) null, fileLocation, fileFilter, thumbLocation, thumbFilter, thumb, size, ext, parentObject, cacheType);
    }

    /* JADX WARNING: Removed duplicated region for block: B:68:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0168  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setImage(im.bclpbkiauv.messenger.ImageLocation r21, java.lang.String r22, im.bclpbkiauv.messenger.ImageLocation r23, java.lang.String r24, im.bclpbkiauv.messenger.ImageLocation r25, java.lang.String r26, android.graphics.drawable.Drawable r27, int r28, java.lang.String r29, java.lang.Object r30, int r31) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = r25
            r6 = r26
            r7 = r27
            r8 = r29
            r9 = r30
            im.bclpbkiauv.messenger.ImageReceiver$SetImageBackup r10 = r0.setImageBackup
            r11 = 0
            if (r10 == 0) goto L_0x0027
            r10.imageLocation = r11
            im.bclpbkiauv.messenger.ImageReceiver$SetImageBackup r10 = r0.setImageBackup
            r10.thumbLocation = r11
            im.bclpbkiauv.messenger.ImageReceiver$SetImageBackup r10 = r0.setImageBackup
            r10.mediaLocation = r11
            im.bclpbkiauv.messenger.ImageReceiver$SetImageBackup r10 = r0.setImageBackup
            r10.thumb = r11
        L_0x0027:
            r10 = 1065353216(0x3f800000, float:1.0)
            r12 = 1
            r13 = 0
            if (r3 != 0) goto L_0x00ac
            if (r5 != 0) goto L_0x00ac
            if (r1 != 0) goto L_0x00ac
            r14 = 0
        L_0x0032:
            r15 = 4
            if (r14 >= r15) goto L_0x003b
            r0.recycleBitmap(r11, r14)
            int r14 = r14 + 1
            goto L_0x0032
        L_0x003b:
            r0.currentImageLocation = r11
            r0.currentImageFilter = r11
            r0.currentImageKey = r11
            r0.currentMediaLocation = r11
            r0.currentMediaFilter = r11
            r0.currentMediaKey = r11
            r0.currentThumbLocation = r11
            r0.currentThumbFilter = r11
            r0.currentThumbKey = r11
            r0.currentMediaDrawable = r11
            r0.mediaShader = r11
            r0.currentImageDrawable = r11
            r0.imageShader = r11
            r0.thumbShader = r11
            r0.crossfadeShader = r11
            r0.currentExt = r8
            r0.currentParentObject = r11
            r0.currentCacheType = r13
            r0.staticThumbDrawable = r7
            r0.currentAlpha = r10
            r0.currentSize = r13
            im.bclpbkiauv.messenger.ImageLoader r10 = im.bclpbkiauv.messenger.ImageLoader.getInstance()
            r10.cancelLoadingForImageReceiver(r0, r12)
            android.view.View r10 = r0.parentView
            if (r10 == 0) goto L_0x0085
            boolean r11 = r0.invalidateAll
            if (r11 == 0) goto L_0x0078
            r10.invalidate()
            goto L_0x0085
        L_0x0078:
            int r11 = r0.imageX
            int r14 = r0.imageY
            int r15 = r0.imageW
            int r15 = r15 + r11
            int r12 = r0.imageH
            int r12 = r12 + r14
            r10.invalidate(r11, r14, r15, r12)
        L_0x0085:
            im.bclpbkiauv.messenger.ImageReceiver$ImageReceiverDelegate r10 = r0.delegate
            if (r10 == 0) goto L_0x00ab
            android.graphics.drawable.Drawable r11 = r0.currentImageDrawable
            if (r11 != 0) goto L_0x009c
            android.graphics.drawable.Drawable r11 = r0.currentThumbDrawable
            if (r11 != 0) goto L_0x009c
            android.graphics.drawable.Drawable r11 = r0.staticThumbDrawable
            if (r11 != 0) goto L_0x009c
            android.graphics.drawable.Drawable r11 = r0.currentMediaDrawable
            if (r11 == 0) goto L_0x009a
            goto L_0x009c
        L_0x009a:
            r11 = 0
            goto L_0x009d
        L_0x009c:
            r11 = 1
        L_0x009d:
            android.graphics.drawable.Drawable r12 = r0.currentImageDrawable
            if (r12 != 0) goto L_0x00a7
            android.graphics.drawable.Drawable r12 = r0.currentMediaDrawable
            if (r12 != 0) goto L_0x00a7
            r12 = 1
            goto L_0x00a8
        L_0x00a7:
            r12 = 0
        L_0x00a8:
            r10.didSetImage(r0, r11, r12)
        L_0x00ab:
            return
        L_0x00ac:
            if (r3 == 0) goto L_0x00b3
            java.lang.String r12 = r3.getKey(r9, r11)
            goto L_0x00b4
        L_0x00b3:
            r12 = r11
        L_0x00b4:
            if (r12 != 0) goto L_0x00b9
            if (r3 == 0) goto L_0x00b9
            r3 = 0
        L_0x00b9:
            r0.currentKeyQuality = r13
            if (r12 != 0) goto L_0x010d
            boolean r14 = r0.needsQualityThumb
            if (r14 == 0) goto L_0x010d
            boolean r14 = r9 instanceof im.bclpbkiauv.messenger.MessageObject
            if (r14 != 0) goto L_0x00cd
            im.bclpbkiauv.tgnet.TLRPC$Document r14 = r0.qulityThumbDocument
            if (r14 == 0) goto L_0x00ca
            goto L_0x00cd
        L_0x00ca:
            r17 = r12
            goto L_0x010f
        L_0x00cd:
            im.bclpbkiauv.tgnet.TLRPC$Document r14 = r0.qulityThumbDocument
            if (r14 == 0) goto L_0x00d2
            goto L_0x00d9
        L_0x00d2:
            r14 = r9
            im.bclpbkiauv.messenger.MessageObject r14 = (im.bclpbkiauv.messenger.MessageObject) r14
            im.bclpbkiauv.tgnet.TLRPC$Document r14 = r14.getDocument()
        L_0x00d9:
            if (r14 == 0) goto L_0x010a
            int r15 = r14.dc_id
            if (r15 == 0) goto L_0x010a
            long r10 = r14.id
            r17 = 0
            int r19 = (r10 > r17 ? 1 : (r10 == r17 ? 0 : -1))
            if (r19 == 0) goto L_0x010a
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "q_"
            r10.append(r11)
            int r11 = r14.dc_id
            r10.append(r11)
            java.lang.String r11 = "_"
            r10.append(r11)
            r17 = r12
            long r11 = r14.id
            r10.append(r11)
            java.lang.String r12 = r10.toString()
            r10 = 1
            r0.currentKeyQuality = r10
            goto L_0x0111
        L_0x010a:
            r17 = r12
            goto L_0x010f
        L_0x010d:
            r17 = r12
        L_0x010f:
            r12 = r17
        L_0x0111:
            java.lang.String r10 = "@"
            if (r12 == 0) goto L_0x0129
            if (r4 == 0) goto L_0x0129
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r12)
            r11.append(r10)
            r11.append(r4)
            java.lang.String r12 = r11.toString()
        L_0x0129:
            if (r1 == 0) goto L_0x0131
            r11 = 0
            java.lang.String r14 = r1.getKey(r9, r11)
            goto L_0x0132
        L_0x0131:
            r14 = 0
        L_0x0132:
            r11 = r14
            if (r11 != 0) goto L_0x0138
            if (r1 == 0) goto L_0x0138
            r1 = 0
        L_0x0138:
            if (r11 == 0) goto L_0x014e
            if (r2 == 0) goto L_0x014e
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r11)
            r14.append(r10)
            r14.append(r2)
            java.lang.String r11 = r14.toString()
        L_0x014e:
            if (r11 != 0) goto L_0x015a
            java.lang.String r14 = r0.currentImageKey
            if (r14 == 0) goto L_0x015a
            boolean r14 = r14.equals(r12)
            if (r14 != 0) goto L_0x0164
        L_0x015a:
            java.lang.String r14 = r0.currentMediaKey
            if (r14 == 0) goto L_0x0193
            boolean r14 = r14.equals(r11)
            if (r14 == 0) goto L_0x0193
        L_0x0164:
            im.bclpbkiauv.messenger.ImageReceiver$ImageReceiverDelegate r14 = r0.delegate
            if (r14 == 0) goto L_0x018a
            android.graphics.drawable.Drawable r15 = r0.currentImageDrawable
            if (r15 != 0) goto L_0x017b
            android.graphics.drawable.Drawable r15 = r0.currentThumbDrawable
            if (r15 != 0) goto L_0x017b
            android.graphics.drawable.Drawable r15 = r0.staticThumbDrawable
            if (r15 != 0) goto L_0x017b
            android.graphics.drawable.Drawable r15 = r0.currentMediaDrawable
            if (r15 == 0) goto L_0x0179
            goto L_0x017b
        L_0x0179:
            r15 = 0
            goto L_0x017c
        L_0x017b:
            r15 = 1
        L_0x017c:
            android.graphics.drawable.Drawable r13 = r0.currentImageDrawable
            if (r13 != 0) goto L_0x0186
            android.graphics.drawable.Drawable r13 = r0.currentMediaDrawable
            if (r13 != 0) goto L_0x0186
            r13 = 1
            goto L_0x0187
        L_0x0186:
            r13 = 0
        L_0x0187:
            r14.didSetImage(r0, r15, r13)
        L_0x018a:
            boolean r13 = r0.canceledLoading
            if (r13 != 0) goto L_0x0193
            boolean r13 = r0.forcePreview
            if (r13 != 0) goto L_0x0193
            return
        L_0x0193:
            im.bclpbkiauv.messenger.ImageLocation r13 = r0.strippedLocation
            if (r13 == 0) goto L_0x019a
            im.bclpbkiauv.messenger.ImageLocation r13 = r0.strippedLocation
            goto L_0x019f
        L_0x019a:
            if (r1 == 0) goto L_0x019e
            r13 = r1
            goto L_0x019f
        L_0x019e:
            r13 = r3
        L_0x019f:
            if (r5 == 0) goto L_0x01a6
            java.lang.String r15 = r5.getKey(r9, r13)
            goto L_0x01a7
        L_0x01a6:
            r15 = 0
        L_0x01a7:
            r14 = r15
            if (r14 == 0) goto L_0x01be
            if (r6 == 0) goto L_0x01be
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r14)
            r15.append(r10)
            r15.append(r6)
            java.lang.String r14 = r15.toString()
        L_0x01be:
            boolean r10 = r0.crossfadeWithOldImage
            r15 = 2
            if (r10 == 0) goto L_0x024b
            android.graphics.drawable.Drawable r10 = r0.currentImageDrawable
            if (r10 == 0) goto L_0x01e9
            r10 = 1
            r0.recycleBitmap(r14, r10)
            r10 = 0
            r0.recycleBitmap(r10, r15)
            r10 = 3
            r0.recycleBitmap(r11, r10)
            android.graphics.BitmapShader r10 = r0.imageShader
            r0.crossfadeShader = r10
            android.graphics.drawable.Drawable r10 = r0.currentImageDrawable
            r0.crossfadeImage = r10
            java.lang.String r10 = r0.currentImageKey
            r0.crossfadeKey = r10
            r10 = 0
            r0.crossfadingWithThumb = r10
            r15 = 0
            r0.currentImageDrawable = r15
            r0.currentImageKey = r15
            goto L_0x025f
        L_0x01e9:
            r10 = 0
            r15 = 0
            android.graphics.drawable.Drawable r15 = r0.currentThumbDrawable
            if (r15 == 0) goto L_0x0210
            r0.recycleBitmap(r12, r10)
            r10 = 2
            r15 = 0
            r0.recycleBitmap(r15, r10)
            r10 = 3
            r0.recycleBitmap(r11, r10)
            android.graphics.BitmapShader r10 = r0.thumbShader
            r0.crossfadeShader = r10
            android.graphics.drawable.Drawable r10 = r0.currentThumbDrawable
            r0.crossfadeImage = r10
            java.lang.String r10 = r0.currentThumbKey
            r0.crossfadeKey = r10
            r10 = 0
            r0.crossfadingWithThumb = r10
            r15 = 0
            r0.currentThumbDrawable = r15
            r0.currentThumbKey = r15
            goto L_0x025f
        L_0x0210:
            r15 = 0
            android.graphics.drawable.Drawable r15 = r0.staticThumbDrawable
            if (r15 == 0) goto L_0x0238
            r0.recycleBitmap(r12, r10)
            r10 = 1
            r0.recycleBitmap(r14, r10)
            r10 = 2
            r15 = 0
            r0.recycleBitmap(r15, r10)
            r10 = 3
            r0.recycleBitmap(r11, r10)
            android.graphics.BitmapShader r10 = r0.thumbShader
            r0.crossfadeShader = r10
            android.graphics.drawable.Drawable r10 = r0.staticThumbDrawable
            r0.crossfadeImage = r10
            r10 = 0
            r0.crossfadingWithThumb = r10
            r15 = 0
            r0.crossfadeKey = r15
            r0.currentThumbDrawable = r15
            r0.currentThumbKey = r15
            goto L_0x025f
        L_0x0238:
            r15 = 0
            r0.recycleBitmap(r12, r10)
            r10 = 1
            r0.recycleBitmap(r14, r10)
            r10 = 2
            r0.recycleBitmap(r15, r10)
            r10 = 3
            r0.recycleBitmap(r11, r10)
            r0.crossfadeShader = r15
            goto L_0x025f
        L_0x024b:
            r10 = 2
            r15 = 0
            r10 = 0
            r0.recycleBitmap(r12, r10)
            r10 = 1
            r0.recycleBitmap(r14, r10)
            r10 = 2
            r0.recycleBitmap(r15, r10)
            r10 = 3
            r0.recycleBitmap(r11, r10)
            r0.crossfadeShader = r15
        L_0x025f:
            r0.currentImageLocation = r3
            r0.currentImageFilter = r4
            r0.currentImageKey = r12
            r0.currentMediaLocation = r1
            r0.currentMediaFilter = r2
            r0.currentMediaKey = r11
            r0.currentThumbLocation = r5
            r0.currentThumbFilter = r6
            r0.currentThumbKey = r14
            r0.currentParentObject = r9
            r0.currentExt = r8
            r10 = r28
            r0.currentSize = r10
            r15 = r31
            r0.currentCacheType = r15
            r0.staticThumbDrawable = r7
            r21 = r1
            r1 = 0
            r0.imageShader = r1
            r0.thumbShader = r1
            r0.mediaShader = r1
            r1 = 1065353216(0x3f800000, float:1.0)
            r0.currentAlpha = r1
            im.bclpbkiauv.messenger.ImageReceiver$ImageReceiverDelegate r1 = r0.delegate
            if (r1 == 0) goto L_0x02b3
            android.graphics.drawable.Drawable r2 = r0.currentImageDrawable
            if (r2 != 0) goto L_0x02a1
            android.graphics.drawable.Drawable r2 = r0.currentThumbDrawable
            if (r2 != 0) goto L_0x02a1
            if (r7 != 0) goto L_0x02a1
            android.graphics.drawable.Drawable r2 = r0.currentMediaDrawable
            if (r2 == 0) goto L_0x029f
            goto L_0x02a1
        L_0x029f:
            r2 = 0
            goto L_0x02a2
        L_0x02a1:
            r2 = 1
        L_0x02a2:
            r16 = r3
            android.graphics.drawable.Drawable r3 = r0.currentImageDrawable
            if (r3 != 0) goto L_0x02ae
            android.graphics.drawable.Drawable r3 = r0.currentMediaDrawable
            if (r3 != 0) goto L_0x02ae
            r3 = 1
            goto L_0x02af
        L_0x02ae:
            r3 = 0
        L_0x02af:
            r1.didSetImage(r0, r2, r3)
            goto L_0x02b5
        L_0x02b3:
            r16 = r3
        L_0x02b5:
            im.bclpbkiauv.messenger.ImageLoader r1 = im.bclpbkiauv.messenger.ImageLoader.getInstance()
            r1.loadImageForImageReceiver(r0)
            android.view.View r1 = r0.parentView
            if (r1 == 0) goto L_0x02d5
            boolean r2 = r0.invalidateAll
            if (r2 == 0) goto L_0x02c8
            r1.invalidate()
            goto L_0x02d5
        L_0x02c8:
            int r2 = r0.imageX
            int r3 = r0.imageY
            int r4 = r0.imageW
            int r4 = r4 + r2
            int r5 = r0.imageH
            int r5 = r5 + r3
            r1.invalidate(r2, r3, r4, r5)
        L_0x02d5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ImageReceiver.setImage(im.bclpbkiauv.messenger.ImageLocation, java.lang.String, im.bclpbkiauv.messenger.ImageLocation, java.lang.String, im.bclpbkiauv.messenger.ImageLocation, java.lang.String, android.graphics.drawable.Drawable, int, java.lang.String, java.lang.Object, int):void");
    }

    public boolean canInvertBitmap() {
        return (this.currentMediaDrawable instanceof ExtendedBitmapDrawable) || (this.currentImageDrawable instanceof ExtendedBitmapDrawable) || (this.currentThumbDrawable instanceof ExtendedBitmapDrawable) || (this.staticThumbDrawable instanceof ExtendedBitmapDrawable);
    }

    public void setColorFilter(ColorFilter filter) {
        this.colorFilter = filter;
    }

    public void setDelegate(ImageReceiverDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setPressed(int value) {
        this.isPressed = value;
    }

    public boolean getPressed() {
        return this.isPressed != 0;
    }

    public void setOrientation(int angle, boolean center) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle > 360) {
            angle -= 360;
        }
        this.thumbOrientation = angle;
        this.imageOrientation = angle;
        this.centerRotation = center;
    }

    public void setInvalidateAll(boolean value) {
        this.invalidateAll = value;
    }

    public Drawable getStaticThumb() {
        return this.staticThumbDrawable;
    }

    public int getAnimatedOrientation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            return animation.getOrientation();
        }
        return 0;
    }

    public int getOrientation() {
        return this.imageOrientation;
    }

    public void setLayerNum(int value) {
        this.currentLayerNum = value;
    }

    public void setImageBitmap(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = null;
        if (bitmap != null) {
            bitmapDrawable = new BitmapDrawable((Resources) null, bitmap);
        }
        setImageBitmap((Drawable) bitmapDrawable);
    }

    public void setImageBitmap(Drawable bitmap) {
        boolean z = true;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        if (!this.crossfadeWithOldImage) {
            for (int a = 0; a < 4; a++) {
                recycleBitmap((String) null, a);
            }
        } else if (this.currentImageDrawable != null) {
            recycleBitmap((String) null, 1);
            recycleBitmap((String) null, 2);
            recycleBitmap((String) null, 3);
            this.crossfadeShader = this.imageShader;
            this.crossfadeImage = this.currentImageDrawable;
            this.crossfadeKey = this.currentImageKey;
            this.crossfadingWithThumb = true;
        } else if (this.currentThumbDrawable != null) {
            recycleBitmap((String) null, 0);
            recycleBitmap((String) null, 2);
            recycleBitmap((String) null, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.currentThumbDrawable;
            this.crossfadeKey = this.currentThumbKey;
            this.crossfadingWithThumb = true;
        } else if (this.staticThumbDrawable != null) {
            recycleBitmap((String) null, 0);
            recycleBitmap((String) null, 1);
            recycleBitmap((String) null, 2);
            recycleBitmap((String) null, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.staticThumbDrawable;
            this.crossfadingWithThumb = true;
            this.crossfadeKey = null;
        } else {
            for (int a2 = 0; a2 < 4; a2++) {
                recycleBitmap((String) null, a2);
            }
            this.crossfadeShader = null;
        }
        Drawable drawable = this.staticThumbDrawable;
        if (drawable instanceof RecyclableDrawable) {
            ((RecyclableDrawable) drawable).recycle();
        }
        if (bitmap instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) bitmap;
            fileDrawable.setParentView(this.parentView);
            fileDrawable.setUseSharedQueue(this.useSharedAnimationQueue);
            if (this.allowStartAnimation) {
                fileDrawable.start();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        } else if (bitmap instanceof RLottieDrawable) {
            RLottieDrawable fileDrawable2 = (RLottieDrawable) bitmap;
            fileDrawable2.addParentView(this.parentView);
            if (this.currentOpenedLayerFlags == 0) {
                fileDrawable2.start();
            }
            fileDrawable2.setAllowDecodeSingleFrame(true);
        }
        this.staticThumbDrawable = bitmap;
        int i = this.roundRadius;
        if (i == 0 || !(bitmap instanceof BitmapDrawable)) {
            this.thumbShader = null;
        } else if (!(bitmap instanceof RLottieDrawable)) {
            if (bitmap instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) bitmap).setRoundRadius(i);
            } else {
                this.thumbShader = new BitmapShader(((BitmapDrawable) bitmap).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
        }
        this.currentMediaLocation = null;
        this.currentMediaFilter = null;
        this.currentMediaDrawable = null;
        this.currentMediaKey = null;
        this.mediaShader = null;
        this.currentImageLocation = null;
        this.currentImageFilter = null;
        this.currentImageDrawable = null;
        this.currentImageKey = null;
        this.imageShader = null;
        this.currentThumbLocation = null;
        this.currentThumbFilter = null;
        this.currentThumbKey = null;
        this.currentKeyQuality = false;
        this.currentExt = null;
        this.currentSize = 0;
        this.currentCacheType = 0;
        this.currentAlpha = 1.0f;
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 != null) {
            setImageBackup2.imageLocation = null;
            this.setImageBackup.thumbLocation = null;
            this.setImageBackup.mediaLocation = null;
            this.setImageBackup.thumb = null;
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            imageReceiverDelegate.didSetImage(this, (this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true, true);
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                int i2 = this.imageX;
                int i3 = this.imageY;
                view.invalidate(i2, i3, this.imageW + i2, this.imageH + i3);
            }
        }
        if (this.forceCrossfade && this.crossfadeWithOldImage && this.crossfadeImage != null) {
            this.currentAlpha = 0.0f;
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            if (this.currentThumbDrawable == null && this.staticThumbDrawable == null) {
                z = false;
            }
            this.crossfadeWithThumb = z;
        }
    }

    public void clearImage() {
        for (int a = 0; a < 4; a++) {
            recycleBitmap((String) null, a);
        }
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
    }

    public void onDetachedFromWindow() {
        if (!(this.currentImageLocation == null && this.currentMediaLocation == null && this.currentThumbLocation == null && this.staticThumbDrawable == null)) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            this.setImageBackup.mediaLocation = this.currentMediaLocation;
            this.setImageBackup.mediaFilter = this.currentMediaFilter;
            this.setImageBackup.imageLocation = this.currentImageLocation;
            this.setImageBackup.imageFilter = this.currentImageFilter;
            this.setImageBackup.thumbLocation = this.currentThumbLocation;
            this.setImageBackup.thumbFilter = this.currentThumbFilter;
            this.setImageBackup.thumb = this.staticThumbDrawable;
            this.setImageBackup.size = this.currentSize;
            this.setImageBackup.ext = this.currentExt;
            this.setImageBackup.cacheType = this.currentCacheType;
            this.setImageBackup.parentObject = this.currentParentObject;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
        clearImage();
    }

    public boolean onAttachedToWindow() {
        RLottieDrawable lottieDrawable;
        RLottieDrawable lottieDrawable2;
        int currentHeavyOperationFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags = currentHeavyOperationFlags;
        this.currentOpenedLayerFlags = currentHeavyOperationFlags & (~this.currentLayerNum);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 != null && (setImageBackup2.imageLocation != null || this.setImageBackup.thumbLocation != null || this.setImageBackup.mediaLocation != null || this.setImageBackup.thumb != null)) {
            setImage(this.setImageBackup.mediaLocation, this.setImageBackup.mediaFilter, this.setImageBackup.imageLocation, this.setImageBackup.imageFilter, this.setImageBackup.thumbLocation, this.setImageBackup.thumbFilter, this.setImageBackup.thumb, this.setImageBackup.size, this.setImageBackup.ext, this.setImageBackup.parentObject, this.setImageBackup.cacheType);
            if (this.currentOpenedLayerFlags != 0 || (lottieDrawable2 = getLottieAnimation()) == null) {
                return true;
            }
            lottieDrawable2.start();
            return true;
        } else if (this.currentOpenedLayerFlags != 0 || (lottieDrawable = getLottieAnimation()) == null) {
            return false;
        } else {
            lottieDrawable.start();
            return false;
        }
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int alpha, BitmapShader shader, int orientation) {
        Paint paint;
        int bitmapH;
        int bitmapW;
        int i;
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i2 = alpha;
        BitmapShader bitmapShader = shader;
        int i3 = orientation;
        if (drawable2 instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
            if (bitmapShader != null) {
                paint = this.roundPaint;
            } else {
                paint = bitmapDrawable.getPaint();
            }
            boolean hasFilter = (paint == null || paint.getColorFilter() == null) ? false : true;
            if (!hasFilter || this.isPressed != 0) {
                if (!hasFilter && (i = this.isPressed) != 0) {
                    if (i == 1) {
                        if (bitmapShader != null) {
                            this.roundPaint.setColorFilter(selectedColorFilter);
                        } else {
                            bitmapDrawable.setColorFilter(selectedColorFilter);
                        }
                    } else if (bitmapShader != null) {
                        this.roundPaint.setColorFilter(selectedGroupColorFilter);
                    } else {
                        bitmapDrawable.setColorFilter(selectedGroupColorFilter);
                    }
                }
            } else if (bitmapShader != null) {
                this.roundPaint.setColorFilter((ColorFilter) null);
            } else if (this.staticThumbDrawable != drawable2) {
                bitmapDrawable.setColorFilter((ColorFilter) null);
            }
            ColorFilter colorFilter2 = this.colorFilter;
            if (colorFilter2 != null) {
                if (bitmapShader != null) {
                    this.roundPaint.setColorFilter(colorFilter2);
                } else {
                    bitmapDrawable.setColorFilter(colorFilter2);
                }
            }
            if ((bitmapDrawable instanceof AnimatedFileDrawable) || (bitmapDrawable instanceof RLottieDrawable)) {
                if (i3 % 360 == 90 || i3 % 360 == 270) {
                    int bitmapW2 = bitmapDrawable.getIntrinsicHeight();
                    bitmapH = bitmapDrawable.getIntrinsicWidth();
                    bitmapW = bitmapW2;
                } else {
                    int bitmapW3 = bitmapDrawable.getIntrinsicWidth();
                    bitmapH = bitmapDrawable.getIntrinsicHeight();
                    bitmapW = bitmapW3;
                }
            } else if (i3 % 360 == 90 || i3 % 360 == 270) {
                int bitmapW4 = bitmapDrawable.getBitmap().getHeight();
                bitmapH = bitmapDrawable.getBitmap().getWidth();
                bitmapW = bitmapW4;
            } else {
                int bitmapW5 = bitmapDrawable.getBitmap().getWidth();
                bitmapH = bitmapDrawable.getBitmap().getHeight();
                bitmapW = bitmapW5;
            }
            int bitmapW6 = this.imageW;
            float f = this.sideClip;
            float realImageW = ((float) bitmapW6) - (f * 2.0f);
            float realImageH = ((float) this.imageH) - (f * 2.0f);
            float scaleW = bitmapW6 == 0 ? 1.0f : ((float) bitmapW) / realImageW;
            float scaleH = this.imageH == 0 ? 1.0f : ((float) bitmapH) / realImageH;
            if (bitmapShader == null) {
                Canvas canvas3 = canvas2;
                int i4 = i3;
                BitmapDrawable bitmapDrawable2 = bitmapDrawable;
                float scaleW2 = scaleW;
                float scaleH2 = scaleH;
                int i5 = i2;
                Paint paint2 = paint;
                float scaleH3 = hasFilter;
                if (this.isAspectFit) {
                    float scale = Math.max(scaleW2, scaleH2);
                    canvas.save();
                    int bitmapW7 = (int) (((float) bitmapW) / scale);
                    int bitmapH2 = (int) (((float) bitmapH) / scale);
                    RectF rectF = this.drawRegion;
                    int i6 = this.imageX;
                    int i7 = this.imageW;
                    float f2 = scale;
                    int i8 = this.imageY;
                    float f3 = realImageH;
                    int i9 = this.imageH;
                    float f4 = realImageW;
                    rectF.set(((float) i6) + (((float) (i7 - bitmapW7)) / 2.0f), ((float) i8) + (((float) (i9 - bitmapH2)) / 2.0f), ((float) i6) + (((float) (i7 + bitmapW7)) / 2.0f), ((float) i8) + (((float) (i9 + bitmapH2)) / 2.0f));
                    BitmapDrawable bitmapDrawable3 = bitmapDrawable2;
                    bitmapDrawable3.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                    if (bitmapDrawable3 instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) bitmapDrawable3).setActualDrawRect(this.drawRegion.left, this.drawRegion.top, this.drawRegion.width(), this.drawRegion.height());
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable3.setAlpha(i5);
                            bitmapDrawable3.draw(canvas3);
                        } catch (Exception e) {
                            onBitmapException(bitmapDrawable3);
                            FileLog.e((Throwable) e);
                        }
                    }
                    canvas.restore();
                } else {
                    float f5 = realImageW;
                    BitmapDrawable bitmapDrawable4 = bitmapDrawable2;
                    if (Math.abs(scaleW2 - scaleH2) > 1.0E-5f) {
                        canvas.save();
                        int i10 = this.imageX;
                        int i11 = this.imageY;
                        canvas3.clipRect(i10, i11, this.imageW + i10, this.imageH + i11);
                        if (i4 % 360 != 0) {
                            if (this.centerRotation) {
                                canvas3.rotate((float) i4, (float) (this.imageW / 2), (float) (this.imageH / 2));
                            } else {
                                canvas3.rotate((float) i4, 0.0f, 0.0f);
                            }
                        }
                        int i12 = this.imageW;
                        if (((float) bitmapW) / scaleH2 > ((float) i12)) {
                            int bitmapW8 = (int) (((float) bitmapW) / scaleH2);
                            RectF rectF2 = this.drawRegion;
                            int i13 = this.imageX;
                            int i14 = this.imageY;
                            rectF2.set(((float) i13) - (((float) (bitmapW8 - i12)) / 2.0f), (float) i14, ((float) i13) + (((float) (i12 + bitmapW8)) / 2.0f), (float) (i14 + this.imageH));
                            float f6 = scaleW2;
                        } else {
                            int bitmapH3 = (int) (((float) bitmapH) / scaleW2);
                            RectF rectF3 = this.drawRegion;
                            int i15 = this.imageX;
                            int i16 = this.imageY;
                            int i17 = this.imageH;
                            float f7 = scaleW2;
                            rectF3.set((float) i15, ((float) i16) - (((float) (bitmapH3 - i17)) / 2.0f), (float) (i15 + i12), ((float) i16) + (((float) (i17 + bitmapH3)) / 2.0f));
                        }
                        if (bitmapDrawable4 instanceof AnimatedFileDrawable) {
                            ((AnimatedFileDrawable) bitmapDrawable4).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                        }
                        if (i4 % 360 == 90 || i4 % 360 == 270) {
                            float width = this.drawRegion.width() / 2.0f;
                            float height = this.drawRegion.height() / 2.0f;
                            float centerX = this.drawRegion.centerX();
                            float centerY = this.drawRegion.centerY();
                            bitmapDrawable4.setBounds((int) (centerX - height), (int) (centerY - width), (int) (centerX + height), (int) (centerY + width));
                        } else {
                            bitmapDrawable4.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                        }
                        if (this.isVisible) {
                            try {
                                bitmapDrawable4.setAlpha(i5);
                                bitmapDrawable4.draw(canvas3);
                            } catch (Exception e2) {
                                onBitmapException(bitmapDrawable4);
                                FileLog.e((Throwable) e2);
                            }
                        }
                        canvas.restore();
                    } else {
                        canvas.save();
                        if (i4 % 360 != 0) {
                            if (this.centerRotation) {
                                canvas3.rotate((float) i4, (float) (this.imageW / 2), (float) (this.imageH / 2));
                            } else {
                                canvas3.rotate((float) i4, 0.0f, 0.0f);
                            }
                        }
                        RectF rectF4 = this.drawRegion;
                        int i18 = this.imageX;
                        int i19 = this.imageY;
                        rectF4.set((float) i18, (float) i19, (float) (i18 + this.imageW), (float) (i19 + this.imageH));
                        if (bitmapDrawable4 instanceof AnimatedFileDrawable) {
                            ((AnimatedFileDrawable) bitmapDrawable4).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                        }
                        if (i4 % 360 == 90 || i4 % 360 == 270) {
                            float width2 = this.drawRegion.width() / 2.0f;
                            float height2 = this.drawRegion.height() / 2.0f;
                            float centerX2 = this.drawRegion.centerX();
                            float centerY2 = this.drawRegion.centerY();
                            bitmapDrawable4.setBounds((int) (centerX2 - height2), (int) (centerY2 - width2), (int) (centerX2 + height2), (int) (centerY2 + width2));
                        } else {
                            bitmapDrawable4.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                        }
                        if (this.isVisible) {
                            try {
                                bitmapDrawable4.setAlpha(i5);
                                bitmapDrawable4.draw(canvas3);
                            } catch (Exception e3) {
                                onBitmapException(bitmapDrawable4);
                                FileLog.e((Throwable) e3);
                            }
                        }
                        canvas.restore();
                    }
                }
            } else if (this.isAspectFit) {
                float scaleW3 = scaleW;
                float scale2 = Math.max(scaleW3, scaleH);
                int bitmapW9 = (int) (((float) bitmapW) / scale2);
                int bitmapH4 = (int) (((float) bitmapH) / scale2);
                RectF rectF5 = this.drawRegion;
                Paint paint3 = paint;
                int i20 = this.imageX;
                boolean z = hasFilter;
                int i21 = this.imageW;
                float f8 = scaleW3;
                int i22 = this.imageY;
                BitmapDrawable bitmapDrawable5 = bitmapDrawable;
                int i23 = this.imageH;
                rectF5.set((float) (i20 + ((i21 - bitmapW9) / 2)), (float) (i22 + ((i23 - bitmapH4) / 2)), (float) (i20 + ((i21 + bitmapW9) / 2)), (float) (i22 + ((i23 + bitmapH4) / 2)));
                if (this.isVisible) {
                    this.roundPaint.setShader(bitmapShader);
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate(this.drawRegion.left, this.drawRegion.top);
                    this.shaderMatrix.preScale(1.0f / scale2, 1.0f / scale2);
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i2);
                    this.roundRect.set(this.drawRegion);
                    RectF rectF6 = this.roundRect;
                    int i24 = this.roundRadius;
                    canvas2.drawRoundRect(rectF6, (float) i24, (float) i24, this.roundPaint);
                }
                Canvas canvas4 = canvas2;
                int i25 = i2;
                int i26 = orientation;
            } else {
                float scaleW4 = scaleW;
                float scaleH4 = scaleH;
                Paint paint4 = paint;
                float scaleH5 = hasFilter;
                this.roundPaint.setShader(bitmapShader);
                float scale3 = 1.0f / Math.min(scaleW4, scaleH4);
                RectF rectF7 = this.roundRect;
                int i27 = this.imageX;
                float f9 = this.sideClip;
                int i28 = this.imageY;
                rectF7.set(((float) i27) + f9, ((float) i28) + f9, ((float) (i27 + this.imageW)) - f9, ((float) (i28 + this.imageH)) - f9);
                this.shaderMatrix.reset();
                if (Math.abs(scaleW4 - scaleH4) <= 5.0E-4f) {
                    RectF rectF8 = this.drawRegion;
                    int i29 = this.imageX;
                    int i30 = this.imageY;
                    rectF8.set((float) i29, (float) i30, ((float) i29) + realImageW, ((float) i30) + realImageH);
                } else if (((float) bitmapW) / scaleH4 > realImageW) {
                    int bitmapW10 = (int) (((float) bitmapW) / scaleH4);
                    RectF rectF9 = this.drawRegion;
                    int i31 = this.imageX;
                    int i32 = this.imageY;
                    rectF9.set(((float) i31) - ((((float) bitmapW10) - realImageW) / 2.0f), (float) i32, ((float) i31) + ((((float) bitmapW10) + realImageW) / 2.0f), ((float) i32) + realImageH);
                } else {
                    int bitmapH5 = (int) (((float) bitmapH) / scaleW4);
                    RectF rectF10 = this.drawRegion;
                    int i33 = this.imageX;
                    int i34 = this.imageY;
                    rectF10.set((float) i33, ((float) i34) - ((((float) bitmapH5) - realImageH) / 2.0f), ((float) i33) + realImageW, ((float) i34) + ((((float) bitmapH5) + realImageH) / 2.0f));
                }
                if (this.isVisible) {
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate(this.drawRegion.left + this.sideClip, this.drawRegion.top + this.sideClip);
                    int i35 = orientation;
                    if (i35 == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, -this.drawRegion.width());
                    } else if (i35 == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.width(), -this.drawRegion.height());
                    } else if (i35 == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.height(), 0.0f);
                    }
                    this.shaderMatrix.preScale(scale3, scale3);
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(alpha);
                    RectF rectF11 = this.roundRect;
                    int i36 = this.roundRadius;
                    canvas.drawRoundRect(rectF11, (float) i36, (float) i36, this.roundPaint);
                } else {
                    Canvas canvas5 = canvas;
                    int i37 = alpha;
                    int i38 = orientation;
                }
            }
            Drawable drawable3 = drawable;
            return;
        }
        Canvas canvas6 = canvas2;
        int i39 = i3;
        int i40 = i2;
        RectF rectF12 = this.drawRegion;
        int i41 = this.imageX;
        int i42 = this.imageY;
        rectF12.set((float) i41, (float) i42, (float) (i41 + this.imageW), (float) (i42 + this.imageH));
        Drawable drawable4 = drawable;
        drawable4.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
        if (this.isVisible) {
            try {
                drawable.setAlpha(alpha);
                drawable4.draw(canvas6);
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
        }
    }

    private void onBitmapException(Drawable bitmapDrawable) {
        if (bitmapDrawable == this.currentMediaDrawable && this.currentMediaKey != null) {
            ImageLoader.getInstance().removeImage(this.currentMediaKey);
            this.currentMediaKey = null;
        } else if (bitmapDrawable == this.currentImageDrawable && this.currentImageKey != null) {
            ImageLoader.getInstance().removeImage(this.currentImageKey);
            this.currentImageKey = null;
        } else if (bitmapDrawable == this.currentThumbDrawable && this.currentThumbKey != null) {
            ImageLoader.getInstance().removeImage(this.currentThumbKey);
            this.currentThumbKey = null;
        }
        setImage(this.currentMediaLocation, this.currentMediaFilter, this.currentImageLocation, this.currentImageFilter, this.currentThumbLocation, this.currentThumbFilter, this.currentThumbDrawable, this.currentSize, this.currentExt, this.currentParentObject, this.currentCacheType);
    }

    private void checkAlphaAnimation(boolean skip) {
        if (!this.manualAlphaAnimator && this.currentAlpha != 1.0f) {
            if (!skip) {
                long dt = System.currentTimeMillis() - this.lastUpdateAlphaTime;
                if (dt > 18) {
                    dt = 18;
                }
                float f = this.currentAlpha + (((float) dt) / 150.0f);
                this.currentAlpha = f;
                if (f > 1.0f) {
                    this.currentAlpha = 1.0f;
                    if (this.crossfadeImage != null) {
                        recycleBitmap((String) null, 2);
                        this.crossfadeShader = null;
                    }
                }
            }
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            View view = this.parentView;
            if (view == null) {
                return;
            }
            if (this.invalidateAll) {
                view.invalidate();
                return;
            }
            int i = this.imageX;
            int i2 = this.imageY;
            view.invalidate(i, i2, this.imageW + i, this.imageH + i2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:88:0x0144 A[Catch:{ Exception -> 0x01a3 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean draw(android.graphics.Canvas r19) {
        /*
            r18 = this;
            r7 = r18
            r0 = 0
            r8 = 0
            im.bclpbkiauv.ui.components.AnimatedFileDrawable r1 = r18.getAnimation()     // Catch:{ Exception -> 0x01a3 }
            r9 = r1
            im.bclpbkiauv.ui.components.RLottieDrawable r1 = r18.getLottieAnimation()     // Catch:{ Exception -> 0x01a3 }
            r10 = r1
            r11 = 1
            if (r9 == 0) goto L_0x0017
            boolean r1 = r9.hasBitmap()     // Catch:{ Exception -> 0x01a3 }
            if (r1 == 0) goto L_0x001f
        L_0x0017:
            if (r10 == 0) goto L_0x0021
            boolean r1 = r10.hasBitmap()     // Catch:{ Exception -> 0x01a3 }
            if (r1 != 0) goto L_0x0021
        L_0x001f:
            r1 = 1
            goto L_0x0022
        L_0x0021:
            r1 = 0
        L_0x0022:
            if (r10 == 0) goto L_0x0029
            android.view.View r2 = r7.parentView     // Catch:{ Exception -> 0x01a3 }
            r10.setCurrentParentView(r2)     // Catch:{ Exception -> 0x01a3 }
        L_0x0029:
            if (r9 != 0) goto L_0x002d
            if (r10 == 0) goto L_0x003e
        L_0x002d:
            if (r1 != 0) goto L_0x003e
            boolean r2 = r7.animationReadySent     // Catch:{ Exception -> 0x01a3 }
            if (r2 != 0) goto L_0x003e
            r7.animationReadySent = r11     // Catch:{ Exception -> 0x01a3 }
            im.bclpbkiauv.messenger.ImageReceiver$ImageReceiverDelegate r2 = r7.delegate     // Catch:{ Exception -> 0x01a3 }
            if (r2 == 0) goto L_0x003e
            im.bclpbkiauv.messenger.ImageReceiver$ImageReceiverDelegate r2 = r7.delegate     // Catch:{ Exception -> 0x01a3 }
            r2.onAnimationReady(r7)     // Catch:{ Exception -> 0x01a3 }
        L_0x003e:
            r2 = 0
            r3 = 0
            boolean r4 = r7.forcePreview     // Catch:{ Exception -> 0x01a3 }
            if (r4 != 0) goto L_0x0058
            android.graphics.drawable.Drawable r4 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r4 == 0) goto L_0x0058
            if (r1 != 0) goto L_0x0058
            android.graphics.drawable.Drawable r4 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01a3 }
            r0 = r4
            android.graphics.BitmapShader r4 = r7.mediaShader     // Catch:{ Exception -> 0x01a3 }
            r3 = r4
            int r4 = r7.imageOrientation     // Catch:{ Exception -> 0x01a3 }
            r2 = r4
            r12 = r1
            r13 = r2
            r14 = r3
            goto L_0x00b0
        L_0x0058:
            boolean r4 = r7.forcePreview     // Catch:{ Exception -> 0x01a3 }
            if (r4 != 0) goto L_0x0074
            android.graphics.drawable.Drawable r4 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r4 == 0) goto L_0x0074
            if (r1 == 0) goto L_0x0066
            android.graphics.drawable.Drawable r4 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r4 == 0) goto L_0x0074
        L_0x0066:
            android.graphics.drawable.Drawable r4 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01a3 }
            r0 = r4
            android.graphics.BitmapShader r4 = r7.imageShader     // Catch:{ Exception -> 0x01a3 }
            r3 = r4
            int r4 = r7.imageOrientation     // Catch:{ Exception -> 0x01a3 }
            r2 = r4
            r1 = 0
            r12 = r1
            r13 = r2
            r14 = r3
            goto L_0x00b0
        L_0x0074:
            android.graphics.drawable.Drawable r4 = r7.crossfadeImage     // Catch:{ Exception -> 0x01a3 }
            if (r4 == 0) goto L_0x0089
            boolean r4 = r7.crossfadingWithThumb     // Catch:{ Exception -> 0x01a3 }
            if (r4 != 0) goto L_0x0089
            android.graphics.drawable.Drawable r4 = r7.crossfadeImage     // Catch:{ Exception -> 0x01a3 }
            r0 = r4
            android.graphics.BitmapShader r4 = r7.crossfadeShader     // Catch:{ Exception -> 0x01a3 }
            r3 = r4
            int r4 = r7.imageOrientation     // Catch:{ Exception -> 0x01a3 }
            r2 = r4
            r12 = r1
            r13 = r2
            r14 = r3
            goto L_0x00b0
        L_0x0089:
            android.graphics.drawable.Drawable r4 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            boolean r4 = r4 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r4 == 0) goto L_0x009c
            android.graphics.drawable.Drawable r4 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            r0 = r4
            android.graphics.BitmapShader r4 = r7.thumbShader     // Catch:{ Exception -> 0x01a3 }
            r3 = r4
            int r4 = r7.thumbOrientation     // Catch:{ Exception -> 0x01a3 }
            r2 = r4
            r12 = r1
            r13 = r2
            r14 = r3
            goto L_0x00b0
        L_0x009c:
            android.graphics.drawable.Drawable r4 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r4 == 0) goto L_0x00ad
            android.graphics.drawable.Drawable r4 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            r0 = r4
            android.graphics.BitmapShader r4 = r7.thumbShader     // Catch:{ Exception -> 0x01a3 }
            r3 = r4
            int r4 = r7.thumbOrientation     // Catch:{ Exception -> 0x01a3 }
            r2 = r4
            r12 = r1
            r13 = r2
            r14 = r3
            goto L_0x00b0
        L_0x00ad:
            r12 = r1
            r13 = r2
            r14 = r3
        L_0x00b0:
            r15 = 1132396544(0x437f0000, float:255.0)
            if (r0 == 0) goto L_0x0186
            byte r1 = r7.crossfadeAlpha     // Catch:{ Exception -> 0x01a3 }
            if (r1 == 0) goto L_0x016a
            boolean r1 = r7.crossfadeWithThumb     // Catch:{ Exception -> 0x01a3 }
            if (r1 == 0) goto L_0x00cf
            if (r12 == 0) goto L_0x00cf
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01a3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01a3 }
            r1 = r18
            r2 = r19
            r3 = r0
            r5 = r14
            r6 = r13
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01a3 }
            goto L_0x0179
        L_0x00cf:
            boolean r1 = r7.crossfadeWithThumb     // Catch:{ Exception -> 0x01a3 }
            if (r1 == 0) goto L_0x0156
            float r1 = r7.currentAlpha     // Catch:{ Exception -> 0x01a3 }
            r2 = 1065353216(0x3f800000, float:1.0)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0156
            r1 = 0
            r2 = 0
            android.graphics.drawable.Drawable r3 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r0 == r3) goto L_0x0111
            android.graphics.drawable.Drawable r3 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r0 != r3) goto L_0x00e6
            goto L_0x0111
        L_0x00e6:
            android.graphics.drawable.Drawable r3 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r0 == r3) goto L_0x0102
            android.graphics.drawable.Drawable r3 = r7.crossfadeImage     // Catch:{ Exception -> 0x01a3 }
            if (r0 != r3) goto L_0x00ef
            goto L_0x0102
        L_0x00ef:
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r0 != r3) goto L_0x013e
            android.graphics.drawable.Drawable r3 = r7.crossfadeImage     // Catch:{ Exception -> 0x01a3 }
            if (r3 == 0) goto L_0x013e
            android.graphics.drawable.Drawable r3 = r7.crossfadeImage     // Catch:{ Exception -> 0x01a3 }
            r1 = r3
            android.graphics.BitmapShader r3 = r7.crossfadeShader     // Catch:{ Exception -> 0x01a3 }
            r2 = r3
            r16 = r1
            r17 = r2
            goto L_0x0142
        L_0x0102:
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r3 == 0) goto L_0x013e
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            r1 = r3
            android.graphics.BitmapShader r3 = r7.thumbShader     // Catch:{ Exception -> 0x01a3 }
            r2 = r3
            r16 = r1
            r17 = r2
            goto L_0x0142
        L_0x0111:
            android.graphics.drawable.Drawable r3 = r7.crossfadeImage     // Catch:{ Exception -> 0x01a3 }
            if (r3 == 0) goto L_0x0120
            android.graphics.drawable.Drawable r3 = r7.crossfadeImage     // Catch:{ Exception -> 0x01a3 }
            r1 = r3
            android.graphics.BitmapShader r3 = r7.crossfadeShader     // Catch:{ Exception -> 0x01a3 }
            r2 = r3
            r16 = r1
            r17 = r2
            goto L_0x0142
        L_0x0120:
            android.graphics.drawable.Drawable r3 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r3 == 0) goto L_0x012f
            android.graphics.drawable.Drawable r3 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            r1 = r3
            android.graphics.BitmapShader r3 = r7.thumbShader     // Catch:{ Exception -> 0x01a3 }
            r2 = r3
            r16 = r1
            r17 = r2
            goto L_0x0142
        L_0x012f:
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r3 == 0) goto L_0x013e
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            r1 = r3
            android.graphics.BitmapShader r3 = r7.thumbShader     // Catch:{ Exception -> 0x01a3 }
            r2 = r3
            r16 = r1
            r17 = r2
            goto L_0x0142
        L_0x013e:
            r16 = r1
            r17 = r2
        L_0x0142:
            if (r16 == 0) goto L_0x0156
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01a3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01a3 }
            int r6 = r7.thumbOrientation     // Catch:{ Exception -> 0x01a3 }
            r1 = r18
            r2 = r19
            r3 = r16
            r5 = r17
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01a3 }
        L_0x0156:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01a3 }
            float r2 = r7.currentAlpha     // Catch:{ Exception -> 0x01a3 }
            float r1 = r1 * r2
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01a3 }
            r1 = r18
            r2 = r19
            r3 = r0
            r5 = r14
            r6 = r13
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01a3 }
            goto L_0x0179
        L_0x016a:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01a3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01a3 }
            r1 = r18
            r2 = r19
            r3 = r0
            r5 = r14
            r6 = r13
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01a3 }
        L_0x0179:
            if (r12 == 0) goto L_0x0181
            boolean r1 = r7.crossfadeWithThumb     // Catch:{ Exception -> 0x01a3 }
            if (r1 == 0) goto L_0x0181
            r1 = 1
            goto L_0x0182
        L_0x0181:
            r1 = 0
        L_0x0182:
            r7.checkAlphaAnimation(r1)     // Catch:{ Exception -> 0x01a3 }
            return r11
        L_0x0186:
            android.graphics.drawable.Drawable r1 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            if (r1 == 0) goto L_0x019f
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01a3 }
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01a3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01a3 }
            r5 = 0
            int r6 = r7.thumbOrientation     // Catch:{ Exception -> 0x01a3 }
            r1 = r18
            r2 = r19
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01a3 }
            r7.checkAlphaAnimation(r12)     // Catch:{ Exception -> 0x01a3 }
            return r11
        L_0x019f:
            r7.checkAlphaAnimation(r12)     // Catch:{ Exception -> 0x01a3 }
            goto L_0x01a7
        L_0x01a3:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01a7:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ImageReceiver.draw(android.graphics.Canvas):boolean");
    }

    public void setManualAlphaAnimator(boolean value) {
        this.manualAlphaAnimator = value;
    }

    public float getCurrentAlpha() {
        return this.currentAlpha;
    }

    public void setCurrentAlpha(float value) {
        this.currentAlpha = value;
    }

    public Drawable getDrawable() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 != null) {
            return drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 != null) {
            return drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 != null) {
            return drawable4;
        }
        return null;
    }

    public Bitmap getBitmap() {
        AnimatedFileDrawable animation = getAnimation();
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null && lottieDrawable.hasBitmap()) {
            return lottieDrawable.getAnimatedBitmap();
        }
        if (animation != null && animation.hasBitmap()) {
            return animation.getAnimatedBitmap();
        }
        Drawable drawable = this.currentMediaDrawable;
        if ((drawable instanceof BitmapDrawable) && !(drawable instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Drawable drawable2 = this.currentImageDrawable;
        if ((drawable2 instanceof BitmapDrawable) && !(drawable2 instanceof AnimatedFileDrawable) && !(this.currentMediaDrawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable2).getBitmap();
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if ((drawable3 instanceof BitmapDrawable) && !(drawable3 instanceof AnimatedFileDrawable) && !(this.currentMediaDrawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable3).getBitmap();
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable4).getBitmap();
        }
        return null;
    }

    public BitmapHolder getBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        AnimatedFileDrawable animation = getAnimation();
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null && lottieDrawable.hasBitmap()) {
            bitmap = lottieDrawable.getAnimatedBitmap();
        } else if (animation == null || !animation.hasBitmap()) {
            Drawable drawable = this.currentMediaDrawable;
            if (!(drawable instanceof BitmapDrawable) || (drawable instanceof AnimatedFileDrawable) || (drawable instanceof RLottieDrawable)) {
                Drawable drawable2 = this.currentImageDrawable;
                if (!(drawable2 instanceof BitmapDrawable) || (drawable2 instanceof AnimatedFileDrawable) || (this.currentMediaDrawable instanceof RLottieDrawable)) {
                    Drawable drawable3 = this.currentThumbDrawable;
                    if (!(drawable3 instanceof BitmapDrawable) || (drawable3 instanceof AnimatedFileDrawable) || (this.currentMediaDrawable instanceof RLottieDrawable)) {
                        Drawable drawable4 = this.staticThumbDrawable;
                        if (drawable4 instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) drawable4).getBitmap();
                        }
                    } else {
                        bitmap = ((BitmapDrawable) drawable3).getBitmap();
                        key = this.currentThumbKey;
                    }
                } else {
                    bitmap = ((BitmapDrawable) drawable2).getBitmap();
                    key = this.currentImageKey;
                }
            } else {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                key = this.currentMediaKey;
            }
        } else {
            bitmap = animation.getAnimatedBitmap();
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key);
        }
        return null;
    }

    public Bitmap getThumbBitmap() {
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Drawable drawable2 = this.staticThumbDrawable;
        if (drawable2 instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable2).getBitmap();
        }
        return null;
    }

    public BitmapHolder getThumbBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            key = this.currentThumbKey;
        } else {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable2).getBitmap();
            }
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key);
        }
        return null;
    }

    public int getBitmapWidth() {
        Drawable drawable = getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicWidth() : animation.getIntrinsicHeight();
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            return lottieDrawable.getIntrinsicWidth();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 != null) {
                return drawable2.getIntrinsicWidth();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getWidth() : bitmap.getHeight();
    }

    public int getBitmapHeight() {
        Drawable drawable = getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicHeight() : animation.getIntrinsicWidth();
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            return lottieDrawable.getIntrinsicHeight();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 != null) {
                return drawable2.getIntrinsicHeight();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getHeight() : bitmap.getWidth();
    }

    public void setVisible(boolean value, boolean invalidate) {
        View view;
        if (this.isVisible != value) {
            this.isVisible = value;
            if (invalidate && (view = this.parentView) != null) {
                if (this.invalidateAll) {
                    view.invalidate();
                    return;
                }
                int i = this.imageX;
                int i2 = this.imageY;
                view.invalidate(i, i2, this.imageW + i, this.imageH + i2);
            }
        }
    }

    public boolean getVisible() {
        return this.isVisible;
    }

    public void setAlpha(float value) {
        this.overrideAlpha = value;
    }

    public void setCrossfadeAlpha(byte value) {
        this.crossfadeAlpha = value;
    }

    public boolean hasImageSet() {
        return (this.currentImageDrawable == null && this.currentMediaDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentImageKey == null && this.currentMediaKey == null) ? false : true;
    }

    public boolean hasBitmapImage() {
        return (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
    }

    public boolean hasNotThumb() {
        return (this.currentImageDrawable == null && this.currentMediaDrawable == null) ? false : true;
    }

    public boolean hasStaticThumb() {
        return this.staticThumbDrawable != null;
    }

    public void setAspectFit(boolean value) {
        this.isAspectFit = value;
    }

    public boolean isAspectFit() {
        return this.isAspectFit;
    }

    public void setParentView(View view) {
        this.parentView = view;
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.setParentView(this.parentView);
        }
    }

    public void setImageX(int x) {
        this.imageX = x;
    }

    public void setImageY(int y) {
        this.imageY = y;
    }

    public void setImageWidth(int width) {
        this.imageW = width;
    }

    public void setImageCoords(int x, int y, int width, int height) {
        this.imageX = x;
        this.imageY = y;
        this.imageW = width;
        this.imageH = height;
    }

    public void setSideClip(float value) {
        this.sideClip = value;
    }

    public float getCenterX() {
        return ((float) this.imageX) + (((float) this.imageW) / 2.0f);
    }

    public float getCenterY() {
        return ((float) this.imageY) + (((float) this.imageH) / 2.0f);
    }

    public int getImageX() {
        return this.imageX;
    }

    public int getImageX2() {
        return this.imageX + this.imageW;
    }

    public int getImageY() {
        return this.imageY;
    }

    public int getImageY2() {
        return this.imageY + this.imageH;
    }

    public int getImageWidth() {
        return this.imageW;
    }

    public int getImageHeight() {
        return this.imageH;
    }

    public float getImageAspectRatio() {
        float f;
        float f2;
        if (this.imageOrientation % 180 != 0) {
            f2 = this.drawRegion.height();
            f = this.drawRegion.width();
        } else {
            f2 = this.drawRegion.width();
            f = this.drawRegion.height();
        }
        return f2 / f;
    }

    public String getExt() {
        return this.currentExt;
    }

    public boolean isInsideImage(float x, float y) {
        int i = this.imageX;
        if (x >= ((float) i) && x <= ((float) (i + this.imageW))) {
            int i2 = this.imageY;
            return y >= ((float) i2) && y <= ((float) (i2 + this.imageH));
        }
    }

    public RectF getDrawRegion() {
        return this.drawRegion;
    }

    public int getNewGuid() {
        int i = this.currentGuid + 1;
        this.currentGuid = i;
        return i;
    }

    public String getImageKey() {
        return this.currentImageKey;
    }

    public String getMediaKey() {
        return this.currentMediaKey;
    }

    public String getThumbKey() {
        return this.currentThumbKey;
    }

    public int getSize() {
        return this.currentSize;
    }

    public ImageLocation getMediaLocation() {
        return this.currentMediaLocation;
    }

    public ImageLocation getImageLocation() {
        return this.currentImageLocation;
    }

    public ImageLocation getThumbLocation() {
        return this.currentThumbLocation;
    }

    public String getMediaFilter() {
        return this.currentMediaFilter;
    }

    public String getImageFilter() {
        return this.currentImageFilter;
    }

    public String getThumbFilter() {
        return this.currentThumbFilter;
    }

    public int getCacheType() {
        return this.currentCacheType;
    }

    public void setForcePreview(boolean value) {
        this.forcePreview = value;
    }

    public void setForceCrossfade(boolean value) {
        this.forceCrossfade = value;
    }

    public boolean isForcePreview() {
        return this.forcePreview;
    }

    public void setRoundRadius(int value) {
        this.roundRadius = value;
    }

    public void setCurrentAccount(int value) {
        this.currentAccount = value;
    }

    public int getRoundRadius() {
        return this.roundRadius;
    }

    public Object getParentObject() {
        return this.currentParentObject;
    }

    public void setNeedsQualityThumb(boolean value) {
        this.needsQualityThumb = value;
    }

    public void setQualityThumbDocument(TLRPC.Document document) {
        this.qulityThumbDocument = document;
    }

    public TLRPC.Document getQulityThumbDocument() {
        return this.qulityThumbDocument;
    }

    public void setCrossfadeWithOldImage(boolean value) {
        this.crossfadeWithOldImage = value;
    }

    public boolean isNeedsQualityThumb() {
        return this.needsQualityThumb;
    }

    public boolean isCurrentKeyQuality() {
        return this.currentKeyQuality;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public void setShouldGenerateQualityThumb(boolean value) {
        this.shouldGenerateQualityThumb = value;
    }

    public boolean isShouldGenerateQualityThumb() {
        return this.shouldGenerateQualityThumb;
    }

    public void setAllowStartAnimation(boolean value) {
        this.allowStartAnimation = value;
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.allowDecodeSingleFrame = value;
    }

    public void setAutoRepeat(int value) {
        this.autoRepeat = value;
        RLottieDrawable drawable = getLottieAnimation();
        if (drawable != null) {
            drawable.setAutoRepeat(value);
        }
    }

    public void setUseSharedAnimationQueue(boolean value) {
        this.useSharedAnimationQueue = value;
    }

    public boolean isAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public void startAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.setUseSharedQueue(this.useSharedAnimationQueue);
            animation.start();
        }
    }

    public void stopAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.stop();
        }
    }

    public boolean isAnimationRunning() {
        AnimatedFileDrawable animation = getAnimation();
        return animation != null && animation.isRunning();
    }

    public AnimatedFileDrawable getAnimation() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable4;
        }
        return null;
    }

    public RLottieDrawable getLottieAnimation() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable4;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public int getTag(int type) {
        if (type == 1) {
            return this.thumbTag;
        }
        if (type == 3) {
            return this.mediaTag;
        }
        return this.imageTag;
    }

    /* access modifiers changed from: protected */
    public void setTag(int value, int type) {
        if (type == 1) {
            this.thumbTag = value;
        } else if (type == 3) {
            this.mediaTag = value;
        } else {
            this.imageTag = value;
        }
    }

    public void setParam(int value) {
        this.param = value;
    }

    public int getParam() {
        return this.param;
    }

    /* access modifiers changed from: protected */
    public boolean setImageBitmapByKey(Drawable drawable, String key, int type, boolean memCache, int guid) {
        Drawable drawable2;
        boolean z = false;
        if (drawable == null || key == null || this.currentGuid != guid) {
            return false;
        }
        if (type == 0) {
            if (!key.equals(this.currentImageKey)) {
                return false;
            }
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentImageKey);
            }
            this.currentImageDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.imageOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            int i = this.roundRadius;
            if (i == 0 || !(drawable instanceof BitmapDrawable)) {
                this.imageShader = null;
            } else if (!(drawable instanceof RLottieDrawable)) {
                if (drawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable).setRoundRadius(i);
                } else {
                    this.imageShader = new BitmapShader(((BitmapDrawable) drawable).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
            }
            if ((memCache || this.forcePreview) && !this.forceCrossfade) {
                this.currentAlpha = 1.0f;
            } else {
                boolean allowCorssfade = true;
                Drawable drawable3 = this.currentMediaDrawable;
                if ((drawable3 instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) drawable3).hasBitmap()) {
                    allowCorssfade = false;
                } else if (this.currentImageDrawable instanceof RLottieDrawable) {
                    allowCorssfade = false;
                }
                if (allowCorssfade && ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade)) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
                }
            }
        } else if (type == 3) {
            if (!key.equals(this.currentMediaKey)) {
                return false;
            }
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentMediaKey);
            }
            this.currentMediaDrawable = drawable;
            int i2 = this.roundRadius;
            if (i2 == 0 || !(drawable instanceof BitmapDrawable)) {
                this.mediaShader = null;
            } else if (!(drawable instanceof RLottieDrawable)) {
                if (drawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable).setRoundRadius(i2);
                } else {
                    this.mediaShader = new BitmapShader(((BitmapDrawable) drawable).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
            }
            if (this.currentImageDrawable == null) {
                if ((memCache || this.forcePreview) && !this.forceCrossfade) {
                    this.currentAlpha = 1.0f;
                } else if ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
                }
            }
        } else if (type == 1) {
            if (this.currentThumbDrawable != null) {
                return false;
            }
            if (!this.forcePreview) {
                AnimatedFileDrawable animation = getAnimation();
                if (animation != null && animation.hasBitmap()) {
                    return false;
                }
                Drawable drawable4 = this.currentImageDrawable;
                if ((drawable4 != null && !(drawable4 instanceof AnimatedFileDrawable)) || ((drawable2 = this.currentMediaDrawable) != null && !(drawable2 instanceof AnimatedFileDrawable))) {
                    return false;
                }
            }
            if (!key.equals(this.currentThumbKey)) {
                return false;
            }
            ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
            this.currentThumbDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.thumbOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            int i3 = this.roundRadius;
            if (i3 == 0 || !(drawable instanceof BitmapDrawable)) {
                this.thumbShader = null;
            } else if (!(drawable instanceof RLottieDrawable)) {
                if (drawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable).setRoundRadius(i3);
                } else {
                    this.thumbShader = new BitmapShader(((BitmapDrawable) drawable).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
            }
            if (memCache || this.crossfadeAlpha == 2) {
                this.currentAlpha = 1.0f;
            } else {
                Object obj = this.currentParentObject;
                if (!(obj instanceof MessageObject) || !((MessageObject) obj).isRoundVideo() || !((MessageObject) this.currentParentObject).isSending()) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = this.staticThumbDrawable != null && this.currentImageKey == null && this.currentMediaKey == null;
                } else {
                    this.currentAlpha = 1.0f;
                }
            }
        }
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) drawable;
            fileDrawable.setParentView(this.parentView);
            fileDrawable.setUseSharedQueue(this.useSharedAnimationQueue);
            if (this.allowStartAnimation) {
                fileDrawable.start();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
            this.animationReadySent = false;
        } else if (drawable instanceof RLottieDrawable) {
            RLottieDrawable fileDrawable2 = (RLottieDrawable) drawable;
            fileDrawable2.addParentView(this.parentView);
            if (this.currentOpenedLayerFlags == 0) {
                fileDrawable2.start();
            }
            fileDrawable2.setAllowDecodeSingleFrame(true);
            fileDrawable2.setAutoRepeat(this.autoRepeat);
            this.animationReadySent = false;
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                int i4 = this.imageX;
                int i5 = this.imageY;
                view.invalidate(i4, i5, this.imageW + i4, this.imageH + i5);
            }
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            boolean z2 = (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
            if (this.currentImageDrawable == null && this.currentMediaDrawable == null) {
                z = true;
            }
            imageReceiverDelegate.didSetImage(this, z2, z);
        }
        return true;
    }

    private void recycleBitmap(String newKey, int type) {
        Drawable image;
        String key;
        String replacedKey;
        if (type == 3) {
            key = this.currentMediaKey;
            image = this.currentMediaDrawable;
        } else if (type == 2) {
            key = this.crossfadeKey;
            image = this.crossfadeImage;
        } else if (type == 1) {
            key = this.currentThumbKey;
            image = this.currentThumbDrawable;
        } else {
            key = this.currentImageKey;
            image = this.currentImageDrawable;
        }
        if (!(key == null || !key.startsWith("-") || (replacedKey = ImageLoader.getInstance().getReplacedKey(key)) == null)) {
            key = replacedKey;
        }
        if (image instanceof RLottieDrawable) {
            ((RLottieDrawable) image).removeParentView(this.parentView);
        }
        String replacedKey2 = ImageLoader.getInstance().getReplacedKey(key);
        if (key != null && ((newKey == null || !newKey.equals(key)) && image != null)) {
            if (image instanceof RLottieDrawable) {
                RLottieDrawable fileDrawable = (RLottieDrawable) image;
                boolean canDelete = ImageLoader.getInstance().decrementUseCount(key);
                if (!ImageLoader.getInstance().isInMemCache(key, true) && canDelete) {
                    fileDrawable.recycle();
                }
            } else if (image instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) image).recycle();
            } else if (image instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                boolean canDelete2 = ImageLoader.getInstance().decrementUseCount(key);
                if (!ImageLoader.getInstance().isInMemCache(key, false) && canDelete2) {
                    bitmap.recycle();
                }
            }
        }
        if (type == 3) {
            this.currentMediaKey = null;
            this.currentMediaDrawable = null;
        } else if (type == 2) {
            this.crossfadeKey = null;
            this.crossfadeImage = null;
        } else if (type == 1) {
            this.currentThumbDrawable = null;
            this.currentThumbKey = null;
        } else {
            this.currentImageDrawable = null;
            this.currentImageKey = null;
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i;
        RLottieDrawable lottieDrawable;
        RLottieDrawable lottieDrawable2;
        if (id == NotificationCenter.didReplacedPhotoInMemCache) {
            String oldKey = args[0];
            String str = this.currentMediaKey;
            if (str != null && str.equals(oldKey)) {
                this.currentMediaKey = args[1];
                this.currentMediaLocation = args[2];
                SetImageBackup setImageBackup2 = this.setImageBackup;
                if (setImageBackup2 != null) {
                    setImageBackup2.mediaLocation = args[2];
                }
            }
            String str2 = this.currentImageKey;
            if (str2 != null && str2.equals(oldKey)) {
                this.currentImageKey = args[1];
                this.currentImageLocation = args[2];
                SetImageBackup setImageBackup3 = this.setImageBackup;
                if (setImageBackup3 != null) {
                    setImageBackup3.imageLocation = args[2];
                }
            }
            String str3 = this.currentThumbKey;
            if (str3 != null && str3.equals(oldKey)) {
                this.currentThumbKey = args[1];
                this.currentThumbLocation = args[2];
                SetImageBackup setImageBackup4 = this.setImageBackup;
                if (setImageBackup4 != null) {
                    setImageBackup4.thumbLocation = args[2];
                }
            }
        } else if (id == NotificationCenter.stopAllHeavyOperations) {
            Integer layer = args[0];
            if (this.currentLayerNum < layer.intValue()) {
                int intValue = this.currentOpenedLayerFlags | layer.intValue();
                this.currentOpenedLayerFlags = intValue;
                if (intValue != 0 && (lottieDrawable2 = getLottieAnimation()) != null) {
                    lottieDrawable2.stop();
                }
            }
        } else if (id == NotificationCenter.startAllHeavyOperations) {
            Integer layer2 = args[0];
            if (this.currentLayerNum < layer2.intValue() && (i = this.currentOpenedLayerFlags) != 0) {
                int i2 = i & (~layer2.intValue());
                this.currentOpenedLayerFlags = i2;
                if (i2 == 0 && (lottieDrawable = getLottieAnimation()) != null) {
                    lottieDrawable.start();
                }
            }
        }
    }
}
