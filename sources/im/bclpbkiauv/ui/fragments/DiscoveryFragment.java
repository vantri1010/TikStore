package im.bclpbkiauv.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.utils.RxHelper;
import com.blankj.utilcode.util.ScreenUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPC2;
import im.bclpbkiauv.ui.WebviewActivity;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.banner.Banner;
import im.bclpbkiauv.ui.components.banner.adapter.BannerAdapter;
import im.bclpbkiauv.ui.components.banner.indicator.RectangleIndicator;
import im.bclpbkiauv.ui.components.banner.listener.OnBannerListener;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.constants.Constants;
import im.bclpbkiauv.ui.fragments.DiscoveryFragment;
import im.bclpbkiauv.ui.hcells.IndexTextCell;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hui.discovery.NearPersonAndGroupActivity;
import im.bclpbkiauv.ui.hui.discovery.QrScanActivity;
import im.bclpbkiauv.ui.hui.discoveryweb.DiscoveryJumpToPage;
import im.bclpbkiauv.ui.hui.friendscircle.glide.GlideUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.FriendsCircleActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcAlbumActivity;
import im.bclpbkiauv.ui.hui.hotGroup.HotGroupRecommendActivity;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hviews.sliding.SlidingLayout;
import java.util.List;

public class DiscoveryFragment extends BaseFmts implements Constants {
    private static final String TAG = "DiscoveryFragment";
    /* access modifiers changed from: private */
    public int album = -1;
    /* access modifiers changed from: private */
    public int albumEmptyRow = -1;
    /* access modifiers changed from: private */
    public int bannerEndRow = -1;
    /* access modifiers changed from: private */
    public int bannerRow = -1;
    /* access modifiers changed from: private */
    public int bannerStartRow = -1;
    /* access modifiers changed from: private */
    public Delegate delegate;
    /* access modifiers changed from: private */
    public int extraDataEndRow = -1;
    private int extraDataReqToken;
    /* access modifiers changed from: private */
    public int extraDataStartRow = -1;
    /* access modifiers changed from: private */
    public int friendsHubEmptyRow = -1;
    /* access modifiers changed from: private */
    public int friendsHubRow = -1;
    /* access modifiers changed from: private */
    public int gameCenterEmptyRow = -1;
    /* access modifiers changed from: private */
    public int gameCenterRow = -1;
    private boolean hasGps;
    /* access modifiers changed from: private */
    public int lastSectionRow = -1;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int miniProgramRow = -1;
    /* access modifiers changed from: private */
    public int nearbyEmptyRow = -1;
    /* access modifiers changed from: private */
    public int nearbyRow = -1;
    /* access modifiers changed from: private */
    public int recommendChannel = -1;
    /* access modifiers changed from: private */
    public int recommendChannelEmptyRow = -1;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int scanRow = -1;
    /* access modifiers changed from: private */
    public int startSectionRow = -1;

