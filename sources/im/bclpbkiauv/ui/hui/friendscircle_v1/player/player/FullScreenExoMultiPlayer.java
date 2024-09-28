package im.bclpbkiauv.ui.hui.friendscircle_v1.player.player;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.SeekBar;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.AbsMultiVideoPlayerView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FullScreenExoMultiPlayer extends Dialog {
    private static final int PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private static final int PROGRESS_UPDATE_INTERNAL = 300;
    private boolean blnMute = false;
    Context context;
    FrameLayout flSeekbar;
    private ImageView iv_mute;
    private ImageView iv_play;
    private LinearLayout ll_state_bar;
    private LinearLayout ll_title_bar;
    private int mDuration;
    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduleFuture;
    private final Runnable mUpdateProgressTask = new Runnable() {
        public final void run() {
            FullScreenExoMultiPlayer.this.lambda$new$5$FullScreenExoMultiPlayer();
        }
    };
    dismissListener mdismissListener;
    RelativeLayout root_view;
    private TextView tv_current_time;
    private TextView tv_title;
    private TextView tv_total_time;
    FrameLayout videoPlayerControlFrameLayout;
    /* access modifiers changed from: private */
    public SeekBar videoPlayerSeekbar;

    public interface dismissListener {
        void doDismissCallback();
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FullScreenExoMultiPlayer(android.content.Context r17, im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.AbsMultiVideoPlayerView r18, float r19, boolean r20, java.lang.String r21, im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.FullScreenExoMultiPlayer.dismissListener r22) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = r18
            r3 = r20
            r4 = 2131755216(0x7f1000d0, float:1.9141305E38)
            r0.<init>(r1, r4)
            r4 = 0
            r0.blnMute = r4
            java.util.concurrent.ScheduledExecutorService r5 = java.util.concurrent.Executors.newSingleThreadScheduledExecutor()
            r0.mExecutorService = r5
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$3beXtOHaZ1oCPa3Ylv9QtvIQD_U r5 = new im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$3beXtOHaZ1oCPa3Ylv9QtvIQD_U
            r5.<init>()
            r0.mUpdateProgressTask = r5
            r5 = r22
            r0.mdismissListener = r5
            r0.context = r1
            android.view.LayoutInflater r6 = r16.getLayoutInflater()
            r7 = 2131493036(0x7f0c00ac, float:1.860954E38)
            r8 = 0
            android.view.View r6 = r6.inflate(r7, r8)
            r7 = 2131297229(0x7f0903cd, float:1.8212397E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.RelativeLayout r7 = (android.widget.RelativeLayout) r7
            r0.root_view = r7
            r7 = 2131296637(0x7f09017d, float:1.8211196E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.FrameLayout r7 = (android.widget.FrameLayout) r7
            r0.flSeekbar = r7
            r7 = 2131297864(0x7f090648, float:1.8213685E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r0.tv_total_time = r7
            r7 = 2131297747(0x7f0905d3, float:1.8213448E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r0.tv_current_time = r7
            r7 = 2131296831(0x7f09023f, float:1.821159E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.ImageView r7 = (android.widget.ImageView) r7
            r0.iv_play = r7
            r7 = 2131296821(0x7f090235, float:1.821157E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.ImageView r7 = (android.widget.ImageView) r7
            r0.iv_mute = r7
            r7 = 2131296961(0x7f0902c1, float:1.8211853E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.LinearLayout r7 = (android.widget.LinearLayout) r7
            r0.ll_title_bar = r7
            r7 = 2131296957(0x7f0902bd, float:1.8211845E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.LinearLayout r7 = (android.widget.LinearLayout) r7
            r0.ll_state_bar = r7
            r7 = 2131297862(0x7f090646, float:1.821368E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r0.tv_title = r7
            r0.setContentView(r6)
            android.view.Window r7 = r16.getWindow()
            r8 = -1
            r7.setLayout(r8, r8)
            r7 = r1
            android.app.Activity r7 = (android.app.Activity) r7
            android.view.WindowManager r7 = r7.getWindowManager()
            android.view.Display r7 = r7.getDefaultDisplay()
            int r9 = r7.getWidth()
            int r10 = r7.getHeight()
            float r11 = (float) r9
            float r11 = r11 / r19
            int r11 = (int) r11
            android.widget.FrameLayout$LayoutParams r12 = new android.widget.FrameLayout$LayoutParams
            if (r11 != 0) goto L_0x00bb
            r13 = -2
            goto L_0x00bc
        L_0x00bb:
            r13 = r11
        L_0x00bc:
            r14 = 17
            r12.<init>(r8, r13, r14)
            r8 = r12
            android.widget.RelativeLayout r12 = r0.root_view
            r12.addView(r2, r8)
            if (r3 == 0) goto L_0x011b
            android.widget.LinearLayout r12 = r0.ll_state_bar
            r12.setVisibility(r4)
            android.widget.LinearLayout r12 = r0.ll_title_bar
            r12.setVisibility(r4)
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager r12 = im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager.getInstance()
            int r12 = r12.getDuration()
            r0.mDuration = r12
            android.widget.TextView r12 = r0.tv_title
            r13 = r21
            r12.setText(r13)
            r0.createSeekBar(r2)
            r16.startVideoProgressUpdate()
            r0.setCanceledOnTouchOutside(r4)
            r4 = 2131296953(0x7f0902b9, float:1.8211837E38)
            android.view.View r4 = r6.findViewById(r4)
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$9kMd2l65ke5yDDqIHWsf666caAY r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$9kMd2l65ke5yDDqIHWsf666caAY
            r12.<init>()
            r4.setOnClickListener(r12)
            android.widget.ImageView r4 = r0.iv_play
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$zGkp90xM_O5XnEUzauHLvBkWUko r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$zGkp90xM_O5XnEUzauHLvBkWUko
            r12.<init>()
            r4.setOnClickListener(r12)
            android.widget.ImageView r4 = r0.iv_mute
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$iiViqzmeMXG-VAAq0UGXE6s2-0k r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$iiViqzmeMXG-VAAq0UGXE6s2-0k
            r12.<init>()
            r4.setOnClickListener(r12)
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$9Nn1_vesRff9rS3lYpnvsf06bBc r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.-$$Lambda$FullScreenExoMultiPlayer$9Nn1_vesRff9rS3lYpnvsf06bBc
            r4.<init>(r1)
            r14 = 1000(0x3e8, double:4.94E-321)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r4, r14)
            goto L_0x011d
        L_0x011b:
            r13 = r21
        L_0x011d:
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager r4 = im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.VideoPlayerManager.getInstance()
            r4.play()
            android.widget.RelativeLayout r4 = r0.root_view
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.FullScreenExoMultiPlayer$1 r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.FullScreenExoMultiPlayer$1
            r12.<init>(r3)
            r4.setOnClickListener(r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.FullScreenExoMultiPlayer.<init>(android.content.Context, im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.AbsMultiVideoPlayerView, float, boolean, java.lang.String, im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.FullScreenExoMultiPlayer$dismissListener):void");
    }

    public /* synthetic */ void lambda$new$0$FullScreenExoMultiPlayer(View v) {
        if (this.root_view.getChildAt(0) != null) {
            this.mdismissListener.doDismissCallback();
        }
        this.root_view.removeAllViews();
        dismiss();
    }

    public /* synthetic */ void lambda$new$1$FullScreenExoMultiPlayer(View v) {
        if (VideoPlayerManager.getInstance().getState() == 2) {
            VideoPlayerManager.getInstance().pause();
            this.iv_play.setImageResource(R.drawable.ic_play);
        } else if (VideoPlayerManager.getInstance().getState() == 4) {
            VideoPlayerManager.getInstance().play();
            this.iv_play.setImageResource(R.drawable.ic_pause);
        }
    }

    public /* synthetic */ void lambda$new$2$FullScreenExoMultiPlayer(View v) {
        if (this.blnMute) {
            VideoPlayerManager.getInstance().setVolume(4);
            this.iv_mute.setImageResource(R.mipmap.ic_game_share_unmute);
            this.blnMute = false;
            return;
        }
        VideoPlayerManager.getInstance().setVolume(0);
        this.iv_mute.setImageResource(R.mipmap.ic_game_share_mute);
        this.blnMute = true;
    }

    public View getView() {
        View view = this.root_view.getChildAt(0);
        this.root_view.removeAllViews();
        dismiss();
        return view;
    }

    private void createSeekBar(final AbsMultiVideoPlayerView player) {
        SeekBar seekBar = new SeekBar(getContext());
        this.videoPlayerSeekbar = seekBar;
        seekBar.setLineHeight(AndroidUtilities.dp(4.0f));
        this.videoPlayerSeekbar.setColors(Color.parseColor("#4D4D4D"), Color.parseColor("#4D4D4D"), Color.parseColor("#FE6022"), Color.parseColor("#FE6022"), Color.parseColor("#4D4D4D"));
        this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate() {
            public void onSeekBarDrag(float progress) {
                if (VideoPlayerManager.getInstance() != null) {
                    VideoPlayerManager.getInstance().seekTo((int) (((float) ((long) player.getmDuration())) * progress));
                }
            }

            public void onSeekBarContinuousDrag(float progress) {
            }
        });
        AnonymousClass3 r0 = new FrameLayout(getContext()) {
            public boolean onTouchEvent(MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!FullScreenExoMultiPlayer.this.videoPlayerSeekbar.onTouchNew(event.getAction(), event.getX(), event.getY())) {
                    return true;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                long duration;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (VideoPlayerManager.getInstance() != null) {
                    duration = (long) player.getmDuration();
                    if (duration == C.TIME_UNSET) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                long duration2 = duration / 1000;
                FullScreenExoMultiPlayer.this.videoPlayerSeekbar.setSize(getMeasuredWidth(), getMeasuredHeight());
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                FullScreenExoMultiPlayer.this.videoPlayerSeekbar.setProgress(0.0f);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.translate(0.0f, 0.0f);
                FullScreenExoMultiPlayer.this.videoPlayerSeekbar.draw(canvas);
                canvas.restore();
            }
        };
        this.videoPlayerControlFrameLayout = r0;
        this.flSeekbar.addView(r0, LayoutHelper.createFrame(-1, -1, 51));
        this.tv_total_time.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf((this.mDuration / 1000) / 60), Integer.valueOf((this.mDuration / 1000) % 60)}));
    }

    private void startVideoProgressUpdate() {
        stopVideoProgressUpdate();
        if (!this.mExecutorService.isShutdown()) {
            this.mScheduleFuture = this.mExecutorService.scheduleAtFixedRate(new Runnable() {
                public final void run() {
                    FullScreenExoMultiPlayer.this.lambda$startVideoProgressUpdate$4$FullScreenExoMultiPlayer();
                }
            }, 100, 300, TimeUnit.MILLISECONDS);
        }
    }

    public /* synthetic */ void lambda$startVideoProgressUpdate$4$FullScreenExoMultiPlayer() {
        AndroidUtilities.runOnUIThread(this.mUpdateProgressTask);
    }

    private void stopVideoProgressUpdate() {
        ScheduledFuture<?> scheduledFuture = this.mScheduleFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    public /* synthetic */ void lambda$new$5$FullScreenExoMultiPlayer() {
        updateProgress(VideoPlayerManager.getInstance().getCurrentPosition());
    }

    private void updateProgress(int position) {
        this.videoPlayerSeekbar.setProgress(((float) position) / ((float) this.mDuration));
        this.tv_current_time.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf((position / 1000) / 60), Integer.valueOf((position / 1000) % 60)}));
        this.videoPlayerControlFrameLayout.invalidate();
        updatePlayState();
    }

    private void updatePlayState() {
        if (VideoPlayerManager.getInstance().getState() == 2) {
            if (!String.valueOf(this.iv_play.getTag()).equals("ic_play")) {
                this.iv_play.setImageResource(R.drawable.ic_pause);
                this.iv_play.setTag("ic_play");
            }
        } else if (VideoPlayerManager.getInstance().getState() == 4 && !String.valueOf(this.iv_play.getTag()).equals("ic_pause")) {
            this.iv_play.setImageResource(R.drawable.ic_play);
            this.iv_play.setTag("ic_pause");
        }
    }

    public void dismiss() {
        if (this.root_view.getChildAt(0) != null) {
            this.mdismissListener.doDismissCallback();
        }
        this.root_view.removeAllViews();
        super.dismiss();
    }
}
