package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.contacts.GreetEditActivity;
import im.bclpbkiauv.ui.hui.contacts.NoteAndGroupingEditActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcSettingActivity;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import org.json.JSONException;
import org.json.JSONObject;

public class AddContactsInfoActivity extends BaseFragment implements View.OnClickListener, NotificationCenter.NotificationCenterDelegate {
    public static final int FROM_APPLY = 7;
    public static final int FROM_APP_CODE_SEARCH = 4;
    public static final int FROM_BOOK = 6;
    public static final int FROM_GROUP = 2;
    public static final int FROM_NEARBY = 5;
    public static final int FROM_PHONE_SEARCH = 3;
    public static final int FROM_QR_CODE = 1;
    private int applyId;
    @BindView(2131296377)
    BackupImageView avatarImage;
    private int expire;
    @BindView(2131296626)
    FrameLayout flReplyLayout;
    private int fromType;
    private String greet;
    @BindView(2131296743)
    ImageView ivGender;
    @BindView(2131297103)
    RelativeLayout llBioSettingView;
    @BindView(2131296912)
    LinearLayout llInfoLayout;
    @BindView(2131296914)
    LinearLayout llOriginalView;
    private Context mContext;
    @BindView(2131296998)
    MryTextView mryNameView;
    @BindView(2131297083)
    RecyclerListView rcvReplyList;
    private int reqState = 0;
    @BindView(2131297438)
    TextView tvAddContactStatus;
    @BindView(2131297457)
    TextView tvBioDesc;
    @BindView(2131297458)
    TextView tvBioText;
    @BindView(2131297553)
    TextView tvNoteSettingView;
    @BindView(2131297558)
    TextView tvOriginalDesc;
    @BindView(2131297559)
    TextView tvOriginalText;
    @BindView(2131297583)
    TextView tvReplyButton;
    @BindView(2131297584)
    TextView tvReplyText;
    @BindView(2131297867)
    MryTextView tvUpdateTime;
    /* access modifiers changed from: private */
    public TLRPC.User user;
    /* access modifiers changed from: private */
    public TLRPCContacts.CL_userFull_v1 userFull;
    private int userGroupId = -1;
    private String userGroupName;
    private String userNote = "";

    public AddContactsInfoActivity(Bundle args, TLRPC.User user2) {
        super(args);
        this.user = user2;
    }

