package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.SpanUtils;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.javaBean.hongbao.UnifyBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import im.bclpbkiauv.ui.hui.adapter.InputNumberFilter;
import im.bclpbkiauv.ui.hui.mine.AboutAppActivity;
import im.bclpbkiauv.ui.hui.packet.SelecteContactsActivity;
import im.bclpbkiauv.ui.hui.packet.bean.RepacketRequest;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletAccountInfo;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.SegmenteView;
import im.bclpbkiauv.ui.utils.AesUtils;
import im.bclpbkiauv.ui.utils.number.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedpktGroupSendActivity extends BaseFragment {
    public static final int RPT_GROUP_EXCLUSIVE = 1002;
    public static final int RPT_GROUP_NORMAL = 1000;
    public static final int RPT_GROUP_RANDOM = 1001;
    private WalletAccountInfo accountInfo;
    private BackupImageView bivUserSelected;
    /* access modifiers changed from: private */
    public Button btnRptSend;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull chatFull;
    private EditText etRptAmountEditView;
    private EditText etRptGreetEditView;
    private EditText etRptTargetEditView;
    private ImageView ivCoinLogo;
    private LinearLayout llPayPassword;
    private LinearLayout llPersonLayout;
    private LinearLayout llSelectedLayout;
    private LinearLayout llTargetLayout;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public List<Integer> mNumbers = new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, -10, 0, -11}));
    private TextView[] mTvPasswords;
    private int notEmptyTvCount;
    private XAlertDialog progressView;
    private int reqId = -1;
    private SegmenteView segRptType;
    private TLRPC.User selectedUser = null;
    private TLRPC.Chat toChat;
    private TextView tvForgotPassword;
    private TextView tvHongbaoUnit;
    private TextView tvMoneyUnit;
    private TextView tvRpkPromt;
    /* access modifiers changed from: private */
    public TextView tvRptAmountHint;
    /* access modifiers changed from: private */
    public TextView tvRptGreetHint;
    private TextView tvRptMountText;
    private TextView tvRptPersonName;
    /* access modifiers changed from: private */
    public TextView tvRptTargetHint;
    private TextView tvRptTargetText;
    private TextView tvRptTargetUnit;
    private TextView tvRptTypeDesc;
    private TextView tvRptTypeText;
    private TextView tvUserName;
    private int typeRpt = 1001;

    public RedpktGroupSendActivity(Bundle args) {
        super(args);
    }

    public void setAccountInfo(WalletAccountInfo accountInfo2) {
        this.accountInfo = accountInfo2;
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onResume() {
        super.onResume();
        getParentActivity().setRequestedOrientation(1);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getParentActivity().setRequestedOrientation(2);
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_hongbao_group_send_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(-328966);
        initActionBar();
        initView();
        updateView();
        return this.fragmentView;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if (isOpen && this.accountInfo == null) {
            getAccountInfo();
        }
    }

    public void setToChat(TLRPC.Chat toChat2) {
        this.toChat = toChat2;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("redpacket_send", R.string.redpacket_send));
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(-1, false);
        this.actionBar.setItemsBackgroundColor(-1, true);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setItemsColor(-1, true);
        this.actionBar.setBackground(this.gameActionBarBackgroundDrawable);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktGroupSendActivity.this.lambda$initActionBar$0$RedpktGroupSendActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$0$RedpktGroupSendActivity(View v) {
        finishFragment();
    }

    private void resetAmountPromt() {
        this.tvRpkPromt.setVisibility(8);
        this.tvRptTypeText.setTextColor(-16777216);
        this.etRptAmountEditView.setTextColor(-16777216);
        this.tvHongbaoUnit.setTextColor(-16777216);
    }

    private void resetCountPromt() {
        this.tvRpkPromt.setVisibility(8);
        this.tvRptTargetText.setTextColor(-16777216);
        this.etRptTargetEditView.setTextColor(-16777216);
        this.tvRptTargetUnit.setTextColor(-16777216);
    }

    private void showAmountPromt(String text) {
        this.tvRpkPromt.setVisibility(0);
        this.tvRpkPromt.setText(text);
        this.tvRptTypeText.setTextColor(-109240);
        this.etRptAmountEditView.setTextColor(-109240);
        this.tvHongbaoUnit.setTextColor(-109240);
    }

    private void showCountPromt(String text) {
        this.tvRpkPromt.setVisibility(0);
        if (!TextUtils.isEmpty(text)) {
            this.tvRpkPromt.setText(text);
        }
        this.tvRptTargetText.setTextColor(-109240);
        this.etRptTargetEditView.setTextColor(-109240);
        this.tvRptTargetUnit.setTextColor(-109240);
    }

    /* access modifiers changed from: private */
    public boolean updateAmountPromt() {
        int i = this.typeRpt;
        if (i == 1000) {
            String sAmount = this.etRptAmountEditView.getText().toString().trim();
            if (sAmount.length() <= 0) {
                resetAmountPromt();
                return false;
            }
            BigDecimal amount = new BigDecimal(sAmount);
            if (amount.compareTo(new BigDecimal("0.01")) < 0) {
                if (BuildVars.EDITION == 0) {
                    showAmountPromt(LocaleController.getString("redpacket_single_limit_personal", R.string.redpacket_single_limit_personal2));
                } else {
                    showAmountPromt(LocaleController.getString("redpacket_single_limit_enterprise", R.string.redpacket_single_limit_enterprise));
                }
                return false;
            }
            int compareTo = amount.compareTo(new BigDecimal("10000"));
            resetAmountPromt();
            String sCount = this.etRptTargetEditView.getText().toString().trim();
            if (sCount.length() <= 0) {
                resetCountPromt();
                return false;
            }
            int parseInt = Integer.parseInt(sCount);
            if (parseInt < 1 || parseInt > 500) {
                showCountPromt(LocaleController.getString("redpacket_number_not_zero", R.string.redpacket_number_not_zero));
                return false;
            }
            resetCountPromt();
            int compareTo2 = amount.multiply(new BigDecimal(sCount)).compareTo(new BigDecimal("10000"));
            resetAmountPromt();
            resetCountPromt();
            return true;
        } else if (i == 1001) {
            String sAmount2 = this.etRptAmountEditView.getText().toString().trim();
            if (sAmount2.length() <= 0) {
                resetAmountPromt();
                return false;
            }
            BigDecimal amount2 = new BigDecimal(sAmount2);
            int compareTo3 = amount2.compareTo(new BigDecimal("10000"));
            resetAmountPromt();
            String count = this.etRptTargetEditView.getText().toString();
            if (count.length() <= 0) {
                resetCountPromt();
                return false;
            }
            int parseInt2 = Integer.parseInt(count);
            if (parseInt2 < 1 || parseInt2 > 500) {
                showCountPromt(LocaleController.getString("redpacket_number_not_zero", R.string.redpacket_number_not_zero));
                return false;
            }
            resetCountPromt();
            if (amount2.divide(new BigDecimal(count), 6, RoundingMode.CEILING).compareTo(new BigDecimal("0.01")) < 0) {
                if (BuildVars.EDITION == 0) {
                    showAmountPromt(LocaleController.getString("redpacket_single_limit_personal", R.string.redpacket_single_limit_personal));
                    showCountPromt("");
                } else {
                    showAmountPromt(LocaleController.getString("redpacket_single_limit_enterprise", R.string.redpacket_single_limit_enterprise));
                    showCountPromt("");
                }
                return false;
            }
            resetAmountPromt();
            resetCountPromt();
            return true;
        } else if (i != 1002) {
            return false;
        } else {
            String sAmount3 = this.etRptAmountEditView.getText().toString().trim();
            if (sAmount3.length() <= 0) {
                resetAmountPromt();
                return false;
            }
            BigDecimal amount3 = new BigDecimal(sAmount3);
            if (amount3.compareTo(new BigDecimal("0.01")) < 0) {
                showAmountPromt(LocaleController.getString("redpacket_single_limit_personal", R.string.redpacket_single_limit_personal3));
                return false;
            }
            int ret = amount3.compareTo(new BigDecimal("10000"));
            resetAmountPromt();
            if (this.selectedUser != null) {
                return true;
            }
            return false;
        }
    }

    private void updateView() {
        this.etRptAmountEditView.setText("");
        this.etRptGreetEditView.setText("");
        this.etRptTargetEditView.setText("");
        this.selectedUser = null;
        resetAmountPromt();
        resetCountPromt();
        int i = this.typeRpt;
        if (i == 1000) {
            this.tvRptTypeText.setVisibility(0);
            this.ivCoinLogo.setVisibility(4);
            this.tvRptTypeText.setText(LocaleController.getString("repacket_single_amount", R.string.repacket_single_amount));
            this.tvRptAmountHint.setText(LocaleController.getString("redpacket_amount", R.string.redpacket_amount));
        } else if (i == 1001) {
            this.tvRptTypeText.setVisibility(0);
            this.ivCoinLogo.setVisibility(4);
            this.tvRptTypeText.setText(LocaleController.getString("redpacket_total_amount", R.string.redpacket_total_amount));
            this.tvRptAmountHint.setText(LocaleController.getString("redpacket_amount", R.string.redpacket_amount));
        } else if (i == 1002) {
            this.tvRptTypeText.setVisibility(4);
            this.ivCoinLogo.setVisibility(0);
            this.tvRptAmountHint.setText(LocaleController.getString("redpacket_amount", R.string.redpacket_amount));
        }
        int i2 = this.typeRpt;
        if (i2 == 1000) {
            this.tvRptTargetText.setText(LocaleController.getString("redpacket_count", R.string.redpacket_count));
            this.tvRptTargetUnit.setText(LocaleController.getString("redpacket_each", R.string.redpacket_each));
            TextView textView = this.tvRptTargetHint;
            Object[] objArr = new Object[1];
            TLRPC.ChatFull chatFull2 = this.chatFull;
            objArr[0] = Integer.valueOf(chatFull2 != null ? chatFull2.participants_count : 0);
            textView.setText(LocaleController.formatString("group_person_number", R.string.group_person_number, objArr));
            this.llTargetLayout.setVisibility(0);
            this.llPersonLayout.setVisibility(8);
        } else if (i2 == 1001) {
            this.tvRptTargetText.setText(LocaleController.getString("redpacket_count", R.string.redpacket_count));
            this.tvRptTargetUnit.setText(LocaleController.getString("redpacket_each", R.string.redpacket_each));
            TextView textView2 = this.tvRptTargetHint;
            Object[] objArr2 = new Object[1];
            TLRPC.ChatFull chatFull3 = this.chatFull;
            objArr2[0] = Integer.valueOf(chatFull3 != null ? chatFull3.participants_count : 0);
            textView2.setText(LocaleController.formatString("group_person_number", R.string.group_person_number, objArr2));
            this.llTargetLayout.setVisibility(0);
            this.llPersonLayout.setVisibility(8);
        } else if (i2 == 1002) {
            this.tvRptTargetText.setText(LocaleController.getString("redpacket_recipients", R.string.redpacket_recipients));
            this.tvRptTargetHint.setText(LocaleController.getString("redpacket_choose_person", R.string.redpacket_choose_person));
            this.tvRptTargetUnit.setText(LocaleController.getString("redpacket_each", R.string.redpacket_each));
            this.llTargetLayout.setVisibility(8);
            this.llPersonLayout.setVisibility(0);
            this.llSelectedLayout.setVisibility(8);
            this.tvRptPersonName.setVisibility(0);
        }
        int i3 = this.typeRpt;
        if (i3 == 1000) {
            this.tvRptTypeDesc.setText("");
        } else if (i3 == 1001) {
            this.tvRptTypeDesc.setText("");
        } else if (i3 == 1002) {
            this.tvRptTypeDesc.setText(LocaleController.getString("redpacket_special", R.string.redpacket_special));
        }
    }

    private void initView() {
        this.segRptType = (SegmenteView) this.fragmentView.findViewById(R.id.segRptType);
        this.llPersonLayout = (LinearLayout) this.fragmentView.findViewById(R.id.llPersonLayout);
        this.llTargetLayout = (LinearLayout) this.fragmentView.findViewById(R.id.llTargetLayout);
        this.llSelectedLayout = (LinearLayout) this.fragmentView.findViewById(R.id.ll_selected_layout);
        this.bivUserSelected = (BackupImageView) this.fragmentView.findViewById(R.id.iv_user_selected);
        this.tvUserName = (TextView) this.fragmentView.findViewById(R.id.tv_user_name);
        this.tvRptMountText = (TextView) this.fragmentView.findViewById(R.id.tvRptMountText);
        this.ivCoinLogo = (ImageView) this.fragmentView.findViewById(R.id.ivCoinLogo);
        this.tvRptTypeText = (TextView) this.fragmentView.findViewById(R.id.tvRptTypeText);
        this.etRptAmountEditView = (EditText) this.fragmentView.findViewById(R.id.etRptAmountEditView);
        this.tvRptAmountHint = (TextView) this.fragmentView.findViewById(R.id.tvRptAmountHint);
        this.tvRpkPromt = (TextView) this.fragmentView.findViewById(R.id.tvRpkPromt);
        this.tvHongbaoUnit = (TextView) this.fragmentView.findViewById(R.id.tvHongbaoUnit);
        this.tvRptTargetText = (TextView) this.fragmentView.findViewById(R.id.tvRptTargetText);
        this.etRptTargetEditView = (EditText) this.fragmentView.findViewById(R.id.etRptTargetEditView);
        this.tvRptTargetHint = (TextView) this.fragmentView.findViewById(R.id.tvRptTargetHint);
        this.tvRptTargetUnit = (TextView) this.fragmentView.findViewById(R.id.tvRptTargetUnit);
        this.tvRptPersonName = (TextView) this.fragmentView.findViewById(R.id.tvRptPersonName);
        this.tvRptTypeDesc = (TextView) this.fragmentView.findViewById(R.id.tvRptTypeDesc);
        this.etRptGreetEditView = (EditText) this.fragmentView.findViewById(R.id.etRptGreetEditView);
        this.tvRptGreetHint = (TextView) this.fragmentView.findViewById(R.id.tvRptGreetHint);
        this.btnRptSend = (Button) this.fragmentView.findViewById(R.id.btnRptSend);
        this.tvMoneyUnit = (TextView) this.fragmentView.findViewById(R.id.tvMoneyUnit);
        this.tvRpkPromt.setVisibility(8);
        this.segRptType.setOnSelectionChangedListener(new SegmenteView.OnSelectionChangedListener() {
            public final void onItemSelected(int i) {
                RedpktGroupSendActivity.this.lambda$initView$1$RedpktGroupSendActivity(i);
            }
        });
        this.tvHongbaoUnit.setText(LocaleController.getString(R.string.UnitCNY));
        this.tvMoneyUnit.setText(LocaleController.getString(R.string.UnitCNY));
        try {
            this.segRptType.setItems(new String[]{LocaleController.getString("redpacket_group_random", R.string.redpacket_group_random), LocaleController.getString("redpacket_group_common", R.string.redpacket_group_common), LocaleController.getString("redpacket_group_exclusive", R.string.redpacket_group_exclusive)}, new String[]{"0", "1", "2"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.tvRptGreetHint.setText(LocaleController.getString("redpacket_greetings_tip", R.string.redpacket_greetings_tip));
        this.etRptAmountEditView.setFilters(new InputFilter[]{new EditInputFilter()});
        this.etRptAmountEditView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RedpktGroupSendActivity.this.tvRptAmountHint.setText(s.length() > 0 ? "" : LocaleController.getString("redpacket_amount", R.string.redpacket_amount));
                RedpktGroupSendActivity.this.updateTotal();
                if (RedpktGroupSendActivity.this.updateAmountPromt()) {
                    RedpktGroupSendActivity.this.btnRptSend.setEnabled(true);
                } else {
                    RedpktGroupSendActivity.this.btnRptSend.setEnabled(false);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.etRptTargetEditView.setFilters(new InputFilter[]{new InputNumberFilter(-1)});
        this.etRptTargetEditView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str;
                TextView access$500 = RedpktGroupSendActivity.this.tvRptTargetHint;
                if (s.length() > 0) {
                    str = "";
                } else {
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf(RedpktGroupSendActivity.this.chatFull != null ? RedpktGroupSendActivity.this.chatFull.participants_count : 0);
                    str = LocaleController.formatString("group_person_number", R.string.group_person_number, objArr);
                }
                access$500.setText(str);
                RedpktGroupSendActivity.this.updateTotal();
                if (RedpktGroupSendActivity.this.updateAmountPromt()) {
                    RedpktGroupSendActivity.this.btnRptSend.setEnabled(true);
                } else {
                    RedpktGroupSendActivity.this.btnRptSend.setEnabled(false);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.etRptGreetEditView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    RedpktGroupSendActivity.this.tvRptGreetHint.setText("");
                } else {
                    RedpktGroupSendActivity.this.tvRptGreetHint.setText(LocaleController.getString("redpacket_greetings_tip", R.string.redpacket_greetings_tip));
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.llPersonLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktGroupSendActivity.this.lambda$initView$3$RedpktGroupSendActivity(view);
            }
        });
        this.btnRptSend.setEnabled(false);
        this.btnRptSend.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktGroupSendActivity.this.lambda$initView$4$RedpktGroupSendActivity(view);
            }
        });
        this.tvRptTargetHint.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
        this.tvRptAmountHint.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
        this.tvRptPersonName.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
        this.tvRptGreetHint.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
        this.btnRptSend.setText(LocaleController.getString(R.string.redpacket_add_money));
        ((TextView) this.fragmentView.findViewById(R.id.tvTimeoutDesc)).setText(LocaleController.getString(R.string.RedPacketsWillReturnWhenNotReceivedIn24Hours));
    }

    public /* synthetic */ void lambda$initView$1$RedpktGroupSendActivity(int checkedId) {
        int parseInt = Integer.parseInt(this.segRptType.getChecked());
        if (parseInt == 0) {
            this.typeRpt = 1001;
        } else if (parseInt == 1) {
            this.typeRpt = 1000;
        } else if (parseInt == 2) {
            this.typeRpt = 1002;
        }
        updateView();
    }

    public /* synthetic */ void lambda$initView$3$RedpktGroupSendActivity(View v) {
        SelecteContactsActivity selectedContactActivity = new SelecteContactsActivity((Bundle) null);
        selectedContactActivity.setChatInfo(this.chatFull);
        selectedContactActivity.setDelegate(new SelecteContactsActivity.ContactsActivityDelegate() {
            public final void didSelectContact(TLRPC.User user) {
                RedpktGroupSendActivity.this.lambda$null$2$RedpktGroupSendActivity(user);
            }
        });
        presentFragment(selectedContactActivity);
    }

    public /* synthetic */ void lambda$null$2$RedpktGroupSendActivity(TLRPC.User user) {
        if (user != null) {
            this.selectedUser = user;
            if (updateAmountPromt()) {
                this.btnRptSend.setEnabled(true);
            } else {
                this.btnRptSend.setEnabled(false);
            }
            this.llSelectedLayout.setVisibility(0);
            this.tvRptPersonName.setVisibility(8);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
            avatarDrawable.setInfo(user);
            this.bivUserSelected.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.bivUserSelected.getImageReceiver().setCurrentAccount(this.currentAccount);
            this.bivUserSelected.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
            this.tvUserName.setText(user.first_name);
        }
    }

    public /* synthetic */ void lambda$initView$4$RedpktGroupSendActivity(View v) {
        createPayAlert(this.accountInfo.getCashAmount() / 100.0d);
    }

    /* access modifiers changed from: private */
    public void updateTotal() {
        int i = this.typeRpt;
        if (i == 1000) {
            if (TextUtils.isEmpty(this.etRptAmountEditView.getText())) {
                this.tvRptMountText.setText("0.00");
            } else if (TextUtils.isEmpty(this.etRptTargetEditView.getText())) {
                this.tvRptMountText.setText("0.00");
            } else {
                String strSingal = this.etRptAmountEditView.getText().toString().trim();
                String strCount = this.etRptTargetEditView.getText().toString().trim();
                this.tvRptMountText.setText(DataTools.format2Decimals(new BigDecimal(strSingal).multiply(new BigDecimal(strCount)).toString()));
            }
        } else if (i == 1001) {
            if (TextUtils.isEmpty(this.etRptAmountEditView.getText())) {
                this.tvRptMountText.setText("0.00");
            } else {
                this.tvRptMountText.setText(DataTools.format2Decimals(this.etRptAmountEditView.getText().toString().trim()));
            }
        } else if (i != 1002) {
        } else {
            if (TextUtils.isEmpty(this.etRptAmountEditView.getText())) {
                this.tvRptMountText.setText("0.00");
            } else {
                this.tvRptMountText.setText(DataTools.format2Decimals(this.etRptAmountEditView.getText().toString().trim()));
            }
        }
    }

    private void createPayAlert(double balance) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        View sheet = LayoutInflater.from(getParentActivity()).inflate(R.layout.layout_hongbao_pay_pwd, (ViewGroup) null, false);
        builder.setCustomView(sheet);
        ((ImageView) sheet.findViewById(R.id.ivAlertClose)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktGroupSendActivity.this.lambda$createPayAlert$5$RedpktGroupSendActivity(view);
            }
        });
        TextView tvTitle = (TextView) sheet.findViewById(R.id.tvTitle);
        if (this.typeRpt == 1002) {
            tvTitle.setText(LocaleController.getString(R.string.redpacket_group_exclusive));
        } else {
            tvTitle.setText(String.format("%s" + LocaleController.getString(R.string.redpacket_each) + LocaleController.getString(R.string.RedPacket) + LocaleController.getString(R.string.Comma) + LocaleController.getString(R.string.HotCoinTotal), new Object[]{this.etRptTargetEditView.getText().toString().trim()}));
        }
        TextView tvShowMoneyView = (TextView) sheet.findViewById(R.id.tvShowMoneyView);
        tvShowMoneyView.setTextColor(-109240);
        tvShowMoneyView.setText(DataTools.format2Decimals(this.tvRptMountText.getText().toString().trim()));
        TextView tvPayMode = (TextView) sheet.findViewById(R.id.tvPayMode);
        tvPayMode.setTextColor(-6710887);
        tvPayMode.setText(LocaleController.getString(R.string.HotCoinPay));
        SpanUtils spanTvBalance = SpanUtils.with((TextView) sheet.findViewById(R.id.tvBlance));
        spanTvBalance.setVerticalAlign(2).append(LocaleController.getString(R.string.friendscircle_publish_remain)).setForegroundColor(-6710887).append(" (").setForegroundColor(-6710887);
        spanTvBalance.append(String.format("%.2f", new Object[]{Double.valueOf(balance)}));
        ((TextView) sheet.findViewById(R.id.tvMoneyUnit)).setText(LocaleController.getString(R.string.UnitCNY));
        spanTvBalance.setForegroundColor(-16777216).append(SQLBuilder.PARENTHESES_RIGHT).setForegroundColor(-6710887).create();
        TextView textView = (TextView) sheet.findViewById(R.id.tvForgotPassword);
        this.tvForgotPassword = textView;
        textView.setText(LocaleController.getString(R.string.PasswordRecovery));
        this.tvForgotPassword.setOnClickListener($$Lambda$RedpktGroupSendActivity$3i49tc6mW3V0mukzg2bL23Xevns.INSTANCE);
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
        gvKeyboard.setAdapter(new MyAdapter());
        gvKeyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                RedpktGroupSendActivity.this.lambda$createPayAlert$7$RedpktGroupSendActivity(adapterView, view, i, j);
            }
        });
        showDialog(builder.create()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                RedpktGroupSendActivity.this.lambda$createPayAlert$8$RedpktGroupSendActivity(dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$createPayAlert$5$RedpktGroupSendActivity(View v) {
        dismissCurrentDialog();
    }

    static /* synthetic */ void lambda$createPayAlert$6(View v) {
    }

    public /* synthetic */ void lambda$createPayAlert$7$RedpktGroupSendActivity(AdapterView parent, View view, int position, long id) {
        int i = position;
        int i2 = 0;
        if (i < 9 || i == 10) {
            int i3 = this.notEmptyTvCount;
            TextView[] textViewArr = this.mTvPasswords;
            if (i3 != textViewArr.length) {
                int length = textViewArr.length;
                int i4 = 0;
                while (true) {
                    if (i4 >= length) {
                        break;
                    }
                    TextView textView = textViewArr[i4];
                    if (TextUtils.isEmpty(textView.getText())) {
                        textView.setText(String.valueOf(this.mNumbers.get(position)));
                        this.notEmptyTvCount++;
                        break;
                    }
                    i4++;
                }
                if (this.notEmptyTvCount == this.mTvPasswords.length) {
                    StringBuilder password = new StringBuilder();
                    TextView[] textViewArr2 = this.mTvPasswords;
                    int length2 = textViewArr2.length;
                    while (i2 < length2) {
                        String text = textViewArr2[i2].getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            password.append(text);
                        }
                        i2++;
                    }
                    sendRedpacket(String.valueOf(new BigDecimal(this.tvRptMountText.getText().toString().trim()).multiply(new BigDecimal("100")).intValue()), AesUtils.encrypt(password.toString().trim()), this.toChat.id, this.toChat.title, this.typeRpt == 1002 ? 1 : Integer.parseInt(this.etRptTargetEditView.getText().toString().trim()));
                }
            }
        } else if (i == 11) {
            if (this.notEmptyTvCount == this.mTvPasswords.length) {
                this.tvForgotPassword.setText(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
                this.tvForgotPassword.setTextColor(-14250753);
                this.tvForgotPassword.setEnabled(true);
                this.llPayPassword.setBackgroundResource(R.drawable.shape_pay_password_bg);
                this.llPayPassword.setDividerDrawable(getParentActivity().getResources().getDrawable(R.drawable.shape_pay_password_divider));
                TextView[] textViewArr3 = this.mTvPasswords;
                int length3 = textViewArr3.length;
                while (i2 < length3) {
                    textViewArr3[i2].setTextColor(-16777216);
                    i2++;
                }
            }
            for (int i5 = this.mTvPasswords.length - 1; i5 >= 0; i5--) {
                if (!TextUtils.isEmpty(this.mTvPasswords[i5].getText())) {
                    this.mTvPasswords[i5].setText((CharSequence) null);
                    this.notEmptyTvCount--;
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$createPayAlert$8$RedpktGroupSendActivity(DialogInterface dialog1) {
        this.notEmptyTvCount = 0;
    }

    private void finish() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                RedpktGroupSendActivity.this.finishFragment();
            }
        });
    }

    private void getAccountInfo() {
        this.progressView = new XAlertDialog(getParentActivity(), 5);
        this.reqId = getConnectionsManager().sendRequest(new TLRPCWallet.TL_getPaymentAccountInfo(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                RedpktGroupSendActivity.this.lambda$getAccountInfo$24$RedpktGroupSendActivity(tLObject, tL_error);
            }
        });
        this.progressView.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                RedpktGroupSendActivity.this.lambda$getAccountInfo$25$RedpktGroupSendActivity(dialogInterface);
            }
        });
        try {
            this.progressView.show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$getAccountInfo$24$RedpktGroupSendActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            this.progressView.dismiss();
            WalletDialogUtil.showConfirmBtnWalletDialog((Object) this, (CharSequence) WalletErrorUtil.getErrorDescription(LocaleController.formatString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater, new Object[0]), error.text), true, (DialogInterface.OnDismissListener) new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    RedpktGroupSendActivity.this.lambda$null$9$RedpktGroupSendActivity(dialogInterface);
                }
            });
            return;
        }
        this.progressView.dismiss();
        if (response instanceof TLRPCWallet.TL_paymentAccountInfoNotExist) {
            WalletDialogUtil.showWalletDialog(this, "", LocaleController.formatString("AccountInfoNotCompleted", R.string.AccountInfoNotCompleted, LocaleController.getString(R.string.SendRedPackets), LocaleController.getString(R.string.SendRedPackets)), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToWalletCenter", R.string.GoToWalletCenter), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    RedpktGroupSendActivity.this.lambda$null$10$RedpktGroupSendActivity(dialogInterface, i);
                }
            }, $$Lambda$RedpktGroupSendActivity$Mb9OP2ZRYjbeVnWJnkJcu22esrA.INSTANCE, new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    RedpktGroupSendActivity.this.lambda$null$13$RedpktGroupSendActivity(dialogInterface);
                }
            });
            return;
        }
        TLApiModel<WalletAccountInfo> model = TLJsonResolve.parse(response, (Class<?>) WalletAccountInfo.class);
        if (model.isSuccess()) {
            WalletAccountInfo walletAccountInfo = (WalletAccountInfo) model.model;
            this.accountInfo = walletAccountInfo;
            WalletConfigBean.setWalletAccountInfo(walletAccountInfo);
            WalletConfigBean.setConfigValue(((WalletAccountInfo) model.model).getRiskList());
            if (!this.accountInfo.hasNormalAuth()) {
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BasicAuthNor", R.string.BasicAuthNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToAuth", R.string.GoToAuth), (DialogInterface.OnClickListener) null, $$Lambda$RedpktGroupSendActivity$GLFBmCr4fB797oYkMdSSMnaW40.INSTANCE, (DialogInterface.OnDismissListener) null);
            } else if (!this.accountInfo.hasBindBank()) {
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BankBindNor", R.string.BankBindNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToBind", R.string.GoToBind), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktGroupSendActivity.this.lambda$null$15$RedpktGroupSendActivity(dialogInterface, i);
                    }
                }, $$Lambda$RedpktGroupSendActivity$UgPVcExX2AzAtazSs9tMEd2fs.INSTANCE, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        RedpktGroupSendActivity.this.lambda$null$18$RedpktGroupSendActivity(dialogInterface);
                    }
                });
            } else if (!this.accountInfo.hasPaypassword()) {
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("PayPasswordNor", R.string.PayPasswordNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("redpacket_goto_set", R.string.redpacket_goto_set), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktGroupSendActivity.this.lambda$null$19$RedpktGroupSendActivity(dialogInterface, i);
                    }
                }, $$Lambda$RedpktGroupSendActivity$mOXjG6fj2qRkidyAQo3sTPguSs.INSTANCE, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        RedpktGroupSendActivity.this.lambda$null$22$RedpktGroupSendActivity(dialogInterface);
                    }
                });
            }
        } else {
            WalletDialogUtil.showConfirmBtnWalletDialog((Object) this, (CharSequence) WalletErrorUtil.getErrorDescription(LocaleController.formatString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater, new Object[0]), model.message), true, (DialogInterface.OnDismissListener) new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    RedpktGroupSendActivity.this.lambda$null$23$RedpktGroupSendActivity(dialogInterface);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$9$RedpktGroupSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$10$RedpktGroupSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$11() {
    }

    public /* synthetic */ void lambda$null$13$RedpktGroupSendActivity(DialogInterface dialog) {
        finish();
    }

    static /* synthetic */ void lambda$null$14(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$15$RedpktGroupSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$16() {
    }

    public /* synthetic */ void lambda$null$18$RedpktGroupSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$19$RedpktGroupSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$20() {
    }

    public /* synthetic */ void lambda$null$22$RedpktGroupSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$23$RedpktGroupSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$getAccountInfo$25$RedpktGroupSendActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequest(this.reqId, true);
        finish();
    }

    private void sendRedpacket(String total, String pwd, int chatId, String chatName, int count) {
        TLRPC.User user;
        TLRPCRedpacket.CL_messages_sendRptTransfer req = new TLRPCRedpacket.CL_messages_sendRptTransfer();
        req.flags = 3;
        req.trans = 0;
        req.type = 0;
        TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerChannel();
        inputPeer.channel_id = this.toChat.id;
        inputPeer.access_hash = this.toChat.access_hash;
        req.peer = inputPeer;
        RepacketRequest bean = new RepacketRequest();
        bean.setOutTradeNo("android_" + getUserConfig().clientUserId + StringUtils.getRandomString(16) + getConnectionsManager().getCurrentTime());
        bean.setNonceStr(StringUtils.getRandomString(32));
        bean.setBody(String.format(LocaleController.formatString("redpacket_send_in_group", R.string.redpacket_send_in_group, chatName), new Object[0]));
        String str = "";
        bean.setDetail(str);
        bean.setAttach(str);
        bean.setTotalFee(total);
        bean.setTradeType("1");
        if (!TextUtils.isEmpty(this.etRptGreetEditView.getText())) {
            str = this.etRptGreetEditView.getText().toString().trim();
        }
        bean.setRemarks(str);
        bean.setInitiator(String.valueOf(getUserConfig().getClientUserId()));
        bean.setPayPassWord(pwd);
        bean.setBusinessKey(UnifyBean.BUSINESS_KEY_REDPACKET);
        bean.setRecipient(String.valueOf(chatId));
        bean.setVersion("1");
        int i = this.typeRpt;
        if (i == 1000) {
            bean.setRedType("1");
            bean.setGrantType("0");
        } else if (i == 1001) {
            bean.setRedType("1");
            bean.setGrantType("1");
        } else if (i == 1002) {
            bean.setRedType("2");
            bean.setGrantType("0");
        }
        bean.setGroups(String.valueOf(chatId));
        bean.setGroupsName(chatName);
        if (this.typeRpt == 1000) {
            bean.setFixedAmount(new BigDecimal(this.etRptAmountEditView.getText().toString().trim()).multiply(new BigDecimal("100")).toString());
        } else {
            bean.setFixedAmount(total);
        }
        if (this.typeRpt == 1002 && (user = this.selectedUser) != null) {
            bean.setRecipient(String.valueOf(user.id));
        }
        bean.setNumber(Integer.valueOf(count));
        TLRPC.TL_dataJSON data = new TLRPC.TL_dataJSON();
        data.data = new Gson().toJson((Object) bean);
        req.redPkg = data;
        req.random_id = getSendMessagesHelper().getNextRandomId();
        this.progressView = new XAlertDialog(getParentActivity(), 5);
        this.reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                RedpktGroupSendActivity.this.lambda$sendRedpacket$35$RedpktGroupSendActivity(tLObject, tL_error);
            }
        });
        this.progressView.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                RedpktGroupSendActivity.this.lambda$sendRedpacket$36$RedpktGroupSendActivity(dialogInterface);
            }
        });
        try {
            this.progressView.show();
            this.progressView.setLoadingText(LocaleController.getString(R.string.Sending));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$sendRedpacket$35$RedpktGroupSendActivity(TLObject response, TLRPC.TL_error error) {
        this.progressView.dismiss();
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RedpktGroupSendActivity.this.lambda$null$34$RedpktGroupSendActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$34$RedpktGroupSendActivity(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            if (error.text == null) {
                error.text = "";
            }
            error.text = error.text.replace("\n", "");
            if (error.text.contains("ACCOUNT_PASSWORD_IN_MINUTES,ERROR_TIMES,WILL_BE_FROZEN")) {
                String[] split = error.text.split("_");
                String str = split[split.length - 2];
                AndroidUtilities.runOnUIThread(new Runnable(split[split.length - 1]) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        RedpktGroupSendActivity.this.lambda$null$26$RedpktGroupSendActivity(this.f$1);
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        RedpktGroupSendActivity.this.lambda$null$27$RedpktGroupSendActivity();
                    }
                }, 2500);
            } else if ("NOT_SUFFICIENT_FUNDS".equals(error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        RedpktGroupSendActivity.this.lambda$null$31$RedpktGroupSendActivity();
                    }
                });
            } else if ("ACCOUNT_HAS_BEEN_FROZEN_CODE".equals(error.text) || "PAY_PASSWORD_MAX_ACCOUNT_FROZEN".equals(error.text)) {
                dismissCurrentDialog();
                WalletDialogUtil.showWalletDialog(this, "", WalletErrorUtil.getErrorDescription(error.text), LocaleController.getString("Confirm", R.string.Confirm), LocaleController.getString("ContactCustomerService", R.string.ContactCustomerService), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktGroupSendActivity.this.lambda$null$32$RedpktGroupSendActivity(dialogInterface, i);
                    }
                }, (DialogInterface.OnDismissListener) null);
            } else if (error.text.contains("EXCEED_RED_PACKET_ONCE_MAX_MONEY")) {
                dismissCurrentDialog();
                WalletDialogUtil.showSingleBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(error.text), LocaleController.getString("Confirm", R.string.Confirm), false, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktGroupSendActivity.this.lambda$null$33$RedpktGroupSendActivity(dialogInterface, i);
                    }
                }, (DialogInterface.OnDismissListener) null);
            } else {
                dismissCurrentDialog();
                WalletErrorUtil.parseErrorDialog((Object) this, error.text);
            }
        } else if (response instanceof TLRPC.Updates) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$26$RedpktGroupSendActivity(String time) {
        this.tvForgotPassword.setText(LocaleController.formatString("PassswordErrorText", R.string.PassswordErrorText, time));
        this.tvForgotPassword.setTextColor(-109240);
        this.tvForgotPassword.setEnabled(false);
        this.llPayPassword.setBackgroundResource(R.drawable.shape_pay_password_error_bg);
        this.llPayPassword.setDividerDrawable(getParentActivity().getResources().getDrawable(R.drawable.shape_pay_password_error_divider));
        for (TextView mTvPassword : this.mTvPasswords) {
            mTvPassword.setTextColor(-109240);
        }
    }

    public /* synthetic */ void lambda$null$27$RedpktGroupSendActivity() {
        this.tvForgotPassword.setText(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
        this.tvForgotPassword.setTextColor(-14250753);
        this.tvForgotPassword.setEnabled(true);
        this.llPayPassword.setBackgroundResource(R.drawable.shape_pay_password_bg);
        this.llPayPassword.setDividerDrawable(getParentActivity().getResources().getDrawable(R.drawable.shape_pay_password_divider));
        for (TextView mTvPassword : this.mTvPasswords) {
            mTvPassword.setText((CharSequence) null);
            mTvPassword.setTextColor(-16777216);
            this.notEmptyTvCount = 0;
        }
    }

    public /* synthetic */ void lambda$null$31$RedpktGroupSendActivity() {
        dismissCurrentDialog();
        WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BalanceIsNotEnough", R.string.BalanceIsNotEnough), LocaleController.getString(R.string.ChangeHotCoinCount), LocaleController.getString(R.string.ChargeAmount), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                RedpktGroupSendActivity.this.lambda$null$29$RedpktGroupSendActivity(dialogInterface, i);
            }
        }, $$Lambda$RedpktGroupSendActivity$5Yd7yd5SRGmkQw7R0vaPnIzZVAY.INSTANCE, (DialogInterface.OnDismissListener) null).getNegativeButton().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
    }

    public /* synthetic */ void lambda$null$29$RedpktGroupSendActivity(DialogInterface dialogInterface, int i) {
        this.etRptAmountEditView.requestFocus();
        EditText editText = this.etRptAmountEditView;
        editText.setSelection(editText.getText().length());
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                RedpktGroupSendActivity.this.lambda$null$28$RedpktGroupSendActivity();
            }
        }, 300);
    }

    public /* synthetic */ void lambda$null$28$RedpktGroupSendActivity() {
        AndroidUtilities.showKeyboard(this.etRptAmountEditView);
    }

    static /* synthetic */ void lambda$null$30(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$32$RedpktGroupSendActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new AboutAppActivity());
    }

    public /* synthetic */ void lambda$null$33$RedpktGroupSendActivity(DialogInterface dialogInterface, int i) {
        this.etRptAmountEditView.setText("");
    }

    public /* synthetic */ void lambda$sendRedpacket$36$RedpktGroupSendActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequest(this.reqId, true);
    }

    public void performService(BaseFragment fragment) {
        String userString;
        int currentAccount = fragment.getCurrentAccount();
        SharedPreferences preferences = MessagesController.getMainSettings(currentAccount);
        int uid = preferences.getInt("support_id", 0);
        TLRPC.User supportUser = null;
        if (!(uid == 0 || (supportUser = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(uid))) != null || (userString = preferences.getString("support_user", (String) null)) == null)) {
            try {
                byte[] datacentersBytes = Base64.decode(userString, 0);
                if (datacentersBytes != null) {
                    SerializedData data = new SerializedData(datacentersBytes);
                    supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                    if (supportUser != null && supportUser.id == 333000) {
                        supportUser = null;
                    }
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                supportUser = null;
            }
        }
        if (supportUser == null) {
            XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
            progressDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new RequestDelegate(preferences, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ BaseFragment f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    RedpktGroupSendActivity.lambda$performService$39(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performService$39(SharedPreferences preferences, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(preferences, (TLRPC.TL_help_support) response, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ TLRPC.TL_help_support f$1;
                private final /* synthetic */ XAlertDialog f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ BaseFragment f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    RedpktGroupSendActivity.lambda$null$37(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    RedpktGroupSendActivity.lambda$null$38(XAlertDialog.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$37(SharedPreferences preferences, TLRPC.TL_help_support res, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("support_id", res.user.id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.commit();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putInt("user_id", res.user.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$null$38(XAlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setParticipants(TLRPC.ChatFull chatFull2) {
        this.chatFull = chatFull2;
    }

    private class MyAdapter extends BaseAdapter {
        private MyAdapter() {
        }

        public int getCount() {
            return RedpktGroupSendActivity.this.mNumbers.size();
        }

        public Integer getItem(int position) {
            return (Integer) RedpktGroupSendActivity.this.mNumbers.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(RedpktGroupSendActivity.this.mContext, R.layout.item_password_number, (ViewGroup) null);
                holder = new ViewHolder();
                holder.tvNumber = (TextView) convertView.findViewById(R.id.btn_number);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < 9 || position == 10) {
                holder.tvNumber.setText(String.valueOf(RedpktGroupSendActivity.this.mNumbers.get(position)));
                holder.tvNumber.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if (position == 9) {
                holder.tvNumber.setVisibility(4);
            } else if (position == 11) {
                holder.tvNumber.setVisibility(4);
                holder.ivDelete.setVisibility(0);
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivDelete;
        TextView tvNumber;

        ViewHolder() {
        }
    }
}
