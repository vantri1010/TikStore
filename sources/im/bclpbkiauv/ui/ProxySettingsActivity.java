package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ProxySettingsActivity extends BaseFragment {
    private static final int FIELD_IP = 0;
    private static final int FIELD_PASSWORD = 3;
    private static final int FIELD_PORT = 1;
    private static final int FIELD_SECRET = 4;
    private static final int FIELD_USER = 2;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public boolean addingNewProxy;
    private TextInfoPrivacyCell bottomCell;
    /* access modifiers changed from: private */
    public SharedConfig.ProxyInfo currentProxyInfo;
    /* access modifiers changed from: private */
    public int currentType;
    private ActionBarMenuItem doneItem;
    private HeaderCell headerCell;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputFields;
    private LinearLayout linearLayout2;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell shareCell;
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

    public ProxySettingsActivity() {
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = new SharedConfig.ProxyInfo("", 1080, "", "", "");
        this.addingNewProxy = true;
    }

    public ProxySettingsActivity(SharedConfig.ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = proxyInfo;
        this.currentType = TextUtils.isEmpty(proxyInfo.secret) ^ true ? 1 : 0;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("ProxyDetails", R.string.ProxyDetails));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                boolean enabled;
                if (id == -1) {
                    ProxySettingsActivity.this.finishFragment();
                } else if (id == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
                    ProxySettingsActivity.this.currentProxyInfo.address = ProxySettingsActivity.this.inputFields[0].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.port = Utilities.parseInt(ProxySettingsActivity.this.inputFields[1].getText().toString()).intValue();
                    if (ProxySettingsActivity.this.currentType == 0) {
                        ProxySettingsActivity.this.currentProxyInfo.secret = "";
                        ProxySettingsActivity.this.currentProxyInfo.username = ProxySettingsActivity.this.inputFields[2].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.password = ProxySettingsActivity.this.inputFields[3].getText().toString();
                    } else {
                        ProxySettingsActivity.this.currentProxyInfo.secret = ProxySettingsActivity.this.inputFields[4].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.username = "";
                        ProxySettingsActivity.this.currentProxyInfo.password = "";
                    }
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    SharedPreferences.Editor editor = preferences.edit();
                    if (ProxySettingsActivity.this.addingNewProxy) {
                        SharedConfig.addProxy(ProxySettingsActivity.this.currentProxyInfo);
                        SharedConfig.currentProxy = ProxySettingsActivity.this.currentProxyInfo;
                        editor.putBoolean("proxy_enabled", true);
                        enabled = true;
                    } else {
                        enabled = preferences.getBoolean("proxy_enabled", false);
                        SharedConfig.saveProxyList();
                    }
                    if (ProxySettingsActivity.this.addingNewProxy || SharedConfig.currentProxy == ProxySettingsActivity.this.currentProxyInfo) {
                        editor.putString("proxy_ip", ProxySettingsActivity.this.currentProxyInfo.address);
                        editor.putString("proxy_pass", ProxySettingsActivity.this.currentProxyInfo.password);
                        editor.putString("proxy_user", ProxySettingsActivity.this.currentProxyInfo.username);
                        editor.putInt("proxy_port", ProxySettingsActivity.this.currentProxyInfo.port);
                        editor.putString("proxy_secret", ProxySettingsActivity.this.currentProxyInfo.secret);
                        ConnectionsManager.setProxySettings(enabled, ProxySettingsActivity.this.currentProxyInfo.address, ProxySettingsActivity.this.currentProxyInfo.port, ProxySettingsActivity.this.currentProxyInfo.username, ProxySettingsActivity.this.currentProxyInfo.password, ProxySettingsActivity.this.currentProxyInfo.secret);
                    }
                    editor.commit();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                    ProxySettingsActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.doneItem = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        scrollView2.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        ((FrameLayout) this.fragmentView).addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.linearLayout2 = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        int a = 0;
        while (a < 2) {
            this.typeCell[a] = new TypeCell(context2);
            this.typeCell[a].setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.typeCell[a].setTag(Integer.valueOf(a));
            if (a == 0) {
                this.typeCell[a].setValue(LocaleController.getString("UseProxySocks5", R.string.UseProxySocks5), a == this.currentType, true);
            } else if (a == 1) {
                this.typeCell[a].setValue(LocaleController.getString("UseProxyMTProto", R.string.UseProxyMTProto), a == this.currentType, false);
            }
            this.linearLayout2.addView(this.typeCell[a], LayoutHelper.createLinear(-1, 50));
            this.typeCell[a].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ProxySettingsActivity.this.lambda$createView$0$ProxySettingsActivity(view);
                }
            });
            a++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[5];
        for (int a2 = 0; a2 < 5; a2++) {
            FrameLayout container = new FrameLayout(context2);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 64));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a2] = new EditTextBoldCursor(context2);
            this.inputFields[a2].setTag(Integer.valueOf(a2));
            this.inputFields[a2].setTextSize(1, 16.0f);
            this.inputFields[a2].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setBackgroundDrawable((Drawable) null);
            this.inputFields[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a2].setCursorWidth(1.5f);
            this.inputFields[a2].setSingleLine(true);
            this.inputFields[a2].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.inputFields[a2].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.inputFields[a2].setTransformHintToHeader(true);
            this.inputFields[a2].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            if (a2 == 0) {
                this.inputFields[a2].setInputType(524305);
                this.inputFields[a2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        ProxySettingsActivity.this.checkShareButton();
                    }
                });
            } else if (a2 == 1) {
                this.inputFields[a2].setInputType(2);
                this.inputFields[a2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!ProxySettingsActivity.this.ignoreOnTextChange) {
                            EditText phoneField = ProxySettingsActivity.this.inputFields[1];
                            int start = phoneField.getSelectionStart();
                            String str = phoneField.getText().toString();
                            StringBuilder builder = new StringBuilder(str.length());
                            for (int a = 0; a < str.length(); a++) {
                                String ch = str.substring(a, a + 1);
                                if ("0123456789".contains(ch)) {
                                    builder.append(ch);
                                }
                            }
                            boolean unused = ProxySettingsActivity.this.ignoreOnTextChange = true;
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
                            boolean unused2 = ProxySettingsActivity.this.ignoreOnTextChange = false;
                            ProxySettingsActivity.this.checkShareButton();
                        }
                    }
                });
            } else if (a2 == 3) {
                this.inputFields[a2].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.inputFields[a2].setTypeface(Typeface.DEFAULT);
                this.inputFields[a2].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.inputFields[a2].setInputType(524289);
            }
            this.inputFields[a2].setImeOptions(268435461);
            if (a2 == 0) {
                this.inputFields[a2].setHintText(LocaleController.getString("UseProxyAddress", R.string.UseProxyAddress));
                this.inputFields[a2].setText(this.currentProxyInfo.address);
            } else if (a2 == 1) {
                this.inputFields[a2].setHintText(LocaleController.getString("UseProxyPort", R.string.UseProxyPort));
                EditTextBoldCursor editTextBoldCursor = this.inputFields[a2];
                editTextBoldCursor.setText("" + this.currentProxyInfo.port);
            } else if (a2 == 2) {
                this.inputFields[a2].setHintText(LocaleController.getString("UseProxyUsername", R.string.UseProxyUsername));
                this.inputFields[a2].setText(this.currentProxyInfo.username);
            } else if (a2 == 3) {
                this.inputFields[a2].setHintText(LocaleController.getString("UseProxyPassword", R.string.UseProxyPassword));
                this.inputFields[a2].setText(this.currentProxyInfo.password);
            } else if (a2 == 4) {
                this.inputFields[a2].setHintText(LocaleController.getString("UseProxySecret", R.string.UseProxySecret));
                this.inputFields[a2].setText(this.currentProxyInfo.secret);
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a2].setSelection(editTextBoldCursorArr[a2].length());
            this.inputFields[a2].setPadding(0, 0, 0, 0);
            container.addView(this.inputFields[a2], LayoutHelper.createFrame(-1.0f, -1.0f, 51, 17.0f, 0.0f, 17.0f, 0.0f));
            this.inputFields[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ProxySettingsActivity.this.lambda$createView$1$ProxySettingsActivity(textView, i, keyEvent);
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.bottomCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("UseProxyInfo", R.string.UseProxyInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
        this.shareCell = textSettingsCell;
        textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.shareCell.setText(LocaleController.getString("ShareFile", R.string.ShareFile), false);
        this.shareCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ProxySettingsActivity.this.lambda$createView$2$ProxySettingsActivity(view);
            }
        });
        this.sectionCell[1] = new ShadowSectionCell(context2);
        this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        checkShareButton();
        updateUiForType();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ProxySettingsActivity(View view) {
        this.currentType = ((Integer) view.getTag()).intValue();
        updateUiForType();
    }

    public /* synthetic */ boolean lambda$createView$1$ProxySettingsActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            int i2 = num + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (i2 < editTextBoldCursorArr.length) {
                editTextBoldCursorArr[num + 1].requestFocus();
            }
            return true;
        } else if (i != 6) {
            return false;
        } else {
            finishFragment();
            return true;
        }
    }

    public /* synthetic */ void lambda$createView$2$ProxySettingsActivity(View v) {
        String url;
        StringBuilder params = new StringBuilder();
        String secret = "";
        String address = this.inputFields[0].getText() != null ? this.inputFields[0].getText().toString() : secret;
        String password = this.inputFields[3].getText() != null ? this.inputFields[3].getText().toString() : secret;
        String user = this.inputFields[2].getText() != null ? this.inputFields[2].getText().toString() : secret;
        String port = this.inputFields[1].getText() != null ? this.inputFields[1].getText().toString() : secret;
        if (this.inputFields[4].getText() != null) {
            secret = this.inputFields[4].getText().toString();
        }
        try {
            if (!TextUtils.isEmpty(address)) {
                params.append("server=");
                params.append(URLEncoder.encode(address, "UTF-8"));
            }
            if (!TextUtils.isEmpty(port)) {
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("port=");
                params.append(URLEncoder.encode(port, "UTF-8"));
            }
            if (this.currentType == 1) {
                url = "https://m12345.com/proxy?";
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("secret=");
                params.append(URLEncoder.encode(secret, "UTF-8"));
            } else {
                url = "https://m12345.com/socks?";
                if (!TextUtils.isEmpty(user)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("user=");
                    params.append(URLEncoder.encode(user, "UTF-8"));
                }
                if (!TextUtils.isEmpty(password)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("pass=");
                    params.append(URLEncoder.encode(password, "UTF-8"));
                }
            }
            if (params.length() != 0) {
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.TEXT", url + params.toString());
                Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", R.string.ShareLink));
                chooserIntent.setFlags(C.ENCODING_PCM_MU_LAW);
                getParentActivity().startActivity(chooserIntent);
            }
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public void checkShareButton() {
        if (this.shareCell != null && this.doneItem != null) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (editTextBoldCursorArr[0] != null && editTextBoldCursorArr[1] != null) {
                if (editTextBoldCursorArr[0].length() == 0 || Utilities.parseInt(this.inputFields[1].getText().toString()).intValue() == 0) {
                    this.shareCell.getTextView().setAlpha(0.5f);
                    this.doneItem.setAlpha(0.5f);
                    this.shareCell.setEnabled(false);
                    this.doneItem.setEnabled(false);
                    return;
                }
                this.shareCell.getTextView().setAlpha(1.0f);
                this.doneItem.setAlpha(1.0f);
                this.shareCell.setEnabled(true);
                this.doneItem.setEnabled(true);
            }
        }
    }

    private void updateUiForType() {
        int i = this.currentType;
        boolean z = true;
        if (i == 0) {
            this.bottomCell.setText(LocaleController.getString("UseProxyInfo", R.string.UseProxyInfo));
            ((View) this.inputFields[4].getParent()).setVisibility(8);
            ((View) this.inputFields[3].getParent()).setVisibility(0);
            ((View) this.inputFields[2].getParent()).setVisibility(0);
        } else if (i == 1) {
            TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell;
            textInfoPrivacyCell.setText(LocaleController.getString("UseProxyMTprotoSettings", R.string.UseProxyMTprotoSettings) + "\n\n" + LocaleController.getString("UseProxySettingsTips", R.string.UseProxySettingsTips));
            ((View) this.inputFields[4].getParent()).setVisibility(0);
            ((View) this.inputFields[3].getParent()).setVisibility(8);
            ((View) this.inputFields[2].getParent()).setVisibility(8);
        }
        this.typeCell[0].setTypeChecked(this.currentType == 0);
        TypeCell typeCell2 = this.typeCell[1];
        if (this.currentType != 1) {
            z = false;
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

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription((View) this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription((View) this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        int a = 0;
        while (true) {
            TypeCell[] typeCellArr = this.typeCell;
            if (a >= typeCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(typeCellArr[a], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription((View) this.typeCell[a], 0, new Class[]{TypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription((View) this.typeCell[a], ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addedIcon));
            a++;
        }
        if (this.inputFields != null) {
            int a2 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (a2 >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription((View) editTextBoldCursorArr[a2].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                a2++;
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        for (int a3 = 0; a3 < 2; a3++) {
            arrayList.add(new ThemeDescription(this.sectionCell[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow));
        }
        arrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription((View) this.bottomCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription((View) this.bottomCell, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}
