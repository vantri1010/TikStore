package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.LanguageCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class LanguageSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> searchResult;
    /* access modifiers changed from: private */
    public Timer searchTimer;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> sortedLanguages;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> unofficialLanguages;

    public boolean onFragmentCreate() {
        fillLanguages();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", R.string.Language));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LanguageSelectActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = LanguageSelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                LanguageSelectActivity.this.search((String) null);
                boolean unused = LanguageSelectActivity.this.searching = false;
                boolean unused2 = LanguageSelectActivity.this.searchWas = false;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.emptyView.setVisibility(8);
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
                }
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                LanguageSelectActivity.this.search(text);
                if (text.length() != 0) {
                    boolean unused = LanguageSelectActivity.this.searchWas = true;
                    if (LanguageSelectActivity.this.listView != null) {
                        LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                    }
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.listAdapter = new ListAdapter(context, false);
        this.searchListViewAdapter = new ListAdapter(context, true);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                LanguageSelectActivity.this.lambda$createView$0$LanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return LanguageSelectActivity.this.lambda$createView$2$LanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$LanguageSelectActivity(View view, int position) {
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof LanguageCell)) {
            LocaleController.LocaleInfo localeInfo = ((LanguageCell) view).getCurrentLocale();
            if (localeInfo != null) {
                LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, this.currentAccount);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            }
            finishFragment();
        }
    }

    public /* synthetic */ boolean lambda$createView$2$LanguageSelectActivity(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof LanguageCell) || (localeInfo = ((LanguageCell) view).getCurrentLocale()) == null || localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("DeleteLocalization", R.string.DeleteLocalization));
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener(localeInfo) {
            private final /* synthetic */ LocaleController.LocaleInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                LanguageSelectActivity.this.lambda$null$1$LanguageSelectActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$1$LanguageSelectActivity(LocaleController.LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
        if (LocaleController.getInstance().deleteLanguage(finalLocaleInfo, this.currentAccount)) {
            fillLanguages();
            ArrayList<LocaleController.LocaleInfo> arrayList = this.searchResult;
            if (arrayList != null) {
                arrayList.remove(finalLocaleInfo);
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
            ListAdapter listAdapter3 = this.searchListViewAdapter;
            if (listAdapter3 != null) {
                listAdapter3.notifyDataSetChanged();
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack && this.listAdapter != null) {
            fillLanguages();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void fillLanguages() {
        Comparator<LocaleController.LocaleInfo> comparator = new Comparator() {
            public final int compare(Object obj, Object obj2) {
                return LanguageSelectActivity.lambda$fillLanguages$3(LocaleController.LocaleInfo.this, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
            }
        };
        this.sortedLanguages = new ArrayList<>();
        this.unofficialLanguages = new ArrayList<>(LocaleController.getInstance().unofficialLanguages);
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int a = 0; a < size; a++) {
            LocaleController.LocaleInfo info = arrayList.get(a);
            if (info.serverIndex != Integer.MAX_VALUE) {
                this.sortedLanguages.add(info);
            } else {
                this.unofficialLanguages.add(info);
            }
        }
        Collections.sort(this.sortedLanguages, comparator);
        Collections.sort(this.unofficialLanguages, comparator);
    }

    static /* synthetic */ int lambda$fillLanguages$3(LocaleController.LocaleInfo currentLocale, LocaleController.LocaleInfo o, LocaleController.LocaleInfo o2) {
        if (o == currentLocale) {
            return -1;
        }
        if (o2 == currentLocale) {
            return 1;
        }
        if (o.serverIndex == o2.serverIndex) {
            return o.name.compareTo(o2.name);
        }
        if (o.serverIndex > o2.serverIndex) {
            return 1;
        }
        if (o.serverIndex < o2.serverIndex) {
            return -1;
        }
        return 0;
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void search(final String query) {
        if (query == null) {
            this.searchResult = null;
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Timer timer = new Timer();
        this.searchTimer = timer;
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    LanguageSelectActivity.this.searchTimer.cancel();
                    Timer unused = LanguageSelectActivity.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                LanguageSelectActivity.this.processSearch(query);
            }
        }, 100, 300);
    }

    /* access modifiers changed from: private */
    public void processSearch(String query) {
        Utilities.searchQueue.postRunnable(new Runnable(query) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LanguageSelectActivity.this.lambda$processSearch$4$LanguageSelectActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processSearch$4$LanguageSelectActivity(String query) {
        if (query.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList());
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList<LocaleController.LocaleInfo> resultArray = new ArrayList<>();
        int N = this.unofficialLanguages.size();
        for (int a = 0; a < N; a++) {
            LocaleController.LocaleInfo c = this.unofficialLanguages.get(a);
            if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c);
            }
        }
        int N2 = this.sortedLanguages.size();
        for (int a2 = 0; a2 < N2; a2++) {
            LocaleController.LocaleInfo c2 = this.sortedLanguages.get(a2);
            if (c2.name.toLowerCase().startsWith(query) || c2.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c2);
            }
        }
        updateSearchResults(resultArray);
    }

    private void updateSearchResults(ArrayList<LocaleController.LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new Runnable(arrCounties) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LanguageSelectActivity.this.lambda$updateSearchResults$5$LanguageSelectActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$5$LanguageSelectActivity(ArrayList arrCounties) {
        this.searchResult = arrCounties;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            this.mContext = context;
            this.search = isSearch;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (!this.search) {
                int count = LanguageSelectActivity.this.sortedLanguages.size();
                if (count != 0) {
                    count++;
                }
                if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                    return count + LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                }
                return count;
            } else if (LanguageSelectActivity.this.searchResult == null) {
                return 0;
            } else {
                return LanguageSelectActivity.this.searchResult.size();
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new ShadowSectionCell(this.mContext);
            } else {
                view = new LanguageCell(this.mContext, false);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean last;
            LocaleController.LocaleInfo localeInfo;
            int itemViewType = holder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                LanguageCell textSettingsCell = (LanguageCell) holder.itemView;
                if (this.search) {
                    localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                    last = position == LanguageSelectActivity.this.searchResult.size() - 1;
                } else if (LanguageSelectActivity.this.unofficialLanguages.isEmpty() || position < 0 || position >= LanguageSelectActivity.this.unofficialLanguages.size()) {
                    if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                        position -= LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                    }
                    localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(position);
                    last = position == LanguageSelectActivity.this.sortedLanguages.size() - 1;
                } else {
                    localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.unofficialLanguages.get(position);
                    last = position == LanguageSelectActivity.this.unofficialLanguages.size() - 1;
                }
                if (localeInfo.isLocal()) {
                    textSettingsCell.setLanguage(localeInfo, String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", R.string.LanguageCustom)}), !last);
                } else {
                    textSettingsCell.setLanguage(localeInfo, (String) null, !last);
                }
                if (localeInfo != LocaleController.getInstance().getCurrentLocaleInfo()) {
                    z = false;
                }
                textSettingsCell.setLanguageSelected(z);
            } else if (itemViewType == 1) {
                ShadowSectionCell sectionCell = (ShadowSectionCell) holder.itemView;
                if (LanguageSelectActivity.this.unofficialLanguages.isEmpty() || position != LanguageSelectActivity.this.unofficialLanguages.size()) {
                    sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                } else {
                    sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                }
            }
        }

        public int getItemViewType(int i) {
            if ((LanguageSelectActivity.this.unofficialLanguages.isEmpty() || (i != LanguageSelectActivity.this.unofficialLanguages.size() && i != LanguageSelectActivity.this.unofficialLanguages.size() + LanguageSelectActivity.this.sortedLanguages.size() + 1)) && (!LanguageSelectActivity.this.unofficialLanguages.isEmpty() || i != LanguageSelectActivity.this.sortedLanguages.size())) {
                return 0;
            }
            return 1;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_addedIcon)};
    }
}
