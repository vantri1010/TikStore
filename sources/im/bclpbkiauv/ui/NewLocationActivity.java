package im.bclpbkiauv.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
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
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.SendLocationCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.BottomDialog;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.utils.NaviUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewLocationActivity extends BaseFragment implements View.OnClickListener, NotificationCenter.NotificationCenterDelegate {
    private static final String KEYWORD = "美食$购物$休闲娱乐$生活服务$酒店$交通设施$房地产$医疗$教育培训$自然地物";
    public static final int LOCATION_SEND = 0;
    public static final int LOCATION_SHARING_SEND = 2;
    public static final int LOCATION_SHARING_VIEW = 3;
    public static final int LOCATION_VIEW = 1;
    private static final int SEARCH_CITY = 1;
    private static final int SEARCH_NEARBY = 0;
    /* access modifiers changed from: private */
    public TLRPC.TL_channelLocation chatLocation;
    private LocationActivityDelegate delegate;
    /* access modifiers changed from: private */
    public long dialogId;
    /* access modifiers changed from: private */
    public TLRPC.TL_channelLocation initialLocation;
    boolean isFirstLoc = true;
    private boolean isInterceptTouch;
    /* access modifiers changed from: private */
    public boolean isSearchMode;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public MyLocationData locData;
    /* access modifiers changed from: private */
    public int locationType;
    /* access modifiers changed from: private */
    public MyAdapter mAdapter;
    /* access modifiers changed from: private */
    public BaiduMap mBaiduMap;
    /* access modifiers changed from: private */
    public BottomSheetBehavior mBehavior;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentDirection = 0;
    /* access modifiers changed from: private */
    public BDLocation mCurrentLocation;
    private FrameLayout mFlBottomMenu;
    /* access modifiers changed from: private */
    public ImageView mIvCenterMarker;
    private ImageView mIvLocation;
    private ImageView mIvNavigate;
    private LinearLayout mLlNavigateInfo;
    LocationClient mLocClient;
    public MyLocationListener mLocationListener;
    MapView mMapView;
    /* access modifiers changed from: private */
    public Overlay mMarker;
    private PoiSearch mPoiSearch;
    /* access modifiers changed from: private */
    public RelativeLayout mRlBottomSheet;
    /* access modifiers changed from: private */
    public RelativeLayout mRlMapContainer;
    /* access modifiers changed from: private */
    public RelativeLayout mRlProgress;
    private RelativeLayout mRlTopBtn;
    private MrySearchView mSearchView;
    /* access modifiers changed from: private */
    public TextView mTvAddress;
    private TextView mTvCancel;
    /* access modifiers changed from: private */
    public TextView mTvLocName;
    private TextView mTvSend;
    /* access modifiers changed from: private */
    public TLRPC.User mUser;
    private ArrayList<LiveLocation> markers = new ArrayList<>();
    /* access modifiers changed from: private */
    public SparseArray<LiveLocation> markersMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public MessageObject messageObject;
    /* access modifiers changed from: private */
    public float oldOffset;
    private float oldX;
    private float oldY;
    /* access modifiers changed from: private */
    public String searchKeyword;
    /* access modifiers changed from: private */
    public int searchType;
    /* access modifiers changed from: private */
    public PoiInfo selectedLocation;

    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2);
    }

    public class LiveLocation {
        public TLRPC.Chat chat;
        public int id;
        public Overlay marker;
        public TLRPC.Message object;
        public TLRPC.User user;

        public LiveLocation() {
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        public MyLocationListener() {
        }

        public void onReceiveLocation(BDLocation location) {
            LiveLocation liveLocation;
            if (location != null && NewLocationActivity.this.mMapView != null) {
                BDLocation unused = NewLocationActivity.this.mCurrentLocation = location;
                MyLocationData unused2 = NewLocationActivity.this.locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction((float) NewLocationActivity.this.mCurrentDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
                if (NewLocationActivity.this.locationType != 3 || !NewLocationActivity.this.getLocationController().isSharingLocation(NewLocationActivity.this.dialogId)) {
                    NewLocationActivity.this.mBaiduMap.setMyLocationData(NewLocationActivity.this.locData);
                }
                if (NewLocationActivity.this.isFirstLoc) {
                    LatLng ll = null;
                    if (NewLocationActivity.this.locationType == 0) {
                        ll = new LatLng(location.getLatitude(), location.getLongitude());
                    } else if (NewLocationActivity.this.locationType == 1) {
                        NewLocationActivity.this.mTvLocName.setText(NewLocationActivity.this.messageObject.messageOwner.media.title);
                        NewLocationActivity.this.mTvAddress.setText(NewLocationActivity.this.messageObject.messageOwner.media.address);
                        TLRPC.GeoPoint geoPoint = NewLocationActivity.this.messageObject.messageOwner.media.geo;
                        ll = new LatLng(geoPoint.lat, geoPoint._long);
                        NewLocationActivity.this.addLocMarker(ll);
                    } else if (NewLocationActivity.this.locationType == 2) {
                        if (NewLocationActivity.this.initialLocation != null) {
                            ll = new LatLng(NewLocationActivity.this.initialLocation.geo_point.lat, NewLocationActivity.this.initialLocation.geo_point._long);
                        } else {
                            ll = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    } else if (NewLocationActivity.this.locationType == 3) {
                        if (NewLocationActivity.this.chatLocation != null) {
                            ll = new LatLng(NewLocationActivity.this.chatLocation.geo_point.lat, NewLocationActivity.this.chatLocation.geo_point._long);
                        } else if (NewLocationActivity.this.messageObject.getFromId() != NewLocationActivity.this.mUser.id) {
                            TLRPC.GeoPoint geoPoint2 = NewLocationActivity.this.messageObject.messageOwner.media.geo;
                            ll = new LatLng(geoPoint2.lat, geoPoint2._long);
                            NewLocationActivity newLocationActivity = NewLocationActivity.this;
                            LiveLocation unused3 = newLocationActivity.addUserMarker(newLocationActivity.messageObject.messageOwner);
                        } else {
                            ll = new LatLng(location.getLatitude(), location.getLongitude());
                            if (NewLocationActivity.this.getLocationController().isSharingLocation(NewLocationActivity.this.dialogId)) {
                                NewLocationActivity newLocationActivity2 = NewLocationActivity.this;
                                LiveLocation unused4 = newLocationActivity2.addUserMarker(newLocationActivity2.messageObject.messageOwner);
                            }
                        }
                        boolean unused5 = NewLocationActivity.this.getRecentLocations();
                    }
                    if (ll != null) {
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll).zoom(18.0f);
                        NewLocationActivity.this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                    if (NewLocationActivity.this.locationType == 0) {
                        NewLocationActivity.this.mIvCenterMarker.setVisibility(0);
                        NewLocationActivity.this.search(0, NewLocationActivity.KEYWORD, 0, true);
                    }
                    NewLocationActivity.this.isFirstLoc = false;
                } else if (NewLocationActivity.this.locationType == 3 && NewLocationActivity.this.getLocationController().isSharingLocation(NewLocationActivity.this.dialogId) && (liveLocation = (LiveLocation) NewLocationActivity.this.markersMap.get(NewLocationActivity.this.mUser.id)) != null) {
                    LatLng ll2 = new LatLng(location.getLatitude(), location.getLongitude());
                    liveLocation.marker.remove();
                    NewLocationActivity newLocationActivity3 = NewLocationActivity.this;
                    liveLocation.marker = newLocationActivity3.createUserMarker(ll2, newLocationActivity3.mUser);
                }
            }
        }
    }

    public NewLocationActivity(int type) {
        this.locationType = type;
    }

    public NewLocationActivity(int type, long dialogId2) {
        this.locationType = type;
        this.dialogId = dialogId2;
    }

    public NewLocationActivity(int type, MessageObject messageObject2) {
        this.locationType = type;
        this.messageObject = messageObject2;
        this.dialogId = messageObject2.getDialogId();
    }

    public void setDelegate(LocationActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setInitialLocation(TLRPC.TL_channelLocation location) {
        this.initialLocation = location;
    }

    public void setChatLocation(int chatId, TLRPC.TL_channelLocation location) {
        this.dialogId = (long) (-chatId);
        this.chatLocation = location;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.mIvCenterMarker.getLayoutParams();
        lp.bottomMargin = (this.mIvCenterMarker.getMeasuredHeight() / 2) + AndroidUtilities.dp(14.0f);
        this.mIvCenterMarker.setLayoutParams(lp);
        layoutProgress(this.mRlBottomSheet.getMeasuredHeight() - this.mBehavior.getPeekHeight());
    }

    public boolean onFragmentCreate() {
        this.mUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        MessageObject messageObject2 = this.messageObject;
        if (messageObject2 != null && messageObject2.isLiveLocation()) {
            getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
            getNotificationCenter().addObserver(this, NotificationCenter.replaceMessagesObjects);
        }
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.mContext = context;
        getParentActivity().getWindow().setSoftInputMode(32);
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_new_location, (ViewGroup) null);
        initActionBar();
        initView();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.swipeBackEnabled = false;
        int i = this.locationType;
        if (i == 0) {
            this.actionBar.setAddToContainer(false);
            return;
        }
        if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("LocationInfo", R.string.LocationInfo));
        } else if (i == 2 || i == 3) {
            this.actionBar.setTitle(LocaleController.getString("LiveLocations", R.string.LiveLocations));
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1) {
                    NewLocationActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
        this.mRlMapContainer = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_map_container);
        this.mMapView = (MapView) this.fragmentView.findViewById(R.id.map_view);
        this.mIvLocation = (ImageView) this.fragmentView.findViewById(R.id.iv_location);
        this.mRlTopBtn = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_top_btn);
        this.mTvCancel = (TextView) this.fragmentView.findViewById(R.id.tv_cancel);
        this.mTvSend = (TextView) this.fragmentView.findViewById(R.id.tv_send);
        this.mIvCenterMarker = (ImageView) this.fragmentView.findViewById(R.id.iv_center_marker);
        this.listView = (RecyclerListView) this.fragmentView.findViewById(R.id.rv_location_info);
        this.mSearchView = (MrySearchView) this.fragmentView.findViewById(R.id.search_view);
        this.mRlBottomSheet = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_bottom_sheet);
        this.mRlProgress = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_progress);
        this.mFlBottomMenu = (FrameLayout) this.fragmentView.findViewById(R.id.fl_bottom_menu);
        this.mLlNavigateInfo = (LinearLayout) this.fragmentView.findViewById(R.id.ll_navigate_info);
        this.mTvLocName = (TextView) this.fragmentView.findViewById(R.id.tv_loc_name);
        this.mTvAddress = (TextView) this.fragmentView.findViewById(R.id.tv_address);
        this.mIvNavigate = (ImageView) this.fragmentView.findViewById(R.id.iv_navigate);
        this.mRlBottomSheet.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.mIvLocation.setBackground(Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(30.0f), Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_windowBackgroundGray)));
        this.mTvCancel.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), 1342177280));
        this.mTvSend.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_avatar_backgroundGreen)));
        this.mIvLocation.setOnClickListener(this);
        this.mTvCancel.setOnClickListener(this);
        this.mTvSend.setOnClickListener(this);
        this.mIvNavigate.setOnClickListener(this);
        initBottom();
        this.mSearchView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(10.0f), Theme.getColor(Theme.key_windowBackgroundGray)));
        this.listView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.mAdapter = new MyAdapter(this.mContext);
        RecyclerListView recyclerListView = this.listView;
        MyAdapter myAdapter = new MyAdapter(this.mContext);
        this.mAdapter = myAdapter;
        recyclerListView.setAdapter(myAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                NewLocationActivity.this.lambda$initView$0$NewLocationActivity(view, i);
            }
        });
        initBaiduMap();
        initSearchView();
    }

    public /* synthetic */ void lambda$initView$0$NewLocationActivity(View view, int position) {
        PoiInfo info = (PoiInfo) this.mAdapter.getItem(position);
        if (info != null) {
            this.selectedLocation = info;
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(info.location);
            this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            this.mAdapter.notifyDataSetChanged();
            if (this.isSearchMode) {
                addLocMarker(info.location);
            }
        }
    }

    private void initBottom() {
        this.mBehavior = BottomSheetBehavior.from(this.mRlBottomSheet);
        int peekHeight = AndroidUtilities.dp(300.0f);
        int i = this.locationType;
        if (i == 0) {
            ViewGroup.LayoutParams lp1 = this.mRlBottomSheet.getLayoutParams();
            lp1.height = (getScreenHeight() / 3) * 2;
            this.mRlBottomSheet.setLayoutParams(lp1);
            this.mRlBottomSheet.setVisibility(0);
            ViewGroup.LayoutParams lp2 = this.mRlMapContainer.getLayoutParams();
            lp2.height = getScreenHeight() - peekHeight;
            this.mRlMapContainer.setLayoutParams(lp2);
            this.mBehavior.setPeekHeight(peekHeight);
            this.mRlTopBtn.setVisibility(0);
        } else if (i == 1) {
            this.mLlNavigateInfo.setVisibility(0);
        } else if (i == 2 || i == 3) {
            SendLocationCell cell = new SendLocationCell(this.mContext, true);
            cell.setDialogId(this.dialogId);
            cell.setOnLiveStopListener(new SendLocationCell.OnLiveStopListener() {
                public final void onStop() {
                    NewLocationActivity.this.lambda$initBottom$1$NewLocationActivity();
                }
            });
            cell.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    NewLocationActivity.this.lambda$initBottom$2$NewLocationActivity(view);
                }
            });
            this.mFlBottomMenu.addView(cell, LayoutHelper.createLinear(-1, -2));
        }
        this.mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            public void onStateChanged(View view, int newState) {
                if (newState == 3) {
                    NewLocationActivity.this.layoutProgress(0);
                } else if (newState == 4) {
                    InputMethodManager manager = (InputMethodManager) NewLocationActivity.this.mContext.getSystemService("input_method");
                    if (manager.isActive() && manager != null) {
                        manager.hideSoftInputFromWindow(NewLocationActivity.this.listView.getWindowToken(), 2);
                    }
                    NewLocationActivity newLocationActivity = NewLocationActivity.this;
                    newLocationActivity.layoutProgress(newLocationActivity.mRlBottomSheet.getMeasuredHeight() - NewLocationActivity.this.mBehavior.getPeekHeight());
                }
            }

            public void onSlide(View view, float slideOffset) {
                int height = NewLocationActivity.this.mRlMapContainer.getMeasuredHeight() / 5;
                if (slideOffset != NewLocationActivity.this.oldOffset) {
                    NewLocationActivity.this.mRlMapContainer.setTranslationY((-slideOffset) * ((float) height));
                    NewLocationActivity newLocationActivity = NewLocationActivity.this;
                    newLocationActivity.layoutProgress((int) ((1.0f - slideOffset) * ((float) (newLocationActivity.mRlBottomSheet.getMeasuredHeight() - NewLocationActivity.this.mBehavior.getPeekHeight()))));
                }
                float unused = NewLocationActivity.this.oldOffset = slideOffset;
            }
        });
    }

    public /* synthetic */ void lambda$initBottom$1$NewLocationActivity() {
        LiveLocation liveLocation = this.markersMap.get(this.mUser.id);
        if (liveLocation != null) {
            liveLocation.marker.remove();
            this.markersMap.remove(this.mUser.id);
        }
        this.mBaiduMap.setMyLocationData(this.locData);
    }

    public /* synthetic */ void lambda$initBottom$2$NewLocationActivity(View v) {
        if (getLocationController().isSharingLocation(this.dialogId)) {
            getLocationController().removeSharingLocation(this.dialogId);
            finishFragment();
            return;
        }
        sendLocation();
    }

    /* access modifiers changed from: private */
    public void layoutProgress(int bottomMargin) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.mRlProgress.getLayoutParams();
        lp.bottomMargin = bottomMargin;
        this.mRlProgress.setLayoutParams(lp);
    }

    private void initBaiduMap() {
        this.mBaiduMap = this.mMapView.getMap();
        this.mMapView.showZoomControls(false);
        this.mMapView.showScaleControl(false);
        this.mBaiduMap.setMapType(1);
        this.mBaiduMap.setMyLocationEnabled(true);
        this.mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            public final void onTouch(MotionEvent motionEvent) {
                NewLocationActivity.this.lambda$initBaiduMap$3$NewLocationActivity(motionEvent);
            }
        });
        initLocationClient();
        if (this.locationType == 0) {
            initPoiSearch();
        }
    }

    public /* synthetic */ void lambda$initBaiduMap$3$NewLocationActivity(MotionEvent motionEvent) {
        motionEvent.getPointerCount();
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action == 3) {
                    this.isInterceptTouch = false;
                }
            } else if (!this.isInterceptTouch) {
                float currentX = motionEvent.getX();
                float currentY = motionEvent.getY();
                if (!(this.oldX == currentX && this.oldY == currentY) && !this.isSearchMode) {
                    search(0, KEYWORD, 0, true);
                }
            } else {
                this.isInterceptTouch = false;
            }
        } else if (this.mBehavior.getState() == 3) {
            this.mBehavior.setState(4);
            this.isInterceptTouch = true;
        } else {
            this.oldX = motionEvent.getX();
            this.oldY = motionEvent.getY();
        }
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
        this.mLocClient.start();
    }

    private void initPoiSearch() {
        PoiSearch newInstance = PoiSearch.newInstance();
        this.mPoiSearch = newInstance;
        newInstance.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            public void onGetPoiResult(PoiResult poiResult) {
                List<PoiInfo> allPoi = poiResult.getAllPoi();
                if (allPoi == null || allPoi.isEmpty()) {
                    allPoi = new ArrayList<>();
                    PoiInfo unused = NewLocationActivity.this.selectedLocation = null;
                } else if (NewLocationActivity.this.isSearchMode) {
                    PoiInfo unused2 = NewLocationActivity.this.selectedLocation = null;
                } else if (NewLocationActivity.this.selectedLocation == null || NewLocationActivity.this.mAdapter.getPage() == 0) {
                    PoiInfo unused3 = NewLocationActivity.this.selectedLocation = allPoi.get(0);
                }
                NewLocationActivity.this.mAdapter.addData(allPoi);
                NewLocationActivity.this.mRlProgress.setVisibility(4);
            }

            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }

            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            }

            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        });
    }

    private void initSearchView() {
        this.mSearchView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.mSearchView.setHintText(LocaleController.getString("SearchLocation", R.string.SearchLocation));
        this.mSearchView.setiSearchViewDelegate(new MrySearchView.ISearchViewDelegate() {
            public void onStart(boolean focus) {
                if (focus) {
                    boolean unused = NewLocationActivity.this.isSearchMode = true;
                    if (NewLocationActivity.this.mBehavior.getState() != 3) {
                        NewLocationActivity.this.mBehavior.setState(3);
                    }
                    NewLocationActivity.this.mIvCenterMarker.setVisibility(4);
                    return;
                }
                boolean unused2 = NewLocationActivity.this.isSearchMode = false;
                if (NewLocationActivity.this.mBehavior.getState() != 4) {
                    NewLocationActivity.this.mBehavior.setState(4);
                }
                NewLocationActivity newLocationActivity = NewLocationActivity.this;
                newLocationActivity.removeMarker(newLocationActivity.mMarker);
                NewLocationActivity.this.mIvCenterMarker.setVisibility(0);
                NewLocationActivity.this.search(0, NewLocationActivity.KEYWORD, 0, true);
            }

            public void onSearchExpand() {
            }

            public boolean canCollapseSearch() {
                return true;
            }

            public void onSearchCollapse() {
            }

            public void onTextChange(String value) {
                if (TextUtils.isEmpty(value)) {
                    NewLocationActivity newLocationActivity = NewLocationActivity.this;
                    newLocationActivity.removeMarker(newLocationActivity.mMarker);
                }
                NewLocationActivity.this.search(1, value, 0, true);
            }

            public void onActionSearch(String trim) {
            }
        });
        KeyboardUtils.registerSoftInputChangedListener((Activity) getParentActivity(), (KeyboardUtils.OnSoftInputChangedListener) new KeyboardUtils.OnSoftInputChangedListener() {
            public final void onSoftInputChanged(int i) {
                NewLocationActivity.this.lambda$initSearchView$4$NewLocationActivity(i);
            }
        });
    }

    public /* synthetic */ void lambda$initSearchView$4$NewLocationActivity(int height) {
        if (height > 0 && this.mBehavior.getState() != 3) {
            this.mBehavior.setState(3);
        }
    }

    /* access modifiers changed from: private */
    public void search(int type, String keyword, int page, boolean isReset) {
        BDLocation bDLocation;
        if (this.locationType == 0 && (bDLocation = this.mCurrentLocation) != null && !TextUtils.isEmpty(bDLocation.getCity()) && this.mBaiduMap.getMapStatus().target != null) {
            if (page == 0) {
                this.mRlProgress.setVisibility(0);
            }
            if (isReset) {
                this.mAdapter.pageReset();
                this.mAdapter.clearData();
            }
            if (type == 0) {
                this.mPoiSearch.searchNearby(new PoiNearbySearchOption().location(this.mBaiduMap.getMapStatus().target).keyword(keyword).radius(1000).pageNum(page).pageCapacity(20).radiusLimit(true));
            } else if (type == 1) {
                this.mPoiSearch.searchInCity(new PoiCitySearchOption().city(this.mCurrentLocation.getCity()).keyword(keyword).pageNum(page).pageCapacity(20));
            }
            this.searchType = type;
            this.searchKeyword = keyword;
        }
    }

    public boolean onBackPressed() {
        if (this.mBehavior.getState() == 3) {
            this.mBehavior.setState(4);
            return false;
        }
        MrySearchView mrySearchView = this.mSearchView;
        if (mrySearchView == null || !mrySearchView.isSearchFieldVisible()) {
            return super.onBackPressed();
        }
        this.mSearchView.closeSearchField();
        return false;
    }

    public void onResume() {
        super.onResume();
        this.mMapView.onResume();
    }

    public void onPause() {
        super.onPause();
        this.mMapView.onPause();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        MrySearchView mrySearchView = this.mSearchView;
        if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
            this.mSearchView.closeSearchField(false);
        }
        PoiSearch poiSearch = this.mPoiSearch;
        if (poiSearch != null) {
            poiSearch.destroy();
            this.mPoiSearch = null;
        }
        LocationClient locationClient = this.mLocClient;
        if (locationClient != null) {
            locationClient.unRegisterLocationListener((BDAbstractLocationListener) this.mLocationListener);
            this.mLocClient.stop();
            this.mLocClient = null;
        }
        this.mBaiduMap.setMyLocationEnabled(false);
        this.mMapView.onDestroy();
        this.mMapView = null;
        this.mBaiduMap = null;
        KeyboardUtils.unregisterSoftInputChangedListener(getParentActivity().getWindow());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_location:
                moveToMyLocation();
                return;
            case R.id.iv_navigate:
                alertNavi();
                return;
            case R.id.tv_cancel:
                finishFragment();
                return;
            case R.id.tv_send:
                sendLocation();
                return;
            default:
                return;
        }
    }

    private void alertNavi() {
        BottomDialog dialog = new BottomDialog(this.mContext);
        dialog.addDialogItem(new BottomDialog.NormalTextItem(0, "百度地图", true));
        dialog.addDialogItem(new BottomDialog.NormalTextItem(1, "高德地图", true));
        dialog.addDialogItem(new BottomDialog.NormalTextItem(2, "腾讯地图", false));
        dialog.setOnItemClickListener(new BottomDialog.OnItemClickListener() {
            public final void onItemClick(int i, View view) {
                NewLocationActivity.this.lambda$alertNavi$5$NewLocationActivity(i, view);
            }
        });
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$alertNavi$5$NewLocationActivity(int id, View v1) {
        startNavi(id);
    }

    private void startNavi(int id) {
        int i = id;
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    if (!AppUtils.isAppInstalled(NaviUtils.PN_TENCENT_MAP)) {
                        ToastUtils.show((int) R.string.InstallTencentMapTips);
                        return;
                    }
                    TLRPC.GeoPoint geoPoint2 = this.messageObject.messageOwner.media.geo;
                    NaviUtils.startTencentNavi(this.mContext, this.mCurrentLocation.getAddress().address, this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude(), this.messageObject.messageOwner.media.address, geoPoint2.lat, geoPoint2._long);
                }
            } else if (!AppUtils.isAppInstalled(NaviUtils.PN_GAODE_MAP)) {
                ToastUtils.show((int) R.string.InstallGaodeMapTips);
            } else {
                TLRPC.GeoPoint geoPoint1 = this.messageObject.messageOwner.media.geo;
                NaviUtils.startGaodeNavi(this.mContext, this.mCurrentLocation.getAddress().address, this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude(), this.messageObject.messageOwner.media.address, geoPoint1.lat, geoPoint1._long);
            }
        } else if (!AppUtils.isAppInstalled(NaviUtils.PN_BAIDU_MAP)) {
            ToastUtils.show((int) R.string.InstallBaiduMapTips);
        } else {
            TLRPC.GeoPoint geoPoint = this.messageObject.messageOwner.media.geo;
            LatLng startPoint = new LatLng(this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude());
            LatLng endPoint = new LatLng(geoPoint.lat, geoPoint._long);
            CoordinateConverter converter = new CoordinateConverter().from(CoordinateConverter.CoordType.COMMON);
            LatLng startPoint2 = converter.coord(startPoint).convert();
            LatLng endPoint2 = converter.coord(endPoint).convert();
            NaviUtils.startBaiduNavi(this.mContext, this.mCurrentLocation.getAddress().address, startPoint2.latitude, startPoint2.longitude, this.messageObject.messageOwner.media.address, endPoint2.latitude, endPoint2.longitude);
        }
    }

    private void moveToMyLocation() {
        LatLng latLng;
        BDLocation bDLocation = this.mCurrentLocation;
        if (bDLocation != null) {
            if (this.locationType == 3) {
                MessageObject messageObject2 = this.messageObject;
                if (messageObject2 == null || messageObject2.getFromId() == this.mUser.id) {
                    latLng = new LatLng(this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude());
                } else {
                    latLng = new LatLng(this.messageObject.messageOwner.media.geo.lat, this.messageObject.messageOwner.media.geo._long);
                }
            } else {
                latLng = new LatLng(bDLocation.getLatitude(), this.mCurrentLocation.getLongitude());
            }
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng);
            this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            if (this.locationType == 0) {
                search(0, KEYWORD, 0, true);
            }
        }
    }

    private void sendLocation() {
        int i = this.locationType;
        if (i == 0) {
            if (this.selectedLocation != null) {
                TLRPC.TL_messageMediaGeo location = new TLRPC.TL_messageMediaGeo();
                location.title = this.selectedLocation.name;
                location.address = this.selectedLocation.address;
                location.geo = new TLRPC.TL_geoPoint();
                location.geo.lat = AndroidUtilities.fixLocationCoord(this.selectedLocation.location.latitude);
                location.geo._long = AndroidUtilities.fixLocationCoord(this.selectedLocation.location.longitude);
                LocationActivityDelegate locationActivityDelegate = this.delegate;
                if (locationActivityDelegate != null) {
                    locationActivityDelegate.didSelectLocation(location, this.locationType, true, 0);
                    finishFragment();
                }
            }
        } else if ((i == 2 || i == 3) && this.delegate != null && getParentActivity() != null && this.mCurrentLocation != null) {
            TLRPC.User user = null;
            if (this.dialogId > 0) {
                user = getMessagesController().getUser(Integer.valueOf((int) this.dialogId));
            }
            showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    NewLocationActivity.this.lambda$sendLocation$6$NewLocationActivity(i);
                }
            }));
        }
    }

    public /* synthetic */ void lambda$sendLocation$6$NewLocationActivity(int param) {
        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
        location.geo = new TLRPC.TL_geoPoint();
        location.geo.lat = AndroidUtilities.fixLocationCoord(this.mCurrentLocation.getLatitude());
        location.geo._long = AndroidUtilities.fixLocationCoord(this.mCurrentLocation.getLongitude());
        location.period = param;
        this.delegate.didSelectLocation(location, this.locationType, true, 0);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public boolean getRecentLocations() {
        ArrayList<TLRPC.Message> messages = getLocationController().locationsCache.get(this.dialogId);
        if (messages == null || messages.isEmpty()) {
            messages = null;
        } else {
            fetchRecentLocations(messages);
        }
        int lower_id = (int) this.dialogId;
        if (lower_id < 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-lower_id));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TLRPC.TL_messages_getRecentLocations req = new TLRPC.TL_messages_getRecentLocations();
        long dialog_id = this.dialogId;
        req.peer = getMessagesController().getInputPeer((int) dialog_id);
        req.limit = 100;
        getConnectionsManager().sendRequest(req, new RequestDelegate(dialog_id) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NewLocationActivity.this.lambda$getRecentLocations$8$NewLocationActivity(this.f$1, tLObject, tL_error);
            }
        });
        if (messages != null) {
            return true;
        }
        return false;
    }

    public /* synthetic */ void lambda$getRecentLocations$8$NewLocationActivity(long dialog_id, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(response, dialog_id) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NewLocationActivity.this.lambda$null$7$NewLocationActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$7$NewLocationActivity(TLObject response, long dialog_id) {
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        int a = 0;
        while (a < res.messages.size()) {
            if (!(res.messages.get(a).media instanceof TLRPC.TL_messageMediaGeoLive)) {
                res.messages.remove(a);
                a--;
            }
            a++;
        }
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
        getMessagesController().putUsers(res.users, false);
        getMessagesController().putChats(res.chats, false);
        getLocationController().locationsCache.put(this.dialogId, res.messages);
        getNotificationCenter().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(dialog_id));
        fetchRecentLocations(res.messages);
    }

    private void fetchRecentLocations(ArrayList<TLRPC.Message> messages) {
        int date = getConnectionsManager().getCurrentTime();
        Iterator<TLRPC.Message> it = messages.iterator();
        while (it.hasNext()) {
            TLRPC.Message message = it.next();
            if (message.date + message.media.period > date) {
                if (message.from_id != this.mUser.id) {
                    addUserMarker(message);
                } else if (getLocationController().isSharingLocation(this.dialogId)) {
                    addUserMarker(message);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public LiveLocation addUserMarker(TLRPC.Message message) {
        LatLng latLng = new LatLng(message.media.geo.lat, message.media.geo._long);
        LiveLocation liveLocation = this.markersMap.get(message.from_id);
        LiveLocation liveLocation2 = liveLocation;
        if (liveLocation == null) {
            liveLocation2 = new LiveLocation();
            liveLocation2.object = message;
            if (liveLocation2.object.from_id != 0) {
                liveLocation2.user = getMessagesController().getUser(Integer.valueOf(liveLocation2.object.from_id));
                liveLocation2.id = liveLocation2.object.from_id;
            } else {
                int did = (int) MessageObject.getDialogId(message);
                if (did > 0) {
                    liveLocation2.user = getMessagesController().getUser(Integer.valueOf(did));
                    liveLocation2.id = did;
                } else {
                    liveLocation2.chat = getMessagesController().getChat(Integer.valueOf(-did));
                    liveLocation2.id = did;
                }
            }
            liveLocation2.marker = createUserMarker(latLng, getMessagesController().getUser(Integer.valueOf(message.from_id)));
            this.markers.add(liveLocation2);
            this.markersMap.put(liveLocation2.id, liveLocation2);
            LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (liveLocation2.id == getUserConfig().getClientUserId() && myInfo != null && liveLocation2.object.id == myInfo.mid && this.mCurrentLocation != null) {
                liveLocation2.marker.remove();
                liveLocation2.marker = createUserMarker(new LatLng(this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude()), getMessagesController().getUser(Integer.valueOf(message.from_id)));
            }
        } else {
            liveLocation2.object = message;
            liveLocation2.marker.remove();
            liveLocation2.marker = createUserMarker(latLng, getMessagesController().getUser(Integer.valueOf(message.from_id)));
        }
        return liveLocation2;
    }

    /* access modifiers changed from: private */
    public void addLocMarker(LatLng ll) {
        OverlayOptions option = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)).draggable(false).flat(false);
        removeMarker(this.mMarker);
        this.mMarker = this.mBaiduMap.addOverlay(option);
    }

    /* access modifiers changed from: private */
    public Overlay createUserMarker(LatLng ll, TLRPC.User user) {
        return this.mBaiduMap.addOverlay(new MarkerOptions().position(ll).icon(createUserAvatar(user)).draggable(false).flat(false));
    }

    /* access modifiers changed from: private */
    public void removeMarker(Overlay marker) {
        if (marker != null && !marker.isRemoved()) {
            marker.remove();
        }
    }

    private BitmapDescriptor createUserAvatar(TLRPC.User user) {
        if (user == null) {
            return BitmapDescriptorFactory.fromResource(R.drawable.map_pin2);
        }
        FrameLayout frameLayout = new FrameLayout(this.mContext);
        frameLayout.setVisibility(8);
        frameLayout.setBackgroundResource(R.drawable.livepin);
        this.mRlMapContainer.addView(frameLayout, LayoutHelper.createRelative(62, 76));
        BackupImageView backupImageView = new BackupImageView(this.mContext);
        backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
        backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) new AvatarDrawable(user), (Object) user);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(52.0f, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
        return BitmapDescriptorFactory.fromView(frameLayout);
    }

    private int getMessageId(TLRPC.Message message) {
        if (message.from_id != 0) {
            return message.from_id;
        }
        return (int) MessageObject.getDialogId(message);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        LiveLocation liveLocation;
        LocationController.SharingLocationInfo myInfo;
        if (id == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue() && args[0].longValue() == this.dialogId && this.messageObject != null) {
                ArrayList<MessageObject> arr = args[1];
                for (int a = 0; a < arr.size(); a++) {
                    MessageObject messageObject2 = arr.get(a);
                    if (messageObject2.isLiveLocation()) {
                        addUserMarker(messageObject2.messageOwner);
                    }
                }
            }
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            long did = args[0].longValue();
            if (did == this.dialogId && this.messageObject != null) {
                ArrayList<MessageObject> messageObjects = args[1];
                for (int a2 = 0; a2 < messageObjects.size(); a2++) {
                    MessageObject messageObject3 = messageObjects.get(a2);
                    if (messageObject3.isLiveLocation() && (liveLocation = this.markersMap.get(getMessageId(messageObject3.messageOwner))) != null && ((myInfo = getLocationController().getSharingLocationInfo(did)) == null || myInfo.mid != messageObject3.getId())) {
                        liveLocation.marker.remove();
                        liveLocation.marker = createUserMarker(new LatLng(messageObject3.messageOwner.media.geo.lat, messageObject3.messageOwner.media.geo._long), getMessagesController().getUser(Integer.valueOf(messageObject3.getFromId())));
                    }
                }
            }
        }
    }

    private class MyAdapter extends PageSelectionAdapter<PoiInfo, ViewHolder> {
        public MyAdapter(Context context) {
            super(context);
        }

        public ViewHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(NewLocationActivity.this.mContext).inflate(R.layout.item_location_info, parent, false));
        }

        public void onBindViewHolderForChild(ViewHolder holder, int position, PoiInfo item) {
            holder.tvLocationName.setText(item.getName());
            holder.tvAddress.setText(item.getAddress());
            double distance = DistanceUtil.getDistance(new LatLng(NewLocationActivity.this.mCurrentLocation.getLatitude(), NewLocationActivity.this.mCurrentLocation.getLongitude()), item.location);
            if (distance <= 100.0d) {
                holder.tvDistance.setText("100m以内");
            } else if (distance <= 100.0d || distance >= 1000.0d) {
                DecimalFormat format = new DecimalFormat("#0.0");
                TextView textView = holder.tvDistance;
                textView.setText(format.format(distance / 1000.0d) + "km");
            } else {
                TextView textView2 = holder.tvDistance;
                textView2.setText(((int) distance) + "m");
            }
            holder.ivSelected.setVisibility(item == NewLocationActivity.this.selectedLocation ? 0 : 4);
        }

        public void loadData(int page) {
            super.loadData(page);
            NewLocationActivity newLocationActivity = NewLocationActivity.this;
            newLocationActivity.search(newLocationActivity.searchType, NewLocationActivity.this.searchKeyword, page, false);
        }
    }

    private class ViewHolder extends PageHolder {
        ImageView ivSelected;
        TextView tvAddress;
        TextView tvDistance;
        TextView tvLocationName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvLocationName = (TextView) itemView.findViewById(R.id.tv_location_name);
            this.tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            this.tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            this.ivSelected = (ImageView) itemView.findViewById(R.id.iv_selected);
        }
    }

    private int getScreenHeight() {
        Display display = ((Activity) this.mContext).getWindowManager().getDefaultDisplay();
        int screenHeight = display.getHeight();
        if (Build.VERSION.SDK_INT < 17 || !Build.BRAND.equalsIgnoreCase("XIAOMI")) {
            return screenHeight;
        }
        boolean isHideNavigationBar = false;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "force_fsg_nav_bar", 0) != 0) {
            isHideNavigationBar = true;
        }
        if (!isHideNavigationBar) {
            return screenHeight;
        }
        Point outSize = new Point();
        display.getRealSize(outSize);
        return outSize.y;
    }
}
