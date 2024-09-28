package im.bclpbkiauv.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearScrollOffsetLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.recyclerview.OnItemClickListener;
import im.bclpbkiauv.ui.dialogs.WalletSelectAbsDialog;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hviews.MryFrameLayout;
import im.bclpbkiauv.ui.hviews.MryLinearLayout;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.List;

public class WalletSelectAbsDialog<T, D extends WalletSelectAbsDialog, VH extends PageHolder> extends BottomSheet {
    protected RecyclerListView.SelectionAdapter adapter;
    protected boolean allowToOverlayActionBar;
    protected MryRoundButton btnConfirm;
    protected MryLinearLayout containerAddButton;
    protected MryFrameLayout containerRv;
    protected List<T> data;
    protected ImageView ivClose;
    protected OnAddButtonClickListener<D> onAddButtonClickListener;
    protected OnConfirmClickListener<T, D> onConfrimClickListener;
    protected OnItemClickListener<T> onItemClickListener;
    protected int recyclerViewMaxHeight;
    protected RecyclerListView rv;
    protected boolean rvAutoHideWhenEmptyData;
    protected int selectPosition;
    protected boolean showAddBtnView;
    protected boolean showCloseView;
    protected boolean showConfirmBtnView;
    protected boolean showDragView;
    protected boolean showListSelectIcon;
    protected boolean showRv;
    protected boolean showSideBar;
    protected boolean showTitleView;
    protected View titleContainer;
    protected MryTextView tvAdd;
    protected MryTextView tvTitle;
    protected MryRoundButton viewDrag;

    public interface OnAddButtonClickListener<D extends WalletSelectAbsDialog> {
        void onAddButtonClick(D d);
    }

    public interface OnConfirmClickListener<T, D extends WalletSelectAbsDialog> {
        void onConfirm(D d, int i, T t);
    }

    public WalletSelectAbsDialog(Context context) {
        this(context, 1);
    }

    public WalletSelectAbsDialog(Context context, boolean useNestScrollViewAsParent) {
        this(context, 1, useNestScrollViewAsParent);
    }

    public WalletSelectAbsDialog(Context context, int backgroundType) {
        this(context, false, backgroundType, false);
    }

    public WalletSelectAbsDialog(Context context, int backgroundType, boolean useNestScrollViewAsParent) {
        this(context, false, backgroundType, useNestScrollViewAsParent);
    }

    public WalletSelectAbsDialog(Context context, boolean needFocus, int backgroundType, boolean useNestScrollViewAsParent) {
        super(context, needFocus, backgroundType);
        init(context, needFocus, backgroundType, useNestScrollViewAsParent);
    }

    /* access modifiers changed from: protected */
    public final void init(Context context, boolean needFocus, int backgroundType) {
    }

    /* access modifiers changed from: protected */
    public void init(Context context, boolean needFocus, int backgroundType, boolean useNestScrollViewAsParent) {
        super.init(context, needFocus, backgroundType);
        setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        View view = LayoutInflater.from(context).inflate(R.layout.wallet_dialog_select_abs, (ViewGroup) null, false);
        setCustomView(view);
        initView(view, context);
    }

