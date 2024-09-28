package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.LocationActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.BaseLocationAdapter;
import im.bclpbkiauv.ui.adapters.LocationActivityAdapter;
import im.bclpbkiauv.ui.adapters.LocationActivitySearchAdapter;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.LocationCell;
import im.bclpbkiauv.ui.cells.LocationLoadingCell;
import im.bclpbkiauv.ui.cells.LocationPoweredCell;
import im.bclpbkiauv.ui.cells.SendLocationCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.MapPlaceholderDrawable;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.ArrayList;
import java.util.Locale;

@Deprecated
public class LocationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int LOCATION_TYPE_GROUP = 4;
    public static final int LOCATION_TYPE_GROUP_VIEW = 5;
    public static final int LOCATION_TYPE_SEND = 0;
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int share = 1;
    /* access modifiers changed from: private */
    public LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private TLRPC.TL_channelLocation chatLocation;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission = true;
    private CircleOptions circleOptions;
    private LocationActivityDelegate delegate;
    private long dialogId;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    private boolean firstFocus = true;
    private boolean firstWas;
    private TLRPC.TL_channelLocation initialLocation;
    /* access modifiers changed from: private */
    public boolean isFirstLocation = true;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ImageView locationButton;
    /* access modifiers changed from: private */
    public int locationType;
    /* access modifiers changed from: private */
    public BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    private MyLocationListener mLocationListener;
    private MapView mMapView;
    /* access modifiers changed from: private */
    public FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private View markerImageView;
    private int markerTop;
    private ArrayList<LiveLocation> markers = new ArrayList<>();
    private SparseArray<LiveLocation> markersMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public MessageObject messageObject;
    /* access modifiers changed from: private */
    public BDLocation myLocation;
    private boolean onResumeCalled;
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    private ChatActivity parentFragment;
    private ImageView routeButton;
    /* access modifiers changed from: private */
    public LocationActivitySearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView searchListView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private Runnable updateRunnable;
    /* access modifiers changed from: private */
    public BDLocation userLocation;
    private boolean userLocationMoved;
    private boolean wasResults;

    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2);
    }

    public class LiveLocation {
        public TLRPC.Chat chat;
        public int id;
        public Marker marker;
        public TLRPC.Message object;
        public TLRPC.User user;

        public LiveLocation() {
        }
    }

    public class Marker {
        public LatLng latLng;
        public Overlay marker;

        public Marker(Overlay marker2, LatLng latLng2) {
            this.marker = marker2;
            this.latLng = latLng2;
        }

        public void setPosition(LatLng latLng2) {
            this.latLng = latLng2;
        }

        public LatLng getPosition() {
            return this.latLng;
        }
    }

    public LocationActivity(int type) {
        this.locationType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        MessageObject messageObject2 = this.messageObject;
        if (messageObject2 == null || !messageObject2.isLiveLocation()) {
            return true;
        }
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.replaceMessagesObjects);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        try {
            if (this.mMapView != null) {
                this.mMapView.onDestroy();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRunnable = null;
        }
    }

    public View createView(Context context) {
        Drawable shadowDrawable;
        TLRPC.Chat chat;
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LocationActivity.this.finishFragment();
                } else if (id == 2) {
                    if (LocationActivity.this.mBaiduMap != null) {
                        LocationActivity.this.mBaiduMap.setMapType(1);
                    }
                } else if (id == 3) {
                    if (LocationActivity.this.mBaiduMap != null) {
                        LocationActivity.this.mBaiduMap.setMapType(2);
                    }
                } else if (id == 1) {
                    try {
                        double lat = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                        double lon = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                        FragmentActivity parentActivity = LocationActivity.this.getParentActivity();
                        parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.chatLocation != null) {
            this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
        } else {
            MessageObject messageObject2 = this.messageObject;
            if (messageObject2 == null) {
                this.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
                if (this.locationType != 4) {
                    menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                        public void onSearchExpand() {
                            boolean unused = LocationActivity.this.searching = true;
                            LocationActivity.this.otherItem.setVisibility(8);
                            LocationActivity.this.listView.setVisibility(8);
                            LocationActivity.this.mapViewClip.setVisibility(8);
                            LocationActivity.this.searchListView.setVisibility(0);
                            LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyView);
                            LocationActivity.this.emptyView.showTextView();
                        }

                        public void onSearchCollapse() {
                            boolean unused = LocationActivity.this.searching = false;
                            boolean unused2 = LocationActivity.this.searchWas = false;
                            LocationActivity.this.otherItem.setVisibility(0);
                            LocationActivity.this.searchListView.setEmptyView((View) null);
                            LocationActivity.this.listView.setVisibility(0);
                            LocationActivity.this.mapViewClip.setVisibility(0);
                            LocationActivity.this.searchListView.setVisibility(8);
                            LocationActivity.this.emptyView.setVisibility(8);
                            LocationActivity.this.searchAdapter.searchDelayed((String) null, (BDLocation) null);
                        }

                        public void onTextChanged(EditText editText) {
                            if (LocationActivity.this.searchAdapter != null) {
                                String text = editText.getText().toString();
                                if (text.length() != 0) {
                                    boolean unused = LocationActivity.this.searchWas = true;
                                }
                                LocationActivity.this.emptyView.showProgress();
                                LocationActivity.this.searchAdapter.searchDelayed(text, LocationActivity.this.userLocation);
                            }
                        }
                    }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
                }
            } else if (messageObject2.isLiveLocation()) {
                this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation));
            } else {
                if (this.messageObject.messageOwner.media.title == null || this.messageObject.messageOwner.media.title.length() <= 0) {
                    this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SharedPlace", R.string.SharedPlace));
                }
                menu.addItem(1, (int) R.drawable.share).setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
            }
        }
        ActionBarMenuItem addItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.otherItem = addItem;
        addItem.addSubItem(2, (int) R.drawable.msg_map, (CharSequence) LocaleController.getString("Map", R.string.Map));
        this.otherItem.addSubItem(3, (int) R.drawable.msg_satellite, (CharSequence) LocaleController.getString("Satellite", R.string.Satellite));
        this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.fragmentView = new FrameLayout(context2) {
            private boolean first = true;

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (changed) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.locationButton = new ImageView(context2);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable2 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable2, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.locationButton.setBackgroundDrawable(drawable);
        this.locationButton.setImageResource(R.drawable.myloc_on);
        this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", R.string.AccDescrMyLocation));
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(animator);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        if (this.chatLocation != null) {
            BDLocation bDLocation = new BDLocation("network");
            this.userLocation = bDLocation;
            bDLocation.setLatitude(this.chatLocation.geo_point.lat);
            this.userLocation.setLongitude(this.chatLocation.geo_point._long);
        } else if (this.messageObject != null) {
            BDLocation bDLocation2 = new BDLocation("network");
            this.userLocation = bDLocation2;
            bDLocation2.setLatitude(this.messageObject.messageOwner.media.geo.lat);
            this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
        }
        this.searchWas = false;
        this.searching = false;
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.mapViewClip = frameLayout2;
        frameLayout2.setBackgroundDrawable(new MapPlaceholderDrawable());
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView2 = this.listView;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context2, this.locationType, this.dialogId);
        this.adapter = locationActivityAdapter2;
        recyclerListView2.setAdapter(locationActivityAdapter2);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.listView;
        AnonymousClass5 r7 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r7;
        recyclerListView3.setLayoutManager(r7);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position;
                if (LocationActivity.this.adapter.getItemCount() != 0 && (position = LocationActivity.this.layoutManager.findFirstVisibleItemPosition()) != -1) {
                    LocationActivity.this.updateClipView(position);
                    if (LocationActivity.this.locationType != 4 && dy > 0 && !LocationActivity.this.adapter.isPulledUp()) {
                        LocationActivity.this.adapter.setPulledUp();
                        if (LocationActivity.this.myLocation != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    LocationActivity.AnonymousClass6.this.lambda$onScrolled$0$LocationActivity$6();
                                }
                            });
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onScrolled$0$LocationActivity$6() {
                LocationActivity.this.adapter.searchPlacesWithQuery((String) null, LocationActivity.this.myLocation, true);
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                LocationActivity.this.lambda$createView$6$LocationActivity(view, i);
            }
        });
        this.adapter.setDelegate(this.dialogId, new BaseLocationAdapter.BaseLocationAdapterDelegate() {
            public final void didLoadedSearchResult(ArrayList arrayList) {
                LocationActivity.this.lambda$createView$7$LocationActivity(arrayList);
            }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        initBaiduMap(context);
        View shadow = new View(context2);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.mapViewClip.addView(shadow, LayoutHelper.createFrame(-1, 3, 83));
        if (this.messageObject == null && this.chatLocation == null) {
            if (!(this.locationType != 4 || this.dialogId == 0 || (chat = getMessagesController().getChat(Integer.valueOf(-((int) this.dialogId)))) == null)) {
                FrameLayout frameLayout1 = new FrameLayout(context2);
                frameLayout1.setBackgroundResource(R.drawable.livepin);
                this.mapViewClip.addView(frameLayout1, LayoutHelper.createFrame(62, 76, 49));
                BackupImageView backupImageView = new BackupImageView(context2);
                backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) new AvatarDrawable(chat), (Object) chat);
                frameLayout1.addView(backupImageView, LayoutHelper.createFrame(52.0f, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                this.markerImageView = frameLayout1;
                frameLayout1.setTag(1);
            }
            if (this.markerImageView == null) {
                ImageView imageView = new ImageView(context2);
                imageView.setImageResource(R.drawable.map_pin2);
                this.mapViewClip.addView(imageView, LayoutHelper.createFrame(28, 48, 49));
                this.markerImageView = imageView;
            }
            EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
            this.emptyView = emptyTextProgressView;
            emptyTextProgressView.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.emptyView.setShowAtCenter(true);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView4 = new RecyclerListView(context2);
            this.searchListView = recyclerListView4;
            recyclerListView4.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            RecyclerListView recyclerListView5 = this.searchListView;
            LocationActivitySearchAdapter locationActivitySearchAdapter2 = new LocationActivitySearchAdapter(context2);
            this.searchAdapter = locationActivitySearchAdapter2;
            recyclerListView5.setAdapter(locationActivitySearchAdapter2);
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    LocationActivity.this.lambda$createView$9$LocationActivity(view, i);
                }
            });
        } else {
            MessageObject messageObject3 = this.messageObject;
            if ((messageObject3 != null && !messageObject3.isLiveLocation()) || this.chatLocation != null) {
                this.routeButton = new ImageView(context2);
                Drawable drawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable shadowDrawable3 = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                    shadowDrawable3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(shadowDrawable3, drawable2, 0, 0);
                    combinedDrawable2.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    shadowDrawable = combinedDrawable2;
                } else {
                    shadowDrawable = drawable2;
                }
                this.routeButton.setBackgroundDrawable(shadowDrawable);
                this.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
                this.routeButton.setImageResource(R.drawable.navigate);
                this.routeButton.setScaleType(ImageView.ScaleType.CENTER);
                if (Build.VERSION.SDK_INT >= 21) {
                    StateListAnimator animator2 = new StateListAnimator();
                    animator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.routeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                    animator2.addState(new int[0], ObjectAnimator.ofFloat(this.routeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                    this.routeButton.setStateListAnimator(animator2);
                    this.routeButton.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View view, Outline outline) {
                            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                        }
                    });
                }
                frameLayout.addView(this.routeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 37.0f));
                this.routeButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        LocationActivity.this.lambda$createView$10$LocationActivity(view);
                    }
                });
                TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
                if (tL_channelLocation != null) {
                    this.adapter.setChatLocation(tL_channelLocation);
                } else {
                    MessageObject messageObject4 = this.messageObject;
                    if (messageObject4 != null) {
                        this.adapter.setMessageObject(messageObject4);
                    }
                }
            }
        }
        MessageObject messageObject5 = this.messageObject;
        if ((messageObject5 == null || messageObject5.isLiveLocation()) && this.chatLocation == null) {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        } else {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 43.0f));
        }
        this.locationButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                LocationActivity.this.lambda$createView$11$LocationActivity(view);
            }
        });
        if (this.messageObject == null && this.chatLocation == null) {
            if (this.initialLocation == null) {
                this.locationButton.setAlpha(0.0f);
            } else {
                this.userLocationMoved = true;
            }
        }
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$6$LocationActivity(View view, int position) {
        MessageObject messageObject2;
        TLRPC.TL_messageMediaVenue venue;
        int i = this.locationType;
        if (i == 4) {
            if (position == 1 && (venue = (TLRPC.TL_messageMediaVenue) this.adapter.getItem(position)) != null) {
                if (this.dialogId == 0) {
                    this.delegate.didSelectLocation(venue, 4, true, 0);
                    finishFragment();
                    return;
                }
                AlertDialog[] progressDialog = {new AlertDialog(getParentActivity(), 3)};
                TLRPC.TL_channels_editLocation req = new TLRPC.TL_channels_editLocation();
                req.address = venue.address;
                req.channel = getMessagesController().getInputChannel(-((int) this.dialogId));
                req.geo_point = new TLRPC.TL_inputGeoPoint();
                req.geo_point.lat = venue.geo.lat;
                req.geo_point._long = venue.geo._long;
                progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog, venue) {
                    private final /* synthetic */ AlertDialog[] f$1;
                    private final /* synthetic */ TLRPC.TL_messageMediaVenue f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocationActivity.this.lambda$null$1$LocationActivity(this.f$1, this.f$2, tLObject, tL_error);
                    }
                })) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        LocationActivity.this.lambda$null$2$LocationActivity(this.f$1, dialogInterface);
                    }
                });
                showDialog(progressDialog[0]);
            }
        } else if (i == 5) {
            if (this.mBaiduMap != null) {
                LatLng ll = new LatLng(this.chatLocation.geo_point.lat, this.chatLocation.geo_point._long);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        } else if (position != 1 || (messageObject2 = this.messageObject) == null || messageObject2.isLiveLocation()) {
            if (position != 1 || this.locationType == 2) {
                if ((position != 2 || this.locationType != 1) && ((position != 1 || this.locationType != 2) && (position != 3 || this.locationType != 3))) {
                    Object object = this.adapter.getItem(position);
                    if (object instanceof TLRPC.TL_messageMediaVenue) {
                        ChatActivity chatActivity = this.parentFragment;
                        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                            this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, true, 0);
                            finishFragment();
                            return;
                        }
                        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(object) {
                            private final /* synthetic */ Object f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void didSelectDate(boolean z, int i) {
                                LocationActivity.this.lambda$null$5$LocationActivity(this.f$1, z, i);
                            }
                        });
                    } else if (object instanceof LiveLocation) {
                        LiveLocation liveLocation = (LiveLocation) object;
                        LatLng ll2 = new LatLng(liveLocation.object.media.geo.lat, liveLocation.object.media.geo._long);
                        MapStatus.Builder builder2 = new MapStatus.Builder();
                        builder2.target(ll2).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                        this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder2.build()));
                    }
                } else if (getLocationController().isSharingLocation(this.dialogId)) {
                    getLocationController().removeSharingLocation(this.dialogId);
                    finishFragment();
                } else if (this.delegate != null && getParentActivity() != null && this.myLocation != null) {
                    TLRPC.User user = null;
                    if (((int) this.dialogId) > 0) {
                        user = getMessagesController().getUser(Integer.valueOf((int) this.dialogId));
                    }
                    showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new MessagesStorage.IntCallback() {
                        public final void run(int i) {
                            LocationActivity.this.lambda$null$4$LocationActivity(i);
                        }
                    }));
                }
            } else if (this.delegate != null && this.userLocation != null) {
                TLRPC.TL_messageMediaGeo location = new TLRPC.TL_messageMediaGeo();
                location.geo = new TLRPC.TL_geoPoint();
                location.geo.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
                location.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
                    this.delegate.didSelectLocation(location, this.locationType, true, 0);
                    finishFragment();
                    return;
                }
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(location) {
                    private final /* synthetic */ TLRPC.TL_messageMediaGeo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void didSelectDate(boolean z, int i) {
                        LocationActivity.this.lambda$null$3$LocationActivity(this.f$1, z, i);
                    }
                });
            }
        } else if (this.mBaiduMap != null) {
            LatLng ll3 = new LatLng(this.messageObject.messageOwner.media.geo.lat, this.messageObject.messageOwner.media.geo._long);
            MapStatus.Builder builder3 = new MapStatus.Builder();
            builder3.target(ll3).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
            this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder3.build()));
        }
    }

    public /* synthetic */ void lambda$null$1$LocationActivity(AlertDialog[] progressDialog, TLRPC.TL_messageMediaVenue venue, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, venue) {
            private final /* synthetic */ AlertDialog[] f$1;
            private final /* synthetic */ TLRPC.TL_messageMediaVenue f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationActivity.this.lambda$null$0$LocationActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$LocationActivity(AlertDialog[] progressDialog, TLRPC.TL_messageMediaVenue venue) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        this.delegate.didSelectLocation(venue, 4, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$2$LocationActivity(int requestId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(requestId, true);
    }

    public /* synthetic */ void lambda$null$3$LocationActivity(TLRPC.TL_messageMediaGeo location, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(location, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$4$LocationActivity(int param) {
        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
        location.geo = new TLRPC.TL_geoPoint();
        location.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        location.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        location.period = param;
        this.delegate.didSelectLocation(location, this.locationType, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$5$LocationActivity(Object object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$7$LocationActivity(ArrayList places) {
        if (!this.wasResults && !places.isEmpty()) {
            this.wasResults = true;
        }
        this.emptyView.showTextView();
    }

    public /* synthetic */ void lambda$createView$9$LocationActivity(View view, int position) {
        TLRPC.TL_messageMediaVenue object = this.searchAdapter.getItem(position);
        if (object != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(object, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(object) {
                private final /* synthetic */ TLRPC.TL_messageMediaVenue f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.this.lambda$null$8$LocationActivity(this.f$1, z, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$8$LocationActivity(TLRPC.TL_messageMediaVenue object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(object, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$10$LocationActivity(View v) {
        Intent intent;
        Activity activity;
        if (Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null && activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) != 0) {
            showPermissionAlert(true);
        } else if (this.myLocation != null) {
            try {
                if (this.messageObject != null) {
                    intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(this.myLocation.getLatitude()), Double.valueOf(this.myLocation.getLongitude()), Double.valueOf(this.messageObject.messageOwner.media.geo.lat), Double.valueOf(this.messageObject.messageOwner.media.geo._long)})));
                } else {
                    intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(this.myLocation.getLatitude()), Double.valueOf(this.myLocation.getLongitude()), Double.valueOf(this.chatLocation.geo_point.lat), Double.valueOf(this.chatLocation.geo_point._long)})));
                }
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$createView$11$LocationActivity(View v) {
        Activity activity;
        if (Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null && activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) != 0) {
            showPermissionAlert(false);
        } else if (this.messageObject != null || this.chatLocation != null) {
            BDLocation bDLocation = this.myLocation;
            if (bDLocation != null && this.mBaiduMap != null) {
                LatLng ll = new LatLng(bDLocation.getLatitude(), this.myLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        } else if (this.myLocation != null && this.mBaiduMap != null) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.setDuration(200);
            animatorSet2.play(ObjectAnimator.ofFloat(this.locationButton, View.ALPHA, new float[]{0.0f}));
            animatorSet2.start();
            this.adapter.setCustomLocation((BDLocation) null);
            this.userLocationMoved = false;
            LatLng ll2 = new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude());
            MapStatus.Builder builder2 = new MapStatus.Builder();
            builder2.target(ll2);
            this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder2.build()));
        }
    }

    private void initBaiduMap(Context context) {
        MapView mapView = new MapView(context);
        this.mMapView = mapView;
        this.mBaiduMap = mapView.getMap();
        this.mMapView.showZoomControls(false);
        this.mMapView.showScaleControl(false);
        this.mBaiduMap.getUiSettings().setCompassEnabled(false);
        this.mBaiduMap.setMapType(1);
        this.mBaiduMap.setMyLocationEnabled(true);
        this.mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            public final void onTouch(MotionEvent motionEvent) {
                LocationActivity.this.lambda$initBaiduMap$12$LocationActivity(motionEvent);
            }
        });
        this.mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            public final void onMapLoaded() {
                LocationActivity.this.lambda$initBaiduMap$13$LocationActivity();
            }
        });
        initLocationClient();
    }

    public /* synthetic */ void lambda$initBaiduMap$12$LocationActivity(MotionEvent motionEvent) {
        BDLocation bDLocation;
        if (this.messageObject == null && this.chatLocation == null) {
            if (motionEvent.getAction() == 0) {
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.setDuration(200);
                this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (this.markerTop - AndroidUtilities.dp(10.0f))})});
                this.animatorSet.start();
            } else if (motionEvent.getAction() == 1) {
                AnimatorSet animatorSet4 = this.animatorSet;
                if (animatorSet4 != null) {
                    animatorSet4.cancel();
                }
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.animatorSet = animatorSet5;
                animatorSet5.setDuration(200);
                this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.markerImageView, View.TRANSLATION_Y, new float[]{(float) this.markerTop})});
                this.animatorSet.start();
                this.adapter.fetchLocationAddress();
            }
            if (motionEvent.getAction() == 2) {
                if (!this.userLocationMoved) {
                    AnimatorSet animatorSet6 = new AnimatorSet();
                    animatorSet6.setDuration(200);
                    animatorSet6.play(ObjectAnimator.ofFloat(this.locationButton, View.ALPHA, new float[]{1.0f}));
                    animatorSet6.start();
                    this.userLocationMoved = true;
                }
                BaiduMap baiduMap = this.mBaiduMap;
                if (!(baiduMap == null || (bDLocation = this.userLocation) == null)) {
                    bDLocation.setLatitude(baiduMap.getLocationData().latitude);
                    this.userLocation.setLongitude(this.mBaiduMap.getLocationData().longitude);
                }
                this.adapter.setCustomLocation(this.userLocation);
            }
        }
    }

    public /* synthetic */ void lambda$initBaiduMap$13$LocationActivity() {
        if (this.mMapView != null && getParentActivity() != null) {
            this.mBaiduMap.setViewPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
            onMapInit();
            this.mapsInitialized = true;
            if (this.onResumeCalled) {
                this.mMapView.onResume();
            }
        }
    }

    private void initLocationClient() {
        LocationClient locationClient = new LocationClient(getParentActivity());
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

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        TLRPC.FileLocation photo;
        LiveLocation liveLocation2 = liveLocation;
        Bitmap result = null;
        try {
            if (liveLocation2.user != null && liveLocation2.user.photo != null) {
                photo = liveLocation2.user.photo.photo_small;
            } else if (liveLocation2.chat == null || liveLocation2.chat.photo == null) {
                photo = null;
            } else {
                photo = liveLocation2.chat.photo.photo_small;
            }
            result = Bitmap.createBitmap(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f), Bitmap.Config.ARGB_8888);
            result.eraseColor(0);
            Canvas canvas = new Canvas(result);
            Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f));
            drawable.draw(canvas);
            Paint roundPaint = new Paint(1);
            RectF bitmapRect = new RectF();
            canvas.save();
            if (photo != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                if (bitmap != null) {
                    BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Matrix matrix = new Matrix();
                    float scale = ((float) AndroidUtilities.dp(52.0f)) / ((float) bitmap.getWidth());
                    matrix.postTranslate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                    matrix.postScale(scale, scale);
                    roundPaint.setShader(shader);
                    shader.setLocalMatrix(matrix);
                    bitmapRect.set((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(57.0f), (float) AndroidUtilities.dp(57.0f));
                    canvas.drawRoundRect(bitmapRect, (float) AndroidUtilities.dp(26.0f), (float) AndroidUtilities.dp(26.0f), roundPaint);
                }
            } else {
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                if (liveLocation2.user != null) {
                    avatarDrawable2.setInfo(liveLocation2.user);
                } else if (liveLocation2.chat != null) {
                    avatarDrawable2.setInfo(liveLocation2.chat);
                }
                canvas.translate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                avatarDrawable2.setBounds(0, 0, AndroidUtilities.dp(52.2f), AndroidUtilities.dp(52.2f));
                avatarDrawable2.draw(canvas);
            }
            canvas.restore();
            try {
                canvas.setBitmap((Bitmap) null);
            } catch (Exception e) {
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        return result;
    }

    private int getMessageId(TLRPC.Message message) {
        if (message.from_id != 0) {
            return message.from_id;
        }
        return (int) MessageObject.getDialogId(message);
    }

    private LiveLocation addUserMarker(TLRPC.Message message) {
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
            liveLocation2.marker = createUserMarker(latLng, liveLocation2);
            this.markers.add(liveLocation2);
            this.markersMap.put(liveLocation2.id, liveLocation2);
            LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (liveLocation2.id == getUserConfig().getClientUserId() && myInfo != null && liveLocation2.object.id == myInfo.mid && this.myLocation != null) {
                liveLocation2.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
            }
        } else {
            liveLocation2.object = message;
            liveLocation2.marker.setPosition(latLng);
        }
        return liveLocation2;
    }

    private LiveLocation addUserMarker(TLRPC.TL_channelLocation location) {
        LatLng latLng = new LatLng(location.geo_point.lat, location.geo_point._long);
        LiveLocation liveLocation = new LiveLocation();
        int did = (int) this.dialogId;
        if (did > 0) {
            liveLocation.user = getMessagesController().getUser(Integer.valueOf(did));
            liveLocation.id = did;
        } else {
            liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-did));
            liveLocation.id = did;
        }
        liveLocation.marker = createUserMarker(latLng, liveLocation);
        this.markers.add(liveLocation);
        this.markersMap.put(liveLocation.id, liveLocation);
        return liveLocation;
    }

    private Marker addLocMarker(LatLng ll) {
        return new Marker(this.mBaiduMap.addOverlay(new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin2)).draggable(false).flat(false)), ll);
    }

    private Marker createUserMarker(LatLng ll, LiveLocation location) {
        return new Marker(this.mBaiduMap.addOverlay(new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap(location))).draggable(false).flat(false).anchor(0.5f, 0.907f)), ll);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        public MyLocationListener() {
        }

        public void onReceiveLocation(BDLocation bdLocation) {
            LocationActivity.this.positionMarker(bdLocation);
            LocationActivity.this.getLocationController().setBaiduMapLocation(bdLocation, LocationActivity.this.isFirstLocation);
            boolean unused = LocationActivity.this.isFirstLocation = false;
        }
    }

    private void onMapInit() {
        if (this.mBaiduMap != null) {
            TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
            if (tL_channelLocation != null) {
                LiveLocation liveLocation = addUserMarker(tL_channelLocation);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(liveLocation.marker.getPosition()).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            } else {
                MessageObject messageObject2 = this.messageObject;
                if (messageObject2 == null) {
                    BDLocation bDLocation = new BDLocation("network");
                    this.userLocation = bDLocation;
                    TLRPC.TL_channelLocation tL_channelLocation2 = this.initialLocation;
                    if (tL_channelLocation2 != null) {
                        LatLng latLng = new LatLng(tL_channelLocation2.geo_point.lat, this.initialLocation.geo_point._long);
                        MapStatus.Builder builder2 = new MapStatus.Builder();
                        builder2.target(latLng).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                        this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder2.build()));
                        this.userLocation.setLatitude(this.initialLocation.geo_point.lat);
                        this.userLocation.setLongitude(this.initialLocation.geo_point._long);
                        this.adapter.setCustomLocation(this.userLocation);
                    } else {
                        bDLocation.setLatitude(20.659322d);
                        this.userLocation.setLongitude(-11.40625d);
                    }
                } else if (messageObject2.isLiveLocation()) {
                    LiveLocation liveLocation2 = addUserMarker(this.messageObject.messageOwner);
                    if (!getRecentLocations()) {
                        MapStatus.Builder builder3 = new MapStatus.Builder();
                        builder3.target(liveLocation2.marker.getPosition()).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                        this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder3.build()));
                    }
                } else {
                    LatLng latLng2 = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                    try {
                        addLocMarker(latLng2);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    MapStatus.Builder builder4 = new MapStatus.Builder();
                    builder4.target(latLng2).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                    this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder4.build()));
                    this.firstFocus = false;
                    getRecentLocations();
                }
            }
            BDLocation lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            AlertDialog.Builder builder5 = new AlertDialog.Builder((Context) getParentActivity());
                            builder5.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder5.setMessage(LocaleController.getString("GpsDisabledAlert", R.string.GpsDisabledAlert));
                            builder5.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    LocationActivity.this.lambda$onMapInit$14$LocationActivity(dialogInterface, i);
                                }
                            });
                            builder5.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            showDialog(builder5.create());
                        }
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onMapInit$14$LocationActivity(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
            }
        }
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
                    LocationActivity.this.lambda$showPermissionAlert$15$LocationActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$15$LocationActivity(DialogInterface dialog, int which) {
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

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            try {
                if (this.mMapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mMapView.getParent()).removeView(this.mMapView);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            FrameLayout frameLayout = this.mapViewClip;
            if (frameLayout != null) {
                frameLayout.addView(this.mMapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
            } else if (this.fragmentView != null) {
                ((FrameLayout) this.fragmentView).addView(this.mMapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateClipView(int firstVisibleItem) {
        if (firstVisibleItem != -1) {
            int height = 0;
            int top = 0;
            View child = this.listView.getChildAt(0);
            if (child != null) {
                if (firstVisibleItem == 0) {
                    top = child.getTop();
                    height = this.overScrollHeight + (top < 0 ? top : 0);
                }
                if (((FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
                    if (height <= 0) {
                        if (this.mMapView.getVisibility() == 0) {
                            this.mMapView.setVisibility(4);
                            this.mapViewClip.setVisibility(4);
                        }
                    } else if (this.mMapView.getVisibility() == 4) {
                        this.mMapView.setVisibility(0);
                        this.mapViewClip.setVisibility(0);
                    }
                    this.mapViewClip.setTranslationY((float) Math.min(0, top));
                    this.mMapView.setTranslationY((float) Math.max(0, (-top) / 2));
                    View view = this.markerImageView;
                    if (view != null) {
                        int dp = ((-top) - AndroidUtilities.dp(view.getTag() == null ? 48.0f : 69.0f)) + (height / 2);
                        this.markerTop = dp;
                        view.setTranslationY((float) dp);
                    }
                    ImageView imageView = this.routeButton;
                    if (imageView != null) {
                        imageView.setTranslationY((float) top);
                    }
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mMapView.getLayoutParams();
                    if (layoutParams != null && layoutParams.height != this.overScrollHeight + AndroidUtilities.dp(10.0f)) {
                        layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                        BaiduMap baiduMap = this.mBaiduMap;
                        if (baiduMap != null) {
                            baiduMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                        }
                        this.mMapView.setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal(boolean resume) {
        if (this.listView != null) {
            int height = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int viewHeight = this.fragmentView.getMeasuredHeight();
            if (viewHeight != 0) {
                this.overScrollHeight = (viewHeight - AndroidUtilities.dp(66.0f)) - height;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
                layoutParams.topMargin = height;
                this.listView.setLayoutParams(layoutParams);
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams2.topMargin = height;
                layoutParams2.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams2);
                RecyclerListView recyclerListView = this.searchListView;
                if (recyclerListView != null) {
                    FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
                    layoutParams3.topMargin = height;
                    this.searchListView.setLayoutParams(layoutParams3);
                }
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.mMapView.getLayoutParams();
                if (layoutParams4 != null) {
                    layoutParams4.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    BaiduMap baiduMap = this.mBaiduMap;
                    if (baiduMap != null) {
                        baiduMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mMapView.setLayoutParams(layoutParams4);
                }
                this.adapter.notifyDataSetChanged();
                if (resume) {
                    LinearLayoutManager linearLayoutManager = this.layoutManager;
                    int i = this.locationType;
                    linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + ((i == 1 || i == 2) ? 66 : 0))));
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(new Runnable() {
                        public final void run() {
                            LocationActivity.this.lambda$fixLayoutInternal$16$LocationActivity();
                        }
                    });
                    return;
                }
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
            }
        }
    }

    public /* synthetic */ void lambda$fixLayoutInternal$16$LocationActivity() {
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        int i = this.locationType;
        linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + ((i == 1 || i == 2) ? 66 : 0))));
        updateClipView(this.layoutManager.findFirstVisibleItemPosition());
    }

    private BDLocation getLastLocation() {
        return this.mLocClient.getLastKnownLocation();
    }

    /* access modifiers changed from: private */
    public void positionMarker(BDLocation location) {
        if (location != null) {
            this.myLocation = new BDLocation(location);
            LiveLocation liveLocation = this.markersMap.get(getUserConfig().getClientUserId());
            LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || myInfo == null || liveLocation.object.id != myInfo.mid)) {
                liveLocation.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            if (this.messageObject == null && this.chatLocation == null && this.mBaiduMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                LocationActivityAdapter locationActivityAdapter = this.adapter;
                if (locationActivityAdapter != null) {
                    if (locationActivityAdapter.isPulledUp()) {
                        this.adapter.searchPlacesWithQuery((String) null, this.myLocation, true);
                    }
                    this.adapter.setGpsLocation(this.myLocation);
                }
                if (!this.userLocationMoved) {
                    this.userLocation = new BDLocation(location);
                    if (this.firstWas) {
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(latLng).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                        this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        return;
                    }
                    this.firstWas = true;
                    MapStatus.Builder builder2 = new MapStatus.Builder();
                    builder2.target(latLng).zoom(this.mBaiduMap.getMaxZoomLevel() - 4.0f);
                    this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder2.build()));
                    return;
                }
                return;
            }
            this.adapter.setGpsLocation(this.myLocation);
        }
    }

    public void setMessageObject(MessageObject message) {
        this.messageObject = message;
        this.dialogId = message.getDialogId();
    }

    public void setChatLocation(int chatId, TLRPC.TL_channelLocation location) {
        this.dialogId = (long) (-chatId);
        this.chatLocation = location;
    }

    public void setDialogId(long did) {
        this.dialogId = did;
    }

    public void setInitialLocation(TLRPC.TL_channelLocation location) {
        this.initialLocation = location;
    }

    private void fetchRecentLocations(ArrayList<TLRPC.Message> messages) {
        LatLngBounds.Builder builder = null;
        if (this.firstFocus) {
            builder = new LatLngBounds.Builder();
        }
        int date = getConnectionsManager().getCurrentTime();
        for (int a = 0; a < messages.size(); a++) {
            TLRPC.Message message = messages.get(a);
            if (message.date + message.media.period > date) {
                if (builder != null) {
                    builder.include(new LatLng(message.media.geo.lat, message.media.geo._long));
                }
                addUserMarker(message);
            }
        }
        if (builder != null) {
            this.firstFocus = false;
            this.adapter.setLiveLocations(this.markers);
            if (this.messageObject.isLiveLocation()) {
                try {
                    LatLngBounds bounds = builder.build();
                    if (messages.size() > 1) {
                        try {
                            this.mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds, AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f)));
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    private boolean getRecentLocations() {
        ArrayList<TLRPC.Message> messages = getLocationController().locationsCache.get(this.messageObject.getDialogId());
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
        long dialog_id = this.messageObject.getDialogId();
        req.peer = getMessagesController().getInputPeer((int) dialog_id);
        req.limit = 100;
        getConnectionsManager().sendRequest(req, new RequestDelegate(dialog_id) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocationActivity.this.lambda$getRecentLocations$18$LocationActivity(this.f$1, tLObject, tL_error);
            }
        });
        if (messages != null) {
            return true;
        }
        return false;
    }

    public /* synthetic */ void lambda$getRecentLocations$18$LocationActivity(long dialog_id, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(response, dialog_id) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LocationActivity.this.lambda$null$17$LocationActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$17$LocationActivity(TLObject response, long dialog_id) {
        if (this.mBaiduMap != null) {
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
            getLocationController().locationsCache.put(dialog_id, res.messages);
            getNotificationCenter().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(dialog_id));
            fetchRecentLocations(res.messages);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        LocationActivityAdapter locationActivityAdapter;
        LiveLocation liveLocation;
        LocationActivityAdapter locationActivityAdapter2;
        int i = id;
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.locationPermissionGranted) {
            BaiduMap baiduMap = this.mBaiduMap;
            if (baiduMap != null) {
                try {
                    baiduMap.setMyLocationEnabled(true);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue() && args[0].longValue() == this.dialogId && this.messageObject != null) {
                ArrayList<MessageObject> arr = args[1];
                boolean added = false;
                for (int a = 0; a < arr.size(); a++) {
                    MessageObject messageObject2 = arr.get(a);
                    if (messageObject2.isLiveLocation()) {
                        addUserMarker(messageObject2.messageOwner);
                        added = true;
                    }
                }
                if (added && (locationActivityAdapter2 = this.adapter) != null) {
                    locationActivityAdapter2.setLiveLocations(this.markers);
                }
            }
        } else if (i == NotificationCenter.replaceMessagesObjects) {
            long did = args[0].longValue();
            if (did == this.dialogId && this.messageObject != null) {
                boolean updated = false;
                ArrayList<MessageObject> messageObjects = args[1];
                for (int a2 = 0; a2 < messageObjects.size(); a2++) {
                    MessageObject messageObject3 = messageObjects.get(a2);
                    if (messageObject3.isLiveLocation() && (liveLocation = this.markersMap.get(getMessageId(messageObject3.messageOwner))) != null) {
                        LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(did);
                        if (myInfo == null || myInfo.mid != messageObject3.getId()) {
                            liveLocation.marker.setPosition(new LatLng(messageObject3.messageOwner.media.geo.lat, messageObject3.messageOwner.media.geo._long));
                        }
                        updated = true;
                    }
                }
                if (updated && (locationActivityAdapter = this.adapter) != null) {
                    locationActivityAdapter.updateLiveLocations();
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        MapView mapView = this.mMapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onPause();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        this.onResumeCalled = false;
    }

    public void onResume() {
        Activity activity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        MapView mapView = this.mMapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onResume();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = true;
        BaiduMap baiduMap = this.mBaiduMap;
        if (baiduMap != null) {
            try {
                baiduMap.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        fixLayoutInternal(true);
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) != 0) {
                activity.requestPermissions(new String[]{PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
    }

    public void setDelegate(LocationActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    private void updateSearchInterface() {
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = $$Lambda$LocationActivity$uCAtyMCa_pym2O3DVm2EHxs9VXg.INSTANCE;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionIcon), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionBackground), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionPressedBackground), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionIcon), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionPressedBackground), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, themeDescriptionDelegate, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_liveLocationProgress), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_location_placeLocationBackground), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialog_liveLocationProgress), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_location_sendLocationIcon), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_location_sendLiveLocationIcon), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_location_sendLocationBackground), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_location_sendLiveLocationBackground), new ThemeDescription((View) this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText2), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueText7), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3)};
    }

    static /* synthetic */ void lambda$getThemeDescriptions$19() {
    }
}
