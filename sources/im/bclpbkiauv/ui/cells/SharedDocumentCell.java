package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.LineProgressView;
import java.util.Date;

public class SharedDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private CheckBox2 checkBox;
    private int currentAccount;
    private TextView dateTextView;
    /* access modifiers changed from: private */
    public TextView extTextView;
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public ImageView placeholderImageView;
    private LineProgressView progressView;
    private ImageView statusImageView;
    /* access modifiers changed from: private */
    public BackupImageView thumbImageView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SharedDocumentCell(Context context) {
        super(context);
        Context context2 = context;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.TAG = DownloadController.getInstance(i).generateObserverTag();
        ImageView imageView = new ImageView(context2);
        this.placeholderImageView = imageView;
        int i2 = 5;
        addView(imageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.extTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_files_iconText));
        this.extTextView.setTextSize(1, 14.0f);
        this.extTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.extTextView.setLines(1);
        this.extTextView.setMaxLines(1);
        this.extTextView.setSingleLine(true);
        this.extTextView.setGravity(17);
        this.extTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.extTextView.setImportantForAccessibility(2);
        addView(this.extTextView, LayoutHelper.createFrame(32.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        AnonymousClass1 r2 = new BackupImageView(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float alpha;
                if (SharedDocumentCell.this.thumbImageView.getImageReceiver().hasBitmapImage()) {
                    alpha = 1.0f - SharedDocumentCell.this.thumbImageView.getImageReceiver().getCurrentAlpha();
                } else {
                    alpha = 1.0f;
                }
                SharedDocumentCell.this.extTextView.setAlpha(alpha);
                SharedDocumentCell.this.placeholderImageView.setAlpha(alpha);
                super.onDraw(canvas);
            }
        };
        this.thumbImageView = r2;
        r2.setRoundRadius(AndroidUtilities.dp(4.0f));
        addView(this.thumbImageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.nameTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(1, 14.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.statusImageView = imageView2;
        imageView2.setVisibility(4);
        this.statusImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sharedMedia_startStopLoadIcon), PorterDuff.Mode.MULTIPLY));
        addView(this.statusImageView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 35.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.dateTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.dateTextView.setTextSize(1, 14.0f);
        this.dateTextView.setLines(1);
        this.dateTextView.setMaxLines(1);
        this.dateTextView.setSingleLine(true);
        this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.dateTextView, LayoutHelper.createFrame(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        LineProgressView lineProgressView = new LineProgressView(context2);
        this.progressView = lineProgressView;
        lineProgressView.setProgressColor(Theme.getColor(Theme.key_sharedMedia_startStopLoadIcon));
        addView(this.progressView, LayoutHelper.createFrame(-1.0f, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context2, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        addView(this.checkBox, LayoutHelper.createFrame(24.0f, 24.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? 0.0f : 33.0f, 28.0f, LocaleController.isRTL ? 33.0f : 0.0f, 0.0f));
    }

    public void setTextAndValueAndTypeAndThumb(String text, String value, String type, String thumb, int resId) {
        this.nameTextView.setText(text);
        this.dateTextView.setText(value);
        if (type != null) {
            this.extTextView.setVisibility(0);
            this.extTextView.setText(type);
        } else {
            this.extTextView.setVisibility(4);
        }
        if (resId == 0) {
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(text, type, false));
            this.placeholderImageView.setVisibility(0);
        } else {
            this.placeholderImageView.setVisibility(4);
        }
        if (thumb == null && resId == 0) {
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            this.thumbImageView.setImageBitmap((Bitmap) null);
            this.thumbImageView.setVisibility(4);
            return;
        }
        if (thumb != null) {
            this.thumbImageView.setImage(thumb, "40_40", (Drawable) null);
        } else {
            Drawable drawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(40.0f), resId);
            Theme.setCombinedDrawableColor(drawable, Theme.getColor(Theme.key_files_folderIconBackground), false);
            Theme.setCombinedDrawableColor(drawable, Theme.getColor(Theme.key_files_folderIcon), true);
            this.thumbImageView.setImageDrawable(drawable);
        }
        this.thumbImageView.setVisibility(0);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.progressView.getVisibility() == 0) {
            updateFileExistIcon();
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    public void setDocument(MessageObject messageObject, boolean divider) {
        String name;
        TLRPC.PhotoSize bigthumb;
        MessageObject messageObject2 = messageObject;
        this.needDivider = divider;
        this.message = messageObject2;
        this.loaded = false;
        this.loading = false;
        TLRPC.Document document = messageObject.getDocument();
        String str = "";
        if (messageObject2 == null || document == null) {
            this.nameTextView.setText(str);
            this.extTextView.setText(str);
            this.dateTextView.setText(str);
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap((Bitmap) null);
        } else {
            String name2 = null;
            if (messageObject.isMusic()) {
                for (int a = 0; a < document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if ((attribute instanceof TLRPC.TL_documentAttributeAudio) && !((attribute.performer == null || attribute.performer.length() == 0) && (attribute.title == null || attribute.title.length() == 0))) {
                        name2 = messageObject.getMusicAuthor() + " - " + messageObject.getMusicTitle();
                    }
                }
            }
            String fileName = FileLoader.getDocumentFileName(document);
            if (name2 == null) {
                name = fileName;
            } else {
                name = name2;
            }
            this.nameTextView.setText(name);
            this.placeholderImageView.setVisibility(0);
            this.extTextView.setVisibility(0);
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(fileName, document.mime_type, false));
            TextView textView = this.extTextView;
            int lastIndexOf = fileName.lastIndexOf(46);
            int idx = lastIndexOf;
            if (lastIndexOf != -1) {
                str = fileName.substring(idx + 1).toLowerCase();
            }
            textView.setText(str);
            TLRPC.PhotoSize bigthumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 40);
            if (thumb == bigthumb2) {
                bigthumb = null;
            } else {
                bigthumb = bigthumb2;
            }
            if (thumb instanceof TLRPC.TL_photoSizeEmpty) {
                TLRPC.PhotoSize photoSize = thumb;
                String str2 = name;
            } else if (thumb == null) {
                TLRPC.PhotoSize photoSize2 = bigthumb;
                TLRPC.PhotoSize photoSize3 = thumb;
                String str3 = name;
            } else {
                this.thumbImageView.getImageReceiver().setNeedsQualityThumb(bigthumb == null);
                this.thumbImageView.getImageReceiver().setShouldGenerateQualityThumb(bigthumb == null);
                this.thumbImageView.setVisibility(0);
                TLRPC.PhotoSize photoSize4 = bigthumb;
                TLRPC.PhotoSize photoSize5 = thumb;
                String str4 = name;
                this.thumbImageView.setImage(ImageLocation.getForDocument(bigthumb, document), "40_40", ImageLocation.getForDocument(thumb, document), "40_40_b", (String) null, 0, 1, messageObject);
                long date = ((long) messageObject2.messageOwner.date) * 1000;
                this.dateTextView.setText(String.format("%s, %s", new Object[]{AndroidUtilities.formatFileSize((long) document.size), LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)))}));
            }
            this.thumbImageView.setVisibility(4);
            this.thumbImageView.setImageBitmap((Bitmap) null);
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            long date2 = ((long) messageObject2.messageOwner.date) * 1000;
            this.dateTextView.setText(String.format("%s, %s", new Object[]{AndroidUtilities.formatFileSize((long) document.size), LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date2)), LocaleController.getInstance().formatterDay.format(new Date(date2)))}));
        }
        setWillNotDraw(!this.needDivider);
        this.progressView.setProgress(0.0f, false);
        updateFileExistIcon();
    }

    public void updateFileExistIcon() {
        MessageObject messageObject = this.message;
        if (messageObject == null || messageObject.messageOwner.media == null) {
            this.loading = false;
            this.loaded = true;
            this.progressView.setVisibility(4);
            this.progressView.setProgress(0.0f, false);
            this.statusImageView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        this.loaded = false;
        if (this.message.attachPathExists || this.message.mediaExists) {
            this.statusImageView.setVisibility(4);
            this.progressView.setVisibility(4);
            this.dateTextView.setPadding(0, 0, 0, 0);
            this.loading = false;
            this.loaded = true;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            return;
        }
        String fileName = FileLoader.getAttachFileName(this.message.getDocument());
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.message, this);
        this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
        this.statusImageView.setVisibility(0);
        this.statusImageView.setImageResource(this.loading ? R.drawable.media_doc_pause : R.drawable.media_doc_load);
        this.dateTextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(14.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : 0, 0);
        if (this.loading) {
            this.progressView.setVisibility(0);
            Float progress = ImageLoader.getInstance().getFileProgress(fileName);
            if (progress == null) {
                progress = Float.valueOf(0.0f);
            }
            this.progressView.setProgress(progress.floatValue(), false);
            return;
        }
        this.progressView.setVisibility(4);
    }

    public MessageObject getMessage() {
        return this.message;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return this.loading;
    }

    public BackupImageView getImageView() {
        return this.thumbImageView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), 1073741824));
        setMeasuredDimension(getMeasuredWidth(), AndroidUtilities.dp(34.0f) + this.nameTextView.getMeasuredHeight() + (this.needDivider ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.nameTextView.getLineCount() > 1) {
            int y = this.nameTextView.getMeasuredHeight() - AndroidUtilities.dp(22.0f);
            TextView textView = this.dateTextView;
            textView.layout(textView.getLeft(), this.dateTextView.getTop() + y, this.dateTextView.getRight(), this.dateTextView.getBottom() + y);
            ImageView imageView = this.statusImageView;
            imageView.layout(imageView.getLeft(), this.statusImageView.getTop() + y, this.statusImageView.getRight(), this.statusImageView.getBottom() + y);
            LineProgressView lineProgressView = this.progressView;
            lineProgressView.layout(lineProgressView.getLeft(), (getMeasuredHeight() - this.progressView.getMeasuredHeight()) - (this.needDivider ? 1 : 0), this.progressView.getRight(), getMeasuredHeight() - (this.needDivider ? 1 : 0));
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onFailedDownload(String name, boolean canceled) {
        updateFileExistIcon();
    }

    public void onSuccessDownload(String name) {
        this.progressView.setProgress(1.0f, true);
        updateFileExistIcon();
    }

    public void onProgressDownload(String fileName, float progress) {
        if (this.progressView.getVisibility() != 0) {
            updateFileExistIcon();
        }
        this.progressView.setProgress(progress, true);
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.checkBox.isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }
}
