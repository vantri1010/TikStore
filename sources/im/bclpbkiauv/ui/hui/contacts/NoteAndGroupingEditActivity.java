package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.KeyboardUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hui.contacts.SelectGroupingActivity;

public class NoteAndGroupingEditActivity extends BaseFragment {
    public static final int ADD_CONTACTS = 1;
    public static final int CONTACTS_PROFILE = 2;
    private final int DONE = 1;
    private int defaultGroupId;
    private String defaultGroupName;
    private AddInfoDelegate delegate;
    @BindView(2131296574)
    EditText etNoteEditView;
    @BindView(2131296624)
    FrameLayout flNoteSettingLayout;
    @BindView(2131296728)
    ImageView ivClearNoteView;
    private TLRPCContacts.TL_contactsGroupInfo selectedGroup;
    @BindView(2131297522)
    TextView tvGroupDescView;
    @BindView(2131297529)
    TextView tvGroupingSettingView;
    @BindView(2131297552)
    TextView tvNoteDescView;
    private int type;
    private TLRPC.User user;
    /* access modifiers changed from: private */
    public String userNote;
    private int user_id;

    public interface AddInfoDelegate {
        void onFinish(int i, String str, String str2);
    }

    public void setDelegate(AddInfoDelegate delegate2) {
        this.delegate = delegate2;
    }

