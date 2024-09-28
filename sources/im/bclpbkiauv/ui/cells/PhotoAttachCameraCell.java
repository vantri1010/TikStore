package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.io.File;

public class PhotoAttachCameraCell extends FrameLayout {
    private ImageView backgroundView;
    private ImageView imageView;
    private int itemSize = AndroidUtilities.dp(0.0f);

    public PhotoAttachCameraCell(Context context) {
        super(context);
        ImageView imageView2 = new ImageView(context);
        this.backgroundView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(this.backgroundView, LayoutHelper.createFrame(80, 80.0f));
        ImageView imageView3 = new ImageView(context);
        this.imageView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setImageResource(R.drawable.instant_camera);
        addView(this.imageView, LayoutHelper.createFrame(80, 80.0f));
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), 1073741824));
    }

    public void setItemSize(int size) {
        this.itemSize = size;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.imageView.getLayoutParams();
        int i = this.itemSize;
        layoutParams.height = i;
        layoutParams.width = i;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.backgroundView.getLayoutParams();
        int i2 = this.itemSize;
        layoutParams2.height = i2;
        layoutParams2.width = i2;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogCameraIcon), PorterDuff.Mode.MULTIPLY));
    }

    public void updateBitmap() {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg").getAbsolutePath());
        } catch (Throwable th) {
        }
        if (bitmap != null) {
            this.backgroundView.setImageBitmap(bitmap);
        } else {
            this.backgroundView.setImageResource(R.drawable.icplaceholder);
        }
    }
}
