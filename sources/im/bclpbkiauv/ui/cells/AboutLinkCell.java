package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.LinkPath;
import im.bclpbkiauv.ui.components.StaticLayoutEx;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;

public class AboutLinkCell extends FrameLayout {
    private String oldText;
    private ClickableSpan pressedLink;
    private SpannableStringBuilder stringBuilder;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private TextView valueTextView;

    public AboutLinkCell(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.valueTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        int i = 5;
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (!LocaleController.isRTL ? 3 : i) | 80, 23.0f, 0.0f, 23.0f, 10.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void didPressUrl(String url) {
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(String text, boolean parseLinks) {
        setTextAndValue(text, (String) null, parseLinks);
    }

    public void setTextAndValue(String text, String value, boolean parseLinks) {
        String str;
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (text == null || (str = this.oldText) == null || !text.equals(str)) {
            this.oldText = text;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.oldText);
            this.stringBuilder = spannableStringBuilder;
            if (parseLinks) {
                MessageObject.addLinks(false, spannableStringBuilder, false);
            }
            Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (TextUtils.isEmpty(value)) {
                this.valueTextView.setVisibility(8);
            } else {
                this.valueTextView.setText(value);
                this.valueTextView.setVisibility(0);
            }
            requestLayout();
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
                                if (url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) {
                                    didPressUrl(url);
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
        if (this.stringBuilder != null) {
            int maxWidth = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(46.0f);
            if (Build.VERSION.SDK_INT >= 24) {
                SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
                this.textLayout = StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), Theme.profile_aboutTextPaint, maxWidth).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? StaticLayoutEx.ALIGN_RIGHT() : StaticLayoutEx.ALIGN_LEFT()).build();
            } else {
                this.textLayout = new StaticLayout(this.stringBuilder, Theme.profile_aboutTextPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        StaticLayout staticLayout = this.textLayout;
        int height = (staticLayout != null ? staticLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f);
        if (this.valueTextView.getVisibility() == 0) {
            height += AndroidUtilities.dp(23.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        int dp = AndroidUtilities.dp(23.0f);
        this.textX = dp;
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas.translate((float) dp, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
        }
        try {
            if (this.textLayout != null) {
                this.textLayout.draw(canvas);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        canvas.restore();
    }
}
