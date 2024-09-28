package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.blankj.utilcode.util.ColorUtils;
import com.king.zxing.util.CodeUtils;
import com.tablayout.SlidingScaleTabLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AppTextView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SimpleTextWatcher;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.NoScrollViewPager;
import im.bclpbkiauv.ui.load.SpinKitView;
import im.bclpbkiauv.ui.wallet.WalletTopUpActivity;
import im.bclpbkiauv.ui.wallet.cell.BtnChargeCell;
import im.bclpbkiauv.ui.wallet.model.AmountRulesBean;
import im.bclpbkiauv.ui.wallet.model.ChargeResBean;
import im.bclpbkiauv.ui.wallet.model.Constants;
import im.bclpbkiauv.ui.wallet.model.PayChannelBean;
import im.bclpbkiauv.ui.wallet.model.PayChannelsResBean;
import im.bclpbkiauv.ui.wallet.model.PayTypeListBean;
import im.bclpbkiauv.ui.wallet.utils.AnimationUtils;
import im.bclpbkiauv.ui.wallet.utils.ExceptionUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WalletTopUpActivity extends BaseFragment {
    private Adapter adapter;
    private AppTextView btnEmpty;
    private LinearLayout container;
    private LinearLayout emptyLayout;
    private ImageView ivEmpty;
    private SpinKitView loadView;
    /* access modifiers changed from: private */
    public boolean loadingPayChannels;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    /* access modifiers changed from: private */
    public ArrayList<PayChannelBean> payList = new ArrayList<>();
    private SlidingScaleTabLayout tabLayout;
    private TextView tvDesc;
    private TextView tvEmpty;
    /* access modifiers changed from: private */
    public NoScrollViewPager viewPager;

    public boolean canBeginSlide() {
        ArrayList<PayChannelBean> arrayList = this.payList;
        if (arrayList == null || arrayList.size() == 0 || this.viewPager.getCurrentItem() == 0) {
            return true;
        }
        return false;
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_top_up_layout, (ViewGroup) null, false);
        initActionBar();
        initViews();
        showLoading();
        loadPayChannels();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.TopUp));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletTopUpActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.emptyLayout = (LinearLayout) this.fragmentView.findViewById(R.id.emptyLayout);
        this.loadView = (SpinKitView) this.fragmentView.findViewById(R.id.loadView);
        this.ivEmpty = (ImageView) this.fragmentView.findViewById(R.id.ivEmpty);
        this.tvEmpty = (TextView) this.fragmentView.findViewById(R.id.tvEmpty);
        this.tvDesc = (TextView) this.fragmentView.findViewById(R.id.tvDesc);
        this.btnEmpty = (AppTextView) this.fragmentView.findViewById(R.id.btnEmpty);
        this.container = (LinearLayout) this.fragmentView.findViewById(R.id.container);
        this.tabLayout = (SlidingScaleTabLayout) this.fragmentView.findViewById(R.id.tabLayout);
        this.viewPager = (NoScrollViewPager) this.fragmentView.findViewById(R.id.viewPager);
        this.tabLayout.setTextUnSelectColor(ColorUtils.getColor(R.color.tab_normal_text));
        this.tabLayout.setTextSelectColor(ColorUtils.getColor(R.color.tab_active_text));
        this.tabLayout.setIndicatorColor(ColorUtils.getColor(R.color.tab_indicator));
        this.viewPager.setEnScroll(true);
        this.viewPager.setOffscreenPageLimit(1);
        NoScrollViewPager noScrollViewPager = this.viewPager;
        AnonymousClass2 r1 = new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                WalletTopUpActivity.this.notifyInnerRvAdapter(position);
            }

            public void onPageScrollStateChanged(int state) {
            }
        };
        this.onPageChangeListener = r1;
        noScrollViewPager.addOnPageChangeListener(r1);
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        this.viewPager.setAdapter(adapter2);
        this.tabLayout.setViewPager(this.viewPager);
    }

    private void showLoading() {
        this.container.setVisibility(8);
        this.btnEmpty.setVisibility(8);
        this.tvDesc.setVisibility(8);
        this.emptyLayout.setVisibility(0);
        this.loadView.setVisibility(0);
        this.tvEmpty.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
        this.tvEmpty.setText(LocaleController.getString(R.string.NowLoading));
        this.ivEmpty.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showError() {
        this.emptyLayout.setVisibility(0);
        this.container.setVisibility(8);
        this.tvDesc.setVisibility(8);
        this.loadView.setVisibility(8);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.emptyLayout);
        this.ivEmpty.setVisibility(0);
        this.ivEmpty.setImageResource(R.mipmap.ic_data_ex);
        this.btnEmpty.setVisibility(0);
        this.tvEmpty.setText(LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
        this.tvEmpty.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvDesc.setText(LocaleController.getString(R.string.ClickTheButtonToTryAgain));
        this.btnEmpty.setText(LocaleController.getString(R.string.Refresh));
    }

    /* access modifiers changed from: private */
    public void showContainer() {
        this.emptyLayout.setVisibility(8);
        this.container.setVisibility(0);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.container);
    }

    private void loadPayChannels() {
        if (!this.loadingPayChannels) {
            this.loadingPayChannels = true;
            TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
            builder.setBusinessKey(Constants.KEY_PAY_CHANNELS);
            builder.addParam("belongType", "topup");
            builder.addParam("company", "Yixin");
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(builder.build(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WalletTopUpActivity.this.lambda$loadPayChannels$0$WalletTopUpActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$loadPayChannels$0$WalletTopUpActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                boolean unused = WalletTopUpActivity.this.loadingPayChannels = false;
                if (error != null) {
                    WalletTopUpActivity.this.showError();
                    ExceptionUtils.handlePayChannelException(error.text);
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) tLObject).data, (Class<?>) PayChannelsResBean.class);
                    if (parse.isSuccess()) {
                        WalletTopUpActivity.this.showContainer();
                        List modelList = parse.modelList;
                        if (modelList != null || !modelList.isEmpty()) {
                            WalletTopUpActivity.this.parsePayChannel(modelList);
                            return;
                        }
                        return;
                    }
                    WalletTopUpActivity.this.showError();
                    ExceptionUtils.handlePayChannelException(parse.message);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void parsePayChannel(List<PayChannelsResBean> modelList) {
        ArrayList<PayTypeListBean> payTypeList;
        if (modelList != null && !modelList.isEmpty()) {
            for (int i = 0; i < modelList.size(); i++) {
                PayChannelsResBean payChannelsResBean = modelList.get(i);
                if (!(payChannelsResBean == null || payChannelsResBean.getPayTypeList() == null || payChannelsResBean.getPayTypeList().isEmpty() || (payTypeList = payChannelsResBean.getPayTypeList()) == null || payTypeList.isEmpty())) {
                    for (int j = 0; j < payTypeList.size(); j++) {
                        PayChannelBean bean = new PayChannelBean();
                        bean.setChannelCode(payChannelsResBean.getChannelCode());
                        bean.setPayType(payTypeList.get(j));
                        this.payList.add(bean);
                    }
                }
            }
            notifyAdapter();
        }
    }

    private void notifyAdapter() {
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
            this.viewPager.setOffscreenPageLimit(this.adapter.getCount());
        }
        SlidingScaleTabLayout slidingScaleTabLayout = this.tabLayout;
        if (slidingScaleTabLayout != null) {
            slidingScaleTabLayout.notifyDataSetChanged();
        }
        notifyInnerRvAdapter(this.viewPager.getCurrentItem());
    }

    /* access modifiers changed from: private */
    public void notifyInnerRvAdapter(int position) {
        ArrayList<PayChannelBean> arrayList;
        if (this.adapter != null && (arrayList = this.payList) != null && position < arrayList.size()) {
            this.fragmentView.postDelayed(new Runnable(position, this.payList.get(position)) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ PayChannelBean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    WalletTopUpActivity.this.lambda$notifyInnerRvAdapter$1$WalletTopUpActivity(this.f$1, this.f$2);
                }
            }, 200);
        }
    }

    public /* synthetic */ void lambda$notifyInnerRvAdapter$1$WalletTopUpActivity(int position, PayChannelBean itemData) {
        InnerPage innerPage = this.adapter.getItem(position);
        if (innerPage != null) {
            innerPage.setItemData(itemData);
        }
        this.adapter.setData(itemData);
    }

    public class InnerPage extends FrameLayout {
        /* access modifiers changed from: private */
        public NumberAdapter adapter;
        private AppTextView btn;
        /* access modifiers changed from: private */
        public EditText etMoney;
        private TextWatcher etWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                InnerPage.this.updateBtnEnable();
            }
        };
        /* access modifiers changed from: private */
        public PayChannelBean itemData;
        private RecyclerListView listView;

        public InnerPage(Context context) {
            super(context);
            inflate(context, R.layout.wallet_recharge_inner_page, this);
            this.etMoney = (EditText) findViewById(R.id.etMoney);
            this.listView = (RecyclerListView) findViewById(R.id.listView);
            this.btn = (AppTextView) findViewById(R.id.btn);
            this.listView.setLayoutManager(new GridLayoutManager(context, 3));
            RecyclerListView recyclerListView = this.listView;
            NumberAdapter numberAdapter = new NumberAdapter(context);
            this.adapter = numberAdapter;
            recyclerListView.setAdapter(numberAdapter);
            this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(WalletTopUpActivity.this) {
                public void onItemClick(View view, int position) {
                    InnerPage.this.etMoney.setText(String.valueOf(InnerPage.this.adapter.getValue(position)));
                    InnerPage.this.etMoney.setSelection(InnerPage.this.etMoney.getText().toString().length());
                    InnerPage.this.clearChecked();
                    if (view instanceof BtnChargeCell) {
                        ((BtnChargeCell) view).setChecked(true);
                    }
                }
            });
            this.etMoney.addTextChangedListener(new SimpleTextWatcher(WalletTopUpActivity.this) {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    InnerPage.this.setBtnEnable(!TextUtils.isEmpty(s) && InnerPage.this.itemData != null);
                }
            });
            this.btn.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WalletTopUpActivity.InnerPage.this.lambda$new$0$WalletTopUpActivity$InnerPage(view);
                }
            });
            setBtnEnable(false);
            updateViewData();
        }

        public /* synthetic */ void lambda$new$0$WalletTopUpActivity$InnerPage(View v) {
            PayTypeListBean payType;
            if (AndroidUtilities.isKeyboardShowed(this.etMoney)) {
                AndroidUtilities.hideKeyboard(this.etMoney);
            }
            PayChannelBean payChannelBean = this.itemData;
            if (payChannelBean != null && (payType = payChannelBean.getPayType()) != null) {
                String bigAmount = new BigDecimal(this.etMoney.getText().toString().trim()).multiply(new BigDecimal("100")).toString();
                AmountRulesBean amountRules = payType.getAmountRules();
                if (amountRules != null && !TextUtils.isEmpty(bigAmount)) {
                    String maxAmount = amountRules.getMaxAmount();
                    String minAmount = amountRules.getMinAmount();
                    if (!"0".equals(maxAmount) && !TextUtils.isEmpty(maxAmount) && new BigDecimal(bigAmount).compareTo(new BigDecimal(maxAmount).multiply(new BigDecimal("100"))) > 0) {
                        ToastUtils.show((CharSequence) "最大值" + maxAmount);
                        return;
                    } else if (!"0".equals(minAmount) && !TextUtils.isEmpty(minAmount) && new BigDecimal(bigAmount).compareTo(new BigDecimal(minAmount).multiply(new BigDecimal("100"))) < 0) {
                        ToastUtils.show((CharSequence) "最小值" + minAmount);
                        return;
                    }
                }
                doCharge(bigAmount);
            }
        }

        private void doCharge(String amount) {
            TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
            builder.setBusinessKey(Constants.KEY_PAY_CHARGE);
            builder.addParam("amount", amount);
            builder.addParam("userId", Integer.valueOf(WalletTopUpActivity.this.getUserConfig().clientUserId));
            builder.addParam("channelCode", this.itemData.getChannelCode());
            builder.addParam("payType", this.itemData.getPayType().getPayType());
            WalletTopUpActivity.this.getConnectionsManager().sendRequest(builder.build(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WalletTopUpActivity.InnerPage.this.lambda$doCharge$1$WalletTopUpActivity$InnerPage(tLObject, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$doCharge$1$WalletTopUpActivity$InnerPage(TLObject response, TLRPC.TL_error error) {
            if (error != null) {
                ExceptionUtils.handlePayChannelException(error.text);
            } else if (response instanceof TLRPCWallet.TL_paymentTransResult) {
                TLApiModel<ChargeResBean> parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) response).data, (Class<?>) ChargeResBean.class);
                if (parse.isSuccess()) {
                    final ChargeResBean model = (ChargeResBean) parse.model;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            WalletTopUpActivity.this.presentFragment(new WalletRechargeH5Activity(1, model.getAction()), true);
                        }
                    });
                } else if ("-1".equals(parse.code)) {
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.RechargeFailed));
                } else {
                    ExceptionUtils.handlePayChannelException(parse.message);
                }
            }
        }

        /* access modifiers changed from: private */
        public void setBtnEnable(boolean enable) {
            this.btn.setEnabled(enable);
            if (enable) {
                this.btn.setTextColor(ColorUtils.getColor(R.color.text_white_color));
                this.btn.setBackgroundResource(R.drawable.btn_primary_selector);
                return;
            }
            this.btn.setTextColor(ColorUtils.getColor(R.color.text_secondary_color));
            this.btn.setBackgroundResource(R.drawable.shape_rect_round_white);
        }

        /* access modifiers changed from: private */
        public void clearChecked() {
            int childCount = this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof BtnChargeCell) {
                    ((BtnChargeCell) childAt).setChecked(false);
                }
            }
        }

        private void updateViewData() {
            if (WalletTopUpActivity.this.getWalletController().getAccountInfo() != null) {
                updateBtnEnable();
            }
        }

        /* access modifiers changed from: private */
        public void updateBtnEnable() {
            AppTextView appTextView = this.btn;
            if (appTextView != null && this.etMoney != null) {
                appTextView.setEnabled(false);
            }
        }

        /* access modifiers changed from: package-private */
        public void setItemData(PayChannelBean itemData2) {
            this.itemData = itemData2;
            if (itemData2 != null && itemData2.getPayType() != null) {
                PayTypeListBean payType = itemData2.getPayType();
                if (payType.getAmountRules() != null) {
                    if (payType.getAmountRules().getSelf() == 1) {
                        this.etMoney.setEnabled(true);
                        this.etMoney.setHint(LocaleController.getString(R.string.PleaseInputRechargeMoneyAmount));
                    } else {
                        this.etMoney.setEnabled(false);
                        this.etMoney.setHint(LocaleController.getString(R.string.PleaseSelectRechargeMoneyAmount));
                    }
                    String amount = payType.getAmountRules().getAmount();
                    ArrayList<Integer> integers = new ArrayList<>();
                    if (TextUtils.isEmpty(amount) || "0".equals(amount)) {
                        integers.add(50);
                        integers.add(100);
                        integers.add(Integer.valueOf(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION));
                        integers.add(500);
                        integers.add(Integer.valueOf(CodeUtils.DEFAULT_REQ_HEIGHT));
                        integers.add(1000);
                    } else {
                        String[] split = amount.split(",");
                        if (split != null && split.length > 0) {
                            for (String parseInt : split) {
                                integers.add(Integer.valueOf(Integer.parseInt(parseInt)));
                            }
                        }
                    }
                    NumberAdapter numberAdapter = this.adapter;
                    if (numberAdapter != null) {
                        numberAdapter.setNumberList(integers);
                        this.adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public String getInputText() {
            EditText editText = this.etMoney;
            return (editText == null || editText.getText() == null) ? "" : this.etMoney.getText().toString().trim();
        }

        /* access modifiers changed from: package-private */
        public void onDestroy() {
            TextWatcher textWatcher;
            removeAllViews();
            EditText editText = this.etMoney;
            if (!(editText == null || (textWatcher = this.etWatcher) == null)) {
                editText.removeTextChangedListener(textWatcher);
                this.etWatcher = null;
            }
            this.btn = null;
            this.etMoney = null;
            this.adapter = null;
        }
    }

    private class NumberAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<Integer> numberList = new ArrayList<>();

        public NumberAdapter(Context mContext2) {
            this.mContext = mContext2;
        }

        public void setNumberList(ArrayList<Integer> numberList2) {
            this.numberList = numberList2;
        }

        public Integer getValue(int position) {
            return this.numberList.get(position);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new BtnChargeCell(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((BtnChargeCell) holder.itemView).setText(this.numberList.get(position) + LocaleController.getString(R.string.UnitMoneyYuan));
        }

        public int getItemCount() {
            return this.numberList.size();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }
    }

    private class Adapter extends PagerAdapter {
        private final SparseArray<InnerPage> viewCaches = new SparseArray<>();

        public Adapter() {
        }

        /* access modifiers changed from: package-private */
        public InnerPage getItem(int position) {
            return this.viewCaches.get(position);
        }

        /* access modifiers changed from: package-private */
        public void setData(PayChannelBean itemData) {
            if (WalletTopUpActivity.this.viewPager != null && itemData != null) {
            }
        }

        public CharSequence getPageTitle(int position) {
            return (WalletTopUpActivity.this.payList == null || position >= WalletTopUpActivity.this.payList.size()) ? "" : ((PayChannelBean) WalletTopUpActivity.this.payList.get(position)).getPayType().getName();
        }

        public int getCount() {
            if (WalletTopUpActivity.this.payList != null) {
                return WalletTopUpActivity.this.payList.size();
            }
            return 0;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            InnerPage innerCell = getItem(position);
            if (innerCell == null) {
                innerCell = new InnerPage(container.getContext());
                this.viewCaches.put(position, innerCell);
            }
            if (innerCell.getParent() != null) {
                ((ViewGroup) innerCell.getParent()).removeView(innerCell);
            }
            AndroidUtilities.showKeyboard(innerCell.etMoney);
            container.addView(innerCell, 0);
            return innerCell;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        /* access modifiers changed from: package-private */
        public void onDestroy() {
            for (int i = 0; i < this.viewCaches.size(); i++) {
                InnerPage page = this.viewCaches.get(i);
                if (page != null) {
                    page.onDestroy();
                }
            }
            this.viewCaches.clear();
        }
    }
}
