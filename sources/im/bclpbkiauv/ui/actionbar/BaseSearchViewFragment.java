package im.bclpbkiauv.ui.actionbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.helper.MryDisplayHelper;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;

public abstract class BaseSearchViewFragment extends BaseFragment implements MrySearchView.ISearchViewDelegate {
    protected boolean mblnMove = true;
    protected MrySearchView searchView;

    /* access modifiers changed from: protected */
    public abstract MrySearchView getSearchView();

    public BaseSearchViewFragment() {
    }

    public BaseSearchViewFragment(Bundle args) {
        super(args);
    }

    public void onBeginSlide() {
        super.onBeginSlide();
        closeSearchView();
    }

    public View createView(Context context) {
        this.searchView = getSearchView();
        initSearchView();
        return super.createView(context);
    }

    /* access modifiers changed from: protected */
    public void initSearchView() {
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView != null) {
            mrySearchView.setiSearchViewDelegate(this);
        }
    }

    public void onStart(boolean focus) {
        if (!this.mblnMove) {
            return;
        }
        if (focus) {
            hideTitle(this.fragmentView);
        } else {
            showTitle(this.fragmentView);
        }
    }

    public void onSearchExpand() {
    }

    public boolean canCollapseSearch() {
        return true;
    }

    public void onSearchCollapse() {
    }

    public void onTextChange(String value) {
    }

    public void onActionSearch(String trim) {
    }

    public void hideTitle(View rootView) {
        if (rootView != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", new float[]{0.0f, (float) (-ActionBar.getCurrentActionBarHeight())});
            animator.setDuration(300);
            animator.start();
            if (this.actionBar != null) {
                this.actionBar.setVisibility(4);
            }
            RecyclerListView rv_list = (RecyclerListView) rootView.findViewWithTag("rv_list");
            if (rv_list != null) {
                rootView.getLayoutParams().height = MryDisplayHelper.getScreenHeight(getParentActivity()) + ActionBar.getCurrentActionBarHeight();
                rv_list.getLayoutParams().height = rv_list.getHeight() + ActionBar.getCurrentActionBarHeight();
            }
        }
    }

    public void showTitle(View rootView) {
        if (rootView != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", new float[]{(float) (-ActionBar.getCurrentActionBarHeight()), 0.0f});
            animator.setDuration(300);
            animator.start();
            if (this.actionBar != null) {
                this.actionBar.setVisibility(0);
            }
            RecyclerListView rv_list = (RecyclerListView) rootView.findViewWithTag("rv_list");
            if (rv_list != null) {
                rootView.getLayoutParams().height = MryDisplayHelper.getScreenHeight(getParentActivity());
                rv_list.getLayoutParams().height = rv_list.getHeight() - ActionBar.getCurrentActionBarHeight();
            }
        }
    }

    public void closeSearchView() {
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
    }

    public boolean onBackPressed() {
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView == null || !mrySearchView.isSearchFieldVisible()) {
            return super.onBackPressed();
        }
        this.searchView.closeSearchField();
        return false;
    }
}
