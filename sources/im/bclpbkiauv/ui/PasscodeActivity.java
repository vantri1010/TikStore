package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.support.fingerprint.FingerprintManagerCompat;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.NumberPicker;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;

public class PasscodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private static final int password_item = 3;
    private static final int pin_item = 2;
    /* access modifiers changed from: private */
    public int autoLockDetailRow;
    /* access modifiers changed from: private */
    public int autoLockRow;
    private int badPasscodeTries;
    /* access modifiers changed from: private */
    public int captureDetailRow;
    /* access modifiers changed from: private */
    public int captureRow;
    /* access modifiers changed from: private */
    public int changePasscodeRow;
    /* access modifiers changed from: private */
    public int currentPasswordType = 0;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    /* access modifiers changed from: private */
    public int fingerprintRow;
    private String firstPassword;
    private long lastPasscodeTry;
    private ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int passcodeDetailRow;
    /* access modifiers changed from: private */
    public int passcodeRow;
    /* access modifiers changed from: private */
    public int passcodeSetStep = 0;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int rowCount;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int type;

    public PasscodeActivity(int type2) {
        this.type = type2;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type != 0) {
            return true;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.type != 3) {
            this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PasscodeActivity.this.finishFragment();
                } else if (id == 1) {
                    if (PasscodeActivity.this.passcodeSetStep == 0) {
                        PasscodeActivity.this.processNext();
                    } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                        PasscodeActivity.this.processDone();
                    }
                } else if (id == 2) {
                    int unused = PasscodeActivity.this.currentPasswordType = 0;
                    PasscodeActivity.this.updateDropDownTextView();
                } else if (id == 3) {
                    int unused2 = PasscodeActivity.this.currentPasswordType = 1;
                    PasscodeActivity.this.updateDropDownTextView();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (this.type != 0) {
            ActionBarMenu menu = this.actionBar.createMenu();
            menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            TextView textView = new TextView(context2);
            this.titleTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            if (this.type != 1) {
                this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", R.string.EnterCurrentPasscode));
            } else if (SharedConfig.passcodeHash.length() != 0) {
                this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", R.string.EnterNewPasscode));
            } else {
                this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", R.string.EnterNewFirstPasscode));
            }
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setGravity(1);
            frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 1, 0.0f, 38.0f, 0.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
            this.passwordEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 20.0f);
            this.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.passwordEditText.setMaxLines(1);
            this.passwordEditText.setLines(1);
            this.passwordEditText.setGravity(1);
            this.passwordEditText.setSingleLine(true);
            if (this.type == 1) {
                this.passcodeSetStep = 0;
                this.passwordEditText.setImeOptions(5);
            } else {
                this.passcodeSetStep = 1;
                this.passwordEditText.setImeOptions(6);
            }
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setTypeface(Typeface.DEFAULT);
            this.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.passwordEditText.setCursorWidth(1.5f);
            frameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(-1.0f, 36.0f, 51, 40.0f, 90.0f, 40.0f, 0.0f));
            this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return PasscodeActivity.this.lambda$createView$0$PasscodeActivity(textView, i, keyEvent);
                }
            });
            this.passwordEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (PasscodeActivity.this.passwordEditText.length() != 4) {
                        return;
                    }
                    if (PasscodeActivity.this.type == 2 && SharedConfig.passcodeType == 0) {
                        PasscodeActivity.this.processDone();
                    } else if (PasscodeActivity.this.type != 1 || PasscodeActivity.this.currentPasswordType != 0) {
                    } else {
                        if (PasscodeActivity.this.passcodeSetStep == 0) {
                            PasscodeActivity.this.processNext();
                        } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                            PasscodeActivity.this.processDone();
                        }
                    }
                }
            });
            this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
            if (this.type == 1) {
                frameLayout.setTag(Theme.key_windowBackgroundWhite);
                ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, menu, 0, 0);
                this.dropDownContainer = actionBarMenuItem;
                actionBarMenuItem.setSubMenuOpenSide(1);
                this.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", R.string.PasscodePIN));
                this.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", R.string.PasscodePassword));
                this.actionBar.addView(this.dropDownContainer, LayoutHelper.createFrame(-2.0f, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
                this.dropDownContainer.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PasscodeActivity.this.lambda$createView$1$PasscodeActivity(view);
                    }
                });
                TextView textView2 = new TextView(context2);
                this.dropDown = textView2;
                textView2.setGravity(3);
                this.dropDown.setSingleLine(true);
                this.dropDown.setLines(1);
                this.dropDown.setMaxLines(1);
                this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
                this.dropDown.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                Drawable mutate = context.getResources().getDrawable(R.drawable.ic_arrow_drop_down).mutate();
                this.dropDownDrawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultTitle), PorterDuff.Mode.MULTIPLY));
                this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
                this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
                this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2.0f, -2.0f, 16, 16.0f, 0.0f, 0.0f, 1.0f));
            } else {
                this.actionBar.setTitle(LocaleController.getString("Passcode", R.string.Passcode));
            }
            updateDropDownTextView();
        } else {
            this.actionBar.setTitle(LocaleController.getString("Passcode", R.string.Passcode));
            frameLayout.setTag(Theme.key_windowBackgroundGray);
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            RecyclerListView recyclerListView = new RecyclerListView(context2);
            this.listView = recyclerListView;
            recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            });
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.listView.setLayoutAnimation((LayoutAnimationController) null);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
            RecyclerListView recyclerListView2 = this.listView;
            ListAdapter listAdapter2 = new ListAdapter(context2);
            this.listAdapter = listAdapter2;
            recyclerListView2.setAdapter(listAdapter2);
            this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    PasscodeActivity.this.lambda$createView$4$PasscodeActivity(view, i);
                }
            });
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$0$PasscodeActivity(TextView textView, int i, KeyEvent keyEvent) {
        int i2 = this.passcodeSetStep;
        if (i2 == 0) {
            processNext();
            return true;
        } else if (i2 != 1) {
            return false;
        } else {
            processDone();
            return true;
        }
    }

    public /* synthetic */ void lambda$createView$1$PasscodeActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$createView$4$PasscodeActivity(View view, int position) {
        if (view.isEnabled()) {
            boolean z = true;
            if (position == this.changePasscodeRow) {
                presentFragment(new PasscodeActivity(1));
            } else if (position == this.passcodeRow) {
                TextCheckCell cell = (TextCheckCell) view;
                if (SharedConfig.passcodeHash.length() != 0) {
                    SharedConfig.passcodeHash = "";
                    SharedConfig.appLocked = false;
                    SharedConfig.saveConfig();
                    int count = this.listView.getChildCount();
                    int a = 0;
                    while (true) {
                        if (a >= count) {
                            break;
                        }
                        View child = this.listView.getChildAt(a);
                        if (child instanceof TextSettingsCell) {
                            ((TextSettingsCell) child).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                            break;
                        }
                        a++;
                    }
                    if (SharedConfig.passcodeHash.length() == 0) {
                        z = false;
                    }
                    cell.setChecked(z);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                    return;
                }
                presentFragment(new PasscodeActivity(1));
            } else if (position == this.autoLockRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("AutoLock", R.string.AutoLock));
                    NumberPicker numberPicker = new NumberPicker(getParentActivity());
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(4);
                    if (SharedConfig.autoLockIn == 0) {
                        numberPicker.setValue(0);
                    } else if (SharedConfig.autoLockIn == 60) {
                        numberPicker.setValue(1);
                    } else if (SharedConfig.autoLockIn == 300) {
                        numberPicker.setValue(2);
                    } else if (SharedConfig.autoLockIn == 3600) {
                        numberPicker.setValue(3);
                    } else if (SharedConfig.autoLockIn == 18000) {
                        numberPicker.setValue(4);
                    }
                    numberPicker.setFormatter($$Lambda$PasscodeActivity$T6R0KFUMmWxvAHca2OAZKIvur6o.INSTANCE);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener(numberPicker, position) {
                        private final /* synthetic */ NumberPicker f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PasscodeActivity.this.lambda$null$3$PasscodeActivity(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    showDialog(builder.create());
                }
            } else if (position == this.fingerprintRow) {
                SharedConfig.useFingerprint = !SharedConfig.useFingerprint;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
            } else if (position == this.captureRow) {
                SharedConfig.allowScreenCapture = !SharedConfig.allowScreenCapture;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                if (!SharedConfig.allowScreenCapture) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("ScreenCaptureAlert", R.string.ScreenCaptureAlert));
                }
            }
        }
    }

    static /* synthetic */ String lambda$null$2(int value) {
        if (value == 0) {
            return LocaleController.getString("AutoLockDisabled", R.string.AutoLockDisabled);
        }
        if (value == 1) {
            return LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 1));
        } else if (value == 2) {
            return LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 5));
        } else if (value == 3) {
            return LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 1));
        } else if (value != 4) {
            return "";
        } else {
            return LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 5));
        }
    }

    public /* synthetic */ void lambda$null$3$PasscodeActivity(NumberPicker numberPicker, int position, DialogInterface dialog, int which) {
        int which2 = numberPicker.getValue();
        if (which2 == 0) {
            SharedConfig.autoLockIn = 0;
        } else if (which2 == 1) {
            SharedConfig.autoLockIn = 60;
        } else if (which2 == 2) {
            SharedConfig.autoLockIn = 300;
        } else if (which2 == 3) {
            SharedConfig.autoLockIn = 3600;
        } else if (which2 == 4) {
            SharedConfig.autoLockIn = 18000;
        }
        this.listAdapter.notifyItemChanged(position);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (this.type != 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PasscodeActivity.this.lambda$onResume$5$PasscodeActivity();
                }
            }, 200);
        }
        fixLayoutInternal();
    }

    public /* synthetic */ void lambda$onResume$5$PasscodeActivity() {
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetPasscode && this.type == 0) {
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.passcodeRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.changePasscodeRow = i;
        this.rowCount = i2 + 1;
        this.passcodeDetailRow = i2;
        if (SharedConfig.passcodeHash.length() > 0) {
            try {
                if (Build.VERSION.SDK_INT >= 23 && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected()) {
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.fingerprintRow = i3;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.autoLockRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.autoLockDetailRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.captureRow = i6;
            this.rowCount = i7 + 1;
            this.captureDetailRow = i7;
            return;
        }
        this.captureRow = -1;
        this.captureDetailRow = -1;
        this.fingerprintRow = -1;
        this.autoLockRow = -1;
        this.autoLockDetailRow = -1;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    PasscodeActivity.this.fixLayoutInternal();
                    return true;
                }
            });
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.type != 0) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    /* access modifiers changed from: private */
    public void updateDropDownTextView() {
        TextView textView = this.dropDown;
        if (textView != null) {
            int i = this.currentPasswordType;
            if (i == 0) {
                textView.setText(LocaleController.getString("PasscodePIN", R.string.PasscodePIN));
            } else if (i == 1) {
                textView.setText(LocaleController.getString("PasscodePassword", R.string.PasscodePassword));
            }
        }
        if ((this.type == 1 && this.currentPasswordType == 0) || (this.type == 2 && SharedConfig.passcodeType == 0)) {
            this.passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            this.passwordEditText.setInputType(3);
            this.passwordEditText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        } else if ((this.type == 1 && this.currentPasswordType == 1) || (this.type == 2 && SharedConfig.passcodeType == 1)) {
            this.passwordEditText.setFilters(new InputFilter[0]);
            this.passwordEditText.setKeyListener((KeyListener) null);
            this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        }
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    /* access modifiers changed from: private */
    public void processNext() {
        if (this.passwordEditText.getText().length() == 0 || (this.currentPasswordType == 0 && this.passwordEditText.getText().length() != 4)) {
            onPasscodeError();
            return;
        }
        if (this.currentPasswordType == 0) {
            this.actionBar.setTitle(LocaleController.getString("PasscodePIN", R.string.PasscodePIN));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PasscodePassword", R.string.PasscodePassword));
        }
        this.dropDownContainer.setVisibility(8);
        this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", R.string.ReEnterYourPasscode));
        this.firstPassword = this.passwordEditText.getText().toString();
        this.passwordEditText.setText("");
        this.passcodeSetStep = 1;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
            return;
        }
        int i = this.type;
        if (i == 1) {
            if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                ToastUtils.show((int) R.string.PasscodeDoNotMatch);
                AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
                this.passwordEditText.setText("");
                return;
            }
            try {
                SharedConfig.passcodeSalt = new byte[16];
                Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                byte[] passcodeBytes = this.firstPassword.getBytes("UTF-8");
                byte[] bytes = new byte[(passcodeBytes.length + 32)];
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, 0, 16);
                System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            SharedConfig.allowScreenCapture = true;
            SharedConfig.passcodeType = this.currentPasswordType;
            SharedConfig.saveConfig();
            finishFragment();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
            this.passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(this.passwordEditText);
        } else if (i != 2) {
        } else {
            if (SharedConfig.passcodeRetryInMs > 0) {
                ToastUtils.showFormat(R.string.TooManyTries, LocaleController.formatPluralString("Seconds", Math.max(1, (int) Math.ceil(((double) SharedConfig.passcodeRetryInMs) / 1000.0d))));
                this.passwordEditText.setText("");
                onPasscodeError();
            } else if (!SharedConfig.checkPasscode(this.passwordEditText.getText().toString())) {
                SharedConfig.increaseBadPasscodeTries();
                this.passwordEditText.setText("");
                onPasscodeError();
            } else {
                SharedConfig.badPasscodeTries = 0;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                presentFragment(new PasscodeActivity(0), true);
            }
        }
    }

    private void onPasscodeError() {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal() {
        if (this.dropDownContainer != null) {
            if (!AndroidUtilities.isTablet()) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.dropDownContainer.getLayoutParams();
                layoutParams.topMargin = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                this.dropDownContainer.setLayoutParams(layoutParams);
            }
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.dropDown.setTextSize(20.0f);
            } else {
                this.dropDown.setTextSize(18.0f);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PasscodeActivity.this.passcodeRow || position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.autoLockRow || position == PasscodeActivity.this.captureRow || (SharedConfig.passcodeHash.length() != 0 && position == PasscodeActivity.this.changePasscodeRow);
        }

        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new TextCheckCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType != 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else {
                View view3 = new TextSettingsCell(this.mContext);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String val;
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                TextCheckCell textCell = (TextCheckCell) holder.itemView;
                textCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                if (position == PasscodeActivity.this.passcodeRow) {
                    String string = LocaleController.getString("Passcode", R.string.Passcode);
                    if (SharedConfig.passcodeHash.length() > 0) {
                        z = true;
                    }
                    textCell.setTextAndCheck(string, z, true);
                } else if (position == PasscodeActivity.this.fingerprintRow) {
                    textCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", R.string.UnlockFingerprint), SharedConfig.useFingerprint, true);
                } else if (position == PasscodeActivity.this.captureRow) {
                    textCell.setTextAndCheck(LocaleController.getString("ScreenCapture", R.string.ScreenCapture), SharedConfig.allowScreenCapture, false);
                    textCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else if (itemViewType == 1) {
                TextSettingsCell textCell2 = (TextSettingsCell) holder.itemView;
                if (position == PasscodeActivity.this.changePasscodeRow) {
                    textCell2.setText(LocaleController.getString("ChangePasscode", R.string.ChangePasscode), false);
                    if (SharedConfig.passcodeHash.length() == 0) {
                        textCell2.setTag(Theme.key_windowBackgroundWhiteGrayText7);
                        textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                    } else {
                        textCell2.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    }
                } else if (position == PasscodeActivity.this.autoLockRow) {
                    if (SharedConfig.autoLockIn == 0) {
                        val = LocaleController.formatString("AutoLockDisabled", R.string.AutoLockDisabled, new Object[0]);
                    } else if (SharedConfig.autoLockIn < 3600) {
                        val = LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60));
                    } else if (SharedConfig.autoLockIn < 86400) {
                        val = LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f))));
                    } else {
                        val = LocaleController.formatString("AutoLockInTime", R.string.AutoLockInTime, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f) / 24.0f))));
                    }
                    textCell2.setTextAndValue(LocaleController.getString("AutoLock", R.string.AutoLock), val, false);
                    textCell2.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                }
                textCell2.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
            } else if (itemViewType == 2) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                if (position == PasscodeActivity.this.passcodeDetailRow) {
                    cell.setText(LocaleController.getString("ChangePasscodeInfo", R.string.ChangePasscodeInfo));
                } else if (position == PasscodeActivity.this.autoLockDetailRow) {
                    cell.setText(LocaleController.getString("AutoLockInfo", R.string.AutoLockInfo));
                } else if (position == PasscodeActivity.this.captureDetailRow) {
                    cell.setText(LocaleController.getString("ScreenCaptureInfo", R.string.ScreenCaptureInfo));
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == PasscodeActivity.this.passcodeRow || position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (position == PasscodeActivity.this.changePasscodeRow || position == PasscodeActivity.this.autoLockRow) {
                return 1;
            }
            if (position == PasscodeActivity.this.passcodeDetailRow || position == PasscodeActivity.this.autoLockDetailRow || position == PasscodeActivity.this.captureDetailRow) {
                return 2;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.dropDown, 0, (Class[]) null, (Paint) null, new Drawable[]{this.dropDownDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText7), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4)};
    }
}
