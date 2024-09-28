package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.HuanHuiUploadFileResponseBean;
import com.bjz.comm.net.factory.ApiHuanHuiFactory;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.GsonUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLApiModel2;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.PhotoAlbumPickerActivity;
import im.bclpbkiauv.ui.PhotoPickerActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.recyclerview.OnItemClickListener;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.BottomDialog;
import im.bclpbkiauv.ui.dialogs.WalletSelect1LineDialog;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.decoration.DefaultItemDecoration;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryFrameLayout;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import im.bclpbkiauv.ui.wallet.WalletWithdrawAddNewAccountActivity;
import im.bclpbkiauv.ui.wallet.model.Constants;
import im.bclpbkiauv.ui.wallet.model.WalletPaymentBankCardBean;
import im.bclpbkiauv.ui.wallet.model.WalletWithdrawTemplateBean;
import im.bclpbkiauv.ui.wallet.utils.ExceptionUtils;
import im.bclpbkiauv.ui.wallet.utils.GlideUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class WalletWithdrawAddNewAccountActivity extends BaseFragment {
    private static final String TAG = "WalletWithdrawAddNewAccountActivity";
    public static final int TYPE_WITHDRAW_ADD_NEW = 0;
    public static final int TYPE_WITHDRAW_MODIFY = 1;
    private Adapter adapter;
    private String currentPicturePath;
    private ActionBarMenuItem doneMenu;
    private MryEmptyView emptyView;
    private boolean isBinding;
    private boolean isSelectPicture;
    /* access modifiers changed from: private */
    public WalletPaymentBankCardBean paymentBankCardBean;
    private RecyclerListView rv;
    /* access modifiers changed from: private */
    public int selectChildPosition = -1;
    /* access modifiers changed from: private */
    public int selectGroupPosition = -1;
    private String supportId;
    /* access modifiers changed from: private */
    public List<WalletWithdrawTemplateBean> templateData;
    private String templateId;
    private Disposable uploadPictureDiaposable;

    public void setPaymentBankCardBean(WalletPaymentBankCardBean paymentBankCardBean2) {
        this.paymentBankCardBean = paymentBankCardBean2;
    }

    public WalletWithdrawAddNewAccountActivity() {
    }

    public WalletWithdrawAddNewAccountActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        if (getArguments() == null) {
            return false;
        }
        this.supportId = getArguments().getString("supportId", (String) null);
        String string = getArguments().getString("templateId", (String) null);
        this.templateId = string;
        String str = this.supportId;
        if (str == null || string == null || !NumberUtil.isNumber(str) || !NumberUtil.isNumber(this.templateId)) {
            return false;
        }
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        ScrollView container = new ScrollView(context);
        frameLayout.addView(container, LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView = frameLayout;
        this.fragmentView.setBackgroundResource(R.color.window_background_gray);
        container.setFillViewport(true);
        initActionBar();
        initView(context);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString(R.string.AddBankCardTitle));
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString(R.string.Done));
        this.doneMenu = addItem;
        ((TextView) addItem.getContentView()).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1) {
                    WalletWithdrawAddNewAccountActivity.this.finishFragment();
                } else if (WalletWithdrawAddNewAccountActivity.this.check(true)) {
                    WalletWithdrawAddNewAccountActivity.this.startToUploadPictrues();
                }
            }
        });
    }

    private void initView(Context context) {
        MryEmptyView mryEmptyView = new MryEmptyView(context);
        this.emptyView = mryEmptyView;
        mryEmptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.emptyView.attach((ViewGroup) this.fragmentView);
        LinearLayout cr = new LinearLayout(context);
        ((FrameLayout) this.fragmentView).addView(cr, LayoutHelper.createFrame(-1, -1.0f));
        cr.setOrientation(1);
        MryFrameLayout containerRv = new MryFrameLayout(context);
        cr.addView(containerRv, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.rv = recyclerListView;
        containerRv.addView(recyclerListView, LayoutHelper.createFrame(-1, -2, 51));
        this.rv.addItemDecoration(new DefaultItemDecoration().setDividerColor(0).setDividerHeight(AndroidUtilities.dp(12.0f)));
        this.rv.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.rv.setPadding(0, 0, 0, AndroidUtilities.dp(50.0f));
        this.rv.setClipToPadding(false);
        this.rv.setLayoutManager(new LinearLayoutManager(context));
        this.rv.setOnItemClickListener((RecyclerListView.OnItemClickListener) $$Lambda$WalletWithdrawAddNewAccountActivity$XZ6vjE5c_hC4Gez2EEby5VcNM.INSTANCE);
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        this.rv.setAdapter(adapter2);
        this.emptyView.showLoading();
    }

    static /* synthetic */ void lambda$initView$0(View view, int position) {
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    private void getTemplateData() {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCWallet.Builder().setBusinessKey("get_withdraw_template").addParam("templateId", Integer.valueOf(Integer.parseInt(this.templateId))).build(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletWithdrawAddNewAccountActivity.this.lambda$getTemplateData$4$WalletWithdrawAddNewAccountActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getTemplateData$4$WalletWithdrawAddNewAccountActivity(TLObject response, TLRPC.TL_error error) {
        if (!isFinishing()) {
            if (error != null || !(response instanceof TLRPCWallet.TL_paymentTransResult)) {
                AndroidUtilities.runOnUIThread(new Runnable(error) {
                    private final /* synthetic */ TLRPC.TL_error f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        WalletWithdrawAddNewAccountActivity.this.lambda$null$3$WalletWithdrawAddNewAccountActivity(this.f$1);
                    }
                });
                return;
            }
            TLApiModel<WalletWithdrawTemplateBean> model = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) response).data, (Class<?>) WalletWithdrawTemplateBean.class);
            if (model.isSuccess()) {
                this.templateData = WalletWithdrawTemplateBean.recreateData(model.modelList, this.paymentBankCardBean);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        WalletWithdrawAddNewAccountActivity.this.lambda$null$1$WalletWithdrawAddNewAccountActivity();
                    }
                });
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable(model) {
                private final /* synthetic */ TLApiModel f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WalletWithdrawAddNewAccountActivity.this.lambda$null$2$WalletWithdrawAddNewAccountActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$1$WalletWithdrawAddNewAccountActivity() {
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        MryEmptyView mryEmptyView = this.emptyView;
        if (mryEmptyView != null) {
            mryEmptyView.showContent();
        }
    }

    public /* synthetic */ void lambda$null$2$WalletWithdrawAddNewAccountActivity(TLApiModel model) {
        MryEmptyView mryEmptyView = this.emptyView;
        if (mryEmptyView != null) {
            mryEmptyView.showError(model.message);
        }
    }

    public /* synthetic */ void lambda$null$3$WalletWithdrawAddNewAccountActivity(TLRPC.TL_error error) {
        MryEmptyView mryEmptyView = this.emptyView;
        if (mryEmptyView != null) {
            mryEmptyView.showError(WalletErrorUtil.getErrorDescription(error.text));
        }
    }

    /* access modifiers changed from: private */
    public boolean check(boolean showToast) {
        List<WalletWithdrawTemplateBean> list;
        if (isFinishing() || this.rv == null || this.adapter == null || (list = this.templateData) == null) {
            return false;
        }
        for (WalletWithdrawTemplateBean b : list) {
            if (b != null) {
                if (b.isTypeInputText()) {
                    if (TextUtils.isEmpty(b.getTextInput())) {
                        if (showToast) {
                            ToastUtils.show((CharSequence) b.getExplan());
                        }
                        return false;
                    }
                } else if (b.isTypeSelect()) {
                    if (b.getSelectDictItem() == null) {
                        if (showToast) {
                            ToastUtils.show((CharSequence) b.getExplan());
                        }
                        return false;
                    }
                } else if (!b.isTypePicture()) {
                    continue;
                } else if (b.getPictureArray() == null) {
                    if (showToast) {
                        ToastUtils.show((CharSequence) b.getExplan());
                    }
                    return false;
                } else {
                    for (int i = 0; i < b.getPictureCount(); i++) {
                        if (!b.hasPictureDataInIndex(i, this.paymentBankCardBean == null)) {
                            if (showToast) {
                                ToastUtils.show((CharSequence) b.getExplan());
                            }
                            return false;
                        }
                    }
                    continue;
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void changeNextButtonEnable() {
        if (this.doneMenu == null) {
            return;
        }
        if (check(false)) {
            ((TextView) this.doneMenu.getContentView()).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        } else {
            ((TextView) this.doneMenu.getContentView()).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        }
    }

    /* access modifiers changed from: private */
    public void startToUploadPictrues() {
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCancel(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                WalletWithdrawAddNewAccountActivity.this.lambda$startToUploadPictrues$5$WalletWithdrawAddNewAccountActivity(dialogInterface);
            }
        });
        showDialog(progressDialog);
        toUploadPictures();
    }

    public /* synthetic */ void lambda$startToUploadPictrues$5$WalletWithdrawAddNewAccountActivity(DialogInterface dialog) {
        cancelUploadPicture();
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
    }

    private void toUploadPictures() {
        List<WalletWithdrawTemplateBean> list = this.templateData;
        if (list != null && list.size() != 0) {
            boolean allUploaded = true;
            for (int i = 0; i < this.templateData.size(); i++) {
                WalletWithdrawTemplateBean t = this.templateData.get(i);
                if (t != null && t.isTypePicture() && t.getPictureArray() != null) {
                    int j = 0;
                    while (true) {
                        if (j >= t.getPictureArray().length) {
                            break;
                        }
                        WalletWithdrawTemplateBean.PictureBean pb = t.getPictureBeanIndex(j);
                        if (pb != null) {
                            if (pb.checkNeedToUploadPictureByIndex(this.paymentBankCardBean == null)) {
                                allUploaded = false;
                                toUploadPicture(i, j, pb.getPath());
                                break;
                            }
                        }
                        j++;
                    }
                }
            }
            if (allUploaded) {
                addNewPaymentAccount();
            }
        }
    }

    private void toUploadPicture(int groupPosition, int childPosition, String path) {
        File file = new File(path);
        if (file.exists()) {
            this.uploadPictureDiaposable = ApiHuanHuiFactory.getInstance().getApiHuanHui().uploadFile("*/*", "gzip, deflate, br", new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("type", "jpg").addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file)).build()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer(groupPosition, childPosition) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    WalletWithdrawAddNewAccountActivity.this.lambda$toUploadPicture$6$WalletWithdrawAddNewAccountActivity(this.f$1, this.f$2, (HuanHuiUploadFileResponseBean) obj);
                }
            }, new Consumer() {
                public final void accept(Object obj) {
                    WalletWithdrawAddNewAccountActivity.this.lambda$toUploadPicture$7$WalletWithdrawAddNewAccountActivity((Throwable) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$toUploadPicture$6$WalletWithdrawAddNewAccountActivity(int groupPosition, int childPosition, HuanHuiUploadFileResponseBean res) throws Exception {
        WalletWithdrawTemplateBean item;
        if (res == null) {
            return;
        }
        if (res.isSuccess()) {
            Adapter adapter2 = this.adapter;
            if (adapter2 != null && (item = adapter2.getItem(groupPosition)) != null) {
                item.setPictureUrl(childPosition, res.furl);
                toUploadPictures();
                return;
            }
            return;
        }
        dismissCurrentDialog();
        ToastUtils.show((CharSequence) LocaleController.getString(R.string.UploadPhotoFailTips));
        FileLog.e(TAG, "toUploadPicture error :" + res.desc);
    }

    public /* synthetic */ void lambda$toUploadPicture$7$WalletWithdrawAddNewAccountActivity(Throwable e) throws Exception {
        dismissCurrentDialog();
        ToastUtils.show((CharSequence) LocaleController.getString(R.string.UploadPhotoFailTips));
        FileLog.e(TAG, "toUploadPicture error", e);
    }

    private void addNewPaymentAccount() {
        List<WalletWithdrawTemplateBean> list = this.templateData;
        if (list != null && list.size() != 0 && !TextUtils.isEmpty(this.templateId) && !TextUtils.isEmpty(this.supportId) && !this.isBinding) {
            this.isBinding = true;
            TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
            builder.setBusinessKey(Constants.KEY_BANK_CARD_BIND);
            builder.addParam("userId", Integer.valueOf(getUserConfig().getClientUserId()));
            builder.addParam("templateId", this.templateId);
            builder.addParam("supportId", this.supportId);
            WalletPaymentBankCardBean walletPaymentBankCardBean = this.paymentBankCardBean;
            if (walletPaymentBankCardBean != null) {
                builder.addParam(TtmlNode.ATTR_ID, Integer.valueOf(walletPaymentBankCardBean.id));
            }
            builder.addParam("info", WalletWithdrawTemplateBean.createInfoJson(this.templateData));
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(builder.build(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WalletWithdrawAddNewAccountActivity.this.lambda$addNewPaymentAccount$9$WalletWithdrawAddNewAccountActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$addNewPaymentAccount$9$WalletWithdrawAddNewAccountActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                WalletWithdrawAddNewAccountActivity.this.lambda$null$8$WalletWithdrawAddNewAccountActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$WalletWithdrawAddNewAccountActivity(TLRPC.TL_error error, TLObject response) {
        if (!isFinishing()) {
            this.isBinding = false;
            dismissCurrentDialog();
            if (error != null || !(response instanceof TLRPCWallet.TL_paymentTransResult)) {
                ExceptionUtils.handlePayChannelException(error.text);
                return;
            }
            TLApiModel2 model = (TLApiModel2) GsonUtils.fromJson(((TLRPCWallet.TL_paymentTransResult) response).getData(), TLApiModel2.class);
            if (model.isSuccess()) {
                getNotificationCenter().postNotificationName(NotificationCenter.bandCardNeedReload, new Object[0]);
                finishFragment();
                return;
            }
            ExceptionUtils.handlePayChannelException(model.result_desc);
        }
    }

    /* access modifiers changed from: private */
    public void showSelectDictDialog(int position, List<WalletWithdrawTemplateBean.DictItemBean> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        WalletSelect1LineDialog<WalletWithdrawTemplateBean.DictItemBean> dialog = new WalletSelect1LineDialog<WalletWithdrawTemplateBean.DictItemBean>(getParentActivity()) {
            public void onBindViewHolder(RecyclerListView.SelectionAdapter adapter, WalletSelect1LineDialog.Holder1Line holder, int position, WalletWithdrawTemplateBean.DictItemBean item) {
                super.onBindViewHolder(adapter, holder, position, item);
                holder.setGone((View) holder.ivIcon, true);
                holder.setGone((View) holder.ivSelect, true);
                holder.setText((View) holder.tvTitle, (CharSequence) item.getDictLabel());
            }
        };
        dialog.getRecyclerViewContainerView().setRadiusAndShadow(AndroidUtilities.dp(12.0f), 3, 1, 1.0f);
        ((WalletSelect1LineDialog) dialog.setRvAutoHideWhenEmptyData(false)).setRecyclerViewMinHeight(AndroidUtilities.dp(250.0f));
        ((WalletSelect1LineDialog) dialog.setShowConfirmButtonView(false)).setOnItemClickListener(new OnItemClickListener(dialog, position) {
            private final /* synthetic */ WalletSelect1LineDialog f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onItemClick(View view, int i, Object obj) {
                WalletWithdrawAddNewAccountActivity.this.lambda$showSelectDictDialog$10$WalletWithdrawAddNewAccountActivity(this.f$1, this.f$2, view, i, (WalletWithdrawTemplateBean.DictItemBean) obj);
            }
        }).setData(data);
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$showSelectDictDialog$10$WalletWithdrawAddNewAccountActivity(WalletSelect1LineDialog dialog, int position, View view, int index, WalletWithdrawTemplateBean.DictItemBean item) {
        dialog.dismiss();
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            WalletWithdrawTemplateBean adapterItem = adapter2.getItem(position);
            if (adapterItem != null) {
                adapterItem.setSelectIndex(index);
            }
            this.adapter.notifyItemChanged(position);
        }
    }

    /* access modifiers changed from: private */
    public void showSelectPictureByPathDialog(int groupPosition, int childposition) {
        Adapter adapter2;
        WalletWithdrawTemplateBean item;
        if (getParentActivity() != null && (adapter2 = this.adapter) != null && (item = adapter2.getItem(groupPosition)) != null) {
            boolean hasPic = item.getPictureArray() != null && item.hasPicturePathInIndex(childposition);
            BottomDialog dialog = new BottomDialog(getParentActivity());
            dialog.addDialogItem(new BottomDialog.NormalTextItem(0, LocaleController.getString("FromCamera", R.string.FromCamera), true));
            dialog.addDialogItem(new BottomDialog.NormalTextItem(1, LocaleController.getString("FromGallery", R.string.FromGallery), hasPic));
            if (hasPic) {
                BottomDialog.NormalTextItem delectItem = new BottomDialog.NormalTextItem(2, LocaleController.getString("Delete", R.string.Delete), false);
                delectItem.getContentTextView().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                dialog.addDialogItem(delectItem);
            }
            dialog.setOnItemClickListener(new BottomDialog.OnItemClickListener(groupPosition, childposition) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onItemClick(int i, View view) {
                    WalletWithdrawAddNewAccountActivity.this.lambda$showSelectPictureByPathDialog$11$WalletWithdrawAddNewAccountActivity(this.f$1, this.f$2, i, view);
                }
            });
            showDialog(dialog);
        }
    }

    public /* synthetic */ void lambda$showSelectPictureByPathDialog$11$WalletWithdrawAddNewAccountActivity(int groupPosition, int childposition, int id, View v) {
        Adapter adapter2;
        WalletWithdrawTemplateBean adapterItem;
        dismissCurrentDialog();
        if (id != 2) {
            this.isSelectPicture = true;
            this.selectGroupPosition = groupPosition;
            this.selectChildPosition = childposition;
        }
        if (id == 0) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                openCamera();
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 11);
            }
        } else if (id == 1) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) == 0) {
                toGallery();
            } else {
                getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 10);
            }
        } else if (id == 2 && (adapter2 = this.adapter) != null && (adapterItem = adapter2.getItem(groupPosition)) != null) {
            adapterItem.setPicturePath(childposition, (String) null);
            this.adapter.notifyItemChanged(groupPosition);
        }
    }

    private void toGallery() {
        PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(2, false, false, (ChatActivity) null);
        fragment.setMaxSelectedPhotos(1, true);
        fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
            public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                if (!(WalletWithdrawAddNewAccountActivity.this.parentLayout == null || WalletWithdrawAddNewAccountActivity.this.parentLayout.fragmentsStack == null || WalletWithdrawAddNewAccountActivity.this.parentLayout.fragmentsStack.size() <= 0)) {
                    BaseFragment f = WalletWithdrawAddNewAccountActivity.this.parentLayout.fragmentsStack.get(WalletWithdrawAddNewAccountActivity.this.parentLayout.fragmentsStack.size() - 1);
                    if (f instanceof PhotoPickerActivity) {
                        f.finishFragment();
                    }
                }
                if (photos != null && photos.size() > 0) {
                    SendMessagesHelper.SendingMediaInfo info = photos.get(0);
                    if (info != null) {
                        WalletWithdrawAddNewAccountActivity walletWithdrawAddNewAccountActivity = WalletWithdrawAddNewAccountActivity.this;
                        walletWithdrawAddNewAccountActivity.showSelectPictureByPath(walletWithdrawAddNewAccountActivity.selectGroupPosition, WalletWithdrawAddNewAccountActivity.this.selectChildPosition, info.path);
                    }
                    WalletWithdrawAddNewAccountActivity.this.resetSelectPictureFlag();
                }
            }

            public void startPhotoSelectActivity() {
                try {
                    Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                    photoPickerIntent.setType("image/*");
                    WalletWithdrawAddNewAccountActivity.this.startActivityForResult(photoPickerIntent, 14);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        });
        presentFragment(fragment);
    }

    public void openCamera() {
        if (getParentActivity() != null) {
            try {
                Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                File image = AndroidUtilities.generatePicturePath();
                if (image != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        takePictureIntent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "im.bclpbkiauv.messenger.provider", image));
                        takePictureIntent.addFlags(2);
                        takePictureIntent.addFlags(1);
                    } else {
                        takePictureIntent.putExtra("output", Uri.fromFile(image));
                    }
                    this.currentPicturePath = image.getAbsolutePath();
                }
                startActivityForResult(takePictureIntent, 13);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showSelectPictureByPath(int selectGroupPosition2, int selectChildPosition2, String path) {
        Adapter adapter2 = this.adapter;
        if (adapter2 != null && selectGroupPosition2 >= 0 && selectChildPosition2 >= 0) {
            WalletWithdrawTemplateBean item = adapter2.getItem(selectGroupPosition2);
            if (item != null) {
                item.setPicturePath(selectChildPosition2, path);
            }
            this.adapter.notifyItemChanged(selectGroupPosition2);
            changeNextButtonEnable();
        }
    }

    /* access modifiers changed from: private */
    public void resetSelectPictureFlag() {
        this.isSelectPicture = false;
        this.selectGroupPosition = -1;
        this.selectChildPosition = -1;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        boolean isFromCamera;
        final String path;
        int orientation;
        int i = requestCode;
        if (resultCode == -1) {
            if (i == 13 || i == 14) {
                if (i == 13) {
                    isFromCamera = true;
                    path = this.currentPicturePath;
                } else {
                    isFromCamera = false;
                    path = AndroidUtilities.getPath(data.getData());
                }
                if (path != null) {
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    int orientation2 = 0;
                    try {
                        int exif = new ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        if (exif == 3) {
                            orientation2 = 180;
                        } else if (exif == 6) {
                            orientation2 = 90;
                        } else if (exif == 8) {
                            orientation2 = 270;
                        }
                        orientation = orientation2;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        orientation = 0;
                    }
                    ArrayList arrayList = new ArrayList();
                    int selectGroupIndex = this.selectGroupPosition;
                    MediaController.PhotoEntry photoEntry = r5;
                    int selectChildIndex = this.selectChildPosition;
                    int selectChildIndex2 = orientation;
                    int i2 = orientation;
                    final int selectGroupIndex2 = selectGroupIndex;
                    MediaController.PhotoEntry photoEntry2 = new MediaController.PhotoEntry(0, 0, 0, path, selectChildIndex2, false);
                    arrayList.add(photoEntry);
                    PhotoViewer.getInstance().setIsFcCrop(false);
                    PhotoViewer.getInstance().setMaxSelectedPhotos(1, true);
                    final int selectChildIndex3 = selectChildIndex;
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 3, new PhotoViewer.EmptyPhotoViewerProvider() {
                        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
                            WalletWithdrawAddNewAccountActivity.this.showSelectPictureByPath(selectGroupIndex2, selectChildIndex3, path);
                        }

                        public boolean allowCaption() {
                            return false;
                        }

                        public boolean canScrollAway() {
                            return false;
                        }

                        public boolean canCaptureMorePhotos() {
                            return false;
                        }
                    }, (ChatActivity) null);
                    if (isFromCamera) {
                        AndroidUtilities.addMediaToGallery(path);
                        this.currentPicturePath = null;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        resetSelectPictureFlag();
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            boolean tag = true;
            int length = grantResults.length;
            for (int i = 0; i < length; i++) {
                tag = grantResults[i] == 0;
                if (!tag) {
                    break;
                }
            }
            if (!tag || !this.isSelectPicture) {
                resetSelectPictureFlag();
            } else {
                toGallery();
            }
        } else if (requestCode != 11) {
        } else {
            if (grantResults == null || grantResults[0] != 0) {
                resetSelectPictureFlag();
            } else {
                openCamera();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if (isOpen && !backward) {
            getTemplateData();
        }
    }

    private String guessMimeType(String path) {
        String contentTypeFor = null;
        try {
            contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            return "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void cancelUploadPicture() {
        Disposable disposable = this.uploadPictureDiaposable;
        if (disposable != null && !disposable.isDisposed()) {
            this.uploadPictureDiaposable.dispose();
        }
    }

    public void onFragmentDestroy() {
        cancelUploadPicture();
        RecyclerListView recyclerListView = this.rv;
        if (recyclerListView != null) {
            recyclerListView.setLayoutManager((RecyclerView.LayoutManager) null);
            this.rv.setAdapter((RecyclerView.Adapter) null);
        }
        super.onFragmentDestroy();
        this.doneMenu = null;
        this.rv = null;
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.destroy();
            this.adapter = null;
        }
        this.paymentBankCardBean = null;
        List<WalletWithdrawTemplateBean> list = this.templateData;
        if (list != null) {
            list.clear();
            this.templateData = null;
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        static final int VIEW_TYPE_INPUT_TEXT = 0;
        static final int VIEW_TYPE_PICTURE = 2;
        static final int VIEW_TYPE_SELECT = 1;
        private SparseArray<PictureAdapter> pictureAdapterSparseArray = new SparseArray<>();
        private SparseArray<WatcherWrapper> watcherSparseArray = new SparseArray<>();

        public Adapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = LayoutInflater.from(WalletWithdrawAddNewAccountActivity.this.getParentActivity()).inflate(R.layout.wallet_item_withdraw_add_way_type_input, parent, false);
            } else if (viewType == 1) {
                view = LayoutInflater.from(WalletWithdrawAddNewAccountActivity.this.getParentActivity()).inflate(R.layout.wallet_item_withdraw_add_way_type_select, parent, false);
            } else if (viewType == 2) {
                View view2 = LayoutInflater.from(WalletWithdrawAddNewAccountActivity.this.getParentActivity()).inflate(R.layout.wallet_item_withdraw_add_way_type_picture, parent, false);
                RecyclerListView rv = (RecyclerListView) view2.findViewById(R.id.rv);
                if (rv != null) {
                    rv.setLayoutManager(new GridLayoutManager(parent.getContext(), 2));
                    rv.addItemDecoration(new DefaultItemDecoration().setDividerColor(0).setDividerWidth(AndroidUtilities.dp(16.0f)).setDividerHeight(AndroidUtilities.dp(16.0f)));
                }
                view = view2;
            } else {
                throw new IllegalArgumentException("Unsupport withdraw template type");
            }
            return new PageHolder(view, 0);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
            RecyclerListView rv;
            PageHolder holder = (PageHolder) holder1;
            final WalletWithdrawTemplateBean item = getItem(position);
            if (item != null) {
                int viewType = holder.getItemViewType();
                holder.setText((View) (MryTextView) holder.itemView.findViewById(R.id.tvTitle), (CharSequence) item.getDisplayName());
                if (viewType == 0) {
                    MryEditText et = (MryEditText) holder.itemView.findViewById(R.id.et);
                    holder.setOnClickListener((View) (ConstraintLayout) holder.itemView.findViewById(R.id.container), (View.OnClickListener) new View.OnClickListener(et) {
                        private final /* synthetic */ MryEditText f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(View view) {
                            WalletWithdrawAddNewAccountActivity.Adapter.this.lambda$onBindViewHolder$0$WalletWithdrawAddNewAccountActivity$Adapter(this.f$1, view);
                        }
                    });
                    if (item.getTextInput() == null) {
                        holder.setHint((View) et, (CharSequence) item.getExplan());
                    } else {
                        holder.setText((View) et, (CharSequence) item.getTextInput());
                    }
                    if (et != null && this.watcherSparseArray.get(position) == null) {
                        this.watcherSparseArray.put(position, new WatcherWrapper(et, new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                if (s != null) {
                                    item.setTextInput(s.toString());
                                    WalletWithdrawAddNewAccountActivity.this.changeNextButtonEnable();
                                }
                            }
                        }));
                        return;
                    }
                    return;
                }
                boolean z = true;
                if (1 == viewType) {
                    MryTextView tvContent = (MryTextView) holder.itemView.findViewById(R.id.tvContent);
                    holder.setOnClickListener((View) (ConstraintLayout) holder.itemView.findViewById(R.id.container), (View.OnClickListener) new View.OnClickListener(position, item) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ WalletWithdrawTemplateBean f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(View view) {
                            WalletWithdrawAddNewAccountActivity.Adapter.this.lambda$onBindViewHolder$1$WalletWithdrawAddNewAccountActivity$Adapter(this.f$1, this.f$2, view);
                        }
                    });
                    if (item.getSelectDictItem() == null) {
                        holder.setHint((View) tvContent, (CharSequence) item.getExplan());
                    } else {
                        holder.setText((View) tvContent, (CharSequence) item.getSelectDictItem().getDictLabel());
                    }
                    if (tvContent != null && this.watcherSparseArray.get(position) == null) {
                        this.watcherSparseArray.put(position, new WatcherWrapper(tvContent, new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                if (s != null) {
                                    item.setTextInput(s.toString());
                                    WalletWithdrawAddNewAccountActivity.this.changeNextButtonEnable();
                                }
                            }
                        }));
                    }
                } else if (2 == viewType && (rv = (RecyclerListView) holder.itemView.findViewById(R.id.rv)) != null && !rv.isComputingLayout()) {
                    rv.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(position) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onItemClick(View view, int i) {
                            WalletWithdrawAddNewAccountActivity.Adapter.this.lambda$onBindViewHolder$2$WalletWithdrawAddNewAccountActivity$Adapter(this.f$1, view, i);
                        }
                    });
                    PictureAdapter adapter = this.pictureAdapterSparseArray.get(position);
                    if (adapter == null) {
                        int pictureCount = item.getPictureCount();
                        if (WalletWithdrawAddNewAccountActivity.this.paymentBankCardBean != null) {
                            z = false;
                        }
                        PictureAdapter adapter2 = new PictureAdapter(pictureCount, z);
                        adapter2.data = item.getPictureArray();
                        rv.setAdapter(adapter2);
                        this.pictureAdapterSparseArray.put(position, adapter2);
                        return;
                    }
                    adapter.data = item.getPictureArray();
                    adapter.notifyDataSetChanged();
                }
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$WalletWithdrawAddNewAccountActivity$Adapter(final MryEditText et, View v) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    et.setFocusable(true);
                    et.setFocusableInTouchMode(true);
                    et.requestFocus();
                    AndroidUtilities.showKeyboard(et);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$1$WalletWithdrawAddNewAccountActivity$Adapter(int position, WalletWithdrawTemplateBean item, View v) {
            WalletWithdrawAddNewAccountActivity.this.showSelectDictDialog(position, item.getDictList());
        }

        public /* synthetic */ void lambda$onBindViewHolder$2$WalletWithdrawAddNewAccountActivity$Adapter(int position, View view, int childPosition) {
            WalletWithdrawAddNewAccountActivity.this.showSelectPictureByPathDialog(position, childPosition);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            WatcherWrapper watcherWrapper;
            super.onViewAttachedToWindow(holder);
            if (holder.getItemViewType() == 0 && (watcherWrapper = this.watcherSparseArray.get(holder.getAdapterPosition())) != null) {
                watcherWrapper.onResume();
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            WatcherWrapper watcherWrapper;
            super.onViewDetachedFromWindow(holder);
            if (holder.getItemViewType() == 0 && (watcherWrapper = this.watcherSparseArray.get(holder.getAdapterPosition())) != null) {
                watcherWrapper.onPasue();
            }
        }

        public int getItemViewType(int position) {
            WalletWithdrawTemplateBean item = getItem(position);
            if (item == null || item.isTypeInputText()) {
                return 0;
            }
            if (item.isTypeSelect()) {
                return 1;
            }
            if (item.isTypePicture()) {
                return 2;
            }
            return 0;
        }

        public int getItemCount() {
            if (WalletWithdrawAddNewAccountActivity.this.templateData != null) {
                return WalletWithdrawAddNewAccountActivity.this.templateData.size();
            }
            return 0;
        }

        /* access modifiers changed from: package-private */
        public WalletWithdrawTemplateBean getItem(int position) {
            if (WalletWithdrawAddNewAccountActivity.this.templateData == null || position < 0 || position >= WalletWithdrawAddNewAccountActivity.this.templateData.size()) {
                return null;
            }
            return (WalletWithdrawTemplateBean) WalletWithdrawAddNewAccountActivity.this.templateData.get(position);
        }

        /* access modifiers changed from: package-private */
        public void destroy() {
            SparseArray<WatcherWrapper> sparseArray = this.watcherSparseArray;
            if (sparseArray != null) {
                sparseArray.clear();
                this.watcherSparseArray = null;
            }
            SparseArray<PictureAdapter> sparseArray2 = this.pictureAdapterSparseArray;
            if (sparseArray2 != null) {
                sparseArray2.clear();
                this.pictureAdapterSparseArray = null;
            }
        }
    }

    public static class PictureAdapter extends RecyclerListView.SelectionAdapter {
        int count;
        WalletWithdrawTemplateBean.PictureBean[] data;
        boolean isAddNewAccount;

        public PictureAdapter(int count2, boolean isAddNewAccount2) {
            this.count = count2;
            this.data = new WalletWithdrawTemplateBean.PictureBean[count2];
            this.isAddNewAccount = isAddNewAccount2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_item_withdraw_add_way_type_picture_item, parent, false), ColorUtils.getColor(R.color.window_background_gray));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageView iv = (ImageView) holder.itemView.findViewById(R.id.iv);
            ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.ivAdd);
            if (iv != null) {
                WalletWithdrawTemplateBean.PictureBean item = getItem(position);
                if (item == null) {
                    iv.setImageResource(0);
                } else if (this.isAddNewAccount) {
                    if (!TextUtils.isEmpty(item.getUrl())) {
                        GlideUtil.loadUrl(iv, item.getUrl(), 0, 0);
                    } else {
                        GlideUtil.loadUrl(iv, item.getPath(), 0, 0);
                    }
                } else if (!TextUtils.isEmpty(item.getPath())) {
                    GlideUtil.loadUrl(iv, item.getPath(), 0, 0);
                } else {
                    GlideUtil.loadUrl(iv, item.getOriginUrl(), 0, 0);
                }
            }
        }

        public int getItemCount() {
            return this.count;
        }

        /* access modifiers changed from: package-private */
        public WalletWithdrawTemplateBean.PictureBean getItem(int position) {
            WalletWithdrawTemplateBean.PictureBean[] pictureBeanArr = this.data;
            if (pictureBeanArr == null || position < 0 || position >= pictureBeanArr.length) {
                return null;
            }
            return pictureBeanArr[position];
        }
    }

    private static class WatcherWrapper {
        private boolean isWatched;
        WeakReference<TextView> tvW;
        TextWatcher watcher;

        public WatcherWrapper(TextView tv, TextWatcher watcher2) {
            this.tvW = new WeakReference<>(tv);
            this.watcher = watcher2;
            onResume();
        }

        /* access modifiers changed from: package-private */
        public void onResume() {
            TextWatcher textWatcher;
            TextView tv = (TextView) this.tvW.get();
            if (tv != null && (textWatcher = this.watcher) != null && !this.isWatched) {
                this.isWatched = true;
                tv.addTextChangedListener(textWatcher);
            }
        }

        /* access modifiers changed from: package-private */
        public void onPasue() {
            TextWatcher textWatcher;
            TextView tv = (TextView) this.tvW.get();
            if (!(tv == null || (textWatcher = this.watcher) == null)) {
                tv.removeTextChangedListener(textWatcher);
            }
            this.isWatched = false;
        }
    }
}
