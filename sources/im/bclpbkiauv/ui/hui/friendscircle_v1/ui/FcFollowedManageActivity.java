package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.AvatarPhotoBean;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.bjz.comm.net.bean.ResponseFcAttentionUsertBeanV1;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.utils.JsonCreateUtils;
import com.bjz.comm.net.utils.RxHelper;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import im.bclpbkiauv.ui.hui.decoration.TopDecorationWithSearch;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.Date;

public class FcFollowedManageActivity extends CommFcActivity {
    private String TAG = getClass().getSimpleName();
    public ArrayList<Integer> canceledFocusUser = new ArrayList<>();
    private FrameLayout container;
    /* access modifiers changed from: private */
    public FrameLayout fl_search_container;
    /* access modifiers changed from: private */
    public FrameLayout fl_search_cover;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public MyAdapter myAdapter;
    /* access modifiers changed from: private */
    public MysearchAdapter mysearchAdapter;
    /* access modifiers changed from: private */
    public String searchText;
    private MrySearchView searchView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_fc_followed_manage;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initActionBar();
        initSearchView();
        initListView();
    }

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("manage", R.string.manage));
        this.actionBar.setDelegate(new ActionBar.ActionBarDelegate() {
            public final void onSearchFieldVisibilityChanged(boolean z) {
                FcFollowedManageActivity.this.lambda$initActionBar$0$FcFollowedManageActivity(z);
            }
        });
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FcFollowedManageActivity.this.finishFragment();
                }
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$0$FcFollowedManageActivity(boolean visible) {
        this.actionBar.getBackButton().setVisibility(visible ? 0 : 8);
    }

    private void initListView() {
        this.container = (FrameLayout) this.fragmentView.findViewById(R.id.container);
        RecyclerListView recyclerListView = (RecyclerListView) this.fragmentView.findViewById(R.id.listView);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.listView.addItemDecoration(new TopDecorationWithSearch(60, false));
        this.mysearchAdapter = new MysearchAdapter(this.mContext);
        RecyclerListView recyclerListView2 = this.listView;
        MyAdapter myAdapter2 = new MyAdapter(this.mContext);
        this.myAdapter = myAdapter2;
        recyclerListView2.setAdapter(myAdapter2);
        this.myAdapter.emptyAttachView(this.container);
        MryEmptyView emptyView = this.myAdapter.getEmptyView();
        emptyView.setEmptyText(LocaleController.getString(R.string.NoFollowedPageDataMessages));
        emptyView.setEmptyResId(R.mipmap.img_empty_default);
        emptyView.setErrorResId(R.mipmap.img_empty_default);
        emptyView.getTextView().setTextColor(this.mContext.getResources().getColor(R.color.color_FFDBC9B8));
        emptyView.getBtn().setPrimaryRadiusAdjustBoundsFillStyle();
        emptyView.getBtn().setRoundBgGradientColors(new int[]{-4789508, -13187843});
        emptyView.getBtn().setStrokeWidth(0);
        emptyView.getBtn().setPadding(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(5.0f));
        emptyView.setOnEmptyClickListener(new MryEmptyView.OnEmptyOrErrorClickListener() {
            public final boolean onEmptyViewButtonClick(boolean z) {
                return FcFollowedManageActivity.this.lambda$initListView$1$FcFollowedManageActivity(z);
            }
        });
        this.myAdapter.setStartPage(0);
        this.myAdapter.showLoading();
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (FcFollowedManageActivity.this.searching && FcFollowedManageActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(FcFollowedManageActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int off = recyclerView.computeVerticalScrollOffset();
                if (off == 0) {
                    FcFollowedManageActivity.this.fl_search_container.setBackgroundColor(Theme.getColor(Theme.key_list_decorationBackground));
                    FcFollowedManageActivity.this.fl_search_container.setScrollY(off > AndroidUtilities.dp(55.0f) ? AndroidUtilities.dp(55.0f) : off);
                } else if (off > 0) {
                    FcFollowedManageActivity.this.fl_search_container.setBackgroundColor(0);
                    FcFollowedManageActivity.this.fl_search_container.setScrollY(off > AndroidUtilities.dp(55.0f) ? AndroidUtilities.dp(55.0f) : off);
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                FcUserInfoBean fcUserInfoBean;
                TextView tv_attention = (TextView) view.findViewById(R.id.tv_attention);
                if (FcFollowedManageActivity.this.searchWas) {
                    fcUserInfoBean = (FcUserInfoBean) FcFollowedManageActivity.this.mysearchAdapter.getData().get(position);
                } else {
                    fcUserInfoBean = (FcUserInfoBean) FcFollowedManageActivity.this.myAdapter.getData().get(position);
                }
                if (!tv_attention.isSelected()) {
                    FcFollowedManageActivity.this.doFollow(position, fcUserInfoBean);
                } else {
                    FcFollowedManageActivity.this.doCancelFocusUser(position, fcUserInfoBean);
                }
            }
        });
    }

    public /* synthetic */ boolean lambda$initListView$1$FcFollowedManageActivity(boolean isEmptyButton) {
        this.myAdapter.showLoading();
        getFcPageList(0, "");
        return false;
    }

    private void initSearchView() {
        FrameLayout frameLayout = (FrameLayout) this.fragmentView.findViewById(R.id.fl_search_cover);
        this.fl_search_cover = frameLayout;
        frameLayout.setOnClickListener($$Lambda$FcFollowedManageActivity$e1QPztXaFkpiZjRxxwBHvtfoOxM.INSTANCE);
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView.findViewById(R.id.fl_search_container);
        this.fl_search_container = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_list_decorationBackground));
        MrySearchView mrySearchView = (MrySearchView) this.fragmentView.findViewById(R.id.searchview);
        this.searchView = mrySearchView;
        mrySearchView.setCancelTextColor(this.mContext.getResources().getColor(R.color.color_778591));
        this.searchView.setEditTextBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_divider), (float) AndroidUtilities.dp(50.0f)));
        this.searchView.setHintText(LocaleController.getString("searchAttentionUser", R.string.searchAttentionUser));
        this.searchView.setiSearchViewDelegate(new MrySearchView.ISearchViewDelegate() {
            public void onStart(boolean focus) {
                if (focus) {
                    FcFollowedManageActivity fcFollowedManageActivity = FcFollowedManageActivity.this;
                    fcFollowedManageActivity.hideTitle(fcFollowedManageActivity.fragmentView);
                    FcFollowedManageActivity.this.fl_search_container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    FcFollowedManageActivity.this.fl_search_cover.setVisibility(0);
                    return;
                }
                FcFollowedManageActivity fcFollowedManageActivity2 = FcFollowedManageActivity.this;
                fcFollowedManageActivity2.showTitle(fcFollowedManageActivity2.fragmentView);
                FcFollowedManageActivity.this.fl_search_container.setBackgroundColor(Theme.getColor(Theme.key_list_decorationBackground));
                FcFollowedManageActivity.this.fl_search_cover.setVisibility(8);
            }

            public void onSearchExpand() {
                boolean unused = FcFollowedManageActivity.this.searching = true;
            }

            public boolean canCollapseSearch() {
                boolean unused = FcFollowedManageActivity.this.searching = false;
                boolean unused2 = FcFollowedManageActivity.this.searchWas = false;
                FcFollowedManageActivity.this.listView.setAdapter(FcFollowedManageActivity.this.myAdapter);
                FcFollowedManageActivity.this.myAdapter.notifyDataSetChanged();
                return true;
            }

            public void onSearchCollapse() {
                boolean unused = FcFollowedManageActivity.this.searching = false;
                boolean unused2 = FcFollowedManageActivity.this.searchWas = false;
            }

            public void onTextChange(String text) {
                if (FcFollowedManageActivity.this.mysearchAdapter != null) {
                    String unused = FcFollowedManageActivity.this.searchText = text;
                    boolean unused2 = FcFollowedManageActivity.this.searchWas = true;
                    if (text.length() != 0) {
                        if (FcFollowedManageActivity.this.listView != null) {
                            FcFollowedManageActivity.this.listView.setAdapter(FcFollowedManageActivity.this.mysearchAdapter);
                            FcFollowedManageActivity.this.mysearchAdapter.notifyDataSetChanged();
                        }
                        FcFollowedManageActivity.this.getFcPageList(0, text);
                    } else if (FcFollowedManageActivity.this.searching) {
                        FcFollowedManageActivity.this.listView.setAdapter(FcFollowedManageActivity.this.mysearchAdapter);
                        FcFollowedManageActivity.this.mysearchAdapter.getData().clear();
                        FcFollowedManageActivity.this.mysearchAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void onActionSearch(String trim) {
            }
        });
    }

    static /* synthetic */ void lambda$initSearchView$2(View v) {
    }

    /* access modifiers changed from: protected */
    public void initData() {
        getFcPageList(0, "");
    }

    private class MysearchAdapter extends PageSelectionAdapter<FcUserInfoBean, PageHolder> {
        public MysearchAdapter(Context context) {
            super(context);
        }

        public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            return new PageHolder(LayoutInflater.from(FcFollowedManageActivity.this.mContext).inflate(R.layout.item_attention_manage, parent, false), 0);
        }

        public void onBindViewHolderForChild(PageHolder holder, int position, FcUserInfoBean item) {
            PageHolder pageHolder = holder;
            BackupImageView img_avatar = (BackupImageView) pageHolder.itemView.findViewById(R.id.img_avatar);
            TextView tv_nick_name = (TextView) pageHolder.itemView.findViewById(R.id.tv_nick_name);
            TextView tv_gender_age = (TextView) pageHolder.itemView.findViewById(R.id.tv_gender_age);
            TextView tv_attention = (TextView) pageHolder.itemView.findViewById(R.id.tv_attention);
            if (Theme.getCurrentTheme().isLight()) {
                tv_nick_name.setTextColor(FcFollowedManageActivity.this.mContext.getResources().getColor(R.color.color_111111));
            } else {
                tv_nick_name.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            }
            tv_nick_name.setText(StringUtils.handleTextName(ContactsController.formatName(item.getFirstName(), item.getLastName()), 12));
            img_avatar.setRoundRadius(AndroidUtilities.dp(5.0f));
            AvatarPhotoBean avatarPhotoBean = item.getPhoto();
            if (avatarPhotoBean != null) {
                int photoSize = avatarPhotoBean.getSmallPhotoSize();
                int localId = avatarPhotoBean.getSmallLocalId();
                long volumeId = avatarPhotoBean.getSmallVolumeId();
                if (!(photoSize == 0 || volumeId == 0 || avatarPhotoBean.getAccess_hash() == 0)) {
                    TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
                    inputPeer.user_id = item.getUserId();
                    inputPeer.access_hash = item.getAccessHash();
                    ImageLocation imageLocation = new ImageLocation();
                    imageLocation.dc_id = 2;
                    imageLocation.photoPeer = inputPeer;
                    imageLocation.location = new TLRPC.TL_fileLocationToBeDeprecated();
                    imageLocation.location.local_id = localId;
                    imageLocation.location.volume_id = volumeId;
                    img_avatar.setImage(imageLocation, "40_40", (Drawable) new AvatarDrawable(), (Object) inputPeer);
                }
            }
            if (item.getSex() != 0) {
                tv_gender_age.setVisibility(0);
                if (item.getSex() == 1) {
                    tv_gender_age.setSelected(true);
                } else {
                    tv_gender_age.setSelected(false);
                }
                tv_gender_age.setText(TimeUtils.getAgeByBirthday(new Date(((long) item.getBirthday()) * 1000)) + "");
                tv_gender_age.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
            } else {
                tv_gender_age.setVisibility(8);
            }
            if (FcFollowedManageActivity.this.canceledFocusUser.size() <= 0 || !FcFollowedManageActivity.this.canceledFocusUser.contains(Integer.valueOf(item.getUserId()))) {
                tv_attention.setText(LocaleController.getString("attentioned", R.string.attentioned));
                tv_attention.setSelected(false);
            } else {
                tv_attention.setText(LocaleController.getString("attention", R.string.attention));
                tv_attention.setSelected(true);
            }
            tv_attention.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        }

        public void loadData(int page) {
            super.loadData(page);
            FcFollowedManageActivity fcFollowedManageActivity = FcFollowedManageActivity.this;
            fcFollowedManageActivity.getFcPageList(page, fcFollowedManageActivity.searchText);
        }
    }

    private class MyAdapter extends PageSelectionAdapter<FcUserInfoBean, PageHolder> {
        public MyAdapter(Context context) {
            super(context);
        }

        public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
            return new PageHolder(LayoutInflater.from(FcFollowedManageActivity.this.mContext).inflate(R.layout.item_attention_manage, parent, false), 0);
        }

        public void onBindViewHolderForChild(PageHolder holder, int position, FcUserInfoBean item) {
            PageHolder pageHolder = holder;
            BackupImageView img_avatar = (BackupImageView) pageHolder.itemView.findViewById(R.id.img_avatar);
            TextView tv_nick_name = (TextView) pageHolder.itemView.findViewById(R.id.tv_nick_name);
            TextView tv_gender_age = (TextView) pageHolder.itemView.findViewById(R.id.tv_gender_age);
            TextView tv_attention = (TextView) pageHolder.itemView.findViewById(R.id.tv_attention);
            if (Theme.getCurrentTheme().isLight()) {
                tv_nick_name.setTextColor(FcFollowedManageActivity.this.mContext.getResources().getColor(R.color.color_111111));
            } else {
                tv_nick_name.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            }
            tv_nick_name.setText(StringUtils.handleTextName(ContactsController.formatName(item.getFirstName(), item.getLastName()), 12));
            img_avatar.setRoundRadius(AndroidUtilities.dp(5.0f));
            AvatarPhotoBean avatarPhotoBean = item.getPhoto();
            if (avatarPhotoBean != null) {
                int photoSize = avatarPhotoBean.getSmallPhotoSize();
                int localId = avatarPhotoBean.getSmallLocalId();
                long volumeId = avatarPhotoBean.getSmallVolumeId();
                if (!(photoSize == 0 || volumeId == 0 || avatarPhotoBean.getAccess_hash() == 0)) {
                    TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
                    inputPeer.user_id = item.getUserId();
                    inputPeer.access_hash = item.getAccessHash();
                    ImageLocation imageLocation = new ImageLocation();
                    imageLocation.dc_id = 2;
                    imageLocation.photoPeer = inputPeer;
                    imageLocation.location = new TLRPC.TL_fileLocationToBeDeprecated();
                    imageLocation.location.local_id = localId;
                    imageLocation.location.volume_id = volumeId;
                    img_avatar.setImage(imageLocation, "40_40", (Drawable) new AvatarDrawable(), (Object) inputPeer);
                }
            }
            if (item.getSex() != 0) {
                tv_gender_age.setVisibility(0);
                if (item.getSex() == 1) {
                    tv_gender_age.setSelected(true);
                } else {
                    tv_gender_age.setSelected(false);
                }
                tv_gender_age.setText(TimeUtils.getAgeByBirthday(new Date(((long) item.getBirthday()) * 1000)) + "");
                tv_gender_age.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
            } else {
                tv_gender_age.setVisibility(8);
            }
            if (FcFollowedManageActivity.this.canceledFocusUser.size() <= 0 || !FcFollowedManageActivity.this.canceledFocusUser.contains(Integer.valueOf(item.getUserId()))) {
                tv_attention.setText(LocaleController.getString("attentioned", R.string.attentioned));
                tv_attention.setSelected(true);
            } else {
                tv_attention.setText(LocaleController.getString("attention", R.string.attention));
                tv_attention.setSelected(false);
            }
            tv_attention.setCompoundDrawablePadding(AndroidUtilities.dp(2.0f));
        }

        public void loadData(int page) {
            super.loadData(page);
            FcFollowedManageActivity.this.getFcPageList(page, "");
        }
    }

    /* access modifiers changed from: private */
    public void getFcPageList(int pageNo, String UserName) {
        RxHelper.getInstance().sendRequest(this.TAG, ApiFactory.getInstance().getApiMomentForum().getFollowedUserList(pageNo * 20, 20, UserName), new Consumer() {
            public final void accept(Object obj) {
                FcFollowedManageActivity.this.lambda$getFcPageList$3$FcFollowedManageActivity((BResponse) obj);
            }
        }, new Consumer() {
            public final void accept(Object obj) {
                FcFollowedManageActivity.this.lambda$getFcPageList$4$FcFollowedManageActivity((Throwable) obj);
            }
        });
    }

    public /* synthetic */ void lambda$getFcPageList$3$FcFollowedManageActivity(BResponse response) throws Exception {
        if (response != null && response.isState()) {
            ResponseFcAttentionUsertBeanV1 mFclistBeanList = (ResponseFcAttentionUsertBeanV1) response.Data;
            if (response.Data == null || this.searchWas) {
                this.fl_search_cover.setVisibility(8);
                this.mysearchAdapter.addData(mFclistBeanList.Users);
                return;
            }
            this.myAdapter.addData(mFclistBeanList.Users);
        }
    }

    public /* synthetic */ void lambda$getFcPageList$4$FcFollowedManageActivity(Throwable throwable) throws Exception {
        if (!this.searchWas) {
            this.myAdapter.showError(RxHelper.getInstance().getErrorInfo(throwable));
            return;
        }
        if (this.actionBar.getVisibility() == 4) {
            showTitle(this.fragmentView);
            this.searchView.cancelFocus();
            this.fl_search_container.setBackgroundColor(Theme.getColor(Theme.key_list_decorationBackground));
            this.fl_search_cover.setVisibility(8);
        }
        this.myAdapter.showError(LocaleController.getString("request_fialed", R.string.fc_request_fialed));
    }

    /* access modifiers changed from: protected */
    public void doFollow(int position, FcUserInfoBean fcUserInfoBean) {
        RxHelper.getInstance().sendRequestNoData(this.TAG, ApiFactory.getInstance().getApiMomentForum().doFollow(JsonCreateUtils.build().addParam("FollowUID", Integer.valueOf(fcUserInfoBean.getUserId())).getHttpBody()), new Consumer(position, fcUserInfoBean) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ FcUserInfoBean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                FcFollowedManageActivity.this.lambda$doFollow$5$FcFollowedManageActivity(this.f$1, this.f$2, (BResponseNoData) obj);
            }
        }, $$Lambda$FcFollowedManageActivity$OcEta5bVRlIcpNdlJZOXaSr6hpg.INSTANCE);
    }

    public /* synthetic */ void lambda$doFollow$5$FcFollowedManageActivity(int position, FcUserInfoBean fcUserInfoBean, BResponseNoData responseNoData) throws Exception {
        if (responseNoData == null) {
            FcToastUtils.show((int) R.string.friendscircle_attention_user_fail);
        } else if (responseNoData.isState()) {
            FcToastUtils.show((CharSequence) responseNoData.Message);
            this.myAdapter.notifyItemChanged(position);
            if (this.canceledFocusUser.contains(Integer.valueOf(fcUserInfoBean.getUserId()))) {
                ArrayList<Integer> arrayList = this.canceledFocusUser;
                arrayList.remove(arrayList.indexOf(Integer.valueOf(fcUserInfoBean.getUserId())));
            }
            if (this.searchWas) {
                this.mysearchAdapter.notifyDataSetChanged();
            } else {
                this.myAdapter.notifyDataSetChanged();
            }
            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcFollowStatusUpdate, this.TAG, Long.valueOf((long) fcUserInfoBean.getUserId()), true);
        } else {
            FcToastUtils.show((CharSequence) responseNoData.Message);
        }
    }

    /* access modifiers changed from: protected */
    public void doCancelFocusUser(int position, FcUserInfoBean fcUserInfoBean) {
        RxHelper.getInstance().sendRequestNoData(this.TAG, ApiFactory.getInstance().getApiMomentForum().doCancelFollowed(JsonCreateUtils.build().addParam("FollowUID", Integer.valueOf(fcUserInfoBean.getUserId())).getHttpBody()), new Consumer(position, fcUserInfoBean) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ FcUserInfoBean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                FcFollowedManageActivity.this.lambda$doCancelFocusUser$7$FcFollowedManageActivity(this.f$1, this.f$2, (BResponseNoData) obj);
            }
        }, $$Lambda$FcFollowedManageActivity$rNfq_HKoAOQdVfykVvk9ebc1hcQ.INSTANCE);
    }

    public /* synthetic */ void lambda$doCancelFocusUser$7$FcFollowedManageActivity(int position, FcUserInfoBean fcUserInfoBean, BResponseNoData responseNoData) throws Exception {
        if (responseNoData == null) {
            FcToastUtils.show((int) R.string.friendscircle_attention_user_cancel_fail);
        } else if (responseNoData.isState()) {
            this.myAdapter.notifyItemChanged(position);
            FcToastUtils.show((CharSequence) responseNoData.Message);
            if (!this.canceledFocusUser.contains(Integer.valueOf(fcUserInfoBean.getUserId()))) {
                this.canceledFocusUser.add(Integer.valueOf(fcUserInfoBean.getUserId()));
            }
            if (this.searchWas) {
                this.mysearchAdapter.notifyDataSetChanged();
            } else {
                this.myAdapter.notifyDataSetChanged();
            }
            NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcFollowStatusUpdate, this.TAG, Long.valueOf((long) fcUserInfoBean.getUserId()), false);
        } else {
            FcToastUtils.show((CharSequence) responseNoData.Message);
        }
    }
}
