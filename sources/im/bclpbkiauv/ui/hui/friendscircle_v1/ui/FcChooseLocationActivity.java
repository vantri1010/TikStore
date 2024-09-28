package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.bjz.comm.net.receiver.NetworkConnectChangedReceiver;
import com.blankj.utilcode.util.KeyboardUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.AdapterLoadMoreView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.FcChooseLocationListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcChooseLocationActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.CommLayoutItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import java.util.ArrayList;
import java.util.List;

public class FcChooseLocationActivity extends BaseSearchViewFragment implements NetworkConnectChangedReceiver.OnNetWorkStateListener {
    private static final String KEYWORD = "美食$购物$休闲娱乐$生活服务$酒店$交通设施$房地产$医疗$教育培训$自然地物";
    private static final int SEARCH_CITY = 1;
    private static final int SEARCH_NEARBY = 0;
    /* access modifiers changed from: private */
    public byte CONFIRM_BUTTON = 1;
    boolean isFirstLoc = true;
    private boolean isNetWorkConnected = false;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public FcChooseLocationListener locationListener;
    /* access modifiers changed from: private */
    public MyAdapter mAdapter;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public BDLocation mCurrentLocation;
    private LocationClient mLocClient;
    private MyLocationListener mLocationListener;
    private PoiSearch mPoiSearch;
    /* access modifiers changed from: private */
    public MyAdapter mSearchAdapter;
    private MrySearchView mSearchView;
    private ActionBarMenu menu;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;
    /* access modifiers changed from: private */
    public int pageNo = 0;
    private RecyclerListView rv_search_location_info;
    /* access modifiers changed from: private */
    public String searchKeyword;
    /* access modifiers changed from: private */
    public int searchPageNo = 0;
    /* access modifiers changed from: private */
    public int searchType;
    /* access modifiers changed from: private */
    public PoiInfo selectedLocation = null;
    /* access modifiers changed from: private */
    public PoiInfo selectedSearchLocation = null;
    /* access modifiers changed from: private */
    public TextView tv_empty;

    public void setFcChooseLocationListener(FcChooseLocationListener listener) {
        this.locationListener = listener;
    }

