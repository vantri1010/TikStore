package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AppTextView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.load.SpinKitView;
import im.bclpbkiauv.ui.load.SpriteFactory;
import im.bclpbkiauv.ui.load.Style;
import im.bclpbkiauv.ui.wallet.model.BankCardInfo;
import im.bclpbkiauv.ui.wallet.model.BankCardListResBean;
import im.bclpbkiauv.ui.wallet.model.Constants;
import im.bclpbkiauv.ui.wallet.model.PayChannelBean;
import im.bclpbkiauv.ui.wallet.utils.ExceptionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WalletBankCardsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public BankCardListResBean bankBean;
    private AppTextView btn;
    /* access modifiers changed from: private */
    public PayChannelBean channelBean;
    /* access modifiers changed from: private */
    public BankCardDelegate delegate;
    private ImageView ivTip;
    private RecyclerListView listView;
    private SpinKitView loadView;
    /* access modifiers changed from: private */
    public ArrayList<BankCardListResBean> modelList;
    /* access modifiers changed from: private */
    public int status = 0;
    private LinearLayout tipLayout;
    private TextView tvDesc;
    private TextView tvTips;

    public interface BankCardDelegate {
        void onSelected(BankCardListResBean bankCardListResBean);
    }

    public void setDelegate(BankCardDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setBean(PayChannelBean bean) {
        this.channelBean = bean;
    }

    public void setBankBean(BankCardListResBean bean) {
        this.bankBean = bean;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.bandCardNeedReload);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.bandCardNeedReload);
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_banks_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        showLoading();
        this.fragmentView.postDelayed(new Runnable() {
            public void run() {
                WalletBankCardsActivity.this.loadBankList();
            }
        }, 1000);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.BankCard));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (WalletBankCardsActivity.this.delegate != null) {
                        if (WalletBankCardsActivity.this.bankBean != null) {
                            WalletBankCardsActivity.this.delegate.onSelected(WalletBankCardsActivity.this.bankBean);
                        } else if (WalletBankCardsActivity.this.modelList != null && !WalletBankCardsActivity.this.modelList.isEmpty()) {
                            WalletBankCardsActivity.this.delegate.onSelected((BankCardListResBean) WalletBankCardsActivity.this.modelList.get(0));
                        }
                    }
                    WalletBankCardsActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.loadView = (SpinKitView) this.fragmentView.findViewById(R.id.loadView);
        this.tipLayout = (LinearLayout) this.fragmentView.findViewById(R.id.tipLayout);
        this.ivTip = (ImageView) this.fragmentView.findViewById(R.id.ivTip);
        this.tvTips = (TextView) this.fragmentView.findViewById(R.id.tvTips);
        this.tvDesc = (TextView) this.fragmentView.findViewById(R.id.tvDesc);
        this.btn = (AppTextView) this.fragmentView.findViewById(R.id.btn);
        this.listView = (RecyclerListView) this.fragmentView.findViewById(R.id.listView);
        this.loadView.setColor(-16744193);
        this.loadView.setIndeterminateDrawable(SpriteFactory.create(Style.CIRCLE));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getParentActivity());
        this.adapter = new ListAdapter(getParentActivity());
        this.listView.setEmptyView(this.tipLayout);
        this.listView.setLayoutManager(layoutManager);
        this.listView.setAdapter(this.adapter);
        this.btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (WalletBankCardsActivity.this.status == 1) {
                    WalletBankCardsActivity.this.showLoading();
                    WalletBankCardsActivity.this.loadBankList();
                    return;
                }
                Bundle args = new Bundle();
                args.putString("supportId", WalletBankCardsActivity.this.channelBean.getPayType().getSupportId() + "");
                args.putString("templateId", WalletBankCardsActivity.this.channelBean.getPayType().getTemplateId() + "");
                WalletBankCardsActivity.this.presentFragment(new WalletWithdrawAddNewAccountActivity(args));
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position == WalletBankCardsActivity.this.modelList.size()) {
                    Bundle args = new Bundle();
                    args.putString("supportId", WalletBankCardsActivity.this.channelBean.getPayType().getSupportId() + "");
                    args.putString("templateId", WalletBankCardsActivity.this.channelBean.getPayType().getTemplateId() + "");
                    WalletBankCardsActivity.this.presentFragment(new WalletWithdrawAddNewAccountActivity(args));
                } else if (WalletBankCardsActivity.this.delegate != null) {
                    WalletBankCardsActivity.this.delegate.onSelected((BankCardListResBean) WalletBankCardsActivity.this.modelList.get(position));
                    WalletBankCardsActivity.this.finishFragment();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showLoading() {
        this.tipLayout.setVisibility(0);
        this.tvDesc.setVisibility(8);
        this.ivTip.setVisibility(8);
        this.btn.setVisibility(8);
        this.tvTips.setText(LocaleController.getString(R.string.NowLoading));
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
        this.loadView.setVisibility(0);
    }

    private void showEmpty() {
        this.tipLayout.setVisibility(0);
        this.tvDesc.setVisibility(0);
        this.ivTip.setVisibility(0);
        this.ivTip.setImageResource(R.mipmap.ic_add_bank_card2);
        this.btn.setVisibility(0);
        this.btn.setText(LocaleController.getString(R.string.ToBindBankCardCaps));
        this.tvTips.setText(LocaleController.getString(R.string.NoBankCard));
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvDesc.setText(LocaleController.getString(R.string.NoBanCardBind));
        this.loadView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showError() {
        this.tipLayout.setVisibility(0);
        this.tvDesc.setVisibility(0);
        this.ivTip.setVisibility(0);
        this.ivTip.setImageResource(R.mipmap.ic_data_ex);
        this.btn.setVisibility(0);
        this.tvTips.setText(LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvDesc.setText(LocaleController.getString(R.string.ClickTheButtonToTryAgain));
        this.btn.setText(LocaleController.getString(R.string.Refresh));
        this.loadView.setVisibility(8);
    }

    private void showContainer() {
        this.tipLayout.setVisibility(8);
        this.listView.setVisibility(0);
    }

    private void sortList() {
        ArrayList<BankCardListResBean> arrayList = this.modelList;
        if (arrayList != null) {
            Collections.sort(arrayList, new Comparator<BankCardListResBean>() {
                public int compare(BankCardListResBean o1, BankCardListResBean o2) {
                    if (o1.getId() > o2.getId()) {
                        return -1;
                    }
                    if (o1.getId() < o2.getId()) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void loadBankList() {
        TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
        builder.setBusinessKey(Constants.KEY_BANK_CARD_LIST);
        builder.addParam("userId", Integer.valueOf(getUserConfig().clientUserId));
        builder.addParam("supportId", Integer.valueOf(this.channelBean.getPayType().getSupportId()));
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(builder.build(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletBankCardsActivity.this.lambda$loadBankList$1$WalletBankCardsActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadBankList$1$WalletBankCardsActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            ExceptionUtils.handlePayChannelException(error.text);
        } else if (response instanceof TLRPCWallet.TL_paymentTransResult) {
            TLApiModel<BankCardListResBean> parse = TLJsonResolve.parse3(((TLRPCWallet.TL_paymentTransResult) response).data, BankCardListResBean.class);
            if (parse.isSuccess()) {
                this.status = 0;
                this.modelList = (ArrayList) parse.modelList;
                sortList();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        WalletBankCardsActivity.this.lambda$null$0$WalletBankCardsActivity();
                    }
                });
                return;
            }
            this.status = 1;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    WalletBankCardsActivity.this.showError();
                    if (WalletBankCardsActivity.this.adapter != null) {
                        WalletBankCardsActivity.this.adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$0$WalletBankCardsActivity() {
        showEmpty();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.bandCardNeedReload) {
            showLoading();
            loadBankList();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemViewType(int position) {
            if (position == WalletBankCardsActivity.this.modelList.size()) {
                return 1;
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 1) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_bank_card_layout, parent, false);
            } else {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_button_layout, parent, false);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String shortNum;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            if (holder.getItemViewType() != 1) {
                View container = viewHolder.itemView.findViewById(R.id.container);
                TextView tvSelected = (TextView) viewHolder.itemView.findViewById(R.id.tvSelected);
                TextView tvName = (TextView) viewHolder.itemView.findViewById(R.id.tvName);
                TextView tvNumber = (TextView) viewHolder.itemView.findViewById(R.id.tvNumber);
                ImageView ivEdit = (ImageView) viewHolder.itemView.findViewById(R.id.ivEdit);
                if (i == 0) {
                    container.setBackgroundResource(R.drawable.cell_top_selector);
                } else {
                    container.setBackgroundResource(R.drawable.cell_middle_selector);
                }
                ivEdit.setTag(Integer.valueOf(position));
                tvSelected.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(4.0f), ColorUtils.getColor(R.color.text_blue_color)));
                BankCardListResBean bean = (BankCardListResBean) WalletBankCardsActivity.this.modelList.get(i);
                int i2 = 0;
                if (WalletBankCardsActivity.this.bankBean == null) {
                    if (i != 0) {
                        i2 = 8;
                    }
                    tvSelected.setVisibility(i2);
                } else {
                    if (WalletBankCardsActivity.this.bankBean.getId() != bean.getId()) {
                        i2 = 8;
                    }
                    tvSelected.setVisibility(i2);
                }
                if (bean != null && !TextUtils.isEmpty(bean.getInfo())) {
                    BankCardInfo bankCardInfo = (BankCardInfo) GsonUtils.fromJson(bean.getInfo(), BankCardInfo.class);
                    String cardNum = bean.getCardNumber() + "";
                    if (cardNum.length() > 4) {
                        shortNum = cardNum.substring(cardNum.length() - 4);
                    } else {
                        shortNum = cardNum;
                    }
                    String name = "";
                    String reactType = bean.getReactType();
                    if (reactType != null) {
                        name = reactType;
                    }
                    if (TextUtils.isEmpty(name)) {
                        name = WalletBankCardsActivity.this.channelBean.getPayType().getName();
                    }
                    SpanUtils.with(tvName).append(name).append(SQLBuilder.PARENTHESES_LEFT).append(shortNum).append(SQLBuilder.PARENTHESES_RIGHT).create();
                    tvNumber.setText(cardNum);
                }
                ivEdit.setBackground(Theme.createSelectorDrawable(ColorUtils.getColor(R.color.click_selector)));
                ivEdit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        BankCardListResBean bankCardListResBean = (BankCardListResBean) WalletBankCardsActivity.this.modelList.get(((Integer) view.getTag()).intValue());
                        WalletDialog dialog = new WalletDialog(WalletBankCardsActivity.this.getParentActivity());
                        dialog.setMessage(LocaleController.getString(R.string.AreYouSureDeleteBankCard), 16, true, true, false);
                        dialog.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        dialog.setPositiveButton(LocaleController.getString("Confirm", R.string.Confirm), 
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0050: INVOKE  
                              (r2v0 'dialog' im.bclpbkiauv.ui.dialogs.WalletDialog)
                              (wrap: java.lang.String : 0x0047: INVOKE  (r3v7 java.lang.String) = 
                              ("Confirm")
                              (wrap: ? : ?: SGET   im.bclpbkiauv.messenger.R.string.Confirm int)
                             im.bclpbkiauv.messenger.LocaleController.getString(java.lang.String, int):java.lang.String type: STATIC)
                              (wrap: im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA : 0x004d: CONSTRUCTOR  (r4v4 im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA) = 
                              (r10v0 'this' im.bclpbkiauv.ui.wallet.WalletBankCardsActivity$ListAdapter$1 A[THIS])
                             call: im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA.<init>(im.bclpbkiauv.ui.wallet.WalletBankCardsActivity$ListAdapter$1):void type: CONSTRUCTOR)
                             im.bclpbkiauv.ui.dialogs.WalletDialog.setPositiveButton(java.lang.String, android.content.DialogInterface$OnClickListener):void type: VIRTUAL in method: im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.ListAdapter.1.onClick(android.view.View):void, dex: classes6.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x004d: CONSTRUCTOR  (r4v4 im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA) = 
                              (r10v0 'this' im.bclpbkiauv.ui.wallet.WalletBankCardsActivity$ListAdapter$1 A[THIS])
                             call: im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA.<init>(im.bclpbkiauv.ui.wallet.WalletBankCardsActivity$ListAdapter$1):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.ListAdapter.1.onClick(android.view.View):void, dex: classes6.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 83 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 89 more
                            */
                        /*
                            this = this;
                            java.lang.Object r0 = r11.getTag()
                            java.lang.Integer r0 = (java.lang.Integer) r0
                            int r0 = r0.intValue()
                            im.bclpbkiauv.ui.wallet.WalletBankCardsActivity$ListAdapter r1 = im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.ListAdapter.this
                            im.bclpbkiauv.ui.wallet.WalletBankCardsActivity r1 = im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.this
                            java.util.ArrayList r1 = r1.modelList
                            java.lang.Object r1 = r1.get(r0)
                            im.bclpbkiauv.ui.wallet.model.BankCardListResBean r1 = (im.bclpbkiauv.ui.wallet.model.BankCardListResBean) r1
                            im.bclpbkiauv.ui.dialogs.WalletDialog r2 = new im.bclpbkiauv.ui.dialogs.WalletDialog
                            im.bclpbkiauv.ui.wallet.WalletBankCardsActivity$ListAdapter r3 = im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.ListAdapter.this
                            im.bclpbkiauv.ui.wallet.WalletBankCardsActivity r3 = im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.this
                            androidx.fragment.app.FragmentActivity r3 = r3.getParentActivity()
                            r2.<init>(r3)
                            r3 = 2131689897(0x7f0f01a9, float:1.9008822E38)
                            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
                            r6 = 16
                            r7 = 1
                            r8 = 1
                            r9 = 0
                            r4 = r2
                            r4.setMessage(r5, r6, r7, r8, r9)
                            java.lang.String r3 = "Cancel"
                            r4 = 2131690308(0x7f0f0344, float:1.9009656E38)
                            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r4)
                            r4 = 0
                            r2.setNegativeButton(r3, r4)
                            java.lang.String r3 = "Confirm"
                            r4 = 2131690670(0x7f0f04ae, float:1.901039E38)
                            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r4)
                            im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA r4 = new im.bclpbkiauv.ui.wallet.-$$Lambda$WalletBankCardsActivity$ListAdapter$1$ZRHKCUz5XwbfwBr4-GRpXcsilUA
                            r4.<init>(r10)
                            r2.setPositiveButton(r3, r4)
                            android.widget.TextView r3 = r2.getPositiveButton()
                            r4 = 2131099927(0x7f060117, float:1.7812221E38)
                            int r4 = com.blankj.utilcode.util.ColorUtils.getColor(r4)
                            r3.setTextColor(r4)
                            android.widget.TextView r3 = r2.getNegativeButton()
                            r4 = 2131099928(0x7f060118, float:1.7812223E38)
                            int r4 = com.blankj.utilcode.util.ColorUtils.getColor(r4)
                            r3.setTextColor(r4)
                            im.bclpbkiauv.ui.wallet.WalletBankCardsActivity$ListAdapter r3 = im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.ListAdapter.this
                            im.bclpbkiauv.ui.wallet.WalletBankCardsActivity r3 = im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.this
                            r3.showDialog(r2)
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.wallet.WalletBankCardsActivity.ListAdapter.AnonymousClass1.onClick(android.view.View):void");
                    }

                    public /* synthetic */ void lambda$onClick$0$WalletBankCardsActivity$ListAdapter$1(DialogInterface dialogInterface, int i) {
                        WalletBankCardsActivity.this.finishFragment();
                    }
                });
                return;
            }
            ((TextView) viewHolder.itemView.findViewById(R.id.tvAction)).setText(LocaleController.getString(R.string.AddNewBankCard));
            ((LinearLayout) viewHolder.itemView.findViewById(R.id.container)).setBackgroundResource(R.drawable.cell_bottom_selector);
        }

        public int getItemCount() {
            if (WalletBankCardsActivity.this.modelList == null || WalletBankCardsActivity.this.modelList.size() == 0) {
                return 0;
            }
            return WalletBankCardsActivity.this.modelList.size() + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }
    }
}
