package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class PhotoPickerPhotoCell extends FrameLayout {
    /* access modifiers changed from: private */
    public AnimatorSet animator;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Paint backgroundPaint = new Paint();
    public CheckBox checkBox;
    public FrameLayout checkFrame;
    public BackupImageView imageView;
    public int itemWidth;
    private MediaController.PhotoEntry photoEntry;
    public FrameLayout videoInfoContainer;
    public TextView videoTextView;
    private boolean zoomOnSelect;

    public PhotoPickerPhotoCell(Context context, boolean zoom) {
        super(context);
        setWillNotDraw(false);
        this.zoomOnSelect = zoom;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.checkFrame = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(42, 42, 53));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.videoInfoContainer = frameLayout2;
        frameLayout2.setBackgroundResource(R.drawable.phototime);
        this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
        ImageView imageView1 = new ImageView(context);
        imageView1.setImageResource(R.drawable.ic_video);
        this.videoInfoContainer.addView(imageView1, LayoutHelper.createFrame(-2, -2, 19));
        TextView textView = new TextView(context);
        this.videoTextView = textView;
        textView.setTextColor(-1);
        this.videoTextView.setTextSize(1, 12.0f);
        this.videoTextView.setImportantForAccessibility(2);
        this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 19, 18.0f, -0.7f, 0.0f, 0.0f));
        CheckBox checkBox2 = new CheckBox(context, R.drawable.checkbig);
        this.checkBox = checkBox2;
        checkBox2.setSize(zoom ? 30 : 26);
        this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkBox.setDrawBackground(true);
        this.checkBox.setColor(-10043398, -1);
        addView(this.checkBox, LayoutHelper.createFrame(zoom ? 30.0f : 26.0f, zoom ? 30.0f : 26.0f, 53, 0.0f, 4.0f, 4.0f, 0.0f));
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.itemWidth, 1073741824));
    }

    public void showCheck(boolean show) {
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
        FrameLayout frameLayout = this.videoInfoContainer;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, "alpha", fArr);
        CheckBox checkBox2 = this.checkBox;
        float[] fArr2 = new float[1];
        if (!show) {
            f = 0.0f;
        }
        fArr2[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(checkBox2, "alpha", fArr2);
        animatorSet4.playTogether(animatorArr);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PhotoPickerPhotoCell.this.animatorSet)) {
                    AnimatorSet unused = PhotoPickerPhotoCell.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
    }

    public void setNum(int num) {
        this.checkBox.setNum(num);
    }

    public void setImage(MediaController.PhotoEntry entry) {
        Drawable thumb = this.zoomOnSelect ? Theme.chat_attachEmptyDrawable : getResources().getDrawable(R.drawable.nophotos);
        this.photoEntry = entry;
        if (entry.thumbPath != null) {
            this.imageView.setImage(this.photoEntry.thumbPath, (String) null, thumb);
        } else if (this.photoEntry.path != null) {
            this.imageView.setOrientation(this.photoEntry.orientation, true);
            if (this.photoEntry.isVideo) {
                this.videoInfoContainer.setVisibility(0);
                int minutes = this.photoEntry.duration / 60;
                this.videoTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(this.photoEntry.duration - (minutes * 60))}));
                setContentDescription(LocaleController.getString("AttachVideo", R.string.AttachVideo) + ", " + LocaleController.formatCallDuration(this.photoEntry.duration));
                BackupImageView backupImageView = this.imageView;
                backupImageView.setImage("vthumb://" + this.photoEntry.imageId + LogUtils.COLON + this.photoEntry.path, (String) null, thumb);
                return;
            }
            this.videoInfoContainer.setVisibility(4);
            setContentDescription(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
            BackupImageView backupImageView2 = this.imageView;
            backupImageView2.setImage("thumb://" + this.photoEntry.imageId + LogUtils.COLON + this.photoEntry.path, (String) null, thumb);
        } else {
            this.imageView.setImageDrawable(thumb);
        }
    }

    public void setImage(MediaController.SearchImage searchImage) {
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
    }

    public void setChecked(int num, final boolean checked, boolean animated) {
        this.checkBox.setChecked(num, checked, animated);
        AnimatorSet animatorSet2 = this.animator;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animator = null;
        }
        if (this.zoomOnSelect) {
            float f = 0.85f;
            if (animated) {
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animator = animatorSet3;
                Animator[] animatorArr = new Animator[2];
                BackupImageView backupImageView = this.imageView;
                Property property = View.SCALE_X;
                float[] fArr = new float[1];
                fArr[0] = checked ? 0.85f : 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(backupImageView, property, fArr);
                BackupImageView backupImageView2 = this.imageView;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                if (!checked) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(backupImageView2, property2, fArr2);
                animatorSet3.playTogether(animatorArr);
                this.animator.setDuration(200);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animation)) {
                            AnimatorSet unused = PhotoPickerPhotoCell.this.animator = null;
                            if (!checked) {
                                PhotoPickerPhotoCell.this.setBackgroundColor(0);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (PhotoPickerPhotoCell.this.animator != null && PhotoPickerPhotoCell.this.animator.equals(animation)) {
                            AnimatorSet unused = PhotoPickerPhotoCell.this.animator = null;
                        }
                    }
                });
                this.animator.start();
                return;
            }
            this.imageView.setScaleX(checked ? 0.85f : 1.0f);
            BackupImageView backupImageView3 = this.imageView;
            if (!checked) {
                f = 1.0f;
            }
            backupImageView3.setScaleY(f);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        MediaController.PhotoEntry photoEntry2;
        if (this.zoomOnSelect) {
            if (this.checkBox.isChecked() || this.imageView.getScaleX() != 1.0f || !this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f || ((photoEntry2 = this.photoEntry) != null && PhotoViewer.isShowingImage(photoEntry2.path))) {
                this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_attachPhotoBackground));
                canvas.drawRect(0.0f, 0.0f, (float) this.imageView.getMeasuredWidth(), (float) this.imageView.getMeasuredHeight(), this.backgroundPaint);
            }
        }
    }
}
