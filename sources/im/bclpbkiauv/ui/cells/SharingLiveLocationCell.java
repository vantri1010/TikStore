package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.LocationActivity;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class SharingLiveLocationCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount;
    private LocationController.SharingLocationInfo currentInfo;
    private SimpleTextView distanceTextView;
    /* access modifiers changed from: private */
    public Runnable invalidateRunnable = new Runnable() {
        public void run() {
            SharingLiveLocationCell sharingLiveLocationCell = SharingLiveLocationCell.this;
            sharingLiveLocationCell.invalidate(((int) sharingLiveLocationCell.rect.left) - 5, ((int) SharingLiveLocationCell.this.rect.top) - 5, ((int) SharingLiveLocationCell.this.rect.right) + 5, ((int) SharingLiveLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000);
        }
    };
    private LocationActivity.LiveLocation liveLocation;
    private Location location = new Location("network");
    private SimpleTextView nameTextView;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();

    public SharingLiveLocationCell(Context context, boolean distance, int padding) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        this.avatarDrawable = new AvatarDrawable();
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i = 5;
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        if (distance) {
            addView(this.avatarImageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 13.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? (float) padding : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : (float) padding, 0.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.distanceTextView = simpleTextView2;
            simpleTextView2.setTextSize(14);
            this.distanceTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.distanceTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? (float) padding : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : (float) padding, 0.0f));
        } else {
            addView(this.avatarImageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 7.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? (float) padding : 74.0f, 17.0f, LocaleController.isRTL ? 74.0f : (float) padding, 0.0f));
        }
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f), 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    public void setDialog(long dialogId, TLRPC.TL_channelLocation chatLocation) {
        this.currentAccount = UserConfig.selectedAccount;
        String address = chatLocation.address;
        String name = "";
        this.avatarDrawable = null;
        int lowerId = (int) dialogId;
        if (lowerId > 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lowerId));
            if (user != null) {
                this.avatarDrawable = new AvatarDrawable(user);
                name = UserObject.getName(user);
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lowerId));
            if (chat != null) {
                this.avatarDrawable = new AvatarDrawable(chat);
                name = chat.title;
                this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
            }
        }
        this.nameTextView.setText(name);
        this.location.setLatitude(chatLocation.geo_point.lat);
        this.location.setLongitude(chatLocation.geo_point._long);
        this.distanceTextView.setText(address);
    }

    public void setDialog(MessageObject messageObject, Location userLocation) {
        String name;
        int fromId = messageObject.messageOwner.from_id;
        if (messageObject.isForwarded()) {
            if (messageObject.messageOwner.fwd_from.channel_id != 0) {
                fromId = -messageObject.messageOwner.fwd_from.channel_id;
            } else {
                fromId = messageObject.messageOwner.fwd_from.from_id;
            }
        }
        this.currentAccount = messageObject.currentAccount;
        String address = null;
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.address)) {
            address = messageObject.messageOwner.media.address;
        }
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
            name = messageObject.messageOwner.media.title;
            Drawable drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.MULTIPLY));
            int color = Theme.getColor(Theme.key_location_placeLocationBackground);
            CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), color, color), drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
        } else {
            name = "";
            this.avatarDrawable = null;
            if (fromId > 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(fromId));
                if (user != null) {
                    this.avatarDrawable = new AvatarDrawable(user);
                    name = UserObject.getName(user);
                    this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                }
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-fromId));
                if (chat != null) {
                    this.avatarDrawable = new AvatarDrawable(chat);
                    name = chat.title;
                    this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
                }
            }
        }
        this.nameTextView.setText(name);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (userLocation != null) {
            float distance = this.location.distanceTo(userLocation);
            if (address != null) {
                this.distanceTextView.setText(String.format("%s - %s", new Object[]{address, LocaleController.formatDistance(distance)}));
                return;
            }
            this.distanceTextView.setText(LocaleController.formatDistance(distance));
        } else if (address != null) {
            this.distanceTextView.setText(address);
        } else {
            this.distanceTextView.setText(LocaleController.getString("Loading", R.string.Loading));
        }
    }

    public void setDialog(LocationActivity.LiveLocation info, BDLocation userLocation) {
        this.liveLocation = info;
        int lower_id = info.id;
        if (lower_id > 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                this.nameTextView.setText(chat.title);
                this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
            }
        }
        LatLng position = info.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        String time = LocaleController.formatLocationUpdateDate((long) (info.object.edit_date != 0 ? info.object.edit_date : info.object.date));
        if (userLocation != null) {
            LatLng newLatlng = new LatLng(this.location.getLatitude(), this.location.getLongitude());
            this.distanceTextView.setText(String.format("%s - %s", new Object[]{time, LocaleController.formatDistance((float) DistanceUtil.getDistance(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), newLatlng))}));
            return;
        }
        this.distanceTextView.setText(time);
    }

    public void setDialog(LocationController.SharingLocationInfo info) {
        this.currentInfo = info;
        int lower_id = (int) info.did;
        if (lower_id > 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                return;
            }
            return;
        }
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
        if (chat != null) {
            this.avatarDrawable.setInfo(chat);
            this.nameTextView.setText(chat.title);
            this.avatarImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int period;
        int stopTime;
        int color;
        if (this.currentInfo != null || this.liveLocation != null) {
            LocationController.SharingLocationInfo sharingLocationInfo = this.currentInfo;
            if (sharingLocationInfo != null) {
                stopTime = sharingLocationInfo.stopTime;
                period = this.currentInfo.period;
            } else {
                stopTime = this.liveLocation.object.date + this.liveLocation.object.media.period;
                period = this.liveLocation.object.media.period;
            }
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (stopTime >= currentTime) {
                float progress = ((float) Math.abs(stopTime - currentTime)) / ((float) period);
                float f = 48.0f;
                float f2 = 18.0f;
                if (LocaleController.isRTL) {
                    RectF rectF = this.rect;
                    float dp = (float) AndroidUtilities.dp(13.0f);
                    if (this.distanceTextView == null) {
                        f2 = 12.0f;
                    }
                    float dp2 = (float) AndroidUtilities.dp(f2);
                    float dp3 = (float) AndroidUtilities.dp(43.0f);
                    if (this.distanceTextView == null) {
                        f = 42.0f;
                    }
                    rectF.set(dp, dp2, dp3, (float) AndroidUtilities.dp(f));
                } else {
                    RectF rectF2 = this.rect;
                    float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f));
                    if (this.distanceTextView == null) {
                        f2 = 12.0f;
                    }
                    float dp4 = (float) AndroidUtilities.dp(f2);
                    float measuredWidth2 = (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f));
                    if (this.distanceTextView == null) {
                        f = 42.0f;
                    }
                    rectF2.set(measuredWidth, dp4, measuredWidth2, (float) AndroidUtilities.dp(f));
                }
                if (this.distanceTextView == null) {
                    color = Theme.getColor(Theme.key_dialog_liveLocationProgress);
                } else {
                    color = Theme.getColor(Theme.key_location_liveLocationProgress);
                }
                Theme.chat_radialProgress2Paint.setColor(color);
                Theme.chat_livePaint.setColor(color);
                canvas.drawArc(this.rect, -90.0f, progress * -360.0f, false, Theme.chat_radialProgress2Paint);
                String text = LocaleController.formatLocationLeftTime(stopTime - currentTime);
                canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
            }
        }
    }
}
