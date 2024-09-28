package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.components.crop.CropRotationWheel;
import im.bclpbkiauv.ui.components.crop.CropView;

public class PhotoCropView extends FrameLayout {
    /* access modifiers changed from: private */
    public CropView cropView;
    /* access modifiers changed from: private */
    public PhotoCropViewDelegate delegate;
    boolean isFcCrop;
    private boolean showOnSetBitmap;
    /* access modifiers changed from: private */
    public CropRotationWheel wheelView;

    public interface PhotoCropViewDelegate {
        void onChange(boolean z);
    }

    public PhotoCropView(Context context) {
        super(context);
        CropView cropView2 = new CropView(getContext());
        this.cropView = cropView2;
        cropView2.setListener(new CropView.CropViewListener() {
            public void onChange(boolean reset) {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(reset);
                }
            }

            public void onAspectLock(boolean enabled) {
                PhotoCropView.this.wheelView.setAspectLock(enabled);
            }
        });
        this.cropView.setBottomPadding((float) AndroidUtilities.dp(64.0f));
        addView(this.cropView);
        CropRotationWheel cropRotationWheel = new CropRotationWheel(getContext());
        this.wheelView = cropRotationWheel;
        cropRotationWheel.setListener(new CropRotationWheel.RotationWheelListener() {
            public void onStart() {
                PhotoCropView.this.cropView.onRotationBegan();
            }

            public void onChange(float angle) {
                PhotoCropView.this.cropView.setRotation(angle);
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(false);
                }
            }

            public void onEnd(float angle) {
                PhotoCropView.this.cropView.onRotationEnded();
            }

            public void aspectRatioPressed() {
                PhotoCropView.this.cropView.showAspectRatioDialog();
            }

            public void rotate90Pressed() {
                PhotoCropView.this.rotate();
            }
        });
        addView(this.wheelView, LayoutHelper.createFrame(-1.0f, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public boolean isFcCrop() {
        return this.isFcCrop;
    }

    public PhotoCropView(Context context, boolean isFcCrop2) {
        super(context);
        this.isFcCrop = isFcCrop2;
        if (isFcCrop2) {
            this.cropView = new CropView(getContext(), true);
        } else {
            this.cropView = new CropView(getContext());
        }
        this.cropView.setListener(new CropView.CropViewListener() {
            public void onChange(boolean reset) {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(reset);
                }
            }

            public void onAspectLock(boolean enabled) {
                PhotoCropView.this.wheelView.setAspectLock(enabled);
            }
        });
        this.cropView.setBottomPadding((float) AndroidUtilities.dp(64.0f));
        addView(this.cropView);
        CropRotationWheel cropRotationWheel = new CropRotationWheel(getContext());
        this.wheelView = cropRotationWheel;
        cropRotationWheel.setListener(new CropRotationWheel.RotationWheelListener() {
            public void onStart() {
                PhotoCropView.this.cropView.onRotationBegan();
            }

            public void onChange(float angle) {
                PhotoCropView.this.cropView.setRotation(angle);
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(false);
                }
            }

            public void onEnd(float angle) {
                PhotoCropView.this.cropView.onRotationEnded();
            }

            public void aspectRatioPressed() {
                PhotoCropView.this.cropView.showAspectRatioDialog();
            }

            public void rotate90Pressed() {
                PhotoCropView.this.rotate();
            }
        });
        addView(this.wheelView, LayoutHelper.createFrame(-1.0f, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void rotate() {
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (cropRotationWheel != null) {
            cropRotationWheel.reset();
        }
        this.cropView.rotate90Degrees();
    }

    public void setBitmap(Bitmap bitmap, int rotation, boolean freeform, boolean update) {
        requestLayout();
        this.cropView.setBitmap(bitmap, rotation, freeform, update);
        int i = 0;
        if (this.showOnSetBitmap) {
            this.showOnSetBitmap = false;
            this.cropView.show();
        }
        this.wheelView.setFreeform(freeform);
        this.wheelView.reset();
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (!freeform) {
            i = 4;
        }
        cropRotationWheel.setVisibility(i);
    }

    public boolean isReady() {
        return this.cropView.isReady();
    }

    public void reset() {
        this.wheelView.reset();
        this.cropView.reset();
    }

    public void onAppear() {
        this.cropView.willShow();
    }

    public void setAspectRatio(float ratio) {
        this.cropView.setAspectRatio(ratio);
    }

    public void hideBackView() {
        this.cropView.hideBackView();
    }

    public void showBackView() {
        this.cropView.showBackView();
    }

    public void setFreeform(boolean freeform) {
        this.cropView.setFreeform(freeform);
    }

    public void onAppeared() {
        CropView cropView2 = this.cropView;
        if (cropView2 != null) {
            cropView2.show();
        } else {
            this.showOnSetBitmap = true;
        }
    }

    public void onDisappear() {
        CropView cropView2 = this.cropView;
        if (cropView2 != null) {
            cropView2.hide();
        }
    }

    public float getRectX() {
        return this.cropView.getCropLeft() - ((float) AndroidUtilities.dp(14.0f));
    }

    public float getRectY() {
        return (this.cropView.getCropTop() - ((float) AndroidUtilities.dp(14.0f))) - ((float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
    }

    public float getRectSizeX() {
        return this.cropView.getCropWidth();
    }

    public float getRectSizeY() {
        return this.cropView.getCropHeight();
    }

    public Bitmap getBitmap() {
        CropView cropView2 = this.cropView;
        if (cropView2 != null) {
            return cropView2.getResult();
        }
        return null;
    }

    public void setDelegate(PhotoCropViewDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        CropView cropView2 = this.cropView;
        if (cropView2 != null) {
            cropView2.updateLayout();
        }
    }
}
