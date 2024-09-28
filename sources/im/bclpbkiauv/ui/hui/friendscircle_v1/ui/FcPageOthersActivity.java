package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.AvatarPhotoBean;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.bean.RespOthersFcListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcPageOthersPresenter;
import com.bjz.comm.net.utils.HttpUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import im.bclpbkiauv.javaBean.fc.FcLocationInfoBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.ShapeUtils;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.dialogs.FcDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeItemReplyAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcListActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.decoration.SpacesItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.state.ScreenViewState;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.AutoPlayTool;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.FcDialogUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FcPageOthersActivity extends CommFcListActivity implements NotificationCenter.NotificationCenterDelegate, FcItemActionClickListener, BaseFcContract.IFcPageOthersView {
    private String TAG = FcPageOthersActivity.class.getSimpleName();
    private long accessHash;
    /* access modifiers changed from: private */
    public AutoPlayTool autoPlayTool;
    /* access modifiers changed from: private */
    public MryTextView btnFollow;
    private ImageView ivFcBg;
    /* access modifiers changed from: private */
    public ImageView ivFcOperate;
    /* access modifiers changed from: private */
    public BackupImageView ivUserAvatar;
    private UserFcListAdapter mAdapter;
    private FcPageOthersPresenter mPresenter;
    /* access modifiers changed from: private */
    public SmartRefreshLayout mSmartRefreshLayout;
    private ArrayList<RespFcListBean> mTempFcList = new ArrayList<>();
    /* access modifiers changed from: private */
    public int pageNo = 0;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            TLRPC.User user;
            if (fileLocation == null || FcPageOthersActivity.this.isFinishing()) {
                return null;
            }
            TLRPC.FileLocation photoBig = null;
            if (!(FcPageOthersActivity.this.userId == 0 || (user = MessagesController.getInstance(FcPageOthersActivity.this.currentAccount).getUser(Integer.valueOf(FcPageOthersActivity.this.userId))) == null || user.photo == null || user.photo.photo_big == null)) {
                photoBig = user.photo.photo_big;
            }
            if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                return null;
            }
            int[] coords = new int[2];
            FcPageOthersActivity.this.ivUserAvatar.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            object.viewX = coords[0];
            object.viewY = coords[1];
            object.parentView = FcPageOthersActivity.this.ivUserAvatar;
            object.imageReceiver = FcPageOthersActivity.this.ivUserAvatar.getImageReceiver();
            if (FcPageOthersActivity.this.userId != 0) {
                object.dialogId = FcPageOthersActivity.this.userId;
            }
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.size = -1;
            object.radius = FcPageOthersActivity.this.ivUserAvatar.getImageReceiver().getRoundRadius();
            object.scale = FcPageOthersActivity.this.ivUserAvatar.getScaleX();
            return object;
        }

        public void willHidePhotoViewer() {
            FcPageOthersActivity.this.ivUserAvatar.getImageReceiver().setVisible(true, true);
        }
    };
    private int replyChildPosition = -1;
    private RespFcListBean replyItemModel;
    private int replyParentPosition = -1;
    private RelativeLayout rlEmptyView;
    private RelativeLayout rlIgnoreView;
    private int roundNum = 1;
    /* access modifiers changed from: private */
    public RecyclerView rvFcList;
    RecyclerView.OnScrollListener rvScrollListener = new RecyclerView.OnScrollListener() {
        boolean isScroll = false;

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (FcPageOthersActivity.this.autoPlayTool != null && this.isScroll) {
                FcPageOthersActivity.this.autoPlayTool.onScrolledAndDeactivate();
            }
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            this.isScroll = newState != 0;
            if (newState != 0) {
                return;
            }
            if (FcPageOthersActivity.this.mSmartRefreshLayout.getState() == RefreshState.None || FcPageOthersActivity.this.mSmartRefreshLayout.getState() == RefreshState.RefreshFinish) {
                FcPageOthersActivity.this.isActivePlayer(recyclerView);
            }
        }
    };
    private MryTextView tvFansNum;
    private MryTextView tvFollowedUserNum;
    private MryTextView tvGender;
    private MryTextView tvLikeNum;
    private MryTextView tvPublishFcNum;
    private MryTextView tvUserName;
    /* access modifiers changed from: private */
    public int userId = 0;

    public FcPageOthersActivity(FcUserInfoBean fcUserInfoBean) {
        if (fcUserInfoBean != null) {
            this.userId = fcUserInfoBean.getUserId();
            this.accessHash = fcUserInfoBean.getAccessHash();
        }
    }

    public FcPageOthersActivity(int userId2) {
        this.userId = userId2;
    }

    public FcPageOthersActivity(int userId2, long accessHash2) {
        this.userId = userId2;
        this.accessHash = accessHash2;
    }

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_fc_page_others;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        VideoPlayerManager.getInstance().setVolume(0);
        this.actionBar.setVisibility(8);
        this.mSmartRefreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.smartRefreshLayout);
        CollapsingToolbarLayout ctlTitle = (CollapsingToolbarLayout) this.fragmentView.findViewById(R.id.ctl_title);
        Toolbar toolbar = (Toolbar) this.fragmentView.findViewById(R.id.toolbar);
        ActionBar actionBar = this.actionBar;
        int actionBarHeight = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = actionBarHeight;
        toolbar.setLayoutParams(layoutParams);
        ctlTitle.setMinimumHeight(actionBarHeight);
        ctlTitle.setContentScrimColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.fragmentView.findViewById(R.id.rl_header).setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ImageView icBack = (ImageView) this.fragmentView.findViewById(R.id.ic_back);
        MryTextView tvTitle = (MryTextView) this.fragmentView.findViewById(R.id.tv_title);
        tvTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.ivFcOperate = (ImageView) this.fragmentView.findViewById(R.id.iv_fc_operate);
        this.ivFcBg = (ImageView) this.fragmentView.findViewById(R.id.iv_fc_bg);
        BackupImageView backupImageView = (BackupImageView) this.fragmentView.findViewById(R.id.iv_user_avatar);
        this.ivUserAvatar = backupImageView;
        backupImageView.setBackground(ShapeUtils.create(this.mContext.getResources().getColor(R.color.color_FFE8E8E8), (float) AndroidUtilities.dp(8.0f)));
        this.ivUserAvatar.setRoundRadius(AndroidUtilities.dp(8.0f));
        this.tvUserName = (MryTextView) this.fragmentView.findViewById(R.id.tv_user_name);
        this.tvGender = (MryTextView) this.fragmentView.findViewById(R.id.tv_gender);
        this.btnFollow = (MryTextView) this.fragmentView.findViewById(R.id.btn_follow);
        this.tvPublishFcNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_publish_fc_num);
        this.tvFollowedUserNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_followed_user_num);
        this.tvLikeNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_like_num);
        this.tvFansNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_fans_num);
        this.tvPublishFcNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.tvFollowedUserNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.tvLikeNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.tvFansNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.rvFcList = (RecyclerView) this.fragmentView.findViewById(R.id.rv_fc_list);
        this.rlEmptyView = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_empty);
        this.rlIgnoreView = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_ignore);
        final CollapsingToolbarLayout collapsingToolbarLayout = ctlTitle;
        final ImageView imageView = icBack;
        final MryTextView mryTextView = tvTitle;
        AnonymousClass1 r7 = r0;
        final boolean isLight = Theme.getCurrentTheme().isLight();
        AnonymousClass1 r0 = new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) < collapsingToolbarLayout.getScrimVisibleHeightTrigger()) {
                    imageView.setImageResource(R.mipmap.ic_fc_back_white);
                    FcPageOthersActivity.this.ivFcOperate.setImageResource(R.mipmap.ic_fc_user_operate_white);
                    mryTextView.setAlpha(0.0f);
                    return;
                }
                if (isLight) {
                    imageView.setImageResource(R.mipmap.ic_fc_back_black);
                } else {
                    imageView.setImageResource(R.mipmap.ic_fc_back_white);
                }
                FcPageOthersActivity.this.ivFcOperate.setImageResource(R.mipmap.ic_fc_user_operate_black);
                mryTextView.setAlpha(1.0f);
            }
        };
        ((AppBarLayout) this.fragmentView.findViewById(R.id.mAppbarLayout)).addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) r7);
        icBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcPageOthersActivity.this.finishFragment();
            }
        });
        this.ivUserAvatar.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcPageOthersActivity.this.lambda$initView$0$FcPageOthersActivity(view);
            }
        });
        ((MryTextView) this.fragmentView.findViewById(R.id.btn_chat)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FcPageOthersActivity.this.userId > 0) {
                    Bundle args1 = new Bundle();
                    args1.putInt("user_id", FcPageOthersActivity.this.userId);
                    FcPageOthersActivity.this.presentFragment(new ChatActivity(args1));
                }
            }
        });
        this.btnFollow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FcPageOthersActivity.this.userId <= 0) {
                    return;
                }
                if (FcPageOthersActivity.this.btnFollow.isSelected()) {
                    FcPageOthersActivity fcPageOthersActivity = FcPageOthersActivity.this;
                    fcPageOthersActivity.doCancelFollowed((long) fcPageOthersActivity.userId);
                    return;
                }
                FcPageOthersActivity fcPageOthersActivity2 = FcPageOthersActivity.this;
                fcPageOthersActivity2.doFollow((long) fcPageOthersActivity2.userId);
            }
        });
        this.mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                FcPageOthersActivity.this.loadFcBaseInfo();
                int unused = FcPageOthersActivity.this.pageNo = 0;
                FcPageOthersActivity.this.getFcPageList();
            }

            public void onLoadMore(RefreshLayout refreshLayout) {
                FcPageOthersActivity.this.getFcPageList();
            }
        });
        this.mSmartRefreshLayout.setEnableLoadMore(false);
        this.layoutManager = new LinearLayoutManager(this.mContext, 1, false);
        this.rvFcList.setLayoutManager(this.layoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(AndroidUtilities.dp(7.0f));
        decoration.isShowTop(true);
        this.rvFcList.addItemDecoration(decoration);
        UserFcListAdapter userFcListAdapter = new UserFcListAdapter(new ArrayList(), getParentActivity(), getClassGuid(), this);
        this.mAdapter = userFcListAdapter;
        userFcListAdapter.setFooterCount(1);
        this.rvFcList.setAdapter(this.mAdapter);
        this.rvFcList.addOnScrollListener(this.rvScrollListener);
    }

    public /* synthetic */ void lambda$initView$0$FcPageOthersActivity(View v) {
        TLRPC.User user1;
        if (this.userId > 0 && (user1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.userId))) != null && user1.photo != null && user1.photo.photo_big != null) {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            if (user1.photo.dc_id != 0) {
                user1.photo.photo_big.dc_id = user1.photo.dc_id;
            }
            PhotoViewer.getInstance().openPhoto(user1.photo.photo_big, this.provider);
        }
    }

    public void onResume() {
        super.onResume();
        getParentActivity().getWindow().setSoftInputMode(16);
        VideoPlayerManager.getInstance().resume();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (FcPageOthersActivity.this.rvScrollListener != null) {
                    FcPageOthersActivity.this.rvScrollListener.onScrollStateChanged(FcPageOthersActivity.this.rvFcList, 0);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcFollowStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcPermissionStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcLikeStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcReplyItem);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fcDeleteReplyItem);
        VideoPlayerManager.getInstance().release();
        FcPageOthersPresenter fcPageOthersPresenter = this.mPresenter;
        if (fcPageOthersPresenter != null) {
            fcPageOthersPresenter.unSubscribeTask();
        }
    }

    /* access modifiers changed from: protected */
    public void initData() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcFollowStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcPermissionStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcLikeStatusUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcReplyItem);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fcDeleteReplyItem);
        this.mPresenter = new FcPageOthersPresenter(this);
        loadUserInfo();
        loadFcBaseInfo();
        getFcPageList();
    }

    private void loadUserInfo() {
        TLRPC.User itemUser;
        if (this.userId > 0 && (itemUser = getAccountInstance().getMessagesController().getUser(Integer.valueOf(this.userId))) != null) {
            this.ivUserAvatar.setImage(ImageLocation.getForUser(itemUser, false), "60_60", (Drawable) new AvatarDrawable(itemUser, true), (Object) itemUser);
            this.tvUserName.setText(StringUtils.handleTextName(ContactsController.formatName(itemUser.first_name, itemUser.last_name), 12));
        }
    }

    /* access modifiers changed from: private */
    public void loadFcBaseInfo() {
        int i = this.userId;
        if (i != -1) {
            this.mPresenter.getActionCount((long) i);
            this.mPresenter.checkIsFollowed((long) this.userId);
        }
    }

    public void checkIsFollowedSucc(Boolean isFollowed) {
        if (isFollowed != null) {
            this.btnFollow.setSelected(isFollowed.booleanValue());
            this.btnFollow.setText(isFollowed.booleanValue() ? "已关注" : "关注");
        }
    }

    public void getActionCountSucc(RespFcUserStatisticsBean data) {
        if (data != null) {
            this.tvPublishFcNum.setText(Integer.toString(data.getForumCount()));
            this.tvFollowedUserNum.setText(Integer.toString(data.getFollowCount()));
            this.tvLikeNum.setText(Integer.toString(data.getThumbCount()));
            this.tvFansNum.setText(Integer.toString(data.getFansCount()));
            if (!TextUtils.isEmpty(data.getHomeBackground()) && this.ivFcBg != null) {
                GlideUtils instance = GlideUtils.getInstance();
                instance.load(HttpUtils.getInstance().getDownloadFileUrl() + data.getHomeBackground(), this.mContext, this.ivFcBg, (int) R.drawable.shape_fc_default_pic_bg);
            }
            FcUserInfoBean fcUserInfoBean = data.getUser();
            if (fcUserInfoBean != null) {
                if (this.tvGender != null) {
                    if (fcUserInfoBean.getSex() != 0) {
                        MryTextView mryTextView = this.tvGender;
                        boolean z = true;
                        if (fcUserInfoBean.getSex() != 1) {
                            z = false;
                        }
                        mryTextView.setSelected(z);
                        String str = "";
                        if (fcUserInfoBean.getBirthday() > 0) {
                            int ageByBirthday = TimeUtils.getAgeByBirthday(new Date(((long) fcUserInfoBean.getBirthday()) * 1000));
                            MryTextView mryTextView2 = this.tvGender;
                            if (ageByBirthday > 0) {
                                str = String.valueOf(ageByBirthday);
                            }
                            mryTextView2.setText(str);
                            this.tvGender.setCompoundDrawablePadding(ageByBirthday > 0 ? AndroidUtilities.dp(2.0f) : 0);
                        } else {
                            this.tvGender.setText(str);
                            this.tvGender.setCompoundDrawablePadding(0);
                        }
                        this.tvGender.setVisibility(0);
                    } else {
                        this.tvGender.setVisibility(8);
                    }
                }
                AvatarPhotoBean avatarPhotoBean = fcUserInfoBean.getPhoto();
                if (avatarPhotoBean != null) {
                    int photoSize = avatarPhotoBean.getSmallPhotoSize();
                    int localId = avatarPhotoBean.getSmallLocalId();
                    long volumeId = avatarPhotoBean.getSmallVolumeId();
                    if (!(photoSize == 0 || volumeId == 0 || avatarPhotoBean.getAccess_hash() == 0)) {
                        TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
                        inputPeer.user_id = fcUserInfoBean.getUserId();
                        inputPeer.access_hash = fcUserInfoBean.getAccessHash();
                        ImageLocation imageLocation = new ImageLocation();
                        imageLocation.dc_id = 2;
                        imageLocation.photoPeer = inputPeer;
                        imageLocation.location = new TLRPC.TL_fileLocationToBeDeprecated();
                        imageLocation.location.local_id = localId;
                        imageLocation.location.volume_id = volumeId;
                        this.ivUserAvatar.setImage(imageLocation, "40_40", (Drawable) new AvatarDrawable(), (Object) inputPeer);
                    }
                }
                this.tvUserName.setText(StringUtils.handleTextName(ContactsController.formatName(fcUserInfoBean.getFirstName(), fcUserInfoBean.getLastName()), 12));
            }
        }
    }

    /* access modifiers changed from: private */
    public void getFcPageList() {
        if (this.userId > 0) {
            if (this.pageNo == 0) {
                this.mTempFcList.clear();
                this.roundNum = 1;
            }
            this.mPresenter.getFCList(10, this.pageNo == 0 ? 0 : this.mAdapter.getEndListId(), (long) this.userId, this.roundNum);
        }
    }

    public void getFCListSucc(String code, RespOthersFcListBean response) {
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        if (TextUtils.equals(code, "SUC_FORUM_OTHER_MAIN_IGNORE")) {
            refreshPageState(true);
        } else {
            formatFcListData(response);
        }
    }

    private void formatFcListData(RespOthersFcListBean response) {
        if (response != null) {
            ArrayList<RespFcListBean> forums = response.getForums();
            boolean finish = response.isFinish();
            if (forums == null || forums.size() <= 0) {
                int startIndex = 0;
                int adapterCount = 0;
                if (this.pageNo != 0) {
                    adapterCount = this.mAdapter.getItemCount() - this.mAdapter.getFooterSize();
                }
                if (this.pageNo != 0) {
                    startIndex = adapterCount;
                }
                ArrayList<RespFcListBean> mLoadFcList = new ArrayList<>();
                for (int i = startIndex; i < this.mTempFcList.size(); i++) {
                    mLoadFcList.add(this.mTempFcList.get(i));
                }
                setData(mLoadFcList);
                return;
            }
            this.mTempFcList.addAll(forums);
            int adapterCount2 = 0;
            if (this.pageNo != 0) {
                adapterCount2 = this.mAdapter.getItemCount() - this.mAdapter.getFooterSize();
            }
            int maxLoadSize = adapterCount2 + 10;
            if (this.mTempFcList.size() >= maxLoadSize) {
                int startIndex2 = 0;
                if (this.pageNo != 0) {
                    startIndex2 = adapterCount2;
                }
                ArrayList<RespFcListBean> mLoadFcList2 = new ArrayList<>();
                for (int i2 = startIndex2; i2 < maxLoadSize; i2++) {
                    mLoadFcList2.add(this.mTempFcList.get(i2));
                }
                setData(mLoadFcList2);
            } else if (!finish) {
                this.roundNum++;
                getFcPageList();
            } else {
                int startIndex3 = 0;
                if (this.pageNo != 0) {
                    startIndex3 = adapterCount2;
                }
                ArrayList<RespFcListBean> mLoadFcList3 = new ArrayList<>();
                for (int i3 = startIndex3; i3 < this.mTempFcList.size(); i3++) {
                    mLoadFcList3.add(this.mTempFcList.get(i3));
                }
                setData(mLoadFcList3);
            }
        } else {
            setData((ArrayList<RespFcListBean>) null);
        }
    }

    public void getFCListFailed(String msg) {
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        FcToastUtils.show((CharSequence) LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail));
    }

    private void setData(ArrayList<RespFcListBean> mFclistBeanList) {
        if (this.pageNo == 0) {
            if (mFclistBeanList == null || mFclistBeanList.size() == 0) {
                this.mSmartRefreshLayout.setEnableLoadMore(false);
                this.mAdapter.refresh(new ArrayList());
            } else {
                if (mFclistBeanList.size() < 10) {
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
            refreshPageState(false);
            return;
        }
        if (mFclistBeanList == null || mFclistBeanList.size() < 10) {
            mFclistBeanList.add(new RespFcListBean());
            this.mSmartRefreshLayout.setEnableLoadMore(false);
        }
        this.mAdapter.loadMore(mFclistBeanList);
        refreshPageState(false);
        if (mFclistBeanList.size() > 0) {
            this.pageNo++;
        }
    }

    private void refreshPageState(boolean isShowIgnore) {
        if (isShowIgnore) {
            this.rlIgnoreView.setVisibility(0);
            this.rlEmptyView.setVisibility(8);
            this.rvFcList.setVisibility(8);
            return;
        }
        this.rlIgnoreView.setVisibility(8);
        UserFcListAdapter userFcListAdapter = this.mAdapter;
        if (userFcListAdapter == null || userFcListAdapter.getDataList().size() > this.mAdapter.getHeaderFooterCount()) {
            this.rlEmptyView.setVisibility(8);
            this.rvFcList.setVisibility(0);
            return;
        }
        this.rlEmptyView.setVisibility(0);
        this.rvFcList.setVisibility(8);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i1;
        int childPosition;
        int i = id;
        Object[] objArr = args;
        if (i == NotificationCenter.userFullInfoDidLoad) {
            if (objArr != null && objArr.length >= 2 && (objArr[1] instanceof TLRPC.UserFull) && ((Integer) objArr[0]).intValue() == this.userId) {
                TLRPC.UserFull userInfo = (TLRPC.UserFull) objArr[1];
                if (userInfo instanceof TLRPCContacts.CL_userFull_v1) {
                    setExtraUserInfoData((TLRPCContacts.CL_userFull_v1) userInfo);
                }
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
                            RespFcListBean respFcListBean = dataList.get(i2);
                            if (respFcListBean != null && respFcListBean.getCreateBy() == createBy) {
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
                            RespFcListBean respFcListBean2 = dataList2.get(i3);
                            if (respFcListBean2 != null && respFcListBean2.getForumID() == forumId) {
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
        } else if (i == NotificationCenter.fcLikeStatusUpdate) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                FcLikeBean fcLikeBean = (FcLikeBean) objArr[1];
                boolean isLike = ((Boolean) objArr[2]).booleanValue();
                int position3 = -1;
                if (this.mAdapter != null && fcLikeBean != null && fcLikeBean.getCommentID() == 0) {
                    List<RespFcListBean> dataList3 = this.mAdapter.getDataList();
                    int i4 = 0;
                    while (true) {
                        if (i4 < dataList3.size()) {
                            RespFcListBean respFcListBean3 = dataList3.get(i4);
                            if (respFcListBean3 != null && respFcListBean3.getForumID() == fcLikeBean.getForumID()) {
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
                    doLikeAfterViewChange(position3, isLike, fcLikeBean);
                }
            }
        } else if (i == NotificationCenter.fcReplyItem) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                FcReplyBean data = (FcReplyBean) objArr[1];
                int position4 = -1;
                UserFcListAdapter userFcListAdapter3 = this.mAdapter;
                if (userFcListAdapter3 != null && data != null) {
                    List<RespFcListBean> dataList4 = userFcListAdapter3.getDataList();
                    int i5 = 0;
                    while (true) {
                        if (i5 < dataList4.size()) {
                            RespFcListBean respFcListBean4 = dataList4.get(i5);
                            if (respFcListBean4 != null && respFcListBean4.getForumID() == data.getForumID()) {
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
                    doReplySuccAfterViewChange(data, position4);
                }
            }
        } else if (i == NotificationCenter.fcDeleteReplyItem) {
            if (!TextUtils.equals(this.TAG, (String) objArr[0])) {
                long forumId2 = ((Long) objArr[1]).longValue();
                long commentId = ((Long) objArr[2]).longValue();
                int childPosition2 = -1;
                UserFcListAdapter userFcListAdapter4 = this.mAdapter;
                if (userFcListAdapter4 != null && forumId2 > 0 && commentId > 0) {
                    List<RespFcListBean> dataList5 = userFcListAdapter4.getDataList();
                    int i6 = 0;
                    while (true) {
                        if (i6 >= dataList5.size()) {
                            break;
                        }
                        RespFcListBean respFcListBean5 = dataList5.get(i6);
                        if (respFcListBean5 == null || respFcListBean5.getForumID() != forumId2) {
                            i6++;
                        } else {
                            int parentPosition = i6;
                            ArrayList<FcReplyBean> comments = respFcListBean5.getComments();
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
                    int i7 = childPosition;
                    doDeleteReplySuccAfterViewChange(forumId2, commentId, i1, childPosition);
                    return;
                }
                i1 = -1;
                childPosition = -1;
                if (i1 != -1) {
                }
            }
        }
    }

    private void setExtraUserInfoData(TLRPCContacts.CL_userFull_v1 userFullV1) {
        if (userFullV1 != null) {
            final TLRPCContacts.CL_userFull_v1_Bean userFullV1Bean = userFullV1.getExtendBean();
            if (userFullV1Bean != null) {
                if (userFullV1Bean.sex != 0) {
                    this.tvGender.setSelected(userFullV1Bean.sex == 1);
                    String str = "";
                    if (userFullV1Bean.birthday > 0) {
                        int ageByBirthday = TimeUtils.getAgeByBirthday(new Date(((long) userFullV1Bean.birthday) * 1000));
                        MryTextView mryTextView = this.tvGender;
                        if (ageByBirthday > 0) {
                            str = String.valueOf(ageByBirthday);
                        }
                        mryTextView.setText(str);
                        this.tvGender.setCompoundDrawablePadding(ageByBirthday > 0 ? AndroidUtilities.dp(2.0f) : 0);
                    } else {
                        this.tvGender.setText(str);
                        this.tvGender.setCompoundDrawablePadding(0);
                    }
                    this.tvGender.setVisibility(0);
                } else {
                    this.tvGender.setVisibility(8);
                }
            }
            TLRPC.User user = userFullV1.user;
            if (user != null) {
                MessagesController.getInstance(UserConfig.selectedAccount).putUser(user, false);
                this.ivUserAvatar.setImage(ImageLocation.getForUser(user, false), "60_60", (Drawable) new AvatarDrawable(user, true), (Object) user);
                this.tvUserName.setText(StringUtils.handleTextName(ContactsController.formatName(user.first_name, user.last_name), 12));
                if (user.mutual_contact || user.contact) {
                    this.ivFcOperate.setVisibility(0);
                    this.ivFcOperate.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            FcPageOthersActivity.this.presentFragment(new FcSettingActivity((long) FcPageOthersActivity.this.userId, userFullV1Bean.sex));
                        }
                    });
                }
            }
        }
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
                        FcPageOthersActivity.this.lambda$onAction$1$FcPageOthersActivity(this.f$1, this.f$2, view);
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
                        FcPageOthersActivity.this.lambda$onAction$2$FcPageOthersActivity(this.f$1, this.f$2, view);
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
                        FcPageOthersActivity.this.lambda$onAction$3$FcPageOthersActivity(this.f$1, view);
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

    public /* synthetic */ void lambda$onAction$1$FcPageOthersActivity(int position, RespFcListBean model, View dialog) {
        doDeleteItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$2$FcPageOthersActivity(int position, RespFcListBean model, View dialog) {
        doIgnoreItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$3$FcPageOthersActivity(RespFcListBean model, View dialog) {
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

    /* access modifiers changed from: protected */
    public void doFollowAfterViewChange(int position, boolean isFollow) {
        this.btnFollow.setText(isFollow ? "已关注" : "关注");
        this.btnFollow.setSelected(isFollow);
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
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageOthersActivity.doLikeAfterViewChange(int, boolean, com.bjz.comm.net.bean.FcLikeBean):void");
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
}
