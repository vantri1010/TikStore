package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Bitmaps;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.io.File;

public class PhotoCropActivity extends BaseFragment {
    private static final int done_button = 1;
    private String bitmapKey;
    /* access modifiers changed from: private */
    public PhotoEditActivityDelegate delegate = null;
    /* access modifiers changed from: private */
    public boolean doneButtonPressed = false;
    /* access modifiers changed from: private */
    public BitmapDrawable drawable;
    /* access modifiers changed from: private */
    public Bitmap imageToCrop;
    /* access modifiers changed from: private */
    public boolean sameBitmap = false;
    /* access modifiers changed from: private */
    public PhotoCropView view;

    public interface PhotoEditActivityDelegate {
        void didFinishEdit(Bitmap bitmap);
    }

    private class PhotoCropView extends FrameLayout {
        int bitmapHeight;
        int bitmapWidth;
        int bitmapX;
        int bitmapY;
        Paint circlePaint = null;
        int draggingState = 0;
        boolean freeform;
        Paint halfPaint = null;
        float oldX = 0.0f;
        float oldY = 0.0f;
        Paint rectPaint = null;
        float rectSizeX = 600.0f;
        float rectSizeY = 600.0f;
        float rectX = -1.0f;
        float rectY = -1.0f;
        int viewHeight;
        int viewWidth;

        public PhotoCropView(Context context) {
            super(context);
            init();
        }

