package im.bclpbkiauv.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestTimeDelegate;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class ProxyListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int callsDetailRow;
    /* access modifiers changed from: private */
    public int callsRow;
    /* access modifiers changed from: private */
    public int connectionsHeaderRow;
    /* access modifiers changed from: private */
    public int currentConnectionState;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int proxyAddRow;
    /* access modifiers changed from: private */
    public int proxyDetailRow;
    /* access modifiers changed from: private */
    public int proxyEndRow;
    /* access modifiers changed from: private */
    public int proxyStartRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int useProxyDetailRow;
    /* access modifiers changed from: private */
    public boolean useProxyForCalls;
    /* access modifiers changed from: private */
    public int useProxyRow;
    /* access modifiers changed from: private */
    public boolean useProxySettings;

    public class TextDetailProxyCell extends FrameLayout {
        private Drawable checkDrawable;
        private ImageView checkImageView;
        private int color;
        /* access modifiers changed from: private */
        public SharedConfig.ProxyInfo currentInfo;
        private TextView textView;
        final /* synthetic */ ProxyListActivity this$0;
        private TextView valueTextView;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public TextDetailProxyCell(im.bclpbkiauv.ui.ProxyListActivity r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r19
                r2 = r18
                r0.this$0 = r2
                r0.<init>(r1)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.textView = r3
                java.lang.String r4 = "windowBackgroundWhiteBlackText"
                int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r0.textView
                r4 = 1
                r5 = 1098907648(0x41800000, float:16.0)
                r3.setTextSize(r4, r5)
                android.widget.TextView r3 = r0.textView
                r3.setLines(r4)
                android.widget.TextView r3 = r0.textView
                r3.setMaxLines(r4)
                android.widget.TextView r3 = r0.textView
                r3.setSingleLine(r4)
                android.widget.TextView r3 = r0.textView
                android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
                r3.setEllipsize(r5)
                android.widget.TextView r3 = r0.textView
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r6 = 5
                r7 = 3
                if (r5 == 0) goto L_0x0044
                r5 = 5
                goto L_0x0045
            L_0x0044:
                r5 = 3
            L_0x0045:
                r5 = r5 | 16
                r3.setGravity(r5)
                android.widget.TextView r3 = r0.textView
                r8 = -1073741824(0xffffffffc0000000, float:-2.0)
                r9 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0056
                r5 = 5
                goto L_0x0057
            L_0x0056:
                r5 = 3
            L_0x0057:
                r10 = r5 | 48
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r15 = 56
                r16 = 21
                if (r5 == 0) goto L_0x0064
                r5 = 56
                goto L_0x0066
            L_0x0064:
                r5 = 21
            L_0x0066:
                float r11 = (float) r5
                r12 = 1092616192(0x41200000, float:10.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0070
                r5 = 21
                goto L_0x0072
            L_0x0070:
                r5 = 56
            L_0x0072:
                float r13 = (float) r5
                r14 = 0
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r5)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r1)
                r0.valueTextView = r3
                r5 = 1095761920(0x41500000, float:13.0)
                r3.setTextSize(r4, r5)
                android.widget.TextView r3 = r0.valueTextView
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x008f
                r5 = 5
                goto L_0x0090
            L_0x008f:
                r5 = 3
            L_0x0090:
                r3.setGravity(r5)
                android.widget.TextView r3 = r0.valueTextView
                r3.setLines(r4)
                android.widget.TextView r3 = r0.valueTextView
                r3.setMaxLines(r4)
                android.widget.TextView r3 = r0.valueTextView
                r3.setSingleLine(r4)
                android.widget.TextView r3 = r0.valueTextView
                r4 = 1086324736(0x40c00000, float:6.0)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                r3.setCompoundDrawablePadding(r4)
                android.widget.TextView r3 = r0.valueTextView
                android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
                r3.setEllipsize(r4)
                android.widget.TextView r3 = r0.valueTextView
                r4 = 0
                r3.setPadding(r4, r4, r4, r4)
                android.widget.TextView r3 = r0.valueTextView
                r8 = -1073741824(0xffffffffc0000000, float:-2.0)
                r9 = -1073741824(0xffffffffc0000000, float:-2.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00c6
                r5 = 5
                goto L_0x00c7
            L_0x00c6:
                r5 = 3
            L_0x00c7:
                r10 = r5 | 48
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00d0
                r5 = 56
                goto L_0x00d2
            L_0x00d0:
                r5 = 21
            L_0x00d2:
                float r11 = (float) r5
                r12 = 1108082688(0x420c0000, float:35.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x00db
                r15 = 21
            L_0x00db:
                float r13 = (float) r15
                r14 = 0
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r5)
                android.widget.ImageView r3 = new android.widget.ImageView
                r3.<init>(r1)
                r0.checkImageView = r3
                r5 = 2131231473(0x7f0802f1, float:1.8079028E38)
                r3.setImageResource(r5)
                android.widget.ImageView r3 = r0.checkImageView
                android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
                java.lang.String r8 = "windowBackgroundWhiteGrayText3"
                int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
                android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
                r5.<init>(r8, r9)
                r3.setColorFilter(r5)
                android.widget.ImageView r3 = r0.checkImageView
                android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
                r3.setScaleType(r5)
                android.widget.ImageView r3 = r0.checkImageView
                r5 = 2131690991(0x7f0f05ef, float:1.9011041E38)
                java.lang.String r8 = "Edit"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r5)
                r3.setContentDescription(r5)
                android.widget.ImageView r3 = r0.checkImageView
                r8 = 1111490560(0x42400000, float:48.0)
                r9 = 1111490560(0x42400000, float:48.0)
                boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r5 == 0) goto L_0x0124
                r6 = 3
            L_0x0124:
                r10 = r6 | 48
                r11 = 1090519040(0x41000000, float:8.0)
                r12 = 1090519040(0x41000000, float:8.0)
                r13 = 1090519040(0x41000000, float:8.0)
                r14 = 0
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r3, r5)
                android.widget.ImageView r3 = r0.checkImageView
                im.bclpbkiauv.ui.-$$Lambda$ProxyListActivity$TextDetailProxyCell$dRn6QVgSRRYCN8gv6Z_HWtrFNsQ r5 = new im.bclpbkiauv.ui.-$$Lambda$ProxyListActivity$TextDetailProxyCell$dRn6QVgSRRYCN8gv6Z_HWtrFNsQ
                r5.<init>()
                r3.setOnClickListener(r5)
                r0.setWillNotDraw(r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ProxyListActivity.TextDetailProxyCell.<init>(im.bclpbkiauv.ui.ProxyListActivity, android.content.Context):void");
        }

        public /* synthetic */ void lambda$new$0$ProxyListActivity$TextDetailProxyCell(View v) {
            this.this$0.presentFragment(new ProxySettingsActivity(this.currentInfo));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + 1, 1073741824));
        }

        public void setProxy(SharedConfig.ProxyInfo proxyInfo) {
            TextView textView2 = this.textView;
            textView2.setText(proxyInfo.address + LogUtils.COLON + proxyInfo.port);
            this.currentInfo = proxyInfo;
        }

        public void updateStatus() {
            String colorKey;
            if (SharedConfig.currentProxy != this.currentInfo || !this.this$0.useProxySettings) {
                if (this.currentInfo.checking) {
                    this.valueTextView.setText(LocaleController.getString("Checking", R.string.Checking));
                    colorKey = Theme.key_windowBackgroundWhiteGrayText2;
                } else if (this.currentInfo.available) {
                    if (this.currentInfo.ping != 0) {
                        TextView textView2 = this.valueTextView;
                        textView2.setText(LocaleController.getString("Available", R.string.Available) + ", " + LocaleController.formatString("Ping", R.string.Ping, Long.valueOf(this.currentInfo.ping)));
                    } else {
                        this.valueTextView.setText(LocaleController.getString("Available", R.string.Available));
                    }
                    colorKey = Theme.key_windowBackgroundWhiteGreenText;
                } else {
                    this.valueTextView.setText(LocaleController.getString("Unavailable", R.string.Unavailable));
                    colorKey = Theme.key_windowBackgroundWhiteRedText4;
                }
            } else if (this.this$0.currentConnectionState == 3 || this.this$0.currentConnectionState == 5) {
                colorKey = Theme.key_windowBackgroundWhiteBlueText6;
                if (this.currentInfo.ping != 0) {
                    TextView textView3 = this.valueTextView;
                    textView3.setText(LocaleController.getString("Connected", R.string.Connected) + ", " + LocaleController.formatString("Ping", R.string.Ping, Long.valueOf(this.currentInfo.ping)));
                } else {
                    this.valueTextView.setText(LocaleController.getString("Connected", R.string.Connected));
                }
                if (!this.currentInfo.checking && !this.currentInfo.available) {
                    this.currentInfo.availableCheckTime = 0;
                }
            } else {
                colorKey = Theme.key_windowBackgroundWhiteGrayText2;
                this.valueTextView.setText(LocaleController.getString("Connecting", R.string.Connecting));
            }
            this.color = Theme.getColor(colorKey);
            this.valueTextView.setTag(colorKey);
            this.valueTextView.setTextColor(this.color);
            Drawable drawable = this.checkDrawable;
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(this.color, PorterDuff.Mode.MULTIPLY));
            }
        }

        public void setChecked(boolean checked) {
            if (checked) {
                if (this.checkDrawable == null) {
                    this.checkDrawable = getResources().getDrawable(R.drawable.proxy_check).mutate();
                }
                Drawable drawable = this.checkDrawable;
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(this.color, PorterDuff.Mode.MULTIPLY));
                }
                if (LocaleController.isRTL) {
                    this.valueTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.checkDrawable, (Drawable) null);
                } else {
                    this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(this.checkDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
                }
            } else {
                this.valueTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            }
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateStatus();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        SharedConfig.loadProxyList();
        this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        this.useProxySettings = preferences.getBoolean("proxy_enabled", false) && !SharedConfig.proxyList.isEmpty();
        this.useProxyForCalls = preferences.getBoolean("proxy_enabled_calls", false);
        updateRows(true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("ProxySettings", R.string.ProxySettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ProxyListActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ProxyListActivity.this.lambda$createView$0$ProxyListActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ProxyListActivity.this.lambda$createView$2$ProxyListActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ProxyListActivity(View view, int position) {
        int i = position;
        if (i == this.useProxyRow) {
            if (SharedConfig.currentProxy == null) {
                if (!SharedConfig.proxyList.isEmpty()) {
                    SharedConfig.currentProxy = SharedConfig.proxyList.get(0);
                    if (!this.useProxySettings) {
                        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                        editor.putString("proxy_ip", SharedConfig.currentProxy.address);
                        editor.putString("proxy_pass", SharedConfig.currentProxy.password);
                        editor.putString("proxy_user", SharedConfig.currentProxy.username);
                        editor.putInt("proxy_port", SharedConfig.currentProxy.port);
                        editor.putString("proxy_secret", SharedConfig.currentProxy.secret);
                        editor.commit();
                    }
                } else {
                    presentFragment(new ProxySettingsActivity());
                    return;
                }
            }
            this.useProxySettings = !this.useProxySettings;
            SharedPreferences globalMainSettings2 = MessagesController.getGlobalMainSettings();
            ((TextCheckCell) view).setChecked(this.useProxySettings);
            if (!this.useProxySettings) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.callsRow);
                if (holder != null) {
                    ((TextCheckCell) holder.itemView).setChecked(false);
                }
                this.useProxyForCalls = false;
            }
            SharedPreferences.Editor editor2 = MessagesController.getGlobalMainSettings().edit();
            editor2.putBoolean("proxy_enabled", this.useProxySettings);
            editor2.commit();
            ConnectionsManager.setProxySettings(this.useProxySettings, SharedConfig.currentProxy.address, SharedConfig.currentProxy.port, SharedConfig.currentProxy.username, SharedConfig.currentProxy.password, SharedConfig.currentProxy.secret);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
            for (int a = this.proxyStartRow; a < this.proxyEndRow; a++) {
                RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(a);
                if (holder2 != null) {
                    ((TextDetailProxyCell) holder2.itemView).updateStatus();
                }
            }
        } else if (i == this.callsRow) {
            boolean z = !this.useProxyForCalls;
            this.useProxyForCalls = z;
            ((TextCheckCell) view).setChecked(z);
            SharedPreferences.Editor editor3 = MessagesController.getGlobalMainSettings().edit();
            editor3.putBoolean("proxy_enabled_calls", this.useProxyForCalls);
            editor3.commit();
        } else if (i >= this.proxyStartRow && i < this.proxyEndRow) {
            SharedConfig.ProxyInfo info = SharedConfig.proxyList.get(i - this.proxyStartRow);
            this.useProxySettings = true;
            SharedPreferences.Editor editor4 = MessagesController.getGlobalMainSettings().edit();
            editor4.putString("proxy_ip", info.address);
            editor4.putString("proxy_pass", info.password);
            editor4.putString("proxy_user", info.username);
            editor4.putInt("proxy_port", info.port);
            editor4.putString("proxy_secret", info.secret);
            editor4.putBoolean("proxy_enabled", this.useProxySettings);
            if (!info.secret.isEmpty()) {
                this.useProxyForCalls = false;
                editor4.putBoolean("proxy_enabled_calls", false);
            }
            editor4.commit();
            SharedConfig.currentProxy = info;
            for (int a2 = this.proxyStartRow; a2 < this.proxyEndRow; a2++) {
                RecyclerListView.Holder holder3 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(a2);
                if (holder3 != null) {
                    TextDetailProxyCell cell = (TextDetailProxyCell) holder3.itemView;
                    cell.setChecked(cell.currentInfo == info);
                    cell.updateStatus();
                }
            }
            updateRows(false);
            RecyclerListView.Holder holder4 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.useProxyRow);
            if (holder4 != null) {
                ((TextCheckCell) holder4.itemView).setChecked(true);
            }
            ConnectionsManager.setProxySettings(this.useProxySettings, SharedConfig.currentProxy.address, SharedConfig.currentProxy.port, SharedConfig.currentProxy.username, SharedConfig.currentProxy.password, SharedConfig.currentProxy.secret);
        } else if (i == this.proxyAddRow) {
            presentFragment(new ProxySettingsActivity());
        }
    }

    public /* synthetic */ boolean lambda$createView$2$ProxyListActivity(View view, int position) {
        if (position < this.proxyStartRow || position >= this.proxyEndRow) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("DeleteProxy", R.string.DeleteProxy));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(SharedConfig.proxyList.get(position - this.proxyStartRow)) {
            private final /* synthetic */ SharedConfig.ProxyInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ProxyListActivity.this.lambda$null$1$ProxyListActivity(this.f$1, dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$1$ProxyListActivity(SharedConfig.ProxyInfo info, DialogInterface dialog, int which) {
        SharedConfig.deleteProxy(info);
        if (SharedConfig.currentProxy == null) {
            this.useProxyForCalls = false;
            this.useProxySettings = false;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        updateRows(true);
    }

    private void updateRows(boolean notify) {
        ListAdapter listAdapter2;
        boolean change = false;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.useProxyRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.useProxyDetailRow = i;
        this.rowCount = i2 + 1;
        this.connectionsHeaderRow = i2;
        if (!SharedConfig.proxyList.isEmpty()) {
            int i3 = this.rowCount;
            this.proxyStartRow = i3;
            int size = i3 + SharedConfig.proxyList.size();
            this.rowCount = size;
            this.proxyEndRow = size;
        } else {
            this.proxyStartRow = -1;
            this.proxyEndRow = -1;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.proxyAddRow = i4;
        this.rowCount = i5 + 1;
        this.proxyDetailRow = i5;
        if (SharedConfig.currentProxy == null || SharedConfig.currentProxy.secret.isEmpty()) {
            if (this.callsRow == -1) {
                change = true;
            }
            int i6 = this.rowCount;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.callsRow = i6;
            this.rowCount = i7 + 1;
            this.callsDetailRow = i7;
            if (!notify && change) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeInserted(this.proxyDetailRow + 1, 2);
            }
        } else {
            if (this.callsRow != -1) {
                change = true;
            }
            this.callsRow = -1;
            this.callsDetailRow = -1;
            if (!notify && change) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeRemoved(this.proxyDetailRow + 1, 2);
            }
        }
        checkProxyList();
        if (notify && (listAdapter2 = this.listAdapter) != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void checkProxyList() {
        int count = SharedConfig.proxyList.size();
        for (int a = 0; a < count; a++) {
            SharedConfig.ProxyInfo proxyInfo = SharedConfig.proxyList.get(a);
            if (!proxyInfo.checking && SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime >= 120000) {
                proxyInfo.checking = true;
                proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(this.currentAccount).checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret, new RequestTimeDelegate() {
                    public final void run(long j) {
                        AndroidUtilities.runOnUIThread(new Runnable(j) {
                            private final /* synthetic */ long f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                ProxyListActivity.lambda$null$3(SharedConfig.ProxyInfo.this, this.f$1);
                            }
                        });
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$null$3(SharedConfig.ProxyInfo proxyInfo, long time) {
        proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
        proxyInfo.checking = false;
        if (time == -1) {
            proxyInfo.available = false;
            proxyInfo.ping = 0;
        } else {
            proxyInfo.ping = time;
            proxyInfo.available = true;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, proxyInfo);
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int idx;
        RecyclerListView.Holder holder;
        int idx2;
        RecyclerListView.Holder holder2;
        if (id == NotificationCenter.proxySettingsChanged) {
            updateRows(true);
        } else if (id == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                if (this.listView != null && SharedConfig.currentProxy != null && (idx2 = SharedConfig.proxyList.indexOf(SharedConfig.currentProxy)) >= 0 && (holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.proxyStartRow + idx2)) != null) {
                    ((TextDetailProxyCell) holder2.itemView).updateStatus();
                }
            }
        } else if (id == NotificationCenter.proxyCheckDone && this.listView != null && (idx = SharedConfig.proxyList.indexOf(args[0])) >= 0 && (holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.proxyStartRow + idx)) != null) {
            ((TextDetailProxyCell) holder.itemView).updateStatus();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ProxyListActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                if (itemViewType == 1) {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == ProxyListActivity.this.proxyAddRow) {
                        textCell.setText(LocaleController.getString("AddProxy", R.string.AddProxy), false);
                    }
                } else if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ProxyListActivity.this.connectionsHeaderRow) {
                        headerCell.setText(LocaleController.getString("ProxyConnections", R.string.ProxyConnections));
                    }
                } else if (itemViewType == 3) {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == ProxyListActivity.this.useProxyRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxySettings", R.string.UseProxySettings), ProxyListActivity.this.useProxySettings, false);
                    } else if (position == ProxyListActivity.this.callsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), ProxyListActivity.this.useProxyForCalls, false);
                    }
                } else if (itemViewType == 4) {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == ProxyListActivity.this.callsDetailRow) {
                        cell.setText(LocaleController.getString("UseProxyForCallsInfo", R.string.UseProxyForCallsInfo));
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    }
                } else if (itemViewType == 5) {
                    TextDetailProxyCell cell2 = (TextDetailProxyCell) holder.itemView;
                    SharedConfig.ProxyInfo info = SharedConfig.proxyList.get(position - ProxyListActivity.this.proxyStartRow);
                    cell2.setProxy(info);
                    if (SharedConfig.currentProxy != info) {
                        z = false;
                    }
                    cell2.setChecked(z);
                }
            } else if (position == ProxyListActivity.this.proxyDetailRow && ProxyListActivity.this.callsRow == -1) {
                holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 3) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                int position = holder.getAdapterPosition();
                if (position == ProxyListActivity.this.useProxyRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxySettings);
                } else if (position == ProxyListActivity.this.callsRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxyForCalls);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == ProxyListActivity.this.useProxyRow || position == ProxyListActivity.this.callsRow || position == ProxyListActivity.this.proxyAddRow || (position >= ProxyListActivity.this.proxyStartRow && position < ProxyListActivity.this.proxyEndRow);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new ShadowSectionCell(this.mContext);
            } else if (viewType == 1) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 2) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 3) {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 4) {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            } else if (viewType == 5) {
                view = new TextDetailProxyCell(ProxyListActivity.this, this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == ProxyListActivity.this.useProxyDetailRow || position == ProxyListActivity.this.proxyDetailRow) {
                return 0;
            }
            if (position == ProxyListActivity.this.proxyAddRow) {
                return 1;
            }
            if (position == ProxyListActivity.this.useProxyRow || position == ProxyListActivity.this.callsRow) {
                return 3;
            }
            if (position == ProxyListActivity.this.connectionsHeaderRow) {
                return 2;
            }
            if (position < ProxyListActivity.this.proxyStartRow || position >= ProxyListActivity.this.proxyEndRow) {
                return 4;
            }
            return 5;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailProxyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailProxyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueText6), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGreenText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"checkImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4)};
    }
}
