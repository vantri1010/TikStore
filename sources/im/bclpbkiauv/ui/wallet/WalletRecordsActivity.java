package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.library.MyRecyclerViewList;
import com.library.PowerfulStickyDecoration;
import com.library.listener.OnGroupClickListener;
import com.library.listener.PowerGroupListener;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.components.AppTextView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.dialogs.TimeWheelPickerDialog;
import im.bclpbkiauv.ui.hviews.MryLinearLayout;
import im.bclpbkiauv.ui.load.SpinKitView;
import im.bclpbkiauv.ui.load.SpriteFactory;
import im.bclpbkiauv.ui.load.Style;
import im.bclpbkiauv.ui.utils.number.MoneyUtil;
import im.bclpbkiauv.ui.wallet.WalletRecordsActivity;
import im.bclpbkiauv.ui.wallet.model.BillRecordResBillListBean;
import im.bclpbkiauv.ui.wallet.model.BillRecordsReqBean;
import im.bclpbkiauv.ui.wallet.model.BillRecordsResBean;
import im.bclpbkiauv.ui.wallet.model.Constants;
import im.bclpbkiauv.ui.wallet.utils.AnimationUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WalletRecordsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public HashMap<String, BillRecordsResBean> beanMap = new HashMap<>();
    private AppTextView btn;
    private LinearLayout container;
    /* access modifiers changed from: private */
    public int currentPage = 1;
    /* access modifiers changed from: private */
    public ArrayList<String> dateKeyLis = new ArrayList<>();
    /* access modifiers changed from: private */
    public PowerfulStickyDecoration decoration;
    private View emptyDivider;
    private MryLinearLayout emptyLayout;
    private boolean end = false;
    /* access modifiers changed from: private */
    public MyRecyclerViewList listView;
    private SpinKitView loadView;
    /* access modifiers changed from: private */
    public int pageSize = 20;
    /* access modifiers changed from: private */
    public SmartRefreshLayout refreshLayout;
    /* access modifiers changed from: private */
    public Date selectDate;
    /* access modifiers changed from: private */
    public String selectDateStr = "";
    private LinearLayout selectLayout;
    /* access modifiers changed from: private */
    public String tempKey = "";
    private LinearLayout tipLayout;
    private TextView tvEmptyIn;
    private TextView tvEmptyOut;
    /* access modifiers changed from: private */
    public TextView tvSelectDate2;
    private TextView tvTips;

    static /* synthetic */ int access$208(WalletRecordsActivity x0) {
        int i = x0.currentPage;
        x0.currentPage = i + 1;
        return i;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_records_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        showLoading();
        this.fragmentView.postDelayed(new Runnable() {
            public final void run() {
                WalletRecordsActivity.this.lambda$createView$0$WalletRecordsActivity();
            }
        }, 1000);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$WalletRecordsActivity() {
        loadRecords(this.selectDateStr, this.currentPage, this.pageSize, false, true);
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.TransactionDetails2));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletRecordsActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.refreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.refreshLayout);
        this.loadView = (SpinKitView) this.fragmentView.findViewById(R.id.loadView);
        this.emptyLayout = (MryLinearLayout) this.fragmentView.findViewById(R.id.emptyLayout);
        this.tipLayout = (LinearLayout) this.fragmentView.findViewById(R.id.tipLayout);
        this.tvEmptyIn = (TextView) this.fragmentView.findViewById(R.id.tvEmptyIn);
        this.tvEmptyOut = (TextView) this.fragmentView.findViewById(R.id.tvEmptyOut);
        this.emptyDivider = this.fragmentView.findViewById(R.id.emptyDivider);
        this.tvTips = (TextView) this.fragmentView.findViewById(R.id.tvTips);
        this.btn = (AppTextView) this.fragmentView.findViewById(R.id.btn);
        this.selectLayout = (LinearLayout) this.fragmentView.findViewById(R.id.selectLayout);
        this.tvSelectDate2 = (TextView) this.fragmentView.findViewById(R.id.tvSelectDate2);
        this.container = (LinearLayout) this.fragmentView.findViewById(R.id.container);
        this.listView = (MyRecyclerViewList) this.fragmentView.findViewById(R.id.listView);
        this.loadView.setColor(-16744193);
        this.loadView.setIndeterminateDrawable(SpriteFactory.create(Style.CIRCLE));
        SpanUtils.with(this.tvEmptyIn).append(LocaleController.getString(R.string.IncomeFormat)).append("₫").setTypeface(Typeface.MONOSPACE).append("0.00").create();
        SpanUtils.with(this.tvEmptyOut).append(LocaleController.getString(R.string.ExpenditureFormat)).append("₫").setTypeface(Typeface.MONOSPACE).append("0.00").create();
        this.selectLayout.setBackground(Theme.getSelectorDrawable(false));
        this.btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WalletRecordsActivity.this.showLoading();
                WalletRecordsActivity walletRecordsActivity = WalletRecordsActivity.this;
                walletRecordsActivity.loadRecords(walletRecordsActivity.selectDateStr, WalletRecordsActivity.this.currentPage, WalletRecordsActivity.this.pageSize, false, true);
            }
        });
        rebuildSticky();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getParentActivity());
        this.adapter = new ListAdapter(getParentActivity());
        this.listView.setEmptyView(this.tipLayout);
        this.listView.setLayoutManager(layoutManager);
        this.listView.addItemDecoration(this.decoration);
        this.listView.setAdapter(this.adapter);
        if (this.selectDate == null) {
            this.selectDate = Calendar.getInstance().getTime();
        }
        this.selectDateStr = TimeUtils.millis2String(this.selectDate.getTime(), "yyyy-MM");
        this.tempKey = TimeUtils.millis2String(this.selectDate.getTime(), "yyyy/MM");
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                int section = WalletRecordsActivity.this.adapter.getSectionForPosition(position);
                int row = WalletRecordsActivity.this.adapter.getPositionInSectionForPosition(position);
                if (row >= 0 && section >= 0) {
                    BillRecordResBillListBean billRecordResBillListBean = ((BillRecordsResBean) WalletRecordsActivity.this.beanMap.get((String) WalletRecordsActivity.this.dateKeyLis.get(section))).getBillList().get(row);
                    if (billRecordResBillListBean.getOrderType() == 1) {
                        WalletRecordWithdrawDetailActivity fragment = new WalletRecordWithdrawDetailActivity();
                        fragment.setBean(billRecordResBillListBean);
                        WalletRecordsActivity.this.presentFragment(fragment);
                    } else if (billRecordResBillListBean.getOrderType() == 3) {
                        WalletRecordWithdrawReturnDetailActivity fragment2 = new WalletRecordWithdrawReturnDetailActivity();
                        fragment2.setBean(billRecordResBillListBean);
                        WalletRecordsActivity.this.presentFragment(fragment2);
                    } else {
                        WalletRecordDetailActivity fragment3 = new WalletRecordDetailActivity();
                        fragment3.setBean(billRecordResBillListBean);
                        WalletRecordsActivity.this.presentFragment(fragment3);
                    }
                }
            }
        });
        this.tvSelectDate2.setText(TimeUtils.millis2String(this.selectDate.getTime(), "yyyy/MM"));
        this.selectLayout.setEnabled(true);
        this.selectLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TimeWheelPickerDialog.Builder builder = TimeWheelPickerDialog.getDefaultBuilder(WalletRecordsActivity.this.getParentActivity(), new OnTimeSelectListener() {
                    public final void onTimeSelect(Date date, View view) {
                        WalletRecordsActivity.AnonymousClass4.this.lambda$onClick$0$WalletRecordsActivity$4(date, view);
                    }
                });
                if (WalletRecordsActivity.this.selectDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(WalletRecordsActivity.this.selectDate);
                    builder.setDate(calendar);
                } else {
                    builder.setDate(Calendar.getInstance());
                }
                builder.setType(new boolean[]{true, true, false, false, false, false});
                WalletRecordsActivity.this.showDialog(builder.build());
            }

            public /* synthetic */ void lambda$onClick$0$WalletRecordsActivity$4(Date date, View v) {
                Date unused = WalletRecordsActivity.this.selectDate = date;
                String selectStr = TimeUtils.millis2String(WalletRecordsActivity.this.selectDate.getTime(), "yyyy-MM");
                if (!WalletRecordsActivity.this.selectDateStr.equals(selectStr)) {
                    String unused2 = WalletRecordsActivity.this.selectDateStr = selectStr;
                    int unused3 = WalletRecordsActivity.this.currentPage = 1;
                    WalletRecordsActivity.this.tvSelectDate2.setText(TimeUtils.millis2String(WalletRecordsActivity.this.selectDate.getTime(), "yyyy/MM"));
                    WalletRecordsActivity walletRecordsActivity = WalletRecordsActivity.this;
                    String unused4 = walletRecordsActivity.tempKey = TimeUtils.millis2String(walletRecordsActivity.selectDate.getTime(), "yyyy/MM");
                    if (WalletRecordsActivity.this.decoration != null) {
                        WalletRecordsActivity.this.decoration.clearCache();
                    }
                    WalletRecordsActivity walletRecordsActivity2 = WalletRecordsActivity.this;
                    walletRecordsActivity2.loadRecordsBySelected(walletRecordsActivity2.selectDateStr, WalletRecordsActivity.this.currentPage, WalletRecordsActivity.this.pageSize);
                }
            }
        });
        this.refreshLayout.setEnableAutoLoadMore(true);
        this.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (WalletRecordsActivity.this.decoration != null) {
                    WalletRecordsActivity.this.decoration.clearCache();
                }
                WalletRecordsActivity walletRecordsActivity = WalletRecordsActivity.this;
                walletRecordsActivity.loadRecords(walletRecordsActivity.selectDateStr, WalletRecordsActivity.this.currentPage, WalletRecordsActivity.this.pageSize, false, true);
            }

            public void onRefresh(RefreshLayout refreshLayout) {
                if (WalletRecordsActivity.this.selectDate != null) {
                    if (WalletRecordsActivity.this.decoration != null) {
                        WalletRecordsActivity.this.decoration.clearCache();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(WalletRecordsActivity.this.selectDate);
                    calendar.add(2, 1);
                    if (calendar.compareTo(Calendar.getInstance()) > 0) {
                        refreshLayout.finishRefresh();
                        return;
                    }
                    Date unused = WalletRecordsActivity.this.selectDate = calendar.getTime();
                    int unused2 = WalletRecordsActivity.this.currentPage = 1;
                    WalletRecordsActivity walletRecordsActivity = WalletRecordsActivity.this;
                    String unused3 = walletRecordsActivity.selectDateStr = TimeUtils.millis2String(walletRecordsActivity.selectDate.getTime(), "yyyy-MM");
                    WalletRecordsActivity.this.tvSelectDate2.setText(TimeUtils.millis2String(WalletRecordsActivity.this.selectDate.getTime(), "yyyy/MM"));
                    WalletRecordsActivity walletRecordsActivity2 = WalletRecordsActivity.this;
                    String unused4 = walletRecordsActivity2.tempKey = TimeUtils.millis2String(walletRecordsActivity2.selectDate.getTime(), "yyyy/MM");
                    WalletRecordsActivity walletRecordsActivity3 = WalletRecordsActivity.this;
                    walletRecordsActivity3.loadRecords(walletRecordsActivity3.selectDateStr, WalletRecordsActivity.this.currentPage, WalletRecordsActivity.this.pageSize, true, false);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void rebuildSticky() {
        this.decoration = PowerfulStickyDecoration.Builder.init(new PowerGroupListener() {
            public String getGroupName(int position) {
                return WalletRecordsActivity.this.adapter.getLetter(position);
            }

            public View getGroupView(int position) {
                String in;
                String out;
                View view = LayoutInflater.from(WalletRecordsActivity.this.getParentActivity()).inflate(R.layout.item_wallet_record_header_layout, (ViewGroup) null, false);
                view.setVisibility(0);
                int section = WalletRecordsActivity.this.adapter.getSectionForPosition(position);
                if (section != -1) {
                    String s = (String) WalletRecordsActivity.this.dateKeyLis.get(section);
                    BillRecordsResBean billRecordsResBean = (BillRecordsResBean) WalletRecordsActivity.this.beanMap.get(s);
                    TextView tvIn = (TextView) view.findViewById(R.id.tvIn);
                    TextView tvOut = (TextView) view.findViewById(R.id.tvOut);
                    ((TextView) view.findViewById(R.id.tvSelectDate)).setText(s);
                    if (billRecordsResBean == null) {
                        SpanUtils.with(tvIn).append(LocaleController.getString(R.string.IncomeFormat)).append("₫").setTypeface(Typeface.MONOSPACE).append("0.00").create();
                        SpanUtils.with(tvOut).append(LocaleController.getString(R.string.ExpenditureFormat)).append("₫").setTypeface(Typeface.MONOSPACE).append("0.00").create();
                    } else {
                        String in2 = billRecordsResBean.getStatistics().getIncomeAmount() + "";
                        if (TextUtils.isEmpty(in2)) {
                            in = "0.00";
                        } else {
                            in = MoneyUtil.formatToString(new BigDecimal(in2).divide(new BigDecimal("100")).toString(), 2);
                        }
                        SpanUtils.with(tvIn).append(LocaleController.getString(R.string.IncomeFormat)).append("₫").setTypeface(Typeface.MONOSPACE).append(in).create();
                        String out2 = billRecordsResBean.getStatistics().getExpenditureAmount() + "";
                        if (TextUtils.isEmpty(out2)) {
                            out = "0.00";
                        } else {
                            out = MoneyUtil.formatToString(new BigDecimal(out2).divide(new BigDecimal("100")).toString(), 2);
                        }
                        SpanUtils.with(tvOut).append(LocaleController.getString(R.string.ExpenditureFormat)).append("₫").setTypeface(Typeface.MONOSPACE).append(out).create();
                    }
                }
                return view;
            }
        }).setCacheEnable(false).setGroupHeight(AndroidUtilities.dp(62.0f)).setGroupBackground(ColorUtils.getColor(R.color.window_background_gray)).setOnClickListener(new OnGroupClickListener() {
            public void onClick(int position, int id) {
                if (id != -1) {
                    TimeWheelPickerDialog.Builder builder = TimeWheelPickerDialog.getDefaultBuilder(WalletRecordsActivity.this.getParentActivity(), new OnTimeSelectListener() {
                        public final void onTimeSelect(Date date, View view) {
                            WalletRecordsActivity.AnonymousClass7.this.lambda$onClick$0$WalletRecordsActivity$7(date, view);
                        }
                    });
                    if (WalletRecordsActivity.this.selectDate != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(WalletRecordsActivity.this.selectDate);
                        builder.setDate(calendar);
                    } else {
                        builder.setDate(Calendar.getInstance());
                    }
                    builder.setType(new boolean[]{true, true, false, false, false, false});
                    WalletRecordsActivity.this.showDialog(builder.build());
                }
            }

            public /* synthetic */ void lambda$onClick$0$WalletRecordsActivity$7(Date date, View v) {
                Date unused = WalletRecordsActivity.this.selectDate = date;
                String selectStr = TimeUtils.millis2String(WalletRecordsActivity.this.selectDate.getTime(), "yyyy-MM");
                if (!WalletRecordsActivity.this.selectDateStr.equals(selectStr)) {
                    String unused2 = WalletRecordsActivity.this.selectDateStr = selectStr;
                    int unused3 = WalletRecordsActivity.this.currentPage = 1;
                    WalletRecordsActivity.this.tvSelectDate2.setText(TimeUtils.millis2String(WalletRecordsActivity.this.selectDate.getTime(), "yyyy/MM"));
                    WalletRecordsActivity walletRecordsActivity = WalletRecordsActivity.this;
                    String unused4 = walletRecordsActivity.tempKey = TimeUtils.millis2String(walletRecordsActivity.selectDate.getTime(), "yyyy/MM");
                    if (WalletRecordsActivity.this.decoration != null) {
                        WalletRecordsActivity.this.decoration.clearCache();
                    }
                    WalletRecordsActivity walletRecordsActivity2 = WalletRecordsActivity.this;
                    walletRecordsActivity2.loadRecordsBySelected(walletRecordsActivity2.selectDateStr, WalletRecordsActivity.this.currentPage, WalletRecordsActivity.this.pageSize);
                }
            }
        }).build();
    }

    /* access modifiers changed from: private */
    public void showError() {
        this.tipLayout.setVisibility(0);
        this.selectLayout.setVisibility(4);
        this.emptyDivider.setVisibility(4);
        this.emptyLayout.setBackgroundResource(R.color.window_background_white);
        this.emptyLayout.setBorderWidth(AndroidUtilities.dp(0.5f));
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.tipLayout);
        this.btn.setVisibility(0);
        this.tvTips.setText(LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.btn.setText(LocaleController.getString(R.string.Refresh));
        this.loadView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showLoading() {
        this.tipLayout.setVisibility(0);
        this.selectLayout.setVisibility(4);
        this.emptyLayout.setBackgroundResource(R.color.transparent);
        this.emptyLayout.setBorderWidth(0);
        this.emptyDivider.setVisibility(8);
        this.tvTips.setText(LocaleController.getString(R.string.NowLoading));
        this.loadView.setVisibility(0);
        this.btn.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showEmpty() {
        this.tipLayout.setVisibility(0);
        this.selectLayout.setVisibility(0);
        this.emptyDivider.setVisibility(0);
        this.emptyLayout.setBackgroundResource(R.color.window_background_white);
        this.emptyLayout.setBorderWidth(AndroidUtilities.dp(0.5f));
        this.tvTips.setText(LocaleController.getString(R.string.NoNewBill));
        this.loadView.setVisibility(8);
        this.btn.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void loadRecords(String date, int page, int pageSize2, boolean refresh, boolean loadMore) {
        BillRecordsReqBean bean = new BillRecordsReqBean();
        bean.setBusinessKey(Constants.KEY_BALANCE_LIST);
        bean.setUserId(getUserConfig().clientUserId);
        bean.setPageNum(page);
        bean.setPageSize(pageSize2);
        bean.setDate(date);
        TLRPCWallet.TL_paymentTrans<BillRecordsReqBean> req = new TLRPCWallet.TL_paymentTrans<>();
        req.requestModel = bean;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(refresh) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletRecordsActivity.this.lambda$loadRecords$1$WalletRecordsActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadRecords$1$WalletRecordsActivity(final boolean refresh, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (error != null) {
                    WalletRecordsActivity.this.showError();
                    if (WalletRecordsActivity.this.adapter != null) {
                        WalletRecordsActivity.this.adapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse3(((TLRPCWallet.TL_paymentTransResult) tLObject).data, BillRecordsResBean.class);
                    if (parse.isSuccess()) {
                        if (refresh) {
                            WalletRecordsActivity.this.refreshLayout.finishRefresh();
                            WalletRecordsActivity.this.dateKeyLis.clear();
                            WalletRecordsActivity.this.beanMap.clear();
                            WalletRecordsActivity.this.listView.removeItemDecoration(WalletRecordsActivity.this.decoration);
                            WalletRecordsActivity.this.rebuildSticky();
                            WalletRecordsActivity.this.listView.addItemDecoration(WalletRecordsActivity.this.decoration);
                        } else {
                            WalletRecordsActivity.this.refreshLayout.finishLoadMore();
                        }
                        WalletRecordsActivity.access$208(WalletRecordsActivity.this);
                        if (parse.modelList != null && !parse.modelList.isEmpty()) {
                            WalletRecordsActivity.this.handleData(parse.modelList);
                        }
                        WalletRecordsActivity.this.showEmpty();
                        if (WalletRecordsActivity.this.adapter != null) {
                            WalletRecordsActivity.this.adapter.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                    WalletRecordsActivity.this.showError();
                    if (WalletRecordsActivity.this.adapter != null) {
                        WalletRecordsActivity.this.adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadRecordsBySelected(String date, int page, int pageSize2) {
        BillRecordsReqBean bean = new BillRecordsReqBean();
        bean.setBusinessKey(Constants.KEY_BALANCE_LIST);
        bean.setUserId(getUserConfig().clientUserId);
        bean.setPageNum(page);
        bean.setPageSize(pageSize2);
        bean.setDate(date);
        TLRPCWallet.TL_paymentTrans<BillRecordsReqBean> req = new TLRPCWallet.TL_paymentTrans<>();
        req.requestModel = bean;
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletRecordsActivity.this.lambda$loadRecordsBySelected$2$WalletRecordsActivity(this.f$1, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                WalletRecordsActivity.this.lambda$loadRecordsBySelected$3$WalletRecordsActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(progressDialog);
    }

    public /* synthetic */ void lambda$loadRecordsBySelected$2$WalletRecordsActivity(final AlertDialog progressDialog, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (error != null) {
                    WalletRecordsActivity.this.showError();
                    if (WalletRecordsActivity.this.adapter != null) {
                        WalletRecordsActivity.this.adapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse3(((TLRPCWallet.TL_paymentTransResult) tLObject).data, BillRecordsResBean.class);
                    if (parse.isSuccess()) {
                        WalletRecordsActivity.this.refreshLayout.finishRefresh();
                        WalletRecordsActivity.this.dateKeyLis.clear();
                        WalletRecordsActivity.this.beanMap.clear();
                        WalletRecordsActivity.this.listView.removeItemDecoration(WalletRecordsActivity.this.decoration);
                        WalletRecordsActivity.this.rebuildSticky();
                        WalletRecordsActivity.this.listView.addItemDecoration(WalletRecordsActivity.this.decoration);
                        WalletRecordsActivity.access$208(WalletRecordsActivity.this);
                        if (parse.modelList != null && !parse.modelList.isEmpty()) {
                            WalletRecordsActivity.this.handleData(parse.modelList);
                        }
                        WalletRecordsActivity.this.showEmpty();
                    } else {
                        WalletRecordsActivity.this.showError();
                    }
                    if (WalletRecordsActivity.this.adapter != null) {
                        WalletRecordsActivity.this.adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public /* synthetic */ void lambda$loadRecordsBySelected$3$WalletRecordsActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    /* access modifiers changed from: private */
    public void handleData(List<BillRecordsResBean> modelList) {
        int count = 0;
        for (int i = 0; i < modelList.size(); i++) {
            BillRecordsResBean bean = modelList.get(i);
            BillRecordsResBean billRecordsResBean = this.beanMap.get(bean.getDateTime());
            if (billRecordsResBean == null) {
                fillKeys(bean.getDateTime());
                this.dateKeyLis.add(bean.getDateTime());
                this.beanMap.put(bean.getDateTime(), bean);
            } else {
                billRecordsResBean.getBillList().addAll(bean.getBillList());
            }
            count += bean.getBillList().size();
        }
        if (count < this.pageSize) {
            this.refreshLayout.setEnableLoadMore(false);
        }
    }

    private void fillKeys(String dateKey) {
        String str = dateKey;
        if (this.beanMap.get(this.tempKey) == null && !this.tempKey.equals(str)) {
            this.dateKeyLis.add(this.tempKey);
        }
        Long mon = im.bclpbkiauv.ui.utils.number.TimeUtils.getTimeLong("yyyy/MM", str);
        Long tempMon = im.bclpbkiauv.ui.utils.number.TimeUtils.getTimeLong("yyyy/MM", this.tempKey);
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(mon.longValue());
        Calendar temp = Calendar.getInstance();
        temp.setTimeInMillis(tempMon.longValue());
        int i = 1;
        int tempYear = temp.get(1);
        int currentYear = current.get(1);
        int tempMonth = temp.get(2) + 1;
        int currentMonth = current.get(2) + 1;
        int i2 = 1;
        while (i2 < (((tempYear - currentYear) * 12) + tempMonth) - currentMonth) {
            current.add(2, i);
            this.dateKeyLis.add(TimeUtils.millis2String(current.getTime().getTime(), "yyyy/MM"));
            i2++;
            tempYear = tempYear;
            i = 1;
        }
        this.tempKey = str;
    }

    private class ListAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                view = new EmptyCell(this.mContext, AndroidUtilities.dp(12.0f));
            } else if (viewType != 2) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_wallet_balance_record_layout, parent, false);
            } else {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_wallet_balance_record_empty_layout, parent, false);
            }
            return new RecyclerListView.Holder(view);
        }

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == -1) {
                section = WalletRecordsActivity.this.dateKeyLis.size() - 1;
            }
            if (section < 0 || section >= WalletRecordsActivity.this.dateKeyLis.size()) {
                return "";
            }
            return (String) WalletRecordsActivity.this.dateKeyLis.get(section);
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }

        public int getSectionCount() {
            if (WalletRecordsActivity.this.dateKeyLis != null) {
                return 0 + WalletRecordsActivity.this.dateKeyLis.size();
            }
            return 0;
        }

        public int getCountForSection(int section) {
            BillRecordsResBean billRecordsResBean = (BillRecordsResBean) WalletRecordsActivity.this.beanMap.get((String) WalletRecordsActivity.this.dateKeyLis.get(section));
            if (billRecordsResBean == null || billRecordsResBean.getBillList().size() == 0) {
                return 1;
            }
            return billRecordsResBean.getBillList().size() + 1;
        }

        public boolean isEnabled(int section, int row) {
            return false;
        }

        public int getItemViewType(int section, int position) {
            BillRecordsResBean billRecordsResBean = (BillRecordsResBean) WalletRecordsActivity.this.beanMap.get((String) WalletRecordsActivity.this.dateKeyLis.get(section));
            if (billRecordsResBean == null || billRecordsResBean.getBillList() == null || billRecordsResBean.getBillList().size() == 0) {
                return 2;
            }
            if (position == billRecordsResBean.getBillList().size()) {
                return 1;
            }
            return 0;
        }

        public Object getItem(int section, int position) {
            BillRecordsResBean billRecordsResBean = (BillRecordsResBean) WalletRecordsActivity.this.beanMap.get((String) WalletRecordsActivity.this.dateKeyLis.get(section));
            if (billRecordsResBean.getBillList() == null || billRecordsResBean.getBillList().size() == 0) {
                return null;
            }
            return billRecordsResBean.getBillList().get(position);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            String withdrawAmount;
            int i = position;
            RecyclerView.ViewHolder viewHolder = holder;
            int type = holder.getItemViewType();
            if (type == 0) {
                ConstraintLayout container = (ConstraintLayout) viewHolder.itemView.findViewById(R.id.container);
                ImageView ivIcon = (ImageView) viewHolder.itemView.findViewById(R.id.ivIcon);
                TextView tvTitle = (TextView) viewHolder.itemView.findViewById(R.id.tvTitle);
                TextView tvTime = (TextView) viewHolder.itemView.findViewById(R.id.tvTime);
                TextView tvAmount = (TextView) viewHolder.itemView.findViewById(R.id.tvAmount);
                TextView tvBalance = (TextView) viewHolder.itemView.findViewById(R.id.tvBalance);
                View divider = viewHolder.itemView.findViewById(R.id.divider);
                if (i == getCountForSection(section) - 2) {
                    divider.setVisibility(8);
                    container.setBackgroundResource(R.drawable.cell_bottom_selector);
                } else {
                    divider.setVisibility(0);
                    container.setBackgroundResource(R.drawable.cell_middle_selector);
                }
                BillRecordResBillListBean billRecordResBillListBean = ((BillRecordsResBean) WalletRecordsActivity.this.beanMap.get((String) WalletRecordsActivity.this.dateKeyLis.get(section))).getBillList().get(i);
                ivIcon.setImageResource(billRecordResBillListBean.getTypeIcon());
                tvTitle.setText(WalletRecordsActivity.this.getTitle(billRecordResBillListBean));
                String createTime = billRecordResBillListBean.getCreateTime();
                if (!TextUtils.isEmpty(createTime)) {
                    createTime = im.bclpbkiauv.ui.utils.number.TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", createTime, "HH:mm:ss dd/MM/yy");
                }
                tvTime.setText(createTime);
                Object obj = "";
                int i2 = type;
                if (billRecordResBillListBean.getServiceCharge() != 0) {
                    ConstraintLayout constraintLayout = container;
                    StringBuilder sb = new StringBuilder();
                    ImageView imageView = ivIcon;
                    sb.append(billRecordResBillListBean.getAmount());
                    sb.append("");
                    BigDecimal bigDecimal = new BigDecimal(sb.toString());
                    StringBuilder sb2 = new StringBuilder();
                    TextView textView = tvTitle;
                    sb2.append(billRecordResBillListBean.getServiceCharge());
                    sb2.append("");
                    withdrawAmount = bigDecimal.add(new BigDecimal(sb2.toString())).divide(new BigDecimal("100")).toString();
                } else {
                    ImageView imageView2 = ivIcon;
                    TextView textView2 = tvTitle;
                    withdrawAmount = new BigDecimal(billRecordResBillListBean.getAmount() + "").divide(new BigDecimal("100")).toString();
                }
                if (WalletRecordsActivity.this.getAddRender(billRecordResBillListBean.getOrderType())) {
                    tvAmount.setTextColor(ColorUtils.getColor(R.color.text_amount_add_color));
                } else {
                    tvAmount.setTextColor(ColorUtils.getColor(R.color.text_secondary_color));
                }
                SpanUtils.with(tvAmount).append(billRecordResBillListBean.getDp()).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(withdrawAmount, 2)).create();
                SpanUtils span = SpanUtils.with(tvBalance);
                if (billRecordResBillListBean.getOrderType() == 7) {
                    tvBalance.setVisibility(8);
                    span.append(LocaleController.getString(R.string.Refunded));
                    span.create();
                } else if (billRecordResBillListBean.getOrderType() == 3) {
                    tvBalance.setVisibility(8);
                    span.append(LocaleController.getString(R.string.WithdrawalFailure));
                    span.create();
                } else if (billRecordResBillListBean.getOrderType() == 12) {
                    tvBalance.setVisibility(8);
                    span.append(LocaleController.getString(R.string.Refunded));
                    if (!TextUtils.isEmpty(billRecordResBillListBean.getGroupsNumber())) {
                        String rAmount = billRecordResBillListBean.getRefundAmount();
                        if (!TextUtils.isEmpty(rAmount)) {
                            String str = withdrawAmount;
                            span.append(SQLBuilder.PARENTHESES_LEFT).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(new BigDecimal(rAmount).divide(new BigDecimal("100")).toString(), 2)).append(SQLBuilder.PARENTHESES_RIGHT);
                        }
                    }
                    span.create();
                } else {
                    tvBalance.setVisibility(8);
                }
            } else {
                int i3 = section;
                int i4 = type;
            }
        }

        public View getSectionHeaderView(int section, View view) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public boolean getAddRender(int type) {
        if (type == 0 || type == 5 || type == 8 || type == 13 || type == 21 || type == 19 || type == 27) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public String getTitle(BillRecordResBillListBean bean) {
        int orderType = bean.getOrderType();
        if (orderType == 0) {
            String channel = getChannel(bean);
            return String.format(LocaleController.getString(R.string.TopUpFrom), new Object[]{channel});
        } else if (orderType == 1) {
            String channel2 = getChannel(bean);
            return String.format(LocaleController.getString(R.string.WithdrawalTo), new Object[]{channel2});
        } else if (orderType != 3) {
            if (orderType != 21) {
                switch (orderType) {
                    case 5:
                        String targetUserStr = getTargetUserStr(bean);
                        return String.format(LocaleController.getString(R.string.TransferFromSomebody), new Object[]{targetUserStr});
                    case 6:
                        String targetUserStr2 = getTargetUserStr(bean);
                        return String.format(LocaleController.getString(R.string.TransferToSombody2), new Object[]{targetUserStr2});
                    case 7:
                        String targetUserStr3 = getTargetUserStr(bean);
                        return String.format(LocaleController.getString(R.string.TransferRefundFromSomebody), new Object[]{targetUserStr3});
                    case 8:
                        String targetStr = getRedPacketTargetStr(bean);
                        return String.format(LocaleController.getString(R.string.RedPacketFromSomebody), new Object[]{targetStr});
                    case 9:
                        String targetUserStr4 = getTargetUserStr(bean);
                        return String.format(LocaleController.getString(R.string.RedPacketToSomebody), new Object[]{targetUserStr4});
                    case 10:
                        String targetStr2 = getGroupTargetStr(bean);
                        return String.format(LocaleController.getString(R.string.RedPacketToSomebody), new Object[]{targetStr2});
                    case 11:
                        String targetStr3 = getGroupTargetStr(bean);
                        return String.format(LocaleController.getString(R.string.RedPacketToSomebody), new Object[]{targetStr3});
                    case 12:
                        String targetStr4 = getRedPacketTargetStr(bean);
                        return String.format(LocaleController.getString(R.string.RedPacketRefundFromSomebody), new Object[]{targetStr4});
                    case 13:
                        break;
                    default:
                        switch (orderType) {
                            case 25:
                                return LocaleController.getString(R.string.BackOfficeAccount);
                            case 26:
                                String targetUserStr5 = getTargetUserStr(bean);
                                return String.format(LocaleController.getString(R.string.LiveRewardToFormat), new Object[]{targetUserStr5});
                            case 27:
                                String targetUserStr6 = getTargetUserStr(bean);
                                return String.format(LocaleController.getString(R.string.LiveRewardFromFormat), new Object[]{targetUserStr6});
                            default:
                                return LocaleController.getString(R.string.UnKnown);
                        }
                }
            }
            return LocaleController.getString(R.string.BackstageAccount);
        } else {
            String channel3 = getChannel(bean);
            return String.format(LocaleController.getString(R.string.WithdrawalFailureRefund), new Object[]{channel3});
        }
    }

    private String getChannel(BillRecordResBillListBean bean) {
        if (TextUtils.isEmpty(bean.getSubInstitutionName())) {
            return "";
        }
        return new StringBuilder(bean.getSubInstitutionName()).toString();
    }

    private String getTargetUserStr(BillRecordResBillListBean bean) {
        if (TextUtils.isEmpty(bean.getEffectUserId())) {
            return "";
        }
        TLRPC.User user = getMessagesController().getUser(Integer.valueOf(Integer.parseInt(bean.getEffectUserId())));
        if (user != null) {
            return user.first_name;
        }
        if (bean.getEffectUserName() != null) {
            return bean.getEffectUserName();
        }
        return "";
    }

    private String getGroupTargetStr(BillRecordResBillListBean bean) {
        if (TextUtils.isEmpty(bean.getGroupsNumber())) {
            return "";
        }
        TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(Integer.parseInt(bean.getGroupsNumber())));
        if (chat != null) {
            return chat.title;
        }
        if (bean.getGroupsName() != null) {
            return bean.getGroupsName();
        }
        return "";
    }

    private String getRedPacketTargetStr(BillRecordResBillListBean bean) {
        String targetStr = getGroupTargetStr(bean);
        if (TextUtils.isEmpty(targetStr)) {
            return getTargetUserStr(bean);
        }
        return targetStr;
    }
}
