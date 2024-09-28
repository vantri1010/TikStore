package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.ItemTouchHelper;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Bitmaps;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.File;

public class VideoSeekPreviewImage extends View {
    private Paint bitmapPaint = new Paint(2);
    private RectF bitmapRect = new RectF();
    private BitmapShader bitmapShader;
    private Bitmap bitmapToDraw;
    private Bitmap bitmapToRecycle;
    private int currentPixel = -1;
    private VideoSeekPreviewImageDelegate delegate;
    private RectF dstR = new RectF();
    private long duration;
    private AnimatedFileDrawable fileDrawable;
    private Drawable frameDrawable;
    private String frameTime;
    private Runnable loadRunnable;
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint(2);
    private float pendingProgress;
    private int pixelWidth;
    private Runnable progressRunnable;
    private boolean ready;
    private TextPaint textPaint = new TextPaint(1);
    private int timeWidth;
    private Uri videoUri;

    public interface VideoSeekPreviewImageDelegate {
        void onReady();
    }

    public VideoSeekPreviewImage(Context context, VideoSeekPreviewImageDelegate videoSeekPreviewImageDelegate) {
        super(context);
        setVisibility(4);
        this.frameDrawable = context.getResources().getDrawable(R.drawable.videopreview);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.delegate = videoSeekPreviewImageDelegate;
    }