    public NoteAndGroupingEditActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.swipeBackEnabled = false;
        if (this.arguments != null) {
            this.user_id = this.arguments.getInt("user_id");
            this.type = this.arguments.getInt("type", 0);
            this.defaultGroupId = this.arguments.getInt("groupId");
            this.defaultGroupName = this.arguments.getString("groupName", "");
            this.userNote = this.arguments.getString("userNote", "");
        }
        TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(this.user_id));
        this.user = user2;
        if (user2 == null) {
            return false;
        }
        return true;
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_note_and_grouping_edit_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setOnTouchListener($$Lambda$NoteAndGroupingEditActivity$7jtlarke9qw8HHgbqtSJXA6TsQ.INSTANCE);
        useButterKnife();
        initActionBar();
        initView();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("SetGroupingAndRemarks", R.string.SetGroupingAndRemarks));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!NoteAndGroupingEditActivity.this.etNoteEditView.getText().toString().equals(NoteAndGroupingEditActivity.this.userNote)) {
                        NoteAndGroupingEditActivity.this.showSaveDialog();
                    } else {
                        NoteAndGroupingEditActivity.this.finishFragment();
                    }
                } else if (id != 1) {
                } else {
                    if (!NoteAndGroupingEditActivity.this.etNoteEditView.getText().toString().equals(NoteAndGroupingEditActivity.this.userNote)) {
                        NoteAndGroupingEditActivity.this.setGroupingAndNote();
                    } else {
                        NoteAndGroupingEditActivity.this.finishFragment();
                    }
                }
            }
        });
        this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", R.string.Done));
    }

    private void initView() {
        this.tvGroupingSettingView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvGroupingSettingView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.flNoteSettingLayout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvNoteDescView.setText(LocaleController.getString("NoteSetting", R.string.NoteSetting));
        this.etNoteEditView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.etNoteEditView.setHint(LocaleController.getString("InputNoteText", R.string.InputNoteText));
        this.etNoteEditView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.etNoteEditView.setFilters(new InputFilter[]{getLengthFilter(32)});
        this.etNoteEditView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                NoteAndGroupingEditActivity.this.ivClearNoteView.setVisibility(!TextUtils.isEmpty(s) ? 0 : 8);
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.tvGroupingSettingView.setText(this.defaultGroupName);
        int i = this.type;
        if (i == 1) {
            this.etNoteEditView.setText(this.userNote);
        } else if (i == 2) {
            EditText editText = this.etNoteEditView;
            String str = this.user.first_name;
            this.userNote = str;
            editText.setText(str);
        }
    }

    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: private */
    public void showSaveDialog() {
        WalletDialog dialog = new WalletDialog(getParentActivity());
        dialog.setMessage(LocaleController.getString("SaveGroupingChangeTips", R.string.SaveGroupingChangeTips));
        dialog.setPositiveButton(LocaleController.getString("Save", R.string.Save), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                NoteAndGroupingEditActivity.this.lambda$showSaveDialog$1$NoteAndGroupingEditActivity(dialogInterface, i);
            }
        });
        dialog.setNegativeButton(LocaleController.getString("NotSave", R.string.NotSave), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                NoteAndGroupingEditActivity.this.lambda$showSaveDialog$2$NoteAndGroupingEditActivity(dialogInterface, i);
            }
        });
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$showSaveDialog$1$NoteAndGroupingEditActivity(DialogInterface dialogInterface, int i) {
        setGroupingAndNote();
    }

    public /* synthetic */ void lambda$showSaveDialog$2$NoteAndGroupingEditActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void setGroupingAndNote() {
        if (this.user != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            TLRPCContacts.TL_setUserGroup req = new TLRPCContacts.TL_setUserGroup();
            TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo = this.selectedGroup;
            req.group_id = tL_contactsGroupInfo != null ? tL_contactsGroupInfo.group_id : Math.max(this.defaultGroupId, 0);
            TLRPCContacts.TL_inputPeerUserChange inputPeer = new TLRPCContacts.TL_inputPeerUserChange();
            inputPeer.access_hash = this.user.access_hash;
            inputPeer.user_id = this.user.id;
            inputPeer.fist_name = this.etNoteEditView.getText().toString();
            req.users.add(inputPeer);
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NoteAndGroupingEditActivity.this.lambda$setGroupingAndNote$4$NoteAndGroupingEditActivity(this.f$1, tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    NoteAndGroupingEditActivity.this.lambda$setGroupingAndNote$5$NoteAndGroupingEditActivity(this.f$1, dialogInterface);
                }
            });
            showDialog(alertDialog);
        }
    }

    public /* synthetic */ void lambda$setGroupingAndNote$4$NoteAndGroupingEditActivity(AlertDialog alertDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, error, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                NoteAndGroupingEditActivity.this.lambda$null$3$NoteAndGroupingEditActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$NoteAndGroupingEditActivity(AlertDialog alertDialog, TLRPC.TL_error error, TLObject response) {
        alertDialog.dismiss();
        if (error != null) {
            ToastUtils.show((CharSequence) error.text);
        } else if (response instanceof TLRPC.TL_boolTrue) {
            AddInfoDelegate addInfoDelegate = this.delegate;
            if (addInfoDelegate != null) {
                TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo = this.selectedGroup;
                int max = tL_contactsGroupInfo != null ? tL_contactsGroupInfo.group_id : Math.max(this.defaultGroupId, 0);
                TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo2 = this.selectedGroup;
                addInfoDelegate.onFinish(max, tL_contactsGroupInfo2 != null ? tL_contactsGroupInfo2.title : "", this.etNoteEditView.getText().toString());
            }
            finishFragment();
        } else {
            ToastUtils.show((CharSequence) "设置失败，请稍后重试");
        }
    }

    public /* synthetic */ void lambda$setGroupingAndNote$5$NoteAndGroupingEditActivity(int reqId, DialogInterface dialog1) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    @OnClick({2131297529, 2131296728})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.ivClearNoteView) {
            this.etNoteEditView.setText("");
        } else if (id == R.id.tvGroupingSettingView) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", this.user_id);
            TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo = this.selectedGroup;
            bundle.putInt("groupId", tL_contactsGroupInfo != null ? tL_contactsGroupInfo.group_id : this.defaultGroupId);
            SelectGroupingActivity selectGroupingActivity = new SelectGroupingActivity(bundle);
            selectGroupingActivity.setDelegate(new SelectGroupingActivity.SelectGroupingActivityDelegate() {
                public final void onFinish(TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo) {
                    NoteAndGroupingEditActivity.this.lambda$onViewClicked$6$NoteAndGroupingEditActivity(tL_contactsGroupInfo);
                }
            });
            presentFragment(selectGroupingActivity);
        }
    }

    public /* synthetic */ void lambda$onViewClicked$6$NoteAndGroupingEditActivity(TLRPCContacts.TL_contactsGroupInfo group) {
        this.selectedGroup = group;
        if (group != null) {
            this.tvGroupingSettingView.setText(group.title);
            AddInfoDelegate addInfoDelegate = this.delegate;
            if (addInfoDelegate != null) {
                addInfoDelegate.onFinish(this.selectedGroup.group_id, this.selectedGroup.title, this.user.first_name);
            }
        }
    }

    public void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput((View) this.etNoteEditView);
    }

    private InputFilter getLengthFilter(int maxLen) {
        return new InputFilter(maxLen) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                return NoteAndGroupingEditActivity.lambda$getLengthFilter$7(this.f$0, charSequence, i, i2, spanned, i3, i4);
            }
        };
    }

    static /* synthetic */ CharSequence lambda$getLengthFilter$7(int maxLen, CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
        int count;
        int dindex = 0;
        int count2 = 0;
        while (count2 <= maxLen && dindex < dest.length()) {
            int dindex2 = dindex + 1;
            if (dest.charAt(dindex) < 128) {
                count2++;
            } else {
                count2 += 2;
            }
            dindex = dindex2;
        }
        if (count2 > maxLen) {
            return dest.subSequence(0, dindex - 1);
        }
        int sindex = 0;
        while (count2 <= maxLen && sindex < src.length()) {
            int sindex2 = sindex + 1;
            if (src.charAt(sindex) < 128) {
                count = count2 + 1;
            } else {
                count = count2 + 2;
            }
            sindex = sindex2;
        }
        if (count2 > maxLen) {
            sindex--;
        }
        return src.subSequence(0, sindex);
    }

    public boolean onBackPressed() {
        if (this.etNoteEditView.getText().toString().equals(this.userNote)) {
            return super.onBackPressed();
        }
        showSaveDialog();
        return false;
    }
}