    /* access modifiers changed from: protected */
    public void initView(View rootView, Context context) {
        this.showDragView = false;
        this.showTitleView = true;
        this.showCloseView = true;
        this.showRv = true;
        this.showSideBar = false;
        this.rvAutoHideWhenEmptyData = true;
        this.showAddBtnView = false;
        this.showConfirmBtnView = true;
        this.showListSelectIcon = true;
        this.viewDrag = (MryRoundButton) rootView.findViewById(R.id.viewDrag);
        this.titleContainer = rootView.findViewById(R.id.titleContainer);
        this.ivClose = (ImageView) rootView.findViewById(R.id.ivClose);
        this.tvTitle = (MryTextView) rootView.findViewById(R.id.tvTitle);
        this.containerRv = (MryFrameLayout) rootView.findViewById(R.id.containerRv);
        this.containerAddButton = (MryLinearLayout) rootView.findViewById(R.id.containerAddButton);
        this.tvAdd = (MryTextView) rootView.findViewById(R.id.tvAdd);
        this.btnConfirm = (MryRoundButton) rootView.findViewById(R.id.btnConfirm);
        this.containerRv.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        initRv(context);
        this.ivClose.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletSelectAbsDialog.this.lambda$initView$0$WalletSelectAbsDialog(view);
            }
        });
        this.containerAddButton.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerAddButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletSelectAbsDialog.this.lambda$initView$1$WalletSelectAbsDialog(view);
            }
        });
        this.btnConfirm.setPrimaryRadiusAdjustBoundsFillStyle();
        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletSelectAbsDialog.this.lambda$initView$2$WalletSelectAbsDialog(view);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$WalletSelectAbsDialog(View view1) {
        dismiss();
    }

    public /* synthetic */ void lambda$initView$1$WalletSelectAbsDialog(View v) {
        OnAddButtonClickListener<D> onAddButtonClickListener2 = this.onAddButtonClickListener;
        if (onAddButtonClickListener2 != null) {
            onAddButtonClickListener2.onAddButtonClick(this);
        }
    }

    public /* synthetic */ void lambda$initView$2$WalletSelectAbsDialog(View view1) {
        OnConfirmClickListener<T, D> onConfirmClickListener = this.onConfrimClickListener;
        if (onConfirmClickListener != null) {
            int i = this.selectPosition;
            onConfirmClickListener.onConfirm(this, i, getItem(i));
        }
    }

    /* access modifiers changed from: protected */
    public void initRv(Context context) {
        AnonymousClass1 r0 = new RecyclerListView(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthSpec, int heightSpec) {
                int h;
                super.onMeasure(widthSpec, heightSpec);
                int w = View.MeasureSpec.getSize(widthSpec);
                int height = getMeasuredHeight();
                int h2 = View.MeasureSpec.getSize(heightSpec);
                int i = 0;
                int maxh = WalletSelectAbsDialog.this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0;
                if (WalletSelectAbsDialog.this.applyBottomPadding) {
                    i = AndroidUtilities.dp(8.0f);
                }
                int maxh2 = maxh + i;
                if (!WalletSelectAbsDialog.this.allowToOverlayActionBar) {
                    maxh2 += ActionBar.getCurrentActionBarHeight();
                }
                if (WalletSelectAbsDialog.this.showDragView) {
                    maxh2 += AndroidUtilities.dp(20.0f);
                }
                if (WalletSelectAbsDialog.this.showTitleView) {
                    maxh2 += AndroidUtilities.dp(56.0f);
                }
                if (WalletSelectAbsDialog.this.showAddBtnView) {
                    maxh2 += AndroidUtilities.dp(88.0f);
                }
                if (WalletSelectAbsDialog.this.showConfirmBtnView) {
                    maxh2 += AndroidUtilities.dp(118.0f);
                }
                if (AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x) {
                    h = Math.min(h2, AndroidUtilities.displaySize.y - maxh2);
                } else {
                    h = Math.min(h2, AndroidUtilities.displaySize.x - maxh2);
                }
                int h3 = Math.min(h, height);
                if (h3 <= 0) {
                    h3 = AndroidUtilities.dp(250.0f);
                }
                if (WalletSelectAbsDialog.this.recyclerViewMaxHeight > 0) {
                    h3 = Math.min(WalletSelectAbsDialog.this.recyclerViewMaxHeight, h3);
                }
                setMeasuredDimension(w, h3);
            }
        };
        this.rv = r0;
        this.containerRv.addView(r0, 0, LayoutHelper.createFrame(-1, -2.0f));
        this.rv.setLayoutManager(new LinearScrollOffsetLayoutManager(context));
        this.rv.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                WalletSelectAbsDialog.this.lambda$initRv$3$WalletSelectAbsDialog(view, i);
            }
        });
        AnonymousClass2 r02 = new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return WalletSelectAbsDialog.this.isEnabled((PageHolder) holder);
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return WalletSelectAbsDialog.this.onCreateViewHolder(parent, viewType);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                WalletSelectAbsDialog walletSelectAbsDialog = WalletSelectAbsDialog.this;
                walletSelectAbsDialog.onBindViewHolder(this, (PageHolder) holder, position, walletSelectAbsDialog.getItem(position));
            }

            public int getItemCount() {
                return WalletSelectAbsDialog.this.getItemCount();
            }

            public int getItemViewType(int position) {
                return WalletSelectAbsDialog.this.getItemViewType(position);
            }
        };
        this.adapter = r02;
        this.rv.setAdapter(r02);
    }

    public /* synthetic */ void lambda$initRv$3$WalletSelectAbsDialog(View view12, int position) {
        this.selectPosition = position;
        RecyclerListView.SelectionAdapter selectionAdapter = this.adapter;
        if (selectionAdapter != null) {
            selectionAdapter.notifyDataSetChanged();
        }
        dismiss();
        OnItemClickListener<T> onItemClickListener2 = this.onItemClickListener;
        if (onItemClickListener2 != null) {
            onItemClickListener2.onItemClick(view12, position, getItem(position));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        RecyclerListView.SelectionAdapter selectionAdapter;
        super.onCreate(savedInstanceState);
        if (this.containerView != null) {
            this.containerView.setClipToPadding(false);
        }
        MryRoundButton mryRoundButton = this.viewDrag;
        if (mryRoundButton != null) {
            if (this.showDragView && mryRoundButton.getVisibility() != 0) {
                this.viewDrag.setVisibility(0);
            } else if (!this.showDragView && this.viewDrag.getVisibility() != 8) {
                this.viewDrag.setVisibility(8);
            }
        }
        View view = this.titleContainer;
        if (!(view == null || this.tvTitle == null)) {
            if (this.showTitleView && view.getVisibility() != 0) {
                this.titleContainer.setVisibility(0);
            } else if (!this.showTitleView && this.titleContainer.getVisibility() != 8) {
                this.titleContainer.setVisibility(8);
            }
        }
        View view2 = this.titleContainer;
        if (!(view2 == null || this.ivClose == null)) {
            if (this.showCloseView && view2.getVisibility() != 0) {
                this.titleContainer.setVisibility(0);
            } else if (!this.showCloseView && this.titleContainer.getVisibility() != 8) {
                this.titleContainer.setVisibility(8);
            }
        }
        MryFrameLayout mryFrameLayout = this.containerRv;
        if (mryFrameLayout != null) {
            if (this.showRv) {
                if (this.rvAutoHideWhenEmptyData && (((selectionAdapter = this.adapter) == null || selectionAdapter.getItemCount() == 0) && this.containerRv.getVisibility() != 8)) {
                    this.containerRv.setVisibility(8);
                } else if (this.containerRv.getVisibility() != 0) {
                    this.containerRv.setVisibility(0);
                }
            } else if (mryFrameLayout.getVisibility() != 8) {
                this.containerRv.setVisibility(8);
            }
        }
        MryLinearLayout mryLinearLayout = this.containerAddButton;
        if (mryLinearLayout != null) {
            if (this.showAddBtnView && mryLinearLayout.getVisibility() != 0) {
                this.containerAddButton.setVisibility(0);
            } else if (!this.showAddBtnView && this.containerAddButton.getVisibility() != 8) {
                this.containerAddButton.setVisibility(8);
            }
        }
        MryRoundButton mryRoundButton2 = this.btnConfirm;
        if (mryRoundButton2 == null) {
            return;
        }
        if (this.showConfirmBtnView && mryRoundButton2.getVisibility() != 0) {
            this.btnConfirm.setVisibility(0);
        } else if (!this.showConfirmBtnView && this.btnConfirm.getVisibility() != 8) {
            this.btnConfirm.setVisibility(8);
        }
    }

    public void setTitle(CharSequence value) {
        boolean z = !TextUtils.isEmpty(value);
        this.showTitleView = z;
        MryTextView mryTextView = this.tvTitle;
        if (mryTextView != null && z) {
            mryTextView.setText(value);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public boolean isEnabled(VH vh) {
        return false;
    }

    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public void onBindViewHolder(RecyclerListView.SelectionAdapter adapter2, VH holder, int position, T t) {
        boolean z = true;
        holder.setGone((int) R.id.divider, position == getItemCount() - 1 && getItemCount() > 3);
        if (position == this.selectPosition && !this.showListSelectIcon) {
            z = false;
        }
        holder.setInVisible((int) R.id.ivSelect, z);
        holder.setImageColorFilter((int) R.id.ivSelect, Theme.key_windowBackgroundWhiteBlueButton);
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getItemCount() {
        List<T> list = this.data;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public int getSelectPosition() {
        return this.selectPosition;
    }

    public T getItem(int position) {
        List<T> list = this.data;
        if (list == null || position < 0 || position >= list.size()) {
            return null;
        }
        return this.data.get(position);
    }

    public List<T> getData() {
        return this.data;
    }

    public D setTitles(CharSequence titles) {
        setTitle(titles);
        return this;
    }

    public D setAdapter(RecyclerListView.SelectionAdapter adapter2) {
        if (this.adapter != adapter2) {
            this.adapter = adapter2;
            RecyclerListView recyclerListView = this.rv;
            if (recyclerListView != null) {
                recyclerListView.setAdapter(adapter2);
            }
        }
        return this;
    }

    public D setData(List<T> data2) {
        this.data = data2;
        return this;
    }

    public D setSelectPosition(int selectPosition2) {
        this.selectPosition = selectPosition2;
        return this;
    }

    public D setShowDragView(boolean showDragView2) {
        this.showDragView = showDragView2;
        if (showDragView2) {
            setRecyclerViewContainerMargins(0, 20, 0, 0);
        }
        return this;
    }

    public D setShowTitleView(boolean showTitleView2) {
        this.showTitleView = showTitleView2;
        return this;
    }

    public D setShowCloseView(boolean showCloseView2) {
        this.showCloseView = showCloseView2;
        return this;
    }

    public D setShowRecyclerView(boolean showRv2) {
        this.showRv = showRv2;
        return this;
    }

    public D setShowSideBar(boolean showSideBar2) {
        this.showSideBar = showSideBar2;
        return this;
    }

    public D setShowAddButtonView(boolean showAddBtnView2) {
        this.showAddBtnView = showAddBtnView2;
        return this;
    }

    public D setShowConfirmButtonView(boolean showConfirmBtnView2) {
        this.showConfirmBtnView = showConfirmBtnView2;
        return this;
    }

    public D setShowListSelectIcon(boolean showListSelectIcon2) {
        this.showListSelectIcon = showListSelectIcon2;
        return this;
    }

    public D setRecyclerViewMinHeight(int recyclerViewMinHeight) {
        RecyclerListView recyclerListView = this.rv;
        if (recyclerListView != null) {
            recyclerListView.setMinimumHeight(recyclerViewMinHeight);
        }
        return this;
    }

    public D setRecyclerViewMaxHeight(int recyclerViewMaxHeight2) {
        this.recyclerViewMaxHeight = recyclerViewMaxHeight2;
        return this;
    }

    public D setRecyclerViewMargins(int margin) {
        return setRecyclerViewMargins(margin, margin, margin, margin);
    }

    public D setRecyclerViewMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        return setRecyclerViewMargins(leftMargin, topMargin, rightMargin, bottomMargin, true);
    }

    public D setRecyclerViewMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin, boolean isDpValue) {
        RecyclerListView recyclerListView = this.rv;
        if (recyclerListView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            lp.leftMargin = isDpValue ? AndroidUtilities.dp((float) leftMargin) : leftMargin;
            lp.topMargin = isDpValue ? AndroidUtilities.dp((float) topMargin) : topMargin;
            lp.rightMargin = isDpValue ? AndroidUtilities.dp((float) rightMargin) : rightMargin;
            lp.bottomMargin = isDpValue ? AndroidUtilities.dp((float) bottomMargin) : bottomMargin;
            this.rv.setLayoutParams(lp);
        }
        return this;
    }

    public D setRecyclerViewContainerMargins(int margin) {
        return setRecyclerViewContainerMargins(margin, margin, margin, margin, true);
    }

    public D setRecyclerViewContainerMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        return setRecyclerViewContainerMargins(leftMargin, topMargin, rightMargin, bottomMargin, true);
    }

    public D setRecyclerViewContainerMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin, boolean isDpValue) {
        MryFrameLayout mryFrameLayout = this.containerRv;
        if (mryFrameLayout != null) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mryFrameLayout.getLayoutParams();
            lp.leftMargin = isDpValue ? AndroidUtilities.dp((float) leftMargin) : leftMargin;
            lp.topMargin = isDpValue ? AndroidUtilities.dp((float) topMargin) : topMargin;
            lp.rightMargin = isDpValue ? AndroidUtilities.dp((float) rightMargin) : rightMargin;
            lp.bottomMargin = isDpValue ? AndroidUtilities.dp((float) bottomMargin) : bottomMargin;
            this.containerRv.setLayoutParams(lp);
        }
        return this;
    }

    public D setRvAutoHideWhenEmptyData(boolean rvAutoHideWhenEmptyData2) {
        this.rvAutoHideWhenEmptyData = rvAutoHideWhenEmptyData2;
        return this;
    }

    public D setOnItemClickListener(OnItemClickListener<T> onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
        return this;
    }

    public D setOnConfrimClickListener(OnConfirmClickListener<T, D> onConfrimClickListener2) {
        this.onConfrimClickListener = onConfrimClickListener2;
        return this;
    }

    public D setOnAddButtonClickListener(OnAddButtonClickListener<D> onAddButtonClickListener2) {
        this.onAddButtonClickListener = onAddButtonClickListener2;
        return this;
    }

    public MryRoundButton getDragView() {
        return this.viewDrag;
    }

    public View getTitleContainerView() {
        return this.titleContainer;
    }

    public ImageView getCloseView() {
        return this.ivClose;
    }

    public MryTextView getTitleTextView() {
        return this.tvTitle;
    }

    public MryFrameLayout getRecyclerViewContainerView() {
        return this.containerRv;
    }

    public RecyclerListView getRecyclerView() {
        return this.rv;
    }

    public MryLinearLayout getAddButtonContainerView() {
        return this.containerAddButton;
    }

    public MryTextView getAddButtonTextView() {
        return this.tvAdd;
    }

    public MryRoundButton getConfirmButton() {
        return this.btnConfirm;
    }

    public void destroy() {
        if (this.containerView != null) {
            this.containerView.removeAllViews();
        }
        if (this.container != null) {
            this.container.removeAllViews();
        }
        MryFrameLayout mryFrameLayout = this.containerRv;
        if (mryFrameLayout != null) {
            mryFrameLayout.removeAllViews();
        }
        RecyclerListView recyclerListView = this.rv;
        if (recyclerListView != null) {
            recyclerListView.setLayoutManager((RecyclerView.LayoutManager) null);
            this.rv.setAdapter((RecyclerView.Adapter) null);
            this.rv.setOnScrollListener((RecyclerView.OnScrollListener) null);
        }
        MryLinearLayout mryLinearLayout = this.containerAddButton;
        if (mryLinearLayout != null) {
            mryLinearLayout.removeAllViews();
        }
        this.titleContainer = null;
        this.ivClose = null;
        this.tvTitle = null;
        this.containerRv = null;
        this.rv = null;
        this.containerAddButton = null;
        this.tvAdd = null;
        this.btnConfirm = null;
        this.adapter = null;
        this.data = null;
        this.onItemClickListener = null;
        this.onConfrimClickListener = null;
        this.onAddButtonClickListener = null;
        this.selectPosition = 0;
        this.recyclerViewMaxHeight = 0;
    }
}
