package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private boolean animateFromPosition;
    private int currentPosition;
    private LinearLayout.LayoutParams defaultExpandLayoutParams;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private int indicatorColor = -10066330;
    private int indicatorHeight;
    private long lastAnimationTime;
    private int lastScrollX = 0;
    private float positionAnimationProgress;
    private Paint rectPaint;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private boolean shouldExpand;
    private float startAnimationPosition;
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(24.0f);
    private LinearLayout tabsContainer;
    private int underlineColor = 436207616;
    private int underlineHeight = AndroidUtilities.dp(2.0f);

    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    public ScrollSlidingTabStrip(Context context) {
        super(context);
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(52.0f), -1);
        this.defaultExpandLayoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabCount = 0;
        this.currentPosition = 0;
        this.animateFromPosition = false;
    }

    public void selectTab(int num) {
        if (num >= 0 && num < this.tabCount) {
            this.tabsContainer.getChildAt(num).performClick();
        }
    }

    public TextView addIconTabWithCounter(Drawable drawable) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setFocusable(true);
        this.tabsContainer.addView(tab);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        tab.setOnClickListener(new View.OnClickListener(position) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ScrollSlidingTabStrip.this.lambda$addIconTabWithCounter$0$ScrollSlidingTabStrip(this.f$1, view);
            }
        });
        tab.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        tab.setSelected(position == this.currentPosition);
        TextView textView = new TextView(getContext());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 12.0f);
        textView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelBadgeText));
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(9.0f), Theme.getColor(Theme.key_chat_emojiPanelBadgeBackground)));
        textView.setMinWidth(AndroidUtilities.dp(18.0f));
        textView.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.0f));
        tab.addView(textView, LayoutHelper.createFrame(-2.0f, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
        return textView;
    }

    public /* synthetic */ void lambda$addIconTabWithCounter$0$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public ImageView addIconTab(Drawable drawable) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        ImageView tab = new ImageView(getContext());
        boolean z = true;
        tab.setFocusable(true);
        tab.setImageDrawable(drawable);
        tab.setScaleType(ImageView.ScaleType.CENTER);
        tab.setOnClickListener(new View.OnClickListener(position) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ScrollSlidingTabStrip.this.lambda$addIconTab$1$ScrollSlidingTabStrip(this.f$1, view);
            }
        });
        this.tabsContainer.addView(tab);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        return tab;
    }

    public /* synthetic */ void lambda$addIconTab$1$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public void addStickerTab(TLRPC.Chat chat) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setFocusable(true);
        tab.setOnClickListener(new View.OnClickListener(position) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ScrollSlidingTabStrip.this.lambda$addStickerTab$2$ScrollSlidingTabStrip(this.f$1, view);
            }
        });
        this.tabsContainer.addView(tab);
        tab.setSelected(position == this.currentPosition);
        BackupImageView imageView = new BackupImageView(getContext());
        imageView.setLayerNum(1);
        imageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
        avatarDrawable.setInfo(chat);
        imageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) avatarDrawable, (Object) chat);
        imageView.setAspectFit(true);
        tab.addView(imageView, LayoutHelper.createFrame(30, 30, 17));
    }

    public /* synthetic */ void lambda$addStickerTab$2$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public View addStickerTab(TLObject thumb, TLRPC.Document sticker, Object parentObject) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setTag(thumb);
        tab.setTag(R.id.parent_tag, parentObject);
        tab.setTag(R.id.object_tag, sticker);
        tab.setFocusable(true);
        tab.setOnClickListener(new View.OnClickListener(position) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ScrollSlidingTabStrip.this.lambda$addStickerTab$3$ScrollSlidingTabStrip(this.f$1, view);
            }
        });
        this.tabsContainer.addView(tab);
        tab.setSelected(position == this.currentPosition);
        BackupImageView imageView = new BackupImageView(getContext());
        imageView.setLayerNum(1);
        imageView.setAspectFit(true);
        tab.addView(imageView, LayoutHelper.createFrame(30, 30, 17));
        return tab;
    }

    public /* synthetic */ void lambda$addStickerTab$3$ScrollSlidingTabStrip(int position, View v) {
        this.delegate.onPageSelected(position);
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            if (this.shouldExpand) {
                v.setLayoutParams(this.defaultExpandLayoutParams);
            } else {
                v.setLayoutParams(this.defaultTabLayoutParams);
            }
        }
    }

    private void scrollToChild(int position) {
        if (this.tabCount != 0 && this.tabsContainer.getChildAt(position) != null) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft();
            if (position > 0) {
                newScrollX -= this.scrollOffset;
            }
            int currentScrollX = getScrollX();
            if (newScrollX == this.lastScrollX) {
                return;
            }
            if (newScrollX < currentScrollX) {
                this.lastScrollX = newScrollX;
                smoothScrollTo(newScrollX, 0);
            } else if (this.scrollOffset + newScrollX > (getWidth() + currentScrollX) - (this.scrollOffset * 2)) {
                int width = (newScrollX - getWidth()) + (this.scrollOffset * 3);
                this.lastScrollX = width;
                smoothScrollTo(width, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setImages();
    }

    public void setImages() {
        ImageLocation imageLocation;
        ScrollSlidingTabStrip scrollSlidingTabStrip = this;
        int tabSize = AndroidUtilities.dp(52.0f);
        int start = getScrollX() / tabSize;
        int end = Math.min(scrollSlidingTabStrip.tabsContainer.getChildCount(), ((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) tabSize)))) + start + 1);
        int a = start;
        while (a < end) {
            View child = scrollSlidingTabStrip.tabsContainer.getChildAt(a);
            Object object = child.getTag();
            Object parentObject = child.getTag(R.id.parent_tag);
            TLRPC.Document sticker = (TLRPC.Document) child.getTag(R.id.object_tag);
            if (object instanceof TLRPC.Document) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90), sticker);
            } else if (object instanceof TLRPC.PhotoSize) {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) object, sticker);
            } else {
                a++;
                scrollSlidingTabStrip = this;
            }
            if (imageLocation != null) {
                BackupImageView imageView = (BackupImageView) ((FrameLayout) child).getChildAt(0);
                if (!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(sticker)) {
                    ImageLocation imageLocation2 = imageLocation;
                    if (imageLocation2.lottieAnimation) {
                        imageView.setImage(imageLocation2, "30_30", "tgs", (Drawable) null, parentObject);
                    } else {
                        imageView.setImage(imageLocation2, (String) null, "webp", (Drawable) null, parentObject);
                    }
                } else {
                    ImageLocation imageLocation3 = imageLocation;
                    imageView.setImage(ImageLocation.getForDocument(sticker), "30_30", imageLocation, (String) null, 0, parentObject);
                }
            }
            a++;
            scrollSlidingTabStrip = this;
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        ImageLocation imageLocation;
        BackupImageView imageView;
        super.onScrollChanged(l, t, oldl, oldt);
        int tabSize = AndroidUtilities.dp(52.0f);
        int oldStart = oldl / tabSize;
        int newStart = l / tabSize;
        int count = ((int) Math.ceil((double) (((float) getMeasuredWidth()) / ((float) tabSize)))) + 1;
        int start = Math.max(0, Math.min(oldStart, newStart));
        int end = Math.min(this.tabsContainer.getChildCount(), Math.max(oldStart, newStart) + count);
        for (int a = start; a < end; a++) {
            View child = this.tabsContainer.getChildAt(a);
            if (child != null) {
                Object object = child.getTag();
                Object parentObject = child.getTag(R.id.parent_tag);
                TLRPC.Document sticker = (TLRPC.Document) child.getTag(R.id.object_tag);
                if (object instanceof TLRPC.Document) {
                    imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90), sticker);
                } else if (object instanceof TLRPC.PhotoSize) {
                    imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) object, sticker);
                }
                if (imageLocation != null) {
                    BackupImageView imageView2 = (BackupImageView) ((FrameLayout) child).getChildAt(0);
                    if (a < newStart) {
                        imageView = imageView2;
                        ImageLocation imageLocation2 = imageLocation;
                        TLRPC.Document document = sticker;
                    } else if (a >= newStart + count) {
                        imageView = imageView2;
                        ImageLocation imageLocation3 = imageLocation;
                        TLRPC.Document document2 = sticker;
                    } else if (!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(sticker)) {
                        BackupImageView imageView3 = imageView2;
                        TLRPC.Document document3 = sticker;
                        ImageLocation imageLocation4 = imageLocation;
                        if (imageLocation4.lottieAnimation) {
                            ImageLocation imageLocation5 = imageLocation4;
                            imageView3.setImage(imageLocation4, "30_30", "tgs", (Drawable) null, parentObject);
                        } else {
                            imageView3.setImage(imageLocation4, (String) null, "webp", (Drawable) null, parentObject);
                        }
                    } else {
                        BackupImageView backupImageView = imageView2;
                        TLRPC.Document document4 = sticker;
                        imageView2.setImage(ImageLocation.getForDocument(sticker), "30_30", imageLocation, (String) null, 0, parentObject);
                    }
                    imageView.setImageDrawable((Drawable) null);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            if (this.underlineHeight > 0) {
                this.rectPaint.setColor(this.underlineColor);
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            }
            if (this.indicatorHeight >= 0) {
                View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
                float lineLeft = 0.0f;
                int width = 0;
                if (currentTab != null) {
                    lineLeft = (float) currentTab.getLeft();
                    width = currentTab.getMeasuredWidth();
                }
                if (this.animateFromPosition) {
                    long newTime = SystemClock.uptimeMillis();
                    this.lastAnimationTime = newTime;
                    float f = this.positionAnimationProgress + (((float) (newTime - this.lastAnimationTime)) / 150.0f);
                    this.positionAnimationProgress = f;
                    if (f >= 1.0f) {
                        this.positionAnimationProgress = 1.0f;
                        this.animateFromPosition = false;
                    }
                    float f2 = this.startAnimationPosition;
                    lineLeft = f2 + ((lineLeft - f2) * CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.positionAnimationProgress));
                    invalidate();
                }
                this.rectPaint.setColor(this.indicatorColor);
                int i = this.indicatorHeight;
                if (i == 0) {
                    canvas.drawRect(lineLeft, 0.0f, lineLeft + ((float) width), (float) height, this.rectPaint);
                    return;
                }
                canvas.drawRect(lineLeft, (float) (height - i), lineLeft + ((float) width), (float) height, this.rectPaint);
            }
        }
    }

    public void setShouldExpand(boolean value) {
        this.shouldExpand = value;
        requestLayout();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void cancelPositionAnimation() {
        this.animateFromPosition = false;
        this.positionAnimationProgress = 1.0f;
    }

    public void onPageScrolled(int position, int first) {
        int i = this.currentPosition;
        if (i != position) {
            View currentTab = this.tabsContainer.getChildAt(i);
            if (currentTab != null) {
                this.startAnimationPosition = (float) currentTab.getLeft();
                this.positionAnimationProgress = 0.0f;
                this.animateFromPosition = true;
                this.lastAnimationTime = SystemClock.uptimeMillis();
            } else {
                this.animateFromPosition = false;
            }
            this.currentPosition = position;
            if (position < this.tabsContainer.getChildCount()) {
                this.positionAnimationProgress = 0.0f;
                int a = 0;
                while (a < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(a).setSelected(a == position);
                    a++;
                }
                if (first != position || position <= 1) {
                    scrollToChild(position);
                } else {
                    scrollToChild(position - 1);
                }
                invalidate();
            }
        }
    }

    public void setIndicatorHeight(int value) {
        this.indicatorHeight = value;
        invalidate();
    }

    public void setIndicatorColor(int value) {
        this.indicatorColor = value;
        invalidate();
    }

    public void setUnderlineColor(int value) {
        this.underlineColor = value;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public void setUnderlineHeight(int value) {
        this.underlineHeight = value;
        invalidate();
    }
}
