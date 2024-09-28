package im.bclpbkiauv.ui.hui.adapter.pageAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class AdapterLoadMoreView extends FrameLayout implements AdapterStateView {
    protected int mState;
    protected View progressBar;
    protected MryTextView tv;

    public AdapterLoadMoreView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AdapterLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdapterLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mState = 0;
        init(context);
    }

    /* access modifiers changed from: protected */
    public void init(Context context) {
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        setLayoutParams(new ViewGroup.LayoutParams(-1, AndroidUtilities.dp(70.0f)));
        LinearLayout parent = new LinearLayout(context);
        parent.setOrientation(0);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(20.0f));
        ((RadialProgressView) this.progressBar).setProgressColor(Theme.getColor(Theme.key_actionBarTabActiveText));
        parent.addView(this.progressBar, LayoutHelper.createLinear(28, 28, 16, 0, 0, 3, 0));
        MryTextView mryTextView = new MryTextView(context);
        this.tv = mryTextView;
        mryTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        parent.addView(this.tv, LayoutHelper.createLinear(-2, -2, 16, 3, 0, 0, 0));
        addView(parent, LayoutHelper.createFrame(-2, -2, 17));
        reset();
    }

    public Context getContexts() {
        return getContext();
    }

    public View getView() {
        return this;
    }

    public void show() {
        updateState(this.mState);
    }

    public void updateState(int state) {
        if (this.mState != state) {
            this.mState = state;
            if (state == 0) {
                reset();
            } else if (state == 2) {
                reset();
            } else if (state == 3) {
                loadMoreStart();
            } else if (state == 4) {
                loadMoreFailed(LocaleController.getString("LoadDataErrorDefault", R.string.LoadDataErrorDefault));
            } else if (state == 5) {
                loadMoreNoMoreData();
            }
        }
    }

    public int getState() {
        return this.mState;
    }

    public void reset() {
        this.mState = 0;
        post(new Runnable() {
            public final void run() {
                AdapterLoadMoreView.this.lambda$reset$0$AdapterLoadMoreView();
            }
        });
    }

    public /* synthetic */ void lambda$reset$0$AdapterLoadMoreView() {
        this.progressBar.setVisibility(8);
        this.tv.setText(LocaleController.getString("LoadMore", R.string.LoadMore));
        this.tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
    }

    public void loadMoreStart() {
        this.mState = 3;
        post(new Runnable() {
            public final void run() {
                AdapterLoadMoreView.this.lambda$loadMoreStart$1$AdapterLoadMoreView();
            }
        });
    }

    public /* synthetic */ void lambda$loadMoreStart$1$AdapterLoadMoreView() {
        this.progressBar.setVisibility(0);
        this.tv.setText(LocaleController.getString("Loading", R.string.Loading));
        this.tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
    }

    public void loadMoreFinish() {
        reset();
    }

    public void loadMoreFailed(CharSequence failedReason) {
        this.mState = 4;
        post(new Runnable(failedReason) {
            private final /* synthetic */ CharSequence f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                AdapterLoadMoreView.this.lambda$loadMoreFailed$2$AdapterLoadMoreView(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$loadMoreFailed$2$AdapterLoadMoreView(CharSequence failedReason) {
        this.progressBar.setVisibility(8);
        MryTextView mryTextView = this.tv;
        mryTextView.setText(failedReason + "");
        this.tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText5));
    }

    public void loadMoreNoMoreData() {
        this.mState = 5;
        post(new Runnable() {
            public final void run() {
                AdapterLoadMoreView.this.lambda$loadMoreNoMoreData$3$AdapterLoadMoreView();
            }
        });
    }

    public /* synthetic */ void lambda$loadMoreNoMoreData$3$AdapterLoadMoreView() {
        this.progressBar.setVisibility(8);
        this.tv.setText(LocaleController.getString("LoadCompleted", R.string.LoadCompleted));
        this.tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
    }
}
