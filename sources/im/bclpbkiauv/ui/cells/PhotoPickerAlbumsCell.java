package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;

public class PhotoPickerAlbumsCell extends FrameLayout {
    private MediaController.AlbumEntry[] albumEntries = new MediaController.AlbumEntry[4];
    private AlbumView[] albumViews = new AlbumView[4];
    private int albumsCount;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    private PhotoPickerAlbumsCellDelegate delegate;

    public interface PhotoPickerAlbumsCellDelegate {
        void didSelectAlbum(MediaController.AlbumEntry albumEntry);
    }

    private class AlbumView extends FrameLayout {
        /* access modifiers changed from: private */
        public TextView countTextView;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        /* access modifiers changed from: private */
        public TextView nameTextView;
        private View selector;
        final /* synthetic */ PhotoPickerAlbumsCell this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public AlbumView(im.bclpbkiauv.ui.cells.PhotoPickerAlbumsCell r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                r2 = r18
                r0.this$0 = r2
                r0.<init>(r1)
                im.bclpbkiauv.ui.components.BackupImageView r2 = new im.bclpbkiauv.ui.components.BackupImageView
                r2.<init>(r1)
                r0.imageView = r2
                r3 = -1082130432(0xffffffffbf800000, float:-1.0)
                r4 = -1
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r4, r3)
                r0.addView(r2, r5)
                android.widget.LinearLayout r2 = new android.widget.LinearLayout
                r2.<init>(r1)
                r5 = 0
                r2.setOrientation(r5)
                r6 = 2130706432(0x7f000000, float:1.7014118E38)
                r2.setBackgroundColor(r6)
                r6 = 28
                r7 = 83
                android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r7)
                r0.addView(r2, r6)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r1)
                r0.nameTextView = r6
                r7 = 1095761920(0x41500000, float:13.0)
                r8 = 1
                r6.setTextSize(r8, r7)
                android.widget.TextView r6 = r0.nameTextView
                r6.setTextColor(r4)
                android.widget.TextView r6 = r0.nameTextView
                r6.setSingleLine(r8)
                android.widget.TextView r6 = r0.nameTextView
                android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
                r6.setEllipsize(r9)
                android.widget.TextView r6 = r0.nameTextView
                r6.setMaxLines(r8)
                android.widget.TextView r6 = r0.nameTextView
                r9 = 16
                r6.setGravity(r9)
                android.widget.TextView r6 = r0.nameTextView
                r10 = 0
                r11 = -1
                r12 = 1065353216(0x3f800000, float:1.0)
                r13 = 8
                r14 = 0
                r15 = 0
                r16 = 0
                android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (float) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r2.addView(r6, r10)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r1)
                r0.countTextView = r6
                r6.setTextSize(r8, r7)
                android.widget.TextView r6 = r0.countTextView
                r7 = -5592406(0xffffffffffaaaaaa, float:NaN)
                r6.setTextColor(r7)
                android.widget.TextView r6 = r0.countTextView
                r6.setSingleLine(r8)
                android.widget.TextView r6 = r0.countTextView
                android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
                r6.setEllipsize(r7)
                android.widget.TextView r6 = r0.countTextView
                r6.setMaxLines(r8)
                android.widget.TextView r6 = r0.countTextView
                r6.setGravity(r9)
                android.widget.TextView r6 = r0.countTextView
                r7 = -2
                r8 = -1
                r9 = 1082130432(0x40800000, float:4.0)
                r10 = 0
                r11 = 1082130432(0x40800000, float:4.0)
                r12 = 0
                android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r7, (int) r8, (float) r9, (float) r10, (float) r11, (float) r12)
                r2.addView(r6, r7)
                android.view.View r6 = new android.view.View
                r6.<init>(r1)
                r0.selector = r6
                android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r5)
                r6.setBackgroundDrawable(r5)
                android.view.View r5 = r0.selector
                android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r4, r3)
                r0.addView(r5, r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.PhotoPickerAlbumsCell.AlbumView.<init>(im.bclpbkiauv.ui.cells.PhotoPickerAlbumsCell, android.content.Context):void");
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (!this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                this.this$0.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_attachPhotoBackground));
                canvas.drawRect(0.0f, 0.0f, (float) this.imageView.getMeasuredWidth(), (float) this.imageView.getMeasuredHeight(), this.this$0.backgroundPaint);
            }
        }
    }

    public PhotoPickerAlbumsCell(Context context) {
        super(context);
        for (int a = 0; a < 4; a++) {
            this.albumViews[a] = new AlbumView(this, context);
            addView(this.albumViews[a]);
            this.albumViews[a].setVisibility(4);
            this.albumViews[a].setTag(Integer.valueOf(a));
            this.albumViews[a].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoPickerAlbumsCell.this.lambda$new$0$PhotoPickerAlbumsCell(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$new$0$PhotoPickerAlbumsCell(View v) {
        PhotoPickerAlbumsCellDelegate photoPickerAlbumsCellDelegate = this.delegate;
        if (photoPickerAlbumsCellDelegate != null) {
            photoPickerAlbumsCellDelegate.didSelectAlbum(this.albumEntries[((Integer) v.getTag()).intValue()]);
        }
    }

    public void setAlbumsCount(int count) {
        int a = 0;
        while (true) {
            AlbumView[] albumViewArr = this.albumViews;
            if (a < albumViewArr.length) {
                albumViewArr[a].setVisibility(a < count ? 0 : 4);
                a++;
            } else {
                this.albumsCount = count;
                return;
            }
        }
    }

    public void setDelegate(PhotoPickerAlbumsCellDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setAlbum(int a, MediaController.AlbumEntry albumEntry) {
        this.albumEntries[a] = albumEntry;
        if (albumEntry != null) {
            AlbumView albumView = this.albumViews[a];
            albumView.imageView.setOrientation(0, true);
            if (albumEntry.coverPhoto == null || albumEntry.coverPhoto.path == null) {
                albumView.imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
            } else {
                albumView.imageView.setOrientation(albumEntry.coverPhoto.orientation, true);
                if (albumEntry.coverPhoto.isVideo) {
                    BackupImageView access$100 = albumView.imageView;
                    access$100.setImage("vthumb://" + albumEntry.coverPhoto.imageId + LogUtils.COLON + albumEntry.coverPhoto.path, (String) null, Theme.chat_attachEmptyDrawable);
                } else {
                    BackupImageView access$1002 = albumView.imageView;
                    access$1002.setImage("thumb://" + albumEntry.coverPhoto.imageId + LogUtils.COLON + albumEntry.coverPhoto.path, (String) null, Theme.chat_attachEmptyDrawable);
                }
            }
            albumView.nameTextView.setText(albumEntry.bucketName);
            albumView.countTextView.setText(String.format("%d", new Object[]{Integer.valueOf(albumEntry.photos.size())}));
            return;
        }
        this.albumViews[a].setVisibility(4);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int itemWidth;
        if (AndroidUtilities.isTablet()) {
            itemWidth = (AndroidUtilities.dp(490.0f) - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        } else {
            itemWidth = (AndroidUtilities.displaySize.x - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        }
        for (int a = 0; a < this.albumsCount; a++) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.albumViews[a].getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(4.0f) + itemWidth) * a;
            layoutParams.width = itemWidth;
            layoutParams.height = itemWidth;
            layoutParams.gravity = 51;
            this.albumViews[a].setLayoutParams(layoutParams);
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f) + itemWidth, 1073741824));
    }
}
