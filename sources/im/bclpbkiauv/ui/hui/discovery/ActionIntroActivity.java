package im.bclpbkiauv.ui.hui.discovery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.ui.ChangePhoneNumberActivity;
import im.bclpbkiauv.ui.ChannelCreateActivity;
import im.bclpbkiauv.ui.GroupCreateFinalActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.ShareLocationDrawable;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.ArrayList;
import org.slf4j.Marker;

public class ActionIntroActivity extends BaseFragment implements LocationController.LocationFetchCallback {
    public static final int ACTION_TYPE_CHANGE_PHONE_NUMBER = 3;
    public static final int ACTION_TYPE_CHANNEL_CREATE = 0;
    public static final int ACTION_TYPE_NEARBY_GROUP_CREATE = 2;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ACCESS = 1;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ENABLED = 4;
    /* access modifiers changed from: private */
    public TextView buttonTextView;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private BDLocation currentGroupCreateLocation;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public TextView descriptionText;
    /* access modifiers changed from: private */
    public TextView descriptionText2;
    private Drawable drawable1;
    private Drawable drawable2;
    /* access modifiers changed from: private */
    public ImageView imageView;
    /* access modifiers changed from: private */
    public TextView subtitleTextView;
    /* access modifiers changed from: private */
    public TextView titleTextView;

