package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class AvatarDelCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarDelDelegate delegate;
    private ImageView ivDelete;
    private TextView tvName;

    public interface AvatarDelDelegate {
        void onClickDelete();
    }

    public void setDelegate(AvatarDelDelegate delegate2) {
        this.delegate = delegate2;
    }

    public AvatarDelCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public AvatarDelCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarDelCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(65.0f), 1073741824));
    }

    private void initLayout(Context context) {
        FrameLayout container = new FrameLayout(context);
        addView(container, LayoutHelper.createFrame(-2, -2, 1));
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setTextSize(AndroidUtilities.dp(16.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        container.addView(this.avatarImage, LayoutHelper.createFrame(45, 45, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(8.0f)));
        TextView textView = new TextView(context);
        this.tvName = textView;
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.tvName.setTextSize(1, 15.0f);
        this.tvName.setLines(1);
        this.tvName.setMaxLines(1);
        this.tvName.setSingleLine(true);
        this.tvName.setGravity(19);
        this.tvName.setEllipsize(TextUtils.TruncateAt.END);
        ImageView imageView = new ImageView(context);
        this.ivDelete = imageView;
        imageView.setImageResource(R.mipmap.icon_create_group_delete);
        this.ivDelete.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        container.addView(this.ivDelete, LayoutHelper.createFrame(32.0f, 32.0f, 5, 0.0f, (float) AndroidUtilities.dp(-2.0f), (float) AndroidUtilities.dp(-2.0f), 0.0f));
        this.ivDelete.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AvatarDelCell.this.lambda$initLayout$0$AvatarDelCell(view);
            }
        });
        setWillNotDraw(false);
    }

    public /* synthetic */ void lambda$initLayout$0$AvatarDelCell(View v) {
        AvatarDelDelegate avatarDelDelegate = this.delegate;
        if (avatarDelDelegate != null) {
            avatarDelDelegate.onClickDelete();
        }
    }

    public void setUser(TLRPC.User user) {
        if (user != null) {
            this.avatarDrawable.setInfo(user);
            this.tvName.setText(ContactsController.formatName(user.first_name, user.last_name));
            this.avatarImage.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
        }
    }
}
