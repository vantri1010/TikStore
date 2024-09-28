package im.bclpbkiauv.ui;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextDetailSettingsCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hui.login.LoginContronllerActivity;

@Deprecated
public class LogoutActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int addAccountRow;
    /* access modifiers changed from: private */
    public int alternativeHeaderRow;
    /* access modifiers changed from: private */
    public int alternativeSectionRow;
    private AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int cacheRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int logoutRow;
    /* access modifiers changed from: private */
    public int logoutSectionRow;
    /* access modifiers changed from: private */
    public int passcodeRow;
    /* access modifiers changed from: private */
    public int phoneRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int supportRow;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.alternativeHeaderRow = 0;
        if (UserConfig.getActivatedAccountsCount() < 3) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addAccountRow = i;
        } else {
            this.addAccountRow = -1;
        }
        if (SharedConfig.passcodeHash.length() <= 0) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.passcodeRow = i2;
        } else {
            this.passcodeRow = -1;
        }
        int i3 = this.rowCount;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.cacheRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.phoneRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.supportRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.alternativeSectionRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.logoutRow = i7;
        this.rowCount = i8 + 1;
        this.logoutSectionRow = i8;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("LogOutTitle", R.string.LogOutTitle));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LogoutActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                LogoutActivity.this.lambda$createView$1$LogoutActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$LogoutActivity(View view, int position, float x, float y) {
        if (position == this.addAccountRow) {
            int freeAccount = -1;
            int a = 0;
            while (true) {
                if (a >= 3) {
                    break;
                } else if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                } else {
                    a++;
                }
            }
            if (freeAccount >= 0) {
                presentFragment(new LoginContronllerActivity(freeAccount));
            }
        } else if (position == this.passcodeRow) {
            presentFragment(new PasscodeActivity(0));
        } else if (position == this.cacheRow) {
            presentFragment(new CacheControlActivity());
        } else if (position == this.phoneRow) {
            presentFragment(new ActionIntroActivity(3));
        } else if (position == this.supportRow) {
            showDialog(AlertsCreator.createSupportAlert(this));
        } else if (position == this.logoutRow && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.getString("AreYouSureLogout", R.string.AreYouSureLogout));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LogoutActivity.this.lambda$null$0$LogoutActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$0$LogoutActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(1);
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

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return LogoutActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell view = (HeaderCell) holder.itemView;
                if (position == LogoutActivity.this.alternativeHeaderRow) {
                    view.setText(LocaleController.getString("AlternativeOptions", R.string.AlternativeOptions));
                }
            } else if (itemViewType == 1) {
                TextDetailSettingsCell view2 = (TextDetailSettingsCell) holder.itemView;
                if (position == LogoutActivity.this.addAccountRow) {
                    view2.setTextAndValueAndIcon(LocaleController.getString("AddAnotherAccount", R.string.AddAnotherAccount), LocaleController.getString("AddAnotherAccountInfo", R.string.AddAnotherAccountInfo), R.drawable.actions_addmember2, true);
                } else if (position == LogoutActivity.this.passcodeRow) {
                    view2.setTextAndValueAndIcon(LocaleController.getString("SetPasscode", R.string.SetPasscode), LocaleController.getString("SetPasscodeInfo", R.string.SetPasscodeInfo), R.drawable.menu_passcode, true);
                } else if (position == LogoutActivity.this.cacheRow) {
                    view2.setTextAndValueAndIcon(LocaleController.getString("ClearCache", R.string.ClearCache), LocaleController.getString("ClearCacheInfo", R.string.ClearCacheInfo), R.drawable.menu_clearcache, true);
                } else if (position == LogoutActivity.this.phoneRow) {
                    view2.setTextAndValueAndIcon(LocaleController.getString("ChangePhoneNumber", R.string.ChangePhoneNumber), LocaleController.getString("ChangePhoneNumberInfo", R.string.ChangePhoneNumberInfo), R.drawable.menu_newphone, true);
                } else if (position == LogoutActivity.this.supportRow) {
                    view2.setTextAndValueAndIcon(LocaleController.getString("ContactSupport", R.string.ContactSupport), LocaleController.getString("ContactSupportInfo", R.string.ContactSupportInfo), R.drawable.menu_support, false);
                }
            } else if (itemViewType == 3) {
                TextSettingsCell view3 = (TextSettingsCell) holder.itemView;
                if (position == LogoutActivity.this.logoutRow) {
                    view3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                    view3.setText(LocaleController.getString("LogOutTitle", R.string.LogOutTitle), false);
                }
            } else if (itemViewType == 4) {
                TextInfoPrivacyCell view4 = (TextInfoPrivacyCell) holder.itemView;
                if (position == LogoutActivity.this.logoutSectionRow) {
                    view4.setText(LocaleController.getString("LogOutInfo", R.string.LogOutInfo));
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == LogoutActivity.this.addAccountRow || position == LogoutActivity.this.passcodeRow || position == LogoutActivity.this.cacheRow || position == LogoutActivity.this.phoneRow || position == LogoutActivity.this.supportRow || position == LogoutActivity.this.logoutRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new HeaderCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType == 1) {
                TextDetailSettingsCell cell = new TextDetailSettingsCell(this.mContext);
                cell.setMultilineDetail(true);
                cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = cell;
            } else if (viewType == 2) {
                view = new ShadowSectionCell(this.mContext);
            } else if (viewType != 3) {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            } else {
                View view3 = new TextSettingsCell(this.mContext);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == LogoutActivity.this.alternativeHeaderRow) {
                return 0;
            }
            if (position == LogoutActivity.this.addAccountRow || position == LogoutActivity.this.passcodeRow || position == LogoutActivity.this.cacheRow || position == LogoutActivity.this.phoneRow || position == LogoutActivity.this.supportRow) {
                return 1;
            }
            if (position == LogoutActivity.this.alternativeSectionRow) {
                return 2;
            }
            if (position == LogoutActivity.this.logoutRow) {
                return 3;
            }
            return 4;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextDetailSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon)};
    }
}
