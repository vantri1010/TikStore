package im.bclpbkiauv.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.support.ArrayUtils;
import im.bclpbkiauv.messenger.time.SunDate;
import im.bclpbkiauv.ui.ThemeActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.BrightnessControlCell;
import im.bclpbkiauv.ui.cells.ChatListCell;
import im.bclpbkiauv.ui.cells.ChatMessageCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.NotificationsCheckCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.cells.ThemePreviewMessagesCell;
import im.bclpbkiauv.ui.cells.ThemeTypeCell;
import im.bclpbkiauv.ui.cells.ThemesHorizontalListCell;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SeekBarView;
import im.bclpbkiauv.ui.components.ShareAlert;
import im.bclpbkiauv.ui.components.ThemeEditorView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Deprecated
public class ThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    public static final int THEME_TYPE_OTHER = 2;
    private static final int create_theme = 1;
    /* access modifiers changed from: private */
    public int automaticBrightnessInfoRow;
    /* access modifiers changed from: private */
    public int automaticBrightnessRow;
    /* access modifiers changed from: private */
    public int automaticHeaderRow;
    /* access modifiers changed from: private */
    public int backgroundRow;
    /* access modifiers changed from: private */
    public int chatListHeaderRow;
    /* access modifiers changed from: private */
    public int chatListInfoRow;
    /* access modifiers changed from: private */
    public int chatListRow;
    /* access modifiers changed from: private */
    public int contactsReimportRow;
    /* access modifiers changed from: private */
    public int contactsSortRow;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int customTabsRow;
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> darkThemes = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> defaultThemes = new ArrayList<>();
    /* access modifiers changed from: private */
    public int directShareRow;
    /* access modifiers changed from: private */
    public int distanceRow;
    /* access modifiers changed from: private */
    public int emojiRow;
    /* access modifiers changed from: private */
    public int enableAnimationsRow;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    boolean hasThemeAccents;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private GpsLocationListener networkLocationListener = new GpsLocationListener();
    /* access modifiers changed from: private */
    public int newThemeInfoRow;
    /* access modifiers changed from: private */
    public int nightAutomaticRow;
    /* access modifiers changed from: private */
    public int nightDisabledRow;
    /* access modifiers changed from: private */
    public int nightScheduledRow;
    /* access modifiers changed from: private */
    public int nightThemeRow;
    /* access modifiers changed from: private */
    public int nightTypeInfoRow;
    /* access modifiers changed from: private */
    public int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    /* access modifiers changed from: private */
    public int raiseToSpeakRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int saveToGalleryRow;
    /* access modifiers changed from: private */
    public int scheduleFromRow;
    /* access modifiers changed from: private */
    public int scheduleFromToInfoRow;
    /* access modifiers changed from: private */
    public int scheduleHeaderRow;
    /* access modifiers changed from: private */
    public int scheduleLocationInfoRow;
    /* access modifiers changed from: private */
    public int scheduleLocationRow;
    /* access modifiers changed from: private */
    public int scheduleToRow;
    /* access modifiers changed from: private */
    public int scheduleUpdateLocationRow;
    /* access modifiers changed from: private */
    public int sendByEnterRow;
    /* access modifiers changed from: private */
    public int settings2Row;
    /* access modifiers changed from: private */
    public int settingsRow;
    /* access modifiers changed from: private */
    public int stickersRow;
    /* access modifiers changed from: private */
    public int stickersSection2Row;
    /* access modifiers changed from: private */
    public int textSizeHeaderRow;
    /* access modifiers changed from: private */
    public int textSizeRow;
    /* access modifiers changed from: private */
    public int themeAccentListRow;
    /* access modifiers changed from: private */
    public int themeHeaderRow;
    /* access modifiers changed from: private */
    public int themeInfoRow;
    /* access modifiers changed from: private */
    public int themeListRow;
    /* access modifiers changed from: private */
    public ThemesHorizontalListCell themesHorizontalListCell;
    private boolean updatingLocation;

    public interface SizeChooseViewDelegate {
        void onSizeChanged();
    }

    private class GpsLocationListener implements LocationListener {
        private GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }

    private class TextSizeCell extends FrameLayout {
        private int endFontSize = 30;
        private int lastWidth;
        private ThemePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        private int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                public final void onSeekBarDrag(float f) {
                    ThemeActivity.TextSizeCell.this.lambda$new$0$ThemeActivity$TextSizeCell(f);
                }
            });
            addView(this.sizeBar, LayoutHelper.createFrame(-1.0f, 38.0f, 51, 9.0f, 5.0f, 43.0f, 0.0f));
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, ThemeActivity.this.parentLayout, 0);
            this.messagesCell = themePreviewMessagesCell;
            addView(themePreviewMessagesCell, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        public /* synthetic */ void lambda$new$0$ThemeActivity$TextSizeCell(float progress) {
            int i = this.startFontSize;
            int fontSize = Math.round(((float) i) + (((float) (this.endFontSize - i)) * progress));
            if (fontSize != SharedConfig.fontSize) {
                SharedConfig.fontSize = fontSize;
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putInt("fons_size", SharedConfig.fontSize);
                editor.commit();
                Theme.chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
                int firstVisPos = ThemeActivity.this.layoutManager.findFirstVisibleItemPosition();
                View firstVisView = firstVisPos != -1 ? ThemeActivity.this.layoutManager.findViewByPosition(firstVisPos) : null;
                int top = firstVisView != null ? firstVisView.getTop() : 0;
                ChatMessageCell[] cells = this.messagesCell.getCells();
                for (int a = 0; a < cells.length; a++) {
                    cells[a].getMessageObject().resetLayout();
                    cells[a].requestLayout();
                }
                if (firstVisView != null) {
                    ThemeActivity.this.layoutManager.scrollToPositionWithOffset(firstVisPos, top);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + SharedConfig.fontSize, (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int w = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.lastWidth != w) {
                SeekBarView seekBarView = this.sizeBar;
                int i = SharedConfig.fontSize;
                int i2 = this.startFontSize;
                seekBarView.setProgress(((float) (i - i2)) / ((float) (this.endFontSize - i2)));
                this.lastWidth = w;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.messagesCell.invalidate();
            this.sizeBar.invalidate();
        }
    }

    public ThemeActivity(int type) {
        this.currentType = type;
        updateRows(true);
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean notify) {
        int i;
        int i2;
        int oldRowCount = this.rowCount;
        int prevThemeAccentListRow = this.themeAccentListRow;
        this.rowCount = 0;
        this.emojiRow = -1;
        this.contactsReimportRow = -1;
        this.contactsSortRow = -1;
        this.scheduleLocationRow = -1;
        this.scheduleUpdateLocationRow = -1;
        this.scheduleLocationInfoRow = -1;
        this.nightDisabledRow = -1;
        this.nightScheduledRow = -1;
        this.nightAutomaticRow = -1;
        this.nightTypeInfoRow = -1;
        this.scheduleHeaderRow = -1;
        this.nightThemeRow = -1;
        this.newThemeInfoRow = -1;
        this.scheduleFromRow = -1;
        this.scheduleToRow = -1;
        this.scheduleFromToInfoRow = -1;
        this.themeListRow = -1;
        this.themeAccentListRow = -1;
        this.themeInfoRow = -1;
        this.preferedHeaderRow = -1;
        this.automaticHeaderRow = -1;
        this.automaticBrightnessRow = -1;
        this.automaticBrightnessInfoRow = -1;
        this.textSizeHeaderRow = -1;
        this.themeHeaderRow = -1;
        this.chatListHeaderRow = -1;
        this.chatListRow = -1;
        this.chatListInfoRow = -1;
        this.textSizeRow = -1;
        this.backgroundRow = -1;
        this.settingsRow = -1;
        this.customTabsRow = -1;
        this.directShareRow = -1;
        this.enableAnimationsRow = -1;
        this.raiseToSpeakRow = -1;
        this.sendByEnterRow = -1;
        this.saveToGalleryRow = -1;
        this.distanceRow = -1;
        this.settings2Row = -1;
        this.stickersRow = -1;
        this.stickersSection2Row = -1;
        int i3 = 2;
        if (this.currentType == 0) {
            this.defaultThemes.clear();
            this.darkThemes.clear();
            int N = Theme.themes.size();
            for (int a = 0; a < N; a++) {
                Theme.ThemeInfo themeInfo = Theme.themes.get(a);
                if (themeInfo.pathToFile != null) {
                    this.darkThemes.add(themeInfo);
                } else {
                    this.defaultThemes.add(themeInfo);
                }
            }
            Collections.sort(this.defaultThemes, $$Lambda$ThemeActivity$8iwdUnho8asAApKwHRqHZjy5kDQ.INSTANCE);
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.textSizeHeaderRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.textSizeRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.backgroundRow = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.newThemeInfoRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.themeHeaderRow = i8;
            this.rowCount = i9 + 1;
            this.themeListRow = i9;
            boolean z = Theme.getCurrentTheme().accentColorOptions != null;
            this.hasThemeAccents = z;
            ThemesHorizontalListCell themesHorizontalListCell2 = this.themesHorizontalListCell;
            if (themesHorizontalListCell2 != null) {
                themesHorizontalListCell2.setDrawDivider(z);
            }
            if (this.hasThemeAccents) {
                int i10 = this.rowCount;
                this.rowCount = i10 + 1;
                this.themeAccentListRow = i10;
            }
            int i11 = this.rowCount;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.themeInfoRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.chatListHeaderRow = i12;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.chatListRow = i13;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.chatListInfoRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.settingsRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.nightThemeRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.customTabsRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.directShareRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.enableAnimationsRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.emojiRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.raiseToSpeakRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.sendByEnterRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.saveToGalleryRow = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.distanceRow = i24;
            int i26 = i25 + 1;
            this.rowCount = i26;
            this.settings2Row = i25;
            int i27 = i26 + 1;
            this.rowCount = i27;
            this.stickersRow = i26;
            this.rowCount = i27 + 1;
            this.stickersSection2Row = i27;
        } else {
            this.darkThemes.clear();
            int N2 = Theme.themes.size();
            for (int a2 = 0; a2 < N2; a2++) {
                Theme.ThemeInfo themeInfo2 = Theme.themes.get(a2);
                if (!themeInfo2.isLight() && (themeInfo2.info == null || themeInfo2.info.document != null)) {
                    this.darkThemes.add(themeInfo2);
                }
            }
            int a3 = this.rowCount;
            int i28 = a3 + 1;
            this.rowCount = i28;
            this.nightDisabledRow = a3;
            int i29 = i28 + 1;
            this.rowCount = i29;
            this.nightScheduledRow = i28;
            int i30 = i29 + 1;
            this.rowCount = i30;
            this.nightAutomaticRow = i29;
            this.rowCount = i30 + 1;
            this.nightTypeInfoRow = i30;
            if (Theme.selectedAutoNightType == 1) {
                int i31 = this.rowCount;
                int i32 = i31 + 1;
                this.rowCount = i32;
                this.scheduleHeaderRow = i31;
                this.rowCount = i32 + 1;
                this.scheduleLocationRow = i32;
                if (Theme.autoNightScheduleByLocation) {
                    int i33 = this.rowCount;
                    int i34 = i33 + 1;
                    this.rowCount = i34;
                    this.scheduleUpdateLocationRow = i33;
                    this.rowCount = i34 + 1;
                    this.scheduleLocationInfoRow = i34;
                } else {
                    int i35 = this.rowCount;
                    int i36 = i35 + 1;
                    this.rowCount = i36;
                    this.scheduleFromRow = i35;
                    int i37 = i36 + 1;
                    this.rowCount = i37;
                    this.scheduleToRow = i36;
                    this.rowCount = i37 + 1;
                    this.scheduleFromToInfoRow = i37;
                }
            } else if (Theme.selectedAutoNightType == 2) {
                int i38 = this.rowCount;
                int i39 = i38 + 1;
                this.rowCount = i39;
                this.automaticHeaderRow = i38;
                int i40 = i39 + 1;
                this.rowCount = i40;
                this.automaticBrightnessRow = i39;
                this.rowCount = i40 + 1;
                this.automaticBrightnessInfoRow = i40;
            }
            if (Theme.selectedAutoNightType != 0) {
                int i41 = this.rowCount;
                int i42 = i41 + 1;
                this.rowCount = i42;
                this.preferedHeaderRow = i41;
                this.rowCount = i42 + 1;
                this.themeListRow = i42;
                boolean z2 = Theme.getCurrentNightTheme().accentColorOptions != null;
                this.hasThemeAccents = z2;
                ThemesHorizontalListCell themesHorizontalListCell3 = this.themesHorizontalListCell;
                if (themesHorizontalListCell3 != null) {
                    themesHorizontalListCell3.setDrawDivider(z2);
                }
                if (this.hasThemeAccents) {
                    int i43 = this.rowCount;
                    this.rowCount = i43 + 1;
                    this.themeAccentListRow = i43;
                }
                int i44 = this.rowCount;
                this.rowCount = i44 + 1;
                this.themeInfoRow = i44;
            }
        }
        if (this.listAdapter != null) {
            if (this.currentType == 1 && this.previousUpdatedType != Theme.selectedAutoNightType && (i2 = this.previousUpdatedType) != -1) {
                int start = this.nightTypeInfoRow + 1;
                if (i2 != Theme.selectedAutoNightType) {
                    int a4 = 0;
                    while (a4 < 3) {
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(a4);
                        if (holder != null) {
                            ((ThemeTypeCell) holder.itemView).setTypeChecked(a4 == Theme.selectedAutoNightType);
                        }
                        a4++;
                    }
                    if (Theme.selectedAutoNightType == 0) {
                        this.listAdapter.notifyItemRangeRemoved(start, oldRowCount - start);
                    } else {
                        int i45 = 4;
                        if (Theme.selectedAutoNightType == 1) {
                            int i46 = this.previousUpdatedType;
                            if (i46 == 0) {
                                this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                            } else if (i46 == 2) {
                                this.listAdapter.notifyItemRangeRemoved(start, 3);
                                ListAdapter listAdapter2 = this.listAdapter;
                                if (!Theme.autoNightScheduleByLocation) {
                                    i45 = 5;
                                }
                                listAdapter2.notifyItemRangeInserted(start, i45);
                            }
                        } else if (Theme.selectedAutoNightType == 2) {
                            int i47 = this.previousUpdatedType;
                            if (i47 == 0) {
                                this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                            } else if (i47 == 1) {
                                ListAdapter listAdapter3 = this.listAdapter;
                                if (!Theme.autoNightScheduleByLocation) {
                                    i45 = 5;
                                }
                                listAdapter3.notifyItemRangeRemoved(start, i45);
                                this.listAdapter.notifyItemRangeInserted(start, 3);
                            }
                        }
                    }
                } else if (this.previousByLocation != Theme.autoNightScheduleByLocation) {
                    this.listAdapter.notifyItemRangeRemoved(start + 2, Theme.autoNightScheduleByLocation ? 3 : 2);
                    ListAdapter listAdapter4 = this.listAdapter;
                    int i48 = start + 2;
                    if (!Theme.autoNightScheduleByLocation) {
                        i3 = 3;
                    }
                    listAdapter4.notifyItemRangeInserted(i48, i3);
                }
            } else if (notify || this.previousUpdatedType == -1) {
                ThemesHorizontalListCell themesHorizontalListCell4 = this.themesHorizontalListCell;
                if (themesHorizontalListCell4 != null) {
                    themesHorizontalListCell4.notifyDataSetChanged(this.listView.getWidth());
                }
                this.listAdapter.notifyDataSetChanged();
            } else if (prevThemeAccentListRow == -1 && (i = this.themeAccentListRow) != -1) {
                this.listAdapter.notifyItemInserted(i);
            } else if (prevThemeAccentListRow == -1 || this.themeAccentListRow != -1) {
                int i49 = this.themeAccentListRow;
                if (i49 != -1) {
                    this.listAdapter.notifyItemChanged(i49);
                }
            } else {
                this.listAdapter.notifyItemRemoved(prevThemeAccentListRow);
            }
        }
        if (this.currentType == 1) {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeListUpdated);
        if (this.currentType == 0) {
            Theme.loadRemoteThemes(this.currentAccount, true);
            Theme.checkCurrentRemoteTheme(true);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        stopLocationUpdate();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeListUpdated);
        Theme.saveAutoNightThemeConfig();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.locationPermissionGranted) {
            updateSunTime((Location) null, true);
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
        } else if (id == NotificationCenter.themeListUpdated) {
            updateRows(true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", R.string.ChatSettings));
            ActionBarMenuItem item = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_other);
            item.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            item.addSubItem(1, (int) R.drawable.menu_palette, (CharSequence) LocaleController.getString("CreateNewThemeMenu", R.string.CreateNewThemeMenu));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ThemeActivity.this.finishFragment();
                } else if (id == 1 && ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
                    builder.setMessage(LocaleController.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    builder.setPositiveButton(LocaleController.getString("CreateTheme", R.string.CreateTheme), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.AnonymousClass1.this.lambda$onItemClick$0$ThemeActivity$1(dialogInterface, i);
                        }
                    });
                    ThemeActivity.this.showDialog(builder.create());
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ThemeActivity$1(DialogInterface dialog, int which) {
                ThemeActivity.this.openThemeCreate();
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                ThemeActivity.this.lambda$createView$4$ThemeActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$4$ThemeActivity(View view, int position, float x, float y) {
        int currentMinute;
        int currentHour;
        String str;
        int i;
        if (position == this.enableAnimationsRow) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean animations = preferences.getBoolean("view_animations", true);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("view_animations", !animations);
            editor.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!animations);
                return;
            }
            return;
        }
        boolean enabled = false;
        if (position == this.backgroundRow) {
            presentFragment(new WallpapersListActivity(0));
        } else if (position == this.sendByEnterRow) {
            SharedPreferences preferences2 = MessagesController.getGlobalMainSettings();
            boolean send = preferences2.getBoolean("send_by_enter", false);
            SharedPreferences.Editor editor2 = preferences2.edit();
            editor2.putBoolean("send_by_enter", !send);
            editor2.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!send);
            }
        } else if (position == this.raiseToSpeakRow) {
            SharedConfig.toogleRaiseToSpeak();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
            }
        } else if (position == this.saveToGalleryRow) {
            SharedConfig.toggleSaveToGallery();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
            }
        } else if (position == this.distanceRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("DistanceUnitsTitle", R.string.DistanceUnitsTitle));
                builder.setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic), LocaleController.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers), LocaleController.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles)}, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.this.lambda$null$1$ThemeActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        } else if (position == this.customTabsRow) {
            SharedConfig.toggleCustomTabs();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
            }
        } else if (position == this.directShareRow) {
            SharedConfig.toggleDirectShare();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.directShare);
            }
        } else if (position != this.contactsReimportRow) {
            if (position == this.contactsSortRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                    builder2.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                    builder2.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new DialogInterface.OnClickListener(position) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.this.lambda$null$2$ThemeActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    showDialog(builder2.create());
                }
            } else if (position == this.stickersRow) {
                presentFragment(new StickersActivity(0));
            } else if (position == this.emojiRow) {
                SharedConfig.toggleBigEmoji();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.allowBigEmoji);
                }
            } else if (position == this.nightThemeRow) {
                if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    presentFragment(new ThemeActivity(1));
                    return;
                }
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                if (Theme.selectedAutoNightType == 0) {
                    Theme.selectedAutoNightType = 2;
                    checkCell.setChecked(true);
                } else {
                    Theme.selectedAutoNightType = 0;
                    checkCell.setChecked(false);
                }
                Theme.saveAutoNightThemeConfig();
                Theme.checkAutoNightThemeConditions();
                if (Theme.selectedAutoNightType != 0) {
                    enabled = true;
                }
                String value = enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
                if (enabled) {
                    if (Theme.selectedAutoNightType == 1) {
                        i = R.string.AutoNightScheduled;
                        str = "AutoNightScheduled";
                    } else {
                        i = R.string.AutoNightAdaptive;
                        str = "AutoNightAdaptive";
                    }
                    value = LocaleController.getString(str, i) + " " + value;
                }
                checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), value, enabled, true);
            } else if (position == this.nightDisabledRow) {
                Theme.selectedAutoNightType = 0;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.nightScheduledRow) {
                Theme.selectedAutoNightType = 1;
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime((Location) null, true);
                }
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.nightAutomaticRow) {
                Theme.selectedAutoNightType = 2;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.scheduleLocationRow) {
                Theme.autoNightScheduleByLocation = !Theme.autoNightScheduleByLocation;
                ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
                updateRows(true);
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime((Location) null, true);
                }
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.scheduleFromRow || position == this.scheduleToRow) {
                if (getParentActivity() != null) {
                    if (position == this.scheduleFromRow) {
                        currentHour = Theme.autoNightDayStartTime / 60;
                        currentMinute = Theme.autoNightDayStartTime - (currentHour * 60);
                    } else {
                        currentHour = Theme.autoNightDayEndTime / 60;
                        currentMinute = Theme.autoNightDayEndTime - (currentHour * 60);
                    }
                    showDialog(new TimePickerDialog(getParentActivity(), new TimePickerDialog.OnTimeSetListener(position, (TextSettingsCell) view) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ TextSettingsCell f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                            ThemeActivity.this.lambda$null$3$ThemeActivity(this.f$1, this.f$2, timePicker, i, i2);
                        }
                    }, currentHour, currentMinute, true));
                }
            } else if (position == this.scheduleUpdateLocationRow) {
                updateSunTime((Location) null, true);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ThemeActivity(DialogInterface dialog, int which) {
        SharedConfig.setDistanceSystemType(which);
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (holder != null) {
            this.listAdapter.onBindViewHolder(holder, this.distanceRow);
        }
    }

    public /* synthetic */ void lambda$null$2$ThemeActivity(int position, DialogInterface dialog, int which) {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("sortContactsBy", which);
        editor.commit();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(position);
        }
    }

    public /* synthetic */ void lambda$null$3$ThemeActivity(int position, TextSettingsCell cell, TimePicker view1, int hourOfDay, int minute) {
        int time = (hourOfDay * 60) + minute;
        if (position == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = time;
            cell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", new Object[]{Integer.valueOf(hourOfDay), Integer.valueOf(minute)}), true);
            return;
        }
        Theme.autoNightDayEndTime = time;
        cell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", new Object[]{Integer.valueOf(hourOfDay), Integer.valueOf(minute)}), true);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            updateRows(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* access modifiers changed from: private */
    public void openThemeCreate() {
        EditTextBoldCursor editText = new EditTextBoldCursor(getParentActivity());
        editText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("Create", R.string.Create), $$Lambda$ThemeActivity$K7Ftaj4WmASecpR6QlaryD9pqOg.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView message = new TextView(getParentActivity());
        message.setText(LocaleController.formatString("EnterThemeName", R.string.EnterThemeName, new Object[0]));
        message.setTextSize(16.0f);
        message.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        linearLayout.addView(message, LayoutHelper.createLinear(-1, -2));
        editText.setTextSize(1, 16.0f);
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setInputType(16385);
        editText.setGravity(51);
        editText.setSingleLine(true);
        editText.setImeOptions(6);
        editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setCursorSize(AndroidUtilities.dp(20.0f));
        editText.setCursorWidth(1.5f);
        editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        linearLayout.addView(editText, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        editText.setOnEditorActionListener($$Lambda$ThemeActivity$CtIAv0mVCQoiAull5_xinkqAxkY.INSTANCE);
        editText.setText(generateThemeName());
        editText.setSelection(editText.length());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        ThemeActivity.lambda$null$7(EditTextBoldCursor.this);
                    }
                });
            }
        });
        showDialog(alertDialog);
        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener(editText, alertDialog) {
            private final /* synthetic */ EditTextBoldCursor f$1;
            private final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                ThemeActivity.this.lambda$openThemeCreate$9$ThemeActivity(this.f$1, this.f$2, view);
            }
        });
    }

    static /* synthetic */ void lambda$openThemeCreate$5(DialogInterface dialog, int which) {
    }

    static /* synthetic */ void lambda$null$7(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    public /* synthetic */ void lambda$openThemeCreate$9$ThemeActivity(EditTextBoldCursor editText, AlertDialog alertDialog, View v) {
        if (editText.length() == 0) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(editText, 2.0f, 0);
            return;
        }
        new ThemeEditorView().show(getParentActivity(), Theme.createNewTheme(editText.getText().toString()));
        updateRows(true);
        alertDialog.dismiss();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!preferences.getBoolean("themehint", false)) {
            preferences.edit().putBoolean("themehint", true).commit();
            ToastUtils.show((int) R.string.CreateNewThemeHelp);
        }
    }

    /* access modifiers changed from: private */
    public void updateSunTime(Location lastKnownLocation, boolean forceUpdate) {
        Activity activity;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
            if (getParentActivity() != null) {
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlert", R.string.GpsDisabledAlert));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ThemeActivity.this.lambda$updateSunTime$10$ThemeActivity(dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                            return;
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else {
                    return;
                }
            }
            try {
                lastKnownLocation = locationManager.getLastKnownLocation("gps");
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation("network");
                }
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation("passive");
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (lastKnownLocation == null || forceUpdate) {
                startLocationUpdate();
                if (lastKnownLocation == null) {
                    return;
                }
            }
            Theme.autoNightLocationLatitude = lastKnownLocation.getLatitude();
            Theme.autoNightLocationLongitude = lastKnownLocation.getLongitude();
            int[] time = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
            Theme.autoNightSunriseTime = time[0];
            Theme.autoNightSunsetTime = time[1];
            Theme.autoNightCityName = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            Theme.autoNightLastSunCheckDay = calendar.get(5);
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    ThemeActivity.this.lambda$updateSunTime$12$ThemeActivity();
                }
            });
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
            if (holder != null && (holder.itemView instanceof TextInfoPrivacyCell)) {
                ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
            }
            if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
                Theme.checkAutoNightThemeConditions();
                return;
            }
            return;
        }
        activity.requestPermissions(new String[]{PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"}, 2);
    }

    public /* synthetic */ void lambda$updateSunTime$10$ThemeActivity(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
            }
        }
    }

    public /* synthetic */ void lambda$updateSunTime$12$ThemeActivity() {
        String name;
        try {
            List<Address> addresses = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (addresses.size() > 0) {
                name = addresses.get(0).getLocality();
            } else {
                name = null;
            }
        } catch (Exception e) {
            name = null;
        }
        AndroidUtilities.runOnUIThread(new Runnable(name) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemeActivity.this.lambda$null$11$ThemeActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$11$ThemeActivity(String nameFinal) {
        RecyclerListView.Holder holder;
        Theme.autoNightCityName = nameFinal;
        if (Theme.autoNightCityName == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", new Object[]{Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude)});
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && (holder = (RecyclerListView.Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow)) != null && (holder.itemView instanceof TextSettingsCell)) {
            ((TextSettingsCell) holder.itemView).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
        }
    }

    private void startLocationUpdate() {
        if (!this.updatingLocation) {
            this.updatingLocation = true;
            LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            try {
                locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    private void showPermissionAlert(boolean byButton) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (byButton) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", R.string.PermissionNoLocationPosition));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", R.string.PermissionNoLocation));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ThemeActivity.this.lambda$showPermissionAlert$13$ThemeActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$13$ThemeActivity(DialogInterface dialog, int which) {
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

    /* access modifiers changed from: private */
    public String getLocationSunString() {
        int currentHour = Theme.autoNightSunriseTime / 60;
        String sunriseTimeStr = String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour), Integer.valueOf(Theme.autoNightSunriseTime - (currentHour * 60))});
        int currentHour2 = Theme.autoNightSunsetTime / 60;
        return LocaleController.formatString("AutoNightUpdateLocationInfo", R.string.AutoNightUpdateLocationInfo, String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour2), Integer.valueOf(Theme.autoNightSunsetTime - (currentHour2 * 60))}), sunriseTimeStr);
    }

    private static class InnerAccentView extends View {
        private ObjectAnimator checkAnimator;
        private float checkedState;
        private int currentColor;
        private Theme.ThemeInfo currentTheme;
        private final Paint paint = new Paint(1);

        InnerAccentView(Context context) {
            super(context);
        }

        /* access modifiers changed from: package-private */
        public void setThemeAndColor(Theme.ThemeInfo themeInfo, int color) {
            this.currentTheme = themeInfo;
            this.currentColor = color;
            updateCheckedState(false);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean animate) {
            boolean checked = this.currentTheme.accentColor == this.currentColor;
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (animate) {
                float[] fArr = new float[1];
                if (!checked) {
                    f = 0.0f;
                }
                fArr[0] = f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                this.checkAnimator = ofFloat;
                ofFloat.setDuration(200);
                this.checkAnimator.start();
                return;
            }
            if (!checked) {
                f = 0.0f;
            }
            setCheckedState(f);
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            invalidate();
        }

        public float getCheckedState() {
            return this.checkedState;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float radius = (float) AndroidUtilities.dp(20.0f);
            this.paint.setColor(this.currentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(((float) getMeasuredWidth()) * 0.5f, ((float) getMeasuredHeight()) * 0.5f, radius - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(((float) getMeasuredWidth()) * 0.5f, ((float) getMeasuredHeight()) * 0.5f, radius - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), this.paint);
        }
    }

    private static class InnerCustomAccentView extends View {
        private int[] colors = new int[7];
        private final Paint paint = new Paint(1);

        InnerCustomAccentView(Context context) {
            super(context);
        }

        /* access modifiers changed from: private */
        public void setTheme(Theme.ThemeInfo themeInfo) {
            int[] options = themeInfo == null ? null : themeInfo.accentColorOptions;
            if (options == null || options.length < 8) {
                this.colors = new int[7];
            } else {
                this.colors = new int[]{options[6], options[4], options[7], options[2], options[0], options[5], options[3]};
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float centerX = ((float) getMeasuredWidth()) * 0.5f;
            float centerY = ((float) getMeasuredHeight()) * 0.5f;
            float radSmall = (float) AndroidUtilities.dp(5.0f);
            float radRing = ((float) AndroidUtilities.dp(20.0f)) - radSmall;
            this.paint.setStyle(Paint.Style.FILL);
            this.paint.setColor(this.colors[0]);
            canvas.drawCircle(centerX, centerY, radSmall, this.paint);
            double angle = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            for (int a = 0; a < 6; a++) {
                this.paint.setColor(this.colors[a + 1]);
                canvas.drawCircle((((float) Math.sin(angle)) * radRing) + centerX, centerY - (((float) Math.cos(angle)) * radRing), radSmall, this.paint);
                angle += 1.0471975511965976d;
            }
        }
    }

    private class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {
        private Theme.ThemeInfo currentTheme;
        private int extraColor;
        private boolean hasExtraColor;
        private Context mContext;
        private int[] options;

        ThemeAccentsListAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            Theme.ThemeInfo currentNightTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.currentTheme = currentNightTheme;
            int[] iArr = currentNightTheme.accentColorOptions;
            this.options = iArr;
            if (iArr != null && ArrayUtils.indexOf(iArr, this.currentTheme.accentColor) == -1) {
                this.extraColor = this.currentTheme.accentColor;
                this.hasExtraColor = true;
            }
            super.notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public long getItemId(int position) {
            return (long) getAccentColor(position);
        }

        public int getItemViewType(int position) {
            return position == getItemCount() - 1 ? 1 : 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType != 0) {
                return new RecyclerListView.Holder(new InnerCustomAccentView(this.mContext));
            }
            return new RecyclerListView.Holder(new InnerAccentView(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            if (itemViewType == 0) {
                ((InnerAccentView) holder.itemView).setThemeAndColor(this.currentTheme, getAccentColor(position));
            } else if (itemViewType == 1) {
                ((InnerCustomAccentView) holder.itemView).setTheme(this.currentTheme);
            }
        }

        public int getItemCount() {
            int[] iArr = this.options;
            if (iArr == null) {
                return 0;
            }
            return iArr.length + (this.hasExtraColor ? 1 : 0) + 1;
        }

        /* access modifiers changed from: package-private */
        public int getAccentColor(int pos) {
            int[] iArr = this.options;
            if (iArr == null) {
                return 0;
            }
            if (this.hasExtraColor && pos == iArr.length) {
                return this.extraColor;
            }
            int[] iArr2 = this.options;
            if (pos < iArr2.length) {
                return iArr2[pos];
            }
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int findCurrentAccent() {
            if (!this.hasExtraColor || this.extraColor != this.currentTheme.accentColor) {
                return ArrayUtils.indexOf(this.options, this.currentTheme.accentColor);
            }
            return this.options.length;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private boolean first = true;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 1 || type == 4 || type == 7 || type == 10 || type == 11 || type == 12;
        }

        /* access modifiers changed from: private */
        public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
            int[] icons;
            CharSequence[] items;
            boolean hasDelete;
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            if ((themeInfo.info == null || themeInfo.themeLoaded) && ThemeActivity.this.currentType != 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                String str = null;
                if (themeInfo.pathToFile == null) {
                    hasDelete = false;
                    items = new CharSequence[]{null, LocaleController.getString("ExportTheme", R.string.ExportTheme)};
                    icons = new int[]{0, R.drawable.msg_shareout};
                } else {
                    hasDelete = themeInfo.info == null || !themeInfo.info.isDefault;
                    CharSequence[] charSequenceArr = new CharSequence[5];
                    charSequenceArr[0] = themeInfo.info != null ? LocaleController.getString("ShareFile", R.string.ShareFile) : null;
                    charSequenceArr[1] = LocaleController.getString("ExportTheme", R.string.ExportTheme);
                    charSequenceArr[2] = (themeInfo.info == null || (!themeInfo.info.isDefault && themeInfo.info.creator)) ? LocaleController.getString("Edit", R.string.Edit) : null;
                    charSequenceArr[3] = (themeInfo.info == null || !themeInfo.info.creator) ? null : LocaleController.getString("ThemeSetUrl", R.string.ThemeSetUrl);
                    if (hasDelete) {
                        str = LocaleController.getString("Delete", R.string.Delete);
                    }
                    charSequenceArr[4] = str;
                    items = charSequenceArr;
                    icons = new int[]{R.drawable.msg_share, R.drawable.msg_shareout, R.drawable.msg_edit, R.drawable.msg_link, R.drawable.msg_delete};
                }
                builder.setItems(items, icons, new DialogInterface.OnClickListener(themeInfo) {
                    private final /* synthetic */ Theme.ThemeInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.ListAdapter.this.lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(this.f$1, dialogInterface, i);
                    }
                });
                AlertDialog alertDialog = builder.create();
                ThemeActivity.this.showDialog(alertDialog);
                if (hasDelete) {
                    alertDialog.setItemColor(alertDialog.getItemsCount() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
                }
            }
        }

        public /* synthetic */ void lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(Theme.ThemeInfo themeInfo, DialogInterface dialog, int which) {
            File currentFile;
            if (ThemeActivity.this.getParentActivity() != null) {
                if (which == 0) {
                    String link = "https://" + MessagesController.getInstance(ThemeActivity.this.currentAccount).linkPrefix + "/addtheme/" + themeInfo.info.slug;
                    ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), (ArrayList<MessageObject>) null, link, false, link, false));
                } else if (which == 1) {
                    if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
                        StringBuilder result = new StringBuilder();
                        for (Map.Entry<String, Integer> entry : Theme.getDefaultColors().entrySet()) {
                            result.append(entry.getKey());
                            result.append("=");
                            result.append(entry.getValue());
                            result.append("\n");
                        }
                        currentFile = new File(ApplicationLoader.getFilesDirFixed(), "default_theme.attheme");
                        FileOutputStream stream = null;
                        try {
                            stream = new FileOutputStream(currentFile);
                            stream.write(AndroidUtilities.getStringBytes(result.toString()));
                            try {
                                stream.close();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (Throwable th) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Exception e3) {
                                    FileLog.e((Throwable) e3);
                                }
                            }
                            throw th;
                        }
                    } else {
                        currentFile = themeInfo.assetName != null ? Theme.getAssetFile(themeInfo.assetName) : new File(themeInfo.pathToFile);
                    }
                    File finalFile = new File(FileLoader.getDirectory(4), FileLoader.fixFileName(currentFile.getName()));
                    try {
                        if (AndroidUtilities.copyFile(currentFile, finalFile)) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/xml");
                            if (Build.VERSION.SDK_INT >= 24) {
                                try {
                                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ThemeActivity.this.getParentActivity(), "im.bclpbkiauv.messenger.provider", finalFile));
                                    intent.setFlags(1);
                                } catch (Exception e4) {
                                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(finalFile));
                                }
                            } else {
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(finalFile));
                            }
                            ThemeActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                } else if (which == 2) {
                    if (ThemeActivity.this.parentLayout != null) {
                        Theme.applyTheme(themeInfo);
                        ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
                        new ThemeEditorView().show(ThemeActivity.this.getParentActivity(), themeInfo);
                    }
                } else if (which == 3) {
                    ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeInfo, false));
                } else if (ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    builder1.setMessage(LocaleController.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
                    builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder1.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener(themeInfo) {
                        private final /* synthetic */ Theme.ThemeInfo f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.ListAdapter.this.lambda$null$0$ThemeActivity$ListAdapter(this.f$1, dialogInterface, i);
                        }
                    });
                    builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    ThemeActivity.this.showDialog(builder1.create());
                }
            }
        }

        public /* synthetic */ void lambda$null$0$ThemeActivity$ListAdapter(Theme.ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            ThemeActivity.this.getMessagesController().saveTheme(themeInfo, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerListView view;
            switch (viewType) {
                case 1:
                    View view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    View view3 = new ThemeTypeCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 5:
                    View view4 = new HeaderCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 6:
                    View view5 = new BrightnessControlCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void didChangedValue(float value) {
                            int oldValue = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int newValue = (int) (value * 100.0f);
                            Theme.autoNightBrighnessThreshold = value;
                            if (oldValue != newValue) {
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    view5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view5;
                    break;
                case 7:
                    View view6 = new TextCheckCell(this.mContext);
                    view6.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view6;
                    break;
                case 8:
                    View view7 = new TextSizeCell(this.mContext);
                    view7.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view7;
                    break;
                case 9:
                    View view8 = new ChatListCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void didSelectChatType(boolean threeLines) {
                            SharedConfig.setUseThreeLinesLayout(threeLines);
                        }
                    };
                    view8.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view8;
                    break;
                case 10:
                    View view9 = new NotificationsCheckCell(this.mContext, 21, 64);
                    view9.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view9;
                    break;
                case 11:
                    this.first = true;
                    ThemesHorizontalListCell unused = ThemeActivity.this.themesHorizontalListCell = new ThemesHorizontalListCell(this.mContext, ThemeActivity.this.currentType, ThemeActivity.this.defaultThemes, ThemeActivity.this.darkThemes) {
                        /* access modifiers changed from: protected */
                        public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
                            ThemeActivity.this.listAdapter.showOptionsForTheme(themeInfo);
                        }

                        /* access modifiers changed from: protected */
                        public void presentFragment(BaseFragment fragment) {
                            ThemeActivity.this.presentFragment(fragment);
                        }

                        /* access modifiers changed from: protected */
                        public void updateRows() {
                            ThemeActivity.this.updateRows(false);
                        }
                    };
                    ThemeActivity.this.themesHorizontalListCell.setDrawDivider(ThemeActivity.this.hasThemeAccents);
                    view = ThemeActivity.this.themesHorizontalListCell;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(148.0f)));
                    break;
                default:
                    RecyclerListView accentsListView = new TintRecyclerListView(this.mContext) {
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (!(getParent() == null || getParent().getParent() == null)) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onInterceptTouchEvent(e);
                        }
                    };
                    accentsListView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    accentsListView.setItemAnimator((RecyclerView.ItemAnimator) null);
                    accentsListView.setLayoutAnimation((LayoutAnimationController) null);
                    accentsListView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
                    accentsListView.setClipToPadding(false);
                    LinearLayoutManager accentsLayoutManager = new LinearLayoutManager(this.mContext);
                    accentsLayoutManager.setOrientation(0);
                    accentsListView.setLayoutManager(accentsLayoutManager);
                    ThemeAccentsListAdapter accentsAdapter = new ThemeAccentsListAdapter(this.mContext);
                    accentsListView.setAdapter(accentsAdapter);
                    accentsListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(accentsAdapter, accentsListView) {
                        private final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$1;
                        private final /* synthetic */ RecyclerListView f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onItemClick(View view, int i) {
                            ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(this.f$1, this.f$2, view, i);
                        }
                    });
                    RecyclerListView recyclerListView = accentsListView;
                    recyclerListView.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(62.0f)));
                    view = recyclerListView;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(ThemeAccentsListAdapter accentsAdapter, RecyclerListView accentsListView, View view1, int position) {
            Theme.ThemeInfo currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            if (position == accentsAdapter.getItemCount() - 1) {
                ThemeActivity themeActivity = ThemeActivity.this;
                themeActivity.presentFragment(new ThemePreviewActivity(currentTheme, false, 1, themeActivity.currentType == 1));
            } else {
                int newAccent = accentsAdapter.getAccentColor(position);
                if (currentTheme.accentColor != newAccent) {
                    Theme.saveThemeAccent(currentTheme, newAccent);
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[2];
                    objArr[0] = currentTheme;
                    objArr[1] = Boolean.valueOf(ThemeActivity.this.currentType == 1);
                    globalInstance.postNotificationName(i, objArr);
                }
            }
            int left = view1.getLeft();
            int right = view1.getRight();
            int extra = AndroidUtilities.dp(52.0f);
            if (left - extra < 0) {
                accentsListView.smoothScrollBy(left - extra, 0);
            } else if (right + extra > accentsListView.getMeasuredWidth()) {
                accentsListView.smoothScrollBy((right + extra) - accentsListView.getMeasuredWidth(), 0);
            }
            int count = accentsListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = accentsListView.getChildAt(a);
                if (child instanceof InnerAccentView) {
                    ((InnerAccentView) child).updateCheckedState(true);
                }
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 1:
                    TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                    if (position == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                            cell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff), false);
                            return;
                        } else {
                            cell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), Theme.getCurrentNightThemeName(), false);
                            return;
                        }
                    } else if (position == ThemeActivity.this.scheduleFromRow) {
                        int currentHour = Theme.autoNightDayStartTime / 60;
                        cell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour), Integer.valueOf(Theme.autoNightDayStartTime - (currentHour * 60))}), true);
                        return;
                    } else if (position == ThemeActivity.this.scheduleToRow) {
                        int currentHour2 = Theme.autoNightDayEndTime / 60;
                        cell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour2), Integer.valueOf(Theme.autoNightDayEndTime - (currentHour2 * 60))}), false);
                        return;
                    } else if (position == ThemeActivity.this.scheduleUpdateLocationRow) {
                        cell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
                        return;
                    } else if (position == ThemeActivity.this.contactsSortRow) {
                        int sort = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                        if (sort == 0) {
                            value2 = LocaleController.getString("Default", R.string.Default);
                        } else if (sort == 1) {
                            value2 = LocaleController.getString("FirstName", R.string.SortFirstName);
                        } else {
                            value2 = LocaleController.getString("LastName", R.string.SortLastName);
                        }
                        cell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), value2, true);
                        return;
                    } else if (position == ThemeActivity.this.backgroundRow) {
                        cell.setText(LocaleController.getString("ChangeChatBackground", R.string.ChangeChatBackground), false);
                        return;
                    } else if (position == ThemeActivity.this.contactsReimportRow) {
                        cell.setText(LocaleController.getString("ImportContacts", R.string.ImportContacts), true);
                        return;
                    } else if (position == ThemeActivity.this.stickersRow) {
                        cell.setText(LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), false);
                        return;
                    } else if (position == ThemeActivity.this.distanceRow) {
                        if (SharedConfig.distanceSystemType == 0) {
                            value = LocaleController.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic);
                        } else if (SharedConfig.distanceSystemType == 1) {
                            value = LocaleController.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers);
                        } else {
                            value = LocaleController.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles);
                        }
                        cell.setTextAndValue(LocaleController.getString("DistanceUnits", R.string.DistanceUnits), value, false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                    if (position == ThemeActivity.this.automaticBrightnessInfoRow) {
                        cell2.setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                        return;
                    } else if (position == ThemeActivity.this.scheduleLocationInfoRow) {
                        cell2.setText(ThemeActivity.this.getLocationSunString());
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (position == ThemeActivity.this.stickersSection2Row || ((position == ThemeActivity.this.nightTypeInfoRow && ThemeActivity.this.themeInfoRow == -1) || (position == ThemeActivity.this.themeInfoRow && ThemeActivity.this.nightTypeInfoRow != -1))) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 4:
                    ThemeTypeCell typeCell = (ThemeTypeCell) holder.itemView;
                    if (position == ThemeActivity.this.nightDisabledRow) {
                        String string = LocaleController.getString("AutoNightDisabled", R.string.AutoNightDisabled);
                        if (Theme.selectedAutoNightType == 0) {
                            z = true;
                        }
                        typeCell.setValue(string, z, true);
                        return;
                    } else if (position == ThemeActivity.this.nightScheduledRow) {
                        String string2 = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                        if (Theme.selectedAutoNightType == 1) {
                            z = true;
                        }
                        typeCell.setValue(string2, z, true);
                        return;
                    } else if (position == ThemeActivity.this.nightAutomaticRow) {
                        String string3 = LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
                        if (Theme.selectedAutoNightType != 2) {
                            z2 = false;
                        }
                        typeCell.setValue(string3, z2, false);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ThemeActivity.this.scheduleHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightSchedule", R.string.AutoNightSchedule));
                        return;
                    } else if (position == ThemeActivity.this.automaticHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightBrightness", R.string.AutoNightBrightness));
                        return;
                    } else if (position == ThemeActivity.this.preferedHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightPreferred", R.string.AutoNightPreferred));
                        return;
                    } else if (position == ThemeActivity.this.settingsRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    } else if (position == ThemeActivity.this.themeHeaderRow) {
                        headerCell.setText(LocaleController.getString("ColorTheme", R.string.ColorTheme));
                        return;
                    } else if (position == ThemeActivity.this.textSizeHeaderRow) {
                        headerCell.setText(LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader));
                        return;
                    } else if (position == ThemeActivity.this.chatListHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChatList", R.string.ChatList));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    ((BrightnessControlCell) holder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position == ThemeActivity.this.scheduleLocationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", R.string.AutoNightLocation), Theme.autoNightScheduleByLocation, true);
                        return;
                    } else if (position == ThemeActivity.this.enableAnimationsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", R.string.EnableAnimations), MessagesController.getGlobalMainSettings().getBoolean("view_animations", true), true);
                        return;
                    } else if (position == ThemeActivity.this.sendByEnterRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", R.string.SendByEnter), MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false), true);
                        return;
                    } else if (position == ThemeActivity.this.saveToGalleryRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), SharedConfig.saveToGallery, true);
                        return;
                    } else if (position == ThemeActivity.this.raiseToSpeakRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                        return;
                    } else if (position == ThemeActivity.this.customTabsRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                        return;
                    } else if (position == ThemeActivity.this.directShareRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", R.string.DirectShare), LocaleController.getString("DirectShareInfo", R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                        return;
                    } else if (position == ThemeActivity.this.emojiRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("LargeEmoji", R.string.LargeEmoji), SharedConfig.allowBigEmoji, true);
                        return;
                    } else {
                        return;
                    }
                case 10:
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) holder.itemView;
                    if (position == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType != 0) {
                            z = true;
                        }
                        boolean enabled = z;
                        String value3 = enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
                        if (enabled) {
                            value3 = (Theme.selectedAutoNightType == 1 ? LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled) : LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive)) + " " + value3;
                        }
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), value3, enabled, true);
                        return;
                    }
                    return;
                case 11:
                    if (this.first) {
                        ThemeActivity.this.themesHorizontalListCell.scrollToCurrentTheme(ThemeActivity.this.listView.getMeasuredWidth(), false);
                        this.first = false;
                        return;
                    }
                    return;
                case 12:
                    RecyclerListView accentsList = (RecyclerListView) holder.itemView;
                    ThemeAccentsListAdapter adapter = (ThemeAccentsListAdapter) accentsList.getAdapter();
                    adapter.notifyDataSetChanged();
                    int pos = adapter.findCurrentAccent();
                    if (pos == -1) {
                        pos = adapter.getItemCount() - 1;
                    }
                    if (pos != -1) {
                        ((LinearLayoutManager) accentsList.getLayoutManager()).scrollToPositionWithOffset(pos, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 4) {
                ((ThemeTypeCell) holder.itemView).setTypeChecked(holder.getAdapterPosition() == Theme.selectedAutoNightType);
            }
            if (type != 2 && type != 3) {
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        }

        public int getItemViewType(int position) {
            if (position == ThemeActivity.this.scheduleFromRow || position == ThemeActivity.this.distanceRow || position == ThemeActivity.this.scheduleToRow || position == ThemeActivity.this.scheduleUpdateLocationRow || position == ThemeActivity.this.backgroundRow || position == ThemeActivity.this.contactsReimportRow || position == ThemeActivity.this.contactsSortRow || position == ThemeActivity.this.stickersRow) {
                return 1;
            }
            if (position == ThemeActivity.this.automaticBrightnessInfoRow || position == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (position == ThemeActivity.this.themeInfoRow || position == ThemeActivity.this.nightTypeInfoRow || position == ThemeActivity.this.scheduleFromToInfoRow || position == ThemeActivity.this.stickersSection2Row || position == ThemeActivity.this.settings2Row || position == ThemeActivity.this.newThemeInfoRow || position == ThemeActivity.this.chatListInfoRow) {
                return 3;
            }
            if (position == ThemeActivity.this.nightDisabledRow || position == ThemeActivity.this.nightScheduledRow || position == ThemeActivity.this.nightAutomaticRow) {
                return 4;
            }
            if (position == ThemeActivity.this.scheduleHeaderRow || position == ThemeActivity.this.automaticHeaderRow || position == ThemeActivity.this.preferedHeaderRow || position == ThemeActivity.this.settingsRow || position == ThemeActivity.this.themeHeaderRow || position == ThemeActivity.this.textSizeHeaderRow || position == ThemeActivity.this.chatListHeaderRow) {
                return 5;
            }
            if (position == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (position == ThemeActivity.this.scheduleLocationRow || position == ThemeActivity.this.enableAnimationsRow || position == ThemeActivity.this.sendByEnterRow || position == ThemeActivity.this.saveToGalleryRow || position == ThemeActivity.this.raiseToSpeakRow || position == ThemeActivity.this.customTabsRow || position == ThemeActivity.this.directShareRow || position == ThemeActivity.this.emojiRow) {
                return 7;
            }
            if (position == ThemeActivity.this.textSizeRow) {
                return 8;
            }
            if (position == ThemeActivity.this.chatListRow) {
                return 9;
            }
            if (position == ThemeActivity.this.nightThemeRow) {
                return 10;
            }
            if (position == ThemeActivity.this.themeListRow) {
                return 11;
            }
            if (position == ThemeActivity.this.themeAccentListRow) {
                return 12;
            }
            return 1;
        }
    }

    private static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    private String generateThemeName() {
        int color;
        List<String> adjectives = Arrays.asList(new String[]{"Ancient", "Antique", "Autumn", "Baby", "Barely", "Baroque", "Blazing", "Blushing", "Bohemian", "Bubbly", "Burning", "Buttered", "Classic", "Clear", "Cool", "Cosmic", "Cotton", "Cozy", "Crystal", "Dark", "Daring", "Darling", "Dawn", "Dazzling", "Deep", "Deepest", "Delicate", "Delightful", "Divine", "Double", "Downtown", "Dreamy", "Dusky", "Dusty", "Electric", "Enchanted", "Endless", "Evening", "Fantastic", "Flirty", "Forever", "Frigid", "Frosty", "Frozen", "Gentle", "Heavenly", "Hyper", "Icy", "Infinite", "Innocent", "Instant", "Luscious", "Lunar", "Lustrous", "Magic", "Majestic", "Mambo", "Midnight", "Millenium", "Morning", "Mystic", "Natural", "Neon", "Night", "Opaque", "Paradise", "Perfect", "Perky", "Polished", "Powerful", "Rich", "Royal", "Sheer", "Simply", "Sizzling", "Solar", "Sparkling", "Splendid", "Spicy", "Spring", "Stellar", "Sugared", "Summer", "Sunny", "Super", "Sweet", "Tender", "Tenacious", "Tidal", "Toasted", "Totally", "Tranquil", "Tropical", "True", "Twilight", "Twinkling", "Ultimate", "Ultra", "Velvety", "Vibrant", "Vintage", "Virtual", "Warm", "Warmest", "Whipped", "Wild", "Winsome"});
        List<String> subjectives = Arrays.asList(new String[]{"Ambrosia", "Attack", "Avalanche", "Blast", "Bliss", "Blossom", "Blush", "Burst", "Butter", "Candy", "Carnival", "Charm", "Chiffon", "Cloud", "Comet", "Delight", "Dream", "Dust", "Fantasy", "Flame", ExifInterface.TAG_FLASH, "Fire", "Freeze", "Frost", "Glade", "Glaze", "Gleam", "Glimmer", "Glitter", "Glow", "Grande", "Haze", "Highlight", "Ice", "Illusion", "Intrigue", "Jewel", "Jubilee", "Kiss", "Lights", "Lollypop", "Love", "Luster", "Madness", "Matte", "Mirage", "Mist", "Moon", "Muse", "Myth", "Nectar", "Nova", "Parfait", "Passion", "Pop", "Rain", "Reflection", "Rhapsody", "Romance", "Satin", "Sensation", "Silk", "Shine", "Shadow", "Shimmer", "Sky", "Spice", "Star", "Sugar", "Sunrise", "Sunset", "Sun", "Twist", "Unbound", "Velvet", "Vibrant", "Waters", "Wine", "Wink", "Wonder", "Zone"});
        HashMap<Integer, String> colors = new HashMap<>();
        colors.put(9306112, "Berry");
        colors.put(14598550, "Brandy");
        colors.put(8391495, "Cherry");
        colors.put(16744272, "Coral");
        colors.put(14372985, "Cranberry");
        colors.put(14423100, "Crimson");
        colors.put(14725375, "Mauve");
        colors.put(16761035, "Pink");
        colors.put(16711680, "Red");
        colors.put(16711807, "Rose");
        colors.put(8406555, "Russet");
        colors.put(16720896, "Scarlet");
        colors.put(15856113, "Seashell");
        colors.put(16724889, "Strawberry");
        colors.put(16760576, "Amber");
        colors.put(15438707, "Apricot");
        colors.put(16508850, "Banana");
        colors.put(10601738, "Citrus");
        colors.put(11560192, "Ginger");
        colors.put(16766720, "Gold");
        colors.put(16640272, "Lemon");
        colors.put(16753920, "Orange");
        colors.put(16770484, "Peach");
        colors.put(16739155, "Persimmon");
        colors.put(14996514, "Sunflower");
        colors.put(15893760, "Tangerine");
        colors.put(16763004, "Topaz");
        colors.put(16776960, "Yellow");
        colors.put(3688720, "Clover");
        colors.put(8628829, "Cucumber");
        colors.put(5294200, "Emerald");
        colors.put(11907932, "Olive");
        colors.put(Integer.valueOf(MotionEventCompat.ACTION_POINTER_INDEX_MASK), "Green");
        colors.put(43115, "Jade");
        colors.put(2730887, "Jungle");
        colors.put(12582656, "Lime");
        colors.put(776785, "Malachite");
        colors.put(10026904, "Mint");
        colors.put(11394989, "Moss");
        colors.put(3234721, "Azure");
        colors.put(255, "Blue");
        colors.put(18347, "Cobalt");
        colors.put(5204422, "Indigo");
        colors.put(96647, "Lagoon");
        colors.put(7461346, "Aquamarine");
        colors.put(1182351, "Ultramarine");
        colors.put(128, "Navy");
        colors.put(3101086, "Sapphire");
        colors.put(7788522, "Sky");
        colors.put(32896, "Teal");
        colors.put(4251856, "Turquoise");
        colors.put(10053324, "Amethyst");
        colors.put(5046581, "Blackberry");
        colors.put(6373457, "Eggplant");
        colors.put(13148872, "Lilac");
        colors.put(11894492, "Lavender");
        colors.put(13421823, "Periwinkle");
        colors.put(8663417, "Plum");
        colors.put(6684825, "Purple");
        colors.put(14204888, "Thistle");
        colors.put(14315734, "Orchid");
        colors.put(2361920, "Violet");
        colors.put(4137225, "Bronze");
        colors.put(3604994, "Chocolate");
        colors.put(8077056, "Cinnamon");
        colors.put(3153694, "Cocoa");
        colors.put(7365973, "Coffee");
        colors.put(7956873, "Rum");
        colors.put(5113350, "Mahogany");
        colors.put(7875865, "Mocha");
        colors.put(12759680, "Sand");
        colors.put(8924439, "Sienna");
        colors.put(7864585, "Maple");
        colors.put(15787660, "Khaki");
        colors.put(12088115, "Copper");
        colors.put(12144200, "Chestnut");
        colors.put(15653316, "Almond");
        colors.put(16776656, "Cream");
        colors.put(12186367, "Diamond");
        colors.put(11109127, "Honey");
        colors.put(16777200, "Ivory");
        colors.put(15392968, "Pearl");
        colors.put(15725299, "Porcelain");
        colors.put(13745832, "Vanilla");
        colors.put(Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK), "White");
        colors.put(8421504, "Gray");
        colors.put(0, "Black");
        colors.put(15266260, "Chrome");
        colors.put(3556687, "Charcoal");
        colors.put(789277, "Ebony");
        colors.put(12632256, "Silver");
        colors.put(16119285, "Smoke");
        colors.put(2499381, "Steel");
        colors.put(5220413, "Apple");
        colors.put(8434628, "Glacier");
        colors.put(16693933, "Melon");
        colors.put(12929932, "Mulberry");
        colors.put(11126466, "Opal");
        colors.put(5547512, "Blue");
        Theme.ThemeInfo themeInfo = Theme.getCurrentTheme();
        if (themeInfo.accentColor != 0) {
            color = themeInfo.accentColor;
        } else {
            color = AndroidUtilities.calcDrawableColor(Theme.getCachedWallpaper())[0];
        }
        String minKey = null;
        int minValue = Integer.MAX_VALUE;
        int r1 = Color.red(color);
        int g1 = Color.green(color);
        int b1 = Color.blue(color);
        for (Map.Entry<Integer, String> entry : colors.entrySet()) {
            Integer value = entry.getKey();
            int r2 = Color.red(value.intValue());
            HashMap<Integer, String> colors2 = colors;
            int rMean = (r1 + r2) / 2;
            int r = r1 - r2;
            int g = g1 - Color.green(value.intValue());
            int b = b1 - Color.blue(value.intValue());
            int color2 = color;
            Theme.ThemeInfo themeInfo2 = themeInfo;
            int d = ((((rMean + 512) * r) * r) >> 8) + (g * 4 * g) + ((((767 - rMean) * b) * b) >> 8);
            if (d < minValue) {
                minValue = d;
                minKey = entry.getValue();
            }
            colors = colors2;
            color = color2;
            themeInfo = themeInfo2;
        }
        int i = color;
        Theme.ThemeInfo themeInfo3 = themeInfo;
        if (Utilities.random.nextInt() % 2 == 0) {
            return adjectives.get(Utilities.random.nextInt(adjectives.size())) + " " + minKey;
        }
        return minKey + " " + subjectives.get(Utilities.random.nextInt(subjectives.size()));
    }

    public ThemeDescription[] getThemeDescriptions() {
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        RecyclerListView recyclerListView5 = this.listView;
        RecyclerListView recyclerListView6 = recyclerListView5;
        RecyclerListView recyclerListView7 = this.listView;
        RecyclerListView recyclerListView8 = recyclerListView7;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_profile_actionIcon), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_profile_actionIcon), new ThemeDescription((View) this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_player_progressBackground), new ThemeDescription((View) recyclerListView6, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_player_progress), new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_addedIcon), new ThemeDescription((View) recyclerListView8, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_player_progress), new ThemeDescription((View) this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_player_progressBackground), new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground), new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubble), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleShadow), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubble), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleShadow), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextIn), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextOut), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheck), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckRead), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckReadSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaSentCheck), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyLine), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyLine), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyNameText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyNameText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMessageText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMessageText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMediaMessageSelectedText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMediaMessageSelectedText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeSelectedText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeSelectedText)};
    }
}
