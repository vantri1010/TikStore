package im.bclpbkiauv.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;

public class ChatTypeCtlActivity extends BaseFragment {
    public ChatTypeCtlActivity(int chatId) {
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_chat_type_ctl_layout, (ViewGroup) null, false);
        useButterKnife();
        initActionBar();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle("群组类型");
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatTypeCtlActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
    }

    public void onResume() {
        super.onResume();
    }

    public void changeChatPublicPrivate(boolean isPrivacy) {
    }
}
