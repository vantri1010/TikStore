package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.EnterMenuView;
import im.bclpbkiauv.ui.constants.ChatEnterMenuType;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.page.PagerGridLayoutManager;
import im.bclpbkiauv.ui.hviews.page.PagerGridSnapHelper;
import java.util.ArrayList;

public class EnterMenuView extends FrameLayout {
    /* access modifiers changed from: private */
    public EnterMenuIndicator bottomPagesView;
    private TLRPC.Chat chatInfo;
    /* access modifiers changed from: private */
    public int currentPage;
    private EnterMenuViewDelegate delegate;
    private Adapter mAdapter;
    private int mCurrentHeight;
    private PagerGridLayoutManager mLayoutManager;
    private RecyclerView mRv;

    public interface EnterMenuViewDelegate {
        void onItemClie(int i, ChatEnterMenuType chatEnterMenuType);
    }

    public EnterMenuView(Context context) {
        this(context, (AttributeSet) null);
    }

    public EnterMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnterMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        RecyclerView recyclerView = new RecyclerView(getContext());
        this.mRv = recyclerView;
        addView(recyclerView, LayoutHelper.createFrame(-1, -1, 17));
        this.mRv.setPadding(0, AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f));
        this.mRv.setOverScrollMode(2);
        new PagerGridSnapHelper().attachToRecyclerView(this.mRv);
        this.mAdapter = new Adapter();
    }

    private void checkLayoutManager(boolean forceCreateNewLayoutManager) {
        if (forceCreateNewLayoutManager || this.mLayoutManager == null || this.mRv.getLayoutManager() == null) {
            PagerGridLayoutManager pagerGridLayoutManager = new PagerGridLayoutManager(2, 4, 1);
            this.mLayoutManager = pagerGridLayoutManager;
            pagerGridLayoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
                public void onPageSizeChanged(int pageSize) {
                }

                public void onPageSelect(int pageIndex) {
                    int unused = EnterMenuView.this.currentPage = pageIndex;
                    if (EnterMenuView.this.bottomPagesView != null) {
                        EnterMenuView.this.bottomPagesView.setCurrentPage(pageIndex);
                    }
                }
            });
            this.mRv.setLayoutManager(this.mLayoutManager);
        }
    }

    private void checkPageBottomIndicator() {
        int total = this.mLayoutManager.getTotalPageCount();
        if (total > 1) {
            EnterMenuIndicator enterMenuIndicator = this.bottomPagesView;
            if (enterMenuIndicator == null) {
                EnterMenuIndicator enterMenuIndicator2 = new EnterMenuIndicator(getContext(), total);
                this.bottomPagesView = enterMenuIndicator2;
                addView(enterMenuIndicator2, LayoutHelper.createFrame((float) (total * 11), 5.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
                return;
            }
            enterMenuIndicator.setPagesCount(total);
        }
    }

    private void update(boolean forceCreateNewAdapter) {
        checkLayoutManager(forceCreateNewAdapter);
        if (forceCreateNewAdapter) {
            Adapter oldAdapter = this.mAdapter;
            Adapter adapter = new Adapter();
            this.mAdapter = adapter;
            adapter.setData(oldAdapter.attachTexts, oldAdapter.attachIcons, oldAdapter.attachTypes);
            this.mAdapter.setCurrentChat(this.chatInfo);
            this.mAdapter.setDelegate(this.delegate);
            this.mRv.setAdapter(this.mAdapter);
        } else if (this.mRv.getAdapter() == null) {
            this.mRv.setAdapter(this.mAdapter);
        } else {
            this.mAdapter.notifyDataSetChanged();
        }
        checkPageBottomIndicator();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mCurrentHeight != h) {
            this.mCurrentHeight = h;
            this.mRv.setOnFlingListener((RecyclerView.OnFlingListener) null);
            update(true);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!(this.currentPage == 0 || getParent() == null)) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setDelegate(EnterMenuViewDelegate delegate2) {
        this.delegate = delegate2;
        Adapter adapter = this.mAdapter;
        if (adapter != null) {
            adapter.setDelegate(delegate2);
        }
    }

    public void setCurrentChat(TLRPC.Chat chatInfo2) {
        this.chatInfo = chatInfo2;
        Adapter adapter = this.mAdapter;
        if (adapter != null) {
            adapter.setCurrentChat(chatInfo2);
            update(false);
        }
    }

    public void setDataAndNotify(ArrayList<String> attachTexts, ArrayList<Integer> attachIcons, ArrayList<ChatEnterMenuType> attachTypes) {
        this.mAdapter.setData(attachTexts, attachIcons, attachTypes);
        update(false);
    }

    private static class Adapter extends RecyclerView.Adapter<PageGridViewHolder> {
        ArrayList<Integer> attachIcons;
        ArrayList<String> attachTexts;
        ArrayList<ChatEnterMenuType> attachTypes;
        private TLRPC.Chat chatInfo;
        private EnterMenuViewDelegate delegate;

        private Adapter() {
            this.attachTexts = new ArrayList<>();
            this.attachIcons = new ArrayList<>();
            this.attachTypes = new ArrayList<>();
        }

        /* access modifiers changed from: package-private */
        public void setData(ArrayList<String> attachTexts2, ArrayList<Integer> attachIcons2, ArrayList<ChatEnterMenuType> attachTypes2) {
            this.attachTexts = attachTexts2;
            this.attachIcons = attachIcons2;
            this.attachTypes = attachTypes2;
        }

        /* access modifiers changed from: private */
        public void setCurrentChat(TLRPC.Chat chatInfo2) {
            this.chatInfo = chatInfo2;
        }

        public void setDelegate(EnterMenuViewDelegate delegate2) {
            this.delegate = delegate2;
        }

        public PageGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attach_menu, parent, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth() / 4, parent.getMeasuredHeight() / 2));
            return new PageGridViewHolder(view);
        }

        public void onBindViewHolder(PageGridViewHolder holder, int position) {
            holder.tvAttachText.setText(this.attachTexts.get(position));
            holder.ivAttachImage.setImageResource(this.attachIcons.get(position).intValue());
            if (Theme.getCurrentTheme() == null || !Theme.getCurrentTheme().isDark()) {
                holder.tvAttachText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            } else {
                int color = Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton);
                holder.ivAttachImage.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.alphaColor(0.4f, color), PorterDuff.Mode.MULTIPLY));
                holder.tvAttachText.setTextColor(AndroidUtilities.alphaColor(0.6f, color));
            }
            boolean enable = menuItemEnable(getItemMenuType(position));
            holder.itemView.setEnabled(enable);
            holder.ivAttachImage.setEnabled(enable);
            holder.tvAttachText.setEnabled(enable);
            holder.itemView.setOnClickListener(new View.OnClickListener(position) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    EnterMenuView.Adapter.this.lambda$onBindViewHolder$0$EnterMenuView$Adapter(this.f$1, view);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$EnterMenuView$Adapter(int position, View v) {
            EnterMenuViewDelegate enterMenuViewDelegate = this.delegate;
            if (enterMenuViewDelegate != null) {
                enterMenuViewDelegate.onItemClie(position, getItemMenuType(position));
            }
        }

        public int getItemCount() {
            ArrayList<String> arrayList = this.attachTexts;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        public ChatEnterMenuType getItemMenuType(int position) {
            ArrayList<ChatEnterMenuType> arrayList = this.attachTypes;
            if (arrayList == null || position < 0 || position >= arrayList.size()) {
                return null;
            }
            return this.attachTypes.get(position);
        }

        public boolean menuItemEnable(ChatEnterMenuType menuType) {
            TLRPC.Chat chat = this.chatInfo;
            if (chat == null) {
                return true;
            }
            if (!ChatObject.isChannel(chat) && !this.chatInfo.megagroup) {
                return true;
            }
            int i = AnonymousClass2.$SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[menuType.ordinal()];
            if (i != 1 && i != 2 && i != 3 && i != 4) {
                return true;
            }
            if ((ChatObject.hasAdminRights(this.chatInfo) || this.chatInfo.default_banned_rights == null || !this.chatInfo.default_banned_rights.send_media) && ChatObject.canSendMedia(this.chatInfo)) {
                return true;
            }
            return false;
        }
    }

    /* renamed from: im.bclpbkiauv.ui.components.EnterMenuView$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType;

        static {
            int[] iArr = new int[ChatEnterMenuType.values().length];
            $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType = iArr;
            try {
                iArr[ChatEnterMenuType.ALBUM.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.DOCUMENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.CAMERA.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.MUSIC.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.VOICECALL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.VIDEOCALL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.LOCATION.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.CONTACTS.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.TRANSFER.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.REDPACKET.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.FAVORITE.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$constants$ChatEnterMenuType[ChatEnterMenuType.POLL.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    public static class PageGridViewHolder extends RecyclerView.ViewHolder {
        public MryAlphaImageView ivAttachImage;
        public MryTextView tvAttachText;

        public PageGridViewHolder(View itemView) {
            super(itemView);
            this.tvAttachText = (MryTextView) itemView.findViewById(R.id.tvAttachText);
            this.ivAttachImage = (MryAlphaImageView) itemView.findViewById(R.id.ivAttachImage);
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space2) {
            this.space = space2;
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = this.space;
            outRect.bottom = this.space;
            outRect.left = this.space;
            outRect.right = this.space;
        }
    }

    public static class EnterMenuIndicator extends View {
        private int color;
        private int currentPage;
        private int pagesCount;
        private Paint paint = new Paint(1);
        private RectF rect = new RectF();
        private int scrollPosition;
        private int selectedColor;

        public EnterMenuIndicator(Context context, int count) {
            super(context);
            this.pagesCount = count;
        }

        public void setPagesCount(int pagesCount2) {
            this.pagesCount = pagesCount2;
            invalidate();
        }

        public void setPageOffset(int position, float offset) {
            this.scrollPosition = position;
            invalidate();
        }

        public void setCurrentPage(int page) {
            this.currentPage = page;
            invalidate();
        }

        public void setColor(String key, String selectedKey) {
            setColor(Theme.getColor(key), Theme.getColor(selectedKey));
        }

        public void setColor(int color2, int selectedColor2) {
            this.color = color2;
            this.selectedColor = selectedColor2;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i = this.color;
            if (i != 0) {
                this.paint.setColor((i & ViewCompat.MEASURED_SIZE_MASK) | -1275068416);
            } else {
                this.paint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon));
            }
            for (int a = 0; a < this.pagesCount; a++) {
                if (a != this.currentPage) {
                    int x = AndroidUtilities.dp(11.0f) * a;
                    this.rect.set((float) x, 0.0f, (float) (AndroidUtilities.dp(5.0f) + x), (float) AndroidUtilities.dp(5.0f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
                }
            }
            int a2 = this.selectedColor;
            if (a2 != 0) {
                this.paint.setColor(a2);
            } else {
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            }
            int x2 = this.currentPage * AndroidUtilities.dp(11.0f);
            this.rect.set((float) x2, 0.0f, (float) (AndroidUtilities.dp(5.0f) + x2), (float) AndroidUtilities.dp(5.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.5f), (float) AndroidUtilities.dp(2.5f), this.paint);
        }
    }
}
