package im.bclpbkiauv.ui.hui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import java.util.List;

public class WatchUserPagerAdapter extends PagerAdapter {
    private Context context;
    private List<TLRPC.User> list;

    public WatchUserPagerAdapter(Context context2, List<TLRPC.User> list2) {
        this.context = context2;
        this.list = list2;
    }

    public int getCount() {
        return this.list.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        BackupImageView iv = new BackupImageView(this.context);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        TLRPC.User user = this.list.get(position);
        if (user != null) {
            avatarDrawable.setInfo(user);
            iv.setRoundRadius(AndroidUtilities.dp(25.0f));
            iv.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
        }
        container.addView(iv);
        return iv;
    }

    public float getPageWidth(int position) {
        if (AndroidUtilities.isScreenOriatationPortrait(this.context)) {
            return 0.37f;
        }
        return 0.1f;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