    public void setProgress(float progress, int w) {
        if (w != 0) {
            this.pixelWidth = w;
            int pixel = ((int) (((float) w) * progress)) / 5;
            if (this.currentPixel != pixel) {
                this.currentPixel = pixel;
            } else {
                return;
            }
        }
        long time = (long) (((float) this.duration) * progress);
        int minutes = (int) ((time / 60) / 1000);
        String format = String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(((int) (time - ((long) ((minutes * 60) * 1000)))) / 1000)});
        this.frameTime = format;
        this.timeWidth = (int) Math.ceil((double) this.textPaint.measureText(format));
        invalidate();
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
        }
        AnimatedFileDrawable file = this.fileDrawable;
        if (file != null) {
            file.resetStream(false);
        }
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        $$Lambda$VideoSeekPreviewImage$A_s60_U59osNchLfKHqIzA0dnUI r6 = new Runnable(progress, time) {
            private final /* synthetic */ float f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                VideoSeekPreviewImage.this.lambda$setProgress$1$VideoSeekPreviewImage(this.f$1, this.f$2);
            }
        };
        this.progressRunnable = r6;
        dispatchQueue.postRunnable(r6);
    }

    public /* synthetic */ void lambda$setProgress$1$VideoSeekPreviewImage(float progress, long time) {
        int height;
        int width;
        if (this.fileDrawable == null) {
            this.pendingProgress = progress;
            return;
        }
        int bitmapSize = Math.max(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, AndroidUtilities.dp(100.0f));
        Bitmap bitmap = this.fileDrawable.getFrameAtTime(time);
        if (bitmap != null) {
            int width2 = bitmap.getWidth();
            int height2 = bitmap.getHeight();
            if (width2 > height2) {
                float scale = ((float) width2) / ((float) bitmapSize);
                width = bitmapSize;
                height = (int) (((float) height2) / scale);
            } else {
                float scale2 = ((float) height2) / ((float) bitmapSize);
                height = bitmapSize;
                width = (int) (((float) width2) / scale2);
            }
            try {
                Bitmap backgroundBitmap = Bitmaps.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                this.dstR.set(0.0f, 0.0f, (float) width, (float) height);
                Canvas canvas = new Canvas(backgroundBitmap);
                canvas.drawBitmap(bitmap, (Rect) null, this.dstR, this.paint);
                canvas.setBitmap((Bitmap) null);
                bitmap = backgroundBitmap;
            } catch (Throwable th) {
                bitmap = null;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(bitmap) {
            private final /* synthetic */ Bitmap f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VideoSeekPreviewImage.this.lambda$null$0$VideoSeekPreviewImage(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$VideoSeekPreviewImage(Bitmap bitmapFinal) {
        int viewHeight;
        int viewWidth;
        if (bitmapFinal != null) {
            if (this.bitmapToDraw != null) {
                Bitmap bitmap = this.bitmapToRecycle;
                if (bitmap != null) {
                    bitmap.recycle();
                }
                this.bitmapToRecycle = this.bitmapToDraw;
            }
            this.bitmapToDraw = bitmapFinal;
            BitmapShader bitmapShader2 = new BitmapShader(this.bitmapToDraw, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.bitmapShader = bitmapShader2;
            bitmapShader2.setLocalMatrix(this.matrix);
            this.bitmapPaint.setShader(this.bitmapShader);
            invalidate();
            int viewSize = AndroidUtilities.dp(150.0f);
            float aspect = ((float) bitmapFinal.getWidth()) / ((float) bitmapFinal.getHeight());
            if (aspect > 1.0f) {
                viewWidth = viewSize;
                viewHeight = (int) (((float) viewSize) / aspect);
            } else {
                viewHeight = viewSize;
                viewWidth = (int) (((float) viewSize) * aspect);
            }
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (!(getVisibility() == 0 && layoutParams.width == viewWidth && layoutParams.height == viewHeight)) {
                layoutParams.width = viewWidth;
                layoutParams.height = viewHeight;
                setVisibility(0);
                requestLayout();
            }
        }
        this.progressRunnable = null;
    }

    public void open(Uri uri) {
        if (uri != null && !uri.equals(this.videoUri)) {
            this.videoUri = uri;
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            $$Lambda$VideoSeekPreviewImage$Vk5WqPjKWvzS4Y_lxaoP2Q3ecc r1 = new Runnable(uri) {
                private final /* synthetic */ Uri f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VideoSeekPreviewImage.this.lambda$open$3$VideoSeekPreviewImage(this.f$1);
                }
            };
            this.loadRunnable = r1;
            dispatchQueue.postRunnable(r1);
        }
    }

    public /* synthetic */ void lambda$open$3$VideoSeekPreviewImage(Uri uri) {
        String path;
        Uri uri2 = uri;
        if ("hchat".equals(uri.getScheme())) {
            int currentAccount = Utilities.parseInt(uri2.getQueryParameter("account")).intValue();
            Object parentObject = FileLoader.getInstance(currentAccount).getParentObject(Utilities.parseInt(uri2.getQueryParameter("rid")).intValue());
            TLRPC.TL_document document = new TLRPC.TL_document();
            document.access_hash = Utilities.parseLong(uri2.getQueryParameter("hash")).longValue();
            document.id = Utilities.parseLong(uri2.getQueryParameter(TtmlNode.ATTR_ID)).longValue();
            document.size = Utilities.parseInt(uri2.getQueryParameter("size")).intValue();
            document.dc_id = Utilities.parseInt(uri2.getQueryParameter("dc")).intValue();
            document.mime_type = uri2.getQueryParameter("mime");
            document.file_reference = Utilities.hexToBytes(uri2.getQueryParameter("reference"));
            TLRPC.TL_documentAttributeFilename filename = new TLRPC.TL_documentAttributeFilename();
            filename.file_name = uri2.getQueryParameter("name");
            document.attributes.add(filename);
            document.attributes.add(new TLRPC.TL_documentAttributeVideo());
            String name = FileLoader.getAttachFileName(document);
            if (FileLoader.getInstance(currentAccount).isLoadingFile(name)) {
                File directory = FileLoader.getDirectory(4);
                path = new File(directory, document.dc_id + "_" + document.id + ".temp").getAbsolutePath();
            } else {
                path = FileLoader.getPathToAttach(document, false).getAbsolutePath();
            }
            AnimatedFileDrawable animatedFileDrawable = r4;
            String str = name;
            AnimatedFileDrawable animatedFileDrawable2 = new AnimatedFileDrawable(new File(path), true, (long) document.size, document, parentObject, currentAccount, true);
            this.fileDrawable = animatedFileDrawable;
            String str2 = path;
        } else {
            this.fileDrawable = new AnimatedFileDrawable(new File(uri.getPath()), true, 0, (TLRPC.Document) null, (Object) null, 0, true);
        }
        this.duration = (long) this.fileDrawable.getDurationMs();
        float f = this.pendingProgress;
        if (f != 0.0f) {
            setProgress(f, this.pixelWidth);
            this.pendingProgress = 0.0f;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                VideoSeekPreviewImage.this.lambda$null$2$VideoSeekPreviewImage();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$VideoSeekPreviewImage() {
        this.loadRunnable = null;
        if (this.fileDrawable != null) {
            this.ready = true;
            this.delegate.onReady();
        }
    }

    public boolean isReady() {
        return this.ready;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = this.bitmapToRecycle;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmapToRecycle = null;
        }
        if (this.bitmapToDraw != null && this.bitmapShader != null) {
            this.matrix.reset();
            float scale = ((float) getMeasuredWidth()) / ((float) this.bitmapToDraw.getWidth());
            this.matrix.preScale(scale, scale);
            this.bitmapRect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.bitmapRect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.bitmapPaint);
            this.frameDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.frameDrawable.draw(canvas);
            canvas.drawText(this.frameTime, (float) ((getMeasuredWidth() - this.timeWidth) / 2), (float) (getMeasuredHeight() - AndroidUtilities.dp(9.0f)), this.textPaint);
        }
    }

    public void close() {
        if (this.loadRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.loadRunnable);
            this.loadRunnable = null;
        }
        if (this.progressRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.progressRunnable);
            this.progressRunnable = null;
        }
        AnimatedFileDrawable drawable = this.fileDrawable;
        if (drawable != null) {
            drawable.resetStream(true);
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                VideoSeekPreviewImage.this.lambda$close$4$VideoSeekPreviewImage();
            }
        });
        setVisibility(4);
        this.bitmapToDraw = null;
        this.bitmapShader = null;
        invalidate();
        this.currentPixel = -1;
        this.videoUri = null;
        this.ready = false;
    }

    public /* synthetic */ void lambda$close$4$VideoSeekPreviewImage() {
        this.pendingProgress = 0.0f;
        AnimatedFileDrawable animatedFileDrawable = this.fileDrawable;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.recycle();
            this.fileDrawable = null;
        }
    }
}
