package im.bclpbkiauv.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.exoplayer2.util.Log;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;

@Deprecated
public class TestActivity extends BaseFragment {
    int num = 0;
    int p = 0;

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle("Test");
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TestActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_test, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(-16711681);
        this.fragmentView.findViewById(R.id.siView).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("------->", " xxxxx");
            }
        });
        return this.fragmentView;
    }
}
