package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.SecureDocument;

public class BackupImageView extends View {
    private int height = -1;
    private ImageReceiver imageReceiver;
    private int width = -1;

    public BackupImageView(Context context) {
        super(context);
        init();
    }

    public BackupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackupImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            this.imageReceiver = new ImageReceiver(this);
        }
    }

    public void setOrientation(int angle, boolean center) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setOrientation(angle, center);
        }
    }

    public void setImage(SecureDocument secureDocument, String filter) {
        setImage(ImageLocation.getForSecureDocument(secureDocument), filter, (ImageLocation) null, (String) null, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, String ext, Drawable thumb, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, ext, 0, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, (String) null, 0, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Bitmap thumb, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, (Drawable) null, thumb, (String) null, 0, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, int size, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, (String) null, size, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Bitmap thumb, int size, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, (Drawable) null, thumb, (String) null, size, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, int size, Object parentObject) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, (Bitmap) null, (String) null, size, parentObject);
    }

    public void setImage(String path, String filter, Drawable thumb) {
        setImage(ImageLocation.getForPath(path), filter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(String path, String filter, String thumbPath, String thumbFilter) {
        setImage(ImageLocation.getForPath(path), filter, ImageLocation.getForPath(thumbPath), thumbFilter, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, Bitmap thumbBitmap, String ext, int size, Object parentObject) {
        Drawable thumb2;
        Bitmap bitmap = thumbBitmap;
        if (bitmap != null) {
            thumb2 = new BitmapDrawable((Resources) null, bitmap);
        } else {
            thumb2 = thumb;
        }
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, thumb2, size, ext, parentObject, 0);
        }
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, String ext, int size, int cacheType, Object parentObject) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, size, ext, parentObject, cacheType);
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.imageReceiver.setImageBitmap(bitmap);
    }

    public void setImageResource(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageBitmap(drawable);
            invalidate();
        }
    }

    public void setImageResource(int resId, int color) {
        Drawable drawable = getResources().getDrawable(resId);
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageBitmap(drawable);
            invalidate();
        }
    }

    public void setImageDrawable(Drawable drawable) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageBitmap(drawable);
        }
    }

    public void setLayerNum(int value) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setLayerNum(value);
        }
    }

    public void setRoundRadius(int value) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setRoundRadius(value);
            invalidate();
        }
    }

    public int getRoundRadius() {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            return imageReceiver2.getRoundRadius();
        }
        return -1;
    }

    public void setAspectFit(boolean value) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setAspectFit(value);
        }
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.onDetachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.onAttachedToWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            if (this.width == -1 || this.height == -1) {
                int paddingLeft = getPaddingLeft();
                int paddingTop = getPaddingTop();
                this.imageReceiver.setImageCoords(paddingLeft, paddingTop, getWidth() - (paddingLeft + getPaddingRight()), getHeight() - (paddingTop + getPaddingBottom()));
            } else {
                int height2 = getHeight();
                int i = this.height;
                imageReceiver2.setImageCoords((getWidth() - this.width) / 2, (height2 - i) / 2, this.width, i);
            }
            this.imageReceiver.draw(canvas);
        }
    }
}
