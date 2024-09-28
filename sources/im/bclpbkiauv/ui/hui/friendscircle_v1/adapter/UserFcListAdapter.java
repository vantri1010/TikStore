package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.FcMediaBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.TopicBean;
import com.bjz.comm.net.utils.HttpUtils;
import com.preview.PhotoPreview;
import com.preview.interfaces.ImageLoader;
import com.preview.interfaces.OnLongClickListener;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcPhotosAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.decoration.GridSpaceItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcClickSpanListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.FcVideoPlayerView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageDetailActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.ExpandableTextView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.FlowLayout;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagFlowLayout;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserFcListAdapter extends BaseFcAdapter<RespFcListBean> {
    private static final int ITEM_TYPE_BOTTOM;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_TEXT;
    private static final int ITEM_TYPE_TEXT_PHOTOS;
    private static final int ITEM_TYPE_TEXT_VIDEO;
    public static final int Index_click_avatar = 0;
    public static final int Index_click_follow;
    public static final int Index_click_like;
    public static final int Index_click_location;
    /* access modifiers changed from: private */
    public static final int Index_click_more_operate;
    public static final int Index_click_pop_cancel_follow;
    public static final int Index_click_pop_delete;
    public static final int Index_click_pop_private;
    public static final int Index_click_pop_public;
    public static final int Index_click_pop_report;
    public static final int Index_click_pop_shield_item;
    public static final int Index_click_pop_shield_user;
    public static final int Index_click_reply;
    public static final int Index_download_photo;
    public static final int Index_download_video;
    private static int index;
    private static int itemType;
    private String TAG = UserFcListAdapter.class.getSimpleName();
    private final TLRPC.User currentUser;
    private int currentUserId;
    private boolean isShowReply = true;
    /* access modifiers changed from: private */
    public FcItemActionClickListener listener;
    /* access modifiers changed from: private */
    public Activity mContext;
    private int mFooterCount = 0;
    private final int mGuid;
    private int mHeaderCount = 0;
    /* access modifiers changed from: private */
    public RespFcListBean operateModel;
    /* access modifiers changed from: private */
    public int operatePosition;
    private FcCommMenuDialog ownFcOperateDialog;
    private PhotoPreview photoPreview;
    private final int screenWidth;

    static {
        index = 0;
        int i = 0 + 1;
        index = i;
        int i2 = i + 1;
        index = i2;
        Index_click_follow = i;
        int i3 = i2 + 1;
        index = i3;
        Index_click_more_operate = i2;
        int i4 = i3 + 1;
        index = i4;
        Index_download_photo = i3;
        int i5 = i4 + 1;
        index = i5;
        Index_download_video = i4;
        int i6 = i5 + 1;
        index = i6;
        Index_click_like = i5;
        int i7 = i6 + 1;
        index = i7;
        Index_click_reply = i6;
        int i8 = i7 + 1;
        index = i8;
        Index_click_location = i7;
        int i9 = i8 + 1;
        index = i9;
        Index_click_pop_public = i8;
        int i10 = i9 + 1;
        index = i10;
        Index_click_pop_private = i9;
        int i11 = i10 + 1;
        index = i11;
        Index_click_pop_delete = i10;
        int i12 = i11 + 1;
        index = i12;
        Index_click_pop_cancel_follow = i11;
        int i13 = i12 + 1;
        index = i13;
        Index_click_pop_shield_item = i12;
        int i14 = i13 + 1;
        index = i14;
        Index_click_pop_shield_user = i13;
        index = i14 + 1;
        Index_click_pop_report = i14;
        itemType = 0;
        int i15 = 0 + 1;
        itemType = i15;
        int i16 = i15 + 1;
        itemType = i16;
        ITEM_TYPE_BOTTOM = i15;
        int i17 = i16 + 1;
        itemType = i17;
        ITEM_TYPE_TEXT = i16;
        int i18 = i17 + 1;
        itemType = i18;
        ITEM_TYPE_TEXT_PHOTOS = i17;
        itemType = i18 + 1;
        ITEM_TYPE_TEXT_VIDEO = i18;
    }

    public UserFcListAdapter(Collection<RespFcListBean> collection, final Activity mContext2, int guid, FcItemActionClickListener listener2) {
        super(collection, R.layout.item_fc_text);
        this.mContext = mContext2;
        this.mGuid = guid;
        this.listener = listener2;
        PhotoPreview photoPreview2 = new PhotoPreview((FragmentActivity) mContext2, false, new ImageLoader() {
            public void onLoadImage(int position, Object object, ImageView imageView) {
                KLog.d("-------大图-" + HttpUtils.getInstance().getDownloadFileUrl() + object);
                GlideUtils instance = GlideUtils.getInstance();
                instance.loadNOCentercrop(HttpUtils.getInstance().getDownloadFileUrl() + object, mContext2, imageView, 0);
            }
        });
        this.photoPreview = photoPreview2;
        photoPreview2.setIndicatorType(0);
        this.photoPreview.setLongClickListener(new OnLongClickListener() {
            public void onLongClick(FrameLayout rootView, Object path, int position) {
                UserFcListAdapter.this.setAction(rootView, UserFcListAdapter.Index_download_photo, position, path);
            }
        });
        this.screenWidth = Util.getScreenWidth(mContext2);
        TLRPC.User currentUser2 = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser();
        this.currentUser = currentUser2;
        if (currentUser2 != null) {
            this.currentUserId = currentUser2.id;
        }
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
        return ((RespFcListBean) this.mList.get((this.mList.size() - 1) - getFooterSize())).getForumID();
    }

    public int getFooterSize() {
        RespFcListBean respFcListBean;
        if (this.mFooterCount <= 0 || getDataList().size() <= 1 || (respFcListBean = (RespFcListBean) getDataList().get(getItemCount() - 1)) == null || respFcListBean.getForumID() != 0) {
            return 0;
        }
        return 1;
    }

    public int getItemViewType(int position) {
        int i = this.mHeaderCount;
        if (i != 0 && position < i) {
            return ITEM_TYPE_HEADER;
        }
        if (this.mFooterCount != 0 && position == getItemCount() - 1 && ((RespFcListBean) this.mList.get(position)).getForumID() == 0) {
            return ITEM_TYPE_BOTTOM;
        }
        if (getDataList() == null || position >= getDataList().size()) {
            return ITEM_TYPE_TEXT;
        }
        RespFcListBean respFcListBean = (RespFcListBean) getDataList().get(position);
        if (respFcListBean == null) {
            return ITEM_TYPE_TEXT;
        }
        ArrayList<FcMediaBean> medias = respFcListBean.getMedias();
        if (medias == null || medias.size() <= 0) {
            return ITEM_TYPE_TEXT;
        }
        FcMediaBean media = medias.get(0);
        if (media.getExt() == 1 || media.getExt() == 3) {
            return ITEM_TYPE_TEXT_PHOTOS;
        }
        if (media.getExt() == 2) {
            return ITEM_TYPE_TEXT_VIDEO;
        }
        return ITEM_TYPE_TEXT;
    }

    public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_fc_home_header, (ViewGroup) null), this.mListener);
        }
        if (viewType == ITEM_TYPE_BOTTOM) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_fc_footer, parent, false), this.mListener);
        }
        if (viewType == ITEM_TYPE_TEXT_PHOTOS) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_text_photos, parent, false), this.mListener);
        }
        if (viewType == ITEM_TYPE_TEXT_VIDEO) {
            return new FcVideoViewHold(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_text_video, parent, false), this.mListener);
        }
        return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_text, parent, false), this.mListener);
    }

    /* access modifiers changed from: protected */
    public void onBindViewHolder(SmartViewHolder viewHolder, RespFcListBean model, int position) {
        int i;
        MryTextView tvFcDetailLocation;
        SmartViewHolder smartViewHolder = viewHolder;
        final RespFcListBean respFcListBean = model;
        final int i2 = position;
        if (getItemViewType(i2) == ITEM_TYPE_HEADER) {
            smartViewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else if (getItemViewType(i2) == ITEM_TYPE_BOTTOM) {
            smartViewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        } else {
            smartViewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            View itemView = smartViewHolder.itemView;
            BackupImageView ivUserAvatar = (BackupImageView) itemView.findViewById(R.id.iv_user_avatar);
            MryTextView tvUserName = (MryTextView) itemView.findViewById(R.id.tv_user_name);
            MryTextView tvPublishTime = (MryTextView) itemView.findViewById(R.id.tv_publish_time);
            MryTextView btnFollow = (MryTextView) itemView.findViewById(R.id.btn_follow);
            ImageView ivMoreOperate = (ImageView) itemView.findViewById(R.id.iv_more_operate);
            MryTextView mryTextView = (MryTextView) itemView.findViewById(R.id.tv_gender);
            TagFlowLayout viewTopics = (TagFlowLayout) itemView.findViewById(R.id.view_topics);
            MryTextView btnReply = (MryTextView) itemView.findViewById(R.id.btn_reply);
            final MryTextView btnLike = (MryTextView) itemView.findViewById(R.id.btn_like);
            ViewStub viewStubLocation = (ViewStub) itemView.findViewById(R.id.viewStub_location);
            ViewStub viewStubReply = (ViewStub) itemView.findViewById(R.id.viewStub_reply);
            ivUserAvatar.setVisibility(8);
            tvUserName.setVisibility(8);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvPublishTime.getLayoutParams();
            BackupImageView backupImageView = ivUserAvatar;
            layoutParams.addRule(15);
            tvPublishTime.setLayoutParams(layoutParams);
            tvPublishTime.setText(TimeUtils.fcFormat2Date(model.getCreateAt()));
            tvPublishTime.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
            MryTextView mryTextView2 = tvUserName;
            RelativeLayout.LayoutParams layoutParams2 = layoutParams;
            if (model.getCreateBy() == ((long) UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())) {
                ivMoreOperate.setVisibility(0);
                ivMoreOperate.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        UserFcListAdapter.this.setAction(v, UserFcListAdapter.Index_click_more_operate, i2, (Object) null);
                        if (respFcListBean.getCreateBy() == ((long) UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())) {
                            UserFcListAdapter.this.popForOwnFc(respFcListBean, i2);
                        }
                    }
                });
                i = 8;
            } else {
                i = 8;
                ivMoreOperate.setVisibility(8);
            }
            btnFollow.setVisibility(i);
            setTextView(itemView, respFcListBean);
            if (model.getMedias() != null && model.getMedias().size() > 0) {
                FcMediaBean fcMediaBean = model.getMedias().get(0);
                if (getItemViewType(i2) == ITEM_TYPE_TEXT_PHOTOS) {
                    setPhotosView(itemView, model.getMedias());
                } else if (getItemViewType(i2) == ITEM_TYPE_TEXT_VIDEO) {
                    setVideoView(itemView, fcMediaBean, i2);
                }
            }
            setTopicsInfo(viewTopics, model.getTopic());
            btnLike.setSelected(model.isHasThumb());
            String str = "0";
            btnLike.setText(model.getThumbUp() > 0 ? String.valueOf(model.getThumbUp()) : str);
            btnLike.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    btnLike.setClickable(false);
                    UserFcListAdapter.this.setAction(v, UserFcListAdapter.Index_click_like, i2, respFcListBean);
                }
            });
            if (model.getCommentCount() > 0) {
                str = String.valueOf(model.getCommentCount());
            }
            btnReply.setText(str);
            btnReply.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    UserFcListAdapter.this.onPresentDetailPage(respFcListBean);
                }
            });
            String name = model.getLocationName();
            String city = model.getLocationCity();
            String str2 = "";
            if (!TextUtils.isEmpty(name)) {
                if (TextUtils.isEmpty(city) || TextUtils.equals(name, city)) {
                    MryTextView mryTextView3 = btnFollow;
                } else {
                    MryTextView mryTextView4 = tvPublishTime;
                    StringBuilder sb = new StringBuilder();
                    MryTextView mryTextView5 = btnFollow;
                    sb.append(city.replace("市", str2));
                    sb.append("·");
                    sb.append(name);
                    name = sb.toString();
                }
                if (viewStubLocation == null || viewStubLocation.getParent() == null) {
                    tvFcDetailLocation = (MryTextView) itemView.findViewById(R.id.tv_fc_detail_location);
                } else {
                    tvFcDetailLocation = (MryTextView) viewStubLocation.inflate().findViewById(R.id.tv_fc_detail_location);
                }
            } else {
                MryTextView mryTextView6 = btnFollow;
                tvFcDetailLocation = null;
            }
            if (tvFcDetailLocation != null) {
                tvFcDetailLocation.setVisibility(TextUtils.isEmpty(name) ? 8 : 0);
                if (!TextUtils.isEmpty(name)) {
                    str2 = name;
                }
                tvFcDetailLocation.setText(str2);
                tvFcDetailLocation.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        UserFcListAdapter.this.setAction(v, UserFcListAdapter.Index_click_location, i2, respFcListBean);
                    }
                });
            }
            if (this.isShowReply) {
                setReplyView(itemView, viewStubReply, respFcListBean, i2);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (UserFcListAdapter.this.listener != null) {
                        UserFcListAdapter.this.listener.onPresentFragment(new FcPageDetailActivity(respFcListBean, false));
                    }
                }
            });
        }
    }

    private void setTextView(View itemView, RespFcListBean model) {
        ExpandableTextView tvContent = (ExpandableTextView) itemView.findViewById(R.id.view_fc_text);
        tvContent.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        if (!TextUtils.isEmpty(model.getContent())) {
            tvContent.bind(model);
            tvContent.setEntitys(model.getEntitys());
            tvContent.setContent(model.getContent());
            tvContent.setLinkClickListener(new FcClickSpanListener(this.mContext, this.mGuid, this.listener));
            tvContent.setVisibility(0);
            return;
        }
        tvContent.setVisibility(8);
    }

    private void setPhotosView(View itemView, ArrayList<FcMediaBean> medias) {
        RecyclerView rlFcDetailPhotos = (RecyclerView) itemView.findViewById(R.id.rv_photos);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlFcDetailPhotos.getLayoutParams();
        if (medias.size() == 1) {
            lp.width = (((int) (((float) this.screenWidth) - Util.dp2px(this.mContext, 40.0f))) / 3) * 2;
            rlFcDetailPhotos.setLayoutManager(new LinearLayoutManager(this.mContext));
        } else if (medias.size() == 2 || medias.size() == 4) {
            rlFcDetailPhotos.setLayoutManager(new GridLayoutManager(this.mContext, 2));
            lp.width = (((int) (((float) this.screenWidth) - Util.dp2px(this.mContext, 40.0f))) / 3) * 2;
            if (rlFcDetailPhotos.getItemDecorationCount() == 0) {
                rlFcDetailPhotos.addItemDecoration(new GridSpaceItemDecoration(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), false));
            }
        } else {
            rlFcDetailPhotos.setLayoutManager(new GridLayoutManager(this.mContext, 3));
            lp.width = (int) (((float) this.screenWidth) - Util.dp2px(this.mContext, 40.0f));
            if (rlFcDetailPhotos.getItemDecorationCount() == 0) {
                rlFcDetailPhotos.addItemDecoration(new GridSpaceItemDecoration(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), false));
            }
        }
        lp.height = -2;
        rlFcDetailPhotos.setLayoutParams(lp);
        rlFcDetailPhotos.setNestedScrollingEnabled(false);
        rlFcDetailPhotos.setAdapter(new FcPhotosAdapter(medias, this.mContext, R.layout.item_friends_circle_img, this.screenWidth, new FcPhotosAdapter.OnPicClickListener(rlFcDetailPhotos) {
            private final /* synthetic */ RecyclerView f$1;

            {
                this.f$1 = r2;
            }

            public final void onPicClick(View view, List list, int i) {
                UserFcListAdapter.this.lambda$setPhotosView$0$UserFcListAdapter(this.f$1, view, list, i);
            }
        }, true));
    }

    public /* synthetic */ void lambda$setPhotosView$0$UserFcListAdapter(RecyclerView rlFcDetailPhotos, View view, List dualist, int position1) {
        PhotoPreview photoPreview2 = this.photoPreview;
        if (photoPreview2 != null) {
            photoPreview2.show((View) rlFcDetailPhotos, position1, (List<?>) dualist);
        }
    }

    private void setVideoView(View itemView, final FcMediaBean fcMediaBean, final int position) {
        final FcVideoPlayerView rlFcDetailVideo = (FcVideoPlayerView) itemView.findViewById(R.id.view_video);
        String strThumb = HttpUtils.getInstance().getDownloadFileUrl() + fcMediaBean.getThum();
        rlFcDetailVideo.bind(HttpUtils.getInstance().getDownloadVideoFileUrl() + fcMediaBean.getName(), "");
        rlFcDetailVideo.getThumbImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        float Ratio = (float) (((double) fcMediaBean.getWidth()) / fcMediaBean.getHeight());
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rlFcDetailVideo.getLayoutParams();
        if (Ratio > 1.0f) {
            params.width = AndroidUtilities.dp(240.0f);
            params.height = AndroidUtilities.dp(140.0f);
        } else {
            params.width = AndroidUtilities.dp(240.0f);
            params.height = AndroidUtilities.dp(320.0f);
        }
        rlFcDetailVideo.setLayoutParams(params);
        rlFcDetailVideo.setRatio(Ratio);
        GlideUtils.getInstance().load(strThumb, (Context) this.mContext, rlFcDetailVideo.getThumbImageView(), (int) R.drawable.shape_fc_default_pic_bg);
        itemView.setTag(HttpUtils.getInstance().getDownloadFileUrl() + fcMediaBean.getName());
        rlFcDetailVideo.setListener(new FcVideoPlayerView.OnClickVideoContainerListener() {
            public void onLongClick() {
                UserFcListAdapter.this.setAction(rlFcDetailVideo, UserFcListAdapter.Index_download_video, position, fcMediaBean.getName());
            }
        });
    }

    private void setTopicsInfo(TagFlowLayout viewTopics, ArrayList<TopicBean> topic) {
        viewTopics.removeAllViews();
        if (topic == null || topic.size() <= 0) {
            viewTopics.setVisibility(8);
            return;
        }
        viewTopics.setVisibility(0);
        viewTopics.setAdapter(new TagAdapter<TopicBean>(topic) {
            public View getView(FlowLayout parent, int position, TopicBean topicBean) {
                MryTextView tv = (MryTextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_child_view_topics, (ViewGroup) null);
                if (!TextUtils.isEmpty(topicBean.getTopicName())) {
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(topicBean.getTopicName());
                    stringBuilder.insert(0, "# ");
                    stringBuilder.setSpan(new ForegroundColorSpan(UserFcListAdapter.this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), 0, 1, 18);
                    tv.setText(stringBuilder);
                }
                return tv;
            }
        });
    }

    /* JADX WARNING: type inference failed for: r6v2, types: [android.view.View] */
    /* JADX WARNING: type inference failed for: r5v9, types: [android.view.View] */
    /* JADX WARNING: type inference failed for: r6v3, types: [android.view.View] */
    /* JADX WARNING: type inference failed for: r5v10, types: [android.view.View] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setReplyView(android.view.View r19, android.view.ViewStub r20, com.bjz.comm.net.bean.RespFcListBean r21, int r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            java.util.ArrayList r3 = r21.getComments()
            if (r3 == 0) goto L_0x00ab
            r3 = 0
            r4 = 0
            r5 = 2131297888(0x7f090660, float:1.8213734E38)
            r6 = 2131297239(0x7f0903d7, float:1.8212417E38)
            if (r2 == 0) goto L_0x0035
            android.view.ViewParent r7 = r20.getParent()
            if (r7 == 0) goto L_0x0035
            r7 = 2131493130(0x7f0c010a, float:1.8609731E38)
            r2.setLayoutResource(r7)
            android.view.View r7 = r20.inflate()
            android.view.View r6 = r7.findViewById(r6)
            r3 = r6
            androidx.recyclerview.widget.RecyclerView r3 = (androidx.recyclerview.widget.RecyclerView) r3
            android.view.View r5 = r7.findViewById(r5)
            r4 = r5
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView r4 = (im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView) r4
            goto L_0x0043
        L_0x0035:
            android.view.View r6 = r1.findViewById(r6)
            r3 = r6
            androidx.recyclerview.widget.RecyclerView r3 = (androidx.recyclerview.widget.RecyclerView) r3
            android.view.View r5 = r1.findViewById(r5)
            r4 = r5
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView r4 = (im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView) r4
        L_0x0043:
            if (r3 == 0) goto L_0x00a8
            if (r4 == 0) goto L_0x00a8
            r5 = 0
            r3.setNestedScrollingEnabled(r5)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter$10 r6 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter$10
            android.app.Activity r7 = r0.mContext
            r6.<init>(r7)
            r7 = 1
            r6.setOrientation(r7)
            r3.setLayoutManager(r6)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeItemReplyAdapter r7 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeItemReplyAdapter
            android.app.Activity r9 = r0.mContext
            java.util.ArrayList r10 = r21.getComments()
            r11 = 2131493076(0x7f0c00d4, float:1.8609622E38)
            r12 = 1
            r15 = 0
            int r14 = r0.mGuid
            im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener r13 = r0.listener
            r8 = r7
            r17 = r13
            r13 = r22
            r16 = r14
            r14 = r21
            r8.<init>(r9, r10, r11, r12, r13, r14, r15, r16, r17)
            r3.setAdapter(r7)
            java.util.ArrayList r8 = r21.getComments()
            int r8 = r8.size()
            if (r8 != 0) goto L_0x0085
            r5 = 8
        L_0x0085:
            r3.setVisibility(r5)
            java.lang.String r5 = "评论一下…"
            r4.setText(r5)
            android.app.Activity r5 = r0.mContext
            android.content.res.Resources r5 = r5.getResources()
            r8 = 2131099737(0x7f060059, float:1.7811836E38)
            int r5 = r5.getColor(r8)
            r4.setTextColor(r5)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter$11 r5 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter$11
            r8 = r21
            r5.<init>(r8)
            r4.setOnClickListener(r5)
            goto L_0x00ad
        L_0x00a8:
            r8 = r21
            goto L_0x00ad
        L_0x00ab:
            r8 = r21
        L_0x00ad:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.UserFcListAdapter.setReplyView(android.view.View, android.view.ViewStub, com.bjz.comm.net.bean.RespFcListBean, int):void");
    }

    /* access modifiers changed from: protected */
    public void popForOwnFc(RespFcListBean model, int position) {
        this.operateModel = model;
        this.operatePosition = position;
        if (this.ownFcOperateDialog == null) {
            List<String> titles = new ArrayList<>();
            titles.add(LocaleController.getString(R.string.firendscircle_delete_dynamic));
            List<Integer> icons = new ArrayList<>();
            icons.add(Integer.valueOf(R.drawable.my_fc_pop_delete));
            this.ownFcOperateDialog = new FcCommMenuDialog(this.mContext, titles, icons, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), (FcCommMenuDialog.RecyclerviewItemClickCallBack) new FcCommMenuDialog.RecyclerviewItemClickCallBack() {
                public void onRecyclerviewItemClick(int index) {
                    if (index == 0) {
                        UserFcListAdapter.this.setAction((View) null, UserFcListAdapter.Index_click_pop_delete, UserFcListAdapter.this.operatePosition, UserFcListAdapter.this.operateModel);
                    }
                }
            }, 1);
        }
        if (this.ownFcOperateDialog.isShowing()) {
            this.ownFcOperateDialog.dismiss();
        } else {
            this.ownFcOperateDialog.show();
        }
    }

    /* access modifiers changed from: private */
    public void setAction(View v, int index2, int position, Object o) {
        FcItemActionClickListener fcItemActionClickListener = this.listener;
        if (fcItemActionClickListener != null) {
            fcItemActionClickListener.onAction(v, index2, position, o);
        }
    }

    /* access modifiers changed from: private */
    public void onPresentDetailPage(RespFcListBean model) {
        FcItemActionClickListener fcItemActionClickListener;
        if (model != null && (fcItemActionClickListener = this.listener) != null) {
            fcItemActionClickListener.onPresentFragment(new FcPageDetailActivity(model, true));
        }
    }

    public void setHeaderCount(int mHeaderCount2) {
        this.mHeaderCount = mHeaderCount2;
    }

    public int getHeaderCount() {
        return this.mHeaderCount;
    }

    public int getHeaderFooterCount() {
        return this.mHeaderCount + this.mFooterCount;
    }

    public void setFooterCount(int mFooterCount2) {
        this.mFooterCount = mFooterCount2;
    }

    public void removeItemByUserID(long userId) {
        if (this.mList != null && this.mList.size() > 0) {
            Iterator<RespFcListBean> iterator = this.mList.iterator();
            int i = 0;
            int startIndex = -1;
            int count = 0;
            while (iterator.hasNext()) {
                if (iterator.next().getCreateBy() == userId) {
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
