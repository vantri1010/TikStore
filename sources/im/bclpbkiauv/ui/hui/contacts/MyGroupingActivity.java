package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hui.adapter.grouping.Artist;
import im.bclpbkiauv.ui.hui.adapter.grouping.Genre;
import im.bclpbkiauv.ui.hui.adapter.grouping.GenreAdapter;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MyGroupingActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int item_mgr = 1;
    private GenreAdapter adapter;
    private int contactsHash;
    private MryEmptyView emptyView;
    /* access modifiers changed from: private */
    public ArrayList<Genre> genres = new ArrayList<>();
    /* access modifiers changed from: private */
    public GroupingMgrActivity groupingMgrActivity;
    private RecyclerView rcvList;
    private MryTextView tvMgrView;

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupingChanged);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupingChanged);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionbar();
        initEmptyView();
        initView();
        initData();
        return this.fragmentView;
    }

    private void initActionbar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("MyGrouping", R.string.MyGrouping));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    MyGroupingActivity.this.finishFragment();
                } else if (id == 1) {
                    GroupingMgrActivity unused = MyGroupingActivity.this.groupingMgrActivity = new GroupingMgrActivity();
                    MyGroupingActivity.this.groupingMgrActivity.setGenres(MyGroupingActivity.this.genres);
                    MyGroupingActivity myGroupingActivity = MyGroupingActivity.this;
                    myGroupingActivity.presentFragment(myGroupingActivity.groupingMgrActivity);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        MryTextView mryTextView = new MryTextView(getParentActivity());
        this.tvMgrView = mryTextView;
        mryTextView.setText(LocaleController.getString("fc_my_manage", R.string.fc_my_manage));
        this.tvMgrView.setTextSize(1, 14.0f);
        this.tvMgrView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.tvMgrView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.tvMgrView.setGravity(16);
        menu.addItemView(1, this.tvMgrView);
    }

    private void initEmptyView() {
        MryEmptyView mryEmptyView = new MryEmptyView(getParentActivity());
        this.emptyView = mryEmptyView;
        mryEmptyView.attach((BaseFragment) this);
        this.emptyView.setEmptyText(LocaleController.getString(R.string.NoGrouping));
        this.emptyView.setEmptyResId(R.mipmap.img_empty_default);
        this.emptyView.setErrorResId(R.mipmap.img_empty_default);
        this.emptyView.setOnEmptyClickListener(new MryEmptyView.OnEmptyOrErrorClickListener() {
            public final boolean onEmptyViewButtonClick(boolean z) {
                return MyGroupingActivity.this.lambda$initEmptyView$0$MyGroupingActivity(z);
            }
        });
    }

    public /* synthetic */ boolean lambda$initEmptyView$0$MyGroupingActivity(boolean isEmptyButton) {
        getContacts();
        return false;
    }

    private void initView() {
        RecyclerView recyclerView = new RecyclerView(getParentActivity());
        this.rcvList = recyclerView;
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        this.rcvList.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        this.rcvList.setOverScrollMode(2);
        RecyclerView recyclerView2 = this.rcvList;
        GenreAdapter genreAdapter = new GenreAdapter(this.genres, this);
        this.adapter = genreAdapter;
        recyclerView2.setAdapter(genreAdapter);
        this.rcvList.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        ((FrameLayout) this.fragmentView).addView(this.rcvList, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 10.0f, 10.0f, 10.0f, 10.0f));
    }

    private void initData() {
        getContacts();
    }

    public void getContacts() {
        this.emptyView.showLoading();
        TLRPCContacts.TL_getContactsV1 req = new TLRPCContacts.TL_getContactsV1();
        req.hash = this.contactsHash;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MyGroupingActivity.this.lambda$getContacts$2$MyGroupingActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getContacts$2$MyGroupingActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MyGroupingActivity.this.lambda$null$1$MyGroupingActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$MyGroupingActivity(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            this.emptyView.showError(error.text);
        } else if (response instanceof TLRPCContacts.TL_contactsV1) {
            TLRPCContacts.TL_contactsV1 contacts = (TLRPCContacts.TL_contactsV1) response;
            this.contactsHash = contacts.hash;
            if (!contacts.users.isEmpty()) {
                Iterator it = contacts.users.iterator();
                while (it.hasNext()) {
                    getMessagesController().putUser((TLRPC.User) it.next(), false);
                }
            }
            if (contacts.group_infos.isEmpty()) {
                this.emptyView.showEmpty();
                return;
            }
            makeGenres(contacts);
            this.emptyView.showContent();
        }
    }

    private void makeGenres(TLRPCContacts.TL_contactsV1 contacts) {
        GenreAdapter genreAdapter = this.adapter;
        if (genreAdapter != null) {
            genreAdapter.storeExpandState();
        }
        this.genres.clear();
        List<TLRPCContacts.TL_contactsGroupInfo> groupInfoList = contacts.group_infos;
        List<TLRPC.Contact> contactsList = contacts.contacts;
        for (TLRPCContacts.TL_contactsGroupInfo groupInfo : groupInfoList) {
            List<Artist> artists = new ArrayList<>();
            for (TLRPC.Contact contact : contactsList) {
                if (contact instanceof TLRPCContacts.TL_contactV1) {
                    TLRPCContacts.TL_contactV1 contactV1 = (TLRPCContacts.TL_contactV1) contact;
                    if (groupInfo.group_id == contactV1.group_id && getMessagesController().getUser(Integer.valueOf(contactV1.user_id)) != null) {
                        artists.add(new Artist(contactV1.user_id));
                    }
                }
            }
            Collections.sort(artists, new Comparator() {
                public final int compare(Object obj, Object obj2) {
                    return MyGroupingActivity.this.lambda$makeGenres$3$MyGroupingActivity((Artist) obj, (Artist) obj2);
                }
            });
            this.genres.add(new Genre(groupInfo, artists));
        }
        Collections.sort(this.genres, $$Lambda$MyGroupingActivity$qsS9uAJ8GqUBR4xFHRMUiRRgsw.INSTANCE);
        GenreAdapter genreAdapter2 = this.adapter;
        if (genreAdapter2 != null) {
            genreAdapter2.restoreExpandState();
            this.adapter.notifyDataSetChanged();
        }
        GroupingMgrActivity groupingMgrActivity2 = this.groupingMgrActivity;
        if (groupingMgrActivity2 != null) {
            groupingMgrActivity2.setGenres(this.genres);
        }
    }

    public /* synthetic */ int lambda$makeGenres$3$MyGroupingActivity(Artist o1, Artist o2) {
        return Integer.compare(getMessagesController().getUser(Integer.valueOf(o2.getUserId())).status.expires, getMessagesController().getUser(Integer.valueOf(o1.getUserId())).status.expires);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.groupingChanged) {
            getContacts();
        }
    }
}
