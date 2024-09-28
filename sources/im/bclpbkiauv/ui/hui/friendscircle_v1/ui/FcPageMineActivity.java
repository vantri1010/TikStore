package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.SPConstant;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcPageMinePresenter;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import com.bjz.comm.net.utils.HttpUtils;
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
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.ShapeUtils;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
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
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.AutoPlayTool;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.FcDialogUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FcPageMineActivity extends CommFcListActivity implements NotificationCenter.NotificationCenterDelegate, FcItemActionClickListener, BaseFcContract.IFcPageMineView {
    private String TAG = FcPageMineActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public AutoPlayTool autoPlayTool;
    /* access modifiers changed from: private */
    public ImagePreSelectorActivity imageSelectorAlert;
    private ImageView ivFcBg;
    /* access modifiers changed from: private */
    public BackupImageView ivUserAvatar;
    private UserFcListAdapter mAdapter;
    /* access modifiers changed from: private */
    public FcPageMinePresenter mPresenter;
    /* access modifiers changed from: private */
    public SmartRefreshLayout mSmartRefreshLayout;
    /* access modifiers changed from: private */
    public int pageNo = 0;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.PhotoEntry> photoEntries = new ArrayList<>();
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            TLRPC.User user;
            if (!(fileLocation == null || (user = MessagesController.getInstance(FcPageMineActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(FcPageMineActivity.this.currentAccount).getClientUserId()))) == null || user.photo == null || user.photo.photo_big == null)) {
                TLRPC.FileLocation photoBig = user.photo.photo_big;
                if (photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                    int[] coords = new int[2];
                    FcPageMineActivity.this.ivUserAvatar.getLocationInWindow(coords);
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    int i = 0;
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (Build.VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = FcPageMineActivity.this.ivUserAvatar;
                    object.imageReceiver = FcPageMineActivity.this.ivUserAvatar.getImageReceiver();
                    object.dialogId = UserConfig.getInstance(FcPageMineActivity.this.currentAccount).getClientUserId();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.size = -1;
                    object.radius = FcPageMineActivity.this.ivUserAvatar.getImageReceiver().getRoundRadius();
                    object.scale = FcPageMineActivity.this.ivUserAvatar.getScaleX();
                    return object;
                }
            }
            return null;
        }

        public void willHidePhotoViewer() {
            FcPageMineActivity.this.ivUserAvatar.getImageReceiver().setVisible(true, true);
        }
    };
    private int replyChildPosition = -1;
    private RespFcListBean replyItemModel;
    private int replyParentPosition = -1;
    private RelativeLayout rlEmpty;
    /* access modifiers changed from: private */
    public RecyclerView rvFcList;
    RecyclerView.OnScrollListener rvScrollListener = new RecyclerView.OnScrollListener() {
        boolean isScroll = false;

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (FcPageMineActivity.this.autoPlayTool != null && this.isScroll) {
                FcPageMineActivity.this.autoPlayTool.onScrolledAndDeactivate();
            }
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            this.isScroll = newState != 0;
            if (newState != 0) {
                return;
            }
            if (FcPageMineActivity.this.mSmartRefreshLayout.getState() == RefreshState.None || FcPageMineActivity.this.mSmartRefreshLayout.getState() == RefreshState.RefreshFinish) {
                FcPageMineActivity.this.isActivePlayer(recyclerView);
            }
        }
    };
    private MryTextView tvFansNum;
    private MryTextView tvFollowedUserNum;
    private MryTextView tvGender;
    private MryTextView tvLikeNum;
    private MryTextView tvPublishFcNum;
    private MryTextView tvUserName;

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_fc_page_mine;
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
        ImageView ivFcPublish = (ImageView) this.fragmentView.findViewById(R.id.iv_fc_publish);
        this.ivFcBg = (ImageView) this.fragmentView.findViewById(R.id.iv_fc_bg);
        BackupImageView backupImageView = (BackupImageView) this.fragmentView.findViewById(R.id.iv_user_avatar);
        this.ivUserAvatar = backupImageView;
        backupImageView.setBackground(ShapeUtils.create(this.mContext.getResources().getColor(R.color.color_FFE8E8E8), (float) AndroidUtilities.dp(8.0f)));
        this.ivUserAvatar.setRoundRadius(AndroidUtilities.dp(8.0f));
        this.tvUserName = (MryTextView) this.fragmentView.findViewById(R.id.tv_user_name);
        this.tvGender = (MryTextView) this.fragmentView.findViewById(R.id.tv_gender);
        this.tvPublishFcNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_publish_fc_num);
        this.tvFollowedUserNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_followed_user_num);
        this.tvLikeNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_like_num);
        this.tvFansNum = (MryTextView) this.fragmentView.findViewById(R.id.tv_fans_num);
        this.tvPublishFcNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.tvFollowedUserNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.tvLikeNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.tvFansNum.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.rvFcList = (RecyclerView) this.fragmentView.findViewById(R.id.rv_fc_list);
        this.rlEmpty = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_empty);
        final CollapsingToolbarLayout collapsingToolbarLayout = ctlTitle;
        final ImageView imageView = icBack;
        final ImageView imageView2 = ivFcPublish;
        AnonymousClass1 r8 = r0;
        final MryTextView mryTextView = tvTitle;
        CollapsingToolbarLayout collapsingToolbarLayout2 = ctlTitle;
        ImageView ivFcPublish2 = ivFcPublish;
        final boolean isLight = Theme.getCurrentTheme().isLight();
        AnonymousClass1 r0 = new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) < collapsingToolbarLayout.getScrimVisibleHeightTrigger()) {
                    imageView.setImageResource(R.mipmap.ic_fc_back_white);
                    imageView2.setImageResource(R.mipmap.ic_fc_publish_white);
                    mryTextView.setAlpha(0.0f);
                    return;
                }
                if (isLight) {
                    imageView.setImageResource(R.mipmap.ic_fc_back_black);
                } else {
                    imageView.setImageResource(R.mipmap.ic_fc_back_white);
                }
                imageView2.setImageResource(R.mipmap.ic_fc_publish_blue);
                mryTextView.setAlpha(1.0f);
            }
        };
        ((AppBarLayout) this.fragmentView.findViewById(R.id.mAppbarLayout)).addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) r8);
        icBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcPageMineActivity.this.finishFragment();
            }
        });
        ivFcPublish2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcPageMineActivity.this.startPublishActivity();
            }
        });
        this.ivFcBg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FcPageMineActivity.this.showResetFcBgDialog();
            }
        });
        this.ivUserAvatar.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcPageMineActivity.this.lambda$initView$0$FcPageMineActivity(view);
            }
        });
        this.mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                FcPageMineActivity.this.loadFcBaseInfo();
                int unused = FcPageMineActivity.this.pageNo = 0;
                FcPageMineActivity.this.getFcPageList();
            }

            public void onLoadMore(RefreshLayout refreshLayout) {
                FcPageMineActivity.this.getFcPageList();
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

    public /* synthetic */ void lambda$initView$0$FcPageMineActivity(View v) {
        TLRPC.User user1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user1 != null && user1.photo != null && user1.photo.photo_big != null) {
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
                if (FcPageMineActivity.this.rvScrollListener != null) {
                    FcPageMineActivity.this.rvScrollListener.onScrollStateChanged(FcPageMineActivity.this.rvFcList, 0);
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
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0);
        String backStr = sharedPreferences.getString("fc_bg" + getUserConfig().getCurrentUser().id, "");
        if (!TextUtils.isEmpty(backStr) && this.ivFcBg != null) {
            GlideUtils.getInstance().load(backStr, this.mContext, this.ivFcBg, (int) R.drawable.shape_fc_default_pic_bg);
        }
        loadUserInfo();
        loadFcBaseInfo();
        getFcPageList();
    }

    private void loadUserInfo() {
        TLRPC.User currentUser;
        if (getUserConfig() != null && getUserConfig().getClientUserId() != -1 && (currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser()) != null) {
            this.ivUserAvatar.setImage(ImageLocation.getForUser(currentUser, false), "60_60", (Drawable) new AvatarDrawable(currentUser, true), (Object) currentUser);
            this.tvUserName.setText(StringUtils.handleTextName(ContactsController.formatName(currentUser.first_name, currentUser.last_name), 12));
        }
    }

    /* access modifiers changed from: private */
    public void loadFcBaseInfo() {
        this.mPresenter.getActionCount((long) getUserConfig().getClientUserId());
    }

    public void getActionCountSucc(RespFcUserStatisticsBean data) {
        if (data != null) {
            this.tvPublishFcNum.setText(Integer.toString(data.getForumCount()));
            this.tvFollowedUserNum.setText(Integer.toString(data.getFollowCount()));
            this.tvLikeNum.setText(Integer.toString(data.getThumbCount()));
            this.tvFansNum.setText(Integer.toString(data.getFansCount()));
            if (!TextUtils.isEmpty(data.getHomeBackground()) && this.ivFcBg != null) {
                saveFcBackground(HttpUtils.getInstance().getDownloadFileUrl() + data.getHomeBackground());
                GlideUtils instance = GlideUtils.getInstance();
                instance.load(HttpUtils.getInstance().getDownloadFileUrl() + data.getHomeBackground(), this.mContext, this.ivFcBg, (int) R.drawable.shape_fc_default_pic_bg);
            }
            FcUserInfoBean fcUserInfoBean = data.getUser();
            if (fcUserInfoBean != null && this.tvGender != null) {
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
                    return;
                }
                this.tvGender.setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: private */
    public void getFcPageList() {
        this.mPresenter.getFCList(20, this.pageNo == 0 ? 0 : this.mAdapter.getEndListId());
    }

    public void getFCListSucc(ArrayList<RespFcListBean> data) {
        this.mSmartRefreshLayout.finishRefresh();
        this.mSmartRefreshLayout.finishLoadMore();
        setData(data);
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
            refreshPageState();
            return;
        }
        if (mFclistBeanList == null || mFclistBeanList.size() < 20) {
            mFclistBeanList.add(new RespFcListBean());
            this.mSmartRefreshLayout.setEnableLoadMore(false);
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
            this.rlEmpty.setVisibility(8);
            this.rvFcList.setVisibility(0);
            return;
        }
        this.rlEmpty.setVisibility(0);
        this.rvFcList.setVisibility(8);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        RespFcListBean respFcListBean;
        int i1;
        int childPosition;
        int i = id;
        Object[] objArr = args;
        if (i == NotificationCenter.userFullInfoDidLoad) {
            if (objArr != null && objArr.length >= 2 && (objArr[1] instanceof TLRPC.UserFull) && ((Integer) objArr[0]).intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                TLRPC.UserFull userInfo = (TLRPC.UserFull) objArr[1];
                if (userInfo instanceof TLRPCContacts.CL_userFull_v1) {
                    setExtraUserInfoData(((TLRPCContacts.CL_userFull_v1) userInfo).getExtendBean());
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

    private void setExtraUserInfoData(TLRPCContacts.CL_userFull_v1_Bean userFullV1Bean) {
        if (userFullV1Bean == null) {
            return;
        }
        if (userFullV1Bean.sex != 0) {
            MryTextView mryTextView = this.tvGender;
            boolean z = true;
            if (userFullV1Bean.sex != 1) {
                z = false;
            }
            mryTextView.setSelected(z);
            String str = "";
            if (userFullV1Bean.birthday > 0) {
                int ageByBirthday = TimeUtils.getAgeByBirthday(new Date(((long) userFullV1Bean.birthday) * 1000));
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
            return;
        }
        this.tvGender.setVisibility(8);
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
                            FcPageMineActivity.this.mPresenter.setFcBackground(name);
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
        if (TextUtils.isEmpty(servicePath) || this.ivFcBg == null) {
            FcToastUtils.show((CharSequence) "设置失败");
            return;
        }
        saveFcBackground(HttpUtils.getInstance().getDownloadFileUrl() + servicePath);
        GlideUtils instance = GlideUtils.getInstance();
        instance.load(HttpUtils.getInstance().getDownloadFileUrl() + servicePath, this.mContext, this.ivFcBg, (int) R.drawable.shape_fc_default_pic_bg);
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
                        FcPageMineActivity.this.lambda$onAction$1$FcPageMineActivity(this.f$1, this.f$2, view);
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
                        FcPageMineActivity.this.lambda$onAction$2$FcPageMineActivity(this.f$1, this.f$2, view);
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
                        FcPageMineActivity.this.lambda$onAction$3$FcPageMineActivity(this.f$1, view);
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

    public /* synthetic */ void lambda$onAction$1$FcPageMineActivity(int position, RespFcListBean model, View dialog) {
        doDeleteItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$2$FcPageMineActivity(int position, RespFcListBean model, View dialog) {
        doIgnoreItem(position, model);
    }

    public /* synthetic */ void lambda$onAction$3$FcPageMineActivity(RespFcListBean model, View dialog) {
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
        this.rvFcList.scrollToPosition(0);
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
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageMineActivity.doLikeAfterViewChange(int, boolean, com.bjz.comm.net.bean.FcLikeBean):void");
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
            AnonymousClass9 r0 = new ImagePreSelectorActivity(getParentActivity()) {
                public void dismissInternal() {
                    if (FcPageMineActivity.this.imageSelectorAlert.isShowing()) {
                        AndroidUtilities.requestAdjustResize(FcPageMineActivity.this.getParentActivity(), FcPageMineActivity.this.classGuid);
                        for (int i = 0; i < FcPageMineActivity.this.photoEntries.size(); i++) {
                            if (((MediaController.PhotoEntry) FcPageMineActivity.this.photoEntries.get(i)).isVideo) {
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
                    if (button == 8 || button == 7 || (button == 4 && !FcPageMineActivity.this.imageSelectorAlert.getSelectedPhotos().isEmpty())) {
                        if (button != 8) {
                            FcPageMineActivity.this.imageSelectorAlert.dismiss();
                        }
                        HashMap<Object, Object> selectedPhotos = FcPageMineActivity.this.imageSelectorAlert.getSelectedPhotos();
                        ArrayList<Object> selectedPhotosOrder = FcPageMineActivity.this.imageSelectorAlert.getSelectedPhotosOrder();
                        int currentSelectMediaType = FcPageMineActivity.this.imageSelectorAlert.getCurrentSelectMediaType();
                        if (selectedPhotos.isEmpty() || selectedPhotosOrder.isEmpty()) {
                            FcPageMineActivity.this.presentFragment(new FcPublishActivity());
                        } else {
                            FcPageMineActivity.this.presentFragment(new FcPublishActivity(FcPageMineActivity.this.imageSelectorAlert, selectedPhotos, selectedPhotosOrder, currentSelectMediaType));
                        }
                    } else if (FcPageMineActivity.this.imageSelectorAlert != null) {
                        FcPageMineActivity.this.imageSelectorAlert.dismissWithButtonClick(button);
                        FcPageMineActivity.this.presentFragment(new FcPublishActivity());
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
                    AndroidUtilities.setAdjustResizeToNothing(FcPageMineActivity.this.getParentActivity(), FcPageMineActivity.this.classGuid);
                }
            });
        }
    }
}
