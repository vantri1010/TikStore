package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.bjz.comm.net.bean.UrlInfoBean;
import com.preview.photoview.PhotoView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.OnPreviewClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.OnPreviewLongClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.BackPressedMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.DurationMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.Message;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.UIStateMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hviews.dialogs.Util;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AlbumPreviewFragment extends BaseFmts implements Observer {
    private static final int TYPE_IMG = 0;
    private static final int TYPE_VIDEO = 1;
    /* access modifiers changed from: private */
    public int currentIndex = -1;
    private int hashCode = 0;
    private boolean isResume = false;
    private Context mContext;
    private long mDelayShowProgressTime = 100;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private String mImgUrl;
    /* access modifiers changed from: private */
    public ProgressBar mLoading;
    /* access modifiers changed from: private */
    public OnPreviewClickListener mOnPreviewClickListener;
    /* access modifiers changed from: private */
    public OnPreviewLongClickListener mOnPreviewLongClickListener;
    /* access modifiers changed from: private */
    public PhotoView mPhotoView;
    private FrameLayout mRoot;
    /* access modifiers changed from: private */
    public ScheduledFuture<?> mSchedule;
    private ScheduledExecutorService mService;
    private FrameLayout mVideoContainer;
    private String mVideoUrl;
    private TextureView textureView;
    /* access modifiers changed from: private */
    public UrlInfoBean urlInfoBean;
    private int urlType = 0;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            initData();
        }
    }

    private void initData() {
        this.mService = Executors.newScheduledThreadPool(1);
        this.mHandler = new Handler();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.fragmentView = inflater.inflate(R.layout.fragment_album_preview, (ViewGroup) null);
            FrameLayout frameLayout = (FrameLayout) this.fragmentView.findViewById(R.id.root);
            this.mRoot = frameLayout;
            frameLayout.setFocusableInTouchMode(true);
            this.mRoot.requestFocus();
            this.mVideoContainer = (FrameLayout) this.fragmentView.findViewById(R.id.tv_video_container);
            this.mPhotoView = (PhotoView) this.fragmentView.findViewById(R.id.photoView);
            this.mLoading = (ProgressBar) this.fragmentView.findViewById(R.id.loading);
            initListener();
            onLoadData();
        }
        return this.fragmentView;
    }

    public void onDestroyView() {
        super.onDestroyView();
        VideoPlayerManager.getInstance().removeObserver(this);
        if (this.urlType == 1 && VideoPlayerManager.getInstance().isViewPlaying(this.hashCode)) {
            VideoPlayerManager.getInstance().stop();
        }
        this.hashCode = 0;
        this.textureView = null;
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        super.lazyLoadData();
        onVisible();
    }

    public void onResume() {
        super.onResume();
        if (!isFirstTimeInThisPage() && !this.isResume && isFragmentVisible()) {
            this.isResume = true;
            onVisible();
        }
    }

    public void onPause() {
        super.onPause();
        if (!isFirstTimeInThisPage() && this.isResume && isFragmentVisible()) {
            this.isResume = false;
            onInvisible();
        }
    }

    /* access modifiers changed from: protected */
    public void onVisible() {
        super.onVisible();
        if (this.urlType == 1) {
            if (this.textureView == null || this.hashCode == 0) {
                TextureView createTextureView = createTextureView();
                this.textureView = createTextureView;
                this.hashCode = createTextureView.hashCode();
                ViewGroup.LayoutParams layoutParams = this.textureView.getLayoutParams();
                int mVideoHeight = layoutParams.height;
                UrlInfoBean urlInfoBean2 = this.urlInfoBean;
                if (urlInfoBean2 != null) {
                    float ratio = urlInfoBean2.getVideoWidth() / this.urlInfoBean.getVideoHeight();
                    int screenWidth = Util.getScreenWidth(getParentActivity());
                    if (ratio > 1.0f) {
                        mVideoHeight = (int) (((float) screenWidth) / ratio);
                    }
                }
                layoutParams.height = mVideoHeight;
                FrameLayout frameLayout = this.mVideoContainer;
                if (frameLayout != null) {
                    frameLayout.addView(this.textureView, layoutParams);
                }
            }
            if (!VideoPlayerManager.getInstance().isViewPlaying(this.hashCode)) {
                VideoPlayerManager.getInstance().stopWithKeepView();
                if (!TextUtils.isEmpty(this.mVideoUrl)) {
                    VideoPlayerManager.getInstance().start(this.mVideoUrl, this.hashCode);
                    VideoPlayerManager.getInstance().setTextureView(this.textureView);
                }
            } else {
                VideoPlayerManager.getInstance().resume();
            }
            PhotoView photoView = this.mPhotoView;
            if (photoView != null) {
                photoView.setVisibility(8);
                return;
            }
            return;
        }
        VideoPlayerManager.getInstance().stopWithKeepView();
    }

    /* access modifiers changed from: protected */
    public void onInvisible() {
        super.onInvisible();
        PhotoView photoView = this.mPhotoView;
        if (photoView != null) {
            photoView.setScale(1.0f);
        }
        getParentActivity().getWindow().clearFlags(128);
        if (this.urlType == 1) {
            PhotoView photoView2 = this.mPhotoView;
            if (photoView2 != null) {
                photoView2.setVisibility(0);
            }
            if (VideoPlayerManager.getInstance().isViewPlaying(this.hashCode)) {
                VideoPlayerManager.getInstance().stopWithKeepView();
            }
        }
    }

    private void initListener() {
        this.mVideoContainer.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (AlbumPreviewFragment.this.mOnPreviewLongClickListener == null) {
                    return false;
                }
                AlbumPreviewFragment.this.mOnPreviewLongClickListener.onLongClick(AlbumPreviewFragment.this.urlInfoBean, AlbumPreviewFragment.this.currentIndex);
                return false;
            }
        });
        this.mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (AlbumPreviewFragment.this.mOnPreviewLongClickListener == null) {
                    return true;
                }
                AlbumPreviewFragment.this.mOnPreviewLongClickListener.onLongClick(AlbumPreviewFragment.this.urlInfoBean, AlbumPreviewFragment.this.currentIndex);
                return true;
            }
        });
        this.mVideoContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (AlbumPreviewFragment.this.mOnPreviewClickListener != null) {
                    AlbumPreviewFragment.this.mOnPreviewClickListener.onClick();
                }
            }
        });
        this.mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (AlbumPreviewFragment.this.mOnPreviewClickListener != null) {
                    AlbumPreviewFragment.this.mOnPreviewClickListener.onClick();
                }
            }
        });
    }

    private void onLoadData() {
        if (!TextUtils.isEmpty(this.mImgUrl)) {
            if (this.urlType == 1) {
                this.mVideoContainer.setVisibility(0);
                VideoPlayerManager.getInstance().addObserver(this);
            } else {
                this.mVideoContainer.setVisibility(8);
            }
            GlideUtils.getInstance().loadNOCentercrop(this.mImgUrl, this.mContext, this.mPhotoView, 0);
            if (this.urlType == 0) {
                checkLoadResult();
            }
        }
    }

    private void checkLoadResult() {
        long j = this.mDelayShowProgressTime;
        int i = 8;
        if (j < 0) {
            this.mLoading.setVisibility(8);
            return;
        }
        ProgressBar progressBar = this.mLoading;
        if (j == 0) {
            i = 0;
        }
        progressBar.setVisibility(i);
        ScheduledExecutorService scheduledExecutorService = this.mService;
        AnonymousClass5 r7 = new Runnable() {
            public void run() {
                if (AlbumPreviewFragment.this.mPhotoView.getDrawable() != null) {
                    AlbumPreviewFragment.this.mHandler.post(new Runnable() {
                        public void run() {
                            AlbumPreviewFragment.this.mLoading.setVisibility(8);
                        }
                    });
                    AlbumPreviewFragment.this.mSchedule.cancel(true);
                } else if (AlbumPreviewFragment.this.mLoading.getVisibility() == 8) {
                    AlbumPreviewFragment.this.mHandler.post(new Runnable() {
                        public void run() {
                            AlbumPreviewFragment.this.mLoading.setVisibility(0);
                        }
                    });
                }
            }
        };
        long j2 = this.mDelayShowProgressTime;
        if (j2 == 0) {
            j2 = 100;
        }
        this.mSchedule = scheduledExecutorService.scheduleWithFixedDelay(r7, j2, 100, TimeUnit.MILLISECONDS);
    }

    public void setData(UrlInfoBean urlInfoBean2, int currentIndex2) {
        if (urlInfoBean2 != null) {
            this.urlInfoBean = urlInfoBean2;
            this.currentIndex = currentIndex2;
            this.mImgUrl = urlInfoBean2.getURLType() == 2 ? urlInfoBean2.getThum() : urlInfoBean2.getURL();
            this.mVideoUrl = urlInfoBean2.getURL();
            this.urlType = urlInfoBean2.getURLType() == 2 ? 1 : 0;
        }
    }

    public TextureView createTextureView() {
        TextureView textureView2 = newTextureView();
        textureView2.setLayoutParams(new FrameLayout.LayoutParams(-1, -1, 17));
        return textureView2;
    }

    /* access modifiers changed from: protected */
    public TextureView newTextureView() {
        return new TextureView(getContext());
    }

    public void setOnLongClickListener(OnPreviewLongClickListener onPreviewLongClickListener) {
        this.mOnPreviewLongClickListener = onPreviewLongClickListener;
    }

    public void setOnPreviewClickListener(OnPreviewClickListener onPreviewClickListener) {
        this.mOnPreviewClickListener = onPreviewClickListener;
    }

    public void update(Observable o, final Object arg) {
        if (getContext() != null && (arg instanceof Message) && this.hashCode == ((Message) arg).getHash() && this.mVideoUrl.equals(((Message) arg).getVideoUrl()) && !(arg instanceof DurationMessage) && !(arg instanceof BackPressedMessage) && (arg instanceof UIStateMessage)) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    AlbumPreviewFragment.this.onChangeUIState(((UIStateMessage) arg).getState());
                }
            });
        }
    }

    public void onChangeUIState(int state) {
        if (state != 0) {
            if (state != 1) {
                if (!(state == 2 || state == 4 || state == 5)) {
                    if (state != 6) {
                        throw new IllegalStateException("Illegal Play State:" + state);
                    }
                }
            }
            this.mLoading.setVisibility(0);
            return;
        }
        this.mLoading.setVisibility(8);
    }
}
