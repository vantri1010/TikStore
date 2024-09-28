package im.bclpbkiauv.ui.hui.friendscircle_v1.player.widget;

import android.content.Context;
import android.view.TextureView;
import android.view.View;

public class NiceTextureView extends TextureView {
    private int videoHeight;
    private int videoWidth;

    public NiceTextureView(Context context) {
        super(context);
    }

    public void adaptVideoSize(int videoWidth2, int videoHeight2) {
        if (this.videoWidth != videoWidth2 && this.videoHeight != videoHeight2) {
            this.videoWidth = videoWidth2;
            this.videoHeight = videoHeight2;
            requestLayout();
        }
    }

    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float viewRotation = getRotation();
        if (viewRotation == 90.0f || viewRotation == 270.0f) {
            int tempMeasureSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempMeasureSpec;
        }
        int width = getDefaultSize(this.videoWidth, widthMeasureSpec);
        int height = getDefaultSize(this.videoHeight, heightMeasureSpec);
        if (this.videoWidth > 0 && this.videoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (widthSpecMode == 1073741824 && heightSpecMode == 1073741824) {
                width = widthSpecSize;
                height = heightSpecSize;
                int i = this.videoWidth;
                int i2 = i * height;
                int i3 = this.videoHeight;
                if (i2 < width * i3) {
                    width = (i * height) / i3;
                } else if (i * height > width * i3) {
                    height = (i3 * width) / i;
                }
            } else if (widthSpecMode == 1073741824) {
                width = widthSpecSize;
                int i4 = this.videoHeight;
                int i5 = this.videoWidth;
                height = (width * i4) / i5;
                if (heightSpecMode == Integer.MIN_VALUE && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = (i5 * height) / i4;
                }
            } else if (heightSpecMode == 1073741824) {
                height = heightSpecSize;
                int i6 = this.videoWidth;
                int i7 = this.videoHeight;
                width = (height * i6) / i7;
                if (widthSpecMode == Integer.MIN_VALUE && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = (i7 * width) / i6;
                }
            } else {
                width = this.videoWidth;
                height = this.videoHeight;
                if (heightSpecMode == Integer.MIN_VALUE && height > heightSpecSize) {
                    height = heightSpecSize;
                    width = (this.videoWidth * height) / this.videoHeight;
                }
                if (widthSpecMode == Integer.MIN_VALUE && width > widthSpecSize) {
                    width = widthSpecSize;
                    height = (this.videoHeight * width) / this.videoWidth;
                }
            }
        }
        setMeasuredDimension(width, height);
    }
}
