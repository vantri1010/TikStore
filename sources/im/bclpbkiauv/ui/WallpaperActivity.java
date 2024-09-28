package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.WallpaperActivity;
import im.bclpbkiauv.ui.WallpapersListActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.ChatActionCell;
import im.bclpbkiauv.ui.cells.ChatMessageCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.components.AnimationProperties;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ColorPicker;
import im.bclpbkiauv.ui.components.CubicBezierInterpolator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgress2;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SeekBarView;
import im.bclpbkiauv.ui.components.ShareAlert;
import im.bclpbkiauv.ui.components.WallpaperParallaxEffect;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import kotlin.UByte;
import org.slf4j.Marker;

public class WallpaperActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private static final int share_item = 1;
    private int TAG;
    /* access modifiers changed from: private */
    public int backgroundColor;
    private BackupImageView backgroundImage;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    /* access modifiers changed from: private */
    public PorterDuff.Mode blendMode = PorterDuff.Mode.SRC_IN;
    private Bitmap blurredBitmap;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private FrameLayout buttonsContainer;
    /* access modifiers changed from: private */
    public CheckBoxView[] checkBoxView;
    /* access modifiers changed from: private */
    public Paint checkPaint;
    private ColorPicker colorPicker;
    /* access modifiers changed from: private */
    public float currentIntensity = 0.4f;
    /* access modifiers changed from: private */
    public Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    /* access modifiers changed from: private */
    public Paint eraserPaint;
    private String imageFilter = "640_360";
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    /* access modifiers changed from: private */
    public boolean isBlurred;
    /* access modifiers changed from: private */
    public boolean isMotion;
    private RecyclerListView listView;
    private String loadingFile = null;
    private File loadingFileObject = null;
    private TLRPC.PhotoSize loadingSize = null;
    private int maxWallpaperSize = 1920;
    /* access modifiers changed from: private */
    public AnimatorSet motionAnimation;
    /* access modifiers changed from: private */
    public WallpaperParallaxEffect parallaxEffect;
    /* access modifiers changed from: private */
    public float parallaxScale = 1.0f;
    /* access modifiers changed from: private */
    public int patternColor;
    /* access modifiers changed from: private */
    public FrameLayout[] patternLayout = new FrameLayout[3];
    /* access modifiers changed from: private */
    public ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer = new FrameLayout[2];
    private TextView[] patternsCancelButton = new TextView[2];
    private LinearLayoutManager patternsLayoutManager;
    private RecyclerListView patternsListView;
    private TextView[] patternsSaveButton = new TextView[2];
    private int previousBackgroundColor;
    private float previousIntensity;
    private TLRPC.TL_wallPaper previousSelectedPattern;
    /* access modifiers changed from: private */
    public boolean progressVisible;
    /* access modifiers changed from: private */
    public RadialProgress2 radialProgress;
    /* access modifiers changed from: private */
    public TLRPC.TL_wallPaper selectedPattern;
    /* access modifiers changed from: private */
    public TextPaint textPaint;

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    private class PatternCell extends BackupImageView implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        private TLRPC.TL_wallPaper currentPattern;
        private RadialProgress2 radialProgress;
        private RectF rect = new RectF();
        private boolean wasSelected;

        public PatternCell(Context context) {
            super(context);
            setRoundRadius(AndroidUtilities.dp(6.0f));
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressRect(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f));
            this.TAG = DownloadController.getInstance(WallpaperActivity.this.currentAccount).generateObserverTag();
        }

        /* access modifiers changed from: private */
        public void setPattern(TLRPC.TL_wallPaper wallPaper) {
            this.currentPattern = wallPaper;
            if (wallPaper != null) {
                setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100), wallPaper.document), "100_100", (ImageLocation) null, (String) null, "jpg", 0, 1, wallPaper);
            } else {
                setImageDrawable((Drawable) null);
            }
            updateSelected(false);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateSelected(false);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0015, code lost:
            r0 = r8.currentPattern;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateSelected(boolean r9) {
            /*
                r8 = this;
                im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = r8.currentPattern
                r1 = 0
                if (r0 != 0) goto L_0x000d
                im.bclpbkiauv.ui.WallpaperActivity r0 = im.bclpbkiauv.ui.WallpaperActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = r0.selectedPattern
                if (r0 == 0) goto L_0x0027
            L_0x000d:
                im.bclpbkiauv.ui.WallpaperActivity r0 = im.bclpbkiauv.ui.WallpaperActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = r0.selectedPattern
                if (r0 == 0) goto L_0x0029
                im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = r8.currentPattern
                if (r0 == 0) goto L_0x0029
                long r2 = r0.id
                im.bclpbkiauv.ui.WallpaperActivity r0 = im.bclpbkiauv.ui.WallpaperActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = r0.selectedPattern
                long r4 = r0.id
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 != 0) goto L_0x0029
            L_0x0027:
                r0 = 1
                goto L_0x002a
            L_0x0029:
                r0 = 0
            L_0x002a:
                if (r0 == 0) goto L_0x003b
                im.bclpbkiauv.ui.WallpaperActivity r2 = im.bclpbkiauv.ui.WallpaperActivity.this
                im.bclpbkiauv.ui.components.RadialProgress2 r3 = r8.radialProgress
                im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r4 = r2.selectedPattern
                r6 = 0
                r5 = r8
                r7 = r9
                r2.updateButtonState(r3, r4, r5, r6, r7)
                goto L_0x0041
            L_0x003b:
                im.bclpbkiauv.ui.components.RadialProgress2 r2 = r8.radialProgress
                r3 = 4
                r2.setIcon(r3, r1, r9)
            L_0x0041:
                r8.invalidate()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.WallpaperActivity.PatternCell.updateSelected(boolean):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            getImageReceiver().setAlpha(0.8f);
            WallpaperActivity.this.backgroundPaint.setColor(WallpaperActivity.this.backgroundColor);
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), WallpaperActivity.this.backgroundPaint);
            super.onDraw(canvas);
            this.radialProgress.setColors(WallpaperActivity.this.patternColor, WallpaperActivity.this.patternColor, -1, -1);
            this.radialProgress.draw(canvas);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, true, canceled);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, WallpaperActivity.this.progressVisible);
            WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, false, true);
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, WallpaperActivity.this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                WallpaperActivity.this.updateButtonState(this.radialProgress, this.currentPattern, this, false, true);
            }
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class CheckBoxView extends View {
        private static final float progressBounceDiff = 0.2f;
        public final Property<CheckBoxView, Float> PROGRESS_PROPERTY = new AnimationProperties.FloatProperty<CheckBoxView>(NotificationCompat.CATEGORY_PROGRESS) {
            public void setValue(CheckBoxView object, float value) {
                float unused = CheckBoxView.this.progress = value;
                CheckBoxView.this.invalidate();
            }

            public Float get(CheckBoxView object) {
                return Float.valueOf(CheckBoxView.this.progress);
            }
        };
        private ObjectAnimator checkAnimator;
        private String currentText;
        private int currentTextSize;
        private Bitmap drawBitmap;
        private Canvas drawCanvas;
        private boolean isChecked;
        private int maxTextSize;
        /* access modifiers changed from: private */
        public float progress;
        private RectF rect = new RectF();

        public CheckBoxView(Context context, boolean check) {
            super(context);
            if (check) {
                this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Bitmap.Config.ARGB_4444);
                this.drawCanvas = new Canvas(this.drawBitmap);
            }
        }

        public void setText(String text, int current, int max) {
            this.currentText = text;
            this.currentTextSize = current;
            this.maxTextSize = max;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.maxTextSize + AndroidUtilities.dp(56.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float checkProgress;
            float bounceProgress;
            Canvas canvas2 = canvas;
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_actionBackgroundPaint);
            int x = ((getMeasuredWidth() - this.currentTextSize) - AndroidUtilities.dp(28.0f)) / 2;
            canvas2.drawText(this.currentText, (float) (AndroidUtilities.dp(28.0f) + x), (float) AndroidUtilities.dp(21.0f), WallpaperActivity.this.textPaint);
            canvas.save();
            canvas2.translate((float) x, (float) AndroidUtilities.dp(7.0f));
            if (this.drawBitmap != null) {
                float bounceProgress2 = this.progress;
                if (bounceProgress2 <= 0.5f) {
                    bounceProgress = bounceProgress2 / 0.5f;
                    checkProgress = bounceProgress;
                } else {
                    bounceProgress = 2.0f - (bounceProgress2 / 0.5f);
                    checkProgress = 1.0f;
                }
                float bounce = ((float) AndroidUtilities.dp(1.0f)) * bounceProgress;
                this.rect.set(bounce, bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce, ((float) AndroidUtilities.dp(18.0f)) - bounce);
                this.drawBitmap.eraseColor(0);
                WallpaperActivity.this.backgroundPaint.setColor(-1);
                Canvas canvas3 = this.drawCanvas;
                RectF rectF = this.rect;
                canvas3.drawRoundRect(rectF, rectF.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.backgroundPaint);
                if (checkProgress != 1.0f) {
                    float rad = Math.min((float) AndroidUtilities.dp(7.0f), (((float) AndroidUtilities.dp(7.0f)) * checkProgress) + bounce);
                    this.rect.set(((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(2.0f)) + rad, ((float) AndroidUtilities.dp(16.0f)) - rad, ((float) AndroidUtilities.dp(16.0f)) - rad);
                    Canvas canvas4 = this.drawCanvas;
                    RectF rectF2 = this.rect;
                    canvas4.drawRoundRect(rectF2, rectF2.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.eraserPaint);
                }
                if (this.progress > 0.5f) {
                    this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(2.5f)) * (1.0f - bounceProgress)))), WallpaperActivity.this.checkPaint);
                    this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.3f), (float) AndroidUtilities.dp(13.0f), (float) ((int) (((float) AndroidUtilities.dp(7.3f)) + (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), (float) ((int) (((float) AndroidUtilities.dp(13.0f)) - (((float) AndroidUtilities.dp(6.0f)) * (1.0f - bounceProgress)))), WallpaperActivity.this.checkPaint);
                }
                canvas2.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
            } else {
                WallpaperActivity.this.backgroundPaint.setColor(WallpaperActivity.this.backgroundColor);
                this.rect.set(0.0f, 0.0f, (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(18.0f));
                RectF rectF3 = this.rect;
                canvas2.drawRoundRect(rectF3, rectF3.width() / 2.0f, this.rect.height() / 2.0f, WallpaperActivity.this.backgroundPaint);
            }
            canvas.restore();
        }

        private void setProgress(float value) {
            if (this.progress != value) {
                this.progress = value;
                invalidate();
            }
        }

        private void cancelCheckAnimator() {
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
        }

        private void animateToCheckedState(boolean newCheckedState) {
            Property<CheckBoxView, Float> property = this.PROGRESS_PROPERTY;
            float[] fArr = new float[1];
            fArr[0] = newCheckedState ? 1.0f : 0.0f;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, property, fArr);
            this.checkAnimator = ofFloat;
            ofFloat.setDuration(300);
            this.checkAnimator.start();
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
        }

        public void setChecked(boolean checked, boolean animated) {
            if (checked != this.isChecked) {
                this.isChecked = checked;
                if (animated) {
                    animateToCheckedState(checked);
                    return;
                }
                cancelCheckAnimator();
                this.progress = checked ? 1.0f : 0.0f;
                invalidate();
            }
        }

        public boolean isChecked() {
            return this.isChecked;
        }
    }

    public WallpaperActivity(Object wallPaper, Bitmap bitmap) {
        this.currentWallpaper = wallPaper;
        this.currentWallpaperBitmap = bitmap;
        if (wallPaper instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) wallPaper;
        } else if (wallPaper instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper object = (WallpapersListActivity.ColorWallpaper) wallPaper;
            this.isMotion = object.motion;
            TLRPC.TL_wallPaper tL_wallPaper2 = object.pattern;
            this.selectedPattern = tL_wallPaper2;
            if (tL_wallPaper2 != null) {
                this.currentIntensity = object.intensity;
            }
        }
    }

    public void setInitialModes(boolean blur, boolean motion) {
        this.isBlurred = blur;
        this.isMotion = motion;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.imageFilter = ((int) (1080.0f / AndroidUtilities.density)) + "_" + ((int) (1920.0f / AndroidUtilities.density)) + "_f";
        this.maxWallpaperSize = Math.min(1920, Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setColor(-1);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        Paint paint = new Paint(1);
        this.checkPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.checkPaint.setColor(0);
        this.checkPaint.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint2 = new Paint(1);
        this.eraserPaint = paint2;
        paint2.setColor(0);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.backgroundPaint = new Paint(1);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        Bitmap bitmap = this.blurredBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.blurredBitmap = null;
        }
        Theme.applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
    }

    public View createView(Context context) {
        boolean buttonsAvailable;
        int startIndex;
        int textsCount;
        String[] texts;
        int[] textSizes;
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("BackgroundPreview", R.string.BackgroundPreview));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                String link;
                if (id == -1) {
                    WallpaperActivity.this.finishFragment();
                } else if (id == 1 && WallpaperActivity.this.getParentActivity() != null) {
                    StringBuilder modes = new StringBuilder();
                    if (WallpaperActivity.this.isBlurred) {
                        modes.append("blur");
                    }
                    if (WallpaperActivity.this.isMotion) {
                        if (modes.length() > 0) {
                            modes.append(Marker.ANY_NON_NULL_MARKER);
                        }
                        modes.append("motion");
                    }
                    if (WallpaperActivity.this.currentWallpaper instanceof TLRPC.TL_wallPaper) {
                        link = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + ((TLRPC.TL_wallPaper) WallpaperActivity.this.currentWallpaper).slug;
                        if (modes.length() > 0) {
                            link = link + "?mode=" + modes.toString();
                        }
                    } else if (WallpaperActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                        WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) WallpaperActivity.this.currentWallpaper;
                        String color = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (WallpaperActivity.this.backgroundColor >> 16)) & UByte.MAX_VALUE), Integer.valueOf(((byte) (WallpaperActivity.this.backgroundColor >> 8)) & UByte.MAX_VALUE), Byte.valueOf((byte) (WallpaperActivity.this.backgroundColor & 255))}).toLowerCase();
                        if (WallpaperActivity.this.selectedPattern != null) {
                            String link2 = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + WallpaperActivity.this.selectedPattern.slug + "?intensity=" + ((int) (WallpaperActivity.this.currentIntensity * 100.0f)) + "&bg_color=" + color;
                            if (modes.length() > 0) {
                                link = link2 + "&mode=" + modes.toString();
                            } else {
                                link = link2;
                            }
                        } else {
                            link = "https://" + MessagesController.getInstance(WallpaperActivity.this.currentAccount).linkPrefix + "/bg/" + color;
                        }
                    } else {
                        return;
                    }
                    WallpaperActivity.this.showDialog(new ShareAlert(WallpaperActivity.this.getParentActivity(), (ArrayList<MessageObject>) null, link, false, link, false));
                }
            }
        });
        Object obj = this.currentWallpaper;
        if ((obj instanceof WallpapersListActivity.ColorWallpaper) || (obj instanceof TLRPC.TL_wallPaper)) {
            this.actionBar.createMenu().addItem(1, (int) R.drawable.ic_share_video);
        }
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        this.hasOwnBackground = true;
        this.backgroundImage = new BackupImageView(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                WallpaperActivity wallpaperActivity = WallpaperActivity.this;
                float unused = wallpaperActivity.parallaxScale = wallpaperActivity.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                if (WallpaperActivity.this.isMotion) {
                    setScaleX(WallpaperActivity.this.parallaxScale);
                    setScaleY(WallpaperActivity.this.parallaxScale);
                }
                if (WallpaperActivity.this.radialProgress != null) {
                    int size = AndroidUtilities.dp(44.0f);
                    int x = (getMeasuredWidth() - size) / 2;
                    int y = (getMeasuredHeight() - size) / 2;
                    WallpaperActivity.this.radialProgress.setProgressRect(x, y, x + size, y + size);
                }
                boolean unused2 = WallpaperActivity.this.progressVisible = getMeasuredWidth() <= getMeasuredHeight();
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (WallpaperActivity.this.progressVisible && WallpaperActivity.this.radialProgress != null) {
                    WallpaperActivity.this.radialProgress.draw(canvas);
                }
            }

            public void setAlpha(float alpha) {
                WallpaperActivity.this.radialProgress.setOverrideAlpha(alpha);
            }
        };
        boolean z = false;
        if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
            textsCount = 3;
            startIndex = this.patterns != null ? 0 : 2;
            buttonsAvailable = (this.patterns == null && this.selectedPattern == null) ? false : true;
        } else {
            textsCount = 2;
            startIndex = 0;
            buttonsAvailable = true;
        }
        frameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.backgroundImage.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                WallpaperActivity.this.lambda$createView$0$WallpaperActivity(imageReceiver, z, z2);
            }

            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
        RadialProgress2 radialProgress2 = new RadialProgress2(this.backgroundImage);
        this.radialProgress = radialProgress2;
        radialProgress2.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, true));
        this.listView.setOverScrollMode(2);
        this.listView.setAdapter(new ListAdapter(context2));
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(buttonsAvailable ? 64.0f : 4.0f));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        AnonymousClass3 r0 = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat = r0;
        r0.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        frameLayout.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WallpaperActivity.this.lambda$createView$1$WallpaperActivity(view);
            }
        });
        TextView textView = new TextView(context2);
        this.bottomOverlayChatText = textView;
        textView.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        this.bottomOverlayChatText.setText(LocaleController.getString("SetBackground", R.string.SetBackground));
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.buttonsContainer = frameLayout2;
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-2.0f, 32.0f, 81, 0.0f, 0.0f, 0.0f, 66.0f));
        String[] texts2 = new String[textsCount];
        int[] textSizes2 = new int[textsCount];
        this.checkBoxView = new CheckBoxView[textsCount];
        if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
            texts2[0] = LocaleController.getString("BackgroundColor", R.string.BackgroundColor);
            texts2[1] = LocaleController.getString("BackgroundPattern", R.string.BackgroundPattern);
            texts2[2] = LocaleController.getString("BackgroundMotion", R.string.BackgroundMotion);
        } else {
            texts2[0] = LocaleController.getString("BackgroundBlurred", R.string.BackgroundBlurred);
            texts2[1] = LocaleController.getString("BackgroundMotion", R.string.BackgroundMotion);
        }
        int a = 0;
        int maxTextSize = 0;
        while (a < texts2.length) {
            textSizes2[a] = (int) Math.ceil((double) this.textPaint.measureText(texts2[a]));
            maxTextSize = Math.max(maxTextSize, textSizes2[a]);
            a++;
            frameLayout = frameLayout;
        }
        FrameLayout frameLayout3 = frameLayout;
        int a2 = startIndex;
        while (a2 < textsCount) {
            int num = a2;
            this.checkBoxView[a2] = new CheckBoxView(context2, !(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) || a2 != 0);
            this.checkBoxView[a2].setText(texts2[a2], textSizes2[a2], maxTextSize);
            if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.checkBoxView[a2].setChecked(a2 == 0 ? this.isBlurred : this.isMotion, z);
            } else if (a2 == 1) {
                this.checkBoxView[a2].setChecked(this.selectedPattern != null, z);
            } else if (a2 == 2) {
                this.checkBoxView[a2].setChecked(this.isMotion, z);
            }
            int width = AndroidUtilities.dp(56.0f) + maxTextSize;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, width);
            layoutParams.gravity = 51;
            layoutParams.leftMargin = a2 == 1 ? AndroidUtilities.dp(9.0f) + width : 0;
            this.buttonsContainer.addView(this.checkBoxView[a2], layoutParams);
            CheckBoxView[] checkBoxViewArr = this.checkBoxView;
            int i = width;
            checkBoxViewArr[a2].setOnClickListener(new View.OnClickListener(num, checkBoxViewArr[a2]) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ WallpaperActivity.CheckBoxView f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    WallpaperActivity.this.lambda$createView$2$WallpaperActivity(this.f$1, this.f$2, view);
                }
            });
            if (startIndex == 0 && a2 == 2) {
                this.checkBoxView[a2].setAlpha(0.0f);
                this.checkBoxView[a2].setVisibility(4);
            }
            a2++;
            z = false;
        }
        if (!buttonsAvailable) {
            this.buttonsContainer.setVisibility(8);
        }
        WallpaperParallaxEffect wallpaperParallaxEffect = new WallpaperParallaxEffect(context2);
        this.parallaxEffect = wallpaperParallaxEffect;
        wallpaperParallaxEffect.setCallback(new WallpaperParallaxEffect.Callback() {
            public final void onOffsetsChanged(int i, int i2) {
                WallpaperActivity.this.lambda$createView$3$WallpaperActivity(i, i2);
            }
        });
        if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
            this.isBlurred = false;
            int a3 = 0;
            while (a3 < 2) {
                int num2 = a3;
                this.patternLayout[a3] = new FrameLayout(context2) {
                    public void onDraw(Canvas canvas) {
                        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                        Theme.chat_composeShadowDrawable.draw(canvas);
                        canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                    }
                };
                this.patternLayout[a3].setVisibility(4);
                this.patternLayout[a3].setWillNotDraw(false);
                FrameLayout frameLayout4 = frameLayout3;
                frameLayout4.addView(this.patternLayout[a3], LayoutHelper.createFrame(-1, a3 == 0 ? 390 : 242, 83));
                this.patternsButtonsContainer[a3] = new FrameLayout(context2) {
                    public void onDraw(Canvas canvas) {
                        int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                        Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                        Theme.chat_composeShadowDrawable.draw(canvas);
                        canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                    }
                };
                this.patternsButtonsContainer[a3].setWillNotDraw(false);
                int[] textSizes3 = textSizes2;
                this.patternsButtonsContainer[a3].setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                String[] texts3 = texts2;
                FrameLayout frameLayout5 = frameLayout4;
                this.patternLayout[a3].addView(this.patternsButtonsContainer[a3], LayoutHelper.createFrame(-1, 51, 80));
                this.patternsCancelButton[a3] = new TextView(context2);
                this.patternsCancelButton[a3].setTextSize(1, 15.0f);
                this.patternsCancelButton[a3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.patternsCancelButton[a3].setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
                this.patternsCancelButton[a3].setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
                this.patternsCancelButton[a3].setGravity(17);
                this.patternsCancelButton[a3].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                this.patternsCancelButton[a3].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 0));
                int textsCount2 = textsCount;
                this.patternsButtonsContainer[a3].addView(this.patternsCancelButton[a3], LayoutHelper.createFrame(-2, -1, 51));
                this.patternsCancelButton[a3].setOnClickListener(new View.OnClickListener(num2) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        WallpaperActivity.this.lambda$createView$4$WallpaperActivity(this.f$1, view);
                    }
                });
                this.patternsSaveButton[a3] = new TextView(context2);
                this.patternsSaveButton[a3].setTextSize(1, 15.0f);
                this.patternsSaveButton[a3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.patternsSaveButton[a3].setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
                this.patternsSaveButton[a3].setText(LocaleController.getString("Save", R.string.Save).toUpperCase());
                this.patternsSaveButton[a3].setGravity(17);
                this.patternsSaveButton[a3].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                this.patternsSaveButton[a3].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 0));
                this.patternsButtonsContainer[a3].addView(this.patternsSaveButton[a3], LayoutHelper.createFrame(-2, -1, 53));
                this.patternsSaveButton[a3].setOnClickListener(new View.OnClickListener(num2) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        WallpaperActivity.this.lambda$createView$5$WallpaperActivity(this.f$1, view);
                    }
                });
                if (a3 == 1) {
                    AnonymousClass6 r1 = new RecyclerListView(context2) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (event.getAction() == 0) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    this.patternsListView = r1;
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 0, false);
                    this.patternsLayoutManager = linearLayoutManager;
                    r1.setLayoutManager(linearLayoutManager);
                    RecyclerListView recyclerListView2 = this.patternsListView;
                    PatternsAdapter patternsAdapter2 = new PatternsAdapter(context2);
                    this.patternsAdapter = patternsAdapter2;
                    recyclerListView2.setAdapter(patternsAdapter2);
                    this.patternsListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                            int position = parent.getChildAdapterPosition(view);
                            outRect.left = AndroidUtilities.dp(12.0f);
                            outRect.top = 0;
                            outRect.bottom = 0;
                            if (position == state.getItemCount() - 1) {
                                outRect.right = AndroidUtilities.dp(12.0f);
                            }
                        }
                    });
                    this.patternLayout[a3].addView(this.patternsListView, LayoutHelper.createFrame(-1.0f, 100.0f, 51, 0.0f, 14.0f, 0.0f, 0.0f));
                    this.patternsListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                        public final void onItemClick(View view, int i) {
                            WallpaperActivity.this.lambda$createView$6$WallpaperActivity(view, i);
                        }
                    });
                    HeaderCell headerCell = new HeaderCell(context2);
                    this.intensityCell = headerCell;
                    headerCell.setText(LocaleController.getString("BackgroundIntensity", R.string.BackgroundIntensity));
                    this.patternLayout[a3].addView(this.intensityCell, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 0.0f, 113.0f, 0.0f, 0.0f));
                    AnonymousClass8 r12 = new SeekBarView(context2) {
                        public boolean onTouchEvent(MotionEvent event) {
                            if (event.getAction() == 0) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onTouchEvent(event);
                        }
                    };
                    this.intensitySeekBar = r12;
                    r12.setProgress(this.currentIntensity);
                    this.intensitySeekBar.setReportChanges(true);
                    this.intensitySeekBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                        public final void onSeekBarDrag(float f) {
                            WallpaperActivity.this.lambda$createView$7$WallpaperActivity(f);
                        }
                    });
                    this.patternLayout[a3].addView(this.intensitySeekBar, LayoutHelper.createFrame(-1.0f, 30.0f, 51, 9.0f, 153.0f, 9.0f, 0.0f));
                } else {
                    ColorPicker colorPicker2 = new ColorPicker(context2, new ColorPicker.ColorPickerDelegate() {
                        public final void setColor(int i) {
                            WallpaperActivity.this.setBackgroundColor(i);
                        }
                    });
                    this.colorPicker = colorPicker2;
                    this.patternLayout[a3].addView(colorPicker2, LayoutHelper.createFrame(-1.0f, -1.0f, 1, 0.0f, 0.0f, 0.0f, 48.0f));
                }
                a3++;
                textSizes2 = textSizes3;
                texts2 = texts3;
                frameLayout3 = frameLayout5;
                textsCount = textsCount2;
            }
            textSizes = textSizes2;
            int i2 = textsCount;
            FrameLayout frameLayout6 = frameLayout3;
            texts = texts2;
        } else {
            textSizes = textSizes2;
            int i3 = textsCount;
            FrameLayout frameLayout7 = frameLayout3;
            texts = texts2;
        }
        setCurrentImage(true);
        int[] iArr = textSizes;
        String[] strArr = texts;
        updateButtonState(this.radialProgress, (Object) null, this, false, false);
        if (!this.backgroundImage.getImageReceiver().hasBitmapImage()) {
            this.fragmentView.setBackgroundColor(-16777216);
        }
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
            this.backgroundImage.getImageReceiver().setForceCrossfade(true);
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$WallpaperActivity(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (set && drawable != null) {
                Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable));
                this.listView.invalidateViews();
                int N = this.buttonsContainer.getChildCount();
                for (int a = 0; a < N; a++) {
                    this.buttonsContainer.getChildAt(a).invalidate();
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
                }
                if (!thumb && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    public /* synthetic */ void lambda$createView$1$WallpaperActivity(View view) {
        boolean done;
        int color;
        long id;
        int color2;
        long saveId;
        long id2;
        File f;
        boolean done2;
        boolean sameFile = false;
        File toFile = new File(ApplicationLoader.getFilesDirFixed(), this.isBlurred ? "wallpaper_original.jpg" : "wallpaper.jpg");
        Object obj = this.currentWallpaper;
        if (obj instanceof TLRPC.TL_wallPaper) {
            try {
                Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                FileOutputStream stream = new FileOutputStream(toFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                stream.close();
                done = true;
            } catch (Exception e) {
                done = false;
                FileLog.e((Throwable) e);
            }
            if (!done) {
                try {
                    done = AndroidUtilities.copyFile(FileLoader.getPathToAttach(((TLRPC.TL_wallPaper) this.currentWallpaper).document, true), toFile);
                } catch (Exception e2) {
                    done = false;
                    FileLog.e((Throwable) e2);
                }
            }
        } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            if (this.selectedPattern != null) {
                try {
                    WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                    Bitmap bitmap2 = this.backgroundImage.getImageReceiver().getBitmap();
                    Bitmap dst = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(dst);
                    canvas.drawColor(this.backgroundColor);
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                    paint.setAlpha((int) (this.currentIntensity * 255.0f));
                    canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
                    FileOutputStream stream2 = new FileOutputStream(toFile);
                    dst.compress(Bitmap.CompressFormat.JPEG, 87, stream2);
                    stream2.close();
                    done = true;
                } catch (Throwable e3) {
                    FileLog.e(e3);
                    done = false;
                }
            } else {
                done = true;
            }
        } else if (obj instanceof WallpapersListActivity.FileWallpaper) {
            WallpapersListActivity.FileWallpaper wallpaper = (WallpapersListActivity.FileWallpaper) obj;
            if (wallpaper.resId != 0 || ((long) wallpaper.resId) == -2) {
                done = true;
            } else {
                try {
                    File fromFile = wallpaper.originalPath != null ? wallpaper.originalPath : wallpaper.path;
                    boolean equals = fromFile.equals(toFile);
                    sameFile = equals;
                    if (equals) {
                        done2 = true;
                    } else {
                        done2 = AndroidUtilities.copyFile(fromFile, toFile);
                    }
                    done = done2;
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                    done = false;
                }
            }
        } else if (obj instanceof MediaController.SearchImage) {
            MediaController.SearchImage wallpaper2 = (MediaController.SearchImage) obj;
            if (wallpaper2.photo != null) {
                f = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(wallpaper2.photo.sizes, this.maxWallpaperSize, true), true);
            } else {
                f = ImageLoader.getHttpFilePath(wallpaper2.imageUrl, "jpg");
            }
            try {
                done = AndroidUtilities.copyFile(f, toFile);
            } catch (Exception e5) {
                FileLog.e((Throwable) e5);
                done = false;
            }
        } else {
            done = false;
        }
        if (this.isBlurred) {
            try {
                FileOutputStream stream3 = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                this.blurredBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream3);
                stream3.close();
                done = true;
            } catch (Throwable e6) {
                FileLog.e(e6);
                done = false;
            }
        }
        String slug = null;
        long saveId2 = 0;
        long access_hash = 0;
        long pattern = 0;
        File path = null;
        Object obj2 = this.currentWallpaper;
        if (obj2 instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) obj2;
            long id3 = wallPaper.id;
            saveId2 = id3;
            access_hash = wallPaper.access_hash;
            slug = wallPaper.slug;
            color = 0;
            id = id3;
        } else if (obj2 instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper wallPaper2 = (WallpapersListActivity.ColorWallpaper) obj2;
            TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
            if (tL_wallPaper != null) {
                long saveId3 = tL_wallPaper.id;
                access_hash = this.selectedPattern.access_hash;
                long saveId4 = saveId3;
                if (wallPaper2.id == wallPaper2.patternId && this.backgroundColor == wallPaper2.color && wallPaper2.intensity - this.currentIntensity <= 0.001f) {
                    id2 = this.selectedPattern.id;
                } else {
                    id2 = -1;
                }
                pattern = this.selectedPattern.id;
                slug = this.selectedPattern.slug;
                saveId2 = saveId4;
                saveId = id2;
            } else {
                saveId = -1;
            }
            color = this.backgroundColor;
            id = saveId;
        } else if (obj2 instanceof WallpapersListActivity.FileWallpaper) {
            WallpapersListActivity.FileWallpaper wallPaper3 = (WallpapersListActivity.FileWallpaper) obj2;
            long id4 = wallPaper3.id;
            path = wallPaper3.path;
            id = id4;
            color = 0;
        } else if (obj2 instanceof MediaController.SearchImage) {
            MediaController.SearchImage wallPaper4 = (MediaController.SearchImage) obj2;
            if (wallPaper4.photo != null) {
                color2 = 0;
                path = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(wallPaper4.photo.sizes, this.maxWallpaperSize, true), true);
            } else {
                color2 = 0;
                path = ImageLoader.getHttpFilePath(wallPaper4.imageUrl, "jpg");
            }
            id = -1;
            color = color2;
        } else {
            id = 0;
            color = 0;
        }
        File toFile2 = toFile;
        boolean sameFile2 = sameFile;
        MessagesController.getInstance(this.currentAccount).saveWallpaperToServer(path, saveId2, slug, access_hash, this.isBlurred, this.isMotion, color, this.currentIntensity, access_hash != 0, 0);
        if (done) {
            Theme.serviceMessageColorBackup = Theme.getColor(Theme.key_chat_serviceBackground);
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("selectedBackground2", id);
            SharedPreferences sharedPreferences = preferences;
            if (!TextUtils.isEmpty(slug)) {
                editor.putString("selectedBackgroundSlug", slug);
            } else {
                editor.remove("selectedBackgroundSlug");
            }
            editor.putBoolean("selectedBackgroundBlurred", this.isBlurred);
            editor.putBoolean("selectedBackgroundMotion", this.isMotion);
            editor.putInt("selectedColor", color);
            editor.putFloat("selectedIntensity", this.currentIntensity);
            editor.putLong("selectedPattern", pattern);
            editor.putBoolean("overrideThemeWallpaper", id != -2);
            editor.commit();
            Theme.reloadWallpaper();
            if (!sameFile2) {
                ImageLoader instance = ImageLoader.getInstance();
                StringBuilder sb = new StringBuilder();
                String str = slug;
                sb.append(ImageLoader.getHttpFileName(toFile2.getAbsolutePath()));
                sb.append("@100_100");
                instance.removeImage(sb.toString());
            }
        }
        WallpaperActivityDelegate wallpaperActivityDelegate = this.delegate;
        if (wallpaperActivityDelegate != null) {
            wallpaperActivityDelegate.didSetNewBackground();
        }
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$2$WallpaperActivity(int num, CheckBoxView view, View v) {
        if (this.buttonsContainer.getAlpha() == 1.0f) {
            boolean z = true;
            if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                view.setChecked(!view.isChecked(), true);
                if (num == 0) {
                    this.isBlurred = view.isChecked();
                    updateBlurred();
                    return;
                }
                boolean isChecked = view.isChecked();
                this.isMotion = isChecked;
                this.parallaxEffect.setEnabled(isChecked);
                animateMotionChange();
            } else if (num == 2) {
                view.setChecked(!view.isChecked(), true);
                boolean isChecked2 = view.isChecked();
                this.isMotion = isChecked2;
                this.parallaxEffect.setEnabled(isChecked2);
                animateMotionChange();
            } else {
                if (num == 1 && this.patternLayout[num].getVisibility() == 0) {
                    this.backgroundImage.setImageDrawable((Drawable) null);
                    this.selectedPattern = null;
                    this.isMotion = false;
                    updateButtonState(this.radialProgress, (Object) null, this, false, true);
                    updateSelectedPattern(true);
                    this.checkBoxView[1].setChecked(false, true);
                    this.patternsListView.invalidateViews();
                }
                if (this.patternLayout[num].getVisibility() == 0) {
                    z = false;
                }
                showPatternsView(num, z);
            }
        }
    }

    public /* synthetic */ void lambda$createView$3$WallpaperActivity(int offsetX, int offsetY) {
        float progress;
        if (this.isMotion) {
            if (this.motionAnimation != null) {
                progress = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            } else {
                progress = 1.0f;
            }
            this.backgroundImage.setTranslationX(((float) offsetX) * progress);
            this.backgroundImage.setTranslationY(((float) offsetY) * progress);
        }
    }

    public /* synthetic */ void lambda$createView$4$WallpaperActivity(int num, View v) {
        if (num == 0) {
            setBackgroundColor(this.previousBackgroundColor);
        } else {
            TLRPC.TL_wallPaper tL_wallPaper = this.previousSelectedPattern;
            this.selectedPattern = tL_wallPaper;
            if (tL_wallPaper == null) {
                this.backgroundImage.setImageDrawable((Drawable) null);
            } else {
                this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", this.selectedPattern.document.size, 1, this.selectedPattern);
            }
            this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
            float f = this.previousIntensity;
            this.currentIntensity = f;
            this.intensitySeekBar.setProgress(f);
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            updateButtonState(this.radialProgress, (Object) null, this, false, true);
            updateSelectedPattern(true);
        }
        showPatternsView(num, false);
    }

    public /* synthetic */ void lambda$createView$5$WallpaperActivity(int num, View v) {
        showPatternsView(num, false);
    }

    public /* synthetic */ void lambda$createView$6$WallpaperActivity(View view, int position) {
        boolean z = false;
        boolean previousMotion = this.selectedPattern != null;
        if (position == 0) {
            this.backgroundImage.setImageDrawable((Drawable) null);
            this.selectedPattern = null;
            this.isMotion = false;
            updateButtonState(this.radialProgress, (Object) null, this, false, true);
        } else {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(position - 1);
            this.backgroundImage.setImage(ImageLocation.getForDocument(wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", wallPaper.document.size, 1, wallPaper);
            this.selectedPattern = wallPaper;
            this.isMotion = this.checkBoxView[2].isChecked();
            updateButtonState(this.radialProgress, (Object) null, this, false, true);
        }
        if (previousMotion == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        CheckBoxView checkBoxView2 = this.checkBoxView[1];
        if (this.selectedPattern != null) {
            z = true;
        }
        checkBoxView2.setChecked(z, true);
        this.patternsListView.invalidateViews();
    }

    public /* synthetic */ void lambda$createView$7$WallpaperActivity(float progress) {
        this.currentIntensity = progress;
        this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
        this.backgroundImage.invalidate();
        this.patternsListView.invalidateViews();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (fileWallpaper.id == -1) {
                    fileWallpaper.id = args[0].longValue();
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(this.radialProgress, (Object) null, this, true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, this.progressVisible);
        updateButtonState(this.radialProgress, (Object) null, this, false, true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, this.progressVisible);
        if (this.radialProgress.getIcon() != 10) {
            updateButtonState(this.radialProgress, (Object) null, this, false, true);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.blurredBitmap = Utilities.blurWallpaper(bitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (this.isBlurred) {
            Bitmap bitmap2 = this.blurredBitmap;
            if (bitmap2 != null) {
                this.backgroundImage.setImageBitmap(bitmap2);
                return;
            }
            return;
        }
        setCurrentImage(false);
    }

    /* JADX WARNING: type inference failed for: r20v0, types: [im.bclpbkiauv.messenger.DownloadController$FileDownloadProgressListener] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateButtonState(im.bclpbkiauv.ui.components.RadialProgress2 r18, java.lang.Object r19, im.bclpbkiauv.messenger.DownloadController.FileDownloadProgressListener r20, boolean r21, boolean r22) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r20
            r3 = r21
            r4 = r22
            if (r2 != r0) goto L_0x0016
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r5 = r0.selectedPattern
            if (r5 == 0) goto L_0x0013
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r5 = r0.selectedPattern
            goto L_0x0018
        L_0x0013:
            java.lang.Object r5 = r0.currentWallpaper
            goto L_0x0018
        L_0x0016:
            r5 = r19
        L_0x0018:
            boolean r6 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper
            r7 = 4
            r8 = 6
            if (r6 != 0) goto L_0x002c
            boolean r6 = r5 instanceof im.bclpbkiauv.messenger.MediaController.SearchImage
            if (r6 == 0) goto L_0x0023
            goto L_0x002c
        L_0x0023:
            if (r2 != r0) goto L_0x0026
            goto L_0x0027
        L_0x0026:
            r7 = 6
        L_0x0027:
            r1.setIcon(r7, r3, r4)
            goto L_0x012d
        L_0x002c:
            if (r19 != 0) goto L_0x0035
            if (r4 == 0) goto L_0x0035
            boolean r6 = r0.progressVisible
            if (r6 != 0) goto L_0x0035
            r4 = 0
        L_0x0035:
            boolean r6 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper
            r9 = 1
            if (r6 == 0) goto L_0x0055
            r6 = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper) r6
            im.bclpbkiauv.tgnet.TLRPC$Document r10 = r6.document
            java.lang.String r10 = im.bclpbkiauv.messenger.FileLoader.getAttachFileName(r10)
            boolean r11 = android.text.TextUtils.isEmpty(r10)
            if (r11 == 0) goto L_0x004a
            return
        L_0x004a:
            im.bclpbkiauv.tgnet.TLRPC$Document r11 = r6.document
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r11, r9)
            im.bclpbkiauv.tgnet.TLRPC$Document r11 = r6.document
            int r6 = r11.size
            goto L_0x008d
        L_0x0055:
            r6 = r5
            im.bclpbkiauv.messenger.MediaController$SearchImage r6 = (im.bclpbkiauv.messenger.MediaController.SearchImage) r6
            im.bclpbkiauv.tgnet.TLRPC$Photo r10 = r6.photo
            if (r10 == 0) goto L_0x0071
            im.bclpbkiauv.tgnet.TLRPC$Photo r10 = r6.photo
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r10 = r10.sizes
            int r11 = r0.maxWallpaperSize
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r10 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r11, r9)
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r10, r9)
            java.lang.String r11 = im.bclpbkiauv.messenger.FileLoader.getAttachFileName(r10)
            int r10 = r10.size
            goto L_0x0084
        L_0x0071:
            java.lang.String r9 = r6.imageUrl
            java.lang.String r10 = "jpg"
            java.io.File r9 = im.bclpbkiauv.messenger.ImageLoader.getHttpFilePath(r9, r10)
            java.lang.String r10 = r9.getName()
            int r11 = r6.size
            r16 = r11
            r11 = r10
            r10 = r16
        L_0x0084:
            boolean r12 = android.text.TextUtils.isEmpty(r11)
            if (r12 == 0) goto L_0x008b
            return
        L_0x008b:
            r6 = r10
            r10 = r11
        L_0x008d:
            boolean r11 = r9.exists()
            r12 = r11
            r13 = 0
            r14 = 1065353216(0x3f800000, float:1.0)
            if (r11 == 0) goto L_0x00c4
            int r11 = r0.currentAccount
            im.bclpbkiauv.messenger.DownloadController r11 = im.bclpbkiauv.messenger.DownloadController.getInstance(r11)
            r11.removeLoadingFileObserver(r2)
            r1.setProgress(r14, r4)
            if (r19 != 0) goto L_0x00a6
            goto L_0x00a7
        L_0x00a6:
            r7 = 6
        L_0x00a7:
            r1.setIcon(r7, r3, r4)
            if (r19 != 0) goto L_0x0107
            im.bclpbkiauv.ui.components.BackupImageView r7 = r0.backgroundImage
            r7.invalidate()
            if (r6 == 0) goto L_0x00be
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            long r14 = (long) r6
            java.lang.String r8 = im.bclpbkiauv.messenger.AndroidUtilities.formatFileSize(r14)
            r7.setSubtitle(r8)
            goto L_0x0107
        L_0x00be:
            im.bclpbkiauv.ui.actionbar.ActionBar r7 = r0.actionBar
            r7.setSubtitle(r13)
            goto L_0x0107
        L_0x00c4:
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.DownloadController r7 = im.bclpbkiauv.messenger.DownloadController.getInstance(r7)
            r7.addLoadingFileObserver(r10, r13, r2)
            int r7 = r0.currentAccount
            im.bclpbkiauv.messenger.FileLoader r7 = im.bclpbkiauv.messenger.FileLoader.getInstance(r7)
            boolean r7 = r7.isLoadingFile(r10)
            im.bclpbkiauv.messenger.ImageLoader r8 = im.bclpbkiauv.messenger.ImageLoader.getInstance()
            java.lang.Float r8 = r8.getFileProgress(r10)
            if (r8 == 0) goto L_0x00e9
            float r11 = r8.floatValue()
            r1.setProgress(r11, r4)
            goto L_0x00ed
        L_0x00e9:
            r11 = 0
            r1.setProgress(r11, r4)
        L_0x00ed:
            r11 = 10
            r1.setIcon(r11, r3, r4)
            if (r19 != 0) goto L_0x0107
            im.bclpbkiauv.ui.actionbar.ActionBar r11 = r0.actionBar
            r13 = 2131691843(0x7f0f0943, float:1.901277E38)
            java.lang.String r14 = "LoadingFullImage"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r13)
            r11.setSubtitle(r13)
            im.bclpbkiauv.ui.components.BackupImageView r11 = r0.backgroundImage
            r11.invalidate()
        L_0x0107:
            if (r19 != 0) goto L_0x012c
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r7 = r0.selectedPattern
            r8 = 1056964608(0x3f000000, float:0.5)
            if (r7 != 0) goto L_0x011b
            android.widget.FrameLayout r7 = r0.buttonsContainer
            if (r12 == 0) goto L_0x0116
            r11 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0118
        L_0x0116:
            r11 = 1056964608(0x3f000000, float:0.5)
        L_0x0118:
            r7.setAlpha(r11)
        L_0x011b:
            android.widget.FrameLayout r7 = r0.bottomOverlayChat
            r7.setEnabled(r12)
            android.widget.TextView r7 = r0.bottomOverlayChatText
            if (r12 == 0) goto L_0x0127
            r14 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0129
        L_0x0127:
            r14 = 1056964608(0x3f000000, float:0.5)
        L_0x0129:
            r7.setAlpha(r14)
        L_0x012c:
        L_0x012d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.WallpaperActivity.updateButtonState(im.bclpbkiauv.ui.components.RadialProgress2, java.lang.Object, im.bclpbkiauv.messenger.DownloadController$FileDownloadProgressListener, boolean, boolean):void");
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        Object obj = this.currentWallpaper;
        if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper wallPaper = (WallpapersListActivity.ColorWallpaper) obj;
            if (wallPaper.patternId != 0) {
                int a = 0;
                int N = this.patterns.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_wallPaper pattern = (TLRPC.TL_wallPaper) this.patterns.get(a);
                    if (pattern.id == wallPaper.patternId) {
                        this.selectedPattern = pattern;
                        break;
                    }
                    a++;
                }
                this.currentIntensity = wallPaper.intensity;
            }
        }
    }

    private void updateSelectedPattern(boolean animated) {
        int count = this.patternsListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.patternsListView.getChildAt(a);
            if (child instanceof PatternCell) {
                ((PatternCell) child).updateSelected(animated);
            }
        }
    }

    private void updateMotionButton() {
        this.checkBoxView[this.selectedPattern != null ? (char) 2 : 0].setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        CheckBoxView checkBoxView2 = this.checkBoxView[2];
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(checkBoxView2, property, fArr);
        CheckBoxView checkBoxView3 = this.checkBoxView[0];
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        if (this.selectedPattern != null) {
            f = 0.0f;
        }
        fArr2[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(checkBoxView3, property2, fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                WallpaperActivity.this.checkBoxView[WallpaperActivity.this.selectedPattern != null ? (char) 0 : 2].setVisibility(4);
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void showPatternsView(int num, boolean show) {
        int index;
        int i = num;
        boolean showMotion = show && i == 1 && this.selectedPattern != null;
        if (show) {
            if (i == 0) {
                int i2 = this.backgroundColor;
                this.previousBackgroundColor = i2;
                this.colorPicker.setColor(i2);
            } else {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                ArrayList<Object> arrayList = this.patterns;
                if (arrayList != null) {
                    TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
                    if (tL_wallPaper == null) {
                        index = 0;
                    } else {
                        index = arrayList.indexOf(tL_wallPaper) + 1;
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(index, ((this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(100.0f)) - AndroidUtilities.dp(12.0f)) / 2);
                }
            }
        }
        this.checkBoxView[showMotion ? (char) 2 : 0].setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList2 = new ArrayList();
        int otherNum = i == 0 ? 1 : 0;
        float f = 1.0f;
        if (show) {
            this.patternLayout[i].setVisibility(0);
            arrayList2.add(ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
            arrayList2.add(ObjectAnimator.ofFloat(this.buttonsContainer, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
            CheckBoxView checkBoxView2 = this.checkBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = showMotion ? 1.0f : 0.0f;
            arrayList2.add(ObjectAnimator.ofFloat(checkBoxView2, property, fArr));
            CheckBoxView checkBoxView3 = this.checkBoxView[0];
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (showMotion) {
                f = 0.0f;
            }
            fArr2[0] = f;
            arrayList2.add(ObjectAnimator.ofFloat(checkBoxView3, property2, fArr2));
            arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
            if (this.patternLayout[otherNum].getVisibility() == 0) {
                arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, new float[]{0.0f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.ALPHA, new float[]{0.0f, 1.0f}));
                this.patternLayout[i].setTranslationY(0.0f);
            } else {
                arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[i].getMeasuredHeight(), 0.0f}));
            }
        } else {
            arrayList2.add(ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{0.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.buttonsContainer, View.TRANSLATION_Y, new float[]{0.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[i].getMeasuredHeight()}));
            arrayList2.add(ObjectAnimator.ofFloat(this.checkBoxView[0], View.ALPHA, new float[]{1.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.checkBoxView[2], View.ALPHA, new float[]{0.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{1.0f}));
        }
        animatorSet.playTogether(arrayList2);
        final boolean z = show;
        final int i3 = otherNum;
        final int i4 = num;
        final boolean z2 = showMotion;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (z && WallpaperActivity.this.patternLayout[i3].getVisibility() == 0) {
                    WallpaperActivity.this.patternLayout[i3].setAlpha(1.0f);
                    WallpaperActivity.this.patternLayout[i3].setVisibility(4);
                } else if (!z) {
                    WallpaperActivity.this.patternLayout[i4].setVisibility(4);
                }
                WallpaperActivity.this.checkBoxView[z2 ? (char) 0 : 2].setVisibility(4);
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.motionAnimation = animatorSet2;
        if (this.isMotion) {
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale})});
        } else {
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f})});
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AnimatorSet unused = WallpaperActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    /* access modifiers changed from: private */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        this.backgroundImage.setBackgroundColor(color);
        CheckBoxView[] checkBoxViewArr = this.checkBoxView;
        if (checkBoxViewArr[0] != null) {
            checkBoxViewArr[0].invalidate();
        }
        int patternColor2 = AndroidUtilities.getPatternColor(this.backgroundColor);
        this.patternColor = patternColor2;
        Theme.applyChatServiceMessageColor(new int[]{patternColor2, patternColor2, patternColor2, patternColor2});
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            this.backgroundImage.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout = this.buttonsContainer;
        if (frameLayout != null) {
            int N = frameLayout.getChildCount();
            for (int a = 0; a < N; a++) {
                this.buttonsContainer.getChildAt(a).invalidate();
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
        }
    }

    private void setCurrentImage(boolean setThumb) {
        Object obj = this.currentWallpaper;
        TLRPC.PhotoSize thumb = null;
        if (obj instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) obj;
            if (setThumb) {
                thumb = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100);
            }
            this.backgroundImage.setImage(ImageLocation.getForDocument(wallPaper.document), this.imageFilter, ImageLocation.getForDocument(thumb, wallPaper.document), "100_100_b", "jpg", wallPaper.document.size, 1, wallPaper);
        } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            setBackgroundColor(((WallpapersListActivity.ColorWallpaper) obj).color);
            TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
            if (tL_wallPaper != null) {
                this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", this.selectedPattern.document.size, 1, this.selectedPattern);
            }
        } else if (obj instanceof WallpapersListActivity.FileWallpaper) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.backgroundImage.setImageBitmap(bitmap);
                return;
            }
            WallpapersListActivity.FileWallpaper wallPaper2 = (WallpapersListActivity.FileWallpaper) obj;
            if (wallPaper2.originalPath != null) {
                this.backgroundImage.setImage(wallPaper2.originalPath.getAbsolutePath(), this.imageFilter, (Drawable) null);
            } else if (wallPaper2.path != null) {
                this.backgroundImage.setImage(wallPaper2.path.getAbsolutePath(), this.imageFilter, (Drawable) null);
            } else if (((long) wallPaper2.resId) == -2) {
                this.backgroundImage.setImageDrawable(Theme.getThemedWallpaper(false));
            } else if (wallPaper2.resId != 0) {
                this.backgroundImage.setImageResource(wallPaper2.resId);
            }
        } else if (obj instanceof MediaController.SearchImage) {
            MediaController.SearchImage wallPaper3 = (MediaController.SearchImage) obj;
            if (wallPaper3.photo != null) {
                TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(wallPaper3.photo.sizes, 100);
                TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(wallPaper3.photo.sizes, this.maxWallpaperSize, true);
                if (image == thumb2) {
                    image = null;
                }
                this.backgroundImage.setImage(ImageLocation.getForPhoto(image, wallPaper3.photo), this.imageFilter, ImageLocation.getForPhoto(thumb2, wallPaper3.photo), "100_100_b", "jpg", image != null ? image.size : 0, 1, wallPaper3);
                return;
            }
            this.backgroundImage.setImage(wallPaper3.imageUrl, this.imageFilter, wallPaper3.thumbUrl, "100_100_b");
        }
    }

    private class PatternsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        public int getItemCount() {
            return (WallpaperActivity.this.patterns != null ? WallpaperActivity.this.patterns.size() : 0) + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new PatternCell(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PatternCell view = (PatternCell) holder.itemView;
            if (position == 0) {
                view.setPattern((TLRPC.TL_wallPaper) null);
            } else {
                view.setPattern((TLRPC.TL_wallPaper) WallpaperActivity.this.patterns.get(position - 1));
            }
            view.getImageReceiver().setColorFilter(new PorterDuffColorFilter(WallpaperActivity.this.patternColor, WallpaperActivity.this.blendMode));
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages = new ArrayList<>();

        public ListAdapter(Context context) {
            this.mContext = context;
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            TLRPC.Message message = new TLRPC.TL_message();
            if (WallpaperActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", R.string.BackgroundColorSinglePreviewLine2);
            } else {
                message.message = LocaleController.getString("BackgroundPreviewLine2", R.string.BackgroundPreviewLine2);
            }
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TLRPC.TL_messageMediaEmpty();
            message.out = true;
            message.to_id = new TLRPC.TL_peerUser();
            message.to_id.user_id = 0;
            MessageObject messageObject = new MessageObject(WallpaperActivity.this.currentAccount, message, true);
            messageObject.eventId = 1;
            messageObject.resetLayout();
            this.messages.add(messageObject);
            TLRPC.Message message2 = new TLRPC.TL_message();
            if (WallpaperActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", R.string.BackgroundColorSinglePreviewLine1);
            } else {
                message2.message = LocaleController.getString("BackgroundPreviewLine1", R.string.BackgroundPreviewLine1);
            }
            message2.date = date + 60;
            message2.dialog_id = 1;
            message2.flags = 265;
            message2.from_id = 0;
            message2.id = 1;
            message2.reply_to_msg_id = 5;
            message2.media = new TLRPC.TL_messageMediaEmpty();
            message2.out = false;
            message2.to_id = new TLRPC.TL_peerUser();
            message2.to_id.user_id = UserConfig.getInstance(WallpaperActivity.this.currentAccount).getClientUserId();
            MessageObject messageObject2 = new MessageObject(WallpaperActivity.this.currentAccount, message2, true);
            messageObject2.eventId = 1;
            messageObject2.resetLayout();
            this.messages.add(messageObject2);
            TLRPC.Message message3 = new TLRPC.TL_message();
            message3.message = LocaleController.formatDateChat((long) date);
            message3.id = 0;
            message3.date = date;
            MessageObject messageObject3 = new MessageObject(WallpaperActivity.this.currentAccount, message3, false);
            messageObject3.type = 10;
            messageObject3.contentType = 1;
            messageObject3.isDateObject = true;
            this.messages.add(messageObject3);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return this.messages.size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new ChatMessageCell(this.mContext);
                ((ChatMessageCell) view).setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                    public /* synthetic */ boolean canPerformActions() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                    }

                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
                    }

                    public /* synthetic */ void didPressRedpkgTransfer(ChatMessageCell chatMessageCell, MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressRedpkgTransfer(this, chatMessageCell, messageObject);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressShare(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressSysNotifyVideoFullPlayer(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSysNotifyVideoFullPlayer(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                    }

                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell, str);
                    }

                    public /* synthetic */ void didPressVoteButton(ChatMessageCell chatMessageCell, TLRPC.TL_pollAnswer tL_pollAnswer) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(int i) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, i);
                    }

                    public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                    }

                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }
                });
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new ChatActionCell.ChatActionCellDelegate() {
                    public /* synthetic */ void didClickImage(ChatActionCell chatActionCell) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didClickImage(this, chatActionCell);
                    }

                    public /* synthetic */ void didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didLongPress(this, chatActionCell, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didPressBotButton(this, messageObject, keyboardButton);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatActionCell chatActionCell, int i) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didPressReplyMessage(this, chatActionCell, i);
                    }

                    public /* synthetic */ void didRedUrl(MessageObject messageObject) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didRedUrl(this, messageObject);
                    }

                    public /* synthetic */ void needOpenUserProfile(int i) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenUserProfile(this, i);
                    }
                });
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return this.messages.get(i).contentType;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean pinnedBotton;
            MessageObject message = this.messages.get(position);
            View view = holder.itemView;
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                boolean pinnedTop = false;
                messageCell.isChat = false;
                int nextType = getItemViewType(position - 1);
                int prevType = getItemViewType(position + 1);
                if ((message.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                    pinnedBotton = false;
                } else {
                    MessageObject nextMessage = this.messages.get(position - 1);
                    pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                }
                if (prevType == holder.getItemViewType()) {
                    MessageObject prevMessage = this.messages.get(position + 1);
                    if (!(prevMessage.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300) {
                        pinnedTop = true;
                    }
                } else {
                    pinnedTop = false;
                }
                messageCell.setFullyDraw(true);
                messageCell.setMessageObject(message, (MessageObject.GroupedMessages) null, pinnedBotton, pinnedTop);
            } else if (view instanceof ChatActionCell) {
                ChatActionCell actionCell = (ChatActionCell) view;
                actionCell.setMessageObject(message);
                actionCell.setAlpha(1.0f);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector));
        int a = 0;
        while (true) {
            FrameLayout[] frameLayoutArr = this.patternLayout;
            if (a >= frameLayoutArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(frameLayoutArr[a], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelShadow));
            arrayList.add(new ThemeDescription(this.patternLayout[a], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelBackground));
            a++;
        }
        int a2 = 0;
        while (true) {
            FrameLayout[] frameLayoutArr2 = this.patternsButtonsContainer;
            if (a2 >= frameLayoutArr2.length) {
                break;
            }
            arrayList.add(new ThemeDescription(frameLayoutArr2[a2], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelShadow));
            arrayList.add(new ThemeDescription(this.patternsButtonsContainer[a2], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelBackground));
            a2++;
        }
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelShadow));
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelBackground));
        arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_fieldOverlayText));
        int a3 = 0;
        while (true) {
            TextView[] textViewArr = this.patternsSaveButton;
            if (a3 >= textViewArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textViewArr[a3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_fieldOverlayText));
            a3++;
        }
        int a4 = 0;
        while (true) {
            TextView[] textViewArr2 = this.patternsCancelButton;
            if (a4 >= textViewArr2.length) {
                break;
            }
            arrayList.add(new ThemeDescription(textViewArr2[a4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_fieldOverlayText));
            a4++;
        }
        ColorPicker colorPicker2 = this.colorPicker;
        if (colorPicker2 != null) {
            colorPicker2.provideThemeDescriptions(arrayList);
        }
        arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
        arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
        arrayList.add(new ThemeDescription((View) this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextIn));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextOut));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckRead));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckReadSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeSelectedText));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
