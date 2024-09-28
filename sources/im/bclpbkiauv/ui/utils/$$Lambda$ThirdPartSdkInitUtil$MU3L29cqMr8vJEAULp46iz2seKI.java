package im.bclpbkiauv.ui.utils;

import android.content.Context;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import im.bclpbkiauv.ui.actionbar.Theme;

/* renamed from: im.bclpbkiauv.ui.utils.-$$Lambda$ThirdPartSdkInitUtil$MU3L29cqMr8vJEAULp46iz2seKI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ThirdPartSdkInitUtil$MU3L29cqMr8vJEAULp46iz2seKI implements DefaultRefreshFooterCreator {
    public static final /* synthetic */ $$Lambda$ThirdPartSdkInitUtil$MU3L29cqMr8vJEAULp46iz2seKI INSTANCE = new $$Lambda$ThirdPartSdkInitUtil$MU3L29cqMr8vJEAULp46iz2seKI();

    private /* synthetic */ $$Lambda$ThirdPartSdkInitUtil$MU3L29cqMr8vJEAULp46iz2seKI() {
    }

    public final RefreshFooter createRefreshFooter(Context context, RefreshLayout refreshLayout) {
        return refreshLayout.setPrimaryColors(Theme.getColor(Theme.key_windowBackgroundGray), Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
    }
}
