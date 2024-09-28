package im.bclpbkiauv.ui.hui.cdnvip;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.GsonUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.javaBean.cdnVip.CdnVipDetailsListBean;
import im.bclpbkiauv.javaBean.cdnVip.CdnVipInfoBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCCdn;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.cdnvip.CdnVipDetailsActivity;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryLinearLayout;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.List;

public class CdnVipDetailsActivity extends BaseFragment {
    private Adapter adapter;
    /* access modifiers changed from: private */
    public CdnVipInfoBean cdnVipInfoBean;
    /* access modifiers changed from: private */
    public List<CdnVipDetailsListBean.Item> data;
    private Delegate delegate;
    private MryEmptyView emptyView;
    private RecyclerListView rv;

    public interface Delegate {
        void onResult(CdnVipInfoBean cdnVipInfoBean);
    }

    public CdnVipDetailsActivity(CdnVipInfoBean cdnVipInfoBean2) {
        this.cdnVipInfoBean = cdnVipInfoBean2;
    }

    public boolean onFragmentCreate() {
        return this.cdnVipInfoBean != null;
    }

    public View createView(Context context) {
        this.actionBar = createActionBar(context);
        this.actionBar.setTitle(LocaleController.getString(R.string.MemberDetails));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CdnVipDetailsActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.fragmentView = container;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        MryEmptyView mryEmptyView = new MryEmptyView(context);
        this.emptyView = mryEmptyView;
        mryEmptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.emptyView.attach((ViewGroup) container);
        this.emptyView.setEmptyResId(R.mipmap.img_empty_default);
        this.emptyView.setEmptyText(LocaleController.getString(R.string.YouDonottHaveRecordYet));
        this.emptyView.showLoading();
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.rv = recyclerListView;
        container.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        this.rv.setLayoutManager(new LinearLayoutManager(context));
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        this.rv.setAdapter(adapter2);
        this.fragmentView.postDelayed(new Runnable() {
            public final void run() {
                CdnVipDetailsActivity.this.getData();
            }
        }, 300);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public void getData() {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCCdn.TL_getUserCdnVipPayRecords(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CdnVipDetailsActivity.this.lambda$getData$1$CdnVipDetailsActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getData$1$CdnVipDetailsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CdnVipDetailsActivity.this.lambda$null$0$CdnVipDetailsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$CdnVipDetailsActivity(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            parseError(error.code, error.text);
        } else if (response instanceof TLRPCCdn.TL_userCdnPayList) {
            try {
                CdnVipDetailsListBean bean = (CdnVipDetailsListBean) GsonUtils.getGson().fromJson(((TLRPCCdn.TL_userCdnPayList) response).pay_list.data, CdnVipDetailsListBean.class);
                if (bean != null) {
                    this.data = bean.getInfoList();
                }
                if (this.adapter != null) {
                    this.adapter.notifyDataSetChanged();
                }
                if (this.emptyView != null) {
                    if (this.data != null) {
                        if (!this.data.isEmpty()) {
                            this.emptyView.showContent();
                            return;
                        }
                    }
                    this.emptyView.showEmpty();
                }
            } catch (Exception e) {
                parseError(0, e.getMessage());
            }
        }
    }

    /* access modifiers changed from: private */
    public void openOrCloseAutoPay() {
        if (this.cdnVipInfoBean != null) {
            TLRPCCdn.TL_setCdnVipAutoPay req = new TLRPCCdn.TL_setCdnVipAutoPay();
            boolean isOpenAutoPay = this.cdnVipInfoBean.isAutoPay();
            req.is_open = !isOpenAutoPay;
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    CdnVipDetailsActivity.this.lambda$openOrCloseAutoPay$2$CdnVipDetailsActivity(dialogInterface);
                }
            });
            progressDialog.show();
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog, isOpenAutoPay) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    CdnVipDetailsActivity.this.lambda$openOrCloseAutoPay$4$CdnVipDetailsActivity(this.f$1, this.f$2, tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$openOrCloseAutoPay$2$CdnVipDetailsActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
    }

