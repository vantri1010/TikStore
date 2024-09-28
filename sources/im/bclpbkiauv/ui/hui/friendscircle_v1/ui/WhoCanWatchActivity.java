package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.WhoCanWatchAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcActivity;

public class WhoCanWatchActivity extends CommFcActivity {
    private WhoCanWatchAdapter adapter;
    @BindView(2131297232)
    RecyclerListView rv;

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_fc_who_can_watch;
    }

    /* access modifiers changed from: protected */
    public void initView() {
        useButterKnife();
        this.rv.setLayoutManager(new LinearLayoutManager(getParentActivity()));
    }

    /* access modifiers changed from: protected */
    public void initData() {
    }
}
