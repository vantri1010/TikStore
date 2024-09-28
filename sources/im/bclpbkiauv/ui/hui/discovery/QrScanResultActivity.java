package im.bclpbkiauv.ui.hui.discovery;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;

public class QrScanResultActivity extends BaseFragment {
    private Context mContext;
    private TextView mTvResult;
    private String text;

    public QrScanResultActivity(String text2) {
        this.text = text2;
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_qr_scan_result, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initView();
        initActionBar();
        return this.fragmentView;
    }

    private void initView() {
        this.mTvResult = (TextView) this.fragmentView.findViewById(R.id.tv_result);
        ((ScrollView) this.fragmentView.findViewById(R.id.scroll_view)).setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mTvResult.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.mTvResult.setText(this.text);
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("QrScanResult", R.string.QrScanResult));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Copy", R.string.Copy));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    QrScanResultActivity.this.finishFragment();
                } else if (id == 1) {
                    QrScanResultActivity.this.copy();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void copy() {
        ((ClipboardManager) this.mContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("result", this.mTvResult.getText().toString().trim()));
        ToastUtils.show((int) R.string.CopySuccess);
    }
}