    public boolean onFragmentCreate() {
        if (this.arguments != null) {
            this.fromType = this.arguments.getInt("from_type", this.fromType);
            this.reqState = this.arguments.getInt("req_state", 0);
            this.applyId = this.arguments.getInt("apply_id", 0);
            this.expire = this.arguments.getInt("expire", 0);
            this.greet = this.arguments.getString("greet", "");
        }
        if (this.user == null) {
            return false;
        }
        TLRPC.UserFull full = MessagesController.getInstance(this.currentAccount).getUserFull(this.user.id);
        if (full instanceof TLRPCContacts.CL_userFull_v1) {
            this.userFull = (TLRPCContacts.CL_userFull_v1) full;
        }
        getMessagesController().loadFullUser(this.user, this.classGuid, true);
        getNotificationCenter().addObserver(this, NotificationCenter.userFullInfoDidLoad);
        return true;
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_add_contact_info_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionBar();
        initViews();
        setViewData();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("PersonalInfo", R.string.PersonalInfo));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AddContactsInfoActivity.this.finishFragment();
                } else if (id != 1) {
                } else {
                    if (AddContactsInfoActivity.this.userFull == null || AddContactsInfoActivity.this.userFull.getExtendBean() == null) {
                        AddContactsInfoActivity addContactsInfoActivity = AddContactsInfoActivity.this;
                        addContactsInfoActivity.presentFragment(new FcSettingActivity((long) addContactsInfoActivity.user.id, 0));
                        return;
                    }
                    AddContactsInfoActivity addContactsInfoActivity2 = AddContactsInfoActivity.this;
                    addContactsInfoActivity2.presentFragment(new FcSettingActivity((long) addContactsInfoActivity2.user.id, AddContactsInfoActivity.this.userFull.getExtendBean().sex));
                }
            }
        });
    }

    private void initViews() {
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.llInfoLayout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.flReplyLayout.getBackground().setColorFilter(Theme.getColor(Theme.key_windowBackgroundGray), PorterDuff.Mode.SRC_IN);
        this.tvReplyText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.tvReplyButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.tvNoteSettingView.setBackground(Theme.getSelectorDrawable(true));
        this.tvNoteSettingView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.llBioSettingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.tvBioDesc.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvBioDesc.setText(LocaleController.getString("UserBio", R.string.UserBio));
        this.tvBioText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.llOriginalView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvOriginalDesc.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvOriginalDesc.setText(LocaleController.getString("OriginalText", R.string.OriginalText));
        this.tvOriginalText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.tvAddContactStatus.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvAddContactStatus.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        int i = this.fromType;
        if (i == 1) {
            this.tvOriginalText.setText(LocaleController.getString("SharedbyQRCode", R.string.SharedbyQRCode));
        } else if (i == 2) {
            this.tvOriginalText.setText(LocaleController.getString("ByGroup", R.string.ByGroup));
        } else if (i == 3) {
            this.tvOriginalText.setText(LocaleController.getString("SearchedByPhone", R.string.SearchedByPhone));
        } else if (i == 4) {
            this.tvOriginalText.setText(LocaleController.getString("SearchedByUsername", R.string.SearchedByUsername));
        } else if (i == 5) {
            this.tvOriginalText.setText(LocaleController.getString("ByNearby", R.string.ByNearby));
        } else if (i == 6) {
            this.tvOriginalText.setText(LocaleController.getString("ByPhoneBook", R.string.ByPhoneBook));
        }
    }

    @OnClick({2131297583, 2131297553, 2131297438})
    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.tvAddContactStatus) {
            if (id != R.id.tvNoteSettingView) {
                if (id == R.id.tvReplyButton) {
                    jumpToReplyGreetActivity();
                }
            } else if (this.fromType != 7 || (this.reqState == 0 && getConnectionsManager().getCurrentTime() <= this.expire)) {
                jumpToEditUserNoteActivity();
            }
        } else if (this.fromType != 7) {
            jumpToEditGreetActivity();
        } else if (this.reqState == 0 && getConnectionsManager().getCurrentTime() <= this.expire) {
            acceptApplyRequest(this.applyId, this.userGroupId, this.userNote);
        }
    }

    private void setViewData() {
        if (this.fromType == 7) {
            int i = this.reqState;
            if (i == 0) {
                if (getConnectionsManager().getCurrentTime() <= this.expire) {
                    this.tvAddContactStatus.setText(LocaleController.getString("AssentRequest", R.string.AssentRequest));
                } else {
                    this.tvAddContactStatus.setText(LocaleController.getString("RequestExpired", R.string.RequestExpired));
                }
            } else if (i == 1 || this.user.contact) {
                this.tvAddContactStatus.setText(LocaleController.getString("AddedContacts", R.string.AddedContacts));
            } else if (this.reqState == 2) {
                this.tvAddContactStatus.setText(LocaleController.getString("RequestExpired", R.string.RequestExpired));
            }
            this.tvAddContactStatus.setTextColor((this.reqState != 0 || getConnectionsManager().getCurrentTime() > this.expire) ? -4737097 : -14250753);
        } else {
            this.tvAddContactStatus.setText(LocaleController.getString("AddFriends", R.string.AddFriends));
        }
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userFull;
        int i2 = 8;
        if (cL_userFull_v1 != null) {
            if (cL_userFull_v1.user != null) {
                this.user = this.userFull.user;
                getMessagesController().putUser(this.user, false);
            }
            this.tvBioText.setText(TextUtils.isEmpty(this.userFull.about) ? LocaleController.getString("BioNothing", R.string.BioNothing) : this.userFull.about);
            boolean isEmpty = TextUtils.isEmpty(this.userFull.extend.data);
            int i3 = R.mipmap.ic_female;
            if (!isEmpty) {
                try {
                    JSONObject json = new JSONObject(this.userFull.extend.data);
                    this.ivGender.setImageResource(json.getInt("sex") == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
                    if (this.fromType == 7) {
                        int source = json.getInt("source");
                        if (source == 1) {
                            this.tvOriginalText.setText(LocaleController.getString("SharedbyQRCode", R.string.SharedbyQRCode));
                        } else if (source == 2) {
                            this.tvOriginalText.setText(LocaleController.getString("ByGroup", R.string.ByGroup));
                        } else if (source == 3) {
                            this.tvOriginalText.setText(LocaleController.getString("SearchedByPhone", R.string.SearchedByPhone));
                        } else if (source == 4) {
                            this.tvOriginalText.setText(LocaleController.getString("SearchedByUsername", R.string.SearchedByUsername));
                        } else if (source == 5) {
                            this.tvOriginalText.setText(LocaleController.getString("ByNearby", R.string.ByNearby));
                        } else if (source == 6) {
                            this.tvOriginalText.setText(LocaleController.getString("ByPhoneBook", R.string.ByPhoneBook));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (this.userFull.getExtendBean() != null) {
                int sex = this.userFull.getExtendBean().sex;
                ImageView imageView = this.ivGender;
                if (sex == 1) {
                    i3 = R.mipmap.ic_male;
                } else if (sex != 2) {
                    i3 = 0;
                }
                imageView.setImageResource(i3);
                if (sex == 1 || sex == 2) {
                    this.ivGender.setVisibility(0);
                } else {
                    this.ivGender.setVisibility(8);
                }
            }
        }
        if (this.user != null) {
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
            avatarDrawable.setInfo(this.user);
            this.mryNameView.setText(UserObject.getName(this.user));
            this.avatarImage.getImageReceiver().setCurrentAccount(this.currentAccount);
            this.avatarImage.setImage(ImageLocation.getForUser(this.user, false), "50_50", (Drawable) avatarDrawable, (Object) this.user);
            this.tvUpdateTime.setText(LocaleController.formatUserStatus(this.currentAccount, this.user));
            FrameLayout frameLayout = this.flReplyLayout;
            if (this.fromType == 7) {
                i2 = 0;
            }
            frameLayout.setVisibility(i2);
            TextView textView = this.tvReplyText;
            textView.setText(this.user.first_name + ": " + this.greet);
        }
    }

    private void acceptApplyRequest(int applyId2, int groupId, String firstName) {
        XDialog.Builder builder = new XDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("AcceptContactTip", R.string.AcceptContactTip));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(applyId2, groupId, firstName) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AddContactsInfoActivity.this.lambda$acceptApplyRequest$4$AddContactsInfoActivity(this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$acceptApplyRequest$4$AddContactsInfoActivity(int applyId2, int groupId, String firstName, DialogInterface dialog, int which) {
        XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
        progressDialog.setLoadingText(LocaleController.getString(R.string.ApplyAdding));
        TLRPCContacts.AcceptContactApply req = new TLRPCContacts.AcceptContactApply();
        req.apply_id = applyId2;
        req.group_id = Math.max(groupId, 0);
        req.first_name = firstName;
        req.last_name = "";
        ConnectionsManager connectionsManager = getConnectionsManager();
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog, applyId2) {
            private final /* synthetic */ XAlertDialog f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AddContactsInfoActivity.this.lambda$null$2$AddContactsInfoActivity(this.f$1, this.f$2, tLObject, tL_error);
            }
        });
        connectionsManager.bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                AddContactsInfoActivity.this.lambda$null$3$AddContactsInfoActivity(this.f$1, dialogInterface);
            }
        });
        progressDialog.show();
    }

    public /* synthetic */ void lambda$null$2$AddContactsInfoActivity(XAlertDialog progressDialog, int applyId2, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            progressDialog.dismiss();
            ToastUtils.show((CharSequence) ContactsUtils.getAboutContactsErrText(error));
            return;
        }
        getMessagesController().processUpdates((TLRPC.Updates) response, false);
        TLRPCContacts.ContactApplyInfo info = new TLRPCContacts.ContactApplyInfo();
        info.id = applyId2;
        info.state = 1;
        AndroidUtilities.runOnUIThread(new Runnable(applyId2, progressDialog) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ XAlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                AddContactsInfoActivity.this.lambda$null$1$AddContactsInfoActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$AddContactsInfoActivity(int applyId2, XAlertDialog progressDialog) {
        this.reqState = 1;
        setViewData();
        getNotificationCenter().postNotificationName(NotificationCenter.contactApplyUpdateState, Integer.valueOf(applyId2), Integer.valueOf(this.reqState));
        finishFragment();
        progressDialog.setLoadingImage(this.mContext.getResources().getDrawable(R.mipmap.ic_apply_send_done), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(20.0f));
        progressDialog.setLoadingText(LocaleController.getString(R.string.AddedContacts));
        this.fragmentView.postDelayed(new Runnable() {
            public final void run() {
                XAlertDialog.this.dismiss();
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    public /* synthetic */ void lambda$null$3$AddContactsInfoActivity(int reqId, DialogInterface hintDialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    private void jumpToEditGreetActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        GreetEditActivity greetEditActivity = new GreetEditActivity(bundle);
        greetEditActivity.setDelegate(new GreetEditActivity.GreetEditDelegate() {
            public final void onFinish(String str) {
                AddContactsInfoActivity.this.startContactApply(str);
            }
        });
        presentFragment(greetEditActivity);
    }

    private void jumpToEditUserNoteActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", this.user.id);
        bundle.putInt("groupId", this.userGroupId);
        bundle.putString("groupName", this.userGroupName);
        bundle.putString("userNote", this.userNote);
        bundle.putInt("type", 1);
        NoteAndGroupingEditActivity contactAddInfoEditActivity = new NoteAndGroupingEditActivity(bundle);
        contactAddInfoEditActivity.setDelegate(new NoteAndGroupingEditActivity.AddInfoDelegate() {
            public final void onFinish(int i, String str, String str2) {
                AddContactsInfoActivity.this.lambda$jumpToEditUserNoteActivity$5$AddContactsInfoActivity(i, str, str2);
            }
        });
        presentFragment(contactAddInfoEditActivity);
    }

    public /* synthetic */ void lambda$jumpToEditUserNoteActivity$5$AddContactsInfoActivity(int groupId, String groupName, String note) {
        this.userGroupId = groupId;
        this.userGroupName = groupName;
        this.userNote = note;
        if (!TextUtils.isEmpty(note)) {
            this.mryNameView.setText(this.userNote);
        } else {
            this.mryNameView.setText(UserObject.getName(this.user));
        }
    }

    private void jumpToReplyGreetActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        this.tvReplyButton.setOnClickListener(new View.OnClickListener(new GreetEditActivity(bundle)) {
            private final /* synthetic */ GreetEditActivity f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                AddContactsInfoActivity.this.lambda$jumpToReplyGreetActivity$6$AddContactsInfoActivity(this.f$1, view);
            }
        });
    }

    public /* synthetic */ void lambda$jumpToReplyGreetActivity$6$AddContactsInfoActivity(GreetEditActivity greetEditActivity, View v) {
        presentFragment(greetEditActivity);
    }

    /* access modifiers changed from: private */
    public void startContactApply(String greet2) {
        XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
        progressDialog.setLoadingText(LocaleController.getString(R.string.ApplySending));
        TLRPCContacts.ContactsRequestApply req = new TLRPCContacts.ContactsRequestApply();
        req.flag = 0;
        req.from_type = this.fromType;
        req.inputUser = getMessagesController().getInputUser(this.user);
        req.first_name = this.userNote;
        req.last_name = "";
        req.greet = greet2;
        req.group_id = Math.max(this.userGroupId, 0);
        ConnectionsManager connectionsManager = getConnectionsManager();
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ XAlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AddContactsInfoActivity.this.lambda$startContactApply$9$AddContactsInfoActivity(this.f$1, tLObject, tL_error);
            }
        });
        connectionsManager.bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                AddContactsInfoActivity.this.lambda$startContactApply$10$AddContactsInfoActivity(this.f$1, dialogInterface);
            }
        });
        progressDialog.show();
    }

    public /* synthetic */ void lambda$startContactApply$9$AddContactsInfoActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, progressDialog, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ XAlertDialog f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                AddContactsInfoActivity.this.lambda$null$8$AddContactsInfoActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$AddContactsInfoActivity(TLRPC.TL_error error, XAlertDialog progressDialog, TLObject response) {
        TLRPC.TL_updates updates;
        if (error == null) {
            if (!(!(response instanceof TLRPC.TL_updates) || (updates = (TLRPC.TL_updates) response) == null || updates.updates == null)) {
                getMessagesController().processUpdates(updates, false);
                for (int i = 0; i < updates.updates.size(); i++) {
                    if (updates.updates.get(i) instanceof TLRPCContacts.ContactApplyResp) {
                        getMessagesController().saveContactsAppliesId(((TLRPCContacts.ContactApplyResp) updates.updates.get(i)).applyInfo.id);
                    }
                }
            }
            progressDialog.setLoadingImage(this.mContext.getResources().getDrawable(R.mipmap.ic_apply_send_done), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(20.0f));
            progressDialog.setLoadingText(LocaleController.getString(R.string.ApplySent));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    XAlertDialog.this.dismiss();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        } else if (this.fromType != 5 || !TextUtils.isEmpty(error.text) || !"USER_ADDCONTACT_TOMANY_BYDAY".equals(error.text)) {
            progressDialog.dismiss();
            ToastUtils.show((CharSequence) ContactsUtils.getAboutContactsErrText(error));
        } else {
            WalletDialogUtil.showConfirmBtnWalletDialog(this, LocaleController.getString(R.string.ContactsAddLimitByDay));
        }
    }

    public /* synthetic */ void lambda$startContactApply$10$AddContactsInfoActivity(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.userFullInfoDidLoad) {
            int userId = args[0].intValue();
            TLRPC.User user2 = this.user;
            if (user2 != null && userId == user2.id && (args[1] instanceof TLRPCContacts.CL_userFull_v1)) {
                this.userFull = args[1];
                setViewData();
            }
        }
    }

    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        super.onFragmentDestroy();
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.llInfoLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.tvReplyText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.tvReplyButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_attachUnactiveTab), new ThemeDescription(this.tvNoteSettingView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.tvNoteSettingView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.llBioSettingView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.tvBioDesc, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.tvBioText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.llOriginalView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.tvOriginalDesc, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.tvOriginalText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.tvAddContactStatus, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.tvAddContactStatus, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText)};
    }
}
