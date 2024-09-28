package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.contacts.GreetEditActivity;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;

public class GreetEditActivity extends BaseFragment {
    public static final int CONTACT_ADD_REPLY = 1;
    public static final int CONTACT_ADD_REQUEST = 0;
    private final int DONE = 1;
    /* access modifiers changed from: private */
    public GreetEditDelegate delegate;
    private Drawable deleteDrawable;
    /* access modifiers changed from: private */
    public MryEditText etGreetEditView;
    /* access modifiers changed from: private */
    public ImageView ivClearGreetView;
    private TextView tvGreetDescView;
    private TextView tvOkView;
    /* access modifiers changed from: private */
    public int type;
    private TLRPC.User user;

    public interface GreetEditDelegate {
        void onFinish(String str);
    }

    public void setDelegate(GreetEditDelegate delegate2) {
        this.delegate = delegate2;
    }

    public GreetEditActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.user = getUserConfig().getCurrentUser();
        if (this.arguments == null) {
            return true;
        }
        this.type = this.arguments.getInt("type", 0);
        return true;
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_greet_edit_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initView();
        setNote();
        return this.fragmentView;
    }

    private void initActionBar() {
        if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("RequestValidation", R.string.RequestValidation));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Reply", R.string.Reply));
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setCastShadows(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GreetEditActivity.this.finishFragment();
                } else if (id != 1) {
                } else {
                    if (GreetEditActivity.this.type != 0) {
                        XDialog.Builder builder = new XDialog.Builder(GreetEditActivity.this.getParentActivity());
                        builder.setTitle("消息回复");
                        builder.setMessage("您确定要回复该条消息吗！");
                        builder.setNegativeButton("取消", (DialogInterface.OnClickListener) null);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                GreetEditActivity.AnonymousClass1.this.lambda$onItemClick$0$GreetEditActivity$1(dialogInterface, i);
                            }
                        });
                        builder.create().show();
                    } else if (GreetEditActivity.this.etGreetEditView.getText().toString().length() >= 100) {
                        ToastUtils.show((CharSequence) LocaleController.getString(R.string.apply_info_too_long));
                    } else {
                        XDialog.Builder builder2 = new XDialog.Builder(GreetEditActivity.this.getParentActivity());
                        builder2.setTitle(LocaleController.getString("AddFriends", R.string.AddFriends));
                        builder2.setMessage(LocaleController.getString("SendContactApplyText", R.string.SendContactApplyText));
                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (GreetEditActivity.this.delegate != null) {
                                    String greet = GreetEditActivity.this.etGreetEditView.getText().toString().trim();
                                    if (TextUtils.isEmpty(greet)) {
                                        greet = GreetEditActivity.this.etGreetEditView.getHint().toString().trim();
                                    }
                                    GreetEditActivity.this.delegate.onFinish(greet);
                                }
                                GreetEditActivity.this.finishFragment();
                            }
                        });
                        builder2.create().show();
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$GreetEditActivity$1(DialogInterface dialog, int which) {
                GreetEditActivity.this.sendReplyMessage();
                GreetEditActivity.this.finishFragment();
            }
        });
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        ActionBarMenu menu = this.actionBar.createMenu();
        this.tvOkView = new TextView(getParentActivity());
        menu.addItem(1, (CharSequence) LocaleController.getString("Send", R.string.Send));
        TextView textView = (TextView) menu.getItem(1).getContentView();
        this.tvOkView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
    }

    /* access modifiers changed from: private */
    public void sendReplyMessage() {
    }

    private void initView() {
        this.fragmentView.findViewById(R.id.content).setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.etGreetEditView = (MryEditText) this.fragmentView.findViewById(R.id.etGreetEditView);
        this.ivClearGreetView = (ImageView) this.fragmentView.findViewById(R.id.ivClearGreetView);
        TextView textView = (TextView) this.fragmentView.findViewById(R.id.tvGreetDescView);
        this.tvGreetDescView = textView;
        if (this.type == 1) {
            textView.setVisibility(8);
        }
        this.tvGreetDescView.setText(LocaleController.getString("ReqeustText", R.string.ReqeustText));
        if (this.type == 1) {
            this.etGreetEditView.setHint(LocaleController.getString("InputReplyContent", R.string.InputReplyContent));
        } else {
            MryEditText mryEditText = this.etGreetEditView;
            mryEditText.setHint(LocaleController.getString("HelloText", R.string.HelloText) + UserObject.getName(this.user));
        }
        this.etGreetEditView.setHintColor(Theme.key_windowBackgroundWhiteHintText);
        Drawable drawable = getParentActivity().getResources().getDrawable(R.drawable.delete);
        this.deleteDrawable = drawable;
        Drawable tintDrawable = DrawableUtils.tintDrawable(drawable, Theme.getColor(Theme.key_windowBackgroundValueText1));
        this.deleteDrawable = tintDrawable;
        tintDrawable.setBounds(0, 0, tintDrawable.getIntrinsicWidth(), this.deleteDrawable.getIntrinsicHeight());
        this.ivClearGreetView.setImageDrawable(this.deleteDrawable);
        this.etGreetEditView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GreetEditActivity.this.ivClearGreetView.setVisibility(s.length() > 0 ? 0 : 8);
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.ivClearGreetView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GreetEditActivity.this.lambda$initView$0$GreetEditActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$GreetEditActivity(View v) {
        this.etGreetEditView.setText("");
    }

    private void setNote() {
    }

    private void sendMessage() {
    }
}
