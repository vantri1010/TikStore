package com.gyf.barlibrary;

import androidx.fragment.app.Fragment;

@Deprecated
public abstract class ImmersionFragment extends Fragment {
    /* access modifiers changed from: protected */
    @Deprecated
    public abstract void immersionInit();

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && immersionEnabled()) {
            immersionInit();
        }
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public boolean immersionEnabled() {
        return true;
    }
}
