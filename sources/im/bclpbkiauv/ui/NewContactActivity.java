package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.CountrySelectActivity;
import im.bclpbkiauv.ui.NewContactActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.HintEditText;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import kotlin.text.Typography;
import org.slf4j.Marker;

public class NewContactActivity extends BaseFragment implements AdapterView.OnItemSelectedListener {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public EditTextBoldCursor codeField;
    /* access modifiers changed from: private */
    public HashMap<String, String> codesMap = new HashMap<>();
    /* access modifiers changed from: private */
    public ArrayList<String> countriesArray = new ArrayList<>();
    private HashMap<String, String> countriesMap = new HashMap<>();
    /* access modifiers changed from: private */
    public TextView countryButton;
    /* access modifiers changed from: private */
    public int countryState;
    /* access modifiers changed from: private */
    public boolean donePressed;
    /* access modifiers changed from: private */
    public ActionBarMenuItem editDoneItem;
    /* access modifiers changed from: private */
    public AnimatorSet editDoneItemAnimation;
    /* access modifiers changed from: private */
    public ContextProgressView editDoneItemProgress;
    /* access modifiers changed from: private */
    public EditTextBoldCursor firstNameField;
    /* access modifiers changed from: private */
    public boolean ignoreOnPhoneChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    /* access modifiers changed from: private */
    public boolean ignoreSelection;
    /* access modifiers changed from: private */
    public String initialPhoneNumber;
    /* access modifiers changed from: private */
    public EditTextBoldCursor lastNameField;
    private View lineView;
    /* access modifiers changed from: private */
    public HintEditText phoneField;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap = new HashMap<>();
    private TextView textView;

