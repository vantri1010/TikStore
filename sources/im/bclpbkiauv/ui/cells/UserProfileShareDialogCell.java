package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class UserProfileShareDialogCell extends LinearLayout {
    private Object data;
    private BackupImageView ivArrvar;
    private MryTextView tvGroupNumber;
    private MryTextView tvName;

    public UserProfileShareDialogCell(Context context) {
        this(context, (Object) null);
    }

    public UserProfileShareDialogCell(Context context, Object data2) {
        this(context, (AttributeSet) null, data2);
    }

    public UserProfileShareDialogCell(Context context, AttributeSet attrs, Object data2) {
        this(context, attrs, 0, data2);
    }

    public UserProfileShareDialogCell(Context context, AttributeSet attrs, int defStyleAttr, Object data2) {
        super(context, attrs, defStyleAttr);
        this.data = data2;
        init(context);
    }

    private void init(Context context) {
        BackupImageView backupImageView = new BackupImageView(context);
        this.ivArrvar = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        addView(this.ivArrvar, LayoutHelper.createFrame(45.0f, 45.0f, 16, 12.0f, 0.0f, 0.0f, 0.0f));
        MryTextView mryTextView = new MryTextView(context);
        this.tvName = mryTextView;
        mryTextView.setTextSize(14.0f);
        this.tvName.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        addView(this.tvName, LayoutHelper.createFrame(-1.0f, -2.0f, 16, 72.0f, 0.0f, 12.0f, 0.0f));
    }

    public void setData(Object data2) {
        this.data = data2;
    }
}