        private void init() {
            Paint paint = new Paint();
            this.rectPaint = paint;
            paint.setColor(1073412858);
            this.rectPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.rectPaint.setStyle(Paint.Style.STROKE);
            Paint paint2 = new Paint();
            this.circlePaint = paint2;
            paint2.setColor(-1);
            Paint paint3 = new Paint();
            this.halfPaint = paint3;
            paint3.setColor(-939524096);
            setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
            setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    int cornerSide = AndroidUtilities.dp(14.0f);
                    if (motionEvent.getAction() == 0) {
                        if (PhotoCropView.this.rectX - ((float) cornerSide) < x && PhotoCropView.this.rectX + ((float) cornerSide) > x && PhotoCropView.this.rectY - ((float) cornerSide) < y && PhotoCropView.this.rectY + ((float) cornerSide) > y) {
                            PhotoCropView.this.draggingState = 1;
                        } else if ((PhotoCropView.this.rectX - ((float) cornerSide)) + PhotoCropView.this.rectSizeX < x && PhotoCropView.this.rectX + ((float) cornerSide) + PhotoCropView.this.rectSizeX > x && PhotoCropView.this.rectY - ((float) cornerSide) < y && PhotoCropView.this.rectY + ((float) cornerSide) > y) {
                            PhotoCropView.this.draggingState = 2;
                        } else if (PhotoCropView.this.rectX - ((float) cornerSide) < x && PhotoCropView.this.rectX + ((float) cornerSide) > x && (PhotoCropView.this.rectY - ((float) cornerSide)) + PhotoCropView.this.rectSizeY < y && PhotoCropView.this.rectY + ((float) cornerSide) + PhotoCropView.this.rectSizeY > y) {
                            PhotoCropView.this.draggingState = 3;
                        } else if ((PhotoCropView.this.rectX - ((float) cornerSide)) + PhotoCropView.this.rectSizeX < x && PhotoCropView.this.rectX + ((float) cornerSide) + PhotoCropView.this.rectSizeX > x && (PhotoCropView.this.rectY - ((float) cornerSide)) + PhotoCropView.this.rectSizeY < y && PhotoCropView.this.rectY + ((float) cornerSide) + PhotoCropView.this.rectSizeY > y) {
                            PhotoCropView.this.draggingState = 4;
                        } else if (PhotoCropView.this.rectX >= x || PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX <= x || PhotoCropView.this.rectY >= y || PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY <= y) {
                            PhotoCropView.this.draggingState = 0;
                        } else {
                            PhotoCropView.this.draggingState = 5;
                        }
                        if (PhotoCropView.this.draggingState != 0) {
                            PhotoCropView.this.requestDisallowInterceptTouchEvent(true);
                        }
                        PhotoCropView.this.oldX = x;
                        PhotoCropView.this.oldY = y;
                    } else if (motionEvent.getAction() == 1) {
                        PhotoCropView.this.draggingState = 0;
                    } else if (motionEvent.getAction() == 2 && PhotoCropView.this.draggingState != 0) {
                        float diffX = x - PhotoCropView.this.oldX;
                        float diffY = y - PhotoCropView.this.oldY;
                        if (PhotoCropView.this.draggingState == 5) {
                            PhotoCropView.this.rectX += diffX;
                            PhotoCropView.this.rectY += diffY;
                            if (PhotoCropView.this.rectX < ((float) PhotoCropView.this.bitmapX)) {
                                PhotoCropView photoCropView = PhotoCropView.this;
                                photoCropView.rectX = (float) photoCropView.bitmapX;
                            } else if (PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX > ((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth))) {
                                PhotoCropView photoCropView2 = PhotoCropView.this;
                                photoCropView2.rectX = ((float) (photoCropView2.bitmapX + PhotoCropView.this.bitmapWidth)) - PhotoCropView.this.rectSizeX;
                            }
                            if (PhotoCropView.this.rectY < ((float) PhotoCropView.this.bitmapY)) {
                                PhotoCropView photoCropView3 = PhotoCropView.this;
                                photoCropView3.rectY = (float) photoCropView3.bitmapY;
                            } else if (PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                PhotoCropView photoCropView4 = PhotoCropView.this;
                                photoCropView4.rectY = ((float) (photoCropView4.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectSizeY;
                            }
                        } else if (PhotoCropView.this.draggingState == 1) {
                            if (PhotoCropView.this.rectSizeX - diffX < 160.0f) {
                                diffX = PhotoCropView.this.rectSizeX - 160.0f;
                            }
                            if (PhotoCropView.this.rectX + diffX < ((float) PhotoCropView.this.bitmapX)) {
                                diffX = ((float) PhotoCropView.this.bitmapX) - PhotoCropView.this.rectX;
                            }
                            if (!PhotoCropView.this.freeform) {
                                if (PhotoCropView.this.rectY + diffX < ((float) PhotoCropView.this.bitmapY)) {
                                    diffX = ((float) PhotoCropView.this.bitmapY) - PhotoCropView.this.rectY;
                                }
                                PhotoCropView.this.rectX += diffX;
                                PhotoCropView.this.rectY += diffX;
                                PhotoCropView.this.rectSizeX -= diffX;
                                PhotoCropView.this.rectSizeY -= diffX;
                            } else {
                                if (PhotoCropView.this.rectSizeY - diffY < 160.0f) {
                                    diffY = PhotoCropView.this.rectSizeY - 160.0f;
                                }
                                if (PhotoCropView.this.rectY + diffY < ((float) PhotoCropView.this.bitmapY)) {
                                    diffY = ((float) PhotoCropView.this.bitmapY) - PhotoCropView.this.rectY;
                                }
                                PhotoCropView.this.rectX += diffX;
                                PhotoCropView.this.rectY += diffY;
                                PhotoCropView.this.rectSizeX -= diffX;
                                PhotoCropView.this.rectSizeY -= diffY;
                            }
                        } else if (PhotoCropView.this.draggingState == 2) {
                            if (PhotoCropView.this.rectSizeX + diffX < 160.0f) {
                                diffX = -(PhotoCropView.this.rectSizeX - 160.0f);
                            }
                            if (PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX + diffX > ((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth))) {
                                diffX = (((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth)) - PhotoCropView.this.rectX) - PhotoCropView.this.rectSizeX;
                            }
                            if (!PhotoCropView.this.freeform) {
                                if (PhotoCropView.this.rectY - diffX < ((float) PhotoCropView.this.bitmapY)) {
                                    diffX = PhotoCropView.this.rectY - ((float) PhotoCropView.this.bitmapY);
                                }
                                PhotoCropView.this.rectY -= diffX;
                                PhotoCropView.this.rectSizeX += diffX;
                                PhotoCropView.this.rectSizeY += diffX;
                            } else {
                                if (PhotoCropView.this.rectSizeY - diffY < 160.0f) {
                                    diffY = PhotoCropView.this.rectSizeY - 160.0f;
                                }
                                if (PhotoCropView.this.rectY + diffY < ((float) PhotoCropView.this.bitmapY)) {
                                    diffY = ((float) PhotoCropView.this.bitmapY) - PhotoCropView.this.rectY;
                                }
                                PhotoCropView.this.rectY += diffY;
                                PhotoCropView.this.rectSizeX += diffX;
                                PhotoCropView.this.rectSizeY -= diffY;
                            }
                        } else if (PhotoCropView.this.draggingState == 3) {
                            if (PhotoCropView.this.rectSizeX - diffX < 160.0f) {
                                diffX = PhotoCropView.this.rectSizeX - 160.0f;
                            }
                            if (PhotoCropView.this.rectX + diffX < ((float) PhotoCropView.this.bitmapX)) {
                                diffX = ((float) PhotoCropView.this.bitmapX) - PhotoCropView.this.rectX;
                            }
                            if (!PhotoCropView.this.freeform) {
                                if ((PhotoCropView.this.rectY + PhotoCropView.this.rectSizeX) - diffX > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                    diffX = ((PhotoCropView.this.rectY + PhotoCropView.this.rectSizeX) - ((float) PhotoCropView.this.bitmapY)) - ((float) PhotoCropView.this.bitmapHeight);
                                }
                                PhotoCropView.this.rectX += diffX;
                                PhotoCropView.this.rectSizeX -= diffX;
                                PhotoCropView.this.rectSizeY -= diffX;
                            } else {
                                if (PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY + diffY > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                    diffY = (((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectY) - PhotoCropView.this.rectSizeY;
                                }
                                PhotoCropView.this.rectX += diffX;
                                PhotoCropView.this.rectSizeX -= diffX;
                                PhotoCropView.this.rectSizeY += diffY;
                                if (PhotoCropView.this.rectSizeY < 160.0f) {
                                    PhotoCropView.this.rectSizeY = 160.0f;
                                }
                            }
                        } else if (PhotoCropView.this.draggingState == 4) {
                            if (PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX + diffX > ((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth))) {
                                diffX = (((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth)) - PhotoCropView.this.rectX) - PhotoCropView.this.rectSizeX;
                            }
                            if (!PhotoCropView.this.freeform) {
                                if (PhotoCropView.this.rectY + PhotoCropView.this.rectSizeX + diffX > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                    diffX = (((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectY) - PhotoCropView.this.rectSizeX;
                                }
                                PhotoCropView.this.rectSizeX += diffX;
                                PhotoCropView.this.rectSizeY += diffX;
                            } else {
                                if (PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY + diffY > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                    diffY = (((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectY) - PhotoCropView.this.rectSizeY;
                                }
                                PhotoCropView.this.rectSizeX += diffX;
                                PhotoCropView.this.rectSizeY += diffY;
                            }
                            if (PhotoCropView.this.rectSizeX < 160.0f) {
                                PhotoCropView.this.rectSizeX = 160.0f;
                            }
                            if (PhotoCropView.this.rectSizeY < 160.0f) {
                                PhotoCropView.this.rectSizeY = 160.0f;
                            }
                        }
                        PhotoCropView.this.oldX = x;
                        PhotoCropView.this.oldY = y;
                        PhotoCropView.this.invalidate();
                    }
                    return true;
                }
            });
        }

        private void updateBitmapSize() {
            if (this.viewWidth != 0 && this.viewHeight != 0 && PhotoCropActivity.this.imageToCrop != null) {
                float f = this.rectX - ((float) this.bitmapX);
                int i = this.bitmapWidth;
                float percX = f / ((float) i);
                float f2 = this.rectY - ((float) this.bitmapY);
                int i2 = this.bitmapHeight;
                float percY = f2 / ((float) i2);
                float percSizeX = this.rectSizeX / ((float) i);
                float percSizeY = this.rectSizeY / ((float) i2);
                float w = (float) PhotoCropActivity.this.imageToCrop.getWidth();
                float h = (float) PhotoCropActivity.this.imageToCrop.getHeight();
                int i3 = this.viewWidth;
                float scaleX = ((float) i3) / w;
                int i4 = this.viewHeight;
                float scaleY = ((float) i4) / h;
                if (scaleX > scaleY) {
                    this.bitmapHeight = i4;
                    this.bitmapWidth = (int) Math.ceil((double) (w * scaleY));
                } else {
                    this.bitmapWidth = i3;
                    this.bitmapHeight = (int) Math.ceil((double) (h * scaleX));
                }
                this.bitmapX = ((this.viewWidth - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f);
                int dp = ((this.viewHeight - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f);
                this.bitmapY = dp;
                if (this.rectX != -1.0f || this.rectY != -1.0f) {
                    int i5 = this.bitmapWidth;
                    this.rectX = (((float) i5) * percX) + ((float) this.bitmapX);
                    int i6 = this.bitmapHeight;
                    this.rectY = (((float) i6) * percY) + ((float) this.bitmapY);
                    this.rectSizeX = ((float) i5) * percSizeX;
                    this.rectSizeY = ((float) i6) * percSizeY;
                } else if (this.freeform) {
                    this.rectY = (float) dp;
                    this.rectX = (float) this.bitmapX;
                    this.rectSizeX = (float) this.bitmapWidth;
                    this.rectSizeY = (float) this.bitmapHeight;
                } else {
                    int i7 = this.bitmapWidth;
                    int i8 = this.bitmapHeight;
                    if (i7 > i8) {
                        this.rectY = (float) dp;
                        this.rectX = (float) (((this.viewWidth - i8) / 2) + AndroidUtilities.dp(14.0f));
                        int i9 = this.bitmapHeight;
                        this.rectSizeX = (float) i9;
                        this.rectSizeY = (float) i9;
                    } else {
                        this.rectX = (float) this.bitmapX;
                        this.rectY = (float) (((this.viewHeight - i7) / 2) + AndroidUtilities.dp(14.0f));
                        int i10 = this.bitmapWidth;
                        this.rectSizeX = (float) i10;
                        this.rectSizeY = (float) i10;
                    }
                }
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            this.viewWidth = (right - left) - AndroidUtilities.dp(28.0f);
            this.viewHeight = (bottom - top) - AndroidUtilities.dp(28.0f);
            updateBitmapSize();
        }

        public Bitmap getBitmap() {
            float f = this.rectX - ((float) this.bitmapX);
            int i = this.bitmapWidth;
            float percY = (this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight);
            float percSizeX = this.rectSizeX / ((float) i);
            float percSizeY = this.rectSizeY / ((float) i);
            int x = (int) (((float) PhotoCropActivity.this.imageToCrop.getWidth()) * (f / ((float) i)));
            int y = (int) (((float) PhotoCropActivity.this.imageToCrop.getHeight()) * percY);
            int sizeX = (int) (((float) PhotoCropActivity.this.imageToCrop.getWidth()) * percSizeX);
            int sizeY = (int) (((float) PhotoCropActivity.this.imageToCrop.getWidth()) * percSizeY);
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x + sizeX > PhotoCropActivity.this.imageToCrop.getWidth()) {
                sizeX = PhotoCropActivity.this.imageToCrop.getWidth() - x;
            }
            if (y + sizeY > PhotoCropActivity.this.imageToCrop.getHeight()) {
                sizeY = PhotoCropActivity.this.imageToCrop.getHeight() - y;
            }
            try {
                return Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, x, y, sizeX, sizeY);
            } catch (Throwable e2) {
                FileLog.e(e2);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            KLog.d("--------重绘");
            if (PhotoCropActivity.this.drawable != null) {
                try {
                    PhotoCropActivity.this.drawable.setBounds(this.bitmapX, this.bitmapY, this.bitmapX + this.bitmapWidth, this.bitmapY + this.bitmapHeight);
                    PhotoCropActivity.this.drawable.draw(canvas);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            int i = this.bitmapX;
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) i, (float) this.bitmapY, (float) (i + this.bitmapWidth), this.rectY, this.halfPaint);
            float f = this.rectY;
            canvas2.drawRect((float) this.bitmapX, f, this.rectX, f + this.rectSizeY, this.halfPaint);
            float f2 = this.rectY;
            canvas.drawRect(this.rectX + this.rectSizeX, f2, (float) (this.bitmapX + this.bitmapWidth), f2 + this.rectSizeY, this.halfPaint);
            int i2 = this.bitmapX;
            Canvas canvas3 = canvas;
            canvas3.drawRect((float) i2, this.rectSizeY + this.rectY, (float) (i2 + this.bitmapWidth), (float) (this.bitmapY + this.bitmapHeight), this.halfPaint);
            float f3 = this.rectX;
            float f4 = this.rectY;
            canvas.drawRect(f3, f4, f3 + this.rectSizeX, f4 + this.rectSizeY, this.rectPaint);
            int side = AndroidUtilities.dp(1.0f);
            float f5 = this.rectX;
            canvas.drawRect(f5 + ((float) side), this.rectY + ((float) side), f5 + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), this.rectY + ((float) (side * 3)), this.circlePaint);
            float f6 = this.rectX;
            float f7 = this.rectY;
            Canvas canvas4 = canvas;
            canvas4.drawRect(f6 + ((float) side), f7 + ((float) side), f6 + ((float) (side * 3)), f7 + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), this.circlePaint);
            float dp = ((this.rectX + this.rectSizeX) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f));
            float f8 = this.rectY;
            canvas.drawRect(dp, f8 + ((float) side), (this.rectX + this.rectSizeX) - ((float) side), f8 + ((float) (side * 3)), this.circlePaint);
            float f9 = this.rectX;
            float f10 = this.rectSizeX;
            float f11 = this.rectY;
            Canvas canvas5 = canvas;
            canvas5.drawRect((f9 + f10) - ((float) (side * 3)), f11 + ((float) side), (f9 + f10) - ((float) side), f11 + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), this.circlePaint);
            canvas.drawRect(this.rectX + ((float) side), ((this.rectY + this.rectSizeY) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f)), this.rectX + ((float) (side * 3)), (this.rectY + this.rectSizeY) - ((float) side), this.circlePaint);
            float f12 = this.rectX;
            canvas.drawRect(f12 + ((float) side), (this.rectY + this.rectSizeY) - ((float) (side * 3)), f12 + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), (this.rectY + this.rectSizeY) - ((float) side), this.circlePaint);
            float dp2 = ((this.rectX + this.rectSizeX) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f));
            float f13 = this.rectY;
            float f14 = this.rectSizeY;
            Canvas canvas6 = canvas;
            canvas6.drawRect(dp2, (f13 + f14) - ((float) (side * 3)), (this.rectX + this.rectSizeX) - ((float) side), (f13 + f14) - ((float) side), this.circlePaint);
            canvas6.drawRect((this.rectX + this.rectSizeX) - ((float) (side * 3)), ((this.rectY + this.rectSizeY) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f)), (this.rectX + this.rectSizeX) - ((float) side), (this.rectY + this.rectSizeY) - ((float) side), this.circlePaint);
            for (int a = 1; a < 3; a++) {
                float f15 = this.rectX;
                float f16 = this.rectSizeX;
                float f17 = this.rectY;
                Canvas canvas7 = canvas;
                canvas7.drawRect(f15 + ((f16 / 3.0f) * ((float) a)), f17 + ((float) side), f15 + ((float) side) + ((f16 / 3.0f) * ((float) a)), (f17 + this.rectSizeY) - ((float) side), this.circlePaint);
                float f18 = this.rectX;
                float f19 = this.rectY;
                float f20 = this.rectSizeY;
                Canvas canvas8 = canvas;
                canvas8.drawRect(f18 + ((float) side), ((f20 / 3.0f) * ((float) a)) + f19, this.rectSizeX + (f18 - ((float) side)), f19 + ((f20 / 3.0f) * ((float) a)) + ((float) side), this.circlePaint);
            }
        }
    }

    public PhotoCropActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        int size;
        KLog.d("-----???????");
        this.swipeBackEnabled = false;
        if (this.imageToCrop == null) {
            String photoPath = getArguments().getString("photoPath");
            Uri photoUri = (Uri) getArguments().getParcelable("photoUri");
            if (photoPath == null && photoUri == null) {
                return false;
            }
            if (photoPath != null && !new File(photoPath).exists()) {
                return false;
            }
            if (AndroidUtilities.isTablet()) {
                size = AndroidUtilities.dp(520.0f);
            } else {
                size = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            }
            Bitmap loadBitmap = ImageLoader.loadBitmap(photoPath, photoUri, (float) size, (float) size, true);
            this.imageToCrop = loadBitmap;
            if (loadBitmap == null) {
                return false;
            }
        }
        this.drawable = new BitmapDrawable(this.imageToCrop);
        super.onFragmentCreate();
        return true;
    }

    /* access modifiers changed from: private */
    public Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90.0f);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public void onFragmentDestroy() {
        Bitmap bitmap;
        super.onFragmentDestroy();
        if (this.bitmapKey != null && ImageLoader.getInstance().decrementUseCount(this.bitmapKey) && !ImageLoader.getInstance().isInMemCache(this.bitmapKey, false)) {
            this.bitmapKey = null;
        }
        if (this.bitmapKey == null && (bitmap = this.imageToCrop) != null && !this.sameBitmap) {
            bitmap.recycle();
            this.imageToCrop = null;
        }
        this.drawable = null;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CropImage", R.string.CropImage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoCropActivity photoCropActivity = PhotoCropActivity.this;
                    Bitmap unused = photoCropActivity.imageToCrop = photoCropActivity.rotateBitmap(photoCropActivity.imageToCrop, 90.0f);
                    BitmapDrawable unused2 = PhotoCropActivity.this.drawable = new BitmapDrawable(PhotoCropActivity.this.imageToCrop);
                    PhotoCropActivity.this.view.invalidate();
                } else if (id == 1) {
                    if (PhotoCropActivity.this.delegate != null && !PhotoCropActivity.this.doneButtonPressed) {
                        Bitmap bitmap = PhotoCropActivity.this.view.getBitmap();
                        if (bitmap == PhotoCropActivity.this.imageToCrop) {
                            boolean unused3 = PhotoCropActivity.this.sameBitmap = true;
                        }
                        PhotoCropActivity.this.delegate.didFinishEdit(bitmap);
                        boolean unused4 = PhotoCropActivity.this.doneButtonPressed = true;
                    }
                    PhotoCropActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        PhotoCropView photoCropView = new PhotoCropView(context);
        this.view = photoCropView;
        this.fragmentView = photoCropView;
        ((PhotoCropView) this.fragmentView).freeform = getArguments().getBoolean("freeform", false);
        this.fragmentView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        return this.fragmentView;
    }

    public void setDelegate(PhotoEditActivityDelegate delegate2) {
        this.delegate = delegate2;
    }
}
