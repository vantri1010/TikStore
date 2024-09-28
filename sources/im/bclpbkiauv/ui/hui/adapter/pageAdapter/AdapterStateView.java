package im.bclpbkiauv.ui.hui.adapter.pageAdapter;

import android.content.Context;
import android.view.View;

public interface AdapterStateView {
    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_LOADING_MORE = 3;
    public static final int STATUS_LOAD_MORE_COMMPLETED = 5;
    public static final int STATUS_LOAD_MORE_FAILED = 4;
    public static final int STATUS_REFRESHING = 1;
    public static final int STATUS_REFRESH_FAILED = 2;

    Context getContexts();

    int getState();

    View getView();

    void loadMoreFailed(CharSequence charSequence);

    void loadMoreFinish();

    void loadMoreNoMoreData();

    void loadMoreStart();

    void reset();

    void show();

    void updateState(int i);
}
