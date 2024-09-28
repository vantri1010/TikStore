package im.bclpbkiauv.ui.hui.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.LanguageCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sliding.SlidingLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class MryLanguageSelectActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int item_done = 1;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private Context mContext;
    private SlidingLayout root;
    private ListAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> searchResult;
    /* access modifiers changed from: private */
    public Timer searchTimer;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public LocaleController.LocaleInfo selectedLanguage;
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

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        return (MrySearchView) this.fragmentView.findViewById(R.id.searchview);
    }

    public View createView(Context context) {
        this.mContext = context;
        this.searching = false;
        this.searchWas = false;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_list_search_frame_layout, (ViewGroup) null, false);
        this.selectedLanguage = LocaleController.getInstance().getCurrentLocaleInfo();
        initActionBar();
        super.createView(context);
        SlidingLayout slidingLayout = (SlidingLayout) this.fragmentView;
        this.root = slidingLayout;
        slidingLayout.setFollowView(this.searchView);
        initList();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("LanguageSetting", R.string.LanguageSetting));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    MryLanguageSelectActivity.this.finishFragment();
                } else if (id == MryLanguageSelectActivity.this.item_done) {
                    if (MryLanguageSelectActivity.this.selectedLanguage != null) {
                        LocaleController.getInstance().applyLanguage(MryLanguageSelectActivity.this.selectedLanguage, true, false, false, true, MryLanguageSelectActivity.this.currentAccount);
                        MryLanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
                    }
                    MryLanguageSelectActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItem(this.item_done, (CharSequence) LocaleController.getString("Done", R.string.Done));
    }

    /* access modifiers changed from: protected */
    public void initSearchView() {
        super.initSearchView();
        this.searchView.setHintText(LocaleController.getString("Search", R.string.Search));
    }

    private void initList() {
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = (RecyclerListView) this.fragmentView.findViewById(R.id.listview);
        EmptyTextProgressView emptyTextProgressView = (EmptyTextProgressView) this.fragmentView.findViewById(R.id.emptyTextProgress);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listAdapter = new ListAdapter(this.mContext, false);
        this.searchListViewAdapter = new ListAdapter(this.mContext, true);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                MryLanguageSelectActivity.this.lambda$initList$0$MryLanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return MryLanguageSelectActivity.this.lambda$initList$2$MryLanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && MryLanguageSelectActivity.this.searching && MryLanguageSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(MryLanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
    }

    public /* synthetic */ void lambda$initList$0$MryLanguageSelectActivity(View view, int position) {
        if (view instanceof LanguageCell) {
            this.selectedLanguage = ((LanguageCell) view).getCurrentLocale();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public /* synthetic */ boolean lambda$initList$2$MryLanguageSelectActivity(View view, int position) {
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
                MryLanguageSelectActivity.this.lambda$null$1$MryLanguageSelectActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$1$MryLanguageSelectActivity(LocaleController.LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
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

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
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
                return MryLanguageSelectActivity.lambda$fillLanguages$3(LocaleController.LocaleInfo.this, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
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
                    MryLanguageSelectActivity.this.searchTimer.cancel();
                    Timer unused = MryLanguageSelectActivity.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                MryLanguageSelectActivity.this.processSearch(query);
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
                MryLanguageSelectActivity.this.lambda$processSearch$4$MryLanguageSelectActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processSearch$4$MryLanguageSelectActivity(String query) {
        if (query.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList());
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList<LocaleController.LocaleInfo> resultArray = new ArrayList<>();
        int N = this.unofficialLanguages.size();
        for (int a = 0; a < N; a++) {
            LocaleController.LocaleInfo c = this.unofficialLanguages.get(a);
            if (c.name.toLowerCase().contains(query) || c.nameEnglish.toLowerCase().contains(query)) {
                resultArray.add(c);
            }
        }
        int N2 = this.sortedLanguages.size();
        for (int a2 = 0; a2 < N2; a2++) {
            LocaleController.LocaleInfo c2 = this.sortedLanguages.get(a2);
            if ((c2.name.toLowerCase().contains(query) || c2.nameEnglish.toLowerCase().contains(query)) && !resultArray.contains(c2)) {
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
                MryLanguageSelectActivity.this.lambda$updateSearchResults$5$MryLanguageSelectActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$5$MryLanguageSelectActivity(ArrayList arrCounties) {
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
                int count = MryLanguageSelectActivity.this.sortedLanguages.size();
                if (count != 0) {
                    count += 2;
                }
                if (!MryLanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                    return count + MryLanguageSelectActivity.this.unofficialLanguages.size() + 1;
                }
                return count;
            } else if (MryLanguageSelectActivity.this.searchResult == null || MryLanguageSelectActivity.this.searchResult.isEmpty()) {
                return 0;
            } else {
                return MryLanguageSelectActivity.this.searchResult.size();
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new View(this.mContext);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                layoutParams.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
                layoutParams.height = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            } else {
                view = new LanguageCell(this.mContext, false);
                RecyclerView.LayoutParams layoutParams2 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams2.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams2.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams2);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean last;
            LocaleController.LocaleInfo localeInfo;
            if (holder.getItemViewType() == 0) {
                LanguageCell textSettingsCell = (LanguageCell) holder.itemView;
                boolean z = false;
                if (this.search) {
                    localeInfo = (LocaleController.LocaleInfo) MryLanguageSelectActivity.this.searchResult.get(position);
                    last = position == MryLanguageSelectActivity.this.searchResult.size() - 1;
                } else if (MryLanguageSelectActivity.this.unofficialLanguages.isEmpty() || position < 0 || position >= MryLanguageSelectActivity.this.unofficialLanguages.size()) {
                    if (!MryLanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                        position -= MryLanguageSelectActivity.this.unofficialLanguages.size() + 1;
                    }
                    localeInfo = (LocaleController.LocaleInfo) MryLanguageSelectActivity.this.sortedLanguages.get(position == 0 ? 0 : position - 1);
                    last = position == MryLanguageSelectActivity.this.sortedLanguages.size();
                } else {
                    localeInfo = (LocaleController.LocaleInfo) MryLanguageSelectActivity.this.unofficialLanguages.get(position);
                    last = position == MryLanguageSelectActivity.this.unofficialLanguages.size() - 1;
                }
                if (localeInfo.isLocal()) {
                    textSettingsCell.setLanguage(localeInfo, String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", R.string.LanguageCustom)}), !last && position != 0);
                } else {
                    textSettingsCell.setLanguage(localeInfo, (String) null, (this.search || position != 0) && !last);
                }
                if (localeInfo == MryLanguageSelectActivity.this.selectedLanguage) {
                    z = true;
                }
                textSettingsCell.setLanguageSelected(z);
                if ((this.search && getItemCount() == 1) || (!this.search && (localeInfo == LocaleController.getInstance().getCurrentLocaleInfo() || getItemCount() == 3))) {
                    textSettingsCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if ((this.search && position == 0) || (!this.search && position == 2)) {
                    textSettingsCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (last) {
                    textSettingsCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            }
        }

        public int getItemViewType(int i) {
            if (this.search) {
                return 0;
            }
            if ((MryLanguageSelectActivity.this.unofficialLanguages.isEmpty() || (i != MryLanguageSelectActivity.this.unofficialLanguages.size() && i != MryLanguageSelectActivity.this.unofficialLanguages.size() + MryLanguageSelectActivity.this.sortedLanguages.size() + 1)) && (!MryLanguageSelectActivity.this.unofficialLanguages.isEmpty() || (i != 1 && i != MryLanguageSelectActivity.this.sortedLanguages.size() + 1))) {
                return 0;
            }
            return 1;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_addedIcon)};
    }

    public void onStart(boolean focus) {
    }

    public void onSearchExpand() {
        this.searching = true;
    }

    public void onSearchCollapse() {
        search((String) null);
        this.searching = false;
        this.searchWas = false;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.setAdapter(this.listAdapter);
        }
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.setVisibility(8);
        }
    }

    public void onTextChange(String text) {
        search(text);
        if (text.length() != 0) {
            this.searchWas = true;
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.setAdapter(this.searchListViewAdapter);
                return;
            }
            return;
        }
        onSearchCollapse();
    }
}
