package im.bclpbkiauv.ui.hviews.swipelist.reservation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.BuildConfig;

public class TopWrappedDividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = {16843284};
    public static final int HORIZONTAL = 0;
    private static final String TAG = "TopWrappedDividerItemDecoration";
    public static final int VERTICAL = 1;
    private final Rect mBounds = new Rect();
    private Drawable mDivider;
    private int mOrientation;

    public TopWrappedDividerItemDecoration(Context context, int orientation) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        Drawable drawable = a.getDrawable(0);
        this.mDivider = drawable;
        if (drawable == null && BuildConfig.DEBUG) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDivider()");
        }
        a.recycle();
        setOrientation(orientation);
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int orientation) {
        if (orientation == 0 || orientation == 1) {
            this.mOrientation = orientation;
            return;
        }
        throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    public void setDivider(Drawable divider) {
        this.mDivider = divider;
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() != null && this.mDivider != null) {
            if (this.mOrientation == 1) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int right;
        int left;
        canvas.save();
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);
            int bottom = this.mBounds.bottom + Math.round(child.getTranslationY());
            this.mDivider.setBounds(left, bottom - this.mDivider.getIntrinsicHeight(), right, bottom);
            this.mDivider.draw(canvas);
            if (i == 0) {
                this.mDivider.setBounds(left, parent.getPaddingTop(), right, parent.getPaddingTop() + this.mDivider.getIntrinsicHeight());
                this.mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int bottom;
        int top;
        canvas.save();
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);
            int right = this.mBounds.right + Math.round(child.getTranslationX());
            this.mDivider.setBounds(right - this.mDivider.getIntrinsicWidth(), top, right, bottom);
            this.mDivider.draw(canvas);
            if (i == 0) {
                this.mDivider.setBounds(parent.getPaddingLeft(), top, parent.getPaddingLeft() + this.mDivider.getIntrinsicWidth(), bottom);
                this.mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Drawable drawable = this.mDivider;
        if (drawable == null) {
            outRect.set(0, 0, 0, 0);
        } else if (this.mOrientation == 1) {
            int dividerHeight = drawable.getIntrinsicHeight();
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(0, dividerHeight, 0, dividerHeight);
            } else {
                outRect.set(0, 0, 0, dividerHeight);
            }
        } else {
            int dividerWidth = drawable.getIntrinsicWidth();
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(dividerWidth, 0, dividerWidth, 0);
            } else {
                outRect.set(0, 0, dividerWidth, 0);
            }
        }
    }
}
