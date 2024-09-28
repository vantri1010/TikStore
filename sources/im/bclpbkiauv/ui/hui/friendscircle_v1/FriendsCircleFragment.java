package im.bclpbkiauv.ui.hui.friendscircle_v1;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcVersionBean;
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
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.adapters.BaseVPAdapter;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcListFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.LazyLoadFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.FcPageListRefreshListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcFollowFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcHomeFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcRecommendFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPublishActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity;
import im.bclpbkiauv.ui.hviews.NoScrollViewPager;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.HashMap;

public class FriendsCircleFragment extends BaseFmts {
    public static int vipLevel = -1;
    private String TAG = FriendsCircleFragment.class.getSimpleName();
    /* access modifiers changed from: private */
    public int currentSelectedPosition = 0;
    /* access modifiers changed from: private */
    public FcPageListRefreshListener fcPageListRefreshListener = new FcPageListRefreshListener() {
        public void onRefreshed(int pageIndex) {
            if (FriendsCircleFragment.this.tabLayout != null && pageIndex < FriendsCircleFragment.this.mFragmentCache.size()) {
                FriendsCircleFragment.this.tabLayout.hideMsg(pageIndex);
                if (FriendsCircleFragment.this.fcVersionBean != null) {
                    if (pageIndex == 0) {
                        FriendsCircleFragment.this.fcVersionBean.setRecommendState(false);
                    } else if (pageIndex == 1) {
                        FriendsCircleFragment.this.fcVersionBean.setFriendState(false);
                    } else if (pageIndex == 2) {
                        FriendsCircleFragment.this.fcVersionBean.setFollowState(false);
                    }
                    if (!FriendsCircleFragment.this.fcVersionBean.isFriendState()) {
                        NotificationCenter.getInstance(FriendsCircleFragment.this.currentAccount).postNotificationName(NotificationCenter.userFriendsCircleUpdate, new Object[0]);
                    }
                }
            }
        }

        public void startFcPublishActivity() {
            startFcPublishActivity();
        }
    };
    /* access modifiers changed from: private */
    public FcVersionBean fcVersionBean;
    /* access modifiers changed from: private */
    public ImagePreSelectorActivity imageSelectorAlert;
    /* access modifiers changed from: private */
    public OnPageSelectedListener listener;
    /* access modifiers changed from: private */
    public LruCache<Integer, CommFcListFragment> mFragmentCache;
    private Observable<BResponse<FcVersionBean>> observable;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.PhotoEntry> photoEntries = new ArrayList<>();
    @BindView(2131297358)
    SlidingTabLayout tabLayout;
    private BaseVPAdapter viewPagerAdapter;
    @BindView(2131297945)
    NoScrollViewPager viewpager;

    public interface OnPageSelectedListener {
        void onPageSelected(int i);
    }

