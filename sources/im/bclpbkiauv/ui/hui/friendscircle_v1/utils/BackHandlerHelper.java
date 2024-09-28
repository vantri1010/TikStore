package im.bclpbkiauv.ui.hui.friendscircle_v1.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FragmentBackHandler;
import java.util.List;

public class BackHandlerHelper {
    public static boolean handleBackPress(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return false;
        }
        for (int i = fragments.size() - 1; i >= 0; i--) {
            if (isFragmentBackHandled(fragments.get(i))) {
                return true;
            }
        }
        if (fragmentManager.getBackStackEntryCount() <= 0) {
            return false;
        }
        fragmentManager.popBackStack();
        return true;
    }

    public static boolean handleBackPress(Fragment fragment) {
        return handleBackPress(fragment.getChildFragmentManager());
    }

    public static boolean handleBackPress(FragmentActivity fragmentActivity) {
        return handleBackPress(fragmentActivity.getSupportFragmentManager());
    }

    public static boolean isFragmentBackHandled(Fragment fragment) {
        return fragment != null && fragment.isVisible() && fragment.getUserVisibleHint() && (fragment instanceof FragmentBackHandler) && ((FragmentBackHandler) fragment).onBackPressed();
    }
}