    public /* synthetic */ void lambda$openOrCloseAutoPay$4$CdnVipDetailsActivity(AlertDialog progressDialog, boolean isOpenAutoPay, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, response, isOpenAutoPay) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                CdnVipDetailsActivity.this.lambda$null$3$CdnVipDetailsActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$CdnVipDetailsActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response, boolean isOpenAutoPay) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (error != null) {
            parseError(1, error.text);
        } else if (response instanceof TLRPCCdn.TL_userCdnVipInfo) {
            try {
                ToastUtils.show((CharSequence) LocaleController.getString(isOpenAutoPay ? R.string.CdnVipAutomaticCloseSuccess : R.string.CdnVipAutomaticOpenSuccess));
                CdnVipInfoBean cdnVipInfoBean2 = (CdnVipInfoBean) GsonUtils.fromJson(((TLRPCCdn.TL_userCdnVipInfo) response).vip_info.data, CdnVipInfoBean.class);
                this.cdnVipInfoBean = cdnVipInfoBean2;
                if (this.adapter != null) {
                    this.adapter.notifyDataSetChanged();
                }
                if (this.delegate != null) {
                    this.delegate.onResult(cdnVipInfoBean2);
                }
            } catch (Exception e) {
                parseError(1, e.getMessage());
            }
        }
    }

    private void parseError(int errorCode, String errorMsg) {
        if (errorCode == 1) {
            WalletDialogUtil.showConfirmBtnWalletDialog(this, LocaleController.getString(this.cdnVipInfoBean.isAutoPay() ? R.string.CdnVipAutomaticCloseFailed : R.string.CdnVipAutomaticOpenFailed));
        } else {
            WalletDialogUtil.showConfirmBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(errorMsg));
        }
    }

    public CdnVipDetailsActivity setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
        return this;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.adapter = null;
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public PageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new PageHolder(LayoutInflater.from(CdnVipDetailsActivity.this.getParentActivity()).inflate(R.layout.item_cdn_vip_details, parent, false));
            }
            MryTextView tv = new MryTextView(CdnVipDetailsActivity.this.getParentActivity());
            tv.setTextSize(13.0f);
            tv.setGravity(17);
            tv.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(60.0f)));
            tv.setText(LocaleController.getString(R.string.friends_circle_location_search_nomore_hint) + "~");
            tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            return new PageHolder((View) tv, 0);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
            int i = position;
            if (holder1.getItemViewType() == 0 && CdnVipDetailsActivity.this.cdnVipInfoBean != null) {
                PageHolder holder = (PageHolder) holder1;
                ((MryLinearLayout) holder.itemView).setRadius(AndroidUtilities.dp(10.0f));
                MryRoundButton btn = (MryRoundButton) holder.getView(R.id.btn);
                btn.setTextColor(-1);
                CdnVipDetailsListBean.Item item = (CdnVipDetailsListBean.Item) CdnVipDetailsActivity.this.data.get(i);
                if (!CdnVipDetailsActivity.this.cdnVipInfoBean.cdnVipIsAvailable()) {
                    holder.setText((int) R.id.tvStatus, (CharSequence) LocaleController.getString(R.string.AppVip) + SQLBuilder.PARENTHESES_LEFT + LocaleController.getString(R.string.RequestExpired) + SQLBuilder.PARENTHESES_RIGHT);
                } else if (i == 0) {
                    holder.setText((int) R.id.tvStatus, (CharSequence) LocaleController.getString(R.string.AppVip));
                    if (CdnVipDetailsActivity.this.cdnVipInfoBean.isAutoPay()) {
                        btn.setText(LocaleController.getString(R.string.TurnOffAutomaticRenewal));
                        btn.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton));
                        btn.setOnClickListener(new View.OnClickListener() {
                            public final void onClick(View view) {
                                CdnVipDetailsActivity.Adapter.this.lambda$onBindViewHolder$1$CdnVipDetailsActivity$Adapter(view);
                            }
                        });
                        holder.setGone((View) btn, false);
                        holder.setGone((int) R.id.tvMoney, true);
                        holder.setText((int) R.id.tvExprieTime, (CharSequence) item.getBgnTimeFormat() + LocaleController.getString(R.string.SoFar));
                        return;
                    }
                } else {
                    holder.setText((int) R.id.tvStatus, (CharSequence) LocaleController.getString(R.string.AppVip) + SQLBuilder.PARENTHESES_LEFT + LocaleController.getString(R.string.RequestExpired) + SQLBuilder.PARENTHESES_RIGHT);
                }
                btn.setOnClickListener((View.OnClickListener) null);
                holder.setGone((View) btn, true);
                holder.setGone((int) R.id.tvMoney, false);
                holder.setText((int) R.id.tvMoney, (CharSequence) item.getMoney());
                holder.setText((int) R.id.tvExprieTime, (CharSequence) item.getBgnTimeFormat() + "-" + item.getEndTimeFormat());
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$1$CdnVipDetailsActivity$Adapter(View v) {
            WalletDialogUtil.showWalletDialog(CdnVipDetailsActivity.this, "", LocaleController.getString(R.string.CdnVipConfirmToCloseAutomicRenewal), LocaleController.getString(R.string.Cancel), LocaleController.getString(R.string.OK), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    CdnVipDetailsActivity.Adapter.this.lambda$null$0$CdnVipDetailsActivity$Adapter(dialogInterface, i);
                }
            }, (DialogInterface.OnDismissListener) null);
        }

        public /* synthetic */ void lambda$null$0$CdnVipDetailsActivity$Adapter(DialogInterface dialog, int which) {
            CdnVipDetailsActivity.this.openOrCloseAutoPay();
        }

        public int getItemViewType(int position) {
            return position == getItemCount() - 1 ? 1 : 0;
        }

        public int getItemCount() {
            if (CdnVipDetailsActivity.this.data == null) {
                return 0;
            }
            return CdnVipDetailsActivity.this.data.size() + 1;
        }
    }
}