    public ActionIntroActivity(int type) {
        this.currentType = type;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ActionIntroActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new ViewGroup(context) {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Code restructure failed: missing block: B:9:0x003a, code lost:
                if (r2 != 4) goto L_0x0372;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onMeasure(int r13, int r14) {
                /*
                    r12 = this;
                    int r0 = android.view.View.MeasureSpec.getSize(r13)
                    int r1 = android.view.View.MeasureSpec.getSize(r14)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    im.bclpbkiauv.ui.actionbar.ActionBar r2 = r2.actionBar
                    r3 = 1073741824(0x40000000, float:2.0)
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    r2.measure(r4, r14)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    int r2 = r2.currentType
                    r4 = 1055286886(0x3ee66666, float:0.45)
                    r5 = 1109917696(0x42280000, float:42.0)
                    r6 = -2147483648(0xffffffff80000000, float:-0.0)
                    r7 = 1058642330(0x3f19999a, float:0.6)
                    r8 = 0
                    if (r2 == 0) goto L_0x02c0
                    r9 = 1
                    if (r2 == r9) goto L_0x020c
                    r9 = 2
                    r10 = 1061662228(0x3f47ae14, float:0.78)
                    r11 = 1054951342(0x3ee147ae, float:0.44)
                    if (r2 == r9) goto L_0x0112
                    r9 = 3
                    if (r2 == r9) goto L_0x003e
                    r4 = 4
                    if (r2 == r4) goto L_0x020c
                    goto L_0x0372
                L_0x003e:
                    if (r0 <= r1) goto L_0x00b3
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r2 = r2.imageView
                    float r9 = (float) r0
                    float r9 = r9 * r4
                    int r9 = (int) r9
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r3)
                    float r11 = (float) r1
                    float r11 = r11 * r10
                    int r10 = (int) r11
                    int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r6)
                    r2.measure(r9, r10)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.subtitleTextView
                    float r9 = (float) r0
                    float r9 = r9 * r4
                    int r4 = (int) r9
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r8)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                    goto L_0x0372
                L_0x00b3:
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r2 = r2.imageView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    float r7 = (float) r1
                    float r7 = r7 * r11
                    int r7 = (int) r7
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r6)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.subtitleTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                    goto L_0x0372
                L_0x0112:
                    if (r0 <= r1) goto L_0x019c
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r2 = r2.imageView
                    float r9 = (float) r0
                    float r9 = r9 * r4
                    int r9 = (int) r9
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r3)
                    float r11 = (float) r1
                    float r11 = r11 * r10
                    int r10 = (int) r11
                    int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r6)
                    r2.measure(r9, r10)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.subtitleTextView
                    float r9 = (float) r0
                    float r9 = r9 * r4
                    int r4 = (int) r9
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText2
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r8)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                    goto L_0x0372
                L_0x019c:
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r2 = r2.imageView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    float r7 = (float) r1
                    float r7 = r7 * r11
                    int r7 = (int) r7
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r6)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.subtitleTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText2
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                    goto L_0x0372
                L_0x020c:
                    r2 = 1120403456(0x42c80000, float:100.0)
                    if (r0 <= r1) goto L_0x026e
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r4 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r4 = r4.imageView
                    int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r3)
                    int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r3)
                    r4.measure(r9, r2)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r8)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                    goto L_0x0372
                L_0x026e:
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r4 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r4 = r4.imageView
                    int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r3)
                    int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r3)
                    r4.measure(r7, r2)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                    goto L_0x0372
                L_0x02c0:
                    if (r0 <= r1) goto L_0x0322
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r2 = r2.imageView
                    float r9 = (float) r0
                    float r9 = r9 * r4
                    int r4 = (int) r9
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    float r9 = (float) r1
                    r10 = 1059984507(0x3f2e147b, float:0.68)
                    float r9 = r9 * r10
                    int r9 = (int) r9
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r3)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r9)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r3)
                    int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r8)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    float r4 = (float) r0
                    float r4 = r4 * r7
                    int r4 = (int) r4
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                    goto L_0x0372
                L_0x0322:
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.ImageView r2 = r2.imageView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    float r7 = (float) r1
                    r9 = 1053575610(0x3ecc49ba, float:0.399)
                    float r7 = r7 * r9
                    int r7 = (int) r7
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r3)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.titleTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.descriptionText
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3)
                    int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r8)
                    r2.measure(r4, r7)
                    im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity r2 = im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.this
                    android.widget.TextView r2 = r2.buttonTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
                    int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r3)
                    r2.measure(r4, r3)
                L_0x0372:
                    r12.setMeasuredDimension(r0, r1)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity.AnonymousClass2.onMeasure(int, int):void");
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int i = r;
                int i2 = b;
                ActionIntroActivity.this.actionBar.layout(0, 0, i, ActionIntroActivity.this.actionBar.getMeasuredHeight());
                int width = i - l;
                int height = i2 - t;
                int access$100 = ActionIntroActivity.this.currentType;
                if (access$100 != 0) {
                    if (access$100 != 1) {
                        if (access$100 != 2) {
                            if (access$100 != 3) {
                                if (access$100 != 4) {
                                    return;
                                }
                            } else if (i > i2) {
                                int y = ((int) ((((float) height) * 0.95f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                                ActionIntroActivity.this.imageView.layout(0, y, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y);
                                int y2 = y + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, y2, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y2);
                                int x = (int) (((float) width) * 0.4f);
                                int y3 = (int) (((float) height) * 0.12f);
                                ActionIntroActivity.this.titleTextView.layout(x, y3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y3);
                                int x2 = (int) (((float) width) * 0.4f);
                                int y4 = (int) (((float) height) * 0.24f);
                                ActionIntroActivity.this.descriptionText.layout(x2, y4, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y4);
                                int x3 = (int) ((((float) width) * 0.4f) + (((((float) width) * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                                int y5 = (int) (((float) height) * 0.8f);
                                ActionIntroActivity.this.buttonTextView.layout(x3, y5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x3, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y5);
                                return;
                            } else {
                                int y6 = (int) (((float) height) * 0.2229f);
                                ActionIntroActivity.this.imageView.layout(0, y6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y6);
                                int y7 = (int) (((float) height) * 0.352f);
                                ActionIntroActivity.this.titleTextView.layout(0, y7, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y7);
                                int y8 = (int) (((float) height) * 0.409f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, y8, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y8);
                                int y9 = (int) (((float) height) * 0.468f);
                                ActionIntroActivity.this.descriptionText.layout(0, y9, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y9);
                                int x4 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                int y10 = (int) (((float) height) * 0.805f);
                                ActionIntroActivity.this.buttonTextView.layout(x4, y10, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x4, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y10);
                                return;
                            }
                        } else if (i > i2) {
                            int y11 = ((int) ((((float) height) * 0.9f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                            ActionIntroActivity.this.imageView.layout(0, y11, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y11);
                            int y12 = y11 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, y12, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y12);
                            int x5 = (int) (((float) width) * 0.4f);
                            int y13 = (int) (((float) height) * 0.12f);
                            ActionIntroActivity.this.titleTextView.layout(x5, y13, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x5, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y13);
                            int x6 = (int) (((float) width) * 0.4f);
                            int y14 = (int) (((float) height) * 0.26f);
                            ActionIntroActivity.this.descriptionText.layout(x6, y14, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x6, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y14);
                            int x7 = (int) ((((float) width) * 0.4f) + (((((float) width) * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int y15 = (int) (((float) height) * 0.6f);
                            ActionIntroActivity.this.buttonTextView.layout(x7, y15, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y15);
                            int x8 = (int) (((float) width) * 0.4f);
                            int y16 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(x8, y16, ActionIntroActivity.this.descriptionText2.getMeasuredWidth() + x8, ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + y16);
                            return;
                        } else {
                            int y17 = (int) (((float) height) * 0.197f);
                            ActionIntroActivity.this.imageView.layout(0, y17, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y17);
                            int y18 = (int) (((float) height) * 0.421f);
                            ActionIntroActivity.this.titleTextView.layout(0, y18, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y18);
                            int y19 = (int) (((float) height) * 0.477f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, y19, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + y19);
                            int y20 = (int) (((float) height) * 0.537f);
                            ActionIntroActivity.this.descriptionText.layout(0, y20, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y20);
                            int x9 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            int y21 = (int) (((float) height) * 0.71f);
                            ActionIntroActivity.this.buttonTextView.layout(x9, y21, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x9, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y21);
                            int y22 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(0, y22, ActionIntroActivity.this.descriptionText2.getMeasuredWidth(), ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + y22);
                            return;
                        }
                    }
                    if (i > i2) {
                        int y23 = (height - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                        int x10 = ((int) ((((float) width) * 0.5f) - ((float) ActionIntroActivity.this.imageView.getMeasuredWidth()))) / 2;
                        ActionIntroActivity.this.imageView.layout(x10, y23, ActionIntroActivity.this.imageView.getMeasuredWidth() + x10, ActionIntroActivity.this.imageView.getMeasuredHeight() + y23);
                        int x11 = (int) (((float) width) * 0.4f);
                        int y24 = (int) (((float) height) * 0.14f);
                        ActionIntroActivity.this.titleTextView.layout(x11, y24, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x11, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y24);
                        int x12 = (int) (((float) width) * 0.4f);
                        int y25 = (int) (((float) height) * 0.31f);
                        ActionIntroActivity.this.descriptionText.layout(x12, y25, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x12, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y25);
                        int x13 = (int) ((((float) width) * 0.4f) + (((((float) width) * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                        int y26 = (int) (((float) height) * 0.78f);
                        ActionIntroActivity.this.buttonTextView.layout(x13, y26, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x13, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y26);
                        return;
                    }
                    int y27 = (int) (((float) height) * 0.214f);
                    int x14 = (width - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                    ActionIntroActivity.this.imageView.layout(x14, y27, ActionIntroActivity.this.imageView.getMeasuredWidth() + x14, ActionIntroActivity.this.imageView.getMeasuredHeight() + y27);
                    int y28 = (int) (((float) height) * 0.414f);
                    ActionIntroActivity.this.titleTextView.layout(0, y28, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y28);
                    int y29 = (int) (((float) height) * 0.493f);
                    ActionIntroActivity.this.descriptionText.layout(0, y29, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y29);
                    int x15 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    int y30 = (int) (((float) height) * 0.71f);
                    ActionIntroActivity.this.buttonTextView.layout(x15, y30, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x15, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y30);
                } else if (i > i2) {
                    int y31 = (height - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                    ActionIntroActivity.this.imageView.layout(0, y31, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y31);
                    int x16 = (int) (((float) width) * 0.4f);
                    int y32 = (int) (((float) height) * 0.22f);
                    ActionIntroActivity.this.titleTextView.layout(x16, y32, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + x16, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y32);
                    int x17 = (int) (((float) width) * 0.4f);
                    int y33 = (int) (((float) height) * 0.39f);
                    ActionIntroActivity.this.descriptionText.layout(x17, y33, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + x17, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y33);
                    int x18 = (int) ((((float) width) * 0.4f) + (((((float) width) * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                    int y34 = (int) (((float) height) * 0.69f);
                    ActionIntroActivity.this.buttonTextView.layout(x18, y34, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x18, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y34);
                } else {
                    int y35 = (int) (((float) height) * 0.188f);
                    ActionIntroActivity.this.imageView.layout(0, y35, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + y35);
                    int y36 = (int) (((float) height) * 0.651f);
                    ActionIntroActivity.this.titleTextView.layout(0, y36, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + y36);
                    int y37 = (int) (((float) height) * 0.731f);
                    ActionIntroActivity.this.descriptionText.layout(0, y37, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + y37);
                    int x19 = (width - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    int y38 = (int) (((float) height) * 0.853f);
                    ActionIntroActivity.this.buttonTextView.layout(x19, y38, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + x19, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + y38);
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener($$Lambda$ActionIntroActivity$UNLn_SjS8_16RJIczhC8KTxUPc.INSTANCE);
        viewGroup.addView(this.actionBar);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        viewGroup.addView(imageView2);
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        TextView textView2 = new TextView(context);
        this.subtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.subtitleTextView.setGravity(1);
        this.subtitleTextView.setTextSize(1, 15.0f);
        this.subtitleTextView.setSingleLine(true);
        this.subtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (this.currentType == 2) {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        this.subtitleTextView.setVisibility(8);
        viewGroup.addView(this.subtitleTextView);
        TextView textView3 = new TextView(context);
        this.descriptionText = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        if (this.currentType == 2) {
            this.descriptionText.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText);
        TextView textView4 = new TextView(context);
        this.descriptionText2 = textView4;
        textView4.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setTextSize(1, 13.0f);
        this.descriptionText2.setVisibility(8);
        if (this.currentType == 2) {
            this.descriptionText2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        } else {
            this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText2);
        TextView textView5 = new TextView(context);
        this.buttonTextView = textView5;
        textView5.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        viewGroup.addView(this.buttonTextView);
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ActionIntroActivity.this.lambda$createView$2$ActionIntroActivity(view);
            }
        });
        int i = this.currentType;
        if (i == 0) {
            this.imageView.setImageResource(R.drawable.channelintro);
            this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.titleTextView.setText(LocaleController.getString("ChannelAlertTitle", R.string.ChannelAlertTitle));
            this.descriptionText.setText(LocaleController.getString("ChannelAlertText", R.string.ChannelAlertText));
            this.buttonTextView.setText(LocaleController.getString("ChannelAlertCreate2", R.string.ChannelAlertCreate2));
        } else if (i == 1) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor(Theme.key_chats_archiveBackground)));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 3));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", R.string.PeopleNearby));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyAccessInfo", R.string.PeopleNearbyAccessInfo));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyAllowAccess", R.string.PeopleNearbyAllowAccess));
        } else if (i == 2) {
            this.subtitleTextView.setVisibility(0);
            this.descriptionText2.setVisibility(0);
            this.imageView.setImageResource(Theme.getCurrentTheme().isDark() ? R.drawable.groupsintro2 : R.drawable.groupsintro);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            TextView textView6 = this.subtitleTextView;
            String str = this.currentGroupCreateDisplayAddress;
            if (str == null) {
                str = "";
            }
            textView6.setText(str);
            this.titleTextView.setText(LocaleController.getString("NearbyCreateGroup", R.string.NearbyCreateGroup));
            this.descriptionText.setText(LocaleController.getString("NearbyCreateGroupInfo", R.string.NearbyCreateGroupInfo));
            this.descriptionText2.setText(LocaleController.getString("NearbyCreateGroupInfo2", R.string.NearbyCreateGroupInfo2));
            this.buttonTextView.setText(LocaleController.getString("NearbyStartGroup", R.string.NearbyStartGroup));
        } else if (i == 3) {
            this.subtitleTextView.setVisibility(0);
            this.drawable1 = context.getResources().getDrawable(R.drawable.sim_old);
            this.drawable2 = context.getResources().getDrawable(R.drawable.sim_new);
            this.drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_changephoneinfo_image), PorterDuff.Mode.MULTIPLY));
            this.drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_changephoneinfo_image2), PorterDuff.Mode.MULTIPLY));
            this.imageView.setImageDrawable(new CombinedDrawable(this.drawable1, this.drawable2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            TextView textView7 = this.subtitleTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            textView7.setText(instance.format(Marker.ANY_NON_NULL_MARKER + getUserConfig().getCurrentUser().phone));
            this.titleTextView.setText(LocaleController.getString("PhoneNumberChange2", R.string.PhoneNumberChange2));
            this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", R.string.PhoneNumberHelp)));
            this.buttonTextView.setText(LocaleController.getString("PhoneNumberChange2", R.string.PhoneNumberChange2));
        } else if (i == 4) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor(Theme.key_chats_archiveBackground)));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 3));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", R.string.PeopleNearby));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyGpsInfo", R.string.PeopleNearbyGpsInfo));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyGps", R.string.PeopleNearbyGps));
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$2$ActionIntroActivity(View v) {
        if (getParentActivity() != null) {
            int i = this.currentType;
            if (i == 0) {
                Bundle args = new Bundle();
                args.putInt("step", 0);
                presentFragment(new ChannelCreateActivity(args), true);
            } else if (i == 1) {
                getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"}, 2);
            } else if (i != 2) {
                if (i == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("PhoneNumberChangeTitle", R.string.PhoneNumberChangeTitle));
                    builder.setMessage(LocaleController.getString("PhoneNumberAlert", R.string.PhoneNumberAlert));
                    builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ActionIntroActivity.this.lambda$null$1$ActionIntroActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                } else if (i == 4) {
                    try {
                        getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } else if (this.currentGroupCreateAddress != null && this.currentGroupCreateLocation != null) {
                Bundle args2 = new Bundle();
                ArrayList<Integer> result = new ArrayList<>();
                result.add(Integer.valueOf(getUserConfig().getClientUserId()));
                args2.putIntegerArrayList("result", result);
                args2.putInt("chatType", 4);
                args2.putString("address", this.currentGroupCreateAddress);
                args2.putParcelable("location", this.currentGroupCreateLocation);
                presentFragment(new GroupCreateFinalActivity(args2), true);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ActionIntroActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new ChangePhoneNumberActivity(), true);
    }

    public void onLocationAddressAvailable(String address, String displayAddress, BDLocation location) {
        TextView textView = this.subtitleTextView;
        if (textView != null) {
            textView.setText(address);
            this.currentGroupCreateAddress = address;
            this.currentGroupCreateDisplayAddress = displayAddress;
            this.currentGroupCreateLocation = location;
        }
    }

    public void onResume() {
        super.onResume();
        if (this.currentType == 4) {
            boolean enabled = true;
            if (Build.VERSION.SDK_INT >= 28) {
                enabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
            } else if (Build.VERSION.SDK_INT >= 19) {
                try {
                    boolean z = false;
                    if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) != 0) {
                        z = true;
                    }
                    enabled = z;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (enabled) {
                presentFragment(new NearPersonAndGroupActivity(), true);
            }
        }
    }

    private void showPermissionAlert(boolean byButton) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", R.string.PermissionNoLocationPosition));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ActionIntroActivity.this.lambda$showPermissionAlert$3$ActionIntroActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$3$ActionIntroActivity(DialogInterface dialog, int which) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void setGroupCreateAddress(String address, String displayAddress, BDLocation location) {
        this.currentGroupCreateAddress = address;
        this.currentGroupCreateDisplayAddress = displayAddress;
        this.currentGroupCreateLocation = location;
        if (location != null && address == null) {
            LocationController.fetchLocationAddress(location, this);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2 && grantResults != null && grantResults.length != 0) {
            if (grantResults[0] == 0) {
                presentFragment(new NearPersonAndGroupActivity(), true);
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", R.string.PermissionNoLocationPosition));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ActionIntroActivity.this.lambda$onRequestPermissionsResultFragment$4$ActionIntroActivity(dialogInterface, i);
                    }
                });
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4$ActionIntroActivity(DialogInterface dialog, int which) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarWhiteSelector), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.subtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText6), new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText), new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButton), new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButtonPressed), new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, new Drawable[]{this.drawable1}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_changephoneinfo_image), new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, new Drawable[]{this.drawable2}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_changephoneinfo_image2)};
    }
}
