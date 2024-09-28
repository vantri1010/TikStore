package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.SeekBarView;

public class BrightnessControlCell extends FrameLayout {
    private ImageView leftImageView;
    private ImageView rightImageView;
    private SeekBarView seekBarView;

    public BrightnessControlCell(Context context) {
        super(context);
        ImageView imageView = new ImageView(context);
        this.leftImageView = imageView;
        imageView.setImageResource(R.drawable.brightness_low);
        addView(this.leftImageView, LayoutHelper.createFrame(24.0f, 24.0f, 51, 17.0f, 12.0f, 0.0f, 0.0f));
        AnonymousClass1 r0 = new SeekBarView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(event);
            }
        };
        this.seekBarView = r0;
        r0.setReportChanges(true);
        this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            public final void onSeekBarDrag(float f) {
                BrightnessControlCell.this.didChangedValue(f);
            }
        });
        addView(this.seekBarView, LayoutHelper.createFrame(-1.0f, 30.0f, 51, 58.0f, 9.0f, 58.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.rightImageView = imageView2;
        imageView2.setImageResource(R.drawable.brightness_high);
        addView(this.rightImageView, LayoutHelper.createFrame(24.0f, 24.0f, 53, 0.0f, 12.0f, 17.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.leftImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.rightImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), PorterDuff.Mode.MULTIPLY));
    }

    /* access modifiers changed from: protected */
    public void didChangedValue(float value) {
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
    }

    public void setProgress(float value) {
        this.seekBarView.setProgress(value);
    }
}
