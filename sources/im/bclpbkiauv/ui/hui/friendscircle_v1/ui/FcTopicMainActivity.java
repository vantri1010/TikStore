package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcPageMinePresenter;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.socks.library.KLog;
import im.bclpbkiauv.javaBean.fc.FcLocationInfoBean;
import im.bclpbkiauv.javaBean.fc.PublishFcBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.dialogs.FcDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeItemReplyAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcListActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.decoration.SpacesItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.state.ScreenViewState;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.AutoPlayTool;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.FcDialogUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FcTopicMainActivity extends CommFcListActivity implements NotificationCenter.NotificationCenterDelegate, FcItemActionClickListener, BaseFcContract.IFcPageMineView {
    private String TAG = FcTopicMainActivity.class.getSimpleName();
    private AppBarLayout appBarLayout;
    /* access modifiers changed from: private */
    public AutoPlayTool autoPlayTool;
    private MryRoundButton btnFollow;
    private MryEmptyView emptyView;
    /* access modifiers changed from: private */
    public ImagePreSelectorActivity imageSelectorAlert;
    private ImageView ivActionBarBg;
    private ImageView ivBack;
    private ImageView ivCamera;
    private UserFcListAdapter mAdapter;
    /* access modifiers changed from: private */
    public FcPageMinePresenter mPresenter;
    /* access modifiers changed from: private */
    public int pageNo = 0;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.PhotoEntry> photoEntries = new ArrayList<>();
    /* access modifiers changed from: private */
    public SmartRefreshLayout refreshLayout;
    private int replyChildPosition = -1;
    private RespFcListBean replyItemModel;
    private int replyParentPosition = -1;
    private RecyclerListView rv;
    private FrameLayout rvContainer;
    RecyclerView.OnScrollListener rvScrollListener = new RecyclerView.OnScrollListener() {
        boolean isScroll = false;

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (FcTopicMainActivity.this.autoPlayTool != null && this.isScroll) {
                FcTopicMainActivity.this.autoPlayTool.onScrolledAndDeactivate();
            }
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            this.isScroll = newState != 0;
            if (newState != 0) {
                return;
            }
            if (FcTopicMainActivity.this.refreshLayout.getState() == RefreshState.None || FcTopicMainActivity.this.refreshLayout.getState() == RefreshState.RefreshFinish) {
                FcTopicMainActivity.this.isActivePlayer(recyclerView);
            }
        }
    };
    private MryTextView tvScanCount;
    private MryTextView tvTopicDes;
    private MryTextView tvTopicName;

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_fc_topic_main;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.actionBar.setAddToContainer(false);
        this.refreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.refreshLayout);
        this.appBarLayout = (AppBarLayout) this.fragmentView.findViewById(R.id.appBarLayout);
        this.ivBack = (ImageView) this.fragmentView.findViewById(R.id.ivBack);
        this.ivCamera = (ImageView) this.fragmentView.findViewById(R.id.ivCamera);
        FrameLayout actionBarContainer = (FrameLayout) this.fragmentView.findViewById(R.id.actionBarContainer);
        this.ivActionBarBg = (ImageView) this.fragmentView.findViewById(R.id.ivActionBarBg);
        this.tvTopicName = (MryTextView) this.fragmentView.findViewById(R.id.tvTopicName);
        this.tvScanCount = (MryTextView) this.fragmentView.findViewById(R.id.tvScanCount);
        this.tvTopicDes = (MryTextView) this.fragmentView.findViewById(R.id.tvTopicDes);
        this.btnFollow = (MryRoundButton) this.fragmentView.findViewById(R.id.btnFollow);
        this.rvContainer = (FrameLayout) this.fragmentView.findViewById(R.id.rvContainer);
        this.rv = (RecyclerListView) this.fragmentView.findViewById(R.id.rv);
        this.appBarLayout.addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) new AppBarLayout.OnOffsetChangedListener(actionBarContainer) {
            private final /* synthetic */ FrameLayout f$1;

            {
                this.f$1 = r2;
            }

            public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                FcTopicMainActivity.this.lambda$initView$0$FcTopicMainActivity(this.f$1, appBarLayout, i);
            }
        });
        ((CollapsingToolbarLayout) this.fragmentView.findViewById(R.id.collapsingToolbarLayout)).setMinimumHeight(AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight());
        actionBarContainer.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        this.ivBack.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), 0, Theme.getColor(Theme.key_actionBarDefaultSelector)));
        this.ivBack.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcTopicMainActivity.this.lambda$initView$1$FcTopicMainActivity(view);
            }
        });
        this.ivCamera.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), 0, Theme.getColor(Theme.key_actionBarDefaultSelector)));
        this.ivCamera.setOnClickListener($$Lambda$FcTopicMainActivity$P_QDjJWNlFUJAbkwTgClK8xhN8s.INSTANCE);
        this.btnFollow.setPrimaryRadiusAdjustBoundsFillStyle();
        this.btnFollow.setBackgroundColor(-13709571);
        this.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                FcTopicMainActivity.this.loadFcBaseInfo();
                int unused = FcTopicMainActivity.this.pageNo = 0;
                FcTopicMainActivity.this.getFcPageList();
            }

            public void onLoadMore(RefreshLayout refreshLayout) {
                FcTopicMainActivity.this.getFcPageList();
            }
        });
        this.rv.addItemDecoration(new SpacesItemDecoration(AndroidUtilities.dp(7.0f)));
        this.rv.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        UserFcListAdapter userFcListAdapter = new UserFcListAdapter(new ArrayList(), getParentActivity(), getClassGuid(), this);
        this.mAdapter = userFcListAdapter;
        userFcListAdapter.setFooterCount(1);
        this.rv.setAdapter(this.mAdapter);
        MryEmptyView mryEmptyView = new MryEmptyView(getParentActivity());
        this.emptyView = mryEmptyView;
        mryEmptyView.attach((ViewGroup) this.rvContainer);
        this.emptyView.showLoading();
    }

    public /* synthetic */ void lambda$initView$0$FcTopicMainActivity(FrameLayout actionBarContainer, AppBarLayout appBarLayout2, int i) {
        float offset = ((float) Math.abs(i)) / ((float) appBarLayout2.getTotalScrollRange());
        if (offset <= 0.0f) {
            actionBarContainer.setBackgroundColor(0);
            this.ivBack.clearColorFilter();
            this.ivCamera.clearColorFilter();
        } else if (offset >= 1.0f) {
            actionBarContainer.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
            this.ivBack.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
            this.ivCamera.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        } else {
            actionBarContainer.setBackgroundColor(AndroidUtilities.alphaColor(offset, Theme.getColor(Theme.key_actionBarDefault)));
            this.ivBack.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.alphaColor(offset, Theme.getColor(Theme.key_actionBarDefaultIcon)), PorterDuff.Mode.MULTIPLY));
            this.ivCamera.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.alphaColor(offset, Theme.getColor(Theme.key_actionBarDefaultIcon)), PorterDuff.Mode.MULTIPLY));
        }
    }

    public /* synthetic */ void lambda$initView$1$FcTopicMainActivity(View v) {
        finishFragment();
    }

    static /* synthetic */ void lambda$initView$2(View v) {
    }

    public void onResume() {
        super.onResume();
        getParentActivity().getWindow().setSoftInputMode(16);
        VideoPlayerManager.getInstance().resume();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                FcTopicMainActivity.this.lambda$onResume$3$FcTopicMainActivity();
            }
        }, 1000);
    }

    public /* synthetic */ void lambda$onResume$3$FcTopicMainActivity() {
        RecyclerView.OnScrollListener onScrollListener = this.rvScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(this.rv, 0);
        }
    }

    public void onPause() {
        if (ScreenViewState.isFullScreen(VideoPlayerManager.getInstance().getmScreenState())) {
            VideoPlayerManager.getInstance().onBackPressed();
        }
        setStopPlayState();
        super.onPause();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcFollowStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcPermissionStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcIgnoreOrDeleteItem);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcLikeStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcIgnoreUser);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcReplyItem);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcDeleteReplyItem);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcPublishSuccess);
        VideoPlayerManager.getInstance().release();
        FcPageMinePresenter fcPageMinePresenter = this.mPresenter;
        if (fcPageMinePresenter != null) {
            fcPageMinePresenter.unSubscribeTask();
        }
    }

    /* access modifiers changed from: protected */
    public void initData() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcFollowStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcPermissionStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcIgnoreOrDeleteItem);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcLikeStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcIgnoreUser);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcReplyItem);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcDeleteReplyItem);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcPublishSuccess);
        this.mPresenter = new FcPageMinePresenter(this);
        loadFcBackground((long) getUserConfig().getClientUserId());
        loadUserInfo();
        loadFcBaseInfo();
        getFcPageList();
    }

    private void loadUserInfo() {
        if (getUserConfig() != null) {
            getUserConfig().getClientUserId();
        }
    }

    /* access modifiers changed from: protected */
    public void setFcBackground(String path) {
        super.setFcBackground(path);
    }

    /* access modifiers changed from: private */
    public void loadFcBaseInfo() {
        this.mPresenter.getActionCount((long) getUserConfig().getClientUserId());
    }

    public void getActionCountSucc(RespFcUserStatisticsBean data) {
    }

    /* access modifiers changed from: private */
    public void getFcPageList() {
        this.mPresenter.getFCList(20, this.pageNo == 0 ? 0 : this.mAdapter.getEndListId());
    }

    public void getFCListSucc(ArrayList<RespFcListBean> data) {
        this.refreshLayout.finishRefresh();
        this.refreshLayout.finishLoadMore();
        setData(data);
    }

    public void getFCListFailed(String msg) {
        this.refreshLayout.finishRefresh();
        this.refreshLayout.finishLoadMore();
        FcToastUtils.show((CharSequence) LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail));
    }

    private void setData(ArrayList<RespFcListBean> mFclistBeanList) {
        if (this.pageNo == 0) {
            if (mFclistBeanList == null || mFclistBeanList.size() == 0) {
                this.refreshLayout.setEnableLoadMore(false);
                this.mAdapter.refresh(new ArrayList());
            } else {
                if (mFclistBeanList.size() < 20) {
                    this.refreshLayout.setEnableLoadMore(false);
                    mFclistBeanList.add(new RespFcListBean());
                } else {
                    this.refreshLayout.setEnableLoadMore(true);
                }
                this.mAdapter.refresh(mFclistBeanList);
                this.pageNo++;
            }
            AutoPlayTool autoPlayTool2 = this.autoPlayTool;
            if (autoPlayTool2 != null) {
                autoPlayTool2.onRefreshDeactivate();
            }
            refreshPageState();
            return;
        }
        if (mFclistBeanList == null || mFclistBeanList.size() < 20) {
            mFclistBeanList.add(new RespFcListBean());
            this.refreshLayout.setEnableLoadMore(false);
        }
        this.mAdapter.loadMore(mFclistBeanList);
        refreshPageState();
        if (mFclistBeanList.size() > 0) {
            this.pageNo++;
        }
    }

    private void refreshPageState() {
        UserFcListAdapter userFcListAdapter = this.mAdapter;
        if (userFcListAdapter == null || userFcListAdapter.getDataList().size() > this.mAdapter.getHeaderFooterCount()) {
            this.emptyView.showContent();
        } else {
            this.emptyView.showEmpty();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        RespFcListBean respFcListBean;
        int i1;
        int childPosition;
        int i = id;
        Object[] objArr = args;
        if (i == NotificationCenter.userFullInfoDidLoad) {
            if (objArr != null && objArr.length >= 2 && (objArr[1] instanceof TLRPC.UserFull) && ((Integer) objArr[0]).intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                boolean z = ((TLRPC.UserFull) objArr[1]) instanceof TLRPCContacts.CL_userFull_v1;
            }
        } else if (i == NotificationCenter.fcFollowStatusUpdate) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                long createBy = ((Long) objArr[1]).longValue();
                boolean isFollow = ((Boolean) objArr[2]).booleanValue();
                int position = -1;
                UserFcListAdapter userFcListAdapter = this.mAdapter;
                if (userFcListAdapter != null) {
                    List<RespFcListBean> dataList = userFcListAdapter.getDataList();
                    int i2 = 0;
                    while (true) {
                        if (i2 < dataList.size()) {
                            RespFcListBean respFcListBean2 = dataList.get(i2);
                            if (respFcListBean2 != null && respFcListBean2.getCreateBy() == createBy) {
                                position = i2;
                                break;
                            }
                            i2++;
                        } else {
                            break;
                        }
                    }
                }
                if (position != -1) {
                    doFollowAfterViewChange(position, isFollow);
                }
            }
        } else if (i == NotificationCenter.fcPermissionStatusUpdate) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                long forumId = ((Long) objArr[1]).longValue();
                int permission = ((Integer) objArr[2]).intValue();
                int position2 = -1;
                UserFcListAdapter userFcListAdapter2 = this.mAdapter;
                if (userFcListAdapter2 != null) {
                    List<RespFcListBean> dataList2 = userFcListAdapter2.getDataList();
                    int i3 = 0;
                    while (true) {
                        if (i3 < dataList2.size()) {
                            RespFcListBean respFcListBean3 = dataList2.get(i3);
                            if (respFcListBean3 != null && respFcListBean3.getForumID() == forumId) {
                                position2 = i3;
                                break;
                            }
                            i3++;
                        } else {
                            break;
                        }
                    }
                }
                if (position2 != -1) {
                    doSetItemPermissionAfterViewChange(forumId, permission, position2);
                }
            }
        } else if (i == NotificationCenter.fcIgnoreOrDeleteItem) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                long forumId2 = ((Long) objArr[1]).longValue();
                int position3 = -1;
                UserFcListAdapter userFcListAdapter3 = this.mAdapter;
                if (userFcListAdapter3 != null && forumId2 > 0) {
                    List<RespFcListBean> dataList3 = userFcListAdapter3.getDataList();
                    int i4 = 0;
                    while (true) {
                        if (i4 < dataList3.size()) {
                            RespFcListBean respFcListBean4 = dataList3.get(i4);
                            if (respFcListBean4 != null && respFcListBean4.getForumID() == forumId2) {
                                position3 = i4;
                                break;
                            }
                            i4++;
                        } else {
                            break;
                        }
                    }
                }
                if (position3 != -1) {
                    doDeleteItemAfterViewChange(forumId2, position3);
                }
            }
        } else if (i == NotificationCenter.fcLikeStatusUpdate) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                FcLikeBean fcLikeBean = (FcLikeBean) objArr[1];
                boolean isLike = ((Boolean) objArr[2]).booleanValue();
                int position4 = -1;
                if (this.mAdapter != null && fcLikeBean != null && fcLikeBean.getCommentID() == 0) {
                    List<RespFcListBean> dataList4 = this.mAdapter.getDataList();
                    int i5 = 0;
                    while (true) {
                        if (i5 < dataList4.size()) {
                            RespFcListBean respFcListBean5 = dataList4.get(i5);
                            if (respFcListBean5 != null && respFcListBean5.getForumID() == fcLikeBean.getForumID()) {
                                position4 = i5;
                                break;
                            }
                            i5++;
                        } else {
                            break;
                        }
                    }
                }
                if (position4 != -1) {
                    doLikeAfterViewChange(position4, isLike, fcLikeBean);
                }
            }
        } else if (i == NotificationCenter.fcIgnoreUser) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                ArrayList<FcIgnoreUserBean> ignores = (ArrayList) objArr[1];
                int position5 = -1;
                if (this.mAdapter != null && ignores != null && ignores.size() > 0) {
                    List<RespFcListBean> dataList5 = this.mAdapter.getDataList();
                    int i6 = 0;
                    while (true) {
                        if (i6 < dataList5.size()) {
                            RespFcListBean respFcListBean6 = dataList5.get(i6);
                            if (respFcListBean6 != null && respFcListBean6.getCreateBy() == ignores.get(0).getUserID()) {
                                position5 = i6;
                                break;
                            }
                            i6++;
                        } else {
                            break;
                        }
                    }
                }
                if (position5 != -1) {
                    doSetIgnoreUserAfterViewChange(true, ignores);
                }
            }
        } else if (i == NotificationCenter.fcReplyItem) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                FcReplyBean data = (FcReplyBean) objArr[1];
                int position6 = -1;
                UserFcListAdapter userFcListAdapter4 = this.mAdapter;
                if (userFcListAdapter4 != null && data != null) {
                    List<RespFcListBean> dataList6 = userFcListAdapter4.getDataList();
                    int i7 = 0;
                    while (true) {
                        if (i7 < dataList6.size()) {
                            RespFcListBean respFcListBean7 = dataList6.get(i7);
                            if (respFcListBean7 != null && respFcListBean7.getForumID() == data.getForumID()) {
                                position6 = i7;
                                break;
                            }
                            i7++;
                        } else {
                            break;
                        }
                    }
                }
                if (position6 != -1) {
                    doReplySuccAfterViewChange(data, position6);
                }
            }
        } else if (i == NotificationCenter.fcDeleteReplyItem) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                long forumId3 = ((Long) objArr[1]).longValue();
                long commentId = ((Long) objArr[2]).longValue();
                int childPosition2 = -1;
                UserFcListAdapter userFcListAdapter5 = this.mAdapter;
                if (userFcListAdapter5 != null && forumId3 > 0 && commentId > 0) {
                    List<RespFcListBean> dataList7 = userFcListAdapter5.getDataList();
                    int i8 = 0;
                    while (true) {
                        if (i8 >= dataList7.size()) {
                            break;
                        }
                        RespFcListBean respFcListBean8 = dataList7.get(i8);
                        if (respFcListBean8 == null || respFcListBean8.getForumID() != forumId3) {
                            i8++;
                        } else {
                            int parentPosition = i8;
                            ArrayList<FcReplyBean> comments = respFcListBean8.getComments();
                            int i12 = 0;
                            while (true) {
                                if (i12 < comments.size()) {
                                    FcReplyBean replyBean = comments.get(i12);
                                    if (replyBean != null && replyBean.getCommentID() == commentId) {
                                        childPosition2 = i12;
                                        break;
                                    }
                                    i12++;
                                } else {
                                    break;
                                }
                            }
                            i1 = parentPosition;
                            childPosition = childPosition2;
                        }
                    }
                    if (i1 != -1 || childPosition == -1) {
                    }
                    int i9 = childPosition;
                    doDeleteReplySuccAfterViewChange(forumId3, commentId, i1, childPosition);
                    return;
                }
                i1 = -1;
                childPosition = -1;
                if (i1 != -1) {
                }
            }
        } else if (i == NotificationCenter.fcPublishSuccess) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0]) && (respFcListBean = (RespFcListBean) objArr[1]) != null) {
                doAfterPublishSuccess(respFcListBean);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void didSelectOnePhoto(String photosPath) {
        super.didSelectOnePhoto(photosPath);
        if (!TextUtils.isEmpty(photosPath)) {
            uploadFile(photosPath, new DataListener<BResponse<FcMediaResponseBean>>() {
                public void onResponse(BResponse<FcMediaResponseBean> result) {
                    if (result != null && result.Data != null) {
                        String name = ((FcMediaResponseBean) result.Data).getName();
                        if (!TextUtils.isEmpty(name)) {
                            FcTopicMainActivity.this.mPresenter.setFcBackground(name);
                        }
                    }
                }

                public void onError(Throwable throwable) {
                    FcToastUtils.show((CharSequence) "设置失败");
                }
            });
        }
    }

    public void setFcBackgroundSucc(String servicePath, String msg) {
    }

    public void setFcBackgroundFailed(String msg) {
        FcToastUtils.show((CharSequence) msg);
    }

    public void onAction(View view, int index, int position, Object object) {
        int i = index;
        int i2 = position;
        Object obj = object;
        if (i == UserFcListAdapter.Index_click_avatar) {
            if (obj instanceof RespFcListBean) {
                RespFcListBean model = (RespFcListBean) obj;
                if (model.getCreateBy() == ((long) getUserConfig().getCurrentUser().id)) {
                    onPresentFragment(new FcPageMineActivity());
                } else {
                    onPresentFragment(new FcPageOthersActivity(model.getCreatorUser()));
                }
            }
        } else if (i == UserFcListAdapter.Index_click_follow) {
            if (obj instanceof RespFcListBean) {
                doFollow(i2, (RespFcListBean) obj);
            }
        } else if (i == UserFcListAdapter.Index_click_pop_cancel_follow) {
            if (obj instanceof RespFcListBean) {
                doCancelFollowed(i2, (RespFcListBean) obj);
            }
        } else if (i == UserFcListAdapter.Index_click_pop_public) {
            if (obj instanceof RespFcListBean) {
                setFcItemPermission((RespFcListBean) obj, 1, i2);
            }
        } else if (i == UserFcListAdapter.Index_click_pop_private) {
            if (obj instanceof RespFcListBean) {
                setFcItemPermission((RespFcListBean) obj, 2, i2);
            }
        } else if (i == UserFcListAdapter.Index_click_pop_delete) {
            if (obj instanceof RespFcListBean) {
                FcDialogUtil.chooseIsDeleteMineItemDialog(this, new FcDialog.OnConfirmClickListener(i2, (RespFcListBean) obj) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ RespFcListBean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        FcTopicMainActivity.this.lambda$onAction$4$FcTopicMainActivity(this.f$1, this.f$2, view);
                    }
                }, (DialogInterface.OnDismissListener) null);
            }
        } else if (i == UserFcListAdapter.Index_click_pop_shield_item) {
            if (obj instanceof RespFcListBean) {
                FcDialogUtil.chooseIsSetOtherFcItemPrivacyDialog(this, new FcDialog.OnConfirmClickListener(i2, (RespFcListBean) obj) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ RespFcListBean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        FcTopicMainActivity.this.lambda$onAction$5$FcTopicMainActivity(this.f$1, this.f$2, view);
                    }
                }, (DialogInterface.OnDismissListener) null);
            }
        } else if (i == UserFcListAdapter.Index_click_pop_shield_user) {
            if (obj instanceof RespFcListBean) {
                FcDialogUtil.choosePrivacyAllFcDialog(this, new FcDialog.OnConfirmClickListener((RespFcListBean) obj) {
                    private final /* synthetic */ RespFcListBean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        FcTopicMainActivity.this.lambda$onAction$6$FcTopicMainActivity(this.f$1, view);
                    }
                }, (DialogInterface.OnDismissListener) null);
            }
        } else if (i == UserFcListAdapter.Index_click_pop_report) {
            if (obj instanceof RespFcListBean) {
                presentFragment(new FcReportActivity(((RespFcListBean) obj).getForumID()));
            }
        } else if (i == UserFcListAdapter.Index_download_photo || i == UserFcListAdapter.Index_download_video) {
            if (obj instanceof String) {
                downloadFileToLocal((String) obj);
            }
        } else if (i == UserFcListAdapter.Index_click_like) {
            if (obj instanceof RespFcListBean) {
                RespFcListBean model2 = (RespFcListBean) obj;
                if (model2.isHasThumb()) {
                    doCancelLikeFc(model2.getForumID(), model2.getCreateBy(), -1, -1, position);
                    return;
                }
                doLike(model2.getForumID(), model2.getCreateBy(), -1, -1, position);
            }
        } else if (i == UserFcListAdapter.Index_click_reply) {
            if (obj instanceof RespFcListBean) {
                RespFcListBean respFcListBean = (RespFcListBean) obj;
            }
        } else if (i == UserFcListAdapter.Index_click_location && (obj instanceof RespFcListBean)) {
            RespFcListBean model3 = (RespFcListBean) obj;
            if (!TextUtils.isEmpty(model3.getLocationName()) && model3.getLongitude() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && model3.getLatitude() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                presentFragment(new FcLocationInfoActivity(new FcLocationInfoBean(model3.getLongitude(), model3.getLatitude(), model3.getLocationName(), model3.getLocationAddress(), model3.getLocationCity())));
            }
        }
    }

    public /* synthetic */ void lambda$onAction$4$FcTopicMainActivity(int position, RespFcListBean model, View dialog) {
        doDeleteItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$5$FcTopicMainActivity(int position, RespFcListBean model, View dialog) {
        doIgnoreItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$6$FcTopicMainActivity(RespFcListBean model, View dialog) {
        ArrayList<FcIgnoreUserBean> list = new ArrayList<>();
        list.add(new FcIgnoreUserBean(model.getCreateBy(), 2));
        doAddIgnoreUser(list);
    }

    public void onReplyClick(View v, String receiver, RespFcListBean model, int itemPosition, int replyPosition, boolean isLongClick) {
        int i = itemPosition;
        int i2 = replyPosition;
        if (isLongClick) {
            showDeleteBottomSheet(model, i, i2);
            return;
        }
        RespFcListBean respFcListBean = model;
        if (i < this.mAdapter.getItemCount()) {
            this.replyItemModel = (RespFcListBean) this.mAdapter.get(i);
        }
        this.replyParentPosition = i;
        this.replyChildPosition = i2;
        showReplyFcDialog(receiver, this.replyItemModel.getForumID(), model.getCreateBy(), false, this.replyChildPosition == -1, model.isRecommend(), model.getRequiredVipLevel());
    }

    public void onPresentFragment(BaseFragment baseFragment) {
        setStopPlayState();
        if (baseFragment != null) {
            presentFragment(baseFragment);
        }
    }

    public void onInputReplyContent(String content, ArrayList<FCEntitysRequest> arrayList) {
        long replayUID;
        long replayID;
        long supUser;
        long supID;
        if (TextUtils.isEmpty(content)) {
            FcToastUtils.show((CharSequence) LocaleController.getString("fc_tips_input_empty_comment", R.string.fc_tips_input_empty_comment));
            return;
        }
        RespFcListBean respFcListBean = this.replyItemModel;
        if (respFcListBean != null) {
            long forumID = respFcListBean.getForumID();
            long forumUser = this.replyItemModel.getCreateBy();
            if (this.replyChildPosition == -1) {
                supID = 0;
                supUser = 0;
                replayID = forumID;
                replayUID = forumUser;
            } else {
                ArrayList<FcReplyBean> comments = this.replyItemModel.getComments();
                if (comments == null || this.replyChildPosition >= comments.size()) {
                    supID = 0;
                    supUser = 0;
                    replayID = 0;
                    replayUID = 0;
                } else {
                    FcReplyBean fcReplyBean = comments.get(this.replyChildPosition);
                    if (fcReplyBean.getReplayID() == forumID) {
                        long replayID2 = fcReplyBean.getCommentID();
                        long replayUID2 = fcReplyBean.getCreateBy();
                        supID = fcReplyBean.getCommentID();
                        supUser = fcReplyBean.getCreateBy();
                        replayID = replayID2;
                        replayUID = replayUID2;
                    } else {
                        long replayID3 = fcReplyBean.getCommentID();
                        long replayUID3 = fcReplyBean.getCreateBy();
                        supID = fcReplyBean.getSupID();
                        supUser = fcReplyBean.getSupUser();
                        replayID = replayID3;
                        replayUID = replayUID3;
                    }
                }
            }
            doReplyFc(new RequestReplyFcBean(forumID, forumUser, replayID, replayUID, supID, supUser, content, this.replyItemModel.getRequiredVipLevel()), this.replyParentPosition);
        }
    }

    public void doAfterPublishSuccess(RespFcListBean mResponseFcPublishBackBean) {
        KLog.d("----------mResponseFcPublishBackBean" + mResponseFcPublishBackBean);
        UserFcListAdapter userFcListAdapter = this.mAdapter;
        if (userFcListAdapter == null || userFcListAdapter.getDataList().size() <= 0) {
            ArrayList<RespFcListBean> dataList = new ArrayList<>();
            dataList.add(mResponseFcPublishBackBean);
            dataList.add(new RespFcListBean());
            this.mAdapter.refresh(dataList);
        } else {
            this.mAdapter.getDataList().add(0, mResponseFcPublishBackBean);
            this.mAdapter.notifyItemInserted(0);
            UserFcListAdapter userFcListAdapter2 = this.mAdapter;
            userFcListAdapter2.notifyItemRangeChanged(0, userFcListAdapter2.getCount());
        }
        refreshPageState();
        this.rv.scrollToPosition(0);
    }

    /* access modifiers changed from: protected */
    public void doFollowAfterViewChange(int position, boolean isFollow) {
        MryTextView btnFollow2;
        long createBy = ((RespFcListBean) this.mAdapter.get(position)).getCreateBy();
        if (this.mAdapter.getItemCount() > 0) {
            for (int j = this.mAdapter.getHeaderCount(); j < this.mAdapter.getCount(); j++) {
                if (((RespFcListBean) this.mAdapter.get(j)).getCreateBy() == createBy) {
                    ((RespFcListBean) this.mAdapter.getDataList().get(j)).setHasFollow(isFollow);
                    View viewByPosition = this.layoutManager.findViewByPosition(j);
                    if (!(viewByPosition == null || (btnFollow2 = (MryTextView) viewByPosition.findViewById(R.id.btn_follow)) == null || btnFollow2.getVisibility() != 0)) {
                        btnFollow2.setText(isFollow ? "已关注" : "关注");
                        btnFollow2.setSelected(isFollow);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doSetItemPermissionAfterViewChange(long forumId, int permission, int position) {
        ((RespFcListBean) this.mAdapter.get(position)).setPermission(permission);
    }

    /* JADX WARNING: type inference failed for: r3v6, types: [android.view.View] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doLikeAfterViewChange(int r7, boolean r8, com.bjz.comm.net.bean.FcLikeBean r9) {
        /*
            r6 = this;
            androidx.recyclerview.widget.RecyclerView$LayoutManager r0 = r6.layoutManager
            android.view.View r0 = r0.findViewByPosition(r7)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0019
            r3 = 2131296429(0x7f0900ad, float:1.8210774E38)
            android.view.View r3 = r0.findViewById(r3)
            r1 = r3
            im.bclpbkiauv.ui.hviews.MryTextView r1 = (im.bclpbkiauv.ui.hviews.MryTextView) r1
            if (r1 == 0) goto L_0x0019
            r1.setClickable(r2)
        L_0x0019:
            if (r9 == 0) goto L_0x008e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "------position"
            r3.append(r4)
            r3.append(r7)
            java.lang.String r4 = "  "
            r3.append(r4)
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            com.socks.library.KLog.d(r3)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter r3 = r6.mAdapter
            java.lang.Object r3 = r3.get(r7)
            com.bjz.comm.net.bean.RespFcListBean r3 = (com.bjz.comm.net.bean.RespFcListBean) r3
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter r4 = r6.mAdapter
            java.lang.Object r4 = r4.get(r7)
            com.bjz.comm.net.bean.RespFcListBean r4 = (com.bjz.comm.net.bean.RespFcListBean) r4
            r4.setHasThumb(r8)
            if (r8 == 0) goto L_0x005d
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter r4 = r6.mAdapter
            java.lang.Object r4 = r4.get(r7)
            com.bjz.comm.net.bean.RespFcListBean r4 = (com.bjz.comm.net.bean.RespFcListBean) r4
            int r5 = r3.getThumbUp()
            int r5 = r5 + r2
            r4.setThumbUp(r5)
            goto L_0x0075
        L_0x005d:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter r4 = r6.mAdapter
            java.lang.Object r4 = r4.get(r7)
            com.bjz.comm.net.bean.RespFcListBean r4 = (com.bjz.comm.net.bean.RespFcListBean) r4
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter r5 = r6.mAdapter
            java.lang.Object r5 = r5.get(r7)
            com.bjz.comm.net.bean.RespFcListBean r5 = (com.bjz.comm.net.bean.RespFcListBean) r5
            int r5 = r5.getThumbUp()
            int r5 = r5 - r2
            r4.setThumbUp(r5)
        L_0x0075:
            if (r1 == 0) goto L_0x008e
            int r2 = r3.getThumbUp()
            if (r2 <= 0) goto L_0x0086
            int r2 = r3.getThumbUp()
            java.lang.String r2 = java.lang.String.valueOf(r2)
            goto L_0x0088
        L_0x0086:
            java.lang.String r2 = "0"
        L_0x0088:
            r1.setText(r2)
            r1.setSelected(r8)
        L_0x008e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcTopicMainActivity.doLikeAfterViewChange(int, boolean, com.bjz.comm.net.bean.FcLikeBean):void");
    }

    /* access modifiers changed from: protected */
    public void doDeleteItemAfterViewChange(long forumId, int position) {
        this.mAdapter.getDataList().remove(position);
        this.mAdapter.notifyItemRemoved(position);
        UserFcListAdapter userFcListAdapter = this.mAdapter;
        userFcListAdapter.notifyItemRangeChanged(position, userFcListAdapter.getItemCount());
        refreshPageState();
    }

    /* access modifiers changed from: protected */
    public void doIgnoreItemAfterViewChange(long forumId, int position) {
        this.mAdapter.getDataList().remove(position);
        this.mAdapter.notifyItemRemoved(position);
        UserFcListAdapter userFcListAdapter = this.mAdapter;
        userFcListAdapter.notifyItemRangeChanged(position, userFcListAdapter.getItemCount());
        refreshPageState();
    }

    /* access modifiers changed from: protected */
    public void doSetIgnoreUserAfterViewChange(boolean isIgnore, ArrayList<FcIgnoreUserBean> ignores) {
        FcIgnoreUserBean ignoreUserBean;
        if (isIgnore && ignores != null && ignores.size() > 0 && (ignoreUserBean = ignores.get(0)) != null) {
            this.mAdapter.removeItemByUserID(ignoreUserBean.getUserID());
        }
        refreshPageState();
    }

    /* access modifiers changed from: protected */
    public void doReplySuccAfterViewChange(FcReplyBean data, int replyParentPosition2) {
        RespFcListBean respFcListBean = (RespFcListBean) this.mAdapter.get(replyParentPosition2);
        if (respFcListBean != null && data != null) {
            ArrayList<FcReplyBean> comments = respFcListBean.getComments();
            ArrayList<FcReplyBean> morelist = new ArrayList<>();
            morelist.add(data);
            if (comments == null || comments.size() == 0) {
                respFcListBean.setComments(morelist);
            } else {
                comments.addAll(morelist);
            }
            respFcListBean.setCommentCount(respFcListBean.getCommentCount() + 1);
            View viewByPosition = this.layoutManager.findViewByPosition(replyParentPosition2);
            if (viewByPosition != null) {
                MryTextView btnReply = (MryTextView) viewByPosition.findViewById(R.id.btn_reply);
                if (btnReply != null) {
                    btnReply.setText(respFcListBean.getCommentCount() > 0 ? String.valueOf(respFcListBean.getCommentCount()) : "0");
                }
                RecyclerView rvReply = (RecyclerView) viewByPosition.findViewById(R.id.rv_fc_comm_reply);
                if (rvReply != null) {
                    FcHomeItemReplyAdapter adapter = (FcHomeItemReplyAdapter) rvReply.getAdapter();
                    if (adapter == null || adapter.getItemCount() <= 0) {
                        this.mAdapter.notifyItemChanged(replyParentPosition2);
                    } else {
                        adapter.loadMore(morelist);
                    }
                }
            }
        }
    }

    public void doDeleteReplySuccAfterViewChange(long forumId, long commentId, int parentPosition, int childPosition) {
        View viewByPosition;
        FcHomeItemReplyAdapter adapter;
        int i = parentPosition;
        int i2 = childPosition;
        RespFcListBean respFcListBean = (RespFcListBean) this.mAdapter.get(i);
        if (respFcListBean != null && (viewByPosition = this.layoutManager.findViewByPosition(i)) != null) {
            RecyclerView rvReply = (RecyclerView) viewByPosition.findViewById(R.id.rv_fc_comm_reply);
            if (!(rvReply == null || i2 == -1 || (adapter = (FcHomeItemReplyAdapter) rvReply.getAdapter()) == null || adapter.getDataList() == null || i2 >= adapter.getDataList().size())) {
                FcReplyBean fcReplyBean = (FcReplyBean) adapter.getDataList().get(i2);
                ArrayList<FcReplyBean> temp = new ArrayList<>(adapter.getDataList());
                Iterator<FcReplyBean> iterator = temp.iterator();
                while (iterator.hasNext()) {
                    FcReplyBean next = iterator.next();
                    if (next != null && (next.getCommentID() == fcReplyBean.getCommentID() || next.getSupID() == fcReplyBean.getCommentID())) {
                        iterator.remove();
                    }
                }
                adapter.refresh(temp);
                respFcListBean.setCommentCount(temp.size());
                respFcListBean.getComments().clear();
                respFcListBean.getComments().addAll(temp);
            }
            MryTextView btnReply = (MryTextView) viewByPosition.findViewById(R.id.btn_reply);
            if (btnReply != null) {
                btnReply.setText(respFcListBean.getCommentCount() > 0 ? String.valueOf(respFcListBean.getCommentCount()) : "0");
            }
        }
    }

    public void onError(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail) : msg);
    }

    /* access modifiers changed from: private */
    public void isActivePlayer(RecyclerView recyclerView) {
        if (this.autoPlayTool == null) {
            this.autoPlayTool = new AutoPlayTool(80, 1);
        }
        if (recyclerView != null) {
            this.autoPlayTool.onActiveWhenNoScrolling(recyclerView);
        }
    }

    private void setStopPlayState() {
        AutoPlayTool autoPlayTool2 = this.autoPlayTool;
        if (autoPlayTool2 != null) {
            autoPlayTool2.onDeactivate();
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
                    if (FcTopicMainActivity.this.imageSelectorAlert.isShowing()) {
                        AndroidUtilities.requestAdjustResize(FcTopicMainActivity.this.getParentActivity(), FcTopicMainActivity.this.classGuid);
                        for (int i = 0; i < FcTopicMainActivity.this.photoEntries.size(); i++) {
                            if (((MediaController.PhotoEntry) FcTopicMainActivity.this.photoEntries.get(i)).isVideo) {
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
                    if (button == 8 || button == 7 || (button == 4 && !FcTopicMainActivity.this.imageSelectorAlert.getSelectedPhotos().isEmpty())) {
                        if (button != 8) {
                            FcTopicMainActivity.this.imageSelectorAlert.dismiss();
                        }
                        HashMap<Object, Object> selectedPhotos = FcTopicMainActivity.this.imageSelectorAlert.getSelectedPhotos();
                        ArrayList<Object> selectedPhotosOrder = FcTopicMainActivity.this.imageSelectorAlert.getSelectedPhotosOrder();
                        int currentSelectMediaType = FcTopicMainActivity.this.imageSelectorAlert.getCurrentSelectMediaType();
                        if (selectedPhotos.isEmpty() || selectedPhotosOrder.isEmpty()) {
                            FcTopicMainActivity.this.presentFragment(new FcPublishActivity());
                        } else {
                            FcTopicMainActivity.this.presentFragment(new FcPublishActivity(FcTopicMainActivity.this.imageSelectorAlert, selectedPhotos, selectedPhotosOrder, currentSelectMediaType));
                        }
                    } else if (FcTopicMainActivity.this.imageSelectorAlert != null) {
                        FcTopicMainActivity.this.imageSelectorAlert.dismissWithButtonClick(button);
                        FcTopicMainActivity.this.presentFragment(new FcPublishActivity());
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
                    AndroidUtilities.setAdjustResizeToNothing(FcTopicMainActivity.this.getParentActivity(), FcTopicMainActivity.this.classGuid);
                }
            });
        }
    }
}
