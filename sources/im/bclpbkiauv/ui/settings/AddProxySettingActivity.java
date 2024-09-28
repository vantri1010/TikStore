package im.bclpbkiauv.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;

@Deprecated
public class AddProxySettingActivity extends BaseFragment {
    private static final int FIELD_IP = 0;
    private static final int FIELD_PASSWORD = 3;
    private static final int FIELD_PORT = 1;
    private static final int FIELD_SECRET = 4;
    private static final int FIELD_USER = 2;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public boolean addingNewProxy;
    /* access modifiers changed from: private */
    public SharedConfig.ProxyInfo currentProxyInfo;
    /* access modifiers changed from: private */
    public int currentType;
    private HeaderCell headerCell;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputFields;
    private EditText metPort;
    private EditText metPwd;
    private EditText metServer;
    private EditText metUserName;
    private ShadowSectionCell[] sectionCell;
    private TypeCell[] typeCell;

    public class TypeCell extends FrameLayout {
        private ImageView checkImage;
        private boolean needDivider;
        private TextView textView;

        public TypeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.checkImage = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
            this.checkImage.setImageResource(R.mipmap.ic_selected);
            addView(this.checkImage, LayoutHelper.createFrame(19.0f, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void setValue(String name, boolean checked, boolean divider) {
            this.textView.setText(name);
            this.checkImage.setVisibility(checked ? 0 : 4);
            this.needDivider = divider;
        }

        public void setTypeChecked(boolean value) {
            this.checkImage.setVisibility(value ? 0 : 4);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public AddProxySettingActivity() {
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = new SharedConfig.ProxyInfo("", 1080, "", "", "");
        this.addingNewProxy = true;
    }

    public AddProxySettingActivity(SharedConfig.ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = proxyInfo;
        this.currentType = TextUtils.isEmpty(proxyInfo.secret) ^ true ? 1 : 0;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    private void initListener() {
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                boolean enabled;
                if (id == -1) {
                    AddProxySettingActivity.this.finishFragment();
                }
                if (id == 1 && AddProxySettingActivity.this.getParentActivity() != null) {
                    AddProxySettingActivity.this.currentProxyInfo.address = AddProxySettingActivity.this.inputFields[0].getText().toString();
                    AddProxySettingActivity.this.currentProxyInfo.port = Utilities.parseInt(AddProxySettingActivity.this.inputFields[1].getText().toString()).intValue();
                    if (AddProxySettingActivity.this.currentType == 0) {
                        AddProxySettingActivity.this.currentProxyInfo.secret = "";
                        AddProxySettingActivity.this.currentProxyInfo.username = AddProxySettingActivity.this.inputFields[2].getText().toString();
                        AddProxySettingActivity.this.currentProxyInfo.password = AddProxySettingActivity.this.inputFields[3].getText().toString();
                    } else {
                        AddProxySettingActivity.this.currentProxyInfo.secret = AddProxySettingActivity.this.inputFields[4].getText().toString();
                        AddProxySettingActivity.this.currentProxyInfo.username = "";
                        AddProxySettingActivity.this.currentProxyInfo.password = "";
                    }
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    SharedPreferences.Editor editor = preferences.edit();
                    if (AddProxySettingActivity.this.addingNewProxy) {
                        SharedConfig.addProxy(AddProxySettingActivity.this.currentProxyInfo);
                        SharedConfig.currentProxy = AddProxySettingActivity.this.currentProxyInfo;
                        editor.putBoolean("proxy_enabled", true);
                        enabled = true;
                    } else {
                        enabled = preferences.getBoolean("proxy_enabled", false);
                        SharedConfig.saveProxyList();
                    }
                    if (AddProxySettingActivity.this.addingNewProxy || SharedConfig.currentProxy == AddProxySettingActivity.this.currentProxyInfo) {
                        editor.putString("proxy_ip", AddProxySettingActivity.this.currentProxyInfo.address);
                        editor.putString("proxy_pass", AddProxySettingActivity.this.currentProxyInfo.password);
                        editor.putString("proxy_user", AddProxySettingActivity.this.currentProxyInfo.username);
                        editor.putInt("proxy_port", AddProxySettingActivity.this.currentProxyInfo.port);
                        editor.putString("proxy_secret", AddProxySettingActivity.this.currentProxyInfo.secret);
                        ConnectionsManager.setProxySettings(enabled, AddProxySettingActivity.this.currentProxyInfo.address, AddProxySettingActivity.this.currentProxyInfo.port, AddProxySettingActivity.this.currentProxyInfo.username, AddProxySettingActivity.this.currentProxyInfo.password, AddProxySettingActivity.this.currentProxyInfo.secret);
                    }
                    editor.commit();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                    AddProxySettingActivity.this.finishFragment();
                }
            }
        });
        this.metServer.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                AddProxySettingActivity.this.checkShareButton();
            }
        });
        this.metPort.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!AddProxySettingActivity.this.ignoreOnTextChange) {
                    EditText phoneField = AddProxySettingActivity.this.inputFields[1];
                    int start = phoneField.getSelectionStart();
                    String str = phoneField.getText().toString();
                    StringBuilder builder = new StringBuilder(str.length());
                    for (int a = 0; a < str.length(); a++) {
                        String ch = str.substring(a, a + 1);
                        if ("0123456789".contains(ch)) {
                            builder.append(ch);
                        }
                    }
                    boolean unused = AddProxySettingActivity.this.ignoreOnTextChange = true;
                    int port = Utilities.parseInt(builder.toString()).intValue();
                    if (port < 0 || port > 65535 || !str.equals(builder.toString())) {
                        if (port < 0) {
                            phoneField.setText("0");
                        } else if (port > 65535) {
                            phoneField.setText("65535");
                        } else {
                            phoneField.setText(builder.toString());
                        }
                    } else if (start >= 0) {
                        phoneField.setSelection(start <= phoneField.length() ? start : phoneField.length());
                    }
                    boolean unused2 = AddProxySettingActivity.this.ignoreOnTextChange = false;
                    AddProxySettingActivity.this.checkShareButton();
                }
            }
        });
    }

    private void initView() {
        this.metServer = (EditText) this.fragmentView.findViewById(R.id.et_server);
        this.metPort = (EditText) this.fragmentView.findViewById(R.id.et_port);
        this.metUserName = (EditText) this.fragmentView.findViewById(R.id.et_username);
        this.metPwd = (EditText) this.fragmentView.findViewById(R.id.et_password);
    }

    private void initState() {
        this.metServer.setText(this.currentProxyInfo.address);
        this.metPwd.setText(this.currentProxyInfo.password);
        EditText editText = this.metPort;
        editText.setText("" + this.currentProxyInfo.port);
        this.metUserName.setText(this.currentProxyInfo.username);
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("ProxyDetails", R.string.ProxyDetails));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.createMenu().addRightItemView(1, LocaleController.getString("Done", R.string.Done));
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_add_proxy, (ViewGroup) null, false);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public void checkShareButton() {
    }

    private void updateUiForType() {
        boolean z = false;
        this.typeCell[0].setTypeChecked(this.currentType == 0);
        TypeCell typeCell2 = this.typeCell[1];
        if (this.currentType == 1) {
            z = true;
        }
        typeCell2.setTypeChecked(z);
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }
}
