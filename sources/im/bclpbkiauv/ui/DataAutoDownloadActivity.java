package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.MaxFileSizeCell;
import im.bclpbkiauv.ui.cells.NotificationsCheckCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckBoxCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Collections;

@Deprecated
public class DataAutoDownloadActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public boolean animateChecked;
    /* access modifiers changed from: private */
    public int autoDownloadRow;
    /* access modifiers changed from: private */
    public int autoDownloadSectionRow;
    /* access modifiers changed from: private */
    public int currentPresetNum;
    /* access modifiers changed from: private */
    public int currentType;
    private DownloadController.Preset defaultPreset;
    /* access modifiers changed from: private */
    public int filesRow;
    /* access modifiers changed from: private */
    public DownloadController.Preset highPreset;
    private String key;
    /* access modifiers changed from: private */
    public String key2;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public DownloadController.Preset lowPreset;
    /* access modifiers changed from: private */
    public DownloadController.Preset mediumPreset;
    /* access modifiers changed from: private */
    public int photosRow;
    /* access modifiers changed from: private */
    public ArrayList<DownloadController.Preset> presets = new ArrayList<>();
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int selectedPreset = 1;
    /* access modifiers changed from: private */
    public int typeHeaderRow;
    /* access modifiers changed from: private */
    public DownloadController.Preset typePreset;
    /* access modifiers changed from: private */
    public int typeSectionRow;
    /* access modifiers changed from: private */
    public int usageHeaderRow;
    /* access modifiers changed from: private */
    public int usageProgressRow;
    /* access modifiers changed from: private */
    public int usageSectionRow;
    /* access modifiers changed from: private */
    public int videosRow;
    /* access modifiers changed from: private */
    public boolean wereAnyChanges;

    private class PresetChooseView extends View {
        private int circleSize;
        private String custom;
        private int customSize;
        private int gapSize;
        private String high;
        private int highSize;
        private int lineSize;
        private String low;
        private int lowSize;
        private String medium;
        private int mediumSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private boolean startMoving;
        private int startMovingPreset;
        private float startX;
        private TextPaint textPaint;

        public PresetChooseView(Context context) {
            super(context);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
            String string = LocaleController.getString("AutoDownloadLow", R.string.AutoDownloadLow);
            this.low = string;
            this.lowSize = (int) Math.ceil((double) this.textPaint.measureText(string));
            String string2 = LocaleController.getString("AutoDownloadMedium", R.string.AutoDownloadMedium);
            this.medium = string2;
            this.mediumSize = (int) Math.ceil((double) this.textPaint.measureText(string2));
            String string3 = LocaleController.getString("AutoDownloadHigh", R.string.AutoDownloadHigh);
            this.high = string3;
            this.highSize = (int) Math.ceil((double) this.textPaint.measureText(string3));
            String string4 = LocaleController.getString("AutoDownloadCustom", R.string.AutoDownloadCustom);
            this.custom = string4;
            this.customSize = (int) Math.ceil((double) this.textPaint.measureText(string4));
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            boolean z = false;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int a = 0;
                while (true) {
                    if (a >= DataAutoDownloadActivity.this.presets.size()) {
                        break;
                    }
                    int i = this.sideSide;
                    int i2 = this.lineSize + (this.gapSize * 2);
                    int i3 = this.circleSize;
                    int cx = i + ((i2 + i3) * a) + (i3 / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == DataAutoDownloadActivity.this.selectedPreset) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingPreset = DataAutoDownloadActivity.this.selectedPreset;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= DataAutoDownloadActivity.this.presets.size()) {
                            break;
                        }
                        int i4 = this.sideSide;
                        int i5 = this.lineSize;
                        int i6 = this.gapSize;
                        int i7 = this.circleSize;
                        int cx2 = i4 + (((i6 * 2) + i5 + i7) * a2) + (i7 / 2);
                        int diff = (i5 / 2) + (i7 / 2) + i6;
                        if (x <= ((float) (cx2 - diff)) || x >= ((float) (cx2 + diff))) {
                            a2++;
                        } else if (DataAutoDownloadActivity.this.selectedPreset != a2) {
                            setPreset(a2);
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    int a3 = 0;
                    while (true) {
                        if (a3 >= 5) {
                            break;
                        }
                        int i8 = this.sideSide;
                        int i9 = this.lineSize + (this.gapSize * 2);
                        int i10 = this.circleSize;
                        int cx3 = i8 + ((i9 + i10) * a3) + (i10 / 2);
                        if (x <= ((float) (cx3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx3))) {
                            a3++;
                        } else if (DataAutoDownloadActivity.this.selectedPreset != a3) {
                            setPreset(a3);
                        }
                    }
                } else if (DataAutoDownloadActivity.this.selectedPreset != this.startMovingPreset) {
                    setPreset(DataAutoDownloadActivity.this.selectedPreset);
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        private void setPreset(int index) {
            int unused = DataAutoDownloadActivity.this.selectedPreset = index;
            DownloadController.Preset preset = (DownloadController.Preset) DataAutoDownloadActivity.this.presets.get(DataAutoDownloadActivity.this.selectedPreset);
            if (preset == DataAutoDownloadActivity.this.lowPreset) {
                int unused2 = DataAutoDownloadActivity.this.currentPresetNum = 0;
            } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                int unused3 = DataAutoDownloadActivity.this.currentPresetNum = 1;
            } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                int unused4 = DataAutoDownloadActivity.this.currentPresetNum = 2;
            } else {
                int unused5 = DataAutoDownloadActivity.this.currentPresetNum = 3;
            }
            if (DataAutoDownloadActivity.this.currentType == 0) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentMobilePreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else if (DataAutoDownloadActivity.this.currentType == 1) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentWifiPreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentRoamingPreset = DataAutoDownloadActivity.this.currentPresetNum;
            }
            SharedPreferences.Editor editor = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
            editor.putInt(DataAutoDownloadActivity.this.key2, DataAutoDownloadActivity.this.currentPresetNum);
            editor.commit();
            DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
            for (int a = 0; a < 3; a++) {
                RecyclerView.ViewHolder holder = DataAutoDownloadActivity.this.listView.findViewHolderForAdapterPosition(DataAutoDownloadActivity.this.photosRow + a);
                if (holder != null) {
                    DataAutoDownloadActivity.this.listAdapter.onBindViewHolder(holder, DataAutoDownloadActivity.this.photosRow + a);
                }
            }
            boolean unused6 = DataAutoDownloadActivity.this.wereAnyChanges = true;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), 1073741824));
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * DataAutoDownloadActivity.this.presets.size())) - ((this.gapSize * 2) * (DataAutoDownloadActivity.this.presets.size() - 1))) - (this.sideSide * 2)) / (DataAutoDownloadActivity.this.presets.size() - 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int size;
            String text;
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int a = 0;
            while (a < DataAutoDownloadActivity.this.presets.size()) {
                int i = this.sideSide;
                int i2 = this.lineSize + (this.gapSize * 2);
                int i3 = this.circleSize;
                int cx = i + ((i2 + i3) * a) + (i3 / 2);
                if (a <= DataAutoDownloadActivity.this.selectedPreset) {
                    this.paint.setColor(Theme.getColor(Theme.key_switchTrackChecked));
                } else {
                    this.paint.setColor(Theme.getColor(Theme.key_switchTrack));
                }
                canvas.drawCircle((float) cx, (float) cy, (float) (a == DataAutoDownloadActivity.this.selectedPreset ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    int width = this.lineSize;
                    if (a == DataAutoDownloadActivity.this.selectedPreset || a == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        width -= AndroidUtilities.dp(3.0f);
                    }
                    if (a == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        x += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (x + width), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                }
                DownloadController.Preset preset = (DownloadController.Preset) DataAutoDownloadActivity.this.presets.get(a);
                if (preset == DataAutoDownloadActivity.this.lowPreset) {
                    text = this.low;
                    size = this.lowSize;
                } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                    text = this.medium;
                    size = this.mediumSize;
                } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                    text = this.high;
                    size = this.highSize;
                } else {
                    text = this.custom;
                    size = this.customSize;
                }
                if (a == 0) {
                    canvas.drawText(text, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (a == DataAutoDownloadActivity.this.presets.size() - 1) {
                    canvas.drawText(text, (float) ((getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(text, (float) (cx - (size / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                a++;
            }
        }
    }

    public DataAutoDownloadActivity(int type) {
        this.currentType = type;
        this.lowPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
        this.mediumPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
        this.highPreset = DownloadController.getInstance(this.currentAccount).highPreset;
        int i = this.currentType;
        if (i == 0) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentMobilePreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).mobilePreset;
            this.defaultPreset = this.mediumPreset;
            this.key = "mobilePreset";
            this.key2 = "currentMobilePreset";
        } else if (i == 1) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentWifiPreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).wifiPreset;
            this.defaultPreset = this.highPreset;
            this.key = "wifiPreset";
            this.key2 = "currentWifiPreset";
        } else {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentRoamingPreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).roamingPreset;
            this.defaultPreset = this.lowPreset;
            this.key = "roamingPreset";
            this.key2 = "currentRoamingPreset";
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        fillPresets();
        updateRows();
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnMobileData", R.string.AutoDownloadOnMobileData));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnWiFiData", R.string.AutoDownloadOnWiFiData));
        } else if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnRoamingData", R.string.AutoDownloadOnRoamingData));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataAutoDownloadActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                DataAutoDownloadActivity.this.lambda$createView$4$DataAutoDownloadActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v38, resolved type: com.google.android.material.chip.Chip} */
    /* JADX WARNING: type inference failed for: r1v27, types: [boolean] */
    /* JADX WARNING: type inference failed for: r1v28 */
    /* JADX WARNING: type inference failed for: r1v29 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$4$DataAutoDownloadActivity(android.view.View r32, int r33, float r34, float r35) {
        /*
            r31 = this;
            r12 = r31
            r13 = r32
            r14 = r33
            int r0 = r12.autoDownloadRow
            r8 = 4
            r9 = 2
            r10 = 3
            r11 = 0
            r15 = 1
            if (r14 != r0) goto L_0x00ee
            int r0 = r12.currentPresetNum
            if (r0 == r10) goto L_0x0030
            if (r0 != 0) goto L_0x001d
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r12.typePreset
            im.bclpbkiauv.messenger.DownloadController$Preset r1 = r12.lowPreset
            r0.set((im.bclpbkiauv.messenger.DownloadController.Preset) r1)
            goto L_0x0030
        L_0x001d:
            if (r0 != r15) goto L_0x0027
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r12.typePreset
            im.bclpbkiauv.messenger.DownloadController$Preset r1 = r12.mediumPreset
            r0.set((im.bclpbkiauv.messenger.DownloadController.Preset) r1)
            goto L_0x0030
        L_0x0027:
            if (r0 != r9) goto L_0x0030
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r12.typePreset
            im.bclpbkiauv.messenger.DownloadController$Preset r1 = r12.highPreset
            r0.set((im.bclpbkiauv.messenger.DownloadController.Preset) r1)
        L_0x0030:
            r0 = r13
            im.bclpbkiauv.ui.cells.TextCheckCell r0 = (im.bclpbkiauv.ui.cells.TextCheckCell) r0
            boolean r1 = r0.isChecked()
            if (r1 != 0) goto L_0x004b
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r2 = r2.enabled
            if (r2 == 0) goto L_0x004b
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.defaultPreset
            int[] r2 = r2.mask
            im.bclpbkiauv.messenger.DownloadController$Preset r3 = r12.typePreset
            int[] r3 = r3.mask
            java.lang.System.arraycopy(r2, r11, r3, r11, r8)
            goto L_0x0052
        L_0x004b:
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r3 = r2.enabled
            r3 = r3 ^ r15
            r2.enabled = r3
        L_0x0052:
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r2 = r2.enabled
            java.lang.String r3 = "windowBackgroundChecked"
            java.lang.String r4 = "windowBackgroundUnchecked"
            if (r2 == 0) goto L_0x0060
            r2 = r3
            goto L_0x0061
        L_0x0060:
            r2 = r4
        L_0x0061:
            r13.setTag(r2)
            r2 = r1 ^ 1
            im.bclpbkiauv.messenger.DownloadController$Preset r5 = r12.typePreset
            boolean r5 = r5.enabled
            if (r5 == 0) goto L_0x006d
            goto L_0x006e
        L_0x006d:
            r3 = r4
        L_0x006e:
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r0.setBackgroundColorAnimated(r2, r3)
            r31.updateRows()
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r2 = r2.enabled
            r3 = 8
            if (r2 == 0) goto L_0x0089
            im.bclpbkiauv.ui.DataAutoDownloadActivity$ListAdapter r2 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r15
            r2.notifyItemRangeInserted(r4, r3)
            goto L_0x0091
        L_0x0089:
            im.bclpbkiauv.ui.DataAutoDownloadActivity$ListAdapter r2 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r15
            r2.notifyItemRangeRemoved(r4, r3)
        L_0x0091:
            im.bclpbkiauv.ui.DataAutoDownloadActivity$ListAdapter r2 = r12.listAdapter
            int r3 = r12.autoDownloadSectionRow
            r2.notifyItemChanged(r3)
            int r2 = r12.currentAccount
            android.content.SharedPreferences r2 = im.bclpbkiauv.messenger.MessagesController.getMainSettings(r2)
            android.content.SharedPreferences$Editor r2 = r2.edit()
            java.lang.String r3 = r12.key
            im.bclpbkiauv.messenger.DownloadController$Preset r4 = r12.typePreset
            java.lang.String r4 = r4.toString()
            r2.putString(r3, r4)
            java.lang.String r3 = r12.key2
            r12.currentPresetNum = r10
            r2.putInt(r3, r10)
            int r3 = r12.currentType
            if (r3 != 0) goto L_0x00c3
            int r3 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r3 = im.bclpbkiauv.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentMobilePreset = r4
            goto L_0x00da
        L_0x00c3:
            if (r3 != r15) goto L_0x00d0
            int r3 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r3 = im.bclpbkiauv.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentWifiPreset = r4
            goto L_0x00da
        L_0x00d0:
            int r3 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r3 = im.bclpbkiauv.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentRoamingPreset = r4
        L_0x00da:
            r2.commit()
            r3 = r1 ^ 1
            r0.setChecked(r3)
            int r3 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r3 = im.bclpbkiauv.messenger.DownloadController.getInstance(r3)
            r3.checkAutodownloadSettings()
            r12.wereAnyChanges = r15
            goto L_0x00fb
        L_0x00ee:
            int r0 = r12.photosRow
            if (r14 == r0) goto L_0x00fd
            int r0 = r12.videosRow
            if (r14 == r0) goto L_0x00fd
            int r0 = r12.filesRow
            if (r14 != r0) goto L_0x00fb
            goto L_0x00fd
        L_0x00fb:
            goto L_0x060d
        L_0x00fd:
            boolean r0 = r32.isEnabled()
            if (r0 != 0) goto L_0x0104
            return
        L_0x0104:
            int r0 = r12.photosRow
            if (r14 != r0) goto L_0x010b
            r0 = 1
            r7 = r0
            goto L_0x0115
        L_0x010b:
            int r0 = r12.videosRow
            if (r14 != r0) goto L_0x0112
            r0 = 4
            r7 = r0
            goto L_0x0115
        L_0x0112:
            r0 = 8
            r7 = r0
        L_0x0115:
            int r16 = im.bclpbkiauv.messenger.DownloadController.typeToIndex(r7)
            int r0 = r12.currentType
            if (r0 != 0) goto L_0x012f
            int r0 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r0 = im.bclpbkiauv.messenger.DownloadController.getInstance(r0)
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r0.getCurrentMobilePreset()
            java.lang.String r1 = "mobilePreset"
            java.lang.String r2 = "currentMobilePreset"
            r6 = r0
            r5 = r1
            r4 = r2
            goto L_0x0155
        L_0x012f:
            if (r0 != r15) goto L_0x0144
            int r0 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r0 = im.bclpbkiauv.messenger.DownloadController.getInstance(r0)
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r0.getCurrentWiFiPreset()
            java.lang.String r1 = "wifiPreset"
            java.lang.String r2 = "currentWifiPreset"
            r6 = r0
            r5 = r1
            r4 = r2
            goto L_0x0155
        L_0x0144:
            int r0 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r0 = im.bclpbkiauv.messenger.DownloadController.getInstance(r0)
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r0.getCurrentRoamingPreset()
            java.lang.String r1 = "roamingPreset"
            java.lang.String r2 = "currentRoamingPreset"
            r6 = r0
            r5 = r1
            r4 = r2
        L_0x0155:
            r3 = r13
            im.bclpbkiauv.ui.cells.NotificationsCheckCell r3 = (im.bclpbkiauv.ui.cells.NotificationsCheckCell) r3
            boolean r17 = r3.isChecked()
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            r1 = 1117257728(0x42980000, float:76.0)
            if (r0 == 0) goto L_0x016b
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            int r0 = (r34 > r0 ? 1 : (r34 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x017d
        L_0x016b:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0239
            int r0 = r32.getMeasuredWidth()
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            float r0 = (float) r0
            int r0 = (r34 > r0 ? 1 : (r34 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x0239
        L_0x017d:
            int r0 = r12.currentPresetNum
            if (r0 == r10) goto L_0x019e
            if (r0 != 0) goto L_0x018b
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r12.typePreset
            im.bclpbkiauv.messenger.DownloadController$Preset r1 = r12.lowPreset
            r0.set((im.bclpbkiauv.messenger.DownloadController.Preset) r1)
            goto L_0x019e
        L_0x018b:
            if (r0 != r15) goto L_0x0195
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r12.typePreset
            im.bclpbkiauv.messenger.DownloadController$Preset r1 = r12.mediumPreset
            r0.set((im.bclpbkiauv.messenger.DownloadController.Preset) r1)
            goto L_0x019e
        L_0x0195:
            if (r0 != r9) goto L_0x019e
            im.bclpbkiauv.messenger.DownloadController$Preset r0 = r12.typePreset
            im.bclpbkiauv.messenger.DownloadController$Preset r1 = r12.highPreset
            r0.set((im.bclpbkiauv.messenger.DownloadController.Preset) r1)
        L_0x019e:
            r0 = 0
            r1 = 0
        L_0x01a0:
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            int r2 = r2.length
            if (r1 >= r2) goto L_0x01b3
            int[] r2 = r6.mask
            r2 = r2[r1]
            r2 = r2 & r7
            if (r2 == 0) goto L_0x01b0
            r0 = 1
            goto L_0x01b3
        L_0x01b0:
            int r1 = r1 + 1
            goto L_0x01a0
        L_0x01b3:
            r1 = 0
        L_0x01b4:
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            int r2 = r2.length
            if (r1 >= r2) goto L_0x01d6
            if (r17 == 0) goto L_0x01c8
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            r8 = r2[r1]
            int r9 = ~r7
            r8 = r8 & r9
            r2[r1] = r8
            goto L_0x01d3
        L_0x01c8:
            if (r0 != 0) goto L_0x01d3
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            r8 = r2[r1]
            r8 = r8 | r7
            r2[r1] = r8
        L_0x01d3:
            int r1 = r1 + 1
            goto L_0x01b4
        L_0x01d6:
            int r1 = r12.currentAccount
            android.content.SharedPreferences r1 = im.bclpbkiauv.messenger.MessagesController.getMainSettings(r1)
            android.content.SharedPreferences$Editor r1 = r1.edit()
            im.bclpbkiauv.messenger.DownloadController$Preset r2 = r12.typePreset
            java.lang.String r2 = r2.toString()
            r1.putString(r5, r2)
            r12.currentPresetNum = r10
            r1.putInt(r4, r10)
            int r2 = r12.currentType
            if (r2 != 0) goto L_0x01fd
            int r2 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r2 = im.bclpbkiauv.messenger.DownloadController.getInstance(r2)
            int r8 = r12.currentPresetNum
            r2.currentMobilePreset = r8
            goto L_0x0214
        L_0x01fd:
            if (r2 != r15) goto L_0x020a
            int r2 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r2 = im.bclpbkiauv.messenger.DownloadController.getInstance(r2)
            int r8 = r12.currentPresetNum
            r2.currentWifiPreset = r8
            goto L_0x0214
        L_0x020a:
            int r2 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r2 = im.bclpbkiauv.messenger.DownloadController.getInstance(r2)
            int r8 = r12.currentPresetNum
            r2.currentRoamingPreset = r8
        L_0x0214:
            r1.commit()
            r2 = r17 ^ 1
            r3.setChecked(r2)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r12.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r13)
            if (r2 == 0) goto L_0x0229
            im.bclpbkiauv.ui.DataAutoDownloadActivity$ListAdapter r8 = r12.listAdapter
            r8.onBindViewHolder(r2, r14)
        L_0x0229:
            int r8 = r12.currentAccount
            im.bclpbkiauv.messenger.DownloadController r8 = im.bclpbkiauv.messenger.DownloadController.getInstance(r8)
            r8.checkAutodownloadSettings()
            r12.wereAnyChanges = r15
            r31.fillPresets()
            goto L_0x060d
        L_0x0239:
            androidx.fragment.app.FragmentActivity r0 = r31.getParentActivity()
            if (r0 != 0) goto L_0x0240
            return
        L_0x0240:
            im.bclpbkiauv.ui.actionbar.BottomSheet$Builder r0 = new im.bclpbkiauv.ui.actionbar.BottomSheet$Builder
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r0.<init>(r1)
            r1 = r0
            r1.setApplyTopPadding(r11)
            r1.setApplyBottomPadding(r11)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            androidx.fragment.app.FragmentActivity r2 = r31.getParentActivity()
            r0.<init>(r2)
            r0.setOrientation(r15)
            r1.setCustomView(r0)
            im.bclpbkiauv.ui.cells.HeaderCell r2 = new im.bclpbkiauv.ui.cells.HeaderCell
            androidx.fragment.app.FragmentActivity r19 = r31.getParentActivity()
            r20 = 1
            r21 = 21
            r22 = 15
            r23 = 0
            r18 = r2
            r18.<init>(r19, r20, r21, r22, r23)
            int r10 = r12.photosRow
            if (r14 != r10) goto L_0x0283
            r10 = 2131690008(0x7f0f0218, float:1.9009047E38)
            java.lang.String r9 = "AutoDownloadPhotosTitle"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r2.setText(r9)
            goto L_0x02a0
        L_0x0283:
            int r9 = r12.videosRow
            if (r14 != r9) goto L_0x0294
            r9 = 2131690018(0x7f0f0222, float:1.9009068E38)
            java.lang.String r10 = "AutoDownloadVideosTitle"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            r2.setText(r9)
            goto L_0x02a0
        L_0x0294:
            r9 = 2131689988(0x7f0f0204, float:1.9009007E38)
            java.lang.String r10 = "AutoDownloadFilesTitle"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            r2.setText(r9)
        L_0x02a0:
            r9 = -1073741824(0xffffffffc0000000, float:-2.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r9)
            r0.addView(r2, r9)
            im.bclpbkiauv.ui.cells.MaxFileSizeCell[] r9 = new im.bclpbkiauv.ui.cells.MaxFileSizeCell[r15]
            im.bclpbkiauv.ui.cells.TextCheckCell[] r10 = new im.bclpbkiauv.ui.cells.TextCheckCell[r15]
            android.animation.AnimatorSet[] r11 = new android.animation.AnimatorSet[r15]
            im.bclpbkiauv.ui.cells.TextCheckBoxCell[] r15 = new im.bclpbkiauv.ui.cells.TextCheckBoxCell[r8]
            r23 = 0
            r13 = r23
        L_0x02b6:
            if (r13 >= r8) goto L_0x0399
            im.bclpbkiauv.ui.cells.TextCheckBoxCell r8 = new im.bclpbkiauv.ui.cells.TextCheckBoxCell
            r24 = r0
            androidx.fragment.app.FragmentActivity r0 = r31.getParentActivity()
            r25 = r1
            r1 = 1
            r8.<init>(r0, r1)
            r15[r13] = r8
            r26 = r2
            r2 = r8
            if (r13 != 0) goto L_0x02eb
            r0 = r15[r13]
            r1 = 2131690040(0x7f0f0238, float:1.9009112E38)
            java.lang.String r8 = "AutodownloadContacts"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r1)
            int[] r8 = r6.mask
            r21 = 0
            r8 = r8[r21]
            r8 = r8 & r7
            if (r8 == 0) goto L_0x02e3
            r8 = 1
            goto L_0x02e4
        L_0x02e3:
            r8 = 0
        L_0x02e4:
            r27 = r3
            r3 = 1
            r0.setTextAndCheck(r1, r8, r3)
            goto L_0x0348
        L_0x02eb:
            r27 = r3
            r3 = 1
            if (r13 != r3) goto L_0x0309
            r0 = r15[r13]
            r1 = 2131690042(0x7f0f023a, float:1.9009116E38)
            java.lang.String r8 = "AutodownloadPrivateChats"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r1)
            int[] r8 = r6.mask
            r8 = r8[r3]
            r8 = r8 & r7
            if (r8 == 0) goto L_0x0304
            r8 = 1
            goto L_0x0305
        L_0x0304:
            r8 = 0
        L_0x0305:
            r0.setTextAndCheck(r1, r8, r3)
            goto L_0x0348
        L_0x0309:
            r8 = 2
            if (r13 != r8) goto L_0x0326
            r0 = r15[r13]
            r1 = 2131690041(0x7f0f0239, float:1.9009114E38)
            java.lang.String r3 = "AutodownloadGroupChats"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r1)
            int[] r3 = r6.mask
            r3 = r3[r8]
            r3 = r3 & r7
            if (r3 == 0) goto L_0x0320
            r3 = 1
            goto L_0x0321
        L_0x0320:
            r3 = 0
        L_0x0321:
            r8 = 1
            r0.setTextAndCheck(r1, r3, r8)
            goto L_0x0348
        L_0x0326:
            r8 = 3
            if (r13 != r8) goto L_0x0348
            r0 = r15[r13]
            r1 = 2131690039(0x7f0f0237, float:1.900911E38)
            java.lang.String r3 = "AutodownloadChannels"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r1)
            int[] r3 = r6.mask
            r3 = r3[r8]
            r3 = r3 & r7
            if (r3 == 0) goto L_0x033d
            r3 = 1
            goto L_0x033e
        L_0x033d:
            r3 = 0
        L_0x033e:
            int r8 = r12.photosRow
            if (r14 == r8) goto L_0x0344
            r8 = 1
            goto L_0x0345
        L_0x0344:
            r8 = 0
        L_0x0345:
            r0.setTextAndCheck(r1, r3, r8)
        L_0x0348:
            r0 = r15[r13]
            r1 = 0
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r1)
            r0.setBackgroundDrawable(r3)
            r8 = r15[r13]
            im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$qv8t1v-66yzxajR4fZh6CnF8vj8 r3 = new im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$qv8t1v-66yzxajR4fZh6CnF8vj8
            r1 = r24
            r0 = r3
            r14 = r1
            r28 = r25
            r1 = r31
            r12 = r3
            r24 = r27
            r3 = r15
            r25 = r4
            r4 = r33
            r27 = r5
            r5 = r9
            r29 = r6
            r6 = r10
            r30 = r7
            r7 = r11
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.setOnClickListener(r12)
            r0 = r15[r13]
            r1 = 1112014848(0x42480000, float:50.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r3, r1)
            r14.addView(r0, r1)
            int r13 = r13 + 1
            r12 = r31
            r0 = r14
            r3 = r24
            r4 = r25
            r2 = r26
            r5 = r27
            r1 = r28
            r6 = r29
            r7 = r30
            r8 = 4
            r14 = r33
            goto L_0x02b6
        L_0x0399:
            r14 = r0
            r28 = r1
            r26 = r2
            r24 = r3
            r25 = r4
            r27 = r5
            r29 = r6
            r30 = r7
            r12 = r31
            int r0 = r12.photosRow
            r7 = r14
            r14 = r33
            if (r14 == r0) goto L_0x04b3
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r0.<init>(r1)
            r6 = r0
            im.bclpbkiauv.ui.DataAutoDownloadActivity$3 r18 = new im.bclpbkiauv.ui.DataAutoDownloadActivity$3
            androidx.fragment.app.FragmentActivity r2 = r31.getParentActivity()
            r3 = 0
            r0 = r18
            r1 = r31
            r4 = r33
            r5 = r6
            r13 = r6
            r6 = r10
            r8 = r7
            r7 = r11
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r0 = 0
            r9[r0] = r18
            r1 = r9[r0]
            r7 = r29
            int[] r2 = r7.sizes
            r2 = r2[r16]
            long r2 = (long) r2
            r1.setSize(r2)
            r1 = r9[r0]
            r0 = 50
            r2 = -1
            android.widget.LinearLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r2, (int) r0)
            r8.addView(r1, r0)
            java.lang.String r0 = "windowBackgroundWhite"
            int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r0)
            r8.setBackgroundColor(r0)
            im.bclpbkiauv.ui.cells.TextCheckCell r0 = new im.bclpbkiauv.ui.cells.TextCheckCell
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r2 = 21
            r3 = 1
            r0.<init>(r1, r2, r3)
            r1 = 0
            r10[r1] = r0
            r0 = r10[r1]
            r2 = 48
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r3, (int) r2)
            r8.addView(r0, r2)
            r0 = r10[r1]
            im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$2fuRTdOEWktok02UE6WNN612GXc r1 = new im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$2fuRTdOEWktok02UE6WNN612GXc
            r1.<init>(r10)
            r0.setOnClickListener(r1)
            androidx.fragment.app.FragmentActivity r0 = r31.getParentActivity()
            r1 = 2131231060(0x7f080154, float:1.807819E38)
            java.lang.String r2 = "windowBackgroundGrayShadow"
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r0, (int) r1, (java.lang.String) r2)
            im.bclpbkiauv.ui.components.CombinedDrawable r1 = new im.bclpbkiauv.ui.components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
            java.lang.String r3 = "windowBackgroundGray"
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r2.<init>(r3)
            r1.<init>(r2, r0)
            r2 = 1
            r1.setFullsize(r2)
            r13.setBackgroundDrawable(r1)
            r2 = -2
            r3 = -1
            android.widget.LinearLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r3, (int) r2)
            r8.addView(r13, r4)
            int r2 = r12.videosRow
            if (r14 != r2) goto L_0x0487
            r2 = 0
            r3 = r9[r2]
            r4 = 2131689993(0x7f0f0209, float:1.9009017E38)
            java.lang.String r5 = "AutoDownloadMaxVideoSize"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            r3 = r10[r2]
            r4 = 2131690012(0x7f0f021c, float:1.9009056E38)
            java.lang.String r5 = "AutoDownloadPreloadVideo"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            boolean r5 = r7.preloadVideo
            r3.setTextAndCheck(r4, r5, r2)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            int[] r4 = r7.sizes
            r4 = r4[r16]
            long r3 = (long) r4
            java.lang.String r3 = im.bclpbkiauv.messenger.AndroidUtilities.formatFileSize(r3)
            r5[r2] = r3
            java.lang.String r3 = "AutoDownloadPreloadVideoInfo"
            r4 = 2131690013(0x7f0f021d, float:1.9009058E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r4, r5)
            r13.setText(r3)
            goto L_0x04b2
        L_0x0487:
            r2 = 0
            r3 = r9[r2]
            r4 = 2131689992(0x7f0f0208, float:1.9009015E38)
            java.lang.String r5 = "AutoDownloadMaxFileSize"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            r3 = r10[r2]
            r4 = 2131690010(0x7f0f021a, float:1.9009052E38)
            java.lang.String r5 = "AutoDownloadPreloadMusic"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            boolean r5 = r7.preloadMusic
            r3.setTextAndCheck(r4, r5, r2)
            r3 = 2131690011(0x7f0f021b, float:1.9009054E38)
            java.lang.String r4 = "AutoDownloadPreloadMusicInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r13.setText(r3)
        L_0x04b2:
            goto L_0x04d8
        L_0x04b3:
            r8 = r7
            r7 = r29
            r2 = 0
            r0 = 0
            r9[r2] = r0
            r10[r2] = r0
            android.view.View r0 = new android.view.View
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r0.<init>(r1)
            java.lang.String r1 = "divider"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout$LayoutParams r1 = new android.widget.LinearLayout$LayoutParams
            r2 = -1
            r3 = 1
            r1.<init>(r2, r3)
            r8.addView(r0, r1)
        L_0x04d8:
            int r0 = r12.videosRow
            if (r14 != r0) goto L_0x050c
            r0 = 0
            r1 = 0
        L_0x04de:
            int r2 = r15.length
            if (r1 >= r2) goto L_0x04ee
            r2 = r15[r1]
            boolean r2 = r2.isChecked()
            if (r2 == 0) goto L_0x04eb
            r0 = 1
            goto L_0x04ee
        L_0x04eb:
            int r1 = r1 + 1
            goto L_0x04de
        L_0x04ee:
            if (r0 != 0) goto L_0x04fd
            r1 = 0
            r2 = r9[r1]
            r3 = 0
            r2.setEnabled(r0, r3)
            r2 = r10[r1]
            r2.setEnabled(r0, r3)
            goto L_0x04ff
        L_0x04fd:
            r1 = 0
            r3 = 0
        L_0x04ff:
            int[] r2 = r7.sizes
            r2 = r2[r16]
            r4 = 2097152(0x200000, float:2.938736E-39)
            if (r2 > r4) goto L_0x050c
            r2 = r10[r1]
            r2.setEnabled(r1, r3)
        L_0x050c:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r0.<init>(r1)
            r13 = r0
            r0 = 1090519040(0x41000000, float:8.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            r13.setPadding(r1, r2, r3, r0)
            r0 = 52
            r1 = -1
            android.widget.LinearLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r1, (int) r0)
            r8.addView(r13, r0)
            android.widget.TextView r0 = new android.widget.TextView
            androidx.fragment.app.FragmentActivity r1 = r31.getParentActivity()
            r0.<init>(r1)
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            java.lang.String r2 = "dialogTextBlue2"
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
            r0.setTextColor(r3)
            r3 = 17
            r0.setGravity(r3)
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r4)
            r0.setTypeface(r5)
            r5 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r6 = "Cancel"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
            java.lang.String r5 = r5.toUpperCase()
            r0.setText(r5)
            r5 = 1092616192(0x41200000, float:10.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r5 = 0
            r0.setPadding(r6, r5, r3, r5)
            r3 = 51
            r5 = 36
            r6 = -2
            android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r5, (int) r3)
            r13.addView(r0, r3)
            im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$y9CzRITRyBYBKqECkd4dW-0IsjU r3 = new im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$y9CzRITRyBYBKqECkd4dW-0IsjU
            r6 = r28
            r3.<init>()
            r0.setOnClickListener(r3)
            android.widget.TextView r3 = new android.widget.TextView
            androidx.fragment.app.FragmentActivity r5 = r31.getParentActivity()
            r3.<init>(r5)
            r5 = r3
            r0 = 1
            r5.setTextSize(r0, r1)
            int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
            r5.setTextColor(r0)
            r0 = 17
            r5.setGravity(r0)
            android.graphics.Typeface r0 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r4)
            r5.setTypeface(r0)
            r0 = 2131693680(0x7f0f1070, float:1.9016495E38)
            java.lang.String r1 = "Save"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            r5.setText(r0)
            r0 = 1092616192(0x41200000, float:10.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            r2 = 0
            r5.setPadding(r1, r2, r0, r2)
            r0 = 53
            r1 = 36
            r2 = -2
            android.widget.FrameLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r2, (int) r1, (int) r0)
            r13.addView(r5, r0)
            im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$cWRgNMUcd76_C89cqB0z5CfJ8Y8 r4 = new im.bclpbkiauv.ui.-$$Lambda$DataAutoDownloadActivity$cWRgNMUcd76_C89cqB0z5CfJ8Y8
            r0 = r4
            r1 = r31
            r2 = r15
            r3 = r30
            r18 = r13
            r13 = r4
            r4 = r9
            r14 = r5
            r5 = r16
            r19 = r6
            r6 = r10
            r20 = r7
            r7 = r33
            r21 = r8
            r8 = r27
            r22 = r9
            r9 = r25
            r23 = r10
            r10 = r19
            r28 = r11
            r11 = r32
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r14.setOnClickListener(r13)
            im.bclpbkiauv.ui.actionbar.BottomSheet r0 = r19.create()
            r12.showDialog(r0)
        L_0x060d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.DataAutoDownloadActivity.lambda$createView$4$DataAutoDownloadActivity(android.view.View, int, float, float):void");
    }

    public /* synthetic */ void lambda$null$0$DataAutoDownloadActivity(TextCheckBoxCell checkBoxCell, TextCheckBoxCell[] cells, int position, MaxFileSizeCell[] sizeCell, TextCheckCell[] checkCell, final AnimatorSet[] animatorSet, View v) {
        if (v.isEnabled()) {
            checkBoxCell.setChecked(!checkBoxCell.isChecked());
            boolean hasAny = false;
            int b = 0;
            while (true) {
                if (b >= cells.length) {
                    break;
                } else if (cells[b].isChecked()) {
                    hasAny = true;
                    break;
                } else {
                    b++;
                }
            }
            if (position == this.videosRow && sizeCell[0].isEnabled() != hasAny) {
                ArrayList<Animator> animators = new ArrayList<>();
                sizeCell[0].setEnabled(hasAny, animators);
                if (sizeCell[0].getSize() > 2097152) {
                    checkCell[0].setEnabled(hasAny, animators);
                }
                if (animatorSet[0] != null) {
                    animatorSet[0].cancel();
                    animatorSet[0] = null;
                }
                animatorSet[0] = new AnimatorSet();
                animatorSet[0].playTogether(animators);
                animatorSet[0].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(animatorSet[0])) {
                            animatorSet[0] = null;
                        }
                    }
                });
                animatorSet[0].setDuration(150);
                animatorSet[0].start();
            }
        }
    }

    public /* synthetic */ void lambda$null$3$DataAutoDownloadActivity(TextCheckBoxCell[] cells, int type, MaxFileSizeCell[] sizeCell, int index, TextCheckCell[] checkCell, int position, String key3, String key22, BottomSheet.Builder builder, View view, View v1) {
        int i = type;
        int i2 = position;
        int i3 = this.currentPresetNum;
        if (i3 != 3) {
            if (i3 == 0) {
                this.typePreset.set(this.lowPreset);
            } else if (i3 == 1) {
                this.typePreset.set(this.mediumPreset);
            } else if (i3 == 2) {
                this.typePreset.set(this.highPreset);
            }
        }
        for (int a = 0; a < 4; a++) {
            if (cells[a].isChecked()) {
                int[] iArr = this.typePreset.mask;
                iArr[a] = iArr[a] | i;
            } else {
                int[] iArr2 = this.typePreset.mask;
                iArr2[a] = iArr2[a] & (~i);
            }
        }
        if (sizeCell[0] != null) {
            int size = (int) sizeCell[0].getSize();
            this.typePreset.sizes[index] = (int) sizeCell[0].getSize();
        }
        if (checkCell[0] != null) {
            if (i2 == this.videosRow) {
                this.typePreset.preloadVideo = checkCell[0].isChecked();
            } else {
                this.typePreset.preloadMusic = checkCell[0].isChecked();
            }
        }
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putString(key3, this.typePreset.toString());
        this.currentPresetNum = 3;
        editor.putInt(key22, 3);
        int i4 = this.currentType;
        if (i4 == 0) {
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
        } else if (i4 == 1) {
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
        } else {
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
        }
        editor.commit();
        builder.getDismissRunnable().run();
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view);
        if (holder != null) {
            this.animateChecked = true;
            this.listAdapter.onBindViewHolder(holder, i2);
            this.animateChecked = false;
        }
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        this.wereAnyChanges = true;
        fillPresets();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.wereAnyChanges) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(this.currentType);
            this.wereAnyChanges = false;
        }
    }

    private void fillPresets() {
        this.presets.clear();
        this.presets.add(this.lowPreset);
        this.presets.add(this.mediumPreset);
        this.presets.add(this.highPreset);
        if (!this.typePreset.equals(this.lowPreset) && !this.typePreset.equals(this.mediumPreset) && !this.typePreset.equals(this.highPreset)) {
            this.presets.add(this.typePreset);
        }
        Collections.sort(this.presets, $$Lambda$DataAutoDownloadActivity$Q74IBfJW6cQK9ZiSXwRzucB4_pc.INSTANCE);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            RecyclerView.ViewHolder holder = recyclerListView.findViewHolderForAdapterPosition(this.usageProgressRow);
            if (holder != null) {
                holder.itemView.requestLayout();
            } else {
                this.listAdapter.notifyItemChanged(this.usageProgressRow);
            }
        }
        int i = this.currentPresetNum;
        if (i == 0 || (i == 3 && this.typePreset.equals(this.lowPreset))) {
            this.selectedPreset = this.presets.indexOf(this.lowPreset);
            return;
        }
        int i2 = this.currentPresetNum;
        if (i2 == 1 || (i2 == 3 && this.typePreset.equals(this.mediumPreset))) {
            this.selectedPreset = this.presets.indexOf(this.mediumPreset);
            return;
        }
        int i3 = this.currentPresetNum;
        if (i3 == 2 || (i3 == 3 && this.typePreset.equals(this.highPreset))) {
            this.selectedPreset = this.presets.indexOf(this.highPreset);
        } else {
            this.selectedPreset = this.presets.indexOf(this.typePreset);
        }
    }

    static /* synthetic */ int lambda$fillPresets$5(DownloadController.Preset o1, DownloadController.Preset o2) {
        int index1 = DownloadController.typeToIndex(4);
        int index2 = DownloadController.typeToIndex(8);
        boolean video1 = false;
        boolean doc1 = false;
        for (int a = 0; a < o1.mask.length; a++) {
            if ((o1.mask[a] & 4) != 0) {
                video1 = true;
            }
            if ((o1.mask[a] & 8) != 0) {
                doc1 = true;
            }
            if (video1 && doc1) {
                break;
            }
        }
        boolean video2 = false;
        boolean doc2 = false;
        for (int a2 = 0; a2 < o2.mask.length; a2++) {
            if ((o2.mask[a2] & 4) != 0) {
                video2 = true;
            }
            if ((o2.mask[a2] & 8) != 0) {
                doc2 = true;
            }
            if (video2 && doc2) {
                break;
            }
        }
        int size1 = (video1 ? o1.sizes[index1] : 0) + (doc1 ? o1.sizes[index2] : 0);
        int size2 = (video2 ? o2.sizes[index1] : 0) + (doc2 ? o2.sizes[index2] : 0);
        if (size1 > size2) {
            return 1;
        }
        if (size1 < size2) {
            return -1;
        }
        return 0;
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.autoDownloadRow = 0;
        this.rowCount = i + 1;
        this.autoDownloadSectionRow = i;
        if (this.typePreset.enabled) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.usageHeaderRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.usageProgressRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.usageSectionRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.typeHeaderRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.photosRow = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.videosRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.filesRow = i8;
            this.rowCount = i9 + 1;
            this.typeSectionRow = i9;
            return;
        }
        this.usageHeaderRow = -1;
        this.usageProgressRow = -1;
        this.usageSectionRow = -1;
        this.typeHeaderRow = -1;
        this.photosRow = -1;
        this.videosRow = -1;
        this.filesRow = -1;
        this.typeSectionRow = -1;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataAutoDownloadActivity.this.rowCount;
        }

        /* JADX WARNING: Removed duplicated region for block: B:72:0x0227  */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x0231  */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x0233  */
        /* JADX WARNING: Removed duplicated region for block: B:81:0x023f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                int r3 = r19.getItemViewType()
                r4 = 0
                r5 = 1
                if (r3 == 0) goto L_0x027e
                r6 = 2
                if (r3 == r6) goto L_0x0250
                r7 = 4
                if (r3 == r7) goto L_0x00b8
                r7 = 5
                if (r3 == r7) goto L_0x0019
                goto L_0x02ca
            L_0x0019:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = (im.bclpbkiauv.ui.cells.TextInfoPrivacyCell) r3
                im.bclpbkiauv.ui.DataAutoDownloadActivity r7 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r7 = r7.typeSectionRow
                r8 = 2131231060(0x7f080154, float:1.807819E38)
                java.lang.String r9 = "windowBackgroundGrayShadow"
                if (r2 != r7) goto L_0x0045
                r5 = 2131689981(0x7f0f01fd, float:1.9008993E38)
                java.lang.String r6 = "AutoDownloadAudioInfo"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r3.setText(r5)
                android.content.Context r5 = r0.mContext
                android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r5, (int) r8, (java.lang.String) r9)
                r3.setBackgroundDrawable(r5)
                r3.setFixedSize(r4)
                goto L_0x02ca
            L_0x0045:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r4 = r4.autoDownloadSectionRow
                if (r2 != r4) goto L_0x02ca
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r4 = r4.usageHeaderRow
                r7 = -1
                if (r4 != r7) goto L_0x00a4
                android.content.Context r4 = r0.mContext
                r7 = 2131231061(0x7f080155, float:1.8078192E38)
                android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r4, (int) r7, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r4 = r4.currentType
                if (r4 != 0) goto L_0x0078
                r4 = 2131690000(0x7f0f0210, float:1.9009031E38)
                java.lang.String r5 = "AutoDownloadOnMobileDataInfo"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x02ca
            L_0x0078:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r4 = r4.currentType
                if (r4 != r5) goto L_0x008e
                r4 = 2131690005(0x7f0f0215, float:1.9009041E38)
                java.lang.String r5 = "AutoDownloadOnWiFiDataInfo"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x02ca
            L_0x008e:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r4 = r4.currentType
                if (r4 != r6) goto L_0x02ca
                r4 = 2131690002(0x7f0f0212, float:1.9009035E38)
                java.lang.String r5 = "AutoDownloadOnRoamingDataInfo"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x02ca
            L_0x00a4:
                android.content.Context r4 = r0.mContext
                android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r4, (int) r8, (java.lang.String) r9)
                r3.setBackgroundDrawable(r4)
                r4 = 0
                r3.setText(r4)
                r4 = 12
                r3.setFixedSize(r4)
                goto L_0x02ca
            L_0x00b8:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.NotificationsCheckCell r3 = (im.bclpbkiauv.ui.cells.NotificationsCheckCell) r3
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.photosRow
                if (r2 != r8) goto L_0x00d2
                r8 = 2131690006(0x7f0f0216, float:1.9009043E38)
                java.lang.String r9 = "AutoDownloadPhotos"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                r9 = 1
                r15 = r8
                r16 = r9
                goto L_0x00f6
            L_0x00d2:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.videosRow
                if (r2 != r8) goto L_0x00e8
                r8 = 2131690016(0x7f0f0220, float:1.9009064E38)
                java.lang.String r9 = "AutoDownloadVideos"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                r9 = 4
                r15 = r8
                r16 = r9
                goto L_0x00f6
            L_0x00e8:
                r8 = 2131689986(0x7f0f0202, float:1.9009003E38)
                java.lang.String r9 = "AutoDownloadFiles"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
                r9 = 8
                r15 = r8
                r16 = r9
            L_0x00f6:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentType
                if (r8 != 0) goto L_0x010e
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentAccount
                im.bclpbkiauv.messenger.DownloadController r8 = im.bclpbkiauv.messenger.DownloadController.getInstance(r8)
                im.bclpbkiauv.messenger.DownloadController$Preset r8 = r8.getCurrentMobilePreset()
                r14 = r8
                goto L_0x0135
            L_0x010e:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentType
                if (r8 != r5) goto L_0x0126
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentAccount
                im.bclpbkiauv.messenger.DownloadController r8 = im.bclpbkiauv.messenger.DownloadController.getInstance(r8)
                im.bclpbkiauv.messenger.DownloadController$Preset r8 = r8.getCurrentWiFiPreset()
                r14 = r8
                goto L_0x0135
            L_0x0126:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentAccount
                im.bclpbkiauv.messenger.DownloadController r8 = im.bclpbkiauv.messenger.DownloadController.getInstance(r8)
                im.bclpbkiauv.messenger.DownloadController$Preset r8 = r8.getCurrentRoamingPreset()
                r14 = r8
            L_0x0135:
                int[] r8 = r14.sizes
                int r9 = im.bclpbkiauv.messenger.DownloadController.typeToIndex(r16)
                r13 = r8[r9]
                r8 = 0
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                r10 = 0
                r12 = r8
            L_0x0145:
                int[] r8 = r14.mask
                int r8 = r8.length
                if (r10 >= r8) goto L_0x01a0
                int[] r8 = r14.mask
                r8 = r8[r10]
                r8 = r8 & r16
                if (r8 == 0) goto L_0x019d
                int r8 = r9.length()
                if (r8 == 0) goto L_0x015d
                java.lang.String r8 = ", "
                r9.append(r8)
            L_0x015d:
                if (r10 == 0) goto L_0x018e
                if (r10 == r5) goto L_0x0181
                if (r10 == r6) goto L_0x0174
                r8 = 3
                if (r10 == r8) goto L_0x0167
                goto L_0x019b
            L_0x0167:
                r8 = 2131689982(0x7f0f01fe, float:1.9008995E38)
                java.lang.String r11 = "AutoDownloadChannels"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
                goto L_0x019b
            L_0x0174:
                r8 = 2131689989(0x7f0f0205, float:1.9009009E38)
                java.lang.String r11 = "AutoDownloadGroups"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
                goto L_0x019b
            L_0x0181:
                r8 = 2131690009(0x7f0f0219, float:1.900905E38)
                java.lang.String r11 = "AutoDownloadPm"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
                goto L_0x019b
            L_0x018e:
                r8 = 2131689983(0x7f0f01ff, float:1.9008997E38)
                java.lang.String r11 = "AutoDownloadContacts"
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
            L_0x019b:
                int r12 = r12 + 1
            L_0x019d:
                int r10 = r10 + 1
                goto L_0x0145
            L_0x01a0:
                if (r12 != r7) goto L_0x01d0
                r9.setLength(r4)
                im.bclpbkiauv.ui.DataAutoDownloadActivity r6 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r6 = r6.photosRow
                if (r2 != r6) goto L_0x01ba
                r6 = 2131689997(0x7f0f020d, float:1.9009025E38)
                java.lang.String r7 = "AutoDownloadOnAllChats"
                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
                r9.append(r6)
                goto L_0x01de
            L_0x01ba:
                r6 = 2131690015(0x7f0f021f, float:1.9009062E38)
                java.lang.Object[] r7 = new java.lang.Object[r5]
                long r10 = (long) r13
                java.lang.String r8 = im.bclpbkiauv.messenger.AndroidUtilities.formatFileSize(r10)
                r7[r4] = r8
                java.lang.String r8 = "AutoDownloadUpToOnAllChats"
                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.formatString(r8, r6, r7)
                r9.append(r6)
                goto L_0x01de
            L_0x01d0:
                if (r12 != 0) goto L_0x01e0
                r6 = 2131689996(0x7f0f020c, float:1.9009023E38)
                java.lang.String r7 = "AutoDownloadOff"
                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
                r9.append(r6)
            L_0x01de:
                r6 = r9
                goto L_0x021f
            L_0x01e0:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r7 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r7 = r7.photosRow
                if (r2 != r7) goto L_0x0200
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r7 = 2131689998(0x7f0f020e, float:1.9009027E38)
                java.lang.Object[] r8 = new java.lang.Object[r5]
                java.lang.String r10 = r9.toString()
                r8[r4] = r10
                java.lang.String r10 = "AutoDownloadOnFor"
                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r10, r7, r8)
                r6.<init>(r7)
                r9 = r6
                goto L_0x021f
            L_0x0200:
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r8 = 2131690003(0x7f0f0213, float:1.9009037E38)
                java.lang.Object[] r6 = new java.lang.Object[r6]
                long r10 = (long) r13
                java.lang.String r10 = im.bclpbkiauv.messenger.AndroidUtilities.formatFileSize(r10)
                r6[r4] = r10
                java.lang.String r10 = r9.toString()
                r6[r5] = r10
                java.lang.String r10 = "AutoDownloadOnUpToFor"
                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.formatString(r10, r8, r6)
                r7.<init>(r6)
                r9 = r7
                r6 = r9
            L_0x021f:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r7 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                boolean r7 = r7.animateChecked
                if (r7 == 0) goto L_0x022f
                if (r12 == 0) goto L_0x022b
                r7 = 1
                goto L_0x022c
            L_0x022b:
                r7 = 0
            L_0x022c:
                r3.setChecked(r7)
            L_0x022f:
                if (r12 == 0) goto L_0x0233
                r11 = 1
                goto L_0x0234
            L_0x0233:
                r11 = 0
            L_0x0234:
                r7 = 0
                r17 = 1
                im.bclpbkiauv.ui.DataAutoDownloadActivity r8 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r8 = r8.filesRow
                if (r2 == r8) goto L_0x0240
                r4 = 1
            L_0x0240:
                r8 = r3
                r9 = r15
                r10 = r6
                r5 = r12
                r12 = r7
                r7 = r13
                r13 = r17
                r17 = r14
                r14 = r4
                r8.setTextAndValueAndCheck(r9, r10, r11, r12, r13, r14)
                goto L_0x02ca
            L_0x0250:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.HeaderCell r3 = (im.bclpbkiauv.ui.cells.HeaderCell) r3
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r4 = r4.usageHeaderRow
                if (r2 != r4) goto L_0x0269
                r4 = 2131689985(0x7f0f0201, float:1.9009E38)
                java.lang.String r5 = "AutoDownloadDataUsage"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x02ca
            L_0x0269:
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r4 = r4.typeHeaderRow
                if (r2 != r4) goto L_0x02ca
                r4 = 2131690014(0x7f0f021e, float:1.900906E38)
                java.lang.String r5 = "AutoDownloadTypes"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x02ca
            L_0x027e:
                android.view.View r3 = r1.itemView
                im.bclpbkiauv.ui.cells.TextCheckCell r3 = (im.bclpbkiauv.ui.cells.TextCheckCell) r3
                im.bclpbkiauv.ui.DataAutoDownloadActivity r6 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                int r6 = r6.autoDownloadRow
                if (r2 != r6) goto L_0x02ca
                r3.setDrawCheckRipple(r5)
                r5 = 2131689994(0x7f0f020a, float:1.900902E38)
                java.lang.String r6 = "AutoDownloadMedia"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                im.bclpbkiauv.ui.DataAutoDownloadActivity r6 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                im.bclpbkiauv.messenger.DownloadController$Preset r6 = r6.typePreset
                boolean r6 = r6.enabled
                r3.setTextAndCheck(r5, r6, r4)
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                im.bclpbkiauv.messenger.DownloadController$Preset r4 = r4.typePreset
                boolean r4 = r4.enabled
                java.lang.String r5 = "windowBackgroundChecked"
                java.lang.String r6 = "windowBackgroundUnchecked"
                if (r4 == 0) goto L_0x02b3
                r4 = r5
                goto L_0x02b4
            L_0x02b3:
                r4 = r6
            L_0x02b4:
                r3.setTag(r4)
                im.bclpbkiauv.ui.DataAutoDownloadActivity r4 = im.bclpbkiauv.ui.DataAutoDownloadActivity.this
                im.bclpbkiauv.messenger.DownloadController$Preset r4 = r4.typePreset
                boolean r4 = r4.enabled
                if (r4 == 0) goto L_0x02c2
                goto L_0x02c3
            L_0x02c2:
                r5 = r6
            L_0x02c3:
                int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                r3.setBackgroundColor(r4)
            L_0x02ca:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.DataAutoDownloadActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DataAutoDownloadActivity.this.photosRow || position == DataAutoDownloadActivity.this.videosRow || position == DataAutoDownloadActivity.this.filesRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                TextCheckCell cell = new TextCheckCell(this.mContext);
                cell.setColors(Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
                cell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                cell.setHeight(56);
                view = cell;
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                layoutParams.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams);
            } else if (viewType == 1) {
                view = new ShadowSectionCell(this.mContext);
                RecyclerView.LayoutParams layoutParams2 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams2.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams2.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams2);
            } else if (viewType == 2) {
                view = new HeaderCell(this.mContext);
                RecyclerView.LayoutParams layoutParams3 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams3.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams3.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams3);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 3) {
                view = new PresetChooseView(this.mContext);
                RecyclerView.LayoutParams layoutParams4 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams4.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams4.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams4);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 4) {
                view = new NotificationsCheckCell(this.mContext);
                RecyclerView.LayoutParams layoutParams5 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams5.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams5.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams5);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 5) {
                view = new TextInfoPrivacyCell(this.mContext);
                RecyclerView.LayoutParams layoutParams6 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams6.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams6.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams6);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == DataAutoDownloadActivity.this.autoDownloadRow) {
                return 0;
            }
            if (position == DataAutoDownloadActivity.this.usageSectionRow) {
                return 1;
            }
            if (position == DataAutoDownloadActivity.this.usageHeaderRow || position == DataAutoDownloadActivity.this.typeHeaderRow) {
                return 2;
            }
            if (position == DataAutoDownloadActivity.this.usageProgressRow) {
                return 3;
            }
            if (position == DataAutoDownloadActivity.this.photosRow || position == DataAutoDownloadActivity.this.videosRow || position == DataAutoDownloadActivity.this.filesRow) {
                return 4;
            }
            return 5;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, NotificationsCheckCell.class, PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundUnchecked), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundCheckText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackBlue), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackBlueChecked), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackBlueThumb), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackBlueThumbChecked), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackBlueSelector), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackBlueSelectorChecked), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack), new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked), new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText)};
    }
}