    public interface Delegate {
        TLRPC2.TL_DiscoveryPageSetting getDiscoveryPageData();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");
        } catch (Throwable th) {
            this.hasGps = false;
        }
        updateRow();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(this.context);
        this.fragmentView = frameLayout;
        this.actionBar = createActionBar();
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Discovery", R.string.Discovery));
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        SlidingLayout root = new SlidingLayout(this.context);
        root.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        frameLayout.addView(root, LayoutHelper.createFrameWithActionBar(-1, -1));
        RecyclerListView recyclerListView = new RecyclerListView(this.context);
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        this.listView.setClipChildren(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(this.context, 1, false));
        root.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        View headerShadow = new View(this.context);
        headerShadow.setBackground(getResources().getDrawable(R.drawable.header_shadow).mutate());
        frameLayout.addView(headerShadow, LayoutHelper.createFrameWithActionBar(-1, 1));
        ListAdapter listAdapter2 = new ListAdapter(this.context);
        this.listAdapter = listAdapter2;
        this.listView.setAdapter(listAdapter2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DiscoveryFragment.this.lambda$onCreateView$0$DiscoveryFragment(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$onCreateView$0$DiscoveryFragment(View view, int position) {
        TLRPC2.TL_DiscoveryPageSetting_SM s;
        Activity activity;
        boolean z = true;
        if (position != this.nearbyRow || !this.hasGps) {
            if (position != this.miniProgramRow) {
                if (position == this.friendsHubRow) {
                    presentFragment(new FriendsCircleActivity());
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.userFriendsCircleUpdate, new Object[0]);
                } else if (position != this.gameCenterRow) {
                    if (position == this.scanRow) {
                        presentFragment(new QrScanActivity());
                    } else if (position == this.album) {
                        presentFragment(new FcAlbumActivity());
                    } else if (position != this.recommendChannel) {
                        int i = this.extraDataStartRow;
                        if (i != -1 && position > i && position < this.extraDataEndRow) {
                            int cpo = (position - i) - 1;
                            Delegate delegate2 = this.delegate;
                            if (delegate2 != null && delegate2.getDiscoveryPageData() != null && cpo >= 0 && cpo < this.delegate.getDiscoveryPageData().getS().size() && (s = this.delegate.getDiscoveryPageData().getS().get(cpo)) != null) {
                                if (TextUtils.isEmpty(s.getUrl())) {
                                    getExtraDataLoginUrl(s);
                                } else if (TextUtils.isEmpty(s.getUrl()) || (!s.getUrl().startsWith("http:") && !s.getUrl().startsWith("https:"))) {
                                    ToastUtils.show((int) R.string.CancelLinkExpired);
                                } else {
                                    presentFragment(DiscoveryJumpToPage.toPage(s.getTitle(), s.getUrl()));
                                }
                            }
                        } else if ((view instanceof ShadowSectionCell) == 0) {
                            ToastUtils.show((int) R.string.NotSupport);
                        }
                    } else if (getUserConfig().isClientActivated()) {
                        presentFragment(new HotGroupRecommendActivity());
                    }
                }
            }
        } else if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
            boolean enabled = true;
            if (Build.VERSION.SDK_INT >= 28) {
                enabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
            } else if (Build.VERSION.SDK_INT >= 19) {
                try {
                    if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) == 0) {
                        z = false;
                    }
                    enabled = z;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                presentFragment(new NearPersonAndGroupActivity());
            } else if (position != this.miniProgramRow) {
                if (position == this.scanRow) {
                    presentFragment(new QrScanActivity());
                } else if (((Integer) view.getTag()).intValue() != position) {
                    ToastUtils.show((int) R.string.NotSupport);
                }
            }
            if (!enabled) {
                presentFragment(new ActionIntroActivity(4));
            } else {
                presentFragment(new NearPersonAndGroupActivity());
            }
        } else {
            presentFragment(new ActionIntroActivity(1));
        }
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        super.lazyLoadData();
    }

    private void updateRow() {
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.bannerStartRow = 0;
        Delegate delegate2 = this.delegate;
        if (!(delegate2 == null || delegate2.getDiscoveryPageData() == null || this.delegate.getDiscoveryPageData().getG().size() <= 0)) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.bannerRow = i;
            this.rowCount = i2 + 1;
            this.bannerEndRow = i2;
        }
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.scanRow = i3;
        Delegate delegate3 = this.delegate;
        if (!(delegate3 == null || delegate3.getDiscoveryPageData() == null || this.delegate.getDiscoveryPageData().getS().size() <= 0)) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.extraDataStartRow = i4;
            int size = i5 + this.delegate.getDiscoveryPageData().getS().size();
            this.rowCount = size;
            this.rowCount = size + 1;
            this.extraDataEndRow = size;
        }
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.lastSectionRow = i6;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void getExtraDataLoginUrl(TLRPC2.TL_DiscoveryPageSetting_SM s) {
        if (s != null && !TextUtils.isEmpty(s.getTitle()) && this.extraDataReqToken == 0) {
            TLRPC2.TL_GetLoginUrl req = new TLRPC2.TL_GetLoginUrl();
            req.app_code = s.getTitle();
            this.extraDataReqToken = getConnectionsManager().sendRequest(req, new RequestDelegate(s) {
                private final /* synthetic */ TLRPC2.TL_DiscoveryPageSetting_SM f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    DiscoveryFragment.this.lambda$getExtraDataLoginUrl$2$DiscoveryFragment(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getExtraDataLoginUrl$2$DiscoveryFragment(TLRPC2.TL_DiscoveryPageSetting_SM s, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, s) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC2.TL_DiscoveryPageSetting_SM f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                DiscoveryFragment.this.lambda$null$1$DiscoveryFragment(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$DiscoveryFragment(TLRPC.TL_error error, TLObject response, TLRPC2.TL_DiscoveryPageSetting_SM s) {
        String str;
        if (error != null || !(response instanceof TLRPC2.TL_LoginUrlInfo)) {
            ToastUtils.show((int) R.string.FailedToGetLink);
            StringBuilder sb = new StringBuilder();
            sb.append("DiscoveryFragment getExtraDataLoginUrl error: ");
            if (error != null) {
                str = "errCode:" + error.code + ", errText:" + error.text;
            } else {
                str = "error is null";
            }
            sb.append(str);
            FileLog.e(sb.toString());
        } else {
            String url = ((TLRPC2.TL_LoginUrlInfo) response).url;
            if (TextUtils.isEmpty(url) || (!url.startsWith("http:") && !url.startsWith("https:"))) {
                ToastUtils.show((int) R.string.CancelLinkExpired);
            } else {
                presentFragment(DiscoveryJumpToPage.toPage(s.getTitle(), url));
            }
        }
        this.extraDataReqToken = 0;
    }

    public void setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(getClass().getSimpleName());
        if (this.extraDataReqToken != 0) {
            getConnectionsManager().cancelRequest(this.extraDataReqToken, false);
            this.extraDataReqToken = 0;
        }
        super.onDestroy();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DiscoveryFragment.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    new View(this.mContext).setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(10.0f)));
                } else if (itemViewType == 2) {
                    Banner<TLRPC2.TL_DiscoveryPageSetting_GM, BannerAdapter<TLRPC2.TL_DiscoveryPageSetting_GM, PageHolder>> banner = (Banner) holder.itemView;
                    BannerAdapter<TLRPC2.TL_DiscoveryPageSetting_GM, PageHolder> adapter = banner.getAdapter();
                    if (banner.getAdapter() == null) {
                        adapter = new BannerAdapter<TLRPC2.TL_DiscoveryPageSetting_GM, PageHolder>((List) null) {
                            public PageHolder onCreateHolder(ViewGroup parent, int viewType) {
                                ImageView iv = new ImageView(DiscoveryFragment.this.getParentActivity());
                                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                iv.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                                return new PageHolder(iv);
                            }

                            public void onBindView(PageHolder holder, TLRPC2.TL_DiscoveryPageSetting_GM data, int position, int size) {
                                ImageView iv = (ImageView) holder.itemView;
                                GlideUtils.getInstance().load(data.getPic(), iv.getContext(), iv, (int) R.mipmap.banner_discovery1);
                            }
                        };
                        banner.setAdapter(adapter);
                    }
                    banner.setOnBannerListener(new OnBannerListener() {
                        public final void OnBannerClick(Object obj, int i) {
                            DiscoveryFragment.ListAdapter.this.lambda$onBindViewHolder$0$DiscoveryFragment$ListAdapter((TLRPC2.TL_DiscoveryPageSetting_GM) obj, i);
                        }
                    });
                    if (!(DiscoveryFragment.this.delegate == null || DiscoveryFragment.this.delegate.getDiscoveryPageData() == null)) {
                        adapter.setDatas(DiscoveryFragment.this.delegate.getDiscoveryPageData().getG());
                    }
                    adapter.notifyDataSetChanged();
                } else if (itemViewType == 3) {
                    new View(this.mContext).setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(15.0f)));
                }
            } else if (position == DiscoveryFragment.this.friendsHubRow) {
                ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("FriendHub", R.string.FriendHub), (int) R.drawable.fmt_discoveryv2_friends_hub, (int) R.mipmap.icon_arrow_right, false);
            } else if (position == DiscoveryFragment.this.scanRow) {
                ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("Scan", R.string.Scan), (int) R.drawable.fmt_discoveryv2_scan, (int) R.mipmap.icon_arrow_right, false);
            } else if (position == DiscoveryFragment.this.nearbyRow) {
                ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("PeopleNearby", R.string.PeopleNearby), (int) R.drawable.fmt_discoveryv2_nearby, (int) R.mipmap.icon_arrow_right, false);
            } else if (position == DiscoveryFragment.this.miniProgramRow) {
                ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("MiniProgram", R.string.MiniProgram), (int) R.mipmap.fmt_discovery_mini_program, (int) R.mipmap.icon_arrow_right, false);
            } else if (position == DiscoveryFragment.this.gameCenterRow) {
                ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("GameCenter", R.string.GameCenter), (int) R.mipmap.fmt_discovery_games, (int) R.mipmap.icon_arrow_right, false);
            } else if (position == DiscoveryFragment.this.album) {
                ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("MyAlbum", R.string.MyAlbum), (int) R.drawable.fmt_discoveryv2_album, (int) R.mipmap.icon_arrow_right, false);
            } else if (position == DiscoveryFragment.this.recommendChannel) {
                ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("HotChannelRecommend", R.string.HotChannelRecommend), (int) R.mipmap.ic_fire, (int) R.mipmap.icon_arrow_right, false);
            } else if (position > DiscoveryFragment.this.extraDataStartRow && position < DiscoveryFragment.this.extraDataEndRow) {
                int cpo = (position - DiscoveryFragment.this.extraDataStartRow) - 1;
                if (DiscoveryFragment.this.delegate != null && DiscoveryFragment.this.delegate.getDiscoveryPageData() != null && cpo >= 0 && cpo < DiscoveryFragment.this.delegate.getDiscoveryPageData().getS().size()) {
                    try {
                        TLRPC2.TL_DiscoveryPageSetting_SM item = DiscoveryFragment.this.delegate.getDiscoveryPageData().getS().get(cpo);
                        ((IndexTextCell) holder.itemView).setTextAndIcon(item.getTitle(), item.getLogo(), AndroidUtilities.dp(7.0f), 0, cpo == DiscoveryFragment.this.extraDataEndRow - 1);
                    } catch (Exception e) {
                    }
                }
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$DiscoveryFragment$ListAdapter(TLRPC2.TL_DiscoveryPageSetting_GM data, int position1) {
            DiscoveryFragment.this.presentFragment(new WebviewActivity(data.getUrl(), (String) null));
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DiscoveryFragment.this.friendsHubRow || position == DiscoveryFragment.this.scanRow || position == DiscoveryFragment.this.nearbyRow || position == DiscoveryFragment.this.gameCenterRow || position == DiscoveryFragment.this.miniProgramRow || position == DiscoveryFragment.this.album || position == DiscoveryFragment.this.recommendChannel || (DiscoveryFragment.this.extraDataStartRow > 0 && position > DiscoveryFragment.this.extraDataStartRow && position < DiscoveryFragment.this.extraDataEndRow);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1 || viewType == 3) {
                View view2 = viewType == 1 ? new ShadowSectionCell(this.mContext) : new ShadowSectionCell(this.mContext, 15);
                view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                view = view2;
            } else if (viewType == 2) {
                Banner banner = new Banner(DiscoveryFragment.this.getContext());
                banner.setBannerRound((float) AndroidUtilities.dp(10.0f));
                banner.setLoopTime(6000);
                int padding = AndroidUtilities.dp(2.0f);
                RectangleIndicator indicator = new RectangleIndicator(DiscoveryFragment.this.getContext());
                indicator.setPadding(padding, padding, padding, padding);
                indicator.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), AndroidUtilities.alphaColor(0.5f, -1)));
                banner.setIndicator(indicator).setIndicatorNormalWidth((float) AndroidUtilities.dp(6.0f)).setIndicatorHeight((float) AndroidUtilities.dp(6.0f)).setIndicatorSpace((float) AndroidUtilities.dp(5.0f)).setIndicatorSelectedWidth((float) AndroidUtilities.dp(15.0f)).setIndicatorRadius((float) AndroidUtilities.dp(6.0f)).setIndicatorSelectedColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)).setIndicatorNormalColor(-1);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, (int) ((((float) ScreenUtils.getScreenWidth()) - ((float) AndroidUtilities.dp(30.0f))) / 3.45f));
                lp.leftMargin = AndroidUtilities.dp(15.0f);
                lp.rightMargin = AndroidUtilities.dp(15.0f);
                banner.setLayoutParams(lp);
                banner.setClipToPadding(false);
                banner.setClipChildren(false);
                view = banner;
            } else {
                View indexTextCell = new IndexTextCell(this.mContext);
                indexTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                indexTextCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                view = indexTextCell;
            }
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == DiscoveryFragment.this.friendsHubEmptyRow || position == DiscoveryFragment.this.nearbyEmptyRow || position == DiscoveryFragment.this.gameCenterEmptyRow || position == DiscoveryFragment.this.lastSectionRow || position == DiscoveryFragment.this.startSectionRow || position == DiscoveryFragment.this.albumEmptyRow || position == DiscoveryFragment.this.recommendChannelEmptyRow || position == DiscoveryFragment.this.extraDataStartRow || position == DiscoveryFragment.this.extraDataEndRow) {
                return 1;
            }
            if (position == DiscoveryFragment.this.bannerRow) {
                return 2;
            }
            if (position == DiscoveryFragment.this.bannerStartRow || position == DiscoveryFragment.this.bannerEndRow) {
                return 3;
            }
            return 0;
        }
    }
}
