package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class SendLocationCell extends FrameLayout {
    private SimpleTextView accurateTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private long dialogId;
    private ImageView imageView;
    /* access modifiers changed from: private */
    public Runnable invalidateRunnable = new Runnable() {
        public void run() {
            SendLocationCell.this.checkText();
            SendLocationCell sendLocationCell = SendLocationCell.this;
            sendLocationCell.invalidate(((int) sendLocationCell.rect.left) - 5, ((int) SendLocationCell.this.rect.top) - 5, ((int) SendLocationCell.this.rect.right) + 5, ((int) SendLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SendLocationCell.this.invalidateRunnable, 1000);
        }
    };
    private OnLiveStopListener onLiveStopListener;
    /* access modifiers changed from: private */
    public RectF rect;
    private SimpleTextView titleTextView;

    public interface OnLiveStopListener {
        void onStop();
    }

    public SendLocationCell(Context context, boolean live) {
        super(context);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setTag(live ? "location_sendLiveLocationBackgroundlocation_sendLiveLocationIcon" : "location_sendLocationBackgroundlocation_sendLocationIcon");
        int dp = AndroidUtilities.dp(40.0f);
        String str = Theme.key_location_sendLiveLocationBackground;
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(dp, Theme.getColor(live ? str : Theme.key_location_sendLocationBackground), Theme.getColor(!live ? Theme.key_location_sendLocationBackground : str));
        if (live) {
            this.rect = new RectF();
            Drawable drawable = getResources().getDrawable(R.drawable.livelocationpin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLiveLocationIcon), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
            setWillNotDraw(false);
        } else {
            Drawable drawable2 = getResources().getDrawable(R.drawable.pin);
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(circle, drawable2);
            combinedDrawable2.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            combinedDrawable2.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable2);
        }
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 13.0f, !LocaleController.isRTL ? 0.0f : 17.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        SimpleTextView simpleTextView2 = this.titleTextView;
        String str2 = Theme.key_windowBackgroundWhiteRedText2;
        simpleTextView2.setTag(live ? str2 : Theme.key_windowBackgroundWhiteBlueText7);
        this.titleTextView.setTextColor(Theme.getColor(!live ? Theme.key_windowBackgroundWhiteBlueText7 : str2));
        this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.titleTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.accurateTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.accurateTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.accurateTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.accurateTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    public void setHasLocation(boolean value) {
        if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null) {
            float f = 1.0f;
            this.titleTextView.setAlpha(value ? 1.0f : 0.5f);
            this.accurateTextView.setAlpha(value ? 1.0f : 0.5f);
            ImageView imageView2 = this.imageView;
            if (!value) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.rect != null) {
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
        }
    }

    public void setText(String title, String text) {
        this.titleTextView.setText(title);
        this.accurateTextView.setText(text);
    }

    public void setDialogId(long did) {
        this.dialogId = did;
        checkText();
    }

    /* access modifiers changed from: private */
    public void checkText() {
        LocationController.SharingLocationInfo info = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (info != null) {
            setText(LocaleController.getString("StopLiveLocation", R.string.StopLiveLocation), LocaleController.formatLocationUpdateDate((long) (info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date)));
            return;
        }
        setText(LocaleController.getString("SendLiveLocation", R.string.SendLiveLocation), LocaleController.getString("SendLiveLocationInfo", R.string.SendLiveLocationInfo));
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
        OnLiveStopListener onLiveStopListener2 = this.onLiveStopListener;
        if (onLiveStopListener2 != null) {
            onLiveStopListener2.onStop();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int currentTime;
        LocationController.SharingLocationInfo currentInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (currentInfo != null && currentInfo.stopTime >= (currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
            float progress = ((float) Math.abs(currentInfo.stopTime - currentTime)) / ((float) currentInfo.period);
            if (LocaleController.isRTL) {
                this.rect.set((float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(43.0f), (float) AndroidUtilities.dp(48.0f));
            } else {
                this.rect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f)), (float) AndroidUtilities.dp(18.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f)), (float) AndroidUtilities.dp(48.0f));
            }
            int color = Theme.getColor(Theme.key_location_liveLocationProgress);
            Theme.chat_radialProgress2Paint.setColor(color);
            Theme.chat_livePaint.setColor(color);
            canvas.drawArc(this.rect, -90.0f, progress * -360.0f, false, Theme.chat_radialProgress2Paint);
            String text = LocaleController.formatLocationLeftTime(Math.abs(currentInfo.stopTime - currentTime));
            canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) AndroidUtilities.dp(37.0f), Theme.chat_livePaint);
        }
    }

    public void setOnLiveStopListener(OnLiveStopListener listener) {
        this.onLiveStopListener = listener;
    }
}
