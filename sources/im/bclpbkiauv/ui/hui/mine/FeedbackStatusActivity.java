package im.bclpbkiauv.ui.hui.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class FeedbackStatusActivity extends BaseFragment {
    @BindView(2131296415)
    Button btnFinish;
    @BindView(2131296739)
    ImageView ivFeedStatusImg;
    @BindView(2131296995)
    MryTextView mryFeedDescText;
    @BindView(2131296996)
    MryTextView mryFeedStatusText;

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_feed_back_success_layout, (ViewGroup) null, false);
        useButterKnife();
        initActionBar();
        initView();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle("反馈问题或建议");
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FeedbackStatusActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
    }

    @OnClick({2131296415})
    public void onViewClicked() {
        finishFragment();
    }
}
