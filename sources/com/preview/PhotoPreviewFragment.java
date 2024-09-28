package com.preview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import com.preview.interfaces.ImageLoader;
import com.preview.interfaces.OnLongClickListener;
import com.preview.photoview.OnFingerUpListener;
import com.preview.photoview.OnViewDragListener;
import com.preview.photoview.PhotoView;
import com.preview.util.Utils;
import com.preview.util.notch.OSUtils;
import im.bclpbkiauv.messenger.R;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PhotoPreviewFragment extends Fragment {
    private final String TAG = PhotoPreviewFragment.class.getSimpleName();
    /* access modifiers changed from: private */
    public float mAlpha = 1.0f;
    private Context mContext;
    private long mDelayShowProgressTime;
    private boolean mFullScreen;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public int[] mImageLocation;
    /* access modifiers changed from: private */
    public int[] mImageSize;
    /* access modifiers changed from: private */
    public int mIntAlpha = 255;
    private ImageLoader mLoadImage;
    /* access modifiers changed from: private */
    public ProgressBar mLoading;
    private boolean mNeedInAnim;
    /* access modifiers changed from: private */
    public OnExitListener mOnExitListener;
    /* access modifiers changed from: private */
    public OnLongClickListener mOnLongClickListener;
    /* access modifiers changed from: private */
    public PhotoView mPhotoView;
    /* access modifiers changed from: private */
    public int mPosition;
    private Integer mProgressColor;
    private Drawable mProgressDrawable;
    /* access modifiers changed from: private */
    public FrameLayout mRoot;
    /* access modifiers changed from: private */
    public ScheduledFuture<?> mSchedule;
    private ScheduledExecutorService mService;
    /* access modifiers changed from: private */
    public Object mUrl;

    public interface OnExitListener {
        void onExit();

        void onStart();
    }

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_preview, (ViewGroup) null);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.root);
        this.mRoot = frameLayout;
        frameLayout.setFocusableInTouchMode(true);
        this.mRoot.requestFocus();
        this.mPhotoView = (PhotoView) view.findViewById(R.id.photoView);
        this.mLoading = (ProgressBar) view.findViewById(R.id.loading);
        initEvent();
        onLoadData();
        return view;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        PhotoView photoView;
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint() && (photoView = this.mPhotoView) != null) {
            photoView.setScale(1.0f);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        ScheduledFuture<?> scheduledFuture = this.mSchedule;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        ScheduledExecutorService scheduledExecutorService = this.mService;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages((Object) null);
        }
    }

    private void initData() {
        this.mService = Executors.newScheduledThreadPool(1);
        this.mHandler = new Handler();
    }

    private void initEvent() {
        this.mRoot.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 4) {
                    return false;
                }
                PhotoPreviewFragment.this.exit();
                return true;
            }
        });
        this.mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (PhotoPreviewFragment.this.mOnLongClickListener == null) {
                    return true;
                }
                PhotoPreviewFragment.this.mOnLongClickListener.onLongClick(PhotoPreviewFragment.this.mRoot, PhotoPreviewFragment.this.mUrl, PhotoPreviewFragment.this.mPosition);
                return true;
            }
        });
        this.mPhotoView.getAttacher().setOnFingerUpListener(new OnFingerUpListener() {
            public void onFingerUp() {
                if (PhotoPreviewFragment.this.mIntAlpha >= 150 || PhotoPreviewFragment.this.mOnExitListener == null) {
                    ValueAnimator va = ValueAnimator.ofFloat(new float[]{PhotoPreviewFragment.this.mPhotoView.getAlpha(), 1.0f});
                    ValueAnimator bgVa = ValueAnimator.ofInt(new int[]{PhotoPreviewFragment.this.mIntAlpha, 255});
                    va.setDuration(200);
                    bgVa.setDuration(200);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            PhotoPreviewFragment.this.mPhotoView.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
                        }
                    });
                    bgVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            PhotoPreviewFragment.this.mRoot.getBackground().setAlpha(((Integer) animation.getAnimatedValue()).intValue());
                        }
                    });
                    va.start();
                    bgVa.start();
                    PhotoPreviewFragment.this.mPhotoView.smoothResetPosition();
                } else {
                    PhotoPreviewFragment.this.exit();
                }
                float unused = PhotoPreviewFragment.this.mAlpha = 1.0f;
                int unused2 = PhotoPreviewFragment.this.mIntAlpha = 255;
            }
        });
        this.mPhotoView.setOnViewDragListener(new OnViewDragListener() {
            public void onDrag(float dx, float dy) {
                PhotoPreviewFragment.this.mPhotoView.scrollBy((int) (-dx), (int) (-dy));
                if (((float) PhotoPreviewFragment.this.mPhotoView.getScrollY()) >= 0.0f) {
                    float unused = PhotoPreviewFragment.this.mAlpha = 1.0f;
                    int unused2 = PhotoPreviewFragment.this.mIntAlpha = 255;
                } else {
                    PhotoPreviewFragment photoPreviewFragment = PhotoPreviewFragment.this;
                    float unused3 = photoPreviewFragment.mAlpha = photoPreviewFragment.mAlpha - (0.001f * dy);
                    PhotoPreviewFragment photoPreviewFragment2 = PhotoPreviewFragment.this;
                    int unused4 = photoPreviewFragment2.mIntAlpha = (int) (((double) photoPreviewFragment2.mIntAlpha) - (((double) dy) * 0.25d));
                }
                if (PhotoPreviewFragment.this.mAlpha > 1.0f) {
                    float unused5 = PhotoPreviewFragment.this.mAlpha = 1.0f;
                } else if (PhotoPreviewFragment.this.mAlpha < 0.0f) {
                    float unused6 = PhotoPreviewFragment.this.mAlpha = 0.0f;
                }
                if (PhotoPreviewFragment.this.mIntAlpha < 0) {
                    int unused7 = PhotoPreviewFragment.this.mIntAlpha = 0;
                } else if (PhotoPreviewFragment.this.mIntAlpha > 255) {
                    int unused8 = PhotoPreviewFragment.this.mIntAlpha = 255;
                }
                PhotoPreviewFragment.this.mRoot.getBackground().setAlpha(PhotoPreviewFragment.this.mIntAlpha);
            }
        });
        this.mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PhotoPreviewFragment.this.exit();
            }
        });
        this.mRoot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PhotoPreviewFragment.this.exit();
            }
        });
    }

    public void onLoadData() {
        this.mAlpha = 1.0f;
        this.mIntAlpha = 255;
        this.mPhotoView.setImageDrawable((Drawable) null);
        this.mLoadImage.onLoadImage(this.mPosition, this.mUrl, this.mPhotoView);
        checkLoadResult();
        this.mRoot.getBackground().setAlpha(this.mIntAlpha);
        if (this.mNeedInAnim) {
            this.mNeedInAnim = false;
            this.mPhotoView.post(new Runnable() {
                public void run() {
                    PhotoPreviewFragment.this.mPhotoView.setVisibility(0);
                    ObjectAnimator scaleXOa = ObjectAnimator.ofFloat(PhotoPreviewFragment.this.mPhotoView, "scaleX", new float[]{(((float) PhotoPreviewFragment.this.mImageSize[0]) * 1.0f) / ((float) PhotoPreviewFragment.this.mPhotoView.getWidth()), 1.0f});
                    ObjectAnimator scaleYOa = ObjectAnimator.ofFloat(PhotoPreviewFragment.this.mPhotoView, "scaleY", new float[]{(((float) PhotoPreviewFragment.this.mImageSize[1]) * 1.0f) / ((float) PhotoPreviewFragment.this.mPhotoView.getHeight()), 1.0f});
                    ObjectAnimator xOa = ObjectAnimator.ofFloat(PhotoPreviewFragment.this.mPhotoView, "translationX", new float[]{((float) PhotoPreviewFragment.this.mImageLocation[0]) - (((float) PhotoPreviewFragment.this.mPhotoView.getWidth()) / 2.0f), 0.0f});
                    ObjectAnimator yOa = ObjectAnimator.ofFloat(PhotoPreviewFragment.this.mPhotoView, "translationY", new float[]{PhotoPreviewFragment.this.getTranslationY(), 0.0f});
                    AnimatorSet set = new AnimatorSet();
                    set.setDuration(250);
                    set.playTogether(new Animator[]{scaleXOa, scaleYOa, xOa, yOa});
                    set.start();
                }
            });
            return;
        }
        this.mPhotoView.setVisibility(0);
    }

    private void checkLoadResult() {
        Integer num;
        int i = 8;
        if (this.mDelayShowProgressTime < 0) {
            this.mLoading.setVisibility(8);
            return;
        }
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            this.mLoading.setIndeterminateDrawable(drawable);
        }
        if (Build.VERSION.SDK_INT >= 21 && (num = this.mProgressColor) != null) {
            this.mLoading.setIndeterminateTintList(ColorStateList.valueOf(num.intValue()));
        }
        ProgressBar progressBar = this.mLoading;
        if (this.mDelayShowProgressTime == 0) {
            i = 0;
        }
        progressBar.setVisibility(i);
        ScheduledExecutorService scheduledExecutorService = this.mService;
        AnonymousClass8 r6 = new Runnable() {
            public void run() {
                if (PhotoPreviewFragment.this.mPhotoView.getDrawable() != null) {
                    PhotoPreviewFragment.this.mHandler.post(new Runnable() {
                        public void run() {
                            PhotoPreviewFragment.this.mLoading.setVisibility(8);
                        }
                    });
                    PhotoPreviewFragment.this.mSchedule.cancel(true);
                } else if (PhotoPreviewFragment.this.mLoading.getVisibility() == 8) {
                    PhotoPreviewFragment.this.mHandler.post(new Runnable() {
                        public void run() {
                            PhotoPreviewFragment.this.mLoading.setVisibility(0);
                        }
                    });
                }
            }
        };
        long j = this.mDelayShowProgressTime;
        if (j == 0) {
            j = 100;
        }
        this.mSchedule = scheduledExecutorService.scheduleWithFixedDelay(r6, j, 100, TimeUnit.MILLISECONDS);
    }

    public void exit() {
        Matrix m = new Matrix();
        m.postScale(((float) this.mImageSize[0]) / ((float) this.mPhotoView.getWidth()), ((float) this.mImageSize[1]) / ((float) this.mPhotoView.getHeight()));
        PhotoView photoView = this.mPhotoView;
        ObjectAnimator scaleOa = ObjectAnimator.ofFloat(photoView, "scale", new float[]{photoView.getAttacher().getScale(m)});
        PhotoView photoView2 = this.mPhotoView;
        ObjectAnimator xOa = ObjectAnimator.ofFloat(photoView2, "translationX", new float[]{(((float) this.mImageLocation[0]) - (((float) photoView2.getWidth()) / 2.0f)) + ((float) this.mPhotoView.getScrollX())});
        ObjectAnimator yOa = ObjectAnimator.ofFloat(this.mPhotoView, "translationY", new float[]{getTranslationY()});
        AnimatorSet set = new AnimatorSet();
        set.setDuration(250);
        set.playTogether(new Animator[]{scaleOa, xOa, yOa});
        OnExitListener onExitListener = this.mOnExitListener;
        if (onExitListener != null) {
            onExitListener.onStart();
        }
        set.start();
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (PhotoPreviewFragment.this.mOnExitListener != null) {
                    PhotoPreviewFragment.this.mOnExitListener.onExit();
                }
            }
        }, 250);
    }

    /* access modifiers changed from: private */
    public float getTranslationY() {
        float translationY = (((float) this.mImageLocation[1]) - (((float) this.mPhotoView.getHeight()) / 2.0f)) + ((float) this.mPhotoView.getScrollY());
        if (OSUtils.isVivo() || !this.mFullScreen) {
            return translationY - ((float) Utils.getStatusBarHeight(this.mContext));
        }
        return translationY;
    }

    public void setData(ImageLoader loadImage, int position, Object url, int[] imageSize, int[] imageLocation, boolean needInAnim, long delayShowProgressTime, Integer progressColor, Drawable progressDrawable, boolean fullScreen) {
        this.mLoadImage = loadImage;
        this.mUrl = url;
        this.mImageSize = imageSize;
        this.mImageLocation = imageLocation;
        this.mNeedInAnim = needInAnim;
        this.mPosition = position;
        this.mDelayShowProgressTime = delayShowProgressTime;
        this.mProgressColor = progressColor;
        this.mProgressDrawable = progressDrawable;
        this.mFullScreen = fullScreen;
    }

    public void setOnExitListener(OnExitListener onExitListener) {
        this.mOnExitListener = onExitListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
    }
}
