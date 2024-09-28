package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import com.blankj.utilcode.constant.TimeConstants;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MrzRecognizer;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SRPHelper;
import im.bclpbkiauv.messenger.SecureDocument;
import im.bclpbkiauv.messenger.SecureDocumentKey;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.CountrySelectActivity;
import im.bclpbkiauv.ui.DocumentSelectActivity;
import im.bclpbkiauv.ui.MrzCameraActivity;
import im.bclpbkiauv.ui.PassportActivity;
import im.bclpbkiauv.ui.PhotoAlbumPickerActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextDetailSettingsCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ChatAttachAlert;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgress;
import im.bclpbkiauv.ui.components.SlideView;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import kotlin.UByte;
import kotlin.text.Typography;
import org.json.JSONObject;
import org.slf4j.Marker;

public class PassportActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int FIELD_ADDRESS_COUNT = 6;
    private static final int FIELD_BIRTHDAY = 3;
    private static final int FIELD_CARDNUMBER = 7;
    private static final int FIELD_CITIZENSHIP = 5;
    private static final int FIELD_CITY = 3;
    private static final int FIELD_COUNTRY = 5;
    private static final int FIELD_EMAIL = 0;
    private static final int FIELD_EXPIRE = 8;
    private static final int FIELD_GENDER = 4;
    private static final int FIELD_IDENTITY_COUNT = 9;
    private static final int FIELD_IDENTITY_NODOC_COUNT = 7;
    private static final int FIELD_MIDNAME = 1;
    private static final int FIELD_NAME = 0;
    private static final int FIELD_NATIVE_COUNT = 3;
    private static final int FIELD_NATIVE_MIDNAME = 1;
    private static final int FIELD_NATIVE_NAME = 0;
    private static final int FIELD_NATIVE_SURNAME = 2;
    private static final int FIELD_PASSWORD = 0;
    private static final int FIELD_PHONE = 2;
    private static final int FIELD_PHONECODE = 1;
    private static final int FIELD_PHONECOUNTRY = 0;
    private static final int FIELD_POSTCODE = 2;
    private static final int FIELD_RESIDENCE = 6;
    private static final int FIELD_STATE = 4;
    private static final int FIELD_STREET1 = 0;
    private static final int FIELD_STREET2 = 1;
    private static final int FIELD_SURNAME = 2;
    public static final int TYPE_ADDRESS = 2;
    public static final int TYPE_EMAIL = 4;
    public static final int TYPE_EMAIL_VERIFICATION = 6;
    public static final int TYPE_IDENTITY = 1;
    public static final int TYPE_MANAGE = 8;
    public static final int TYPE_PASSWORD = 5;
    public static final int TYPE_PHONE = 3;
    public static final int TYPE_PHONE_VERIFICATION = 7;
    public static final int TYPE_REQUEST = 0;
    private static final int UPLOADING_TYPE_DOCUMENTS = 0;
    private static final int UPLOADING_TYPE_FRONT = 2;
    private static final int UPLOADING_TYPE_REVERSE = 3;
    private static final int UPLOADING_TYPE_SELFIE = 1;
    private static final int UPLOADING_TYPE_TRANSLATION = 4;
    private static final int attach_document = 4;
    private static final int attach_gallery = 1;
    private static final int attach_photo = 0;
    private static final int done_button = 2;
    private static final int info_item = 1;
    /* access modifiers changed from: private */
    public TextView acceptTextView;
    private TextSettingsCell addDocumentCell;
    private ShadowSectionCell addDocumentSectionCell;
    /* access modifiers changed from: private */
    public boolean allowNonLatinName;
    private ArrayList<TLRPC.TL_secureRequiredType> availableDocumentTypes;
    private TextInfoPrivacyCell bottomCell;
    private TextInfoPrivacyCell bottomCellTranslation;
    private FrameLayout bottomLayout;
    private boolean callbackCalled;
    /* access modifiers changed from: private */
    public ChatAttachAlert chatAttachAlert;
    /* access modifiers changed from: private */
    public HashMap<String, String> codesMap;
    /* access modifiers changed from: private */
    public ArrayList<String> countriesArray;
    private HashMap<String, String> countriesMap;
    /* access modifiers changed from: private */
    public int currentActivityType;
    /* access modifiers changed from: private */
    public int currentBotId;
    /* access modifiers changed from: private */
    public String currentCallbackUrl;
    /* access modifiers changed from: private */
    public String currentCitizeship;
    /* access modifiers changed from: private */
    public HashMap<String, String> currentDocumentValues;
    /* access modifiers changed from: private */
    public TLRPC.TL_secureRequiredType currentDocumentsType;
    private TLRPC.TL_secureValue currentDocumentsTypeValue;
    /* access modifiers changed from: private */
    public String currentEmail;
    /* access modifiers changed from: private */
    public int[] currentExpireDate;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_authorizationForm currentForm;
    /* access modifiers changed from: private */
    public String currentGender;
    /* access modifiers changed from: private */
    public String currentNonce;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_password currentPassword;
    /* access modifiers changed from: private */
    public String currentPayload;
    private TLRPC.TL_auth_sentCode currentPhoneVerification;
    /* access modifiers changed from: private */
    public LinearLayout currentPhotoViewerLayout;
    private String currentPicturePath;
    /* access modifiers changed from: private */
    public String currentPublicKey;
    /* access modifiers changed from: private */
    public String currentResidence;
    /* access modifiers changed from: private */
    public String currentScope;
    /* access modifiers changed from: private */
    public TLRPC.TL_secureRequiredType currentType;
    private TLRPC.TL_secureValue currentTypeValue;
    /* access modifiers changed from: private */
    public HashMap<String, String> currentValues;
    /* access modifiers changed from: private */
    public int currentViewNum;
    /* access modifiers changed from: private */
    public PassportActivityDelegate delegate;
    private TextSettingsCell deletePassportCell;
    private ArrayList<View> dividers;
    /* access modifiers changed from: private */
    public boolean documentOnly;
    /* access modifiers changed from: private */
    public ArrayList<SecureDocument> documents;
    /* access modifiers changed from: private */
    public HashMap<SecureDocument, SecureDocumentCell> documentsCells;
    /* access modifiers changed from: private */
    public HashMap<String, String> documentsErrors;
    private LinearLayout documentsLayout;
    private HashMap<TLRPC.TL_secureRequiredType, TLRPC.TL_secureRequiredType> documentsToTypesLink;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    /* access modifiers changed from: private */
    public int emailCodeLength;
    private ImageView emptyImageView;
    private LinearLayout emptyLayout;
    private TextView emptyTextView1;
    private TextView emptyTextView2;
    private TextView emptyTextView3;
    private EmptyTextProgressView emptyView;
    private HashMap<String, HashMap<String, String>> errorsMap;
    /* access modifiers changed from: private */
    public HashMap<String, String> errorsValues;
    private View extraBackgroundView;
    private View extraBackgroundView2;
    /* access modifiers changed from: private */
    public HashMap<String, String> fieldsErrors;
    /* access modifiers changed from: private */
    public SecureDocument frontDocument;
    private LinearLayout frontLayout;
    private HeaderCell headerCell;
    /* access modifiers changed from: private */
    public boolean ignoreOnFailure;
    /* access modifiers changed from: private */
    public boolean ignoreOnPhoneChange;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    private String initialValues;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputExtraFields;
    /* access modifiers changed from: private */
    public ViewGroup[] inputFieldContainers;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputFields;
    private HashMap<String, String> languageMap;
    private LinearLayout linearLayout2;
    private HashMap<String, String> mainErrorsMap;
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell nativeInfoCell;
    /* access modifiers changed from: private */
    public boolean needActivityResult;
    private CharSequence noAllDocumentsErrorText;
    private CharSequence noAllTranslationErrorText;
    private ImageView noPasswordImageView;
    private TextView noPasswordSetTextView;
    private TextView noPasswordTextView;
    /* access modifiers changed from: private */
    public boolean[] nonLatinNames;
    private FrameLayout passwordAvatarContainer;
    private TextView passwordForgotButton;
    private TextInfoPrivacyCell passwordInfoRequestTextView;
    private TextInfoPrivacyCell passwordRequestTextView;
    private PassportActivityDelegate pendingDelegate;
    private ErrorRunnable pendingErrorRunnable;
    private Runnable pendingFinishRunnable;
    private String pendingPhone;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    /* access modifiers changed from: private */
    public HashMap<String, String> phoneFormatMap;
    private TextView plusTextView;
    /* access modifiers changed from: private */
    public PassportActivity presentAfterAnimation;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public ContextProgressView progressViewButton;
    private PhotoViewer.PhotoViewerProvider provider;
    /* access modifiers changed from: private */
    public SecureDocument reverseDocument;
    /* access modifiers changed from: private */
    public LinearLayout reverseLayout;
    /* access modifiers changed from: private */
    public byte[] saltedPassword;
    private byte[] savedPasswordHash;
    /* access modifiers changed from: private */
    public byte[] savedSaltedPassword;
    private TextSettingsCell scanDocumentCell;
    /* access modifiers changed from: private */
    public int scrollHeight;
    private ScrollView scrollView;
    private ShadowSectionCell sectionCell;
    private ShadowSectionCell sectionCell2;
    /* access modifiers changed from: private */
    public byte[] secureSecret;
    /* access modifiers changed from: private */
    public long secureSecretId;
    /* access modifiers changed from: private */
    public SecureDocument selfieDocument;
    private LinearLayout selfieLayout;
    private TextInfoPrivacyCell topErrorCell;
    /* access modifiers changed from: private */
    public ArrayList<SecureDocument> translationDocuments;
    private LinearLayout translationLayout;
    private HashMap<TLRPC.TL_secureRequiredType, HashMap<String, String>> typesValues;
    private HashMap<TLRPC.TL_secureRequiredType, TextDetailSecureCell> typesViews;
    private TextSettingsCell uploadDocumentCell;
    private TextDetailSettingsCell uploadFrontCell;
    private TextDetailSettingsCell uploadReverseCell;
    private TextDetailSettingsCell uploadSelfieCell;
    private TextSettingsCell uploadTranslationCell;
    /* access modifiers changed from: private */
    public HashMap<String, SecureDocument> uploadingDocuments;
    /* access modifiers changed from: private */
    public int uploadingFileType;
    /* access modifiers changed from: private */
    public boolean useCurrentValue;
    /* access modifiers changed from: private */
    public int usingSavedPassword;
    /* access modifiers changed from: private */
    public SlideView[] views;

    private interface ErrorRunnable {
        void onError(String str, String str2);
    }

    private interface PassportActivityDelegate {
        void deleteValue(TLRPC.TL_secureRequiredType tL_secureRequiredType, TLRPC.TL_secureRequiredType tL_secureRequiredType2, ArrayList<TLRPC.TL_secureRequiredType> arrayList, boolean z, Runnable runnable, ErrorRunnable errorRunnable);

        SecureDocument saveFile(TLRPC.TL_secureFile tL_secureFile);

        void saveValue(TLRPC.TL_secureRequiredType tL_secureRequiredType, String str, String str2, TLRPC.TL_secureRequiredType tL_secureRequiredType2, String str3, ArrayList<SecureDocument> arrayList, SecureDocument secureDocument, ArrayList<SecureDocument> arrayList2, SecureDocument secureDocument2, SecureDocument secureDocument3, Runnable runnable, ErrorRunnable errorRunnable);
    }

    public class LinkSpan extends ClickableSpan {
        public LinkSpan() {
        }

        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(true);
            ds.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }

        public void onClick(View widget) {
            Browser.openUrl((Context) PassportActivity.this.getParentActivity(), PassportActivity.this.currentForm.privacy_policy_url);
        }
    }

    public class TextDetailSecureCell extends FrameLayout {
        private ImageView checkImageView;
        private boolean needDivider;
        private TextView textView;
        final /* synthetic */ PassportActivity this$0;
        /* access modifiers changed from: private */
        public TextView valueTextView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public TextDetailSecureCell(im.bclpbkiauv.ui.PassportActivity r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                r2 = r18
                r0.this$0 = r2
                r0.<init>(r1)
                int r3 = r18.currentActivityType
                r4 = 21
                r5 = 8
                if (r3 != r5) goto L_0x0018
                r3 = 21
                goto L_0x001a
            L_0x0018:
                r3 = 51
            L_0x001a:
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r1)
                r0.textView = r5
                java.lang.String r6 = "windowBackgroundWhiteBlackText"
                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.textView
                r6 = 1098907648(0x41800000, float:16.0)
                r7 = 1
                r5.setTextSize(r7, r6)
                android.widget.TextView r5 = r0.textView
                r5.setLines(r7)
                android.widget.TextView r5 = r0.textView
                r5.setMaxLines(r7)
                android.widget.TextView r5 = r0.textView
                r5.setSingleLine(r7)
                android.widget.TextView r5 = r0.textView
                android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
                r5.setEllipsize(r6)
                android.widget.TextView r5 = r0.textView
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r8 = 5
                r9 = 3
                if (r6 == 0) goto L_0x0053
                r6 = 5
                goto L_0x0054
            L_0x0053:
                r6 = 3
            L_0x0054:
                r6 = r6 | 16
                r5.setGravity(r6)
                android.widget.TextView r5 = r0.textView
                r10 = -1073741824(0xffffffffc0000000, float:-2.0)
                r11 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x0065
                r6 = 5
                goto L_0x0066
            L_0x0065:
                r6 = 3
            L_0x0066:
                r12 = r6 | 48
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x006e
                r6 = r3
                goto L_0x0070
            L_0x006e:
                r6 = 21
            L_0x0070:
                float r13 = (float) r6
                r14 = 1092616192(0x41200000, float:10.0)
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x007a
                r6 = 21
                goto L_0x007b
            L_0x007a:
                r6 = r3
            L_0x007b:
                float r15 = (float) r6
                r16 = 0
                android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r5, r6)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r1)
                r0.valueTextView = r5
                java.lang.String r6 = "windowBackgroundWhiteGrayText2"
                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.valueTextView
                r6 = 1095761920(0x41500000, float:13.0)
                r5.setTextSize(r7, r6)
                android.widget.TextView r5 = r0.valueTextView
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x00a5
                r6 = 5
                goto L_0x00a6
            L_0x00a5:
                r6 = 3
            L_0x00a6:
                r5.setGravity(r6)
                android.widget.TextView r5 = r0.valueTextView
                r5.setLines(r7)
                android.widget.TextView r5 = r0.valueTextView
                r5.setMaxLines(r7)
                android.widget.TextView r5 = r0.valueTextView
                r5.setSingleLine(r7)
                android.widget.TextView r5 = r0.valueTextView
                android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
                r5.setEllipsize(r6)
                android.widget.TextView r5 = r0.valueTextView
                r6 = 0
                r5.setPadding(r6, r6, r6, r6)
                android.widget.TextView r5 = r0.valueTextView
                r10 = -1073741824(0xffffffffc0000000, float:-2.0)
                r11 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x00d1
                r6 = 5
                goto L_0x00d2
            L_0x00d1:
                r6 = 3
            L_0x00d2:
                r12 = r6 | 48
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x00da
                r6 = r3
                goto L_0x00dc
            L_0x00da:
                r6 = 21
            L_0x00dc:
                float r13 = (float) r6
                r14 = 1108082688(0x420c0000, float:35.0)
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x00e4
                goto L_0x00e5
            L_0x00e4:
                r4 = r3
            L_0x00e5:
                float r15 = (float) r4
                r16 = 0
                android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r5, r4)
                android.widget.ImageView r4 = new android.widget.ImageView
                r4.<init>(r1)
                r0.checkImageView = r4
                android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
                java.lang.String r6 = "featuredStickers_addedIcon"
                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.SRC_IN
                r5.<init>(r6, r7)
                r4.setColorFilter(r5)
                android.widget.ImageView r4 = r0.checkImageView
                r5 = 2131558612(0x7f0d00d4, float:1.8742545E38)
                r4.setImageResource(r5)
                android.widget.ImageView r4 = r0.checkImageView
                r10 = -1073741824(0xffffffffc0000000, float:-2.0)
                r11 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0119
                r8 = 3
            L_0x0119:
                r12 = r8 | 48
                r13 = 1101529088(0x41a80000, float:21.0)
                r14 = 1103626240(0x41c80000, float:25.0)
                r15 = 1101529088(0x41a80000, float:21.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r4, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell.<init>(im.bclpbkiauv.ui.PassportActivity, android.content.Context):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void setTextAndValue(String text, CharSequence value, boolean divider) {
            this.textView.setText(text);
            this.valueTextView.setText(value);
            this.needDivider = divider;
            setWillNotDraw(!divider);
        }

        public void setChecked(boolean checked) {
            this.checkImageView.setVisibility(checked ? 0 : 4);
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        public void setNeedDivider(boolean value) {
            this.needDivider = value;
            setWillNotDraw(!value);
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public class SecureDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
        private int TAG;
        private int buttonState;
        private SecureDocument currentSecureDocument;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        private RadialProgress radialProgress = new RadialProgress(this);
        private TextView textView;
        final /* synthetic */ PassportActivity this$0;
        /* access modifiers changed from: private */
        public TextView valueTextView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public SecureDocumentCell(im.bclpbkiauv.ui.PassportActivity r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                r2 = r18
                r0.this$0 = r2
                r0.<init>(r1)
                int r3 = r18.currentAccount
                im.bclpbkiauv.messenger.DownloadController r3 = im.bclpbkiauv.messenger.DownloadController.getInstance(r3)
                int r3 = r3.generateObserverTag()
                r0.TAG = r3
                im.bclpbkiauv.ui.components.RadialProgress r3 = new im.bclpbkiauv.ui.components.RadialProgress
                r3.<init>(r0)
                r0.radialProgress = r3
                im.bclpbkiauv.ui.components.BackupImageView r3 = new im.bclpbkiauv.ui.components.BackupImageView
                r3.<init>(r1)
                r0.imageView = r3
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r5 = 5
                r6 = 3
                if (r4 == 0) goto L_0x002f
                r4 = 5
                goto L_0x0030
            L_0x002f:
                r4 = 3
            L_0x0030:
                r9 = r4 | 48
                r10 = 1101529088(0x41a80000, float:21.0)
                r11 = 1090519040(0x41000000, float:8.0)
                r12 = 1101529088(0x41a80000, float:21.0)
                r13 = 0
                r7 = 1111490560(0x42400000, float:48.0)
                r8 = 1111490560(0x42400000, float:48.0)
                android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
                r0.addView(r3, r4)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.textView = r3
                java.lang.String r4 = "windowBackgroundWhiteBlackText"
                int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.textView
                r4 = 1098907648(0x41800000, float:16.0)
                r7 = 1
                r3.setTextSize(r7, r4)
                android.widget.TextView r3 = r0.textView
                r3.setLines(r7)
                android.widget.TextView r3 = r0.textView
                r3.setMaxLines(r7)
                android.widget.TextView r3 = r0.textView
                r3.setSingleLine(r7)
                android.widget.TextView r3 = r0.textView
                android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
                r3.setEllipsize(r4)
                android.widget.TextView r3 = r0.textView
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x007b
                r4 = 5
                goto L_0x007c
            L_0x007b:
                r4 = 3
            L_0x007c:
                r4 = r4 | 16
                r3.setGravity(r4)
                android.widget.TextView r3 = r0.textView
                r8 = -1073741824(0xffffffffc0000000, float:-2.0)
                r9 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x008d
                r4 = 5
                goto L_0x008e
            L_0x008d:
                r4 = 3
            L_0x008e:
                r10 = r4 | 48
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r15 = 21
                r16 = 81
                if (r4 == 0) goto L_0x009b
                r4 = 21
                goto L_0x009d
            L_0x009b:
                r4 = 81
            L_0x009d:
                float r11 = (float) r4
                r12 = 1092616192(0x41200000, float:10.0)
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00a7
                r4 = 81
                goto L_0x00a9
            L_0x00a7:
                r4 = 21
            L_0x00a9:
                float r13 = (float) r4
                r14 = 0
                android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r4)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.valueTextView = r3
                java.lang.String r4 = "windowBackgroundWhiteGrayText2"
                int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.valueTextView
                r4 = 1095761920(0x41500000, float:13.0)
                r3.setTextSize(r7, r4)
                android.widget.TextView r3 = r0.valueTextView
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x00d2
                r4 = 5
                goto L_0x00d3
            L_0x00d2:
                r4 = 3
            L_0x00d3:
                r3.setGravity(r4)
                android.widget.TextView r3 = r0.valueTextView
                r3.setLines(r7)
                android.widget.TextView r3 = r0.valueTextView
                r3.setMaxLines(r7)
                android.widget.TextView r3 = r0.valueTextView
                r3.setSingleLine(r7)
                android.widget.TextView r3 = r0.valueTextView
                r4 = 0
                r3.setPadding(r4, r4, r4, r4)
                android.widget.TextView r3 = r0.valueTextView
                r7 = -1073741824(0xffffffffc0000000, float:-2.0)
                r8 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00f6
                goto L_0x00f7
            L_0x00f6:
                r5 = 3
            L_0x00f7:
                r9 = r5 | 48
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0100
                r5 = 21
                goto L_0x0102
            L_0x0100:
                r5 = 81
            L_0x0102:
                float r10 = (float) r5
                r11 = 1108082688(0x420c0000, float:35.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x010b
                r15 = 81
            L_0x010b:
                float r12 = (float) r15
                r13 = 0
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
                r0.addView(r3, r5)
                r0.setWillNotDraw(r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.SecureDocumentCell.<init>(im.bclpbkiauv.ui.PassportActivity, android.content.Context):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, 1073741824));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int x = this.imageView.getLeft() + ((this.imageView.getMeasuredWidth() - AndroidUtilities.dp(24.0f)) / 2);
            int y = this.imageView.getTop() + ((this.imageView.getMeasuredHeight() - AndroidUtilities.dp(24.0f)) / 2);
            this.radialProgress.setProgressRect(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean result = super.drawChild(canvas, child, drawingTime);
            if (child == this.imageView) {
                this.radialProgress.draw(canvas);
            }
            return result;
        }

        public void setTextAndValueAndImage(String text, CharSequence value, SecureDocument document) {
            this.textView.setText(text);
            this.valueTextView.setText(value);
            this.imageView.setImage(document, "48_48");
            this.currentSecureDocument = document;
            updateButtonState(false);
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentSecureDocument);
            boolean fileExists = FileLoader.getPathToAttach(this.currentSecureDocument).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setBackground((Drawable) null, false, false);
                return;
            }
            float f = 0.0f;
            if (this.currentSecureDocument.path != null) {
                if (this.currentSecureDocument.inputFile != null) {
                    DownloadController.getInstance(this.this$0.currentAccount).removeLoadingFileObserver(this);
                    this.radialProgress.setBackground((Drawable) null, false, animated);
                    this.buttonState = -1;
                    return;
                }
                DownloadController.getInstance(this.this$0.currentAccount).addLoadingFileObserver(this.currentSecureDocument.path, this);
                this.buttonState = 1;
                Float progress = ImageLoader.getInstance().getFileProgress(this.currentSecureDocument.path);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, animated);
                RadialProgress radialProgress2 = this.radialProgress;
                if (progress != null) {
                    f = progress.floatValue();
                }
                radialProgress2.setProgress(f, false);
                invalidate();
            } else if (fileExists) {
                DownloadController.getInstance(this.this$0.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setBackground((Drawable) null, false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(this.this$0.currentAccount).addLoadingFileObserver(fileName, this);
                this.buttonState = 1;
                Float progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                this.radialProgress.setBackground(Theme.chat_photoStatesDrawables[5][0], true, animated);
                RadialProgress radialProgress3 = this.radialProgress;
                if (progress2 != null) {
                    f = progress2.floatValue();
                }
                radialProgress3.setProgress(f, animated);
                invalidate();
            }
        }

        public void invalidate() {
            super.invalidate();
            this.textView.invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
            this.radialProgress.setProgress(progress, true);
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        */
    public PassportActivity(int r23, int r24, java.lang.String r25, java.lang.String r26, java.lang.String r27, java.lang.String r28, java.lang.String r29, im.bclpbkiauv.tgnet.TLRPC.TL_account_authorizationForm r30, im.bclpbkiauv.tgnet.TLRPC.TL_account_password r31) {
        /*
            r22 = this;
            r11 = r22
            r12 = r30
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r1 = r22
            r2 = r23
            r3 = r30
            r4 = r31
            r1.<init>((int) r2, (im.bclpbkiauv.tgnet.TLRPC.TL_account_authorizationForm) r3, (im.bclpbkiauv.tgnet.TLRPC.TL_account_password) r4, (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r5, (im.bclpbkiauv.tgnet.TLRPC.TL_secureValue) r6, (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r7, (im.bclpbkiauv.tgnet.TLRPC.TL_secureValue) r8, (java.util.HashMap<java.lang.String, java.lang.String>) r9, (java.util.HashMap<java.lang.String, java.lang.String>) r10)
            r1 = r24
            r11.currentBotId = r1
            r2 = r27
            r11.currentPayload = r2
            r3 = r28
            r11.currentNonce = r3
            r4 = r25
            r11.currentScope = r4
            r5 = r26
            r11.currentPublicKey = r5
            r6 = r29
            r11.currentCallbackUrl = r6
            if (r23 != 0) goto L_0x02a3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureValueError> r0 = r12.errors
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x02a3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureValueError> r0 = r12.errors     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.ui.PassportActivity$2 r7 = new im.bclpbkiauv.ui.PassportActivity$2     // Catch:{ Exception -> 0x02a2 }
            r7.<init>()     // Catch:{ Exception -> 0x02a2 }
            java.util.Collections.sort(r0, r7)     // Catch:{ Exception -> 0x02a2 }
            r0 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureValueError> r7 = r12.errors     // Catch:{ Exception -> 0x02a2 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x02a2 }
        L_0x0048:
            if (r0 >= r7) goto L_0x029f
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureValueError> r8 = r12.errors     // Catch:{ Exception -> 0x02a2 }
            java.lang.Object r8 = r8.get(r0)     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueError r8 = (im.bclpbkiauv.tgnet.TLRPC.SecureValueError) r8     // Catch:{ Exception -> 0x02a2 }
            r9 = 0
            r10 = 0
            boolean r13 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorFrontSide     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r14 = "data"
            java.lang.String r15 = "error_all"
            java.lang.String r1 = "selfie"
            java.lang.String r2 = "reverse"
            java.lang.String r3 = "front"
            java.lang.String r4 = "files"
            java.lang.String r5 = "translation"
            if (r13 == 0) goto L_0x0087
            r13 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorFrontSide r13 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorFrontSide) r13     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r6 = r13.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r6 = r11.getNameForType(r6)     // Catch:{ Exception -> 0x02a2 }
            r16 = r6
            java.lang.String r6 = r13.text     // Catch:{ Exception -> 0x02a2 }
            r17 = r6
            byte[] r6 = r13.file_hash     // Catch:{ Exception -> 0x02a2 }
            r10 = r6
            r6 = r3
            r18 = r8
            r13 = r17
            r21 = r7
            r7 = r6
            r6 = r16
            r16 = r21
            goto L_0x019d
        L_0x0087:
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorReverseSide     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x00a6
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorReverseSide r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorReverseSide) r6     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r13 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r11.getNameForType(r13)     // Catch:{ Exception -> 0x02a2 }
            r16 = r7
            java.lang.String r7 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r17 = r7
            byte[] r7 = r6.file_hash     // Catch:{ Exception -> 0x02a2 }
            r10 = r7
            r6 = r2
            r7 = r6
            r18 = r8
            r6 = r13
            r13 = r17
            goto L_0x019d
        L_0x00a6:
            r16 = r7
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorSelfie     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x00c4
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorSelfie r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorSelfie) r6     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r7 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r7 = r11.getNameForType(r7)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r17 = r7
            byte[] r7 = r6.file_hash     // Catch:{ Exception -> 0x02a2 }
            r10 = r7
            r6 = r1
            r7 = r6
            r18 = r8
            r6 = r17
            goto L_0x019d
        L_0x00c4:
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorTranslationFile     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x00e0
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorTranslationFile r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorTranslationFile) r6     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r7 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r7 = r11.getNameForType(r7)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r17 = r7
            byte[] r7 = r6.file_hash     // Catch:{ Exception -> 0x02a2 }
            r10 = r7
            r6 = r5
            r7 = r6
            r18 = r8
            r6 = r17
            goto L_0x019d
        L_0x00e0:
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorTranslationFiles     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x00f9
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorTranslationFiles r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorTranslationFiles) r6     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r7 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r7 = r11.getNameForType(r7)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r6 = r5
            r18 = r8
            r21 = r7
            r7 = r6
            r6 = r21
            goto L_0x019d
        L_0x00f9:
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorFile     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x0115
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorFile r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorFile) r6     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r7 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r7 = r11.getNameForType(r7)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r17 = r7
            byte[] r7 = r6.file_hash     // Catch:{ Exception -> 0x02a2 }
            r10 = r7
            r6 = r4
            r7 = r6
            r18 = r8
            r6 = r17
            goto L_0x019d
        L_0x0115:
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorFiles     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x012e
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorFiles r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorFiles) r6     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r7 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r7 = r11.getNameForType(r7)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r6 = r4
            r18 = r8
            r21 = r7
            r7 = r6
            r6 = r21
            goto L_0x019d
        L_0x012e:
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueError     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x0149
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueError r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueError) r6     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r7 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r7 = r11.getNameForType(r7)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r17 = r7
            byte[] r7 = r6.hash     // Catch:{ Exception -> 0x02a2 }
            r10 = r7
            r6 = r15
            r7 = r6
            r18 = r8
            r6 = r17
            goto L_0x019d
        L_0x0149:
            boolean r6 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorData     // Catch:{ Exception -> 0x02a2 }
            if (r6 == 0) goto L_0x0289
            r6 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueErrorData r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValueErrorData) r6     // Catch:{ Exception -> 0x02a2 }
            r7 = 0
            r13 = 0
        L_0x0152:
            r17 = r7
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_secureValue> r7 = r12.values     // Catch:{ Exception -> 0x02a2 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x02a2 }
            if (r13 >= r7) goto L_0x0183
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_secureValue> r7 = r12.values     // Catch:{ Exception -> 0x02a2 }
            java.lang.Object r7 = r7.get(r13)     // Catch:{ Exception -> 0x02a2 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r7 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureValue) r7     // Catch:{ Exception -> 0x02a2 }
            r18 = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r8 = r7.data     // Catch:{ Exception -> 0x02a2 }
            if (r8 == 0) goto L_0x017a
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r8 = r7.data     // Catch:{ Exception -> 0x02a2 }
            byte[] r8 = r8.data_hash     // Catch:{ Exception -> 0x02a2 }
            r19 = r7
            byte[] r7 = r6.data_hash     // Catch:{ Exception -> 0x02a2 }
            boolean r7 = java.util.Arrays.equals(r8, r7)     // Catch:{ Exception -> 0x02a2 }
            if (r7 == 0) goto L_0x017c
            r7 = 1
            goto L_0x0187
        L_0x017a:
            r19 = r7
        L_0x017c:
            int r13 = r13 + 1
            r7 = r17
            r8 = r18
            goto L_0x0152
        L_0x0183:
            r18 = r8
            r7 = r17
        L_0x0187:
            if (r7 != 0) goto L_0x018b
            goto L_0x028b
        L_0x018b:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r8 = r6.type     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r8 = r11.getNameForType(r8)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r13 = r6.text     // Catch:{ Exception -> 0x02a2 }
            r17 = r7
            java.lang.String r7 = r6.field     // Catch:{ Exception -> 0x02a2 }
            r9 = r7
            byte[] r7 = r6.data_hash     // Catch:{ Exception -> 0x02a2 }
            r10 = r7
            r7 = r14
            r6 = r8
        L_0x019d:
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r8 = r11.errorsMap     // Catch:{ Exception -> 0x02a2 }
            java.lang.Object r8 = r8.get(r6)     // Catch:{ Exception -> 0x02a2 }
            java.util.HashMap r8 = (java.util.HashMap) r8     // Catch:{ Exception -> 0x02a2 }
            if (r8 != 0) goto L_0x01b8
            java.util.HashMap r17 = new java.util.HashMap     // Catch:{ Exception -> 0x02a2 }
            r17.<init>()     // Catch:{ Exception -> 0x02a2 }
            r8 = r17
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r12 = r11.errorsMap     // Catch:{ Exception -> 0x02a2 }
            r12.put(r6, r8)     // Catch:{ Exception -> 0x02a2 }
            java.util.HashMap<java.lang.String, java.lang.String> r12 = r11.mainErrorsMap     // Catch:{ Exception -> 0x02a2 }
            r12.put(r6, r13)     // Catch:{ Exception -> 0x02a2 }
        L_0x01b8:
            r12 = 2
            if (r10 == 0) goto L_0x01c2
            java.lang.String r17 = android.util.Base64.encodeToString(r10, r12)     // Catch:{ Exception -> 0x02a2 }
            r12 = r17
            goto L_0x01c6
        L_0x01c2:
            java.lang.String r17 = ""
            r12 = r17
        L_0x01c6:
            r17 = -1
            int r20 = r7.hashCode()     // Catch:{ Exception -> 0x02a2 }
            switch(r20) {
                case -1840647503: goto L_0x0200;
                case -906020504: goto L_0x01f8;
                case 3076010: goto L_0x01f0;
                case 97434231: goto L_0x01e8;
                case 97705513: goto L_0x01e0;
                case 329856746: goto L_0x01d8;
                case 1099846370: goto L_0x01d0;
                default: goto L_0x01cf;
            }     // Catch:{ Exception -> 0x02a2 }
        L_0x01cf:
            goto L_0x0208
        L_0x01d0:
            boolean r14 = r7.equals(r2)     // Catch:{ Exception -> 0x02a2 }
            if (r14 == 0) goto L_0x01cf
            r14 = 5
            goto L_0x0209
        L_0x01d8:
            boolean r14 = r7.equals(r15)     // Catch:{ Exception -> 0x02a2 }
            if (r14 == 0) goto L_0x01cf
            r14 = 6
            goto L_0x0209
        L_0x01e0:
            boolean r14 = r7.equals(r3)     // Catch:{ Exception -> 0x02a2 }
            if (r14 == 0) goto L_0x01cf
            r14 = 4
            goto L_0x0209
        L_0x01e8:
            boolean r14 = r7.equals(r4)     // Catch:{ Exception -> 0x02a2 }
            if (r14 == 0) goto L_0x01cf
            r14 = 1
            goto L_0x0209
        L_0x01f0:
            boolean r14 = r7.equals(r14)     // Catch:{ Exception -> 0x02a2 }
            if (r14 == 0) goto L_0x01cf
            r14 = 0
            goto L_0x0209
        L_0x01f8:
            boolean r14 = r7.equals(r1)     // Catch:{ Exception -> 0x02a2 }
            if (r14 == 0) goto L_0x01cf
            r14 = 2
            goto L_0x0209
        L_0x0200:
            boolean r14 = r7.equals(r5)     // Catch:{ Exception -> 0x02a2 }
            if (r14 == 0) goto L_0x01cf
            r14 = 3
            goto L_0x0209
        L_0x0208:
            r14 = -1
        L_0x0209:
            switch(r14) {
                case 0: goto L_0x0283;
                case 1: goto L_0x0268;
                case 2: goto L_0x0255;
                case 3: goto L_0x0239;
                case 4: goto L_0x0226;
                case 5: goto L_0x0213;
                case 6: goto L_0x020e;
                default: goto L_0x020c;
            }     // Catch:{ Exception -> 0x02a2 }
        L_0x020c:
            goto L_0x028b
        L_0x020e:
            r8.put(r15, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x0213:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a2 }
            r1.<init>()     // Catch:{ Exception -> 0x02a2 }
            r1.append(r2)     // Catch:{ Exception -> 0x02a2 }
            r1.append(r12)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x02a2 }
            r8.put(r1, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x0226:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a2 }
            r1.<init>()     // Catch:{ Exception -> 0x02a2 }
            r1.append(r3)     // Catch:{ Exception -> 0x02a2 }
            r1.append(r12)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x02a2 }
            r8.put(r1, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x0239:
            if (r10 == 0) goto L_0x024e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a2 }
            r1.<init>()     // Catch:{ Exception -> 0x02a2 }
            r1.append(r5)     // Catch:{ Exception -> 0x02a2 }
            r1.append(r12)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x02a2 }
            r8.put(r1, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x024e:
            java.lang.String r1 = "translation_all"
            r8.put(r1, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x0255:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a2 }
            r2.<init>()     // Catch:{ Exception -> 0x02a2 }
            r2.append(r1)     // Catch:{ Exception -> 0x02a2 }
            r2.append(r12)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x02a2 }
            r8.put(r1, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x0268:
            if (r10 == 0) goto L_0x027d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a2 }
            r1.<init>()     // Catch:{ Exception -> 0x02a2 }
            r1.append(r4)     // Catch:{ Exception -> 0x02a2 }
            r1.append(r12)     // Catch:{ Exception -> 0x02a2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x02a2 }
            r8.put(r1, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x027d:
            java.lang.String r1 = "files_all"
            r8.put(r1, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x0283:
            if (r9 == 0) goto L_0x028b
            r8.put(r9, r13)     // Catch:{ Exception -> 0x02a2 }
            goto L_0x028b
        L_0x0289:
            r18 = r8
        L_0x028b:
            int r0 = r0 + 1
            r1 = r24
            r4 = r25
            r5 = r26
            r2 = r27
            r3 = r28
            r6 = r29
            r12 = r30
            r7 = r16
            goto L_0x0048
        L_0x029f:
            r16 = r7
            goto L_0x02a3
        L_0x02a2:
            r0 = move-exception
        L_0x02a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.<init>(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm, im.bclpbkiauv.tgnet.TLRPC$TL_account_password):void");
    }

    public PassportActivity(int type, TLRPC.TL_account_authorizationForm form, TLRPC.TL_account_password accountPassword, TLRPC.TL_secureRequiredType secureType, TLRPC.TL_secureValue secureValue, TLRPC.TL_secureRequiredType secureDocumentsType, TLRPC.TL_secureValue secureDocumentsValue, HashMap<String, String> values, HashMap<String, String> documentValues) {
        this.currentCitizeship = "";
        this.currentResidence = "";
        this.currentExpireDate = new int[3];
        this.dividers = new ArrayList<>();
        this.nonLatinNames = new boolean[3];
        this.allowNonLatinName = true;
        this.countriesArray = new ArrayList<>();
        this.countriesMap = new HashMap<>();
        this.codesMap = new HashMap<>();
        this.phoneFormatMap = new HashMap<>();
        this.documents = new ArrayList<>();
        this.translationDocuments = new ArrayList<>();
        this.documentsCells = new HashMap<>();
        this.uploadingDocuments = new HashMap<>();
        this.typesValues = new HashMap<>();
        this.typesViews = new HashMap<>();
        this.documentsToTypesLink = new HashMap<>();
        this.errorsMap = new HashMap<>();
        this.mainErrorsMap = new HashMap<>();
        this.errorsValues = new HashMap<>();
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                if (index < 0 || index >= PassportActivity.this.currentPhotoViewerLayout.getChildCount()) {
                    return null;
                }
                SecureDocumentCell cell = (SecureDocumentCell) PassportActivity.this.currentPhotoViewerLayout.getChildAt(index);
                int[] coords = new int[2];
                cell.imageView.getLocationInWindow(coords);
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                int i = 0;
                object.viewX = coords[0];
                int i2 = coords[1];
                if (Build.VERSION.SDK_INT < 21) {
                    i = AndroidUtilities.statusBarHeight;
                }
                object.viewY = i2 - i;
                object.parentView = PassportActivity.this.currentPhotoViewerLayout;
                object.imageReceiver = cell.imageView.getImageReceiver();
                object.thumb = object.imageReceiver.getBitmapSafe();
                return object;
            }

            public void deleteImageAtIndex(int index) {
                SecureDocument document;
                if (PassportActivity.this.uploadingFileType == 1) {
                    document = PassportActivity.this.selfieDocument;
                } else if (PassportActivity.this.uploadingFileType == 4) {
                    document = (SecureDocument) PassportActivity.this.translationDocuments.get(index);
                } else if (PassportActivity.this.uploadingFileType == 2) {
                    document = PassportActivity.this.frontDocument;
                } else if (PassportActivity.this.uploadingFileType == 3) {
                    document = PassportActivity.this.reverseDocument;
                } else {
                    document = (SecureDocument) PassportActivity.this.documents.get(index);
                }
                SecureDocumentCell cell = (SecureDocumentCell) PassportActivity.this.documentsCells.remove(document);
                if (cell != null) {
                    String key = null;
                    String hash = PassportActivity.this.getDocumentHash(document);
                    if (PassportActivity.this.uploadingFileType == 1) {
                        SecureDocument unused = PassportActivity.this.selfieDocument = null;
                        key = "selfie" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 4) {
                        key = "translation" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 2) {
                        SecureDocument unused2 = PassportActivity.this.frontDocument = null;
                        key = "front" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 3) {
                        SecureDocument unused3 = PassportActivity.this.reverseDocument = null;
                        key = "reverse" + hash;
                    } else if (PassportActivity.this.uploadingFileType == 0) {
                        key = "files" + hash;
                    }
                    if (key != null) {
                        if (PassportActivity.this.documentsErrors != null) {
                            PassportActivity.this.documentsErrors.remove(key);
                        }
                        if (PassportActivity.this.errorsValues != null) {
                            PassportActivity.this.errorsValues.remove(key);
                        }
                    }
                    PassportActivity passportActivity = PassportActivity.this;
                    passportActivity.updateUploadText(passportActivity.uploadingFileType);
                    PassportActivity.this.currentPhotoViewerLayout.removeView(cell);
                }
            }

            public String getDeleteMessageString() {
                if (PassportActivity.this.uploadingFileType == 1) {
                    return LocaleController.formatString("PassportDeleteSelfieAlert", R.string.PassportDeleteSelfieAlert, new Object[0]);
                }
                return LocaleController.formatString("PassportDeleteScanAlert", R.string.PassportDeleteScanAlert, new Object[0]);
            }
        };
        this.currentActivityType = type;
        this.currentForm = form;
        this.currentType = secureType;
        if (secureType != null) {
            this.allowNonLatinName = secureType.native_names;
        }
        this.currentTypeValue = secureValue;
        this.currentDocumentsType = secureDocumentsType;
        this.currentDocumentsTypeValue = secureDocumentsValue;
        this.currentPassword = accountPassword;
        this.currentValues = values;
        this.currentDocumentValues = documentValues;
        int i = this.currentActivityType;
        if (i == 3) {
            this.permissionsItems = new ArrayList<>();
        } else if (i == 7) {
            this.views = new SlideView[3];
        }
        if (this.currentValues == null) {
            this.currentValues = new HashMap<>();
        }
        if (this.currentDocumentValues == null) {
            this.currentDocumentValues = new HashMap<>();
        }
        if (type == 5) {
            if (!(UserConfig.getInstance(this.currentAccount).savedPasswordHash == null || UserConfig.getInstance(this.currentAccount).savedSaltedPassword == null)) {
                this.usingSavedPassword = 1;
                this.savedPasswordHash = UserConfig.getInstance(this.currentAccount).savedPasswordHash;
                this.savedSaltedPassword = UserConfig.getInstance(this.currentAccount).savedSaltedPassword;
            }
            TLRPC.TL_account_password tL_account_password = this.currentPassword;
            if (tL_account_password == null) {
                loadPasswordInfo();
            } else {
                TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
                if (this.usingSavedPassword == 1) {
                    onPasswordDone(true);
                }
            }
            if (!SharedConfig.isPassportConfigLoaded()) {
                TLRPC.TL_help_getPassportConfig req = new TLRPC.TL_help_getPassportConfig();
                req.hash = SharedConfig.passportConfigHash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, $$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0.INSTANCE);
            }
        }
    }

    static /* synthetic */ void lambda$null$0(TLObject response) {
        if (response instanceof TLRPC.TL_help_passportConfig) {
            TLRPC.TL_help_passportConfig res = (TLRPC.TL_help_passportConfig) response;
            SharedConfig.setPassportConfig(res.countries_langs.data, res.hash);
            return;
        }
        SharedConfig.getCountryLangs();
    }

    public void onResume() {
        ViewGroup[] viewGroupArr;
        super.onResume();
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.onResume();
        }
        if (this.currentActivityType == 5 && (viewGroupArr = this.inputFieldContainers) != null && viewGroupArr[0] != null && viewGroupArr[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PassportActivity.this.lambda$onResume$2$PassportActivity();
                }
            }, 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public /* synthetic */ void lambda$onResume$2$PassportActivity() {
        ViewGroup[] viewGroupArr = this.inputFieldContainers;
        if (viewGroupArr != null && viewGroupArr[0] != null && viewGroupArr[0].getVisibility() == 0) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public void onPause() {
        super.onPause();
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.onPause();
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didRemoveTwoStepPassword);
        callCallback(false);
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
        if (this.currentActivityType == 7) {
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a >= slideViewArr.length) {
                    break;
                }
                if (slideViewArr[a] != null) {
                    slideViewArr[a].onDestroyActivity();
                }
                a++;
            }
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.progressDialog = null;
            }
        }
    }

    public View createView(Context context) {
        ChatAttachAlert chatAttachAlert2;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            private boolean onIdentityDone(Runnable finishRunnable, ErrorRunnable errorRunnable) {
                String str;
                String str2;
                String str3;
                char c = 0;
                if (!PassportActivity.this.uploadingDocuments.isEmpty() || PassportActivity.this.checkFieldsForError()) {
                    return false;
                }
                int i = 3;
                char c2 = 2;
                char c3 = 1;
                if (PassportActivity.this.allowNonLatinName) {
                    boolean unused = PassportActivity.this.allowNonLatinName = false;
                    boolean error = false;
                    int a = 0;
                    while (a < PassportActivity.this.nonLatinNames.length) {
                        if (PassportActivity.this.nonLatinNames[a]) {
                            PassportActivity.this.inputFields[a].setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                            if (!error) {
                                error = true;
                                if (PassportActivity.this.nonLatinNames[c]) {
                                    PassportActivity passportActivity = PassportActivity.this;
                                    str = passportActivity.getTranslitString(passportActivity.inputExtraFields[c].getText().toString());
                                } else {
                                    str = PassportActivity.this.inputFields[c].getText().toString();
                                }
                                String firstName = str;
                                if (PassportActivity.this.nonLatinNames[c3]) {
                                    PassportActivity passportActivity2 = PassportActivity.this;
                                    str2 = passportActivity2.getTranslitString(passportActivity2.inputExtraFields[c3].getText().toString());
                                } else {
                                    str2 = PassportActivity.this.inputFields[c3].getText().toString();
                                }
                                String middleName = str2;
                                if (PassportActivity.this.nonLatinNames[c2]) {
                                    PassportActivity passportActivity3 = PassportActivity.this;
                                    str3 = passportActivity3.getTranslitString(passportActivity3.inputExtraFields[c2].getText().toString());
                                } else {
                                    str3 = PassportActivity.this.inputFields[c2].getText().toString();
                                }
                                String lastName = str3;
                                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(middleName) || TextUtils.isEmpty(lastName)) {
                                    PassportActivity passportActivity4 = PassportActivity.this;
                                    passportActivity4.onFieldError(passportActivity4.inputFields[a]);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) PassportActivity.this.getParentActivity());
                                    Object[] objArr = new Object[i];
                                    objArr[c] = firstName;
                                    objArr[c3] = middleName;
                                    objArr[c2] = lastName;
                                    builder.setMessage(LocaleController.formatString("PassportNameCheckAlert", R.string.PassportNameCheckAlert, objArr));
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    $$Lambda$PassportActivity$3$iaoSR1WNzrAYGUmDoVjCnZf0J2M r10 = r1;
                                    String string = LocaleController.getString("Done", R.string.Done);
                                    AlertDialog.Builder builder2 = builder;
                                    $$Lambda$PassportActivity$3$iaoSR1WNzrAYGUmDoVjCnZf0J2M r1 = new DialogInterface.OnClickListener(firstName, middleName, lastName, finishRunnable, errorRunnable) {
                                        private final /* synthetic */ String f$1;
                                        private final /* synthetic */ String f$2;
                                        private final /* synthetic */ String f$3;
                                        private final /* synthetic */ Runnable f$4;
                                        private final /* synthetic */ PassportActivity.ErrorRunnable f$5;

                                        {
                                            this.f$1 = r2;
                                            this.f$2 = r3;
                                            this.f$3 = r4;
                                            this.f$4 = r5;
                                            this.f$5 = r6;
                                        }

                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            PassportActivity.AnonymousClass3.this.lambda$onIdentityDone$0$PassportActivity$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
                                        }
                                    };
                                    builder2.setPositiveButton(string, r10);
                                    builder2.setNegativeButton(LocaleController.getString("Edit", R.string.Edit), new DialogInterface.OnClickListener(a) {
                                        private final /* synthetic */ int f$1;

                                        {
                                            this.f$1 = r2;
                                        }

                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            PassportActivity.AnonymousClass3.this.lambda$onIdentityDone$1$PassportActivity$3(this.f$1, dialogInterface, i);
                                        }
                                    });
                                    PassportActivity.this.showDialog(builder2.create());
                                }
                            }
                        }
                        a++;
                        c = 0;
                        i = 3;
                        c2 = 2;
                        c3 = 1;
                    }
                    if (error) {
                        return false;
                    }
                }
                if (PassportActivity.this.isHasNotAnyChanges()) {
                    PassportActivity.this.finishFragment();
                    return false;
                }
                JSONObject json = null;
                JSONObject documentsJson = null;
                try {
                    if (!PassportActivity.this.documentOnly) {
                        HashMap<String, String> valuesToSave = new HashMap<>(PassportActivity.this.currentValues);
                        if (PassportActivity.this.currentType.native_names) {
                            if (PassportActivity.this.nativeInfoCell.getVisibility() == 0) {
                                valuesToSave.put("first_name_native", PassportActivity.this.inputExtraFields[0].getText().toString());
                                valuesToSave.put("middle_name_native", PassportActivity.this.inputExtraFields[1].getText().toString());
                                valuesToSave.put("last_name_native", PassportActivity.this.inputExtraFields[2].getText().toString());
                            } else {
                                valuesToSave.put("first_name_native", PassportActivity.this.inputFields[0].getText().toString());
                                valuesToSave.put("middle_name_native", PassportActivity.this.inputFields[1].getText().toString());
                                valuesToSave.put("last_name_native", PassportActivity.this.inputFields[2].getText().toString());
                            }
                        }
                        valuesToSave.put("first_name", PassportActivity.this.inputFields[0].getText().toString());
                        valuesToSave.put("middle_name", PassportActivity.this.inputFields[1].getText().toString());
                        valuesToSave.put("last_name", PassportActivity.this.inputFields[2].getText().toString());
                        valuesToSave.put("birth_date", PassportActivity.this.inputFields[3].getText().toString());
                        valuesToSave.put("gender", PassportActivity.this.currentGender);
                        valuesToSave.put("country_code", PassportActivity.this.currentCitizeship);
                        valuesToSave.put("residence_country_code", PassportActivity.this.currentResidence);
                        json = new JSONObject();
                        ArrayList<String> keys = new ArrayList<>(valuesToSave.keySet());
                        Collections.sort(keys, new Comparator() {
                            public final int compare(Object obj, Object obj2) {
                                return PassportActivity.AnonymousClass3.this.lambda$onIdentityDone$2$PassportActivity$3((String) obj, (String) obj2);
                            }
                        });
                        int size = keys.size();
                        for (int a2 = 0; a2 < size; a2++) {
                            String key = keys.get(a2);
                            json.put(key, valuesToSave.get(key));
                        }
                    }
                    if (PassportActivity.this.currentDocumentsType != null) {
                        HashMap<String, String> valuesToSave2 = new HashMap<>(PassportActivity.this.currentDocumentValues);
                        valuesToSave2.put("document_no", PassportActivity.this.inputFields[7].getText().toString());
                        if (PassportActivity.this.currentExpireDate[0] != 0) {
                            valuesToSave2.put("expiry_date", String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(PassportActivity.this.currentExpireDate[2]), Integer.valueOf(PassportActivity.this.currentExpireDate[1]), Integer.valueOf(PassportActivity.this.currentExpireDate[0])}));
                        } else {
                            valuesToSave2.put("expiry_date", "");
                        }
                        documentsJson = new JSONObject();
                        ArrayList<String> keys2 = new ArrayList<>(valuesToSave2.keySet());
                        Collections.sort(keys2, new Comparator() {
                            public final int compare(Object obj, Object obj2) {
                                return PassportActivity.AnonymousClass3.this.lambda$onIdentityDone$3$PassportActivity$3((String) obj, (String) obj2);
                            }
                        });
                        int size2 = keys2.size();
                        for (int a3 = 0; a3 < size2; a3++) {
                            String key2 = keys2.get(a3);
                            documentsJson.put(key2, valuesToSave2.get(key2));
                        }
                    }
                } catch (Exception e) {
                }
                if (PassportActivity.this.fieldsErrors != null) {
                    PassportActivity.this.fieldsErrors.clear();
                }
                if (PassportActivity.this.documentsErrors != null) {
                    PassportActivity.this.documentsErrors.clear();
                }
                PassportActivityDelegate access$4200 = PassportActivity.this.delegate;
                TLRPC.TL_secureRequiredType access$3200 = PassportActivity.this.currentType;
                SecureDocument secureDocument = null;
                String jSONObject = json != null ? json.toString() : null;
                TLRPC.TL_secureRequiredType access$3700 = PassportActivity.this.currentDocumentsType;
                String jSONObject2 = documentsJson != null ? documentsJson.toString() : null;
                SecureDocument access$300 = PassportActivity.this.selfieDocument;
                ArrayList access$400 = PassportActivity.this.translationDocuments;
                SecureDocument access$500 = PassportActivity.this.frontDocument;
                if (PassportActivity.this.reverseLayout != null && PassportActivity.this.reverseLayout.getVisibility() == 0) {
                    secureDocument = PassportActivity.this.reverseDocument;
                }
                access$4200.saveValue(access$3200, (String) null, jSONObject, access$3700, jSONObject2, (ArrayList<SecureDocument>) null, access$300, access$400, access$500, secureDocument, finishRunnable, errorRunnable);
                return true;
            }

            public /* synthetic */ void lambda$onIdentityDone$0$PassportActivity$3(String firstName, String middleName, String lastName, Runnable finishRunnable, ErrorRunnable errorRunnable, DialogInterface dialogInterface, int i) {
                PassportActivity.this.inputFields[0].setText(firstName);
                PassportActivity.this.inputFields[1].setText(middleName);
                PassportActivity.this.inputFields[2].setText(lastName);
                PassportActivity.this.showEditDoneProgress(true, true);
                onIdentityDone(finishRunnable, errorRunnable);
            }

            public /* synthetic */ void lambda$onIdentityDone$1$PassportActivity$3(int num, DialogInterface dialogInterface, int i) {
                PassportActivity passportActivity = PassportActivity.this;
                passportActivity.onFieldError(passportActivity.inputFields[num]);
            }

            public /* synthetic */ int lambda$onIdentityDone$2$PassportActivity$3(String key1, String key2) {
                int val1 = PassportActivity.this.getFieldCost(key1);
                int val2 = PassportActivity.this.getFieldCost(key2);
                if (val1 < val2) {
                    return -1;
                }
                if (val1 > val2) {
                    return 1;
                }
                return 0;
            }

            public /* synthetic */ int lambda$onIdentityDone$3$PassportActivity$3(String key1, String key2) {
                int val1 = PassportActivity.this.getFieldCost(key1);
                int val2 = PassportActivity.this.getFieldCost(key2);
                if (val1 < val2) {
                    return -1;
                }
                if (val1 > val2) {
                    return 1;
                }
                return 0;
            }

            public void onItemClick(int id) {
                JSONObject json;
                String value;
                String value2;
                int i = id;
                if (i != -1) {
                    String str = null;
                    if (i == 1) {
                        if (PassportActivity.this.getParentActivity() != null) {
                            TextView message = new TextView(PassportActivity.this.getParentActivity());
                            String str2 = LocaleController.getString("PassportInfo2", R.string.PassportInfo2);
                            SpannableStringBuilder spanned = new SpannableStringBuilder(str2);
                            int index1 = str2.indexOf(42);
                            int index2 = str2.lastIndexOf(42);
                            if (!(index1 == -1 || index2 == -1)) {
                                spanned.replace(index2, index2 + 1, "");
                                spanned.replace(index1, index1 + 1, "");
                                spanned.setSpan(new URLSpanNoUnderline(LocaleController.getString("PassportInfoUrl", R.string.PassportInfoUrl)) {
                                    public void onClick(View widget) {
                                        PassportActivity.this.dismissCurrentDialog();
                                        super.onClick(widget);
                                    }
                                }, index1, index2 - 1, 33);
                            }
                            message.setText(spanned);
                            message.setTextSize(1, 16.0f);
                            message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
                            message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
                            message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                            message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                            message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) PassportActivity.this.getParentActivity());
                            builder.setView(message);
                            builder.setTitle(LocaleController.getString("PassportInfoTitle", R.string.PassportInfoTitle));
                            builder.setNegativeButton(LocaleController.getString("Close", R.string.Close), (DialogInterface.OnClickListener) null);
                            PassportActivity.this.showDialog(builder.create());
                        }
                    } else if (i != 2) {
                    } else {
                        if (PassportActivity.this.currentActivityType == 5) {
                            PassportActivity.this.onPasswordDone(false);
                        } else if (PassportActivity.this.currentActivityType == 7) {
                            PassportActivity.this.views[PassportActivity.this.currentViewNum].onNextPressed();
                        } else {
                            final Runnable finishRunnable = new Runnable() {
                                public final void run() {
                                    PassportActivity.AnonymousClass3.this.lambda$onItemClick$4$PassportActivity$3();
                                }
                            };
                            ErrorRunnable errorRunnable = new ErrorRunnable() {
                                public void onError(String error, String text) {
                                    if ("PHONE_VERIFICATION_NEEDED".equals(error)) {
                                        PassportActivity.this.startPhoneVerification(true, text, finishRunnable, this, PassportActivity.this.delegate);
                                        return;
                                    }
                                    PassportActivity.this.showEditDoneProgress(true, false);
                                }
                            };
                            if (PassportActivity.this.currentActivityType == 4) {
                                if (PassportActivity.this.useCurrentValue) {
                                    value2 = PassportActivity.this.currentEmail;
                                } else if (!PassportActivity.this.checkFieldsForError()) {
                                    value2 = PassportActivity.this.inputFields[0].getText().toString();
                                } else {
                                    return;
                                }
                                PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value2, (String) null, (TLRPC.TL_secureRequiredType) null, (String) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (SecureDocument) null, finishRunnable, errorRunnable);
                            } else if (PassportActivity.this.currentActivityType == 3) {
                                if (PassportActivity.this.useCurrentValue) {
                                    value = UserConfig.getInstance(PassportActivity.this.currentAccount).getCurrentUser().phone;
                                } else if (!PassportActivity.this.checkFieldsForError()) {
                                    value = PassportActivity.this.inputFields[1].getText().toString() + PassportActivity.this.inputFields[2].getText().toString();
                                } else {
                                    return;
                                }
                                PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, value, (String) null, (TLRPC.TL_secureRequiredType) null, (String) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (SecureDocument) null, finishRunnable, errorRunnable);
                            } else if (PassportActivity.this.currentActivityType != 2) {
                                ErrorRunnable errorRunnable2 = errorRunnable;
                                Runnable finishRunnable2 = finishRunnable;
                                if (PassportActivity.this.currentActivityType == 1) {
                                    if (!onIdentityDone(finishRunnable2, errorRunnable2)) {
                                        return;
                                    }
                                } else if (PassportActivity.this.currentActivityType == 6) {
                                    TLRPC.TL_account_verifyEmail req = new TLRPC.TL_account_verifyEmail();
                                    req.email = (String) PassportActivity.this.currentValues.get("email");
                                    req.code = PassportActivity.this.inputFields[0].getText().toString();
                                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req, new RequestDelegate(finishRunnable2, errorRunnable2, req) {
                                        private final /* synthetic */ Runnable f$1;
                                        private final /* synthetic */ PassportActivity.ErrorRunnable f$2;
                                        private final /* synthetic */ TLRPC.TL_account_verifyEmail f$3;

                                        {
                                            this.f$1 = r2;
                                            this.f$2 = r3;
                                            this.f$3 = r4;
                                        }

                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                            PassportActivity.AnonymousClass3.this.lambda$onItemClick$6$PassportActivity$3(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                                        }
                                    }), PassportActivity.this.classGuid);
                                }
                            } else if (!PassportActivity.this.uploadingDocuments.isEmpty()) {
                                Runnable runnable = finishRunnable;
                                return;
                            } else if (PassportActivity.this.checkFieldsForError()) {
                                ErrorRunnable errorRunnable3 = errorRunnable;
                                Runnable runnable2 = finishRunnable;
                                return;
                            } else if (PassportActivity.this.isHasNotAnyChanges()) {
                                PassportActivity.this.finishFragment();
                                return;
                            } else {
                                JSONObject json2 = null;
                                try {
                                    if (!PassportActivity.this.documentOnly) {
                                        json2 = new JSONObject();
                                        json2.put("street_line1", PassportActivity.this.inputFields[0].getText().toString());
                                        json2.put("street_line2", PassportActivity.this.inputFields[1].getText().toString());
                                        json2.put("post_code", PassportActivity.this.inputFields[2].getText().toString());
                                        json2.put("city", PassportActivity.this.inputFields[3].getText().toString());
                                        json2.put(RemoteConfigConstants.ResponseFieldKey.STATE, PassportActivity.this.inputFields[4].getText().toString());
                                        json2.put("country_code", PassportActivity.this.currentCitizeship);
                                    }
                                    json = json2;
                                } catch (Exception e) {
                                    json = null;
                                }
                                if (PassportActivity.this.fieldsErrors != null) {
                                    PassportActivity.this.fieldsErrors.clear();
                                }
                                if (PassportActivity.this.documentsErrors != null) {
                                    PassportActivity.this.documentsErrors.clear();
                                }
                                PassportActivityDelegate access$4200 = PassportActivity.this.delegate;
                                TLRPC.TL_secureRequiredType access$3200 = PassportActivity.this.currentType;
                                if (json != null) {
                                    str = json.toString();
                                }
                                access$4200.saveValue(access$3200, (String) null, str, PassportActivity.this.currentDocumentsType, (String) null, PassportActivity.this.documents, PassportActivity.this.selfieDocument, PassportActivity.this.translationDocuments, (SecureDocument) null, (SecureDocument) null, finishRunnable, errorRunnable);
                            }
                            PassportActivity.this.showEditDoneProgress(true, true);
                        }
                    }
                } else if (!PassportActivity.this.checkDiscard()) {
                    if (PassportActivity.this.currentActivityType == 0 || PassportActivity.this.currentActivityType == 5) {
                        PassportActivity.this.callCallback(false);
                    }
                    PassportActivity.this.finishFragment();
                }
            }

            public /* synthetic */ void lambda$onItemClick$4$PassportActivity$3() {
                PassportActivity.this.finishFragment();
            }

            public /* synthetic */ void lambda$onItemClick$6$PassportActivity$3(Runnable finishRunnable, ErrorRunnable errorRunnable, TLRPC.TL_account_verifyEmail req, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable(error, finishRunnable, errorRunnable, req) {
                    private final /* synthetic */ TLRPC.TL_error f$1;
                    private final /* synthetic */ Runnable f$2;
                    private final /* synthetic */ PassportActivity.ErrorRunnable f$3;
                    private final /* synthetic */ TLRPC.TL_account_verifyEmail f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass3.this.lambda$null$5$PassportActivity$3(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }

            public /* synthetic */ void lambda$null$5$PassportActivity$3(TLRPC.TL_error error, Runnable finishRunnable, ErrorRunnable errorRunnable, TLRPC.TL_account_verifyEmail req) {
                TLRPC.TL_error tL_error = error;
                if (tL_error == null) {
                    PassportActivity.this.delegate.saveValue(PassportActivity.this.currentType, (String) PassportActivity.this.currentValues.get("email"), (String) null, (TLRPC.TL_secureRequiredType) null, (String) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (SecureDocument) null, finishRunnable, errorRunnable);
                    ErrorRunnable errorRunnable2 = errorRunnable;
                    TLRPC.TL_account_verifyEmail tL_account_verifyEmail = req;
                    return;
                }
                AlertsCreator.processError(PassportActivity.this.currentAccount, tL_error, PassportActivity.this, req, new Object[0]);
                errorRunnable.onError((String) null, (String) null);
            }
        });
        if (this.currentActivityType == 7) {
            AnonymousClass4 r0 = new ScrollView(context) {
                /* access modifiers changed from: protected */
                public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                    return false;
                }

                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    if (PassportActivity.this.currentViewNum == 1 || PassportActivity.this.currentViewNum == 2 || PassportActivity.this.currentViewNum == 4) {
                        rectangle.bottom += AndroidUtilities.dp(40.0f);
                    }
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int unused = PassportActivity.this.scrollHeight = View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(30.0f);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            };
            this.scrollView = r0;
            this.fragmentView = r0;
            this.scrollView.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        } else {
            this.fragmentView = new FrameLayout(context);
            FrameLayout frameLayout = (FrameLayout) this.fragmentView;
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            AnonymousClass5 r6 = new ScrollView(context) {
                /* access modifiers changed from: protected */
                public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
                    return false;
                }

                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                    rectangle.top += AndroidUtilities.dp(20.0f);
                    rectangle.bottom += AndroidUtilities.dp(50.0f);
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }
            };
            this.scrollView = r6;
            r6.setFillViewport(true);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
            frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.currentActivityType == 0 ? 48.0f : 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout2 = linearLayout;
            linearLayout.setOrientation(1);
            this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        }
        int i = this.currentActivityType;
        if (!(i == 0 || i == 8)) {
            this.doneItem = this.actionBar.createMenu().addItemWithWidth(2, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            contextProgressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
            this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            int i2 = this.currentActivityType;
            if ((i2 == 1 || i2 == 2) && (chatAttachAlert2 = this.chatAttachAlert) != null) {
                try {
                    if (chatAttachAlert2.isShowing()) {
                        this.chatAttachAlert.dismiss();
                    }
                } catch (Exception e) {
                }
                this.chatAttachAlert.onDestroy();
                this.chatAttachAlert = null;
            }
        }
        int i3 = this.currentActivityType;
        if (i3 == 5) {
            createPasswordInterface(context);
        } else if (i3 == 0) {
            createRequestInterface(context);
        } else if (i3 == 1) {
            createIdentityInterface(context);
            fillInitialValues();
        } else if (i3 == 2) {
            createAddressInterface(context);
            fillInitialValues();
        } else if (i3 == 3) {
            createPhoneInterface(context);
        } else if (i3 == 4) {
            createEmailInterface(context);
        } else if (i3 == 6) {
            createEmailVerificationInterface(context);
        } else if (i3 == 7) {
            createPhoneVerificationInterface(context);
        } else if (i3 == 8) {
            createManageInterface(context);
        }
        return this.fragmentView;
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert && super.dismissDialogOnPause(dialog);
    }

    public void dismissCurrentDialog() {
        ChatAttachAlert chatAttachAlert2;
        if (this.chatAttachAlert == null || this.visibleDialog != (chatAttachAlert2 = this.chatAttachAlert)) {
            super.dismissCurrentDialog();
            return;
        }
        chatAttachAlert2.closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.hideCamera(true);
    }

    /* access modifiers changed from: private */
    public String getTranslitString(String value) {
        return LocaleController.getInstance().getTranslitString(value, true);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getFieldCost(java.lang.String r2) {
        /*
            r1 = this;
            int r0 = r2.hashCode()
            switch(r0) {
                case -2006252145: goto L_0x00bb;
                case -1537298398: goto L_0x00b1;
                case -1249512767: goto L_0x00a7;
                case -796150911: goto L_0x009b;
                case -796150910: goto L_0x008f;
                case -160985414: goto L_0x0085;
                case 3053931: goto L_0x007a;
                case 109757585: goto L_0x006e;
                case 421072629: goto L_0x0064;
                case 451516732: goto L_0x005a;
                case 475919162: goto L_0x004e;
                case 506677093: goto L_0x0042;
                case 1168724782: goto L_0x0037;
                case 1181577377: goto L_0x002c;
                case 1481071862: goto L_0x0020;
                case 2002465324: goto L_0x0014;
                case 2013122196: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x00c6
        L_0x0009:
            java.lang.String r0 = "last_name"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 4
            goto L_0x00c7
        L_0x0014:
            java.lang.String r0 = "post_code"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 14
            goto L_0x00c7
        L_0x0020:
            java.lang.String r0 = "country_code"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 8
            goto L_0x00c7
        L_0x002c:
            java.lang.String r0 = "middle_name_native"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 3
            goto L_0x00c7
        L_0x0037:
            java.lang.String r0 = "birth_date"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 6
            goto L_0x00c7
        L_0x0042:
            java.lang.String r0 = "document_no"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 10
            goto L_0x00c7
        L_0x004e:
            java.lang.String r0 = "expiry_date"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 11
            goto L_0x00c7
        L_0x005a:
            java.lang.String r0 = "first_name_native"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x00c7
        L_0x0064:
            java.lang.String r0 = "middle_name"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x00c7
        L_0x006e:
            java.lang.String r0 = "state"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 16
            goto L_0x00c7
        L_0x007a:
            java.lang.String r0 = "city"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 15
            goto L_0x00c7
        L_0x0085:
            java.lang.String r0 = "first_name"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x00c7
        L_0x008f:
            java.lang.String r0 = "street_line2"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 13
            goto L_0x00c7
        L_0x009b:
            java.lang.String r0 = "street_line1"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 12
            goto L_0x00c7
        L_0x00a7:
            java.lang.String r0 = "gender"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 7
            goto L_0x00c7
        L_0x00b1:
            java.lang.String r0 = "last_name_native"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 5
            goto L_0x00c7
        L_0x00bb:
            java.lang.String r0 = "residence_country_code"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 9
            goto L_0x00c7
        L_0x00c6:
            r0 = -1
        L_0x00c7:
            switch(r0) {
                case 0: goto L_0x00f4;
                case 1: goto L_0x00f4;
                case 2: goto L_0x00f1;
                case 3: goto L_0x00f1;
                case 4: goto L_0x00ee;
                case 5: goto L_0x00ee;
                case 6: goto L_0x00eb;
                case 7: goto L_0x00e8;
                case 8: goto L_0x00e5;
                case 9: goto L_0x00e2;
                case 10: goto L_0x00df;
                case 11: goto L_0x00dc;
                case 12: goto L_0x00d9;
                case 13: goto L_0x00d6;
                case 14: goto L_0x00d3;
                case 15: goto L_0x00d0;
                case 16: goto L_0x00cd;
                default: goto L_0x00ca;
            }
        L_0x00ca:
            r0 = 100
            return r0
        L_0x00cd:
            r0 = 33
            return r0
        L_0x00d0:
            r0 = 32
            return r0
        L_0x00d3:
            r0 = 31
            return r0
        L_0x00d6:
            r0 = 30
            return r0
        L_0x00d9:
            r0 = 29
            return r0
        L_0x00dc:
            r0 = 28
            return r0
        L_0x00df:
            r0 = 27
            return r0
        L_0x00e2:
            r0 = 26
            return r0
        L_0x00e5:
            r0 = 25
            return r0
        L_0x00e8:
            r0 = 24
            return r0
        L_0x00eb:
            r0 = 23
            return r0
        L_0x00ee:
            r0 = 22
            return r0
        L_0x00f1:
            r0 = 21
            return r0
        L_0x00f4:
            r0 = 20
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.getFieldCost(java.lang.String):int");
    }

    private void createPhoneVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportPhone", R.string.PassportPhone));
        FrameLayout frameLayout = new FrameLayout(context);
        this.scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        for (int a = 0; a < 3; a++) {
            this.views[a] = new PhoneConfirmationView(this, context, a + 2);
            this.views[a].setVisibility(8);
            SlideView slideView = this.views[a];
            float f = 18.0f;
            float f2 = AndroidUtilities.isTablet() ? 26.0f : 18.0f;
            if (AndroidUtilities.isTablet()) {
                f = 26.0f;
            }
            frameLayout.addView(slideView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, f2, 30.0f, f, 0.0f));
        }
        Bundle params = new Bundle();
        params.putString("phone", this.currentValues.get("phone"));
        fillNextCodeParams(params, this.currentPhoneVerification, false);
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.lambda$loadPasswordInfo$4$PassportActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadPasswordInfo$4$PassportActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PassportActivity.this.lambda$null$3$PassportActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$PassportActivity(TLObject response) {
        if (response != null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            updatePasswordInterface();
            if (this.inputFieldContainers[0].getVisibility() == 0) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
            if (this.usingSavedPassword == 1) {
                onPasswordDone(true);
            }
        }
    }

    private void createEmailVerificationInterface(Context context) {
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable((Drawable) null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            int i = 3;
            this.inputFields[a].setInputType(3);
            this.inputFields[a].setImeOptions(268435462);
            this.inputFields[a].setHint(LocaleController.getString("PassportEmailCode", R.string.PassportEmailCode));
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            EditTextBoldCursor editTextBoldCursor = this.inputFields[a];
            if (LocaleController.isRTL) {
                i = 5;
            }
            editTextBoldCursor.setGravity(i);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1.0f, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return PassportActivity.this.lambda$createEmailVerificationInterface$5$PassportActivity(textView, i, keyEvent);
                }
            });
            this.inputFields[a].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!PassportActivity.this.ignoreOnTextChange && PassportActivity.this.emailCodeLength != 0 && PassportActivity.this.inputFields[0].length() == PassportActivity.this.emailCodeLength) {
                        PassportActivity.this.doneItem.callOnClick();
                    }
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.bottomCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.formatString("PassportEmailVerifyInfo", R.string.PassportEmailVerifyInfo, this.currentValues.get("email")));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    public /* synthetic */ boolean lambda$createEmailVerificationInterface$5$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    private void createPasswordInterface(Context context) {
        Context context2 = context;
        TLRPC.User botUser = null;
        if (this.currentForm != null) {
            int a = 0;
            while (true) {
                if (a >= this.currentForm.users.size()) {
                    break;
                }
                TLRPC.User user = this.currentForm.users.get(a);
                if (user.id == this.currentBotId) {
                    botUser = user;
                    break;
                }
                a++;
            }
        } else {
            botUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.actionBar.setTitle(LocaleController.getString("AppPassport", R.string.AppPassport));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        ((FrameLayout) this.fragmentView).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.passwordAvatarContainer = frameLayout;
        this.linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, 100));
        BackupImageView avatarImageView = new BackupImageView(context2);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.passwordAvatarContainer.addView(avatarImageView, LayoutHelper.createFrame(64.0f, 64.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
        avatarImageView.setImage(ImageLocation.getForUser(botUser, false), "50_50", (Drawable) new AvatarDrawable(botUser), (Object) botUser);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.passwordRequestTextView = textInfoPrivacyCell;
        textInfoPrivacyCell.getTextView().setGravity(1);
        if (this.currentBotId == 0) {
            this.passwordRequestTextView.setText(LocaleController.getString("PassportSelfRequest", R.string.PassportSelfRequest));
        } else {
            this.passwordRequestTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PassportRequest", R.string.PassportRequest, UserObject.getFirstName(botUser))));
        }
        ((FrameLayout.LayoutParams) this.passwordRequestTextView.getTextView().getLayoutParams()).gravity = 1;
        int i = 5;
        this.linearLayout2.addView(this.passwordRequestTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.noPasswordImageView = imageView;
        imageView.setImageResource(R.drawable.no_password);
        this.noPasswordImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        this.linearLayout2.addView(this.noPasswordImageView, LayoutHelper.createLinear(-2, -2, 49, 0, 13, 0, 0));
        TextView textView = new TextView(context2);
        this.noPasswordTextView = textView;
        textView.setTextSize(1, 14.0f);
        this.noPasswordTextView.setGravity(1);
        this.noPasswordTextView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(17.0f));
        this.noPasswordTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.noPasswordTextView.setText(LocaleController.getString("AppPassportCreatePasswordInfo", R.string.AppPassportCreatePasswordInfo));
        this.linearLayout2.addView(this.noPasswordTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 10.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.noPasswordSetTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText5));
        this.noPasswordSetTextView.setGravity(17);
        this.noPasswordSetTextView.setTextSize(1, 16.0f);
        this.noPasswordSetTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.noPasswordSetTextView.setText(LocaleController.getString("AppPassportCreatePassword", R.string.AppPassportCreatePassword));
        this.linearLayout2.addView(this.noPasswordSetTextView, LayoutHelper.createFrame(-1.0f, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 9.0f, 21.0f, 0.0f));
        this.noPasswordSetTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PassportActivity.this.lambda$createPasswordInterface$6$PassportActivity(view);
            }
        });
        this.inputFields = new EditTextBoldCursor[1];
        this.inputFieldContainers = new ViewGroup[1];
        for (int a2 = 0; a2 < 1; a2++) {
            this.inputFieldContainers[a2] = new FrameLayout(context2);
            this.linearLayout2.addView(this.inputFieldContainers[a2], LayoutHelper.createLinear(-1, 50));
            this.inputFieldContainers[a2].setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a2] = new EditTextBoldCursor(context2);
            this.inputFields[a2].setTag(Integer.valueOf(a2));
            this.inputFields[a2].setTextSize(1, 16.0f);
            this.inputFields[a2].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setBackgroundDrawable((Drawable) null);
            this.inputFields[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a2].setCursorWidth(1.5f);
            this.inputFields[a2].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
            this.inputFields[a2].setMaxLines(1);
            this.inputFields[a2].setLines(1);
            this.inputFields[a2].setSingleLine(true);
            this.inputFields[a2].setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.inputFields[a2].setTypeface(Typeface.DEFAULT);
            this.inputFields[a2].setImeOptions(268435462);
            this.inputFields[a2].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a2].setGravity(LocaleController.isRTL ? 5 : 3);
            this.inputFieldContainers[a2].addView(this.inputFields[a2], LayoutHelper.createFrame(-1.0f, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return PassportActivity.this.lambda$createPasswordInterface$7$PassportActivity(textView, i, keyEvent);
                }
            });
            this.inputFields[a2].setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.passwordInfoRequestTextView = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.passwordInfoRequestTextView.setText(LocaleController.formatString("PassportRequestPasswordInfo", R.string.PassportRequestPasswordInfo, new Object[0]));
        this.linearLayout2.addView(this.passwordInfoRequestTextView, LayoutHelper.createLinear(-1, -2));
        TextView textView3 = new TextView(context2);
        this.passwordForgotButton = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.passwordForgotButton.setTextSize(1, 14.0f);
        this.passwordForgotButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
        this.passwordForgotButton.setPadding(0, 0, 0, 0);
        LinearLayout linearLayout = this.linearLayout2;
        TextView textView4 = this.passwordForgotButton;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout.addView(textView4, LayoutHelper.createLinear(-2, 30, i | 48, 21, 0, 21, 0));
        this.passwordForgotButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PassportActivity.this.lambda$createPasswordInterface$12$PassportActivity(view);
            }
        });
        updatePasswordInterface();
    }

    public /* synthetic */ void lambda$createPasswordInterface$6$PassportActivity(View v) {
        TwoStepVerificationActivity activity = new TwoStepVerificationActivity(this.currentAccount, 1);
        activity.setCloseAfterSet(true);
        activity.setCurrentPasswordInfo(new byte[0], this.currentPassword);
        presentFragment(activity);
    }

    public /* synthetic */ boolean lambda$createPasswordInterface$7$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    public /* synthetic */ void lambda$createPasswordInterface$12$PassportActivity(View v) {
        if (this.currentPassword.has_recovery) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.this.lambda$null$10$PassportActivity(tLObject, tL_error);
                }
            }, 10), this.classGuid);
        } else if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", R.string.RestorePasswordResetAccount), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.lambda$null$11$PassportActivity(dialogInterface, i);
                }
            });
            builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$10$PassportActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PassportActivity.this.lambda$null$9$PassportActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$PassportActivity(TLRPC.TL_error error, TLObject response) {
        String timeString;
        needHideProgress();
        if (error == null) {
            TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(res) {
                private final /* synthetic */ TLRPC.TL_auth_passwordRecovery f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.lambda$null$8$PassportActivity(this.f$1, dialogInterface, i);
                }
            });
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    public /* synthetic */ void lambda$null$8$PassportActivity(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
        fragment.setRecoveryParams(this.currentPassword);
        this.currentPassword.email_unconfirmed_pattern = res.email_pattern;
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$null$11$PassportActivity(DialogInterface dialog, int which) {
        FragmentActivity parentActivity = getParentActivity();
        Browser.openUrl((Context) parentActivity, "https://m12345.com/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    /* access modifiers changed from: private */
    public void onPasswordDone(boolean saved) {
        String textPassword;
        if (saved) {
            textPassword = null;
        } else {
            textPassword = this.inputFields[0].getText().toString();
            if (TextUtils.isEmpty(textPassword)) {
                onPasscodeError(false);
                return;
            }
            showEditDoneProgress(true, true);
        }
        Utilities.globalQueue.postRunnable(new Runnable(saved, textPassword) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PassportActivity.this.lambda$onPasswordDone$13$PassportActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$onPasswordDone$13$PassportActivity(boolean saved, String textPassword) {
        byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (saved) {
            x_bytes = this.savedPasswordHash;
        } else if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(AndroidUtilities.getStringBytes(textPassword), (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        final boolean z = saved;
        final byte[] bArr = x_bytes;
        final TLRPC.TL_account_getPasswordSettings tL_account_getPasswordSettings = req;
        final String str = textPassword;
        RequestDelegate requestDelegate = new RequestDelegate() {
            private void openRequestInterface() {
                int type;
                if (PassportActivity.this.inputFields != null) {
                    if (!z) {
                        UserConfig.getInstance(PassportActivity.this.currentAccount).savePassword(bArr, PassportActivity.this.saltedPassword);
                    }
                    AndroidUtilities.hideKeyboard(PassportActivity.this.inputFields[0]);
                    boolean unused = PassportActivity.this.ignoreOnFailure = true;
                    if (PassportActivity.this.currentBotId == 0) {
                        type = 8;
                    } else {
                        type = 0;
                    }
                    PassportActivity activity = new PassportActivity(type, PassportActivity.this.currentBotId, PassportActivity.this.currentScope, PassportActivity.this.currentPublicKey, PassportActivity.this.currentPayload, PassportActivity.this.currentNonce, PassportActivity.this.currentCallbackUrl, PassportActivity.this.currentForm, PassportActivity.this.currentPassword);
                    String unused2 = activity.currentEmail = PassportActivity.this.currentEmail;
                    int unused3 = activity.currentAccount = PassportActivity.this.currentAccount;
                    byte[] unused4 = activity.saltedPassword = PassportActivity.this.saltedPassword;
                    byte[] unused5 = activity.secureSecret = PassportActivity.this.secureSecret;
                    long unused6 = activity.secureSecretId = PassportActivity.this.secureSecretId;
                    boolean unused7 = activity.needActivityResult = PassportActivity.this.needActivityResult;
                    if (PassportActivity.this.parentLayout == null || !PassportActivity.this.parentLayout.checkTransitionAnimation()) {
                        PassportActivity.this.presentFragment(activity, true);
                    } else {
                        PassportActivity unused8 = PassportActivity.this.presentAfterAnimation = activity;
                    }
                }
            }

            private void resetSecret() {
                TLRPC.TL_account_updatePasswordSettings req2 = new TLRPC.TL_account_updatePasswordSettings();
                if (PassportActivity.this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                    req2.password = SRPHelper.startCheck(bArr, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) PassportActivity.this.currentPassword.current_algo);
                }
                req2.new_settings = new TLRPC.TL_account_passwordInputSettings();
                req2.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                req2.new_settings.new_secure_settings.secure_secret = new byte[0];
                req2.new_settings.new_secure_settings.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
                req2.new_settings.new_secure_settings.secure_secret_id = 0;
                req2.new_settings.flags |= 4;
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(tL_account_getPasswordSettings, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PassportActivity.AnonymousClass8.this.lambda$resetSecret$3$PassportActivity$8(tLObject, tL_error);
                    }
                });
            }

            public /* synthetic */ void lambda$resetSecret$3$PassportActivity$8(TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable(error) {
                    private final /* synthetic */ TLRPC.TL_error f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$null$2$PassportActivity$8(this.f$1);
                    }
                });
            }

            public /* synthetic */ void lambda$null$2$PassportActivity$8(TLRPC.TL_error error) {
                if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
                    generateNewSecret();
                    return;
                }
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PassportActivity.AnonymousClass8.this.lambda$null$1$PassportActivity$8(tLObject, tL_error);
                    }
                }, 8);
            }

            public /* synthetic */ void lambda$null$1$PassportActivity$8(TLObject response2, TLRPC.TL_error error2) {
                AndroidUtilities.runOnUIThread(new Runnable(error2, response2) {
                    private final /* synthetic */ TLRPC.TL_error f$1;
                    private final /* synthetic */ TLObject f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$null$0$PassportActivity$8(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$null$0$PassportActivity$8(TLRPC.TL_error error2, TLObject response2) {
                if (error2 == null) {
                    TLRPC.TL_account_password unused = PassportActivity.this.currentPassword = (TLRPC.TL_account_password) response2;
                    TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                    resetSecret();
                }
            }

            private void generateNewSecret() {
                Utilities.globalQueue.postRunnable(new Runnable(bArr, str) {
                    private final /* synthetic */ byte[] f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$generateNewSecret$8$PassportActivity$8(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$generateNewSecret$8$PassportActivity$8(byte[] x_bytes, String textPassword) {
                Utilities.random.setSeed(PassportActivity.this.currentPassword.secure_random);
                TLRPC.TL_account_updatePasswordSettings req1 = new TLRPC.TL_account_updatePasswordSettings();
                if (PassportActivity.this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                    req1.password = SRPHelper.startCheck(x_bytes, PassportActivity.this.currentPassword.srp_id, PassportActivity.this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) PassportActivity.this.currentPassword.current_algo);
                }
                req1.new_settings = new TLRPC.TL_account_passwordInputSettings();
                PassportActivity passportActivity = PassportActivity.this;
                byte[] unused = passportActivity.secureSecret = passportActivity.getRandomSecret();
                PassportActivity passportActivity2 = PassportActivity.this;
                long unused2 = passportActivity2.secureSecretId = Utilities.bytesToLong(Utilities.computeSHA256(passportActivity2.secureSecret));
                if (PassportActivity.this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 newAlgo = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) PassportActivity.this.currentPassword.new_secure_algo;
                    byte[] unused3 = PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), newAlgo.salt);
                    byte[] key = new byte[32];
                    System.arraycopy(PassportActivity.this.saltedPassword, 0, key, 0, 32);
                    byte[] iv = new byte[16];
                    System.arraycopy(PassportActivity.this.saltedPassword, 32, iv, 0, 16);
                    Utilities.aesCbcEncryptionByteArraySafe(PassportActivity.this.secureSecret, key, iv, 0, PassportActivity.this.secureSecret.length, 0, 1);
                    req1.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                    req1.new_settings.new_secure_settings.secure_algo = newAlgo;
                    req1.new_settings.new_secure_settings.secure_secret = PassportActivity.this.secureSecret;
                    req1.new_settings.new_secure_settings.secure_secret_id = PassportActivity.this.secureSecretId;
                    req1.new_settings.flags |= 4;
                }
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(req1, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PassportActivity.AnonymousClass8.this.lambda$null$7$PassportActivity$8(tLObject, tL_error);
                    }
                });
            }

            public /* synthetic */ void lambda$null$7$PassportActivity$8(TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable(error) {
                    private final /* synthetic */ TLRPC.TL_error f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$null$6$PassportActivity$8(this.f$1);
                    }
                });
            }

            public /* synthetic */ void lambda$null$6$PassportActivity$8(TLRPC.TL_error error) {
                if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
                    if (PassportActivity.this.currentForm == null) {
                        TLRPC.TL_account_authorizationForm unused = PassportActivity.this.currentForm = new TLRPC.TL_account_authorizationForm();
                    }
                    openRequestInterface();
                    return;
                }
                ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PassportActivity.AnonymousClass8.this.lambda$null$5$PassportActivity$8(tLObject, tL_error);
                    }
                }, 8);
            }

            public /* synthetic */ void lambda$null$5$PassportActivity$8(TLObject response2, TLRPC.TL_error error2) {
                AndroidUtilities.runOnUIThread(new Runnable(error2, response2) {
                    private final /* synthetic */ TLRPC.TL_error f$1;
                    private final /* synthetic */ TLObject f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$null$4$PassportActivity$8(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$null$4$PassportActivity$8(TLRPC.TL_error error2, TLObject response2) {
                if (error2 == null) {
                    TLRPC.TL_account_password unused = PassportActivity.this.currentPassword = (TLRPC.TL_account_password) response2;
                    TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                    generateNewSecret();
                }
            }

            public void run(TLObject response, TLRPC.TL_error error) {
                if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(z) {
                        private final /* synthetic */ boolean f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            PassportActivity.AnonymousClass8.this.lambda$run$10$PassportActivity$8(this.f$1, tLObject, tL_error);
                        }
                    }, 8);
                } else if (error == null) {
                    Utilities.globalQueue.postRunnable(new Runnable(response, str, z) {
                        private final /* synthetic */ TLObject f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ boolean f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            PassportActivity.AnonymousClass8.this.lambda$run$15$PassportActivity$8(this.f$1, this.f$2, this.f$3);
                        }
                    });
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable(z, error) {
                        private final /* synthetic */ boolean f$1;
                        private final /* synthetic */ TLRPC.TL_error f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            PassportActivity.AnonymousClass8.this.lambda$run$16$PassportActivity$8(this.f$1, this.f$2);
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$run$10$PassportActivity$8(boolean saved, TLObject response2, TLRPC.TL_error error2) {
                AndroidUtilities.runOnUIThread(new Runnable(error2, response2, saved) {
                    private final /* synthetic */ TLRPC.TL_error f$1;
                    private final /* synthetic */ TLObject f$2;
                    private final /* synthetic */ boolean f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$null$9$PassportActivity$8(this.f$1, this.f$2, this.f$3);
                    }
                });
            }

            public /* synthetic */ void lambda$null$9$PassportActivity$8(TLRPC.TL_error error2, TLObject response2, boolean saved) {
                if (error2 == null) {
                    TLRPC.TL_account_password unused = PassportActivity.this.currentPassword = (TLRPC.TL_account_password) response2;
                    TwoStepVerificationActivity.initPasswordNewAlgo(PassportActivity.this.currentPassword);
                    PassportActivity.this.onPasswordDone(saved);
                }
            }

            public /* synthetic */ void lambda$run$15$PassportActivity$8(TLObject response, String textPassword, boolean saved) {
                byte[] secure_salt;
                TLRPC.TL_account_passwordSettings settings = (TLRPC.TL_account_passwordSettings) response;
                if (settings.secure_settings != null) {
                    byte[] unused = PassportActivity.this.secureSecret = settings.secure_settings.secure_secret;
                    long unused2 = PassportActivity.this.secureSecretId = settings.secure_settings.secure_secret_id;
                    if (settings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512) {
                        secure_salt = ((TLRPC.TL_securePasswordKdfAlgoSHA512) settings.secure_settings.secure_algo).salt;
                        byte[] unused3 = PassportActivity.this.saltedPassword = Utilities.computeSHA512(secure_salt, AndroidUtilities.getStringBytes(textPassword), secure_salt);
                    } else if (settings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                        TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) settings.secure_settings.secure_algo;
                        secure_salt = algo.salt;
                        byte[] unused4 = PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), algo.salt);
                    } else if (settings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                PassportActivity.AnonymousClass8.this.lambda$null$11$PassportActivity$8();
                            }
                        });
                        return;
                    } else {
                        secure_salt = new byte[0];
                    }
                } else {
                    if (PassportActivity.this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                        TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo2 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) PassportActivity.this.currentPassword.new_secure_algo;
                        secure_salt = algo2.salt;
                        byte[] unused5 = PassportActivity.this.saltedPassword = Utilities.computePBKDF2(AndroidUtilities.getStringBytes(textPassword), algo2.salt);
                    } else {
                        secure_salt = new byte[0];
                    }
                    byte[] unused6 = PassportActivity.this.secureSecret = null;
                    long unused7 = PassportActivity.this.secureSecretId = 0;
                }
                AndroidUtilities.runOnUIThread(new Runnable(settings, saved, secure_salt) {
                    private final /* synthetic */ TLRPC.TL_account_passwordSettings f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ byte[] f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$null$14$PassportActivity$8(this.f$1, this.f$2, this.f$3);
                    }
                });
            }

            public /* synthetic */ void lambda$null$11$PassportActivity$8() {
                AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
            }

            public /* synthetic */ void lambda$null$14$PassportActivity$8(TLRPC.TL_account_passwordSettings settings, boolean saved, byte[] secure_salt) {
                String unused = PassportActivity.this.currentEmail = settings.email;
                if (saved) {
                    PassportActivity passportActivity = PassportActivity.this;
                    byte[] unused2 = passportActivity.saltedPassword = passportActivity.savedSaltedPassword;
                }
                PassportActivity passportActivity2 = PassportActivity.this;
                if (!PassportActivity.checkSecret(passportActivity2.decryptSecret(passportActivity2.secureSecret, PassportActivity.this.saltedPassword), Long.valueOf(PassportActivity.this.secureSecretId)) || secure_salt.length == 0 || PassportActivity.this.secureSecretId == 0) {
                    if (saved) {
                        UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                        int unused3 = PassportActivity.this.usingSavedPassword = 0;
                        PassportActivity.this.updatePasswordInterface();
                        return;
                    }
                    if (PassportActivity.this.currentForm != null) {
                        PassportActivity.this.currentForm.values.clear();
                        PassportActivity.this.currentForm.errors.clear();
                    }
                    if (PassportActivity.this.secureSecret == null || PassportActivity.this.secureSecret.length == 0) {
                        generateNewSecret();
                    } else {
                        resetSecret();
                    }
                } else if (PassportActivity.this.currentBotId == 0) {
                    ConnectionsManager.getInstance(PassportActivity.this.currentAccount).sendRequest(new TLRPC.TL_account_getAllSecureValues(), new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            PassportActivity.AnonymousClass8.this.lambda$null$13$PassportActivity$8(tLObject, tL_error);
                        }
                    });
                } else {
                    openRequestInterface();
                }
            }

            public /* synthetic */ void lambda$null$13$PassportActivity$8(TLObject response1, TLRPC.TL_error error1) {
                AndroidUtilities.runOnUIThread(new Runnable(response1, error1) {
                    private final /* synthetic */ TLObject f$1;
                    private final /* synthetic */ TLRPC.TL_error f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PassportActivity.AnonymousClass8.this.lambda$null$12$PassportActivity$8(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$null$12$PassportActivity$8(TLObject response1, TLRPC.TL_error error1) {
                if (response1 != null) {
                    TLRPC.TL_account_authorizationForm unused = PassportActivity.this.currentForm = new TLRPC.TL_account_authorizationForm();
                    TLRPC.Vector vector = (TLRPC.Vector) response1;
                    int size = vector.objects.size();
                    for (int a = 0; a < size; a++) {
                        PassportActivity.this.currentForm.values.add((TLRPC.TL_secureValue) vector.objects.get(a));
                    }
                    openRequestInterface();
                    return;
                }
                if ("APP_VERSION_OUTDATED".equals(error1.text)) {
                    AlertsCreator.showUpdateAppAlert(PassportActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                } else {
                    PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error1.text);
                }
                PassportActivity.this.showEditDoneProgress(true, false);
            }

            public /* synthetic */ void lambda$run$16$PassportActivity$8(boolean saved, TLRPC.TL_error error) {
                String timeString;
                if (saved) {
                    UserConfig.getInstance(PassportActivity.this.currentAccount).resetSavedPassword();
                    int unused = PassportActivity.this.usingSavedPassword = 0;
                    PassportActivity.this.updatePasswordInterface();
                    if (PassportActivity.this.inputFieldContainers != null && PassportActivity.this.inputFieldContainers[0].getVisibility() == 0) {
                        PassportActivity.this.inputFields[0].requestFocus();
                        AndroidUtilities.showKeyboard(PassportActivity.this.inputFields[0]);
                        return;
                    }
                    return;
                }
                PassportActivity.this.showEditDoneProgress(true, false);
                if (error.text.equals("PASSWORD_HASH_INVALID")) {
                    PassportActivity.this.onPasscodeError(true);
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                } else {
                    PassportActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                }
            }
        };
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
            if (req.password == null) {
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run((TLObject) null, error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10), this.classGuid);
            return;
        }
        TLRPC.TL_error error2 = new TLRPC.TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run((TLObject) null, error2);
    }

    private boolean isPersonalDocument(TLRPC.SecureValueType type) {
        return (type instanceof TLRPC.TL_secureValueTypeDriverLicense) || (type instanceof TLRPC.TL_secureValueTypePassport) || (type instanceof TLRPC.TL_secureValueTypeInternalPassport) || (type instanceof TLRPC.TL_secureValueTypeIdentityCard);
    }

    private boolean isAddressDocument(TLRPC.SecureValueType type) {
        return (type instanceof TLRPC.TL_secureValueTypeUtilityBill) || (type instanceof TLRPC.TL_secureValueTypeBankStatement) || (type instanceof TLRPC.TL_secureValueTypePassportRegistration) || (type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) || (type instanceof TLRPC.TL_secureValueTypeRentalAgreement);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:109:0x032b, code lost:
        if (isPersonalDocument(r2.type) == false) goto L_0x0330;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createRequestInterface(android.content.Context r32) {
        /*
            r31 = this;
            r6 = r31
            r7 = r32
            r0 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r1 = r6.currentForm
            if (r1 == 0) goto L_0x002a
            r1 = 0
        L_0x000a:
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r2 = r6.currentForm
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r2 = r2.users
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x002a
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r2 = r6.currentForm
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r2 = r2.users
            java.lang.Object r2 = r2.get(r1)
            im.bclpbkiauv.tgnet.TLRPC$User r2 = (im.bclpbkiauv.tgnet.TLRPC.User) r2
            int r3 = r2.id
            int r4 = r6.currentBotId
            if (r3 != r4) goto L_0x0027
            r0 = r2
            r8 = r0
            goto L_0x002b
        L_0x0027:
            int r1 = r1 + 1
            goto L_0x000a
        L_0x002a:
            r8 = r0
        L_0x002b:
            android.view.View r0 = r6.fragmentView
            r9 = r0
            android.widget.FrameLayout r9 = (android.widget.FrameLayout) r9
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r1 = 2131689829(0x7f0f0165, float:1.9008684E38)
            java.lang.String r2 = "AppPassport"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            im.bclpbkiauv.ui.actionbar.ActionBarMenu r0 = r0.createMenu()
            r1 = 2131231473(0x7f0802f1, float:1.8079028E38)
            r10 = 1
            r0.addItem((int) r10, (int) r1)
            java.lang.String r11 = "windowBackgroundGrayShadow"
            r12 = -2
            r13 = -1
            r14 = 0
            if (r8 == 0) goto L_0x00e1
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            android.widget.LinearLayout r1 = r6.linearLayout2
            r2 = 100
            android.widget.LinearLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r2)
            r1.addView(r0, r2)
            im.bclpbkiauv.ui.components.BackupImageView r1 = new im.bclpbkiauv.ui.components.BackupImageView
            r1.<init>(r7)
            r2 = 1107296256(0x42000000, float:32.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.setRoundRadius(r2)
            r15 = 1115684864(0x42800000, float:64.0)
            r16 = 1115684864(0x42800000, float:64.0)
            r17 = 17
            r18 = 0
            r19 = 1090519040(0x41000000, float:8.0)
            r20 = 0
            r21 = 0
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.components.AvatarDrawable r2 = new im.bclpbkiauv.ui.components.AvatarDrawable
            r2.<init>((im.bclpbkiauv.tgnet.TLRPC.User) r8)
            im.bclpbkiauv.messenger.ImageLocation r3 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r8, r14)
            java.lang.String r4 = "50_50"
            r1.setImage((im.bclpbkiauv.messenger.ImageLocation) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r8)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
            r3.<init>(r7)
            r6.bottomCell = r3
            r4 = 2131231062(0x7f080156, float:1.8078194E38)
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r7, (int) r4, (java.lang.String) r11)
            r3.setBackgroundDrawable(r4)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = r6.bottomCell
            r4 = 2131692754(0x7f0f0cd2, float:1.9014617E38)
            java.lang.Object[] r5 = new java.lang.Object[r10]
            java.lang.String r15 = im.bclpbkiauv.messenger.UserObject.getFirstName(r8)
            r5[r14] = r15
            java.lang.String r15 = "PassportRequest"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r15, r4, r5)
            android.text.SpannableStringBuilder r4 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r4)
            r3.setText(r4)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = r6.bottomCell
            android.widget.TextView r3 = r3.getTextView()
            r3.setGravity(r10)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = r6.bottomCell
            android.widget.TextView r3 = r3.getTextView()
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r3.gravity = r10
            android.widget.LinearLayout r3 = r6.linearLayout2
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r4 = r6.bottomCell
            android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r12)
            r3.addView(r4, r5)
        L_0x00e1:
            im.bclpbkiauv.ui.cells.HeaderCell r0 = new im.bclpbkiauv.ui.cells.HeaderCell
            r0.<init>(r7)
            r6.headerCell = r0
            r1 = 2131692756(0x7f0f0cd4, float:1.9014621E38)
            java.lang.String r2 = "PassportRequestedInformation"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            im.bclpbkiauv.ui.cells.HeaderCell r0 = r6.headerCell
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout r0 = r6.linearLayout2
            im.bclpbkiauv.ui.cells.HeaderCell r1 = r6.headerCell
            android.widget.LinearLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r12)
            r0.addView(r1, r2)
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            if (r0 == 0) goto L_0x03dd
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r0 = r0.required_types
            int r15 = r0.size()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r5 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r4 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r16 = 0
            r17 = r3
            r3 = r0
            r0 = r16
            r16 = r2
            r2 = r1
        L_0x012f:
            if (r0 >= r15) goto L_0x0203
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r1 = r6.currentForm
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r1 = r1.required_types
            java.lang.Object r1 = r1.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r1 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r1
            boolean r12 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r12 == 0) goto L_0x0171
            r12 = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r12 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r12
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r13 = r12.type
            boolean r13 = r6.isPersonalDocument(r13)
            if (r13 == 0) goto L_0x0150
            r5.add(r12)
            int r3 = r3 + 1
            goto L_0x016f
        L_0x0150:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r13 = r12.type
            boolean r13 = r6.isAddressDocument(r13)
            if (r13 == 0) goto L_0x015e
            r4.add(r12)
            int r2 = r2 + 1
            goto L_0x016f
        L_0x015e:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r13 = r12.type
            boolean r13 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePersonalDetails
            if (r13 == 0) goto L_0x0167
            r16 = 1
            goto L_0x016f
        L_0x0167:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r13 = r12.type
            boolean r13 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeAddress
            if (r13 == 0) goto L_0x016f
            r17 = 1
        L_0x016f:
            goto L_0x01fb
        L_0x0171:
            boolean r12 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredTypeOneOf
            if (r12 == 0) goto L_0x01f9
            r12 = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredTypeOneOf r12 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredTypeOneOf) r12
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r13 = r12.types
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x0182
            goto L_0x01fb
        L_0x0182:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r13 = r12.types
            java.lang.Object r13 = r13.get(r14)
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r13 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r13
            boolean r14 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r14 != 0) goto L_0x0190
            goto L_0x01fb
        L_0x0190:
            r14 = r13
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r14 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r14
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r10 = r14.type
            boolean r10 = r6.isPersonalDocument(r10)
            if (r10 == 0) goto L_0x01c6
            r10 = 0
            r22 = r1
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r1 = r12.types
            int r1 = r1.size()
        L_0x01a4:
            if (r10 >= r1) goto L_0x01c1
            r23 = r1
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r1 = r12.types
            java.lang.Object r1 = r1.get(r10)
            r13 = r1
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r13 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r13
            boolean r1 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r1 != 0) goto L_0x01b6
            goto L_0x01bc
        L_0x01b6:
            r1 = r13
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r1
            r5.add(r1)
        L_0x01bc:
            int r10 = r10 + 1
            r1 = r23
            goto L_0x01a4
        L_0x01c1:
            r23 = r1
            int r3 = r3 + 1
            goto L_0x01fb
        L_0x01c6:
            r22 = r1
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r14.type
            boolean r1 = r6.isAddressDocument(r1)
            if (r1 == 0) goto L_0x01fb
            r1 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r10 = r12.types
            int r10 = r10.size()
        L_0x01d7:
            if (r1 >= r10) goto L_0x01f4
            r23 = r10
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r10 = r12.types
            java.lang.Object r10 = r10.get(r1)
            r13 = r10
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r13 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r13
            boolean r10 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r10 != 0) goto L_0x01e9
            goto L_0x01ef
        L_0x01e9:
            r10 = r13
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r10 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r10
            r4.add(r10)
        L_0x01ef:
            int r1 = r1 + 1
            r10 = r23
            goto L_0x01d7
        L_0x01f4:
            r23 = r10
            int r2 = r2 + 1
            goto L_0x01fb
        L_0x01f9:
            r22 = r1
        L_0x01fb:
            int r0 = r0 + 1
            r10 = 1
            r12 = -2
            r13 = -1
            r14 = 0
            goto L_0x012f
        L_0x0203:
            if (r16 == 0) goto L_0x020b
            r0 = 1
            if (r3 <= r0) goto L_0x0209
            goto L_0x020b
        L_0x0209:
            r0 = 0
            goto L_0x020c
        L_0x020b:
            r0 = 1
        L_0x020c:
            r10 = r0
            if (r17 == 0) goto L_0x0215
            r0 = 1
            if (r2 <= r0) goto L_0x0213
            goto L_0x0215
        L_0x0213:
            r0 = 0
            goto L_0x0216
        L_0x0215:
            r0 = 1
        L_0x0216:
            r12 = r0
            r0 = 0
            r13 = r0
        L_0x0219:
            if (r13 >= r15) goto L_0x03d5
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r0 = r0.required_types
            java.lang.Object r0 = r0.get(r13)
            r14 = r0
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r14 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r14
            boolean r0 = r14 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r0 == 0) goto L_0x02ee
            r0 = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r0
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePhone
            if (r1 != 0) goto L_0x02dd
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeEmail
            if (r1 == 0) goto L_0x023b
            goto L_0x02dd
        L_0x023b:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePersonalDetails
            if (r1 == 0) goto L_0x0256
            if (r10 == 0) goto L_0x0245
            r1 = 0
            goto L_0x0246
        L_0x0245:
            r1 = r5
        L_0x0246:
            r22 = 0
            r23 = r0
            r26 = r1
            r25 = r3
            r24 = r4
            r27 = r22
            r22 = r2
            goto L_0x039d
        L_0x0256:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeAddress
            if (r1 == 0) goto L_0x0271
            if (r12 == 0) goto L_0x0260
            r1 = 0
            goto L_0x0261
        L_0x0260:
            r1 = r4
        L_0x0261:
            r22 = 0
            r23 = r0
            r26 = r1
            r25 = r3
            r24 = r4
            r27 = r22
            r22 = r2
            goto L_0x039d
        L_0x0271:
            if (r10 == 0) goto L_0x02a2
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r6.isPersonalDocument(r1)
            if (r1 == 0) goto L_0x02a2
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r1.add(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r22 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType
            r22.<init>()
            r0 = r22
            r22 = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypePersonalDetails r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            r1.<init>()
            r0.type = r1
            r1 = 1
            r23 = r0
            r27 = r1
            r25 = r3
            r24 = r4
            r26 = r22
            r22 = r2
            goto L_0x039d
        L_0x02a2:
            if (r12 == 0) goto L_0x02d3
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r0.type
            boolean r1 = r6.isAddressDocument(r1)
            if (r1 == 0) goto L_0x02d3
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r1.add(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r22 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType
            r22.<init>()
            r0 = r22
            r22 = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypeAddress r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypeAddress
            r1.<init>()
            r0.type = r1
            r1 = 1
            r23 = r0
            r27 = r1
            r25 = r3
            r24 = r4
            r26 = r22
            r22 = r2
            goto L_0x039d
        L_0x02d3:
            r22 = r2
            r25 = r3
            r24 = r4
            r29 = r5
            goto L_0x03c9
        L_0x02dd:
            r1 = 0
            r22 = 0
            r23 = r0
            r26 = r1
            r25 = r3
            r24 = r4
            r27 = r22
            r22 = r2
            goto L_0x039d
        L_0x02ee:
            boolean r0 = r14 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredTypeOneOf
            if (r0 == 0) goto L_0x03c1
            r0 = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredTypeOneOf r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredTypeOneOf) r0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r1 = r0.types
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0307
            r22 = r2
            r25 = r3
            r24 = r4
            r29 = r5
            goto L_0x03c9
        L_0x0307:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r1 = r0.types
            r22 = r2
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r1 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r1
            boolean r2 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r2 != 0) goto L_0x031e
            r25 = r3
            r24 = r4
            r29 = r5
            goto L_0x03c9
        L_0x031e:
            r2 = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r2
            if (r10 == 0) goto L_0x032e
            r23 = r1
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r2.type
            boolean r1 = r6.isPersonalDocument(r1)
            if (r1 != 0) goto L_0x033a
            goto L_0x0330
        L_0x032e:
            r23 = r1
        L_0x0330:
            if (r12 == 0) goto L_0x03b8
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r2.type
            boolean r1 = r6.isAddressDocument(r1)
            if (r1 == 0) goto L_0x03b8
        L_0x033a:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r24 = 0
            r25 = r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r3 = r0.types
            int r3 = r3.size()
            r30 = r24
            r24 = r4
            r4 = r30
        L_0x034f:
            if (r4 >= r3) goto L_0x0371
            r26 = r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r3 = r0.types
            java.lang.Object r3 = r3.get(r4)
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r3 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r3
            r27 = r0
            boolean r0 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r0 != 0) goto L_0x0362
            goto L_0x0368
        L_0x0362:
            r0 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r0
            r1.add(r0)
        L_0x0368:
            int r4 = r4 + 1
            r23 = r3
            r3 = r26
            r0 = r27
            goto L_0x034f
        L_0x0371:
            r27 = r0
            r26 = r3
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r0 = r2.type
            boolean r0 = r6.isPersonalDocument(r0)
            if (r0 == 0) goto L_0x038a
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypePersonalDetails r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            r2.<init>()
            r0.type = r2
            goto L_0x0396
        L_0x038a:
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType
            r0.<init>()
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypeAddress r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureValueTypeAddress
            r2.<init>()
            r0.type = r2
        L_0x0396:
            r2 = 1
            r23 = r0
            r26 = r1
            r27 = r2
        L_0x039d:
            int r0 = r15 + -1
            if (r13 != r0) goto L_0x03a4
            r28 = 1
            goto L_0x03a6
        L_0x03a4:
            r28 = 0
        L_0x03a6:
            r0 = r31
            r1 = r32
            r2 = r23
            r3 = r26
            r4 = r27
            r29 = r5
            r5 = r28
            r0.addField(r1, r2, r3, r4, r5)
            goto L_0x03c9
        L_0x03b8:
            r27 = r0
            r25 = r3
            r24 = r4
            r29 = r5
            goto L_0x03c9
        L_0x03c1:
            r22 = r2
            r25 = r3
            r24 = r4
            r29 = r5
        L_0x03c9:
            int r13 = r13 + 1
            r2 = r22
            r4 = r24
            r3 = r25
            r5 = r29
            goto L_0x0219
        L_0x03d5:
            r22 = r2
            r25 = r3
            r24 = r4
            r29 = r5
        L_0x03dd:
            if (r8 == 0) goto L_0x049e
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
            r0.<init>(r7)
            r6.bottomCell = r0
            r1 = 2131231061(0x7f080155, float:1.8078192E38)
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r7, (int) r1, (java.lang.String) r11)
            r0.setBackgroundDrawable(r1)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = r6.bottomCell
            java.lang.String r1 = "windowBackgroundWhiteGrayText4"
            r0.setLinkTextColorKey(r1)
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r0 = r6.currentForm
            java.lang.String r0 = r0.privacy_policy_url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r2 = 2
            if (r0 != 0) goto L_0x045a
            r0 = 2131692751(0x7f0f0ccf, float:1.901461E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = im.bclpbkiauv.messenger.UserObject.getFirstName(r8)
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = r8.username
            r4 = 1
            r2[r4] = r3
            java.lang.String r3 = "PassportPolicy"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r0, r2)
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r0)
            r3 = 42
            int r4 = r0.indexOf(r3)
            int r3 = r0.lastIndexOf(r3)
            r5 = -1
            if (r4 == r5) goto L_0x0454
            if (r3 == r5) goto L_0x0454
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r5 = r6.bottomCell
            android.widget.TextView r5 = r5.getTextView()
            im.bclpbkiauv.messenger.AndroidUtilities$LinkMovementMethodMy r10 = new im.bclpbkiauv.messenger.AndroidUtilities$LinkMovementMethodMy
            r10.<init>()
            r5.setMovementMethod(r10)
            int r5 = r3 + 1
            java.lang.String r10 = ""
            r2.replace(r3, r5, r10)
            int r5 = r4 + 1
            r2.replace(r4, r5, r10)
            im.bclpbkiauv.ui.PassportActivity$LinkSpan r5 = new im.bclpbkiauv.ui.PassportActivity$LinkSpan
            r5.<init>()
            int r10 = r3 + -1
            r11 = 33
            r2.setSpan(r5, r4, r10, r11)
        L_0x0454:
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r5 = r6.bottomCell
            r5.setText(r2)
            goto L_0x047a
        L_0x045a:
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = r6.bottomCell
            r3 = 2131692739(0x7f0f0cc3, float:1.9014587E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = im.bclpbkiauv.messenger.UserObject.getFirstName(r8)
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = r8.username
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "PassportNoPolicy"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r2)
            android.text.SpannableStringBuilder r2 = im.bclpbkiauv.messenger.AndroidUtilities.replaceTags(r2)
            r0.setText(r2)
        L_0x047a:
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = r6.bottomCell
            android.widget.TextView r0 = r0.getTextView()
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setHighlightColor(r1)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = r6.bottomCell
            android.widget.TextView r0 = r0.getTextView()
            r1 = 1
            r0.setGravity(r1)
            android.widget.LinearLayout r0 = r6.linearLayout2
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r1 = r6.bottomCell
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r3, (int) r2)
            r0.addView(r1, r4)
        L_0x049e:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.bottomLayout = r0
            java.lang.String r1 = "passport_authorizeBackground"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            java.lang.String r2 = "passport_authorizeBackgroundSelected"
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorWithBackgroundDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            android.widget.FrameLayout r0 = r6.bottomLayout
            r1 = 48
            r2 = 80
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r3, (int) r1, (int) r2)
            r9.addView(r0, r1)
            android.widget.FrameLayout r0 = r6.bottomLayout
            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$CbcdFUUBGNt-H_js__FRnUEUjZ8 r1 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$CbcdFUUBGNt-H_js__FRnUEUjZ8
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.acceptTextView = r0
            r1 = 1090519040(0x41000000, float:8.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            r0.setCompoundDrawablePadding(r1)
            android.widget.TextView r0 = r6.acceptTextView
            r1 = 2131230841(0x7f080079, float:1.8077746E38)
            r2 = 0
            r0.setCompoundDrawablesWithIntrinsicBounds(r1, r2, r2, r2)
            android.widget.TextView r0 = r6.acceptTextView
            java.lang.String r1 = "passport_authorizeText"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.acceptTextView
            r1 = 2131692632(0x7f0f0c58, float:1.901437E38)
            java.lang.String r2 = "PassportAuthorize"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.widget.TextView r0 = r6.acceptTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r6.acceptTextView
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r6.acceptTextView
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r2)
            android.widget.FrameLayout r0 = r6.bottomLayout
            android.widget.TextView r2 = r6.acceptTextView
            r3 = -2
            r4 = -1
            android.widget.FrameLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r3, (int) r4, (int) r1)
            r0.addView(r2, r1)
            im.bclpbkiauv.ui.components.ContextProgressView r0 = new im.bclpbkiauv.ui.components.ContextProgressView
            r1 = 0
            r0.<init>(r7, r1)
            r6.progressViewButton = r0
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r6.bottomLayout
            im.bclpbkiauv.ui.components.ContextProgressView r1 = r6.progressViewButton
            r2 = -1082130432(0xffffffffbf800000, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r1 = 2131231072(0x7f080160, float:1.8078215E38)
            r0.setBackgroundResource(r1)
            r10 = -1082130432(0xffffffffbf800000, float:-1.0)
            r11 = 1077936128(0x40400000, float:3.0)
            r12 = 83
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r9.addView(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.createRequestInterface(android.content.Context):void");
    }

    /* JADX WARNING: type inference failed for: r7v6, types: [im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType] */
    /* JADX WARNING: type inference failed for: r7v8, types: [im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createRequestInterface$16$PassportActivity(android.view.View r22) {
        /*
            r21 = this;
            r1 = r21
            java.lang.String r2 = ""
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            r0 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r4 = r1.currentForm
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r4 = r4.required_types
            int r4 = r4.size()
        L_0x0013:
            r5 = 0
            r6 = 1
            if (r0 >= r4) goto L_0x00d4
            im.bclpbkiauv.tgnet.TLRPC$TL_account_authorizationForm r7 = r1.currentForm
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r7 = r7.required_types
            java.lang.Object r7 = r7.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r7 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r7
            boolean r8 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r8 == 0) goto L_0x0029
            r8 = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r8
            goto L_0x0073
        L_0x0029:
            boolean r8 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredTypeOneOf
            if (r8 == 0) goto L_0x00d0
            r8 = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredTypeOneOf r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredTypeOneOf) r8
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r9 = r8.types
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x003a
            goto L_0x00d0
        L_0x003a:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r9 = r8.types
            java.lang.Object r9 = r9.get(r5)
            r7 = r9
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r7 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r7
            boolean r9 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r9 != 0) goto L_0x0049
            goto L_0x00d0
        L_0x0049:
            r9 = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r9 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r9
            r10 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r11 = r8.types
            int r11 = r11.size()
        L_0x0053:
            if (r10 >= r11) goto L_0x0072
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType> r12 = r8.types
            java.lang.Object r12 = r12.get(r10)
            r7 = r12
            im.bclpbkiauv.tgnet.TLRPC$SecureRequiredType r7 = (im.bclpbkiauv.tgnet.TLRPC.SecureRequiredType) r7
            boolean r12 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType
            if (r12 != 0) goto L_0x0063
            goto L_0x006f
        L_0x0063:
            r12 = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r12 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r12
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r13 = r1.getValueByType(r12, r6)
            if (r13 == 0) goto L_0x006f
            r9 = r12
            r8 = r9
            goto L_0x0073
        L_0x006f:
            int r10 = r10 + 1
            goto L_0x0053
        L_0x0072:
            r8 = r9
        L_0x0073:
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r6 = r1.getValueByType(r8, r6)
            r9 = 200(0xc8, double:9.9E-322)
            r11 = 1073741824(0x40000000, float:2.0)
            java.lang.String r12 = "vibrator"
            if (r6 != 0) goto L_0x0097
            androidx.fragment.app.FragmentActivity r2 = r21.getParentActivity()
            java.lang.Object r2 = r2.getSystemService(r12)
            android.os.Vibrator r2 = (android.os.Vibrator) r2
            if (r2 == 0) goto L_0x008f
            r2.vibrate(r9)
        L_0x008f:
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r9 = r1.getViewByType(r8)
            im.bclpbkiauv.messenger.AndroidUtilities.shakeView(r9, r11, r5)
            return
        L_0x0097:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r13 = r8.type
            java.lang.String r13 = r1.getNameForType(r13)
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r14 = r1.errorsMap
            java.lang.Object r14 = r14.get(r13)
            java.util.HashMap r14 = (java.util.HashMap) r14
            if (r14 == 0) goto L_0x00c4
            boolean r15 = r14.isEmpty()
            if (r15 != 0) goto L_0x00c4
            androidx.fragment.app.FragmentActivity r2 = r21.getParentActivity()
            java.lang.Object r2 = r2.getSystemService(r12)
            android.os.Vibrator r2 = (android.os.Vibrator) r2
            if (r2 == 0) goto L_0x00bc
            r2.vibrate(r9)
        L_0x00bc:
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r9 = r1.getViewByType(r8)
            im.bclpbkiauv.messenger.AndroidUtilities.shakeView(r9, r11, r5)
            return
        L_0x00c4:
            im.bclpbkiauv.ui.PassportActivity$1ValueToSend r5 = new im.bclpbkiauv.ui.PassportActivity$1ValueToSend
            boolean r9 = r8.selfie_required
            boolean r10 = r8.translation_required
            r5.<init>(r6, r9, r10)
            r3.add(r5)
        L_0x00d0:
            int r0 = r0 + 1
            goto L_0x0013
        L_0x00d4:
            r1.showEditDoneProgress(r5, r6)
            im.bclpbkiauv.tgnet.TLRPC$TL_account_acceptAuthorization r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_account_acceptAuthorization
            r0.<init>()
            r4 = r0
            int r0 = r1.currentBotId
            r4.bot_id = r0
            java.lang.String r0 = r1.currentScope
            r4.scope = r0
            java.lang.String r0 = r1.currentPublicKey
            r4.public_key = r0
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            r7 = r0
            r0 = 0
            int r8 = r3.size()
            r9 = r0
        L_0x00f5:
            if (r9 >= r8) goto L_0x0319
            java.lang.Object r0 = r3.get(r9)
            r10 = r0
            im.bclpbkiauv.ui.PassportActivity$1ValueToSend r10 = (im.bclpbkiauv.ui.PassportActivity.AnonymousClass1ValueToSend) r10
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r11 = r10.value
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            r12 = r0
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r0 = r11.plain_data
            if (r0 == 0) goto L_0x0133
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r0 = r11.plain_data
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_securePlainEmail
            if (r0 == 0) goto L_0x0115
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r0 = r11.plain_data
            im.bclpbkiauv.tgnet.TLRPC$TL_securePlainEmail r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_securePlainEmail) r0
            goto L_0x0129
        L_0x0115:
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r0 = r11.plain_data
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_securePlainPhone
            if (r0 == 0) goto L_0x0129
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r0 = r11.plain_data
            im.bclpbkiauv.tgnet.TLRPC$TL_securePlainPhone r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_securePlainPhone) r0
            r16 = r3
            r17 = r8
            r20 = r10
            r18 = r12
            goto L_0x02fd
        L_0x0129:
            r16 = r3
            r17 = r8
            r20 = r10
            r18 = r12
            goto L_0x02fd
        L_0x0133:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x02f4 }
            r0.<init>()     // Catch:{ Exception -> 0x02f4 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r13 = r11.data     // Catch:{ Exception -> 0x02f4 }
            java.lang.String r14 = "secret"
            r15 = 2
            if (r13 == 0) goto L_0x0170
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r13 = r11.data     // Catch:{ Exception -> 0x0165 }
            byte[] r13 = r13.secret     // Catch:{ Exception -> 0x0165 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r6 = r11.data     // Catch:{ Exception -> 0x0165 }
            byte[] r6 = r6.data_hash     // Catch:{ Exception -> 0x0165 }
            byte[] r6 = r1.decryptValueSecret(r13, r6)     // Catch:{ Exception -> 0x0165 }
            java.lang.String r13 = "data_hash"
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r5 = r11.data     // Catch:{ Exception -> 0x0165 }
            byte[] r5 = r5.data_hash     // Catch:{ Exception -> 0x0165 }
            java.lang.String r5 = android.util.Base64.encodeToString(r5, r15)     // Catch:{ Exception -> 0x0165 }
            r12.put(r13, r5)     // Catch:{ Exception -> 0x0165 }
            java.lang.String r5 = android.util.Base64.encodeToString(r6, r15)     // Catch:{ Exception -> 0x0165 }
            r12.put(r14, r5)     // Catch:{ Exception -> 0x0165 }
            java.lang.String r5 = "data"
            r0.put(r5, r12)     // Catch:{ Exception -> 0x0165 }
            goto L_0x0170
        L_0x0165:
            r0 = move-exception
            r16 = r3
            r17 = r8
            r20 = r10
            r18 = r12
            goto L_0x02fd
        L_0x0170:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureFile> r5 = r11.files     // Catch:{ Exception -> 0x02f4 }
            boolean r5 = r5.isEmpty()     // Catch:{ Exception -> 0x02f4 }
            java.lang.String r6 = "file_hash"
            if (r5 != 0) goto L_0x01f0
            org.json.JSONArray r5 = new org.json.JSONArray     // Catch:{ Exception -> 0x01e5 }
            r5.<init>()     // Catch:{ Exception -> 0x01e5 }
            r13 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureFile> r15 = r11.files     // Catch:{ Exception -> 0x01e5 }
            int r15 = r15.size()     // Catch:{ Exception -> 0x01e5 }
        L_0x0186:
            if (r13 >= r15) goto L_0x01d7
            r16 = r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureFile> r3 = r11.files     // Catch:{ Exception -> 0x01ce }
            java.lang.Object r3 = r3.get(r13)     // Catch:{ Exception -> 0x01ce }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureFile) r3     // Catch:{ Exception -> 0x01ce }
            r17 = r8
            byte[] r8 = r3.secret     // Catch:{ Exception -> 0x01c7 }
            r18 = r12
            byte[] r12 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            byte[] r8 = r1.decryptValueSecret(r8, r12)     // Catch:{ Exception -> 0x0224 }
            org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ Exception -> 0x0224 }
            r12.<init>()     // Catch:{ Exception -> 0x0224 }
            r19 = r15
            byte[] r15 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            r20 = r3
            r3 = 2
            java.lang.String r15 = android.util.Base64.encodeToString(r15, r3)     // Catch:{ Exception -> 0x0224 }
            r12.put(r6, r15)     // Catch:{ Exception -> 0x0224 }
            java.lang.String r15 = android.util.Base64.encodeToString(r8, r3)     // Catch:{ Exception -> 0x0224 }
            r12.put(r14, r15)     // Catch:{ Exception -> 0x0224 }
            r5.put(r12)     // Catch:{ Exception -> 0x0224 }
            int r13 = r13 + 1
            r3 = r16
            r8 = r17
            r12 = r18
            r15 = r19
            goto L_0x0186
        L_0x01c7:
            r0 = move-exception
            r18 = r12
            r20 = r10
            goto L_0x02fd
        L_0x01ce:
            r0 = move-exception
            r17 = r8
            r18 = r12
            r20 = r10
            goto L_0x02fd
        L_0x01d7:
            r16 = r3
            r17 = r8
            r18 = r12
            r19 = r15
            java.lang.String r3 = "files"
            r0.put(r3, r5)     // Catch:{ Exception -> 0x0224 }
            goto L_0x01f6
        L_0x01e5:
            r0 = move-exception
            r16 = r3
            r17 = r8
            r18 = r12
            r20 = r10
            goto L_0x02fd
        L_0x01f0:
            r16 = r3
            r17 = r8
            r18 = r12
        L_0x01f6:
            im.bclpbkiauv.tgnet.TLRPC$SecureFile r3 = r11.front_side     // Catch:{ Exception -> 0x02f0 }
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureFile     // Catch:{ Exception -> 0x02f0 }
            if (r3 == 0) goto L_0x0229
            im.bclpbkiauv.tgnet.TLRPC$SecureFile r3 = r11.front_side     // Catch:{ Exception -> 0x0224 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureFile) r3     // Catch:{ Exception -> 0x0224 }
            byte[] r5 = r3.secret     // Catch:{ Exception -> 0x0224 }
            byte[] r8 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            byte[] r5 = r1.decryptValueSecret(r5, r8)     // Catch:{ Exception -> 0x0224 }
            org.json.JSONObject r8 = new org.json.JSONObject     // Catch:{ Exception -> 0x0224 }
            r8.<init>()     // Catch:{ Exception -> 0x0224 }
            byte[] r12 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            r13 = 2
            java.lang.String r12 = android.util.Base64.encodeToString(r12, r13)     // Catch:{ Exception -> 0x0224 }
            r8.put(r6, r12)     // Catch:{ Exception -> 0x0224 }
            java.lang.String r12 = android.util.Base64.encodeToString(r5, r13)     // Catch:{ Exception -> 0x0224 }
            r8.put(r14, r12)     // Catch:{ Exception -> 0x0224 }
            java.lang.String r12 = "front_side"
            r0.put(r12, r8)     // Catch:{ Exception -> 0x0224 }
            goto L_0x0229
        L_0x0224:
            r0 = move-exception
            r20 = r10
            goto L_0x02fd
        L_0x0229:
            im.bclpbkiauv.tgnet.TLRPC$SecureFile r3 = r11.reverse_side     // Catch:{ Exception -> 0x02f0 }
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureFile     // Catch:{ Exception -> 0x02f0 }
            if (r3 == 0) goto L_0x0256
            im.bclpbkiauv.tgnet.TLRPC$SecureFile r3 = r11.reverse_side     // Catch:{ Exception -> 0x0224 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureFile) r3     // Catch:{ Exception -> 0x0224 }
            byte[] r5 = r3.secret     // Catch:{ Exception -> 0x0224 }
            byte[] r8 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            byte[] r5 = r1.decryptValueSecret(r5, r8)     // Catch:{ Exception -> 0x0224 }
            org.json.JSONObject r8 = new org.json.JSONObject     // Catch:{ Exception -> 0x0224 }
            r8.<init>()     // Catch:{ Exception -> 0x0224 }
            byte[] r12 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            r13 = 2
            java.lang.String r12 = android.util.Base64.encodeToString(r12, r13)     // Catch:{ Exception -> 0x0224 }
            r8.put(r6, r12)     // Catch:{ Exception -> 0x0224 }
            java.lang.String r12 = android.util.Base64.encodeToString(r5, r13)     // Catch:{ Exception -> 0x0224 }
            r8.put(r14, r12)     // Catch:{ Exception -> 0x0224 }
            java.lang.String r12 = "reverse_side"
            r0.put(r12, r8)     // Catch:{ Exception -> 0x0224 }
        L_0x0256:
            boolean r3 = r10.selfie_required     // Catch:{ Exception -> 0x02f0 }
            if (r3 == 0) goto L_0x0287
            im.bclpbkiauv.tgnet.TLRPC$SecureFile r3 = r11.selfie     // Catch:{ Exception -> 0x0224 }
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureFile     // Catch:{ Exception -> 0x0224 }
            if (r3 == 0) goto L_0x0287
            im.bclpbkiauv.tgnet.TLRPC$SecureFile r3 = r11.selfie     // Catch:{ Exception -> 0x0224 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureFile) r3     // Catch:{ Exception -> 0x0224 }
            byte[] r5 = r3.secret     // Catch:{ Exception -> 0x0224 }
            byte[] r8 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            byte[] r5 = r1.decryptValueSecret(r5, r8)     // Catch:{ Exception -> 0x0224 }
            org.json.JSONObject r8 = new org.json.JSONObject     // Catch:{ Exception -> 0x0224 }
            r8.<init>()     // Catch:{ Exception -> 0x0224 }
            byte[] r12 = r3.file_hash     // Catch:{ Exception -> 0x0224 }
            r13 = 2
            java.lang.String r12 = android.util.Base64.encodeToString(r12, r13)     // Catch:{ Exception -> 0x0224 }
            r8.put(r6, r12)     // Catch:{ Exception -> 0x0224 }
            java.lang.String r12 = android.util.Base64.encodeToString(r5, r13)     // Catch:{ Exception -> 0x0224 }
            r8.put(r14, r12)     // Catch:{ Exception -> 0x0224 }
            java.lang.String r12 = "selfie"
            r0.put(r12, r8)     // Catch:{ Exception -> 0x0224 }
        L_0x0287:
            boolean r3 = r10.translation_required     // Catch:{ Exception -> 0x02f0 }
            if (r3 == 0) goto L_0x02e1
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureFile> r3 = r11.translation     // Catch:{ Exception -> 0x02f0 }
            boolean r3 = r3.isEmpty()     // Catch:{ Exception -> 0x02f0 }
            if (r3 != 0) goto L_0x02e1
            org.json.JSONArray r3 = new org.json.JSONArray     // Catch:{ Exception -> 0x02f0 }
            r3.<init>()     // Catch:{ Exception -> 0x02f0 }
            r5 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureFile> r8 = r11.translation     // Catch:{ Exception -> 0x02f0 }
            int r8 = r8.size()     // Catch:{ Exception -> 0x02f0 }
        L_0x029f:
            if (r5 >= r8) goto L_0x02d6
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$SecureFile> r12 = r11.translation     // Catch:{ Exception -> 0x02f0 }
            java.lang.Object r12 = r12.get(r5)     // Catch:{ Exception -> 0x02f0 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r12 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureFile) r12     // Catch:{ Exception -> 0x02f0 }
            byte[] r13 = r12.secret     // Catch:{ Exception -> 0x02f0 }
            byte[] r15 = r12.file_hash     // Catch:{ Exception -> 0x02f0 }
            byte[] r13 = r1.decryptValueSecret(r13, r15)     // Catch:{ Exception -> 0x02f0 }
            org.json.JSONObject r15 = new org.json.JSONObject     // Catch:{ Exception -> 0x02f0 }
            r15.<init>()     // Catch:{ Exception -> 0x02f0 }
            r19 = r8
            byte[] r8 = r12.file_hash     // Catch:{ Exception -> 0x02f0 }
            r20 = r10
            r10 = 2
            java.lang.String r8 = android.util.Base64.encodeToString(r8, r10)     // Catch:{ Exception -> 0x02ee }
            r15.put(r6, r8)     // Catch:{ Exception -> 0x02ee }
            java.lang.String r8 = android.util.Base64.encodeToString(r13, r10)     // Catch:{ Exception -> 0x02ee }
            r15.put(r14, r8)     // Catch:{ Exception -> 0x02ee }
            r3.put(r15)     // Catch:{ Exception -> 0x02ee }
            int r5 = r5 + 1
            r8 = r19
            r10 = r20
            goto L_0x029f
        L_0x02d6:
            r19 = r8
            r20 = r10
            java.lang.String r5 = "translation"
            r0.put(r5, r3)     // Catch:{ Exception -> 0x02ee }
            goto L_0x02e3
        L_0x02e1:
            r20 = r10
        L_0x02e3:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r3 = r11.type     // Catch:{ Exception -> 0x02ee }
            java.lang.String r3 = r1.getNameForType(r3)     // Catch:{ Exception -> 0x02ee }
            r7.put(r3, r0)     // Catch:{ Exception -> 0x02ee }
            goto L_0x02fd
        L_0x02ee:
            r0 = move-exception
            goto L_0x02fd
        L_0x02f0:
            r0 = move-exception
            r20 = r10
            goto L_0x02fd
        L_0x02f4:
            r0 = move-exception
            r16 = r3
            r17 = r8
            r20 = r10
            r18 = r12
        L_0x02fd:
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValueHash r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureValueHash
            r0.<init>()
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r3 = r11.type
            r0.type = r3
            byte[] r3 = r11.hash
            r0.hash = r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_secureValueHash> r3 = r4.value_hashes
            r3.add(r0)
            int r9 = r9 + 1
            r3 = r16
            r8 = r17
            r5 = 0
            r6 = 1
            goto L_0x00f5
        L_0x0319:
            r16 = r3
            r17 = r8
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            r3 = r0
            java.lang.String r0 = "secure_data"
            r3.put(r0, r7)     // Catch:{ Exception -> 0x0329 }
            goto L_0x032a
        L_0x0329:
            r0 = move-exception
        L_0x032a:
            java.lang.String r0 = r1.currentPayload
            if (r0 == 0) goto L_0x0335
            java.lang.String r5 = "payload"
            r3.put(r5, r0)     // Catch:{ Exception -> 0x0334 }
            goto L_0x0335
        L_0x0334:
            r0 = move-exception
        L_0x0335:
            java.lang.String r0 = r1.currentNonce
            if (r0 == 0) goto L_0x0340
            java.lang.String r5 = "nonce"
            r3.put(r5, r0)     // Catch:{ Exception -> 0x033f }
            goto L_0x0340
        L_0x033f:
            r0 = move-exception
        L_0x0340:
            java.lang.String r5 = r3.toString()
            byte[] r0 = im.bclpbkiauv.messenger.AndroidUtilities.getStringBytes(r5)
            im.bclpbkiauv.ui.PassportActivity$EncryptionResult r6 = r1.encryptData(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureCredentialsEncrypted r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureCredentialsEncrypted
            r0.<init>()
            r4.credentials = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_secureCredentialsEncrypted r0 = r4.credentials
            byte[] r8 = r6.fileHash
            r0.hash = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureCredentialsEncrypted r0 = r4.credentials
            byte[] r8 = r6.encryptedData
            r0.data = r8
            java.lang.String r0 = r1.currentPublicKey     // Catch:{ Exception -> 0x03a0 }
            java.lang.String r8 = "\\n"
            java.lang.String r0 = r0.replaceAll(r8, r2)     // Catch:{ Exception -> 0x03a0 }
            java.lang.String r8 = "-----BEGIN PUBLIC KEY-----"
            java.lang.String r0 = r0.replace(r8, r2)     // Catch:{ Exception -> 0x03a0 }
            java.lang.String r8 = "-----END PUBLIC KEY-----"
            java.lang.String r0 = r0.replace(r8, r2)     // Catch:{ Exception -> 0x03a0 }
            java.lang.String r2 = "RSA"
            java.security.KeyFactory r2 = java.security.KeyFactory.getInstance(r2)     // Catch:{ Exception -> 0x03a0 }
            java.security.spec.X509EncodedKeySpec r8 = new java.security.spec.X509EncodedKeySpec     // Catch:{ Exception -> 0x03a0 }
            r9 = 0
            byte[] r9 = android.util.Base64.decode(r0, r9)     // Catch:{ Exception -> 0x03a0 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x03a0 }
            java.security.PublicKey r9 = r2.generatePublic(r8)     // Catch:{ Exception -> 0x03a0 }
            java.security.interfaces.RSAPublicKey r9 = (java.security.interfaces.RSAPublicKey) r9     // Catch:{ Exception -> 0x03a0 }
            java.lang.String r10 = "RSA/NONE/OAEPWithSHA1AndMGF1Padding"
            java.lang.String r11 = "BC"
            javax.crypto.Cipher r10 = javax.crypto.Cipher.getInstance(r10, r11)     // Catch:{ Exception -> 0x03a0 }
            r11 = 1
            r10.init(r11, r9)     // Catch:{ Exception -> 0x03a0 }
            im.bclpbkiauv.tgnet.TLRPC$TL_secureCredentialsEncrypted r11 = r4.credentials     // Catch:{ Exception -> 0x03a0 }
            byte[] r12 = r6.decrypyedFileSecret     // Catch:{ Exception -> 0x03a0 }
            byte[] r12 = r10.doFinal(r12)     // Catch:{ Exception -> 0x03a0 }
            r11.secret = r12     // Catch:{ Exception -> 0x03a0 }
            goto L_0x03a4
        L_0x03a0:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03a4:
            int r0 = r1.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)
            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$8TnkaefSoveuKiOICFNrROraJpM r2 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$8TnkaefSoveuKiOICFNrROraJpM
            r2.<init>()
            int r0 = r0.sendRequest(r4, r2)
            int r2 = r1.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r2 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r2)
            int r8 = r1.classGuid
            r2.bindRequestToGuid(r0, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.lambda$createRequestInterface$16$PassportActivity(android.view.View):void");
    }

    public /* synthetic */ void lambda$null$15$PassportActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PassportActivity.this.lambda$null$14$PassportActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$PassportActivity(TLRPC.TL_error error) {
        if (error == null) {
            this.ignoreOnFailure = true;
            callCallback(true);
            finishFragment();
            return;
        }
        showEditDoneProgress(false, false);
        if ("APP_VERSION_OUTDATED".equals(error.text)) {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    private void createManageInterface(Context context) {
        boolean documentOnly2;
        TLRPC.TL_secureRequiredType requiredType;
        ArrayList<TLRPC.TL_secureRequiredType> documentTypes;
        Context context2 = context;
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("AppPassport", R.string.AppPassport));
        this.actionBar.createMenu().addItem(1, (int) R.drawable.profile_info);
        HeaderCell headerCell2 = new HeaderCell(context2);
        this.headerCell = headerCell2;
        headerCell2.setText(LocaleController.getString("PassportProvidedInformation", R.string.PassportProvidedInformation));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
        this.sectionCell = shadowSectionCell;
        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
        this.addDocumentCell = textSettingsCell;
        textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.addDocumentCell.setText(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd), true);
        this.linearLayout2.addView(this.addDocumentCell, LayoutHelper.createLinear(-1, -2));
        this.addDocumentCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PassportActivity.this.lambda$createManageInterface$17$PassportActivity(view);
            }
        });
        TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
        this.deletePassportCell = textSettingsCell2;
        textSettingsCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
        this.deletePassportCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.deletePassportCell.setText(LocaleController.getString("AppPassportDelete", R.string.AppPassportDelete), false);
        this.linearLayout2.addView(this.deletePassportCell, LayoutHelper.createLinear(-1, -2));
        this.deletePassportCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PassportActivity.this.lambda$createManageInterface$21$PassportActivity(view);
            }
        });
        ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
        this.addDocumentSectionCell = shadowSectionCell2;
        shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.addDocumentSectionCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        if (AndroidUtilities.isTablet()) {
            this.linearLayout2.addView(this.emptyLayout, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(528.0f) - ActionBar.getCurrentActionBarHeight()));
        } else {
            this.linearLayout2.addView(this.emptyLayout, new LinearLayout.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        }
        ImageView imageView = new ImageView(context2);
        this.emptyImageView = imageView;
        imageView.setImageResource(R.drawable.no_passport);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), PorterDuff.Mode.MULTIPLY));
        this.emptyLayout.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.emptyTextView1 = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.emptyTextView1.setGravity(17);
        this.emptyTextView1.setTextSize(1, 15.0f);
        this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView1.setText(LocaleController.getString("PassportNoDocuments", R.string.PassportNoDocuments));
        this.emptyLayout.addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        TextView textView2 = new TextView(context2);
        this.emptyTextView2 = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setTextSize(1, 14.0f);
        this.emptyTextView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.emptyTextView2.setText(LocaleController.getString("PassportNoDocumentsInfo", R.string.PassportNoDocumentsInfo));
        this.emptyLayout.addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        TextView textView3 = new TextView(context2);
        this.emptyTextView3 = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setTextSize(1, 15.0f);
        this.emptyTextView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView3.setGravity(17);
        this.emptyTextView3.setText(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd).toUpperCase());
        this.emptyLayout.addView(this.emptyTextView3, LayoutHelper.createLinear(-2, 30, 17, 0, 16, 0, 0));
        this.emptyTextView3.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PassportActivity.this.lambda$createManageInterface$22$PassportActivity(view);
            }
        });
        int size = this.currentForm.values.size();
        int a = 0;
        while (a < size) {
            TLRPC.TL_secureValue value = this.currentForm.values.get(a);
            if (isPersonalDocument(value.type)) {
                ArrayList<TLRPC.TL_secureRequiredType> documentTypes2 = new ArrayList<>();
                TLRPC.TL_secureRequiredType requiredType2 = new TLRPC.TL_secureRequiredType();
                requiredType2.type = value.type;
                requiredType2.selfie_required = true;
                requiredType2.translation_required = true;
                documentTypes2.add(requiredType2);
                TLRPC.TL_secureRequiredType requiredType3 = new TLRPC.TL_secureRequiredType();
                requiredType3.type = new TLRPC.TL_secureValueTypePersonalDetails();
                documentTypes = documentTypes2;
                requiredType = requiredType3;
                documentOnly2 = true;
            } else if (isAddressDocument(value.type)) {
                ArrayList<TLRPC.TL_secureRequiredType> documentTypes3 = new ArrayList<>();
                TLRPC.TL_secureRequiredType requiredType4 = new TLRPC.TL_secureRequiredType();
                requiredType4.type = value.type;
                requiredType4.translation_required = true;
                documentTypes3.add(requiredType4);
                TLRPC.TL_secureRequiredType requiredType5 = new TLRPC.TL_secureRequiredType();
                requiredType5.type = new TLRPC.TL_secureValueTypeAddress();
                documentTypes = documentTypes3;
                requiredType = requiredType5;
                documentOnly2 = true;
            } else {
                TLRPC.TL_secureRequiredType requiredType6 = new TLRPC.TL_secureRequiredType();
                requiredType6.type = value.type;
                documentTypes = null;
                requiredType = requiredType6;
                documentOnly2 = false;
            }
            addField(context, requiredType, documentTypes, documentOnly2, a == size + -1);
            a++;
        }
        updateManageVisibility();
    }

    public /* synthetic */ void lambda$createManageInterface$17$PassportActivity(View v) {
        openAddDocumentAlert();
    }

    public /* synthetic */ void lambda$createManageInterface$21$PassportActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$null$20$PassportActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("AppPassportDeleteAlert", R.string.AppPassportDeleteAlert));
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$20$PassportActivity(DialogInterface dialog, int which) {
        TLRPC.TL_account_deleteSecureValue req = new TLRPC.TL_account_deleteSecureValue();
        for (int a = 0; a < this.currentForm.values.size(); a++) {
            req.types.add(this.currentForm.values.get(a).type);
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.lambda$null$19$PassportActivity(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$19$PassportActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                PassportActivity.this.lambda$null$18$PassportActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$18$PassportActivity() {
        int a = 0;
        while (a < this.linearLayout2.getChildCount()) {
            View child = this.linearLayout2.getChildAt(a);
            if (child instanceof TextDetailSecureCell) {
                this.linearLayout2.removeView(child);
                a--;
            }
            a++;
        }
        needHideProgress();
        this.typesViews.clear();
        this.typesValues.clear();
        this.currentForm.values.clear();
        updateManageVisibility();
    }

    public /* synthetic */ void lambda$createManageInterface$22$PassportActivity(View v) {
        openAddDocumentAlert();
    }

    private boolean hasNotValueForType(Class<? extends TLRPC.SecureValueType> type) {
        int count = this.currentForm.values.size();
        for (int a = 0; a < count; a++) {
            if (this.currentForm.values.get(a).type.getClass() == type) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUnfilledValues() {
        return hasNotValueForType(TLRPC.TL_secureValueTypePhone.class) || hasNotValueForType(TLRPC.TL_secureValueTypeEmail.class) || hasNotValueForType(TLRPC.TL_secureValueTypePersonalDetails.class) || hasNotValueForType(TLRPC.TL_secureValueTypePassport.class) || hasNotValueForType(TLRPC.TL_secureValueTypeInternalPassport.class) || hasNotValueForType(TLRPC.TL_secureValueTypeIdentityCard.class) || hasNotValueForType(TLRPC.TL_secureValueTypeDriverLicense.class) || hasNotValueForType(TLRPC.TL_secureValueTypeAddress.class) || hasNotValueForType(TLRPC.TL_secureValueTypeUtilityBill.class) || hasNotValueForType(TLRPC.TL_secureValueTypePassportRegistration.class) || hasNotValueForType(TLRPC.TL_secureValueTypeTemporaryRegistration.class) || hasNotValueForType(TLRPC.TL_secureValueTypeBankStatement.class) || hasNotValueForType(TLRPC.TL_secureValueTypeRentalAgreement.class);
    }

    private void openAddDocumentAlert() {
        ArrayList<CharSequence> values = new ArrayList<>();
        ArrayList<Class<? extends TLRPC.SecureValueType>> types = new ArrayList<>();
        if (hasNotValueForType(TLRPC.TL_secureValueTypePhone.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPhone", R.string.ActionBotDocumentPhone));
            types.add(TLRPC.TL_secureValueTypePhone.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeEmail.class)) {
            values.add(LocaleController.getString("ActionBotDocumentEmail", R.string.ActionBotDocumentEmail));
            types.add(TLRPC.TL_secureValueTypeEmail.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePersonalDetails.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentity", R.string.ActionBotDocumentIdentity));
            types.add(TLRPC.TL_secureValueTypePersonalDetails.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport));
            types.add(TLRPC.TL_secureValueTypePassport.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeInternalPassport.class)) {
            values.add(LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport));
            types.add(TLRPC.TL_secureValueTypeInternalPassport.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypePassportRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
            types.add(TLRPC.TL_secureValueTypePassportRegistration.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeTemporaryRegistration.class)) {
            values.add(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
            types.add(TLRPC.TL_secureValueTypeTemporaryRegistration.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeIdentityCard.class)) {
            values.add(LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard));
            types.add(TLRPC.TL_secureValueTypeIdentityCard.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeDriverLicense.class)) {
            values.add(LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence));
            types.add(TLRPC.TL_secureValueTypeDriverLicense.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeAddress.class)) {
            values.add(LocaleController.getString("ActionBotDocumentAddress", R.string.ActionBotDocumentAddress));
            types.add(TLRPC.TL_secureValueTypeAddress.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeUtilityBill.class)) {
            values.add(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
            types.add(TLRPC.TL_secureValueTypeUtilityBill.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeBankStatement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
            types.add(TLRPC.TL_secureValueTypeBankStatement.class);
        }
        if (hasNotValueForType(TLRPC.TL_secureValueTypeRentalAgreement.class)) {
            values.add(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
            types.add(TLRPC.TL_secureValueTypeRentalAgreement.class);
        }
        if (getParentActivity() != null && !values.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("PassportNoDocumentsAdd", R.string.PassportNoDocumentsAdd));
            builder.setItems((CharSequence[]) values.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(types) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.lambda$openAddDocumentAlert$23$PassportActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$openAddDocumentAlert$23$PassportActivity(ArrayList types, DialogInterface dialog, int which) {
        TLRPC.TL_secureRequiredType requiredType = null;
        TLRPC.TL_secureRequiredType documentRequiredType = null;
        try {
            requiredType = new TLRPC.TL_secureRequiredType();
            requiredType.type = (TLRPC.SecureValueType) ((Class) types.get(which)).newInstance();
        } catch (Exception e) {
        }
        boolean z = true;
        if (isPersonalDocument(requiredType.type)) {
            documentRequiredType = requiredType;
            documentRequiredType.selfie_required = true;
            documentRequiredType.translation_required = true;
            requiredType = new TLRPC.TL_secureRequiredType();
            requiredType.type = new TLRPC.TL_secureValueTypePersonalDetails();
        } else if (isAddressDocument(requiredType.type)) {
            documentRequiredType = requiredType;
            requiredType = new TLRPC.TL_secureRequiredType();
            requiredType.type = new TLRPC.TL_secureValueTypeAddress();
        }
        ArrayList arrayList = new ArrayList();
        if (documentRequiredType == null) {
            z = false;
        }
        openTypeActivity(requiredType, documentRequiredType, arrayList, z);
    }

    private void updateManageVisibility() {
        if (this.currentForm.values.isEmpty()) {
            this.emptyLayout.setVisibility(0);
            this.sectionCell.setVisibility(8);
            this.headerCell.setVisibility(8);
            this.addDocumentCell.setVisibility(8);
            this.deletePassportCell.setVisibility(8);
            this.addDocumentSectionCell.setVisibility(8);
            return;
        }
        this.emptyLayout.setVisibility(8);
        this.sectionCell.setVisibility(0);
        this.headerCell.setVisibility(0);
        this.deletePassportCell.setVisibility(0);
        this.addDocumentSectionCell.setVisibility(0);
        if (hasUnfilledValues()) {
            this.addDocumentCell.setVisibility(0);
        } else {
            this.addDocumentCell.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void callCallback(boolean success) {
        int i;
        int i2;
        if (this.callbackCalled) {
            return;
        }
        if (!TextUtils.isEmpty(this.currentCallbackUrl)) {
            if (success) {
                FragmentActivity parentActivity = getParentActivity();
                Browser.openUrl((Context) parentActivity, Uri.parse(this.currentCallbackUrl + "&tg_passport=success"));
            } else if (!this.ignoreOnFailure && ((i2 = this.currentActivityType) == 5 || i2 == 0)) {
                FragmentActivity parentActivity2 = getParentActivity();
                Browser.openUrl((Context) parentActivity2, Uri.parse(this.currentCallbackUrl + "&tg_passport=cancel"));
            }
            this.callbackCalled = true;
        } else if (this.needActivityResult) {
            if (success || (!this.ignoreOnFailure && ((i = this.currentActivityType) == 5 || i == 0))) {
                getParentActivity().setResult(success ? -1 : 0);
            }
            this.callbackCalled = true;
        }
    }

    private void createEmailInterface(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("PassportEmail", R.string.PassportEmail));
        if (!TextUtils.isEmpty(this.currentEmail)) {
            TextSettingsCell settingsCell1 = new TextSettingsCell(context2);
            settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            settingsCell1.setText(LocaleController.formatString("PassportPhoneUseSame", R.string.PassportPhoneUseSame, this.currentEmail), false);
            this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
            settingsCell1.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PassportActivity.this.lambda$createEmailInterface$24$PassportActivity(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell;
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportPhoneUseSameEmailInfo", R.string.PassportPhoneUseSameEmailInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        this.inputFields = new EditTextBoldCursor[1];
        for (int a = 0; a < 1; a++) {
            ViewGroup container = new FrameLayout(context2);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 50));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context2);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable((Drawable) null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setInputType(33);
            this.inputFields[a].setImeOptions(268435462);
            this.inputFields[a].setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", R.string.PaymentShippingEmailPlaceholder));
            TLRPC.TL_secureValue tL_secureValue = this.currentTypeValue;
            if (tL_secureValue != null && (tL_secureValue.plain_data instanceof TLRPC.TL_securePlainEmail)) {
                TLRPC.TL_securePlainEmail securePlainEmail = (TLRPC.TL_securePlainEmail) this.currentTypeValue.plain_data;
                if (!TextUtils.isEmpty(securePlainEmail.email)) {
                    this.inputFields[a].setText(securePlainEmail.email);
                }
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
            this.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.inputFields[a].setGravity(LocaleController.isRTL ? 5 : 3);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1.0f, -2.0f, 51, 21.0f, 12.0f, 21.0f, 6.0f));
            this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return PassportActivity.this.lambda$createEmailInterface$25$PassportActivity(textView, i, keyEvent);
                }
            });
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.bottomCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("PassportEmailUploadInfo", R.string.PassportEmailUploadInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
    }

    public /* synthetic */ void lambda$createEmailInterface$24$PassportActivity(View v) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    public /* synthetic */ boolean lambda$createEmailInterface$25$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 && i != 5) {
            return false;
        }
        this.doneItem.callOnClick();
        return true;
    }

    /* JADX WARNING: type inference failed for: r9v36, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createPhoneInterface(android.content.Context r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r1.actionBar
            java.lang.String r3 = "PassportPhone"
            r4 = 2131692744(0x7f0f0cc8, float:1.9014597E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r4)
            r0.setTitle(r3)
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r1.languageMap = r0
            r3 = 3
            r4 = 2
            r5 = 1
            r6 = 0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0076 }
            java.io.InputStreamReader r7 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0076 }
            android.content.res.Resources r8 = r25.getResources()     // Catch:{ Exception -> 0x0076 }
            android.content.res.AssetManager r8 = r8.getAssets()     // Catch:{ Exception -> 0x0076 }
            java.lang.String r9 = "countries.txt"
            java.io.InputStream r8 = r8.open(r9)     // Catch:{ Exception -> 0x0076 }
            r7.<init>(r8)     // Catch:{ Exception -> 0x0076 }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0076 }
        L_0x0035:
            java.lang.String r7 = r0.readLine()     // Catch:{ Exception -> 0x0076 }
            r8 = r7
            if (r7 == 0) goto L_0x0072
            java.lang.String r7 = ";"
            java.lang.String[] r7 = r8.split(r7)     // Catch:{ Exception -> 0x0076 }
            java.util.ArrayList<java.lang.String> r9 = r1.countriesArray     // Catch:{ Exception -> 0x0076 }
            r10 = r7[r4]     // Catch:{ Exception -> 0x0076 }
            r9.add(r6, r10)     // Catch:{ Exception -> 0x0076 }
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r1.countriesMap     // Catch:{ Exception -> 0x0076 }
            r10 = r7[r4]     // Catch:{ Exception -> 0x0076 }
            r11 = r7[r6]     // Catch:{ Exception -> 0x0076 }
            r9.put(r10, r11)     // Catch:{ Exception -> 0x0076 }
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r1.codesMap     // Catch:{ Exception -> 0x0076 }
            r10 = r7[r6]     // Catch:{ Exception -> 0x0076 }
            r11 = r7[r4]     // Catch:{ Exception -> 0x0076 }
            r9.put(r10, r11)     // Catch:{ Exception -> 0x0076 }
            int r9 = r7.length     // Catch:{ Exception -> 0x0076 }
            if (r9 <= r3) goto L_0x0067
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r1.phoneFormatMap     // Catch:{ Exception -> 0x0076 }
            r10 = r7[r6]     // Catch:{ Exception -> 0x0076 }
            r11 = r7[r3]     // Catch:{ Exception -> 0x0076 }
            r9.put(r10, r11)     // Catch:{ Exception -> 0x0076 }
        L_0x0067:
            java.util.HashMap<java.lang.String, java.lang.String> r9 = r1.languageMap     // Catch:{ Exception -> 0x0076 }
            r10 = r7[r5]     // Catch:{ Exception -> 0x0076 }
            r11 = r7[r4]     // Catch:{ Exception -> 0x0076 }
            r9.put(r10, r11)     // Catch:{ Exception -> 0x0076 }
            goto L_0x0035
        L_0x0072:
            r0.close()     // Catch:{ Exception -> 0x0076 }
            goto L_0x007a
        L_0x0076:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x007a:
            java.util.ArrayList<java.lang.String> r0 = r1.countriesArray
            im.bclpbkiauv.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE r7 = im.bclpbkiauv.ui.$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE
            java.util.Collections.sort(r0, r7)
            int r0 = r1.currentAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r0.getCurrentUser()
            java.lang.String r7 = r0.phone
            im.bclpbkiauv.ui.cells.TextSettingsCell r0 = new im.bclpbkiauv.ui.cells.TextSettingsCell
            r0.<init>(r2)
            r8 = r0
            java.lang.String r0 = "windowBackgroundWhiteBlueText4"
            int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
            r8.setTextColor(r0)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r5)
            r8.setBackgroundDrawable(r0)
            r0 = 2131692748(0x7f0f0ccc, float:1.9014605E38)
            java.lang.Object[] r9 = new java.lang.Object[r5]
            im.bclpbkiauv.phoneformat.PhoneFormat r10 = im.bclpbkiauv.phoneformat.PhoneFormat.getInstance()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "+"
            r11.append(r12)
            r11.append(r7)
            java.lang.String r11 = r11.toString()
            java.lang.String r10 = r10.format(r11)
            r9[r6] = r10
            java.lang.String r10 = "PassportPhoneUseSame"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r10, r0, r9)
            r8.setText(r0, r6)
            android.widget.LinearLayout r0 = r1.linearLayout2
            r9 = -2
            r10 = -1
            android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r9)
            r0.addView(r8, r11)
            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$Pbb57Rcd3MmTfTBT2eZsBwuCcGE r0 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$Pbb57Rcd3MmTfTBT2eZsBwuCcGE
            r0.<init>()
            r8.setOnClickListener(r0)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
            r0.<init>(r2)
            r1.bottomCell = r0
            java.lang.String r11 = "windowBackgroundGrayShadow"
            r13 = 2131231061(0x7f080155, float:1.8078192E38)
            android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r2, (int) r13, (java.lang.String) r11)
            r0.setBackgroundDrawable(r14)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = r1.bottomCell
            r14 = 2131692750(0x7f0f0cce, float:1.9014609E38)
            java.lang.String r15 = "PassportPhoneUseSameInfo"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r0.setText(r14)
            android.widget.LinearLayout r0 = r1.linearLayout2
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r14 = r1.bottomCell
            android.widget.LinearLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r9)
            r0.addView(r14, r15)
            im.bclpbkiauv.ui.cells.HeaderCell r0 = new im.bclpbkiauv.ui.cells.HeaderCell
            r0.<init>(r2)
            r1.headerCell = r0
            r14 = 2131692747(0x7f0f0ccb, float:1.9014603E38)
            java.lang.String r15 = "PassportPhoneUseOther"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            r0.setText(r14)
            im.bclpbkiauv.ui.cells.HeaderCell r0 = r1.headerCell
            java.lang.String r14 = "windowBackgroundWhite"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r0.setBackgroundColor(r15)
            android.widget.LinearLayout r0 = r1.linearLayout2
            im.bclpbkiauv.ui.cells.HeaderCell r15 = r1.headerCell
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r9)
            r0.addView(r15, r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r0 = new im.bclpbkiauv.ui.components.EditTextBoldCursor[r3]
            r1.inputFields = r0
            r0 = 0
        L_0x013c:
            if (r0 >= r3) goto L_0x0384
            if (r0 != r4) goto L_0x014a
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r13 = r1.inputFields
            im.bclpbkiauv.ui.components.HintEditText r15 = new im.bclpbkiauv.ui.components.HintEditText
            r15.<init>(r2)
            r13[r0] = r15
            goto L_0x0153
        L_0x014a:
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r13 = r1.inputFields
            im.bclpbkiauv.ui.components.EditTextBoldCursor r15 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
            r15.<init>(r2)
            r13[r0] = r15
        L_0x0153:
            r13 = 50
            if (r0 != r5) goto L_0x0173
            android.widget.LinearLayout r15 = new android.widget.LinearLayout
            r15.<init>(r2)
            r9 = r15
            android.widget.LinearLayout r9 = (android.widget.LinearLayout) r9
            r9.setOrientation(r6)
            android.widget.LinearLayout r9 = r1.linearLayout2
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r13)
            r9.addView(r15, r13)
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r15.setBackgroundColor(r9)
            goto L_0x0197
        L_0x0173:
            if (r0 != r4) goto L_0x0181
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r5]
            android.view.ViewParent r9 = r9.getParent()
            r15 = r9
            android.view.ViewGroup r15 = (android.view.ViewGroup) r15
            goto L_0x0197
        L_0x0181:
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r2)
            r15 = r9
            android.widget.LinearLayout r9 = r1.linearLayout2
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r13)
            r9.addView(r15, r13)
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r15.setBackgroundColor(r9)
        L_0x0197:
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r0)
            r9.setTag(r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r13 = 1098907648(0x41800000, float:16.0)
            r9.setTextSize(r5, r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            java.lang.String r16 = "windowBackgroundWhiteHintText"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r9.setHintTextColor(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            java.lang.String r10 = "windowBackgroundWhiteBlackText"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r9.setTextColor(r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r13 = 0
            r9.setBackgroundDrawable(r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r9.setCursorColor(r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r13 = 1101004800(0x41a00000, float:20.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            r9.setCursorSize(r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r13 = 1069547520(0x3fc00000, float:1.5)
            r9.setCursorWidth(r13)
            if (r0 != 0) goto L_0x021d
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$XNTHCZGV77ALpolC6DRZ9vrOmIc r13 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$XNTHCZGV77ALpolC6DRZ9vrOmIc
            r13.<init>()
            r9.setOnTouchListener(r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r1.inputFields
            r9 = r9[r0]
            r13 = 2131690589(0x7f0f045d, float:1.9010226E38)
            java.lang.String r5 = "ChooseCountry"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r13)
            r9.setText(r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = r1.inputFields
            r5 = r5[r0]
            r5.setInputType(r6)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = r1.inputFields
            r5 = r5[r0]
            r5.setFocusable(r6)
            goto L_0x023b
        L_0x021d:
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = r1.inputFields
            r5 = r5[r0]
            r5.setInputType(r3)
            if (r0 != r4) goto L_0x0231
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = r1.inputFields
            r5 = r5[r0]
            r9 = 268435462(0x10000006, float:2.5243567E-29)
            r5.setImeOptions(r9)
            goto L_0x023b
        L_0x0231:
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = r1.inputFields
            r5 = r5[r0]
            r9 = 268435461(0x10000005, float:2.5243564E-29)
            r5.setImeOptions(r9)
        L_0x023b:
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = r1.inputFields
            r9 = r5[r0]
            r5 = r5[r0]
            int r5 = r5.length()
            r9.setSelection(r5)
            r5 = 19
            r9 = 5
            r13 = 1
            if (r0 != r13) goto L_0x02c2
            android.widget.TextView r13 = new android.widget.TextView
            r13.<init>(r2)
            r1.plusTextView = r13
            r13.setText(r12)
            android.widget.TextView r13 = r1.plusTextView
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r13.setTextColor(r10)
            android.widget.TextView r10 = r1.plusTextView
            r3 = 1
            r13 = 1098907648(0x41800000, float:16.0)
            r10.setTextSize(r3, r13)
            android.widget.TextView r3 = r1.plusTextView
            r17 = -2
            r18 = -2
            r19 = 1101529088(0x41a80000, float:21.0)
            r20 = 1094713344(0x41400000, float:12.0)
            r21 = 0
            r22 = 1086324736(0x40c00000, float:6.0)
            android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (float) r19, (float) r20, (float) r21, (float) r22)
            r15.addView(r3, r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r10 = 1092616192(0x41200000, float:10.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r3.setPadding(r10, r6, r6, r6)
            r3 = 1
            android.text.InputFilter[] r10 = new android.text.InputFilter[r3]
            android.text.InputFilter$LengthFilter r3 = new android.text.InputFilter$LengthFilter
            r3.<init>(r9)
            r10[r6] = r3
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r3.setFilters(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r3.setGravity(r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r17 = 55
            r19 = 0
            r21 = 1098907648(0x41800000, float:16.0)
            android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (float) r19, (float) r20, (float) r21, (float) r22)
            r15.addView(r3, r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            im.bclpbkiauv.ui.PassportActivity$9 r5 = new im.bclpbkiauv.ui.PassportActivity$9
            r5.<init>()
            r3.addTextChangedListener(r5)
            goto L_0x0341
        L_0x02c2:
            if (r0 != r4) goto L_0x030e
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r3.setPadding(r6, r6, r6, r6)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r3.setGravity(r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r5 = 0
            r3.setHintText(r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r5 = 2131692968(0x7f0f0da8, float:1.9015051E38)
            java.lang.String r9 = "PaymentShippingPhoneNumber"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            r3.setHint(r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r17 = -1
            r18 = -2
            r19 = 0
            r20 = 1094713344(0x41400000, float:12.0)
            r21 = 1101529088(0x41a80000, float:21.0)
            r22 = 1086324736(0x40c00000, float:6.0)
            android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (float) r19, (float) r20, (float) r21, (float) r22)
            r15.addView(r3, r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            im.bclpbkiauv.ui.PassportActivity$10 r5 = new im.bclpbkiauv.ui.PassportActivity$10
            r5.<init>()
            r3.addTextChangedListener(r5)
            goto L_0x0341
        L_0x030e:
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r5 = 1086324736(0x40c00000, float:6.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r3.setPadding(r6, r6, r6, r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0324
            goto L_0x0325
        L_0x0324:
            r9 = 3
        L_0x0325:
            r3.setGravity(r9)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            r17 = -1082130432(0xffffffffbf800000, float:-1.0)
            r18 = -1073741824(0xffffffffc0000000, float:-2.0)
            r19 = 51
            r20 = 1101529088(0x41a80000, float:21.0)
            r21 = 1094713344(0x41400000, float:12.0)
            r22 = 1101529088(0x41a80000, float:21.0)
            r23 = 1086324736(0x40c00000, float:6.0)
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r15.addView(r3, r5)
        L_0x0341:
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$SXDoe29OdsbezR0mJ1tNrQGT_9I r5 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$SXDoe29OdsbezR0mJ1tNrQGT_9I
            r5.<init>()
            r3.setOnEditorActionListener(r5)
            if (r0 != r4) goto L_0x035b
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r1.inputFields
            r3 = r3[r0]
            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$u3iCi0UVigPkYfp0XM6aM0DfeLs r5 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$u3iCi0UVigPkYfp0XM6aM0DfeLs
            r5.<init>()
            r3.setOnKeyListener(r5)
        L_0x035b:
            if (r0 != 0) goto L_0x037c
            android.view.View r3 = new android.view.View
            r3.<init>(r2)
            java.util.ArrayList<android.view.View> r5 = r1.dividers
            r5.add(r3)
            java.lang.String r5 = "divider"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            r3.setBackgroundColor(r5)
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            r9 = 83
            r10 = -1
            r13 = 1
            r5.<init>(r10, r13, r9)
            r15.addView(r3, r5)
        L_0x037c:
            int r0 = r0 + 1
            r3 = 3
            r5 = 1
            r9 = -2
            r10 = -1
            goto L_0x013c
        L_0x0384:
            r3 = 0
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x039b }
            java.lang.String r4 = "phone"
            java.lang.Object r0 = r0.getSystemService(r4)     // Catch:{ Exception -> 0x039b }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ Exception -> 0x039b }
            if (r0 == 0) goto L_0x039a
            java.lang.String r4 = r0.getSimCountryIso()     // Catch:{ Exception -> 0x039b }
            java.lang.String r4 = r4.toUpperCase()     // Catch:{ Exception -> 0x039b }
            r3 = r4
        L_0x039a:
            goto L_0x039f
        L_0x039b:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x039f:
            if (r3 == 0) goto L_0x03c4
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r1.languageMap
            java.lang.Object r0 = r0.get(r3)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x03c4
            java.util.ArrayList<java.lang.String> r4 = r1.countriesArray
            int r4 = r4.indexOf(r0)
            r5 = -1
            if (r4 == r5) goto L_0x03c4
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = r1.inputFields
            r6 = 1
            r5 = r5[r6]
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.countriesMap
            java.lang.Object r6 = r6.get(r0)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            r5.setText(r6)
        L_0x03c4:
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
            r0.<init>(r2)
            r1.bottomCell = r0
            r4 = 2131231061(0x7f080155, float:1.8078192E38)
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r11)
            r0.setBackgroundDrawable(r4)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = r1.bottomCell
            r4 = 2131692746(0x7f0f0cca, float:1.90146E38)
            java.lang.String r5 = "PassportPhoneUploadInfo"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            android.widget.LinearLayout r0 = r1.linearLayout2
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r4 = r1.bottomCell
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r6, (int) r5)
            r0.addView(r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.createPhoneInterface(android.content.Context):void");
    }

    public /* synthetic */ void lambda$createPhoneInterface$26$PassportActivity(View v) {
        this.useCurrentValue = true;
        this.doneItem.callOnClick();
        this.useCurrentValue = false;
    }

    public /* synthetic */ boolean lambda$createPhoneInterface$29$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.lambda$null$28$PassportActivity(country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$28$PassportActivity(CountrySelectActivity.Country country) {
        if (country != null) {
            this.inputFields[0].setText(country.name);
            if (this.countriesArray.indexOf(country.name) != -1) {
                this.ignoreOnTextChange = true;
                String code = this.countriesMap.get(country.name);
                this.inputFields[1].setText(code);
                String hint = this.phoneFormatMap.get(code);
                this.inputFields[2].setHintText(hint != null ? hint.replace('X', Typography.ndash) : null);
                this.ignoreOnTextChange = false;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PassportActivity.this.lambda$null$27$PassportActivity();
                }
            }, 300);
            this.inputFields[2].requestFocus();
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[2].setSelection(editTextBoldCursorArr[2].length());
        }
    }

    public /* synthetic */ void lambda$null$27$PassportActivity() {
        AndroidUtilities.showKeyboard(this.inputFields[2]);
    }

    public /* synthetic */ boolean lambda$createPhoneInterface$30$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            this.inputFields[2].requestFocus();
            return true;
        } else if (i != 6) {
            return false;
        } else {
            this.doneItem.callOnClick();
            return true;
        }
    }

    public /* synthetic */ boolean lambda$createPhoneInterface$31$PassportActivity(View v, int keyCode, KeyEvent event) {
        if (keyCode != 67 || this.inputFields[2].length() != 0) {
            return false;
        }
        this.inputFields[1].requestFocus();
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        editTextBoldCursorArr[1].setSelection(editTextBoldCursorArr[1].length());
        this.inputFields[1].dispatchKeyEvent(event);
        return true;
    }

    private void createAddressInterface(Context context) {
        final String key;
        Context context2 = context;
        this.languageMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] args = line.split(";");
                this.languageMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.topErrorCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        TLRPC.TL_secureRequiredType tL_secureRequiredType = this.currentDocumentsType;
        if (tL_secureRequiredType != null) {
            if (tL_secureRequiredType.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration));
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                this.actionBar.setTitle(LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration));
            }
            HeaderCell headerCell2 = new HeaderCell(context2);
            this.headerCell = headerCell2;
            headerCell2.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout = new LinearLayout(context2);
            this.documentsLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.linearLayout2.addView(this.documentsLayout, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.uploadDocumentCell = textSettingsCell;
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.uploadDocumentCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PassportActivity.this.lambda$createAddressInterface$32$PassportActivity(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            if (this.currentBotId != 0) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAddressUploadInfo", R.string.PassportAddAddressUploadInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddAgreementInfo", R.string.PassportAddAgreementInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBillInfo", R.string.PassportAddBillInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddPassportRegistrationInfo", R.string.PassportAddPassportRegistrationInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddTemporaryRegistrationInfo", R.string.PassportAddTemporaryRegistrationInfo);
            } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                this.noAllDocumentsErrorText = LocaleController.getString("PassportAddBankInfo", R.string.PassportAddBankInfo);
            } else {
                this.noAllDocumentsErrorText = "";
            }
            CharSequence text = this.noAllDocumentsErrorText;
            HashMap<String, String> hashMap = this.documentsErrors;
            if (hashMap != null) {
                String str = hashMap.get("files_all");
                String errorText = str;
                if (str != null) {
                    String errorText2 = errorText;
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(errorText2);
                    stringBuilder.append("\n\n");
                    stringBuilder.append(this.noAllDocumentsErrorText);
                    text = stringBuilder;
                    stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, errorText2.length(), 33);
                    this.errorsValues.put("files_all", "");
                }
            }
            this.bottomCell.setText(text);
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                HeaderCell headerCell3 = new HeaderCell(context2);
                this.headerCell = headerCell3;
                headerCell3.setText(LocaleController.getString("PassportTranslation", R.string.PassportTranslation));
                this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout3 = new LinearLayout(context2);
                this.translationLayout = linearLayout3;
                linearLayout3.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
                this.uploadTranslationCell = textSettingsCell2;
                textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PassportActivity.this.lambda$createAddressInterface$33$PassportActivity(view);
                    }
                });
                TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
                this.bottomCellTranslation = textInfoPrivacyCell3;
                textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", R.string.PassportAddTranslationUploadInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationAgreementInfo", R.string.PassportAddTranslationAgreementInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBillInfo", R.string.PassportAddTranslationBillInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationPassportRegistrationInfo", R.string.PassportAddTranslationPassportRegistrationInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationTemporaryRegistrationInfo", R.string.PassportAddTranslationTemporaryRegistrationInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationBankInfo", R.string.PassportAddTranslationBankInfo);
                } else {
                    this.noAllTranslationErrorText = "";
                }
                CharSequence text2 = this.noAllTranslationErrorText;
                HashMap<String, String> hashMap2 = this.documentsErrors;
                if (hashMap2 != null) {
                    String str2 = hashMap2.get("translation_all");
                    String errorText3 = str2;
                    if (str2 != null) {
                        SpannableStringBuilder stringBuilder2 = new SpannableStringBuilder(errorText3);
                        stringBuilder2.append("\n\n");
                        stringBuilder2.append(this.noAllTranslationErrorText);
                        text2 = stringBuilder2;
                        stringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, errorText3.length(), 33);
                        this.errorsValues.put("translation_all", "");
                    }
                }
                this.bottomCellTranslation.setText(text2);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
        }
        HeaderCell headerCell4 = new HeaderCell(context2);
        this.headerCell = headerCell4;
        headerCell4.setText(LocaleController.getString("PassportAddressHeader", R.string.PassportAddressHeader));
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[6];
        int a = 0;
        for (int i = 6; a < i; i = 6) {
            final EditTextBoldCursor field = new EditTextBoldCursor(context2);
            this.inputFields[a] = field;
            ViewGroup container = new FrameLayout(context2) {
                private StaticLayout errorLayout;
                float offsetX;

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                    StaticLayout errorLayout2 = field.getErrorLayout(width);
                    this.errorLayout = errorLayout2;
                    if (errorLayout2 != null) {
                        int lineCount = errorLayout2.getLineCount();
                        if (lineCount > 1) {
                            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), 1073741824);
                        }
                        if (LocaleController.isRTL != 0) {
                            float maxW = 0.0f;
                            int a = 0;
                            while (true) {
                                if (a >= lineCount) {
                                    break;
                                } else if (this.errorLayout.getLineLeft(a) != 0.0f) {
                                    this.offsetX = 0.0f;
                                    break;
                                } else {
                                    maxW = Math.max(maxW, this.errorLayout.getLineWidth(a));
                                    if (a == lineCount - 1) {
                                        this.offsetX = ((float) width) - maxW;
                                    }
                                    a++;
                                }
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (this.errorLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, field.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                        this.errorLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            container.setWillNotDraw(false);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, -2));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            int i2 = 5;
            if (a == 5) {
                View view = new View(context2);
                this.extraBackgroundView = view;
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(-1, i));
            }
            if (this.documentOnly && this.currentDocumentsType != null) {
                container.setVisibility(8);
                View view2 = this.extraBackgroundView;
                if (view2 != null) {
                    view2.setVisibility(8);
                }
            }
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setSupportRtlHint(true);
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable((Drawable) null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            if (a == 5) {
                this.inputFields[a].setOnTouchListener(new View.OnTouchListener() {
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return PassportActivity.this.lambda$createAddressInterface$35$PassportActivity(view, motionEvent);
                    }
                });
                this.inputFields[a].setInputType(0);
                this.inputFields[a].setFocusable(false);
            } else {
                this.inputFields[a].setInputType(16385);
                this.inputFields[a].setImeOptions(268435461);
            }
            if (a == 0) {
                this.inputFields[a].setHintText(LocaleController.getString("PassportStreet1", R.string.PassportStreet1));
                key = "street_line1";
            } else if (a == 1) {
                this.inputFields[a].setHintText(LocaleController.getString("PassportStreet2", R.string.PassportStreet2));
                key = "street_line2";
            } else if (a == 2) {
                this.inputFields[a].setHintText(LocaleController.getString("PassportPostcode", R.string.PassportPostcode));
                key = "post_code";
            } else if (a == 3) {
                this.inputFields[a].setHintText(LocaleController.getString("PassportCity", R.string.PassportCity));
                key = "city";
            } else if (a == 4) {
                this.inputFields[a].setHintText(LocaleController.getString("PassportState", R.string.PassportState));
                key = RemoteConfigConstants.ResponseFieldKey.STATE;
            } else if (a != 5) {
                a++;
            } else {
                this.inputFields[a].setHintText(LocaleController.getString("PassportCountry", R.string.PassportCountry));
                key = "country_code";
            }
            setFieldValues(this.currentValues, this.inputFields[a], key);
            if (a == 2) {
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    private boolean ignore;

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!this.ignore) {
                            this.ignore = true;
                            boolean error = false;
                            int a = 0;
                            while (true) {
                                if (a >= s.length()) {
                                    break;
                                }
                                char ch = s.charAt(a);
                                if ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && !((ch >= '0' && ch <= '9') || ch == '-' || ch == ' '))) {
                                    error = true;
                                    break;
                                }
                                a++;
                            }
                            this.ignore = false;
                            if (error) {
                                field.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                            } else {
                                PassportActivity.this.checkFieldForError(field, key, s, false);
                            }
                        }
                    }
                });
                this.inputFields[a].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            } else {
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        PassportActivity.this.checkFieldForError(field, key, s, false);
                    }
                });
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
            this.inputFields[a].setPadding(0, 0, 0, 0);
            EditTextBoldCursor editTextBoldCursor = this.inputFields[a];
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            editTextBoldCursor.setGravity(i2 | 16);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1.0f, 64.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return PassportActivity.this.lambda$createAddressInterface$36$PassportActivity(textView, i, keyEvent);
                }
            });
            a++;
        }
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
        this.sectionCell = shadowSectionCell;
        this.linearLayout2.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.documentOnly && this.currentDocumentsType != null) {
            this.headerCell.setVisibility(8);
            this.sectionCell.setVisibility(8);
        }
        if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
            TLRPC.TL_secureValue tL_secureValue = this.currentDocumentsTypeValue;
            if (tL_secureValue != null) {
                addDocumentViews(tL_secureValue.files);
                addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
            }
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            TextSettingsCell settingsCell1 = new TextSettingsCell(context2);
            settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            if (this.currentDocumentsType == null) {
                settingsCell1.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
            } else {
                settingsCell1.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
            }
            this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
            settingsCell1.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PassportActivity.this.lambda$createAddressInterface$37$PassportActivity(view);
                }
            });
            ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
            this.sectionCell = shadowSectionCell2;
            shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            if (this.documentOnly && this.currentDocumentsType != null) {
                this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        }
        updateUploadText(0);
        updateUploadText(4);
    }

    public /* synthetic */ void lambda$createAddressInterface$32$PassportActivity(View v) {
        this.uploadingFileType = 0;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createAddressInterface$33$PassportActivity(View v) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    public /* synthetic */ boolean lambda$createAddressInterface$35$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.lambda$null$34$PassportActivity(country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$34$PassportActivity(CountrySelectActivity.Country country) {
        if (country != null) {
            this.inputFields[5].setText(country.name);
            this.currentCitizeship = country.shortname;
        }
    }

    public /* synthetic */ boolean lambda$createAddressInterface$36$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int num = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (num < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[num].isFocusable()) {
                this.inputFields[num].requestFocus();
            } else {
                this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$createAddressInterface$37$PassportActivity(View v) {
        createDocumentDeleteAlert();
    }

    private void createDocumentDeleteAlert() {
        boolean[] checks = {true};
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(checks) {
            private final /* synthetic */ boolean[] f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$createDocumentDeleteAlert$38$PassportActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (this.documentOnly && this.currentDocumentsType == null && (this.currentType.type instanceof TLRPC.TL_secureValueTypeAddress)) {
            builder.setMessage(LocaleController.getString("PassportDeleteAddressAlert", R.string.PassportDeleteAddressAlert));
        } else if (!this.documentOnly || this.currentDocumentsType != null || !(this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails)) {
            builder.setMessage(LocaleController.getString("PassportDeleteDocumentAlert", R.string.PassportDeleteDocumentAlert));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeletePersonalAlert", R.string.PassportDeletePersonalAlert));
        }
        if (!this.documentOnly && this.currentDocumentsType != null) {
            FrameLayout frameLayout = new FrameLayout(getParentActivity());
            CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
            cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.currentType.type instanceof TLRPC.TL_secureValueTypeAddress) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentAddress", R.string.PassportDeleteDocumentAddress), "", true, false);
            } else if (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                cell.setText(LocaleController.getString("PassportDeleteDocumentPersonal", R.string.PassportDeleteDocumentPersonal), "", true, false);
            }
            cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48, 51));
            cell.setOnClickListener(new View.OnClickListener(checks) {
                private final /* synthetic */ boolean[] f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(View view) {
                    PassportActivity.lambda$createDocumentDeleteAlert$39(this.f$0, view);
                }
            });
            builder.setView(frameLayout);
        }
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$createDocumentDeleteAlert$38$PassportActivity(boolean[] checks, DialogInterface dialog, int which) {
        if (!this.documentOnly) {
            this.currentValues.clear();
        }
        this.currentDocumentValues.clear();
        this.delegate.deleteValue(this.currentType, this.currentDocumentsType, this.availableDocumentTypes, checks[0], (Runnable) null, (ErrorRunnable) null);
        finishFragment();
    }

    static /* synthetic */ void lambda$createDocumentDeleteAlert$39(boolean[] checks, View v) {
        if (v.isEnabled()) {
            checks[0] = !checks[0];
            ((CheckBoxCell) v).setChecked(checks[0], true);
        }
    }

    /* access modifiers changed from: private */
    public void onFieldError(View field) {
        if (field != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(field, 2.0f, 0);
            scrollToField(field);
        }
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void scrollToField(android.view.View r6) {
        /*
            r5 = this;
        L_0x0000:
            if (r6 == 0) goto L_0x0012
            android.widget.LinearLayout r0 = r5.linearLayout2
            int r0 = r0.indexOfChild(r6)
            if (r0 >= 0) goto L_0x0012
            android.view.ViewParent r0 = r6.getParent()
            r6 = r0
            android.view.View r6 = (android.view.View) r6
            goto L_0x0000
        L_0x0012:
            if (r6 == 0) goto L_0x002c
            android.widget.ScrollView r0 = r5.scrollView
            r1 = 0
            int r2 = r6.getTop()
            android.widget.ScrollView r3 = r5.scrollView
            int r3 = r3.getMeasuredHeight()
            int r4 = r6.getMeasuredHeight()
            int r3 = r3 - r4
            int r3 = r3 / 2
            int r2 = r2 - r3
            r0.smoothScrollTo(r1, r2)
        L_0x002c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.scrollToField(android.view.View):void");
    }

    /* access modifiers changed from: private */
    public String getDocumentHash(SecureDocument document) {
        if (document == null) {
            return "";
        }
        if (document.secureFile != null && document.secureFile.file_hash != null) {
            return Base64.encodeToString(document.secureFile.file_hash, 2);
        }
        if (document.fileHash != null) {
            return Base64.encodeToString(document.fileHash, 2);
        }
        return "";
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkFieldForError(im.bclpbkiauv.ui.components.EditTextBoldCursor r4, java.lang.String r5, android.text.Editable r6, boolean r7) {
        /*
            r3 = this;
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r3.errorsValues
            r1 = 0
            if (r0 == 0) goto L_0x003a
            java.lang.Object r0 = r0.get(r5)
            java.lang.String r0 = (java.lang.String) r0
            r2 = r0
            if (r0 == 0) goto L_0x003a
            boolean r0 = android.text.TextUtils.equals(r2, r6)
            if (r0 == 0) goto L_0x0036
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r3.fieldsErrors
            if (r0 == 0) goto L_0x0025
            java.lang.Object r0 = r0.get(r5)
            java.lang.String r0 = (java.lang.String) r0
            r2 = r0
            if (r0 == 0) goto L_0x0025
            r4.setErrorText(r2)
            goto L_0x003d
        L_0x0025:
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r3.documentsErrors
            if (r0 == 0) goto L_0x003d
            java.lang.Object r0 = r0.get(r5)
            java.lang.String r0 = (java.lang.String) r0
            r1 = r0
            if (r0 == 0) goto L_0x003d
            r4.setErrorText(r1)
            goto L_0x003d
        L_0x0036:
            r4.setErrorText(r1)
            goto L_0x003d
        L_0x003a:
            r4.setErrorText(r1)
        L_0x003d:
            if (r7 == 0) goto L_0x0042
            java.lang.String r0 = "error_document_all"
            goto L_0x0044
        L_0x0042:
            java.lang.String r0 = "error_all"
        L_0x0044:
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r3.errorsValues
            if (r1 == 0) goto L_0x0057
            boolean r1 = r1.containsKey(r0)
            if (r1 == 0) goto L_0x0057
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r3.errorsValues
            r1.remove(r0)
            r1 = 0
            r3.checkTopErrorCell(r1)
        L_0x0057:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.checkFieldForError(im.bclpbkiauv.ui.components.EditTextBoldCursor, java.lang.String, android.text.Editable, boolean):void");
    }

    /* access modifiers changed from: private */
    public boolean checkFieldsForError() {
        EditTextBoldCursor[] fields;
        String key;
        TextDetailSettingsCell textDetailSettingsCell;
        if (this.currentDocumentsType != null) {
            if (this.errorsValues.containsKey("error_all") || this.errorsValues.containsKey("error_document_all")) {
                onFieldError(this.topErrorCell);
                return true;
            }
            if (this.uploadDocumentCell != null) {
                if (this.documents.isEmpty()) {
                    onFieldError(this.uploadDocumentCell);
                    return true;
                }
                int a = 0;
                int size = this.documents.size();
                while (a < size) {
                    SecureDocument document = this.documents.get(a);
                    String key2 = "files" + getDocumentHash(document);
                    if (key2 == null || !this.errorsValues.containsKey(key2)) {
                        a++;
                    } else {
                        onFieldError(this.documentsCells.get(document));
                        return true;
                    }
                }
            }
            if (this.errorsValues.containsKey("files_all") || this.errorsValues.containsKey("translation_all")) {
                onFieldError(this.bottomCell);
                return true;
            }
            TextDetailSettingsCell textDetailSettingsCell2 = this.uploadFrontCell;
            if (textDetailSettingsCell2 != null) {
                if (this.frontDocument == null) {
                    onFieldError(textDetailSettingsCell2);
                    return true;
                }
                if (this.errorsValues.containsKey("front" + getDocumentHash(this.frontDocument))) {
                    onFieldError(this.documentsCells.get(this.frontDocument));
                    return true;
                }
            }
            if (((this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) || (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) && (textDetailSettingsCell = this.uploadReverseCell) != null) {
                if (this.reverseDocument == null) {
                    onFieldError(textDetailSettingsCell);
                    return true;
                }
                if (this.errorsValues.containsKey("reverse" + getDocumentHash(this.reverseDocument))) {
                    onFieldError(this.documentsCells.get(this.reverseDocument));
                    return true;
                }
            }
            TextDetailSettingsCell textDetailSettingsCell3 = this.uploadSelfieCell;
            if (!(textDetailSettingsCell3 == null || this.currentBotId == 0)) {
                if (this.selfieDocument == null) {
                    onFieldError(textDetailSettingsCell3);
                    return true;
                }
                if (this.errorsValues.containsKey("selfie" + getDocumentHash(this.selfieDocument))) {
                    onFieldError(this.documentsCells.get(this.selfieDocument));
                    return true;
                }
            }
            if (!(this.uploadTranslationCell == null || this.currentBotId == 0)) {
                if (this.translationDocuments.isEmpty()) {
                    onFieldError(this.uploadTranslationCell);
                    return true;
                }
                int a2 = 0;
                int size2 = this.translationDocuments.size();
                while (a2 < size2) {
                    SecureDocument document2 = this.translationDocuments.get(a2);
                    String key3 = "translation" + getDocumentHash(document2);
                    if (key3 == null || !this.errorsValues.containsKey(key3)) {
                        a2++;
                    } else {
                        onFieldError(this.documentsCells.get(document2));
                        return true;
                    }
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                fields = this.inputFields;
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell = this.nativeInfoCell;
                fields = (textInfoPrivacyCell == null || textInfoPrivacyCell.getVisibility() != 0) ? null : this.inputExtraFields;
            }
            if (fields != null) {
                for (int a3 = 0; a3 < fields.length; a3++) {
                    boolean error = false;
                    if (fields[a3].hasErrorText()) {
                        error = true;
                    }
                    if (!this.errorsValues.isEmpty()) {
                        if (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                            if (i == 0) {
                                switch (a3) {
                                    case 0:
                                        key = "first_name";
                                        break;
                                    case 1:
                                        key = "middle_name";
                                        break;
                                    case 2:
                                        key = "last_name";
                                        break;
                                    case 3:
                                        key = "birth_date";
                                        break;
                                    case 4:
                                        key = "gender";
                                        break;
                                    case 5:
                                        key = "country_code";
                                        break;
                                    case 6:
                                        key = "residence_country_code";
                                        break;
                                    case 7:
                                        key = "document_no";
                                        break;
                                    case 8:
                                        key = "expiry_date";
                                        break;
                                    default:
                                        key = null;
                                        break;
                                }
                            } else if (a3 == 0) {
                                key = "first_name_native";
                            } else if (a3 == 1) {
                                key = "middle_name_native";
                            } else if (a3 != 2) {
                                key = null;
                            } else {
                                key = "last_name_native";
                            }
                        } else if (!(this.currentType.type instanceof TLRPC.TL_secureValueTypeAddress)) {
                            key = null;
                        } else if (a3 == 0) {
                            key = "street_line1";
                        } else if (a3 == 1) {
                            key = "street_line2";
                        } else if (a3 == 2) {
                            key = "post_code";
                        } else if (a3 == 3) {
                            key = "city";
                        } else if (a3 == 4) {
                            key = RemoteConfigConstants.ResponseFieldKey.STATE;
                        } else if (a3 != 5) {
                            key = null;
                        } else {
                            key = "country_code";
                        }
                        if (key != null) {
                            String value = this.errorsValues.get(key);
                            if (!TextUtils.isEmpty(value) && value.equals(fields[a3].getText().toString())) {
                                error = true;
                            }
                        }
                    }
                    if (!this.documentOnly || this.currentDocumentsType == null || a3 >= 7) {
                        if (!error) {
                            int len = fields[a3].length();
                            boolean allowZeroLength = false;
                            int i2 = this.currentActivityType;
                            if (i2 == 1) {
                                if (a3 == 8) {
                                    continue;
                                } else if ((i == 0 && (a3 == 0 || a3 == 2 || a3 == 1)) || (i == 1 && (a3 == 0 || a3 == 1 || a3 == 2))) {
                                    if (len > 255) {
                                        error = true;
                                    }
                                    if ((i == 0 && a3 == 1) || (i == 1 && a3 == 1)) {
                                        allowZeroLength = true;
                                    }
                                } else if (a3 == 7 && len > 24) {
                                    error = true;
                                }
                            } else if (i2 == 2) {
                                if (a3 == 1) {
                                    continue;
                                } else if (a3 == 3) {
                                    if (len < 2) {
                                        error = true;
                                    }
                                } else if (a3 == 4) {
                                    if (!"US".equals(this.currentCitizeship)) {
                                        continue;
                                    } else if (len < 2) {
                                        error = true;
                                    }
                                } else if (a3 == 2 && (len < 2 || len > 10)) {
                                    error = true;
                                }
                            }
                            if (!error && !allowZeroLength && len == 0) {
                                error = true;
                            }
                        }
                        if (error) {
                            onFieldError(fields[a3]);
                            return true;
                        }
                    }
                }
                continue;
            }
        }
        return false;
    }

    private void createIdentityInterface(Context context) {
        HashMap<String, String> values;
        final String key;
        final HashMap<String, String> values2;
        final String key2;
        Context context2 = context;
        this.languageMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] args = line.split(";");
                this.languageMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.topErrorCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
        boolean z = false;
        this.topErrorCell.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        int i = -1;
        this.linearLayout2.addView(this.topErrorCell, LayoutHelper.createLinear(-1, -2));
        checkTopErrorCell(true);
        if (this.currentDocumentsType != null) {
            HeaderCell headerCell2 = new HeaderCell(context2);
            this.headerCell = headerCell2;
            if (this.documentOnly) {
                headerCell2.setText(LocaleController.getString("PassportDocuments", R.string.PassportDocuments));
            } else {
                headerCell2.setText(LocaleController.getString("PassportRequiredDocuments", R.string.PassportRequiredDocuments));
            }
            this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout = new LinearLayout(context2);
            this.frontLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.linearLayout2.addView(this.frontLayout, LayoutHelper.createLinear(-1, -2));
            TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(context2);
            this.uploadFrontCell = textDetailSettingsCell;
            textDetailSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.linearLayout2.addView(this.uploadFrontCell, LayoutHelper.createLinear(-1, -2));
            this.uploadFrontCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PassportActivity.this.lambda$createIdentityInterface$40$PassportActivity(view);
                }
            });
            LinearLayout linearLayout3 = new LinearLayout(context2);
            this.reverseLayout = linearLayout3;
            linearLayout3.setOrientation(1);
            this.linearLayout2.addView(this.reverseLayout, LayoutHelper.createLinear(-1, -2));
            boolean divider = this.currentDocumentsType.selfie_required;
            TextDetailSettingsCell textDetailSettingsCell2 = new TextDetailSettingsCell(context2);
            this.uploadReverseCell = textDetailSettingsCell2;
            textDetailSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.uploadReverseCell.setTextAndValue(LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide), LocaleController.getString("PassportReverseSideInfo", R.string.PassportReverseSideInfo), divider);
            this.linearLayout2.addView(this.uploadReverseCell, LayoutHelper.createLinear(-1, -2));
            this.uploadReverseCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PassportActivity.this.lambda$createIdentityInterface$41$PassportActivity(view);
                }
            });
            if (this.currentDocumentsType.selfie_required) {
                LinearLayout linearLayout4 = new LinearLayout(context2);
                this.selfieLayout = linearLayout4;
                linearLayout4.setOrientation(1);
                this.linearLayout2.addView(this.selfieLayout, LayoutHelper.createLinear(-1, -2));
                TextDetailSettingsCell textDetailSettingsCell3 = new TextDetailSettingsCell(context2);
                this.uploadSelfieCell = textDetailSettingsCell3;
                textDetailSettingsCell3.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.uploadSelfieCell.setTextAndValue(LocaleController.getString("PassportSelfie", R.string.PassportSelfie), LocaleController.getString("PassportSelfieInfo", R.string.PassportSelfieInfo), this.currentType.translation_required);
                this.linearLayout2.addView(this.uploadSelfieCell, LayoutHelper.createLinear(-1, -2));
                this.uploadSelfieCell.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PassportActivity.this.lambda$createIdentityInterface$42$PassportActivity(view);
                    }
                });
            }
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportPersonalUploadInfo", R.string.PassportPersonalUploadInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
            if (this.currentDocumentsType.translation_required) {
                HeaderCell headerCell3 = new HeaderCell(context2);
                this.headerCell = headerCell3;
                headerCell3.setText(LocaleController.getString("PassportTranslation", R.string.PassportTranslation));
                this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout5 = new LinearLayout(context2);
                this.translationLayout = linearLayout5;
                linearLayout5.setOrientation(1);
                this.linearLayout2.addView(this.translationLayout, LayoutHelper.createLinear(-1, -2));
                TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
                this.uploadTranslationCell = textSettingsCell;
                textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.linearLayout2.addView(this.uploadTranslationCell, LayoutHelper.createLinear(-1, -2));
                this.uploadTranslationCell.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        PassportActivity.this.lambda$createIdentityInterface$43$PassportActivity(view);
                    }
                });
                TextInfoPrivacyCell textInfoPrivacyCell3 = new TextInfoPrivacyCell(context2);
                this.bottomCellTranslation = textInfoPrivacyCell3;
                textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                if (this.currentBotId != 0) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddTranslationUploadInfo", R.string.PassportAddTranslationUploadInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddPassportInfo", R.string.PassportAddPassportInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddInternalPassportInfo", R.string.PassportAddInternalPassportInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddIdentityCardInfo", R.string.PassportAddIdentityCardInfo);
                } else if (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                    this.noAllTranslationErrorText = LocaleController.getString("PassportAddDriverLicenceInfo", R.string.PassportAddDriverLicenceInfo);
                } else {
                    this.noAllTranslationErrorText = "";
                }
                CharSequence text = this.noAllTranslationErrorText;
                HashMap<String, String> hashMap = this.documentsErrors;
                if (hashMap != null) {
                    String str = hashMap.get("translation_all");
                    String errorText = str;
                    if (str != null) {
                        String errorText2 = errorText;
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(errorText2);
                        stringBuilder.append("\n\n");
                        stringBuilder.append(this.noAllTranslationErrorText);
                        text = stringBuilder;
                        stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, errorText2.length(), 33);
                        this.errorsValues.put("translation_all", "");
                    }
                }
                this.bottomCellTranslation.setText(text);
                this.linearLayout2.addView(this.bottomCellTranslation, LayoutHelper.createLinear(-1, -2));
            }
        } else if (Build.VERSION.SDK_INT >= 18) {
            TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
            this.scanDocumentCell = textSettingsCell2;
            textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.scanDocumentCell.setText(LocaleController.getString("PassportScanPassport", R.string.PassportScanPassport), false);
            this.linearLayout2.addView(this.scanDocumentCell, LayoutHelper.createLinear(-1, -2));
            this.scanDocumentCell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PassportActivity.this.lambda$createIdentityInterface$45$PassportActivity(view);
                }
            });
            TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context2);
            this.bottomCell = textInfoPrivacyCell4;
            textInfoPrivacyCell4.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            this.bottomCell.setText(LocaleController.getString("PassportScanPassportInfo", R.string.PassportScanPassportInfo));
            this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        }
        HeaderCell headerCell4 = new HeaderCell(context2);
        this.headerCell = headerCell4;
        if (this.documentOnly) {
            headerCell4.setText(LocaleController.getString("PassportDocument", R.string.PassportDocument));
        } else {
            headerCell4.setText(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        }
        this.headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
        int count = this.currentDocumentsType != null ? 9 : 7;
        this.inputFields = new EditTextBoldCursor[count];
        int a = 0;
        while (true) {
            int i2 = 64;
            if (a < count) {
                final EditTextBoldCursor field = new EditTextBoldCursor(context2);
                this.inputFields[a] = field;
                ViewGroup container = new FrameLayout(context2) {
                    private StaticLayout errorLayout;
                    private float offsetX;

                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                        StaticLayout errorLayout2 = field.getErrorLayout(width);
                        this.errorLayout = errorLayout2;
                        if (errorLayout2 != null) {
                            int lineCount = errorLayout2.getLineCount();
                            if (lineCount > 1) {
                                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), 1073741824);
                            }
                            if (LocaleController.isRTL != 0) {
                                float maxW = 0.0f;
                                int a = 0;
                                while (true) {
                                    if (a >= lineCount) {
                                        break;
                                    } else if (this.errorLayout.getLineLeft(a) != 0.0f) {
                                        this.offsetX = 0.0f;
                                        break;
                                    } else {
                                        maxW = Math.max(maxW, this.errorLayout.getLineWidth(a));
                                        if (a == lineCount - 1) {
                                            this.offsetX = ((float) width) - maxW;
                                        }
                                        a++;
                                    }
                                }
                            }
                        }
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        if (this.errorLayout != null) {
                            canvas.save();
                            canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, field.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                            this.errorLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                };
                container.setWillNotDraw(z);
                this.linearLayout2.addView(container, LayoutHelper.createLinear(i, 64));
                container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                if (a == count - 1) {
                    View view = new View(context2);
                    this.extraBackgroundView = view;
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    this.linearLayout2.addView(this.extraBackgroundView, LayoutHelper.createLinear(i, 6));
                }
                if (this.documentOnly && this.currentDocumentsType != null) {
                    if (a < 7) {
                        container.setVisibility(8);
                        View view2 = this.extraBackgroundView;
                        if (view2 != null) {
                            view2.setVisibility(8);
                        }
                    }
                }
                this.inputFields[a].setTag(Integer.valueOf(a));
                this.inputFields[a].setSupportRtlHint(true);
                this.inputFields[a].setTextSize(1, 16.0f);
                this.inputFields[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                this.inputFields[a].setTransformHintToHeader(true);
                this.inputFields[a].setBackgroundDrawable((Drawable) null);
                this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
                this.inputFields[a].setCursorWidth(1.5f);
                this.inputFields[a].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                if (a == 5 || a == 6) {
                    this.inputFields[a].setOnTouchListener(new View.OnTouchListener() {
                        public final boolean onTouch(View view, MotionEvent motionEvent) {
                            return PassportActivity.this.lambda$createIdentityInterface$47$PassportActivity(view, motionEvent);
                        }
                    });
                    this.inputFields[a].setInputType(0);
                } else if (a == 3 || a == 8) {
                    this.inputFields[a].setOnTouchListener(new View.OnTouchListener(context2) {
                        private final /* synthetic */ Context f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final boolean onTouch(View view, MotionEvent motionEvent) {
                            return PassportActivity.this.lambda$createIdentityInterface$50$PassportActivity(this.f$1, view, motionEvent);
                        }
                    });
                    this.inputFields[a].setInputType(0);
                    this.inputFields[a].setFocusable(false);
                } else if (a == 4) {
                    this.inputFields[a].setOnTouchListener(new View.OnTouchListener() {
                        public final boolean onTouch(View view, MotionEvent motionEvent) {
                            return PassportActivity.this.lambda$createIdentityInterface$52$PassportActivity(view, motionEvent);
                        }
                    });
                    this.inputFields[a].setInputType(0);
                    this.inputFields[a].setFocusable(false);
                } else {
                    this.inputFields[a].setInputType(16385);
                    this.inputFields[a].setImeOptions(268435461);
                }
                switch (a) {
                    case 0:
                        if (this.currentType.native_names) {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportNameLatin", R.string.PassportNameLatin));
                        } else {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportName", R.string.PassportName));
                        }
                        key2 = "first_name";
                        values2 = this.currentValues;
                        break;
                    case 1:
                        if (this.currentType.native_names) {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportMidnameLatin", R.string.PassportMidnameLatin));
                        } else {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportMidname", R.string.PassportMidname));
                        }
                        key2 = "middle_name";
                        values2 = this.currentValues;
                        break;
                    case 2:
                        if (this.currentType.native_names) {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportSurnameLatin", R.string.PassportSurnameLatin));
                        } else {
                            this.inputFields[a].setHintText(LocaleController.getString("PassportSurname", R.string.PassportSurname));
                        }
                        key2 = "last_name";
                        values2 = this.currentValues;
                        break;
                    case 3:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportBirthdate", R.string.PassportBirthdate));
                        key2 = "birth_date";
                        values2 = this.currentValues;
                        break;
                    case 4:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportGender", R.string.PassportGender));
                        key2 = "gender";
                        values2 = this.currentValues;
                        break;
                    case 5:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportCitizenship", R.string.PassportCitizenship));
                        key2 = "country_code";
                        values2 = this.currentValues;
                        break;
                    case 6:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportResidence", R.string.PassportResidence));
                        key2 = "residence_country_code";
                        values2 = this.currentValues;
                        break;
                    case 7:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportDocumentNumber", R.string.PassportDocumentNumber));
                        key2 = "document_no";
                        values2 = this.currentDocumentValues;
                        break;
                    case 8:
                        this.inputFields[a].setHintText(LocaleController.getString("PassportExpired", R.string.PassportExpired));
                        key2 = "expiry_date";
                        values2 = this.currentDocumentValues;
                        break;
                }
                setFieldValues(values2, this.inputFields[a], key2);
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
                if (a == 0 || a == 2 || a == 1) {
                    this.inputFields[a].addTextChangedListener(new TextWatcher() {
                        private boolean ignore;

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            if (!this.ignore) {
                                int num = ((Integer) field.getTag()).intValue();
                                boolean error = false;
                                int a = 0;
                                while (true) {
                                    if (a >= s.length()) {
                                        break;
                                    }
                                    char ch = s.charAt(a);
                                    if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && !((ch >= 'A' && ch <= 'Z') || ch == ' ' || ch == '\'' || ch == ',' || ch == '.' || ch == '&' || ch == '-' || ch == '/'))) {
                                        error = true;
                                        break;
                                    }
                                    a++;
                                }
                                if (!error || PassportActivity.this.allowNonLatinName) {
                                    PassportActivity.this.nonLatinNames[num] = error;
                                    PassportActivity.this.checkFieldForError(field, key2, s, false);
                                    return;
                                }
                                field.setErrorText(LocaleController.getString("PassportUseLatinOnly", R.string.PassportUseLatinOnly));
                            }
                        }
                    });
                } else {
                    this.inputFields[a].addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            PassportActivity passportActivity = PassportActivity.this;
                            passportActivity.checkFieldForError(field, key2, s, values2 == passportActivity.currentDocumentValues);
                            int field12 = ((Integer) field.getTag()).intValue();
                            EditTextBoldCursor editTextBoldCursor = PassportActivity.this.inputFields[field12];
                            if (field12 == 6) {
                                PassportActivity.this.checkNativeFields(true);
                            }
                        }
                    });
                }
                this.inputFields[a].setPadding(0, 0, 0, 0);
                this.inputFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                container.addView(this.inputFields[a], LayoutHelper.createFrame(-1.0f, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                this.inputFields[a].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        return PassportActivity.this.lambda$createIdentityInterface$53$PassportActivity(textView, i, keyEvent);
                    }
                });
                a++;
                z = false;
                i = -1;
            } else {
                ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
                this.sectionCell2 = shadowSectionCell;
                this.linearLayout2.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
                HeaderCell headerCell5 = new HeaderCell(context2);
                this.headerCell = headerCell5;
                headerCell5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.linearLayout2.addView(this.headerCell, LayoutHelper.createLinear(-1, -2));
                int i3 = 3;
                this.inputExtraFields = new EditTextBoldCursor[3];
                int a2 = 0;
                while (a2 < i3) {
                    final EditTextBoldCursor field2 = new EditTextBoldCursor(context2);
                    this.inputExtraFields[a2] = field2;
                    ViewGroup container2 = new FrameLayout(context2) {
                        private StaticLayout errorLayout;
                        private float offsetX;

                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            int width = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(34.0f);
                            StaticLayout errorLayout2 = field2.getErrorLayout(width);
                            this.errorLayout = errorLayout2;
                            if (errorLayout2 != null) {
                                int lineCount = errorLayout2.getLineCount();
                                if (lineCount > 1) {
                                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.errorLayout.getLineBottom(lineCount - 1) - this.errorLayout.getLineBottom(0)), 1073741824);
                                }
                                if (LocaleController.isRTL != 0) {
                                    float maxW = 0.0f;
                                    int a = 0;
                                    while (true) {
                                        if (a >= lineCount) {
                                            break;
                                        } else if (this.errorLayout.getLineLeft(a) != 0.0f) {
                                            this.offsetX = 0.0f;
                                            break;
                                        } else {
                                            maxW = Math.max(maxW, this.errorLayout.getLineWidth(a));
                                            if (a == lineCount - 1) {
                                                this.offsetX = ((float) width) - maxW;
                                            }
                                            a++;
                                        }
                                    }
                                }
                            }
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }

                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            if (this.errorLayout != null) {
                                canvas.save();
                                canvas.translate(((float) AndroidUtilities.dp(21.0f)) + this.offsetX, field2.getLineY() + ((float) AndroidUtilities.dp(3.0f)));
                                this.errorLayout.draw(canvas);
                                canvas.restore();
                            }
                        }
                    };
                    container2.setWillNotDraw(false);
                    this.linearLayout2.addView(container2, LayoutHelper.createLinear(-1, i2));
                    container2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    if (a2 == 2) {
                        View view3 = new View(context2);
                        this.extraBackgroundView2 = view3;
                        view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        this.linearLayout2.addView(this.extraBackgroundView2, LayoutHelper.createLinear(-1, 6));
                    }
                    this.inputExtraFields[a2].setTag(Integer.valueOf(a2));
                    this.inputExtraFields[a2].setSupportRtlHint(true);
                    this.inputExtraFields[a2].setTextSize(1, 16.0f);
                    this.inputExtraFields[a2].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                    this.inputExtraFields[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.inputExtraFields[a2].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                    this.inputExtraFields[a2].setTransformHintToHeader(true);
                    this.inputExtraFields[a2].setBackgroundDrawable((Drawable) null);
                    this.inputExtraFields[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    this.inputExtraFields[a2].setCursorSize(AndroidUtilities.dp(20.0f));
                    this.inputExtraFields[a2].setCursorWidth(1.5f);
                    this.inputExtraFields[a2].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    this.inputExtraFields[a2].setInputType(16385);
                    this.inputExtraFields[a2].setImeOptions(268435461);
                    if (a2 == 0) {
                        key = "first_name_native";
                        values = this.currentValues;
                    } else if (a2 == 1) {
                        key = "middle_name_native";
                        values = this.currentValues;
                    } else if (a2 != 2) {
                        a2++;
                        i3 = 3;
                        i2 = 64;
                    } else {
                        key = "last_name_native";
                        values = this.currentValues;
                    }
                    setFieldValues(values, this.inputExtraFields[a2], key);
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    editTextBoldCursorArr2[a2].setSelection(editTextBoldCursorArr2[a2].length());
                    if (a2 == 0 || a2 == 2 || a2 == 1) {
                        this.inputExtraFields[a2].addTextChangedListener(new TextWatcher() {
                            private boolean ignore;

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                if (!this.ignore) {
                                    PassportActivity.this.checkFieldForError(field2, key, s, false);
                                }
                            }
                        });
                    }
                    this.inputExtraFields[a2].setPadding(0, 0, 0, 0);
                    this.inputExtraFields[a2].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    container2.addView(this.inputExtraFields[a2], LayoutHelper.createFrame(-1.0f, -1.0f, 51, 21.0f, 0.0f, 21.0f, 0.0f));
                    this.inputExtraFields[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            return PassportActivity.this.lambda$createIdentityInterface$54$PassportActivity(textView, i, keyEvent);
                        }
                    });
                    a2++;
                    i3 = 3;
                    i2 = 64;
                }
                TextInfoPrivacyCell textInfoPrivacyCell5 = new TextInfoPrivacyCell(context2);
                this.nativeInfoCell = textInfoPrivacyCell5;
                this.linearLayout2.addView(textInfoPrivacyCell5, LayoutHelper.createLinear(-1, -2));
                if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
                    TLRPC.TL_secureValue tL_secureValue = this.currentDocumentsTypeValue;
                    if (tL_secureValue != null) {
                        addDocumentViews(tL_secureValue.files);
                        if (this.currentDocumentsTypeValue.front_side instanceof TLRPC.TL_secureFile) {
                            addDocumentViewInternal((TLRPC.TL_secureFile) this.currentDocumentsTypeValue.front_side, 2);
                        }
                        if (this.currentDocumentsTypeValue.reverse_side instanceof TLRPC.TL_secureFile) {
                            addDocumentViewInternal((TLRPC.TL_secureFile) this.currentDocumentsTypeValue.reverse_side, 3);
                        }
                        if (this.currentDocumentsTypeValue.selfie instanceof TLRPC.TL_secureFile) {
                            addDocumentViewInternal((TLRPC.TL_secureFile) this.currentDocumentsTypeValue.selfie, 1);
                        }
                        addTranslationDocumentViews(this.currentDocumentsTypeValue.translation);
                    }
                    TextSettingsCell settingsCell1 = new TextSettingsCell(context2);
                    settingsCell1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    settingsCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                    if (this.currentDocumentsType == null) {
                        settingsCell1.setText(LocaleController.getString("PassportDeleteInfo", R.string.PassportDeleteInfo), false);
                    } else {
                        settingsCell1.setText(LocaleController.getString("PassportDeleteDocument", R.string.PassportDeleteDocument), false);
                    }
                    this.linearLayout2.addView(settingsCell1, LayoutHelper.createLinear(-1, -2));
                    settingsCell1.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            PassportActivity.this.lambda$createIdentityInterface$55$PassportActivity(view);
                        }
                    });
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
                    this.sectionCell = shadowSectionCell2;
                    shadowSectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
                } else {
                    this.nativeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                }
                updateInterfaceStringsForDocumentType();
                checkNativeFields(false);
                return;
            }
        }
    }

    public /* synthetic */ void lambda$createIdentityInterface$40$PassportActivity(View v) {
        this.uploadingFileType = 2;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$41$PassportActivity(View v) {
        this.uploadingFileType = 3;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$42$PassportActivity(View v) {
        this.uploadingFileType = 1;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$43$PassportActivity(View v) {
        this.uploadingFileType = 4;
        openAttachMenu();
    }

    public /* synthetic */ void lambda$createIdentityInterface$45$PassportActivity(View v) {
        if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
            MrzCameraActivity fragment = new MrzCameraActivity();
            fragment.setDelegate(new MrzCameraActivity.MrzCameraActivityDelegate() {
                public final void didFindMrzInfo(MrzRecognizer.Result result) {
                    PassportActivity.this.lambda$null$44$PassportActivity(result);
                }
            });
            presentFragment(fragment);
            return;
        }
        getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 22);
    }

    public /* synthetic */ void lambda$null$44$PassportActivity(MrzRecognizer.Result result) {
        if (!TextUtils.isEmpty(result.firstName)) {
            this.inputFields[0].setText(result.firstName);
        }
        if (!TextUtils.isEmpty(result.middleName)) {
            this.inputFields[1].setText(result.middleName);
        }
        if (!TextUtils.isEmpty(result.lastName)) {
            this.inputFields[2].setText(result.lastName);
        }
        if (result.gender != 0) {
            int i = result.gender;
            if (i == 1) {
                this.currentGender = "male";
                this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
            } else if (i == 2) {
                this.currentGender = "female";
                this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            String str = result.nationality;
            this.currentCitizeship = str;
            String country = this.languageMap.get(str);
            if (country != null) {
                this.inputFields[5].setText(country);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            String str2 = result.issuingCountry;
            this.currentResidence = str2;
            String country2 = this.languageMap.get(str2);
            if (country2 != null) {
                this.inputFields[6].setText(country2);
            }
        }
        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
        }
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$47$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            CountrySelectActivity fragment = new CountrySelectActivity(false);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate(v) {
                private final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    PassportActivity.this.lambda$null$46$PassportActivity(this.f$1, country);
                }
            });
            presentFragment(fragment);
        }
        return true;
    }

    public /* synthetic */ void lambda$null$46$PassportActivity(View v, CountrySelectActivity.Country country) {
        if (country != null) {
            int field12 = ((Integer) v.getTag()).intValue();
            EditTextBoldCursor editText = this.inputFields[field12];
            if (field12 == 5) {
                this.currentCitizeship = country.shortname;
            } else {
                this.currentResidence = country.shortname;
            }
            editText.setText(country.name + "");
        }
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$50$PassportActivity(Context context, View v, MotionEvent event) {
        int currentYearDiff;
        int maxYear;
        int minYear;
        String title;
        int selectedYear;
        int selectedMonth;
        int selectedDay;
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() != 1) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(1);
        int i2 = calendar.get(2);
        int i3 = calendar.get(5);
        try {
            EditTextBoldCursor field1 = (EditTextBoldCursor) v;
            int num = ((Integer) field1.getTag()).intValue();
            if (num == 8) {
                title = LocaleController.getString("PassportSelectExpiredDate", R.string.PassportSelectExpiredDate);
                minYear = 0;
                maxYear = 20;
                currentYearDiff = 0;
            } else {
                title = LocaleController.getString("PassportSelectBithdayDate", R.string.PassportSelectBithdayDate);
                minYear = -120;
                maxYear = 0;
                currentYearDiff = -18;
            }
            String[] args = field1.getText().toString().split("\\.");
            if (args.length == 3) {
                selectedDay = Utilities.parseInt(args[0]).intValue();
                selectedMonth = Utilities.parseInt(args[1]).intValue();
                selectedYear = Utilities.parseInt(args[2]).intValue();
            } else {
                selectedDay = -1;
                selectedMonth = -1;
                selectedYear = -1;
            }
            AlertDialog.Builder builder = AlertsCreator.createDatePickerDialog(context, minYear, maxYear, currentYearDiff, selectedDay, selectedMonth, selectedYear, title, num == 8, new AlertsCreator.DatePickerDelegate(num, field1) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ EditTextBoldCursor f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void didSelectDate(int i, int i2, int i3) {
                    PassportActivity.this.lambda$null$48$PassportActivity(this.f$1, this.f$2, i, i2, i3);
                }
            });
            if (num == 8) {
                String[] strArr = args;
                builder.setNegativeButton(LocaleController.getString("PassportSelectNotExpire", R.string.PassportSelectNotExpire), new DialogInterface.OnClickListener(field1) {
                    private final /* synthetic */ EditTextBoldCursor f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PassportActivity.this.lambda$null$49$PassportActivity(this.f$1, dialogInterface, i);
                    }
                });
            }
            showDialog(builder.create());
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return true;
        }
    }

    public /* synthetic */ void lambda$null$48$PassportActivity(int num, EditTextBoldCursor field1, int year1, int month, int dayOfMonth1) {
        if (num == 8) {
            int[] iArr = this.currentExpireDate;
            iArr[0] = year1;
            iArr[1] = month + 1;
            iArr[2] = dayOfMonth1;
        }
        field1.setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(dayOfMonth1), Integer.valueOf(month + 1), Integer.valueOf(year1)}));
    }

    public /* synthetic */ void lambda$null$49$PassportActivity(EditTextBoldCursor field1, DialogInterface dialog, int which) {
        int[] iArr = this.currentExpireDate;
        iArr[2] = 0;
        iArr[1] = 0;
        iArr[0] = 0;
        field1.setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$52$PassportActivity(View v, MotionEvent event) {
        if (getParentActivity() == null) {
            return false;
        }
        if (event.getAction() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("PassportSelectGender", R.string.PassportSelectGender));
            builder.setItems(new CharSequence[]{LocaleController.getString("PassportMale", R.string.PassportMale), LocaleController.getString("PassportFemale", R.string.PassportFemale)}, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.lambda$null$51$PassportActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return true;
    }

    public /* synthetic */ void lambda$null$51$PassportActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.currentGender = "male";
            this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
        } else if (i == 1) {
            this.currentGender = "female";
            this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
        }
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$53$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int num = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
        if (num < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[num].isFocusable()) {
                this.inputFields[num].requestFocus();
            } else {
                this.inputFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public /* synthetic */ boolean lambda$createIdentityInterface$54$PassportActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        int num = ((Integer) textView.getTag()).intValue() + 1;
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (num < editTextBoldCursorArr.length) {
            if (editTextBoldCursorArr[num].isFocusable()) {
                this.inputExtraFields[num].requestFocus();
            } else {
                this.inputExtraFields[num].dispatchTouchEvent(MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0));
                textView.clearFocus();
                AndroidUtilities.hideKeyboard(textView);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$createIdentityInterface$55$PassportActivity(View v) {
        createDocumentDeleteAlert();
    }

    private void updateInterfaceStringsForDocumentType() {
        if (this.currentDocumentsType != null) {
            this.actionBar.setTitle(getTextForType(this.currentDocumentsType.type));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PassportPersonal", R.string.PassportPersonal));
        }
        updateUploadText(2);
        updateUploadText(3);
        updateUploadText(1);
        updateUploadText(4);
    }

    /* access modifiers changed from: private */
    public void updateUploadText(int type) {
        boolean z = true;
        int i = 0;
        if (type == 0) {
            if (this.uploadDocumentCell != null) {
                if (this.documents.size() >= 1) {
                    this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadAdditinalDocument", R.string.PassportUploadAdditinalDocument), false);
                } else {
                    this.uploadDocumentCell.setText(LocaleController.getString("PassportUploadDocument", R.string.PassportUploadDocument), false);
                }
            }
        } else if (type == 1) {
            TextDetailSettingsCell textDetailSettingsCell = this.uploadSelfieCell;
            if (textDetailSettingsCell != null) {
                if (this.selfieDocument != null) {
                    i = 8;
                }
                textDetailSettingsCell.setVisibility(i);
            }
        } else if (type == 4) {
            if (this.uploadTranslationCell != null) {
                if (this.translationDocuments.size() >= 1) {
                    this.uploadTranslationCell.setText(LocaleController.getString("PassportUploadAdditinalDocument", R.string.PassportUploadAdditinalDocument), false);
                } else {
                    this.uploadTranslationCell.setText(LocaleController.getString("PassportUploadDocument", R.string.PassportUploadDocument), false);
                }
            }
        } else if (type == 2) {
            if (this.uploadFrontCell != null) {
                TLRPC.TL_secureRequiredType tL_secureRequiredType = this.currentDocumentsType;
                if (tL_secureRequiredType == null || (!tL_secureRequiredType.selfie_required && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense))) {
                    z = false;
                }
                boolean divider = z;
                if ((this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport) || (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                    this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportMainPage", R.string.PassportMainPage), LocaleController.getString("PassportMainPageInfo", R.string.PassportMainPageInfo), divider);
                } else {
                    this.uploadFrontCell.setTextAndValue(LocaleController.getString("PassportFrontSide", R.string.PassportFrontSide), LocaleController.getString("PassportFrontSideInfo", R.string.PassportFrontSideInfo), divider);
                }
                TextDetailSettingsCell textDetailSettingsCell2 = this.uploadFrontCell;
                if (this.frontDocument != null) {
                    i = 8;
                }
                textDetailSettingsCell2.setVisibility(i);
            }
        } else if (type == 3 && this.uploadReverseCell != null) {
            if ((this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) || (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) {
                this.reverseLayout.setVisibility(0);
                TextDetailSettingsCell textDetailSettingsCell3 = this.uploadReverseCell;
                if (this.reverseDocument != null) {
                    i = 8;
                }
                textDetailSettingsCell3.setVisibility(i);
                return;
            }
            this.reverseLayout.setVisibility(8);
            this.uploadReverseCell.setVisibility(8);
        }
    }

    private void checkTopErrorCell(boolean init) {
        String errorText;
        String errorText2;
        if (this.topErrorCell != null) {
            SpannableStringBuilder stringBuilder = null;
            if (this.fieldsErrors != null && ((init || this.errorsValues.containsKey("error_all")) && (errorText2 = this.fieldsErrors.get("error_all")) != null)) {
                stringBuilder = new SpannableStringBuilder(errorText2);
                if (init) {
                    this.errorsValues.put("error_all", "");
                }
            }
            if (this.documentsErrors != null && ((init || this.errorsValues.containsKey("error_document_all")) && (errorText = this.documentsErrors.get("error_all")) != null)) {
                if (stringBuilder == null) {
                    stringBuilder = new SpannableStringBuilder(errorText);
                } else {
                    stringBuilder.append("\n\n").append(errorText);
                }
                if (init) {
                    this.errorsValues.put("error_document_all", "");
                }
            }
            if (stringBuilder != null) {
                stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3)), 0, stringBuilder.length(), 33);
                this.topErrorCell.setText(stringBuilder);
                this.topErrorCell.setVisibility(0);
            } else if (this.topErrorCell.getVisibility() != 8) {
                this.topErrorCell.setVisibility(8);
            }
        }
    }

    private void addDocumentViewInternal(TLRPC.TL_secureFile f, int uploadingType) {
        addDocumentView(new SecureDocument(getSecureDocumentKey(f.secret, f.file_hash), f, (String) null, (byte[]) null, (byte[]) null), uploadingType);
    }

    private void addDocumentViews(ArrayList<TLRPC.SecureFile> files) {
        this.documents.clear();
        int size = files.size();
        for (int a = 0; a < size; a++) {
            TLRPC.SecureFile secureFile = files.get(a);
            if (secureFile instanceof TLRPC.TL_secureFile) {
                addDocumentViewInternal((TLRPC.TL_secureFile) secureFile, 0);
            }
        }
    }

    private void addTranslationDocumentViews(ArrayList<TLRPC.SecureFile> files) {
        this.translationDocuments.clear();
        int size = files.size();
        for (int a = 0; a < size; a++) {
            TLRPC.SecureFile secureFile = files.get(a);
            if (secureFile instanceof TLRPC.TL_secureFile) {
                addDocumentViewInternal((TLRPC.TL_secureFile) secureFile, 4);
            }
        }
    }

    private void setFieldValues(HashMap<String, String> values, EditTextBoldCursor editText, String key) {
        String str = values.get(key);
        String value = str;
        if (str != null) {
            char c = 65535;
            switch (key.hashCode()) {
                case -2006252145:
                    if (key.equals("residence_country_code")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1249512767:
                    if (key.equals("gender")) {
                        c = 2;
                        break;
                    }
                    break;
                case 475919162:
                    if (key.equals("expiry_date")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1481071862:
                    if (key.equals("country_code")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                this.currentCitizeship = value;
                String country = this.languageMap.get(value);
                if (country != null) {
                    editText.setText(country);
                }
            } else if (c == 1) {
                this.currentResidence = value;
                String country2 = this.languageMap.get(value);
                if (country2 != null) {
                    editText.setText(country2);
                }
            } else if (c != 2) {
                if (c != 3) {
                    editText.setText(value);
                } else {
                    boolean ok = false;
                    if (!TextUtils.isEmpty(value)) {
                        String[] args = value.split("\\.");
                        if (args.length == 3) {
                            this.currentExpireDate[0] = Utilities.parseInt(args[2]).intValue();
                            this.currentExpireDate[1] = Utilities.parseInt(args[1]).intValue();
                            this.currentExpireDate[2] = Utilities.parseInt(args[0]).intValue();
                            editText.setText(value);
                            ok = true;
                        }
                    }
                    if (!ok) {
                        int[] iArr = this.currentExpireDate;
                        iArr[2] = 0;
                        iArr[1] = 0;
                        iArr[0] = 0;
                        editText.setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
                    }
                }
            } else if ("male".equals(value)) {
                this.currentGender = value;
                editText.setText(LocaleController.getString("PassportMale", R.string.PassportMale));
            } else if ("female".equals(value)) {
                this.currentGender = value;
                editText.setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
            }
        }
        HashMap<String, String> hashMap = this.fieldsErrors;
        if (hashMap != null) {
            String str2 = hashMap.get(key);
            String value2 = str2;
            if (str2 != null) {
                editText.setErrorText(value2);
                this.errorsValues.put(key, editText.getText().toString());
                return;
            }
        }
        HashMap<String, String> hashMap2 = this.documentsErrors;
        if (hashMap2 != null) {
            String str3 = hashMap2.get(key);
            String value3 = str3;
            if (str3 != null) {
                editText.setErrorText(value3);
                this.errorsValues.put(key, editText.getText().toString());
            }
        }
    }

    private void addDocumentView(SecureDocument document, int type) {
        String key;
        String text;
        String value;
        HashMap<String, String> hashMap;
        if (type == 1) {
            this.selfieDocument = document;
            if (this.selfieLayout == null) {
                return;
            }
        } else if (type == 4) {
            this.translationDocuments.add(document);
            if (this.translationLayout == null) {
                return;
            }
        } else if (type == 2) {
            this.frontDocument = document;
            if (this.frontLayout == null) {
                return;
            }
        } else if (type == 3) {
            this.reverseDocument = document;
            if (this.reverseLayout == null) {
                return;
            }
        } else {
            this.documents.add(document);
            if (this.documentsLayout == null) {
                return;
            }
        }
        if (getParentActivity() != null) {
            SecureDocumentCell cell = new SecureDocumentCell(this, getParentActivity());
            cell.setTag(document);
            cell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.documentsCells.put(document, cell);
            String hash = getDocumentHash(document);
            if (type == 1) {
                text = LocaleController.getString("PassportSelfie", R.string.PassportSelfie);
                this.selfieLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "selfie" + hash;
            } else if (type == 4) {
                text = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                this.translationLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "translation" + hash;
            } else if (type == 2) {
                if ((this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport) || (this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                    text = LocaleController.getString("PassportMainPage", R.string.PassportMainPage);
                } else {
                    text = LocaleController.getString("PassportFrontSide", R.string.PassportFrontSide);
                }
                this.frontLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "front" + hash;
            } else if (type == 3) {
                text = LocaleController.getString("PassportReverseSide", R.string.PassportReverseSide);
                this.reverseLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "reverse" + hash;
            } else {
                text = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                this.documentsLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                key = "files" + hash;
            }
            if (!(key == null || (hashMap = this.documentsErrors) == null)) {
                String str = hashMap.get(key);
                value = str;
                if (str != null) {
                    cell.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                    this.errorsValues.put(key, "");
                    cell.setTextAndValueAndImage(text, value, document);
                    cell.setOnClickListener(new View.OnClickListener(type) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(View view) {
                            PassportActivity.this.lambda$addDocumentView$56$PassportActivity(this.f$1, view);
                        }
                    });
                    cell.setOnLongClickListener(new View.OnLongClickListener(type, document, cell, key) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ SecureDocument f$2;
                        private final /* synthetic */ PassportActivity.SecureDocumentCell f$3;
                        private final /* synthetic */ String f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final boolean onLongClick(View view) {
                            return PassportActivity.this.lambda$addDocumentView$58$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, view);
                        }
                    });
                }
            }
            value = LocaleController.formatDateForBan((long) document.secureFile.date);
            cell.setTextAndValueAndImage(text, value, document);
            cell.setOnClickListener(new View.OnClickListener(type) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    PassportActivity.this.lambda$addDocumentView$56$PassportActivity(this.f$1, view);
                }
            });
            cell.setOnLongClickListener(new View.OnLongClickListener(type, document, cell, key) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ SecureDocument f$2;
                private final /* synthetic */ PassportActivity.SecureDocumentCell f$3;
                private final /* synthetic */ String f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final boolean onLongClick(View view) {
                    return PassportActivity.this.lambda$addDocumentView$58$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addDocumentView$56$PassportActivity(int type, View v) {
        this.uploadingFileType = type;
        if (type == 1) {
            this.currentPhotoViewerLayout = this.selfieLayout;
        } else if (type == 4) {
            this.currentPhotoViewerLayout = this.translationLayout;
        } else if (type == 2) {
            this.currentPhotoViewerLayout = this.frontLayout;
        } else if (type == 3) {
            this.currentPhotoViewerLayout = this.reverseLayout;
        } else {
            this.currentPhotoViewerLayout = this.documentsLayout;
        }
        SecureDocument document1 = (SecureDocument) v.getTag();
        PhotoViewer.getInstance().setParentActivity(getParentActivity());
        if (type == 0) {
            PhotoViewer instance = PhotoViewer.getInstance();
            ArrayList<SecureDocument> arrayList = this.documents;
            instance.openPhoto(arrayList, arrayList.indexOf(document1), this.provider);
            return;
        }
        PhotoViewer instance2 = PhotoViewer.getInstance();
        ArrayList<SecureDocument> arrayList2 = this.translationDocuments;
        instance2.openPhoto(arrayList2, arrayList2.indexOf(document1), this.provider);
    }

    public /* synthetic */ boolean lambda$addDocumentView$58$PassportActivity(int type, SecureDocument document, SecureDocumentCell cell, String key, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (type == 1) {
            builder.setMessage(LocaleController.getString("PassportDeleteSelfie", R.string.PassportDeleteSelfie));
        } else {
            builder.setMessage(LocaleController.getString("PassportDeleteScan", R.string.PassportDeleteScan));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(document, type, cell, key) {
            private final /* synthetic */ SecureDocument f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ PassportActivity.SecureDocumentCell f$3;
            private final /* synthetic */ String f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$null$57$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$57$PassportActivity(SecureDocument document, int type, SecureDocumentCell cell, String key, DialogInterface dialog, int which) {
        this.documentsCells.remove(document);
        if (type == 1) {
            this.selfieDocument = null;
            this.selfieLayout.removeView(cell);
        } else if (type == 4) {
            this.translationDocuments.remove(document);
            this.translationLayout.removeView(cell);
        } else if (type == 2) {
            this.frontDocument = null;
            this.frontLayout.removeView(cell);
        } else if (type == 3) {
            this.reverseDocument = null;
            this.reverseLayout.removeView(cell);
        } else {
            this.documents.remove(document);
            this.documentsLayout.removeView(cell);
        }
        if (key != null) {
            HashMap<String, String> hashMap = this.documentsErrors;
            if (hashMap != null) {
                hashMap.remove(key);
            }
            HashMap<String, String> hashMap2 = this.errorsValues;
            if (hashMap2 != null) {
                hashMap2.remove(key);
            }
        }
        updateUploadText(type);
        if (document.path != null && this.uploadingDocuments.remove(document.path) != null) {
            if (this.uploadingDocuments.isEmpty()) {
                this.doneItem.setEnabled(true);
                this.doneItem.setAlpha(1.0f);
            }
            FileLoader.getInstance(this.currentAccount).cancelUploadFile(document.path, false);
        }
    }

    private String getNameForType(TLRPC.SecureValueType type) {
        if (type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
            return "personal_details";
        }
        if (type instanceof TLRPC.TL_secureValueTypePassport) {
            return "passport";
        }
        if (type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
            return "internal_passport";
        }
        if (type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
            return "driver_license";
        }
        if (type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
            return "identity_card";
        }
        if (type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
            return "utility_bill";
        }
        if (type instanceof TLRPC.TL_secureValueTypeAddress) {
            return "address";
        }
        if (type instanceof TLRPC.TL_secureValueTypeBankStatement) {
            return "bank_statement";
        }
        if (type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
            return "rental_agreement";
        }
        if (type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
            return "temporary_registration";
        }
        if (type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
            return "passport_registration";
        }
        if (type instanceof TLRPC.TL_secureValueTypeEmail) {
            return "email";
        }
        if (type instanceof TLRPC.TL_secureValueTypePhone) {
            return "phone";
        }
        return "";
    }

    private TextDetailSecureCell getViewByType(TLRPC.TL_secureRequiredType requiredType) {
        TLRPC.TL_secureRequiredType requiredType2;
        TextDetailSecureCell view = this.typesViews.get(requiredType);
        if (view != null || (requiredType2 = this.documentsToTypesLink.get(requiredType)) == null) {
            return view;
        }
        return this.typesViews.get(requiredType2);
    }

    private String getTextForType(TLRPC.SecureValueType type) {
        if (type instanceof TLRPC.TL_secureValueTypePassport) {
            return LocaleController.getString("ActionBotDocumentPassport", R.string.ActionBotDocumentPassport);
        }
        if (type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
            return LocaleController.getString("ActionBotDocumentDriverLicence", R.string.ActionBotDocumentDriverLicence);
        }
        if (type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
            return LocaleController.getString("ActionBotDocumentIdentityCard", R.string.ActionBotDocumentIdentityCard);
        }
        if (type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
            return LocaleController.getString("ActionBotDocumentUtilityBill", R.string.ActionBotDocumentUtilityBill);
        }
        if (type instanceof TLRPC.TL_secureValueTypeBankStatement) {
            return LocaleController.getString("ActionBotDocumentBankStatement", R.string.ActionBotDocumentBankStatement);
        }
        if (type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
            return LocaleController.getString("ActionBotDocumentRentalAgreement", R.string.ActionBotDocumentRentalAgreement);
        }
        if (type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
            return LocaleController.getString("ActionBotDocumentInternalPassport", R.string.ActionBotDocumentInternalPassport);
        }
        if (type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
            return LocaleController.getString("ActionBotDocumentPassportRegistration", R.string.ActionBotDocumentPassportRegistration);
        }
        if (type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
            return LocaleController.getString("ActionBotDocumentTemporaryRegistration", R.string.ActionBotDocumentTemporaryRegistration);
        }
        if (type instanceof TLRPC.TL_secureValueTypePhone) {
            return LocaleController.getString("ActionBotDocumentPhone", R.string.ActionBotDocumentPhone);
        }
        if (type instanceof TLRPC.TL_secureValueTypeEmail) {
            return LocaleController.getString("ActionBotDocumentEmail", R.string.ActionBotDocumentEmail);
        }
        return "";
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0193, code lost:
        if (r11 == null) goto L_0x0195;
     */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0350 A[Catch:{ Exception -> 0x039c }] */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0381 A[Catch:{ Exception -> 0x039c }] */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x03ef  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x041a  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0424  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0433  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x045e  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x046f  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0480  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x048b  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x050c  */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x05cd  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x05d1  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0130  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTypeValue(im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType r37, java.lang.String r38, java.lang.String r39, im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType r40, java.lang.String r41, boolean r42, int r43) {
        /*
            r36 = this;
            r7 = r36
            r8 = r37
            r9 = r38
            r10 = r39
            r11 = r40
            r12 = r41
            r13 = r43
            java.lang.String r14 = "last_name_native"
            java.lang.String r15 = "middle_name_native"
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell> r0 = r7.typesViews
            java.lang.Object r0 = r0.get(r8)
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r0 = (im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell) r0
            r6 = 8
            r5 = 1
            if (r0 != 0) goto L_0x0065
            int r1 = r7.currentActivityType
            if (r1 != r6) goto L_0x0064
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r4 = r1
            if (r11 == 0) goto L_0x002e
            r4.add(r11)
        L_0x002e:
            android.widget.LinearLayout r1 = r7.linearLayout2
            int r2 = r1.getChildCount()
            int r2 = r2 + -6
            android.view.View r3 = r1.getChildAt(r2)
            boolean r1 = r3 instanceof im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell
            if (r1 == 0) goto L_0x0044
            r1 = r3
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r1 = (im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell) r1
            r1.setNeedDivider(r5)
        L_0x0044:
            androidx.fragment.app.FragmentActivity r2 = r36.getParentActivity()
            r16 = 1
            r17 = 1
            r1 = r36
            r18 = r3
            r3 = r37
            r19 = r4
            r20 = r14
            r14 = 1
            r5 = r16
            r6 = r17
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r0 = r1.addField(r2, r3, r4, r5, r6)
            r36.updateManageVisibility()
            r1 = r0
            goto L_0x0069
        L_0x0064:
            return
        L_0x0065:
            r20 = r14
            r14 = 1
            r1 = r0
        L_0x0069:
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r0 = r7.typesValues
            java.lang.Object r0 = r0.get(r8)
            r2 = r0
            java.util.HashMap r2 = (java.util.HashMap) r2
            if (r11 == 0) goto L_0x007d
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r0 = r7.typesValues
            java.lang.Object r0 = r0.get(r11)
            java.util.HashMap r0 = (java.util.HashMap) r0
            goto L_0x007e
        L_0x007d:
            r0 = 0
        L_0x007e:
            r4 = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r5 = r7.getValueByType(r8, r14)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r6 = r7.getValueByType(r11, r14)
            if (r10 == 0) goto L_0x00e9
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r7.languageMap
            if (r0 != 0) goto L_0x00e9
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7.languageMap = r0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00df }
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00df }
            android.content.Context r18 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00df }
            android.content.res.Resources r18 = r18.getResources()     // Catch:{ Exception -> 0x00df }
            android.content.res.AssetManager r14 = r18.getAssets()     // Catch:{ Exception -> 0x00df }
            r18 = r5
            java.lang.String r5 = "countries.txt"
            java.io.InputStream r5 = r14.open(r5)     // Catch:{ Exception -> 0x00db }
            r3.<init>(r5)     // Catch:{ Exception -> 0x00db }
            r0.<init>(r3)     // Catch:{ Exception -> 0x00db }
        L_0x00b0:
            java.lang.String r3 = r0.readLine()     // Catch:{ Exception -> 0x00db }
            r5 = r3
            if (r3 == 0) goto L_0x00d1
            java.lang.String r3 = ";"
            java.lang.String[] r3 = r5.split(r3)     // Catch:{ Exception -> 0x00db }
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r7.languageMap     // Catch:{ Exception -> 0x00db }
            r21 = r5
            r19 = 1
            r5 = r3[r19]     // Catch:{ Exception -> 0x00db }
            r22 = r1
            r17 = 2
            r1 = r3[r17]     // Catch:{ Exception -> 0x00d9 }
            r14.put(r5, r1)     // Catch:{ Exception -> 0x00d9 }
            r1 = r22
            goto L_0x00b0
        L_0x00d1:
            r22 = r1
            r21 = r5
            r0.close()     // Catch:{ Exception -> 0x00d9 }
            goto L_0x00e7
        L_0x00d9:
            r0 = move-exception
            goto L_0x00e4
        L_0x00db:
            r0 = move-exception
            r22 = r1
            goto L_0x00e4
        L_0x00df:
            r0 = move-exception
            r22 = r1
            r18 = r5
        L_0x00e4:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00e7:
            r1 = 0
            goto L_0x00f0
        L_0x00e9:
            r22 = r1
            r18 = r5
            r1 = 0
            r7.languageMap = r1
        L_0x00f0:
            r3 = 0
            java.lang.String r1 = "PassportDocuments"
            if (r9 == 0) goto L_0x0130
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r0 = r8.type
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePhone
            if (r0 == 0) goto L_0x011a
            im.bclpbkiauv.phoneformat.PhoneFormat r0 = im.bclpbkiauv.phoneformat.PhoneFormat.getInstance()
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r5 = "+"
            r15.append(r5)
            r15.append(r9)
            java.lang.String r5 = r15.toString()
            java.lang.String r3 = r0.format(r5)
            r27 = r2
            r28 = r4
            goto L_0x0421
        L_0x011a:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r0 = r8.type
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeEmail
            if (r0 == 0) goto L_0x0128
            r3 = r38
            r27 = r2
            r28 = r4
            goto L_0x0421
        L_0x0128:
            r27 = r2
            r24 = r3
            r28 = r4
            goto L_0x041f
        L_0x0130:
            r0 = 0
            int r5 = r7.currentActivityType
            r14 = 8
            if (r5 == r14) goto L_0x0166
            if (r11 == 0) goto L_0x0166
            boolean r5 = android.text.TextUtils.isEmpty(r41)
            if (r5 == 0) goto L_0x0141
            if (r6 == 0) goto L_0x0166
        L_0x0141:
            if (r0 != 0) goto L_0x0149
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r0 = r5
        L_0x0149:
            r5 = 1
            if (r13 <= r5) goto L_0x0156
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            java.lang.String r5 = r7.getTextForType(r5)
            r0.append(r5)
            goto L_0x0166
        L_0x0156:
            boolean r5 = android.text.TextUtils.isEmpty(r41)
            if (r5 == 0) goto L_0x0166
            r5 = 2131692655(0x7f0f0c6f, float:1.9014416E38)
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r5)
            r0.append(r14)
        L_0x0166:
            if (r10 != 0) goto L_0x0171
            if (r12 == 0) goto L_0x016b
            goto L_0x0171
        L_0x016b:
            r23 = r0
            r24 = r3
            goto L_0x01f1
        L_0x0171:
            if (r2 != 0) goto L_0x0174
            return
        L_0x0174:
            r2.clear()
            r5 = 0
            r14 = 0
            r23 = r0
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r0 = r8.type
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePersonalDetails
            if (r0 == 0) goto L_0x01c2
            int r0 = r7.currentActivityType
            if (r0 != 0) goto L_0x018b
            if (r42 == 0) goto L_0x0188
            goto L_0x018b
        L_0x0188:
            r24 = r3
            goto L_0x0195
        L_0x018b:
            int r0 = r7.currentActivityType
            r24 = r3
            r3 = 8
            if (r0 != r3) goto L_0x01ae
            if (r11 != 0) goto L_0x01ae
        L_0x0195:
            java.lang.String r25 = "first_name"
            java.lang.String r26 = "middle_name"
            java.lang.String r27 = "last_name"
            java.lang.String r28 = "first_name_native"
            java.lang.String r29 = "middle_name_native"
            java.lang.String r30 = "last_name_native"
            java.lang.String r31 = "birth_date"
            java.lang.String r32 = "gender"
            java.lang.String r33 = "country_code"
            java.lang.String r34 = "residence_country_code"
            java.lang.String[] r0 = new java.lang.String[]{r25, r26, r27, r28, r29, r30, r31, r32, r33, r34}
            r5 = r0
        L_0x01ae:
            int r0 = r7.currentActivityType
            if (r0 == 0) goto L_0x01b8
            r3 = 8
            if (r0 != r3) goto L_0x01ec
            if (r11 == 0) goto L_0x01ec
        L_0x01b8:
            java.lang.String r0 = "document_no"
            java.lang.String r3 = "expiry_date"
            java.lang.String[] r0 = new java.lang.String[]{r0, r3}
            r14 = r0
            goto L_0x01ec
        L_0x01c2:
            r24 = r3
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r0 = r8.type
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeAddress
            if (r0 == 0) goto L_0x01ec
            int r0 = r7.currentActivityType
            if (r0 != 0) goto L_0x01d0
            if (r42 == 0) goto L_0x01d8
        L_0x01d0:
            int r0 = r7.currentActivityType
            r3 = 8
            if (r0 != r3) goto L_0x01ec
            if (r11 != 0) goto L_0x01ec
        L_0x01d8:
            java.lang.String r25 = "street_line1"
            java.lang.String r26 = "street_line2"
            java.lang.String r27 = "post_code"
            java.lang.String r28 = "city"
            java.lang.String r29 = "state"
            java.lang.String r30 = "country_code"
            java.lang.String[] r0 = new java.lang.String[]{r25, r26, r27, r28, r29, r30}
            r5 = r0
        L_0x01ec:
            if (r5 != 0) goto L_0x01f9
            if (r14 == 0) goto L_0x01f1
            goto L_0x01f9
        L_0x01f1:
            r27 = r2
            r28 = r4
            r0 = r23
            goto L_0x0418
        L_0x01f9:
            r0 = 0
            r3 = 0
            r25 = 0
            r35 = r25
            r25 = r3
            r3 = r35
        L_0x0203:
            r9 = 2
            if (r3 >= r9) goto L_0x0410
            if (r3 != 0) goto L_0x0220
            if (r10 == 0) goto L_0x023b
            org.json.JSONObject r9 = new org.json.JSONObject     // Catch:{ Exception -> 0x0215 }
            r9.<init>(r10)     // Catch:{ Exception -> 0x0215 }
            r0 = r9
            r9 = r5
            r26 = r5
            r9 = r0
            goto L_0x0240
        L_0x0215:
            r0 = move-exception
            r27 = r2
            r28 = r4
            r26 = r5
            r0 = r23
            goto L_0x0418
        L_0x0220:
            if (r4 != 0) goto L_0x022d
            r27 = r2
            r28 = r4
            r26 = r5
            r9 = r20
            r4 = 2
            goto L_0x03fe
        L_0x022d:
            if (r12 == 0) goto L_0x023b
            org.json.JSONObject r9 = new org.json.JSONObject     // Catch:{ Exception -> 0x0215 }
            r9.<init>(r12)     // Catch:{ Exception -> 0x0215 }
            r0 = r9
            r9 = r14
            r26 = r5
            r5 = r9
            r9 = r0
            goto L_0x0240
        L_0x023b:
            r9 = r0
            r26 = r5
            r5 = r25
        L_0x0240:
            if (r5 == 0) goto L_0x03ef
            if (r9 != 0) goto L_0x0251
            r27 = r2
            r28 = r4
            r29 = r5
            r25 = r9
            r9 = r20
            r4 = 2
            goto L_0x03fa
        L_0x0251:
            java.util.Iterator r0 = r9.keys()     // Catch:{ all -> 0x0284 }
        L_0x0255:
            boolean r25 = r0.hasNext()     // Catch:{ all -> 0x0284 }
            if (r25 == 0) goto L_0x0281
            java.lang.Object r25 = r0.next()     // Catch:{ all -> 0x0284 }
            java.lang.String r25 = (java.lang.String) r25     // Catch:{ all -> 0x0284 }
            r27 = r25
            if (r3 != 0) goto L_0x0271
            r25 = r0
            r0 = r27
            java.lang.String r10 = r9.getString(r0)     // Catch:{ all -> 0x0284 }
            r2.put(r0, r10)     // Catch:{ all -> 0x0284 }
            goto L_0x027c
        L_0x0271:
            r25 = r0
            r0 = r27
            java.lang.String r10 = r9.getString(r0)     // Catch:{ all -> 0x0284 }
            r4.put(r0, r10)     // Catch:{ all -> 0x0284 }
        L_0x027c:
            r10 = r39
            r0 = r25
            goto L_0x0255
        L_0x0281:
            r25 = r0
            goto L_0x0288
        L_0x0284:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x03e7 }
        L_0x0288:
            r0 = 0
        L_0x0289:
            int r10 = r5.length     // Catch:{ Exception -> 0x03e7 }
            if (r0 >= r10) goto L_0x03d7
            r10 = r5[r0]     // Catch:{ Exception -> 0x03e7 }
            boolean r10 = r9.has(r10)     // Catch:{ Exception -> 0x03e7 }
            if (r10 == 0) goto L_0x03bc
            if (r23 != 0) goto L_0x02a7
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x029e }
            r10.<init>()     // Catch:{ Exception -> 0x029e }
            r23 = r10
            goto L_0x02a9
        L_0x029e:
            r0 = move-exception
            r27 = r2
            r28 = r4
            r0 = r23
            goto L_0x0418
        L_0x02a7:
            r10 = r23
        L_0x02a9:
            r27 = r2
            r2 = r5[r0]     // Catch:{ Exception -> 0x03b6 }
            java.lang.String r2 = r9.getString(r2)     // Catch:{ Exception -> 0x03b6 }
            if (r2 == 0) goto L_0x03aa
            boolean r23 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x03b6 }
            if (r23 != 0) goto L_0x03a0
            r28 = r4
            java.lang.String r4 = "first_name_native"
            r25 = r9
            r9 = r5[r0]     // Catch:{ Exception -> 0x039c }
            boolean r4 = r4.equals(r9)     // Catch:{ Exception -> 0x039c }
            if (r4 != 0) goto L_0x0396
            r4 = r5[r0]     // Catch:{ Exception -> 0x039c }
            boolean r4 = r15.equals(r4)     // Catch:{ Exception -> 0x039c }
            if (r4 != 0) goto L_0x0390
            r4 = r5[r0]     // Catch:{ Exception -> 0x039c }
            r9 = r20
            boolean r4 = r9.equals(r4)     // Catch:{ Exception -> 0x039c }
            if (r4 == 0) goto L_0x02de
            r29 = r5
            r4 = 2
            goto L_0x03b3
        L_0x02de:
            int r4 = r10.length()     // Catch:{ Exception -> 0x039c }
            if (r4 <= 0) goto L_0x0314
            java.lang.String r4 = "last_name"
            r12 = r5[r0]     // Catch:{ Exception -> 0x039c }
            boolean r4 = r4.equals(r12)     // Catch:{ Exception -> 0x039c }
            if (r4 != 0) goto L_0x030f
            r4 = r5[r0]     // Catch:{ Exception -> 0x039c }
            boolean r4 = r9.equals(r4)     // Catch:{ Exception -> 0x039c }
            if (r4 != 0) goto L_0x030f
            java.lang.String r4 = "middle_name"
            r12 = r5[r0]     // Catch:{ Exception -> 0x039c }
            boolean r4 = r4.equals(r12)     // Catch:{ Exception -> 0x039c }
            if (r4 != 0) goto L_0x030f
            r4 = r5[r0]     // Catch:{ Exception -> 0x039c }
            boolean r4 = r15.equals(r4)     // Catch:{ Exception -> 0x039c }
            if (r4 == 0) goto L_0x0309
            goto L_0x030f
        L_0x0309:
            java.lang.String r4 = ", "
            r10.append(r4)     // Catch:{ Exception -> 0x039c }
            goto L_0x0314
        L_0x030f:
            java.lang.String r4 = " "
            r10.append(r4)     // Catch:{ Exception -> 0x039c }
        L_0x0314:
            r4 = r5[r0]     // Catch:{ Exception -> 0x039c }
            int r12 = r4.hashCode()     // Catch:{ Exception -> 0x039c }
            r29 = r5
            r5 = -2006252145(0xffffffff886b058f, float:-7.0724274E-34)
            if (r12 == r5) goto L_0x0340
            r5 = -1249512767(0xffffffffb585f2c1, float:-9.979923E-7)
            if (r12 == r5) goto L_0x0336
            r5 = 1481071862(0x58475cf6, float:8.7680831E14)
            if (r12 == r5) goto L_0x032c
        L_0x032b:
            goto L_0x034a
        L_0x032c:
            java.lang.String r5 = "country_code"
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x039c }
            if (r4 == 0) goto L_0x032b
            r5 = 0
            goto L_0x034b
        L_0x0336:
            java.lang.String r5 = "gender"
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x039c }
            if (r4 == 0) goto L_0x032b
            r5 = 2
            goto L_0x034b
        L_0x0340:
            java.lang.String r5 = "residence_country_code"
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x039c }
            if (r4 == 0) goto L_0x032b
            r5 = 1
            goto L_0x034b
        L_0x034a:
            r5 = -1
        L_0x034b:
            if (r5 == 0) goto L_0x0381
            r4 = 1
            if (r5 == r4) goto L_0x0381
            r4 = 2
            if (r5 == r4) goto L_0x0357
            r10.append(r2)     // Catch:{ Exception -> 0x039c }
            goto L_0x03b3
        L_0x0357:
            java.lang.String r5 = "male"
            boolean r5 = r5.equals(r2)     // Catch:{ Exception -> 0x039c }
            if (r5 == 0) goto L_0x036c
            java.lang.String r5 = "PassportMale"
            r12 = 2131692724(0x7f0f0cb4, float:1.9014556E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r12)     // Catch:{ Exception -> 0x039c }
            r10.append(r5)     // Catch:{ Exception -> 0x039c }
            goto L_0x03b3
        L_0x036c:
            java.lang.String r5 = "female"
            boolean r5 = r5.equals(r2)     // Catch:{ Exception -> 0x039c }
            if (r5 == 0) goto L_0x03b3
            java.lang.String r5 = "PassportFemale"
            r12 = 2131692662(0x7f0f0c76, float:1.901443E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r12)     // Catch:{ Exception -> 0x039c }
            r10.append(r5)     // Catch:{ Exception -> 0x039c }
            goto L_0x03b3
        L_0x0381:
            r4 = 2
            java.util.HashMap<java.lang.String, java.lang.String> r5 = r7.languageMap     // Catch:{ Exception -> 0x039c }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ Exception -> 0x039c }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x039c }
            if (r5 == 0) goto L_0x03b3
            r10.append(r5)     // Catch:{ Exception -> 0x039c }
            goto L_0x03b3
        L_0x0390:
            r29 = r5
            r9 = r20
            r4 = 2
            goto L_0x03b3
        L_0x0396:
            r29 = r5
            r9 = r20
            r4 = 2
            goto L_0x03b3
        L_0x039c:
            r0 = move-exception
            r0 = r10
            goto L_0x0418
        L_0x03a0:
            r28 = r4
            r29 = r5
            r25 = r9
            r9 = r20
            r4 = 2
            goto L_0x03b3
        L_0x03aa:
            r28 = r4
            r29 = r5
            r25 = r9
            r9 = r20
            r4 = 2
        L_0x03b3:
            r23 = r10
            goto L_0x03c7
        L_0x03b6:
            r0 = move-exception
            r28 = r4
            r0 = r10
            goto L_0x0418
        L_0x03bc:
            r27 = r2
            r28 = r4
            r29 = r5
            r25 = r9
            r9 = r20
            r4 = 2
        L_0x03c7:
            int r0 = r0 + 1
            r12 = r41
            r20 = r9
            r9 = r25
            r2 = r27
            r4 = r28
            r5 = r29
            goto L_0x0289
        L_0x03d7:
            r27 = r2
            r28 = r4
            r29 = r5
            r25 = r9
            r9 = r20
            r4 = 2
            r0 = r25
            r25 = r29
            goto L_0x03fe
        L_0x03e7:
            r0 = move-exception
            r27 = r2
            r28 = r4
            r0 = r23
            goto L_0x0418
        L_0x03ef:
            r27 = r2
            r28 = r4
            r29 = r5
            r25 = r9
            r9 = r20
            r4 = 2
        L_0x03fa:
            r0 = r25
            r25 = r29
        L_0x03fe:
            int r3 = r3 + 1
            r10 = r39
            r12 = r41
            r20 = r9
            r5 = r26
            r2 = r27
            r4 = r28
            r9 = r38
            goto L_0x0203
        L_0x0410:
            r27 = r2
            r28 = r4
            r26 = r5
            r0 = r23
        L_0x0418:
            if (r0 == 0) goto L_0x041f
            java.lang.String r3 = r0.toString()
            goto L_0x0421
        L_0x041f:
            r3 = r24
        L_0x0421:
            r0 = 0
            if (r42 != 0) goto L_0x0433
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r2 = r7.errorsMap
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r4 = r8.type
            java.lang.String r4 = r7.getNameForType(r4)
            java.lang.Object r2 = r2.get(r4)
            java.util.HashMap r2 = (java.util.HashMap) r2
            goto L_0x0434
        L_0x0433:
            r2 = 0
        L_0x0434:
            if (r11 == 0) goto L_0x0447
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, java.lang.String>> r4 = r7.errorsMap
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            java.lang.String r5 = r7.getNameForType(r5)
            java.lang.Object r4 = r4.get(r5)
            java.util.HashMap r4 = (java.util.HashMap) r4
            r16 = r4
            goto L_0x0449
        L_0x0447:
            r16 = 0
        L_0x0449:
            r4 = r16
            if (r2 == 0) goto L_0x0453
            int r5 = r2.size()
            if (r5 > 0) goto L_0x045b
        L_0x0453:
            if (r4 == 0) goto L_0x0485
            int r5 = r4.size()
            if (r5 <= 0) goto L_0x0485
        L_0x045b:
            r1 = 0
            if (r42 != 0) goto L_0x046d
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r7.mainErrorsMap
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r8.type
            java.lang.String r5 = r7.getNameForType(r5)
            java.lang.Object r3 = r3.get(r5)
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
        L_0x046d:
            if (r1 != 0) goto L_0x0480
            java.util.HashMap<java.lang.String, java.lang.String> r3 = r7.mainErrorsMap
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            java.lang.String r5 = r7.getNameForType(r5)
            java.lang.Object r3 = r3.get(r5)
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            r3 = r1
            goto L_0x0481
        L_0x0480:
            r3 = r1
        L_0x0481:
            r0 = 1
            r1 = 1
            goto L_0x05c2
        L_0x0485:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r8.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePersonalDetails
            if (r5 == 0) goto L_0x050c
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x0509
            if (r11 != 0) goto L_0x049f
            r1 = 2131692742(0x7f0f0cc6, float:1.9014593E38)
            java.lang.String r5 = "PassportPersonalDetailsInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
            r1 = 1
            goto L_0x05c2
        L_0x049f:
            int r5 = r7.currentActivityType
            r9 = 8
            if (r5 != r9) goto L_0x04af
            r5 = 2131692655(0x7f0f0c6f, float:1.9014416E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r5)
            r1 = 1
            goto L_0x05c2
        L_0x04af:
            r1 = 1
            if (r13 != r1) goto L_0x04fd
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r11.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePassport
            if (r1 == 0) goto L_0x04c4
            r1 = 2131692671(0x7f0f0c7f, float:1.9014449E38)
            java.lang.String r5 = "PassportIdentityPassport"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
            r1 = 1
            goto L_0x05c2
        L_0x04c4:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r11.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeInternalPassport
            if (r1 == 0) goto L_0x04d6
            r1 = 2131692670(0x7f0f0c7e, float:1.9014447E38)
            java.lang.String r5 = "PassportIdentityInternalPassport"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
            r1 = 1
            goto L_0x05c2
        L_0x04d6:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r11.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeDriverLicense
            if (r1 == 0) goto L_0x04e8
            r1 = 2131692668(0x7f0f0c7c, float:1.9014443E38)
            java.lang.String r5 = "PassportIdentityDriverLicence"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
            r1 = 1
            goto L_0x05c2
        L_0x04e8:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r11.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeIdentityCard
            if (r1 == 0) goto L_0x04fa
            r1 = 2131692669(0x7f0f0c7d, float:1.9014445E38)
            java.lang.String r5 = "PassportIdentityID"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
            r1 = 1
            goto L_0x05c2
        L_0x04fa:
            r1 = 1
            goto L_0x05c2
        L_0x04fd:
            r1 = 2131692667(0x7f0f0c7b, float:1.901444E38)
            java.lang.String r5 = "PassportIdentityDocumentInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
            r1 = 1
            goto L_0x05c2
        L_0x0509:
            r1 = 1
            goto L_0x05c2
        L_0x050c:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r8.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeAddress
            if (r5 == 0) goto L_0x0596
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x0594
            if (r11 != 0) goto L_0x0526
            r1 = 2131692631(0x7f0f0c57, float:1.9014368E38)
            java.lang.String r5 = "PassportAddressNoUploadInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
            r1 = 1
            goto L_0x05c2
        L_0x0526:
            int r5 = r7.currentActivityType
            r9 = 8
            if (r5 != r9) goto L_0x0536
            r5 = 2131692655(0x7f0f0c6f, float:1.9014416E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r5)
            r1 = 1
            goto L_0x05c2
        L_0x0536:
            r1 = 1
            if (r13 != r1) goto L_0x058a
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeRentalAgreement
            if (r5 == 0) goto L_0x054a
            r5 = 2131692604(0x7f0f0c3c, float:1.9014313E38)
            java.lang.String r9 = "PassportAddAgreementInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            goto L_0x05c2
        L_0x054a:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeUtilityBill
            if (r5 == 0) goto L_0x055a
            r5 = 2131692608(0x7f0f0c40, float:1.901432E38)
            java.lang.String r9 = "PassportAddBillInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            goto L_0x05c2
        L_0x055a:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePassportRegistration
            if (r5 == 0) goto L_0x056a
            r5 = 2131692618(0x7f0f0c4a, float:1.9014341E38)
            java.lang.String r9 = "PassportAddPassportRegistrationInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            goto L_0x05c2
        L_0x056a:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeTemporaryRegistration
            if (r5 == 0) goto L_0x057a
            r5 = 2131692620(0x7f0f0c4c, float:1.9014345E38)
            java.lang.String r9 = "PassportAddTemporaryRegistrationInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            goto L_0x05c2
        L_0x057a:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r11.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeBankStatement
            if (r5 == 0) goto L_0x05c2
            r5 = 2131692606(0x7f0f0c3e, float:1.9014317E38)
            java.lang.String r9 = "PassportAddBankInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            goto L_0x05c2
        L_0x058a:
            r5 = 2131692630(0x7f0f0c56, float:1.9014366E38)
            java.lang.String r9 = "PassportAddressInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            goto L_0x05c2
        L_0x0594:
            r1 = 1
            goto L_0x05c2
        L_0x0596:
            r1 = 1
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r8.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePhone
            if (r5 == 0) goto L_0x05ad
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x05c2
            r5 = 2131692745(0x7f0f0cc9, float:1.9014599E38)
            java.lang.String r9 = "PassportPhoneInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
            goto L_0x05c2
        L_0x05ad:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r8.type
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeEmail
            if (r5 == 0) goto L_0x05c2
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x05c2
            r5 = 2131692658(0x7f0f0c72, float:1.9014422E38)
            java.lang.String r9 = "PassportEmailInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r5)
        L_0x05c2:
            r5 = r22
            r5.setValue(r3)
            android.widget.TextView r9 = r5.valueTextView
            if (r0 == 0) goto L_0x05d1
            java.lang.String r10 = "windowBackgroundWhiteRedText3"
            goto L_0x05d4
        L_0x05d1:
            java.lang.String r10 = "windowBackgroundWhiteGrayText2"
        L_0x05d4:
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r9.setTextColor(r10)
            if (r0 != 0) goto L_0x05f0
            int r9 = r7.currentActivityType
            r10 = 8
            if (r9 == r10) goto L_0x05f0
            if (r42 == 0) goto L_0x05e7
            if (r11 != 0) goto L_0x05eb
        L_0x05e7:
            if (r42 != 0) goto L_0x05f0
            if (r18 == 0) goto L_0x05f0
        L_0x05eb:
            if (r11 == 0) goto L_0x05ef
            if (r6 == 0) goto L_0x05f0
        L_0x05ef:
            goto L_0x05f1
        L_0x05f0:
            r1 = 0
        L_0x05f1:
            r5.setChecked(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.setTypeValue(im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.lang.String, im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, boolean, int):void");
    }

    /* access modifiers changed from: private */
    public void checkNativeFields(boolean byEdit) {
        String header;
        EditTextBoldCursor[] editTextBoldCursorArr;
        if (this.inputExtraFields != null) {
            String country = this.languageMap.get(this.currentResidence);
            String lang = SharedConfig.getCountryLangs().get(this.currentResidence);
            if (this.currentType.native_names && !TextUtils.isEmpty(this.currentResidence) && !"EN".equals(lang)) {
                if (this.nativeInfoCell.getVisibility() != 0) {
                    this.nativeInfoCell.setVisibility(0);
                    this.headerCell.setVisibility(0);
                    this.extraBackgroundView2.setVisibility(0);
                    int a = 0;
                    while (true) {
                        editTextBoldCursorArr = this.inputExtraFields;
                        if (a >= editTextBoldCursorArr.length) {
                            break;
                        }
                        ((View) editTextBoldCursorArr[a].getParent()).setVisibility(0);
                        a++;
                    }
                    if (editTextBoldCursorArr[0].length() == 0 && this.inputExtraFields[1].length() == 0 && this.inputExtraFields[2].length() == 0) {
                        int a2 = 0;
                        while (true) {
                            boolean[] zArr = this.nonLatinNames;
                            if (a2 >= zArr.length) {
                                break;
                            } else if (zArr[a2]) {
                                this.inputExtraFields[0].setText(this.inputFields[0].getText());
                                this.inputExtraFields[1].setText(this.inputFields[1].getText());
                                this.inputExtraFields[2].setText(this.inputFields[2].getText());
                                break;
                            } else {
                                a2++;
                            }
                        }
                    }
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                }
                this.nativeInfoCell.setText(LocaleController.formatString("PassportNativeInfo", R.string.PassportNativeInfo, country));
                if (lang != null) {
                    header = LocaleController.getServerString("PassportLanguage_" + lang);
                } else {
                    header = null;
                }
                if (header != null) {
                    this.headerCell.setText(LocaleController.formatString("PassportNativeHeaderLang", R.string.PassportNativeHeaderLang, header));
                } else {
                    this.headerCell.setText(LocaleController.getString("PassportNativeHeader", R.string.PassportNativeHeader));
                }
                for (int a3 = 0; a3 < 3; a3++) {
                    if (a3 != 0) {
                        if (a3 != 1) {
                            if (a3 == 2) {
                                if (header != null) {
                                    this.inputExtraFields[a3].setHintText(LocaleController.getString("PassportSurname", R.string.PassportSurname));
                                } else {
                                    this.inputExtraFields[a3].setHintText(LocaleController.formatString("PassportSurnameCountry", R.string.PassportSurnameCountry, country));
                                }
                            }
                        } else if (header != null) {
                            this.inputExtraFields[a3].setHintText(LocaleController.getString("PassportMidname", R.string.PassportMidname));
                        } else {
                            this.inputExtraFields[a3].setHintText(LocaleController.formatString("PassportMidnameCountry", R.string.PassportMidnameCountry, country));
                        }
                    } else if (header != null) {
                        this.inputExtraFields[a3].setHintText(LocaleController.getString("PassportName", R.string.PassportName));
                    } else {
                        this.inputExtraFields[a3].setHintText(LocaleController.formatString("PassportNameCountry", R.string.PassportNameCountry, country));
                    }
                }
                if (byEdit) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            PassportActivity.this.lambda$checkNativeFields$59$PassportActivity();
                        }
                    });
                }
            } else if (this.nativeInfoCell.getVisibility() != 8) {
                this.nativeInfoCell.setVisibility(8);
                this.headerCell.setVisibility(8);
                this.extraBackgroundView2.setVisibility(8);
                int a4 = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                    if (a4 >= editTextBoldCursorArr2.length) {
                        break;
                    }
                    ((View) editTextBoldCursorArr2[a4].getParent()).setVisibility(8);
                    a4++;
                }
                if (((this.currentBotId != 0 || this.currentDocumentsType == null) && this.currentTypeValue != null && !this.documentOnly) || this.currentDocumentsTypeValue != null) {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else {
                    this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                }
            }
        }
    }

    public /* synthetic */ void lambda$checkNativeFields$59$PassportActivity() {
        EditTextBoldCursor[] editTextBoldCursorArr = this.inputExtraFields;
        if (editTextBoldCursorArr != null) {
            scrollToField(editTextBoldCursorArr[0]);
        }
    }

    private String getErrorsString(HashMap<String, String> errors, HashMap<String, String> documentErrors) {
        HashMap<String, String> hashMap;
        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < 2; a++) {
            if (a == 0) {
                hashMap = errors;
            } else {
                hashMap = documentErrors;
            }
            if (hashMap != null) {
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    String value = entry.getValue();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                        value = value.toLowerCase();
                    }
                    if (value.endsWith(".")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    stringBuilder.append(value);
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.append('.');
        }
        return stringBuilder.toString();
    }

    private TLRPC.TL_secureValue getValueByType(TLRPC.TL_secureRequiredType requiredType, boolean check) {
        String[] keys;
        if (requiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            TLRPC.TL_secureValue secureValue = this.currentForm.values.get(a);
            if (requiredType.type.getClass() == secureValue.type.getClass()) {
                if (check) {
                    if (requiredType.selfie_required && !(secureValue.selfie instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    if (requiredType.translation_required && secureValue.translation.isEmpty()) {
                        return null;
                    }
                    if (isAddressDocument(requiredType.type) && secureValue.files.isEmpty()) {
                        return null;
                    }
                    if (isPersonalDocument(requiredType.type) && !(secureValue.front_side instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    if (((requiredType.type instanceof TLRPC.TL_secureValueTypeDriverLicense) || (requiredType.type instanceof TLRPC.TL_secureValueTypeIdentityCard)) && !(secureValue.reverse_side instanceof TLRPC.TL_secureFile)) {
                        return null;
                    }
                    if ((requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) || (requiredType.type instanceof TLRPC.TL_secureValueTypeAddress)) {
                        if (!(requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails)) {
                            keys = new String[]{"street_line1", "street_line2", "post_code", "city", RemoteConfigConstants.ResponseFieldKey.STATE, "country_code"};
                        } else if (requiredType.native_names) {
                            keys = new String[]{"first_name_native", "last_name_native", "birth_date", "gender", "country_code", "residence_country_code"};
                        } else {
                            keys = new String[]{"first_name", "last_name", "birth_date", "gender", "country_code", "residence_country_code"};
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(decryptData(secureValue.data.data, decryptValueSecret(secureValue.data.secret, secureValue.data.data_hash), secureValue.data.data_hash));
                            for (int b = 0; b < keys.length; b++) {
                                if (!jsonObject.has(keys[b]) || TextUtils.isEmpty(jsonObject.getString(keys[b]))) {
                                    return null;
                                }
                            }
                        } catch (Throwable th) {
                            return null;
                        }
                    }
                }
                return secureValue;
            }
        }
        return null;
    }

    private void openTypeActivity(TLRPC.TL_secureRequiredType requiredType, TLRPC.TL_secureRequiredType documentRequiredType, ArrayList<TLRPC.TL_secureRequiredType> availableDocumentTypes2, boolean documentOnly2) {
        int activityType;
        TLRPC.TL_secureRequiredType tL_secureRequiredType = requiredType;
        TLRPC.TL_secureRequiredType tL_secureRequiredType2 = documentRequiredType;
        ArrayList<TLRPC.TL_secureRequiredType> arrayList = availableDocumentTypes2;
        final boolean z = documentOnly2;
        final int availableDocumentTypesCount = arrayList != null ? availableDocumentTypes2.size() : 0;
        TLRPC.SecureValueType type = tL_secureRequiredType.type;
        TLRPC.SecureValueType documentType = tL_secureRequiredType2 != null ? tL_secureRequiredType2.type : null;
        if (type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
            activityType = 1;
        } else if (type instanceof TLRPC.TL_secureValueTypeAddress) {
            activityType = 2;
        } else if (type instanceof TLRPC.TL_secureValueTypePhone) {
            activityType = 3;
        } else if (type instanceof TLRPC.TL_secureValueTypeEmail) {
            activityType = 4;
        } else {
            activityType = -1;
        }
        if (activityType != -1) {
            HashMap<String, String> errors = !z ? this.errorsMap.get(getNameForType(type)) : null;
            HashMap<String, String> documentsErrors2 = this.errorsMap.get(getNameForType(documentType));
            int activityType2 = activityType;
            TLRPC.SecureValueType secureValueType = documentType;
            final TLRPC.SecureValueType type2 = type;
            PassportActivity activity = new PassportActivity(activityType, this.currentForm, this.currentPassword, requiredType, getValueByType(tL_secureRequiredType, false), documentRequiredType, getValueByType(tL_secureRequiredType2, false), (HashMap<String, String>) this.typesValues.get(tL_secureRequiredType), (HashMap<String, String>) tL_secureRequiredType2 != null ? this.typesValues.get(tL_secureRequiredType2) : null);
            activity.delegate = new PassportActivityDelegate() {
                private TLRPC.InputSecureFile getInputSecureFile(SecureDocument document) {
                    if (document.inputFile != null) {
                        TLRPC.TL_inputSecureFileUploaded inputSecureFileUploaded = new TLRPC.TL_inputSecureFileUploaded();
                        inputSecureFileUploaded.id = document.inputFile.id;
                        inputSecureFileUploaded.parts = document.inputFile.parts;
                        inputSecureFileUploaded.md5_checksum = document.inputFile.md5_checksum;
                        inputSecureFileUploaded.file_hash = document.fileHash;
                        inputSecureFileUploaded.secret = document.fileSecret;
                        return inputSecureFileUploaded;
                    }
                    TLRPC.TL_inputSecureFile inputSecureFile = new TLRPC.TL_inputSecureFile();
                    inputSecureFile.id = document.secureFile.id;
                    inputSecureFile.access_hash = document.secureFile.access_hash;
                    return inputSecureFile;
                }

                /* access modifiers changed from: private */
                public void renameFile(SecureDocument oldDocument, TLRPC.TL_secureFile newSecureFile) {
                    FileLoader.getPathToAttach(oldDocument).renameTo(FileLoader.getPathToAttach(newSecureFile));
                    ImageLoader.getInstance().replaceImageInCache(oldDocument.secureFile.dc_id + "_" + oldDocument.secureFile.id, newSecureFile.dc_id + "_" + newSecureFile.id, (ImageLocation) null, false);
                }

                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_securePlainPhone} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_securePlainEmail} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_securePlainPhone} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_securePlainPhone} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void saveValue(im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType r23, java.lang.String r24, java.lang.String r25, im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType r26, java.lang.String r27, java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r28, im.bclpbkiauv.messenger.SecureDocument r29, java.util.ArrayList<im.bclpbkiauv.messenger.SecureDocument> r30, im.bclpbkiauv.messenger.SecureDocument r31, im.bclpbkiauv.messenger.SecureDocument r32, java.lang.Runnable r33, im.bclpbkiauv.ui.PassportActivity.ErrorRunnable r34) {
                    /*
                        r22 = this;
                        r14 = r22
                        r13 = r23
                        r12 = r24
                        r11 = r26
                        r10 = r28
                        r9 = r29
                        r8 = r30
                        r7 = r31
                        r6 = r32
                        r5 = r34
                        r0 = 0
                        boolean r1 = android.text.TextUtils.isEmpty(r25)
                        if (r1 != 0) goto L_0x004f
                        im.bclpbkiauv.tgnet.TLRPC$TL_inputSecureValue r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputSecureValue
                        r1.<init>()
                        r0 = r1
                        im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r13.type
                        r0.type = r1
                        int r1 = r0.flags
                        r1 = r1 | 1
                        r0.flags = r1
                        im.bclpbkiauv.ui.PassportActivity r1 = im.bclpbkiauv.ui.PassportActivity.this
                        byte[] r2 = im.bclpbkiauv.messenger.AndroidUtilities.getStringBytes(r25)
                        im.bclpbkiauv.ui.PassportActivity$EncryptionResult r1 = r1.encryptData(r2)
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureData
                        r2.<init>()
                        r0.data = r2
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r2 = r0.data
                        byte[] r3 = r1.encryptedData
                        r2.data = r3
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r2 = r0.data
                        byte[] r3 = r1.fileHash
                        r2.data_hash = r3
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r2 = r0.data
                        byte[] r3 = r1.fileSecret
                        r2.secret = r3
                        goto L_0x0085
                    L_0x004f:
                        boolean r1 = android.text.TextUtils.isEmpty(r24)
                        if (r1 != 0) goto L_0x0085
                        im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r13
                        boolean r2 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeEmail
                        if (r2 == 0) goto L_0x0064
                        im.bclpbkiauv.tgnet.TLRPC$TL_securePlainEmail r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_securePlainEmail
                        r1.<init>()
                        r1.email = r12
                        goto L_0x0071
                    L_0x0064:
                        boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePhone
                        if (r1 == 0) goto L_0x0084
                        im.bclpbkiauv.tgnet.TLRPC$TL_securePlainPhone r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_securePlainPhone
                        r1.<init>()
                        r1.phone = r12
                    L_0x0071:
                        im.bclpbkiauv.tgnet.TLRPC$TL_inputSecureValue r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputSecureValue
                        r2.<init>()
                        r0 = r2
                        im.bclpbkiauv.tgnet.TLRPC$SecureValueType r2 = r13.type
                        r0.type = r2
                        int r2 = r0.flags
                        r2 = r2 | 32
                        r0.flags = r2
                        r0.plain_data = r1
                        goto L_0x0086
                    L_0x0084:
                        return
                    L_0x0085:
                    L_0x0086:
                        boolean r1 = r14
                        if (r1 != 0) goto L_0x0093
                        if (r0 != 0) goto L_0x0093
                        if (r5 == 0) goto L_0x0092
                        r1 = 0
                        r5.onError(r1, r1)
                    L_0x0092:
                        return
                    L_0x0093:
                        if (r11 == 0) goto L_0x0153
                        im.bclpbkiauv.tgnet.TLRPC$TL_inputSecureValue r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputSecureValue
                        r1.<init>()
                        im.bclpbkiauv.tgnet.TLRPC$SecureValueType r2 = r11.type
                        r1.type = r2
                        boolean r2 = android.text.TextUtils.isEmpty(r27)
                        if (r2 != 0) goto L_0x00cd
                        int r2 = r1.flags
                        r2 = r2 | 1
                        r1.flags = r2
                        im.bclpbkiauv.ui.PassportActivity r2 = im.bclpbkiauv.ui.PassportActivity.this
                        byte[] r3 = im.bclpbkiauv.messenger.AndroidUtilities.getStringBytes(r27)
                        im.bclpbkiauv.ui.PassportActivity$EncryptionResult r2 = r2.encryptData(r3)
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_secureData
                        r3.<init>()
                        r1.data = r3
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r3 = r1.data
                        byte[] r4 = r2.encryptedData
                        r3.data = r4
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r3 = r1.data
                        byte[] r4 = r2.fileHash
                        r3.data_hash = r4
                        im.bclpbkiauv.tgnet.TLRPC$TL_secureData r3 = r1.data
                        byte[] r4 = r2.fileSecret
                        r3.secret = r4
                    L_0x00cd:
                        if (r7 == 0) goto L_0x00db
                        im.bclpbkiauv.tgnet.TLRPC$InputSecureFile r2 = r14.getInputSecureFile(r7)
                        r1.front_side = r2
                        int r2 = r1.flags
                        r2 = r2 | 2
                        r1.flags = r2
                    L_0x00db:
                        if (r6 == 0) goto L_0x00e9
                        im.bclpbkiauv.tgnet.TLRPC$InputSecureFile r2 = r14.getInputSecureFile(r6)
                        r1.reverse_side = r2
                        int r2 = r1.flags
                        r2 = r2 | 4
                        r1.flags = r2
                    L_0x00e9:
                        if (r9 == 0) goto L_0x00f7
                        im.bclpbkiauv.tgnet.TLRPC$InputSecureFile r2 = r14.getInputSecureFile(r9)
                        r1.selfie = r2
                        int r2 = r1.flags
                        r2 = r2 | 8
                        r1.flags = r2
                    L_0x00f7:
                        if (r8 == 0) goto L_0x011e
                        boolean r2 = r30.isEmpty()
                        if (r2 != 0) goto L_0x011e
                        int r2 = r1.flags
                        r2 = r2 | 64
                        r1.flags = r2
                        r2 = 0
                        int r3 = r30.size()
                    L_0x010a:
                        if (r2 >= r3) goto L_0x011e
                        java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputSecureFile> r4 = r1.translation
                        java.lang.Object r15 = r8.get(r2)
                        im.bclpbkiauv.messenger.SecureDocument r15 = (im.bclpbkiauv.messenger.SecureDocument) r15
                        im.bclpbkiauv.tgnet.TLRPC$InputSecureFile r15 = r14.getInputSecureFile(r15)
                        r4.add(r15)
                        int r2 = r2 + 1
                        goto L_0x010a
                    L_0x011e:
                        if (r10 == 0) goto L_0x0145
                        boolean r2 = r28.isEmpty()
                        if (r2 != 0) goto L_0x0145
                        int r2 = r1.flags
                        r2 = r2 | 16
                        r1.flags = r2
                        r2 = 0
                        int r3 = r28.size()
                    L_0x0131:
                        if (r2 >= r3) goto L_0x0145
                        java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputSecureFile> r4 = r1.files
                        java.lang.Object r15 = r10.get(r2)
                        im.bclpbkiauv.messenger.SecureDocument r15 = (im.bclpbkiauv.messenger.SecureDocument) r15
                        im.bclpbkiauv.tgnet.TLRPC$InputSecureFile r15 = r14.getInputSecureFile(r15)
                        r4.add(r15)
                        int r2 = r2 + 1
                        goto L_0x0131
                    L_0x0145:
                        boolean r2 = r14
                        if (r2 == 0) goto L_0x014f
                        r0 = r1
                        r1 = 0
                        r3 = r0
                        r17 = r1
                        goto L_0x0157
                    L_0x014f:
                        r3 = r0
                        r17 = r1
                        goto L_0x0157
                    L_0x0153:
                        r1 = 0
                        r3 = r0
                        r17 = r1
                    L_0x0157:
                        r15 = r22
                        r16 = r17
                        im.bclpbkiauv.tgnet.TLRPC$TL_account_saveSecureValue r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_account_saveSecureValue
                        r0.<init>()
                        r2 = r0
                        r4 = r2
                        r2.value = r3
                        im.bclpbkiauv.ui.PassportActivity r0 = im.bclpbkiauv.ui.PassportActivity.this
                        long r0 = r0.secureSecretId
                        r2.secure_secret_id = r0
                        im.bclpbkiauv.ui.PassportActivity r0 = im.bclpbkiauv.ui.PassportActivity.this
                        int r0 = r0.currentAccount
                        im.bclpbkiauv.tgnet.ConnectionsManager r1 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)
                        im.bclpbkiauv.ui.PassportActivity$19$1 r0 = new im.bclpbkiauv.ui.PassportActivity$19$1
                        r18 = r0
                        r19 = r1
                        r1 = r22
                        r20 = r2
                        r2 = r34
                        r21 = r3
                        r3 = r24
                        r5 = r26
                        r6 = r23
                        r7 = r28
                        r8 = r29
                        r9 = r31
                        r10 = r32
                        r11 = r30
                        r12 = r25
                        r13 = r27
                        r14 = r33
                        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
                        r2 = r18
                        r1 = r19
                        r0 = r20
                        r1.sendRequest(r0, r2)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.AnonymousClass19.saveValue(im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.lang.String, im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.lang.String, java.util.ArrayList, im.bclpbkiauv.messenger.SecureDocument, java.util.ArrayList, im.bclpbkiauv.messenger.SecureDocument, im.bclpbkiauv.messenger.SecureDocument, java.lang.Runnable, im.bclpbkiauv.ui.PassportActivity$ErrorRunnable):void");
                }

                public SecureDocument saveFile(TLRPC.TL_secureFile secureFile) {
                    String path = FileLoader.getDirectory(4) + "/" + secureFile.dc_id + "_" + secureFile.id + ".jpg";
                    EncryptionResult result = PassportActivity.this.createSecureDocument(path);
                    return new SecureDocument(result.secureDocumentKey, secureFile, path, result.fileHash, result.fileSecret);
                }

                public void deleteValue(TLRPC.TL_secureRequiredType requiredType, TLRPC.TL_secureRequiredType documentRequiredType, ArrayList<TLRPC.TL_secureRequiredType> documentRequiredTypes, boolean deleteType, Runnable finishRunnable, ErrorRunnable errorRunnable) {
                    PassportActivity.this.deleteValueInternal(requiredType, documentRequiredType, documentRequiredTypes, deleteType, finishRunnable, errorRunnable, z);
                }
            };
            activity.currentAccount = this.currentAccount;
            activity.saltedPassword = this.saltedPassword;
            activity.secureSecret = this.secureSecret;
            activity.currentBotId = this.currentBotId;
            activity.fieldsErrors = errors;
            activity.documentOnly = z;
            activity.documentsErrors = documentsErrors2;
            TLRPC.SecureValueType secureValueType2 = type2;
            activity.availableDocumentTypes = availableDocumentTypes2;
            if (activityType2 == 4) {
                activity.currentEmail = this.currentEmail;
            }
            presentFragment(activity);
            return;
        }
        TLRPC.SecureValueType secureValueType3 = documentType;
        TLRPC.SecureValueType secureValueType4 = type;
        ArrayList<TLRPC.TL_secureRequiredType> arrayList2 = arrayList;
    }

    /* access modifiers changed from: private */
    public TLRPC.TL_secureValue removeValue(TLRPC.TL_secureRequiredType requiredType) {
        if (requiredType == null) {
            return null;
        }
        int size = this.currentForm.values.size();
        for (int a = 0; a < size; a++) {
            if (requiredType.type.getClass() == this.currentForm.values.get(a).type.getClass()) {
                return this.currentForm.values.remove(a);
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void deleteValueInternal(TLRPC.TL_secureRequiredType requiredType, TLRPC.TL_secureRequiredType documentRequiredType, ArrayList<TLRPC.TL_secureRequiredType> documentRequiredTypes, boolean deleteType, Runnable finishRunnable, ErrorRunnable errorRunnable, boolean documentOnly2) {
        TLRPC.TL_secureRequiredType tL_secureRequiredType = requiredType;
        TLRPC.TL_secureRequiredType tL_secureRequiredType2 = documentRequiredType;
        if (tL_secureRequiredType != null) {
            TLRPC.TL_account_deleteSecureValue req = new TLRPC.TL_account_deleteSecureValue();
            if (!documentOnly2 || tL_secureRequiredType2 == null) {
                if (deleteType) {
                    req.types.add(tL_secureRequiredType.type);
                }
                if (tL_secureRequiredType2 != null) {
                    req.types.add(tL_secureRequiredType2.type);
                }
            } else {
                req.types.add(tL_secureRequiredType2.type);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(errorRunnable, documentOnly2, documentRequiredType, requiredType, deleteType, documentRequiredTypes, finishRunnable) {
                private final /* synthetic */ PassportActivity.ErrorRunnable f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ TLRPC.TL_secureRequiredType f$3;
                private final /* synthetic */ TLRPC.TL_secureRequiredType f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ ArrayList f$6;
                private final /* synthetic */ Runnable f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.this.lambda$deleteValueInternal$61$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$deleteValueInternal$61$PassportActivity(ErrorRunnable errorRunnable, boolean documentOnly2, TLRPC.TL_secureRequiredType documentRequiredType, TLRPC.TL_secureRequiredType requiredType, boolean deleteType, ArrayList documentRequiredTypes, Runnable finishRunnable, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, errorRunnable, documentOnly2, documentRequiredType, requiredType, deleteType, documentRequiredTypes, finishRunnable) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ PassportActivity.ErrorRunnable f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ TLRPC.TL_secureRequiredType f$4;
            private final /* synthetic */ TLRPC.TL_secureRequiredType f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ ArrayList f$7;
            private final /* synthetic */ Runnable f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                PassportActivity.this.lambda$null$60$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$60$PassportActivity(im.bclpbkiauv.tgnet.TLRPC.TL_error r20, im.bclpbkiauv.ui.PassportActivity.ErrorRunnable r21, boolean r22, im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType r23, im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType r24, boolean r25, java.util.ArrayList r26, java.lang.Runnable r27) {
        /*
            r19 = this;
            r8 = r19
            r9 = r20
            r10 = r21
            r11 = r23
            r12 = r24
            r13 = r26
            if (r9 == 0) goto L_0x0026
            if (r10 == 0) goto L_0x0016
            java.lang.String r0 = r9.text
            r1 = 0
            r10.onError(r0, r1)
        L_0x0016:
            r0 = 2131689824(0x7f0f0160, float:1.9008674E38)
            java.lang.String r1 = "AppName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.String r1 = r9.text
            r8.showAlertWithText(r0, r1)
            goto L_0x0124
        L_0x0026:
            if (r22 == 0) goto L_0x0032
            if (r11 == 0) goto L_0x002e
            r8.removeValue(r11)
            goto L_0x003a
        L_0x002e:
            r8.removeValue(r12)
            goto L_0x003a
        L_0x0032:
            if (r25 == 0) goto L_0x0037
            r8.removeValue(r12)
        L_0x0037:
            r8.removeValue(r11)
        L_0x003a:
            int r0 = r8.currentActivityType
            r1 = 8
            r2 = 0
            if (r0 != r1) goto L_0x006b
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell> r0 = r8.typesViews
            java.lang.Object r0 = r0.remove(r12)
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r0 = (im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell) r0
            if (r0 == 0) goto L_0x0066
            android.widget.LinearLayout r1 = r8.linearLayout2
            r1.removeView(r0)
            android.widget.LinearLayout r1 = r8.linearLayout2
            int r3 = r1.getChildCount()
            int r3 = r3 + -6
            android.view.View r1 = r1.getChildAt(r3)
            boolean r3 = r1 instanceof im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell
            if (r3 == 0) goto L_0x0066
            r3 = r1
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r3 = (im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell) r3
            r3.setNeedDivider(r2)
        L_0x0066:
            r19.updateManageVisibility()
            goto L_0x011f
        L_0x006b:
            r0 = 0
            r1 = r23
            if (r1 == 0) goto L_0x00bc
            if (r13 == 0) goto L_0x00bc
            int r3 = r26.size()
            r4 = 1
            if (r3 <= r4) goto L_0x00bc
            r3 = 0
            int r4 = r26.size()
        L_0x007e:
            if (r3 >= r4) goto L_0x00ad
            java.lang.Object r5 = r13.get(r3)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r5
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r6 = r8.getValueByType(r5, r2)
            if (r6 == 0) goto L_0x00aa
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r7 = r6.data
            if (r7 == 0) goto L_0x00a8
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r7 = r6.data
            byte[] r7 = r7.data
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r14 = r6.data
            byte[] r14 = r14.secret
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r15 = r6.data
            byte[] r15 = r15.data_hash
            byte[] r14 = r8.decryptValueSecret(r14, r15)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r15 = r6.data
            byte[] r15 = r15.data_hash
            java.lang.String r0 = r8.decryptData(r7, r14, r15)
        L_0x00a8:
            r1 = r5
            goto L_0x00ad
        L_0x00aa:
            int r3 = r3 + 1
            goto L_0x007e
        L_0x00ad:
            if (r1 != 0) goto L_0x00b9
            java.lang.Object r3 = r13.get(r2)
            r1 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r1
            r14 = r0
            r15 = r1
            goto L_0x00be
        L_0x00b9:
            r14 = r0
            r15 = r1
            goto L_0x00be
        L_0x00bc:
            r14 = r0
            r15 = r1
        L_0x00be:
            if (r25 == 0) goto L_0x00d9
            r3 = 0
            r4 = 0
            if (r13 == 0) goto L_0x00ca
            int r0 = r26.size()
            r7 = r0
            goto L_0x00cb
        L_0x00ca:
            r7 = 0
        L_0x00cb:
            r0 = r19
            r1 = r24
            r2 = r3
            r3 = r4
            r4 = r15
            r5 = r14
            r6 = r22
            r0.setTypeValue(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x011f
        L_0x00d9:
            r0 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r7 = r8.getValueByType(r12, r2)
            if (r7 == 0) goto L_0x00ff
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r1 = r7.data
            if (r1 == 0) goto L_0x00ff
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r1 = r7.data
            byte[] r1 = r1.data
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r3 = r7.data
            byte[] r3 = r3.secret
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r4 = r7.data
            byte[] r4 = r4.data_hash
            byte[] r3 = r8.decryptValueSecret(r3, r4)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r4 = r7.data
            byte[] r4 = r4.data_hash
            java.lang.String r0 = r8.decryptData(r1, r3, r4)
            r16 = r0
            goto L_0x0101
        L_0x00ff:
            r16 = r0
        L_0x0101:
            r3 = 0
            if (r13 == 0) goto L_0x010b
            int r0 = r26.size()
            r17 = r0
            goto L_0x010d
        L_0x010b:
            r17 = 0
        L_0x010d:
            r0 = r19
            r1 = r24
            r2 = r3
            r3 = r16
            r4 = r15
            r5 = r14
            r6 = r22
            r18 = r7
            r7 = r17
            r0.setTypeValue(r1, r2, r3, r4, r5, r6, r7)
        L_0x011f:
            if (r27 == 0) goto L_0x0124
            r27.run()
        L_0x0124:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.lambda$null$60$PassportActivity(im.bclpbkiauv.tgnet.TLRPC$TL_error, im.bclpbkiauv.ui.PassportActivity$ErrorRunnable, boolean, im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, boolean, java.util.ArrayList, java.lang.Runnable):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x01b1  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x022d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private im.bclpbkiauv.ui.PassportActivity.TextDetailSecureCell addField(android.content.Context r22, im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType r23, java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType> r24, boolean r25, boolean r26) {
        /*
            r21 = this;
            r8 = r21
            r9 = r23
            r10 = r24
            r11 = r25
            r0 = 0
            if (r10 == 0) goto L_0x0011
            int r1 = r24.size()
            r7 = r1
            goto L_0x0012
        L_0x0011:
            r7 = 0
        L_0x0012:
            im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell r1 = new im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell
            r12 = r22
            r1.<init>(r8, r12)
            r13 = r1
            r1 = 1
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r1)
            r13.setBackgroundDrawable(r2)
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r2 = r9.type
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePersonalDetails
            r3 = 2131692781(0x7f0f0ced, float:1.9014672E38)
            java.lang.String r4 = "PassportTwoDocuments"
            r5 = 2
            java.lang.String r6 = ""
            if (r2 == 0) goto L_0x0093
            if (r10 == 0) goto L_0x0083
            boolean r2 = r24.isEmpty()
            if (r2 == 0) goto L_0x0039
            goto L_0x0083
        L_0x0039:
            if (r11 == 0) goto L_0x004e
            int r2 = r24.size()
            if (r2 != r1) goto L_0x004e
            java.lang.Object r1 = r10.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r1
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r1.type
            java.lang.String r1 = r8.getTextForType(r1)
            goto L_0x008c
        L_0x004e:
            if (r11 == 0) goto L_0x0079
            int r2 = r24.size()
            if (r2 != r5) goto L_0x0079
            java.lang.Object[] r2 = new java.lang.Object[r5]
            java.lang.Object r5 = r10.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r5
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r5.type
            java.lang.String r5 = r8.getTextForType(r5)
            r2[r0] = r5
            java.lang.Object r5 = r10.get(r1)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r5
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r5.type
            java.lang.String r5 = r8.getTextForType(r5)
            r2[r1] = r5
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r2)
            goto L_0x008c
        L_0x0079:
            r1 = 2131692666(0x7f0f0c7a, float:1.9014439E38)
            java.lang.String r2 = "PassportIdentityDocument"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            goto L_0x008c
        L_0x0083:
            r1 = 2131692741(0x7f0f0cc5, float:1.901459E38)
            java.lang.String r2 = "PassportPersonalDetails"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
        L_0x008c:
            r2 = r26 ^ 1
            r13.setTextAndValue(r1, r6, r2)
            goto L_0x0124
        L_0x0093:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r2 = r9.type
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeAddress
            if (r2 == 0) goto L_0x00fb
            if (r10 == 0) goto L_0x00ec
            boolean r2 = r24.isEmpty()
            if (r2 == 0) goto L_0x00a2
            goto L_0x00ec
        L_0x00a2:
            if (r11 == 0) goto L_0x00b7
            int r2 = r24.size()
            if (r2 != r1) goto L_0x00b7
            java.lang.Object r1 = r10.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r1
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r1.type
            java.lang.String r1 = r8.getTextForType(r1)
            goto L_0x00f5
        L_0x00b7:
            if (r11 == 0) goto L_0x00e2
            int r2 = r24.size()
            if (r2 != r5) goto L_0x00e2
            java.lang.Object[] r2 = new java.lang.Object[r5]
            java.lang.Object r5 = r10.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r5
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r5.type
            java.lang.String r5 = r8.getTextForType(r5)
            r2[r0] = r5
            java.lang.Object r5 = r10.get(r1)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r5
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r5 = r5.type
            java.lang.String r5 = r8.getTextForType(r5)
            r2[r1] = r5
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r2)
            goto L_0x00f5
        L_0x00e2:
            r1 = 2131692759(0x7f0f0cd7, float:1.9014627E38)
            java.lang.String r2 = "PassportResidentialAddress"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            goto L_0x00f5
        L_0x00ec:
            r1 = 2131692628(0x7f0f0c54, float:1.9014361E38)
            java.lang.String r2 = "PassportAddress"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
        L_0x00f5:
            r2 = r26 ^ 1
            r13.setTextAndValue(r1, r6, r2)
            goto L_0x0124
        L_0x00fb:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r9.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypePhone
            if (r1 == 0) goto L_0x0110
            r1 = 2131692744(0x7f0f0cc8, float:1.9014597E38)
            java.lang.String r2 = "PassportPhone"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r2 = r26 ^ 1
            r13.setTextAndValue(r1, r6, r2)
            goto L_0x0124
        L_0x0110:
            im.bclpbkiauv.tgnet.TLRPC$SecureValueType r1 = r9.type
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_secureValueTypeEmail
            if (r1 == 0) goto L_0x0124
            r1 = 2131692656(0x7f0f0c70, float:1.9014418E38)
            java.lang.String r2 = "PassportEmail"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r2 = r26 ^ 1
            r13.setTextAndValue(r1, r6, r2)
        L_0x0124:
            int r1 = r8.currentActivityType
            r2 = 8
            r3 = -2
            r4 = -1
            if (r1 != r2) goto L_0x013c
            android.widget.LinearLayout r1 = r8.linearLayout2
            int r2 = r1.getChildCount()
            int r2 = r2 + -5
            android.widget.LinearLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r4, (int) r3)
            r1.addView(r13, r2, r3)
            goto L_0x0145
        L_0x013c:
            android.widget.LinearLayout r1 = r8.linearLayout2
            android.widget.LinearLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r4, (int) r3)
            r1.addView(r13, r2)
        L_0x0145:
            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$YaBJNhI7BVaO4af8aWPWr0Vm-pE r1 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$YaBJNhI7BVaO4af8aWPWr0Vm-pE
            r1.<init>(r10, r9, r11)
            r13.setOnClickListener(r1)
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell> r1 = r8.typesViews
            r1.put(r9, r13)
            r1 = 0
            r2 = 0
            r3 = 0
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r4 = r8.typesValues
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r4.put(r9, r5)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r14 = r8.getValueByType(r9, r0)
            if (r14 == 0) goto L_0x01a5
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r4 = r14.plain_data
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_securePlainEmail
            if (r4 == 0) goto L_0x0175
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r4 = r14.plain_data
            im.bclpbkiauv.tgnet.TLRPC$TL_securePlainEmail r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_securePlainEmail) r4
            java.lang.String r1 = r4.email
            r15 = r1
            r16 = r2
            goto L_0x01a8
        L_0x0175:
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r4 = r14.plain_data
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_securePlainPhone
            if (r4 == 0) goto L_0x0185
            im.bclpbkiauv.tgnet.TLRPC$SecurePlainData r4 = r14.plain_data
            im.bclpbkiauv.tgnet.TLRPC$TL_securePlainPhone r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_securePlainPhone) r4
            java.lang.String r1 = r4.phone
            r15 = r1
            r16 = r2
            goto L_0x01a8
        L_0x0185:
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r4 = r14.data
            if (r4 == 0) goto L_0x01a5
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r4 = r14.data
            byte[] r4 = r4.data
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r5 = r14.data
            byte[] r5 = r5.secret
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r6 = r14.data
            byte[] r6 = r6.data_hash
            byte[] r5 = r8.decryptValueSecret(r5, r6)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r6 = r14.data
            byte[] r6 = r6.data_hash
            java.lang.String r2 = r8.decryptData(r4, r5, r6)
            r15 = r1
            r16 = r2
            goto L_0x01a8
        L_0x01a5:
            r15 = r1
            r16 = r2
        L_0x01a8:
            r1 = 0
            if (r10 == 0) goto L_0x022d
            boolean r2 = r24.isEmpty()
            if (r2 != 0) goto L_0x022d
            r2 = 0
            r4 = 0
            int r5 = r24.size()
        L_0x01b7:
            if (r4 >= r5) goto L_0x0217
            java.lang.Object r6 = r10.get(r4)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r6
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.util.HashMap<java.lang.String, java.lang.String>> r0 = r8.typesValues
            r18 = r3
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            r0.put(r6, r3)
            java.util.HashMap<im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType> r0 = r8.documentsToTypesLink
            r0.put(r6, r9)
            if (r2 != 0) goto L_0x0209
            r0 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_secureValue r3 = r8.getValueByType(r6, r0)
            if (r3 == 0) goto L_0x0204
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r0 = r3.data
            if (r0 == 0) goto L_0x01fa
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r0 = r3.data
            byte[] r0 = r0.data
            r19 = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r2 = r3.data
            byte[] r2 = r2.secret
            r20 = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r5 = r3.data
            byte[] r5 = r5.data_hash
            byte[] r2 = r8.decryptValueSecret(r2, r5)
            im.bclpbkiauv.tgnet.TLRPC$TL_secureData r5 = r3.data
            byte[] r5 = r5.data_hash
            java.lang.String r0 = r8.decryptData(r0, r2, r5)
            goto L_0x0200
        L_0x01fa:
            r19 = r2
            r20 = r5
            r0 = r18
        L_0x0200:
            r1 = r6
            r2 = 1
            r3 = r0
            goto L_0x0211
        L_0x0204:
            r19 = r2
            r20 = r5
            goto L_0x020d
        L_0x0209:
            r19 = r2
            r20 = r5
        L_0x020d:
            r3 = r18
            r2 = r19
        L_0x0211:
            int r4 = r4 + 1
            r5 = r20
            r0 = 0
            goto L_0x01b7
        L_0x0217:
            r19 = r2
            r18 = r3
            r20 = r5
            if (r1 != 0) goto L_0x022a
            r0 = 0
            java.lang.Object r0 = r10.get(r0)
            r1 = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_secureRequiredType) r1
            r17 = r1
            goto L_0x0231
        L_0x022a:
            r17 = r1
            goto L_0x0231
        L_0x022d:
            r17 = r1
            r18 = r3
        L_0x0231:
            r0 = r21
            r1 = r23
            r2 = r15
            r3 = r16
            r4 = r17
            r5 = r18
            r6 = r25
            r0.setTypeValue(r1, r2, r3, r4, r5, r6, r7)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.addField(android.content.Context, im.bclpbkiauv.tgnet.TLRPC$TL_secureRequiredType, java.util.ArrayList, boolean, boolean):im.bclpbkiauv.ui.PassportActivity$TextDetailSecureCell");
    }

    public /* synthetic */ void lambda$addField$65$PassportActivity(ArrayList documentRequiredTypes, TLRPC.TL_secureRequiredType requiredType, boolean documentOnly2, View v) {
        String str;
        int i;
        TLRPC.TL_secureRequiredType documentsType = null;
        if (documentRequiredTypes != null) {
            int a = 0;
            int count = documentRequiredTypes.size();
            while (true) {
                if (a >= count) {
                    break;
                }
                TLRPC.TL_secureRequiredType documentType = (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(a);
                if (getValueByType(documentType, false) != null || count == 1) {
                    documentsType = documentType;
                } else {
                    a++;
                }
            }
        }
        if (!(requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) && !(requiredType.type instanceof TLRPC.TL_secureValueTypeAddress)) {
            boolean z = requiredType.type instanceof TLRPC.TL_secureValueTypePhone;
            boolean phoneField = z;
            if ((z || (requiredType.type instanceof TLRPC.TL_secureValueTypeEmail)) && getValueByType(requiredType, false) != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(requiredType, documentOnly2) {
                    private final /* synthetic */ TLRPC.TL_secureRequiredType f$1;
                    private final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PassportActivity.this.lambda$null$64$PassportActivity(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (phoneField) {
                    i = R.string.PassportDeletePhoneAlert;
                    str = "PassportDeletePhoneAlert";
                } else {
                    i = R.string.PassportDeleteEmailAlert;
                    str = "PassportDeleteEmailAlert";
                }
                builder.setMessage(LocaleController.getString(str, i));
                showDialog(builder.create());
                return;
            }
        } else if (documentsType == null && documentRequiredTypes != null && !documentRequiredTypes.isEmpty()) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            if (requiredType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                builder2.setTitle(LocaleController.getString("PassportIdentityDocument", R.string.PassportIdentityDocument));
            } else if (requiredType.type instanceof TLRPC.TL_secureValueTypeAddress) {
                builder2.setTitle(LocaleController.getString("PassportAddress", R.string.PassportAddress));
            }
            ArrayList<String> strings = new ArrayList<>();
            int count2 = documentRequiredTypes.size();
            for (int a2 = 0; a2 < count2; a2++) {
                TLRPC.TL_secureRequiredType documentType2 = (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(a2);
                if (documentType2.type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                    strings.add(LocaleController.getString("PassportAddLicence", R.string.PassportAddLicence));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypePassport) {
                    strings.add(LocaleController.getString("PassportAddPassport", R.string.PassportAddPassport));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                    strings.add(LocaleController.getString("PassportAddInternalPassport", R.string.PassportAddInternalPassport));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                    strings.add(LocaleController.getString("PassportAddCard", R.string.PassportAddCard));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                    strings.add(LocaleController.getString("PassportAddBill", R.string.PassportAddBill));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeBankStatement) {
                    strings.add(LocaleController.getString("PassportAddBank", R.string.PassportAddBank));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                    strings.add(LocaleController.getString("PassportAddAgreement", R.string.PassportAddAgreement));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                    strings.add(LocaleController.getString("PassportAddTemporaryRegistration", R.string.PassportAddTemporaryRegistration));
                } else if (documentType2.type instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                    strings.add(LocaleController.getString("PassportAddPassportRegistration", R.string.PassportAddPassportRegistration));
                }
            }
            builder2.setItems((CharSequence[]) strings.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(requiredType, documentRequiredTypes, documentOnly2) {
                private final /* synthetic */ TLRPC.TL_secureRequiredType f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    PassportActivity.this.lambda$null$62$PassportActivity(this.f$1, this.f$2, this.f$3, dialogInterface, i);
                }
            });
            showDialog(builder2.create());
            return;
        }
        openTypeActivity(requiredType, documentsType, documentRequiredTypes, documentOnly2);
    }

    public /* synthetic */ void lambda$null$62$PassportActivity(TLRPC.TL_secureRequiredType requiredType, ArrayList documentRequiredTypes, boolean documentOnly2, DialogInterface dialog, int which) {
        openTypeActivity(requiredType, (TLRPC.TL_secureRequiredType) documentRequiredTypes.get(which), documentRequiredTypes, documentOnly2);
    }

    public /* synthetic */ void lambda$null$64$PassportActivity(TLRPC.TL_secureRequiredType requiredType, boolean documentOnly2, DialogInterface dialog, int which) {
        needShowProgress();
        deleteValueInternal(requiredType, (TLRPC.TL_secureRequiredType) null, (ArrayList<TLRPC.TL_secureRequiredType>) null, true, new Runnable() {
            public final void run() {
                PassportActivity.this.needHideProgress();
            }
        }, new ErrorRunnable() {
            public final void onError(String str, String str2) {
                PassportActivity.this.lambda$null$63$PassportActivity(str, str2);
            }
        }, documentOnly2);
    }

    public /* synthetic */ void lambda$null$63$PassportActivity(String error, String text) {
        needHideProgress();
    }

    private class EncryptionResult {
        byte[] decrypyedFileSecret;
        byte[] encryptedData;
        byte[] fileHash;
        byte[] fileSecret;
        SecureDocumentKey secureDocumentKey;

        public EncryptionResult(byte[] d, byte[] fs, byte[] dfs, byte[] fh, byte[] fk, byte[] fi) {
            this.encryptedData = d;
            this.fileSecret = fs;
            this.fileHash = fh;
            this.decrypyedFileSecret = dfs;
            this.secureDocumentKey = new SecureDocumentKey(fk, fi);
        }
    }

    private SecureDocumentKey getSecureDocumentKey(byte[] file_secret, byte[] file_hash) {
        byte[] file_secret_hash = Utilities.computeSHA512(decryptValueSecret(file_secret, file_hash), file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        return new SecureDocumentKey(file_key, file_iv);
    }

    /* access modifiers changed from: private */
    public byte[] decryptSecret(byte[] secret, byte[] passwordHash) {
        if (secret == null || secret.length != 32) {
            return null;
        }
        byte[] key = new byte[32];
        System.arraycopy(passwordHash, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(passwordHash, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(secret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        return decryptedSecret;
    }

    private byte[] decryptValueSecret(byte[] encryptedSecureValueSecret, byte[] hash) {
        byte[] bArr = encryptedSecureValueSecret;
        byte[] bArr2 = hash;
        if (bArr == null || bArr.length != 32 || bArr2 == null || bArr2.length != 32) {
            return null;
        }
        byte[] key = new byte[32];
        System.arraycopy(this.saltedPassword, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(this.saltedPassword, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(this.secureSecret, 0, decryptedSecret, 0, 32);
        byte[] decryptedSecret2 = decryptedSecret;
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        if (!checkSecret(decryptedSecret2, (Long) null)) {
            return null;
        }
        byte[] secret_hash = Utilities.computeSHA512(decryptedSecret2, bArr2);
        byte[] file_secret_key = new byte[32];
        System.arraycopy(secret_hash, 0, file_secret_key, 0, 32);
        byte[] file_secret_iv = new byte[16];
        System.arraycopy(secret_hash, 32, file_secret_iv, 0, 16);
        byte[] result = new byte[32];
        System.arraycopy(bArr, 0, result, 0, 32);
        byte[] result2 = result;
        byte[] bArr3 = file_secret_iv;
        byte[] bArr4 = file_secret_key;
        Utilities.aesCbcEncryptionByteArraySafe(result, file_secret_key, file_secret_iv, 0, result.length, 0, 0);
        return result2;
    }

    /* access modifiers changed from: private */
    public EncryptionResult createSecureDocument(String path) {
        byte[] b = new byte[((int) new File(path).length())];
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(path, "rws");
            f.readFully(b);
        } catch (Exception e) {
        }
        EncryptionResult result = encryptData(b);
        try {
            f.seek(0);
            f.write(result.encryptedData);
            f.close();
        } catch (Exception e2) {
        }
        return result;
    }

    private String decryptData(byte[] data, byte[] file_secret, byte[] file_hash) {
        byte[] bArr = data;
        byte[] bArr2 = file_secret;
        byte[] bArr3 = file_hash;
        if (bArr == null || bArr2 == null || bArr2.length != 32 || bArr3 == null || bArr3.length != 32) {
            return null;
        }
        byte[] file_secret_hash = Utilities.computeSHA512(file_secret, file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        byte[] decryptedData = new byte[bArr.length];
        System.arraycopy(bArr, 0, decryptedData, 0, bArr.length);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedData, file_key, file_iv, 0, decryptedData.length, 0, 0);
        if (!Arrays.equals(Utilities.computeSHA256(decryptedData), bArr3)) {
            return null;
        }
        int dataOffset = decryptedData[0] & 255;
        return new String(decryptedData, dataOffset, decryptedData.length - dataOffset);
    }

    public static boolean checkSecret(byte[] secret, Long id) {
        if (secret == null || secret.length != 32) {
            return false;
        }
        int sum = 0;
        for (byte b : secret) {
            sum += b & UByte.MAX_VALUE;
        }
        if (sum % 255 != 239) {
            return false;
        }
        if (id == null || Utilities.bytesToLong(Utilities.computeSHA256(secret)) == id.longValue()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public byte[] getRandomSecret() {
        byte[] secret = new byte[32];
        Utilities.random.nextBytes(secret);
        int sum = 0;
        for (byte b : secret) {
            sum += b & UByte.MAX_VALUE;
        }
        int sum2 = sum % 255;
        if (sum2 != 239) {
            int a = Utilities.random.nextInt(32);
            int val = (secret[a] & 255) + (239 - sum2);
            if (val < 255) {
                val += 255;
            }
            secret[a] = (byte) (val % 255);
        }
        return secret;
    }

    /* access modifiers changed from: private */
    public EncryptionResult encryptData(byte[] data) {
        byte[] bArr = data;
        byte[] file_secret = getRandomSecret();
        int extraLen = Utilities.random.nextInt(208) + 32;
        while ((bArr.length + extraLen) % 16 != 0) {
            extraLen++;
        }
        byte[] padding = new byte[extraLen];
        Utilities.random.nextBytes(padding);
        padding[0] = (byte) extraLen;
        byte[] paddedData = new byte[(bArr.length + extraLen)];
        System.arraycopy(padding, 0, paddedData, 0, extraLen);
        System.arraycopy(bArr, 0, paddedData, extraLen, bArr.length);
        byte[] file_hash = Utilities.computeSHA256(paddedData);
        byte[] file_secret_hash = Utilities.computeSHA512(file_secret, file_hash);
        byte[] file_key = new byte[32];
        System.arraycopy(file_secret_hash, 0, file_key, 0, 32);
        byte[] file_iv = new byte[16];
        System.arraycopy(file_secret_hash, 32, file_iv, 0, 16);
        Utilities.aesCbcEncryptionByteArraySafe(paddedData, file_key, file_iv, 0, paddedData.length, 0, 1);
        byte[] key = new byte[32];
        System.arraycopy(this.saltedPassword, 0, key, 0, 32);
        byte[] iv = new byte[16];
        System.arraycopy(this.saltedPassword, 32, iv, 0, 16);
        byte[] decryptedSecret = new byte[32];
        System.arraycopy(this.secureSecret, 0, decryptedSecret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(decryptedSecret, key, iv, 0, decryptedSecret.length, 0, 0);
        byte[] secret_hash = Utilities.computeSHA512(decryptedSecret, file_hash);
        byte[] file_secret_key = new byte[32];
        System.arraycopy(secret_hash, 0, file_secret_key, 0, 32);
        byte[] file_secret_iv = new byte[16];
        byte[] file_iv2 = file_iv;
        System.arraycopy(secret_hash, 32, file_secret_iv, 0, 16);
        byte[] encrypyed_file_secret = new byte[32];
        System.arraycopy(file_secret, 0, encrypyed_file_secret, 0, 32);
        Utilities.aesCbcEncryptionByteArraySafe(encrypyed_file_secret, file_secret_key, file_secret_iv, 0, encrypyed_file_secret.length, 0, 1);
        byte[] bArr2 = secret_hash;
        byte[] bArr3 = file_secret_hash;
        byte[] bArr4 = file_hash;
        byte[] bArr5 = paddedData;
        return new EncryptionResult(paddedData, encrypyed_file_secret, file_secret, file_hash, file_key, file_iv2);
    }

    /* access modifiers changed from: private */
    public void showAlertWithText(String title, String text) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder.setTitle(title);
            builder.setMessage(text);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public void onPasscodeError(boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                this.inputFields[0].setText("");
            }
            AndroidUtilities.shakeView(this.inputFields[0], 2.0f, 0);
        }
    }

    /* access modifiers changed from: private */
    public void startPhoneVerification(boolean checkPermissions, String phone, Runnable finishRunnable, ErrorRunnable errorRunnable, PassportActivityDelegate delegate2) {
        TLRPC.TL_account_sendVerifyPhoneCode req = new TLRPC.TL_account_sendVerifyPhoneCode();
        req.phone_number = phone;
        req.settings = new TLRPC.TL_codeSettings();
        req.settings.allow_flashcall = false;
        req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (req.settings.allow_app_hash) {
            preferences.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
        } else {
            preferences.edit().remove("sms_hash").commit();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(phone, delegate2, req) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ PassportActivity.PassportActivityDelegate f$2;
            private final /* synthetic */ TLRPC.TL_account_sendVerifyPhoneCode f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PassportActivity.this.lambda$startPhoneVerification$67$PassportActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$startPhoneVerification$67$PassportActivity(String phone, PassportActivityDelegate delegate2, TLRPC.TL_account_sendVerifyPhoneCode req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, phone, delegate2, response, req) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ PassportActivity.PassportActivityDelegate f$3;
            private final /* synthetic */ TLObject f$4;
            private final /* synthetic */ TLRPC.TL_account_sendVerifyPhoneCode f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                PassportActivity.this.lambda$null$66$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$66$PassportActivity(TLRPC.TL_error error, String phone, PassportActivityDelegate delegate2, TLObject response, TLRPC.TL_account_sendVerifyPhoneCode req) {
        TLRPC.TL_error tL_error = error;
        String str = phone;
        if (tL_error == null) {
            HashMap<String, String> values = new HashMap<>();
            values.put("phone", str);
            PassportActivity activity = new PassportActivity(7, this.currentForm, this.currentPassword, this.currentType, (TLRPC.TL_secureValue) null, (TLRPC.TL_secureRequiredType) null, (TLRPC.TL_secureValue) null, values, (HashMap<String, String>) null);
            activity.currentAccount = this.currentAccount;
            activity.saltedPassword = this.saltedPassword;
            activity.secureSecret = this.secureSecret;
            activity.delegate = delegate2;
            activity.currentPhoneVerification = (TLRPC.TL_auth_sentCode) response;
            presentFragment(activity, true);
            TLRPC.TL_account_sendVerifyPhoneCode tL_account_sendVerifyPhoneCode = req;
            return;
        }
        PassportActivityDelegate passportActivityDelegate = delegate2;
        AlertsCreator.processError(this.currentAccount, tL_error, this, req, str);
    }

    /* access modifiers changed from: private */
    public void updatePasswordInterface() {
        if (this.noPasswordImageView != null) {
            TLRPC.TL_account_password tL_account_password = this.currentPassword;
            if (tL_account_password == null || this.usingSavedPassword != 0) {
                this.noPasswordImageView.setVisibility(8);
                this.noPasswordTextView.setVisibility(8);
                this.noPasswordSetTextView.setVisibility(8);
                this.passwordAvatarContainer.setVisibility(8);
                this.inputFieldContainers[0].setVisibility(8);
                this.doneItem.setVisibility(8);
                this.passwordForgotButton.setVisibility(8);
                this.passwordInfoRequestTextView.setVisibility(8);
                this.passwordRequestTextView.setVisibility(8);
                this.emptyView.setVisibility(0);
            } else if (!tL_account_password.has_password) {
                this.passwordRequestTextView.setVisibility(0);
                this.noPasswordImageView.setVisibility(0);
                this.noPasswordTextView.setVisibility(0);
                this.noPasswordSetTextView.setVisibility(0);
                this.passwordAvatarContainer.setVisibility(8);
                this.inputFieldContainers[0].setVisibility(8);
                this.doneItem.setVisibility(8);
                this.passwordForgotButton.setVisibility(8);
                this.passwordInfoRequestTextView.setVisibility(8);
                this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 25.0f, 0.0f, 0.0f));
                this.emptyView.setVisibility(8);
            } else {
                this.passwordRequestTextView.setVisibility(0);
                this.noPasswordImageView.setVisibility(8);
                this.noPasswordTextView.setVisibility(8);
                this.noPasswordSetTextView.setVisibility(8);
                this.emptyView.setVisibility(8);
                this.passwordAvatarContainer.setVisibility(0);
                this.inputFieldContainers[0].setVisibility(0);
                this.doneItem.setVisibility(0);
                this.passwordForgotButton.setVisibility(0);
                this.passwordInfoRequestTextView.setVisibility(0);
                this.passwordRequestTextView.setLayoutParams(LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
                if (this.inputFields != null) {
                    TLRPC.TL_account_password tL_account_password2 = this.currentPassword;
                    if (tL_account_password2 == null || TextUtils.isEmpty(tL_account_password2.hint)) {
                        this.inputFields[0].setHint(LocaleController.getString("LoginPassword", R.string.LoginPassword));
                    } else {
                        this.inputFields[0].setHint(this.currentPassword.hint);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void showEditDoneProgress(boolean animateDoneItem, boolean show) {
        final boolean z = show;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (animateDoneItem && this.doneItem != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), View.ALPHA, new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            PassportActivity.this.progressView.setVisibility(4);
                        } else {
                            PassportActivity.this.doneItem.getContentView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        } else if (this.acceptTextView != null) {
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.progressViewButton.setVisibility(0);
                this.bottomLayout.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{1.0f})});
            } else {
                this.acceptTextView.setVisibility(0);
                this.bottomLayout.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressViewButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            PassportActivity.this.progressViewButton.setVisibility(4);
                        } else {
                            PassportActivity.this.acceptTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (PassportActivity.this.doneItemAnimation != null && PassportActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = PassportActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        SecureDocumentCell cell;
        ActionBarMenuItem actionBarMenuItem;
        if (id == NotificationCenter.FileDidUpload) {
            String location = args[0];
            SecureDocument document = this.uploadingDocuments.get(location);
            if (document != null) {
                document.inputFile = args[1];
                this.uploadingDocuments.remove(location);
                if (this.uploadingDocuments.isEmpty() && (actionBarMenuItem = this.doneItem) != null) {
                    actionBarMenuItem.setEnabled(true);
                    this.doneItem.setAlpha(1.0f);
                }
                HashMap<SecureDocument, SecureDocumentCell> hashMap = this.documentsCells;
                if (!(hashMap == null || (cell = hashMap.get(document)) == null)) {
                    cell.updateButtonState(true);
                }
                HashMap<String, String> hashMap2 = this.errorsValues;
                if (hashMap2 != null && hashMap2.containsKey("error_document_all")) {
                    this.errorsValues.remove("error_document_all");
                    checkTopErrorCell(false);
                }
                if (document.type == 0) {
                    if (this.bottomCell != null && !TextUtils.isEmpty(this.noAllDocumentsErrorText)) {
                        this.bottomCell.setText(this.noAllDocumentsErrorText);
                    }
                    this.errorsValues.remove("files_all");
                } else if (document.type == 4) {
                    if (this.bottomCellTranslation != null && !TextUtils.isEmpty(this.noAllTranslationErrorText)) {
                        this.bottomCellTranslation.setText(this.noAllTranslationErrorText);
                    }
                    this.errorsValues.remove("translation_all");
                }
            }
        } else if (id != NotificationCenter.FileDidFailUpload) {
            if (id == NotificationCenter.didSetTwoStepPassword) {
                if (args == null || args.length <= 0) {
                    this.currentPassword = null;
                    loadPasswordInfo();
                } else {
                    if (args[7] != null) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                        if (editTextBoldCursorArr[0] != null) {
                            editTextBoldCursorArr[0].setText(args[7]);
                        }
                    }
                    if (args[6] == null) {
                        TLRPC.TL_account_password tL_account_password = new TLRPC.TL_account_password();
                        this.currentPassword = tL_account_password;
                        tL_account_password.current_algo = args[1];
                        this.currentPassword.new_secure_algo = args[2];
                        this.currentPassword.secure_random = args[3];
                        this.currentPassword.has_recovery = !TextUtils.isEmpty(args[4]);
                        this.currentPassword.hint = args[5];
                        this.currentPassword.srp_id = -1;
                        this.currentPassword.srp_B = new byte[256];
                        Utilities.random.nextBytes(this.currentPassword.srp_B);
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputFields;
                        if (editTextBoldCursorArr2[0] != null && editTextBoldCursorArr2[0].length() > 0) {
                            this.usingSavedPassword = 2;
                        }
                    }
                }
                updatePasswordInterface();
                return;
            }
            int i = NotificationCenter.didRemoveTwoStepPassword;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (this.presentAfterAnimation != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    PassportActivity.this.lambda$onTransitionAnimationEnd$68$PassportActivity();
                }
            });
        }
        int i = this.currentActivityType;
        if (i == 5) {
            if (isOpen) {
                if (this.inputFieldContainers[0].getVisibility() == 0) {
                    this.inputFields[0].requestFocus();
                    AndroidUtilities.showKeyboard(this.inputFields[0]);
                }
                if (this.usingSavedPassword == 2) {
                    onPasswordDone(false);
                }
            }
        } else if (i == 7) {
            if (isOpen) {
                this.views[this.currentViewNum].onShow();
            }
        } else if (i == 4) {
            if (isOpen) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if (i == 6) {
            if (isOpen) {
                this.inputFields[0].requestFocus();
                AndroidUtilities.showKeyboard(this.inputFields[0]);
            }
        } else if ((i == 2 || i == 1) && Build.VERSION.SDK_INT >= 21) {
            createChatAttachView();
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$68$PassportActivity() {
        presentFragment(this.presentAfterAnimation, true);
        this.presentAfterAnimation = null;
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            ToastUtils.show((int) R.string.UnsupportedAttachment);
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 0 || requestCode == 2) {
            createChatAttachView();
            ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
            if (chatAttachAlert2 != null) {
                chatAttachAlert2.onActivityResultFragment(requestCode, data, this.currentPicturePath);
            }
            this.currentPicturePath = null;
        } else if (requestCode != 1) {
        } else {
            if (data == null || data.getData() == null) {
                showAttachmentError();
                return;
            }
            ArrayList<SendMessagesHelper.SendingMediaInfo> photos = new ArrayList<>();
            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
            info.uri = data.getData();
            photos.add(info);
            processSelectedFiles(photos);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ChatAttachAlert chatAttachAlert2;
        TextSettingsCell textSettingsCell;
        int i = this.currentActivityType;
        if ((i == 1 || i == 2) && (chatAttachAlert2 = this.chatAttachAlert) != null) {
            if (requestCode == 17 && chatAttachAlert2 != null) {
                chatAttachAlert2.checkCamera(false);
            } else if (requestCode == 21) {
                if (getParentActivity() != null && grantResults != null && grantResults.length != 0 && grantResults[0] != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("PermissionNoAudioVideo", R.string.PermissionNoAudioVideo));
                    builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PassportActivity.this.lambda$onRequestPermissionsResultFragment$69$PassportActivity(dialogInterface, i);
                        }
                    });
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    builder.show();
                }
            } else if (requestCode == 19 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
                processSelectedAttach(0);
            } else if (requestCode == 22 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0 && (textSettingsCell = this.scanDocumentCell) != null) {
                textSettingsCell.callOnClick();
            }
        } else if (this.currentActivityType == 3 && requestCode == 6) {
            startPhoneVerification(false, this.pendingPhone, this.pendingFinishRunnable, this.pendingErrorRunnable, this.pendingDelegate);
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$69$PassportActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void saveSelfArgs(Bundle args) {
        String str = this.currentPicturePath;
        if (str != null) {
            args.putString("path", str);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        this.currentPicturePath = args.getString("path");
    }

    public boolean onBackPressed() {
        int i = this.currentActivityType;
        if (i == 7) {
            this.views[this.currentViewNum].onBackPressed(true);
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a >= slideViewArr.length) {
                    break;
                }
                if (slideViewArr[a] != null) {
                    slideViewArr[a].onDestroyActivity();
                }
                a++;
            }
        } else if (i == 0 || i == 5) {
            callCallback(false);
        } else if (i == 1 || i == 2) {
            return !checkDiscard();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        if (this.currentActivityType == 3 && Build.VERSION.SDK_INT >= 23 && dialog == this.permissionsDialog && !this.permissionsItems.isEmpty()) {
            getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
        }
    }

    public void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.progressDialog.show();
        }
    }

    public void needHideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
    }

    public void setPage(int page, boolean animated, Bundle params) {
        if (page == 3) {
            this.doneItem.setVisibility(8);
        }
        SlideView[] slideViewArr = this.views;
        final SlideView outView = slideViewArr[this.currentViewNum];
        final SlideView newView = slideViewArr[page];
        this.currentViewNum = page;
        newView.setParams(params, false);
        newView.onShow();
        if (animated) {
            newView.setTranslationX((float) AndroidUtilities.displaySize.x);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.setDuration(300);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(outView, "translationX", new float[]{(float) (-AndroidUtilities.displaySize.x)}), ObjectAnimator.ofFloat(newView, "translationX", new float[]{0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    newView.setVisibility(0);
                }

                public void onAnimationEnd(Animator animation) {
                    outView.setVisibility(8);
                    outView.setX(0.0f);
                }
            });
            animatorSet.start();
            return;
        }
        newView.setTranslationX(0.0f);
        newView.setVisibility(0);
        if (outView != newView) {
            outView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res, boolean animated) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TLRPC.TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            params.putInt("type", 4);
            params.putInt("length", res.type.length);
            setPage(2, animated, params);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            params.putInt("type", 3);
            params.putString("pattern", res.type.pattern);
            setPage(1, animated, params);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeSms) {
            params.putInt("type", 2);
            params.putInt("length", res.type.length);
            setPage(0, animated, params);
        }
    }

    private void openAttachMenu() {
        if (getParentActivity() != null) {
            boolean z = true;
            if (this.uploadingFileType != 0 || this.documents.size() < 20) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
                if (this.uploadingFileType != 1) {
                    z = false;
                }
                chatAttachAlert2.setOpenWithFrontFaceCamera(z);
                this.chatAttachAlert.setMaxSelectedPhotos(getMaxSelectedDocuments(), false);
                this.chatAttachAlert.loadGalleryPhotos();
                if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
                    AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
                }
                this.chatAttachAlert.init();
                showDialog(this.chatAttachAlert);
                return;
            }
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", 20)));
        }
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert2 = new ChatAttachAlert(getParentActivity(), this);
            this.chatAttachAlert = chatAttachAlert2;
            chatAttachAlert2.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() {
                public void didPressedButton(int button, boolean arg, boolean notify, int scheduleDate) {
                    if (PassportActivity.this.getParentActivity() != null && PassportActivity.this.chatAttachAlert != null) {
                        if (button == 8 || button == 7) {
                            if (button != 8) {
                                PassportActivity.this.chatAttachAlert.dismiss();
                            }
                            HashMap<Object, Object> selectedPhotos = PassportActivity.this.chatAttachAlert.getSelectedPhotos();
                            ArrayList<Object> selectedPhotosOrder = PassportActivity.this.chatAttachAlert.getSelectedPhotosOrder();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList<SendMessagesHelper.SendingMediaInfo> photos = new ArrayList<>();
                                for (int a = 0; a < selectedPhotosOrder.size(); a++) {
                                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(a));
                                    SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                                    if (photoEntry.imagePath != null) {
                                        info.path = photoEntry.imagePath;
                                    } else if (photoEntry.path != null) {
                                        info.path = photoEntry.path;
                                    }
                                    photos.add(info);
                                    photoEntry.reset();
                                }
                                PassportActivity.this.processSelectedFiles(photos);
                                return;
                            }
                            return;
                        }
                        if (PassportActivity.this.chatAttachAlert != null) {
                            PassportActivity.this.chatAttachAlert.dismissWithButtonClick(button);
                        }
                        PassportActivity.this.processSelectedAttach(button);
                    }
                }

                public View getRevealView() {
                    return null;
                }

                public void didSelectBot(TLRPC.User user) {
                }

                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(PassportActivity.this.fragmentView.findFocus());
                }

                public void needEnterComment() {
                }
            });
        }
    }

    private int getMaxSelectedDocuments() {
        int i = this.uploadingFileType;
        if (i == 0) {
            return 20 - this.documents.size();
        }
        if (i == 4) {
            return 20 - this.translationDocuments.size();
        }
        return 1;
    }

    /* access modifiers changed from: private */
    public void processSelectedAttach(int which) {
        if (which == 0) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
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
                    startActivityForResult(takePictureIntent, 0);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            }
        } else if (which == 1) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) == 0) {
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(0, false, false, (ChatActivity) null);
                fragment.setCurrentAccount(this.currentAccount);
                fragment.setMaxSelectedPhotos(getMaxSelectedDocuments(), false);
                fragment.setAllowSearchImages(false);
                fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                        PassportActivity.this.processSelectedFiles(photos);
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                            photoPickerIntent.setType("image/*");
                            PassportActivity.this.startActivityForResult(photoPickerIntent, 1);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                presentFragment(fragment);
                return;
            }
            getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 4);
        } else if (which != 4) {
        } else {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) == 0) {
                DocumentSelectActivity fragment2 = new DocumentSelectActivity(false);
                fragment2.setCurrentAccount(this.currentAccount);
                fragment2.setCanSelectOnlyImageFiles(true);
                fragment2.setMaxSelectedFiles(getMaxSelectedDocuments());
                fragment2.setDelegate(new DocumentSelectActivity.DocumentSelectActivityDelegate() {
                    public /* synthetic */ void startMusicSelectActivity(BaseFragment baseFragment) {
                        DocumentSelectActivity.DocumentSelectActivityDelegate.CC.$default$startMusicSelectActivity(this, baseFragment);
                    }

                    public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files, boolean notify, int scheduleDate) {
                        activity.finishFragment();
                        ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList = new ArrayList<>();
                        int count = files.size();
                        for (int a = 0; a < count; a++) {
                            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                            info.path = files.get(a);
                            arrayList.add(info);
                        }
                        PassportActivity.this.processSelectedFiles(arrayList);
                    }

                    public void startDocumentSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                            if (Build.VERSION.SDK_INT >= 18) {
                                photoPickerIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
                            }
                            photoPickerIntent.setType("*/*");
                            PassportActivity.this.startActivityForResult(photoPickerIntent, 21);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                presentFragment(fragment2);
                return;
            }
            getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 4);
        }
    }

    private void fillInitialValues() {
        if (this.initialValues == null) {
            this.initialValues = getCurrentValues();
        }
    }

    private String getCurrentValues() {
        StringBuilder values = new StringBuilder();
        int a = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (a >= editTextBoldCursorArr.length) {
                break;
            }
            values.append(editTextBoldCursorArr[a].getText());
            values.append(",");
            a++;
        }
        if (this.inputExtraFields != null) {
            int a2 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                if (a2 >= editTextBoldCursorArr2.length) {
                    break;
                }
                values.append(editTextBoldCursorArr2[a2].getText());
                values.append(",");
                a2++;
            }
        }
        int count = this.documents.size();
        for (int a3 = 0; a3 < count; a3++) {
            values.append(this.documents.get(a3).secureFile.id);
        }
        SecureDocument secureDocument = this.frontDocument;
        if (secureDocument != null) {
            values.append(secureDocument.secureFile.id);
        }
        SecureDocument secureDocument2 = this.reverseDocument;
        if (secureDocument2 != null) {
            values.append(secureDocument2.secureFile.id);
        }
        SecureDocument secureDocument3 = this.selfieDocument;
        if (secureDocument3 != null) {
            values.append(secureDocument3.secureFile.id);
        }
        int count2 = this.translationDocuments.size();
        for (int a4 = 0; a4 < count2; a4++) {
            values.append(this.translationDocuments.get(a4).secureFile.id);
        }
        return values.toString();
    }

    /* access modifiers changed from: private */
    public boolean isHasNotAnyChanges() {
        String str = this.initialValues;
        return str == null || str.equals(getCurrentValues());
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (isHasNotAnyChanges()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PassportActivity.this.lambda$checkDiscard$70$PassportActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
        builder.setMessage(LocaleController.getString("PassportDiscardChanges", R.string.PassportDiscardChanges));
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$checkDiscard$70$PassportActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void processSelectedFiles(ArrayList<SendMessagesHelper.SendingMediaInfo> photos) {
        boolean allFieldsAreEmpty;
        if (!photos.isEmpty()) {
            int i = this.uploadingFileType;
            if (i == 1 || i == 4) {
                allFieldsAreEmpty = false;
            } else if (this.currentType.type instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                allFieldsAreEmpty = true;
                int a = 0;
                while (true) {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    if (a < editTextBoldCursorArr.length) {
                        if (a != 5 && a != 8 && a != 4 && a != 6 && editTextBoldCursorArr[a].length() > 0) {
                            allFieldsAreEmpty = false;
                            break;
                        }
                        a++;
                    } else {
                        break;
                    }
                }
            } else {
                allFieldsAreEmpty = false;
            }
            Utilities.globalQueue.postRunnable(new Runnable(photos, this.uploadingFileType, allFieldsAreEmpty) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    PassportActivity.this.lambda$processSelectedFiles$73$PassportActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$processSelectedFiles$73$PassportActivity(ArrayList photos, int type, boolean needRecoginze) {
        TLRPC.PhotoSize size;
        int i = type;
        int i2 = this.uploadingFileType;
        int count = Math.min((i2 == 0 || i2 == 4) ? 20 : 1, photos.size());
        boolean didRecognizeSuccessfully = false;
        for (int a = 0; a < count; a++) {
            SendMessagesHelper.SendingMediaInfo info = (SendMessagesHelper.SendingMediaInfo) photos.get(a);
            Bitmap bitmap = ImageLoader.loadBitmap(info.path, info.uri, 2048.0f, 2048.0f, false);
            if (!(bitmap == null || (size = ImageLoader.scaleAndSaveImage(bitmap, 2048.0f, 2048.0f, 89, false, 320, 320)) == null)) {
                TLRPC.TL_secureFile secureFile = new TLRPC.TL_secureFile();
                secureFile.dc_id = (int) size.location.volume_id;
                secureFile.id = (long) size.location.local_id;
                secureFile.date = (int) (System.currentTimeMillis() / 1000);
                SecureDocument document = this.delegate.saveFile(secureFile);
                document.type = i;
                AndroidUtilities.runOnUIThread(new Runnable(document, i) {
                    private final /* synthetic */ SecureDocument f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        PassportActivity.this.lambda$null$71$PassportActivity(this.f$1, this.f$2);
                    }
                });
                if (needRecoginze && !didRecognizeSuccessfully) {
                    try {
                        MrzRecognizer.Result result = MrzRecognizer.recognize(bitmap, this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense);
                        if (result != null) {
                            didRecognizeSuccessfully = true;
                            AndroidUtilities.runOnUIThread(new Runnable(result) {
                                private final /* synthetic */ MrzRecognizer.Result f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    PassportActivity.this.lambda$null$72$PassportActivity(this.f$1);
                                }
                            });
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        }
        ArrayList arrayList = photos;
        SharedConfig.saveConfig();
    }

    public /* synthetic */ void lambda$null$71$PassportActivity(SecureDocument document, int type) {
        int i = this.uploadingFileType;
        if (i == 1) {
            SecureDocument secureDocument = this.selfieDocument;
            if (secureDocument != null) {
                SecureDocumentCell cell = this.documentsCells.remove(secureDocument);
                if (cell != null) {
                    this.selfieLayout.removeView(cell);
                }
                this.selfieDocument = null;
            }
        } else if (i == 4) {
            if (this.translationDocuments.size() >= 20) {
                return;
            }
        } else if (i == 2) {
            SecureDocument secureDocument2 = this.frontDocument;
            if (secureDocument2 != null) {
                SecureDocumentCell cell2 = this.documentsCells.remove(secureDocument2);
                if (cell2 != null) {
                    this.frontLayout.removeView(cell2);
                }
                this.frontDocument = null;
            }
        } else if (i == 3) {
            SecureDocument secureDocument3 = this.reverseDocument;
            if (secureDocument3 != null) {
                SecureDocumentCell cell3 = this.documentsCells.remove(secureDocument3);
                if (cell3 != null) {
                    this.reverseLayout.removeView(cell3);
                }
                this.reverseDocument = null;
            }
        } else if (i == 0 && this.documents.size() >= 20) {
            return;
        }
        this.uploadingDocuments.put(document.path, document);
        this.doneItem.setEnabled(false);
        this.doneItem.setAlpha(0.5f);
        FileLoader.getInstance(this.currentAccount).uploadFile(document.path, false, true, 16777216);
        addDocumentView(document, type);
        updateUploadText(type);
    }

    public /* synthetic */ void lambda$null$72$PassportActivity(MrzRecognizer.Result result) {
        if (result.type == 2) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeIdentityCard)) {
                int a1 = 0;
                int count1 = this.availableDocumentTypes.size();
                while (true) {
                    if (a1 >= count1) {
                        break;
                    }
                    TLRPC.TL_secureRequiredType requiredType = this.availableDocumentTypes.get(a1);
                    if (requiredType.type instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                        this.currentDocumentsType = requiredType;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                    a1++;
                }
            }
        } else if (result.type == 1) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypePassport)) {
                int a12 = 0;
                int count12 = this.availableDocumentTypes.size();
                while (true) {
                    if (a12 >= count12) {
                        break;
                    }
                    TLRPC.TL_secureRequiredType requiredType2 = this.availableDocumentTypes.get(a12);
                    if (requiredType2.type instanceof TLRPC.TL_secureValueTypePassport) {
                        this.currentDocumentsType = requiredType2;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                    a12++;
                }
            }
        } else if (result.type == 3) {
            if (!(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeInternalPassport)) {
                int a13 = 0;
                int count13 = this.availableDocumentTypes.size();
                while (true) {
                    if (a13 >= count13) {
                        break;
                    }
                    TLRPC.TL_secureRequiredType requiredType3 = this.availableDocumentTypes.get(a13);
                    if (requiredType3.type instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                        this.currentDocumentsType = requiredType3;
                        updateInterfaceStringsForDocumentType();
                        break;
                    }
                    a13++;
                }
            }
        } else if (result.type == 4 && !(this.currentDocumentsType.type instanceof TLRPC.TL_secureValueTypeDriverLicense)) {
            int a14 = 0;
            int count14 = this.availableDocumentTypes.size();
            while (true) {
                if (a14 >= count14) {
                    break;
                }
                TLRPC.TL_secureRequiredType requiredType4 = this.availableDocumentTypes.get(a14);
                if (requiredType4.type instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                    this.currentDocumentsType = requiredType4;
                    updateInterfaceStringsForDocumentType();
                    break;
                }
                a14++;
            }
        }
        if (!TextUtils.isEmpty(result.firstName)) {
            this.inputFields[0].setText(result.firstName);
        }
        if (!TextUtils.isEmpty(result.middleName)) {
            this.inputFields[1].setText(result.middleName);
        }
        if (!TextUtils.isEmpty(result.lastName)) {
            this.inputFields[2].setText(result.lastName);
        }
        if (!TextUtils.isEmpty(result.number)) {
            this.inputFields[7].setText(result.number);
        }
        if (result.gender != 0) {
            int i = result.gender;
            if (i == 1) {
                this.currentGender = "male";
                this.inputFields[4].setText(LocaleController.getString("PassportMale", R.string.PassportMale));
            } else if (i == 2) {
                this.currentGender = "female";
                this.inputFields[4].setText(LocaleController.getString("PassportFemale", R.string.PassportFemale));
            }
        }
        if (!TextUtils.isEmpty(result.nationality)) {
            String str = result.nationality;
            this.currentCitizeship = str;
            String country = this.languageMap.get(str);
            if (country != null) {
                this.inputFields[5].setText(country);
            }
        }
        if (!TextUtils.isEmpty(result.issuingCountry)) {
            String str2 = result.issuingCountry;
            this.currentResidence = str2;
            String country2 = this.languageMap.get(str2);
            if (country2 != null) {
                this.inputFields[6].setText(country2);
            }
        }
        if (result.birthDay > 0 && result.birthMonth > 0 && result.birthYear > 0) {
            this.inputFields[3].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.birthDay), Integer.valueOf(result.birthMonth), Integer.valueOf(result.birthYear)}));
        }
        if (result.expiryDay <= 0 || result.expiryMonth <= 0 || result.expiryYear <= 0) {
            int[] iArr = this.currentExpireDate;
            iArr[2] = 0;
            iArr[1] = 0;
            iArr[0] = 0;
            this.inputFields[8].setText(LocaleController.getString("PassportNoExpireDate", R.string.PassportNoExpireDate));
            return;
        }
        this.currentExpireDate[0] = result.expiryYear;
        this.currentExpireDate[1] = result.expiryMonth;
        this.currentExpireDate[2] = result.expiryDay;
        this.inputFields[8].setText(String.format(Locale.US, "%02d.%02d.%d", new Object[]{Integer.valueOf(result.expiryDay), Integer.valueOf(result.expiryMonth), Integer.valueOf(result.expiryYear)}));
    }

    public void setNeedActivityResult(boolean needActivityResult2) {
        this.needActivityResult = needActivityResult2;
    }

    private class ProgressView extends View {
        private Paint paint = new Paint();
        private Paint paint2 = new Paint();
        private float progress;

        public ProgressView(Context context) {
            super(context);
            this.paint.setColor(Theme.getColor(Theme.key_login_progressInner));
            this.paint2.setColor(Theme.getColor(Theme.key_login_progressOuter));
        }

        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int start = (int) (((float) getMeasuredWidth()) * this.progress);
            canvas.drawRect(0.0f, 0.0f, (float) start, (float) getMeasuredHeight(), this.paint2);
            canvas.drawRect((float) start, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }
    }

    public class PhoneConfirmationView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private ImageView blackImageView;
        private ImageView blueImageView;
        /* access modifiers changed from: private */
        public EditTextBoldCursor[] codeField;
        private LinearLayout codeFieldContainer;
        /* access modifiers changed from: private */
        public int codeTime = 15000;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        /* access modifiers changed from: private */
        public boolean ignoreOnTextChange;
        /* access modifiers changed from: private */
        public double lastCodeTime;
        /* access modifiers changed from: private */
        public double lastCurrentTime;
        /* access modifiers changed from: private */
        public String lastError = "";
        /* access modifiers changed from: private */
        public int length;
        private boolean nextPressed;
        /* access modifiers changed from: private */
        public int nextType;
        private int openTime;
        private String pattern = "*";
        /* access modifiers changed from: private */
        public String phone;
        /* access modifiers changed from: private */
        public String phoneHash;
        /* access modifiers changed from: private */
        public TextView problemText;
        /* access modifiers changed from: private */
        public ProgressView progressView;
        final /* synthetic */ PassportActivity this$0;
        /* access modifiers changed from: private */
        public int time = TimeConstants.MIN;
        /* access modifiers changed from: private */
        public TextView timeText;
        /* access modifiers changed from: private */
        public Timer timeTimer;
        /* access modifiers changed from: private */
        public int timeout;
        private final Object timerSync = new Object();
        private TextView titleTextView;
        /* access modifiers changed from: private */
        public int verificationType;
        /* access modifiers changed from: private */
        public boolean waitingForEvent;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public PhoneConfirmationView(im.bclpbkiauv.ui.PassportActivity r31, android.content.Context r32, int r33) {
            /*
                r30 = this;
                r0 = r30
                r1 = r31
                r2 = r32
                r0.this$0 = r1
                r0.<init>(r2)
                java.lang.Object r3 = new java.lang.Object
                r3.<init>()
                r0.timerSync = r3
                r3 = 60000(0xea60, float:8.4078E-41)
                r0.time = r3
                r3 = 15000(0x3a98, float:2.102E-41)
                r0.codeTime = r3
                java.lang.String r3 = ""
                r0.lastError = r3
                java.lang.String r3 = "*"
                r0.pattern = r3
                r3 = r33
                r0.verificationType = r3
                r4 = 1
                r0.setOrientation(r4)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.confirmTextView = r5
                java.lang.String r6 = "windowBackgroundWhiteGrayText6"
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r5.setTextColor(r7)
                android.widget.TextView r5 = r0.confirmTextView
                r7 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r7)
                android.widget.TextView r5 = r0.confirmTextView
                r8 = 1073741824(0x40000000, float:2.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r9 = (float) r9
                r10 = 1065353216(0x3f800000, float:1.0)
                r5.setLineSpacing(r9, r10)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r0.titleTextView = r5
                java.lang.String r9 = "windowBackgroundWhiteBlackText"
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                r5.setTextColor(r11)
                android.widget.TextView r5 = r0.titleTextView
                r11 = 1099956224(0x41900000, float:18.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.titleTextView
                java.lang.String r11 = "fonts/rmedium.ttf"
                android.graphics.Typeface r11 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r11)
                android.widget.TextView r5 = r0.titleTextView
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r13 = 3
                if (r11 == 0) goto L_0x007d
                r11 = 5
                goto L_0x007e
            L_0x007d:
                r11 = 3
            L_0x007e:
                r5.setGravity(r11)
                android.widget.TextView r5 = r0.titleTextView
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r5.setLineSpacing(r11, r10)
                android.widget.TextView r5 = r0.titleTextView
                r11 = 49
                r5.setGravity(r11)
                int r5 = r0.verificationType
                r14 = -2
                if (r5 != r13) goto L_0x012c
                android.widget.TextView r5 = r0.confirmTextView
                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x009f
                r9 = 5
                goto L_0x00a0
            L_0x009f:
                r9 = 3
            L_0x00a0:
                r9 = r9 | 48
                r5.setGravity(r9)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x00b0
                r9 = 5
                goto L_0x00b1
            L_0x00b0:
                r9 = 3
            L_0x00b1:
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r9)
                r0.addView(r5, r9)
                android.widget.ImageView r9 = new android.widget.ImageView
                r9.<init>(r2)
                r15 = 2131231426(0x7f0802c2, float:1.8078933E38)
                r9.setImageResource(r15)
                boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r15 == 0) goto L_0x00fb
                r16 = 1115684864(0x42800000, float:64.0)
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 19
                r19 = 1073741824(0x40000000, float:2.0)
                r20 = 1073741824(0x40000000, float:2.0)
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r9, r15)
                android.widget.TextView r15 = r0.confirmTextView
                r16 = -1082130432(0xffffffffbf800000, float:-1.0)
                r17 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r18 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r18 == 0) goto L_0x00e9
                r18 = 5
                goto L_0x00eb
            L_0x00e9:
                r18 = 3
            L_0x00eb:
                r19 = 1118044160(0x42a40000, float:82.0)
                r20 = 0
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r15, r12)
                goto L_0x012a
            L_0x00fb:
                android.widget.TextView r12 = r0.confirmTextView
                r15 = -1082130432(0xffffffffbf800000, float:-1.0)
                r16 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r17 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r17 == 0) goto L_0x0108
                r17 = 5
                goto L_0x010a
            L_0x0108:
                r17 = 3
            L_0x010a:
                r18 = 0
                r19 = 0
                r20 = 1118044160(0x42a40000, float:82.0)
                r21 = 0
                android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
                r5.addView(r12, r15)
                r16 = 1115684864(0x42800000, float:64.0)
                r17 = 1117257728(0x42980000, float:76.0)
                r18 = 21
                r20 = 1073741824(0x40000000, float:2.0)
                r22 = 1073741824(0x40000000, float:2.0)
                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r5.addView(r9, r12)
            L_0x012a:
                goto L_0x0210
            L_0x012c:
                android.widget.TextView r5 = r0.confirmTextView
                r5.setGravity(r11)
                android.widget.FrameLayout r5 = new android.widget.FrameLayout
                r5.<init>(r2)
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
                int r12 = r0.verificationType
                java.lang.String r15 = "chats_actionBackground"
                if (r12 != r4) goto L_0x01ac
                android.widget.ImageView r12 = new android.widget.ImageView
                r12.<init>(r2)
                r0.blackImageView = r12
                r11 = 2131231607(0x7f080377, float:1.80793E38)
                r12.setImageResource(r11)
                android.widget.ImageView r11 = r0.blackImageView
                android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
                int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
                r12.<init>(r9, r7)
                r11.setColorFilter(r12)
                android.widget.ImageView r7 = r0.blackImageView
                r23 = -1073741824(0xffffffffc0000000, float:-2.0)
                r24 = -1073741824(0xffffffffc0000000, float:-2.0)
                r25 = 51
                r26 = 0
                r27 = 0
                r28 = 0
                r29 = 0
                android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.ImageView r7 = new android.widget.ImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                r9 = 2131231605(0x7f080375, float:1.8079296E38)
                r7.setImageResource(r9)
                android.widget.ImageView r7 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
                r9.<init>(r11, r12)
                r7.setColorFilter(r9)
                android.widget.ImageView r7 = r0.blueImageView
                android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.TextView r7 = r0.titleTextView
                r9 = 2131693850(0x7f0f111a, float:1.901684E38)
                java.lang.String r11 = "SentAppCodeTitle"
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
                r7.setText(r9)
                goto L_0x01ee
            L_0x01ac:
                android.widget.ImageView r7 = new android.widget.ImageView
                r7.<init>(r2)
                r0.blueImageView = r7
                r9 = 2131231606(0x7f080376, float:1.8079298E38)
                r7.setImageResource(r9)
                android.widget.ImageView r7 = r0.blueImageView
                android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
                android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
                r9.<init>(r11, r12)
                r7.setColorFilter(r9)
                android.widget.ImageView r7 = r0.blueImageView
                r23 = -1073741824(0xffffffffc0000000, float:-2.0)
                r24 = -1073741824(0xffffffffc0000000, float:-2.0)
                r25 = 51
                r26 = 0
                r27 = 0
                r28 = 0
                r29 = 0
                android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
                r5.addView(r7, r9)
                android.widget.TextView r7 = r0.titleTextView
                r9 = 2131693854(0x7f0f111e, float:1.9016848E38)
                java.lang.String r11 = "SentSmsCodeTitle"
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
                r7.setText(r9)
            L_0x01ee:
                android.widget.TextView r7 = r0.titleTextView
                r23 = -2
                r24 = -2
                r25 = 49
                r26 = 0
                r27 = 18
                r28 = 0
                r29 = 0
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29)
                r0.addView(r7, r9)
                android.widget.TextView r7 = r0.confirmTextView
                r27 = 17
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r23, (int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29)
                r0.addView(r7, r9)
            L_0x0210:
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r2)
                r0.codeFieldContainer = r5
                r7 = 0
                r5.setOrientation(r7)
                android.widget.LinearLayout r5 = r0.codeFieldContainer
                r9 = 36
                android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r9, (int) r4)
                r0.addView(r5, r9)
                int r5 = r0.verificationType
                if (r5 != r13) goto L_0x0231
                android.widget.LinearLayout r5 = r0.codeFieldContainer
                r9 = 8
                r5.setVisibility(r9)
            L_0x0231:
                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$1 r5 = new im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$1
                r5.<init>(r2, r1)
                r0.timeText = r5
                int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
                r5.setTextColor(r6)
                android.widget.TextView r5 = r0.timeText
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r6 = (float) r6
                r5.setLineSpacing(r6, r10)
                int r5 = r0.verificationType
                r6 = 1097859072(0x41700000, float:15.0)
                r9 = 1092616192(0x41200000, float:10.0)
                if (r5 != r13) goto L_0x0291
                android.widget.TextView r5 = r0.timeText
                r11 = 1096810496(0x41600000, float:14.0)
                r5.setTextSize(r4, r11)
                android.widget.TextView r5 = r0.timeText
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0260
                r11 = 5
                goto L_0x0261
            L_0x0260:
                r11 = 3
            L_0x0261:
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r11)
                im.bclpbkiauv.ui.PassportActivity$ProgressView r5 = new im.bclpbkiauv.ui.PassportActivity$ProgressView
                r5.<init>(r2)
                r0.progressView = r5
                android.widget.TextView r5 = r0.timeText
                boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r11 == 0) goto L_0x0277
                r12 = 5
                goto L_0x0278
            L_0x0277:
                r12 = 3
            L_0x0278:
                r5.setGravity(r12)
                im.bclpbkiauv.ui.PassportActivity$ProgressView r5 = r0.progressView
                r17 = -1
                r18 = 3
                r19 = 0
                r20 = 1094713344(0x41400000, float:12.0)
                r21 = 0
                r22 = 0
                android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r17, (int) r18, (float) r19, (float) r20, (float) r21, (float) r22)
                r0.addView(r5, r11)
                goto L_0x02b3
            L_0x0291:
                android.widget.TextView r5 = r0.timeText
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r11, r7, r12)
                android.widget.TextView r5 = r0.timeText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.timeText
                r11 = 49
                r5.setGravity(r11)
                android.widget.TextView r5 = r0.timeText
                android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r11)
                r0.addView(r5, r12)
            L_0x02b3:
                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$2 r5 = new im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$2
                r5.<init>(r2, r1)
                r0.problemText = r5
                java.lang.String r11 = "windowBackgroundWhiteBlueText4"
                int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
                r5.setTextColor(r11)
                android.widget.TextView r5 = r0.problemText
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r11 = (float) r11
                r5.setLineSpacing(r11, r10)
                android.widget.TextView r5 = r0.problemText
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r5.setPadding(r7, r8, r7, r9)
                android.widget.TextView r5 = r0.problemText
                r5.setTextSize(r4, r6)
                android.widget.TextView r5 = r0.problemText
                r6 = 49
                r5.setGravity(r6)
                int r5 = r0.verificationType
                if (r5 != r4) goto L_0x02fa
                android.widget.TextView r4 = r0.problemText
                r5 = 2131690916(0x7f0f05a4, float:1.901089E38)
                java.lang.String r6 = "DidNotGetTheCodeSms"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
                goto L_0x0308
            L_0x02fa:
                android.widget.TextView r4 = r0.problemText
                r5 = 2131690915(0x7f0f05a3, float:1.9010887E38)
                java.lang.String r6 = "DidNotGetTheCode"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r4.setText(r5)
            L_0x0308:
                android.widget.TextView r4 = r0.problemText
                r5 = 49
                android.widget.LinearLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r14, (int) r5)
                r0.addView(r4, r5)
                android.widget.TextView r4 = r0.problemText
                im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$LHnr5fvECII3ZEUwzZ4YV_F6M18 r5 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$LHnr5fvECII3ZEUwzZ4YV_F6M18
                r5.<init>()
                r4.setOnClickListener(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.<init>(im.bclpbkiauv.ui.PassportActivity, android.content.Context, int):void");
        }

        public /* synthetic */ void lambda$new$0$PassportActivity$PhoneConfirmationView(View v) {
            if (!this.nextPressed) {
                if (!((this.nextType == 4 && this.verificationType == 2) || this.nextType == 0)) {
                    resendCode();
                    return;
                }
                try {
                    PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    String version = String.format(Locale.US, "%s (%d)", new Object[]{pInfo.versionName, Integer.valueOf(pInfo.versionCode)});
                    Intent mailer = new Intent("android.intent.action.SEND");
                    mailer.setType("message/rfc822");
                    mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@stel.com"});
                    mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.phone);
                    mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.phone + "\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                    getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
                } catch (Exception e) {
                    AlertsCreator.showSimpleAlert(this.this$0, LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            ImageView imageView;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.verificationType != 3 && (imageView = this.blueImageView) != null) {
                int innerHeight = imageView.getMeasuredHeight() + this.titleTextView.getMeasuredHeight() + this.confirmTextView.getMeasuredHeight() + AndroidUtilities.dp(35.0f);
                int requiredHeight = AndroidUtilities.dp(80.0f);
                int maxHeight = AndroidUtilities.dp(291.0f);
                if (this.this$0.scrollHeight - innerHeight < requiredHeight) {
                    setMeasuredDimension(getMeasuredWidth(), innerHeight + requiredHeight);
                } else if (this.this$0.scrollHeight > maxHeight) {
                    setMeasuredDimension(getMeasuredWidth(), maxHeight);
                } else {
                    setMeasuredDimension(getMeasuredWidth(), this.this$0.scrollHeight);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int t2;
            super.onLayout(changed, l, t, r, b);
            if (this.verificationType != 3 && this.blueImageView != null) {
                int bottom = this.confirmTextView.getBottom();
                int height = getMeasuredHeight() - bottom;
                if (this.problemText.getVisibility() == 0) {
                    int h = this.problemText.getMeasuredHeight();
                    t2 = (bottom + height) - h;
                    TextView textView = this.problemText;
                    textView.layout(textView.getLeft(), t2, this.problemText.getRight(), t2 + h);
                } else if (this.timeText.getVisibility() == 0) {
                    int h2 = this.timeText.getMeasuredHeight();
                    t2 = (bottom + height) - h2;
                    TextView textView2 = this.timeText;
                    textView2.layout(textView2.getLeft(), t2, this.timeText.getRight(), t2 + h2);
                } else {
                    t2 = bottom + height;
                }
                int h3 = this.codeFieldContainer.getMeasuredHeight();
                int t3 = (((t2 - bottom) - h3) / 2) + bottom;
                LinearLayout linearLayout = this.codeFieldContainer;
                linearLayout.layout(linearLayout.getLeft(), t3, this.codeFieldContainer.getRight(), t3 + h3);
                int height2 = t3;
            }
        }

        /* access modifiers changed from: private */
        public void resendCode() {
            Bundle params = new Bundle();
            params.putString("phone", this.phone);
            this.nextPressed = true;
            this.this$0.needShowProgress();
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.phone;
            req.phone_code_hash = this.phoneHash;
            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(params, req) {
                private final /* synthetic */ Bundle f$1;
                private final /* synthetic */ TLRPC.TL_auth_resendCode f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PassportActivity.PhoneConfirmationView.this.lambda$resendCode$3$PassportActivity$PhoneConfirmationView(this.f$1, this.f$2, tLObject, tL_error);
                }
            }, 2);
        }

        public /* synthetic */ void lambda$resendCode$3$PassportActivity$PhoneConfirmationView(Bundle params, TLRPC.TL_auth_resendCode req, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, params, response, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ Bundle f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ TLRPC.TL_auth_resendCode f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    PassportActivity.PhoneConfirmationView.this.lambda$null$2$PassportActivity$PhoneConfirmationView(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$PassportActivity$PhoneConfirmationView(TLRPC.TL_error error, Bundle params, TLObject response, TLRPC.TL_auth_resendCode req) {
            this.nextPressed = false;
            if (error == null) {
                this.this$0.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response, true);
            } else {
                AlertDialog dialog = (AlertDialog) AlertsCreator.processError(this.this$0.currentAccount, error, this.this$0, req, new Object[0]);
                if (dialog != null && error.text.contains("PHONE_CODE_EXPIRED")) {
                    dialog.setPositiveButtonListener(new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PassportActivity.PhoneConfirmationView.this.lambda$null$1$PassportActivity$PhoneConfirmationView(dialogInterface, i);
                        }
                    });
                }
            }
            this.this$0.needHideProgress();
        }

        public /* synthetic */ void lambda$null$1$PassportActivity$PhoneConfirmationView(DialogInterface dialog1, int which) {
            onBackPressed(true);
            this.this$0.finishFragment();
        }

        public boolean needBackButton() {
            return true;
        }

        public void onCancelPressed() {
            this.nextPressed = false;
        }

        public void setParams(Bundle params, boolean restore) {
            int i;
            int i2;
            Bundle bundle = params;
            if (bundle != null) {
                this.waitingForEvent = true;
                int i3 = this.verificationType;
                if (i3 == 2) {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i3 == 3) {
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                }
                this.currentParams = bundle;
                this.phone = bundle.getString("phone");
                this.phoneHash = bundle.getString("phoneHash");
                int i4 = bundle.getInt("timeout");
                this.time = i4;
                this.timeout = i4;
                this.openTime = (int) (System.currentTimeMillis() / 1000);
                this.nextType = bundle.getInt("nextType");
                this.pattern = bundle.getString("pattern");
                int i5 = bundle.getInt("length");
                this.length = i5;
                if (i5 == 0) {
                    this.length = 5;
                }
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                int i6 = 8;
                int i7 = 0;
                if (editTextBoldCursorArr != null && editTextBoldCursorArr.length == this.length) {
                    int a = 0;
                    while (true) {
                        EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                        if (a >= editTextBoldCursorArr2.length) {
                            break;
                        }
                        editTextBoldCursorArr2[a].setText("");
                        a++;
                    }
                } else {
                    this.codeField = new EditTextBoldCursor[this.length];
                    int a2 = 0;
                    while (a2 < this.length) {
                        final int num = a2;
                        this.codeField[a2] = new EditTextBoldCursor(getContext());
                        this.codeField[a2].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        this.codeField[a2].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        this.codeField[a2].setCursorSize(AndroidUtilities.dp(20.0f));
                        this.codeField[a2].setCursorWidth(1.5f);
                        Drawable pressedDrawable = getResources().getDrawable(R.drawable.search_dark_activated).mutate();
                        pressedDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), PorterDuff.Mode.MULTIPLY));
                        this.codeField[a2].setBackgroundDrawable(pressedDrawable);
                        this.codeField[a2].setImeOptions(268435461);
                        this.codeField[a2].setTextSize(1, 20.0f);
                        this.codeField[a2].setMaxLines(1);
                        this.codeField[a2].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        this.codeField[a2].setPadding(0, 0, 0, 0);
                        this.codeField[a2].setGravity(49);
                        if (this.verificationType == 3) {
                            this.codeField[a2].setEnabled(false);
                            this.codeField[a2].setInputType(0);
                            this.codeField[a2].setVisibility(i6);
                        } else {
                            this.codeField[a2].setInputType(3);
                        }
                        this.codeFieldContainer.addView(this.codeField[a2], LayoutHelper.createLinear(34, 36, 1, 0, 0, a2 != this.length - 1 ? 7 : 0, 0));
                        this.codeField[a2].addTextChangedListener(new TextWatcher() {
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void afterTextChanged(Editable s) {
                                int len;
                                if (!PhoneConfirmationView.this.ignoreOnTextChange && (len = s.length()) >= 1) {
                                    if (len > 1) {
                                        String text = s.toString();
                                        boolean unused = PhoneConfirmationView.this.ignoreOnTextChange = true;
                                        for (int a = 0; a < Math.min(PhoneConfirmationView.this.length - num, len); a++) {
                                            if (a == 0) {
                                                s.replace(0, len, text.substring(a, a + 1));
                                            } else {
                                                PhoneConfirmationView.this.codeField[num + a].setText(text.substring(a, a + 1));
                                            }
                                        }
                                        boolean unused2 = PhoneConfirmationView.this.ignoreOnTextChange = false;
                                    }
                                    if (num != PhoneConfirmationView.this.length - 1) {
                                        PhoneConfirmationView.this.codeField[num + 1].setSelection(PhoneConfirmationView.this.codeField[num + 1].length());
                                        PhoneConfirmationView.this.codeField[num + 1].requestFocus();
                                    }
                                    if ((num == PhoneConfirmationView.this.length - 1 || (num == PhoneConfirmationView.this.length - 2 && len >= 2)) && PhoneConfirmationView.this.getCode().length() == PhoneConfirmationView.this.length) {
                                        PhoneConfirmationView.this.onNextPressed();
                                    }
                                }
                            }
                        });
                        this.codeField[a2].setOnKeyListener(new View.OnKeyListener(num) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                                return PassportActivity.PhoneConfirmationView.this.lambda$setParams$4$PassportActivity$PhoneConfirmationView(this.f$1, view, i, keyEvent);
                            }
                        });
                        this.codeField[a2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                                return PassportActivity.PhoneConfirmationView.this.lambda$setParams$5$PassportActivity$PhoneConfirmationView(textView, i, keyEvent);
                            }
                        });
                        a2++;
                        i6 = 8;
                    }
                }
                ProgressView progressView2 = this.progressView;
                if (progressView2 != null) {
                    progressView2.setVisibility(this.nextType != 0 ? 0 : 8);
                }
                if (this.phone != null) {
                    PhoneFormat instance = PhoneFormat.getInstance();
                    String number = instance.format(Marker.ANY_NON_NULL_MARKER + this.phone);
                    CharSequence str = "";
                    int i8 = this.verificationType;
                    if (i8 == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(number)));
                    } else if (i8 == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(number)));
                    } else if (i8 == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, LocaleController.addNbsp(number)));
                    }
                    this.confirmTextView.setText(str);
                    if (this.verificationType != 3) {
                        AndroidUtilities.showKeyboard(this.codeField[0]);
                        this.codeField[0].requestFocus();
                    } else {
                        AndroidUtilities.hideKeyboard(this.codeField[0]);
                    }
                    destroyTimer();
                    destroyCodeTimer();
                    this.lastCurrentTime = (double) System.currentTimeMillis();
                    if (this.verificationType == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                        this.problemText.setVisibility(8);
                        this.timeText.setVisibility(0);
                        int i9 = this.nextType;
                        if (i9 == 4) {
                            this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 1, 0));
                        } else if (i9 == 2) {
                            this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 1, 0));
                        }
                        createTimer();
                    } else if (this.verificationType == 2 && ((i = this.nextType) == 4 || i == 3)) {
                        this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView = this.timeText;
                        if (this.time < 1000) {
                            i7 = 8;
                        }
                        textView.setVisibility(i7);
                        createTimer();
                    } else if (this.verificationType == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, 2, 0));
                        this.problemText.setVisibility(this.time < 1000 ? 0 : 8);
                        TextView textView2 = this.timeText;
                        if (this.time < 1000) {
                            i7 = 8;
                        }
                        textView2.setVisibility(i7);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        this.problemText.setVisibility(8);
                        createCodeTimer();
                    }
                }
            }
        }

        public /* synthetic */ boolean lambda$setParams$4$PassportActivity$PhoneConfirmationView(int num, View v, int keyCode, KeyEvent event) {
            if (keyCode != 67 || this.codeField[num].length() != 0 || num <= 0) {
                return false;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            editTextBoldCursorArr[num - 1].setSelection(editTextBoldCursorArr[num - 1].length());
            this.codeField[num - 1].requestFocus();
            this.codeField[num - 1].dispatchKeyEvent(event);
            return true;
        }

        public /* synthetic */ boolean lambda$setParams$5$PassportActivity$PhoneConfirmationView(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            onNextPressed();
            return true;
        }

        /* access modifiers changed from: private */
        public void createCodeTimer() {
            if (this.codeTimer == null) {
                this.codeTime = 15000;
                this.codeTimer = new Timer();
                this.lastCodeTime = (double) System.currentTimeMillis();
                this.codeTimer.schedule(new TimerTask() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                              (wrap: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo) = 
                              (r1v0 'this' im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$4 A[THIS])
                             call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo.<init>(im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$4):void type: CONSTRUCTOR)
                             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.4.run():void, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo) = 
                              (r1v0 'this' im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$4 A[THIS])
                             call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo.<init>(im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$4):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.4.run():void, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 83 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 89 more
                            */
                        /*
                            this = this;
                            im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo r0 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$4$sX5u6E-hJAPgI4m-C2b4gQDtyxo
                            r0.<init>(r1)
                            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass4.run():void");
                    }

                    public /* synthetic */ void lambda$run$0$PassportActivity$PhoneConfirmationView$4() {
                        double currentTime = (double) System.currentTimeMillis();
                        double unused = PhoneConfirmationView.this.lastCodeTime = currentTime;
                        PhoneConfirmationView phoneConfirmationView = PhoneConfirmationView.this;
                        int unused2 = phoneConfirmationView.codeTime = (int) (((double) phoneConfirmationView.codeTime) - (currentTime - PhoneConfirmationView.this.lastCodeTime));
                        if (PhoneConfirmationView.this.codeTime <= 1000) {
                            PhoneConfirmationView.this.problemText.setVisibility(0);
                            PhoneConfirmationView.this.timeText.setVisibility(8);
                            PhoneConfirmationView.this.destroyCodeTimer();
                        }
                    }
                }, 0, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    if (this.codeTimer != null) {
                        this.codeTimer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        private void createTimer() {
            if (this.timeTimer == null) {
                Timer timer = new Timer();
                this.timeTimer = timer;
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (PhoneConfirmationView.this.timeTimer != null) {
                            double currentTime = (double) System.currentTimeMillis();
                            PhoneConfirmationView phoneConfirmationView = PhoneConfirmationView.this;
                            int unused = phoneConfirmationView.time = (int) (((double) phoneConfirmationView.time) - (currentTime - PhoneConfirmationView.this.lastCurrentTime));
                            double unused2 = PhoneConfirmationView.this.lastCurrentTime = currentTime;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (PhoneConfirmationView.this.time >= 1000) {
                                        int minutes = (PhoneConfirmationView.this.time / 1000) / 60;
                                        int seconds = (PhoneConfirmationView.this.time / 1000) - (minutes * 60);
                                        if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 3) {
                                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("CallText", R.string.CallText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                        } else if (PhoneConfirmationView.this.nextType == 2) {
                                            PhoneConfirmationView.this.timeText.setText(LocaleController.formatString("SmsText", R.string.SmsText, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                                        }
                                        if (PhoneConfirmationView.this.progressView != null) {
                                            PhoneConfirmationView.this.progressView.setProgress(1.0f - (((float) PhoneConfirmationView.this.time) / ((float) PhoneConfirmationView.this.timeout)));
                                            return;
                                        }
                                        return;
                                    }
                                    if (PhoneConfirmationView.this.progressView != null) {
                                        PhoneConfirmationView.this.progressView.setProgress(1.0f);
                                    }
                                    PhoneConfirmationView.this.destroyTimer();
                                    if (PhoneConfirmationView.this.verificationType == 3) {
                                        AndroidUtilities.setWaitingForCall(false);
                                        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                                        boolean unused = PhoneConfirmationView.this.waitingForEvent = false;
                                        PhoneConfirmationView.this.destroyCodeTimer();
                                        PhoneConfirmationView.this.resendCode();
                                    } else if (PhoneConfirmationView.this.verificationType != 2 && PhoneConfirmationView.this.verificationType != 4) {
                                    } else {
                                        if (PhoneConfirmationView.this.nextType == 4 || PhoneConfirmationView.this.nextType == 2) {
                                            if (PhoneConfirmationView.this.nextType == 4) {
                                                PhoneConfirmationView.this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                                            } else {
                                                PhoneConfirmationView.this.timeText.setText(LocaleController.getString("SendingSms", R.string.SendingSms));
                                            }
                                            PhoneConfirmationView.this.createCodeTimer();
                                            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
                                            req.phone_number = PhoneConfirmationView.this.phone;
                                            req.phone_code_hash = PhoneConfirmationView.this.phoneHash;
                                            ConnectionsManager.getInstance(PhoneConfirmationView.this.this$0.currentAccount).sendRequest(req, 
                                            /*  JADX ERROR: Method code generation error
                                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x01be: INVOKE  
                                                  (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x01b5: INVOKE  (r1v11 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                                  (wrap: int : 0x01b1: INVOKE  (r1v10 int) = 
                                                  (wrap: im.bclpbkiauv.ui.PassportActivity : 0x01af: IGET  (r1v9 im.bclpbkiauv.ui.PassportActivity) = 
                                                  (wrap: im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView : 0x01ad: IGET  (r1v8 im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView) = 
                                                  (wrap: im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 : 0x01ab: IGET  (r1v7 im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5) = 
                                                  (r9v0 'this' im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1 A[THIS])
                                                 im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.5.1.this$2 im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5)
                                                 im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.5.this$1 im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView)
                                                 im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this$0 im.bclpbkiauv.ui.PassportActivity)
                                                 im.bclpbkiauv.ui.PassportActivity.access$11700(im.bclpbkiauv.ui.PassportActivity):int type: STATIC)
                                                 im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                                  (r0v22 'req' im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode)
                                                  (wrap: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q : 0x01bb: CONSTRUCTOR  (r2v1 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q) = 
                                                  (r9v0 'this' im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1 A[THIS])
                                                 call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q.<init>(im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1):void type: CONSTRUCTOR)
                                                  (2 int)
                                                 im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate, int):int type: VIRTUAL in method: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.5.1.run():void, dex: classes2.dex
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:156)
                                                	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x01bb: CONSTRUCTOR  (r2v1 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q) = 
                                                  (r9v0 'this' im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1 A[THIS])
                                                 call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q.<init>(im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.5.1.run():void, dex: classes2.dex
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                	... 138 more
                                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q, state: NOT_LOADED
                                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                	... 144 more
                                                */
                                            /*
                                                this = this;
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.time
                                                r1 = 1065353216(0x3f800000, float:1.0)
                                                r2 = 3
                                                r3 = 1000(0x3e8, float:1.401E-42)
                                                r4 = 4
                                                r5 = 0
                                                r6 = 2
                                                if (r0 < r3) goto L_0x00b9
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.time
                                                int r0 = r0 / r3
                                                int r0 = r0 / 60
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r7 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r7 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r7 = r7.time
                                                int r7 = r7 / r3
                                                int r3 = r0 * 60
                                                int r7 = r7 - r3
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r3 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r3 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r3 = r3.nextType
                                                r8 = 1
                                                if (r3 == r4) goto L_0x006c
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r3 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r3 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r3 = r3.nextType
                                                if (r3 != r2) goto L_0x003f
                                                goto L_0x006c
                                            L_0x003f:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r2 = r2.nextType
                                                if (r2 != r6) goto L_0x008e
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                android.widget.TextView r2 = r2.timeText
                                                r3 = 2131694001(0x7f0f11b1, float:1.9017146E38)
                                                java.lang.Object[] r4 = new java.lang.Object[r6]
                                                java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
                                                r4[r5] = r6
                                                java.lang.Integer r5 = java.lang.Integer.valueOf(r7)
                                                r4[r8] = r5
                                                java.lang.String r5 = "SmsText"
                                                java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r4)
                                                r2.setText(r3)
                                                goto L_0x008e
                                            L_0x006c:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                android.widget.TextView r2 = r2.timeText
                                                r3 = 2131690300(0x7f0f033c, float:1.900964E38)
                                                java.lang.Object[] r4 = new java.lang.Object[r6]
                                                java.lang.Integer r6 = java.lang.Integer.valueOf(r0)
                                                r4[r5] = r6
                                                java.lang.Integer r5 = java.lang.Integer.valueOf(r7)
                                                r4[r8] = r5
                                                java.lang.String r5 = "CallText"
                                                java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r4)
                                                r2.setText(r3)
                                            L_0x008e:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                im.bclpbkiauv.ui.PassportActivity$ProgressView r2 = r2.progressView
                                                if (r2 == 0) goto L_0x00b7
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r2 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                im.bclpbkiauv.ui.PassportActivity$ProgressView r2 = r2.progressView
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r3 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r3 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r3 = r3.time
                                                float r3 = (float) r3
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r4 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r4 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r4 = r4.timeout
                                                float r4 = (float) r4
                                                float r3 = r3 / r4
                                                float r1 = r1 - r3
                                                r2.setProgress(r1)
                                            L_0x00b7:
                                                goto L_0x01c2
                                            L_0x00b9:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                im.bclpbkiauv.ui.PassportActivity$ProgressView r0 = r0.progressView
                                                if (r0 == 0) goto L_0x00ce
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                im.bclpbkiauv.ui.PassportActivity$ProgressView r0 = r0.progressView
                                                r0.setProgress(r1)
                                            L_0x00ce:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                r0.destroyTimer()
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.verificationType
                                                if (r0 != r2) goto L_0x0102
                                                im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForCall(r5)
                                                im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                                int r1 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveCall
                                                r0.removeObserver(r9, r1)
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                boolean unused = r0.waitingForEvent = r5
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                r0.destroyCodeTimer()
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                r0.resendCode()
                                                goto L_0x01c2
                                            L_0x0102:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.verificationType
                                                if (r0 == r6) goto L_0x0116
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.verificationType
                                                if (r0 != r4) goto L_0x01c2
                                            L_0x0116:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.nextType
                                                if (r0 == r4) goto L_0x0158
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.nextType
                                                if (r0 != r6) goto L_0x012b
                                                goto L_0x0158
                                            L_0x012b:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.nextType
                                                if (r0 != r2) goto L_0x0157
                                                im.bclpbkiauv.messenger.AndroidUtilities.setWaitingForSms(r5)
                                                im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
                                                int r1 = im.bclpbkiauv.messenger.NotificationCenter.didReceiveSmsCode
                                                r0.removeObserver(r9, r1)
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                boolean unused = r0.waitingForEvent = r5
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                r0.destroyCodeTimer()
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                r0.resendCode()
                                                goto L_0x01c2
                                            L_0x0157:
                                                goto L_0x01c2
                                            L_0x0158:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                int r0 = r0.nextType
                                                if (r0 != r4) goto L_0x0177
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                android.widget.TextView r0 = r0.timeText
                                                r1 = 2131690302(0x7f0f033e, float:1.9009644E38)
                                                java.lang.String r2 = "Calling"
                                                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                                                r0.setText(r1)
                                                goto L_0x018b
                                            L_0x0177:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                android.widget.TextView r0 = r0.timeText
                                                r1 = 2131693842(0x7f0f1112, float:1.9016824E38)
                                                java.lang.String r2 = "SendingSms"
                                                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                                                r0.setText(r1)
                                            L_0x018b:
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r0 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                r0.createCodeTimer()
                                                im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_auth_resendCode
                                                r0.<init>()
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r1 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r1 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                java.lang.String r1 = r1.phone
                                                r0.phone_number = r1
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r1 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r1 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                java.lang.String r1 = r1.phoneHash
                                                r0.phone_code_hash = r1
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5 r1 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.this
                                                im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView r1 = im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.this
                                                im.bclpbkiauv.ui.PassportActivity r1 = r1.this$0
                                                int r1 = r1.currentAccount
                                                im.bclpbkiauv.tgnet.ConnectionsManager r1 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r1)
                                                im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q r2 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$TtPspW0HMUfsSv4V7r0RapImX3Q
                                                r2.<init>(r9)
                                                r1.sendRequest(r0, r2, r6)
                                                goto L_0x0157
                                            L_0x01c2:
                                                return
                                            */
                                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.AnonymousClass1.run():void");
                                        }

                                        public /* synthetic */ void lambda$run$1$PassportActivity$PhoneConfirmationView$5$1(TLObject response, TLRPC.TL_error error) {
                                            if (error != null && error.text != null) {
                                                AndroidUtilities.runOnUIThread(
                                                /*  JADX ERROR: Method code generation error
                                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                                                      (wrap: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ : 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ) = 
                                                      (r1v0 'this' im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1 A[THIS])
                                                      (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                                     call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ.<init>(im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR)
                                                     im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.5.1.lambda$run$1$PassportActivity$PhoneConfirmationView$5$1(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0008: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ) = 
                                                      (r1v0 'this' im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1 A[THIS])
                                                      (r3v0 'error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
                                                     call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ.<init>(im.bclpbkiauv.ui.PassportActivity$PhoneConfirmationView$5$1, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.5.1.lambda$run$1$PassportActivity$PhoneConfirmationView$5$1(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                    	... 129 more
                                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ, state: NOT_LOADED
                                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                    	... 135 more
                                                    */
                                                /*
                                                    this = this;
                                                    if (r3 == 0) goto L_0x000e
                                                    java.lang.String r0 = r3.text
                                                    if (r0 == 0) goto L_0x000e
                                                    im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ r0 = new im.bclpbkiauv.ui.-$$Lambda$PassportActivity$PhoneConfirmationView$5$1$AN0bsILrJg6VRXwJPDS6U19zQkQ
                                                    r0.<init>(r1, r3)
                                                    im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                                L_0x000e:
                                                    return
                                                */
                                                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PassportActivity.PhoneConfirmationView.AnonymousClass5.AnonymousClass1.lambda$run$1$PassportActivity$PhoneConfirmationView$5$1(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
                                            }

                                            public /* synthetic */ void lambda$null$0$PassportActivity$PhoneConfirmationView$5$1(TLRPC.TL_error error) {
                                                String unused = PhoneConfirmationView.this.lastError = error.text;
                                            }
                                        });
                                    }
                                }
                            }, 0, 1000);
                        }
                    }

                    /* access modifiers changed from: private */
                    public void destroyTimer() {
                        try {
                            synchronized (this.timerSync) {
                                if (this.timeTimer != null) {
                                    this.timeTimer.cancel();
                                    this.timeTimer = null;
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }

                    /* access modifiers changed from: private */
                    public String getCode() {
                        if (this.codeField == null) {
                            return "";
                        }
                        StringBuilder codeBuilder = new StringBuilder();
                        int a = 0;
                        while (true) {
                            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                            if (a >= editTextBoldCursorArr.length) {
                                return codeBuilder.toString();
                            }
                            codeBuilder.append(PhoneFormat.stripExceptNumbers(editTextBoldCursorArr[a].getText().toString()));
                            a++;
                        }
                    }

                    public void onNextPressed() {
                        if (!this.nextPressed) {
                            String code = getCode();
                            if (TextUtils.isEmpty(code)) {
                                AndroidUtilities.shakeView(this.codeFieldContainer, 2.0f, 0);
                                return;
                            }
                            this.nextPressed = true;
                            int i = this.verificationType;
                            if (i == 2) {
                                AndroidUtilities.setWaitingForSms(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                            } else if (i == 3) {
                                AndroidUtilities.setWaitingForCall(false);
                                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                            }
                            this.waitingForEvent = false;
                            this.this$0.showEditDoneProgress(true, true);
                            TLRPC.TL_account_verifyPhone req = new TLRPC.TL_account_verifyPhone();
                            req.phone_number = this.phone;
                            req.phone_code = code;
                            req.phone_code_hash = this.phoneHash;
                            destroyTimer();
                            this.this$0.needShowProgress();
                            ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, new RequestDelegate(req) {
                                private final /* synthetic */ TLRPC.TL_account_verifyPhone f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    PassportActivity.PhoneConfirmationView.this.lambda$onNextPressed$7$PassportActivity$PhoneConfirmationView(this.f$1, tLObject, tL_error);
                                }
                            }, 2);
                        }
                    }

                    public /* synthetic */ void lambda$onNextPressed$7$PassportActivity$PhoneConfirmationView(TLRPC.TL_account_verifyPhone req, TLObject response, TLRPC.TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable(error, req) {
                            private final /* synthetic */ TLRPC.TL_error f$1;
                            private final /* synthetic */ TLRPC.TL_account_verifyPhone f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                PassportActivity.PhoneConfirmationView.this.lambda$null$6$PassportActivity$PhoneConfirmationView(this.f$1, this.f$2);
                            }
                        });
                    }

                    public /* synthetic */ void lambda$null$6$PassportActivity$PhoneConfirmationView(TLRPC.TL_error error, TLRPC.TL_account_verifyPhone req) {
                        int i;
                        int i2;
                        TLRPC.TL_error tL_error = error;
                        this.this$0.needHideProgress();
                        this.nextPressed = false;
                        if (tL_error == null) {
                            destroyTimer();
                            destroyCodeTimer();
                            this.this$0.delegate.saveValue(this.this$0.currentType, (String) this.this$0.currentValues.get("phone"), (String) null, (TLRPC.TL_secureRequiredType) null, (String) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (ArrayList<SecureDocument>) null, (SecureDocument) null, (SecureDocument) null, new Runnable() {
                                public final void run() {
                                    PassportActivity.this.finishFragment();
                                }
                            }, (ErrorRunnable) null);
                            TLRPC.TL_account_verifyPhone tL_account_verifyPhone = req;
                            return;
                        }
                        this.lastError = tL_error.text;
                        if ((this.verificationType == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((this.verificationType == 2 && ((i = this.nextType) == 4 || i == 3)) || (this.verificationType == 4 && this.nextType == 2))) {
                            createTimer();
                        }
                        int i3 = this.verificationType;
                        if (i3 == 2) {
                            AndroidUtilities.setWaitingForSms(true);
                            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                        } else if (i3 == 3) {
                            AndroidUtilities.setWaitingForCall(true);
                            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                        }
                        this.waitingForEvent = true;
                        if (this.verificationType != 3) {
                            AlertsCreator.processError(this.this$0.currentAccount, tL_error, this.this$0, req, new Object[0]);
                        } else {
                            TLRPC.TL_account_verifyPhone tL_account_verifyPhone2 = req;
                        }
                        this.this$0.showEditDoneProgress(true, false);
                        if (tL_error.text.contains("PHONE_CODE_EMPTY") || tL_error.text.contains("PHONE_CODE_INVALID")) {
                            int a = 0;
                            while (true) {
                                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                                if (a < editTextBoldCursorArr.length) {
                                    editTextBoldCursorArr[a].setText("");
                                    a++;
                                } else {
                                    editTextBoldCursorArr[0].requestFocus();
                                    return;
                                }
                            }
                        } else if (tL_error.text.contains("PHONE_CODE_EXPIRED")) {
                            onBackPressed(true);
                            this.this$0.setPage(0, true, (Bundle) null);
                        }
                    }

                    public boolean onBackPressed(boolean force) {
                        if (!force) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.this$0.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("StopVerification", R.string.StopVerification));
                            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), (DialogInterface.OnClickListener) null);
                            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    PassportActivity.PhoneConfirmationView.this.lambda$onBackPressed$8$PassportActivity$PhoneConfirmationView(dialogInterface, i);
                                }
                            });
                            this.this$0.showDialog(builder.create());
                            return false;
                        }
                        TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
                        req.phone_number = this.phone;
                        req.phone_code_hash = this.phoneHash;
                        ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req, $$Lambda$PassportActivity$PhoneConfirmationView$ubqKv5L68FvevcJDUdAhNdU870.INSTANCE, 2);
                        destroyTimer();
                        destroyCodeTimer();
                        this.currentParams = null;
                        int i = this.verificationType;
                        if (i == 2) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                        } else if (i == 3) {
                            AndroidUtilities.setWaitingForCall(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                        }
                        this.waitingForEvent = false;
                        return true;
                    }

                    public /* synthetic */ void lambda$onBackPressed$8$PassportActivity$PhoneConfirmationView(DialogInterface dialogInterface, int i) {
                        onBackPressed(true);
                        this.this$0.setPage(0, true, (Bundle) null);
                    }

                    static /* synthetic */ void lambda$onBackPressed$9(TLObject response, TLRPC.TL_error error) {
                    }

                    public void onDestroyActivity() {
                        super.onDestroyActivity();
                        int i = this.verificationType;
                        if (i == 2) {
                            AndroidUtilities.setWaitingForSms(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                        } else if (i == 3) {
                            AndroidUtilities.setWaitingForCall(false);
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                        }
                        this.waitingForEvent = false;
                        destroyTimer();
                        destroyCodeTimer();
                    }

                    public void onShow() {
                        super.onShow();
                        LinearLayout linearLayout = this.codeFieldContainer;
                        if (linearLayout != null && linearLayout.getVisibility() == 0) {
                            for (int a = this.codeField.length - 1; a >= 0; a--) {
                                if (a == 0 || this.codeField[a].length() != 0) {
                                    this.codeField[a].requestFocus();
                                    EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                                    editTextBoldCursorArr[a].setSelection(editTextBoldCursorArr[a].length());
                                    AndroidUtilities.showKeyboard(this.codeField[a]);
                                    return;
                                }
                            }
                        }
                    }

                    public void didReceivedNotification(int id, int account, Object... args) {
                        if (this.waitingForEvent && this.codeField != null) {
                            if (id == NotificationCenter.didReceiveSmsCode) {
                                this.codeField[0].setText("" + args[0]);
                                onNextPressed();
                            } else if (id == NotificationCenter.didReceiveCall) {
                                String num = "" + args[0];
                                if (AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                                    this.ignoreOnTextChange = true;
                                    this.codeField[0].setText(num);
                                    this.ignoreOnTextChange = false;
                                    onNextPressed();
                                }
                            }
                        }
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
                    arrayList.add(new ThemeDescription(this.extraBackgroundView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                    View view = this.extraBackgroundView2;
                    if (view != null) {
                        arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                    }
                    for (int a = 0; a < this.dividers.size(); a++) {
                        arrayList.add(new ThemeDescription(this.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider));
                    }
                    for (Map.Entry<SecureDocument, SecureDocumentCell> entry : this.documentsCells.entrySet()) {
                        SecureDocumentCell value = entry.getValue();
                        arrayList.add(new ThemeDescription(value, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{SecureDocumentCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                        arrayList.add(new ThemeDescription((View) value, 0, new Class[]{SecureDocumentCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                        arrayList.add(new ThemeDescription((View) value, 0, new Class[]{SecureDocumentCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
                    }
                    arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                    arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                    arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
                    arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                    arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                    arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
                    arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow));
                    arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_SELECTORWHITE, new Class[]{TextDetailSecureCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                    LinearLayout linearLayout = this.linearLayout2;
                    LinearLayout linearLayout3 = linearLayout;
                    arrayList.add(new ThemeDescription((View) linearLayout3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                    arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider));
                    LinearLayout linearLayout4 = this.linearLayout2;
                    LinearLayout linearLayout5 = linearLayout4;
                    arrayList.add(new ThemeDescription((View) linearLayout5, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
                    arrayList.add(new ThemeDescription((View) this.linearLayout2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailSecureCell.class}, new String[]{"checkImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addedIcon));
                    arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                    arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
                    arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow));
                    arrayList.add(new ThemeDescription((View) this.linearLayout2, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
                    if (this.inputFields != null) {
                        int a2 = 0;
                        while (true) {
                            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                            if (a2 >= editTextBoldCursorArr.length) {
                                break;
                            }
                            arrayList.add(new ThemeDescription((View) editTextBoldCursorArr[a2].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                            arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                            arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                            arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
                            arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                            arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                            arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText3));
                            a2++;
                        }
                    } else {
                        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
                        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText3));
                    }
                    if (this.inputExtraFields != null) {
                        int a3 = 0;
                        while (true) {
                            EditTextBoldCursor[] editTextBoldCursorArr2 = this.inputExtraFields;
                            if (a3 >= editTextBoldCursorArr2.length) {
                                break;
                            }
                            arrayList.add(new ThemeDescription((View) editTextBoldCursorArr2[a3].getParent(), ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
                            arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                            arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                            arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
                            arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                            arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                            arrayList.add(new ThemeDescription(this.inputExtraFields[a3], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText3));
                            a3++;
                        }
                    }
                    arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
                    arrayList.add(new ThemeDescription(this.noPasswordImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelIcons));
                    arrayList.add(new ThemeDescription(this.noPasswordTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
                    arrayList.add(new ThemeDescription(this.noPasswordSetTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText5));
                    arrayList.add(new ThemeDescription(this.passwordForgotButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                    arrayList.add(new ThemeDescription(this.plusTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                    arrayList.add(new ThemeDescription(this.acceptTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_passport_authorizeText));
                    arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_passport_authorizeBackground));
                    arrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_passport_authorizeBackgroundSelected));
                    arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressInner2));
                    arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressOuter2));
                    arrayList.add(new ThemeDescription(this.progressViewButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressInner2));
                    arrayList.add(new ThemeDescription(this.progressViewButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressOuter2));
                    arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sessions_devicesImage));
                    arrayList.add(new ThemeDescription(this.emptyTextView1, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
                    arrayList.add(new ThemeDescription(this.emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
                    arrayList.add(new ThemeDescription(this.emptyTextView3, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
                    return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
                }
            }
