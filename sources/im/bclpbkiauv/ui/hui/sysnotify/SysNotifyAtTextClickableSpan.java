package im.bclpbkiauv.ui.hui.sysnotify;

import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;

public class SysNotifyAtTextClickableSpan extends ClickableSpan {
    public BaseFragment mBaseFragment;
    public int mUserId;

    public SysNotifyAtTextClickableSpan(int userid, BaseFragment baseFragment) {
        this.mUserId = userid;
        this.mBaseFragment = baseFragment;
    }

    public void onClick(View widget) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", this.mUserId);
        this.mBaseFragment.presentFragment(new NewProfileActivity(bundle));
    }

    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
