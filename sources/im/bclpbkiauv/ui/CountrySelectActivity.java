package im.bclpbkiauv.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.CountrySelectActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.LetterSectionCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.decoration.StickyDecoration;
import im.bclpbkiauv.ui.decoration.listener.GroupListener;
import im.bclpbkiauv.ui.hui.CharacterParser;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import org.slf4j.Marker;

public class CountrySelectActivity extends BaseSearchViewFragment {
    private CountrySelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public CountryAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean needChangeChar;
    /* access modifiers changed from: private */
    public boolean needPhoneCode;
    private CountrySearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public SideBar sideBar;

    public static class Country {
        public String code;
        public String name;
        public String phoneFormat;
        public String shortname;
    }

    public interface CountrySelectActivityDelegate {
        void didSelectCountry(Country country);
    }

    public CountrySelectActivity(boolean phoneCode) {
        this.needPhoneCode = phoneCode;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.setOnScrollListener((RecyclerView.OnScrollListener) null);
        }
        super.onFragmentDestroy();
    }

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        this.searchView = new MrySearchView(getParentActivity());
        int margin = AndroidUtilities.dp(10.0f);
        ((FrameLayout) this.fragmentView).addView(this.searchView, LayoutHelper.createFrame(-1, 35, margin, margin, margin, margin));
        return this.searchView;
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        initActionBar();
        super.createView(context);
        initList(frameLayout, context);
        initSideBar(frameLayout, context);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CountrySelectActivity.this.finishFragment();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void initSearchView() {
        super.initSearchView();
        this.searchView.setHintText(LocaleController.getString("Search", R.string.Search));
    }

    private void initList(FrameLayout frameLayout, Context context) {
        CountryAdapter countryAdapter = new CountryAdapter(this, context);
        this.listViewAdapter = countryAdapter;
        this.searchListViewAdapter = new CountrySearchAdapter(context, countryAdapter.getCountries());
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        int i = 1;
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 0, AndroidUtilities.dp(55.0f), 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setOverScrollMode(2);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.addItemDecoration(StickyDecoration.Builder.init(new GroupListener() {
            public final String getGroupName(int i) {
                return CountrySelectActivity.this.lambda$initList$0$CountrySelectActivity(i);
            }
        }).setGroupBackground(Theme.getColor(Theme.key_windowBackgroundGray)).setGroupTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText)).setGroupTextTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf")).setGroupHeight(AndroidUtilities.dp(24.0f)).setDivideColor(Color.parseColor("#EE96BC")).setGroupTextSize(AndroidUtilities.dp(14.0f)).setTextSideMargin(AndroidUtilities.dp(15.0f)).build());
        this.listView.addItemDecoration(TopBottomDecoration.getDefaultTopBottomCornerBg(0, 10, 7.5f));
        this.listView.setAdapter(this.listViewAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(55.0f), AndroidUtilities.dp(10.0f), 0));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CountrySelectActivity.this.lambda$initList$1$CountrySelectActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && CountrySelectActivity.this.searching && CountrySelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                String s = CountrySelectActivity.this.listViewAdapter.getLetter(firstPosition);
                if (TextUtils.isEmpty(s) && CountrySelectActivity.this.listViewAdapter.getSectionForPosition(firstPosition) == 0) {
                    s = CountrySelectActivity.this.listViewAdapter.getLetter(CountrySelectActivity.this.listViewAdapter.getPositionForSection(1));
                }
                CountrySelectActivity.this.sideBar.setChooseChar(s);
            }
        });
    }

    public /* synthetic */ String lambda$initList$0$CountrySelectActivity(int position) {
        if (this.searchWas) {
            CountrySearchAdapter countrySearchAdapter = this.searchListViewAdapter;
            if (countrySearchAdapter == null || countrySearchAdapter.getItemCount() <= 0) {
                return null;
            }
            String name = this.searchListViewAdapter.getItem(0).name;
            if (TextUtils.isEmpty(name)) {
                return null;
            }
            if (this.needChangeChar) {
                return CharacterParser.getInstance().getSelling(name).substring(0, 1).toUpperCase();
            }
            return name.substring(0, 1).toUpperCase();
        } else if (this.listViewAdapter.getItemCount() <= position || position <= -1) {
            return null;
        } else {
            return this.listViewAdapter.getLetter(position);
        }
    }

    public /* synthetic */ void lambda$initList$1$CountrySelectActivity(View view, int position) {
        Country country;
        CountrySelectActivityDelegate countrySelectActivityDelegate;
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                country = this.listViewAdapter.getItem(section, row);
            } else {
                return;
            }
        } else {
            country = this.searchListViewAdapter.getItem(position);
        }
        if (position >= 0) {
            finishFragment();
            if (country != null && (countrySelectActivityDelegate = this.delegate) != null) {
                countrySelectActivityDelegate.didSelectCountry(country);
            }
        }
    }

    private void initSideBar(FrameLayout frameLayout, Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(50.0f);
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        frameLayout.addView(textView, LayoutHelper.createFrame(100, 100, 17));
        SideBar sideBar2 = new SideBar(context);
        this.sideBar = sideBar2;
        sideBar2.setCharsOnly();
        this.sideBar.setTextView(textView);
        frameLayout.addView(this.sideBar, LayoutHelper.createFrame(35.0f, 420.0f, 21, 0.0f, 56.0f, 0.0f, 56.0f));
        this.sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                CountrySelectActivity.this.lambda$initSideBar$2$CountrySelectActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$2$CountrySelectActivity(String s) {
        int section = 0;
        if (!"#".equals(s)) {
            section = this.listViewAdapter.getSectionForChar(s.charAt(0));
        }
        int position = this.listViewAdapter.getPositionForSection(section);
        if (position != -1) {
            this.listView.getLayoutManager().scrollToPosition(position);
        }
    }

    public void onResume() {
        super.onResume();
        CountryAdapter countryAdapter = this.listViewAdapter;
        if (countryAdapter != null) {
            countryAdapter.notifyDataSetChanged();
        }
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public class CountryAdapter extends RecyclerListView.SectionsAdapter {
        private HashMap<String, ArrayList<Country>> countries = new HashMap<>();
        private Context mContext;
        private ArrayList<String> sortedCountries = new ArrayList<>();
        final /* synthetic */ CountrySelectActivity this$0;

        /* JADX WARNING: Removed duplicated region for block: B:25:0x0068 A[Catch:{ Exception -> 0x0166 }] */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x007b A[Catch:{ Exception -> 0x0166 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public CountryAdapter(im.bclpbkiauv.ui.CountrySelectActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r1 = r18
                r2 = r19
                r1.this$0 = r2
                r18.<init>()
                java.util.HashMap r0 = new java.util.HashMap
                r0.<init>()
                r1.countries = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r1.sortedCountries = r0
                r3 = r20
                r1.mContext = r3
                r0 = 0
                r4 = 2131691590(0x7f0f0846, float:1.9012256E38)
                r5 = 0
                im.bclpbkiauv.messenger.LocaleController r6 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0166 }
                im.bclpbkiauv.messenger.LocaleController$LocaleInfo r6 = r6.getCurrentLocaleInfo()     // Catch:{ Exception -> 0x0166 }
                java.lang.String r7 = "countries.txt"
                r8 = 2
                r9 = 1
                if (r6 == 0) goto L_0x007c
                java.lang.String r10 = r6.nameEnglish     // Catch:{ Exception -> 0x0166 }
                if (r10 == 0) goto L_0x007c
                java.lang.String r10 = r6.nameEnglish     // Catch:{ Exception -> 0x0166 }
                r11 = -1
                int r12 = r10.hashCode()     // Catch:{ Exception -> 0x0166 }
                r13 = -1303729356(0xffffffffb24aab34, float:-1.1796875E-8)
                if (r12 == r13) goto L_0x005d
                r13 = 60895824(0x3a13250, float:9.474281E-37)
                if (r12 == r13) goto L_0x0053
                r13 = 1132825637(0x43858c25, float:267.09488)
                if (r12 == r13) goto L_0x0049
            L_0x0048:
                goto L_0x0066
            L_0x0049:
                java.lang.String r12 = "Simplified Chinese"
                boolean r10 = r10.equals(r12)     // Catch:{ Exception -> 0x0166 }
                if (r10 == 0) goto L_0x0048
                r11 = 1
                goto L_0x0066
            L_0x0053:
                java.lang.String r12 = "English"
                boolean r10 = r10.equals(r12)     // Catch:{ Exception -> 0x0166 }
                if (r10 == 0) goto L_0x0048
                r11 = 0
                goto L_0x0066
            L_0x005d:
                java.lang.String r12 = "Traditional Chinese"
                boolean r10 = r10.equals(r12)     // Catch:{ Exception -> 0x0166 }
                if (r10 == 0) goto L_0x0048
                r11 = 2
            L_0x0066:
                if (r11 == 0) goto L_0x007b
                if (r11 == r9) goto L_0x0074
                if (r11 == r8) goto L_0x006d
                goto L_0x007c
            L_0x006d:
                java.lang.String r10 = "countries_tw.txt"
                r0 = r10
                boolean unused = r2.needChangeChar = r9     // Catch:{ Exception -> 0x0166 }
                goto L_0x007c
            L_0x0074:
                java.lang.String r10 = "countries_cn.txt"
                r0 = r10
                boolean unused = r2.needChangeChar = r9     // Catch:{ Exception -> 0x0166 }
                goto L_0x007c
            L_0x007b:
                r0 = r7
            L_0x007c:
                if (r0 != 0) goto L_0x007f
                r0 = r7
            L_0x007f:
                java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x0166 }
                r7.<init>()     // Catch:{ Exception -> 0x0166 }
                im.bclpbkiauv.ui.CountrySelectActivity$Country r10 = new im.bclpbkiauv.ui.CountrySelectActivity$Country     // Catch:{ Exception -> 0x0166 }
                r10.<init>()     // Catch:{ Exception -> 0x0166 }
                r11 = 2131690586(0x7f0f045a, float:1.901022E38)
                java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r11)     // Catch:{ Exception -> 0x0166 }
                r10.name = r11     // Catch:{ Exception -> 0x0166 }
                java.lang.String r11 = "86"
                r10.code = r11     // Catch:{ Exception -> 0x0166 }
                java.lang.String r11 = "CN"
                r10.shortname = r11     // Catch:{ Exception -> 0x0166 }
                java.lang.String r11 = "XXX XXXX XXXX"
                r10.phoneFormat = r11     // Catch:{ Exception -> 0x0166 }
                r7.add(r10)     // Catch:{ Exception -> 0x0166 }
                im.bclpbkiauv.ui.CountrySelectActivity$Country r11 = new im.bclpbkiauv.ui.CountrySelectActivity$Country     // Catch:{ Exception -> 0x0166 }
                r11.<init>()     // Catch:{ Exception -> 0x0166 }
                r12 = 2131694450(0x7f0f1372, float:1.9018057E38)
                java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r12)     // Catch:{ Exception -> 0x0166 }
                r11.name = r12     // Catch:{ Exception -> 0x0166 }
                java.lang.String r12 = "1"
                r11.code = r12     // Catch:{ Exception -> 0x0166 }
                java.lang.String r12 = "US"
                r11.shortname = r12     // Catch:{ Exception -> 0x0166 }
                java.lang.String r12 = "XXX XXX XXXX"
                r11.phoneFormat = r12     // Catch:{ Exception -> 0x0166 }
                r7.add(r11)     // Catch:{ Exception -> 0x0166 }
                java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.ui.CountrySelectActivity$Country>> r12 = r1.countries     // Catch:{ Exception -> 0x0166 }
                java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r4)     // Catch:{ Exception -> 0x0166 }
                r12.put(r13, r7)     // Catch:{ Exception -> 0x0166 }
                android.content.Context r12 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0166 }
                android.content.res.Resources r12 = r12.getResources()     // Catch:{ Exception -> 0x0166 }
                android.content.res.AssetManager r12 = r12.getAssets()     // Catch:{ Exception -> 0x0166 }
                java.io.InputStream r12 = r12.open(r0)     // Catch:{ Exception -> 0x0166 }
                java.io.BufferedReader r13 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0166 }
                java.io.InputStreamReader r14 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0166 }
                r14.<init>(r12)     // Catch:{ Exception -> 0x0166 }
                r13.<init>(r14)     // Catch:{ Exception -> 0x0166 }
            L_0x00df:
                java.lang.String r14 = r13.readLine()     // Catch:{ Exception -> 0x0166 }
                r15 = r14
                if (r14 == 0) goto L_0x015f
                java.lang.String r14 = ";"
                java.lang.String[] r14 = r15.split(r14)     // Catch:{ Exception -> 0x0166 }
                im.bclpbkiauv.ui.CountrySelectActivity$Country r16 = new im.bclpbkiauv.ui.CountrySelectActivity$Country     // Catch:{ Exception -> 0x0166 }
                r16.<init>()     // Catch:{ Exception -> 0x0166 }
                r17 = r16
                r4 = r14[r5]     // Catch:{ Exception -> 0x0166 }
                r5 = r17
                r5.code = r4     // Catch:{ Exception -> 0x0166 }
                r4 = r14[r9]     // Catch:{ Exception -> 0x0166 }
                r5.shortname = r4     // Catch:{ Exception -> 0x0166 }
                r4 = r14[r8]     // Catch:{ Exception -> 0x0166 }
                r5.name = r4     // Catch:{ Exception -> 0x0166 }
                int r4 = r14.length     // Catch:{ Exception -> 0x0166 }
                r8 = 3
                if (r4 <= r8) goto L_0x0109
                r4 = r14[r8]     // Catch:{ Exception -> 0x0166 }
                r5.phoneFormat = r4     // Catch:{ Exception -> 0x0166 }
            L_0x0109:
                boolean r4 = r19.needChangeChar     // Catch:{ Exception -> 0x0166 }
                if (r4 == 0) goto L_0x012f
                im.bclpbkiauv.ui.hui.CharacterParser r4 = im.bclpbkiauv.ui.hui.CharacterParser.getInstance()     // Catch:{ Exception -> 0x0166 }
                java.lang.String r8 = r5.name     // Catch:{ Exception -> 0x0166 }
                r2 = 0
                java.lang.String r8 = r8.substring(r2, r9)     // Catch:{ Exception -> 0x0166 }
                java.lang.String r2 = r4.getSelling(r8)     // Catch:{ Exception -> 0x0166 }
                java.lang.String r2 = r2.toUpperCase()     // Catch:{ Exception -> 0x0166 }
                int r4 = r2.length()     // Catch:{ Exception -> 0x0166 }
                if (r4 <= r9) goto L_0x013a
                r4 = 0
                java.lang.String r8 = r2.substring(r4, r9)     // Catch:{ Exception -> 0x0166 }
                r2 = r8
                goto L_0x013a
            L_0x012f:
                java.lang.String r2 = r5.name     // Catch:{ Exception -> 0x0166 }
                r4 = 0
                java.lang.String r2 = r2.substring(r4, r9)     // Catch:{ Exception -> 0x0166 }
                java.lang.String r2 = r2.toUpperCase()     // Catch:{ Exception -> 0x0166 }
            L_0x013a:
                java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.ui.CountrySelectActivity$Country>> r4 = r1.countries     // Catch:{ Exception -> 0x0166 }
                java.lang.Object r4 = r4.get(r2)     // Catch:{ Exception -> 0x0166 }
                java.util.ArrayList r4 = (java.util.ArrayList) r4     // Catch:{ Exception -> 0x0166 }
                if (r4 != 0) goto L_0x0154
                java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x0166 }
                r8.<init>()     // Catch:{ Exception -> 0x0166 }
                r4 = r8
                java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.ui.CountrySelectActivity$Country>> r8 = r1.countries     // Catch:{ Exception -> 0x0166 }
                r8.put(r2, r4)     // Catch:{ Exception -> 0x0166 }
                java.util.ArrayList<java.lang.String> r8 = r1.sortedCountries     // Catch:{ Exception -> 0x0166 }
                r8.add(r2)     // Catch:{ Exception -> 0x0166 }
            L_0x0154:
                r4.add(r5)     // Catch:{ Exception -> 0x0166 }
                r2 = r19
                r4 = 2131691590(0x7f0f0846, float:1.9012256E38)
                r5 = 0
                r8 = 2
                goto L_0x00df
            L_0x015f:
                r13.close()     // Catch:{ Exception -> 0x0166 }
                r12.close()     // Catch:{ Exception -> 0x0166 }
                goto L_0x016a
            L_0x0166:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x016a:
                java.util.ArrayList<java.lang.String> r0 = r1.sortedCountries
                im.bclpbkiauv.ui.-$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE r2 = im.bclpbkiauv.ui.$$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE
                java.util.Collections.sort(r0, r2)
                java.util.ArrayList<java.lang.String> r0 = r1.sortedCountries
                r2 = 2131691590(0x7f0f0846, float:1.9012256E38)
                java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2)
                r4 = 0
                r0.add(r4, r2)
                java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.ui.CountrySelectActivity$Country>> r0 = r1.countries
                java.util.Collection r0 = r0.values()
                java.util.Iterator r0 = r0.iterator()
            L_0x0188:
                boolean r2 = r0.hasNext()
                if (r2 == 0) goto L_0x019a
                java.lang.Object r2 = r0.next()
                java.util.ArrayList r2 = (java.util.ArrayList) r2
                im.bclpbkiauv.ui.-$$Lambda$CountrySelectActivity$CountryAdapter$XfoEsVC7VPQbOjyltgjE8VRMASs r4 = im.bclpbkiauv.ui.$$Lambda$CountrySelectActivity$CountryAdapter$XfoEsVC7VPQbOjyltgjE8VRMASs.INSTANCE
                java.util.Collections.sort(r2, r4)
                goto L_0x0188
            L_0x019a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.CountrySelectActivity.CountryAdapter.<init>(im.bclpbkiauv.ui.CountrySelectActivity, android.content.Context):void");
        }

        public HashMap<String, ArrayList<Country>> getCountries() {
            return this.countries;
        }

        public Country getItem(int section, int position) {
            if (section < 0 || section >= this.sortedCountries.size()) {
                return null;
            }
            ArrayList<Country> arr = this.countries.get(this.sortedCountries.get(section));
            if (position < 0 || position >= arr.size()) {
                return null;
            }
            return arr.get(position);
        }

        public boolean isEnabled(int section, int row) {
            return row < this.countries.get(this.sortedCountries.get(section)).size();
        }

        public int getSectionForChar(char section) {
            for (int i = 0; i < getSectionCount() - 1; i++) {
                if (this.sortedCountries.get(i).toUpperCase().charAt(0) == section) {
                    return i;
                }
            }
            return -1;
        }

        public int getPositionForSection(int section) {
            if (section == -1) {
                return -1;
            }
            int positionStart = 0;
            int N = getSectionCount();
            for (int i = 0; i < N; i++) {
                if (i >= section) {
                    return positionStart;
                }
                positionStart += getCountForSection(i);
            }
            return -1;
        }

        public int getSectionCount() {
            return this.sortedCountries.size();
        }

        public int getCountForSection(int section) {
            int count = this.countries.get(this.sortedCountries.get(section)).size();
            if (section != this.sortedCountries.size() - 1) {
                return count + 1;
            }
            return count;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new LetterSectionCell(this.mContext);
                ((LetterSectionCell) view).setCellHeight(AndroidUtilities.dp(48.0f));
            }
            ((LetterSectionCell) view).setLetter(this.sortedCountries.get(section).toUpperCase());
            return view;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new View(this.mContext);
            } else {
                view = new TextSettingsCell(this.mContext);
                view.setPadding(16, 0, 16, 0);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            String str;
            if (holder.getItemViewType() == 0) {
                Country c = this.countries.get(this.sortedCountries.get(section)).get(position);
                TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                String str2 = c.name;
                if (this.this$0.needPhoneCode) {
                    str = Marker.ANY_NON_NULL_MARKER + c.code;
                } else {
                    str = null;
                }
                textSettingsCell.setTextAndValue(str2, str, false);
            }
        }

        public int getItemViewType(int section, int position) {
            return position < this.countries.get(this.sortedCountries.get(section)).size() ? 0 : 1;
        }

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == -1) {
                section = this.sortedCountries.size() - 1;
            }
            return this.sortedCountries.get(section);
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }
    }

    public class CountrySearchAdapter extends RecyclerListView.SelectionAdapter {
        private HashMap<String, ArrayList<Country>> countries;
        private Context mContext;
        private ArrayList<Country> searchResult;
        /* access modifiers changed from: private */
        public Timer searchTimer;

        public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> countries2) {
            this.mContext = context;
            this.countries = countries2;
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
                        CountrySearchAdapter.this.searchTimer.cancel();
                        Timer unused = CountrySearchAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    CountrySearchAdapter.this.processSearch(query.toLowerCase());
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
                    CountrySelectActivity.CountrySearchAdapter.this.lambda$processSearch$0$CountrySelectActivity$CountrySearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$0$CountrySelectActivity$CountrySearchAdapter(String query) {
            if (query.trim().toLowerCase().length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            ArrayList<Country> resultArray = new ArrayList<>();
            boolean isInputEn = Pattern.compile("([a-zA-Z])").matcher(query).matches();
            String n = query.substring(0, 1).toUpperCase();
            if (!isInputEn) {
                n = CharacterParser.getInstance().getSelling(n).toUpperCase();
                if (n.length() > 1) {
                    n = n.substring(0, 1);
                }
            }
            ArrayList<Country> arr = this.countries.get(n);
            if (arr != null) {
                Iterator<Country> it = arr.iterator();
                while (it.hasNext()) {
                    Country c = it.next();
                    if (CountrySelectActivity.this.needChangeChar) {
                        if (!isInputEn) {
                            if (c.name.toLowerCase().startsWith(query)) {
                                resultArray.add(c);
                            }
                        } else if (CharacterParser.getInstance().getSelling(c.name).toLowerCase().startsWith(query)) {
                            resultArray.add(c);
                        }
                    } else if (c.name.toLowerCase().startsWith(query)) {
                        resultArray.add(c);
                    }
                }
            }
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(ArrayList<Country> arrCounties) {
            AndroidUtilities.runOnUIThread(new Runnable(arrCounties) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CountrySelectActivity.CountrySearchAdapter.this.lambda$updateSearchResults$1$CountrySelectActivity$CountrySearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$1$CountrySelectActivity$CountrySearchAdapter(ArrayList arrCounties) {
            this.searchResult = arrCounties;
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            ArrayList<Country> arrayList = this.searchResult;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        public Country getItem(int i) {
            if (i < 0 || i >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new TextSettingsCell(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            Country c = this.searchResult.get(position);
            TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
            String str2 = c.name;
            if (CountrySelectActivity.this.needPhoneCode) {
                str = Marker.ANY_NON_NULL_MARKER + c.code;
            } else {
                str = null;
            }
            boolean z = true;
            if (position == this.searchResult.size() - 1) {
                z = false;
            }
            textSettingsCell.setTextAndValue(str2, str, z);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollActive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollInactive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollText), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4)};
    }

    public void onStart(boolean focus) {
    }

    public void onSearchExpand() {
        this.searching = true;
    }

    public void onSearchCollapse() {
        CountrySearchAdapter countrySearchAdapter = this.searchListViewAdapter;
        if (countrySearchAdapter != null) {
            countrySearchAdapter.search((String) null);
        }
        this.searching = false;
        this.searchWas = false;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.setAdapter(this.listViewAdapter);
            this.listView.setFastScrollVisible(false);
        }
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
        }
    }

    public void onTextChange(String text) {
        this.searchListViewAdapter.search(text);
        if (text.length() != 0) {
            this.searchWas = true;
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.setAdapter(this.searchListViewAdapter);
                this.listView.setFastScrollVisible(false);
            }
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            return;
        }
        onSearchCollapse();
    }

    public void onActionSearch(String trim) {
    }
}
