package im.bclpbkiauv.ui.hui.friendscircle_v1.base;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FragmentBackHandler;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.BackHandlerHelper;
import java.util.List;

public abstract class LazyLoadFragment extends Fragment implements FragmentBackHandler {
    private boolean isDataLoaded;
    private boolean isHidden = true;
    protected boolean isPaused = true;
    private boolean isViewCreated;
    private boolean isVisibleToUser;

    /* access modifiers changed from: protected */
    public abstract void loadData();

    public void setUserVisibleHint(boolean isVisibleToUser2) {
        super.setUserVisibleHint(isVisibleToUser2);
        this.isVisibleToUser = isVisibleToUser2;
        tryLoadData();
        checkIsVisible();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.isViewCreated = true;
        tryLoadData();
    }

    public void onResume() {
        super.onResume();
        if (this.isPaused) {
            this.isPaused = false;
            checkIsVisible();
        }
    }

    public void onPause() {
        super.onPause();
        if (!this.isPaused) {
            this.isPaused = true;
            checkIsVisible();
        }
    }

    public void onResumeForBaseFragment() {
        if (this.isPaused) {
            this.isPaused = false;
            checkIsVisible();
        }
    }

    public void onPauseForBaseFragment() {
        if (!this.isPaused) {
            this.isPaused = true;
            checkIsVisible();
        }
    }

    public void checkLoadData() {
        if (!this.isDataLoaded) {
            tryLoadData();
        }
        checkIsVisible();
    }

    public void checkIsVisible() {
        if (this.isViewCreated && this.isDataLoaded) {
            if (!isParentVisible() || !this.isVisibleToUser || this.isPaused) {
                onInvisible();
            } else {
                onVisible();
            }
        }
    }

    public void onVisible() {
    }

    public void onInvisible() {
    }

    public boolean isDataLoaded() {
        return this.isDataLoaded;
    }

    public boolean isVisibleToUser() {
        return this.isVisibleToUser;
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.isHidden = hidden;
        if (!hidden) {
            tryLoadData1();
        }
    }

    private boolean isParentVisible() {
        Fragment fragment = getParentFragment();
        return fragment == null || ((fragment instanceof LazyLoadFragment) && ((LazyLoadFragment) fragment).isVisibleToUser) || ((fragment instanceof BaseFmts) && ((BaseFmts) fragment).isFragmentVisible());
    }

    private void dispatchParentVisibleState() {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (!fragments.isEmpty()) {
            for (Fragment child : fragments) {
                if ((child instanceof LazyLoadFragment) && ((LazyLoadFragment) child).isVisibleToUser) {
                    ((LazyLoadFragment) child).tryLoadData();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isNeedReload() {
        return false;
    }

    public void tryLoadData() {
        if (this.isViewCreated && this.isVisibleToUser && isParentVisible()) {
            if (isNeedReload() || !this.isDataLoaded) {
                loadData();
                this.isDataLoaded = true;
                dispatchParentVisibleState();
            }
        }
    }

    private void dispatchParentHiddenState() {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (!fragments.isEmpty()) {
            for (Fragment child : fragments) {
                if ((child instanceof LazyLoadFragment) && !((LazyLoadFragment) child).isHidden) {
                    ((LazyLoadFragment) child).tryLoadData1();
                }
            }
        }
    }

    private boolean isParentHidden() {
        Fragment fragment = getParentFragment();
        if (fragment == null) {
            return false;
        }
        if (!(fragment instanceof LazyLoadFragment) || ((LazyLoadFragment) fragment).isHidden) {
            return true;
        }
        return false;
    }

    public void tryLoadData1() {
        if (isParentHidden()) {
            return;
        }
        if (isNeedReload() || !this.isDataLoaded) {
            loadData();
            this.isDataLoaded = true;
            dispatchParentHiddenState();
        }
    }

    public void onDestroy() {
        this.isViewCreated = false;
        this.isVisibleToUser = false;
        this.isDataLoaded = false;
        this.isHidden = true;
        this.isPaused = true;
        super.onDestroy();
    }

    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress((Fragment) this);
    }
}
