package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import com.bjz.comm.net.bean.AvatarPhotoBean;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.bjz.comm.net.bean.TopicBean;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCLinkSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcChildReplyListDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanCreateListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.LinkSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextBuilder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.Collection;

public class FcDialogChildReplyAdapter extends BaseFcAdapter<FcReplyBean> {
    private static final int ITEM_TYPE_BOTTOM;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_REPLY;
    private static int itemType;
    private final int currentUserId;
    /* access modifiers changed from: private */
    public FcChildReplyListDialog.ChildReplyListListener listener;
    private Context mContext;
    /* access modifiers changed from: private */
    public final int mGuid;
    /* access modifiers changed from: private */
    public FcReplyBean mParentFcReplyBean;
    /* access modifiers changed from: private */
    public int mParentFcReplyPosition;
    private SpanCreateListener spanCreateListener = new SpanCreateListener() {
        public ClickAtUserSpan getCustomClickAtUserSpan(Context context, FCEntitysResponse FCEntitysResponse, int color, SpanAtUserCallBack spanClickCallBack) {
            return new FCClickAtUserSpan(FcDialogChildReplyAdapter.this.mGuid, FCEntitysResponse, color, new SpanAtUserCallBack() {
                public void onPresentFragment(BaseFragment baseFragment) {
                    if (FcDialogChildReplyAdapter.this.listener != null && baseFragment != null) {
                        FcDialogChildReplyAdapter.this.listener.onPresentFragment(baseFragment);
                    }
                }
            });
        }

        public ClickTopicSpan getCustomClickTopicSpan(Context context, TopicBean topicBean, int color, SpanTopicCallBack spanTopicCallBack) {
            return new FCClickTopicSpan(topicBean, color, spanTopicCallBack);
        }

        public LinkSpan getCustomLinkSpan(Context context, String url, int color, SpanUrlCallBack spanUrlCallBack) {
            return new FCLinkSpan(context, url, color, spanUrlCallBack);
        }
    };

    public FcDialogChildReplyAdapter(Collection<FcReplyBean> collection, Context mContext2, int guid, FcChildReplyListDialog.ChildReplyListListener listener2) {
        super(collection, R.layout.item_fc_detail_child_reply);
        this.mContext = mContext2;
        this.mGuid = guid;
        this.listener = listener2;
        this.currentUserId = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id;
    }

    public void setListener(FcChildReplyListDialog.ChildReplyListListener listener2) {
        this.listener = listener2;
    }

    public void setFcReplyBean(FcReplyBean ParentFcReplyBean, int ParentFcReplyPosition) {
        this.mParentFcReplyBean = ParentFcReplyBean;
        this.mParentFcReplyPosition = ParentFcReplyPosition;
    }

    public void setParentFcReplyBean(FcReplyBean mParentFcReplyBean2) {
        this.mParentFcReplyBean = mParentFcReplyBean2;
    }

    public void setParentFcReplyPosition(int mParentFcReplyPosition2) {
        this.mParentFcReplyPosition = mParentFcReplyPosition2;
    }

    public FcReplyBean getParentFcReplyBean() {
        return this.mParentFcReplyBean;
    }

