package im.bclpbkiauv.ui.hui.friendscircle_v1.player.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.BackPressedMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.DurationMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.Message;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.UIStateMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.FullScreenExoMultiPlayer;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.state.ScreenViewState;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.utils.Utils;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AbsMultiVideoPlayerView extends RelativeLayout implements IVideoPlayerView, View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, AudioManager.OnAudioFocusChangeListener, Observer {
    protected static final float BRIGHTNESS_STEP = 0.08f;
    protected static final float MAX_BRIGHTNESS = 1.0f;
    protected static final int PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    protected static final int PROGRESS_UPDATE_INTERNAL = 300;
    protected static final int TOTAL_PERCENT = 100;
    protected static final int VIDEO_SEEK_STEP = 2000;
    protected static final int VOLUME_STEP = 1;
    protected float Ratio = 0.0f;
    protected boolean blnParticular = false;
    FullScreenExoMultiPlayer dialog;
    protected boolean isAutoPlay = false;
    protected OnClickVideoContainerListener listener;
    protected AudioManager mAudioManager;
    protected int mAutoDismissTime = 2000;
    protected ProgressBar mBottomProgressBar;
    protected float mBrightnessDistance = 0.0f;
    protected int mCurrentGestureState = 0;
    protected int mCurrentScreenState = 1;
    protected int mCurrentState = 0;
    protected Timer mDismissControllerViewTimer;
    protected DismissControllerViewTimerTask mDismissControllerViewTimerTask;
    protected int mDuration = 0;
    protected final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    protected ViewStub mFullScreenViewStub;
    protected int mGestureSeekToPosition = -1;
    protected boolean mIsTouchControllerView = false;
    protected int mMaxVolume;
    protected int mOldIndex = 0;
    protected ViewGroup mOldParent;
    protected ScheduledFuture<?> mScheduleFuture;
    protected int mScreenHeight;
    protected int mScreenWidth;
    protected boolean mShowNormalStateTitleView = true;
    protected int mSmallWindowHeight;
    protected int mSmallWindowWidth;
    protected boolean mToggleFullScreen = false;
    protected float mTouchDownX;
    protected float mTouchDownY;
    protected int mTouchSlop = 0;
    protected final Runnable mUpdateProgressTask = new Runnable() {
        public void run() {
            AbsMultiVideoPlayerView.this.updateProgress(AbsMultiVideoPlayerView.this.videoPlayerMgr.getCurrentPosition());
        }
    };
    protected ProgressBar mVideoBrightnessProgress;
    protected LinearLayout mVideoBrightnessView;
    protected ProgressBar mVideoChangeProgressBar;
    protected TextView mVideoChangeProgressCurrPro;
    protected ImageView mVideoChangeProgressIcon;
    protected TextView mVideoChangeProgressTotal;
    protected View mVideoChangeProgressView;
    protected View mVideoControllerView;
    protected View mVideoErrorView;
    protected ImageView mVideoFullScreenBackView;
    protected ImageView mVideoFullScreenView;
    protected View mVideoHeaderViewContainer;
    protected int mVideoHeight;
    protected ProgressBar mVideoLoadingBar;
    protected SeekBar mVideoPlaySeekBar;
    protected TextView mVideoPlayTimeView;
    public ImageView mVideoPlayView;
    protected ImageView mVideoSmallWindowBackView;
    protected FrameLayout mVideoTextureViewContainer;
    protected ImageView mVideoThumbView;
    protected CharSequence mVideoTitle;
    protected TextView mVideoTitleView;
    protected TextView mVideoTotalTimeView;
    protected String mVideoUrl;
    protected ProgressBar mVideoVolumeProgress;
    protected LinearLayout mVideoVolumeView;
    protected int mVideoWidth;
    public int mViewHash;
    protected float mVolumeDistance = 0.0f;
    protected VideoPlayerManager videoPlayerMgr;
    protected IVideoPlayerState videoPlayerState;

    public interface OnClickVideoContainerListener {
        void onClickView();
    }

    public int getCurrentState() {
        return this.mCurrentState;
    }

    public int getCurrentScreenState() {
        return this.mCurrentScreenState;
    }

    public void setVideoPlayerMgr(VideoPlayerManager videoPlayerMgr2) {
        this.videoPlayerMgr = videoPlayerMgr2;
    }

    public VideoPlayerManager getVideoPlayerMgr() {
        return this.videoPlayerMgr;
    }

    public void setRatio(float Ratio2) {
        this.Ratio = Ratio2;
    }

    public int getmDuration() {
        return this.mDuration;
    }

    public void setBlnParticular(boolean blnParticular2) {
        this.blnParticular = blnParticular2;
    }

    public AbsMultiVideoPlayerView(Context context) {
        super(context);
        initView(context);
    }

    public AbsMultiVideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AbsMultiVideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public AbsMultiVideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    /* access modifiers changed from: protected */
    public int getPlayerLayoutId() {
        return R.layout.vp_layout_videoplayer;
    }

    /* access modifiers changed from: protected */
    public void initView(Context context) {
        this.mViewHash = toString().hashCode();
        this.mScreenWidth = Utils.getWindowWidth(context);
        this.mScreenHeight = Utils.getWindowHeight(context);
        int i = this.mScreenWidth / 2;
        this.mSmallWindowWidth = i;
        this.mSmallWindowHeight = (int) ((((((float) i) * 1.0f) / 16.0f) * 9.0f) + 0.5f);
        inflate(context, getPlayerLayoutId(), this);
        setDescendantFocusability(393216);
        setBackgroundColor(-16777216);
        findAndBindView();
    }

    /* access modifiers changed from: protected */
    public void findAndBindView() {
        this.mVideoTextureViewContainer = (FrameLayout) findViewById(R.id.vp_video_surface_container);
        ImageView imageView = (ImageView) findViewById(R.id.vp_video_thumb);
        this.mVideoThumbView = imageView;
        imageView.setBackground((Drawable) null);
        this.mBottomProgressBar = (ProgressBar) findViewById(R.id.vp_video_bottom_progress);
        this.mVideoLoadingBar = (ProgressBar) findViewById(R.id.vp_video_loading);
        this.mVideoPlayView = (ImageView) findViewById(R.id.vp_video_play);
        this.mVideoErrorView = findViewById(R.id.vp_video_play_error_view);
        this.mVideoControllerView = findViewById(R.id.vp_video_bottom_controller_view);
        this.mVideoPlayTimeView = (TextView) findViewById(R.id.vp_video_play_time);
        this.mVideoTotalTimeView = (TextView) findViewById(R.id.vp_video_total_time);
        this.mVideoPlaySeekBar = (SeekBar) findViewById(R.id.vp_video_seek_progress);
        this.mVideoFullScreenView = (ImageView) findViewById(R.id.vp_video_fullscreen);
        this.mVideoSmallWindowBackView = (ImageView) findViewById(R.id.vp_video_small_window_back);
        this.mVideoHeaderViewContainer = findViewById(R.id.vp_video_header_view);
        this.mVideoFullScreenBackView = (ImageView) findViewById(R.id.vp_video_fullScreen_back);
        this.mVideoTitleView = (TextView) findViewById(R.id.vp_video_title);
        this.mFullScreenViewStub = (ViewStub) findViewById(R.id.vp_fullscreen_view_stub);
        this.mVideoPlayView.setOnClickListener(this);
        this.mVideoThumbView.setOnClickListener(this);
        this.mVideoTextureViewContainer.setOnClickListener(this);
        this.mVideoTextureViewContainer.setOnTouchListener(this);
        this.mVideoErrorView.setOnClickListener(this);
        this.mVideoFullScreenView.setOnClickListener(this);
        this.mVideoPlaySeekBar.setOnTouchListener(this);
        this.mVideoErrorView.setOnClickListener(this);
        this.mVideoControllerView.setOnTouchListener(this);
        this.mVideoPlaySeekBar.setOnSeekBarChangeListener(this);
        this.mVideoSmallWindowBackView.setOnClickListener(this);
        this.mVideoFullScreenBackView.setOnClickListener(this);
    }

    public void setListener(OnClickVideoContainerListener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: protected */
    public void resetViewState() {
        this.mCurrentState = 0;
        this.mCurrentScreenState = 1;
        onPlayStateChanged(0);
    }

    public void textureViewClick() {
        if (!this.blnParticular) {
            toggleFullScreen();
        } else if (this.mCurrentScreenState != 3) {
            toggleFullScreen();
        }
    }

    public void bind(String videoUrl, CharSequence title, boolean showNormalStateTitleView, boolean autoPlay) {
        this.mShowNormalStateTitleView = showNormalStateTitleView;
        this.isAutoPlay = autoPlay;
        this.mVideoTitle = title;
        this.mVideoUrl = videoUrl;
        if (!TextUtils.isEmpty(title) && !this.blnParticular) {
            this.mVideoTitleView.setText(this.mVideoTitle);
        }
        resetViewState();
        if (autoPlay) {
            startPlayVideo();
        }
    }

    public void bind(String videoUrl, CharSequence title, boolean autoPlay) {
        bind(videoUrl, title, this.mShowNormalStateTitleView, autoPlay);
    }

    public void bind(String videoUrl, CharSequence title) {
        bind(videoUrl, title, this.mShowNormalStateTitleView, false);
    }

    public void bind(String videoUrl) {
        bind(videoUrl, (CharSequence) null);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (R.id.vp_video_surface_container == id) {
            IVideoPlayerState iVideoPlayerState = this.videoPlayerState;
            if (iVideoPlayerState != null) {
                iVideoPlayerState.onVideoClick();
                return;
            }
            return;
        }
        if (!this.videoPlayerMgr.isViewPlaying(this.mViewHash)) {
            this.videoPlayerMgr.stop();
        }
        int state = this.videoPlayerMgr.getState();
        if (R.id.vp_video_play == id) {
            if (TextUtils.isEmpty(this.mVideoUrl)) {
                Toast.makeText(getContext(), R.string.vp_no_url, 0).show();
                return;
            }
            OnClickVideoContainerListener onClickVideoContainerListener = this.listener;
            if (onClickVideoContainerListener != null) {
                onClickVideoContainerListener.onClickView();
            }
            if (state != 0) {
                if (state == 2) {
                    this.videoPlayerMgr.pause();
                    return;
                } else if (state == 4) {
                    this.videoPlayerMgr.play();
                    return;
                } else if (state == 5) {
                    this.videoPlayerMgr.seekTo(0);
                    this.videoPlayerMgr.play();
                    return;
                } else if (state != 6) {
                    return;
                }
            }
            startPlayVideo();
        } else if (R.id.vp_video_thumb == id) {
            OnClickVideoContainerListener onClickVideoContainerListener2 = this.listener;
            if (onClickVideoContainerListener2 != null) {
                onClickVideoContainerListener2.onClickView();
            }
            startPlayVideo();
        } else if (R.id.vp_video_fullscreen == id) {
            toggleFullScreen();
        } else if (R.id.vp_video_play_error_view == id) {
            startPlayVideo();
        } else if (R.id.vp_video_small_window_back == id) {
            exitSmallWindowPlay(true);
        } else if (R.id.vp_video_fullScreen_back == id) {
            exitFullScreen();
        }
    }

    public boolean isViewPlaying() {
        return this.videoPlayerMgr.isViewPlaying(this.mViewHash);
    }

    public void newStartplay() {
        newStartplay((View) null);
    }

    public void newStartplay(View view) {
        if (!this.videoPlayerMgr.isViewPlaying(this.mViewHash)) {
            this.videoPlayerMgr.stop();
            this.videoPlayerMgr.setPlayingView(view);
        }
        int state = this.videoPlayerMgr.getState();
        if (TextUtils.isEmpty(this.mVideoUrl)) {
            Toast.makeText(getContext(), R.string.vp_no_url, 0).show();
            return;
        }
        if (state != 0) {
            if (state == 2) {
                this.videoPlayerMgr.pause();
                return;
            } else if (state == 4) {
                this.videoPlayerMgr.play();
                return;
            } else if (state == 5) {
                this.videoPlayerMgr.seekTo(0);
                this.videoPlayerMgr.play();
                return;
            } else if (state != 6) {
                return;
            }
        }
        startPlayVideo();
    }

    public void startPlayVideo() {
        if (Utils.isConnected(getContext()) || this.videoPlayerMgr.isCached(this.mVideoUrl)) {
            ((Activity) getContext()).getWindow().addFlags(128);
            requestAudioFocus();
            if (this.videoPlayerMgr.getvLast() != getParent()) {
                this.videoPlayerMgr.setVLastVisiable(false);
            }
            this.videoPlayerMgr.removeTextureView();
            TextureView textureView = createTextureView();
            this.mVideoTextureViewContainer.addView(textureView);
            if (!ScreenViewState.isFullScreen(this.mCurrentScreenState) && !this.blnParticular) {
                this.videoPlayerMgr.setVolume(0);
            }
            this.videoPlayerMgr.start(this.mVideoUrl, this.mViewHash);
            this.videoPlayerMgr.setTextureView(textureView);
            return;
        }
        Toast.makeText(getContext(), R.string.vp_no_network, 0).show();
    }

    public TextureView createTextureView() {
        TextureView textureView = newTextureView();
        textureView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1, 17));
        return textureView;
    }

    /* access modifiers changed from: protected */
    public TextureView newTextureView() {
        return new TextureView(getContext());
    }

    public ImageView getThumbImageView() {
        return this.mVideoThumbView;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.videoPlayerMgr.seekTo((seekBar.getProgress() * this.mDuration) / 100);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Utils.log("attached to window, view hash:" + this.mViewHash);
        this.videoPlayerMgr.addObserver(this);
        this.mToggleFullScreen = false;
        ScreenViewState.isSmallWindow(this.mCurrentScreenState);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Utils.log("detached from window, view hash:" + this.mViewHash);
        this.videoPlayerMgr.removeObserver(this);
        if (!this.mToggleFullScreen) {
            if (this.videoPlayerMgr.getConfig().isSmallWindowPlayEnable()) {
                int id = getId();
                return;
            }
            if (this.mCurrentState != 0) {
                this.videoPlayerMgr.stop();
            }
            onPlayStateChanged(0);
        }
    }

    public final void update(Observable o, final Object arg) {
        if (getContext() == null || !(arg instanceof Message) || this.mViewHash != ((Message) arg).getHash() || !this.mVideoUrl.equals(((Message) arg).getVideoUrl())) {
            return;
        }
        if (arg instanceof DurationMessage) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    AbsMultiVideoPlayerView.this.onDurationChanged(((DurationMessage) arg).getDuration());
                }
            });
        } else if (arg instanceof BackPressedMessage) {
            onBackPressed((BackPressedMessage) arg);
        } else if (arg instanceof UIStateMessage) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    AbsMultiVideoPlayerView.this.onPlayStateChanged(((UIStateMessage) arg).getState());
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onBackPressed(BackPressedMessage message) {
        if (ScreenViewState.isFullScreen(message.getScreenState())) {
            exitFullScreen();
        }
    }

    /* access modifiers changed from: protected */
    public void onPlayStateChanged(int state) {
        this.mCurrentState = state;
        onChangeUIState(state);
        switch (state) {
            case 0:
                Utils.log("state change to: STATE_NORMAL");
                resetDuration();
                stopVideoProgressUpdate();
                abandonAudioFocus();
                ((Activity) getContext()).getWindow().clearFlags(128);
                return;
            case 1:
                Utils.log("state change to: STATE_LOADING");
                return;
            case 2:
                Utils.log("state change to: STATE_PLAYING");
                startVideoProgressUpdate();
                return;
            case 3:
                Utils.log("state change to: STATE_PLAYING_BUFFERING_START");
                return;
            case 4:
                Utils.log("state change to: STATE_PAUSE");
                stopVideoProgressUpdate();
                return;
            case 5:
                Utils.log("state change to: STATE_AUTO_COMPLETE");
                stopVideoProgressUpdate();
                exitFullScreen();
                exitSmallWindowPlay(true);
                return;
            case 6:
                Utils.log("state change to: STATE_ERROR");
                resetDuration();
                stopVideoProgressUpdate();
                abandonAudioFocus();
                return;
            default:
                throw new IllegalStateException("Illegal Play State:" + state);
        }
    }

    /* access modifiers changed from: protected */
    public void resetDuration() {
        this.mDuration = 0;
    }

    public void onChangeUIState(int state) {
        switch (state) {
            case 0:
                onChangeUINormalState();
                return;
            case 1:
                onChangeUILoadingState();
                return;
            case 2:
                onChangeUIPlayingState();
                return;
            case 3:
                onChangeUISeekBufferingState();
                return;
            case 4:
                onChangeUIPauseState();
                return;
            case 5:
                onChangeUICompleteState();
                return;
            case 6:
                onChangeUIErrorState();
                return;
            default:
                throw new IllegalStateException("Illegal Play State:" + state);
        }
    }

    public void onDurationChanged(int duration) {
        this.mDuration = duration;
        this.mVideoTotalTimeView.setText(Utils.formatVideoTimeLength((long) duration));
    }

    /* access modifiers changed from: protected */
    public void onChangeVideoHeaderViewState(boolean showHeaderView) {
        if (!showHeaderView) {
            Utils.hideViewIfNeed(this.mVideoHeaderViewContainer);
        } else if (ScreenViewState.isFullScreen(this.mCurrentScreenState)) {
            Utils.showViewIfNeed(this.mVideoHeaderViewContainer);
        } else if (!ScreenViewState.isNormal(this.mCurrentScreenState)) {
            Utils.hideViewIfNeed(this.mVideoHeaderViewContainer);
        } else if (this.mShowNormalStateTitleView) {
            Utils.showViewIfNeed(this.mVideoHeaderViewContainer);
        } else {
            Utils.hideViewIfNeed(this.mVideoHeaderViewContainer);
        }
    }

    public void onChangeUINormalState() {
        Utils.showViewIfNeed(this.mVideoThumbView);
        Utils.hideViewIfNeed(this.mVideoLoadingBar);
        this.mVideoPlayView.setVisibility(0);
        this.mVideoPlayView.setImageResource(R.drawable.vp_play_selector);
        Utils.showViewIfNeed(this.mVideoPlayView);
        Utils.hideViewIfNeed(this.mVideoControllerView);
        Utils.hideViewIfNeed(this.mBottomProgressBar);
        Utils.hideViewIfNeed(this.mVideoErrorView);
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            Utils.showViewIfNeed(this.mVideoSmallWindowBackView);
        } else if (ScreenViewState.isFullScreen(this.mCurrentScreenState)) {
            Utils.hideViewIfNeed(this.mVideoSmallWindowBackView);
        }
        onChangeVideoHeaderViewState(true);
    }

    public void onChangeUILoadingState() {
        Utils.showViewIfNeed(this.mVideoLoadingBar);
        Utils.hideViewIfNeed(this.mVideoPlayView);
        Utils.hideViewIfNeed(this.mVideoControllerView);
        Utils.hideViewIfNeed(this.mBottomProgressBar);
        Utils.hideViewIfNeed(this.mVideoErrorView);
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            Utils.showViewIfNeed(this.mVideoSmallWindowBackView);
        } else {
            Utils.hideViewIfNeed(this.mVideoSmallWindowBackView);
        }
        onChangeVideoHeaderViewState(false);
    }

    public void onChangeUIPlayingState() {
        Utils.hideViewIfNeed(this.mVideoThumbView);
        Utils.hideViewIfNeed(this.mVideoLoadingBar);
        Utils.hideViewIfNeed(this.mVideoErrorView);
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            Utils.hideViewIfNeed(this.mVideoControllerView);
            cancelDismissControllerViewTimer();
            if (!this.blnParticular) {
                Utils.showViewIfNeed(this.mBottomProgressBar);
            }
            this.mVideoPlayView.setVisibility(8);
            Utils.showViewIfNeed(this.mVideoSmallWindowBackView);
        } else {
            Utils.showViewIfNeed(this.mVideoControllerView);
            startDismissControllerViewTimer();
            Utils.hideViewIfNeed(this.mBottomProgressBar);
            this.mVideoPlayView.setVisibility(8);
            Utils.hideViewIfNeed(this.mVideoSmallWindowBackView);
        }
        onChangeVideoHeaderViewState(true);
    }

    public void onChangeUISeekBufferingState() {
        Utils.hideViewIfNeed(this.mVideoThumbView);
        Utils.showViewIfNeed(this.mVideoLoadingBar);
        Utils.hideViewIfNeed(this.mVideoPlayView);
        Utils.hideViewIfNeed(this.mVideoErrorView);
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            Utils.hideViewIfNeed(this.mVideoControllerView);
            cancelDismissControllerViewTimer();
            if (!this.blnParticular) {
                Utils.showViewIfNeed(this.mBottomProgressBar);
            }
            Utils.showViewIfNeed(this.mVideoSmallWindowBackView);
        } else {
            Utils.showViewIfNeed(this.mVideoControllerView);
            cancelDismissControllerViewTimer();
            Utils.hideViewIfNeed(this.mBottomProgressBar);
            Utils.hideViewIfNeed(this.mVideoSmallWindowBackView);
        }
        onChangeVideoHeaderViewState(false);
    }

    public void onChangeUIPauseState() {
        Utils.hideViewIfNeed(this.mVideoThumbView);
        Utils.hideViewIfNeed(this.mVideoLoadingBar);
        Utils.showViewIfNeed(this.mVideoControllerView);
        cancelDismissControllerViewTimer();
        Utils.hideViewIfNeed(this.mBottomProgressBar);
        Utils.hideViewIfNeed(this.mVideoErrorView);
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            Utils.showViewIfNeed(this.mVideoSmallWindowBackView);
            this.mVideoPlayView.setVisibility(8);
        } else {
            this.mVideoPlayView.setImageResource(R.drawable.vp_play_selector);
            this.mVideoPlayView.setVisibility(8);
            Utils.hideViewIfNeed(this.mVideoSmallWindowBackView);
        }
        onChangeVideoHeaderViewState(true);
    }

    public void setCompleteDelegate(IVideoPlayerState state) {
        this.videoPlayerState = state;
    }

    public void onChangeUICompleteState() {
        Utils.showViewIfNeed(this.mVideoThumbView);
        Utils.hideViewIfNeed(this.mVideoLoadingBar);
        this.mVideoPlayView.setImageResource(R.drawable.vp_replay_selector);
        Utils.showViewIfNeed(this.mVideoPlayView);
        Utils.hideViewIfNeed(this.mVideoControllerView);
        cancelDismissControllerViewTimer();
        Utils.hideViewIfNeed(this.mBottomProgressBar);
        Utils.hideViewIfNeed(this.mVideoErrorView);
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            Utils.showViewIfNeed(this.mVideoSmallWindowBackView);
        } else {
            Utils.hideViewIfNeed(this.mVideoSmallWindowBackView);
        }
        updateProgress(this.mDuration);
        onChangeVideoHeaderViewState(true);
        IVideoPlayerState iVideoPlayerState = this.videoPlayerState;
        if (iVideoPlayerState != null) {
            iVideoPlayerState.onVideoComplete();
        }
    }

    public void onChangeUIErrorState() {
        Utils.hideViewIfNeed(this.mVideoThumbView);
        Utils.hideViewIfNeed(this.mVideoLoadingBar);
        Utils.hideViewIfNeed(this.mVideoPlayView);
        Utils.hideViewIfNeed(this.mVideoControllerView);
        cancelDismissControllerViewTimer();
        Utils.hideViewIfNeed(this.mBottomProgressBar);
        Utils.showViewIfNeed(this.mVideoErrorView);
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            Utils.showViewIfNeed(this.mVideoSmallWindowBackView);
        } else {
            Utils.hideViewIfNeed(this.mVideoSmallWindowBackView);
        }
        onChangeVideoHeaderViewState(false);
    }

    public void onChangeUIWhenTouchVideoView() {
        if (this.mCurrentState == 2) {
            if (!Utils.isViewShown(this.mVideoPlayView) || !Utils.isViewShown(this.mVideoControllerView)) {
            }
            showFullScreenTouchStateView();
        }
    }

    /* access modifiers changed from: protected */
    public void hideFullScreenTouchStateView() {
        Utils.hideViewIfNeed(this.mVideoPlayView);
        if (!this.blnParticular) {
            Utils.showViewIfNeed(this.mBottomProgressBar);
        }
        onChangeVideoHeaderViewState(false);
        cancelDismissControllerViewTimer();
    }

    /* access modifiers changed from: protected */
    public void showFullScreenTouchStateView() {
        Utils.showViewIfNeed(this.mVideoPlayView);
        Utils.hideViewIfNeed(this.mBottomProgressBar);
        startDismissControllerViewTimer();
        onChangeVideoHeaderViewState(true);
    }

    public void startDismissControllerViewTimer() {
        cancelDismissControllerViewTimer();
        this.mDismissControllerViewTimer = new Timer();
        DismissControllerViewTimerTask dismissControllerViewTimerTask = new DismissControllerViewTimerTask();
        this.mDismissControllerViewTimerTask = dismissControllerViewTimerTask;
        this.mDismissControllerViewTimer.schedule(dismissControllerViewTimerTask, (long) this.mAutoDismissTime);
    }

    public void cancelDismissControllerViewTimer() {
        Timer timer = this.mDismissControllerViewTimer;
        if (timer != null) {
            timer.cancel();
        }
        DismissControllerViewTimerTask dismissControllerViewTimerTask = this.mDismissControllerViewTimerTask;
        if (dismissControllerViewTimerTask != null) {
            dismissControllerViewTimerTask.cancel();
        }
    }

    public void setAutoPlay(boolean auto) {
        this.isAutoPlay = auto;
    }

    public void setControlBarCanShow(boolean show) {
    }

    public void setTitleBarCanShow(boolean show) {
    }

    public void destroy() {
    }

    public void setCoverData(Object uri) {
    }

    public void changeScreenMode(int screenMode) {
    }

    public int getScreenMode() {
        return 0;
    }

    public int getBufferPercentage() {
        return 0;
    }

    public void setScreenBrightness(int brightness) {
    }

    public int getScreenBrightness() {
        return 0;
    }

    public int getBufferingPosition() {
        return 0;
    }

    public class DismissControllerViewTimerTask extends TimerTask {
        public DismissControllerViewTimerTask() {
        }

        public void run() {
            int state = AbsMultiVideoPlayerView.this.mCurrentState;
            if (state != 0 && state != 6 && state != 5 && AbsMultiVideoPlayerView.this.getContext() != null && (AbsMultiVideoPlayerView.this.getContext() instanceof Activity)) {
                ((Activity) AbsMultiVideoPlayerView.this.getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        AbsMultiVideoPlayerView.this.hideFullScreenTouchStateView();
                    }
                });
            }
        }
    }

    /* access modifiers changed from: protected */
    public void startVideoProgressUpdate() {
        stopVideoProgressUpdate();
        if (!this.mExecutorService.isShutdown()) {
            this.mScheduleFuture = this.mExecutorService.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    AbsMultiVideoPlayerView absMultiVideoPlayerView = AbsMultiVideoPlayerView.this;
                    absMultiVideoPlayerView.post(absMultiVideoPlayerView.mUpdateProgressTask);
                }
            }, 100, 300, TimeUnit.MILLISECONDS);
        }
    }

    /* access modifiers changed from: protected */
    public void stopVideoProgressUpdate() {
        ScheduledFuture<?> scheduledFuture = this.mScheduleFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    /* access modifiers changed from: protected */
    public void updateProgress(int position) {
        int i = position * 100;
        int i2 = this.mDuration;
        if (i2 == 0) {
            i2 = 1;
        }
        int progress = i / i2;
        this.mVideoPlayTimeView.setText(Utils.formatVideoTimeLength((long) position));
        this.mVideoPlaySeekBar.setProgress(progress);
        this.mBottomProgressBar.setProgress(progress);
    }

    public void toggleFullScreen() {
        if (ScreenViewState.isFullScreen(this.mCurrentScreenState)) {
            exitFullScreen();
            if (!this.blnParticular) {
                this.videoPlayerMgr.setVolume(0);
            }
        } else if (ScreenViewState.isNormal(this.mCurrentScreenState)) {
            startFullScreen();
            this.videoPlayerMgr.setVolume(4);
        } else {
            throw new IllegalStateException("the screen state is error, state=" + this.mCurrentScreenState);
        }
    }

    public void startFullScreen() {
        this.mToggleFullScreen = true;
        VideoPlayerManager videoPlayerManager = this.videoPlayerMgr;
        this.mCurrentScreenState = 3;
        videoPlayerManager.setScreenState(3);
        this.videoPlayerMgr.pause();
        ViewGroup viewGroup = (ViewGroup) Utils.getActivity(getContext()).findViewById(16908290);
        this.mVideoWidth = getWidth();
        this.mVideoHeight = getHeight();
        ViewGroup viewGroup2 = (ViewGroup) getParent();
        this.mOldParent = viewGroup2;
        if (viewGroup2 != null) {
            this.mOldIndex = viewGroup2.indexOfChild(this);
            this.mOldParent.removeView(this);
        }
        if (this.Ratio == 0.0f) {
            this.Ratio = this.videoPlayerMgr.getVideoRatio();
        }
        FullScreenExoMultiPlayer fullScreenExoMultiPlayer = new FullScreenExoMultiPlayer(getContext(), this, this.Ratio, this.blnParticular, TextUtils.isEmpty(this.mVideoTitle) ? "" : String.valueOf(this.mVideoTitle), new FullScreenExoMultiPlayer.dismissListener() {
            public void doDismissCallback() {
                AbsMultiVideoPlayerView.this.toggleFullScreen();
            }
        });
        this.dialog = fullScreenExoMultiPlayer;
        fullScreenExoMultiPlayer.show();
        viewStubFullScreenGestureView();
        Utils.getActivity(getContext()).getWindow().addFlags(1024);
        Utils.getActivity(getContext()).setRequestedOrientation(2);
        this.mVideoFullScreenView.setImageResource(R.drawable.vp_ic_minimize);
        startVideoProgressUpdate();
        this.videoPlayerMgr.play();
    }

    public void exitFullScreen() {
        if (ScreenViewState.isFullScreen(this.mCurrentScreenState)) {
            this.mToggleFullScreen = true;
            VideoPlayerManager videoPlayerManager = this.videoPlayerMgr;
            this.mCurrentScreenState = 1;
            videoPlayerManager.setScreenState(1);
            this.videoPlayerMgr.pause();
            this.dialog.getView();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(this.mVideoWidth, this.mVideoHeight);
            ViewGroup viewGroup = this.mOldParent;
            if (viewGroup != null) {
                viewGroup.addView(this, this.mOldIndex, lp);
            }
            Utils.getActivity(getContext()).getWindow().clearFlags(1024);
            Utils.getActivity(getContext()).setRequestedOrientation(1);
            if (!(this.mCurrentState == 5 || this.mOldParent == null)) {
                this.videoPlayerMgr.play();
            }
            this.mVideoFullScreenView.setImageResource(R.drawable.vp_ic_fullscreen);
            this.mOldParent = null;
            this.mOldIndex = 0;
        }
    }

    public void toggleSmallWindow() {
        if (this.mCurrentState != 0) {
            if (!this.videoPlayerMgr.hasViewPlaying()) {
                resetViewState();
            } else if (ScreenViewState.isNormal(this.mCurrentScreenState)) {
                startSmallWindowPlay();
            } else {
                exitSmallWindowPlay(false);
            }
        }
    }

    public void startSmallWindowPlay() {
        stopVideoProgressUpdate();
        VideoPlayerManager videoPlayerManager = this.videoPlayerMgr;
        this.mCurrentScreenState = 4;
        videoPlayerManager.setScreenState(4);
        AbsMultiVideoPlayerView absVideoPlayerView = new AbsMultiVideoPlayerView(getContext());
        absVideoPlayerView.setId(R.id.vp_small_window_view_id);
        absVideoPlayerView.mDuration = this.mDuration;
        absVideoPlayerView.mVideoUrl = this.mVideoUrl;
        absVideoPlayerView.mViewHash = this.mViewHash;
        absVideoPlayerView.mShowNormalStateTitleView = this.mShowNormalStateTitleView;
        TextureView textureView = absVideoPlayerView.createTextureView();
        absVideoPlayerView.mVideoTextureViewContainer.addView(textureView);
        this.videoPlayerMgr.setTextureView(textureView);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(this.mSmallWindowWidth, this.mSmallWindowHeight);
        lp.gravity = 85;
        ((ViewGroup) Utils.getActivity(getContext()).findViewById(16908290)).addView(absVideoPlayerView, lp);
        absVideoPlayerView.mCurrentScreenState = this.mCurrentScreenState;
        absVideoPlayerView.mCurrentState = this.mCurrentState;
        absVideoPlayerView.onPlayStateChanged(this.mCurrentState);
    }

    public void exitSmallWindowPlay(boolean forceStop) {
        if (ScreenViewState.isSmallWindow(this.mCurrentScreenState)) {
            ViewGroup windowContent = (ViewGroup) Utils.getActivity(getContext()).findViewById(16908290);
            AbsMultiVideoPlayerView smallWindowView = (AbsMultiVideoPlayerView) windowContent.findViewById(R.id.vp_small_window_view_id);
            smallWindowView.stopVideoProgressUpdate();
            VideoPlayerManager videoPlayerManager = this.videoPlayerMgr;
            this.mCurrentScreenState = 1;
            videoPlayerManager.setScreenState(1);
            this.videoPlayerMgr.setTextureView((TextureView) null);
            smallWindowView.mVideoTextureViewContainer.removeAllViews();
            this.mDuration = smallWindowView.mDuration;
            this.mVideoUrl = smallWindowView.mVideoUrl;
            this.mViewHash = smallWindowView.mViewHash;
            this.mCurrentState = smallWindowView.mCurrentState;
            this.mShowNormalStateTitleView = smallWindowView.mShowNormalStateTitleView;
            if (forceStop) {
                this.videoPlayerMgr.stop();
                windowContent.removeView(smallWindowView);
                return;
            }
            windowContent.removeView(smallWindowView);
            TextureView textureView = createTextureView();
            this.mVideoTextureViewContainer.addView(textureView);
            this.videoPlayerMgr.setTextureView(textureView);
            onPlayStateChanged(this.mCurrentState);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -2) {
            if (this.videoPlayerMgr.isPlaying()) {
                this.videoPlayerMgr.pause();
            }
            Utils.log("AudioManager.AUDIOFOCUS_LOSS_TRANSIENT");
        } else if (focusChange == -1) {
            this.videoPlayerMgr.stop();
            Utils.log("AudioManager.AUDIOFOCUS_LOSS");
        } else if (focusChange == 1 && this.videoPlayerMgr.getState() == 4) {
            this.videoPlayerMgr.play();
        }
    }

    /* access modifiers changed from: protected */
    public void requestAudioFocus() {
        ((AudioManager) getContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO)).requestAudioFocus(this, 3, 2);
    }

    /* access modifiers changed from: protected */
    public void abandonAudioFocus() {
        ((AudioManager) getContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO)).abandonAudioFocus(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (!ScreenViewState.isSmallWindow(this.mCurrentScreenState) && R.id.vp_video_surface_container == id) {
        }
        return false;
    }

    public void onTouchToControllerView(MotionEvent event) {
        if (event.getAction() == 0) {
            this.mIsTouchControllerView = true;
            cancelDismissControllerViewTimer();
        }
    }

    public void onTouchToVideoView(MotionEvent event) {
        int action = event.getAction();
        boolean z = false;
        if (action == 0) {
            cancelDismissControllerViewTimer();
        } else if (action == 1) {
            if (this.mIsTouchControllerView) {
                startDismissControllerViewTimer();
            } else {
                onChangeUIWhenTouchVideoView();
            }
            this.mIsTouchControllerView = false;
        }
        int action2 = event.getAction();
        if (action2 == 0) {
            this.mTouchDownX = event.getRawX();
            this.mTouchDownY = event.getRawY();
        } else if (action2 == 1) {
            int i = this.mCurrentGestureState;
            if (i == 1) {
                int i2 = this.mGestureSeekToPosition;
                if (i2 != -1) {
                    this.videoPlayerMgr.seekTo(i2);
                    this.mGestureSeekToPosition = -1;
                    Utils.hideViewIfNeed(this.mVideoChangeProgressView);
                    showFullScreenTouchStateView();
                }
            } else if (i == 2) {
                Utils.hideViewIfNeed(this.mVideoVolumeView);
            } else if (i == 3) {
                Utils.hideViewIfNeed(this.mVideoBrightnessView);
            }
        } else if (action2 == 2) {
            int i3 = this.mCurrentState;
            if (i3 == 2 || i3 == 4) {
                float xDis = Math.abs(this.mTouchDownX - event.getRawX());
                float yDis = Math.abs(event.getRawY() - this.mTouchDownY);
                Utils.logTouch("TouchSlop:" + this.mTouchSlop + ", xDis:" + xDis + ", yDis:" + yDis);
                if (isFlingLeft(this.mTouchDownX, this.mTouchDownY, event)) {
                    hideFullScreenTouchStateView();
                    Utils.logTouch("Fling Left");
                    this.mTouchDownX = event.getRawX();
                    this.mTouchDownY = event.getRawY();
                } else if (isFlingRight(this.mTouchDownX, this.mTouchDownY, event)) {
                    hideFullScreenTouchStateView();
                    Utils.logTouch("Fling Right");
                    this.mTouchDownX = event.getRawX();
                    this.mTouchDownY = event.getRawY();
                } else if (isScrollVertical(this.mTouchDownX, this.mTouchDownY, event)) {
                    hideFullScreenTouchStateView();
                    if (isScrollVerticalRight(this.mTouchDownX, event)) {
                        Utils.logTouch("isScrollVerticalRight");
                        if (Math.abs(event.getRawY() - this.mTouchDownY) >= this.mVolumeDistance) {
                            if (event.getRawY() < this.mTouchDownY) {
                                z = true;
                            }
                            changeVideoVolume(z);
                            this.mTouchDownX = event.getRawX();
                            this.mTouchDownY = event.getRawY();
                        }
                    } else if (isScrollVerticalLeft(this.mTouchDownX, event)) {
                        Utils.logTouch("isScrollVerticalLeft");
                        if (Math.abs(event.getRawY() - this.mTouchDownY) >= this.mBrightnessDistance) {
                            if (event.getRawY() < this.mTouchDownY) {
                                z = true;
                            }
                            changeBrightness(z);
                            this.mTouchDownX = event.getRawX();
                            this.mTouchDownY = event.getRawY();
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initFullScreenGestureParams() {
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        AudioManager audioManager = (AudioManager) getContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.mAudioManager = audioManager;
        int streamMaxVolume = audioManager.getStreamMaxVolume(3);
        this.mMaxVolume = streamMaxVolume;
        int i = this.mScreenHeight;
        this.mVolumeDistance = (((float) i) / 3.0f) / ((float) streamMaxVolume);
        this.mBrightnessDistance = (((float) i) / 3.0f) / 12.5f;
        this.mVideoVolumeProgress.setProgress((int) ((((((double) this.mAudioManager.getStreamVolume(3)) * 1.0d) / ((double) this.mMaxVolume)) * 100.0d) + 0.5d));
        float screenLight = ((float) Settings.System.getInt(getContext().getContentResolver(), "screen_brightness", 255)) / 255.0f;
        ((Activity) getContext()).getWindow().getAttributes().screenBrightness = screenLight;
        this.mVideoBrightnessProgress.setProgress((int) (100.0f * screenLight));
    }

    /* access modifiers changed from: protected */
    public void viewStubFullScreenGestureView() {
        ViewStub viewStub = this.mFullScreenViewStub;
        if (viewStub != null) {
            viewStub.setVisibility(0);
            this.mVideoVolumeView = (LinearLayout) findViewById(R.id.vp_video_volume);
            this.mVideoVolumeProgress = (ProgressBar) findViewById(R.id.vp_video_volume_progressbar);
            this.mVideoBrightnessView = (LinearLayout) findViewById(R.id.vp_video_brightness);
            this.mVideoBrightnessProgress = (ProgressBar) findViewById(R.id.vp_video_brightness_progressbar);
            this.mVideoChangeProgressView = findViewById(R.id.vp_video_change_progress_view);
            this.mVideoChangeProgressIcon = (ImageView) findViewById(R.id.vp_video_change_progress_icon);
            this.mVideoChangeProgressCurrPro = (TextView) findViewById(R.id.vp_video_change_progress_current);
            this.mVideoChangeProgressTotal = (TextView) findViewById(R.id.vp_video_change_progress_total);
            this.mVideoChangeProgressBar = (ProgressBar) findViewById(R.id.vp_video_change_progress_bar);
            initFullScreenGestureParams();
        }
    }

    /* access modifiers changed from: protected */
    public void changeVideoVolume(boolean isTurnUp) {
        int volume;
        this.mCurrentGestureState = 2;
        Utils.showViewIfNeed(this.mVideoVolumeView);
        int volume2 = this.mAudioManager.getStreamVolume(3);
        if (isTurnUp) {
            int i = volume2 + 1;
            int i2 = this.mMaxVolume;
            if (i < i2) {
                i2 = volume2 + 1;
            }
            volume = i2;
        } else {
            volume = volume2 + -1 > 0 ? volume2 - 1 : 0;
        }
        this.mAudioManager.setStreamVolume(3, volume, 0);
        this.mVideoVolumeProgress.setProgress((int) ((((((double) volume) * 1.0d) / ((double) this.mMaxVolume)) * 100.0d) + 0.5d));
    }

    /* access modifiers changed from: protected */
    public void changeBrightness(boolean isDodge) {
        float brightness;
        this.mCurrentGestureState = 3;
        Utils.showViewIfNeed(this.mVideoBrightnessView);
        WindowManager.LayoutParams mWindowAttr = ((Activity) getContext()).getWindow().getAttributes();
        float brightness2 = mWindowAttr.screenBrightness;
        if (isDodge) {
            float f = 1.0f;
            if (brightness2 < 1.0f) {
                f = brightness2 + BRIGHTNESS_STEP;
            }
            brightness = f;
        } else {
            float f2 = 0.0f;
            if (brightness2 > 0.0f) {
                f2 = brightness2 - BRIGHTNESS_STEP;
            }
            brightness = f2;
        }
        mWindowAttr.screenBrightness = brightness;
        ((Activity) getContext()).getWindow().setAttributes(mWindowAttr);
        this.mVideoBrightnessProgress.setProgress((int) (100.0f * brightness));
    }

    /* access modifiers changed from: protected */
    public void videoSeek(boolean isForward) {
        this.mCurrentGestureState = 1;
        Utils.showViewIfNeed(this.mVideoChangeProgressView);
        if (this.mGestureSeekToPosition == -1) {
            this.mGestureSeekToPosition = this.videoPlayerMgr.getCurrentPosition();
        }
        if (isForward) {
            this.mVideoChangeProgressIcon.setImageResource(R.drawable.vp_ic_fast_forward);
            int i = this.mGestureSeekToPosition;
            int i2 = i + 2000;
            int i3 = this.mDuration;
            if (i2 < i3) {
                i3 = i + 2000;
            }
            this.mGestureSeekToPosition = i3;
        } else {
            this.mVideoChangeProgressIcon.setImageResource(R.drawable.vp_ic_fast_back);
            int i4 = this.mGestureSeekToPosition;
            this.mGestureSeekToPosition = i4 - 2000 <= 0 ? 0 : i4 - 2000;
        }
        this.mVideoChangeProgressCurrPro.setText(Utils.formatVideoTimeLength((long) this.mGestureSeekToPosition));
        TextView textView = this.mVideoChangeProgressTotal;
        textView.setText("/" + Utils.formatVideoTimeLength((long) this.mDuration));
        this.mVideoChangeProgressBar.setProgress((int) ((((((float) this.mGestureSeekToPosition) * 1.0f) / ((float) this.mDuration)) * 100.0f) + 0.5f));
    }

    /* access modifiers changed from: protected */
    public boolean isFlingRight(float downX, float downY, MotionEvent e2) {
        return e2.getRawX() - downX > ((float) this.mTouchSlop) && Math.abs(e2.getRawY() - downY) < ((float) this.mTouchSlop);
    }

    /* access modifiers changed from: protected */
    public boolean isFlingLeft(float downX, float downY, MotionEvent e2) {
        return downX - e2.getRawX() > ((float) this.mTouchSlop) && Math.abs(e2.getRawY() - downY) < ((float) this.mTouchSlop);
    }

    /* access modifiers changed from: protected */
    public boolean isScrollVertical(float downX, float downY, MotionEvent e2) {
        return Math.abs(e2.getRawX() - downX) < ((float) this.mTouchSlop) && Math.abs(e2.getRawY() - downY) > ((float) this.mTouchSlop);
    }

    /* access modifiers changed from: protected */
    public boolean isScrollVerticalRight(float downX, MotionEvent e2) {
        return downX > ((float) (this.mScreenWidth / 2)) && e2.getRawX() > ((float) (this.mScreenWidth / 2));
    }

    /* access modifiers changed from: protected */
    public boolean isScrollVerticalLeft(float downX, MotionEvent e2) {
        return downX < ((float) (this.mScreenWidth / 2)) && e2.getRawX() < ((float) (this.mScreenWidth / 2));
    }

    public int getmViewHash() {
        return this.mViewHash;
    }
}
