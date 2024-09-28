package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class LocationCell extends FrameLayout {
    private TextView addressTextView;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needDivider;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public LocationCell(Context context) {
        super(context);
        Context context2 = context;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        backupImageView.setBackgroundResource(R.drawable.round_grey);
        this.imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), PorterDuff.Mode.MULTIPLY));
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 8.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.nameTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        int i2 = 16;
        addView(this.nameTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 16 : 72), 5.0f, (float) (LocaleController.isRTL ? 72 : 16), 0.0f));
        TextView textView2 = new TextView(context2);
        this.addressTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.addressTextView.setMaxLines(1);
        this.addressTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.addressTextView.setSingleLine(true);
        this.addressTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.addressTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 16 : 72), 30.0f, (float) (LocaleController.isRTL ? 72 : i2), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setLocation(TLRPC.TL_messageMediaVenue location, String icon, boolean divider) {
        this.needDivider = divider;
        this.nameTextView.setText(location.title);
        this.addressTextView.setText(location.address);
        this.imageView.setImage(icon, (String) null, (Drawable) null);
        setWillNotDraw(!divider);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
