package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.util.ArrayList;

public class VideoTimelinePlayView extends View {
    private static final Object sync = new Object();
    private float bufferedProgress = 0.5f;
    private AsyncTask<Integer, Integer, Bitmap> currentTask;
    private VideoTimelineViewDelegate delegate;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    /* access modifiers changed from: private */
    public int frameHeight;
    /* access modifiers changed from: private */
    public long frameTimeOffset;
    /* access modifiers changed from: private */
    public int frameWidth;
    /* access modifiers changed from: private */
    public ArrayList<Bitmap> frames = new ArrayList<>();
    /* access modifiers changed from: private */
    public int framesToLoad;
    private boolean isRoundFrames;
    private int lastWidth;
    private float maxProgressDiff = 1.0f;
    /* access modifiers changed from: private */
    public MediaMetadataRetriever mediaMetadataRetriever;
    private float minProgressDiff = 0.0f;
    private Paint paint;
    private Paint paint2;
    private float playProgress = 0.5f;
    private float pressDx;
    private boolean pressedLeft;
    private boolean pressedPlay;
    private boolean pressedRight;
    private float progressLeft;
    private float progressRight = 1.0f;
    private Rect rect1;
    private Rect rect2;
    private RectF rect3 = new RectF();
    private long videoLength;

    public interface VideoTimelineViewDelegate {
        void didStartDragging();

        void didStopDragging();

        void onLeftProgressChanged(float f);

        void onPlayProgressChanged(float f);

        void onRightProgressChanged(float f);
    }

    public VideoTimelinePlayView(Context context) {
        super(context);
        Paint paint3 = new Paint(1);
        this.paint = paint3;
        paint3.setColor(-1);
        Paint paint4 = new Paint();
        this.paint2 = paint4;
        paint4.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        Drawable drawable = context.getResources().getDrawable(R.drawable.video_cropleft);
        this.drawableLeft = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        Drawable drawable2 = context.getResources().getDrawable(R.drawable.video_cropright);
        this.drawableRight = drawable2;
        drawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
    }

    public float getProgress() {
        return this.playProgress;
    }

    public float getLeftProgress() {
        return this.progressLeft;
    }

    public float getRightProgress() {
        return this.progressRight;
    }

    public void setMinProgressDiff(float value) {
        this.minProgressDiff = value;
    }

    public void setMaxProgressDiff(float value) {
        this.maxProgressDiff = value;
        float f = this.progressRight;
        float f2 = this.progressLeft;
        if (f - f2 > value) {
            this.progressRight = f2 + value;
            invalidate();
        }
    }

