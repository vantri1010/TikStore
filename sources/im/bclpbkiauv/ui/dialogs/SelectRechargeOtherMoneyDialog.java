package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.ScreenUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryRoundButtonDrawable;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.List;

public class SelectRechargeOtherMoneyDialog extends BottomSheet {
    private SelectRechargeOtherMoneyCallBack callBack;
    private PageSelectionAdapter<Object, PageHolder> mAdapter;
    private String mOriginalMoney;
    /* access modifiers changed from: private */
    public int mSelectIndex;
    private RecyclerListView rv;

    public interface SelectRechargeOtherMoneyCallBack {
        void onItemSelected(SelectRechargeOtherMoneyDialog selectRechargeOtherMoneyDialog, Object obj, int i);
    }

    public SelectRechargeOtherMoneyDialog(Context context, int selectIndex) {
        this(context, true, selectIndex);
    }

    public SelectRechargeOtherMoneyDialog(Context context, boolean needFocus, int selectIndex) {
        this(context, needFocus, 1, selectIndex);
    }

    public SelectRechargeOtherMoneyDialog(Context context, boolean needFocus, int backgroundType, int selectIndex) {
        super(context, needFocus, backgroundType);
        this.mSelectIndex = -1;
        this.mSelectIndex = selectIndex;
    }

    public void setOriginalMoney(String originalMoney) {
        this.mOriginalMoney = originalMoney;
    }

    /* access modifiers changed from: protected */
    public void init(Context context, boolean needFocus, int backgroundType) {
        super.init(context, needFocus, backgroundType);
        setApplyBottomPadding(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_wallet_select_recharge_other_money, (ViewGroup) null);
        setCustomView(view);
        setApplyBottomPadding(false);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable());
        window.setGravity(17);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth();
        lp.height = (ScreenUtils.getScreenHeight() / 4) * 3;
        window.setAttributes(lp);
        initView(view);
    }

    private void initView(View view) {
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        ImageView ivBack = (ImageView) view.findViewById(R.id.ivBack);
        MryTextView tvTitle = (MryTextView) view.findViewById(R.id.tvTitle);
        MryTextView tvSubTitle = (MryTextView) view.findViewById(R.id.tvSubTitle);
        this.rv = (RecyclerListView) view.findViewById(R.id.rv);
        MryRoundButton btn = (MryRoundButton) view.findViewById(R.id.btn);
        ivBack.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        tvTitle.setBold();
        if (!TextUtils.isEmpty(this.mOriginalMoney)) {
            tvTitle.setText("₫" + this.mOriginalMoney + LocaleController.getString(R.string.SelectRechargeOtherMoneyDialogTitle));
        }
        tvSubTitle.setTextColor(Theme.key_windowBackgroundWhiteGrayText3);
        btn.setPrimaryRadiusAdjustBoundsFillStyle();
        ivBack.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SelectRechargeOtherMoneyDialog.this.lambda$initView$0$SelectRechargeOtherMoneyDialog(view);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SelectRechargeOtherMoneyDialog.this.lambda$initView$1$SelectRechargeOtherMoneyDialog(view);
            }
        });
        this.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        AnonymousClass1 r4 = new PageSelectionAdapter<Object, PageHolder>(getContext()) {
            public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
                return new PageHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_wallet_recharge_edtion_2_select_money, parent, false));
            }

            public void onBindViewHolderForChild(PageHolder holder, int position, Object item) {
                if (item != null) {
                    holder.setGone((int) R.id.iv, SelectRechargeOtherMoneyDialog.this.mSelectIndex != position);
                    Drawable ivBg = holder.getView(R.id.iv).getBackground();
                    if (ivBg != null) {
                        ivBg.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton), PorterDuff.Mode.SRC_IN);
                    }
                    MryRoundButtonDrawable bg = new MryRoundButtonDrawable();
                    bg.setStrokeWidth(AndroidUtilities.dp(0.5f));
                    bg.setIsRadiusAdjustBounds(false);
                    bg.setCornerRadius((float) AndroidUtilities.dp(5.0f));
                    if (SelectRechargeOtherMoneyDialog.this.mSelectIndex == position) {
                        bg.setStrokeColors(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
                        bg.setBgData(ColorStateList.valueOf(AndroidUtilities.alphaColor(0.1f, Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton))));
                        holder.setTextColor((int) R.id.tv, Theme.key_windowBackgroundWhiteBlueText);
                    } else {
                        bg.setStrokeColors(ColorStateList.valueOf(Theme.getColor(Theme.key_dialogGrayLine)));
                        bg.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhite)));
                        holder.setTextColor((int) R.id.tv, Theme.key_windowBackgroundWhiteBlackText);
                    }
                    holder.itemView.setBackground(bg);
                    holder.setText((int) R.id.tv, (CharSequence) item.toString());
                }
            }
        };
        this.mAdapter = r4;
        r4.setShowLoadMoreViewEnable(false);
        this.rv.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SelectRechargeOtherMoneyDialog.this.lambda$initView$2$SelectRechargeOtherMoneyDialog(view, i);
            }
        });
        this.rv.setAdapter(this.mAdapter);
    }

    public /* synthetic */ void lambda$initView$0$SelectRechargeOtherMoneyDialog(View v) {
        dismiss();
    }

    public /* synthetic */ void lambda$initView$1$SelectRechargeOtherMoneyDialog(View v) {
        PageSelectionAdapter<Object, PageHolder> pageSelectionAdapter;
        int i;
        if (this.callBack != null && (pageSelectionAdapter = this.mAdapter) != null && (i = this.mSelectIndex) > 0 && i < pageSelectionAdapter.getData().size()) {
            this.callBack.onItemSelected(this, this.mAdapter.getData().get(this.mSelectIndex), this.mSelectIndex);
        }
    }

    public /* synthetic */ void lambda$initView$2$SelectRechargeOtherMoneyDialog(View view1, int position) {
        this.mSelectIndex = position;
        PageSelectionAdapter<Object, PageHolder> pageSelectionAdapter = this.mAdapter;
        if (pageSelectionAdapter != null) {
            pageSelectionAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return true;
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }

    public void setData(List<Object> list) {
        PageSelectionAdapter<Object, PageHolder> pageSelectionAdapter = this.mAdapter;
        if (pageSelectionAdapter != null) {
            pageSelectionAdapter.setData(list);
        }
    }

    public void setCallBack(SelectRechargeOtherMoneyCallBack callBack2) {
        this.callBack = callBack2;
    }
}
