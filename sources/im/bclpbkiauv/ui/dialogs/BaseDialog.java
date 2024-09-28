package im.bclpbkiauv.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import com.blankj.utilcode.util.ScreenUtils;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u0016\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u001a\u0010\u000f\u001a\u00020\f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\u000eH\u0016J\u0012\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0014J\b\u0010\u0017\u001a\u00020\u0014H\u0014J\u000e\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aJ\u001e\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u0005J\u001e\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\u0005R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0004\u001a\u00020\u0005X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n¨\u0006\u001f"}, d2 = {"Lim/bclpbkiauv/ui/dialogs/BaseDialog;", "Landroidx/appcompat/app/AlertDialog;", "activity", "Landroid/content/Context;", "res", "", "(Landroid/content/Context;I)V", "getRes", "()I", "setRes", "(I)V", "dispatchTouchEvent", "", "ev", "Landroid/view/MotionEvent;", "isOutsideEditTextClick", "v", "Landroid/view/View;", "event", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onStart", "setDarkBg", "bg", "", "setWidthAndHeight", "width", "height", "params", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: BaseDialog.kt */
public class BaseDialog extends AlertDialog {
    private final Context activity;
    private int res;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public BaseDialog(Context activity2, int res2) {
        super(activity2);
        Intrinsics.checkParameterIsNotNull(activity2, "activity");
        this.activity = activity2;
        this.res = res2;
    }

    public final int getRes() {
        return this.res;
    }

    public final void setRes(int i) {
        this.res = i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(this.res);
        setCanceledOnTouchOutside(false);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    public final void setWidthAndHeight(float width, float height, int params) {
        Window window = getWindow();
        if (window == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(window, "window!!");
        WindowManager.LayoutParams windowParams = window.getAttributes();
        Intrinsics.checkExpressionValueIsNotNull(windowParams, "window!!.attributes");
        windowParams.gravity = params;
        if (height == 0.0f) {
            windowParams.height = -2;
        } else {
            windowParams.height = (int) (((float) ScreenUtils.getScreenHeight()) * height);
        }
        if (width == 0.0f) {
            windowParams.width = -2;
        } else {
            windowParams.width = (int) (((float) ScreenUtils.getScreenWidth()) * width);
        }
        windowParams.dimAmount = 0.6f;
        Window window2 = getWindow();
        if (window2 == null) {
            Intrinsics.throwNpe();
        }
        window2.setBackgroundDrawableResource(17170445);
        Window window3 = getWindow();
        if (window3 == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(window3, "window!!");
        window3.setAttributes(windowParams);
    }

    public final void setWidthAndHeight(int width, int height, int params) {
        Window window = getWindow();
        if (window == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(window, "window!!");
        WindowManager.LayoutParams windowParams = window.getAttributes();
        Intrinsics.checkExpressionValueIsNotNull(windowParams, "window!!.attributes");
        windowParams.gravity = 17;
        if (height == 0) {
            windowParams.height = -2;
        } else {
            windowParams.height = height;
        }
        if (width == 0) {
            windowParams.width = -2;
        } else {
            windowParams.width = width;
        }
        windowParams.dimAmount = 0.6f;
        Window window2 = getWindow();
        if (window2 == null) {
            Intrinsics.throwNpe();
        }
        window2.setBackgroundDrawableResource(17170445);
        Window window3 = getWindow();
        if (window3 == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(window3, "window!!");
        window3.setAttributes(windowParams);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        InputMethodManager imm;
        Intrinsics.checkParameterIsNotNull(ev, "ev");
        if (ev.getAction() == 0) {
            View v = getCurrentFocus();
            if (isOutsideEditTextClick(v, ev) && (imm = (InputMethodManager) this.activity.getSystemService("input_method")) != null) {
                if (v == null) {
                    Intrinsics.throwNpe();
                }
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return super.dispatchTouchEvent(ev);
        }
        Window window = getWindow();
        if (window == null) {
            Intrinsics.throwNpe();
        }
        if (window.superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isOutsideEditTextClick(View v, MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(event, NotificationCompat.CATEGORY_EVENT);
        if (v == null || !(v instanceof EditText)) {
            return false;
        }
        int[] leftTop = {0, 0};
        v.getLocationInWindow(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int bottom = v.getHeight() + top;
        int right = v.getWidth() + left;
        if (event.getX() <= ((float) left) || event.getX() >= ((float) right) || event.getY() <= ((float) top) || event.getY() >= ((float) bottom)) {
            return true;
        }
        return false;
    }

    public final void setDarkBg(float bg) {
        Window window = getWindow();
        if (window == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(window, "window!!");
        WindowManager.LayoutParams windowParams = window.getAttributes();
        Intrinsics.checkExpressionValueIsNotNull(windowParams, "window!!.attributes");
        windowParams.dimAmount = bg;
        Window window2 = getWindow();
        if (window2 == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(window2, "window!!");
        window2.setAttributes(windowParams);
    }
}
