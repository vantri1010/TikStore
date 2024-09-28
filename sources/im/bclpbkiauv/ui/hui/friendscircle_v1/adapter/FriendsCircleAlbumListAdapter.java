package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import com.bjz.comm.net.bean.RespFcAlbumListBean;
import com.bjz.comm.net.utils.HttpUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import im.bclpbkiauv.ui.hviews.dialogs.Util;
import java.util.Collection;
import java.util.Iterator;

public class FriendsCircleAlbumListAdapter extends BaseFcAdapter<RespFcAlbumListBean> {
    private final Activity mActivity;
    private final int screenWidth;

    public FriendsCircleAlbumListAdapter(Collection<RespFcAlbumListBean> collection, int layoutId, AdapterView.OnItemClickListener listener, Activity mActivity2) {
        super(collection, layoutId, listener);
        this.mActivity = mActivity2;
        this.screenWidth = Util.getScreenWidth(mActivity2);
    }

    public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.mLayoutId, parent, false), this.mListener);
    }

    /* access modifiers changed from: protected */
    public void onBindViewHolder(SmartViewHolder holder, RespFcAlbumListBean model, int position) {
        RelativeLayout rl_item_fc_album_list = (RelativeLayout) holder.itemView.findViewById(R.id.rl_item_fc_album_list);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) rl_item_fc_album_list.getLayoutParams();
        lp.width = ((int) (((float) this.screenWidth) - Util.dp2px(this.mActivity, 40.0f))) / 3;
        lp.height = lp.width;
        lp.bottomMargin = AndroidUtilities.dp(2.0f);
        rl_item_fc_album_list.setLayoutParams(lp);
        ImageView iv_fc_img_thumb = (ImageView) holder.itemView.findViewById(R.id.iv_fc_img_thumb);
        if (model != null && !TextUtils.isEmpty(model.getThum())) {
            GlideUtils instance = GlideUtils.getInstance();
            instance.load(HttpUtils.getInstance().getDownloadFileUrl() + model.getThum(), (Context) this.mActivity, iv_fc_img_thumb, (int) R.drawable.fc_default_pic);
        }
        ((ImageView) holder.itemView.findViewById(R.id.iv_play_button)).setVisibility(model.getExt() == 2 ? 0 : 8);
    }

    public void removeItemByForumID(long forumID) {
        if (this.mList != null && this.mList.size() > 0) {
            Iterator<RespFcAlbumListBean> iterator = this.mList.iterator();
            int i = 0;
            int startIndex = -1;
            int count = 0;
            while (iterator.hasNext()) {
                if (iterator.next().getMainID() == forumID) {
                    iterator.remove();
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    count++;
                }
                i++;
            }
            if (startIndex != -1 && count > 0) {
                notifyItemRangeRemoved(startIndex, count);
            }
        }
    }
}
