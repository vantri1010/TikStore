package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;

public class ChatActionCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    private int customDate;
    private CharSequence customText;
    /* access modifiers changed from: private */
    public ChatActionCellDelegate delegate;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private float lastTouchX;
    private float lastTouchY;
    private URLSpan pressedLink;
    private ClickableSpan pressedRedLink;
    private int previousWidth;
    private int textHeight;
    private StaticLayout textLayout;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;
    private boolean wasLayout;

    public interface ChatActionCellDelegate {
        void didClickImage(ChatActionCell chatActionCell);

        void didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void didRedUrl(MessageObject messageObject);

        void needOpenUserProfile(int i);

        /* renamed from: im.bclpbkiauv.ui.cells.ChatActionCell$ChatActionCellDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didClickImage(ChatActionCellDelegate _this, ChatActionCell cell) {
            }

            public static void $default$didLongPress(ChatActionCellDelegate _this, ChatActionCell cell, float x, float y) {
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate _this, int uid) {
            }

            public static void $default$didPressBotButton(ChatActionCellDelegate _this, MessageObject messageObject, TLRPC.KeyboardButton button) {
            }

            public static void $default$didPressReplyMessage(ChatActionCellDelegate _this, ChatActionCell cell, int id) {
            }

            public static void $default$didRedUrl(ChatActionCellDelegate _this, MessageObject messageObject) {
            }
        }
    }

    public ChatActionCell(Context context) {
        super(context);
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setDelegate(ChatActionCellDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setCustomDate(int date, boolean scheduled) {
        CharSequence newText;
        if (this.customDate != date) {
            if (scheduled) {
                newText = LocaleController.formatString("MessageScheduledOn", R.string.MessageScheduledOn, LocaleController.formatDateChat((long) date));
            } else {
                newText = LocaleController.formatDateChat((long) date);
            }
            CharSequence charSequence = this.customText;
            if (charSequence == null || !TextUtils.equals(newText, charSequence)) {
                this.customDate = date;
                this.customText = newText;
                if (getMeasuredWidth() != 0) {
                    createLayout(this.customText, getMeasuredWidth());
                    invalidate();
                }
                if (!this.wasLayout) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            ChatActionCell.this.requestLayout();
                        }
                    });
                } else {
                    buildLayout();
                }
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        if (this.currentMessageObject != messageObject || (!this.hasReplyMessage && messageObject.replyMessageObject != null)) {
            this.currentMessageObject = messageObject;
            messageObject.setDelegate(new MessageObject.Delegate() {
                public void onClickRed() {
                    if (ChatActionCell.this.delegate != null) {
                        ChatActionCell.this.delegate.didRedUrl(ChatActionCell.this.currentMessageObject);
                    }
                }
            });
            this.hasReplyMessage = messageObject.replyMessageObject != null;
            this.previousWidth = 0;
            if (this.currentMessageObject.type == 11) {
                int id = 0;
                if (messageObject.messageOwner.to_id != null) {
                    if (messageObject.messageOwner.to_id.chat_id != 0) {
                        id = messageObject.messageOwner.to_id.chat_id;
                    } else if (messageObject.messageOwner.to_id.channel_id != 0) {
                        id = messageObject.messageOwner.to_id.channel_id;
                    } else {
                        id = messageObject.messageOwner.to_id.user_id;
                        if (id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            id = messageObject.messageOwner.from_id;
                        }
                    }
                }
                this.avatarDrawable.setInfo(id, (String) null, (String) null);
                if (this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.currentMessageObject, 0);
                } else {
                    TLRPC.PhotoSize photo = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0f));
                    if (photo != null) {
                        this.imageReceiver.setImage(ImageLocation.getForObject(photo, this.currentMessageObject.photoThumbsObject), "50_50", this.avatarDrawable, (String) null, this.currentMessageObject, 0);
                    } else {
                        this.imageReceiver.setImageBitmap((Drawable) this.avatarDrawable);
                    }
                }
                this.imageReceiver.setVisible(true ^ PhotoViewer.isShowingImage(this.currentMessageObject), false);
            } else {
                this.imageReceiver.setImageBitmap((Bitmap) null);
            }
            requestLayout();
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public ImageReceiver getPhotoImage() {
        return this.imageReceiver;
    }

    /* access modifiers changed from: protected */
    public void onLongPress() {
        ChatActionCellDelegate chatActionCellDelegate = this.delegate;
        if (chatActionCellDelegate != null) {
            chatActionCellDelegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        this.lastTouchX = x;
        float y = event.getY();
        this.lastTouchY = y;
        boolean result = false;
        if (event.getAction() != 0) {
            if (event.getAction() != 2) {
                cancelCheckLongPress();
            }
            if (this.imagePressed) {
                if (event.getAction() == 1) {
                    this.imagePressed = false;
                    ChatActionCellDelegate chatActionCellDelegate = this.delegate;
                    if (chatActionCellDelegate != null) {
                        chatActionCellDelegate.didClickImage(this);
                        playSoundEffect(0);
                    }
                } else if (event.getAction() == 3) {
                    this.imagePressed = false;
                } else if (event.getAction() == 2 && !this.imageReceiver.isInsideImage(x, y)) {
                    this.imagePressed = false;
                }
            }
        } else if (this.delegate != null) {
            if (this.currentMessageObject.type == 11 && this.imageReceiver.isInsideImage(x, y)) {
                this.imagePressed = true;
                result = true;
            }
            if (result) {
                startCheckLongPress();
            }
        }
        if (!result && (event.getAction() == 0 || (!(this.pressedLink == null && this.pressedRedLink == null) && event.getAction() == 1))) {
            int i = this.textX;
            if (x >= ((float) i)) {
                int i2 = this.textY;
                if (y >= ((float) i2) && x <= ((float) (i + this.textWidth)) && y <= ((float) (this.textHeight + i2))) {
                    float x2 = x - ((float) this.textXLeft);
                    int line = this.textLayout.getLineForVertical((int) (y - ((float) i2)));
                    int off = this.textLayout.getOffsetForHorizontal(line, x2);
                    float left = this.textLayout.getLineLeft(line);
                    if (left <= x2 && this.textLayout.getLineWidth(line) + left >= x2 && (this.currentMessageObject.messageText instanceof Spannable) && !(this.currentMessageObject.messageOwner.action instanceof TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer)) {
                        URLSpan[] link = (URLSpan[]) ((Spannable) this.currentMessageObject.messageText).getSpans(off, off, URLSpan.class);
                        if (link.length == 0) {
                            this.pressedLink = null;
                        } else if (event.getAction() == 0) {
                            this.pressedLink = link[0];
                            result = true;
                        } else if (link[0] == this.pressedLink) {
                            if (this.delegate != null) {
                                String url = link[0].getURL();
                                if (url.startsWith("game")) {
                                    this.delegate.didPressReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                                } else if (url.startsWith("http")) {
                                    Browser.openUrl(getContext(), url);
                                } else {
                                    this.delegate.needOpenUserProfile(Integer.parseInt(url));
                                }
                            }
                            result = true;
                        }
                    } else if (this.currentMessageObject.messageOwner.action instanceof TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) {
                        ClickableSpan[] link2 = (ClickableSpan[]) ((Spannable) this.currentMessageObject.messageText).getSpans(off, off, ClickableSpan.class);
                        if (link2.length == 0) {
                            this.pressedRedLink = null;
                        } else if (event.getAction() == 0) {
                            this.pressedRedLink = link2[0];
                            result = true;
                        } else if (link2[0] == this.pressedRedLink) {
                            ChatActionCellDelegate chatActionCellDelegate2 = this.delegate;
                            if (chatActionCellDelegate2 != null) {
                                chatActionCellDelegate2.didRedUrl(this.currentMessageObject);
                            }
                            result = true;
                        }
                    } else {
                        this.pressedRedLink = null;
                    }
                }
            }
            this.pressedLink = null;
            this.pressedRedLink = null;
        }
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
    }

    private void createLayout(CharSequence text, int width) {
        if (!TextUtils.isEmpty(text)) {
            int maxWidth = width - AndroidUtilities.dp(30.0f);
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject == null || messageObject.messageOwner == null || this.currentMessageObject.messageOwner.action == null || !(this.currentMessageObject.messageOwner.action instanceof TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer)) {
                Theme.chat_actionTextPaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
                Theme.chat_actionTextPaint.linkColor = Theme.getColor(Theme.key_chat_serviceLink);
            } else {
                Theme.chat_actionTextPaint.setColor(Theme.getColor(Theme.key_chat_redpacketServiceText));
                Theme.chat_actionTextPaint.linkColor = Theme.getColor(Theme.key_chat_redpacketLinkServiceText);
            }
            StaticLayout staticLayout = new StaticLayout(text, Theme.chat_actionTextPaint, maxWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            this.textLayout = staticLayout;
            this.textHeight = 0;
            this.textWidth = 0;
            try {
                int linesCount = staticLayout.getLineCount();
                int a = 0;
                while (a < linesCount) {
                    try {
                        float lineWidth = this.textLayout.getLineWidth(a);
                        if (lineWidth > ((float) maxWidth)) {
                            lineWidth = (float) maxWidth;
                        }
                        this.textHeight = (int) Math.max((double) this.textHeight, Math.ceil((double) this.textLayout.getLineBottom(a)));
                        this.textWidth = (int) Math.max((double) this.textWidth, Math.ceil((double) lineWidth));
                        a++;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            this.textX = (width - this.textWidth) / 2;
            this.textY = AndroidUtilities.dp(11.0f);
            this.textXLeft = (width - this.textLayout.getWidth()) / 2;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentMessageObject == null && this.customText == null) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), this.textHeight + AndroidUtilities.dp(20.0f));
            return;
        }
        int width = Math.max(AndroidUtilities.dp(30.0f), View.MeasureSpec.getSize(widthMeasureSpec));
        if (this.previousWidth != width) {
            this.wasLayout = true;
            this.previousWidth = width;
            buildLayout();
        }
        MessageObject messageObject = this.currentMessageObject;
        int i = 0;
        if (messageObject == null || !(messageObject.messageOwner.media instanceof TLRPCRedpacket.CL_messagesPayBillOverMedia) || !TextUtils.isEmpty(this.currentMessageObject.messageText)) {
            int i2 = this.textHeight;
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 != null && messageObject2.type == 11) {
                i = 70;
            }
            setMeasuredDimension(width, i2 + AndroidUtilities.dp((float) (20 + i)));
            return;
        }
        setMeasuredDimension(width, 0);
    }

    private void buildLayout() {
        CharSequence text;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            text = this.customText;
        } else if (messageObject.messageOwner == null || this.currentMessageObject.messageOwner.media == null || this.currentMessageObject.messageOwner.media.ttl_seconds == 0) {
            text = this.currentMessageObject.messageText;
        } else if (this.currentMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photoEmpty) {
            text = LocaleController.getString("AttachPhotoExpired", R.string.AttachPhotoExpired);
        } else if (this.currentMessageObject.messageOwner.media.document instanceof TLRPC.TL_documentEmpty) {
            text = LocaleController.getString("AttachVideoExpired", R.string.AttachVideoExpired);
        } else {
            text = this.currentMessageObject.messageText;
        }
        createLayout(text, this.previousWidth);
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && messageObject2.type == 11) {
            this.imageReceiver.setImageCoords((this.previousWidth - AndroidUtilities.dp(64.0f)) / 2, this.textHeight + AndroidUtilities.dp(15.0f), AndroidUtilities.dp(64.0f), AndroidUtilities.dp(64.0f));
        }
    }

    public int getCustomDate() {
        return this.customDate;
    }

    private int findMaxWidthAroundLine(int line) {
        int width = (int) Math.ceil((double) this.textLayout.getLineWidth(line));
        int count = this.textLayout.getLineCount();
        for (int a = line + 1; a < count; a++) {
            int w = (int) Math.ceil((double) this.textLayout.getLineWidth(a));
            if (Math.abs(w - width) >= AndroidUtilities.dp(10.0f)) {
                break;
            }
            width = Math.max(w, width);
        }
        for (int a2 = line - 1; a2 >= 0; a2--) {
            int w2 = (int) Math.ceil((double) this.textLayout.getLineWidth(a2));
            if (Math.abs(w2 - width) >= AndroidUtilities.dp(10.0f)) {
                break;
            }
            width = Math.max(w2, width);
        }
        return width;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            this.imageReceiver.draw(canvas);
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            int count = staticLayout.getLineCount();
            int previousLineBottom = 0;
            int finalWidth = AndroidUtilities.dp(50.0f);
            int finalHeight = AndroidUtilities.dp(6.0f);
            int finalY = AndroidUtilities.dp(8.0f);
            for (int a = 0; a < count; a++) {
                int width = findMaxWidthAroundLine(a);
                if (width > finalWidth) {
                    finalWidth = width;
                }
                int lineBottom = this.textLayout.getLineBottom(a);
                finalHeight += lineBottom - previousLineBottom;
                previousLineBottom = lineBottom;
            }
            int finalWidth2 = finalWidth + AndroidUtilities.dp(11.0f);
            int finalX = (getMeasuredWidth() - finalWidth2) / 2;
            canvas.drawRoundRect(new RectF((float) finalX, (float) finalY, (float) (finalX + finalWidth2), (float) (finalHeight + finalY)), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.chat_actionBackgroundPaint2);
            canvas.save();
            canvas.translate((float) this.textXLeft, (float) this.textY);
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 == null || messageObject2.messageOwner == null || this.currentMessageObject.messageOwner.action == null || !(this.currentMessageObject.messageOwner.action instanceof TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer)) {
                Theme.chat_actionTextPaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
                Theme.chat_actionTextPaint.linkColor = Theme.getColor(Theme.key_chat_serviceLink);
            } else {
                Theme.chat_actionTextPaint.setColor(Theme.getColor(Theme.key_chat_redpacketServiceText));
                Theme.chat_actionTextPaint.linkColor = Theme.getColor(Theme.key_chat_redpacketServiceText);
            }
            this.textLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (!TextUtils.isEmpty(this.customText) || this.currentMessageObject != null) {
            info.setText(!TextUtils.isEmpty(this.customText) ? this.customText : this.currentMessageObject.messageText);
            info.setEnabled(true);
        }
    }
}
