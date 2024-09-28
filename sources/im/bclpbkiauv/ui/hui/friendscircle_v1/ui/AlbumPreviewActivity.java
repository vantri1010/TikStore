package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.baidu.mapapi.UIMsg;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.UrlInfoBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcCommonPresenter;
import com.bjz.comm.net.utils.JsonCreateUtils;
import com.bjz.comm.net.utils.RxHelper;
import com.preview.BaseFragmentPagerAdapter;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.dialogs.FcDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.OnPreviewClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.OnPreviewLongClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.FcDialogUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlbumPreviewActivity extends CommFcActivity implements BaseFcContract.IFcCommView {
    private static final String TAG = AlbumPreviewActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public int currentIndex = 0;
    private FragmentManager fragmentManager;
    private LinearLayout ll_title;
    /* access modifiers changed from: private */
    public MyFragmentPagerAdapter mAdapter;
    /* access modifiers changed from: private */
    public FcCommonPresenter mFcCommonPresenter;
    /* access modifiers changed from: private */
    public OnDeleteDelegate mOnDeleteDelegate;
    /* access modifiers changed from: private */
    public ViewPager mVpContent;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public TextView tv_title;
    /* access modifiers changed from: private */
    public List<UrlInfoBean> urlInfoBeanList = new ArrayList();

    public interface OnDeleteDelegate {
        void onDelete(long j, int i);
    }

    public AlbumPreviewActivity(List<UrlInfoBean> urlInfoBeanList2, int currentIndex2) {
        if (urlInfoBeanList2 != null && urlInfoBeanList2.size() > 0) {
            this.currentIndex = currentIndex2;
            this.urlInfoBeanList.clear();
            this.urlInfoBeanList.addAll(urlInfoBeanList2);
        }
    }

    public void setOnDeleteDelegate(OnDeleteDelegate onDeleteDelegate) {
        this.mOnDeleteDelegate = onDeleteDelegate;
    }

    public boolean onFragmentCreate() {
        this.mFcCommonPresenter = new FcCommonPresenter(this);
        return super.onFragmentCreate();
    }

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_albums_preview;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        List<UrlInfoBean> list;
        UrlInfoBean bean;
        this.actionBar.setVisibility(8);
        final FrameLayout mRootView = (FrameLayout) this.fragmentView.findViewById(R.id.root_view);
        this.ll_title = (LinearLayout) this.fragmentView.findViewById(R.id.ll_title);
        this.tv_title = (TextView) this.fragmentView.findViewById(R.id.tv_title);
        this.fragmentView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mRootView.removeView(AlbumPreviewActivity.this.mVpContent);
                ViewPager unused = AlbumPreviewActivity.this.mVpContent = null;
                ViewParent parent = mRootView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(mRootView);
                }
                AlbumPreviewActivity.this.finishFragment();
            }
        });
        this.mVpContent = (ViewPager) this.fragmentView.findViewById(R.id.vp_preview_fragment);
        this.fragmentManager = getParentActivity().getSupportFragmentManager();
        ViewPager viewPager = this.mVpContent;
        viewPager.setId(viewPager.hashCode());
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(this.fragmentManager, this.urlInfoBeanList.size());
        this.mAdapter = myFragmentPagerAdapter;
        myFragmentPagerAdapter.setOnUpdateFragmentDataListener(new MyFragmentPagerAdapter.OnUpdateFragmentDataListener() {
            public void onUpdate(AlbumPreviewFragment fragment, int position) {
                fragment.setData((UrlInfoBean) AlbumPreviewActivity.this.urlInfoBeanList.get(position), position);
                fragment.setOnLongClickListener(new OnPreviewLongClickListener() {
                    public void onLongClick(UrlInfoBean urlInfoBean, int position) {
                        AlbumPreviewActivity.this.showMenuDialog(urlInfoBean, position);
                    }
                });
                fragment.setOnPreviewClickListener(new OnPreviewClickListener() {
                    public void onClick() {
                        AlbumPreviewActivity.this.controlTitleVisible();
                    }
                });
            }
        });
        this.mVpContent.setAdapter(this.mAdapter);
        this.mVpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                UrlInfoBean bean;
                if (!(AlbumPreviewActivity.this.urlInfoBeanList == null || position >= AlbumPreviewActivity.this.urlInfoBeanList.size() || AlbumPreviewActivity.this.tv_title == null || (bean = (UrlInfoBean) AlbumPreviewActivity.this.urlInfoBeanList.get(position)) == null)) {
                    long createTime = bean.getCreateTime();
                    if (createTime > 0) {
                        AlbumPreviewActivity.this.tv_title.setText(TimeUtils.fcFormat2Date(createTime));
                    }
                }
                int unused = AlbumPreviewActivity.this.currentIndex = position;
            }

            public void onPageScrollStateChanged(int position) {
            }
        });
        int i = this.currentIndex;
        if (i == 0 && (list = this.urlInfoBeanList) != null && i < list.size() && (bean = this.urlInfoBeanList.get(this.currentIndex)) != null) {
            long createTime = bean.getCreateTime();
            if (createTime > 0) {
                this.tv_title.setText(TimeUtils.fcFormat2Date(createTime));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initData() {
        this.mVpContent.setCurrentItem(this.currentIndex);
    }

    /* access modifiers changed from: protected */
    public void showMenuDialog(UrlInfoBean urlInfoBean, int clickPosition) {
        int urlType = urlInfoBean.getURLType();
        String tips = LocaleController.getString("save_pic", R.string.save_pic);
        String suffix = ".jpg";
        if (urlType == 1) {
            tips = LocaleController.getString("save_pic", R.string.save_pic);
            suffix = ".jpg";
        } else if (urlType == 2) {
            tips = LocaleController.getString("save_video", R.string.save_video);
            suffix = ".mp4";
        } else if (urlType == 3) {
            tips = LocaleController.getString("save_pic", R.string.save_pic);
            suffix = ".gif";
        }
        List<String> list = new ArrayList<>();
        list.add(tips);
        list.add(LocaleController.getString("Delete", R.string.Delete));
        final String finalSuffix = suffix;
        final UrlInfoBean urlInfoBean2 = urlInfoBean;
        final int i = clickPosition;
        List<String> list2 = list;
        new FcCommMenuDialog((Activity) getParentActivity(), list2, (List<Integer>) null, new int[]{Theme.getColor(Theme.key_dialogTextBlack), ContextCompat.getColor(this.mContext, R.color.color_item_menu_red_f74c31)}, (FcCommMenuDialog.RecyclerviewItemClickCallBack) new FcCommMenuDialog.RecyclerviewItemClickCallBack() {
            public void onRecyclerviewItemClick(int position) {
                if (position == 0) {
                    AlbumPreviewActivity.this.showProgress();
                    FcCommonPresenter access$600 = AlbumPreviewActivity.this.mFcCommonPresenter;
                    String url = urlInfoBean2.getURL();
                    String absolutePath = AndroidUtilities.getAlbumDir(false).getAbsolutePath();
                    access$600.downloadFile(url, absolutePath, TimeUtils.getCurrentTime() + finalSuffix);
                } else if (position == 1) {
                    AlbumPreviewActivity.this.showDeleteDialog(urlInfoBean2, i);
                }
            }
        }, 1).show();
    }

    /* access modifiers changed from: private */
    public void showDeleteDialog(UrlInfoBean urlInfoBean, int position) {
        boolean hasSameGroup;
        if (urlInfoBean != null) {
            long forumID = urlInfoBean.getForumID();
            int urlType = urlInfoBean.getURLType();
            ArrayList<UrlInfoBean> deleteList = new ArrayList<>();
            if (urlType == 1) {
                for (UrlInfoBean bean : this.urlInfoBeanList) {
                    if (bean != null && bean.getForumID() == forumID) {
                        deleteList.add(bean);
                    }
                }
            }
            if (deleteList.size() > 1) {
                hasSameGroup = true;
            } else {
                hasSameGroup = false;
            }
            FcDialogUtil.showDeleteAlbumItemDialog(this, urlType, hasSameGroup, new FcDialog.OnConfirmClickListener(forumID, deleteList, position) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void onClick(View view) {
                    AlbumPreviewActivity.this.lambda$showDeleteDialog$0$AlbumPreviewActivity(this.f$1, this.f$2, this.f$3, view);
                }
            }, (DialogInterface.OnDismissListener) null);
        }
    }

    public /* synthetic */ void lambda$showDeleteDialog$0$AlbumPreviewActivity(long forumID, ArrayList deleteList, int position, View dialog) {
        doDeleteFc(forumID, deleteList, position);
    }

    private void doDeleteFc(long forumID, ArrayList<UrlInfoBean> deleteList, int position) {
        showProgress();
        final ArrayList<UrlInfoBean> arrayList = deleteList;
        final long j = forumID;
        final int i = position;
        RxHelper.getInstance().sendRequestNoData(TAG, ApiFactory.getInstance().getApiMomentForum().doDeleteItem(JsonCreateUtils.build().addParam("ForumID", Long.valueOf(forumID)).getHttpBody()), new Consumer<BResponseNoData>() {
            public void accept(BResponseNoData responseNoData) throws Exception {
                AlbumPreviewActivity.this.hideProgress();
                if (responseNoData != null) {
                    if (responseNoData.isState()) {
                        ArrayList arrayList = arrayList;
                        if (arrayList == null || arrayList.size() <= 0) {
                            AlbumPreviewActivity.this.urlInfoBeanList.remove(i);
                            AlbumPreviewActivity.this.mAdapter.setData(AlbumPreviewActivity.this.urlInfoBeanList.size());
                            if (AlbumPreviewActivity.this.mOnDeleteDelegate != null) {
                                AlbumPreviewActivity.this.mOnDeleteDelegate.onDelete(j, i);
                            }
                            if (AlbumPreviewActivity.this.urlInfoBeanList.size() == 0) {
                                AlbumPreviewActivity.this.finishFragment();
                            }
                        } else {
                            Iterator<UrlInfoBean> iterator = AlbumPreviewActivity.this.urlInfoBeanList.iterator();
                            while (iterator.hasNext()) {
                                if (iterator.next().getForumID() == j) {
                                    iterator.remove();
                                }
                            }
                            AlbumPreviewActivity.this.mAdapter.setData(AlbumPreviewActivity.this.urlInfoBeanList.size());
                            if (AlbumPreviewActivity.this.mOnDeleteDelegate != null) {
                                AlbumPreviewActivity.this.mOnDeleteDelegate.onDelete(j, i);
                            }
                            if (AlbumPreviewActivity.this.urlInfoBeanList.size() == 0) {
                                AlbumPreviewActivity.this.finishFragment();
                            }
                        }
                    }
                    AlbumPreviewActivity.this.showTipsDialog(LocaleController.getString(R.string.deleted_succ));
                    return;
                }
                FcToastUtils.show((int) R.string.deleted_fail);
            }
        }, new Consumer<Throwable>() {
            public void accept(Throwable throwable) throws Exception {
                AlbumPreviewActivity.this.hideProgress();
                FcToastUtils.show((CharSequence) RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    /* access modifiers changed from: private */
    public void controlTitleVisible() {
        LinearLayout linearLayout = this.ll_title;
        if (linearLayout == null) {
            return;
        }
        if (linearLayout.getVisibility() == 0) {
            hideTitle();
        } else {
            showTitle();
        }
    }

    public void hideTitle() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.ll_title, "translationY", new float[]{0.0f, (float) (-ActionBar.getCurrentActionBarHeight())});
        animator.setDuration(300);
        animator.start();
        this.ll_title.setVisibility(8);
    }

    public void showTitle() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.ll_title, "translationY", new float[]{(float) (-ActionBar.getCurrentActionBarHeight()), 0.0f});
        animator.setDuration(300);
        animator.start();
        this.ll_title.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void showProgress() {
        if (this.progressDialog == null) {
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
        }
        if (!this.progressDialog.isShowing()) {
            this.progressDialog.show();
        }
    }

    /* access modifiers changed from: private */
    public void hideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        List<UrlInfoBean> list = this.urlInfoBeanList;
        if (list != null) {
            list.clear();
            this.urlInfoBeanList = null;
        }
        VideoPlayerManager.getInstance().release();
        FcCommonPresenter fcCommonPresenter = this.mFcCommonPresenter;
        if (fcCommonPresenter != null) {
            fcCommonPresenter.unSubscribeTask();
        }
    }

    public void onDownloadFileSucc(File file) {
        hideProgress();
        AndroidUtilities.addMediaToGallery(Uri.fromFile(file));
        showTipsDialog(LocaleController.getString(R.string.save_album_success));
    }

    /* access modifiers changed from: private */
    public void showTipsDialog(String content) {
        if (!TextUtils.isEmpty(content)) {
            try {
                final WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
                WindowManager.LayoutParams para = new WindowManager.LayoutParams();
                para.height = AndroidUtilities.dp(133.0f);
                para.width = AndroidUtilities.dp(133.0f);
                para.flags = 24;
                para.format = -2;
                para.type = UIMsg.m_AppUI.MSG_APP_VERSION_FORCE;
                para.gravity = 17;
                final FrameLayout rootView = new FrameLayout(this.mContext);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(0);
                gradientDrawable.setCornerRadius(11.0f);
                gradientDrawable.setColor(Color.parseColor("#8C000000"));
                rootView.setBackground(gradientDrawable);
                TextView tv_tips = new TextView(this.mContext);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
                layoutParams.gravity = 17;
                tv_tips.setLayoutParams(layoutParams);
                tv_tips.setGravity(17);
                tv_tips.setTextAlignment(4);
                tv_tips.setTextColor(-1);
                tv_tips.setTextSize(16.0f);
                tv_tips.setText(content);
                Drawable drawable = this.mContext.getResources().getDrawable(R.mipmap.ic_album_tips_success);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv_tips.setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
                tv_tips.setCompoundDrawablePadding(AndroidUtilities.dp(20.0f));
                rootView.addView(tv_tips);
                wm.addView(rootView, para);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        wm.removeView(rootView);
                    }
                }, 1800);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onDownloadFileError(String msg) {
        hideProgress();
        FcToastUtils.show((CharSequence) LocaleController.getString(R.string.save_album_error));
    }

    public void onError(String msg) {
    }

    private static class MyFragmentPagerAdapter extends BaseFragmentPagerAdapter {
        private OnUpdateFragmentDataListener mOnUpdateFragmentDataListener;
        private int size;

        public interface OnUpdateFragmentDataListener {
            void onUpdate(AlbumPreviewFragment albumPreviewFragment, int i);
        }

        public MyFragmentPagerAdapter(FragmentManager fm, int size2) {
            super(fm);
            this.size = size2;
        }

        public Fragment getItem(int position) {
            return new AlbumPreviewFragment();
        }

        public Object instantiateItem(ViewGroup container, int position) {
            OnUpdateFragmentDataListener onUpdateFragmentDataListener;
            Object item = super.instantiateItem(container, position);
            if ((item instanceof AlbumPreviewFragment) && (onUpdateFragmentDataListener = this.mOnUpdateFragmentDataListener) != null) {
                onUpdateFragmentDataListener.onUpdate((AlbumPreviewFragment) item, position);
            }
            return item;
        }

        public boolean dataIsChange(Object object) {
            return true;
        }

        public int getCount() {
            return this.size;
        }

        public void setOnUpdateFragmentDataListener(OnUpdateFragmentDataListener onUpdateFragmentDataListener) {
            this.mOnUpdateFragmentDataListener = onUpdateFragmentDataListener;
        }

        public void setData(int size2) {
            this.size = size2;
            notifyDataSetChanged();
        }
    }
}
