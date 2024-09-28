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
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcPageRecommendPresenter;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.socks.library.KLog;
import im.bclpbkiauv.javaBean.fc.FcLocationInfoBean;
import im.bclpbkiauv.javaBean.fc.RecommendFcListBean;
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

public class FcRecommendFragment extends CommFcListFragment implements NotificationCenter.NotificationCenterDelegate, FcItemActionClickListener, BaseFcContract.IFcPageRecommendView, FcDoReplyDialog.OnFcDoReplyListener {
    private String TAG = FcRecommendFragment.class.getSimpleName();
    /* access modifiers changed from: private */
    public AutoPlayTool autoPlayTool;
    private int[] coord = new int[2];
    private int[] coordedt = new int[2];
    private MryEmptyView emptyView;
    private FcHomeAdapter mAdapter;
    private BaseFcContract.IFcPageRecommendPresenter mPresenter;
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
            if (FcRecommendFragment.this.autoPlayTool != null && this.isScroll) {
                FcRecommendFragment.this.autoPlayTool.onScrolledAndDeactivate();
            }
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            this.isScroll = newState != 0;
            if (newState != 0) {
                return;
            }
            if (FcRecommendFragment.this.mSmartRefreshLayout.getState() == RefreshState.None || FcRecommendFragment.this.mSmartRefreshLayout.getState() == RefreshState.RefreshFinish) {
                FcRecommendFragment.this.isActivePlayer(recyclerView);
            }
        }
    };

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.fragment_fc_page_recommend;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.mSmartRefreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.smartRefreshLayout);
        this.rvFcList = (RecyclerView) this.fragmentView.findViewById(R.id.rv_fc_list);
        this.mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                int unused = FcRecommendFragment.this.pageNo = 0;
                FcRecommendFragment.this.getFcPageList();
            }

            public void onLoadMore(RefreshLayout refreshLayout) {
                FcRecommendFragment.this.getFcPageList();
            }
        });
        this.layoutManager = new LinearLayoutManager(this.mContext, 1, false);
        this.rvFcList.setLayoutManager(this.layoutManager);
        this.rvFcList.addItemDecoration(new SpacesItemDecoration(AndroidUtilities.dp(7.0f)));
        FcHomeAdapter fcHomeAdapter = new FcHomeAdapter(new ArrayList(), getParentActivity(), getClassGuid(), 1, this);
        this.mAdapter = fcHomeAdapter;
        fcHomeAdapter.setFooterCount(1);
        this.mAdapter.isShowReplyList(false);
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
        this.emptyView.setEmptyText(LocaleController.getString(R.string.NoRecommendPageDataMessages));
        this.emptyView.setEmptyResId(R.mipmap.img_empty_default);
        this.emptyView.setErrorResId(R.mipmap.img_empty_default);
        this.emptyView.getTextView().setTextColor(this.mContext.getResources().getColor(R.color.color_FFDBC9B8));
        this.emptyView.getBtn().setPrimaryRadiusAdjustBoundsFillStyle();
        this.emptyView.getBtn().setRoundBgGradientColors(new int[]{-4789508, -13187843});
        this.emptyView.getBtn().setStrokeWidth(0);
        this.emptyView.getBtn().setPadding(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(5.0f));
        this.emptyView.setOnEmptyClickListener(new MryEmptyView.OnEmptyOrErrorClickListener() {
            public final boolean onEmptyViewButtonClick(boolean z) {
                return FcRecommendFragment.this.lambda$initEmptyView$0$FcRecommendFragment(z);
            }
        });
        flContainer.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
    }

    public /* synthetic */ boolean lambda$initEmptyView$0$FcRecommendFragment(boolean isEmptyButton) {
        this.pageNo = 0;
        refreshPageState(true, (String) null);
        getFcPageList();
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
        this.mPresenter = new FcPageRecommendPresenter(this);
        getDBCache();
        if ((this.fcVersionBean != null && this.fcVersionBean.isRecommendState()) || this.pageNo == 0) {
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
                if (FcRecommendFragment.this.rvScrollListener != null) {
                    FcRecommendFragment.this.rvScrollListener.onScrollStateChanged(FcRecommendFragment.this.rvFcList, 0);
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
        BaseFcContract.IFcPageRecommendPresenter iFcPageRecommendPresenter = this.mPresenter;
        if (iFcPageRecommendPresenter != null) {
            iFcPageRecommendPresenter.unSubscribeTask();
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

    /* JADX WARNING: Removed duplicated region for block: B:132:0x0243  */
    /* JADX WARNING: Removed duplicated region for block: B:193:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r17, int r18, java.lang.Object... r19) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "---------通知"
            r0.append(r1)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            com.socks.library.KLog.d(r0)
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcFollowStatusUpdate
            r1 = 2
            r2 = -1
            r3 = 0
            r4 = 1
            if (r8 != r0) goto L_0x0068
            r0 = r19[r3]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r3 = r7.TAG
            boolean r3 = android.text.TextUtils.equals(r3, r0)
            if (r3 != 0) goto L_0x0066
            r3 = r19[r4]
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            r1 = r19[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r5 = -1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r6 = r7.mAdapter
            if (r6 == 0) goto L_0x0061
            java.util.List r6 = r6.getDataList()
            r9 = 0
        L_0x0046:
            int r10 = r6.size()
            if (r9 >= r10) goto L_0x0061
            java.lang.Object r10 = r6.get(r9)
            com.bjz.comm.net.bean.RespFcListBean r10 = (com.bjz.comm.net.bean.RespFcListBean) r10
            if (r10 == 0) goto L_0x005e
            long r11 = r10.getCreateBy()
            int r13 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r13 != 0) goto L_0x005e
            r5 = r9
            goto L_0x0061
        L_0x005e:
            int r9 = r9 + 1
            goto L_0x0046
        L_0x0061:
            if (r5 == r2) goto L_0x0066
            r7.doFollowAfterViewChange(r5, r1)
        L_0x0066:
            goto L_0x026e
        L_0x0068:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcPermissionStatusUpdate
            if (r8 != r0) goto L_0x00b4
            r0 = r19[r3]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r3 = r7.TAG
            boolean r3 = android.text.TextUtils.equals(r3, r0)
            if (r3 != 0) goto L_0x00b2
            r3 = r19[r4]
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            r1 = r19[r1]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r5 = -1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r6 = r7.mAdapter
            if (r6 == 0) goto L_0x00ad
            java.util.List r6 = r6.getDataList()
            r9 = 0
        L_0x0092:
            int r10 = r6.size()
            if (r9 >= r10) goto L_0x00ad
            java.lang.Object r10 = r6.get(r9)
            com.bjz.comm.net.bean.RespFcListBean r10 = (com.bjz.comm.net.bean.RespFcListBean) r10
            if (r10 == 0) goto L_0x00aa
            long r11 = r10.getForumID()
            int r13 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r13 != 0) goto L_0x00aa
            r5 = r9
            goto L_0x00ad
        L_0x00aa:
            int r9 = r9 + 1
            goto L_0x0092
        L_0x00ad:
            if (r5 == r2) goto L_0x00b2
            r7.doSetItemPermissionAfterViewChange(r3, r1, r5)
        L_0x00b2:
            goto L_0x026e
        L_0x00b4:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcIgnoreOrDeleteItem
            r5 = 0
            if (r8 != r0) goto L_0x00fe
            r0 = r19[r3]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r7.TAG
            boolean r1 = android.text.TextUtils.equals(r1, r0)
            if (r1 != 0) goto L_0x00fc
            r1 = r19[r4]
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
            r1 = -1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r9 = r7.mAdapter
            if (r9 == 0) goto L_0x00f7
            int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r10 <= 0) goto L_0x00f7
            java.util.List r5 = r9.getDataList()
            r6 = 0
        L_0x00dc:
            int r9 = r5.size()
            if (r6 >= r9) goto L_0x00f7
            java.lang.Object r9 = r5.get(r6)
            com.bjz.comm.net.bean.RespFcListBean r9 = (com.bjz.comm.net.bean.RespFcListBean) r9
            if (r9 == 0) goto L_0x00f4
            long r10 = r9.getForumID()
            int r12 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r12 != 0) goto L_0x00f4
            r1 = r6
            goto L_0x00f7
        L_0x00f4:
            int r6 = r6 + 1
            goto L_0x00dc
        L_0x00f7:
            if (r1 == r2) goto L_0x00fc
            r7.doDeleteItemAfterViewChange(r3, r1)
        L_0x00fc:
            goto L_0x026e
        L_0x00fe:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcLikeStatusUpdate
            if (r8 != r0) goto L_0x0156
            r0 = r19[r3]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r3 = r7.TAG
            boolean r3 = android.text.TextUtils.equals(r3, r0)
            if (r3 != 0) goto L_0x0154
            r3 = r19[r4]
            com.bjz.comm.net.bean.FcLikeBean r3 = (com.bjz.comm.net.bean.FcLikeBean) r3
            r1 = r19[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r4 = -1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r9 = r7.mAdapter
            if (r9 == 0) goto L_0x014f
            if (r3 == 0) goto L_0x014f
            long r9 = r3.getCommentID()
            int r11 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r11 != 0) goto L_0x014f
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r5 = r7.mAdapter
            java.util.List r5 = r5.getDataList()
            r6 = 0
        L_0x0130:
            int r9 = r5.size()
            if (r6 >= r9) goto L_0x014f
            java.lang.Object r9 = r5.get(r6)
            com.bjz.comm.net.bean.RespFcListBean r9 = (com.bjz.comm.net.bean.RespFcListBean) r9
            if (r9 == 0) goto L_0x014c
            long r10 = r9.getForumID()
            long r12 = r3.getForumID()
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x014c
            r4 = r6
            goto L_0x014f
        L_0x014c:
            int r6 = r6 + 1
            goto L_0x0130
        L_0x014f:
            if (r4 == r2) goto L_0x0154
            r7.doLikeAfterViewChange(r4, r1, r3)
        L_0x0154:
            goto L_0x026e
        L_0x0156:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcIgnoreUser
            if (r8 != r0) goto L_0x01aa
            r0 = r19[r3]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r7.TAG
            boolean r1 = android.text.TextUtils.equals(r1, r0)
            if (r1 != 0) goto L_0x01a8
            r1 = r19[r4]
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            r5 = -1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r6 = r7.mAdapter
            if (r6 == 0) goto L_0x01a3
            if (r1 == 0) goto L_0x01a3
            int r6 = r1.size()
            if (r6 <= 0) goto L_0x01a3
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r6 = r7.mAdapter
            java.util.List r6 = r6.getDataList()
            r9 = 0
        L_0x017e:
            int r10 = r6.size()
            if (r9 >= r10) goto L_0x01a3
            java.lang.Object r10 = r6.get(r9)
            com.bjz.comm.net.bean.RespFcListBean r10 = (com.bjz.comm.net.bean.RespFcListBean) r10
            if (r10 == 0) goto L_0x01a0
            long r11 = r10.getCreateBy()
            java.lang.Object r13 = r1.get(r3)
            com.bjz.comm.net.bean.FcIgnoreUserBean r13 = (com.bjz.comm.net.bean.FcIgnoreUserBean) r13
            long r13 = r13.getUserID()
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 != 0) goto L_0x01a0
            r5 = r9
            goto L_0x01a3
        L_0x01a0:
            int r9 = r9 + 1
            goto L_0x017e
        L_0x01a3:
            if (r5 == r2) goto L_0x01a8
            r7.doSetIgnoreUserAfterViewChange(r4, r1)
        L_0x01a8:
            goto L_0x026e
        L_0x01aa:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcReplyItem
            if (r8 != r0) goto L_0x01f0
            r0 = r19[r3]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r7.TAG
            boolean r1 = android.text.TextUtils.equals(r1, r0)
            if (r1 != 0) goto L_0x01ee
            r1 = r19[r4]
            com.bjz.comm.net.bean.FcReplyBean r1 = (com.bjz.comm.net.bean.FcReplyBean) r1
            r3 = -1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r4 = r7.mAdapter
            if (r4 == 0) goto L_0x01e9
            if (r1 == 0) goto L_0x01e9
            java.util.List r4 = r4.getDataList()
            r5 = 0
        L_0x01ca:
            int r6 = r4.size()
            if (r5 >= r6) goto L_0x01e9
            java.lang.Object r6 = r4.get(r5)
            com.bjz.comm.net.bean.RespFcListBean r6 = (com.bjz.comm.net.bean.RespFcListBean) r6
            if (r6 == 0) goto L_0x01e6
            long r9 = r6.getForumID()
            long r11 = r1.getForumID()
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x01e6
            r3 = r5
            goto L_0x01e9
        L_0x01e6:
            int r5 = r5 + 1
            goto L_0x01ca
        L_0x01e9:
            if (r3 == r2) goto L_0x01ee
            r7.doReplySuccAfterViewChange(r1, r3)
        L_0x01ee:
            goto L_0x026e
        L_0x01f0:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcDeleteReplyItem
            if (r8 != r0) goto L_0x024d
            r0 = r19[r3]
            r9 = r0
            java.lang.String r9 = (java.lang.String) r9
            java.lang.String r0 = r7.TAG
            boolean r0 = android.text.TextUtils.equals(r0, r9)
            if (r0 != 0) goto L_0x026d
            r0 = r19[r4]
            java.lang.Long r0 = (java.lang.Long) r0
            long r10 = r0.longValue()
            r0 = r19[r1]
            java.lang.Long r0 = (java.lang.Long) r0
            long r12 = r0.longValue()
            r0 = -1
            r14 = -1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter r1 = r7.mAdapter
            if (r1 == 0) goto L_0x0240
            int r3 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x0240
            int r3 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x0240
            java.util.List r1 = r1.getDataList()
            r3 = 0
        L_0x0224:
            int r4 = r1.size()
            if (r3 >= r4) goto L_0x0240
            java.lang.Object r4 = r1.get(r3)
            com.bjz.comm.net.bean.RespFcListBean r4 = (com.bjz.comm.net.bean.RespFcListBean) r4
            if (r4 == 0) goto L_0x023d
            long r5 = r4.getForumID()
            int r15 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r15 != 0) goto L_0x023d
            r0 = r3
            r15 = r0
            goto L_0x0241
        L_0x023d:
            int r3 = r3 + 1
            goto L_0x0224
        L_0x0240:
            r15 = r0
        L_0x0241:
            if (r15 == r2) goto L_0x026d
            r0 = r16
            r1 = r10
            r3 = r12
            r5 = r15
            r6 = r14
            r0.doDeleteReplySuccAfterViewChange(r1, r3, r5, r6)
            goto L_0x026d
        L_0x024d:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fcPublishSuccess
            if (r8 != r0) goto L_0x026d
            r0 = r19[r3]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r1 = r7.TAG
            boolean r1 = android.text.TextUtils.equals(r1, r0)
            if (r1 != 0) goto L_0x026e
            r1 = r19[r4]
            com.bjz.comm.net.bean.RespFcListBean r1 = (com.bjz.comm.net.bean.RespFcListBean) r1
            if (r1 == 0) goto L_0x026e
            boolean r2 = r1.isRecommend()
            if (r2 == 0) goto L_0x026e
            r7.doAfterPublishSuccess(r1)
            goto L_0x026e
        L_0x026d:
        L_0x026e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcRecommendFragment.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public void setPageNo(int pageNo2) {
        this.pageNo = pageNo2;
    }

    public void getFcPageList() {
        this.mPresenter.getFcList(20, this.pageNo == 0 ? 0 : this.mAdapter.getEndListId());
    }

    private void getDBCache() {
        ArrayList<RecommendFcListBean> queryAll = FcDBHelper.getInstance().getQueryByOrder(RecommendFcListBean.class);
        ArrayList<RespFcListBean> tempList = new ArrayList<>();
        Iterator<RecommendFcListBean> it = queryAll.iterator();
        while (it.hasNext()) {
            RecommendFcListBean recommendFcListBean = it.next();
            if (recommendFcListBean != null) {
                tempList.add(recommendFcListBean);
            }
        }
        setData(tempList);
    }

    public void getFcListSucc(ArrayList<RespFcListBean> data) {
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        if (this.pageNo == 0) {
            refreshDotStatus(0);
            FcDBHelper.getInstance().deleteAll(RecommendFcListBean.class);
        }
        if (data != null) {
            ArrayList<RecommendFcListBean> tempInsertList = new ArrayList<>();
            Iterator<RespFcListBean> it = data.iterator();
            while (it.hasNext()) {
                tempInsertList.add(new RecommendFcListBean(it.next()));
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
                        FcRecommendFragment.this.lambda$onAction$1$FcRecommendFragment(this.f$1, this.f$2, view);
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
                        FcRecommendFragment.this.lambda$onAction$2$FcRecommendFragment(this.f$1, this.f$2, view);
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
                        FcRecommendFragment.this.lambda$onAction$3$FcRecommendFragment(this.f$1, view);
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

    public /* synthetic */ void lambda$onAction$1$FcRecommendFragment(int position, RespFcListBean model, View dialog) {
        doDeleteItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$2$FcRecommendFragment(int position, RespFcListBean model, View dialog) {
        doIgnoreItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$3$FcRecommendFragment(RespFcListBean model, View dialog) {
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
        showReplyFcDialog(receiver, model.getForumID(), model.getCreateBy(), true, this.replyChildPosition == -1, true, model.getRequiredVipLevel());
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
        saveNewFcToLocal(new RecommendFcListBean(mResponseFcPublishBackBean));
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
        updateLocalFollowStatus(RecommendFcListBean.class, createBy, isFollow);
    }

    /* access modifiers changed from: protected */
    public void doSetItemPermissionAfterViewChange(long forumId, int permission, int position) {
        ((RespFcListBean) this.mAdapter.get(position)).setPermission(permission);
        updateLocalItemPermissionStatus(RecommendFcListBean.class, forumId, permission);
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
            java.lang.Class<im.bclpbkiauv.javaBean.fc.RecommendFcListBean> r5 = im.bclpbkiauv.javaBean.fc.RecommendFcListBean.class
            long r6 = r3.getForumID()
            int r9 = r3.getThumbUp()
            r4 = r10
            r8 = r12
            r4.updateLocalItemLikeStatus(r5, r6, r8, r9)
        L_0x009d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.fragments.FcRecommendFragment.doLikeAfterViewChange(int, boolean, com.bjz.comm.net.bean.FcLikeBean):void");
    }

    /* access modifiers changed from: protected */
    public void doDeleteItemAfterViewChange(long forumId, int position) {
        this.mAdapter.getDataList().remove(position);
        this.mAdapter.notifyItemRemoved(position);
        FcHomeAdapter fcHomeAdapter = this.mAdapter;
        fcHomeAdapter.notifyItemRangeChanged(position, fcHomeAdapter.getItemCount());
        refreshPageState(false, (String) null);
        deleteLocalItemById(RecommendFcListBean.class, forumId);
    }

    /* access modifiers changed from: protected */
    public void doIgnoreItemAfterViewChange(long forumId, int position) {
        this.mAdapter.getDataList().remove(position);
        this.mAdapter.notifyItemRemoved(position);
        FcHomeAdapter fcHomeAdapter = this.mAdapter;
        fcHomeAdapter.notifyItemRangeChanged(position, fcHomeAdapter.getItemCount());
        refreshPageState(false, (String) null);
        deleteLocalItemById(RecommendFcListBean.class, forumId);
    }

    /* access modifiers changed from: protected */
    public void doSetIgnoreUserAfterViewChange(boolean isIgnore, ArrayList<FcIgnoreUserBean> ignores) {
        if (isIgnore && ignores != null && ignores.size() > 0) {
            FcIgnoreUserBean ignoreUserBean = ignores.get(0);
            if (ignoreUserBean != null) {
                this.mAdapter.removeItemByUserID(ignoreUserBean.getUserID());
            }
            refreshPageState(false, (String) null);
            deleteLocalItemByUserId(RecommendFcListBean.class, ignores.get(0).getUserID());
        }
    }

    /* access modifiers changed from: protected */
    public void doReplySuccAfterViewChange(FcReplyBean data, int replyParentPosition2) {
        ArrayList<FcReplyBean> morelist = new ArrayList<>();
        morelist.add(data);
        RespFcListBean respFcListBean = (RespFcListBean) this.mAdapter.get(replyParentPosition2);
        if (respFcListBean != null) {
            ArrayList<FcReplyBean> comments = respFcListBean.getComments();
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
        updateLocalReplyStatus(RecommendFcListBean.class, data.getForumID(), data);
    }

    public void doDeleteReplySuccAfterViewChange(long forumId, long commentId, int parentPosition, int childPosition) {
        FcHomeItemReplyAdapter adapter;
        int i = parentPosition;
        int i2 = childPosition;
        RespFcListBean respFcListBean = (RespFcListBean) this.mAdapter.get(i);
        if (respFcListBean != null) {
            View viewByPosition = this.layoutManager.findViewByPosition(i);
            if (viewByPosition != null) {
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
                deleteLocalReply(RecommendFcListBean.class, forumId, commentId, respFcListBean.getCommentCount());
            }
        }
    }

    public void onError(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail) : msg);
    }
}