    public int getParentFcReplyPosition() {
        return this.mParentFcReplyPosition;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public long getEndListId() {
        if (this.mList.size() == 0) {
            return 0;
        }
        return ((FcReplyBean) this.mList.get((this.mList.size() - 1) - getFooterSize())).getCommentID();
    }

    public int getFooterSize() {
        FcReplyBean fcReplyBean;
        if (getDataList().size() <= 1 || (fcReplyBean = (FcReplyBean) getDataList().get(getItemCount() - 1)) == null || fcReplyBean.getCommentID() != 0) {
            return 0;
        }
        return 1;
    }

    static {
        itemType = 0;
        int i = 0 + 1;
        itemType = i;
        int i2 = i + 1;
        itemType = i2;
        ITEM_TYPE_BOTTOM = i;
        itemType = i2 + 1;
        ITEM_TYPE_REPLY = i2;
    }

    public int getItemViewType(int position) {
        if (position == 0 && this.mParentFcReplyBean != null) {
            return ITEM_TYPE_HEADER;
        }
        if (position == getItemCount() - 1 && ((FcReplyBean) this.mList.get(position)).getCommentID() == 0) {
            return ITEM_TYPE_BOTTOM;
        }
        return ITEM_TYPE_REPLY;
    }

    public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_reply_list_header, parent, false), this.mListener);
        }
        if (viewType == ITEM_TYPE_BOTTOM) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_fc_footer, parent, false), this.mListener);
        }
        return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_detail_child_reply, parent, false), this.mListener);
    }

    /* access modifiers changed from: protected */
    public void onBindViewHolder(SmartViewHolder viewHolder, FcReplyBean model, int position) {
        SmartViewHolder smartViewHolder = viewHolder;
        final int i = position;
        smartViewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (getItemViewType(i) == ITEM_TYPE_HEADER) {
            View itemView = smartViewHolder.itemView;
            BackupImageView ivUserAvatar = (BackupImageView) itemView.findViewById(R.id.iv_user_avatar);
            ivUserAvatar.setRoundRadius(AndroidUtilities.dp(5.0f));
            RichTextView txt_parent_comment = (RichTextView) itemView.findViewById(R.id.txt_parent_comment);
            itemView.findViewById(R.id.view_divider).setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            FcUserInfoBean creator = this.mParentFcReplyBean.getCreator();
            long createAt = this.mParentFcReplyBean.getCreateAt();
            String str = "0";
            final MryTextView btnLike = (MryTextView) itemView.findViewById(R.id.btn_like);
            bindUserInfo(creator, createAt, ivUserAvatar, (MryTextView) itemView.findViewById(R.id.tv_user_name), (MryTextView) itemView.findViewById(R.id.tv_publish_time), position);
            btnLike.setSelected(this.mParentFcReplyBean.isHasThumb());
            btnLike.setText(this.mParentFcReplyBean.getThumbUp() > 0 ? String.valueOf(this.mParentFcReplyBean.getThumbUp()) : str);
            btnLike.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    btnLike.setClickable(false);
                    FcDialogChildReplyAdapter.this.setAction(v, FcDetailAdapter.Index_child_reply_click_like, i, FcDialogChildReplyAdapter.this.mParentFcReplyBean);
                }
            });
            bindReplyView(this.mParentFcReplyBean, txt_parent_comment, false, position, itemView);
        } else {
            String str2 = "0";
            if (getItemViewType(i) != ITEM_TYPE_BOTTOM) {
                View itemView2 = smartViewHolder.itemView;
                RichTextView txt_parent_comment2 = (RichTextView) itemView2.findViewById(R.id.txt_parent_comment);
                BackupImageView ivUserAvatar2 = (BackupImageView) itemView2.findViewById(R.id.iv_user_avatar);
                ivUserAvatar2.setRoundRadius(AndroidUtilities.dp(5.0f));
                itemView2.findViewById(R.id.view_divider).setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                final MryTextView btnLike2 = (MryTextView) itemView2.findViewById(R.id.btn_like);
                bindUserInfo(model.getCreator(), model.getCreateAt(), ivUserAvatar2, (MryTextView) itemView2.findViewById(R.id.tv_user_name), (MryTextView) itemView2.findViewById(R.id.tv_publish_time), position);
                btnLike2.setSelected(model.isHasThumb());
                btnLike2.setText(model.getThumbUp() > 0 ? String.valueOf(model.getThumbUp()) : str2);
                final FcReplyBean fcReplyBean = model;
                btnLike2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        btnLike2.setClickable(false);
                        FcDialogChildReplyAdapter.this.setAction(v, FcDetailAdapter.Index_child_reply_click_like, i, fcReplyBean);
                    }
                });
                bindReplyView(model, txt_parent_comment2, true, position, itemView2);
                return;
            }
        }
        FcReplyBean fcReplyBean2 = model;
    }

    private RichTextView bindReplyView(FcReplyBean model, RichTextView txt_comment, boolean isReply, int position, View itemView) {
        RichTextView txt_comment2;
        final FcReplyBean fcReplyBean = model;
        final int i = position;
        View view = itemView;
        if (txt_comment == null) {
            txt_comment2 = new RichTextView(this.mContext);
            txt_comment2.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
        } else {
            txt_comment2 = txt_comment;
        }
        new RichTextBuilder(this.mContext).setContent(model.getContent() == null ? "" : model.getContent()).setLinkColor(ContextCompat.getColor(this.mContext, R.color.color_FF09A4C9)).setAtColor(ContextCompat.getColor(this.mContext, R.color.color_FF09A4C9)).setTextView(txt_comment2).setListUser(model.getEntitys()).setNeedUrl(true).setSpanCreateListener(this.spanCreateListener).build();
        FcUserInfoBean replayUserInfo = model.getReplayUser();
        if (isReply && replayUserInfo != null) {
            SpannableStringBuilder headerStr = new SpannableStringBuilder();
            if (model.getReplayID() != this.mParentFcReplyBean.getForumID()) {
                headerStr.append(LocaleController.getString("Reply", R.string.Reply));
                headerStr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), 0, headerStr.length(), 33);
                int StartIndex = headerStr.length();
                headerStr.append(StringUtils.handleTextName(ContactsController.formatName(replayUserInfo.getFirstName(), replayUserInfo.getLastName()), 12));
                headerStr.append(" : ");
                headerStr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), StartIndex, headerStr.length(), 33);
            }
            CharSequence content = txt_comment2.getText();
            if (!TextUtils.isEmpty(headerStr) && !TextUtils.isEmpty(content)) {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
                stringBuilder.insert(0, headerStr, 0, headerStr.length());
                txt_comment2.setText(stringBuilder);
            }
            FcUserInfoBean creatorUserInfo = model.getCreator();
            String receiver = "";
            if (creatorUserInfo != null) {
                receiver = StringUtils.handleTextName(ContactsController.formatName(creatorUserInfo.getFirstName(), creatorUserInfo.getLastName()), 12);
            }
            final String finalCommentUserName = receiver;
            if (view != null) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        if (FcDialogChildReplyAdapter.this.listener == null) {
                            return true;
                        }
                        FcDialogChildReplyAdapter.this.listener.onChildReplyClick(v, finalCommentUserName, fcReplyBean, FcDialogChildReplyAdapter.this.mParentFcReplyPosition, i, true);
                        return true;
                    }
                });
                int i2 = this.currentUserId;
                if (!(i2 == 0 || ((long) i2) == model.getCreateBy())) {
                    view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (FcDialogChildReplyAdapter.this.listener != null) {
                                FcDialogChildReplyAdapter.this.listener.onChildReplyClick(v, finalCommentUserName, fcReplyBean, FcDialogChildReplyAdapter.this.mParentFcReplyPosition, i, false);
                            }
                        }
                    });
                }
            }
            txt_comment2.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    if (FcDialogChildReplyAdapter.this.listener == null) {
                        return true;
                    }
                    FcDialogChildReplyAdapter.this.listener.onChildReplyClick(v, finalCommentUserName, fcReplyBean, FcDialogChildReplyAdapter.this.mParentFcReplyPosition, i, true);
                    return true;
                }
            });
            int i3 = this.currentUserId;
            if (!(i3 == 0 || ((long) i3) == model.getCreateBy())) {
                txt_comment2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (FcDialogChildReplyAdapter.this.listener != null) {
                            FcDialogChildReplyAdapter.this.listener.onChildReplyClick(v, finalCommentUserName, fcReplyBean, FcDialogChildReplyAdapter.this.mParentFcReplyPosition, i, false);
                        }
                    }
                });
            }
        }
        return txt_comment2;
    }

    private void bindUserInfo(FcUserInfoBean fcUserInfoBean, long createAt, BackupImageView ivUserAvatar, MryTextView tvUserName, MryTextView tvPublishTime, int position) {
        BackupImageView backupImageView = ivUserAvatar;
        MryTextView mryTextView = tvUserName;
        final int i = position;
        if (fcUserInfoBean != null) {
            AvatarPhotoBean avatarPhotoBean = fcUserInfoBean.getPhoto();
            if (avatarPhotoBean != null) {
                int photoSize = avatarPhotoBean.getSmallPhotoSize();
                int localId = avatarPhotoBean.getSmallLocalId();
                long volumeId = avatarPhotoBean.getSmallVolumeId();
                if (!(photoSize == 0 || volumeId == 0 || avatarPhotoBean.getAccess_hash() == 0)) {
                    TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
                    inputPeer.user_id = fcUserInfoBean.getUserId();
                    inputPeer.access_hash = fcUserInfoBean.getAccessHash();
                    ImageLocation imageLocation = new ImageLocation();
                    imageLocation.dc_id = 2;
                    imageLocation.photoPeer = inputPeer;
                    imageLocation.location = new TLRPC.TL_fileLocationToBeDeprecated();
                    imageLocation.location.local_id = localId;
                    imageLocation.location.volume_id = volumeId;
                    backupImageView.setImage(imageLocation, "40_40", (Drawable) new AvatarDrawable(), (Object) inputPeer);
                }
            }
            mryTextView.setText(StringUtils.handleTextName(ContactsController.formatName(fcUserInfoBean.getFirstName(), fcUserInfoBean.getLastName()), 12));
            tvPublishTime.setText(TimeUtils.fcFormat2Date(createAt));
            backupImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FcDialogChildReplyAdapter.this.setAction(v, FcDetailAdapter.Index_child_reply_click_avatar, i, FcDialogChildReplyAdapter.this.mParentFcReplyBean);
                }
            });
            mryTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FcDialogChildReplyAdapter.this.setAction(v, FcDetailAdapter.Index_child_reply_click_avatar, i, FcDialogChildReplyAdapter.this.mParentFcReplyBean);
                }
            });
            return;
        }
        MryTextView mryTextView2 = tvPublishTime;
    }

    /* access modifiers changed from: private */
    public void setAction(View v, int index, int position, Object o) {
        FcChildReplyListDialog.ChildReplyListListener childReplyListListener = this.listener;
        if (childReplyListListener != null) {
            childReplyListListener.onChildReplyListAction(v, index, position, o);
        }
    }
}
