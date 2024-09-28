package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class PhotoAttachPhotoCell extends FrameLayout {
    private static Rect rect = new Rect();
    /* access modifiers changed from: private */
    public AnimatorSet animator;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Paint backgroundPaint = new Paint();
    private CheckBox2 checkBox;
    private FrameLayout checkFrame;
    private FrameLayout container;
    private PhotoAttachPhotoCellDelegate delegate;
    private BackupImageView imageView;
    private boolean isLast;
    private boolean isVertical;
    private int itemSize;
    private boolean itemSizeChanged;
    private boolean mblnNewStyle = false;
    private RelativeLayout mediaInfoContainer;
    private final ImageView mediaInfoDrawableRight;
    private boolean needCheckShow;
    private MediaController.PhotoEntry photoEntry;
    private boolean pressed;
    private MediaController.SearchImage searchEntry;
    private TextView videoTextView;
    private boolean zoomOnSelect = true;

    public interface PhotoAttachPhotoCellDelegate {
        void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PhotoAttachPhotoCell(Context context) {
        super(context);
        Context context2 = context;
        setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.container = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(80, 80.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        this.container.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass1 r4 = new RelativeLayout(context2) {
            private RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
            }
        };
        this.mediaInfoContainer = r4;
        r4.setWillNotDraw(false);
        this.mediaInfoContainer.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
        this.container.addView(this.mediaInfoContainer, LayoutHelper.createFrame(-2.0f, 17.0f, 83, 4.0f, 0.0f, 0.0f, 4.0f));
        ImageView imageView2 = new ImageView(context2);
        this.mediaInfoDrawableRight = imageView2;
        imageView2.setId(imageView2.hashCode());
        this.mediaInfoDrawableRight.setImageResource(R.drawable.play_mini_video);
        this.mediaInfoContainer.addView(this.mediaInfoDrawableRight, LayoutHelper.createRelative(-2, -2, 15));
        TextView textView = new TextView(context2);
        this.videoTextView = textView;
        textView.setTextColor(-1);
        this.videoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.mediaInfoContainer.addView(this.videoTextView, LayoutHelper.createRelative(-2.0f, -2.0f, 4, 0, 0, 0, 15, 1, this.mediaInfoDrawableRight.getId()));
        CheckBox2 checkBox2 = new CheckBox2(context2, 24);
        this.checkBox = checkBox2;
        checkBox2.setDrawBackgroundAsArc(7);
        this.checkBox.setColor(Theme.key_chat_attachCheckBoxBackground, Theme.key_chat_attachPhotoBackground, Theme.key_chat_attachCheckBoxCheck);
        addView(this.checkBox, LayoutHelper.createFrame(26.0f, 26.0f, 51, 52.0f, 4.0f, 0.0f, 0.0f));
        this.checkBox.setVisibility(0);
        setFocusable(true);
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.checkFrame = frameLayout2;
        addView(frameLayout2, LayoutHelper.createFrame(42.0f, 42.0f, 51, 38.0f, 0.0f, 0.0f, 0.0f));
        this.itemSize = AndroidUtilities.dp(80.0f);
    }

    public void setIsVertical(boolean value) {
        this.isVertical = value;
    }

    public void setItemSize(int size) {
        this.itemSize = size;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.container.getLayoutParams();
        int i = this.itemSize;
        layoutParams.height = i;
        layoutParams.width = i;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.checkFrame.getLayoutParams();
        layoutParams2.gravity = 53;
        layoutParams2.leftMargin = 0;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.checkBox.getLayoutParams();
        layoutParams3.gravity = 53;
        layoutParams3.leftMargin = 0;
        int dp = AndroidUtilities.dp(5.0f);
        layoutParams3.topMargin = dp;
        layoutParams3.rightMargin = dp;
        this.checkBox.setDrawBackgroundAsArc(6);
        this.itemSizeChanged = true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.itemSizeChanged) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), 1073741824));
            return;
        }
        int i = 0;
        if (this.isVertical) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824);
            if (!this.isLast) {
                i = 6;
            }
            super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i + 80)), 1073741824));
            return;
        }
        if (!this.isLast) {
            i = 6;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i + 80)), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824));
    }

    public MediaController.PhotoEntry getPhotoEntry() {
        return this.photoEntry;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public float getScale() {
        return this.container.getScaleX();
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    public FrameLayout getCheckFrame() {
        return this.checkFrame;
    }

    public View getVideoInfoContainer() {
        return this.mediaInfoContainer;
    }

    public void setPhotoEntry(MediaController.PhotoEntry entry, boolean needCheckShow2, boolean last) {
        boolean z = false;
        this.pressed = false;
        this.photoEntry = entry;
        this.isLast = last;
        if (entry.isVideo) {
            this.imageView.setOrientation(0, true);
            this.mediaInfoContainer.setVisibility(0);
            int minutes = this.photoEntry.duration / 60;
            int seconds = this.photoEntry.duration - (minutes * 60);
            if (minutes == 0 && seconds == 0) {
                seconds = 1;
            }
            this.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}));
        } else if (this.photoEntry.path.endsWith(".gif")) {
            this.mediaInfoContainer.setVisibility(0);
            this.mediaInfoDrawableRight.setVisibility(8);
            this.videoTextView.setText("GIF");
        } else {
            this.mediaInfoContainer.setVisibility(4);
        }
        if (this.photoEntry.thumbPath != null) {
            this.imageView.setImage(this.photoEntry.thumbPath, (String) null, Theme.chat_attachEmptyDrawable);
        } else if (this.photoEntry.path == null) {
            this.imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
        } else if (this.photoEntry.isVideo) {
            BackupImageView backupImageView = this.imageView;
            backupImageView.setImage("vthumb://" + this.photoEntry.imageId + LogUtils.COLON + this.photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
        } else {
            this.imageView.setOrientation(this.photoEntry.orientation, true);
            BackupImageView backupImageView2 = this.imageView;
            backupImageView2.setImage("thumb://" + this.photoEntry.imageId + LogUtils.COLON + this.photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
        }
        boolean showing = needCheckShow2 && PhotoViewer.isShowingImage(this.photoEntry.path);
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!showing) {
            z = true;
        }
        imageReceiver.setVisible(z, true);
        float f = 0.0f;
        float f2 = 1.0f;
        this.checkBox.setAlpha(showing ? 0.0f : 1.0f);
        RelativeLayout relativeLayout = this.mediaInfoContainer;
        if (!showing) {
            f = 1.0f;
        }
        relativeLayout.setAlpha(f);
        if (this.mblnNewStyle) {
            BackupImageView backupImageView3 = this.imageView;
            if (!this.checkBox.isChecked()) {
                f2 = 0.3f;
            }
            backupImageView3.setAlpha(f2);
        }
        requestLayout();
    }

    public void setPhotoEntry(MediaController.SearchImage searchImage, boolean needCheckShow2, boolean last) {
        boolean z = false;
        this.pressed = false;
        this.searchEntry = searchImage;
        this.isLast = last;
        Drawable thumb = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(R.drawable.nophotos);
        if (searchImage.thumbPhotoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(searchImage.thumbPhotoSize, searchImage.photo), (String) null, thumb, (Object) searchImage);
        } else if (searchImage.photoSize != null) {
            this.imageView.setImage(ImageLocation.getForPhoto(searchImage.photoSize, searchImage.photo), "80_80", thumb, (Object) searchImage);
        } else if (searchImage.thumbPath != null) {
            this.imageView.setImage(searchImage.thumbPath, (String) null, thumb);
        } else if (searchImage.thumbUrl != null && searchImage.thumbUrl.length() > 0) {
            this.imageView.setImage(searchImage.thumbUrl, (String) null, thumb);
        } else if (MessageObject.isDocumentHasThumb(searchImage.document)) {
            this.imageView.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(searchImage.document.thumbs, 320), searchImage.document), (String) null, thumb, (Object) searchImage);
        } else {
            this.imageView.setImageDrawable(thumb);
        }
        boolean showing = needCheckShow2 && PhotoViewer.isShowingImage(searchImage.getPathToAttach());
        ImageReceiver imageReceiver = this.imageView.getImageReceiver();
        if (!showing) {
            z = true;
        }
        imageReceiver.setVisible(z, true);
        float f = 0.0f;
        float f2 = 1.0f;
        this.checkBox.setAlpha(showing ? 0.0f : 1.0f);
        RelativeLayout relativeLayout = this.mediaInfoContainer;
        if (!showing) {
            f = 1.0f;
        }
        relativeLayout.setAlpha(f);
        if (this.mblnNewStyle) {
            BackupImageView backupImageView = this.imageView;
            if (!this.checkBox.isChecked()) {
                f2 = 0.3f;
            }
            backupImageView.setAlpha(f2);
        }
        requestLayout();
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public void setChecked(int num, final boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() == 0) {
            this.checkBox.setChecked(num, checked, animated);
            float f = 1.0f;
            if (this.itemSizeChanged) {
                float fScale = 0.787f;
                if (this.mblnNewStyle) {
                    fScale = 1.0f;
                }
                AnimatorSet animatorSet2 = this.animator;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animator = null;
                }
                if (animated) {
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.animator = animatorSet3;
                    Animator[] animatorArr = new Animator[2];
                    FrameLayout frameLayout = this.container;
                    Property property = View.SCALE_X;
                    float[] fArr = new float[1];
                    fArr[0] = checked ? fScale : 1.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
                    FrameLayout frameLayout2 = this.container;
                    Property property2 = View.SCALE_Y;
                    float[] fArr2 = new float[1];
                    fArr2[0] = checked ? fScale : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
                    animatorSet3.playTogether(animatorArr);
                    this.animator.setDuration(200);
                    this.animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (PhotoAttachPhotoCell.this.animator != null && PhotoAttachPhotoCell.this.animator.equals(animation)) {
                                AnimatorSet unused = PhotoAttachPhotoCell.this.animator = null;
                                if (!checked) {
                                    PhotoAttachPhotoCell.this.setBackgroundColor(0);
                                }
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (PhotoAttachPhotoCell.this.animator != null && PhotoAttachPhotoCell.this.animator.equals(animation)) {
                                AnimatorSet unused = PhotoAttachPhotoCell.this.animator = null;
                            }
                        }
                    });
                    this.animator.start();
                } else {
                    this.container.setScaleX(checked ? fScale : 1.0f);
                    this.container.setScaleY(checked ? fScale : 1.0f);
                }
            }
            if (this.mblnNewStyle) {
                BackupImageView backupImageView = this.imageView;
                if (!this.checkBox.isChecked()) {
                    f = 0.3f;
                }
                backupImageView.setAlpha(f);
            }
        }
    }

    public void setNum(int num) {
        this.checkBox.setNum(num);
    }

    public void setOnCheckClickLisnener(View.OnClickListener onCheckClickLisnener) {
        this.checkFrame.setOnClickListener(onCheckClickLisnener);
    }

    public void setDelegate(PhotoAttachPhotoCellDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void callDelegate() {
        this.delegate.onCheckClick(this);
    }

    public void showImage() {
        this.imageView.getImageReceiver().setVisible(true, true);
    }

    public void showCheck(boolean show) {
        if (!show || this.checkBox.getVisibility() != 8) {
            float f = 1.0f;
            if (this.checkBox.getAlpha() == 1.0f) {
                return;
            }
            if (show || this.checkBox.getAlpha() != 0.0f) {
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.setInterpolator(new DecelerateInterpolator());
                this.animatorSet.setDuration(180);
                AnimatorSet animatorSet4 = this.animatorSet;
                Animator[] animatorArr = new Animator[2];
                RelativeLayout relativeLayout = this.mediaInfoContainer;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(relativeLayout, property, fArr);
                CheckBox2 checkBox2 = this.checkBox;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(checkBox2, property2, fArr2);
                animatorSet4.playTogether(animatorArr);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PhotoAttachPhotoCell.this.animatorSet)) {
                            AnimatorSet unused = PhotoAttachPhotoCell.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
        }
    }

    public void clearAnimation() {
        super.clearAnimation();
        AnimatorSet animatorSet2 = this.animator;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animator = null;
            float f = 0.787f;
            this.container.setScaleX(this.checkBox.isChecked() ? 0.787f : 1.0f);
            FrameLayout frameLayout = this.container;
            if (!this.checkBox.isChecked()) {
                f = 1.0f;
            }
            frameLayout.setScaleY(f);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        this.checkFrame.getHitRect(rect);
        if (event.getAction() == 0) {
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                this.pressed = true;
                invalidate();
                result = true;
            }
        } else if (this.pressed) {
            if (event.getAction() == 1) {
                getParent().requestDisallowInterceptTouchEvent(true);
                this.pressed = false;
                playSoundEffect(0);
                sendAccessibilityEvent(1);
                this.delegate.onCheckClick(this);
                invalidate();
            } else if (event.getAction() == 3) {
                this.pressed = false;
                invalidate();
            } else if (event.getAction() == 2 && !rect.contains((int) event.getX(), (int) event.getY())) {
                this.pressed = false;
                invalidate();
            }
        }
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        MediaController.PhotoEntry photoEntry2;
        MediaController.SearchImage searchImage;
        if (this.checkBox.isChecked() || this.container.getScaleX() != 1.0f || !this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || (((photoEntry2 = this.photoEntry) != null && PhotoViewer.isShowingImage(photoEntry2.path)) || ((searchImage = this.searchEntry) != null && PhotoViewer.isShowingImage(searchImage.getPathToAttach())))) {
            this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_attachPhotoBackground));
            canvas.drawRect(0.0f, 0.0f, (float) this.imageView.getMeasuredWidth(), (float) this.imageView.getMeasuredHeight(), this.backgroundPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(true);
        MediaController.PhotoEntry photoEntry2 = this.photoEntry;
        if (photoEntry2 == null || !photoEntry2.isVideo) {
            info.setText(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
        } else {
            info.setText(LocaleController.getString("AttachVideo", R.string.AttachVideo) + ", " + LocaleController.formatCallDuration(this.photoEntry.duration));
        }
        if (this.checkBox.isChecked()) {
            info.setSelected(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.acc_action_open_photo, LocaleController.getString("Open", R.string.Open)));
        }
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (action == R.id.acc_action_open_photo) {
            View parent = (View) getParent();
            parent.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) getLeft(), (float) ((getTop() + getHeight()) - 1), 0));
            parent.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) getLeft(), (float) ((getTop() + getHeight()) - 1), 0));
        }
        return super.performAccessibilityAction(action, arguments);
    }

    public void setNewStyle(boolean blnNewStyle) {
        this.mblnNewStyle = blnNewStyle;
    }
}
