package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.view.View;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.RespFcIgnoreBean;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.utils.RxHelper;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.CommFcListActivity;
import im.bclpbkiauv.ui.hviews.MrySwitch;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;

public class FcSettingActivity extends CommFcListActivity {
    private static final String TAG = FcSettingActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public MrySwitch mSwitchLookMyFc;
    /* access modifiers changed from: private */
    public MrySwitch mSwitchLookOtherFc;
    private int sex;
    /* access modifiers changed from: private */
    public long userId;

    public FcSettingActivity(long userId2, int sex2) {
        this.userId = userId2;
        this.sex = sex2;
    }

    /* access modifiers changed from: protected */
    public int getLayoutRes() {
        return R.layout.activity_friends_cricle_setting;
    }

    /* access modifiers changed from: protected */
    public void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("friends_circle_setting", R.string.friends_circle_setting));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FcSettingActivity.this.finishFragment();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void initView() {
        initActionBar();
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ColorTextView txt_report1 = (ColorTextView) this.fragmentView.findViewById(R.id.txt_report1);
        ColorTextView txt_report2 = (ColorTextView) this.fragmentView.findViewById(R.id.txt_report2);
        int i = this.sex;
        if (i != 0) {
            if (i == 1) {
                txt_report1.setText(LocaleController.getString(R.string.friendscircle_dont_let_him_look));
                txt_report2.setText(LocaleController.getString(R.string.friendscircle_dont_look_him));
            } else if (i == 2) {
                txt_report1.setText(LocaleController.getString(R.string.friendscircle_dont_let_her_look));
                txt_report2.setText(LocaleController.getString(R.string.friendscircle_dont_look_her));
            }
        }
        this.mSwitchLookMyFc = (MrySwitch) this.fragmentView.findViewById(R.id.switch_look_me);
        this.mSwitchLookOtherFc = (MrySwitch) this.fragmentView.findViewById(R.id.switch_look_other);
        this.mSwitchLookMyFc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FcSettingActivity.this.mSwitchLookMyFc.isChecked()) {
                    ArrayList<FcIgnoreUserBean> ignores = new ArrayList<>();
                    ignores.add(new FcIgnoreUserBean(FcSettingActivity.this.userId, 1));
                    FcSettingActivity.this.doDeleteIgnoreUser(ignores);
                    return;
                }
                ArrayList<FcIgnoreUserBean> ignores2 = new ArrayList<>();
                ignores2.add(new FcIgnoreUserBean(FcSettingActivity.this.userId, 1));
                FcSettingActivity.this.doAddIgnoreUser(ignores2);
            }
        });
        this.mSwitchLookOtherFc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FcSettingActivity.this.mSwitchLookOtherFc.isChecked()) {
                    ArrayList<FcIgnoreUserBean> ignores = new ArrayList<>();
                    ignores.add(new FcIgnoreUserBean(FcSettingActivity.this.userId, 2));
                    FcSettingActivity.this.doDeleteIgnoreUser(ignores);
                    return;
                }
                ArrayList<FcIgnoreUserBean> ignores2 = new ArrayList<>();
                ignores2.add(new FcIgnoreUserBean(FcSettingActivity.this.userId, 2));
                FcSettingActivity.this.doAddIgnoreUser(ignores2);
            }
        });
    }

    public void doAddIgnoreUserSucc(ArrayList<FcIgnoreUserBean> ignores, String msg) {
        doSetIgnoreUserAfterViewChange(true, ignores);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcIgnoreUser, TAG, ignores);
    }

    public void doAddIgnoreUserFailed(String msg) {
    }

    public void doDeleteIgnoreUserSucc(ArrayList<FcIgnoreUserBean> ignores, String msg) {
        doSetIgnoreUserAfterViewChange(false, ignores);
    }

    public void doDeleteIgnoreUserFiled(String msg) {
    }

    /* access modifiers changed from: protected */
    public void doSetIgnoreUserAfterViewChange(boolean isIgnore, ArrayList<FcIgnoreUserBean> ignores) {
        FcIgnoreUserBean ignoreUserBean;
        if (ignores != null && ignores.size() > 0 && (ignoreUserBean = ignores.get(0)) != null) {
            int look = ignoreUserBean.getLook();
            if (look == 1) {
                this.mSwitchLookMyFc.setChecked(isIgnore, true);
            } else if (look == 2) {
                this.mSwitchLookOtherFc.setChecked(isIgnore, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initData() {
        getFcIgnoreSetting();
    }

    private void getFcIgnoreSetting() {
        RxHelper.getInstance().sendRequest(TAG, ApiFactory.getInstance().getApiMomentForum().getUserIgnoreSetting(this.userId), new Consumer() {
            public final void accept(Object obj) {
                FcSettingActivity.this.lambda$getFcIgnoreSetting$0$FcSettingActivity((BResponse) obj);
            }
        }, $$Lambda$FcSettingActivity$rvJls0YAUwZgDelUx0D4SGEMH8.INSTANCE);
    }

    public /* synthetic */ void lambda$getFcIgnoreSetting$0$FcSettingActivity(BResponse response) throws Exception {
        RespFcIgnoreBean data;
        if (response.isState() && (data = (RespFcIgnoreBean) response.Data) != null) {
            this.mSwitchLookMyFc.setChecked(data.isLookMe(), true);
            this.mSwitchLookOtherFc.setChecked(data.isLookOther(), true);
        }
    }

    static /* synthetic */ void lambda$getFcIgnoreSetting$1(Throwable throwable) throws Exception {
    }

    public void onInputReplyContent(String content, ArrayList<FCEntitysRequest> arrayList) {
    }
}
