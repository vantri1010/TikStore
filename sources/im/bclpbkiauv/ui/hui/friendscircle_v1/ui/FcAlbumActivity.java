package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import com.bjz.comm.net.bean.RespFcAlbumListBean;
import com.bjz.comm.net.bean.UrlInfoBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FCAlbumListPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import im.bclpbkiauv.javaBean.fc.FollowedFcListBean;
import im.bclpbkiauv.javaBean.fc.HomeFcListBean;
import im.bclpbkiauv.javaBean.fc.RecommendFcListBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.decoration.StickyDecoration;
import im.bclpbkiauv.ui.decoration.listener.GroupListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FriendsCircleAlbumListAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.FcDBHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.AlbumPreviewActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.MyRecyclerView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import java.util.ArrayList;
import java.util.List;

public class FcAlbumActivity extends CommFcActivity implements BaseFcContract.IFcPageAlbumListView, AdapterView.OnItemClickListener {
    /* access modifiers changed from: private */
    public String TAG = FcAlbumActivity.class.getSimpleName();
    private MryEmptyView emptyView;
    GridLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public FriendsCircleAlbumListAdapter mAdapter;
    private BaseFcContract.IFcPageAlbumListPresenter mPresenter;
    private MyRecyclerView mRecyclerView;
    /* access modifiers changed from: private */
    public long requestId = 0;
    private SmartRefreshLayout smartRefreshLayout;

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_friends_circle_albums;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar(LocaleController.getString("Gallery", R.string.Gallery));
        this.mRecyclerView = (MyRecyclerView) this.fragmentView.findViewById(R.id.rv_albums);
        this.smartRefreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.smartRefreshLayout);
        this.mRecyclerView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initEmptyView();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mContext, 3);
        this.layoutManager = gridLayoutManager;
        this.mRecyclerView.setLayoutManager(gridLayoutManager);
        FriendsCircleAlbumListAdapter friendsCircleAlbumListAdapter = new FriendsCircleAlbumListAdapter(new ArrayList(), R.layout.item_friends_circle_album_list, this, getParentActivity());
        this.mAdapter = friendsCircleAlbumListAdapter;
        this.mRecyclerView.setAdapter(friendsCircleAlbumListAdapter);
        StickyDecoration decoration = StickyDecoration.Builder.init(new GroupListener() {
            public String getGroupName(int position) {
                if (FcAlbumActivity.this.mAdapter == null || FcAlbumActivity.this.mAdapter.getDataList().size() <= position || position <= -1) {
                    return null;
                }
                return TimeUtils.YearMon(((RespFcAlbumListBean) FcAlbumActivity.this.mAdapter.getDataList().get(position)).getCreateAt());
            }
        }).setGroupBackground(Theme.getColor(Theme.key_windowBackgroundGray)).setGroupHeight(AndroidUtilities.dp(35.0f)).setDivideColor(Theme.getColor(Theme.key_windowBackgroundGray)).setGroupTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText)).setGroupTextSize((int) AndroidUtilities.sp2px(15.0f)).build();
        decoration.resetSpan(this.mRecyclerView, this.layoutManager);
        this.mRecyclerView.addItemDecoration(decoration);
    }

    private void initEmptyView() {
        FrameLayout flContainer = (FrameLayout) this.fragmentView.findViewById(R.id.fl_container);
        MryEmptyView mryEmptyView = new MryEmptyView(this.mContext);
        this.emptyView = mryEmptyView;
        mryEmptyView.attach((ViewGroup) flContainer);
        this.emptyView.setEmptyText(LocaleController.getString(R.string.friendscircle_album_list_no_data));
        this.emptyView.setEmptyResId(R.mipmap.img_empty_default);
        this.emptyView.setErrorResId(R.mipmap.img_empty_default);
        this.emptyView.setOnEmptyClickListener(new MryEmptyView.OnEmptyOrErrorClickListener() {
            public final boolean onEmptyViewButtonClick(boolean z) {
                return FcAlbumActivity.this.lambda$initEmptyView$0$FcAlbumActivity(z);
            }
        });
        flContainer.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
    }

    public /* synthetic */ boolean lambda$initEmptyView$0$FcAlbumActivity(boolean isEmptyButton) {
        this.requestId = 0;
        refreshPageState(true, (String) null);
        getFcAlbumList();
        return false;
    }

    /* access modifiers changed from: protected */
    public void initData() {
        this.mPresenter = new FCAlbumListPresenter(this);
        this.smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                long unused = FcAlbumActivity.this.requestId = 0;
                FcAlbumActivity.this.getFcAlbumList();
            }

            public void onLoadMore(RefreshLayout refreshLayout) {
                List<RespFcAlbumListBean> dataList = FcAlbumActivity.this.mAdapter.getDataList();
                if (dataList != null && dataList.size() > 20) {
                    long unused = FcAlbumActivity.this.requestId = dataList.get(dataList.size() - 1).getID();
                    FcAlbumActivity.this.getFcAlbumList();
                }
            }
        });
        this.requestId = 0;
        refreshPageState(true, (String) null);
        getFcAlbumList();
    }

    /* access modifiers changed from: private */
    public void refreshPageState(boolean isRefreshing, String errorMsg) {
        if (isRefreshing) {
            this.emptyView.showLoading();
        } else if (this.requestId == 0 && !TextUtils.isEmpty(errorMsg) && this.mAdapter.getDataList().size() == 0) {
            this.emptyView.showError(errorMsg);
        } else {
            FriendsCircleAlbumListAdapter friendsCircleAlbumListAdapter = this.mAdapter;
            if (friendsCircleAlbumListAdapter != null && friendsCircleAlbumListAdapter.getDataList().size() == 0) {
                this.emptyView.showEmpty();
            } else if (this.emptyView.getCurrentStatus() != 2) {
                this.emptyView.showContent();
            }
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        BaseFcContract.IFcPageAlbumListPresenter iFcPageAlbumListPresenter = this.mPresenter;
        if (iFcPageAlbumListPresenter != null) {
            iFcPageAlbumListPresenter.unSubscribeTask();
        }
    }

    /* access modifiers changed from: private */
    public void getFcAlbumList() {
        if (this.requestId == 0) {
            this.smartRefreshLayout.setEnableLoadMore(false);
        } else {
            this.smartRefreshLayout.setEnableRefresh(false);
        }
        this.mPresenter.getAlbumList(getUserConfig().getClientUserId(), this.requestId, 20);
    }

    public void getAlbumListSucc(ArrayList<RespFcAlbumListBean> data) {
        this.smartRefreshLayout.finishRefresh();
        this.smartRefreshLayout.finishLoadMore();
        setData(data);
    }

    public void getAlbumListFailed(String msg) {
        this.smartRefreshLayout.finishRefresh();
        this.smartRefreshLayout.finishLoadMore();
        setData((ArrayList<RespFcAlbumListBean>) null);
        FcToastUtils.show(TextUtils.isEmpty(msg) ? Integer.valueOf(R.string.friendscircle_home_request_fail) : msg);
        refreshPageState(false, msg);
    }

    private void setData(ArrayList<RespFcAlbumListBean> data) {
        if (this.requestId == 0) {
            if (data == null || data.size() == 0) {
                this.smartRefreshLayout.setEnableLoadMore(false);
            } else {
                if (data.size() < 20) {
                    this.smartRefreshLayout.setEnableLoadMore(false);
                } else {
                    this.smartRefreshLayout.setEnableLoadMore(true);
                }
                this.mAdapter.refresh(data);
            }
            refreshPageState(false, (String) null);
            return;
        }
        if (data == null || data.size() < 20) {
            this.smartRefreshLayout.setEnableLoadMore(false);
        }
        if (data != null && data.size() > 0) {
            this.mAdapter.loadMore(data);
        }
        refreshPageState(false, (String) null);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        List<UrlInfoBean> urlInfoBeanList = new ArrayList<>();
        for (RespFcAlbumListBean bean : this.mAdapter.getDataList()) {
            if (!(bean == null || bean.getName() == null)) {
                urlInfoBeanList.add(new UrlInfoBean(bean));
            }
        }
        AlbumPreviewActivity albumPreviewActivity = new AlbumPreviewActivity(urlInfoBeanList, position);
        albumPreviewActivity.setOnDeleteDelegate(new AlbumPreviewActivity.OnDeleteDelegate() {
            public void onDelete(long forumID, int position) {
                FcDBHelper.getInstance().deleteItemById(RecommendFcListBean.class, forumID);
                FcDBHelper.getInstance().deleteItemById(HomeFcListBean.class, forumID);
                FcDBHelper.getInstance().deleteItemById(FollowedFcListBean.class, forumID);
                NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcIgnoreOrDeleteItem, FcAlbumActivity.this.TAG, Long.valueOf(forumID));
                FcAlbumActivity.this.mAdapter.removeItemByForumID(forumID);
                FcAlbumActivity.this.refreshPageState(false, (String) null);
            }
        });
        presentFragment(albumPreviewActivity);
    }
}
