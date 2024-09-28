package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bjz.comm.net.bean.FcMediaBean;
import com.bjz.comm.net.utils.HttpUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FcPhotosAdapter extends BaseFcAdapter<FcMediaBean> {
    /* access modifiers changed from: private */
    public ArrayList<String> bigPicList = new ArrayList<>();
    private Activity mContext;
    /* access modifiers changed from: private */
    public OnPicClickListener mOnPicClickListener;
    private final int screenWidth;

    public interface OnPicClickListener {
        void onPicClick(View view, List<String> list, int i);
    }

    public FcPhotosAdapter(Collection<FcMediaBean> collection, Activity mContext2, int layoutId, int screenWidth2, OnPicClickListener listener, boolean flag) {
        super(collection, layoutId);
        this.mContext = mContext2;
        this.mOnPicClickListener = listener;
        this.flag = flag;
        this.screenWidth = screenWidth2;
    }

    /* access modifiers changed from: protected */
    public void onBindViewHolder(SmartViewHolder abrItem, FcMediaBean model, final int position) {
        String picName;
        ImageView item_icon = (ImageView) abrItem.itemView.findViewById(R.id.item_icon);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) item_icon.getLayoutParams();
        if (getItemCount() == 1) {
            lp.width = ((this.screenWidth - AndroidUtilities.dp(40.0f)) / 3) * 2;
            lp.height = (lp.width / 3) * 4;
            picName = model.getName();
        } else {
            lp.width = ((this.screenWidth - AndroidUtilities.dp(40.0f)) / 3) - AndroidUtilities.dp(2.0f);
            lp.height = lp.width;
            picName = model.getExt() == 3 ? model.getName() : model.getThum();
        }
        item_icon.setLayoutParams(lp);
        GlideUtils instance = GlideUtils.getInstance();
        instance.load(HttpUtils.getInstance().getDownloadFileUrl() + picName, (Context) this.mContext, item_icon, (int) R.drawable.shape_fc_default_pic_bg);
        FcMediaBean fcMediaBean = (FcMediaBean) this.mList.get(position);
        if (fcMediaBean != null) {
            this.bigPicList.add(fcMediaBean.getName());
        }
        item_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FcPhotosAdapter.this.mOnPicClickListener != null) {
                    FcPhotosAdapter.this.mOnPicClickListener.onPicClick(v, FcPhotosAdapter.this.bigPicList, position);
                }
            }
        });
    }
}
