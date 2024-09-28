package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.serenegiant.usb.UVCCamera;
import com.zhy.http.okhttp.OkHttpUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.messenger.camera.CameraController;
import im.bclpbkiauv.messenger.camera.CameraInfo;
import im.bclpbkiauv.messenger.camera.CameraSession;
import im.bclpbkiauv.messenger.camera.Size;
import im.bclpbkiauv.messenger.video.MP4Builder;
import im.bclpbkiauv.messenger.video.Mp4Movie;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.InstantCameraView;
import im.bclpbkiauv.ui.components.VideoPlayer;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

public class InstantCameraView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Size aspectRatio;
    /* access modifiers changed from: private */
    public ChatActivity baseFragment;
    private FrameLayout cameraContainer;
    /* access modifiers changed from: private */
    public File cameraFile;
    /* access modifiers changed from: private */
    public volatile boolean cameraReady;
    /* access modifiers changed from: private */
    public CameraSession cameraSession;
    /* access modifiers changed from: private */
    public int[] cameraTexture = new int[1];
    /* access modifiers changed from: private */
    public float cameraTextureAlpha = 1.0f;
    /* access modifiers changed from: private */
    public CameraGLThread cameraThread;
    /* access modifiers changed from: private */
    public boolean cancelled;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private boolean deviceHasGoodCamera;
    /* access modifiers changed from: private */
    public long duration;
    /* access modifiers changed from: private */
    public TLRPC.InputEncryptedFile encryptedFile;
    /* access modifiers changed from: private */
    public TLRPC.InputFile file;
    /* access modifiers changed from: private */
    public boolean isFrontface = true;
    /* access modifiers changed from: private */
    public boolean isSecretChat;
    /* access modifiers changed from: private */
    public byte[] iv;
    /* access modifiers changed from: private */
    public byte[] key;
    private Bitmap lastBitmap;
    /* access modifiers changed from: private */
    public float[] mMVPMatrix;
    /* access modifiers changed from: private */
    public float[] mSTMatrix;
    /* access modifiers changed from: private */
    public float[] moldSTMatrix;
    /* access modifiers changed from: private */
    public AnimatorSet muteAnimation;
    /* access modifiers changed from: private */
    public ImageView muteImageView;
    /* access modifiers changed from: private */
    public int[] oldCameraTexture = new int[1];
    /* access modifiers changed from: private */
    public Paint paint;
    private Size pictureSize;
    private int[] position = new int[2];
    /* access modifiers changed from: private */
    public Size previewSize;
    private float progress;
    private Timer progressTimer;
    /* access modifiers changed from: private */
    public long recordStartTime;
    private long recordedTime;
    /* access modifiers changed from: private */
    public boolean recording;
    /* access modifiers changed from: private */
    public int recordingGuid;
    private RectF rect;
    private boolean requestingPermissions;
    /* access modifiers changed from: private */
    public float scaleX;
    /* access modifiers changed from: private */
    public float scaleY;
    private CameraInfo selectedCamera;
    /* access modifiers changed from: private */
    public long size;
    /* access modifiers changed from: private */
    public ImageView switchCameraButton;
    /* access modifiers changed from: private */
    public FloatBuffer textureBuffer;
    /* access modifiers changed from: private */
    public BackupImageView textureOverlayView;
    /* access modifiers changed from: private */
    public TextureView textureView;
    /* access modifiers changed from: private */
    public Runnable timerRunnable = new Runnable() {
        public void run() {
            if (InstantCameraView.this.recording) {
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(InstantCameraView.this.recordingGuid), Long.valueOf(InstantCameraView.this.duration = System.currentTimeMillis() - InstantCameraView.this.recordStartTime), Double.valueOf(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50);
            }
        }
    };
    /* access modifiers changed from: private */
    public FloatBuffer vertexBuffer;
    /* access modifiers changed from: private */
    public VideoEditedInfo videoEditedInfo;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public InstantCameraView(Context context, ChatActivity parentFragment) {
        super(context);
        Context context2 = context;
        this.aspectRatio = SharedConfig.roundCamera16to9 ? new Size(16, 9) : new Size(4, 3);
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.moldSTMatrix = new float[16];
        setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return InstantCameraView.this.lambda$new$0$InstantCameraView(view, motionEvent);
            }
        });
        setWillNotDraw(false);
        setBackgroundColor(-1073741824);
        this.baseFragment = parentFragment;
        this.recordingGuid = parentFragment.getClassGuid();
        this.isSecretChat = this.baseFragment.getCurrentEncryptedChat() != null;
        AnonymousClass3 r7 = new Paint(1) {
            public void setAlpha(int a) {
                super.setAlpha(a);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint = r7;
        r7.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (Build.VERSION.SDK_INT >= 21) {
            AnonymousClass4 r72 = new FrameLayout(context2) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    InstantCameraView.this.invalidate();
                }
            };
            this.cameraContainer = r72;
            r72.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                }
            });
            this.cameraContainer.setClipToOutline(true);
            this.cameraContainer.setWillNotDraw(false);
        } else {
            final Path path = new Path();
            final Paint paint2 = new Paint(1);
            paint2.setColor(-16777216);
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            AnonymousClass6 r8 = new FrameLayout(context2) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    path.reset();
                    path.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Path.Direction.CW);
                    path.toggleInverseFillType();
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    try {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(path, paint2);
                    } catch (Exception e) {
                    }
                }
            };
            this.cameraContainer = r8;
            r8.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, (Paint) null);
        }
        addView(this.cameraContainer, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize, 17));
        ImageView imageView = new ImageView(context2);
        this.switchCameraButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", R.string.AccDescrSwitchCamera));
        addView(this.switchCameraButton, LayoutHelper.createFrame(48.0f, 48.0f, 83, 20.0f, 0.0f, 0.0f, 14.0f));
        this.switchCameraButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                InstantCameraView.this.lambda$new$1$InstantCameraView(view);
            }
        });
        ImageView imageView2 = new ImageView(context2);
        this.muteImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.muteImageView.setImageResource(R.drawable.video_mute);
        this.muteImageView.setAlpha(0.0f);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        ((FrameLayout.LayoutParams) this.muteImageView.getLayoutParams()).topMargin = (AndroidUtilities.roundMessageSize / 2) - AndroidUtilities.dp(24.0f);
        BackupImageView backupImageView = new BackupImageView(getContext());
        this.textureOverlayView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        addView(this.textureOverlayView, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize, 17));
        setVisibility(4);
    }

    public /* synthetic */ boolean lambda$new$0$InstantCameraView(View v, MotionEvent event) {
        ChatActivity chatActivity;
        if (event.getAction() == 0 && (chatActivity = this.baseFragment) != null) {
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                boolean mute = !videoPlayer2.isMuted();
                this.videoPlayer.setMute(mute);
                AnimatorSet animatorSet2 = this.muteAnimation;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.muteAnimation = animatorSet3;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView = this.muteImageView;
                float[] fArr = new float[1];
                float f = 1.0f;
                fArr[0] = mute ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView, "alpha", fArr);
                ImageView imageView2 = this.muteImageView;
                float[] fArr2 = new float[1];
                fArr2[0] = mute ? 1.0f : 0.5f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView2, "scaleX", fArr2);
                ImageView imageView3 = this.muteImageView;
                float[] fArr3 = new float[1];
                if (!mute) {
                    f = 0.5f;
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView3, "scaleY", fArr3);
                animatorSet3.playTogether(animatorArr);
                this.muteAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(InstantCameraView.this.muteAnimation)) {
                            AnimatorSet unused = InstantCameraView.this.muteAnimation = null;
                        }
                    }
                });
                this.muteAnimation.setDuration(180);
                this.muteAnimation.setInterpolator(new DecelerateInterpolator());
                this.muteAnimation.start();
            } else {
                chatActivity.checkRecordLocked();
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$new$1$InstantCameraView(View v) {
        CameraSession cameraSession2;
        if (this.cameraReady && (cameraSession2 = this.cameraSession) != null && cameraSession2.isInitied() && this.cameraThread != null) {
            switchCamera();
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    InstantCameraView.this.switchCameraButton.setImageResource(InstantCameraView.this.isFrontface ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                    ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
                }
            });
            animator.start();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getVisibility() != 0) {
            this.cameraContainer.setTranslationY((float) (getMeasuredHeight() / 2));
            this.textureOverlayView.setTranslationY((float) (getMeasuredHeight() / 2));
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.recordProgressChanged) {
            if (args[0].intValue() == this.recordingGuid) {
                long t = args[1].longValue();
                this.progress = ((float) t) / 60000.0f;
                this.recordedTime = t;
                invalidate();
            }
        } else if (id == NotificationCenter.FileDidUpload) {
            String location = args[0];
            File file2 = this.cameraFile;
            if (file2 != null && file2.getAbsolutePath().equals(location)) {
                this.file = args[1];
                this.encryptedFile = args[2];
                this.size = args[5].longValue();
                if (this.encryptedFile != null) {
                    this.key = args[3];
                    this.iv = args[4];
                }
            }
        }
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float x = this.cameraContainer.getX();
        float y = this.cameraContainer.getY();
        this.rect.set(x - ((float) AndroidUtilities.dp(8.0f)), y - ((float) AndroidUtilities.dp(8.0f)), ((float) this.cameraContainer.getMeasuredWidth()) + x + ((float) AndroidUtilities.dp(8.0f)), ((float) this.cameraContainer.getMeasuredHeight()) + y + ((float) AndroidUtilities.dp(8.0f)));
        float f = this.progress;
        if (f != 0.0f) {
            canvas.drawArc(this.rect, -90.0f, f * 360.0f, false, this.paint);
        }
        if (Theme.chat_roundVideoShadow != null) {
            int x1 = ((int) x) - AndroidUtilities.dp(3.0f);
            int y1 = ((int) y) - AndroidUtilities.dp(2.0f);
            canvas.save();
            canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), (float) ((AndroidUtilities.roundMessageSize / 2) + x1 + AndroidUtilities.dp(3.0f)), (float) ((AndroidUtilities.roundMessageSize / 2) + y1 + AndroidUtilities.dp(3.0f)));
            Theme.chat_roundVideoShadow.setAlpha((int) (this.cameraContainer.getAlpha() * 255.0f));
            Theme.chat_roundVideoShadow.setBounds(x1, y1, AndroidUtilities.roundMessageSize + x1 + AndroidUtilities.dp(6.0f), AndroidUtilities.roundMessageSize + y1 + AndroidUtilities.dp(6.0f));
            Theme.chat_roundVideoShadow.draw(canvas);
            canvas.restore();
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        setAlpha(0.0f);
        this.switchCameraButton.setAlpha(0.0f);
        this.cameraContainer.setAlpha(0.0f);
        this.textureOverlayView.setAlpha(0.0f);
        this.muteImageView.setAlpha(0.0f);
        this.muteImageView.setScaleX(1.0f);
        this.muteImageView.setScaleY(1.0f);
        this.cameraContainer.setScaleX(0.1f);
        this.cameraContainer.setScaleY(0.1f);
        this.textureOverlayView.setScaleX(0.1f);
        this.textureOverlayView.setScaleY(0.1f);
        if (this.cameraContainer.getMeasuredWidth() != 0) {
            FrameLayout frameLayout = this.cameraContainer;
            frameLayout.setPivotX((float) (frameLayout.getMeasuredWidth() / 2));
            FrameLayout frameLayout2 = this.cameraContainer;
            frameLayout2.setPivotY((float) (frameLayout2.getMeasuredHeight() / 2));
            BackupImageView backupImageView = this.textureOverlayView;
            backupImageView.setPivotX((float) (backupImageView.getMeasuredWidth() / 2));
            BackupImageView backupImageView2 = this.textureOverlayView;
            backupImageView2.setPivotY((float) (backupImageView2.getMeasuredHeight() / 2));
        }
        if (visibility == 0) {
            try {
                ((Activity) getContext()).getWindow().addFlags(128);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            ((Activity) getContext()).getWindow().clearFlags(128);
        }
    }

    public void showCamera() {
        if (this.textureView == null) {
            this.switchCameraButton.setImageResource(R.drawable.camera_revert1);
            this.textureOverlayView.setAlpha(1.0f);
            if (this.lastBitmap == null) {
                try {
                    this.lastBitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg").getAbsolutePath());
                } catch (Throwable th) {
                }
            }
            Bitmap bitmap = this.lastBitmap;
            if (bitmap != null) {
                this.textureOverlayView.setImageBitmap(bitmap);
            } else {
                this.textureOverlayView.setImageResource(R.drawable.icplaceholder);
            }
            this.cameraReady = false;
            this.isFrontface = true;
            this.selectedCamera = null;
            this.recordedTime = 0;
            this.progress = 0.0f;
            this.cancelled = false;
            this.file = null;
            this.encryptedFile = null;
            this.key = null;
            this.iv = null;
            if (initCamera()) {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
                File directory = FileLoader.getDirectory(4);
                this.cameraFile = new File(directory, SharedConfig.getLastLocalId() + ".mp4");
                SharedConfig.saveConfig();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("show round camera");
                }
                TextureView textureView2 = new TextureView(getContext());
                this.textureView = textureView2;
                textureView2.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("camera surface available");
                        }
                        if (InstantCameraView.this.cameraThread == null && surface != null && !InstantCameraView.this.cancelled) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("start create thread");
                            }
                            CameraGLThread unused = InstantCameraView.this.cameraThread = new CameraGLThread(surface, width, height);
                        }
                    }

                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        if (InstantCameraView.this.cameraThread != null) {
                            InstantCameraView.this.cameraThread.shutdown(0);
                            CameraGLThread unused = InstantCameraView.this.cameraThread = null;
                        }
                        if (InstantCameraView.this.cameraSession == null) {
                            return true;
                        }
                        CameraController.getInstance().close(InstantCameraView.this.cameraSession, (CountDownLatch) null, (Runnable) null);
                        return true;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    }
                });
                this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
                setVisibility(0);
                startAnimation(true);
            }
        }
    }

    public FrameLayout getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean open) {
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.showTemporary(!open);
        }
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.animatorSet = animatorSet3;
        Animator[] animatorArr = new Animator[12];
        float[] fArr = new float[1];
        float f = 0.0f;
        fArr[0] = open ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(this, "alpha", fArr);
        ImageView imageView = this.switchCameraButton;
        float[] fArr2 = new float[1];
        fArr2[0] = open ? 1.0f : 0.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(imageView, "alpha", fArr2);
        animatorArr[2] = ObjectAnimator.ofFloat(this.muteImageView, "alpha", new float[]{0.0f});
        Paint paint2 = this.paint;
        int[] iArr = new int[1];
        iArr[0] = open ? 255 : 0;
        animatorArr[3] = ObjectAnimator.ofInt(paint2, "alpha", iArr);
        FrameLayout frameLayout = this.cameraContainer;
        float[] fArr3 = new float[1];
        fArr3[0] = open ? 1.0f : 0.0f;
        animatorArr[4] = ObjectAnimator.ofFloat(frameLayout, "alpha", fArr3);
        FrameLayout frameLayout2 = this.cameraContainer;
        float[] fArr4 = new float[1];
        fArr4[0] = open ? 1.0f : 0.1f;
        animatorArr[5] = ObjectAnimator.ofFloat(frameLayout2, "scaleX", fArr4);
        FrameLayout frameLayout3 = this.cameraContainer;
        float[] fArr5 = new float[1];
        fArr5[0] = open ? 1.0f : 0.1f;
        animatorArr[6] = ObjectAnimator.ofFloat(frameLayout3, "scaleY", fArr5);
        FrameLayout frameLayout4 = this.cameraContainer;
        float[] fArr6 = new float[2];
        fArr6[0] = open ? (float) (getMeasuredHeight() / 2) : 0.0f;
        fArr6[1] = open ? 0.0f : (float) (getMeasuredHeight() / 2);
        animatorArr[7] = ObjectAnimator.ofFloat(frameLayout4, "translationY", fArr6);
        BackupImageView backupImageView = this.textureOverlayView;
        float[] fArr7 = new float[1];
        fArr7[0] = open ? 1.0f : 0.0f;
        animatorArr[8] = ObjectAnimator.ofFloat(backupImageView, "alpha", fArr7);
        BackupImageView backupImageView2 = this.textureOverlayView;
        float[] fArr8 = new float[1];
        fArr8[0] = open ? 1.0f : 0.1f;
        animatorArr[9] = ObjectAnimator.ofFloat(backupImageView2, "scaleX", fArr8);
        BackupImageView backupImageView3 = this.textureOverlayView;
        float[] fArr9 = new float[1];
        fArr9[0] = open ? 1.0f : 0.1f;
        animatorArr[10] = ObjectAnimator.ofFloat(backupImageView3, "scaleY", fArr9);
        BackupImageView backupImageView4 = this.textureOverlayView;
        float[] fArr10 = new float[2];
        fArr10[0] = open ? (float) (getMeasuredHeight() / 2) : 0.0f;
        if (!open) {
            f = (float) (getMeasuredHeight() / 2);
        }
        fArr10[1] = f;
        animatorArr[11] = ObjectAnimator.ofFloat(backupImageView4, "translationY", fArr10);
        animatorSet3.playTogether(animatorArr);
        if (!open) {
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.animatorSet)) {
                        InstantCameraView.this.hideCamera(true);
                        InstantCameraView.this.setVisibility(4);
                    }
                }
            });
        }
        this.animatorSet.setDuration(180);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.start();
    }

    public Rect getCameraRect() {
        this.cameraContainer.getLocationOnScreen(this.position);
        int[] iArr = this.position;
        return new Rect((float) iArr[0], (float) iArr[1], (float) this.cameraContainer.getWidth(), (float) this.cameraContainer.getHeight());
    }

    public void changeVideoPreviewState(int state, float progress2) {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            if (state == 0) {
                startProgressTimer();
                this.videoPlayer.play();
            } else if (state == 1) {
                stopProgressTimer();
                this.videoPlayer.pause();
            } else if (state == 2) {
                videoPlayer2.seekTo((long) (((float) videoPlayer2.getDuration()) * progress2));
            }
        }
    }

    public void send(int state, boolean notify, int scheduleDate) {
        int send;
        int i = state;
        int i2 = scheduleDate;
        if (this.textureView != null) {
            stopProgressTimer();
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.releasePlayer(true);
                this.videoPlayer = null;
            }
            if (i == 4) {
                if (this.videoEditedInfo.needConvert()) {
                    this.file = null;
                    this.encryptedFile = null;
                    this.key = null;
                    this.iv = null;
                    double totalDuration = (double) this.videoEditedInfo.estimatedDuration;
                    this.videoEditedInfo.estimatedDuration = (this.videoEditedInfo.endTime >= 0 ? this.videoEditedInfo.endTime : this.videoEditedInfo.estimatedDuration) - (this.videoEditedInfo.startTime >= 0 ? this.videoEditedInfo.startTime : 0);
                    VideoEditedInfo videoEditedInfo2 = this.videoEditedInfo;
                    videoEditedInfo2.estimatedSize = Math.max(1, (long) (((double) this.size) * (((double) videoEditedInfo2.estimatedDuration) / totalDuration)));
                    this.videoEditedInfo.bitrate = 400000;
                    if (this.videoEditedInfo.startTime > 0) {
                        this.videoEditedInfo.startTime *= 1000;
                    }
                    if (this.videoEditedInfo.endTime > 0) {
                        this.videoEditedInfo.endTime *= 1000;
                    }
                    FileLoader.getInstance(this.currentAccount).cancelUploadFile(this.cameraFile.getAbsolutePath(), false);
                } else {
                    this.videoEditedInfo.estimatedSize = Math.max(1, this.size);
                }
                this.videoEditedInfo.file = this.file;
                this.videoEditedInfo.encryptedFile = this.encryptedFile;
                this.videoEditedInfo.key = this.key;
                this.videoEditedInfo.iv = this.iv;
                this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.cameraFile.getAbsolutePath(), 0, true), this.videoEditedInfo, notify, i2);
                if (i2 != 0) {
                    startAnimation(false);
                    return;
                }
                return;
            }
            boolean z = notify;
            this.cancelled = this.recordedTime < 800;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            if (this.cameraThread != null) {
                NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                int i3 = NotificationCenter.recordStopped;
                Object[] objArr = new Object[2];
                objArr[0] = Integer.valueOf(this.recordingGuid);
                objArr[1] = Integer.valueOf((this.cancelled || i != 3) ? 0 : 2);
                instance.postNotificationName(i3, objArr);
                if (this.cancelled) {
                    send = 0;
                } else if (i == 3) {
                    send = 2;
                } else {
                    send = 1;
                }
                saveLastCameraBitmap();
                this.cameraThread.shutdown(send);
                this.cameraThread = null;
            }
            if (this.cancelled != 0) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), true);
                startAnimation(false);
            }
        }
    }

    private void saveLastCameraBitmap() {
        if (this.textureView.getBitmap() != null) {
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(this.textureView.getBitmap(), 80, 80, true);
            this.lastBitmap = createScaledBitmap;
            if (createScaledBitmap != null) {
                Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), this.lastBitmap.getHeight(), this.lastBitmap.getRowBytes());
                try {
                    this.lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg")));
                } catch (Throwable th) {
                }
            }
        }
    }

    public void cancel() {
        stopProgressTimer();
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.releasePlayer(true);
            this.videoPlayer = null;
        }
        if (this.textureView != null) {
            this.cancelled = true;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(this.recordingGuid), 0);
            if (this.cameraThread != null) {
                saveLastCameraBitmap();
                this.cameraThread.shutdown(0);
                this.cameraThread = null;
            }
            File file2 = this.cameraFile;
            if (file2 != null) {
                file2.delete();
                this.cameraFile = null;
            }
            startAnimation(false);
        }
    }

    public void setAlpha(float alpha) {
        ((ColorDrawable) getBackground()).setAlpha((int) (192.0f * alpha));
        invalidate();
    }

    public View getSwitchButtonView() {
        return this.switchCameraButton;
    }

    public View getMuteImageView() {
        return this.muteImageView;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void hideCamera(boolean async) {
        destroy(async, (Runnable) null);
        this.cameraContainer.removeView(this.textureView);
        this.cameraContainer.setTranslationX(0.0f);
        this.cameraContainer.setTranslationY(0.0f);
        this.textureOverlayView.setTranslationX(0.0f);
        this.textureOverlayView.setTranslationY(0.0f);
        this.textureView = null;
    }

    private void switchCamera() {
        saveLastCameraBitmap();
        Bitmap bitmap = this.lastBitmap;
        if (bitmap != null) {
            this.textureOverlayView.setImageBitmap(bitmap);
            this.textureOverlayView.animate().setDuration(120).alpha(1.0f).setInterpolator(new DecelerateInterpolator()).start();
        }
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, (CountDownLatch) null, (Runnable) null);
            this.cameraSession = null;
        }
        this.isFrontface = !this.isFrontface;
        initCamera();
        this.cameraReady = false;
        this.cameraThread.reinitForNewCamera();
    }

    private boolean initCamera() {
        CameraInfo cameraInfo;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos == null) {
            return false;
        }
        CameraInfo notFrontface = null;
        int a = 0;
        while (true) {
            if (a >= cameraInfos.size()) {
                break;
            }
            cameraInfo = cameraInfos.get(a);
            if (!cameraInfo.isFrontface()) {
                notFrontface = cameraInfo;
            }
            if ((!this.isFrontface || !cameraInfo.isFrontface()) && (this.isFrontface || cameraInfo.isFrontface())) {
                notFrontface = cameraInfo;
                a++;
            }
        }
        this.selectedCamera = cameraInfo;
        if (this.selectedCamera == null) {
            this.selectedCamera = notFrontface;
        }
        CameraInfo cameraInfo2 = this.selectedCamera;
        if (cameraInfo2 == null) {
            return false;
        }
        ArrayList<Size> previewSizes = cameraInfo2.getPreviewSizes();
        ArrayList<Size> pictureSizes = this.selectedCamera.getPictureSizes();
        this.previewSize = CameraController.chooseOptimalSize(previewSizes, UVCCamera.DEFAULT_PREVIEW_HEIGHT, 270, this.aspectRatio);
        this.pictureSize = CameraController.chooseOptimalSize(pictureSizes, UVCCamera.DEFAULT_PREVIEW_HEIGHT, 270, this.aspectRatio);
        if (this.previewSize.mWidth != this.pictureSize.mWidth) {
            boolean found = false;
            for (int a2 = previewSizes.size() - 1; a2 >= 0; a2--) {
                Size preview = previewSizes.get(a2);
                int b = pictureSizes.size() - 1;
                while (true) {
                    if (b < 0) {
                        break;
                    }
                    Size picture = pictureSizes.get(b);
                    if (preview.mWidth >= this.pictureSize.mWidth && preview.mHeight >= this.pictureSize.mHeight && preview.mWidth == picture.mWidth && preview.mHeight == picture.mHeight) {
                        this.previewSize = preview;
                        this.pictureSize = picture;
                        found = true;
                        break;
                    }
                    b--;
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                for (int a3 = previewSizes.size() - 1; a3 >= 0; a3--) {
                    Size preview2 = previewSizes.get(a3);
                    int b2 = pictureSizes.size() - 1;
                    while (true) {
                        if (b2 < 0) {
                            break;
                        }
                        Size picture2 = pictureSizes.get(b2);
                        if (preview2.mWidth >= 240 && preview2.mHeight >= 240 && preview2.mWidth == picture2.mWidth && preview2.mHeight == picture2.mHeight) {
                            this.previewSize = preview2;
                            this.pictureSize = picture2;
                            found = true;
                            break;
                        }
                        b2--;
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("preview w = " + this.previewSize.mWidth + " h = " + this.previewSize.mHeight);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void createCamera(SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new Runnable(surfaceTexture) {
            private final /* synthetic */ SurfaceTexture f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                InstantCameraView.this.lambda$createCamera$4$InstantCameraView(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$createCamera$4$InstantCameraView(SurfaceTexture surfaceTexture) {
        if (this.cameraThread != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("create camera session");
            }
            surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
            CameraSession cameraSession2 = new CameraSession(this.selectedCamera, this.previewSize, this.pictureSize, 256);
            this.cameraSession = cameraSession2;
            this.cameraThread.setCurrentSession(cameraSession2);
            CameraController.getInstance().openRound(this.cameraSession, surfaceTexture, new Runnable() {
                public final void run() {
                    InstantCameraView.this.lambda$null$2$InstantCameraView();
                }
            }, new Runnable() {
                public final void run() {
                    InstantCameraView.this.lambda$null$3$InstantCameraView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$InstantCameraView() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("camera initied");
            }
            this.cameraSession.setInitied();
        }
    }

    public /* synthetic */ void lambda$null$3$InstantCameraView() {
        this.cameraThread.setCurrentSession(this.cameraSession);
    }

    /* access modifiers changed from: private */
    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] != 0) {
            return shader;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(shader));
        }
        GLES20.glDeleteShader(shader);
        return 0;
    }

    /* access modifiers changed from: private */
    public void startProgressTimer() {
        Timer timer = this.progressTimer;
        if (timer != null) {
            try {
                timer.cancel();
                this.progressTimer = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        Timer timer2 = new Timer();
        this.progressTimer = timer2;
        timer2.schedule(new TimerTask() {
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        InstantCameraView.AnonymousClass10.this.lambda$run$0$InstantCameraView$10();
                    }
                });
            }

            public /* synthetic */ void lambda$run$0$InstantCameraView$10() {
                try {
                    if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoEditedInfo != null) {
                        long j = 0;
                        if (InstantCameraView.this.videoEditedInfo.endTime > 0 && InstantCameraView.this.videoPlayer.getCurrentPosition() >= InstantCameraView.this.videoEditedInfo.endTime) {
                            VideoPlayer access$1000 = InstantCameraView.this.videoPlayer;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$1000.seekTo(j);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }, 0, 17);
    }

    private void stopProgressTimer() {
        Timer timer = this.progressTimer;
        if (timer != null) {
            try {
                timer.cancel();
                this.progressTimer = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public class CameraGLThread extends DispatchQueue {
        private final int DO_REINIT_MESSAGE = 2;
        private final int DO_RENDER_MESSAGE = 0;
        private final int DO_SETSESSION_MESSAGE = 3;
        private final int DO_SHUTDOWN_MESSAGE = 1;
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        private Integer cameraId = 0;
        private SurfaceTexture cameraSurface;
        private CameraSession currentSession;
        private int drawProgram;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private GL gl;
        private boolean initied;
        private int positionHandle;
        private boolean recording;
        private int rotationAngle;
        private SurfaceTexture surfaceTexture;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private VideoRecorder videoEncoder;

        public CameraGLThread(SurfaceTexture surface, int surfaceWidth, int surfaceHeight) {
            super("CameraGLThread");
            this.surfaceTexture = surface;
            int width = InstantCameraView.this.previewSize.getWidth();
            int height = InstantCameraView.this.previewSize.getHeight();
            float scale = ((float) surfaceWidth) / ((float) Math.min(width, height));
            int width2 = (int) (((float) width) * scale);
            int height2 = (int) (((float) height) * scale);
            if (width2 > height2) {
                float unused = InstantCameraView.this.scaleX = 1.0f;
                float unused2 = InstantCameraView.this.scaleY = ((float) width2) / ((float) surfaceHeight);
                return;
            }
            float unused3 = InstantCameraView.this.scaleX = ((float) height2) / ((float) surfaceWidth);
            float unused4 = InstantCameraView.this.scaleY = 1.0f;
        }

        private boolean initGL() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start init gl");
            }
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eGLConfig = configs[0];
                this.eglConfig = eGLConfig;
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    EGL10 egl103 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl103.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    this.gl = this.eglContext.getGL();
                    float tX = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                    float tY = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                    float[] verticesData = {-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                    float[] texData = {0.5f - tX, 0.5f - tY, tX + 0.5f, 0.5f - tY, 0.5f - tX, tY + 0.5f, tX + 0.5f, 0.5f + tY};
                    this.videoEncoder = new VideoRecorder();
                    FloatBuffer unused = InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                    InstantCameraView.this.vertexBuffer.put(verticesData).position(0);
                    FloatBuffer unused2 = InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                    InstantCameraView.this.textureBuffer.put(texData).position(0);
                    Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                    int vertexShader = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                    int fragmentShader = InstantCameraView.this.loadShader(35632, InstantCameraView.FRAGMENT_SCREEN_SHADER);
                    if (vertexShader == 0 || fragmentShader == 0) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("failed creating shader");
                        }
                        finish();
                        return false;
                    }
                    int glCreateProgram = GLES20.glCreateProgram();
                    this.drawProgram = glCreateProgram;
                    GLES20.glAttachShader(glCreateProgram, vertexShader);
                    GLES20.glAttachShader(this.drawProgram, fragmentShader);
                    GLES20.glLinkProgram(this.drawProgram);
                    int[] linkStatus = new int[1];
                    GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                    if (linkStatus[0] == 0) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("failed link shader");
                        }
                        GLES20.glDeleteProgram(this.drawProgram);
                        this.drawProgram = 0;
                    } else {
                        this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                        this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                        this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                        this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                    }
                    GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                    GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                    GLES20.glTexParameteri(36197, 10241, 9729);
                    GLES20.glTexParameteri(36197, 10240, 9729);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                    SurfaceTexture surfaceTexture3 = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                    this.cameraSurface = surfaceTexture3;
                    surfaceTexture3.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                        public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                            InstantCameraView.CameraGLThread.this.lambda$initGL$0$InstantCameraView$CameraGLThread(surfaceTexture);
                        }
                    });
                    InstantCameraView.this.createCamera(this.cameraSurface);
                    if (!BuildVars.LOGS_ENABLED) {
                        return true;
                    }
                    FileLog.e("gl initied");
                    return true;
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        public /* synthetic */ void lambda$initGL$0$InstantCameraView$CameraGLThread(SurfaceTexture surfaceTexture2) {
            requestRender();
        }

        public void reinitForNewCamera() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(2), 0);
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        public void setCurrentSession(CameraSession session) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, session), 0);
            }
        }

        private void onDraw(Integer cameraId2) {
            if (this.initied) {
                if (!this.eglContext.equals(this.egl10.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                    EGL10 egl102 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            return;
                        }
                        return;
                    }
                }
                this.cameraSurface.updateTexImage();
                if (!this.recording) {
                    this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, EGL14.eglGetCurrentContext());
                    this.recording = true;
                    int orientation = this.currentSession.getCurrentOrientation();
                    if (orientation == 90 || orientation == 270) {
                        float temp = InstantCameraView.this.scaleX;
                        InstantCameraView instantCameraView = InstantCameraView.this;
                        float unused = instantCameraView.scaleX = instantCameraView.scaleY;
                        float unused2 = InstantCameraView.this.scaleY = temp;
                    }
                }
                this.videoEncoder.frameAvailable(this.cameraSurface, cameraId2, System.nanoTime());
                this.cameraSurface.getTransformMatrix(InstantCameraView.this.mSTMatrix);
                GLES20.glUseProgram(this.drawProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
                GLES20.glEnableVertexAttribArray(this.positionHandle);
                GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.textureHandle);
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
                GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glDisableVertexAttribArray(this.positionHandle);
                GLES20.glDisableVertexAttribArray(this.textureHandle);
                GLES20.glBindTexture(36197, 0);
                GLES20.glUseProgram(0);
                this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            }
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            if (what == 0) {
                onDraw((Integer) inputMessage.obj);
            } else if (what == 1) {
                finish();
                if (this.recording) {
                    this.videoEncoder.stopRecording(inputMessage.arg1);
                }
                Looper looper = Looper.myLooper();
                if (looper != null) {
                    looper.quit();
                }
            } else if (what == 2) {
                EGL10 egl102 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurface;
                if (egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    SurfaceTexture surfaceTexture2 = this.cameraSurface;
                    if (surfaceTexture2 != null) {
                        surfaceTexture2.getTransformMatrix(InstantCameraView.this.moldSTMatrix);
                        this.cameraSurface.setOnFrameAvailableListener((SurfaceTexture.OnFrameAvailableListener) null);
                        this.cameraSurface.release();
                        InstantCameraView.this.oldCameraTexture[0] = InstantCameraView.this.cameraTexture[0];
                        float unused = InstantCameraView.this.cameraTextureAlpha = 0.0f;
                        InstantCameraView.this.cameraTexture[0] = 0;
                    }
                    this.cameraId = Integer.valueOf(this.cameraId.intValue() + 1);
                    boolean unused2 = InstantCameraView.this.cameraReady = false;
                    GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                    GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                    GLES20.glTexParameteri(36197, 10241, 9729);
                    GLES20.glTexParameteri(36197, 10240, 9729);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    SurfaceTexture surfaceTexture3 = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                    this.cameraSurface = surfaceTexture3;
                    surfaceTexture3.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                        public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                            InstantCameraView.CameraGLThread.this.lambda$handleMessage$1$InstantCameraView$CameraGLThread(surfaceTexture);
                        }
                    });
                    InstantCameraView.this.createCamera(this.cameraSurface);
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
            } else if (what == 3) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("set gl rednderer session");
                }
                CameraSession newSession = (CameraSession) inputMessage.obj;
                CameraSession cameraSession = this.currentSession;
                if (cameraSession == newSession) {
                    this.rotationAngle = cameraSession.getWorldAngle();
                    Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                    if (this.rotationAngle != 0) {
                        Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, (float) this.rotationAngle, 0.0f, 0.0f, 1.0f);
                        return;
                    }
                    return;
                }
                this.currentSession = newSession;
            }
        }

        public /* synthetic */ void lambda$handleMessage$1$InstantCameraView$CameraGLThread(SurfaceTexture surfaceTexture2) {
            requestRender();
        }

        public void shutdown(int send) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, send, 0), 0);
            }
        }

        public void requestRender() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0, this.cameraId), 0);
            }
        }
    }

    private static class EncoderHandler extends Handler {
        private WeakReference<VideoRecorder> mWeakEncoder;

        public EncoderHandler(VideoRecorder encoder) {
            this.mWeakEncoder = new WeakReference<>(encoder);
        }

        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            VideoRecorder encoder = (VideoRecorder) this.mWeakEncoder.get();
            if (encoder != null) {
                if (what == 0) {
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("start encoder");
                        }
                        encoder.prepareEncoder();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        encoder.handleStopRecording(0);
                        Looper.myLooper().quit();
                    }
                } else if (what == 1) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stop encoder");
                    }
                    encoder.handleStopRecording(inputMessage.arg1);
                } else if (what == 2) {
                    encoder.handleVideoFrameAvailable((((long) inputMessage.arg1) << 32) | (((long) inputMessage.arg2) & 4294967295L), (Integer) inputMessage.obj);
                } else if (what == 3) {
                    encoder.handleAudioFrameAvailable((AudioBufferInfo) inputMessage.obj);
                }
            }
        }

        public void exit() {
            Looper.myLooper().quit();
        }
    }

    private class AudioBufferInfo {
        byte[] buffer;
        boolean last;
        int lastWroteBuffer;
        long[] offset;
        int[] read;
        int results;

        private AudioBufferInfo() {
            this.buffer = new byte[CacheDataSink.DEFAULT_BUFFER_SIZE];
            this.offset = new long[10];
            this.read = new int[10];
        }
    }

    private class VideoRecorder implements Runnable {
        private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
        private static final int FRAME_RATE = 30;
        private static final int IFRAME_INTERVAL = 1;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private int alphaHandle;
        private MediaCodec.BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioFirst;
        /* access modifiers changed from: private */
        public AudioRecord audioRecorder;
        private long audioStartTime;
        private boolean audioStopedByTime;
        private int audioTrackIndex;
        private boolean blendEnabled;
        /* access modifiers changed from: private */
        public ArrayBlockingQueue<AudioBufferInfo> buffers;
        private ArrayList<AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private long desyncTime;
        private int drawProgram;
        private android.opengl.EGLConfig eglConfig;
        private android.opengl.EGLContext eglContext;
        private android.opengl.EGLDisplay eglDisplay;
        private android.opengl.EGLSurface eglSurface;
        /* access modifiers changed from: private */
        public volatile EncoderHandler handler;
        private Integer lastCameraId;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private boolean ready;
        private Runnable recorderRunnable;
        /* access modifiers changed from: private */
        public volatile boolean running;
        private int scaleXHandle;
        private int scaleYHandle;
        /* access modifiers changed from: private */
        public volatile int sendWhenDone;
        private android.opengl.EGLContext sharedEglContext;
        private boolean skippedFirst;
        private long skippedTime;
        private Surface surface;
        private final Object sync;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private MediaCodec.BufferInfo videoBufferInfo;
        private boolean videoConvertFirstWrite;
        private MediaCodec videoEncoder;
        private File videoFile;
        private long videoFirst;
        private int videoHeight;
        private long videoLast;
        private int videoTrackIndex;
        private int videoWidth;
        private int zeroTimeStamps;

        private VideoRecorder() {
            this.videoConvertFirstWrite = true;
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            this.buffersToWrite = new ArrayList<>();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.audioStartTime = -1;
            this.currentTimestamp = 0;
            this.lastTimestamp = -1;
            this.sync = new Object();
            this.videoFirst = -1;
            this.audioFirst = -1;
            this.lastCameraId = 0;
            this.buffers = new ArrayBlockingQueue<>(10);
            this.recorderRunnable = new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:12:0x002d, code lost:
                    if (im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.access$3400(r11.this$1) == 0) goto L_0x00e4;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r11 = this;
                        r0 = -1
                        r2 = 0
                    L_0x0003:
                        r3 = 0
                        r4 = 1
                        if (r2 != 0) goto L_0x00e4
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        boolean r5 = r5.running
                        if (r5 != 0) goto L_0x0031
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r5 = r5.audioRecorder
                        int r5 = r5.getRecordingState()
                        if (r5 == r4) goto L_0x0031
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0025 }
                        android.media.AudioRecord r5 = r5.audioRecorder     // Catch:{ Exception -> 0x0025 }
                        r5.stop()     // Catch:{ Exception -> 0x0025 }
                        goto L_0x0027
                    L_0x0025:
                        r5 = move-exception
                        r2 = 1
                    L_0x0027:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        int r5 = r5.sendWhenDone
                        if (r5 != 0) goto L_0x0031
                        goto L_0x00e4
                    L_0x0031:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r5 = r5.buffers
                        boolean r5 = r5.isEmpty()
                        if (r5 == 0) goto L_0x0048
                        im.bclpbkiauv.ui.components.InstantCameraView$AudioBufferInfo r5 = new im.bclpbkiauv.ui.components.InstantCameraView$AudioBufferInfo
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r6 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        im.bclpbkiauv.ui.components.InstantCameraView r6 = im.bclpbkiauv.ui.components.InstantCameraView.this
                        r7 = 0
                        r5.<init>()
                        goto L_0x0054
                    L_0x0048:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r5 = r5.buffers
                        java.lang.Object r5 = r5.poll()
                        im.bclpbkiauv.ui.components.InstantCameraView$AudioBufferInfo r5 = (im.bclpbkiauv.ui.components.InstantCameraView.AudioBufferInfo) r5
                    L_0x0054:
                        r5.lastWroteBuffer = r3
                        r3 = 10
                        r5.results = r3
                        r6 = 0
                    L_0x005b:
                        if (r6 >= r3) goto L_0x00a2
                        r7 = -1
                        int r9 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
                        if (r9 != 0) goto L_0x006b
                        long r7 = java.lang.System.nanoTime()
                        r9 = 1000(0x3e8, double:4.94E-321)
                        long r0 = r7 / r9
                    L_0x006b:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r7 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        android.media.AudioRecord r7 = r7.audioRecorder
                        byte[] r8 = r5.buffer
                        int r9 = r6 * 2048
                        r10 = 2048(0x800, float:2.87E-42)
                        int r7 = r7.read(r8, r9, r10)
                        if (r7 > 0) goto L_0x008a
                        r5.results = r6
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r8 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        boolean r8 = r8.running
                        if (r8 != 0) goto L_0x00a2
                        r5.last = r4
                        goto L_0x00a2
                    L_0x008a:
                        long[] r8 = r5.offset
                        r8[r6] = r0
                        int[] r8 = r5.read
                        r8[r6] = r7
                        r8 = 1000000(0xf4240, float:1.401298E-39)
                        int r8 = r8 * r7
                        r9 = 44100(0xac44, float:6.1797E-41)
                        int r8 = r8 / r9
                        int r8 = r8 / 2
                        long r9 = (long) r8
                        long r0 = r0 + r9
                        int r6 = r6 + 1
                        goto L_0x005b
                    L_0x00a2:
                        int r4 = r5.results
                        if (r4 >= 0) goto L_0x00c1
                        boolean r4 = r5.last
                        if (r4 == 0) goto L_0x00ab
                        goto L_0x00c1
                    L_0x00ab:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r3 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        boolean r3 = r3.running
                        if (r3 != 0) goto L_0x00b5
                        r2 = 1
                        goto L_0x00e2
                    L_0x00b5:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r3 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00bf }
                        java.util.concurrent.ArrayBlockingQueue r3 = r3.buffers     // Catch:{ Exception -> 0x00bf }
                        r3.put(r5)     // Catch:{ Exception -> 0x00bf }
                        goto L_0x00e2
                    L_0x00bf:
                        r3 = move-exception
                        goto L_0x00e2
                    L_0x00c1:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r4 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        boolean r4 = r4.running
                        if (r4 != 0) goto L_0x00ce
                        int r4 = r5.results
                        if (r4 >= r3) goto L_0x00ce
                        r2 = 1
                    L_0x00ce:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r3 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        im.bclpbkiauv.ui.components.InstantCameraView$EncoderHandler r3 = r3.handler
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r4 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        im.bclpbkiauv.ui.components.InstantCameraView$EncoderHandler r4 = r4.handler
                        r6 = 3
                        android.os.Message r4 = r4.obtainMessage(r6, r5)
                        r3.sendMessage(r4)
                    L_0x00e2:
                        goto L_0x0003
                    L_0x00e4:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00ee }
                        android.media.AudioRecord r5 = r5.audioRecorder     // Catch:{ Exception -> 0x00ee }
                        r5.release()     // Catch:{ Exception -> 0x00ee }
                        goto L_0x00f2
                    L_0x00ee:
                        r5 = move-exception
                        im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r5)
                    L_0x00f2:
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r5 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        im.bclpbkiauv.ui.components.InstantCameraView$EncoderHandler r5 = r5.handler
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r6 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        im.bclpbkiauv.ui.components.InstantCameraView$EncoderHandler r6 = r6.handler
                        im.bclpbkiauv.ui.components.InstantCameraView$VideoRecorder r7 = im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.this
                        int r7 = r7.sendWhenDone
                        android.os.Message r3 = r6.obtainMessage(r4, r7, r3)
                        r5.sendMessage(r3)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.AnonymousClass1.run():void");
                }
            };
        }

        public void startRecording(File outputFile, android.opengl.EGLContext sharedContext) {
            int bitrate;
            int resolution;
            String model = Build.DEVICE;
            if (model == null) {
                model = "";
            }
            if (model.startsWith("zeroflte") || model.startsWith("zenlte")) {
                resolution = 320;
                bitrate = 600000;
            } else {
                resolution = PsExtractor.VIDEO_STREAM_MASK;
                bitrate = 400000;
            }
            this.videoFile = outputFile;
            this.videoWidth = resolution;
            this.videoHeight = resolution;
            this.videoBitrate = bitrate;
            this.sharedEglContext = sharedContext;
            synchronized (this.sync) {
                if (!this.running) {
                    this.running = true;
                    Thread thread = new Thread(this, "TextureMovieEncoder");
                    thread.setPriority(10);
                    thread.start();
                    while (!this.ready) {
                        try {
                            this.sync.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                    this.handler.sendMessage(this.handler.obtainMessage(0));
                }
            }
        }

        public void stopRecording(int send) {
            this.handler.sendMessage(this.handler.obtainMessage(1, send, 0));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
            r2 = r7.zeroTimeStamps + 1;
            r7.zeroTimeStamps = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
            if (r2 <= 1) goto L_0x0027;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
            if (im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0025;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
            im.bclpbkiauv.messenger.FileLog.d("fix timestamp enabled");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
            r0 = r10;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0028, code lost:
            r7.zeroTimeStamps = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
            r7.handler.sendMessage(r7.handler.obtainMessage(2, (int) (r0 >> 32), (int) r0, r9));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x000a, code lost:
            r0 = r8.getTimestamp();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r0 != 0) goto L_0x0028;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(android.graphics.SurfaceTexture r8, java.lang.Integer r9, long r10) {
            /*
                r7 = this;
                java.lang.Object r0 = r7.sync
                monitor-enter(r0)
                boolean r1 = r7.ready     // Catch:{ all -> 0x003e }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                return
            L_0x0009:
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                long r0 = r8.getTimestamp()
                r2 = 0
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 != 0) goto L_0x0028
                int r2 = r7.zeroTimeStamps
                r3 = 1
                int r2 = r2 + r3
                r7.zeroTimeStamps = r2
                if (r2 <= r3) goto L_0x0027
                boolean r2 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x0025
                java.lang.String r2 = "fix timestamp enabled"
                im.bclpbkiauv.messenger.FileLog.d(r2)
            L_0x0025:
                r0 = r10
                goto L_0x002b
            L_0x0027:
                return
            L_0x0028:
                r2 = 0
                r7.zeroTimeStamps = r2
            L_0x002b:
                im.bclpbkiauv.ui.components.InstantCameraView$EncoderHandler r2 = r7.handler
                im.bclpbkiauv.ui.components.InstantCameraView$EncoderHandler r3 = r7.handler
                r4 = 2
                r5 = 32
                long r5 = r0 >> r5
                int r6 = (int) r5
                int r5 = (int) r0
                android.os.Message r3 = r3.obtainMessage(r4, r6, r5, r9)
                r2.sendMessage(r3)
                return
            L_0x003e:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.InstantCameraView.VideoRecorder.frameAvailable(android.graphics.SurfaceTexture, java.lang.Integer, long):void");
        }

        public void run() {
            Looper.prepare();
            synchronized (this.sync) {
                this.handler = new EncoderHandler(this);
                this.ready = true;
                this.sync.notify();
            }
            Looper.loop();
            synchronized (this.sync) {
                this.ready = false;
            }
        }

        /* access modifiers changed from: private */
        public void handleAudioFrameAvailable(AudioBufferInfo input) {
            ByteBuffer inputBuffer;
            if (!this.audioStopedByTime) {
                AudioBufferInfo input2 = input;
                this.buffersToWrite.add(input2);
                if (this.audioFirst == -1) {
                    if (this.videoFirst != -1) {
                        while (true) {
                            boolean ok = false;
                            int a = 0;
                            while (true) {
                                if (a >= input2.results) {
                                    break;
                                } else if (a == 0 && Math.abs(this.videoFirst - input2.offset[a]) > 10000000) {
                                    this.desyncTime = this.videoFirst - input2.offset[a];
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("detected desync between audio and video " + this.desyncTime);
                                    }
                                } else if (input2.offset[a] >= this.videoFirst) {
                                    input2.lastWroteBuffer = a;
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("found first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("ignore first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                    a++;
                                }
                            }
                            if (ok) {
                                break;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("first audio frame not found, removing buffers " + input2.results);
                            }
                            this.buffersToWrite.remove(input2);
                            if (!this.buffersToWrite.isEmpty()) {
                                input2 = this.buffersToWrite.get(0);
                            } else {
                                return;
                            }
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("video record not yet started");
                        return;
                    } else {
                        return;
                    }
                }
                if (this.audioStartTime == -1) {
                    this.audioStartTime = input2.offset[input2.lastWroteBuffer];
                }
                if (this.buffersToWrite.size() > 1) {
                    input2 = this.buffersToWrite.get(0);
                }
                try {
                    drainEncoder(false);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                boolean isLast = false;
                while (input2 != null) {
                    try {
                        int inputBufferIndex = this.audioEncoder.dequeueInputBuffer(0);
                        if (inputBufferIndex >= 0) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                inputBuffer = this.audioEncoder.getInputBuffer(inputBufferIndex);
                            } else {
                                ByteBuffer inputBuffer2 = this.audioEncoder.getInputBuffers()[inputBufferIndex];
                                inputBuffer2.clear();
                                inputBuffer = inputBuffer2;
                            }
                            long startWriteTime = input2.offset[input2.lastWroteBuffer];
                            int a2 = input2.lastWroteBuffer;
                            while (true) {
                                if (a2 > input2.results) {
                                    break;
                                }
                                if (a2 < input2.results) {
                                    if (!this.running && input2.offset[a2] >= this.videoLast - this.desyncTime) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("stop audio encoding because of stoped video recording at " + input2.offset[a2] + " last video " + this.videoLast);
                                        }
                                        this.audioStopedByTime = true;
                                        isLast = true;
                                        input2 = null;
                                        this.buffersToWrite.clear();
                                    } else if (inputBuffer.remaining() < input2.read[a2]) {
                                        input2.lastWroteBuffer = a2;
                                        input2 = null;
                                        break;
                                    } else {
                                        inputBuffer.put(input2.buffer, a2 * 2048, input2.read[a2]);
                                    }
                                }
                                if (a2 >= input2.results - 1) {
                                    this.buffersToWrite.remove(input2);
                                    if (this.running) {
                                        this.buffers.put(input2);
                                    }
                                    if (this.buffersToWrite.isEmpty()) {
                                        isLast = input2.last;
                                        input2 = null;
                                        break;
                                    }
                                    input2 = this.buffersToWrite.get(0);
                                }
                                a2++;
                            }
                            MediaCodec mediaCodec = this.audioEncoder;
                            int position = inputBuffer.position();
                            long j = 0;
                            if (startWriteTime != 0) {
                                j = startWriteTime - this.audioStartTime;
                            }
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, position, j, isLast ? 4 : 0);
                        }
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void handleVideoFrameAvailable(long timestampNanos, Integer cameraId) {
            long dt;
            long alphaDt;
            long j = timestampNanos;
            Integer num = cameraId;
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (!this.lastCameraId.equals(num)) {
                this.lastTimestamp = -1;
                this.lastCameraId = num;
            }
            long dt2 = this.lastTimestamp;
            if (dt2 == -1) {
                this.lastTimestamp = j;
                dt = 0;
                if (this.currentTimestamp != 0) {
                    alphaDt = (System.currentTimeMillis() - this.lastCommitedFrameTime) * 1000000;
                    dt = 0;
                } else {
                    alphaDt = 0;
                }
            } else {
                alphaDt = j - dt2;
                long j2 = alphaDt;
                this.lastTimestamp = j;
                dt = alphaDt;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (!this.skippedFirst) {
                long j3 = this.skippedTime + alphaDt;
                this.skippedTime = j3;
                if (j3 >= 200000000) {
                    this.skippedFirst = true;
                } else {
                    return;
                }
            }
            this.currentTimestamp += alphaDt;
            if (this.videoFirst == -1) {
                this.videoFirst = j / 1000;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("first video frame was at " + this.videoFirst);
                }
            }
            this.videoLast = j;
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniform1f(this.scaleXHandle, InstantCameraView.this.scaleX);
            GLES20.glUniform1f(this.scaleYHandle, InstantCameraView.this.scaleY);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
            GLES20.glActiveTexture(33984);
            if (InstantCameraView.this.oldCameraTexture[0] != 0) {
                if (!this.blendEnabled) {
                    GLES20.glEnable(3042);
                    this.blendEnabled = true;
                }
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.moldSTMatrix, 0);
                GLES20.glUniform1f(this.alphaHandle, 1.0f);
                GLES20.glBindTexture(36197, InstantCameraView.this.oldCameraTexture[0]);
                GLES20.glDrawArrays(5, 0, 4);
            }
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
            GLES20.glUniform1f(this.alphaHandle, InstantCameraView.this.cameraTextureAlpha);
            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glDisableVertexAttribArray(this.positionHandle);
            GLES20.glDisableVertexAttribArray(this.textureHandle);
            GLES20.glBindTexture(36197, 0);
            GLES20.glUseProgram(0);
            EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, this.currentTimestamp);
            EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            if (InstantCameraView.this.oldCameraTexture[0] != 0 && InstantCameraView.this.cameraTextureAlpha < 1.0f) {
                InstantCameraView instantCameraView = InstantCameraView.this;
                float unused = instantCameraView.cameraTextureAlpha = instantCameraView.cameraTextureAlpha + (((float) dt) / 2.0E8f);
                if (InstantCameraView.this.cameraTextureAlpha > 1.0f) {
                    GLES20.glDisable(3042);
                    this.blendEnabled = false;
                    float unused2 = InstantCameraView.this.cameraTextureAlpha = 1.0f;
                    GLES20.glDeleteTextures(1, InstantCameraView.this.oldCameraTexture, 0);
                    InstantCameraView.this.oldCameraTexture[0] = 0;
                    if (!InstantCameraView.this.cameraReady) {
                        boolean unused3 = InstantCameraView.this.cameraReady = true;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                InstantCameraView.VideoRecorder.this.lambda$handleVideoFrameAvailable$0$InstantCameraView$VideoRecorder();
                            }
                        });
                    }
                }
            } else if (!InstantCameraView.this.cameraReady) {
                boolean unused4 = InstantCameraView.this.cameraReady = true;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        InstantCameraView.VideoRecorder.this.lambda$handleVideoFrameAvailable$1$InstantCameraView$VideoRecorder();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$handleVideoFrameAvailable$0$InstantCameraView$VideoRecorder() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        public /* synthetic */ void lambda$handleVideoFrameAvailable$1$InstantCameraView$VideoRecorder() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        /* access modifiers changed from: private */
        public void handleStopRecording(int send) {
            if (this.running) {
                this.sendWhenDone = send;
                this.running = false;
                return;
            }
            try {
                drainEncoder(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            MediaCodec mediaCodec = this.videoEncoder;
            if (mediaCodec != null) {
                try {
                    mediaCodec.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            MediaCodec mediaCodec2 = this.audioEncoder;
            if (mediaCodec2 != null) {
                try {
                    mediaCodec2.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            MP4Builder mP4Builder = this.mediaMuxer;
            if (mP4Builder != null) {
                try {
                    mP4Builder.finishMovie();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            if (send != 0) {
                AndroidUtilities.runOnUIThread(new Runnable(send) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        InstantCameraView.VideoRecorder.this.lambda$handleStopRecording$3$InstantCameraView$VideoRecorder(this.f$1);
                    }
                });
            } else {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).cancelUploadFile(this.videoFile.getAbsolutePath(), false);
                this.videoFile.delete();
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            Surface surface2 = this.surface;
            if (surface2 != null) {
                surface2.release();
                this.surface = null;
            }
            if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(this.eglDisplay);
            }
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglConfig = null;
            this.handler.exit();
        }

        public /* synthetic */ void lambda$handleStopRecording$3$InstantCameraView$VideoRecorder(int send) {
            VideoEditedInfo unused = InstantCameraView.this.videoEditedInfo = new VideoEditedInfo();
            InstantCameraView.this.videoEditedInfo.roundVideo = true;
            InstantCameraView.this.videoEditedInfo.startTime = -1;
            InstantCameraView.this.videoEditedInfo.endTime = -1;
            InstantCameraView.this.videoEditedInfo.file = InstantCameraView.this.file;
            InstantCameraView.this.videoEditedInfo.encryptedFile = InstantCameraView.this.encryptedFile;
            InstantCameraView.this.videoEditedInfo.key = InstantCameraView.this.key;
            InstantCameraView.this.videoEditedInfo.iv = InstantCameraView.this.iv;
            InstantCameraView.this.videoEditedInfo.estimatedSize = Math.max(1, InstantCameraView.this.size);
            InstantCameraView.this.videoEditedInfo.framerate = 25;
            VideoEditedInfo access$1100 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalWidth = PsExtractor.VIDEO_STREAM_MASK;
            access$1100.resultWidth = PsExtractor.VIDEO_STREAM_MASK;
            VideoEditedInfo access$11002 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalHeight = PsExtractor.VIDEO_STREAM_MASK;
            access$11002.resultHeight = PsExtractor.VIDEO_STREAM_MASK;
            InstantCameraView.this.videoEditedInfo.originalPath = this.videoFile.getAbsolutePath();
            if (send != 1) {
                VideoPlayer unused2 = InstantCameraView.this.videoPlayer = new VideoPlayer();
                InstantCameraView.this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoPlayer.isPlaying() && playbackState == 4) {
                            VideoPlayer access$1000 = InstantCameraView.this.videoPlayer;
                            long j = 0;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$1000.seekTo(j);
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.e((Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    }

                    public void onRenderedFirstFrame() {
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }
                });
                InstantCameraView.this.videoPlayer.setTextureView(InstantCameraView.this.textureView);
                InstantCameraView.this.videoPlayer.preparePlayer(Uri.fromFile(this.videoFile), "other");
                InstantCameraView.this.videoPlayer.play();
                InstantCameraView.this.videoPlayer.setMute(true);
                InstantCameraView.this.startProgressTimer();
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "alpha", new float[]{0.0f}), ObjectAnimator.ofInt(InstantCameraView.this.paint, "alpha", new int[]{0}), ObjectAnimator.ofFloat(InstantCameraView.this.muteImageView, "alpha", new float[]{1.0f})});
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.start();
                InstantCameraView.this.videoEditedInfo.estimatedDuration = InstantCameraView.this.duration;
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.audioDidSent, Integer.valueOf(InstantCameraView.this.recordingGuid), InstantCameraView.this.videoEditedInfo, this.videoFile.getAbsolutePath());
            } else if (InstantCameraView.this.baseFragment.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(InstantCameraView.this.baseFragment.getParentActivity(), UserObject.isUserSelf(InstantCameraView.this.baseFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate() {
                    public final void didSelectDate(boolean z, int i) {
                        InstantCameraView.VideoRecorder.this.lambda$null$2$InstantCameraView$VideoRecorder(z, i);
                    }
                });
            } else {
                InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true), InstantCameraView.this.videoEditedInfo, true, 0);
            }
            didWriteData(this.videoFile, 0, true);
        }

        public /* synthetic */ void lambda$null$2$InstantCameraView$VideoRecorder(boolean notify, int scheduleDate) {
            InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true), InstantCameraView.this.videoEditedInfo, notify, scheduleDate);
            InstantCameraView.this.startAnimation(false);
        }

        /* access modifiers changed from: private */
        public void prepareEncoder() {
            try {
                int recordBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                if (recordBufferSize <= 0) {
                    recordBufferSize = 3584;
                }
                int bufferSize = 49152;
                if (49152 < recordBufferSize) {
                    bufferSize = ((recordBufferSize / 2048) + 1) * 2048 * 2;
                }
                for (int a = 0; a < 3; a++) {
                    this.buffers.add(new AudioBufferInfo());
                }
                AudioRecord audioRecord = r9;
                AudioRecord audioRecord2 = new AudioRecord(0, 44100, 16, 2, bufferSize);
                this.audioRecorder = audioRecord;
                audioRecord.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + bufferSize);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new MediaCodec.BufferInfo();
                this.videoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString("mime", "audio/mp4a-latm");
                audioFormat.setInteger("aac-profile", 2);
                audioFormat.setInteger("sample-rate", 44100);
                audioFormat.setInteger("channel-count", 1);
                audioFormat.setInteger("bitrate", 32000);
                audioFormat.setInteger("max-input-size", CacheDataSink.DEFAULT_BUFFER_SIZE);
                MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder = createEncoderByType;
                createEncoderByType.configure(audioFormat, (Surface) null, (MediaCrypto) null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                format.setInteger("color-format", 2130708361);
                format.setInteger("bitrate", this.videoBitrate);
                format.setInteger("frame-rate", 30);
                format.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(format, (Surface) null, (MediaCrypto) null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie movie = new Mp4Movie();
                movie.setCacheFile(this.videoFile);
                movie.setRotation(0);
                movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(movie, InstantCameraView.this.isSecretChat);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        InstantCameraView.VideoRecorder.this.lambda$prepareEncoder$4$InstantCameraView$VideoRecorder();
                    }
                });
                if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    android.opengl.EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
                    this.eglDisplay = eglGetDisplay;
                    if (eglGetDisplay != EGL14.EGL_NO_DISPLAY) {
                        int[] version = new int[2];
                        if (EGL14.eglInitialize(this.eglDisplay, version, 0, version, 1)) {
                            if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                                int[] attribList = {12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344};
                                android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
                                if (EGL14.eglChooseConfig(this.eglDisplay, attribList, 0, configs, 0, configs.length, new int[1], 0)) {
                                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, configs[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                                    this.eglConfig = configs[0];
                                } else {
                                    throw new RuntimeException("Unable to find a suitable EGLConfig");
                                }
                            }
                            EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, new int[1], 0);
                            if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
                                android.opengl.EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, new int[]{12344}, 0);
                                this.eglSurface = eglCreateWindowSurface;
                                if (eglCreateWindowSurface == null) {
                                    throw new RuntimeException("surface was null");
                                } else if (!EGL14.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                                    }
                                    throw new RuntimeException("eglMakeCurrent failed");
                                } else {
                                    GLES20.glBlendFunc(770, 771);
                                    int vertexShader = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                                    int fragmentShader = InstantCameraView.this.loadShader(35632, InstantCameraView.FRAGMENT_SHADER);
                                    if (vertexShader != 0 && fragmentShader != 0) {
                                        int glCreateProgram = GLES20.glCreateProgram();
                                        this.drawProgram = glCreateProgram;
                                        GLES20.glAttachShader(glCreateProgram, vertexShader);
                                        GLES20.glAttachShader(this.drawProgram, fragmentShader);
                                        GLES20.glLinkProgram(this.drawProgram);
                                        int[] linkStatus = new int[1];
                                        GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                                        if (linkStatus[0] == 0) {
                                            GLES20.glDeleteProgram(this.drawProgram);
                                            this.drawProgram = 0;
                                            return;
                                        }
                                        this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                                        this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                                        this.scaleXHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleX");
                                        this.scaleYHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleY");
                                        this.alphaHandle = GLES20.glGetUniformLocation(this.drawProgram, "alpha");
                                        this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                        this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                                    }
                                }
                            } else {
                                throw new IllegalStateException("surface already created");
                            }
                        } else {
                            this.eglDisplay = null;
                            throw new RuntimeException("unable to initialize EGL14");
                        }
                    } else {
                        throw new RuntimeException("unable to get EGL14 display");
                    }
                } else {
                    throw new RuntimeException("EGL already set up");
                }
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        public /* synthetic */ void lambda$prepareEncoder$4$InstantCameraView$VideoRecorder() {
            if (!InstantCameraView.this.cancelled) {
                try {
                    InstantCameraView.this.performHapticFeedback(3, 2);
                } catch (Exception e) {
                }
                AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
                boolean unused = InstantCameraView.this.recording = true;
                long unused2 = InstantCameraView.this.recordStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable);
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(InstantCameraView.this.recordingGuid));
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        private void didWriteData(File file, long availableSize, boolean last) {
            long j = 0;
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(file.toString(), InstantCameraView.this.isSecretChat, false, 1, ConnectionsManager.FileTypeVideo, true);
                this.videoConvertFirstWrite = false;
                if (last) {
                    FileLoader instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
                    String file2 = file.toString();
                    boolean access$3800 = InstantCameraView.this.isSecretChat;
                    if (last) {
                        j = file.length();
                    }
                    instance.checkUploadNewDataAvailable(file2, access$3800, availableSize, j);
                    return;
                }
                return;
            }
            FileLoader instance2 = FileLoader.getInstance(InstantCameraView.this.currentAccount);
            String file3 = file.toString();
            boolean access$38002 = InstantCameraView.this.isSecretChat;
            if (last) {
                j = file.length();
            }
            instance2.checkUploadNewDataAvailable(file3, access$38002, availableSize, j);
        }

        public void drainEncoder(boolean endOfStream) throws Exception {
            ByteBuffer encodedData;
            ByteBuffer encodedData2;
            if (endOfStream) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = null;
            int i = 21;
            if (Build.VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, OkHttpUtils.DEFAULT_MILLISECONDS);
                byte b = 1;
                if (encoderStatus != -1) {
                    if (encoderStatus == -3) {
                        if (Build.VERSION.SDK_INT < i) {
                            encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
                        }
                    } else if (encoderStatus == -2) {
                        MediaFormat newFormat = this.videoEncoder.getOutputFormat();
                        if (this.videoTrackIndex == -5) {
                            this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                        }
                    } else if (encoderStatus < 0) {
                        continue;
                    } else {
                        if (Build.VERSION.SDK_INT < i) {
                            encodedData2 = encoderOutputBuffers[encoderStatus];
                        } else {
                            encodedData2 = this.videoEncoder.getOutputBuffer(encoderStatus);
                        }
                        if (encodedData2 != null) {
                            if (this.videoBufferInfo.size > 1) {
                                if ((this.videoBufferInfo.flags & 2) == 0) {
                                    long availableSize = this.mediaMuxer.writeSampleData(this.videoTrackIndex, encodedData2, this.videoBufferInfo, true);
                                    if (availableSize != 0) {
                                        didWriteData(this.videoFile, availableSize, false);
                                    }
                                } else if (this.videoTrackIndex == -5) {
                                    byte[] csd = new byte[this.videoBufferInfo.size];
                                    encodedData2.limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
                                    encodedData2.position(this.videoBufferInfo.offset);
                                    encodedData2.get(csd);
                                    ByteBuffer sps = null;
                                    ByteBuffer pps = null;
                                    int a = this.videoBufferInfo.size - 1;
                                    while (true) {
                                        if (a >= 0 && a > 3) {
                                            if (csd[a] == b && csd[a - 1] == 0 && csd[a - 2] == 0 && csd[a - 3] == 0) {
                                                sps = ByteBuffer.allocate(a - 3);
                                                pps = ByteBuffer.allocate(this.videoBufferInfo.size - (a - 3));
                                                sps.put(csd, 0, a - 3).position(0);
                                                pps.put(csd, a - 3, this.videoBufferInfo.size - (a - 3)).position(0);
                                                break;
                                            }
                                            a--;
                                            b = 1;
                                        } else {
                                            break;
                                        }
                                    }
                                    MediaFormat newFormat2 = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                                    if (!(sps == null || pps == null)) {
                                        newFormat2.setByteBuffer("csd-0", sps);
                                        newFormat2.setByteBuffer("csd-1", pps);
                                    }
                                    this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat2, false);
                                }
                            }
                            this.videoEncoder.releaseOutputBuffer(encoderStatus, false);
                            if ((this.videoBufferInfo.flags & 4) != 0) {
                                break;
                            }
                        } else {
                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                        }
                    }
                    i = 21;
                } else if (!endOfStream) {
                    break;
                } else {
                    i = 21;
                }
            }
            if (Build.VERSION.SDK_INT < i) {
                encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus2 = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0);
                if (encoderStatus2 == -1) {
                    if (!endOfStream) {
                        return;
                    }
                    if (!this.running && this.sendWhenDone == 0) {
                        return;
                    }
                } else if (encoderStatus2 == -3) {
                    if (Build.VERSION.SDK_INT < i) {
                        encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus2 == -2) {
                    MediaFormat newFormat3 = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(newFormat3, true);
                    }
                } else if (encoderStatus2 < 0) {
                    continue;
                } else {
                    if (Build.VERSION.SDK_INT < i) {
                        encodedData = encoderOutputBuffers[encoderStatus2];
                    } else {
                        encodedData = this.audioEncoder.getOutputBuffer(encoderStatus2);
                    }
                    if (encodedData != null) {
                        if ((this.audioBufferInfo.flags & 2) != 0) {
                            this.audioBufferInfo.size = 0;
                        }
                        if (this.audioBufferInfo.size != 0) {
                            long availableSize2 = this.mediaMuxer.writeSampleData(this.audioTrackIndex, encodedData, this.audioBufferInfo, false);
                            if (availableSize2 != 0) {
                                didWriteData(this.videoFile, availableSize2, false);
                            }
                        }
                        this.audioEncoder.releaseOutputBuffer(encoderStatus2, false);
                        if ((this.audioBufferInfo.flags & 4) != 0) {
                            return;
                        }
                    } else {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus2 + " was null");
                    }
                }
                i = 21;
            }
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            try {
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                    EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                    EGL14.eglReleaseThread();
                    EGL14.eglTerminate(this.eglDisplay);
                    this.eglDisplay = EGL14.EGL_NO_DISPLAY;
                    this.eglContext = EGL14.EGL_NO_CONTEXT;
                    this.eglConfig = null;
                }
            } finally {
                super.finalize();
            }
        }
    }
}
