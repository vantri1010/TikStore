package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;

public class ScaleTextureView extends TextureView {
    private Matrix mMatrix = getMatrix();
    private int mVideoHeight;
    private int mVideoWidth;

    public ScaleTextureView(Context context) {
        super(context);
    }

    public ScaleTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setmVideoWidth(int mVideoWidth2) {
        this.mVideoWidth = mVideoWidth2;
    }

    public void setmVideoHeight(int mVideoHeight2) {
        this.mVideoHeight = mVideoHeight2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = getDefaultSize(this.mVideoWidth, widthMeasureSpec);
        int viewHeight = getDefaultSize(this.mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
        float scaleX = (((float) viewWidth) * 1.0f) / ((float) this.mVideoWidth);
        float scaleY = (((float) viewHeight) * 1.0f) / ((float) this.mVideoHeight);
        float maxScale = Math.max(scaleX, scaleY);
        this.mMatrix.setScale(maxScale / scaleX, maxScale / scaleY, (float) (viewWidth / 2), (float) (viewHeight / 2));
        setTransform(this.mMatrix);
    }
}
