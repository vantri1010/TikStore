package im.bclpbkiauv.ui.utils;

import android.content.Context;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import im.bclpbkiauv.ui.actionbar.Theme;

/* renamed from: im.bclpbkiauv.ui.utils.-$$Lambda$ThirdPartSdkInitUtil$YySnwPfh111coomuGvLAWOMTQDM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ThirdPartSdkInitUtil$YySnwPfh111coomuGvLAWOMTQDM implements DefaultRefreshHeaderCreator {
    public static final /* synthetic */ $$Lambda$ThirdPartSdkInitUtil$YySnwPfh111coomuGvLAWOMTQDM INSTANCE = new $$Lambda$ThirdPartSdkInitUtil$YySnwPfh111coomuGvLAWOMTQDM();

    private /* synthetic */ $$Lambda$ThirdPartSdkInitUtil$YySnwPfh111coomuGvLAWOMTQDM() {
    }

    public final RefreshHeader createRefreshHeader(Context context, RefreshLayout refreshLayout) {
        return refreshLayout.setPrimaryColors(Theme.getColor(Theme.key_windowBackgroundGray), Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
    }
}
