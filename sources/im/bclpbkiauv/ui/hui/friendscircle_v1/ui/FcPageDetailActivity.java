package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.text.TextUtils;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcLikesBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcPageDetailPresenter;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.socks.library.KLog;
import im.bclpbkiauv.javaBean.fc.FcLocationInfoBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.ShapeUtils;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailLikedUserAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDialogChildReplyAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcListActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.state.ScreenViewState;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.AutoPlayTool;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcChildReplyListDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FcPageDetailActivity extends CommFcListActivity implements FcItemActionClickListener, BaseFcContract.IFcPageDetailView, FcChildReplyListDialog.ChildReplyListListener {
    /* access modifiers changed from: private */
    public AutoPlayTool autoPlayTool;
    private MryTextView btnReply;
    private int commentChildPosition;
    private FcReplyBean commentItemModel;
    private int commentPageNo;
    private int commentParentPosition;
    private int[] coord;
    private int[] coordedt;
    private long forumId;
    private boolean isShowAtUser;
    private boolean isShowReplyDialog;
    int likeUserPageNo;
    private FcDetailAdapter mAdapter;
    private FcDialogChildReplyAdapter mChildReplyListAdapter;
    private BaseFcContract.IFcPageDetailPresenter mPresenter;
    private RespFcListBean mRespFcDetailBean;
    /* access modifiers changed from: private */
    public SmartRefreshLayout mSmartRefreshLayout;
    private int pageIndex;
    private int replyCommentPosition;
    private FcReplyBean replyItemModel;
    private FcChildReplyListDialog replyListDialog;
    private int replyPageNo;
    private int replyPosition;
    /* access modifiers changed from: private */
    public RecyclerView rvFcList;
    RecyclerView.OnScrollListener rvScrollListener;
    private long userId;

    public FcPageDetailActivity(long forumId2) {
        this.coord = new int[2];
        this.coordedt = new int[2];
        this.commentPageNo = 0;
        this.commentParentPosition = -1;
        this.commentChildPosition = -1;
        this.isShowReplyDialog = false;
        this.pageIndex = 2;
        this.isShowAtUser = false;
        this.likeUserPageNo = 0;
        this.replyPageNo = 0;
        this.replyCommentPosition = -1;
        this.replyPosition = -1;
        this.rvScrollListener = new RecyclerView.OnScrollListener() {
            boolean isScroll = false;

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (FcPageDetailActivity.this.autoPlayTool != null && this.isScroll) {
                    FcPageDetailActivity.this.autoPlayTool.onScrolledAndDeactivate();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                this.isScroll = newState != 0;
                if (newState != 0) {
                    return;
                }
                if (FcPageDetailActivity.this.mSmartRefreshLayout.getState() == RefreshState.None || FcPageDetailActivity.this.mSmartRefreshLayout.getState() == RefreshState.RefreshFinish) {
                    FcPageDetailActivity.this.isActivePlayer(recyclerView);
                }
            }
        };
        this.forumId = forumId2;
    }

    public FcPageDetailActivity(RespFcListBean mRespFcDetailBean2, boolean isShowReplyDialog2) {
        this.coord = new int[2];
        this.coordedt = new int[2];
        this.commentPageNo = 0;
        this.commentParentPosition = -1;
        this.commentChildPosition = -1;
        this.isShowReplyDialog = false;
        this.pageIndex = 2;
        this.isShowAtUser = false;
        this.likeUserPageNo = 0;
        this.replyPageNo = 0;
        this.replyCommentPosition = -1;
        this.replyPosition = -1;
        this.rvScrollListener = new RecyclerView.OnScrollListener() {
            boolean isScroll = false;

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (FcPageDetailActivity.this.autoPlayTool != null && this.isScroll) {
                    FcPageDetailActivity.this.autoPlayTool.onScrolledAndDeactivate();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                this.isScroll = newState != 0;
                if (newState != 0) {
                    return;
                }
                if (FcPageDetailActivity.this.mSmartRefreshLayout.getState() == RefreshState.None || FcPageDetailActivity.this.mSmartRefreshLayout.getState() == RefreshState.RefreshFinish) {
                    FcPageDetailActivity.this.isActivePlayer(recyclerView);
                }
            }
        };
        this.mRespFcDetailBean = mRespFcDetailBean2;
        if (mRespFcDetailBean2 != null) {
            this.forumId = mRespFcDetailBean2.getForumID();
            this.userId = mRespFcDetailBean2.getCreateBy();
        }
        this.isShowReplyDialog = isShowReplyDialog2;
        KLog.d("-----mResFcDetailBean" + mRespFcDetailBean2.toString());
    }

    public FcPageDetailActivity(RespFcListBean mRespFcDetailBean2, int pageIndex2, boolean isShowReplyDialog2) {
        this.coord = new int[2];
        this.coordedt = new int[2];
        boolean z = false;
        this.commentPageNo = 0;
        this.commentParentPosition = -1;
        this.commentChildPosition = -1;
        this.isShowReplyDialog = false;
        this.pageIndex = 2;
        this.isShowAtUser = false;
        this.likeUserPageNo = 0;
        this.replyPageNo = 0;
        this.replyCommentPosition = -1;
        this.replyPosition = -1;
        this.rvScrollListener = new RecyclerView.OnScrollListener() {
            boolean isScroll = false;

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (FcPageDetailActivity.this.autoPlayTool != null && this.isScroll) {
                    FcPageDetailActivity.this.autoPlayTool.onScrolledAndDeactivate();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                this.isScroll = newState != 0;
                if (newState != 0) {
                    return;
                }
                if (FcPageDetailActivity.this.mSmartRefreshLayout.getState() == RefreshState.None || FcPageDetailActivity.this.mSmartRefreshLayout.getState() == RefreshState.RefreshFinish) {
                    FcPageDetailActivity.this.isActivePlayer(recyclerView);
                }
            }
        };
        this.mRespFcDetailBean = mRespFcDetailBean2;
        this.pageIndex = pageIndex2;
        this.isShowAtUser = pageIndex2 == 1 ? true : z;
        if (mRespFcDetailBean2 != null) {
            this.forumId = mRespFcDetailBean2.getForumID();
            this.userId = mRespFcDetailBean2.getCreateBy();
        }
        this.isShowReplyDialog = isShowReplyDialog2;
        KLog.d("-----mResFcDetailBean" + mRespFcDetailBean2.toString());
    }

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_fc_page_detail;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initActionBar(LocaleController.getString("friends_circle_detail", R.string.friends_circle_detail));
        this.actionBar.setCastShadows(false);
        View view = new View(getParentActivity());
        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.actionBar.addView(view, LayoutHelper.createFrame(-1.0f, 0.5f, 80));
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        if (getParentActivity() != null) {
            getParentActivity().getWindow().setSoftInputMode(16);
        }
        VideoPlayerManager.getInstance().setVolume(0);
        this.mSmartRefreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.smartRefreshLayout);
        this.rvFcList = (RecyclerView) this.fragmentView.findViewById(R.id.rv_fc_list);
        MryTextView mryTextView = (MryTextView) this.fragmentView.findViewById(R.id.btn_reply);
        this.btnReply = mryTextView;
        mryTextView.setBackground(ShapeUtils.createStrokeAndFill(this.mContext.getResources().getColor(R.color.color_FFD8D8D8), (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(20.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                FcPageDetailActivity.this.getDetail();
            }

            public void onLoadMore(RefreshLayout refreshLayout) {
                FcPageDetailActivity.this.getMoreComments();
            }
        });
        this.btnReply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcPageDetailActivity.this.showReplyFcDialog();
            }
        });
        this.layoutManager = new LinearLayoutManager(this.mContext, 1, false);
        this.rvFcList.setLayoutManager(this.layoutManager);
        FcDetailAdapter fcDetailAdapter = new FcDetailAdapter(new ArrayList(), getParentActivity(), getClassGuid(), this);
        this.mAdapter = fcDetailAdapter;
        this.rvFcList.setAdapter(fcDetailAdapter);
        this.rvFcList.addOnScrollListener(this.rvScrollListener);
    }

    /* access modifiers changed from: private */
    public void showReplyFcDialog() {
        RespFcListBean respFcListBean = this.mRespFcDetailBean;
        if (respFcListBean != null) {
            FcUserInfoBean creatorUser = respFcListBean.getCreatorUser();
            String forumUserName = "";
            if (creatorUser != null) {
                forumUserName = StringUtils.handleTextName(ContactsController.formatName(creatorUser.getFirstName(), creatorUser.getLastName()), 12);
            }
            super.showReplyFcDialog(forumUserName, this.mRespFcDetailBean.getForumID(), this.mRespFcDetailBean.getCreateBy(), this.isShowAtUser, true, this.mRespFcDetailBean.isRecommend(), this.mRespFcDetailBean.getRequiredVipLevel());
            this.commentParentPosition = 0;
            this.commentChildPosition = -1;
        }
    }

    /* access modifiers changed from: protected */
    public void initData() {
        FcDetailAdapter fcDetailAdapter;
        this.mPresenter = new FcPageDetailPresenter(this);
        RespFcListBean respFcListBean = this.mRespFcDetailBean;
        if (!(respFcListBean == null || (fcDetailAdapter = this.mAdapter) == null)) {
            fcDetailAdapter.setFcContentData(respFcListBean);
            loadCommentData(this.mRespFcDetailBean.getComments());
        }
        getDetail();
        if (this.isShowReplyDialog && this.mRespFcDetailBean != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    FcPageDetailActivity.this.showReplyFcDialog();
                }
            }, 500);
        }
    }

    public void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().resume();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (FcPageDetailActivity.this.rvScrollListener != null) {
                    FcPageDetailActivity.this.rvScrollListener.onScrollStateChanged(FcPageDetailActivity.this.rvFcList, 0);
                }
            }
        }, 1000);
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
        VideoPlayerManager.getInstance().release();
        BaseFcContract.IFcPageDetailPresenter iFcPageDetailPresenter = this.mPresenter;
        if (iFcPageDetailPresenter != null) {
            iFcPageDetailPresenter.unSubscribeTask();
        }
        dismissFcDoReplyDialog();
    }

    public void getDetail() {
        long j = this.forumId;
        if (j != 0) {
            this.mPresenter.getDetail(j, this.userId);
        }
    }

    public void getDetailSucc(RespFcListBean data) {
        View viewByPosition;
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        if (data != null) {
            this.isShowAtUser = this.pageIndex != 2 && data.isRecommend();
            RespFcListBean respFcListBean = this.mRespFcDetailBean;
            if (respFcListBean == null || respFcListBean.getCreatorUser() == null || (TextUtils.isEmpty(this.mRespFcDetailBean.getContent()) && this.mRespFcDetailBean.getMedias() == null)) {
                this.mRespFcDetailBean = data;
                KLog.d("---------commentList.size()" + this.mRespFcDetailBean.getComments().size());
                this.mAdapter.setFcContentData(this.mRespFcDetailBean);
                loadCommentData(this.mRespFcDetailBean.getComments());
            } else {
                this.mRespFcDetailBean = data;
                KLog.d("---------commentList.size()" + this.mRespFcDetailBean.getComments().size());
                this.mAdapter.setFcContentData(this.mRespFcDetailBean);
                boolean hasThumb = this.mRespFcDetailBean.isHasThumb();
                int thumbUp = this.mRespFcDetailBean.getThumbUp();
                int commentCount = this.mRespFcDetailBean.getCommentCount();
                if (!(this.layoutManager == null || this.mAdapter.getItemCount() <= 0 || (viewByPosition = this.layoutManager.findViewByPosition(0)) == null)) {
                    MryTextView btnLike = (MryTextView) viewByPosition.findViewById(R.id.btn_like);
                    String str = "0";
                    if (btnLike != null) {
                        btnLike.setClickable(true);
                        btnLike.setText(thumbUp > 0 ? String.valueOf(thumbUp) : str);
                        btnLike.setSelected(hasThumb);
                    }
                    MryTextView btnReply2 = (MryTextView) viewByPosition.findViewById(R.id.btn_reply);
                    if (btnReply2 != null) {
                        if (commentCount > 0) {
                            str = String.valueOf(commentCount);
                        }
                        btnReply2.setText(str);
                    }
                }
            }
            this.commentPageNo = 0;
            getMoreComments();
            this.likeUserPageNo = 0;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    FcPageDetailActivity.this.getLikeUserList();
                }
            }, 800);
            return;
        }
        FcToastUtils.show((CharSequence) LocaleController.getString("CFGF1", R.string.CFGF1));
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                FcPageDetailActivity.this.finishFragment(true);
            }
        }, 1000);
    }

    public void getDetailFailed(String msg) {
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        FcToastUtils.show((CharSequence) LocaleController.getString("CFGF1", R.string.CFGF1));
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                FcPageDetailActivity.this.finishFragment(true);
            }
        }, 1000);
    }

    /* access modifiers changed from: private */
    public void getMoreComments() {
        RespFcListBean respFcListBean = this.mRespFcDetailBean;
        if (respFcListBean != null && respFcListBean.getForumID() != 0) {
            long commentId = 0;
            long forumUserId = 0;
            FcReplyBean fcReplyBean = this.commentPageNo == 0 ? null : this.mAdapter.getEndListId();
            if (fcReplyBean != null) {
                commentId = fcReplyBean.getCommentID();
                forumUserId = fcReplyBean.getCreateBy();
            }
            this.mPresenter.getComments(this.mRespFcDetailBean.getForumID(), commentId, forumUserId, 20);
        }
    }

    public void getCommentsSucc(ArrayList<FcReplyBean> data) {
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        if (data != null) {
            loadCommentData(data);
            this.commentPageNo++;
        }
    }

    private void loadCommentData(ArrayList<FcReplyBean> data) {
        if (this.commentPageNo != 0) {
            if (data == null || data.size() < 20) {
                data.add(new FcReplyBean());
                this.mSmartRefreshLayout.setEnableLoadMore(false);
            }
            this.mAdapter.loadMore(data);
        } else if (data == null || data.size() == 0) {
            this.mSmartRefreshLayout.setEnableLoadMore(false);
            ArrayList<FcReplyBean> temp = new ArrayList<>();
            temp.add(new FcReplyBean());
            if (data.size() < 20) {
                temp.add(new FcReplyBean());
            }
            this.mAdapter.refresh(temp);
        } else {
            if (data.size() < 20) {
                this.mSmartRefreshLayout.setEnableLoadMore(false);
            } else {
                this.mSmartRefreshLayout.setEnableLoadMore(true);
            }
            ArrayList<FcReplyBean> temp2 = new ArrayList<>();
            temp2.add(new FcReplyBean());
            temp2.addAll(data);
            if (data.size() < 20) {
                temp2.add(new FcReplyBean());
            }
            this.mAdapter.refresh(temp2);
        }
    }

    public void getCommentsFailed(String msg) {
        this.mSmartRefreshLayout.finishLoadMore();
        this.mSmartRefreshLayout.setEnableLoadMore(false);
        FcToastUtils.show((int) R.string.friendscircle_home_request_fail);
    }

    /* access modifiers changed from: private */
    public void getLikeUserList() {
        List<FcLikeBean> dataList;
        FcLikeBean fcLikeBean;
        RespFcListBean respFcListBean = this.mRespFcDetailBean;
        if (respFcListBean != null && respFcListBean.getThumbUp() > 0) {
            long thumbId = 0;
            int limit = 8;
            if (this.likeUserPageNo != 0) {
                limit = 32;
                FcDetailLikedUserAdapter fcLikedUserAdapter = this.mAdapter.getFcLikedUserAdapter();
                if (!(fcLikedUserAdapter == null || (dataList = fcLikedUserAdapter.getDataList()) == null || dataList.size() <= 0 || (fcLikeBean = dataList.get(dataList.size() - 1)) == null)) {
                    thumbId = fcLikeBean.getThumbID();
                }
            }
            this.mPresenter.getLikeUserList(this.mRespFcDetailBean.getForumID(), thumbId, limit);
        }
    }

    public void getLikeUserListSucc(RespFcLikesBean data) {
        if (data != null && data.getThumbs() != null && data.getUserInfo() != null && data.getThumbs().size() > 0 && data.getUserInfo().size() >= data.getThumbs().size()) {
            ArrayList<FcLikeBean> tempList = new ArrayList<>();
            Iterator<FcLikeBean> thumbsIterator = data.getThumbs().iterator();
            while (thumbsIterator.hasNext()) {
                FcLikeBean thumbNext = thumbsIterator.next();
                Iterator<FcUserInfoBean> it = data.getUserInfo().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    FcUserInfoBean userInfoNext = it.next();
                    if (thumbNext.getCreateBy() == ((long) userInfoNext.getUserId())) {
                        thumbNext.setCreator(userInfoNext);
                        tempList.add(thumbNext);
                        thumbsIterator.remove();
                        break;
                    }
                }
            }
            this.mAdapter.setFcLikeBeans(this.likeUserPageNo, tempList);
            this.likeUserPageNo++;
        }
    }

    public void getLikeUserListFiled(String msg) {
    }

    public void onReplyRefreshData() {
    }

    public void onReplyLoadMoreData(FcReplyBean parentFcReplyBean, int position) {
        if (parentFcReplyBean != null && position > 0) {
            getMoreReplyList(parentFcReplyBean, position);
        }
    }

    public void getMoreReplyList(FcReplyBean parentFcReplyBean, int position) {
        RespFcListBean respFcListBean = this.mRespFcDetailBean;
        if (respFcListBean != null) {
            long commentId = 0;
            if (respFcListBean.getForumID() != 0) {
                if (this.replyPageNo != 0) {
                    commentId = this.mChildReplyListAdapter.getEndListId();
                }
                this.mPresenter.getReplyList(parentFcReplyBean, position, commentId, 20);
            }
        }
    }

    public void getReplyListSucc(FcReplyBean parentFcReplyBean, int parentFcReplyPosition, ArrayList<FcReplyBean> data) {
        loadReplyData(parentFcReplyBean, parentFcReplyPosition, data);
        this.replyPageNo++;
    }

    private void loadReplyData(FcReplyBean parentFcReplyBean, int parentFcReplyPosition, ArrayList<FcReplyBean> data) {
        if (this.replyListDialog == null) {
            FcChildReplyListDialog fcChildReplyListDialog = new FcChildReplyListDialog(getParentActivity());
            this.replyListDialog = fcChildReplyListDialog;
            fcChildReplyListDialog.setListener(this);
            this.mChildReplyListAdapter = this.replyListDialog.getChildReplyListAdapter();
        }
        if (this.replyPageNo == 0) {
            this.replyListDialog.setParentFcReplyData(parentFcReplyBean, parentFcReplyPosition);
        }
        this.replyListDialog.loadData(data, this.replyPageNo);
        FcChildReplyListDialog fcChildReplyListDialog2 = this.replyListDialog;
        if (fcChildReplyListDialog2 != null && !fcChildReplyListDialog2.isShowing()) {
            this.replyListDialog.show();
        }
    }

    public void getReplyListFailed(FcReplyBean parentFcReplyBean, int parentFcReplyPosition, String msg) {
        if (this.replyPageNo != 0 || parentFcReplyBean == null) {
            loadReplyData(parentFcReplyBean, parentFcReplyPosition, (ArrayList<FcReplyBean>) null);
        } else {
            loadReplyData(parentFcReplyBean, parentFcReplyPosition, parentFcReplyBean.getSubComment());
        }
    }

    public void onAction(View view, int index, int position, Object object) {
        int i = index;
        Object obj = object;
        if (i == FcDetailAdapter.Index_click_avatar) {
            long userId2 = 0;
            FcUserInfoBean fcUserInfoBean = null;
            if (obj instanceof RespFcListBean) {
                RespFcListBean model = (RespFcListBean) obj;
                userId2 = model.getCreateBy();
                fcUserInfoBean = model.getCreatorUser();
            } else if (obj instanceof FcLikeBean) {
                FcLikeBean model2 = (FcLikeBean) obj;
                userId2 = model2.getCreateBy();
                fcUserInfoBean = model2.getCreator();
            } else if (obj instanceof FcUserInfoBean) {
                fcUserInfoBean = (FcUserInfoBean) obj;
                userId2 = (long) fcUserInfoBean.getUserId();
            }
            if (userId2 != 0) {
                if (userId2 == ((long) getUserConfig().getCurrentUser().id)) {
                    presentFragment(new FcPageMineActivity());
                } else if (fcUserInfoBean != null) {
                    presentFragment(new FcPageOthersActivity(fcUserInfoBean));
                }
            }
            int i2 = position;
            return;
        }
        if (i == FcDetailAdapter.Index_download_photo) {
            int i3 = position;
        } else if (i == FcDetailAdapter.Index_download_video) {
            int i4 = position;
        } else if (i == FcDetailAdapter.Index_click_forum_like) {
            if (obj instanceof RespFcListBean) {
                RespFcListBean model3 = (RespFcListBean) obj;
                if (model3.isHasThumb()) {
                    doCancelLikeFc(model3.getForumID(), model3.getCreateBy(), -1, -1, position);
                } else {
                    doLike(model3.getForumID(), model3.getCreateBy(), -1, -1, position);
                }
                int i5 = position;
                return;
            }
            int i6 = position;
            return;
        } else if (i == FcDetailAdapter.Index_click_comment_like) {
            if (obj instanceof FcReplyBean) {
                FcReplyBean model4 = (FcReplyBean) obj;
                if (model4.isHasThumb()) {
                    doCancelLikeFc(model4.getForumID(), model4.getForumUser(), model4.getCommentID(), model4.getCreateBy(), position);
                } else {
                    doLike(model4.getForumID(), this.mRespFcDetailBean.getCreateBy(), model4.getCommentID(), model4.getForumID(), position);
                }
                int i7 = position;
                return;
            }
            int i8 = position;
            return;
        } else if (i == FcDetailAdapter.Index_click_comment) {
            showReplyFcDialog();
            int i9 = position;
            return;
        } else if (i != FcDetailAdapter.Index_click_more_reply) {
            int i10 = position;
            if (i == FcDetailAdapter.Index_click_location) {
                if (obj instanceof RespFcListBean) {
                    RespFcListBean model5 = (RespFcListBean) obj;
                    if (!TextUtils.isEmpty(model5.getLocationName()) && model5.getLongitude() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && model5.getLatitude() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                        presentFragment(new FcLocationInfoActivity(new FcLocationInfoBean(model5.getLongitude(), model5.getLatitude(), model5.getLocationName(), model5.getLocationAddress(), model5.getLocationCity())));
                        return;
                    }
                    return;
                }
                return;
            } else if (i == FcDetailAdapter.Index_click_load_more_like && (obj instanceof FcLikeBean)) {
                getLikeUserList();
                return;
            } else {
                return;
            }
        } else if (obj instanceof FcReplyBean) {
            this.replyPageNo = 0;
            getMoreReplyList((FcReplyBean) obj, position);
            return;
        } else {
            int i11 = position;
            return;
        }
        if (obj instanceof String) {
            downloadFileToLocal((String) obj);
        }
    }

    public void onPresentFragment(BaseFragment baseFragment) {
        setStopPlayState();
        if (baseFragment != null) {
            presentFragment(baseFragment);
        }
    }

    public void onReplyClick(View v, String receiver, RespFcListBean model, int itemPosition, int replyPosition2, boolean isLongClick) {
        FcReplyBean fcReplyBean;
        FcReplyBean deleteReplyBean;
        int i = itemPosition;
        int i2 = replyPosition2;
        if (!isLongClick) {
            if (i < this.mAdapter.getItemCount()) {
                if (i2 != -1) {
                    FcReplyBean fcReplyBean2 = (FcReplyBean) this.mAdapter.get(i);
                    if (!(fcReplyBean2 == null || fcReplyBean2.getSubComment() == null || i2 >= fcReplyBean2.getSubComment().size())) {
                        this.commentItemModel = fcReplyBean2.getSubComment().get(i2);
                    }
                } else {
                    this.commentItemModel = (FcReplyBean) this.mAdapter.get(i);
                }
            }
            this.commentParentPosition = i;
            this.commentChildPosition = i2;
            RespFcListBean respFcListBean = this.mRespFcDetailBean;
            if (respFcListBean != null) {
                showReplyFcDialog(receiver, respFcListBean.getForumID(), this.mRespFcDetailBean.getCreateBy(), this.isShowAtUser, false, this.mRespFcDetailBean.isRecommend(), this.mRespFcDetailBean.getRequiredVipLevel());
            }
        } else if (i < this.mAdapter.getItemCount() && this.mAdapter.get(i) != null && (fcReplyBean = (FcReplyBean) this.mAdapter.get(i)) != null) {
            if (i2 == -1 || fcReplyBean.getSubComment() == null || i2 >= fcReplyBean.getSubComment().size()) {
                deleteReplyBean = fcReplyBean;
            } else {
                deleteReplyBean = fcReplyBean.getSubComment().get(i2);
            }
            showDeleteBottomSheet(deleteReplyBean, i, i2);
        }
    }

    public void onInputReplyContent(String content, ArrayList<FCEntitysRequest> atUserBeanList) {
        long supUser;
        long supID;
        long replayUID;
        long replayID;
        long supUser2;
        long supID2;
        long replayUID2;
        long replayID2;
        if (TextUtils.isEmpty(content)) {
            FcToastUtils.show((CharSequence) LocaleController.getString("fc_tips_input_empty_comment", R.string.fc_tips_input_empty_comment));
            return;
        }
        FcChildReplyListDialog fcChildReplyListDialog = this.replyListDialog;
        if (fcChildReplyListDialog == null || !fcChildReplyListDialog.isShowing()) {
            RespFcListBean respFcListBean = this.mRespFcDetailBean;
            if (respFcListBean != null) {
                long forumID = respFcListBean.getForumID();
                long forumUser = this.mRespFcDetailBean.getCreateBy();
                FcReplyBean fcReplyBean = this.commentItemModel;
                if (fcReplyBean == null || this.commentParentPosition <= 0) {
                    replayID = forumID;
                    replayUID = forumUser;
                    supID = 0;
                    supUser = 0;
                } else if (fcReplyBean.getReplayID() == forumID) {
                    long replayID3 = this.commentItemModel.getCommentID();
                    replayID = replayID3;
                    replayUID = this.commentItemModel.getCreateBy();
                    supID = this.commentItemModel.getCommentID();
                    supUser = this.commentItemModel.getCreateBy();
                } else {
                    long replayID4 = this.commentItemModel.getCommentID();
                    replayID = replayID4;
                    replayUID = this.commentItemModel.getCreateBy();
                    supID = this.commentItemModel.getSupID();
                    supUser = this.commentItemModel.getSupUser();
                }
                RequestReplyFcBean requestReplyFcBean = new RequestReplyFcBean(forumID, forumUser, replayID, replayUID, supID, supUser, content, this.mRespFcDetailBean.getRequiredVipLevel());
                requestReplyFcBean.setEntitys(atUserBeanList);
                doReplyFc(requestReplyFcBean, this.commentParentPosition);
                return;
            }
            return;
        }
        FcReplyBean fcReplyBean2 = this.replyItemModel;
        if (fcReplyBean2 != null) {
            long forumID2 = fcReplyBean2.getForumID();
            long forumUser2 = this.replyItemModel.getForumUser();
            if (this.replyItemModel.getReplayID() == forumID2) {
                long replayID5 = this.replyItemModel.getCommentID();
                replayID2 = replayID5;
                replayUID2 = this.replyItemModel.getCreateBy();
                supID2 = this.replyItemModel.getCommentID();
                supUser2 = this.replyItemModel.getCreateBy();
            } else {
                long replayID6 = this.replyItemModel.getCommentID();
                replayID2 = replayID6;
                replayUID2 = this.replyItemModel.getCreateBy();
                supID2 = this.replyItemModel.getSupID();
                supUser2 = this.replyItemModel.getSupUser();
            }
            doReplyFc(new RequestReplyFcBean(forumID2, forumUser2, replayID2, replayUID2, supID2, supUser2, content, this.mRespFcDetailBean.getRequiredVipLevel()), this.replyPosition);
            ArrayList<FCEntitysRequest> arrayList = atUserBeanList;
        }
    }

    public void onChildReplyListAction(View view, int index, int position, Object object) {
        int i = index;
        Object obj = object;
        if (i == FcDetailAdapter.Index_child_reply_click_avatar) {
            if (obj instanceof FcReplyBean) {
                final FcReplyBean model = (FcReplyBean) obj;
                FcChildReplyListDialog fcChildReplyListDialog = this.replyListDialog;
                if (fcChildReplyListDialog != null && fcChildReplyListDialog.isShowing()) {
                    this.replyListDialog.dismiss();
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (model.getCreateBy() == ((long) FcPageDetailActivity.this.getUserConfig().getCurrentUser().id)) {
                            FcPageDetailActivity.this.onPresentFragment(new FcPageMineActivity());
                        } else {
                            FcPageDetailActivity.this.onPresentFragment(new FcPageOthersActivity(model.getCreator()));
                        }
                    }
                }, 500);
            }
        } else if (i == FcDetailAdapter.Index_child_reply_click_like && (obj instanceof FcReplyBean)) {
            FcReplyBean model2 = (FcReplyBean) obj;
            if (model2.isHasThumb()) {
                doCancelLikeFc(model2.getForumID(), model2.getForumUser(), model2.getCommentID(), model2.getCreateBy(), position);
                return;
            }
            doLike(model2.getForumID(), model2.getForumUser(), model2.getCommentID(), model2.getForumID(), position);
        }
    }

    public void onChildReplyClick(View v, String receiver, FcReplyBean model, int parentPosition, int itemPosition, boolean isLongClick) {
        FcReplyBean fcReplyBean = model;
        int i = parentPosition;
        int i2 = itemPosition;
        if (this.mChildReplyListAdapter == null) {
            return;
        }
        if (!isLongClick) {
            this.replyItemModel = fcReplyBean;
            this.replyCommentPosition = i;
            this.replyPosition = i2;
            RespFcListBean respFcListBean = this.mRespFcDetailBean;
            if (respFcListBean != null) {
                showReplyFcDialog(receiver, respFcListBean.getForumID(), this.mRespFcDetailBean.getCreateBy(), this.isShowAtUser, false, this.mRespFcDetailBean.isRecommend(), this.mRespFcDetailBean.getRequiredVipLevel());
            }
        } else if (fcReplyBean != null) {
            showDeleteBottomSheet(fcReplyBean, i, i2);
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

    /* JADX WARNING: type inference failed for: r1v19, types: [android.view.View] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doLikeAfterViewChange(int r8, boolean r9, com.bjz.comm.net.bean.FcLikeBean r10) {
        /*
            r7 = this;
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcChildReplyListDialog r0 = r7.replyListDialog
            r1 = 2131296429(0x7f0900ad, float:1.8210774E38)
            java.lang.String r2 = "0"
            r3 = 1
            if (r0 == 0) goto L_0x007a
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x007a
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDialogChildReplyAdapter r0 = r7.mChildReplyListAdapter
            int r0 = r0.getParentFcReplyPosition()
            if (r8 != 0) goto L_0x0073
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r4 = r7.mAdapter
            int r4 = r4.getItemCount()
            if (r0 >= r4) goto L_0x0073
            androidx.recyclerview.widget.RecyclerView$LayoutManager r4 = r7.layoutManager
            android.view.View r4 = r4.findViewByPosition(r0)
            r5 = 0
            if (r4 == 0) goto L_0x0073
            android.view.View r1 = r4.findViewById(r1)
            im.bclpbkiauv.ui.hviews.MryTextView r1 = (im.bclpbkiauv.ui.hviews.MryTextView) r1
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r5 = r7.mAdapter
            java.lang.Object r5 = r5.get(r0)
            com.bjz.comm.net.bean.FcReplyBean r5 = (com.bjz.comm.net.bean.FcReplyBean) r5
            r5.setHasThumb(r9)
            if (r9 == 0) goto L_0x004d
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r6 = r7.mAdapter
            java.lang.Object r6 = r6.get(r0)
            com.bjz.comm.net.bean.FcReplyBean r6 = (com.bjz.comm.net.bean.FcReplyBean) r6
            int r6 = r6.getThumbUp()
            int r6 = r6 + r3
            r5.setThumbUp(r6)
            goto L_0x005d
        L_0x004d:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r6 = r7.mAdapter
            java.lang.Object r6 = r6.get(r0)
            com.bjz.comm.net.bean.FcReplyBean r6 = (com.bjz.comm.net.bean.FcReplyBean) r6
            int r6 = r6.getThumbUp()
            int r6 = r6 - r3
            r5.setThumbUp(r6)
        L_0x005d:
            if (r1 == 0) goto L_0x0073
            int r3 = r5.getThumbUp()
            if (r3 <= 0) goto L_0x006d
            int r2 = r5.getThumbUp()
            java.lang.String r2 = java.lang.String.valueOf(r2)
        L_0x006d:
            r1.setText(r2)
            r1.setSelected(r9)
        L_0x0073:
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcChildReplyListDialog r1 = r7.replyListDialog
            r1.doLike(r8, r9, r10)
            goto L_0x014f
        L_0x007a:
            androidx.recyclerview.widget.RecyclerView$LayoutManager r0 = r7.layoutManager
            android.view.View r0 = r0.findViewByPosition(r8)
            r4 = 0
            if (r0 == 0) goto L_0x008f
            android.view.View r1 = r0.findViewById(r1)
            r4 = r1
            im.bclpbkiauv.ui.hviews.MryTextView r4 = (im.bclpbkiauv.ui.hviews.MryTextView) r4
            if (r4 == 0) goto L_0x008f
            r4.setClickable(r3)
        L_0x008f:
            if (r10 == 0) goto L_0x014f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "------position"
            r1.append(r5)
            r1.append(r8)
            java.lang.String r5 = "  "
            r1.append(r5)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            com.socks.library.KLog.d(r1)
            if (r8 != 0) goto L_0x010b
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r1 = r7.mAdapter
            com.bjz.comm.net.bean.RespFcListBean r1 = r1.getFcContentBean()
            r1.setHasThumb(r9)
            if (r9 == 0) goto L_0x00cf
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r1 = r7.mAdapter
            com.bjz.comm.net.bean.RespFcListBean r1 = r1.getFcContentBean()
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r5 = r7.mAdapter
            com.bjz.comm.net.bean.RespFcListBean r5 = r5.getFcContentBean()
            int r5 = r5.getThumbUp()
            int r5 = r5 + r3
            r1.setThumbUp(r5)
            goto L_0x00e3
        L_0x00cf:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r1 = r7.mAdapter
            com.bjz.comm.net.bean.RespFcListBean r1 = r1.getFcContentBean()
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r5 = r7.mAdapter
            com.bjz.comm.net.bean.RespFcListBean r5 = r5.getFcContentBean()
            int r5 = r5.getThumbUp()
            int r5 = r5 - r3
            r1.setThumbUp(r5)
        L_0x00e3:
            if (r4 == 0) goto L_0x0105
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r1 = r7.mAdapter
            com.bjz.comm.net.bean.RespFcListBean r1 = r1.getFcContentBean()
            int r1 = r1.getThumbUp()
            if (r1 <= 0) goto L_0x00ff
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r1 = r7.mAdapter
            com.bjz.comm.net.bean.RespFcListBean r1 = r1.getFcContentBean()
            int r1 = r1.getThumbUp()
            java.lang.String r2 = java.lang.String.valueOf(r1)
        L_0x00ff:
            r4.setText(r2)
            r4.setSelected(r9)
        L_0x0105:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r1 = r7.mAdapter
            r1.doLikeUserChanged(r10, r9)
            goto L_0x014f
        L_0x010b:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r1 = r7.mAdapter
            java.lang.Object r1 = r1.get(r8)
            com.bjz.comm.net.bean.FcReplyBean r1 = (com.bjz.comm.net.bean.FcReplyBean) r1
            r1.setHasThumb(r9)
            if (r9 == 0) goto L_0x0129
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r5 = r7.mAdapter
            java.lang.Object r5 = r5.get(r8)
            com.bjz.comm.net.bean.FcReplyBean r5 = (com.bjz.comm.net.bean.FcReplyBean) r5
            int r5 = r5.getThumbUp()
            int r5 = r5 + r3
            r1.setThumbUp(r5)
            goto L_0x0139
        L_0x0129:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter r5 = r7.mAdapter
            java.lang.Object r5 = r5.get(r8)
            com.bjz.comm.net.bean.FcReplyBean r5 = (com.bjz.comm.net.bean.FcReplyBean) r5
            int r5 = r5.getThumbUp()
            int r5 = r5 - r3
            r1.setThumbUp(r5)
        L_0x0139:
            if (r4 == 0) goto L_0x014f
            int r3 = r1.getThumbUp()
            if (r3 <= 0) goto L_0x0149
            int r2 = r1.getThumbUp()
            java.lang.String r2 = java.lang.String.valueOf(r2)
        L_0x0149:
            r4.setText(r2)
            r4.setSelected(r9)
        L_0x014f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageDetailActivity.doLikeAfterViewChange(int, boolean, com.bjz.comm.net.bean.FcLikeBean):void");
    }

    /* access modifiers changed from: protected */
    public void doReplySuccAfterViewChange(FcReplyBean data, int replyParentPosition) {
        MryTextView btnReply2;
        MryTextView btnReply3;
        FcChildReplyListDialog fcChildReplyListDialog = this.replyListDialog;
        String str = "0";
        if (fcChildReplyListDialog == null || !fcChildReplyListDialog.isShowing()) {
            RespFcListBean fcContentBean = this.mAdapter.getFcContentBean();
            if (fcContentBean != null) {
                fcContentBean.setCommentCount(fcContentBean.getCommentCount() + 1);
                this.mAdapter.setFcContentData(fcContentBean);
                View viewByPosition = this.layoutManager.findViewByPosition(0);
                if (!(viewByPosition == null || (btnReply2 = (MryTextView) viewByPosition.findViewById(R.id.btn_reply)) == null)) {
                    if (fcContentBean.getCommentCount() > 0) {
                        str = String.valueOf(fcContentBean.getCommentCount());
                    }
                    btnReply2.setText(str);
                }
            }
            if (replyParentPosition != 0 || this.mAdapter.getFcContentBean() == null) {
                FcReplyBean fcReplyBean = (FcReplyBean) this.mAdapter.get(replyParentPosition);
                if (fcReplyBean != null && data != null) {
                    fcReplyBean.setSubComments(fcReplyBean.getSubComments() + 1);
                    ArrayList<FcReplyBean> comments = fcReplyBean.getSubComment();
                    if (comments == null || comments.size() == 0) {
                        comments = new ArrayList<>();
                    }
                    comments.add(data);
                    fcReplyBean.setSubComment(comments);
                    this.mAdapter.notifyItemChanged(replyParentPosition);
                }
            } else if (this.mAdapter.getItemCount() < 2 || this.mAdapter.getFooterSize() == 0) {
                ArrayList<FcReplyBean> moreList = new ArrayList<>();
                moreList.add(data);
                this.mAdapter.loadMore(moreList);
            } else {
                this.mAdapter.getDataList().add(this.mAdapter.getItemCount() - 1, data);
                FcDetailAdapter fcDetailAdapter = this.mAdapter;
                fcDetailAdapter.notifyItemInserted(fcDetailAdapter.getItemCount() - 1);
                FcDetailAdapter fcDetailAdapter2 = this.mAdapter;
                fcDetailAdapter2.notifyItemRangeChanged(fcDetailAdapter2.getItemCount() - 1, this.mAdapter.getFooterSize());
            }
        } else {
            RespFcListBean fcContentBean2 = this.mAdapter.getFcContentBean();
            if (fcContentBean2 != null) {
                fcContentBean2.setCommentCount(fcContentBean2.getCommentCount() + 1);
                this.mAdapter.setFcContentData(fcContentBean2);
                View viewByPosition2 = this.layoutManager.findViewByPosition(0);
                if (!(viewByPosition2 == null || (btnReply3 = (MryTextView) viewByPosition2.findViewById(R.id.btn_reply)) == null)) {
                    if (fcContentBean2.getCommentCount() > 0) {
                        str = String.valueOf(fcContentBean2.getCommentCount());
                    }
                    btnReply3.setText(str);
                }
            }
            this.replyListDialog.doReply(data);
            FcChildReplyListDialog fcChildReplyListDialog2 = this.replyListDialog;
            if (fcChildReplyListDialog2 != null) {
                List<FcReplyBean> dataList = fcChildReplyListDialog2.getRealDataList();
                int commentPosition = this.replyCommentPosition;
                FcReplyBean fcReplyBean2 = (FcReplyBean) this.mAdapter.get(commentPosition);
                if (dataList != null && fcReplyBean2 != null && fcReplyBean2.getSubComment() != null) {
                    ((FcReplyBean) this.mAdapter.get(commentPosition)).getSubComment().clear();
                    ((FcReplyBean) this.mAdapter.get(commentPosition)).getSubComment().addAll(dataList);
                    ((FcReplyBean) this.mAdapter.get(commentPosition)).setSubComments(dataList.size());
                    this.mAdapter.notifyItemChanged(commentPosition);
                }
            }
        }
    }

    public void doDeleteReplySuccAfterViewChange(long forumId2, long commentId, int parentPosition, int childPosition) {
        MryTextView btnReply2;
        MryTextView btnReply3;
        FcChildReplyListDialog fcChildReplyListDialog = this.replyListDialog;
        String str = "0";
        if (fcChildReplyListDialog == null || !fcChildReplyListDialog.isShowing()) {
            RespFcListBean fcContentBean = this.mAdapter.getFcContentBean();
            FcReplyBean fcReplyBean = (FcReplyBean) this.mAdapter.get(parentPosition);
            if (fcReplyBean != null) {
                if (childPosition == -1) {
                    fcContentBean.setCommentCount((fcContentBean.getCommentCount() - ((FcReplyBean) this.mAdapter.get(parentPosition)).getSubComments()) - 1);
                    this.mAdapter.getDataList().remove(parentPosition);
                    this.mAdapter.notifyItemRemoved(parentPosition);
                    FcDetailAdapter fcDetailAdapter = this.mAdapter;
                    fcDetailAdapter.notifyItemRangeChanged(parentPosition, fcDetailAdapter.getItemCount() - parentPosition);
                } else {
                    fcContentBean.setCommentCount(fcContentBean.getCommentCount() - 1);
                    fcReplyBean.setSubComments(fcReplyBean.getSubComments() - 1);
                    fcReplyBean.getSubComment().remove(childPosition);
                    this.mAdapter.notifyItemChanged(parentPosition);
                }
            }
            if (fcContentBean != null) {
                this.mAdapter.setFcContentData(fcContentBean);
                View viewByPosition = this.layoutManager.findViewByPosition(0);
                if (viewByPosition != null && (btnReply2 = (MryTextView) viewByPosition.findViewById(R.id.btn_reply)) != null) {
                    if (fcContentBean.getCommentCount() > 0) {
                        str = String.valueOf(fcContentBean.getCommentCount());
                    }
                    btnReply2.setText(str);
                    return;
                }
                return;
            }
            return;
        }
        RespFcListBean fcContentBean2 = this.mAdapter.getFcContentBean();
        if (fcContentBean2 != null) {
            fcContentBean2.setCommentCount(fcContentBean2.getCommentCount() - 1);
            this.mAdapter.setFcContentData(fcContentBean2);
            View viewByPosition2 = this.layoutManager.findViewByPosition(0);
            if (!(viewByPosition2 == null || (btnReply3 = (MryTextView) viewByPosition2.findViewById(R.id.btn_reply)) == null)) {
                if (fcContentBean2.getCommentCount() > 0) {
                    str = String.valueOf(fcContentBean2.getCommentCount());
                }
                btnReply3.setText(str);
            }
        }
        this.replyListDialog.doDeleteReply(childPosition);
        FcChildReplyListDialog fcChildReplyListDialog2 = this.replyListDialog;
        if (fcChildReplyListDialog2 != null) {
            List<FcReplyBean> dataList = fcChildReplyListDialog2.getRealDataList();
            int commentPosition = parentPosition;
            FcReplyBean fcReplyBean2 = (FcReplyBean) this.mAdapter.get(commentPosition);
            if (dataList != null && fcReplyBean2 != null && fcReplyBean2.getSubComment() != null) {
                ((FcReplyBean) this.mAdapter.get(commentPosition)).getSubComment().clear();
                ((FcReplyBean) this.mAdapter.get(commentPosition)).getSubComment().addAll(dataList);
                ((FcReplyBean) this.mAdapter.get(commentPosition)).setSubComments(dataList.size());
                this.mAdapter.notifyItemChanged(commentPosition);
            }
        }
    }

    public void onError(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail) : msg);
    }
}
