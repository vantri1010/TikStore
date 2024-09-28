package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.RespTopicBean;
import com.bjz.comm.net.bean.RespTopicTypeBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.utils.RxHelper;
import com.tablayout.SlidingTabLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.decoration.TopDecorationWithSearch;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddTopicActivity extends CommFcActivity implements NotificationCenter.NotificationCenterDelegate {
    private static final String TAG = "AddTopicActivity";
    private long TopicTypeID = 0;
    private AddTopicAdapter addTopicAdapter;
    /* access modifiers changed from: private */
    public List<AddTopicFragment> addTopicFragmentList;
    /* access modifiers changed from: private */
    public HashMap<String, RespTopicBean.Item> cacheSelectedHashmap;
    private FrameLayout container;
    /* access modifiers changed from: private */
    public LinearLayout content_container;
    private MryEmptyView emptyViewDialog;
    /* access modifiers changed from: private */
    public FrameLayout fl_search_container;
    /* access modifiers changed from: private */
    public FrameLayout fl_search_cover;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public MysearchAdapter mysearchAdapter;
    /* access modifiers changed from: private */
    public String query;
    private List<RespTopicBean.Item> respTopicBeans;
    private MrySearchView searchView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private SlidingTabLayout tabLayout;
    /* access modifiers changed from: private */
    public List<RespTopicTypeBean> topicTypes;
    private ViewPager vp_container;

    public AddTopicActivity(HashMap<String, RespTopicBean.Item> cacheSelectedHashmap2) {
        if (cacheSelectedHashmap2 == null) {
            this.cacheSelectedHashmap = new HashMap<>();
        } else {
            this.cacheSelectedHashmap = cacheSelectedHashmap2;
        }
    }

    public boolean onFragmentCreate() {
        this.swipeBackEnabled = false;
        this.addTopicFragmentList = new ArrayList();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.selectedTopicSuccess);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.selectedTopicSuccess);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        notifyTopicSelectChanged();
    }

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_add_topic;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initActionBar();
        initEmptyView();
        initTablayoutAndViewPagber();
        initSearchView();
        initListView();
    }

    private void initEmptyView() {
        MryEmptyView mryEmptyView = new MryEmptyView(getParentActivity());
        this.emptyViewDialog = mryEmptyView;
        mryEmptyView.setEmptyResId(R.mipmap.img_empty_default);
        this.emptyViewDialog.setErrorResId(R.mipmap.img_empty_default);
        this.emptyViewDialog.attach((BaseFragment) this);
        this.emptyViewDialog.setOnEmptyClickListener(new MryEmptyView.OnEmptyOrErrorClickListener() {
            public final boolean onEmptyViewButtonClick(boolean z) {
                return AddTopicActivity.this.lambda$initEmptyView$0$AddTopicActivity(z);
            }
        });
        this.emptyViewDialog.showLoading();
    }

    public /* synthetic */ boolean lambda$initEmptyView$0$AddTopicActivity(boolean isEmptyButton) {
        MryEmptyView mryEmptyView = this.emptyViewDialog;
        if (mryEmptyView != null) {
            mryEmptyView.showLoading();
        }
        getFcTopicList();
        return false;
    }

    private void initTablayoutAndViewPagber() {
        this.tabLayout = (SlidingTabLayout) this.fragmentView.findViewById(R.id.tabLayout);
        this.vp_container = (ViewPager) this.fragmentView.findViewById(R.id.vp_container);
        this.tabLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.tabLayout.setTextSelectColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tabLayout.setTextUnselectColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        ViewPager viewPager = this.vp_container;
        AddTopicAdapter addTopicAdapter2 = new AddTopicAdapter(this.mContext, getParentActivity().getSupportFragmentManager());
        this.addTopicAdapter = addTopicAdapter2;
        viewPager.setAdapter(addTopicAdapter2);
        this.tabLayout.setViewPager(this.vp_container);
    }

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("addtopic", R.string.addtopic));
        this.actionBar.setDelegate(new ActionBar.ActionBarDelegate() {
            public final void onSearchFieldVisibilityChanged(boolean z) {
                AddTopicActivity.this.lambda$initActionBar$1$AddTopicActivity(z);
            }
        });
        MryTextView mryTextView = new MryTextView(this.mContext);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{this.mContext.getResources().getColor(R.color.color_87DFFA), this.mContext.getResources().getColor(R.color.color_2ECEFD)});
        mryTextView.setText(LocaleController.getString("sure", R.string.sure));
        mryTextView.setTextSize(13.0f);
        mryTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        mryTextView.setGravity(17);
        gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(50.0f));
        gradientDrawable.setShape(0);
        mryTextView.setBackground(gradientDrawable);
        FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(63, 30.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(15.0f);
        layoutParams.topMargin = AndroidUtilities.dp(6.0f);
        this.actionBar.createMenu().addItemView(1, mryTextView, layoutParams);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1 || id == 1) {
                    AddTopicActivity.this.getAccountInstance().getNotificationCenter();
                    NotificationCenter.getInstance(AddTopicActivity.this.currentAccount).postNotificationName(NotificationCenter.selectedTopicSuccessToPublish, AddTopicActivity.this.cacheSelectedHashmap);
                    AddTopicActivity.this.finishFragment();
                }
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$1$AddTopicActivity(boolean visible) {
        this.actionBar.getBackButton().setVisibility(visible ? 0 : 8);
    }

    private void initListView() {
        this.container = (FrameLayout) this.fragmentView.findViewById(R.id.container);
        this.content_container = (LinearLayout) this.fragmentView.findViewById(R.id.content_container);
        RecyclerListView recyclerListView = (RecyclerListView) this.fragmentView.findViewById(R.id.listView);
        this.listView = recyclerListView;
        recyclerListView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView.setLayoutManager(new GridLayoutManager(this.mContext, 2));
        this.listView.addItemDecoration(new TopDecorationWithSearch(60, true));
        MysearchAdapter mysearchAdapter2 = new MysearchAdapter(this.mContext);
        this.mysearchAdapter = mysearchAdapter2;
        this.listView.setAdapter(mysearchAdapter2);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (AddTopicActivity.this.searching && AddTopicActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(AddTopicActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int off = recyclerView.computeVerticalScrollOffset();
                if (off == 0) {
                    AddTopicActivity.this.fl_search_container.setBackgroundColor(Theme.getColor(Theme.key_list_decorationBackground));
                    AddTopicActivity.this.fl_search_container.setScrollY(off > AndroidUtilities.dp(55.0f) ? AndroidUtilities.dp(55.0f) : off);
                } else if (off > 0) {
                    AddTopicActivity.this.fl_search_container.setBackgroundColor(0);
                    AddTopicActivity.this.fl_search_container.setScrollY(off > AndroidUtilities.dp(55.0f) ? AndroidUtilities.dp(55.0f) : off);
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AddTopicActivity.this.lambda$initListView$2$AddTopicActivity(view, i);
            }
        });
    }

    public /* synthetic */ void lambda$initListView$2$AddTopicActivity(View view, int position) {
        RespTopicBean.Item selectedtopic = (RespTopicBean.Item) this.mysearchAdapter.getData().get(position);
        String key = selectedtopic.ID + "" + selectedtopic.TypeID;
        if (this.cacheSelectedHashmap.get(key) != null) {
            this.cacheSelectedHashmap.remove(key);
            getAccountInstance().getNotificationCenter();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.selectedTopicSuccess, new Object[0]);
        } else if (this.cacheSelectedHashmap.size() >= 3) {
            FcToastUtils.show((CharSequence) LocaleController.getString("selcetthreetopic", R.string.selcetthreetopic));
        } else {
            this.cacheSelectedHashmap.put(key, selectedtopic);
            getAccountInstance().getNotificationCenter();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.selectedTopicSuccess, new Object[0]);
        }
        this.mysearchAdapter.notifyDataSetChanged();
    }

    private void initSearchView() {
        FrameLayout frameLayout = (FrameLayout) this.fragmentView.findViewById(R.id.fl_search_cover);
        this.fl_search_cover = frameLayout;
        frameLayout.setOnClickListener($$Lambda$AddTopicActivity$neUUk9n8uwwV8hOlTk2v3c9nDCY.INSTANCE);
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView.findViewById(R.id.fl_search_container);
        this.fl_search_container = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_list_decorationBackground));
        MrySearchView mrySearchView = (MrySearchView) this.fragmentView.findViewById(R.id.searchview);
        this.searchView = mrySearchView;
        mrySearchView.setCancelTextColor(this.mContext.getResources().getColor(R.color.color_778591));
        this.searchView.setEditTextBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_divider), (float) AndroidUtilities.dp(50.0f)));
        this.searchView.setHintText(LocaleController.getString("searchTopic", R.string.searchTopic));
        this.searchView.setiSearchViewDelegate(new MrySearchView.ISearchViewDelegate() {
            public void onStart(boolean focus) {
                if (focus) {
                    AddTopicActivity addTopicActivity = AddTopicActivity.this;
                    addTopicActivity.hideTitle(addTopicActivity.fragmentView);
                    AddTopicActivity.this.fl_search_container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    AddTopicActivity.this.fl_search_cover.setVisibility(0);
                    return;
                }
                if (AddTopicActivity.this.actionBar.getVisibility() == 4) {
                    AddTopicActivity addTopicActivity2 = AddTopicActivity.this;
                    addTopicActivity2.showTitle(addTopicActivity2.fragmentView);
                }
                AddTopicActivity.this.fl_search_container.setBackgroundColor(Theme.getColor(Theme.key_list_decorationBackground));
                AddTopicActivity.this.fl_search_cover.setVisibility(8);
            }

            public void onSearchExpand() {
                boolean unused = AddTopicActivity.this.searching = true;
            }

            public boolean canCollapseSearch() {
                boolean unused = AddTopicActivity.this.searching = false;
                boolean unused2 = AddTopicActivity.this.searchWas = false;
                AddTopicActivity.this.listView.setAdapter(AddTopicActivity.this.mysearchAdapter);
                AddTopicActivity.this.mysearchAdapter.notifyDataSetChanged();
                return true;
            }

            public void onSearchCollapse() {
                boolean unused = AddTopicActivity.this.searching = false;
                boolean unused2 = AddTopicActivity.this.searchWas = false;
                AddTopicActivity.this.listView.setVisibility(8);
                AddTopicActivity.this.content_container.setVisibility(0);
            }

            public void onTextChange(String text) {
                if (AddTopicActivity.this.mysearchAdapter != null) {
                    String unused = AddTopicActivity.this.query = text;
                    if (text.length() != 0) {
                        boolean unused2 = AddTopicActivity.this.searchWas = true;
                        AddTopicActivity.this.dosearchtopic();
                    } else if (AddTopicActivity.this.searching) {
                        AddTopicActivity.this.listView.setAdapter(AddTopicActivity.this.mysearchAdapter);
                        AddTopicActivity.this.mysearchAdapter.getData().clear();
                        AddTopicActivity.this.mysearchAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void onActionSearch(String trim) {
            }
        });
    }

    static /* synthetic */ void lambda$initSearchView$3(View v) {
    }

    /* access modifiers changed from: protected */
    public void initData() {
        getFcTopicList();
    }

    private class MysearchAdapter extends PageSelectionAdapter<RespTopicBean.Item, PageHolder> {
        public MysearchAdapter(Context context) {
            super(context);
        }

        public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            return new PageHolder(LayoutInflater.from(AddTopicActivity.this.mContext).inflate(R.layout.item_fc_topic, parent, false));
        }

        public void onBindViewHolderForChild(PageHolder holder, int position, RespTopicBean.Item item) {
            PageHolder pageHolder = holder;
            int i = position;
            RespTopicBean.Item item2 = item;
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(70.0f));
            if (i % 2 == 0) {
                layoutParams.topMargin = AndroidUtilities.dp(15.0f);
                layoutParams.leftMargin = AndroidUtilities.dp(15.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
            } else {
                layoutParams.rightMargin = AndroidUtilities.dp(15.0f);
                layoutParams.topMargin = AndroidUtilities.dp(15.0f);
            }
            if (getItemCount() % 2 == 0) {
                if (i == getItemCount() - 1 || i == getItemCount() - 2) {
                    layoutParams.bottomMargin = AndroidUtilities.dp(15.0f);
                }
            } else if (i == getItemCount() - 1) {
                layoutParams.bottomMargin = AndroidUtilities.dp(15.0f);
            }
            pageHolder.itemView.setLayoutParams(layoutParams);
            HashMap access$000 = AddTopicActivity.this.cacheSelectedHashmap;
            if (access$000.get(item2.ID + "" + item2.TypeID) == null) {
                pageHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                pageHolder.itemView.setBackground(DrawableUtils.createLayerDrawable(AddTopicActivity.this.mContext.getResources().getColor(R.color.color_F0FCFF), AddTopicActivity.this.mContext.getResources().getColor(R.color.color_2ECEFD), 0.0f));
            }
            MryTextView tv_title = (MryTextView) pageHolder.itemView.findViewById(R.id.tv_title);
            MryTextView tv_subtitle = (MryTextView) pageHolder.itemView.findViewById(R.id.tv_subtitle);
            MryTextView tv_tag = (MryTextView) pageHolder.itemView.findViewById(R.id.tv_tag);
            tv_title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            tv_subtitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            tv_tag.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            tv_title.setText(Html.fromHtml(String.format(LocaleController.getString("topictitle", R.string.topictitle), new Object[]{item2.TopicName})));
            tv_subtitle.setText(item2.Subtitle);
            if (item2.Tag == 1) {
                tv_tag.setText(LocaleController.getString("fc_new", R.string.fc_new));
                tv_tag.setBackground(DrawableUtils.getGradientDrawable(new float[]{(float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f}, AddTopicActivity.this.mContext.getResources().getColor(R.color.color_FFFD8A94), AddTopicActivity.this.mContext.getResources().getColor(R.color.color_FFFD6FCB)));
            } else if (item2.Tag == 2) {
                tv_tag.setText(LocaleController.getString("fc_recommend", R.string.fc_recommend));
                tv_tag.setBackground(DrawableUtils.getGradientDrawable(new float[]{(float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f}, AddTopicActivity.this.mContext.getResources().getColor(R.color.color_FF50F7FD), AddTopicActivity.this.mContext.getResources().getColor(R.color.color_FF2ED2FE)));
            } else {
                tv_tag.setVisibility(8);
            }
        }

        public void loadData(int page) {
            super.loadData(page);
            AddTopicActivity.this.getTopics(page);
        }
    }

    /* access modifiers changed from: private */
    public void dosearchtopic() {
        this.content_container.setVisibility(8);
        this.listView.setVisibility(0);
        this.fl_search_cover.setVisibility(8);
        this.mysearchAdapter.notifyDataSetChanged();
        getTopics(0);
    }

    /* access modifiers changed from: protected */
    public void getFcTopicList() {
        RxHelper.getInstance().sendRequest(TAG, ApiFactory.getInstance().getApiMomentForum().getFcTopicList(), new Consumer() {
            public final void accept(Object obj) {
                AddTopicActivity.this.lambda$getFcTopicList$4$AddTopicActivity((BResponse) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                AddTopicActivity.this.lambda$getFcTopicList$5$AddTopicActivity((Throwable) obj);
            }
        });
    }

    public /* synthetic */ void lambda$getFcTopicList$4$AddTopicActivity(BResponse responseData) throws Exception {
        if (responseData == null) {
            this.emptyViewDialog.showError(LocaleController.getString("fc_request_fialed", R.string.fc_request_fialed));
            FcToastUtils.show((CharSequence) "请求失败");
        } else if (!responseData.isState() || responseData.Data == null) {
            this.emptyViewDialog.showEmpty();
            FcToastUtils.show((CharSequence) responseData.Message);
        } else {
            List<RespTopicTypeBean> list = (List) responseData.Data;
            this.topicTypes = list;
            for (RespTopicTypeBean topicTypes2 : list) {
                this.addTopicFragmentList.add(new AddTopicFragment(topicTypes2, this.cacheSelectedHashmap));
            }
            this.emptyViewDialog.showContent();
            if (this.addTopicFragmentList.size() > 0) {
                this.addTopicAdapter.notifyDataSetChanged();
                this.tabLayout.notifyDataSetChanged();
            }
        }
    }

    public /* synthetic */ void lambda$getFcTopicList$5$AddTopicActivity(Throwable throwable) throws Exception {
        this.emptyViewDialog.showError(LocaleController.getString("fc_request_fialed", R.string.fc_request_fialed));
    }

    private class AddTopicAdapter extends FragmentStatePagerAdapter {
        private Context mContext;

        public AddTopicAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.mContext = context;
        }

        public Fragment getItem(int position) {
            if (AddTopicActivity.this.addTopicFragmentList.get(position) == null) {
                return null;
            }
            return (Fragment) AddTopicActivity.this.addTopicFragmentList.get(position);
        }

        public int getCount() {
            if (AddTopicActivity.this.topicTypes == null) {
                return 0;
            }
            return AddTopicActivity.this.topicTypes.size();
        }

        public CharSequence getPageTitle(int position) {
            return AddTopicActivity.this.topicTypes == null ? "" : ((RespTopicTypeBean) AddTopicActivity.this.topicTypes.get(position)).TopicTypeName;
        }
    }

    private void notifyTopicSelectChanged() {
        for (AddTopicFragment fragment : this.addTopicFragmentList) {
            fragment.setselectedItemCount(this.cacheSelectedHashmap);
        }
    }

    /* access modifiers changed from: private */
    public void getTopics(int pageNo) {
        RxHelper.getInstance().sendRequest(TAG, ApiFactory.getInstance().getApiMomentForum().getFcTopic(this.TopicTypeID, this.query, pageNo * 20, 20), new Consumer() {
            public final void accept(Object obj) {
                AddTopicActivity.this.lambda$getTopics$6$AddTopicActivity((BResponse) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                AddTopicActivity.this.lambda$getTopics$7$AddTopicActivity((Throwable) obj);
            }
        });
    }

    public /* synthetic */ void lambda$getTopics$6$AddTopicActivity(BResponse response) throws Exception {
        if (response == null || !response.isState()) {
            this.mysearchAdapter.showError(LocaleController.getString("request_fialed", R.string.fc_request_fialed));
        } else if (response.Data != null) {
            List<RespTopicBean.Item> topics = ((RespTopicBean) response.Data).getTopics();
            this.respTopicBeans = topics;
            this.mysearchAdapter.addData(topics);
        } else {
            this.mysearchAdapter.showEmpty();
        }
    }

    public /* synthetic */ void lambda$getTopics$7$AddTopicActivity(Throwable throwable) throws Exception {
        this.mysearchAdapter.showError(LocaleController.getString("request_fialed", R.string.fc_request_fialed));
    }

    public boolean onBackPressed() {
        getAccountInstance().getNotificationCenter();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.selectedTopicSuccessToPublish, this.cacheSelectedHashmap);
        return super.onBackPressed();
    }
}
