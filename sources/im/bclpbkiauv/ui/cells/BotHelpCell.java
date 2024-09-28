package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LinkPath;
import im.bclpbkiauv.ui.components.TypefaceSpan;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;

public class BotHelpCell extends View {
    private BotHelpCellDelegate delegate;
    private int height;
    private String oldText;
    private ClickableSpan pressedLink;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private int width;

    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context) {
        super(context);
    }

    public void setDelegate(BotHelpCellDelegate botHelpCellDelegate) {
        this.delegate = botHelpCellDelegate;
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(String text) {
        int maxWidth;
        String str = text;
        if (str == null || text.length() == 0) {
            setVisibility(8);
        } else if (str == null || !str.equals(this.oldText)) {
            this.oldText = str;
            setVisibility(0);
            if (AndroidUtilities.isTablet()) {
                maxWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                maxWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            String[] lines = str.split("\n");
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String help = LocaleController.getString("BotInfoTitle", R.string.BotInfoTitle);
            stringBuilder.append(help);
            stringBuilder.append("\n\n");
            for (int a = 0; a < lines.length; a++) {
                stringBuilder.append(lines[a].trim());
                if (a != lines.length - 1) {
                    stringBuilder.append("\n");
                }
            }
            MessageObject.addLinks(false, stringBuilder);
            stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, help.length(), 33);
            Emoji.replaceEmoji(stringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            try {
                StaticLayout staticLayout = r4;
                StaticLayout staticLayout2 = new StaticLayout(stringBuilder, Theme.chat_msgTextPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textLayout = staticLayout;
                this.width = 0;
                this.height = staticLayout.getHeight() + AndroidUtilities.dp(22.0f);
                int count = this.textLayout.getLineCount();
                for (int a2 = 0; a2 < count; a2++) {
                    this.width = (int) Math.ceil((double) Math.max((float) this.width, this.textLayout.getLineWidth(a2) + this.textLayout.getLineLeft(a2)));
                }
                if (this.width > maxWidth) {
                    this.width = maxWidth;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.width += AndroidUtilities.dp(22.0f);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean result = false;
        if (this.textLayout != null) {
            if (event.getAction() == 0 || (this.pressedLink != null && event.getAction() == 1)) {
                if (event.getAction() == 0) {
                    resetPressedLink();
                    try {
                        int x2 = (int) (x - ((float) this.textX));
                        int line = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                        int off = this.textLayout.getOffsetForHorizontal(line, (float) x2);
                        float left = this.textLayout.getLineLeft(line);
                        if (left > ((float) x2) || this.textLayout.getLineWidth(line) + left < ((float) x2)) {
                            resetPressedLink();
                        } else {
                            Spannable buffer = (Spannable) this.textLayout.getText();
                            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                            if (link.length != 0) {
                                resetPressedLink();
                                ClickableSpan clickableSpan = link[0];
                                this.pressedLink = clickableSpan;
                                result = true;
                                try {
                                    int start = buffer.getSpanStart(clickableSpan);
                                    this.urlPath.setCurrentLayout(this.textLayout, start, 0.0f);
                                    this.textLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), this.urlPath);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            } else {
                                resetPressedLink();
                            }
                        }
                    } catch (Exception e2) {
                        resetPressedLink();
                        FileLog.e((Throwable) e2);
                    }
                } else {
                    ClickableSpan clickableSpan2 = this.pressedLink;
                    if (clickableSpan2 != null) {
                        try {
                            if (clickableSpan2 instanceof URLSpanNoUnderline) {
                                String url = ((URLSpanNoUnderline) clickableSpan2).getURL();
                                if ((url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) && this.delegate != null) {
                                    this.delegate.didPressUrl(url);
                                }
                            } else if (clickableSpan2 instanceof URLSpan) {
                                Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                            } else {
                                clickableSpan2.onClick(this);
                            }
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                        resetPressedLink();
                        result = true;
                    }
                }
            } else if (event.getAction() == 3) {
                resetPressedLink();
            }
        }
        return result || super.onTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), this.height + AndroidUtilities.dp(8.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int x = (getWidth() - this.width) / 2;
        int y = AndroidUtilities.dp(4.0f);
        Theme.chat_msgInMediaShadowDrawable.setBounds(x, y, this.width + x, this.height + y);
        Theme.chat_msgInMediaShadowDrawable.draw(canvas);
        Theme.chat_msgInMediaDrawable.setBounds(x, y, this.width + x, this.height + y);
        Theme.chat_msgInMediaDrawable.draw(canvas);
        Theme.chat_msgTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
        Theme.chat_msgTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkIn);
        canvas.save();
        int dp = AndroidUtilities.dp(11.0f) + x;
        this.textX = dp;
        int dp2 = AndroidUtilities.dp(11.0f) + y;
        this.textY = dp2;
        canvas.translate((float) dp, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            staticLayout.draw(canvas);
        }
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(this.textLayout.getText());
    }
}
