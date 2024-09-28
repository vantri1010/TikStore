package im.bclpbkiauv.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PhoneBookSelectActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBoxSquare;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PhonebookShareActivity extends BaseFragment {
    private ListAdapter adapter;
    private BackupImageView avatarImage;
    private FrameLayout bottomLayout;
    private TLRPC.User currentUser;
    private PhoneBookSelectActivity.PhoneBookSelectActivityDelegate delegate;
    /* access modifiers changed from: private */
    public int detailRow;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public int extraHeight;
    private View extraHeightView;
    /* access modifiers changed from: private */
    public boolean isImport;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private TextView nameTextView;
    /* access modifiers changed from: private */
    public ArrayList<AndroidUtilities.VcardItem> other;
    /* access modifiers changed from: private */
    public int overscrollRow;
    private ChatActivity parentFragment;
    /* access modifiers changed from: private */
    public int phoneDividerRow;
    /* access modifiers changed from: private */
    public int phoneEndRow;
    /* access modifiers changed from: private */
    public int phoneStartRow;
    /* access modifiers changed from: private */
    public ArrayList<AndroidUtilities.VcardItem> phones;
    /* access modifiers changed from: private */
    public int rowCount;
    private View shadowView;
    private TextView shareTextView;
    private int user_id;
    /* access modifiers changed from: private */
    public int vcardEndRow;
    /* access modifiers changed from: private */
    public int vcardStartRow;

    public class TextCheckBoxCell extends FrameLayout {
        private CheckBoxSquare checkBox;
        private ImageView imageView;
        private TextView textView;
        final /* synthetic */ PhonebookShareActivity this$0;
        private TextView valueTextView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public TextCheckBoxCell(im.bclpbkiauv.ui.PhonebookShareActivity r21, android.content.Context r22) {
            /*
                r20 = this;
                r0 = r20
                r1 = r22
                r2 = r21
                r0.this$0 = r2
                r0.<init>(r1)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.textView = r3
                java.lang.String r4 = "windowBackgroundWhiteBlackText"
                int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.textView
                r4 = 1098907648(0x41800000, float:16.0)
                r5 = 1
                r3.setTextSize(r5, r4)
                android.widget.TextView r3 = r0.textView
                r6 = 0
                r3.setSingleLine(r6)
                android.widget.TextView r3 = r0.textView
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r8 = 5
                r9 = 3
                if (r7 == 0) goto L_0x0034
                r7 = 5
                goto L_0x0035
            L_0x0034:
                r7 = 3
            L_0x0035:
                r7 = r7 | 48
                r3.setGravity(r7)
                android.widget.TextView r3 = r0.textView
                android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
                r3.setEllipsize(r7)
                android.widget.TextView r3 = r0.textView
                r10 = -1082130432(0xffffffffbf800000, float:-1.0)
                r11 = -1082130432(0xffffffffbf800000, float:-1.0)
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x004d
                r7 = 5
                goto L_0x004e
            L_0x004d:
                r7 = 3
            L_0x004e:
                r12 = r7 | 48
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r17 = 17
                r18 = 64
                r19 = 1116602368(0x428e0000, float:71.0)
                if (r7 == 0) goto L_0x0068
                boolean r7 = r21.isImport
                if (r7 == 0) goto L_0x0063
                r7 = 17
                goto L_0x0065
            L_0x0063:
                r7 = 64
            L_0x0065:
                float r7 = (float) r7
                r13 = r7
                goto L_0x006a
            L_0x0068:
                r13 = 1116602368(0x428e0000, float:71.0)
            L_0x006a:
                r14 = 1092616192(0x41200000, float:10.0)
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r7 == 0) goto L_0x0073
                r15 = 1116602368(0x428e0000, float:71.0)
                goto L_0x0080
            L_0x0073:
                boolean r7 = r21.isImport
                if (r7 == 0) goto L_0x007c
                r7 = 17
                goto L_0x007e
            L_0x007c:
                r7 = 64
            L_0x007e:
                float r7 = (float) r7
                r15 = r7
            L_0x0080:
                r16 = 0
                android.widget.FrameLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r3, r7)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.valueTextView = r3
                java.lang.String r7 = "windowBackgroundWhiteGrayText2"
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                r3.setTextColor(r7)
                android.widget.TextView r3 = r0.valueTextView
                r7 = 1095761920(0x41500000, float:13.0)
                r3.setTextSize(r5, r7)
                android.widget.TextView r3 = r0.valueTextView
                r3.setLines(r5)
                android.widget.TextView r3 = r0.valueTextView
                r3.setMaxLines(r5)
                android.widget.TextView r3 = r0.valueTextView
                r3.setSingleLine(r5)
                android.widget.TextView r3 = r0.valueTextView
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00b8
                r5 = 5
                goto L_0x00b9
            L_0x00b8:
                r5 = 3
            L_0x00b9:
                r3.setGravity(r5)
                android.widget.TextView r3 = r0.valueTextView
                r10 = -1073741824(0xffffffffc0000000, float:-2.0)
                r11 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00c8
                r12 = 5
                goto L_0x00c9
            L_0x00c8:
                r12 = 3
            L_0x00c9:
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00db
                boolean r5 = r21.isImport
                if (r5 == 0) goto L_0x00d6
                r5 = 17
                goto L_0x00d8
            L_0x00d6:
                r5 = 64
            L_0x00d8:
                float r5 = (float) r5
                r13 = r5
                goto L_0x00dd
            L_0x00db:
                r13 = 1116602368(0x428e0000, float:71.0)
            L_0x00dd:
                r14 = 1108082688(0x420c0000, float:35.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00e6
                r15 = 1116602368(0x428e0000, float:71.0)
                goto L_0x00f3
            L_0x00e6:
                boolean r5 = r21.isImport
                if (r5 == 0) goto L_0x00ef
                r5 = 17
                goto L_0x00f1
            L_0x00ef:
                r5 = 64
            L_0x00f1:
                float r5 = (float) r5
                r15 = r5
            L_0x00f3:
                r16 = 0
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r3, r5)
                android.widget.ImageView r3 = new android.widget.ImageView
                r3.<init>(r1)
                r0.imageView = r3
                android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
                r3.setScaleType(r5)
                android.widget.ImageView r3 = r0.imageView
                android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
                java.lang.String r7 = "windowBackgroundWhiteGrayIcon"
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
                android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
                r5.<init>(r7, r10)
                r3.setColorFilter(r5)
                android.widget.ImageView r3 = r0.imageView
                r10 = -1073741824(0xffffffffc0000000, float:-2.0)
                r11 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0127
                r5 = 5
                goto L_0x0128
            L_0x0127:
                r5 = 3
            L_0x0128:
                r12 = r5 | 48
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r7 = 0
                if (r5 == 0) goto L_0x0131
                r13 = 0
                goto L_0x0133
            L_0x0131:
                r13 = 1098907648(0x41800000, float:16.0)
            L_0x0133:
                r14 = 1101004800(0x41a00000, float:20.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x013c
                r15 = 1098907648(0x41800000, float:16.0)
                goto L_0x013d
            L_0x013c:
                r15 = 0
            L_0x013d:
                r16 = 0
                android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r3, r4)
                boolean r3 = r21.isImport
                if (r3 != 0) goto L_0x0180
                im.bclpbkiauv.ui.components.CheckBoxSquare r3 = new im.bclpbkiauv.ui.components.CheckBoxSquare
                r3.<init>(r1, r6)
                r0.checkBox = r3
                r3.setDuplicateParentStateEnabled(r6)
                im.bclpbkiauv.ui.components.CheckBoxSquare r3 = r0.checkBox
                r3.setFocusable(r6)
                im.bclpbkiauv.ui.components.CheckBoxSquare r3 = r0.checkBox
                r3.setFocusableInTouchMode(r6)
                im.bclpbkiauv.ui.components.CheckBoxSquare r3 = r0.checkBox
                r3.setClickable(r6)
                im.bclpbkiauv.ui.components.CheckBoxSquare r3 = r0.checkBox
                r10 = 1099956224(0x41900000, float:18.0)
                r11 = 1099956224(0x41900000, float:18.0)
                boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x0170
                r8 = 3
            L_0x0170:
                r12 = r8 | 16
                r13 = 1100480512(0x41980000, float:19.0)
                r14 = 0
                r15 = 1100480512(0x41980000, float:19.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r3, r4)
            L_0x0180:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PhonebookShareActivity.TextCheckBoxCell.<init>(im.bclpbkiauv.ui.PhonebookShareActivity, android.content.Context):void");
        }

        public void invalidate() {
            super.invalidate();
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                checkBoxSquare.invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = widthMeasureSpec;
            int i2 = heightMeasureSpec;
            measureChildWithMargins(this.textView, i, 0, i2, 0);
            measureChildWithMargins(this.valueTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            measureChildWithMargins(this.imageView, i, 0, i2, 0);
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                measureChildWithMargins(checkBoxSquare, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(64.0f), this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int y = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            TextView textView2 = this.valueTextView;
            textView2.layout(textView2.getLeft(), y, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + y);
        }

        public void setVCardItem(AndroidUtilities.VcardItem item, int icon) {
            if (!TextUtils.isEmpty(item.fullData)) {
                this.textView.setText(item.getValue(true));
                this.valueTextView.setText(item.getType());
            } else {
                this.textView.setText(LocaleController.getString("NumberUnknown", R.string.NumberUnknown));
                this.valueTextView.setText(LocaleController.getString("PhoneMobile", R.string.PhoneMobile));
            }
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                checkBoxSquare.setChecked(item.checked, false);
            }
            if (icon != 0) {
                this.imageView.setImageResource(icon);
            } else {
                this.imageView.setImageDrawable((Drawable) null);
            }
        }

        public void setChecked(boolean checked) {
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                checkBoxSquare.setChecked(checked, true);
            }
        }

        public boolean isChecked() {
            CheckBoxSquare checkBoxSquare = this.checkBox;
            return checkBoxSquare != null && checkBoxSquare.isChecked();
        }
    }

    public PhonebookShareActivity(ContactsController.Contact contact, Uri uri, File file, String name) {
        this(0, contact, uri, file, name);
    }

    public PhonebookShareActivity(int user_id2, ContactsController.Contact contact, Uri uri, File file, String name) {
        this.other = new ArrayList<>();
        this.phones = new ArrayList<>();
        if (user_id2 != 0) {
            this.user_id = user_id2;
        }
        ArrayList<TLRPC.User> result = null;
        ArrayList<AndroidUtilities.VcardItem> items = new ArrayList<>();
        if (uri != null) {
            result = AndroidUtilities.loadVCardFromStream(uri, this.currentAccount, false, items, name);
        } else if (file != null) {
            result = AndroidUtilities.loadVCardFromStream(Uri.fromFile(file), this.currentAccount, false, items, name);
            file.delete();
            this.isImport = true;
        } else if (contact.key != null) {
            result = AndroidUtilities.loadVCardFromStream(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, contact.key), this.currentAccount, true, items, name);
        } else {
            this.currentUser = contact.user;
            AndroidUtilities.VcardItem item = new AndroidUtilities.VcardItem();
            item.type = 0;
            if (!TextUtils.isEmpty(this.currentUser.phone)) {
                ArrayList<String> arrayList = item.vcardData;
                String str = "TEL;MOBILE:+" + this.currentUser.phone;
                item.fullData = str;
                arrayList.add(str);
            } else {
                ArrayList<String> arrayList2 = item.vcardData;
                item.fullData = "";
                arrayList2.add("");
            }
            this.phones.add(item);
        }
        if (result != null) {
            for (int a = 0; a < items.size(); a++) {
                AndroidUtilities.VcardItem item2 = items.get(a);
                if (item2.type == 0) {
                    boolean exists = false;
                    int b = 0;
                    while (true) {
                        if (b >= this.phones.size()) {
                            break;
                        } else if (this.phones.get(b).getValue(false).equals(item2.getValue(false))) {
                            exists = true;
                            break;
                        } else {
                            b++;
                        }
                    }
                    if (exists) {
                        item2.checked = false;
                    } else {
                        this.phones.add(item2);
                    }
                } else {
                    this.other.add(item2);
                }
            }
            if (result != null && !result.isEmpty()) {
                this.currentUser = result.get(0);
                if (contact != null && contact.user != null) {
                    this.currentUser.photo = contact.user.photo;
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (this.currentUser == null) {
            return false;
        }
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.overscrollRow = 0;
        this.rowCount = i + 1;
        this.emptyRow = i;
        if (this.phones.isEmpty()) {
            this.phoneStartRow = -1;
            this.phoneEndRow = -1;
        } else {
            int i2 = this.rowCount;
            this.phoneStartRow = i2;
            int size = i2 + this.phones.size();
            this.rowCount = size;
            this.phoneEndRow = size;
        }
        if (this.other.isEmpty()) {
            this.phoneDividerRow = -1;
            this.vcardStartRow = -1;
            this.vcardEndRow = -1;
        } else {
            if (this.phones.isEmpty()) {
                this.phoneDividerRow = -1;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.phoneDividerRow = i3;
            }
            int i4 = this.rowCount;
            this.vcardStartRow = i4;
            int size2 = i4 + this.other.size();
            this.rowCount = size2;
            this.vcardEndRow = size2;
        }
        int i5 = this.rowCount;
        this.rowCount = i5 + 1;
        this.detailRow = i5;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhonebookShareActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != PhonebookShareActivity.this.listView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (PhonebookShareActivity.this.parentLayout != null) {
                    int actionBarHeight = 0;
                    int childCount = getChildCount();
                    int a = 0;
                    while (true) {
                        if (a >= childCount) {
                            break;
                        }
                        View view = getChildAt(a);
                        if (view == child || !(view instanceof ActionBar) || view.getVisibility() != 0) {
                            a++;
                        } else if (((ActionBar) view).getCastShadows()) {
                            actionBarHeight = view.getMeasuredHeight();
                        }
                    }
                    PhonebookShareActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                }
                return result;
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass3 r4 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r4;
        recyclerListView2.setLayoutManager(r4);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PhonebookShareActivity.this.lambda$createView$1$PhonebookShareActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return PhonebookShareActivity.this.lambda$createView$3$PhonebookShareActivity(view, i);
            }
        });
        frameLayout.addView(this.actionBar);
        View view = new View(context);
        this.extraHeightView = view;
        view.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        View view2 = new View(context);
        this.shadowView = view2;
        view2.setBackgroundResource(R.drawable.header_shadow);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42.0f, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_profile_title));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, 118.0f, 8.0f, 10.0f, 0.0f));
        needLayout();
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (PhonebookShareActivity.this.layoutManager.getItemCount() != 0) {
                    int height = 0;
                    int i = 0;
                    View child = recyclerView.getChildAt(0);
                    if (child != null) {
                        if (PhonebookShareActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            int dp = AndroidUtilities.dp(88.0f);
                            if (child.getTop() < 0) {
                                i = child.getTop();
                            }
                            height = dp + i;
                        }
                        if (PhonebookShareActivity.this.extraHeight != height) {
                            int unused = PhonebookShareActivity.this.extraHeight = height;
                            PhonebookShareActivity.this.needLayout();
                        }
                    }
                }
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.bottomLayout = frameLayout2;
        frameLayout2.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(Theme.key_passport_authorizeBackground), Theme.getColor(Theme.key_passport_authorizeBackgroundSelected)));
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhonebookShareActivity.this.lambda$createView$5$PhonebookShareActivity(view);
            }
        });
        TextView textView2 = new TextView(context);
        this.shareTextView = textView2;
        textView2.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.shareTextView.setTextColor(Theme.getColor(Theme.key_passport_authorizeText));
        if (this.isImport) {
            this.shareTextView.setText(LocaleController.getString("AddContactChat", R.string.AddContactChat));
        } else {
            this.shareTextView.setText(LocaleController.getString("ContactShare", R.string.ContactShare));
        }
        this.shareTextView.setTextSize(1, 14.0f);
        this.shareTextView.setGravity(17);
        this.shareTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomLayout.addView(this.shareTextView, LayoutHelper.createFrame(-2, -1, 17));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(shadow, LayoutHelper.createFrame(-1.0f, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setProfile(true);
        avatarDrawable.setInfo(5, this.currentUser.first_name, this.currentUser.last_name);
        avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        this.avatarImage.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", (Drawable) avatarDrawable, (Object) this.currentUser);
        this.nameTextView.setText(ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PhonebookShareActivity(View view, int position) {
        AndroidUtilities.VcardItem item;
        int i = this.phoneStartRow;
        if (position < i || position >= this.phoneEndRow) {
            int i2 = this.vcardStartRow;
            if (position < i2 || position >= this.vcardEndRow) {
                item = null;
            } else {
                item = this.other.get(position - i2);
            }
        } else {
            item = this.phones.get(position - i);
        }
        if (item != null) {
            if (!this.isImport) {
                item.checked = !item.checked;
                if (position >= this.phoneStartRow && position < this.phoneEndRow) {
                    boolean hasChecked = false;
                    int a = 0;
                    while (true) {
                        if (a >= this.phones.size()) {
                            break;
                        } else if (this.phones.get(a).checked) {
                            hasChecked = true;
                            break;
                        } else {
                            a++;
                        }
                    }
                    this.bottomLayout.setEnabled(hasChecked);
                    this.shareTextView.setAlpha(hasChecked ? 1.0f : 0.5f);
                }
                ((TextCheckBoxCell) view).setChecked(item.checked);
            } else if (item.type == 0) {
                try {
                    Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + item.getValue(false)));
                    intent.addFlags(C.ENCODING_PCM_MU_LAW);
                    getParentActivity().startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (item.type == 1) {
                Browser.openUrl((Context) getParentActivity(), "mailto:" + item.getValue(false));
            } else if (item.type == 3) {
                String url = item.getValue(false);
                if (!url.startsWith("http")) {
                    url = "http://" + url;
                }
                Browser.openUrl((Context) getParentActivity(), url);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PhonebookShareActivity.lambda$null$0(AndroidUtilities.VcardItem.this, dialogInterface, i);
                    }
                });
                showDialog(builder.create());
            }
        }
    }

    static /* synthetic */ void lambda$null$0(AndroidUtilities.VcardItem item, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", item.getValue(false)));
                ToastUtils.show((int) R.string.TextCopied);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ boolean lambda$createView$3$PhonebookShareActivity(View view, int position) {
        AndroidUtilities.VcardItem item;
        int i = this.phoneStartRow;
        if (position < i || position >= this.phoneEndRow) {
            int i2 = this.vcardStartRow;
            if (position < i2 || position >= this.vcardEndRow) {
                item = null;
            } else {
                item = this.other.get(position - i2);
            }
        } else {
            item = this.phones.get(position - i);
        }
        if (item == null) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhonebookShareActivity.lambda$null$2(AndroidUtilities.VcardItem.this, dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return true;
    }

    static /* synthetic */ void lambda$null$2(AndroidUtilities.VcardItem item, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", item.getValue(false)));
                if (item.type == 0) {
                    ToastUtils.show((int) R.string.PhoneCopied);
                } else if (item.type == 1) {
                    ToastUtils.show((int) R.string.EmailCopied);
                } else if (item.type == 3) {
                    ToastUtils.show((int) R.string.LinkCopied);
                } else {
                    ToastUtils.show((int) R.string.TextCopied);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$createView$5$PhonebookShareActivity(View v) {
        StringBuilder builder;
        TLRPC.User user;
        if (!this.isImport) {
            if (!this.currentUser.restriction_reason.isEmpty()) {
                builder = new StringBuilder(this.currentUser.restriction_reason.get(0).text);
            } else {
                builder = new StringBuilder(String.format(Locale.US, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", new Object[]{ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)}));
            }
            int idx = builder.lastIndexOf("END:VCARD");
            if (idx >= 0) {
                this.currentUser.phone = null;
                for (int a = this.phones.size() - 1; a >= 0; a--) {
                    AndroidUtilities.VcardItem item = this.phones.get(a);
                    if (item.checked) {
                        if (this.currentUser.phone == null) {
                            this.currentUser.phone = item.getValue(false);
                        }
                        for (int b = 0; b < item.vcardData.size(); b++) {
                            builder.insert(idx, item.vcardData.get(b) + "\n");
                        }
                    }
                }
                for (int a2 = this.other.size() - 1; a2 >= 0; a2--) {
                    AndroidUtilities.VcardItem item2 = this.other.get(a2);
                    if (item2.checked) {
                        for (int b2 = item2.vcardData.size() - 1; b2 >= 0; b2 += -1) {
                            builder.insert(idx, item2.vcardData.get(b2) + "\n");
                        }
                    }
                }
                TLRPC.TL_restrictionReason reason = new TLRPC.TL_restrictionReason();
                reason.text = builder.toString();
                reason.reason = "";
                reason.platform = "";
                this.currentUser.restriction_reason.add(reason);
            }
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectContact(this.currentUser, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhonebookShareActivity.this.lambda$null$4$PhonebookShareActivity(z, i);
                }
            });
        } else if (getParentActivity() != null && this.user_id != 0 && (user = getMessagesController().getUser(Integer.valueOf(this.user_id))) != null) {
            if (user.self || user.contact) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", user.id);
                presentFragment(new NewProfileActivity(bundle));
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("from_type", 6);
            presentFragment(new AddContactsInfoActivity(bundle2, user));
        }
    }

    public /* synthetic */ void lambda$null$4$PhonebookShareActivity(boolean notify, int scheduleDate) {
        this.delegate.didSelectContact(this.currentUser, notify, scheduleDate);
        finishFragment();
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void setDelegate(PhoneBookSelectActivity.PhoneBookSelectActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void needLayout() {
        int i = 0;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) newTop);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (this.extraHeight + newTop));
            this.avatarImage.setScaleX(((diff * 18.0f) + 42.0f) / 42.0f);
            this.avatarImage.setScaleY(((18.0f * diff) + 42.0f) / 42.0f);
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float avatarY = ((((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (diff + 1.0f))) - (AndroidUtilities.density * 21.0f)) + (AndroidUtilities.density * 27.0f * diff);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationX(AndroidUtilities.density * -21.0f * diff);
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) (AndroidUtilities.density * 7.0f * diff))));
            this.nameTextView.setScaleX((diff * 0.12f) + 1.0f);
            this.nameTextView.setScaleY((0.12f * diff) + 1.0f);
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (PhonebookShareActivity.this.fragmentView == null) {
                        return true;
                    }
                    PhonebookShareActivity.this.needLayout();
                    PhonebookShareActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PhonebookShareActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int icon;
            AndroidUtilities.VcardItem item;
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    TextCheckBoxCell cell = (TextCheckBoxCell) holder.itemView;
                    if (position < PhonebookShareActivity.this.phoneStartRow || position >= PhonebookShareActivity.this.phoneEndRow) {
                        item = (AndroidUtilities.VcardItem) PhonebookShareActivity.this.other.get(position - PhonebookShareActivity.this.vcardStartRow);
                        if (position == PhonebookShareActivity.this.vcardStartRow) {
                            icon = R.drawable.profile_info;
                        } else {
                            icon = 0;
                        }
                    } else {
                        item = (AndroidUtilities.VcardItem) PhonebookShareActivity.this.phones.get(position - PhonebookShareActivity.this.phoneStartRow);
                        if (position == PhonebookShareActivity.this.phoneStartRow) {
                            icon = R.drawable.profile_phone;
                        } else {
                            icon = 0;
                        }
                    }
                    cell.setVCardItem(item, icon);
                }
            } else if (position == PhonebookShareActivity.this.overscrollRow) {
                ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(88.0f));
            } else {
                ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(16.0f));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position >= PhonebookShareActivity.this.phoneStartRow && position < PhonebookShareActivity.this.phoneEndRow) || (position >= PhonebookShareActivity.this.vcardStartRow && position < PhonebookShareActivity.this.vcardEndRow);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new EmptyCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 1) {
                view = new TextCheckBoxCell(PhonebookShareActivity.this, this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 2) {
                view = new DividerCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view.setPadding(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            } else if (viewType == 3) {
                view = new ShadowSectionCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == PhonebookShareActivity.this.emptyRow || position == PhonebookShareActivity.this.overscrollRow) {
                return 0;
            }
            if (position >= PhonebookShareActivity.this.phoneStartRow && position < PhonebookShareActivity.this.phoneEndRow) {
                return 1;
            }
            if (position >= PhonebookShareActivity.this.vcardStartRow && position < PhonebookShareActivity.this.vcardEndRow) {
                return 1;
            }
            if (position != PhonebookShareActivity.this.phoneDividerRow && position == PhonebookShareActivity.this.detailRow) {
                return 3;
            }
            return 2;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.shareTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_passport_authorizeText), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_passport_authorizeBackground), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_passport_authorizeBackgroundSelected), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareUnchecked), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareDisabled), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareBackground), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareCheck)};
    }
}
