package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class PhotoAttachPermissionCell extends FrameLayout {
    private ImageView imageView;
    private ImageView imageView2;
    private int itemSize = AndroidUtilities.dp(80.0f);
    private TextView textView;

    public PhotoAttachPermissionCell(Context context) {
        super(context);
        ImageView imageView3 = new ImageView(context);
        this.imageView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_attachPermissionImage), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(44.0f, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        ImageView imageView4 = new ImageView(context);
        this.imageView2 = imageView4;
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_attachPermissionMark), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView2, LayoutHelper.createFrame(44.0f, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_chat_attachPermissionText));
        this.textView.setTextSize(1, 12.0f);
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 5.0f, 13.0f, 5.0f, 0.0f));
    }

    public void setItemSize(int size) {
        this.itemSize = size;
    }

    public void setType(int type) {
        if (type == 0) {
            this.imageView.setImageResource(R.drawable.permissions_camera1);
            this.imageView2.setImageResource(R.drawable.permissions_camera2);
            this.textView.setText(LocaleController.getString("CameraPermissionText", R.string.CameraPermissionText));
            this.imageView.setLayoutParams(LayoutHelper.createFrame(44.0f, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
            this.imageView2.setLayoutParams(LayoutHelper.createFrame(44.0f, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
            return;
        }
        this.imageView.setImageResource(R.drawable.permissions_gallery1);
        this.imageView2.setImageResource(R.drawable.permissions_gallery2);
        this.textView.setText(LocaleController.getString("GalleryPermissionText", R.string.GalleryPermissionText));
        this.imageView.setLayoutParams(LayoutHelper.createFrame(44.0f, 44.0f, 17, 0.0f, 0.0f, 2.0f, 27.0f));
        this.imageView2.setLayoutParams(LayoutHelper.createFrame(44.0f, 44.0f, 17, 0.0f, 0.0f, 2.0f, 27.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), 1073741824));
    }
}
