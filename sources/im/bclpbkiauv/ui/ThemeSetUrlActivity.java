package im.bclpbkiauv.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.cells.ThemePreviewMessagesCell;
import im.bclpbkiauv.ui.cells.ThemesHorizontalListCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;

public class ThemeSetUrlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextInfoPrivacyCell checkInfoCell;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextSettingsCell createCell;
    private TextInfoPrivacyCell createInfoCell;
    /* access modifiers changed from: private */
    public boolean creatingNewTheme;
    private View divider;
    private View doneButton;
    private EditText editText;
    private HeaderCell headerCell;
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell helpInfoCell;
    /* access modifiers changed from: private */
    public boolean ignoreCheck;
    /* access modifiers changed from: private */
    public CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayoutTypeContainer;
    /* access modifiers changed from: private */
    public EditTextBoldCursor linkField;
    private ThemePreviewMessagesCell messagesCell;
    private EditTextBoldCursor nameField;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public Theme.ThemeInfo themeInfo;

    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String value) {
            this.url = value;
        }

        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public void onClick(View widget) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                ToastUtils.show((int) R.string.LinkCopied);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public ThemeSetUrlActivity(Theme.ThemeInfo theme, boolean newTheme) {
        this.themeInfo = theme;
        this.creatingNewTheme = newTheme;
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.creatingNewTheme) {
            this.actionBar.setTitle(LocaleController.getString("NewThemeTitle", R.string.NewThemeTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditThemeTitle", R.string.EditThemeTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ThemeSetUrlActivity.this.finishFragment();
                } else if (id == 1) {
                    ThemeSetUrlActivity.this.saveTheme();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.fragmentView = new LinearLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener($$Lambda$ThemeSetUrlActivity$PJbCjibhjxzLXvw40ofzEdKn6E.INSTANCE);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayoutTypeContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell2 = new HeaderCell(context2, 23);
        this.headerCell = headerCell2;
        headerCell2.setText(LocaleController.getString("Info", R.string.Info));
        this.linearLayoutTypeContainer.addView(this.headerCell);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.nameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.nameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.nameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameField.setMaxLines(1);
        this.nameField.setLines(1);
        this.nameField.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameField.setBackgroundDrawable((Drawable) null);
        this.nameField.setPadding(0, 0, 0, 0);
        this.nameField.setSingleLine(true);
        this.nameField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
        this.nameField.setInputType(163872);
        this.nameField.setImeOptions(6);
        this.nameField.setHint(LocaleController.getString("ThemeNamePlaceholder", R.string.ThemeNamePlaceholder));
        this.nameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.nameField.setCursorWidth(1.5f);
        this.linearLayoutTypeContainer.addView(this.nameField, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        this.nameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ThemeSetUrlActivity.this.lambda$createView$1$ThemeSetUrlActivity(textView, i, keyEvent);
            }
        });
        AnonymousClass2 r6 = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        };
        this.divider = r6;
        this.linearLayoutTypeContainer.addView(r6, new LinearLayout.LayoutParams(-1, 1));
        LinearLayout linkContainer = new LinearLayout(context2);
        linkContainer.setOrientation(0);
        this.linearLayoutTypeContainer.addView(linkContainer, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        EditText editText2 = new EditText(context2);
        this.editText = editText2;
        editText2.setText(getMessagesController().linkPrefix + "/addtheme/");
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        linkContainer.addView(this.editText, LayoutHelper.createLinear(-2, 50));
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
        this.linkField = editTextBoldCursor2;
        editTextBoldCursor2.setTextSize(1, 18.0f);
        this.linkField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.linkField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.linkField.setMaxLines(1);
        this.linkField.setLines(1);
        this.linkField.setBackgroundDrawable((Drawable) null);
        this.linkField.setPadding(0, 0, 0, 0);
        this.linkField.setSingleLine(true);
        this.linkField.setInputType(163872);
        this.linkField.setImeOptions(6);
        this.linkField.setHint(LocaleController.getString("SetUrlPlaceholder", R.string.SetUrlPlaceholder));
        this.linkField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.linkField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.linkField.setCursorWidth(1.5f);
        linkContainer.addView(this.linkField, LayoutHelper.createLinear(-1, 50));
        this.linkField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ThemeSetUrlActivity.this.lambda$createView$2$ThemeSetUrlActivity(textView, i, keyEvent);
            }
        });
        this.linkField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ThemeSetUrlActivity.this.ignoreCheck) {
                    ThemeSetUrlActivity themeSetUrlActivity = ThemeSetUrlActivity.this;
                    boolean unused = themeSetUrlActivity.checkUrl(themeSetUrlActivity.linkField.getText().toString(), false);
                }
            }

            public void afterTextChanged(Editable editable) {
                if (!ThemeSetUrlActivity.this.creatingNewTheme) {
                    if (ThemeSetUrlActivity.this.linkField.length() > 0) {
                        String url = "https://" + MessagesController.getInstance(ThemeSetUrlActivity.this.themeInfo.account).linkPrefix + "/addtheme/" + ThemeSetUrlActivity.this.linkField.getText();
                        String text = LocaleController.formatString("ThemeHelpLink", R.string.ThemeHelpLink, url);
                        int index = text.indexOf(url);
                        SpannableStringBuilder textSpan = new SpannableStringBuilder(text);
                        if (index >= 0) {
                            textSpan.setSpan(new LinkSpan(url), index, url.length() + index, 33);
                        }
                        ThemeSetUrlActivity.this.helpInfoCell.setText(TextUtils.concat(new CharSequence[]{ThemeSetUrlActivity.this.infoText, "\n\n", textSpan}));
                        return;
                    }
                    ThemeSetUrlActivity.this.helpInfoCell.setText(ThemeSetUrlActivity.this.infoText);
                }
            }
        });
        if (this.creatingNewTheme) {
            this.linkField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public final void onFocusChange(View view, boolean z) {
                    ThemeSetUrlActivity.this.lambda$createView$3$ThemeSetUrlActivity(view, z);
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.checkInfoCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.checkInfoCell.setVisibility(8);
        this.checkInfoCell.setBottomPadding(0);
        linearLayout.addView(this.checkInfoCell, LayoutHelper.createLinear(-1, -2));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.helpInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.getTextView().setMovementMethod(new LinkMovementMethodMy());
        this.helpInfoCell.getTextView().setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", R.string.ThemeCreateHelp)));
        } else {
            TextInfoPrivacyCell textInfoPrivacyCell3 = this.helpInfoCell;
            SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("ThemeSetUrlHelp", R.string.ThemeSetUrlHelp));
            this.infoText = replaceTags;
            textInfoPrivacyCell3.setText(replaceTags);
        }
        linearLayout.addView(this.helpInfoCell, LayoutHelper.createLinear(-1, -2));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context2, this.parentLayout, 1);
            this.messagesCell = themePreviewMessagesCell;
            linearLayout.addView(themePreviewMessagesCell, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.createCell = textSettingsCell;
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.createCell.setText(LocaleController.getString("UseDifferentTheme", R.string.UseDifferentTheme), false);
            linearLayout.addView(this.createCell, LayoutHelper.createLinear(-1, -2));
            this.createCell.setOnClickListener(new View.OnClickListener(context2) {
                private final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ThemeSetUrlActivity.this.lambda$createView$5$ThemeSetUrlActivity(this.f$1, view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context2);
            this.createInfoCell = textInfoPrivacyCell4;
            textInfoPrivacyCell4.setText(AndroidUtilities.replaceTags(LocaleController.getString("UseDifferentThemeInfo", R.string.UseDifferentThemeInfo)));
            this.createInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            linearLayout.addView(this.createInfoCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        }
        Theme.ThemeInfo themeInfo2 = this.themeInfo;
        if (themeInfo2 != null) {
            this.ignoreCheck = true;
            this.nameField.setText(themeInfo2.name);
            EditTextBoldCursor editTextBoldCursor3 = this.nameField;
            editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
            this.linkField.setText(this.themeInfo.info.slug);
            EditTextBoldCursor editTextBoldCursor4 = this.linkField;
            editTextBoldCursor4.setSelection(editTextBoldCursor4.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ boolean lambda$createView$1$ThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.nameField);
        return true;
    }

    public /* synthetic */ boolean lambda$createView$2$ThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$3$ThemeSetUrlActivity(View v, boolean hasFocus) {
        if (hasFocus) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp2", R.string.ThemeCreateHelp2)));
        } else {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", R.string.ThemeCreateHelp)));
        }
    }

    public /* synthetic */ void lambda$createView$5$ThemeSetUrlActivity(Context context, View v) {
        if (getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity(), false, 1);
            builder.setApplyBottomPadding(false);
            LinearLayout container = new LinearLayout(context);
            container.setOrientation(1);
            TextView titleView = new TextView(context);
            titleView.setText(LocaleController.getString("ChooseTheme", R.string.ChooseTheme));
            titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            titleView.setTextSize(1, 20.0f);
            titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            container.addView(titleView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
            titleView.setOnTouchListener($$Lambda$ThemeSetUrlActivity$nHsMx0cKmz1bjBu3TI2W2jbdwk.INSTANCE);
            builder.setCustomView(container);
            ArrayList<Theme.ThemeInfo> themes = new ArrayList<>();
            int N = Theme.themes.size();
            for (int a = 0; a < N; a++) {
                Theme.ThemeInfo themeInfo2 = Theme.themes.get(a);
                if (themeInfo2.info == null || themeInfo2.info.document != null) {
                    themes.add(themeInfo2);
                }
            }
            final BottomSheet.Builder builder2 = builder;
            ThemesHorizontalListCell cell = new ThemesHorizontalListCell(context, 2, themes, new ArrayList()) {
                /* access modifiers changed from: protected */
                public void updateRows() {
                    builder2.getDismissRunnable().run();
                }
            };
            container.addView(cell, LayoutHelper.createLinear(-1, 148, 0.0f, 7.0f, 0.0f, 1.0f));
            cell.scrollToCurrentTheme(this.fragmentView.getMeasuredWidth(), false);
            showDialog(builder.create());
        }
    }

    static /* synthetic */ boolean lambda$null$4(View v2, MotionEvent event) {
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        AlertDialog alertDialog;
        AlertDialog alertDialog2;
        if (id == NotificationCenter.themeUploadedToServer) {
            if (args[0] == this.themeInfo && (alertDialog2 = this.progressDialog) != null) {
                try {
                    alertDialog2.dismiss();
                    this.progressDialog = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                Theme.applyTheme(this.themeInfo, false);
                finishFragment();
            }
        } else if (id == NotificationCenter.themeUploadError && args[0] == this.themeInfo && (alertDialog = this.progressDialog) != null) {
            try {
                alertDialog.dismiss();
                this.progressDialog = null;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean checkUrl(String url, boolean alert) {
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.themeInfo.account).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (url != null) {
            if (url.startsWith("_") || url.endsWith("_")) {
                setCheckText(LocaleController.getString("SetUrlInvalid", R.string.SetUrlInvalid), Theme.key_windowBackgroundWhiteRedText4);
                return false;
            }
            int a = 0;
            while (a < url.length()) {
                char ch = url.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalidStartNumber", R.string.SetUrlInvalidStartNumber));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalidStartNumber", R.string.SetUrlInvalidStartNumber), Theme.key_windowBackgroundWhiteRedText4);
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalid", R.string.SetUrlInvalid));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalid", R.string.SetUrlInvalid), Theme.key_windowBackgroundWhiteRedText4);
                    }
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (url == null || url.length() < 5) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalidShort", R.string.SetUrlInvalidShort));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidShort", R.string.SetUrlInvalidShort), Theme.key_windowBackgroundWhiteRedText4);
            }
            return false;
        } else if (url.length() > 64) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("SetUrlInvalidLong", R.string.SetUrlInvalidLong));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidLong", R.string.SetUrlInvalidLong), Theme.key_windowBackgroundWhiteRedText4);
            }
            return false;
        } else {
            if (!alert) {
                Theme.ThemeInfo themeInfo2 = this.themeInfo;
                if (url.equals((themeInfo2 == null || themeInfo2.info.slug == null) ? "" : this.themeInfo.info.slug)) {
                    setCheckText(LocaleController.formatString("SetUrlAvailable", R.string.SetUrlAvailable, url), Theme.key_windowBackgroundWhiteGreenText);
                    return true;
                }
                setCheckText(LocaleController.getString("SetUrlChecking", R.string.SetUrlChecking), Theme.key_windowBackgroundWhiteGrayText8);
                this.lastCheckName = url;
                $$Lambda$ThemeSetUrlActivity$GivdAB9Avn6ta4LmJ5SOGswjLM r0 = new Runnable(url) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ThemeSetUrlActivity.this.lambda$checkUrl$8$ThemeSetUrlActivity(this.f$1);
                    }
                };
                this.checkRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 300);
            }
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUrl$8$ThemeSetUrlActivity(String url) {
        TLRPC.TL_account_createTheme req = new TLRPC.TL_account_createTheme();
        req.slug = url;
        req.title = "";
        req.document = new TLRPC.TL_inputDocumentEmpty();
        this.checkReqId = ConnectionsManager.getInstance(this.themeInfo.account).sendRequest(req, new RequestDelegate(url) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ThemeSetUrlActivity.this.lambda$null$7$ThemeSetUrlActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$7$ThemeSetUrlActivity(String url, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(url, error) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ThemeSetUrlActivity.this.lambda$null$6$ThemeSetUrlActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$ThemeSetUrlActivity(String url, TLRPC.TL_error error) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(url)) {
            if (error == null || (!"THEME_SLUG_INVALID".equals(error.text) && !"THEME_SLUG_OCCUPIED".equals(error.text))) {
                setCheckText(LocaleController.formatString("SetUrlAvailable", R.string.SetUrlAvailable, url), Theme.key_windowBackgroundWhiteGreenText);
                this.lastNameAvailable = true;
                return;
            }
            setCheckText(LocaleController.getString("SetUrlInUse", R.string.SetUrlInUse), Theme.key_windowBackgroundWhiteRedText4);
            this.lastNameAvailable = false;
        }
    }

    private void setCheckText(String text, String colorKey) {
        if (TextUtils.isEmpty(text)) {
            this.checkInfoCell.setVisibility(8);
            if (this.creatingNewTheme) {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            } else {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        } else {
            this.checkInfoCell.setVisibility(0);
            this.checkInfoCell.setText(text);
            this.checkInfoCell.setTag(colorKey);
            this.checkInfoCell.setTextColor(colorKey);
            if (this.creatingNewTheme) {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
            } else {
                this.helpInfoCell.setBackgroundDrawable((Drawable) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveTheme() {
        if (!checkUrl(this.linkField.getText().toString(), true) || getParentActivity() == null) {
            return;
        }
        if (this.nameField.length() == 0) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("ThemeNameInvalid", R.string.ThemeNameInvalid));
        } else if (this.creatingNewTheme) {
            String str = this.themeInfo.name;
            String str2 = this.themeInfo.info.slug;
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setOnCancelListener($$Lambda$ThemeSetUrlActivity$UWqFxyYfiIwUAAq7VHAtrJntF0.INSTANCE);
            this.progressDialog.show();
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            TLRPC.TL_theme tL_theme = themeInfo2.info;
            String obj = this.nameField.getText().toString();
            tL_theme.title = obj;
            themeInfo2.name = obj;
            this.themeInfo.info.slug = this.linkField.getText().toString();
            Theme.saveCurrentTheme(this.themeInfo, true, true, true);
        } else {
            String currentName = "";
            String currentUrl = this.themeInfo.info.slug == null ? currentName : this.themeInfo.info.slug;
            if (this.themeInfo.name != null) {
                currentName = this.themeInfo.name;
            }
            String newUrl = this.linkField.getText().toString();
            String newName = this.nameField.getText().toString();
            if (!currentUrl.equals(newUrl) || !currentName.equals(newName)) {
                this.progressDialog = new AlertDialog(getParentActivity(), 3);
                TLRPC.TL_account_updateTheme req = new TLRPC.TL_account_updateTheme();
                TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
                inputTheme.id = this.themeInfo.info.id;
                inputTheme.access_hash = this.themeInfo.info.access_hash;
                req.theme = inputTheme;
                req.format = "android";
                req.slug = newUrl;
                req.flags = 1 | req.flags;
                req.title = newName;
                req.flags |= 2;
                int reqId = ConnectionsManager.getInstance(this.themeInfo.account).sendRequest(req, new RequestDelegate(req) {
                    private final /* synthetic */ TLRPC.TL_account_updateTheme f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ThemeSetUrlActivity.this.lambda$saveTheme$12$ThemeSetUrlActivity(this.f$1, tLObject, tL_error);
                    }
                }, 2);
                ConnectionsManager.getInstance(this.themeInfo.account).bindRequestToGuid(reqId, this.classGuid);
                this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ThemeSetUrlActivity.this.lambda$saveTheme$13$ThemeSetUrlActivity(this.f$1, dialogInterface);
                    }
                });
                this.progressDialog.show();
                return;
            }
            finishFragment();
        }
    }

    static /* synthetic */ void lambda$saveTheme$9(DialogInterface dialog) {
    }

    public /* synthetic */ void lambda$saveTheme$12$ThemeSetUrlActivity(TLRPC.TL_account_updateTheme req, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_theme) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_theme) response) {
                private final /* synthetic */ TLRPC.TL_theme f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ThemeSetUrlActivity.this.lambda$null$10$ThemeSetUrlActivity(this.f$1);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(error, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLRPC.TL_account_updateTheme f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ThemeSetUrlActivity.this.lambda$null$11$ThemeSetUrlActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$10$ThemeSetUrlActivity(TLRPC.TL_theme theme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Theme.setThemeUploadInfo(this.themeInfo, theme, false);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$11$ThemeSetUrlActivity(TLRPC.TL_error error, TLRPC.TL_account_updateTheme req) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertsCreator.processError(this.themeInfo.account, error, this, req, new Object[0]);
    }

    public /* synthetic */ void lambda$saveTheme$13$ThemeSetUrlActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.themeInfo.account).cancelRequest(reqId, true);
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription(this.createInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.createInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.helpInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.helpInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGreenText), new ThemeDescription((View) this.createCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.linkField, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.nameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.nameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.nameField, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.divider, 0, (Class[]) null, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.divider, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubble), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleSelected), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleShadow), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubble), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleSelected), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleShadow), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextIn), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextOut), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheck), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckSelected), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckRead), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckReadSelected), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaSentCheck), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyLine), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyLine), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyNameText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyNameText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMessageText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMessageText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMediaMessageSelectedText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMediaMessageSelectedText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeSelectedText), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeSelectedText)};
    }
}
