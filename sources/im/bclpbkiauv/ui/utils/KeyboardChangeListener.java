package im.bclpbkiauv.ui.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class KeyboardChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private View mContentView;
    private boolean mIsDestroy;
    private int mKeyBoardHeight;
    private KeyBoardListener mKeyBoardListener;
    private int mLastKeyBoardHeight;
    private int mOriginHeight;
    private int mPreHeight;
    private Rect mRect;

    public interface KeyBoardListener {
        void onKeyboardChange(boolean z, int i);
    }

    public void setKeyBoardListener(KeyBoardListener keyBoardListen) {
        this.mKeyBoardListener = keyBoardListen;
    }

    public KeyboardChangeListener(Activity contextObj) {
        if (contextObj != null) {
            init(findContentView(contextObj));
        }
    }

    public KeyboardChangeListener(View view) {
        init(view);
    }

    private void init(View view) {
        if (view != null) {
            this.mContentView = view;
            if (view != null) {
                this.mRect = new Rect();
                addContentTreeObserver();
            }
        }
    }

    private View findContentView(Activity contextObj) {
        return contextObj.findViewById(16908290);
    }

    private void addContentTreeObserver() {
        this.mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void destroy() {
        this.mIsDestroy = true;
        View view = this.mContentView;
        if (!(view == null || view.getViewTreeObserver() == null)) {
            this.mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            this.mContentView = null;
        }
        this.mRect = null;
        this.mKeyBoardListener = null;
        this.mOriginHeight = 0;
        this.mPreHeight = 0;
        this.mKeyBoardHeight = 0;
        this.mLastKeyBoardHeight = 0;
    }

    public void onGlobalLayout() {
        int currHeight;
        boolean isShow;
        if (!this.mIsDestroy && (currHeight = this.mContentView.getHeight()) != 0) {
            boolean hasChange = false;
            int keyBoardHeight = 0;
            boolean z = true;
            if (this.mPreHeight == 0) {
                this.mPreHeight = currHeight;
                this.mOriginHeight = currHeight;
            } else {
                keyBoardHeight = getKeyboardHeight();
                if (this.mPreHeight != currHeight) {
                    hasChange = true;
                    this.mPreHeight = currHeight;
                    if (keyBoardHeight == 0) {
                        keyBoardHeight = this.mOriginHeight - currHeight;
                    }
                } else {
                    hasChange = keyBoardHeight != this.mLastKeyBoardHeight;
                }
                this.mLastKeyBoardHeight = keyBoardHeight;
            }
            if (hasChange) {
                int i = this.mOriginHeight;
                if (i != this.mPreHeight) {
                    if (keyBoardHeight == 0) {
                        keyBoardHeight = i - currHeight;
                    }
                    if (keyBoardHeight <= 0) {
                        z = false;
                    }
                    isShow = z;
                } else if (keyBoardHeight > 0) {
                    isShow = true;
                } else {
                    isShow = false;
                }
                if (this.mKeyBoardHeight == 0) {
                    this.mKeyBoardHeight = keyBoardHeight;
                }
                KeyBoardListener keyBoardListener = this.mKeyBoardListener;
                if (keyBoardListener != null) {
                    keyBoardListener.onKeyboardChange(isShow, this.mKeyBoardHeight);
                }
            }
        }
    }

    public int getKeyboardHeight() {
        this.mContentView.getWindowVisibleDisplayFrame(this.mRect);
        if (this.mRect.bottom == 0 && this.mRect.top == 0) {
            return 0;
        }
        return Math.max(0, ((this.mContentView.getHeight() - (this.mRect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(this.mContentView)) - (this.mRect.bottom - this.mRect.top));
    }
}
