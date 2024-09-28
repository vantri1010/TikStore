package im.bclpbkiauv.ui.hui.friendscircle_v1;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.tablayout.SlidingTabLayout;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.NoScrollViewPager;

public class FriendsCircleActivity_ViewBinding implements Unbinder {
    private FriendsCircleActivity target;

    public FriendsCircleActivity_ViewBinding(FriendsCircleActivity target2, View source) {
        this.target = target2;
        target2.containerTab = Utils.findRequiredView(source, R.id.containerTab, "field 'containerTab'");
        target2.tabLayout = (SlidingTabLayout) Utils.findRequiredViewAsType(source, R.id.tabLayout, "field 'tabLayout'", SlidingTabLayout.class);
        target2.viewpager = (NoScrollViewPager) Utils.findRequiredViewAsType(source, R.id.viewpager, "field 'viewpager'", NoScrollViewPager.class);
    }

    public void unbind() {
        FriendsCircleActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.containerTab = null;
            target2.tabLayout = null;
            target2.viewpager = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
