package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.bjz.comm.net.bean.AvatarPhotoBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.ShapeUtils;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.Util;
import java.util.Collection;

public class FcDetailLikedUserAdapter extends BaseFcAdapter<FcLikeBean> {
    private static final int ITEM_TYPE_ITEM = 0;
    private static final int ITEM_TYPE_LOAD_MORE;
    private static int itemType;
    /* access modifiers changed from: private */
    public final FcItemActionClickListener listener;
    private final Activity mContext;
    private final int screenWidth;
    private SmartViewHolder smartViewHolder;
    private int spanCount = 8;
    private int thumbUp;

    public FcDetailLikedUserAdapter(Activity context, Collection<FcLikeBean> collection, int layoutId, boolean flag, int thumbUp2, FcItemActionClickListener listener2) {
        super(collection, layoutId);
        this.flag = flag;
        this.mContext = context;
        this.thumbUp = thumbUp2;
        this.listener = listener2;
        this.screenWidth = Util.getScreenWidth(context);
    }

    static {
        itemType = 0;
        int i = 0 + 1;
        itemType = i;
        itemType = i + 1;
        ITEM_TYPE_LOAD_MORE = i;
    }

    public void setThumbUp(boolean isLike) {
        if (isLike) {
            this.thumbUp++;
        } else {
            this.thumbUp--;
        }
    }

    public int getThumbUp() {
        return this.thumbUp;
    }

    public int getItemViewType(int position) {
        int itemCount = getItemCount();
        if (itemCount < this.thumbUp && position == itemCount - 1 && getItemCount() % this.spanCount == 0) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return ITEM_TYPE_ITEM;
    }

    public int getItemCount() {
        int itemCount = super.getItemCount();
        if (itemCount < this.thumbUp) {
            int i = this.spanCount;
            if (itemCount % i == 1) {
                return itemCount - 1;
            }
            if (itemCount % i == 7) {
                return itemCount + 1;
            }
        }
        return itemCount;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != ITEM_TYPE_LOAD_MORE) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_detail_liked_user, (ViewGroup) null), this.mListener);
        }
        MryTextView mryTextView = new MryTextView(this.mContext);
        mryTextView.setId(mryTextView.hashCode());
        mryTextView.setBackground(ShapeUtils.create(this.mContext.getResources().getColor(R.color.color_FFF5F5F5), (float) AndroidUtilities.dp(5.0f)));
        mryTextView.setText("更多");
        FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(40, 40.0f);
        layoutParams.topMargin = AndroidUtilities.dp(4.0f);
        layoutParams.bottomMargin = AndroidUtilities.dp(4.0f);
        mryTextView.setLayoutParams(layoutParams);
        mryTextView.setGravity(17);
        mryTextView.setTextAlignment(4);
        mryTextView.setTextColor(this.mContext.getResources().getColor(R.color.color_FFBCBCBC));
        mryTextView.setTextSize(12.0f);
        mryTextView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.ic_fc_arrow_down);
        drawable.setBounds(0, 0, 0, 0);
        mryTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, drawable);
        SmartViewHolder smartViewHolder2 = new SmartViewHolder(mryTextView, this.mListener);
        this.smartViewHolder = smartViewHolder2;
        return smartViewHolder2;
    }

    /* access modifiers changed from: protected */
    public void onBindViewHolder(SmartViewHolder viewHolder, final FcLikeBean model, final int position) {
        if (getItemViewType(position) == ITEM_TYPE_LOAD_MORE) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (FcDetailLikedUserAdapter.this.listener != null) {
                        FcDetailLikedUserAdapter.this.listener.onAction(v, FcDetailAdapter.Index_click_load_more_like, position, model);
                    }
                }
            });
            return;
        }
        View itemView = viewHolder.itemView;
        itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        BackupImageView ivUserAvatar = (BackupImageView) itemView.findViewById(R.id.iv_user_avatar);
        ivUserAvatar.setRoundRadius(AndroidUtilities.dp(5.0f));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ivUserAvatar.getLayoutParams();
        lp.width = ((this.screenWidth - AndroidUtilities.dp(40.0f)) - (AndroidUtilities.dp(8.0f) * 7)) / 8;
        lp.height = lp.width;
        ivUserAvatar.setLayoutParams(lp);
        bindUserInfo(model.getCreator(), ivUserAvatar, itemView, lp.width + "_" + lp.width, position);
    }

    private void bindUserInfo(final FcUserInfoBean fcUserInfoBean, BackupImageView ivUserAvatar, View itemView, String imgTag, int position) {
        FcUserInfoBean fcUserInfoBean2 = fcUserInfoBean;
        BackupImageView backupImageView = ivUserAvatar;
        ivUserAvatar.setImageResource(R.drawable.shape_bg_item_fc_detail_user_avatar);
        if (fcUserInfoBean2 != null) {
            AvatarPhotoBean avatarPhotoBean = fcUserInfoBean.getPhoto();
            if (avatarPhotoBean != null) {
                int photoSize = avatarPhotoBean.getSmallPhotoSize();
                int localId = avatarPhotoBean.getSmallLocalId();
                long volumeId = avatarPhotoBean.getSmallVolumeId();
                if (photoSize == 0 || volumeId == 0 || avatarPhotoBean.getAccess_hash() == 0) {
                    View view = itemView;
                    String str = imgTag;
                } else {
                    TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
                    inputPeer.user_id = fcUserInfoBean.getUserId();
                    inputPeer.access_hash = fcUserInfoBean.getAccessHash();
                    ImageLocation imageLocation = new ImageLocation();
                    imageLocation.dc_id = 2;
                    imageLocation.photoPeer = inputPeer;
                    imageLocation.location = new TLRPC.TL_fileLocationToBeDeprecated();
                    imageLocation.location.local_id = localId;
                    imageLocation.location.volume_id = volumeId;
                    AvatarDrawable drawable = new AvatarDrawable();
                    drawable.setInfo(fcUserInfoBean.getUserId(), fcUserInfoBean.getFirstName(), fcUserInfoBean.getLastName());
                    ivUserAvatar.setImage(imageLocation, imgTag, (Drawable) drawable, (Object) imageLocation.photoPeer);
                    itemView.setTag(Integer.valueOf(fcUserInfoBean.getUserId()));
                }
            } else {
                View view2 = itemView;
                String str2 = imgTag;
            }
            final int i = position;
            ivUserAvatar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (FcDetailLikedUserAdapter.this.listener != null) {
                        FcDetailLikedUserAdapter.this.listener.onAction(v, FcDetailAdapter.Index_click_avatar, i, fcUserInfoBean);
                    }
                }
            });
            return;
        }
        View view3 = itemView;
        String str3 = imgTag;
        int i2 = position;
    }
}
