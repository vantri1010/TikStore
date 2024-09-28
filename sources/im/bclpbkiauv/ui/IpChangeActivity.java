package im.bclpbkiauv.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.utils.number.NumberUtil;

public class IpChangeActivity extends BaseFragment {
    private final int done = 1;
    private TextView doneTextBtn;
    private TextView tvChangeIp;
    private TextView tvChangePort;

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_change_ip_layout, (ViewGroup) null, false);
        useButterKnife();
        initActionBar();
        initView();
        return this.fragmentView;
    }

    private void initView() {
        this.tvChangeIp = (TextView) this.fragmentView.findViewById(R.id.et_ip_text);
        this.tvChangePort = (TextView) this.fragmentView.findViewById(R.id.et_port_text);
    }

    private void initActionBar() {
        this.actionBar.setTitle("IP切换");
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        ActionBarMenu menu = this.actionBar.createMenu();
        TextView textView = new TextView(getParentActivity());
        this.doneTextBtn = textView;
        textView.setText("完成");
        this.doneTextBtn.setTextSize(1, 14.0f);
        this.doneTextBtn.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.doneTextBtn.setGravity(16);
        menu.addItemView(1, this.doneTextBtn);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    IpChangeActivity.this.finishFragment();
                } else if (id == 1) {
                    IpChangeActivity.this.changeIp();
                    IpChangeActivity.this.finishFragment();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void changeIp() {
        String ip = this.tvChangeIp.getText().toString();
        String port = this.tvChangePort.getText().toString();
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port) || !NumberUtil.isNumber(port)) {
            ToastUtils.show((CharSequence) "请检查输入的地址和端口");
            return;
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).applyDatacenterAddress(2, ip, Integer.parseInt(port));
        ConnectionsManager.getInstance(UserConfig.selectedAccount).resumeNetworkMaybe();
    }
}
