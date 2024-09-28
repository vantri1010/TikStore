package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NotificationsSettingsActivity;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import org.slf4j.Marker;

public class ProfileSearchCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private int avatarLeft;
    private TLRPC.Chat chat;
    private boolean countIsBiggerThanTen;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop = AndroidUtilities.dp(19.0f);
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private long dialog_id;
    private boolean drawBotIcon;
    private boolean drawBroadcastIcon;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawGroupIcon;
    private boolean drawSecretLockIcon;
    private TLRPC.EncryptedChat encryptedChat;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int lastUnreadCount;
    private int miViewType = 0;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameWidth;
    private RectF rect = new RectF();
    private boolean savedMessages;
    private StaticLayout statusLayout;
    private int statusLeft;
    private CharSequence subLabel;
    private int sublabelOffsetX;
    private int sublabelOffsetY;
    private float topOffset;
    public boolean useSeparator;
    private TLRPC.User user;

    public ProfileSearchCell(Context context) {
        super(context);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.avatarImage = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setData(TLObject object, TLRPC.EncryptedChat ec, CharSequence n, CharSequence s, boolean needCount, boolean saved) {
        this.currentName = n;
        if (object instanceof TLRPC.User) {
            this.user = (TLRPC.User) object;
            this.chat = null;
        } else if (object instanceof TLRPC.Chat) {
            this.chat = (TLRPC.Chat) object;
            this.user = null;
        }
        this.encryptedChat = ec;
        this.subLabel = s;
        this.drawCount = needCount;
        this.savedMessages = saved;
        update(0);
    }

    public void setMiViewType(int miViewType2) {
        this.miViewType = miViewType2;
    }

    public void setException(NotificationsSettingsActivity.NotificationException exception, CharSequence name) {
        String text;
        String text2;
        TLRPC.User user2;
        boolean enabled;
        NotificationsSettingsActivity.NotificationException notificationException = exception;
        boolean custom = notificationException.hasCustom;
        int value = notificationException.notify;
        int delta = notificationException.muteUntil;
        if (value != 3 || delta == Integer.MAX_VALUE) {
            if (value == 0) {
                enabled = true;
            } else if (value == 1) {
                enabled = true;
            } else if (value == 2) {
                enabled = false;
            } else {
                enabled = false;
            }
            if (!enabled || !custom) {
                text = enabled ? LocaleController.getString("NotificationsUnmuted", R.string.NotificationsUnmuted) : LocaleController.getString("NotificationsMuted", R.string.NotificationsMuted);
                int i = delta;
            } else {
                text = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                int i2 = delta;
            }
        } else {
            int delta2 = delta - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (delta2 <= 0) {
                if (custom) {
                    text = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                    int i3 = delta2;
                } else {
                    text = LocaleController.getString("NotificationsUnmuted", R.string.NotificationsUnmuted);
                    int i4 = delta2;
                }
            } else if (delta2 < 3600) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta2 / 60));
                int i5 = delta2;
            } else if (delta2 < 86400) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta2) / 60.0f) / 60.0f))));
                int i6 = delta2;
            } else if (delta2 < 31536000) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta2) / 60.0f) / 60.0f) / 24.0f))));
                int i7 = delta2;
            } else {
                text = null;
                int i8 = delta2;
            }
        }
        if (text == null) {
            text2 = LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
        } else {
            text2 = text;
        }
        int lower_id = (int) notificationException.did;
        int high_id = (int) (notificationException.did >> 32);
        if (lower_id == 0) {
            TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
            if (encryptedChat2 != null && (user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id))) != null) {
                setData(user2, encryptedChat2, name, text2, false, false);
            }
        } else if (lower_id > 0) {
            TLRPC.User user3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user3 != null) {
                setData(user3, (TLRPC.EncryptedChat) null, name, text2, false, false);
            }
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat2 != null) {
                setData(chat2, (TLRPC.EncryptedChat) null, name, text2, false, false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), AndroidUtilities.dp(70.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!(this.user == null && this.chat == null && this.encryptedChat == null) && changed) {
            buildLayout();
        }
    }

    public void setSublabelOffset(int x, int y) {
        this.sublabelOffsetX = x;
        this.sublabelOffsetY = y;
    }

    public void buildLayout() {
        CharSequence nameString;
        TextPaint currentNamePaint;
        int statusWidth;
        if (this.topOffset == 0.0f) {
            this.topOffset = ((float) (getMeasuredHeight() - AndroidUtilities.dp(48.0f))) / 2.0f;
        }
        this.drawBroadcastIcon = false;
        this.drawSecretLockIcon = false;
        this.drawGroupIcon = false;
        this.drawCheck = false;
        this.drawBotIcon = false;
        if (!LocaleController.isRTL) {
            this.nameLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            this.nameLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC.EncryptedChat encryptedChat2 = this.encryptedChat;
        if (encryptedChat2 != null) {
            this.drawSecretLockIcon = true;
            this.dialog_id = ((long) encryptedChat2.id) << 32;
        } else {
            TLRPC.Chat chat2 = this.chat;
            if (chat2 != null) {
                this.dialog_id = (long) (-chat2.id);
                if (SharedConfig.drawDialogIcons) {
                    if (!ChatObject.isChannel(this.chat) || this.chat.megagroup) {
                        this.drawGroupIcon = true;
                    } else {
                        this.drawBroadcastIcon = true;
                    }
                }
                this.drawCheck = this.chat.verified;
            } else {
                TLRPC.User user2 = this.user;
                if (user2 != null) {
                    this.dialog_id = (long) user2.id;
                    if (SharedConfig.drawDialogIcons && this.user.bot && !MessagesController.isSupportUser(this.user)) {
                        this.drawBotIcon = true;
                    }
                    this.drawCheck = this.user.verified;
                }
            }
        }
        if (this.currentName != null) {
            nameString = this.currentName;
        } else {
            String nameString2 = "";
            TLRPC.Chat chat3 = this.chat;
            if (chat3 != null) {
                nameString2 = chat3.title;
            } else {
                TLRPC.User user3 = this.user;
                if (user3 != null) {
                    nameString2 = UserObject.getName(user3);
                }
            }
            nameString = nameString2.replace(10, ' ');
        }
        if (nameString.length() == 0) {
            TLRPC.User user4 = this.user;
            if (user4 == null || user4.phone == null || this.user.phone.length() == 0) {
                nameString = LocaleController.getString("HiddenName", R.string.HiddenName);
            } else {
                nameString = PhoneFormat.getInstance().format(Marker.ANY_NON_NULL_MARKER + this.user.phone);
            }
        }
        if (this.encryptedChat != null) {
            currentNamePaint = Theme.dialogs_searchNameEncryptedPaint;
        } else {
            currentNamePaint = Theme.dialogs_searchNamePaint;
        }
        if (!LocaleController.isRTL) {
            statusWidth = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f);
            this.nameWidth = statusWidth;
            this.avatarLeft = AndroidUtilities.dp(16.0f);
        } else {
            statusWidth = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
            this.nameWidth = statusWidth;
            this.avatarLeft = AndroidUtilities.dp(11.0f) + getPaddingLeft();
        }
        this.nameWidth -= getPaddingLeft() + getPaddingRight();
        int statusWidth2 = statusWidth - (getPaddingLeft() + getPaddingRight());
        if (this.drawCount != 0) {
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog == null || dialog.unread_count == 0) {
                this.lastUnreadCount = 0;
                this.countLayout = null;
            } else {
                this.lastUnreadCount = dialog.unread_count;
                String countString = String.format("%d", new Object[]{Integer.valueOf(dialog.unread_count)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(countString)));
                this.countLayout = new StaticLayout(countString, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                int w = this.countWidth + AndroidUtilities.dp(18.0f);
                this.nameWidth -= w;
                boolean z = this.lastUnreadCount > 10;
                this.countIsBiggerThanTen = z;
                this.countTop += z ? AndroidUtilities.dp(3.0f) : 0;
                if (!LocaleController.isRTL) {
                    this.countLeft = (getMeasuredWidth() - this.countWidth) - AndroidUtilities.dp(20.0f);
                } else {
                    this.countLeft = AndroidUtilities.dp(20.0f);
                    this.nameLeft += w;
                }
            }
        } else {
            this.lastUnreadCount = 0;
            this.countLayout = null;
        }
        StaticLayout staticLayout = r9;
        int statusWidth3 = statusWidth2;
        StaticLayout staticLayout2 = new StaticLayout(TextUtils.ellipsize(nameString, currentNamePaint, (float) (this.nameWidth - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), currentNamePaint, this.nameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.nameLayout = staticLayout;
        CharSequence statusString = null;
        TextPaint currentStatusPaint = Theme.dialogs_offlinePaint;
        if (!LocaleController.isRTL) {
            this.statusLeft = AndroidUtilities.dp((float) AndroidUtilities.leftBaseline);
        } else {
            this.statusLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC.Chat chat4 = this.chat;
        if (chat4 == null || this.subLabel != null) {
            if (this.subLabel != null) {
                statusString = this.subLabel;
            } else {
                TLRPC.User user5 = this.user;
                if (user5 != null) {
                    if (MessagesController.isSupportUser(user5)) {
                        statusString = LocaleController.getString("SupportStatus", R.string.SupportStatus);
                    } else if (this.user.bot) {
                        statusString = LocaleController.getString("Bot", R.string.Bot);
                    } else if (this.user.id == 333000 || this.user.id == 777000) {
                        statusString = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                    } else if (this.miViewType == 0) {
                        statusString = LocaleController.formatUserStatus(this.currentAccount, this.user);
                        TLRPC.User user6 = this.user;
                        if (user6 != null && (user6.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || (this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()))) {
                            TextPaint currentStatusPaint2 = Theme.dialogs_onlinePaint;
                            statusString = LocaleController.getString("Online", R.string.Online);
                            currentStatusPaint = currentStatusPaint2;
                        }
                    } else {
                        boolean[] booleans = {false};
                        statusString = LocaleController.formatUserStatusNew(this.currentAccount, this.user, booleans);
                        if (booleans[0]) {
                            TextPaint textPaint = new TextPaint();
                            textPaint.setTextSize(currentStatusPaint.getTextSize());
                            textPaint.setColor(Color.parseColor("#42B71E"));
                            currentStatusPaint = textPaint;
                        }
                    }
                }
            }
            if (this.savedMessages) {
                statusString = null;
            }
        } else if (chat4 != null) {
            if (!ChatObject.isChannel(chat4) || this.chat.megagroup) {
                if (this.chat.has_geo) {
                    statusString = LocaleController.getString("MegaLocation", R.string.MegaLocation);
                } else if (TextUtils.isEmpty(this.chat.username)) {
                    statusString = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
                } else {
                    statusString = LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase();
                }
            } else if (TextUtils.isEmpty(this.chat.username)) {
                statusString = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
            } else {
                statusString = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
            }
        }
        if (!TextUtils.isEmpty(statusString)) {
            this.statusLayout = new StaticLayout(TextUtils.ellipsize(statusString, currentStatusPaint, (float) (statusWidth3 - AndroidUtilities.dp(12.0f)), TextUtils.TruncateAt.END), currentStatusPaint, statusWidth3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } else {
            this.statusLayout = null;
        }
        this.avatarImage.setImageCoords(this.avatarLeft, (int) this.topOffset, AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f));
        if (LocaleController.isRTL) {
            if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineLeft(0) == 0.0f) {
                double widthpx = Math.ceil((double) this.nameLayout.getLineWidth(0));
                int i = this.nameWidth;
                if (widthpx < ((double) i)) {
                    this.nameLeft = (int) (((double) this.nameLeft) + (((double) i) - widthpx));
                }
            }
            StaticLayout staticLayout3 = this.statusLayout;
            if (staticLayout3 != null && staticLayout3.getLineCount() > 0 && this.statusLayout.getLineLeft(0) == 0.0f) {
                double widthpx2 = Math.ceil((double) this.statusLayout.getLineWidth(0));
                if (widthpx2 < ((double) statusWidth3)) {
                    this.statusLeft = (int) (((double) this.statusLeft) + (((double) statusWidth3) - widthpx2));
                }
            }
        } else {
            if (this.nameLayout.getLineCount() > 0 && this.nameLayout.getLineRight(0) == ((float) this.nameWidth)) {
                double widthpx3 = Math.ceil((double) this.nameLayout.getLineWidth(0));
                int i2 = this.nameWidth;
                if (widthpx3 < ((double) i2)) {
                    this.nameLeft = (int) (((double) this.nameLeft) - (((double) i2) - widthpx3));
                }
            }
            StaticLayout staticLayout4 = this.statusLayout;
            if (staticLayout4 != null && staticLayout4.getLineCount() > 0 && this.statusLayout.getLineRight(0) == ((float) statusWidth3)) {
                double widthpx4 = Math.ceil((double) this.statusLayout.getLineWidth(0));
                if (widthpx4 < ((double) statusWidth3)) {
                    this.statusLeft = (int) (((double) this.statusLeft) - (((double) statusWidth3) - widthpx4));
                }
            }
        }
        this.nameLeft += getPaddingLeft();
        this.statusLeft += getPaddingLeft();
    }

    public void update(int mask) {
        TLRPC.Dialog dialog;
        String newName;
        TLRPC.User user2;
        TLRPC.FileLocation fileLocation;
        TLRPC.FileLocation photo = null;
        TLRPC.User user3 = this.user;
        if (user3 != null) {
            this.avatarDrawable.setInfo(user3);
            if (this.savedMessages) {
                this.avatarDrawable.setAvatarType(1);
                this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, (Object) null, 0);
            } else {
                if (this.user.photo != null) {
                    photo = this.user.photo.photo_small;
                }
                this.avatarImage.setImage(ImageLocation.getForUser(this.user, false), "50_50", this.avatarDrawable, (String) null, this.user, 0);
            }
        } else {
            TLRPC.Chat chat2 = this.chat;
            if (chat2 != null) {
                if (chat2.photo != null) {
                    photo = this.chat.photo.photo_small;
                }
                this.avatarDrawable.setInfo(this.chat);
                this.avatarImage.setImage(ImageLocation.getForChat(this.chat, false), "50_50", this.avatarDrawable, (String) null, this.chat, 0);
            } else {
                this.avatarDrawable.setInfo(0, (String) null, (String) null);
                this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, (Object) null, 0);
            }
        }
        if (mask != 0) {
            boolean continueUpdate = false;
            if (!(((mask & 2) == 0 || this.user == null) && ((mask & 8) == 0 || this.chat == null)) && ((this.lastAvatar != null && photo == null) || ((this.lastAvatar == null && photo != null) || !((fileLocation = this.lastAvatar) == null || photo == null || (fileLocation.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                continueUpdate = true;
            }
            if (!(continueUpdate || (mask & 4) == 0 || (user2 = this.user) == null)) {
                int newStatus = 0;
                if (user2.status != null) {
                    newStatus = this.user.status.expires;
                }
                if (newStatus != this.lastStatus) {
                    continueUpdate = true;
                }
            }
            if (!((continueUpdate || (mask & 1) == 0 || this.user == null) && ((mask & 16) == 0 || this.chat == null))) {
                if (this.user != null) {
                    newName = this.user.first_name + this.user.last_name;
                } else {
                    newName = this.chat.title;
                }
                if (!newName.equals(this.lastName)) {
                    continueUpdate = true;
                }
            }
            if (!(continueUpdate || !this.drawCount || (mask & 256) == 0 || (dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id)) == null || dialog.unread_count == this.lastUnreadCount)) {
                continueUpdate = true;
            }
            if (!continueUpdate) {
                return;
            }
        }
        TLRPC.User user4 = this.user;
        if (user4 != null) {
            if (user4.status != null) {
                this.lastStatus = this.user.status.expires;
            } else {
                this.lastStatus = 0;
            }
            this.lastName = this.user.first_name + this.user.last_name;
        } else {
            TLRPC.Chat chat3 = this.chat;
            if (chat3 != null) {
                this.lastName = chat3.title;
            }
        }
        this.lastAvatar = photo;
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int x;
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
            }
            float f = 3.0f;
            if (this.nameLayout != null) {
                canvas.save();
                canvas.translate((float) this.nameLeft, this.topOffset);
                this.nameLayout.draw(canvas);
                canvas.restore();
                if (this.drawCheck) {
                    if (!LocaleController.isRTL) {
                        x = (int) (((float) this.nameLeft) + this.nameLayout.getLineRight(0) + ((float) AndroidUtilities.dp(6.0f)));
                    } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                        x = (this.nameLeft - AndroidUtilities.dp(6.0f)) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                    } else {
                        x = (int) (((((double) (this.nameLeft + this.nameWidth)) - Math.ceil((double) this.nameLayout.getLineWidth(0))) - ((double) AndroidUtilities.dp(6.0f))) - ((double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
                    }
                    setDrawableBounds(Theme.dialogs_verifiedDrawable, (float) x, this.topOffset + ((float) AndroidUtilities.dp(3.0f)));
                    setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, (float) x, this.topOffset + ((float) AndroidUtilities.dp(3.0f)));
                    Theme.dialogs_verifiedDrawable.draw(canvas);
                    Theme.dialogs_verifiedCheckDrawable.draw(canvas);
                }
            }
            if (this.statusLayout != null) {
                canvas.save();
                canvas.translate((float) (this.statusLeft + this.sublabelOffsetX), ((float) (AndroidUtilities.dp(34.0f) + this.sublabelOffsetY)) + this.topOffset);
                this.statusLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countLayout != null) {
                Paint paint = MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint;
                if (this.countIsBiggerThanTen) {
                    int x2 = this.countLeft - AndroidUtilities.dp(6.0f);
                    float radius = (float) AndroidUtilities.dp(8.0f);
                    this.rect.set((float) x2, (float) this.countTop, (float) (this.countWidth + x2 + AndroidUtilities.dp(10.0f)), (float) (this.countTop + AndroidUtilities.dp(16.0f)));
                    canvas.drawRoundRect(this.rect, radius, radius, paint);
                } else {
                    int x3 = this.countLeft - AndroidUtilities.dp(4.0f);
                    float w = (float) (this.countWidth + AndroidUtilities.dp(8.0f));
                    int i = this.countTop;
                    this.rect.set((float) x3, (float) i, ((float) x3) + w, ((float) i) + w);
                    canvas.drawRoundRect(this.rect, w / 2.0f, w / 2.0f, paint);
                }
                if (this.countLayout != null) {
                    canvas.save();
                    float dp = (float) (this.countLeft - AndroidUtilities.dp(this.countIsBiggerThanTen ? 1.0f : 0.5f));
                    int i2 = this.countTop;
                    if (this.countIsBiggerThanTen) {
                        f = 2.0f;
                    }
                    canvas.translate(dp, (float) (i2 + AndroidUtilities.dp(f)));
                    this.countLayout.draw(canvas);
                    canvas.restore();
                }
            }
            this.avatarImage.draw(canvas);
            if (this.drawSecretLockIcon) {
                int height = Theme.dialogs_lockDrawable.getIntrinsicHeight();
                setDrawableBounds(Theme.dialogs_lockDrawable, this.avatarLeft, (int) ((this.topOffset + ((float) this.avatarImage.getImageHeight())) - ((float) height)), Theme.dialogs_lockDrawable.getIntrinsicWidth(), height);
                Theme.dialogs_lockDrawable.draw(canvas);
            } else if (this.drawGroupIcon) {
                setDrawableBounds(Theme.dialogs_groupDrawable, this.avatarLeft, (int) ((this.topOffset + ((float) this.avatarImage.getImageHeight())) - ((float) AndroidUtilities.dp(7.5f))), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(7.5f));
                Theme.dialogs_groupDrawable.draw(canvas);
            } else if (this.drawBroadcastIcon) {
                int height2 = Theme.dialogs_broadcastDrawable.getIntrinsicHeight();
                setDrawableBounds(Theme.dialogs_broadcastDrawable, this.avatarLeft, (int) ((this.topOffset + ((float) this.avatarImage.getImageHeight())) - ((float) height2)), Theme.dialogs_broadcastDrawable.getIntrinsicWidth(), height2);
                Theme.dialogs_broadcastDrawable.draw(canvas);
            } else if (this.drawBotIcon) {
                int height3 = Theme.dialogs_botDrawable.getIntrinsicHeight();
                setDrawableBounds(Theme.dialogs_botDrawable, this.avatarLeft, (int) ((this.topOffset + ((float) this.avatarImage.getImageHeight())) - ((float) height3)), Theme.dialogs_botDrawable.getIntrinsicWidth(), height3);
                Theme.dialogs_botDrawable.draw(canvas);
            }
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder builder = new StringBuilder();
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout != null) {
            builder.append(staticLayout.getText());
        }
        if (this.statusLayout != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(this.statusLayout.getText());
        }
        info.setText(builder.toString());
    }
}
