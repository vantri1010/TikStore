package im.bclpbkiauv.ui.hui.mine;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class FeedbackActivity extends BaseFragment {
    @BindView(2131296414)
    Button btnFeedSubmit;
    @BindView(2131296566)
    EditText etFeedDescText;
    @BindView(2131296997)
    MryTextView mryFeedTitle;
    @BindView(2131297493)
    TextView tvFeedPromt;

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_feed_back_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
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
                    FeedbackActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
        this.mryFeedTitle.setBold();
        this.mryFeedTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.mryFeedTitle.setText("问题或建议（不少于5个字）");
        this.tvFeedPromt.setBackgroundColor(-570319);
        this.tvFeedPromt.setVisibility(8);
        this.etFeedDescText.setHint("请输入描述性文字（必填）");
        this.etFeedDescText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.etFeedDescText.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(7.5f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.btnFeedSubmit.setText("提交");
        this.btnFeedSubmit.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.btnFeedSubmit.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(7.5f), Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
    }

    private void submitInfomation() {
        String info = this.etFeedDescText.getText().toString().trim();
        if (TextUtils.isEmpty(info)) {
            this.tvFeedPromt.setText("请填写描述文字");
            this.tvFeedPromt.setVisibility(0);
            this.mryFeedTitle.setVisibility(8);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    FeedbackActivity.this.lambda$submitInfomation$0$FeedbackActivity();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        } else if (info.length() < 5) {
            this.tvFeedPromt.setText("请填写不低于5个字的描述");
            this.tvFeedPromt.setVisibility(0);
            this.mryFeedTitle.setVisibility(8);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    FeedbackActivity.this.lambda$submitInfomation$1$FeedbackActivity();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        } else {
            this.tvFeedPromt.setVisibility(8);
            this.mryFeedTitle.setVisibility(0);
        }
    }

    public /* synthetic */ void lambda$submitInfomation$0$FeedbackActivity() {
        this.tvFeedPromt.setVisibility(8);
        this.mryFeedTitle.setVisibility(0);
    }

    public /* synthetic */ void lambda$submitInfomation$1$FeedbackActivity() {
        this.tvFeedPromt.setVisibility(8);
        this.mryFeedTitle.setVisibility(0);
    }

    @OnClick({2131296414})
    public void onViewClicked() {
        submitInfomation();
    }
}
