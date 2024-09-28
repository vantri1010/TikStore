package im.bclpbkiauv.ui.hui.friendscircle_v1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcVersionBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import com.bjz.comm.net.utils.RxHelper;
import com.google.gson.Gson;
import com.tablayout.SlidingTabLayout;
import im.bclpbkiauv.javaBean.fc.PublishFcBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.BaseVPAdapter;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcListFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.LazyLoadFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.FcPageListRefreshListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcFollowFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcHomeFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcRecommendFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcFollowedManageActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPublishActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity;
import im.bclpbkiauv.ui.hviews.NoScrollViewPager;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.HashMap;

public class FriendsCircleActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int BTN_MANAGE_FOLLOED_USER = 0;
    /* access modifiers changed from: private */
    public int BTN_PUBLISH = 1;
    private String TAG = FriendsCircleActivity.class.getSimpleName();
    @BindView(2131296493)
    View containerTab;
    /* access modifiers changed from: private */
    public int currentSelectedPosition = 0;
    /* access modifiers changed from: private */
    public FcPageListRefreshListener fcPageListRefreshListener = new FcPageListRefreshListener() {
        public void onRefreshed(int pageIndex) {
            if (FriendsCircleActivity.this.tabLayout != null && pageIndex < FriendsCircleActivity.this.mFragmentCache.size()) {
                FriendsCircleActivity.this.tabLayout.hideMsg(pageIndex);
                if (FriendsCircleActivity.this.fcVersionBean != null) {
                    if (pageIndex == 0) {
                        FriendsCircleActivity.this.fcVersionBean.setRecommendState(false);
                    } else if (pageIndex == 1) {
                        FriendsCircleActivity.this.fcVersionBean.setFriendState(false);
                    } else if (pageIndex == 2) {
                        FriendsCircleActivity.this.fcVersionBean.setFollowState(false);
                    }
                    if (!FriendsCircleActivity.this.fcVersionBean.isFriendState()) {
                        NotificationCenter.getInstance(FriendsCircleActivity.this.currentAccount).postNotificationName(NotificationCenter.userFriendsCircleUpdate, new Object[0]);
                    }
                }
            }
        }

        public void startFcPublishActivity() {
            FriendsCircleActivity.this.startPublishActivity();
        }
    };
    /* access modifiers changed from: private */
    public FcVersionBean fcVersionBean;
    /* access modifiers changed from: private */
    public ImagePreSelectorActivity imageSelectorAlert;
    /* access modifiers changed from: private */
    public LruCache<Integer, CommFcListFragment> mFragmentCache;
    /* access modifiers changed from: private */
    public ActionBarMenuItem manageItem;
    /* access modifiers changed from: private */
    public ActionBarMenu menu;
    private Observable<BResponse<FcVersionBean>> observable;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.PhotoEntry> photoEntries = new ArrayList<>();
    /* access modifiers changed from: private */
    public ActionBarMenuItem publishItem;
    @BindView(2131297358)
    SlidingTabLayout tabLayout;
    private BaseVPAdapter viewPagerAdapter;
    @BindView(2131297945)
    NoScrollViewPager viewpager;

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_friends_ciecle, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        VideoPlayerManager.getInstance().setVolume(0);
        useButterKnife();
        initActionBar();
        initTabLayout();
        initViewPager();
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFriendsCircleUpdate);
        return super.onFragmentCreate();
    }

    public void onResume() {
        super.onResume();
        queryFcVersion();
        LruCache<Integer, CommFcListFragment> lruCache = this.mFragmentCache;
        if (lruCache != null && lruCache.size() > 0) {
            for (int i = 0; i < this.mFragmentCache.size(); i++) {
                LazyLoadFragment commFcListFragment = this.mFragmentCache.get(Integer.valueOf(i));
                if (commFcListFragment != null && commFcListFragment.isAdded()) {
                    commFcListFragment.onResumeForBaseFragment();
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        LruCache<Integer, CommFcListFragment> lruCache = this.mFragmentCache;
        if (lruCache != null && lruCache.size() > 0) {
            for (int i = 0; i < this.mFragmentCache.size(); i++) {
                LazyLoadFragment commFcListFragment = this.mFragmentCache.get(Integer.valueOf(i));
                if (commFcListFragment != null && commFcListFragment.isAdded()) {
                    commFcListFragment.onPauseForBaseFragment();
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        CommFcListFragment commFcListFragment;
        if (id == NotificationCenter.userFriendsCircleUpdate && this.tabLayout != null && this.mFragmentCache != null && args.length != 0 && (args[0] instanceof TLRPC.TL_updateUserMomentStateV1)) {
            TLRPC.TL_updateUserMomentStateV1 userMomentStateV1 = args[0];
            if (userMomentStateV1.type != 2 || userMomentStateV1.user_id == 0) {
                this.tabLayout.hideMsg(1);
                return;
            }
            if (this.mFragmentCache.size() > 1 && (commFcListFragment = this.mFragmentCache.get(1)) != null) {
                FcVersionBean fcVersionBean2 = commFcListFragment.getFcVersionBean();
                fcVersionBean2.setFriendState(true);
                commFcListFragment.setFcVersionBean(fcVersionBean2);
            }
            this.tabLayout.showDot(1);
        }
    }

    private void queryFcVersion() {
        this.observable = ApiFactory.getInstance().getApiMomentForum().checkVersion();
        RxHelper.getInstance().sendRequest(this.TAG, this.observable, new Consumer<BResponse<FcVersionBean>>() {
            public void accept(BResponse<FcVersionBean> response) throws Exception {
                FriendsCircleActivity.this.refreshFcVersionData((FcVersionBean) response.Data);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
            }
        });
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFriendsCircleUpdate);
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        if (this.observable != null) {
            RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(this.TAG);
            this.observable = null;
        }
        BaseVPAdapter baseVPAdapter = this.viewPagerAdapter;
        if (baseVPAdapter != null) {
            baseVPAdapter.destroy();
            this.viewPagerAdapter = null;
        }
    }

    private void initActionBar() {
        this.actionBar = createActionBar(getParentActivity());
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FriendsCircleActivity.this.finishFragment();
                } else if (id == FriendsCircleActivity.this.BTN_PUBLISH && (FriendsCircleActivity.this.currentSelectedPosition == 0 || FriendsCircleActivity.this.currentSelectedPosition == 1)) {
                    FriendsCircleActivity.this.startPublishActivity();
                } else if (id == FriendsCircleActivity.this.BTN_MANAGE_FOLLOED_USER && FriendsCircleActivity.this.currentSelectedPosition == 2) {
                    FriendsCircleActivity.this.presentFragment(new FcFollowedManageActivity());
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.menu = createMenu;
        ActionBarMenuItem addItemWithWidth = createMenu.addItemWithWidth(this.BTN_MANAGE_FOLLOED_USER, R.mipmap.ic_fc_menu_manage_followed_user, AndroidUtilities.dp(40.0f), 0, 14);
        this.manageItem = addItemWithWidth;
        ((ImageView) addItemWithWidth.getContentView()).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.SRC_IN));
        ActionBarMenuItem addItemWithWidth2 = this.menu.addItemWithWidth(this.BTN_PUBLISH, R.mipmap.ic_fc_publish_blue, AndroidUtilities.dp(40.0f), 0, 14);
        this.publishItem = addItemWithWidth2;
        ((ImageView) addItemWithWidth2.getContentView()).clearColorFilter();
        this.manageItem.setVisibility(8);
    }

    private void initTabLayout() {
        ((ViewGroup) this.fragmentView).removeView(this.containerTab);
        this.tabLayout.setMsgViewBackgroundColor(Color.parseColor("#FFFA0B0B"));
        this.tabLayout.setMsgViewWidth(6);
        if (!Theme.getCurrentTheme().isLight()) {
            this.tabLayout.setTextSelectColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.tabLayout.setTextUnselectColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        }
        this.actionBar.addView(this.containerTab, LayoutHelper.createFrame(-1.0f, -2.0f, 81, 68.0f, 0.0f, 68.0f, 0.0f));
        View view = new View(getParentActivity());
        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.actionBar.addView(view, LayoutHelper.createFrame(-1.0f, 0.5f, 80));
    }

    private void initViewPager() {
        this.viewpager.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.viewpager.setEnScroll(true);
        this.viewpager.setOffscreenPageLimit(3);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(LocaleController.getString(R.string.fc_page_title_recommend));
        titles.add(LocaleController.getString(R.string.fc_page_title_friends));
        titles.add(LocaleController.getString(R.string.fc_page_title_follow));
        AnonymousClass4 r1 = new BaseVPAdapter<String>(getParentActivity().getSupportFragmentManager(), titles) {
            public Fragment getIMItem(int position) {
                if (FriendsCircleActivity.this.mFragmentCache == null) {
                    LruCache unused = FriendsCircleActivity.this.mFragmentCache = new LruCache(getCount());
                }
                CommFcListFragment newF = (CommFcListFragment) FriendsCircleActivity.this.mFragmentCache.get(Integer.valueOf(position));
                if (newF == null) {
                    if (position == 0) {
                        newF = new FcRecommendFragment();
                    } else if (position == 1) {
                        newF = new FcHomeFragment();
                    } else if (position == 2) {
                        newF = new FcFollowFragment();
                    }
                    if (newF != null) {
                        newF.setFcPageListRefreshListener(FriendsCircleActivity.this.fcPageListRefreshListener);
                    }
                    FriendsCircleActivity.this.mFragmentCache.put(Integer.valueOf(position), newF);
                }
                return newF;
            }
        };
        this.viewPagerAdapter = r1;
        this.viewpager.setAdapter(r1);
        this.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                int unused = FriendsCircleActivity.this.currentSelectedPosition = position;
                if (FriendsCircleActivity.this.menu != null) {
                    if (position == 2) {
                        FriendsCircleActivity.this.manageItem.setVisibility(0);
                        FriendsCircleActivity.this.publishItem.setVisibility(8);
                    } else {
                        FriendsCircleActivity.this.manageItem.setVisibility(8);
                        FriendsCircleActivity.this.publishItem.setVisibility(0);
                    }
                }
                if (FriendsCircleActivity.this.tabLayout != null) {
                    FriendsCircleActivity.this.tabLayout.hideMsg(position);
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.tabLayout.setViewPager(this.viewpager);
    }

    public boolean canBeginSlide() {
        NoScrollViewPager noScrollViewPager = this.viewpager;
        return noScrollViewPager != null && noScrollViewPager.getCurrentItem() == 0;
    }

    public void refreshFcVersionData(FcVersionBean fcVersionBean2) {
        SlidingTabLayout slidingTabLayout = this.tabLayout;
        if (slidingTabLayout != null && this.mFragmentCache != null) {
            this.fcVersionBean = fcVersionBean2;
            if (fcVersionBean2 != null) {
                for (int i = 0; i < this.mFragmentCache.size(); i++) {
                    CommFcListFragment commFcListFragment = this.mFragmentCache.get(Integer.valueOf(i));
                    if (commFcListFragment != null) {
                        commFcListFragment.setFcVersionBean(fcVersionBean2);
                    }
                }
                if (fcVersionBean2.isFriendState() != 0) {
                    this.tabLayout.showDot(1);
                    return;
                }
                return;
            }
            slidingTabLayout.hideMsg(0);
            this.tabLayout.hideMsg(1);
            this.tabLayout.hideMsg(2);
            for (int i2 = 0; i2 < this.mFragmentCache.size(); i2++) {
                CommFcListFragment commFcListFragment2 = this.mFragmentCache.get(Integer.valueOf(i2));
                if (commFcListFragment2 != null) {
                    commFcListFragment2.setFcVersionBean(new FcVersionBean());
                }
            }
        }
    }

    public void startPublishActivity() {
        String publishJson = AppPreferenceUtil.getString("PublishFcBean", "");
        if (!TextUtils.isEmpty(publishJson)) {
            PublishFcBean publishFcBean = (PublishFcBean) new Gson().fromJson(publishJson, PublishFcBean.class);
            if (publishFcBean != null) {
                presentFragment(new FcPublishActivity(publishFcBean));
                AppPreferenceUtil.putString("PublishFcBean", "");
                return;
            }
            openAttachMenu();
            return;
        }
        openAttachMenu();
    }

    private void openAttachMenu() {
        createChatAttachView();
        this.imageSelectorAlert.setCurrentSelectMediaType(0);
        this.imageSelectorAlert.loadGalleryPhotos();
        this.imageSelectorAlert.setMaxSelectedPhotos(9, true);
        this.imageSelectorAlert.init();
        this.imageSelectorAlert.setCancelable(false);
        showDialog(this.imageSelectorAlert);
    }

    private void createChatAttachView() {
        if (this.imageSelectorAlert == null) {
            AnonymousClass7 r0 = new ImagePreSelectorActivity(getParentActivity()) {
                public void dismissInternal() {
                    if (FriendsCircleActivity.this.imageSelectorAlert.isShowing()) {
                        AndroidUtilities.requestAdjustResize(FriendsCircleActivity.this.getParentActivity(), FriendsCircleActivity.this.classGuid);
                        for (int i = 0; i < FriendsCircleActivity.this.photoEntries.size(); i++) {
                            if (((MediaController.PhotoEntry) FriendsCircleActivity.this.photoEntries.get(i)).isVideo) {
                                super.dismissInternal();
                                return;
                            }
                        }
                    }
                    super.dismissInternal();
                }
            };
            this.imageSelectorAlert = r0;
            r0.setDelegate(new ImagePreSelectorActivity.ChatAttachViewDelegate() {
                public void didPressedButton(int button, boolean arg, boolean notify, int scheduleDate) {
                    if (button == 8 || button == 7 || (button == 4 && !FriendsCircleActivity.this.imageSelectorAlert.getSelectedPhotos().isEmpty())) {
                        if (button != 8) {
                            FriendsCircleActivity.this.imageSelectorAlert.dismiss();
                        }
                        HashMap<Object, Object> selectedPhotos = FriendsCircleActivity.this.imageSelectorAlert.getSelectedPhotos();
                        ArrayList<Object> selectedPhotosOrder = FriendsCircleActivity.this.imageSelectorAlert.getSelectedPhotosOrder();
                        int currentSelectMediaType = FriendsCircleActivity.this.imageSelectorAlert.getCurrentSelectMediaType();
                        if (selectedPhotos.isEmpty() || selectedPhotosOrder.isEmpty()) {
                            FriendsCircleActivity.this.presentFragment(new FcPublishActivity());
                            return;
                        }
                        FriendsCircleActivity friendsCircleActivity = FriendsCircleActivity.this;
                        friendsCircleActivity.presentFragment(new FcPublishActivity(friendsCircleActivity.imageSelectorAlert, selectedPhotos, selectedPhotosOrder, currentSelectMediaType));
                    } else if (FriendsCircleActivity.this.imageSelectorAlert != null) {
                        FriendsCircleActivity.this.imageSelectorAlert.dismissWithButtonClick(button);
                        FriendsCircleActivity.this.presentFragment(new FcPublishActivity());
                    }
                }

                public void didSelectBot(TLRPC.User user) {
                }

                public void onCameraOpened() {
                }

                public View getRevealView() {
                    return null;
                }

                public void needEnterComment() {
                    AndroidUtilities.setAdjustResizeToNothing(FriendsCircleActivity.this.getParentActivity(), FriendsCircleActivity.this.classGuid);
                }
            });
        }
    }
}
