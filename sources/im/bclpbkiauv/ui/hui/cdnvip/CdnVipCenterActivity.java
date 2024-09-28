package im.bclpbkiauv.ui.hui.cdnvip;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.javaBean.cdnVip.CdnVipInfoBean;
import im.bclpbkiauv.javaBean.cdnVip.CdnVipUnitPriceBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.tgnet.ParamsUtil;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCCdn;
import im.bclpbkiauv.tgnet.TLRPCFriendsHub;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.KeyboardAdapter;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.cdnvip.CdnVipDetailsActivity;
import im.bclpbkiauv.ui.hui.mine.AboutAppActivity;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletAccountInfo;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.MryLinearLayout;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.utils.AesUtils;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class CdnVipCenterActivity extends BaseFragment implements CdnVipDetailsActivity.Delegate {
    private WalletAccountInfo accountInfo;
    @BindView(2131296338)
    FrameLayout actionBarContainer;
    private PageSelectionAdapter<Integer, PageHolder> adapter;
    @BindView(2131296406)
    MryTextView btn;
    @BindView(2131296439)
    View card;
    private String cdnPrice;
    /* access modifiers changed from: private */
    public CdnVipInfoBean cdnVipInfoBean;
    @BindView(2131296712)
    BackupImageView ivAvatar;
    @BindView(2131296715)
    ImageView ivBgBottom;
    @BindView(2131296716)
    ImageView ivBgTop;
    @BindView(2131296898)
    View llBottom;
    private LinearLayout llPayPassword;
    private List<Integer> mNumbers = new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, -10, 0, -11}));
    private TextView[] mTvPasswords;
    private int notEmptyTvCount;
    @BindView(2131297232)
    RecyclerListView rv;
    @BindView(2131297461)
    TextView tvBottomTips;
    private TextView tvForgotPassword;
    @BindView(2131297659)
    MryTextView tvStatusOrTime;
    @BindView(2131297664)
    MryTextView tvTeQuan;
    @BindView(2131297665)
    MryTextView tvTime;
    @BindView(2131297667)
    MryTextView tvTips;
    @BindView(2131297694)
    MryTextView tvUnitPrice;
    @BindView(2131297699)
    MryTextView tvUserName;
    @BindView(2131297705)
    MryTextView tvVipTop;
    private TLRPC.User user;

    public boolean onFragmentCreate() {
        TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId()));
        this.user = user2;
        return user2 != null;
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_cdn_vip_center, (ViewGroup) null, false);
        useButterKnife();
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar(this.fragmentView.findViewById(R.id.actionBarContainer));
        initView(context);
        initRv(context);
        this.fragmentView.postDelayed(new Runnable() {
            public final void run() {
                CdnVipCenterActivity.this.getUserCdnVipInfo();
            }
        }, 300);
        return this.fragmentView;
    }

    private void initActionBar(View container) {
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setBackgroundColor(0);
        this.actionBar.setTitle(LocaleController.getString(R.string.VipCenter));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CdnVipCenterActivity.this.finishFragment();
                    return;
                }
                CdnVipCenterActivity cdnVipCenterActivity = CdnVipCenterActivity.this;
                cdnVipCenterActivity.presentFragment(new CdnVipDetailsActivity(cdnVipCenterActivity.cdnVipInfoBean).setDelegate(CdnVipCenterActivity.this));
            }
        });
        ActionBarMenuItem item = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString(R.string.MemberDetails));
        ((TextView) item.getContentView()).setTypeface((Typeface) null);
        ((TextView) item.getContentView()).setTextSize(14.0f);
        this.actionBarContainer.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 80));
    }

    private void initView(Context context) {
        this.ivAvatar.setRoundRadius(AndroidUtilities.dp(8.0f));
        this.ivAvatar.setImage(ImageLocation.getForUser(this.user, false), "65_65", (Drawable) new AvatarDrawable(this.user), (Object) this.user);
        this.tvUserName.setText(UserObject.getName(this.user));
        this.tvVipTop.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.llBottom.bringToFront();
        if (Theme.getCurrentTheme() != null && Theme.getCurrentTheme().isDark()) {
            this.ivBgTop.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.alphaColor(0.1f, Theme.getColor(Theme.key_windowBackgroundGray)), PorterDuff.Mode.MULTIPLY));
            this.ivBgBottom.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.alphaColor(0.1f, Theme.getColor(Theme.key_windowBackgroundGray)), PorterDuff.Mode.MULTIPLY));
        }
        if (ScreenUtils.getAppScreenHeight() >= 2340) {
            this.llBottom.setLayoutParams(new RelativeLayout.LayoutParams(-1, ScreenUtils.getScreenHeight() - AndroidUtilities.dp(160.0f)));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.tvBottomTips.getLayoutParams();
            lp.height = 0;
            lp.weight = 1.0f;
            this.tvBottomTips.setLayoutParams(lp);
        }
        this.card.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvTips.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.tvBottomTips.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        SpanUtils.with(this.tvTime).append("30").setFontSize(45, true).append(LocaleController.getString(R.string.TimeUnitOfDay)).setFontSize(20, true).setVerticalAlign(0).create();
        SpanUtils append = SpanUtils.with(this.tvBottomTips).append(LocaleController.getString(R.string.CdbVipBottomTips1)).append(LocaleController.getString(R.string.AppVIPMembershipAgreement)).setClickSpan(new ClickableSpan() {
            public void onClick(View widget) {
            }

            public void updateDrawState(TextPaint ds) {
                ds.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                ds.setUnderlineText(true);
            }
        }).append(LocaleController.getString(R.string.CdbVipBottomTips3));
        append.append(LocaleController.getString(R.string.CdbVipBottomTips2) + LocaleController.getString(R.string.MemberServiceAgreement)).setClickSpan(new ClickableSpan() {
            public void onClick(View widget) {
            }

            public void updateDrawState(TextPaint ds) {
                ds.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                ds.setUnderlineText(true);
            }
        }).create();
    }

    private void initRv(Context context) {
        this.rv.setLayoutManager(new GridLayoutManager(context, 2));
        AnonymousClass4 r0 = new PageSelectionAdapter<Integer, PageHolder>(context) {
            public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
                return new PageHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_cdb_vip_center, parent, false));
            }

            public void onBindViewHolderForChild(PageHolder holder, int position, Integer item) {
                String str;
                String str2;
                String str3;
                MryLinearLayout card = (MryLinearLayout) holder.itemView;
                card.setBorderColor(Theme.getColor(Theme.key_divider));
                card.setBorderWidth(1);
                card.setRadius(AndroidUtilities.dp(5.0f));
                card.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                holder.setTextColorThemeGray((int) R.id.tv1);
                holder.setTextColorThemeGray((int) R.id.tv2);
                if (position == 0 || position == 1) {
                    holder.setGone((int) R.id.tv2, false);
                    holder.setImageResId((int) R.id.iv, position == 0 ? R.mipmap.cv_center_tequan1 : R.mipmap.cv_center_tequan2);
                    if (position == 0) {
                        str = LocaleController.getString(R.string.CdnVipTeQuan1);
                    } else {
                        str = LocaleController.getString(R.string.CdnVipTeQuan2);
                    }
                    holder.setText((int) R.id.tv1, (CharSequence) str);
                    if (position == 0) {
                        str2 = LocaleController.getString(R.string.CdnVipTeQuan1_1);
                    } else {
                        str2 = LocaleController.getString(R.string.CdnVipTeQuan2_1);
                    }
                    holder.setText((int) R.id.tv2, (CharSequence) str2);
                    return;
                }
                holder.setImageResId((int) R.id.iv, position == 2 ? R.mipmap.cv_center_tequan3 : R.mipmap.cv_center_tequan4);
                if (position == 2) {
                    str3 = LocaleController.getString(R.string.CdnVipTeQuan3);
                } else {
                    str3 = LocaleController.getString(R.string.CdnVipTeQuan4);
                }
                holder.setText((int) R.id.tv1, (CharSequence) str3);
                holder.setGone((int) R.id.tv2, true);
            }

            /* access modifiers changed from: protected */
            public boolean isEnableForChild(PageHolder holder) {
                return false;
            }
        };
        this.adapter = r0;
        r0.setData(new ArrayList(Arrays.asList(new Integer[]{0, 1, 2, 3})));
        this.rv.setAdapter(this.adapter);
    }

    /* access modifiers changed from: private */
    public void getUserCdnVipInfo() {
        TLRPCCdn.TL_getUserCdnVipInfo req = new TLRPCCdn.TL_getUserCdnVipInfo();
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                CdnVipCenterActivity.this.lambda$getUserCdnVipInfo$0$CdnVipCenterActivity(dialogInterface);
            }
        });
        progressDialog.show();
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CdnVipCenterActivity.this.lambda$getUserCdnVipInfo$1$CdnVipCenterActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getUserCdnVipInfo$0$CdnVipCenterActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
    }

    public /* synthetic */ void lambda$getUserCdnVipInfo$1$CdnVipCenterActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        parseCdnVipInfo(progressDialog, error, response, 0);
    }

    private void parseCdnVipInfo(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response, int type) {
        AndroidUtilities.runOnUIThread(new Runnable(error, progressDialog, response, type) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ AlertDialog f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                CdnVipCenterActivity.this.lambda$parseCdnVipInfo$2$CdnVipCenterActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$parseCdnVipInfo$2$CdnVipCenterActivity(TLRPC.TL_error error, AlertDialog progressDialog, TLObject response, int type) {
        if (error != null) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            parseError(0, error.text);
        } else if (response instanceof TLRPCCdn.TL_userCdnVipInfo) {
            try {
                this.cdnVipInfoBean = (CdnVipInfoBean) GsonUtils.fromJson(((TLRPCCdn.TL_userCdnVipInfo) response).vip_info.data, CdnVipInfoBean.class);
                setViewData();
                if (type == 0) {
                    getCdnVipUnitPrice(progressDialog);
                } else if (type == 1) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    WalletDialogUtil.showConfirmBtnWalletDialog(this, LocaleController.getString(R.string.AppCdnVipOpenSuccess), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
                    getNotificationCenter().postNotificationName(NotificationCenter.cdnVipBuySuccess, new Object[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                parseError(0, e.getMessage());
            }
        }
    }

    private void setViewData() {
        CdnVipInfoBean cdnVipInfoBean2 = this.cdnVipInfoBean;
        if (cdnVipInfoBean2 == null || !cdnVipInfoBean2.cdnVipIsAvailable()) {
            this.btn.setText(LocaleController.getString(R.string.OpenAppVIPNow));
            this.tvVipTop.setBackgroundResource(R.mipmap.cv_center_top_is_vip_false);
            this.tvVipTop.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            this.tvStatusOrTime.setText(LocaleController.getString(R.string.ChooseTheOpeningTime));
            this.tvTips.setText(LocaleController.getString(R.string.CdnVipCenterTips));
            return;
        }
        this.btn.setText(LocaleController.getString(R.string.SeeCdnVipDetails));
        this.tvVipTop.setBackgroundResource(R.mipmap.cv_center_top_is_vip_true);
        this.tvVipTop.setTextColor(-5476835);
        this.tvStatusOrTime.setText(LocaleController.getString(R.string.AlreadyIsCdnVip));
        if (this.cdnVipInfoBean.isAutoPay()) {
            this.tvTips.setText(LocaleController.getString(R.string.CdnVipCenterTips2));
            return;
        }
        MryTextView mryTextView = this.tvTips;
        mryTextView.setText(LocaleController.getString(R.string.CdnVipExpirationTime) + this.cdnVipInfoBean.getEndTimeFormat());
    }

    private void getCdnVipUnitPrice(AlertDialog progressDialog) {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCFriendsHub.TL_GetOtherConfig(), new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CdnVipCenterActivity.this.lambda$getCdnVipUnitPrice$4$CdnVipCenterActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getCdnVipUnitPrice$4$CdnVipCenterActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                CdnVipCenterActivity.this.lambda$null$3$CdnVipCenterActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$CdnVipCenterActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (error != null) {
            parseError(0, error.text);
            return;
        }
        TLRPCFriendsHub.TL_OtherConfig result = (TLRPCFriendsHub.TL_OtherConfig) response;
        try {
            if (result.data != null && !TextUtils.isEmpty(result.data.data)) {
                new JSONObject(result.data.data);
                CdnVipUnitPriceBean bean = (CdnVipUnitPriceBean) GsonUtils.fromJson(result.data.data, CdnVipUnitPriceBean.class);
                if (bean.getCdnPrice().size() > 0) {
                    this.cdnPrice = bean.getCdnPrice().get(0).getPriceStandard(0);
                    MryTextView mryTextView = this.tvUnitPrice;
                    mryTextView.setText("₫" + this.cdnPrice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            parseError(0, e.getMessage());
        }
    }

    private void openCdnVip(String payPwd) {
        TLRPCCdn.TL_payCdnVip req = new TLRPCCdn.TL_payCdnVip();
        req.req_info = new TLRPC.TL_dataJSON();
        TLRPC.TL_dataJSON tL_dataJSON = req.req_info;
        String[] strArr = {"payformonth", "level", "paypassword"};
        Object[] objArr = new Object[3];
        objArr[0] = 1;
        CdnVipInfoBean cdnVipInfoBean2 = this.cdnVipInfoBean;
        objArr[1] = Integer.valueOf(cdnVipInfoBean2 != null ? cdnVipInfoBean2.level : 1);
        objArr[2] = payPwd;
        tL_dataJSON.data = ParamsUtil.toJson(strArr, objArr);
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                CdnVipCenterActivity.this.lambda$openCdnVip$5$CdnVipCenterActivity(dialogInterface);
            }
        });
        progressDialog.show();
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CdnVipCenterActivity.this.lambda$openCdnVip$6$CdnVipCenterActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$openCdnVip$5$CdnVipCenterActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
    }

    public /* synthetic */ void lambda$openCdnVip$6$CdnVipCenterActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        parseCdnVipInfo(progressDialog, error, response, 1);
    }

    private void getUserAccountInfo() {
        TLRPCWallet.TL_getPaymentAccountInfo req = new TLRPCWallet.TL_getPaymentAccountInfo();
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                CdnVipCenterActivity.this.lambda$getUserAccountInfo$7$CdnVipCenterActivity(dialogInterface);
            }
        });
        progressDialog.show();
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CdnVipCenterActivity.this.lambda$getUserAccountInfo$14$CdnVipCenterActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getUserAccountInfo$7$CdnVipCenterActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
    }

    public /* synthetic */ void lambda$getUserAccountInfo$14$CdnVipCenterActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                CdnVipCenterActivity.this.lambda$null$13$CdnVipCenterActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$13$CdnVipCenterActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response) {
        TLRPC.TL_error tL_error = error;
        TLObject tLObject = response;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (tL_error != null) {
            parseError(tL_error.code, tL_error.text);
        } else if (tLObject instanceof TLRPCWallet.TL_paymentAccountInfoNotExist) {
            WalletDialogUtil.showWalletDialog(this, "", LocaleController.formatString("AccountInfoNotCompleted", R.string.AccountInfoNotCompleted, LocaleController.getString(R.string.BuyCdnVip), LocaleController.getString(R.string.Retry)), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToWalletCenter", R.string.GoToWalletCenter), (DialogInterface.OnClickListener) null, $$Lambda$CdnVipCenterActivity$euJGaNmjgGHpHcEv3Vgtb5HEFuw.INSTANCE, (DialogInterface.OnDismissListener) null);
        } else {
            TLApiModel<WalletAccountInfo> model = TLJsonResolve.parse(tLObject, (Class<?>) WalletAccountInfo.class);
            if (model.isSuccess()) {
                WalletAccountInfo walletAccountInfo = (WalletAccountInfo) model.model;
                this.accountInfo = walletAccountInfo;
                WalletConfigBean.setWalletAccountInfo(walletAccountInfo);
                WalletConfigBean.setConfigValue(((WalletAccountInfo) model.model).getRiskList());
                if (this.accountInfo.isLocked()) {
                    WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString(R.string.PleaseContractServerToFindPayPasswordOrTryIt24HoursLater), LocaleController.getString(R.string.Close), LocaleController.getString(R.string.ContactCustomerService), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            CdnVipCenterActivity.this.lambda$null$9$CdnVipCenterActivity(dialogInterface, i);
                        }
                    }, (DialogInterface.OnDismissListener) null);
                } else if (!this.accountInfo.hasNormalAuth()) {
                    WalletDialogUtil.showWalletDialog(this, "", LocaleController.formatString("BankCardNotBindTips", R.string.BankCardNotBindTips, LocaleController.getString(R.string.BuyCdnVip)), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToBind", R.string.GoToBind), (DialogInterface.OnClickListener) null, $$Lambda$CdnVipCenterActivity$ywEOnooYyFrdjfphPnmJZt4z5iA.INSTANCE, (DialogInterface.OnDismissListener) null);
                } else if (!this.accountInfo.hasBindBank()) {
                    WalletDialogUtil.showWalletDialog(this, "", LocaleController.formatString("BankCardNotBindTips", R.string.BankCardNotBindTips, LocaleController.getString(R.string.BuyCdnVip)), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToBind", R.string.GoToBind), (DialogInterface.OnClickListener) null, $$Lambda$CdnVipCenterActivity$vvO5GZ7wejLhgurR_zZ14fPFmRM.INSTANCE, (DialogInterface.OnDismissListener) null);
                } else if (!this.accountInfo.hasPaypassword()) {
                    WalletDialogUtil.showWalletDialog(this, "", LocaleController.formatString("PayPasswordNotSetTips", R.string.PayPasswordNotSetTips, LocaleController.getString(R.string.BuyCdnVip)), LocaleController.getString("Close", R.string.Close), LocaleController.getString("redpacket_goto_set", R.string.redpacket_goto_set), (DialogInterface.OnClickListener) null, $$Lambda$CdnVipCenterActivity$r9i1z9JDrIPIii4bYlySphUsPbU.INSTANCE, (DialogInterface.OnDismissListener) null);
                } else {
                    showPayPwdDialog();
                }
            } else {
                parseError(0, model.message);
            }
        }
    }

    static /* synthetic */ void lambda$null$8(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$9$CdnVipCenterActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new AboutAppActivity());
    }

    static /* synthetic */ void lambda$null$10(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$null$11(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$null$12(DialogInterface dialogInterface, int i) {
    }

    private void showPayPwdDialog() {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        View sheet = LayoutInflater.from(getParentActivity()).inflate(R.layout.layout_hongbao_pay_pwd, (ViewGroup) null, false);
        builder.setCustomView(sheet);
        ((ImageView) sheet.findViewById(R.id.ivAlertClose)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CdnVipCenterActivity.this.lambda$showPayPwdDialog$15$CdnVipCenterActivity(view);
            }
        });
        ((TextView) sheet.findViewById(R.id.tvTitle)).setText(LocaleController.getString("AppCdnVipServiceMothlySubscription", R.string.AppCdnVipServiceMothlySubscription));
        TextView tvShowMoneyView = (TextView) sheet.findViewById(R.id.tvShowMoneyView);
        tvShowMoneyView.setTextColor(-109240);
        tvShowMoneyView.setText(DataTools.format2Decimals(this.cdnPrice));
        TextView tvMoneyUnit = (TextView) sheet.findViewById(R.id.tvMoneyUnit);
        TextView tvPayMode = (TextView) sheet.findViewById(R.id.tvPayMode);
        tvPayMode.setTextColor(-6710887);
        tvPayMode.setText(LocaleController.getString(R.string.HotCoinPay));
        SpanUtils spanTvBalance = SpanUtils.with((TextView) sheet.findViewById(R.id.tvBlance));
        spanTvBalance.setVerticalAlign(2).append(LocaleController.getString(R.string.friendscircle_publish_remain)).setForegroundColor(-6710887).append(" (").setForegroundColor(-6710887);
        if (BuildVars.EDITION == 0) {
            spanTvBalance.append(NumberUtil.replacesSientificE(this.accountInfo.getCashAmount() / 100.0d, "#0.00"));
            tvMoneyUnit.setText(LocaleController.getString(R.string.HotCoin));
        } else {
            spanTvBalance.append(NumberUtil.replacesSientificE(this.accountInfo.getCashAmount() / 100.0d, "#0.00"));
            tvMoneyUnit.setText("CG");
        }
        spanTvBalance.setForegroundColor(-16777216).append(SQLBuilder.PARENTHESES_RIGHT).setForegroundColor(-6710887).create();
        TextView textView = (TextView) sheet.findViewById(R.id.tvForgotPassword);
        this.tvForgotPassword = textView;
        textView.setOnClickListener($$Lambda$CdnVipCenterActivity$_7lvM_3A5n7z6hwM1sUSO_YSVwU.INSTANCE);
        this.llPayPassword = (LinearLayout) sheet.findViewById(R.id.ll_pay_password);
        TextView[] textViewArr = new TextView[6];
        this.mTvPasswords = textViewArr;
        textViewArr[0] = (TextView) sheet.findViewById(R.id.tv_password_1);
        this.mTvPasswords[1] = (TextView) sheet.findViewById(R.id.tv_password_2);
        this.mTvPasswords[2] = (TextView) sheet.findViewById(R.id.tv_password_3);
        this.mTvPasswords[3] = (TextView) sheet.findViewById(R.id.tv_password_4);
        this.mTvPasswords[4] = (TextView) sheet.findViewById(R.id.tv_password_5);
        this.mTvPasswords[5] = (TextView) sheet.findViewById(R.id.tv_password_6);
        GridView gvKeyboard = (GridView) sheet.findViewById(R.id.gvKeyboard);
        gvKeyboard.setAdapter(new KeyboardAdapter(this.mNumbers, getParentActivity()));
        gvKeyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                CdnVipCenterActivity.this.lambda$showPayPwdDialog$17$CdnVipCenterActivity(adapterView, view, i, j);
            }
        });
        showDialog(builder.create()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                CdnVipCenterActivity.this.lambda$showPayPwdDialog$18$CdnVipCenterActivity(dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$showPayPwdDialog$15$CdnVipCenterActivity(View v) {
        dismissCurrentDialog();
    }

    static /* synthetic */ void lambda$showPayPwdDialog$16(View v) {
    }

    public /* synthetic */ void lambda$showPayPwdDialog$17$CdnVipCenterActivity(AdapterView parent, View view, int position, long id) {
        if (position < 9 || position == 10) {
            int i = this.notEmptyTvCount;
            TextView[] textViewArr = this.mTvPasswords;
            if (i != textViewArr.length) {
                int length = textViewArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    TextView textView = textViewArr[i2];
                    if (TextUtils.isEmpty(textView.getText())) {
                        textView.setText(String.valueOf(this.mNumbers.get(position)));
                        this.notEmptyTvCount++;
                        break;
                    }
                    i2++;
                }
                if (this.notEmptyTvCount == this.mTvPasswords.length) {
                    StringBuilder password = new StringBuilder();
                    for (TextView textView2 : this.mTvPasswords) {
                        String text = textView2.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            password.append(text);
                        }
                    }
                    openCdnVip(AesUtils.encrypt(password.toString().trim()));
                }
            }
        } else if (position == 11) {
            for (int i3 = this.mTvPasswords.length - 1; i3 >= 0; i3--) {
                if (!TextUtils.isEmpty(this.mTvPasswords[i3].getText())) {
                    this.mTvPasswords[i3].setText((CharSequence) null);
                    this.notEmptyTvCount--;
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$showPayPwdDialog$18$CdnVipCenterActivity(DialogInterface dialog1) {
        this.notEmptyTvCount = 0;
    }

    /* access modifiers changed from: package-private */
    @OnClick({2131296406})
    public void onClick(View v) {
        CdnVipInfoBean cdnVipInfoBean2 = this.cdnVipInfoBean;
        if (cdnVipInfoBean2 == null) {
            return;
        }
        if (cdnVipInfoBean2.cdnVipIsAvailable()) {
            presentFragment(new CdnVipDetailsActivity(this.cdnVipInfoBean).setDelegate(this));
        } else if (!TextUtils.isEmpty(this.cdnPrice)) {
            getUserAccountInfo();
        }
    }

    private void parseError(int errorCode, String errorMsg) {
        WalletDialogUtil.showConfirmBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(errorMsg));
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onResult(CdnVipInfoBean cdnVipInfoBean2) {
        if (cdnVipInfoBean2 != null) {
            this.cdnVipInfoBean = cdnVipInfoBean2;
            setViewData();
        }
    }
}