    public View createView(Context context) {
        String countryName;
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NewContactActivity.this.finishFragment();
                } else if (id == 1 && !NewContactActivity.this.donePressed) {
                    if (NewContactActivity.this.firstNameField.length() == 0) {
                        Vibrator v = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v != null) {
                            v.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0f, 0);
                    } else if (NewContactActivity.this.codeField.length() == 0) {
                        Vibrator v2 = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v2 != null) {
                            v2.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0f, 0);
                    } else if (NewContactActivity.this.phoneField.length() == 0) {
                        Vibrator v3 = (Vibrator) NewContactActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v3 != null) {
                            v3.vibrate(200);
                        }
                        AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0f, 0);
                    } else {
                        boolean unused = NewContactActivity.this.donePressed = true;
                        NewContactActivity.this.showEditDoneProgress(true, true);
                        TLRPC.TL_contacts_importContacts req = new TLRPC.TL_contacts_importContacts();
                        TLRPC.TL_inputPhoneContact inputPhoneContact = new TLRPC.TL_inputPhoneContact();
                        inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
                        inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
                        inputPhoneContact.phone = Marker.ANY_NON_NULL_MARKER + NewContactActivity.this.codeField.getText().toString() + NewContactActivity.this.phoneField.getText().toString();
                        req.contacts.add(inputPhoneContact);
                        ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(NewContactActivity.this.currentAccount).sendRequest(req, new RequestDelegate(inputPhoneContact, req) {
                            private final /* synthetic */ TLRPC.TL_inputPhoneContact f$1;
                            private final /* synthetic */ TLRPC.TL_contacts_importContacts f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                NewContactActivity.AnonymousClass1.this.lambda$onItemClick$2$NewContactActivity$1(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        }, 2), NewContactActivity.this.classGuid);
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$NewContactActivity$1(TLRPC.TL_inputPhoneContact inputPhoneContact, TLRPC.TL_contacts_importContacts req, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_contacts_importedContacts) response, inputPhoneContact, error, req) {
                    private final /* synthetic */ TLRPC.TL_contacts_importedContacts f$1;
                    private final /* synthetic */ TLRPC.TL_inputPhoneContact f$2;
                    private final /* synthetic */ TLRPC.TL_error f$3;
                    private final /* synthetic */ TLRPC.TL_contacts_importContacts f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        NewContactActivity.AnonymousClass1.this.lambda$null$1$NewContactActivity$1(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }

            public /* synthetic */ void lambda$null$1$NewContactActivity$1(TLRPC.TL_contacts_importedContacts res, TLRPC.TL_inputPhoneContact inputPhoneContact, TLRPC.TL_error error, TLRPC.TL_contacts_importContacts req) {
                boolean unused = NewContactActivity.this.donePressed = false;
                if (res == null) {
                    NewContactActivity.this.showEditDoneProgress(false, true);
                    AlertsCreator.processError(NewContactActivity.this.currentAccount, error, NewContactActivity.this, req, new Object[0]);
                } else if (!res.users.isEmpty()) {
                    MessagesController.getInstance(NewContactActivity.this.currentAccount).putUsers(res.users, false);
                    MessagesController.openChatOrProfileWith(res.users.get(0), (TLRPC.Chat) null, NewContactActivity.this, 1, true);
                } else if (NewContactActivity.this.getParentActivity() != null) {
                    NewContactActivity.this.showEditDoneProgress(false, true);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) NewContactActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ContactNotRegistered", R.string.ContactNotRegistered, ContactsController.formatName(inputPhoneContact.first_name, inputPhoneContact.last_name)));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    builder.setPositiveButton(LocaleController.getString("Invite", R.string.Invite), new DialogInterface.OnClickListener(inputPhoneContact) {
                        private final /* synthetic */ TLRPC.TL_inputPhoneContact f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            NewContactActivity.AnonymousClass1.this.lambda$null$0$NewContactActivity$1(this.f$1, dialogInterface, i);
                        }
                    });
                    NewContactActivity.this.showDialog(builder.create());
                }
            }

            public /* synthetic */ void lambda$null$0$NewContactActivity$1(TLRPC.TL_inputPhoneContact inputPhoneContact, DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", inputPhoneContact.phone, (String) null));
                    intent.putExtra("sms_body", ContactsController.getInstance(NewContactActivity.this.currentAccount).getInviteText(1));
                    NewContactActivity.this.getParentActivity().startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        });
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setInfo(5, "", "");
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.editDoneItem = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString("Done", R.string.Done));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.editDoneItemProgress = contextProgressView;
        this.editDoneItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.editDoneItemProgress.setVisibility(4);
        this.fragmentView = new ScrollView(context2);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        linearLayout.setOnTouchListener($$Lambda$NewContactActivity$tBSptTK4znQKTdcJ59T_UMnQxQ0.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 24.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImage = backupImageView;
        backupImageView.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60.0f, 60.0f, 51, 0.0f, 9.0f, 0.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setGravity(3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1.0f, 34.0f, 51, 84.0f, 0.0f, 0.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.lambda$createView$1$NewContactActivity(textView, i, keyEvent);
            }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString());
                NewContactActivity.this.avatarImage.invalidate();
            }
        });
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
        this.lastNameField = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(5);
        this.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
        this.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        frameLayout.addView(this.lastNameField, LayoutHelper.createFrame(-1.0f, 34.0f, 51, 84.0f, 44.0f, 0.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.lambda$createView$2$NewContactActivity(textView, i, keyEvent);
            }
        });
        this.lastNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString());
                NewContactActivity.this.avatarImage.invalidate();
            }
        });
        TextView textView2 = new TextView(context2);
        this.countryButton = textView2;
        textView2.setTextSize(1, 18.0f);
        this.countryButton.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f), 0);
        this.countryButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.countryButton.setMaxLines(1);
        this.countryButton.setSingleLine(true);
        this.countryButton.setEllipsize(TextUtils.TruncateAt.END);
        this.countryButton.setGravity(3);
        this.countryButton.setBackgroundResource(R.drawable.spinner_states);
        linearLayout.addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0f, 24.0f, 0.0f, 14.0f));
        this.countryButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                NewContactActivity.this.lambda$createView$5$NewContactActivity(view);
            }
        });
        View view = new View(context2);
        this.lineView = view;
        view.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.lineView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
        linearLayout.addView(this.lineView, LayoutHelper.createLinear(-1, 1, 0.0f, -17.5f, 0.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setText(Marker.ANY_NON_NULL_MARKER);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 18.0f);
        this.textView.setImportantForAccessibility(2);
        linearLayout2.addView(this.textView, LayoutHelper.createLinear(-2, -2));
        EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context2);
        this.codeField = editTextBoldCursor3;
        editTextBoldCursor3.setInputType(3);
        this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.codeField.setCursorWidth(1.5f);
        this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0f);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        linearLayout2.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 16.0f, 0.0f));
        this.codeField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (!NewContactActivity.this.ignoreOnTextChange) {
                    boolean unused = NewContactActivity.this.ignoreOnTextChange = true;
                    String text = PhoneFormat.stripExceptNumbers(NewContactActivity.this.codeField.getText().toString());
                    NewContactActivity.this.codeField.setText(text);
                    String str = null;
                    if (text.length() == 0) {
                        NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                        NewContactActivity.this.phoneField.setHintText((String) null);
                        int unused2 = NewContactActivity.this.countryState = 1;
                    } else {
                        boolean ok = false;
                        String textToSet = null;
                        if (text.length() > 4) {
                            boolean unused3 = NewContactActivity.this.ignoreOnTextChange = true;
                            int a = 4;
                            while (true) {
                                if (a < 1) {
                                    break;
                                }
                                String sub = text.substring(0, a);
                                if (((String) NewContactActivity.this.codesMap.get(sub)) != null) {
                                    ok = true;
                                    textToSet = text.substring(a) + NewContactActivity.this.phoneField.getText().toString();
                                    text = sub;
                                    NewContactActivity.this.codeField.setText(sub);
                                    break;
                                }
                                a--;
                            }
                            if (!ok) {
                                boolean unused4 = NewContactActivity.this.ignoreOnTextChange = true;
                                textToSet = text.substring(1) + NewContactActivity.this.phoneField.getText().toString();
                                EditTextBoldCursor access$200 = NewContactActivity.this.codeField;
                                String substring = text.substring(0, 1);
                                text = substring;
                                access$200.setText(substring);
                            }
                        }
                        String country = (String) NewContactActivity.this.codesMap.get(text);
                        if (country != null) {
                            int index = NewContactActivity.this.countriesArray.indexOf(country);
                            if (index != -1) {
                                boolean unused5 = NewContactActivity.this.ignoreSelection = true;
                                NewContactActivity.this.countryButton.setText((CharSequence) NewContactActivity.this.countriesArray.get(index));
                                String hint = (String) NewContactActivity.this.phoneFormatMap.get(text);
                                HintEditText access$300 = NewContactActivity.this.phoneField;
                                if (hint != null) {
                                    str = hint.replace('X', Typography.ndash);
                                }
                                access$300.setHintText(str);
                                int unused6 = NewContactActivity.this.countryState = 0;
                            } else {
                                NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                                NewContactActivity.this.phoneField.setHintText((String) null);
                                int unused7 = NewContactActivity.this.countryState = 2;
                            }
                        } else {
                            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", R.string.WrongCountry));
                            NewContactActivity.this.phoneField.setHintText((String) null);
                            int unused8 = NewContactActivity.this.countryState = 2;
                        }
                        if (!ok) {
                            NewContactActivity.this.codeField.setSelection(NewContactActivity.this.codeField.getText().length());
                        }
                        if (textToSet != null) {
                            if (NewContactActivity.this.initialPhoneNumber == null) {
                                NewContactActivity.this.phoneField.requestFocus();
                            }
                            NewContactActivity.this.phoneField.setText(textToSet);
                            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
                        }
                    }
                    boolean unused9 = NewContactActivity.this.ignoreOnTextChange = false;
                }
            }
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.lambda$createView$6$NewContactActivity(textView, i, keyEvent);
            }
        });
        HintEditText hintEditText = new HintEditText(context2);
        this.phoneField = hintEditText;
        hintEditText.setInputType(3);
        this.phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.phoneField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.phoneField.setPadding(0, 0, 0, 0);
        this.phoneField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.phoneField.setCursorWidth(1.5f);
        this.phoneField.setTextSize(1, 18.0f);
        this.phoneField.setMaxLines(1);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(268435462);
        linearLayout2.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
        this.phoneField.addTextChangedListener(new TextWatcher() {
            private int actionPosition;
            private int characterAction = -1;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 0 && after == 1) {
                    this.characterAction = 1;
                } else if (count != 1 || after != 0) {
                    this.characterAction = -1;
                } else if (s.charAt(start) != ' ' || start <= 0) {
                    this.characterAction = 2;
                } else {
                    this.characterAction = 3;
                    this.actionPosition = start - 1;
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                int i;
                int i2;
                if (!NewContactActivity.this.ignoreOnPhoneChange) {
                    int start = NewContactActivity.this.phoneField.getSelectionStart();
                    String str = NewContactActivity.this.phoneField.getText().toString();
                    if (this.characterAction == 3) {
                        str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1);
                        start--;
                    }
                    StringBuilder builder = new StringBuilder(str.length());
                    for (int a = 0; a < str.length(); a++) {
                        String ch = str.substring(a, a + 1);
                        if ("0123456789".contains(ch)) {
                            builder.append(ch);
                        }
                    }
                    boolean unused = NewContactActivity.this.ignoreOnPhoneChange = true;
                    String hint = NewContactActivity.this.phoneField.getHintText();
                    if (hint != null) {
                        int a2 = 0;
                        while (true) {
                            if (a2 >= builder.length()) {
                                break;
                            } else if (a2 < hint.length()) {
                                if (hint.charAt(a2) == ' ') {
                                    builder.insert(a2, ' ');
                                    a2++;
                                    if (!(start != a2 || (i2 = this.characterAction) == 2 || i2 == 3)) {
                                        start++;
                                    }
                                }
                                a2++;
                            } else {
                                builder.insert(a2, ' ');
                                if (start == a2 + 1 && (i = this.characterAction) != 2 && i != 3) {
                                    start++;
                                }
                            }
                        }
                    }
                    NewContactActivity.this.phoneField.setText(builder);
                    if (start >= 0) {
                        NewContactActivity.this.phoneField.setSelection(start <= NewContactActivity.this.phoneField.length() ? start : NewContactActivity.this.phoneField.length());
                    }
                    NewContactActivity.this.phoneField.onTextChange();
                    boolean unused2 = NewContactActivity.this.ignoreOnPhoneChange = false;
                }
            }
        });
        this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.lambda$createView$7$NewContactActivity(textView, i, keyEvent);
            }
        });
        this.phoneField.setOnKeyListener(new View.OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return NewContactActivity.this.lambda$createView$8$NewContactActivity(view, i, keyEvent);
            }
        });
        HashMap hashMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] args = line.split(";");
                this.countriesArray.add(0, args[2]);
                this.countriesMap.put(args[2], args[0]);
                this.codesMap.put(args[0], args[2]);
                if (args.length > 3) {
                    this.phoneFormatMap.put(args[0], args[3]);
                }
                hashMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Collections.sort(this.countriesArray, $$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE);
        if (!TextUtils.isEmpty(this.initialPhoneNumber)) {
            this.codeField.setText(this.initialPhoneNumber);
            this.initialPhoneNumber = null;
        } else {
            String country = null;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    country = telephonyManager.getSimCountryIso().toUpperCase();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (!(country == null || (countryName = (String) hashMap.get(country)) == null || this.countriesArray.indexOf(countryName) == -1)) {
                this.codeField.setText(this.countriesMap.get(countryName));
                this.countryState = 0;
            }
            if (this.codeField.length() == 0) {
                this.countryButton.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                this.phoneField.setHintText((String) null);
                this.countryState = 1;
            }
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ boolean lambda$createView$1$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.lastNameField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return true;
    }

    public /* synthetic */ boolean lambda$createView$2$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    public /* synthetic */ void lambda$createView$5$NewContactActivity(View view) {
        CountrySelectActivity fragment = new CountrySelectActivity(true);
        fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
            public final void didSelectCountry(CountrySelectActivity.Country country) {
                NewContactActivity.this.lambda$null$4$NewContactActivity(country);
            }
        });
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$null$4$NewContactActivity(CountrySelectActivity.Country country) {
        selectCountry((String) null, country);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                NewContactActivity.this.lambda$null$3$NewContactActivity();
            }
        }, 300);
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
    }

    public /* synthetic */ void lambda$null$3$NewContactActivity() {
        AndroidUtilities.showKeyboard(this.phoneField);
    }

    public /* synthetic */ boolean lambda$createView$6$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.phoneField.requestFocus();
        HintEditText hintEditText = this.phoneField;
        hintEditText.setSelection(hintEditText.length());
        return true;
    }

    public /* synthetic */ boolean lambda$createView$7$NewContactActivity(TextView textView2, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.editDoneItem.performClick();
        return true;
    }

    public /* synthetic */ boolean lambda$createView$8$NewContactActivity(View v, int keyCode, KeyEvent event) {
        if (keyCode != 67 || this.phoneField.length() != 0) {
            return false;
        }
        this.codeField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.codeField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.codeField.dispatchKeyEvent(event);
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void setInitialPhoneNumber(String value) {
        this.initialPhoneNumber = value;
    }

    public void selectCountry(String name, CountrySelectActivity.Country country) {
        String str = null;
        if (name != null) {
            if (this.countriesArray.indexOf(name) != -1) {
                this.ignoreOnTextChange = true;
                String code = this.countriesMap.get(name);
                this.codeField.setText(code);
                this.countryButton.setText(name);
                String hint = this.phoneFormatMap.get(code);
                HintEditText hintEditText = this.phoneField;
                if (hint != null) {
                    str = hint.replace('X', Typography.ndash);
                }
                hintEditText.setHintText(str);
                this.countryState = 0;
                this.ignoreOnTextChange = false;
            }
        } else if (country != null) {
            this.ignoreOnTextChange = true;
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            editTextBoldCursor.setText(country.code + "");
            if (country.phoneFormat != null) {
                HintEditText hintEditText2 = this.phoneField;
                if (country.phoneFormat != null) {
                    str = country.phoneFormat.replace('X', Typography.ndash);
                }
                hintEditText2.setHintText(str);
            }
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (this.ignoreSelection) {
            this.ignoreSelection = false;
            return;
        }
        this.ignoreOnTextChange = true;
        this.codeField.setText(this.countriesMap.get(this.countriesArray.get(i)));
        this.ignoreOnTextChange = false;
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean show, boolean animated) {
        final boolean z = show;
        AnimatorSet animatorSet = this.editDoneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animated) {
            this.editDoneItemAnimation = new AnimatorSet();
            if (z) {
                this.editDoneItemProgress.setVisibility(0);
                this.editDoneItem.setEnabled(false);
                this.editDoneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{1.0f})});
            } else {
                this.editDoneItem.getContentView().setVisibility(0);
                this.editDoneItem.setEnabled(true);
                this.editDoneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.editDoneItem.getContentView(), "alpha", new float[]{1.0f})});
            }
            this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        if (!z) {
                            NewContactActivity.this.editDoneItemProgress.setVisibility(4);
                        } else {
                            NewContactActivity.this.editDoneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (NewContactActivity.this.editDoneItemAnimation != null && NewContactActivity.this.editDoneItemAnimation.equals(animation)) {
                        AnimatorSet unused = NewContactActivity.this.editDoneItemAnimation = null;
                    }
                }
            });
            this.editDoneItemAnimation.setDuration(150);
            this.editDoneItemAnimation.start();
        } else if (z) {
            this.editDoneItem.getContentView().setScaleX(0.1f);
            this.editDoneItem.getContentView().setScaleY(0.1f);
            this.editDoneItem.getContentView().setAlpha(0.0f);
            this.editDoneItemProgress.setScaleX(1.0f);
            this.editDoneItemProgress.setScaleY(1.0f);
            this.editDoneItemProgress.setAlpha(1.0f);
            this.editDoneItem.getContentView().setVisibility(4);
            this.editDoneItemProgress.setVisibility(0);
            this.editDoneItem.setEnabled(false);
        } else {
            this.editDoneItemProgress.setScaleX(0.1f);
            this.editDoneItemProgress.setScaleY(0.1f);
            this.editDoneItemProgress.setAlpha(0.0f);
            this.editDoneItem.getContentView().setScaleX(1.0f);
            this.editDoneItem.getContentView().setScaleY(1.0f);
            this.editDoneItem.getContentView().setAlpha(1.0f);
            this.editDoneItem.getContentView().setVisibility(0);
            this.editDoneItemProgress.setVisibility(4);
            this.editDoneItem.setEnabled(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                NewContactActivity.this.lambda$getThemeDescriptions$9$NewContactActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.codeField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayLine), new ThemeDescription(this.countryButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editDoneItemProgress, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressInner2), new ThemeDescription(this.editDoneItemProgress, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressOuter2), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, themeDescriptionDelegate, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$9$NewContactActivity() {
        if (this.avatarImage != null) {
            this.avatarDrawable.setInfo(5, this.firstNameField.getText().toString(), this.lastNameField.getText().toString());
            this.avatarImage.invalidate();
        }
    }
}
