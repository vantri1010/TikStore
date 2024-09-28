package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.CheckBox;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class AudioCell extends FrameLayout {
    private MediaController.AudioEntry audioEntry;
    private TextView authorTextView;
    private CheckBox checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private AudioCellDelegate delegate;
    private TextView genreTextView;
    private boolean needDivider;
    private ImageView playButton;
    private TextView timeTextView;
    private TextView titleTextView;

    public interface AudioCellDelegate {
        void startedPlayingAudio(MessageObject messageObject);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AudioCell(Context context) {
        super(context);
        Context context2 = context;
        ImageView imageView = new ImageView(context2);
        this.playButton = imageView;
        int i = 5;
        addView(imageView, LayoutHelper.createFrame(46.0f, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 13.0f, 13.0f, LocaleController.isRTL ? 13.0f : 0.0f, 0.0f));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AudioCell.this.lambda$new$0$AudioCell(view);
            }
        });
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setTextSize(1, 14.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLines(1);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.titleTextView, LayoutHelper.createFrame(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 7.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.genreTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.genreTextView.setTextSize(1, 14.0f);
        this.genreTextView.setLines(1);
        this.genreTextView.setMaxLines(1);
        this.genreTextView.setSingleLine(true);
        this.genreTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.genreTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.genreTextView, LayoutHelper.createFrame(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 28.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.authorTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.authorTextView.setTextSize(1, 14.0f);
        this.authorTextView.setLines(1);
        this.authorTextView.setMaxLines(1);
        this.authorTextView.setSingleLine(true);
        this.authorTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.authorTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.authorTextView, LayoutHelper.createFrame(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 44.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        TextView textView4 = new TextView(context2);
        this.timeTextView = textView4;
        textView4.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.timeTextView.setTextSize(1, 13.0f);
        this.timeTextView.setLines(1);
        this.timeTextView.setMaxLines(1);
        this.timeTextView.setSingleLine(true);
        this.timeTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.timeTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(this.timeTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 18.0f : 0.0f, 11.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
        CheckBox checkBox2 = new CheckBox(context2, R.drawable.round_check2);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(0);
        this.checkBox.setColor(Theme.getColor(Theme.key_musicPicker_checkbox), Theme.getColor(Theme.key_musicPicker_checkboxCheck));
        addView(this.checkBox, LayoutHelper.createFrame(22.0f, 22.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 18.0f : 0.0f, 39.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0$AudioCell(View v) {
        if (this.audioEntry == null) {
            return;
        }
        if (!MediaController.getInstance().isPlayingMessage(this.audioEntry.messageObject) || MediaController.getInstance().isMessagePaused()) {
            ArrayList<MessageObject> arrayList = new ArrayList<>();
            arrayList.add(this.audioEntry.messageObject);
            if (MediaController.getInstance().setPlaylist(arrayList, this.audioEntry.messageObject)) {
                setPlayDrawable(true);
                AudioCellDelegate audioCellDelegate = this.delegate;
                if (audioCellDelegate != null) {
                    audioCellDelegate.startedPlayingAudio(this.audioEntry.messageObject);
                    return;
                }
                return;
            }
            return;
        }
        MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.audioEntry.messageObject);
        setPlayDrawable(false);
    }

    private void setPlayDrawable(boolean play) {
        String str;
        int i;
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(46.0f), Theme.getColor(Theme.key_musicPicker_buttonBackground), Theme.getColor(Theme.key_musicPicker_buttonBackground));
        Drawable drawable = getResources().getDrawable(play ? R.drawable.audiosend_pause : R.drawable.audiosend_play);
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_musicPicker_buttonIcon), PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
        combinedDrawable.setCustomSize(AndroidUtilities.dp(46.0f), AndroidUtilities.dp(46.0f));
        this.playButton.setBackgroundDrawable(combinedDrawable);
        ImageView imageView = this.playButton;
        if (play) {
            i = R.string.AccActionPause;
            str = "AccActionPause";
        } else {
            i = R.string.AccActionPlay;
            str = "AccActionPlay";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
    }

    public ImageView getPlayButton() {
        return this.playButton;
    }

    public TextView getTitleTextView() {
        return this.titleTextView;
    }

    public TextView getGenreTextView() {
        return this.genreTextView;
    }

    public TextView getTimeTextView() {
        return this.timeTextView;
    }

    public TextView getAuthorTextView() {
        return this.authorTextView;
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setAudio(MediaController.AudioEntry entry, boolean divider, boolean checked) {
        this.audioEntry = entry;
        this.titleTextView.setText(entry.title);
        this.genreTextView.setText(this.audioEntry.genre);
        this.authorTextView.setText(this.audioEntry.author);
        boolean z = true;
        this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(this.audioEntry.duration / 60), Integer.valueOf(this.audioEntry.duration % 60)}));
        if (!MediaController.getInstance().isPlayingMessage(this.audioEntry.messageObject) || MediaController.getInstance().isMessagePaused()) {
            z = false;
        }
        setPlayDrawable(z);
        this.needDivider = divider;
        setWillNotDraw(!divider);
        this.checkBox.setChecked(checked, false);
    }

    public void setChecked(boolean value) {
        this.checkBox.setChecked(value, true);
    }

    public void setDelegate(AudioCellDelegate audioCellDelegate) {
        this.delegate = audioCellDelegate;
    }

    public MediaController.AudioEntry getAudioEntry() {
        return this.audioEntry;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