    public void setRoundFrames(boolean value) {
        this.isRoundFrames = value;
        if (value) {
            this.rect1 = new Rect(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            this.rect2 = new Rect();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int width = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        int startX = ((int) (((float) width) * this.progressLeft)) + AndroidUtilities.dp(16.0f);
        float f = this.progressLeft;
        int playX = ((int) (((float) width) * (f + ((this.progressRight - f) * this.playProgress)))) + AndroidUtilities.dp(16.0f);
        int endX = ((int) (((float) width) * this.progressRight)) + AndroidUtilities.dp(16.0f);
        if (event.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int additionWidth = AndroidUtilities.dp(12.0f);
            int additionWidthPlay = AndroidUtilities.dp(8.0f);
            if (((float) (playX - additionWidthPlay)) <= x && x <= ((float) (playX + additionWidthPlay)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging();
                }
                this.pressedPlay = true;
                this.pressDx = (float) ((int) (x - ((float) playX)));
                invalidate();
                return true;
            } else if (((float) (startX - additionWidth)) <= x && x <= ((float) (startX + additionWidth)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) startX)));
                invalidate();
                return true;
            } else if (((float) (endX - additionWidth)) <= x && x <= ((float) (endX + additionWidth)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
                if (videoTimelineViewDelegate3 != null) {
                    videoTimelineViewDelegate3.didStartDragging();
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) endX)));
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (this.pressedLeft) {
                VideoTimelineViewDelegate videoTimelineViewDelegate4 = this.delegate;
                if (videoTimelineViewDelegate4 != null) {
                    videoTimelineViewDelegate4.didStopDragging();
                }
                this.pressedLeft = false;
                return true;
            } else if (this.pressedRight) {
                VideoTimelineViewDelegate videoTimelineViewDelegate5 = this.delegate;
                if (videoTimelineViewDelegate5 != null) {
                    videoTimelineViewDelegate5.didStopDragging();
                }
                this.pressedRight = false;
                return true;
            } else if (this.pressedPlay) {
                VideoTimelineViewDelegate videoTimelineViewDelegate6 = this.delegate;
                if (videoTimelineViewDelegate6 != null) {
                    videoTimelineViewDelegate6.didStopDragging();
                }
                this.pressedPlay = false;
                return true;
            }
        } else if (event.getAction() == 2) {
            if (this.pressedPlay) {
                float dp = ((float) (((int) (x - this.pressDx)) - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.playProgress = dp;
                float f2 = this.progressLeft;
                if (dp < f2) {
                    this.playProgress = f2;
                } else {
                    float f3 = this.progressRight;
                    if (dp > f3) {
                        this.playProgress = f3;
                    }
                }
                float f4 = this.playProgress;
                float f5 = this.progressLeft;
                float f6 = this.progressRight;
                float f7 = (f4 - f5) / (f6 - f5);
                this.playProgress = f7;
                VideoTimelineViewDelegate videoTimelineViewDelegate7 = this.delegate;
                if (videoTimelineViewDelegate7 != null) {
                    videoTimelineViewDelegate7.onPlayProgressChanged(f5 + ((f6 - f5) * f7));
                }
                invalidate();
                return true;
            } else if (this.pressedLeft) {
                int startX2 = (int) (x - this.pressDx);
                if (startX2 < AndroidUtilities.dp(16.0f)) {
                    startX2 = AndroidUtilities.dp(16.0f);
                } else if (startX2 > endX) {
                    startX2 = endX;
                }
                float dp2 = ((float) (startX2 - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.progressLeft = dp2;
                float f8 = this.progressRight;
                float f9 = this.maxProgressDiff;
                if (f8 - dp2 > f9) {
                    this.progressRight = dp2 + f9;
                } else {
                    float f10 = this.minProgressDiff;
                    if (f10 != 0.0f && f8 - dp2 < f10) {
                        float f11 = f8 - f10;
                        this.progressLeft = f11;
                        if (f11 < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate8 = this.delegate;
                if (videoTimelineViewDelegate8 != null) {
                    videoTimelineViewDelegate8.onLeftProgressChanged(this.progressLeft);
                }
                invalidate();
                return true;
            } else if (this.pressedRight) {
                int endX2 = (int) (x - this.pressDx);
                if (endX2 < startX) {
                    endX2 = startX;
                } else if (endX2 > AndroidUtilities.dp(16.0f) + width) {
                    endX2 = width + AndroidUtilities.dp(16.0f);
                }
                float dp3 = ((float) (endX2 - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.progressRight = dp3;
                float f12 = this.progressLeft;
                float f13 = this.maxProgressDiff;
                if (dp3 - f12 > f13) {
                    this.progressLeft = dp3 - f13;
                } else {
                    float f14 = this.minProgressDiff;
                    if (f14 != 0.0f && dp3 - f12 < f14) {
                        float f15 = f12 + f14;
                        this.progressRight = f15;
                        if (f15 > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate9 = this.delegate;
                if (videoTimelineViewDelegate9 != null) {
                    videoTimelineViewDelegate9.onRightProgressChanged(this.progressRight);
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public void setVideoPath(String path, float left, float right) {
        destroy();
        MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
        this.mediaMetadataRetriever = mediaMetadataRetriever2;
        this.progressLeft = left;
        this.progressRight = right;
        try {
            mediaMetadataRetriever2.setDataSource(path);
            this.videoLength = Long.parseLong(this.mediaMetadataRetriever.extractMetadata(9));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        invalidate();
    }

    public void setDelegate(VideoTimelineViewDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void reloadFrames(int frameNum) {
        if (this.mediaMetadataRetriever != null) {
            if (frameNum == 0) {
                if (this.isRoundFrames) {
                    int dp = AndroidUtilities.dp(56.0f);
                    this.frameWidth = dp;
                    this.frameHeight = dp;
                    this.framesToLoad = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / (((float) this.frameHeight) / 2.0f)));
                } else {
                    this.frameHeight = AndroidUtilities.dp(40.0f);
                    this.framesToLoad = (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight;
                    this.frameWidth = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / ((float) this.framesToLoad)));
                }
                this.frameTimeOffset = this.videoLength / ((long) this.framesToLoad);
            }
            AnonymousClass1 r0 = new AsyncTask<Integer, Integer, Bitmap>() {
                private int frameNum = 0;

                /* access modifiers changed from: protected */
                public Bitmap doInBackground(Integer... objects) {
                    this.frameNum = objects[0].intValue();
                    if (isCancelled()) {
                        return null;
                    }
                    try {
                        Bitmap bitmap = VideoTimelinePlayView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelinePlayView.this.frameTimeOffset * ((long) this.frameNum) * 1000, 2);
                        if (isCancelled()) {
                            return null;
                        }
                        if (bitmap == null) {
                            return bitmap;
                        }
                        Bitmap result = Bitmap.createBitmap(VideoTimelinePlayView.this.frameWidth, VideoTimelinePlayView.this.frameHeight, bitmap.getConfig());
                        Canvas canvas = new Canvas(result);
                        float scaleX = ((float) VideoTimelinePlayView.this.frameWidth) / ((float) bitmap.getWidth());
                        float scaleY = ((float) VideoTimelinePlayView.this.frameHeight) / ((float) bitmap.getHeight());
                        float scale = scaleX > scaleY ? scaleX : scaleY;
                        int w = (int) (((float) bitmap.getWidth()) * scale);
                        int h = (int) (((float) bitmap.getHeight()) * scale);
                        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((VideoTimelinePlayView.this.frameWidth - w) / 2, (VideoTimelinePlayView.this.frameHeight - h) / 2, w, h), (Paint) null);
                        bitmap.recycle();
                        return result;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return null;
                    }
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(Bitmap bitmap) {
                    if (!isCancelled()) {
                        VideoTimelinePlayView.this.frames.add(bitmap);
                        VideoTimelinePlayView.this.invalidate();
                        if (this.frameNum < VideoTimelinePlayView.this.framesToLoad) {
                            VideoTimelinePlayView.this.reloadFrames(this.frameNum + 1);
                        }
                    }
                }
            };
            this.currentTask = r0;
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{Integer.valueOf(frameNum), null, null});
        }
    }

    public void destroy() {
        synchronized (sync) {
            try {
                if (this.mediaMetadataRetriever != null) {
                    this.mediaMetadataRetriever.release();
                    this.mediaMetadataRetriever = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        for (int a = 0; a < this.frames.size(); a++) {
            Bitmap bitmap = this.frames.get(a);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
    }

    public boolean isDragging() {
        return this.pressedPlay;
    }

    public void setProgress(float value) {
        this.playProgress = value;
        invalidate();
    }

    public void clearFrames() {
        for (int a = 0; a < this.frames.size(); a++) {
            Bitmap bitmap = this.frames.get(a);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.lastWidth != widthSize) {
            clearFrames();
            this.lastWidth = widthSize;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int width = getMeasuredWidth() - AndroidUtilities.dp(36.0f);
        float f = 16.0f;
        int startX = ((int) (((float) width) * this.progressLeft)) + AndroidUtilities.dp(16.0f);
        int endX = ((int) (((float) width) * this.progressRight)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        canvas2.clipRect(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(20.0f) + width, AndroidUtilities.dp(48.0f));
        if (!this.frames.isEmpty() || this.currentTask != null) {
            int offset = 0;
            int a = 0;
            while (a < this.frames.size()) {
                Bitmap bitmap = this.frames.get(a);
                if (bitmap != null) {
                    int x = AndroidUtilities.dp(f) + ((this.isRoundFrames ? this.frameWidth / 2 : this.frameWidth) * offset);
                    int y = AndroidUtilities.dp(6.0f);
                    if (this.isRoundFrames) {
                        this.rect2.set(x, y, x + AndroidUtilities.dp(28.0f), y + AndroidUtilities.dp(28.0f));
                        canvas2.drawBitmap(bitmap, this.rect1, this.rect2, (Paint) null);
                    } else {
                        canvas2.drawBitmap(bitmap, (float) x, (float) y, (Paint) null);
                    }
                }
                offset++;
                a++;
                f = 16.0f;
            }
        } else {
            reloadFrames(0);
        }
        int top = AndroidUtilities.dp(6.0f);
        int end = AndroidUtilities.dp(48.0f);
        canvas.drawRect((float) AndroidUtilities.dp(16.0f), (float) top, (float) startX, (float) AndroidUtilities.dp(46.0f), this.paint2);
        canvas.drawRect((float) (AndroidUtilities.dp(4.0f) + endX), (float) top, (float) (AndroidUtilities.dp(16.0f) + width + AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(46.0f), this.paint2);
        canvas.drawRect((float) startX, (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(2.0f) + startX), (float) end, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + endX), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(4.0f) + endX), (float) end, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + startX), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(4.0f) + endX), (float) top, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + startX), (float) (end - AndroidUtilities.dp(2.0f)), (float) (AndroidUtilities.dp(4.0f) + endX), (float) end, this.paint);
        canvas.restore();
        this.rect3.set((float) (startX - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(2.0f) + startX), (float) end);
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        this.drawableLeft.setBounds(startX - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), AndroidUtilities.dp(2.0f) + startX, ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableLeft.draw(canvas2);
        this.rect3.set((float) (AndroidUtilities.dp(2.0f) + endX), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(12.0f) + endX), (float) end);
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        this.drawableRight.setBounds(AndroidUtilities.dp(2.0f) + endX, AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), AndroidUtilities.dp(12.0f) + endX, ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableRight.draw(canvas2);
        float f2 = this.progressLeft;
        float cx = ((float) AndroidUtilities.dp(18.0f)) + (((float) width) * (f2 + ((this.progressRight - f2) * this.playProgress)));
        this.rect3.set(cx - ((float) AndroidUtilities.dp(1.5f)), (float) AndroidUtilities.dp(2.0f), ((float) AndroidUtilities.dp(1.5f)) + cx, (float) AndroidUtilities.dp(50.0f));
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.paint2);
        canvas2.drawCircle(cx, (float) AndroidUtilities.dp(52.0f), (float) AndroidUtilities.dp(3.5f), this.paint2);
        this.rect3.set(cx - ((float) AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(2.0f), ((float) AndroidUtilities.dp(1.0f)) + cx, (float) AndroidUtilities.dp(50.0f));
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.paint);
        canvas2.drawCircle(cx, (float) AndroidUtilities.dp(52.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
    }
}
