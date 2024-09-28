package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.SnowflakesEffect;
import org.slf4j.Marker;

public class DrawerProfileCell extends FrameLayout {
    private boolean accountsShowed;
    private ImageView arrowView;
    private BackupImageView avatarImageView;
    private Integer currentColor;
    private Rect destRect = new Rect();
    private TextView nameTextView;
    private Paint paint = new Paint();
    private TextView phoneTextView;
    private ImageView shadowView;
    private SnowflakesEffect snowflakesEffect;
    private Rect srcRect = new Rect();

    public DrawerProfileCell(Context context) {
        super(context);
        String str;
        int i;
        ImageView imageView = new ImageView(context);
        this.shadowView = imageView;
        imageView.setVisibility(4);
        this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.shadowView.setImageResource(R.drawable.bottom_shadow);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(64.0f, 64.0f, 83, 16.0f, 0.0f, 0.0f, 67.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 16.0f, 0.0f, 76.0f, 28.0f));
        TextView textView2 = new TextView(context);
        this.phoneTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 83, 16.0f, 0.0f, 76.0f, 9.0f));
        ImageView imageView2 = new ImageView(context);
        this.arrowView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.arrowView;
        if (this.accountsShowed) {
            i = R.string.AccDescrHideAccounts;
            str = "AccDescrHideAccounts";
        } else {
            i = R.string.AccDescrShowAccounts;
            str = "AccDescrShowAccounts";
        }
        imageView3.setContentDescription(LocaleController.getString(str, i));
        addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
        if (Theme.getEventType() == 0) {
            this.snowflakesEffect = new SnowflakesEffect();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, 1073741824));
            return;
        }
        try {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), 1073741824));
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148.0f));
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int color;
        Canvas canvas2 = canvas;
        Drawable backgroundDrawable = Theme.getCachedWallpaper();
        String backgroundKey = applyBackground(false);
        boolean useImageBackground = !backgroundKey.equals(Theme.key_chats_menuTopBackground) && Theme.isCustomTheme() && !Theme.isPatternWallpaper() && backgroundDrawable != null && !(backgroundDrawable instanceof ColorDrawable);
        boolean drawCatsShadow = false;
        if (!useImageBackground && Theme.hasThemeKey(Theme.key_chats_menuTopShadowCats)) {
            color = Theme.getColor(Theme.key_chats_menuTopShadowCats);
            drawCatsShadow = true;
        } else if (Theme.hasThemeKey(Theme.key_chats_menuTopShadow)) {
            color = Theme.getColor(Theme.key_chats_menuTopShadow);
        } else {
            color = Theme.getServiceMessageColor() | -16777216;
        }
        Integer num = this.currentColor;
        if (num == null || num.intValue() != color) {
            this.currentColor = Integer.valueOf(color);
            this.shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_chats_menuName));
        if (useImageBackground) {
            this.phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhone));
            if (this.shadowView.getVisibility() != 0) {
                this.shadowView.setVisibility(0);
            }
            if (backgroundDrawable instanceof ColorDrawable) {
                boolean z = useImageBackground;
                int i = color;
            } else if (backgroundDrawable instanceof GradientDrawable) {
                String str = backgroundKey;
                boolean z2 = useImageBackground;
                int i2 = color;
            } else if (backgroundDrawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
                float scaleX = ((float) getMeasuredWidth()) / ((float) bitmap.getWidth());
                float scaleY = ((float) getMeasuredHeight()) / ((float) bitmap.getHeight());
                float scale = scaleX < scaleY ? scaleY : scaleX;
                int width = (int) (((float) getMeasuredWidth()) / scale);
                int height = (int) (((float) getMeasuredHeight()) / scale);
                int x = (bitmap.getWidth() - width) / 2;
                int y = (bitmap.getHeight() - height) / 2;
                String str2 = backgroundKey;
                boolean z3 = useImageBackground;
                this.srcRect.set(x, y, x + width, y + height);
                int i3 = color;
                this.destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                try {
                    canvas2.drawBitmap(bitmap, this.srcRect, this.destRect, this.paint);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            } else {
                boolean z4 = useImageBackground;
                int i4 = color;
            }
            backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            backgroundDrawable.draw(canvas2);
        } else {
            boolean z5 = useImageBackground;
            int i5 = color;
            int visibility = drawCatsShadow ? 0 : 4;
            if (this.shadowView.getVisibility() != visibility) {
                this.shadowView.setVisibility(visibility);
            }
            this.phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhoneCats));
            super.onDraw(canvas);
        }
        SnowflakesEffect snowflakesEffect2 = this.snowflakesEffect;
        if (snowflakesEffect2 != null) {
            snowflakesEffect2.onDraw(this, canvas2);
        }
    }

    public boolean isAccountsShowed() {
        return this.accountsShowed;
    }

    public void setAccountsShowed(boolean value) {
        if (this.accountsShowed != value) {
            this.accountsShowed = value;
            this.arrowView.setImageResource(value ? R.drawable.collapse_up : R.drawable.collapse_down);
        }
    }

    public void setOnArrowClickListener(View.OnClickListener onClickListener) {
        this.arrowView.setOnClickListener(new View.OnClickListener(onClickListener) {
            private final /* synthetic */ View.OnClickListener f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                DrawerProfileCell.this.lambda$setOnArrowClickListener$0$DrawerProfileCell(this.f$1, view);
            }
        });
    }

    public /* synthetic */ void lambda$setOnArrowClickListener$0$DrawerProfileCell(View.OnClickListener onClickListener, View v) {
        String str;
        int i;
        boolean z = !this.accountsShowed;
        this.accountsShowed = z;
        this.arrowView.setImageResource(z ? R.drawable.collapse_up : R.drawable.collapse_down);
        onClickListener.onClick(this);
        ImageView imageView = this.arrowView;
        if (this.accountsShowed) {
            i = R.string.AccDescrHideAccounts;
            str = "AccDescrHideAccounts";
        } else {
            i = R.string.AccDescrShowAccounts;
            str = "AccDescrShowAccounts";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
    }

    public void setUser(TLRPC.User user, boolean accounts) {
        if (user != null) {
            this.accountsShowed = accounts;
            this.arrowView.setImageResource(accounts ? R.drawable.collapse_up : R.drawable.collapse_down);
            this.nameTextView.setText(UserObject.getName(user));
            TextView textView = this.phoneTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            textView.setText(instance.format(Marker.ANY_NON_NULL_MARKER + user.phone));
            AvatarDrawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
            applyBackground(true);
        }
    }

    public String applyBackground(boolean force) {
        String currentTag = (String) getTag();
        String backgroundKey = Theme.key_chats_menuTopBackground;
        if (!Theme.hasThemeKey(backgroundKey) || Theme.getColor(backgroundKey) == 0) {
            backgroundKey = Theme.key_chats_menuTopBackgroundCats;
        }
        if (force || !backgroundKey.equals(currentTag)) {
            setBackgroundColor(Theme.getColor(backgroundKey));
            setTag(backgroundKey);
        }
        return backgroundKey;
    }
}