    public FriendsCircleFragment(OnPageSelectedListener listener2) {
        this.listener = listener2;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.fragmentView = LayoutInflater.from(this.context).inflate(R.layout.fragment_friends_ciecle, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initTabLayout();
        initViewPager();
        loadUserInfo();
        return this.fragmentView;
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        super.lazyLoadData();
        LruCache<Integer, CommFcListFragment> lruCache = this.mFragmentCache;
        if (lruCache != null && lruCache.size() > 0) {
            for (int i = 0; i < this.mFragmentCache.size(); i++) {
                LazyLoadFragment commFcListFragment = this.mFragmentCache.get(Integer.valueOf(i));
                if (commFcListFragment != null && commFcListFragment.isAdded()) {
                    commFcListFragment.checkLoadData();
                }
            }
        }
    }

    public void onResumeForBaseFragment() {
        super.onResumeForBaseFragment();
        loadUserInfo();
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

    public void onPauseForBaseFragment() {
        super.onPauseForBaseFragment();
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

    public void onVisible() {
        super.onVisible();
        loadUserInfo();
        LruCache<Integer, CommFcListFragment> lruCache = this.mFragmentCache;
        if (lruCache != null && lruCache.size() > 0) {
            for (int i = 0; i < this.mFragmentCache.size(); i++) {
                LazyLoadFragment commFcListFragment = this.mFragmentCache.get(Integer.valueOf(i));
                if (commFcListFragment != null && commFcListFragment.isAdded() && commFcListFragment.isDataLoaded()) {
                    commFcListFragment.onVisible();
                }
            }
        }
    }

    public void onInvisible() {
        super.onInvisible();
        LruCache<Integer, CommFcListFragment> lruCache = this.mFragmentCache;
        if (lruCache != null && lruCache.size() > 0) {
            for (int i = 0; i < this.mFragmentCache.size(); i++) {
                LazyLoadFragment commFcListFragment = this.mFragmentCache.get(Integer.valueOf(i));
                if (commFcListFragment != null && commFcListFragment.isAdded()) {
                    commFcListFragment.onInvisible();
                }
            }
        }
    }

    private void loadUserInfo() {
    }

    public void onDestroyView() {
        super.onDestroyView();
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        vipLevel = -1;
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

    private void initTabLayout() {
        this.tabLayout.setMsgViewBackgroundColor(Color.parseColor("#FFFA0B0B"));
        this.tabLayout.setMsgViewWidth(6);
    }

    private void initViewPager() {
        this.viewpager.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.viewpager.setEnScroll(true);
        this.viewpager.setOffscreenPageLimit(3);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(LocaleController.getString(R.string.fc_page_title_recommend));
        titles.add(LocaleController.getString(R.string.fc_page_title_friends));
        titles.add(LocaleController.getString(R.string.fc_page_title_follow));
        AnonymousClass1 r1 = new BaseVPAdapter<String>(getChildFragmentManager(), titles) {
            public Fragment getIMItem(int position) {
                if (FriendsCircleFragment.this.mFragmentCache == null) {
                    LruCache unused = FriendsCircleFragment.this.mFragmentCache = new LruCache(getCount());
                }
                CommFcListFragment newF = (CommFcListFragment) FriendsCircleFragment.this.mFragmentCache.get(Integer.valueOf(position));
                if (newF == null) {
                    if (position == 0) {
                        newF = new FcRecommendFragment();
                    } else if (position == 1) {
                        newF = new FcHomeFragment();
                    } else if (position == 2) {
                        newF = new FcFollowFragment();
                    }
                    if (newF != null) {
                        newF.setFcPageListRefreshListener(FriendsCircleFragment.this.fcPageListRefreshListener);
                    }
                    FriendsCircleFragment.this.mFragmentCache.put(Integer.valueOf(position), newF);
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
                int unused = FriendsCircleFragment.this.currentSelectedPosition = position;
                if (FriendsCircleFragment.this.listener != null) {
                    FriendsCircleFragment.this.listener.onPageSelected(position);
                }
                if (FriendsCircleFragment.this.tabLayout != null) {
                    FriendsCircleFragment.this.tabLayout.hideMsg(position);
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.tabLayout.setViewPager(this.viewpager);
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
                if (fcVersionBean2.isFriendState() != 0 && this.currentSelectedPosition != 1) {
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
            AnonymousClass4 r0 = new ImagePreSelectorActivity(getParentActivity()) {
                public void dismissInternal() {
                    if (FriendsCircleFragment.this.imageSelectorAlert.isShowing()) {
                        AndroidUtilities.requestAdjustResize(FriendsCircleFragment.this.getParentActivity(), FriendsCircleFragment.this.classGuid);
                        for (int i = 0; i < FriendsCircleFragment.this.photoEntries.size(); i++) {
                            if (((MediaController.PhotoEntry) FriendsCircleFragment.this.photoEntries.get(i)).isVideo) {
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
                    if (button == 8 || button == 7 || (button == 4 && !FriendsCircleFragment.this.imageSelectorAlert.getSelectedPhotos().isEmpty())) {
                        if (button != 8) {
                            FriendsCircleFragment.this.imageSelectorAlert.dismiss();
                        }
                        HashMap<Object, Object> selectedPhotos = FriendsCircleFragment.this.imageSelectorAlert.getSelectedPhotos();
                        ArrayList<Object> selectedPhotosOrder = FriendsCircleFragment.this.imageSelectorAlert.getSelectedPhotosOrder();
                        int currentSelectMediaType = FriendsCircleFragment.this.imageSelectorAlert.getCurrentSelectMediaType();
                        if (selectedPhotos.isEmpty() || selectedPhotosOrder.isEmpty()) {
                            FriendsCircleFragment.this.presentFragment(new FcPublishActivity());
                            return;
                        }
                        FriendsCircleFragment friendsCircleFragment = FriendsCircleFragment.this;
                        friendsCircleFragment.presentFragment(new FcPublishActivity(friendsCircleFragment.imageSelectorAlert, selectedPhotos, selectedPhotosOrder, currentSelectMediaType));
                    } else if (FriendsCircleFragment.this.imageSelectorAlert != null) {
                        FriendsCircleFragment.this.imageSelectorAlert.dismissWithButtonClick(button);
                        FriendsCircleFragment.this.presentFragment(new FcPublishActivity());
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
                    AndroidUtilities.setAdjustResizeToNothing(FriendsCircleFragment.this.getParentActivity(), FriendsCircleFragment.this.classGuid);
                }
            });
        }
    }
}
