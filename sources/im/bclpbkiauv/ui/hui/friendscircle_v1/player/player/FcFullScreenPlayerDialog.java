package im.bclpbkiauv.ui.hui.friendscircle_v1.player.player;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.utils.Utils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.FcVideoPlayerView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FcFullScreenPlayerDialog extends Dialog implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {
    private static final int PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private static final int PROGRESS_UPDATE_INTERNAL = 300;
    private boolean blnMute = false;
    Context context;
    public ImageView iv_mute;
    public ImageView iv_play;
    private LinearLayout ll_state_bar;
    private LinearLayout ll_title_bar;
    private int mDuration;
    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduleFuture;
    private final Runnable mUpdateProgressTask = new Runnable() {
        public final void run() {
            FcFullScreenPlayerDialog.this.lambda$new$6$FcFullScreenPlayerDialog();
        }
    };
    private VideoPlayerManager mVideoPlayerManager;
    DismissListener mdismissListener;
    RelativeLayout root_view;
    private TextView tv_current_time;
    private TextView tv_title;
    private TextView tv_total_time;
    private SeekBar videoPlayerSeekbar;

    public interface DismissListener {
        void doDismissCallback();
    }

    public FcFullScreenPlayerDialog(Context context2, FcVideoPlayerView player, float Ratio, String strTitle, DismissListener Listener) {
        super(context2, R.style.DialogTheme);
        this.mdismissListener = Listener;
        this.context = context2;
        View view = getLayoutInflater().inflate(R.layout.dialog_fc_full_screen_player, (ViewGroup) null);
        this.root_view = (RelativeLayout) view.findViewById(R.id.root_view);
        this.videoPlayerSeekbar = (SeekBar) view.findViewById(R.id.fl_seekbar);
        this.tv_total_time = (TextView) view.findViewById(R.id.tv_total_time);
        this.tv_current_time = (TextView) view.findViewById(R.id.tv_current_time);
        this.iv_play = (ImageView) view.findViewById(R.id.iv_play);
        this.iv_mute = (ImageView) view.findViewById(R.id.iv_mute);
        this.ll_title_bar = (LinearLayout) view.findViewById(R.id.ll_title_bar);
        this.ll_state_bar = (LinearLayout) view.findViewById(R.id.ll_state_bar);
        this.tv_title = (TextView) view.findViewById(R.id.tv_title);
        setContentView(view);
        getWindow().setLayout(-1, -1);
        Display display = ((Activity) context2).getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int mVideoHeight = (int) (((float) width) / Ratio);
        this.root_view.addView(player, new FrameLayout.LayoutParams(-1, mVideoHeight == 0 ? -2 : mVideoHeight, 17));
        this.ll_state_bar.setVisibility(0);
        this.ll_title_bar.setVisibility(0);
        VideoPlayerManager videoPlayerMgr = player.getVideoPlayerMgr();
        this.mVideoPlayerManager = videoPlayerMgr;
        this.mDuration = videoPlayerMgr.getDuration();
        this.tv_title.setText(strTitle);
        startVideoProgressUpdate();
        setCanceledOnTouchOutside(false);
        view.findViewById(R.id.ll_return).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcFullScreenPlayerDialog.this.lambda$new$0$FcFullScreenPlayerDialog(view);
            }
        });
        view.findViewById(R.id.colse_fullscreen).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcFullScreenPlayerDialog.this.lambda$new$1$FcFullScreenPlayerDialog(view);
            }
        });
        player.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FcFullScreenPlayerDialog.this.iv_play.getVisibility() == 8) {
                    FcFullScreenPlayerDialog.this.showControllerView();
                } else {
                    FcFullScreenPlayerDialog.this.hideControllerView();
                }
            }
        });
        this.iv_play.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcFullScreenPlayerDialog.this.lambda$new$2$FcFullScreenPlayerDialog(view);
            }
        });
        this.iv_mute.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FcFullScreenPlayerDialog.this.lambda$new$3$FcFullScreenPlayerDialog(view);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable(context2) {
            private final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ((Activity) this.f$0).setRequestedOrientation(1);
            }
        }, 1000);
        this.tv_total_time.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf((this.mDuration / 1000) / 60), Integer.valueOf((this.mDuration / 1000) % 60)}));
        this.videoPlayerSeekbar.setOnTouchListener(this);
        this.videoPlayerSeekbar.setOnSeekBarChangeListener(this);
    }

    public /* synthetic */ void lambda$new$0$FcFullScreenPlayerDialog(View v) {
        dismiss();
    }

    public /* synthetic */ void lambda$new$1$FcFullScreenPlayerDialog(View v) {
        dismiss();
    }

    public /* synthetic */ void lambda$new$2$FcFullScreenPlayerDialog(View v) {
        if (this.mVideoPlayerManager.getState() == 2) {
            this.mVideoPlayerManager.pause();
            this.iv_play.setImageResource(R.mipmap.iv_pc_agent_description_video_btn_play);
        } else if (this.mVideoPlayerManager.getState() == 4) {
            this.mVideoPlayerManager.play();
            this.iv_play.setImageResource(R.mipmap.iv_pc_agent_description_video_btn_stop);
            hideControllerView();
        }
    }

    public /* synthetic */ void lambda$new$3$FcFullScreenPlayerDialog(View v) {
        if (this.blnMute) {
            this.mVideoPlayerManager.setVolume(4);
            this.iv_mute.setImageResource(R.mipmap.ic_game_share_unmute);
            this.blnMute = false;
            return;
        }
        this.mVideoPlayerManager.setVolume(0);
        this.iv_mute.setImageResource(R.mipmap.ic_game_share_mute);
        this.blnMute = true;
    }

    public void changeControllerState() {
        if (this.iv_play.getVisibility() == 8) {
            showControllerView();
        } else {
            hideControllerView();
        }
    }

    /* access modifiers changed from: private */
    public void showControllerView() {
        this.ll_title_bar.setVisibility(0);
        this.ll_state_bar.setVisibility(0);
        this.iv_play.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void hideControllerView() {
        this.ll_title_bar.setVisibility(8);
        this.ll_state_bar.setVisibility(8);
        this.iv_play.setVisibility(8);
    }

    public View getView() {
        stopVideoProgressUpdate();
        View view = this.root_view.getChildAt(0);
        this.root_view.removeAllViews();
        dismiss();
        return view;
    }

    private void startVideoProgressUpdate() {
        stopVideoProgressUpdate();
        if (!this.mExecutorService.isShutdown()) {
            this.mScheduleFuture = this.mExecutorService.scheduleAtFixedRate(new Runnable() {
                public final void run() {
                    FcFullScreenPlayerDialog.this.lambda$startVideoProgressUpdate$5$FcFullScreenPlayerDialog();
                }
            }, 100, 300, TimeUnit.MILLISECONDS);
        }
    }

    public /* synthetic */ void lambda$startVideoProgressUpdate$5$FcFullScreenPlayerDialog() {
        AndroidUtilities.runOnUIThread(this.mUpdateProgressTask);
    }

    private void stopVideoProgressUpdate() {
        ScheduledFuture<?> scheduledFuture = this.mScheduleFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    public /* synthetic */ void lambda$new$6$FcFullScreenPlayerDialog() {
        VideoPlayerManager videoPlayerManager = this.mVideoPlayerManager;
        if (videoPlayerManager != null) {
            updateProgress(videoPlayerManager.getCurrentPosition());
        }
    }

    private void updateProgress(int position) {
        this.tv_current_time.setText(Utils.formatVideoTimeLength((long) position));
        int i = position * 100;
        int i2 = this.mDuration;
        if (i2 == 0) {
            i2 = 1;
        }
        this.videoPlayerSeekbar.setProgress(i / i2);
    }

    public void dismiss() {
        if (this.root_view.getChildAt(0) != null) {
            this.mdismissListener.doDismissCallback();
        }
        this.root_view.removeAllViews();
        super.dismiss();
    }

    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.mVideoPlayerManager.seekTo((seekBar.getProgress() * this.mDuration) / 100);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