    public View createView(Context context) {
        this.mContext = context;
        getParentActivity().getWindow().setSoftInputMode(32);
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_friends_cricle_choose_location, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initView();
        return this.fragmentView;
    }

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        return this.mSearchView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("friendscircle_publish_choose_location", R.string.friendscircle_publish_choose_location));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FcChooseLocationActivity.this.finishFragment();
                } else if (id == FcChooseLocationActivity.this.CONFIRM_BUTTON && FcChooseLocationActivity.this.selectedLocation != null && FcChooseLocationActivity.this.locationListener != null) {
                    FcChooseLocationActivity.this.locationListener.doAfterChooseLocationSuccess(FcChooseLocationActivity.this.selectedLocation);
                    FcChooseLocationActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.menu = createMenu;
        createMenu.addRightItemView(this.CONFIRM_BUTTON, LocaleController.getString("confirm", R.string.confirm));
        this.menu.setAlpha(0.3f);
    }

    private void initView() {
        this.mSearchView = (MrySearchView) this.fragmentView.findViewById(R.id.search_view);
        this.listView = (RecyclerListView) this.fragmentView.findViewById(R.id.rv_location_info);
        this.rv_search_location_info = (RecyclerListView) this.fragmentView.findViewById(R.id.rv_search_location_info);
        this.tv_empty = (TextView) this.fragmentView.findViewById(R.id.tv_empty);
        this.listView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView.setLayoutManager(new LinearLayoutManager(this.mContext) {
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1, -1);
            }
        });
        this.listView.addItemDecoration(new CommLayoutItemDecoration(AndroidUtilities.dp(1.0f)));
        MyAdapter myAdapter = new MyAdapter(this.mContext);
        this.mAdapter = myAdapter;
        myAdapter.setAdapterStateView(new LoadMoreView(getParentActivity()));
        this.listView.setAdapter(this.mAdapter);
        this.rv_search_location_info.setLayoutManager(new LinearLayoutManager(this.mContext) {
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1, -1);
            }
        });
        this.rv_search_location_info.addItemDecoration(new CommLayoutItemDecoration(AndroidUtilities.dp(0.5f)));
        MyAdapter myAdapter2 = new MyAdapter(this.mContext);
        this.mSearchAdapter = myAdapter2;
        myAdapter2.setAdapterStateView(new LoadMoreView(getParentActivity()));
        this.rv_search_location_info.setAdapter(this.mSearchAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                FcChooseLocationActivity.this.lambda$initView$0$FcChooseLocationActivity(view, i);
            }
        });
        this.rv_search_location_info.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                FcChooseLocationActivity.this.lambda$initView$1$FcChooseLocationActivity(view, i);
            }
        });
        PoiInfo noPoiInfo = new PoiInfo();
        noPoiInfo.setName("不显示位置");
        this.mAdapter.addData(noPoiInfo);
        this.mAdapter.addData(new PoiInfo());
        initSearchView();
        initBaiduMap();
        registerNetworkChangeReceiver();
    }

    public /* synthetic */ void lambda$initView$0$FcChooseLocationActivity(View view, int position) {
        PoiInfo info = (PoiInfo) this.mAdapter.getItem(position);
        if (info != null && !TextUtils.isEmpty(info.getName())) {
            this.selectedLocation = info;
            this.mAdapter.notifyDataSetChanged();
            setConfirmButtonAlpha();
        }
    }

    public /* synthetic */ void lambda$initView$1$FcChooseLocationActivity(View view, int position) {
        PoiInfo info = (PoiInfo) this.mSearchAdapter.getItem(position);
        if (info != null) {
            this.selectedSearchLocation = info;
            this.mSearchAdapter.notifyDataSetChanged();
            FcChooseLocationListener fcChooseLocationListener = this.locationListener;
            if (fcChooseLocationListener != null) {
                fcChooseLocationListener.doAfterChooseLocationSuccess(info);
                finishFragment();
            }
        }
    }

    public void initSearchView() {
        this.mSearchView.getEditor().setBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_windowBackgroundGray), (float) AndroidUtilities.dp(17.0f)));
        this.mSearchView.setHintText(LocaleController.getString("friendscircle_hint_edit_search_location", R.string.friendscircle_hint_edit_search_location));
        this.mSearchView.setiSearchViewDelegate(this);
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        NetworkConnectChangedReceiver networkConnectChangedReceiver2 = new NetworkConnectChangedReceiver();
        this.networkConnectChangedReceiver = networkConnectChangedReceiver2;
        networkConnectChangedReceiver2.setNetWorkStateListener(this);
        this.mContext.registerReceiver(this.networkConnectChangedReceiver, filter);
    }

    public void onNetWorkStateChange(boolean isConnected) {
        LocationClient locationClient;
        this.isNetWorkConnected = isConnected;
        if (isConnected && this.mCurrentLocation == null && (locationClient = this.mLocClient) != null) {
            locationClient.start();
        }
    }

    private void initBaiduMap() {
        initLocationClient();
        initPoiSearch();
    }

    private void initLocationClient() {
        LocationClient locationClient = new LocationClient(this.mContext);
        this.mLocClient = locationClient;
        MyLocationListener myLocationListener = new MyLocationListener();
        this.mLocationListener = myLocationListener;
        locationClient.registerLocationListener((BDAbstractLocationListener) myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType(CoordinateType.GCJ02);
        option.setScanSpan(1000);
        option.setIgnoreKillProcess(false);
        option.setIsNeedAddress(true);
        this.mLocClient.setLocOption(option);
    }

    private void initPoiSearch() {
        PoiSearch newInstance = PoiSearch.newInstance();
        this.mPoiSearch = newInstance;
        newInstance.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            public void onGetPoiResult(PoiResult poiResult) {
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                if (FcChooseLocationActivity.this.isFirstLoc) {
                    FcChooseLocationActivity.this.isFirstLoc = false;
                }
                if (FcChooseLocationActivity.this.searchType == 0) {
                    if (allPoi != null && allPoi.size() > 0) {
                        if (FcChooseLocationActivity.this.pageNo == 0) {
                            PoiInfo unused = FcChooseLocationActivity.this.selectedLocation = null;
                            FcChooseLocationActivity.this.mAdapter.pageReset();
                            FcChooseLocationActivity.this.mAdapter.clearData();
                            ArrayList<PoiInfo> newList = new ArrayList<>();
                            PoiInfo noPoiInfo = new PoiInfo();
                            noPoiInfo.setName("不显示位置");
                            newList.add(0, noPoiInfo);
                            if (FcChooseLocationActivity.this.mCurrentLocation != null) {
                                String city = FcChooseLocationActivity.this.mCurrentLocation.getCity();
                                if (!TextUtils.isEmpty(city)) {
                                    PoiInfo cityPoiInfo = new PoiInfo();
                                    cityPoiInfo.setName(city);
                                    cityPoiInfo.setCity(city);
                                    cityPoiInfo.setLocation(new LatLng(FcChooseLocationActivity.this.mCurrentLocation.getLatitude(), FcChooseLocationActivity.this.mCurrentLocation.getLongitude()));
                                    newList.add(1, cityPoiInfo);
                                }
                            }
                            newList.addAll(allPoi);
                            FcChooseLocationActivity.this.mAdapter.addData(newList);
                            return;
                        }
                        FcChooseLocationActivity.this.mAdapter.addData(allPoi);
                    }
                } else if (FcChooseLocationActivity.this.searchType == 1) {
                    if (FcChooseLocationActivity.this.searchPageNo == 0) {
                        PoiInfo unused2 = FcChooseLocationActivity.this.selectedSearchLocation = null;
                        FcChooseLocationActivity.this.mSearchAdapter.pageReset();
                        FcChooseLocationActivity.this.mSearchAdapter.clearData();
                    }
                    FcChooseLocationActivity.this.mSearchAdapter.addData(allPoi);
                    if (FcChooseLocationActivity.this.mSearchAdapter.getItemCount() > 0) {
                        FcChooseLocationActivity.this.tv_empty.setVisibility(8);
                    } else if (!TextUtils.equals(FcChooseLocationActivity.this.searchKeyword, FcChooseLocationActivity.KEYWORD)) {
                        FcChooseLocationActivity.this.tv_empty.setVisibility(0);
                    } else {
                        FcChooseLocationActivity.this.tv_empty.setVisibility(8);
                    }
                }
            }

            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }

            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            }

            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        });
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        public MyLocationListener() {
        }

        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                BDLocation unused = FcChooseLocationActivity.this.mCurrentLocation = location;
                if (FcChooseLocationActivity.this.isFirstLoc) {
                    FcChooseLocationActivity.this.search(0, FcChooseLocationActivity.KEYWORD, 0);
                }
            }
        }
    }

    public void onTextChange(String value) {
        super.onTextChange(value);
        if (!TextUtils.isEmpty(value)) {
            search(1, value, 0);
        } else {
            search(1, KEYWORD, 0);
        }
    }

    public void onSearchCollapse() {
        super.onSearchCollapse();
        search(0, KEYWORD, 0);
    }

    /* access modifiers changed from: private */
    public void search(int type, String keyword, int page) {
        if (!this.isNetWorkConnected) {
            FcToastUtils.show((CharSequence) "网络连接异常");
        } else if (this.mCurrentLocation != null) {
            if (type == 0) {
                this.pageNo = page;
            } else if (type == 1) {
                this.searchPageNo = page;
            }
            if (type == 0) {
                this.mPoiSearch.searchNearby(new PoiNearbySearchOption().location(new LatLng(this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude())).keyword(keyword).radius(1000).pageNum(page).pageCapacity(20).radiusLimit(true));
            } else if (type == 1) {
                if (!TextUtils.isEmpty(keyword)) {
                    String city = this.mCurrentLocation.getCity();
                    if (!TextUtils.isEmpty(city)) {
                        this.mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(keyword).pageNum(page).pageCapacity(20));
                    } else {
                        return;
                    }
                } else {
                    this.mPoiSearch.searchNearby(new PoiNearbySearchOption().location(new LatLng(this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude())).keyword(keyword).radius(1000).pageNum(page).pageCapacity(20).radiusLimit(true));
                }
            }
            this.searchType = type;
            this.searchKeyword = keyword;
        }
    }

    public void onStart(boolean focus) {
        super.onStart(focus);
        this.searchType = focus;
        this.searchKeyword = focus ? this.searchKeyword : KEYWORD;
        this.rv_search_location_info.setVisibility(focus ? 0 : 8);
        this.tv_empty.setVisibility(8);
        setConfirmButtonAlpha();
    }

    private class MyAdapter extends PageSelectionAdapter<PoiInfo, PageHolder> {
        private int TYPE_CONTENT = 1;
        private int TYPE_CURRENT = -1;
        private int TYPE_PROGRESS = 0;

        public MyAdapter(Context context) {
            super(context);
        }

        public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            if (viewType == this.TYPE_PROGRESS) {
                FcChooseLocationActivity fcChooseLocationActivity = FcChooseLocationActivity.this;
                return new ProgressViewHolder(LayoutInflater.from(fcChooseLocationActivity.mContext).inflate(R.layout.item_friends_circle_location_progress, parent, false));
            }
            FcChooseLocationActivity fcChooseLocationActivity2 = FcChooseLocationActivity.this;
            return new ViewHolder(LayoutInflater.from(fcChooseLocationActivity2.mContext).inflate(R.layout.item_friends_circle_location_info, parent, false));
        }

        public void onBindViewHolderForChild(PageHolder holder, int position, PoiInfo item) {
            if (getItemViewTypeForChild(position) == this.TYPE_PROGRESS) {
                ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
                return;
            }
            ViewHolder contentHolder = (ViewHolder) holder;
            String str = "";
            contentHolder.tvLocationName.setText(item.getName() == null ? str : item.getName());
            TextView textView = contentHolder.tvAddress;
            if (item.getAddress() != null) {
                str = item.getAddress();
            }
            textView.setText(str);
            int i = 0;
            contentHolder.tvAddress.setVisibility(item.getAddress() == null ? 8 : 0);
            if (FcChooseLocationActivity.this.searchType == 0) {
                ImageView imageView = contentHolder.ivSelected;
                if (item != FcChooseLocationActivity.this.selectedLocation) {
                    i = 4;
                }
                imageView.setVisibility(i);
            } else if (FcChooseLocationActivity.this.searchType == 1) {
                ImageView imageView2 = contentHolder.ivSelected;
                if (item != FcChooseLocationActivity.this.selectedSearchLocation) {
                    i = 4;
                }
                imageView2.setVisibility(i);
            }
        }

        public void loadData(int page) {
            super.loadData(page);
            FcChooseLocationActivity fcChooseLocationActivity = FcChooseLocationActivity.this;
            fcChooseLocationActivity.search(fcChooseLocationActivity.searchType, FcChooseLocationActivity.this.searchKeyword, page);
        }

        /* access modifiers changed from: protected */
        public int getItemViewTypeForChild(int position) {
            PoiInfo item = (PoiInfo) getItem(position);
            if (item == null) {
                this.TYPE_CURRENT = this.TYPE_CONTENT;
            } else if (!TextUtils.isEmpty(item.getName())) {
                this.TYPE_CURRENT = this.TYPE_CONTENT;
            } else {
                this.TYPE_CURRENT = this.TYPE_PROGRESS;
            }
            return this.TYPE_CURRENT;
        }
    }

    private class ViewHolder extends PageHolder {
        ImageView ivSelected;
        TextView tvAddress;
        TextView tvLocationName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvLocationName = (TextView) itemView.findViewById(R.id.tv_location_name);
            this.tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            this.ivSelected = (ImageView) itemView.findViewById(R.id.iv_selected);
        }
    }

    private class ProgressViewHolder extends PageHolder {
        ProgressBar pb_search_location;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            this.pb_search_location = (ProgressBar) itemView.findViewById(R.id.pb_search_location);
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NetworkConnectChangedReceiver networkConnectChangedReceiver2 = this.networkConnectChangedReceiver;
        if (networkConnectChangedReceiver2 != null) {
            networkConnectChangedReceiver2.setNetWorkStateListener((NetworkConnectChangedReceiver.OnNetWorkStateListener) null);
            this.mContext.unregisterReceiver(this.networkConnectChangedReceiver);
        }
        PoiSearch poiSearch = this.mPoiSearch;
        if (poiSearch != null) {
            poiSearch.destroy();
        }
        LocationClient locationClient = this.mLocClient;
        if (locationClient != null) {
            locationClient.unRegisterLocationListener((BDAbstractLocationListener) this.mLocationListener);
            this.mLocClient.stop();
        }
        KeyboardUtils.unregisterSoftInputChangedListener(getParentActivity().getWindow());
    }

    private void setConfirmButtonAlpha() {
        if (this.locationListener == null || this.selectedLocation == null) {
            this.menu.setAlpha(0.3f);
        } else if (this.searchType == 0) {
            this.menu.setAlpha(1.0f);
        } else {
            this.menu.setAlpha(0.3f);
        }
    }

    private static class LoadMoreView extends AdapterLoadMoreView {
        public LoadMoreView(Context context) {
            super(context);
        }

        public LoadMoreView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        /* access modifiers changed from: protected */
        public void init(Context context) {
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            setLayoutParams(new ViewGroup.LayoutParams(-1, AndroidUtilities.dp(60.0f)));
            RelativeLayout parent = new RelativeLayout(context);
            this.tv = new MryTextView(context);
            this.tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.tv.setGravity(17);
            this.tv.setLayoutParams(LayoutHelper.createRelative(-2, -1, 13));
            this.tv.setId(R.id.tv_location_progress_name);
            parent.addView(this.tv);
            this.progressBar = new RadialProgressView(context);
            ((RadialProgressView) this.progressBar).setStrokeWidth(1.0f);
            ((RadialProgressView) this.progressBar).setProgressColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            ((RadialProgressView) this.progressBar).setSize(AndroidUtilities.dp(10.0f));
            this.progressBar.setLayoutParams(LayoutHelper.createRelative(-2.0f, -2.0f, 0, 0, 0, 0, 7, R.id.tv_location_progress_name));
            parent.addView(this.progressBar);
            addView(parent, LayoutHelper.createRelative(-1, -2));
        }

        public void loadMoreStart() {
            this.mState = 3;
            post(new Runnable() {
                public final void run() {
                    FcChooseLocationActivity.LoadMoreView.this.lambda$loadMoreStart$0$FcChooseLocationActivity$LoadMoreView();
                }
            });
        }

        public /* synthetic */ void lambda$loadMoreStart$0$FcChooseLocationActivity$LoadMoreView() {
            this.progressBar.setVisibility(0);
            this.tv.setText(LocaleController.getString("friends_circle_location_search_hint", R.string.friends_circle_location_search_hint));
            this.tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }

        public void loadMoreNoMoreData() {
            this.mState = 5;
            post(new Runnable() {
                public final void run() {
                    FcChooseLocationActivity.LoadMoreView.this.lambda$loadMoreNoMoreData$1$FcChooseLocationActivity$LoadMoreView();
                }
            });
        }

        public /* synthetic */ void lambda$loadMoreNoMoreData$1$FcChooseLocationActivity$LoadMoreView() {
            this.progressBar.setVisibility(8);
            this.tv.setText(LocaleController.getString("friends_circle_location_search_nomore_hint", R.string.friends_circle_location_search_nomore_hint));
            this.tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
        }
    }
}
