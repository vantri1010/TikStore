package im.bclpbkiauv.ui.hui.friendscircle_v1.fragments;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcPageHomePresenter;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.socks.library.KLog;
import im.bclpbkiauv.javaBean.fc.FcLocationInfoBean;
import im.bclpbkiauv.javaBean.fc.HomeFcListBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.dialogs.FcDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeItemReplyAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcListFragment;
import im.bclpbkiauv.ui.hui.friendscircle_v1.decoration.SpacesItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.FcDBHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.state.ScreenViewState;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcLocationInfoActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageMineActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageOthersActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcReportActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.AutoPlayTool;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.FcDialogUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FcHomeFragment extends CommFcListFragment implements NotificationCenter.NotificationCenterDelegate, FcItemActionClickListener, BaseFcContract.IFcPageHomeView, FcDoReplyDialog.OnFcDoReplyListener {
    private String TAG = FcHomeFragment.class.getSimpleName();
    /* access modifiers changed from: private */
    public AutoPlayTool autoPlayTool;
    private int[] coord = new int[2];
    private int[] coordedt = new int[2];
    private MryEmptyView emptyView;
    private FcHomeAdapter mAdapter;
    private BaseFcContract.IFcPageHomePresenter mPresenter;
    /* access modifiers changed from: private */
    public SmartRefreshLayout mSmartRefreshLayout;
    /* access modifiers changed from: private */
    public int pageNo = 0;
    private int replyChildPosition = -1;
    private RespFcListBean replyItemModel;
    private int replyParentPosition = -1;
    /* access modifiers changed from: private */
    public RecyclerView rvFcList;
    RecyclerView.OnScrollListener rvScrollListener = new RecyclerView.OnScrollListener() {
        boolean isScroll = false;

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (FcHomeFragment.this.autoPlayTool != null && this.isScroll) {
                FcHomeFragment.this.autoPlayTool.onScrolledAndDeactivate();
            }
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            this.isScroll = newState != 0;
            if (newState != 0) {
                return;
            }
            if (FcHomeFragment.this.mSmartRefreshLayout.getState() == RefreshState.None || FcHomeFragment.this.mSmartRefreshLayout.getState() == RefreshState.RefreshFinish) {
                FcHomeFragment.this.isActivePlayer(recyclerView);
            }
        }
    };

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.fragment_fc_page_home;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mSmartRefreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.smartRefreshLayout);
        this.rvFcList = (RecyclerView) this.fragmentView.findViewById(R.id.rv_fc_list);
        this.mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                int unused = FcHomeFragment.this.pageNo = 0;
                FcHomeFragment.this.getFcPageList();
            }

            public void onLoadMore(RefreshLayout refreshLayout) {
                FcHomeFragment.this.getFcPageList();
            }
        });
        this.layoutManager = new LinearLayoutManager(this.mContext, 1, false);
        this.rvFcList.setLayoutManager(this.layoutManager);
        this.rvFcList.addItemDecoration(new SpacesItemDecoration(AndroidUtilities.dp(7.0f)));
        FcHomeAdapter fcHomeAdapter = new FcHomeAdapter(new ArrayList(), getParentActivity(), getClassGuid(), 2, this);
        this.mAdapter = fcHomeAdapter;
        fcHomeAdapter.setFooterCount(1);
        this.rvFcList.setAdapter(this.mAdapter);
        this.rvFcList.addOnScrollListener(this.rvScrollListener);
        initEmptyView();
    }

    private void initEmptyView() {
        FrameLayout flContainer = (FrameLayout) this.fragmentView.findViewById(R.id.fl_container);
        this.emptyView = new MryEmptyView(this.mContext);
        if (Theme.getCurrentTheme().isLight()) {
            this.emptyView.setBackgroundColor(this.mContext.getResources().getColor(R.color.color_FFF6F7F9));
        } else {
            this.emptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        }
        this.emptyView.attach((ViewGroup) flContainer);
        this.emptyView.setEmptyText(LocaleController.getString(R.string.NoHubMessages));
        this.emptyView.setEmptyResId(R.mipmap.img_empty_default);
        this.emptyView.setErrorResId(R.mipmap.img_empty_default);
        this.emptyView.getTextView().setTextColor(this.mContext.getResources().getColor(R.color.color_FFDBC9B8));
        this.emptyView.getBtn().setPrimaryRadiusAdjustBoundsFillStyle();
        this.emptyView.getBtn().setRoundBgGradientColors(new int[]{-4789508, -13187843});
        this.emptyView.getBtn().setStrokeWidth(0);
        this.emptyView.getBtn().setPadding(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(5.0f));
        this.emptyView.setEmptyBtnText(LocaleController.getString("PublishNow", R.string.PublishNow));
        this.emptyView.setOnEmptyClickListener(new MryEmptyView.OnEmptyOrErrorClickListener() {
            public final boolean onEmptyViewButtonClick(boolean z) {
                return FcHomeFragment.this.lambda$initEmptyView$0$FcHomeFragment(z);
            }
        });
        flContainer.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
    }

    public /* synthetic */ boolean lambda$initEmptyView$0$FcHomeFragment(boolean isEmptyButton) {
        if (isEmptyButton) {
            startFcPublishActivity();
        } else {
            this.pageNo = 0;
            refreshPageState(true, (String) null);
            getFcPageList();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void initData() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcFollowStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcPermissionStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcIgnoreOrDeleteItem);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcLikeStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcIgnoreUser);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcReplyItem);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcDeleteReplyItem);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcPublishSuccess);
        this.mPresenter = new FcPageHomePresenter(this);
        getDBCache();
        if ((this.fcVersionBean != null && this.fcVersionBean.isFriendState()) || this.pageNo == 0) {
            this.pageNo = 0;
            refreshPageState(true, (String) null);
            getFcPageList();
        }
    }

    public void onVisible() {
        super.onVisible();
        getParentActivity().getWindow().setSoftInputMode(16);
        VideoPlayerManager.getInstance().resume();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (FcHomeFragment.this.rvScrollListener != null) {
                    FcHomeFragment.this.rvScrollListener.onScrollStateChanged(FcHomeFragment.this.rvFcList, 0);
                }
            }
        }, 1000);
    }

    public void onInvisible() {
        super.onInvisible();
        if (ScreenViewState.isFullScreen(VideoPlayerManager.getInstance().getmScreenState())) {
            VideoPlayerManager.getInstance().onBackPressed();
        }
        setStopPlayState();
    }

    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcFollowStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcPermissionStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcIgnoreOrDeleteItem);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcLikeStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcIgnoreUser);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcReplyItem);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcDeleteReplyItem);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcPublishSuccess);
        VideoPlayerManager.getInstance().release();
        BaseFcContract.IFcPageHomePresenter iFcPageHomePresenter = this.mPresenter;
        if (iFcPageHomePresenter != null) {
            iFcPageHomePresenter.unSubscribeTask();
        }
    }

    private void refreshPageState(boolean isRefreshing, String errorMsg) {
        if (isRefreshing) {
            this.emptyView.showLoading();
        } else if (this.pageNo != 0 || TextUtils.isEmpty(errorMsg) || this.mAdapter.getDataList().size() > this.mAdapter.getHeaderFooterCount()) {
            FcHomeAdapter fcHomeAdapter = this.mAdapter;
            if (fcHomeAdapter != null && fcHomeAdapter.getDataList().size() <= this.mAdapter.getHeaderFooterCount()) {
                this.emptyView.showEmpty();
            } else if (this.emptyView.getCurrentStatus() != 2) {
                this.emptyView.showContent();
            }
        } else {
            this.emptyView.showError(errorMsg);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        RespFcListBean respFcListBean;
        int childPosition;
        int i1;
        int i = id;
        KLog.d("---------通知" + i);
        if (i == NotificationCenter.fcFollowStatusUpdate) {
            if (!TextUtils.equals(this.TAG, args[0])) {
                long createBy = args[1].longValue();
                boolean isFollow = args[2].booleanValue();
                int position = -1;
                FcHomeAdapter fcHomeAdapter = this.mAdapter;
                if (fcHomeAdapter != null) {
                    List<RespFcListBean> dataList = fcHomeAdapter.getDataList();
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
            if (!TextUtils.equals(this.TAG, args[0])) {
                long forumId = args[1].longValue();
                int permission = args[2].intValue();
                int position2 = -1;
                FcHomeAdapter fcHomeAdapter2 = this.mAdapter;
                if (fcHomeAdapter2 != null) {
                    List<RespFcListBean> dataList2 = fcHomeAdapter2.getDataList();
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
            if (!TextUtils.equals(this.TAG, args[0])) {
                long forumId2 = args[1].longValue();
                int position3 = -1;
                FcHomeAdapter fcHomeAdapter3 = this.mAdapter;
                if (fcHomeAdapter3 != null && forumId2 > 0) {
                    List<RespFcListBean> dataList3 = fcHomeAdapter3.getDataList();
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
            if (!TextUtils.equals(this.TAG, args[0])) {
                FcLikeBean fcLikeBean = args[1];
                boolean isLike = args[2].booleanValue();
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
            if (!TextUtils.equals(this.TAG, args[0])) {
                ArrayList<FcIgnoreUserBean> ignores = args[1];
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
            if (!TextUtils.equals(this.TAG, args[0])) {
                FcReplyBean data = args[1];
                int position6 = -1;
                FcHomeAdapter fcHomeAdapter4 = this.mAdapter;
                if (fcHomeAdapter4 != null && data != null) {
                    List<RespFcListBean> dataList6 = fcHomeAdapter4.getDataList();
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
            if (!TextUtils.equals(this.TAG, args[0])) {
                long forumId3 = args[1].longValue();
                long commentId = args[2].longValue();
                int childPosition2 = -1;
                FcHomeAdapter fcHomeAdapter5 = this.mAdapter;
                if (fcHomeAdapter5 != null && forumId3 > 0 && commentId > 0) {
                    List<RespFcListBean> dataList7 = fcHomeAdapter5.getDataList();
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
                    if (i1 != -1 && childPosition != -1) {
                        doDeleteReplySuccAfterViewChange(forumId3, commentId, i1, childPosition);
                        return;
                    }
                }
                i1 = -1;
                childPosition = -1;
                if (i1 != -1) {
                }
            }
        } else if (i == NotificationCenter.fcPublishSuccess) {
            if (!TextUtils.equals(this.TAG, args[0]) && (respFcListBean = args[1]) != null) {
                doAfterPublishSuccess(respFcListBean);
            }
        }
    }

    public void setPageNo(int pageNo2) {
        this.pageNo = pageNo2;
    }

    public void getFcPageList() {
        this.mPresenter.getFcList(20, this.pageNo == 0 ? 0 : this.mAdapter.getEndListId());
    }

    private void getDBCache() {
        ArrayList<HomeFcListBean> queryAll = FcDBHelper.getInstance().getQueryByOrder(HomeFcListBean.class);
        ArrayList<RespFcListBean> tempList = new ArrayList<>();
        Iterator<HomeFcListBean> it = queryAll.iterator();
        while (it.hasNext()) {
            HomeFcListBean homeFclistBean = it.next();
            if (homeFclistBean != null) {
                tempList.add(homeFclistBean);
            }
        }
        setData(tempList);
    }

    public void getFcListSucc(ArrayList<RespFcListBean> data) {
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        if (this.pageNo == 0) {
            refreshDotStatus(1);
            FcDBHelper.getInstance().deleteAll(HomeFcListBean.class);
        }
        if (data != null) {
            ArrayList<HomeFcListBean> tempInsertList = new ArrayList<>();
            Iterator<RespFcListBean> it = data.iterator();
            while (it.hasNext()) {
                tempInsertList.add(new HomeFcListBean(it.next()));
            }
            FcDBHelper.getInstance().insertAll(tempInsertList);
        }
        setData(data);
    }

    public void getFcListFailed(String msg) {
        if (this.pageNo == 0) {
            getDBCache();
        }
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        FcToastUtils.show((CharSequence) LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail));
        refreshPageState(false, msg);
    }

    private void setData(ArrayList<RespFcListBean> mFclistBeanList) {
        if (this.pageNo == 0) {
            if (mFclistBeanList == null || mFclistBeanList.size() == 0) {
                this.mSmartRefreshLayout.setEnableLoadMore(false);
                this.mAdapter.refresh(new ArrayList());
            } else {
                if (mFclistBeanList.size() < 20) {
                    this.mSmartRefreshLayout.setEnableLoadMore(false);
                    mFclistBeanList.add(new RespFcListBean());
                } else {
                    this.mSmartRefreshLayout.setEnableLoadMore(true);
                }
                this.mAdapter.refresh(mFclistBeanList);
                this.pageNo++;
            }
            AutoPlayTool autoPlayTool2 = this.autoPlayTool;
            if (autoPlayTool2 != null) {
                autoPlayTool2.onRefreshDeactivate();
            }
            refreshPageState(false, (String) null);
            return;
        }
        if (mFclistBeanList == null || mFclistBeanList.size() < 20) {
            mFclistBeanList.add(new RespFcListBean());
            this.mSmartRefreshLayout.setEnableLoadMore(false);
        }
        this.mAdapter.loadMore(mFclistBeanList);
        refreshPageState(false, (String) null);
        if (mFclistBeanList.size() > 0) {
            this.pageNo++;
        }
    }

    public void onAction(View view, int index, int position, Object object) {
        int i = index;
        int i2 = position;
        Object obj = object;
        if (i == FcHomeAdapter.Index_click_avatar) {
            if (obj instanceof RespFcListBean) {
                RespFcListBean model = (RespFcListBean) obj;
                if (model.getCreateBy() == ((long) getUserConfig().getCurrentUser().id)) {
                    onPresentFragment(new FcPageMineActivity());
                } else {
                    onPresentFragment(new FcPageOthersActivity(model.getCreatorUser()));
                }
            }
        } else if (i == FcHomeAdapter.Index_click_follow) {
            if (obj instanceof RespFcListBean) {
                doFollow(i2, (RespFcListBean) obj);
            }
        } else if (i == FcHomeAdapter.Index_click_pop_cancel_follow) {
            if (obj instanceof RespFcListBean) {
                doCancelFollowed(i2, (RespFcListBean) obj);
            }
        } else if (i == FcHomeAdapter.Index_click_pop_public) {
            if (obj instanceof RespFcListBean) {
                setFcItemPermission((RespFcListBean) obj, 1, i2);
            }
        } else if (i == FcHomeAdapter.Index_click_pop_private) {
            if (obj instanceof RespFcListBean) {
                setFcItemPermission((RespFcListBean) obj, 2, i2);
            }
        } else if (i == FcHomeAdapter.Index_click_pop_delete) {
            if (obj instanceof RespFcListBean) {
                FcDialogUtil.chooseIsDeleteMineItemDialog(this, new FcDialog.OnConfirmClickListener(i2, (RespFcListBean) obj) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ RespFcListBean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        FcHomeFragment.this.lambda$onAction$1$FcHomeFragment(this.f$1, this.f$2, view);
                    }
                }, (DialogInterface.OnDismissListener) null);
            }
        } else if (i == FcHomeAdapter.Index_click_pop_shield_item) {
            if (obj instanceof RespFcListBean) {
                FcDialogUtil.chooseIsSetOtherFcItemPrivacyDialog(this, new FcDialog.OnConfirmClickListener(i2, (RespFcListBean) obj) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ RespFcListBean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        FcHomeFragment.this.lambda$onAction$2$FcHomeFragment(this.f$1, this.f$2, view);
                    }
                }, (DialogInterface.OnDismissListener) null);
            }
        } else if (i == FcHomeAdapter.Index_click_pop_shield_user) {
            if (obj instanceof RespFcListBean) {
                FcDialogUtil.choosePrivacyAllFcDialog(this, new FcDialog.OnConfirmClickListener((RespFcListBean) obj) {
                    private final /* synthetic */ RespFcListBean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        FcHomeFragment.this.lambda$onAction$3$FcHomeFragment(this.f$1, view);
                    }
                }, (DialogInterface.OnDismissListener) null);
            }
        } else if (i == FcHomeAdapter.Index_click_pop_report) {
            if (obj instanceof RespFcListBean) {
                presentFragment(new FcReportActivity(((RespFcListBean) obj).getForumID()));
            }
        } else if (i == FcHomeAdapter.Index_download_photo || i == FcHomeAdapter.Index_download_video) {
            if (obj instanceof String) {
                downloadFileToLocal((String) obj);
            }
        } else if (i == FcHomeAdapter.Index_click_like) {
            if (obj instanceof RespFcListBean) {
                RespFcListBean model2 = (RespFcListBean) obj;
                if (model2.isHasThumb()) {
                    doCancelLikeFc(i2, model2);
                } else {
                    doLike(i2, model2);
                }
            }
        } else if (i == FcHomeAdapter.Index_click_reply) {
            if (obj instanceof RespFcListBean) {
                RespFcListBean respFcListBean = (RespFcListBean) obj;
            }
        } else if (i == FcHomeAdapter.Index_click_location && (obj instanceof RespFcListBean)) {
            RespFcListBean model3 = (RespFcListBean) obj;
            if (!TextUtils.isEmpty(model3.getLocationName()) && model3.getLongitude() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && model3.getLatitude() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                presentFragment(new FcLocationInfoActivity(new FcLocationInfoBean(model3.getLongitude(), model3.getLatitude(), model3.getLocationName(), model3.getLocationAddress(), model3.getLocationCity())));
            }
        }
    }

    public /* synthetic */ void lambda$onAction$1$FcHomeFragment(int position, RespFcListBean model, View dialog) {
        doDeleteItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$2$FcHomeFragment(int position, RespFcListBean model, View dialog) {
        doIgnoreItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$3$FcHomeFragment(RespFcListBean model, View dialog) {
        ArrayList<FcIgnoreUserBean> list = new ArrayList<>();
        list.add(new FcIgnoreUserBean(model.getCreateBy(), 2));
        doAddIgnoreUser(list);
    }

    public void onPresentFragment(BaseFragment baseFragment) {
        setStopPlayState();
        if (baseFragment != null) {
            presentFragment(baseFragment);
        }
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
        showReplyFcDialog(receiver, model.getForumID(), model.getCreateBy(), false, this.replyChildPosition == -1, model.isRecommend(), model.getRequiredVipLevel());
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

    public void doAfterPublishSuccess(RespFcListBean mResponseFcPublishBackBean) {
        KLog.d("----------mResponseFcPublishBackBean" + mResponseFcPublishBackBean);
        FcHomeAdapter fcHomeAdapter = this.mAdapter;
        if (fcHomeAdapter == null || fcHomeAdapter.getDataList().size() <= 0) {
            ArrayList<RespFcListBean> dataList = new ArrayList<>();
            dataList.add(mResponseFcPublishBackBean);
            dataList.add(new RespFcListBean());
            this.mAdapter.refresh(dataList);
        } else {
            this.mAdapter.getDataList().add(0, mResponseFcPublishBackBean);
            this.mAdapter.notifyItemInserted(0);
            FcHomeAdapter fcHomeAdapter2 = this.mAdapter;
            fcHomeAdapter2.notifyItemRangeChanged(0, fcHomeAdapter2.getCount());
        }
        refreshPageState(false, (String) null);
        this.rvFcList.scrollToPosition(0);
        saveNewFcToLocal(new HomeFcListBean(mResponseFcPublishBackBean));
    }

    /* access modifiers changed from: protected */
    public void doFollowAfterViewChange(int position, boolean isFollow) {
        MryTextView btnFollow;
        long createBy = ((RespFcListBean) this.mAdapter.get(position)).getCreateBy();
        if (this.mAdapter.getItemCount() > 0) {
            for (int j = this.mAdapter.getHeaderCount(); j < this.mAdapter.getCount(); j++) {
                if (((RespFcListBean) this.mAdapter.get(j)).getCreateBy() == createBy) {
                    ((RespFcListBean) this.mAdapter.getDataList().get(j)).setHasFollow(isFollow);
                    View viewByPosition = this.layoutManager.findViewByPosition(j);
                    if (!(viewByPosition == null || (btnFollow = (MryTextView) viewByPosition.findViewById(R.id.btn_follow)) == null || btnFollow.getVisibility() != 0)) {
                        btnFollow.setText(isFollow ? "已关注" : "关注");
                        btnFollow.setSelected(isFollow);
                    }
                }
            }
        }
        updateLocalFollowStatus(HomeFcListBean.class, createBy, isFollow);
    }

    /* access modifiers changed from: protected */
    public void doSetItemPermissionAfterViewChange(long forumId, int permission, int position) {
        ((RespFcListBean) this.mAdapter.get(position)).setPermission(permission);
        updateLocalItemPermissionStatus(HomeFcListBean.class, forumId, permission);
    }

    /* JADX WARNING: type inference failed for: r3v6, types: [android.view.View] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doLikeAfterViewChange(int r11, boolean r12, com.bjz.comm.net.bean.FcLikeBean r13) {
        /*
            r10 = this;
            androidx.recyclerview.widget.RecyclerView$LayoutManager r0 = r10.layoutManager
            android.view.View r0 = r0.findViewByPosition(r11)
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
            if (r13 == 0) goto L_0x009d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "------position"
            r3.append(r4)
            r3.append(r11)
            java.lang.String r4 = "  "
            r3.append(r4)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            com.socks.library.KLog.d(r3)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r3 = r10.mAdapter
            java.lang.Object r3 = r3.get(r11)
            com.bjz.comm.net.bean.RespFcListBean r3 = (com.bjz.comm.net.bean.RespFcListBean) r3
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r4 = r10.mAdapter
            java.lang.Object r4 = r4.get(r11)
            com.bjz.comm.net.bean.RespFcListBean r4 = (com.bjz.comm.net.bean.RespFcListBean) r4
            r4.setHasThumb(r12)
            if (r12 == 0) goto L_0x005d
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r4 = r10.mAdapter
            java.lang.Object r4 = r4.get(r11)
            com.bjz.comm.net.bean.RespFcListBean r4 = (com.bjz.comm.net.bean.RespFcListBean) r4
            int r5 = r3.getThumbUp()
            int r5 = r5 + r2
            r4.setThumbUp(r5)
            goto L_0x0075
        L_0x005d:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r4 = r10.mAdapter
            java.lang.Object r4 = r4.get(r11)
            com.bjz.comm.net.bean.RespFcListBean r4 = (com.bjz.comm.net.bean.RespFcListBean) r4
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r5 = r10.mAdapter
            java.lang.Object r5 = r5.get(r11)
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
            r1.setSelected(r12)
        L_0x008e:
            java.lang.Class<im.bclpbkiauv.javaBean.fc.HomeFcListBean> r5 = im.bclpbkiauv.javaBean.fc.HomeFcListBean.class
            long r6 = r3.getForumID()
            int r9 = r3.getThumbUp()
            r4 = r10
            r8 = r12
            r4.updateLocalItemLikeStatus(r5, r6, r8, r9)
        L_0x009d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcHomeFragment.doLikeAfterViewChange(int, boolean, com.bjz.comm.net.bean.FcLikeBean):void");
    }

    /* access modifiers changed from: protected */
    public void doDeleteItemAfterViewChange(long forumId, int position) {
        this.mAdapter.getDataList().remove(position);
        this.mAdapter.notifyItemRemoved(position);
        FcHomeAdapter fcHomeAdapter = this.mAdapter;
        fcHomeAdapter.notifyItemRangeChanged(position, fcHomeAdapter.getItemCount());
        refreshPageState(false, (String) null);
        deleteLocalItemById(HomeFcListBean.class, forumId);
    }

    /* access modifiers changed from: protected */
    public void doIgnoreItemAfterViewChange(long forumId, int position) {
        this.mAdapter.getDataList().remove(position);
        this.mAdapter.notifyItemRemoved(position);
        FcHomeAdapter fcHomeAdapter = this.mAdapter;
        fcHomeAdapter.notifyItemRangeChanged(position, fcHomeAdapter.getItemCount());
        refreshPageState(false, (String) null);
        deleteLocalItemById(HomeFcListBean.class, forumId);
    }

    /* access modifiers changed from: protected */
    public void doSetIgnoreUserAfterViewChange(boolean isIgnore, ArrayList<FcIgnoreUserBean> ignores) {
        if (isIgnore && ignores != null && ignores.size() > 0) {
            FcIgnoreUserBean ignoreUserBean = ignores.get(0);
            if (ignoreUserBean != null) {
                this.mAdapter.removeItemByUserID(ignoreUserBean.getUserID());
            }
            refreshPageState(false, (String) null);
            deleteLocalItemByUserId(HomeFcListBean.class, ignores.get(0).getUserID());
        }
    }

    /* access modifiers changed from: protected */
    public void doReplySuccAfterViewChange(FcReplyBean data, int replyParentPosition2) {
        RespFcListBean respFcListBean = (RespFcListBean) this.mAdapter.get(replyParentPosition2);
        if (!(respFcListBean == null || data == null)) {
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
        updateLocalReplyStatus(HomeFcListBean.class, data.getForumID(), data);
    }

    public void doDeleteReplySuccAfterViewChange(long forumId, long commentId, int parentPosition, int childPosition) {
        View viewByPosition;
        FcHomeItemReplyAdapter adapter;
        int i = parentPosition;
        int i2 = childPosition;
        RespFcListBean respFcListBean = (RespFcListBean) this.mAdapter.get(i);
        if (!(respFcListBean == null || (viewByPosition = this.layoutManager.findViewByPosition(i)) == null)) {
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
        if (respFcListBean != null) {
            deleteLocalReply(HomeFcListBean.class, forumId, commentId, respFcListBean.getCommentCount());
        }
    }

    public void onError(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail) : msg);
    }
}
