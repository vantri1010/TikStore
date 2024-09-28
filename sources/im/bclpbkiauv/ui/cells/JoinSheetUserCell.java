package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import org.slf4j.Marker;

public class JoinSheetUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView imageView;
    private TextView nameTextView;
    private int[] result = new int[1];

    public JoinSheetUserCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54.0f, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), 1073741824));
    }

    public void setUser(TLRPC.User user) {
        this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.avatarDrawable.setInfo(user);
        this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
    }

    public void setCount(int count) {
        this.nameTextView.setText("");
        AvatarDrawable avatarDrawable2 = this.avatarDrawable;
        avatarDrawable2.setInfo(0, (String) null, (String) null, Marker.ANY_NON_NULL_MARKER + LocaleController.formatShortNumber(count, this.result));
        this.imageView.setImage((ImageLocation) null, "50_50", (Drawable) this.avatarDrawable, (Object) null);
    }
}
