package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.bean.AvatarPhotoBean;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcMediaBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.TopicBean;
import com.bjz.comm.net.expandViewModel.StatusType;
import com.bjz.comm.net.utils.HttpUtils;
import com.preview.PhotoPreview;
import com.preview.interfaces.ImageLoader;
import com.preview.interfaces.OnLongClickListener;
import com.socks.library.KLog;
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
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcPhotosAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.decoration.GridSpaceItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCLinkSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcClickSpanListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.FcVideoPlayerView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanCreateListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.LinkSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.ExpandableTextView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.FlowLayout;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagFlowLayout;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextBuilder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class FcDetailAdapter extends BaseFcAdapter<FcReplyBean> {
    private static final int ITEM_TYPE_BOTTOM;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_REPLY;
    public static final int Index_child_reply_click_avatar;
    public static final int Index_child_reply_click_like;
    public static final int Index_click_avatar = 0;
    public static final int Index_click_comment;
    public static final int Index_click_comment_like;
    public static final int Index_click_forum_like;
    public static final int Index_click_load_more_like;
    public static final int Index_click_location;
    public static final int Index_click_more_reply;
    public static final int Index_download_photo;
    public static final int Index_download_video;
    private static int index;
    private static int itemType;
    private String TAG = FcDetailAdapter.class.getSimpleName();
    /* access modifiers changed from: private */
    public final int currentUserId;
    private FcDetailLikedUserAdapter fcLikedUserAdapter;
    private boolean isShowAtUser = false;
    private GridLayoutManager likeLayoutManager;
    /* access modifiers changed from: private */
    public FcItemActionClickListener listener;
    /* access modifiers changed from: private */
    public Activity mContext;
    /* access modifiers changed from: private */
    public RespFcListBean mFcContentBean;
    /* access modifiers changed from: private */
    public final int mGuid;
    private PhotoPreview photoPreview;
    private RelativeLayout rlLikeUsers;
    private RecyclerView rvLikeUsers;
    private final int screenWidth;
    private SpanCreateListener spanCreateListener = new SpanCreateListener() {
        public ClickAtUserSpan getCustomClickAtUserSpan(Context context, FCEntitysResponse FCEntitysResponse, int color, SpanAtUserCallBack spanClickCallBack) {
            return new FCClickAtUserSpan(FcDetailAdapter.this.mGuid, FCEntitysResponse, color, new SpanAtUserCallBack() {
                public void onPresentFragment(BaseFragment baseFragment) {
                    if (FcDetailAdapter.this.listener != null && baseFragment != null) {
                        FcDetailAdapter.this.listener.onPresentFragment(baseFragment);
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

    static {
        index = 0;
        int i = 0 + 1;
        index = i;
        int i2 = i + 1;
        index = i2;
        Index_download_photo = i;
        int i3 = i2 + 1;
        index = i3;
        Index_download_video = i2;
        int i4 = i3 + 1;
        index = i4;
        Index_click_forum_like = i3;
        int i5 = i4 + 1;
        index = i5;
        Index_click_comment_like = i4;
        int i6 = i5 + 1;
        index = i6;
        Index_click_comment = i5;
        int i7 = i6 + 1;
        index = i7;
        Index_click_more_reply = i6;
        int i8 = i7 + 1;
        index = i8;
        Index_click_location = i7;
        int i9 = i8 + 1;
        index = i9;
        Index_click_load_more_like = i8;
        int i10 = i9 + 1;
        index = i10;
        Index_child_reply_click_avatar = i9;
        index = i10 + 1;
        Index_child_reply_click_like = i10;
        itemType = 0;
        int i11 = 0 + 1;
        itemType = i11;
        int i12 = i11 + 1;
        itemType = i12;
        ITEM_TYPE_BOTTOM = i11;
        itemType = i12 + 1;
        ITEM_TYPE_REPLY = i12;
    }

    public FcDetailAdapter(Collection<FcReplyBean> collection, final Activity mContext2, int guid, FcItemActionClickListener listener2) {
        super(collection, R.layout.item_fc_detail_parent_reply);
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
                FcDetailAdapter.this.setAction(rootView, FcDetailAdapter.Index_download_photo, position, path);
            }
        });
        this.screenWidth = Util.getScreenWidth(mContext2);
        this.currentUserId = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id;
    }

    public void setFcContentData(RespFcListBean mFcContentBean2) {
        this.mFcContentBean = mFcContentBean2;
        if (mFcContentBean2 != null) {
            this.isShowAtUser = mFcContentBean2.isRecommend();
        }
    }

    public RespFcListBean getFcContentBean() {
        return this.mFcContentBean;
    }

    public void setFcLikeBeans(int pageNo, ArrayList<FcLikeBean> fcLikeBeans) {
        if (this.rlLikeUsers != null && this.rvLikeUsers != null) {
            if (fcLikeBeans == null || fcLikeBeans.size() <= 0) {
                this.rlLikeUsers.setVisibility(8);
                return;
            }
            this.rlLikeUsers.setVisibility(0);
            if (this.fcLikedUserAdapter == null) {
                setLikedUserView();
            }
            if (pageNo == 0) {
                this.fcLikedUserAdapter.refresh(fcLikeBeans);
            } else {
                this.fcLikedUserAdapter.loadMore(fcLikeBeans);
            }
        }
    }

    public FcDetailLikedUserAdapter getFcLikedUserAdapter() {
        return this.fcLikedUserAdapter;
    }

    public void doLikeUserChanged(FcLikeBean data, boolean isLike) {
        RelativeLayout relativeLayout = this.rlLikeUsers;
        if (relativeLayout != null && this.rvLikeUsers != null) {
            if (isLike) {
                relativeLayout.setVisibility(0);
                FcDetailLikedUserAdapter fcDetailLikedUserAdapter = this.fcLikedUserAdapter;
                if (fcDetailLikedUserAdapter == null) {
                    setLikedUserView();
                } else {
                    fcDetailLikedUserAdapter.setThumbUp(true);
                }
                if (this.fcLikedUserAdapter.getDataList() == null) {
                    List<FcLikeBean> fcLikeBeans = new ArrayList<>();
                    fcLikeBeans.add(data);
                    this.fcLikedUserAdapter.refresh(fcLikeBeans);
                    return;
                }
                this.fcLikedUserAdapter.getDataList().add(0, data);
                int size = this.fcLikedUserAdapter.getDataList().size();
                if (size >= this.fcLikedUserAdapter.getThumbUp() || !(size % 8 == 0 || size % 8 == 1)) {
                    this.fcLikedUserAdapter.notifyItemInserted(0);
                } else {
                    this.fcLikedUserAdapter.notifyDataSetChanged();
                }
            } else if (this.fcLikedUserAdapter != null && relativeLayout.getVisibility() == 0) {
                List<FcLikeBean> fcLikeBeans2 = this.fcLikedUserAdapter.getDataList();
                if (fcLikeBeans2 != null && fcLikeBeans2.size() > 0 && this.likeLayoutManager != null) {
                    int itemCount = fcLikeBeans2.size();
                    int i1 = 0;
                    while (true) {
                        if (i1 >= itemCount) {
                            break;
                        }
                        View view = this.likeLayoutManager.findViewByPosition(i1);
                        if (view.getTag() == null || ((long) ((Integer) view.getTag()).intValue()) != data.getCreateBy()) {
                            i1++;
                        } else {
                            this.fcLikedUserAdapter.getDataList().remove(i1);
                            this.fcLikedUserAdapter.setThumbUp(false);
                            int size2 = this.fcLikedUserAdapter.getDataList().size();
                            if (size2 >= this.fcLikedUserAdapter.getThumbUp() || !(size2 % 8 == 0 || size2 % 8 == 7)) {
                                this.fcLikedUserAdapter.notifyItemRemoved(i1);
                                FcDetailLikedUserAdapter fcDetailLikedUserAdapter2 = this.fcLikedUserAdapter;
                                fcDetailLikedUserAdapter2.notifyItemRangeChanged(i1, fcDetailLikedUserAdapter2.getItemCount());
                            } else {
                                this.fcLikedUserAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                if (this.fcLikedUserAdapter.getDataList().size() == 0) {
                    this.rlLikeUsers.setVisibility(8);
                }
            }
        }
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public FcReplyBean getEndListId() {
        if (this.mList.size() == 0) {
            return null;
        }
        return (FcReplyBean) this.mList.get((this.mList.size() - 1) - getFooterSize());
    }

    public int getFooterSize() {
        FcReplyBean fcReplyBean;
        if (getDataList().size() <= 1 || (fcReplyBean = (FcReplyBean) getDataList().get(getItemCount() - 1)) == null || fcReplyBean.getCommentID() != 0) {
            return 0;
        }
        return 1;
    }

    public int getItemViewType(int position) {
        if (position == 0 && this.mFcContentBean != null) {
            return ITEM_TYPE_HEADER;
        }
        if (position == getItemCount() - 1 && ((FcReplyBean) this.mList.get(position)).getCommentID() == 0) {
            return ITEM_TYPE_BOTTOM;
        }
        return ITEM_TYPE_REPLY;
    }

    public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new FcVideoViewHold(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_detail_header_content, parent, false), this.mListener);
        }
        if (viewType == ITEM_TYPE_BOTTOM) {
            return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_fc_footer, parent, false), this.mListener);
        }
        return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fc_detail_parent_reply, parent, false), this.mListener);
    }

    /* JADX WARNING: type inference failed for: r8v17, types: [android.view.View] */
    /* JADX WARNING: type inference failed for: r8v18, types: [android.view.View] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(im.bclpbkiauv.ui.hui.adapter.SmartViewHolder r26, com.bjz.comm.net.bean.FcReplyBean r27, int r28) {
        /*
            r25 = this;
            r9 = r25
            r10 = r26
            r11 = r27
            r12 = r28
            android.view.View r0 = r10.itemView
            java.lang.String r13 = "windowBackgroundWhite"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r0.setBackgroundColor(r1)
            int r0 = r9.getItemViewType(r12)
            int r1 = ITEM_TYPE_HEADER
            r2 = 2131297935(0x7f09068f, float:1.8213829E38)
            r3 = 2131296429(0x7f0900ad, float:1.8210774E38)
            r4 = 2131297821(0x7f09061d, float:1.8213598E38)
            r5 = 2131297869(0x7f09064d, float:1.8213695E38)
            r6 = 1084227584(0x40a00000, float:5.0)
            r7 = 2131296851(0x7f090253, float:1.821163E38)
            java.lang.String r15 = "0"
            r8 = 1
            java.lang.String r16 = "windowBackgroundGray"
            if (r0 != r1) goto L_0x024d
            android.view.View r1 = r10.itemView
            android.view.View r0 = r1.findViewById(r7)
            r7 = r0
            im.bclpbkiauv.ui.components.BackupImageView r7 = (im.bclpbkiauv.ui.components.BackupImageView) r7
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r7.setRoundRadius(r0)
            android.view.View r0 = r1.findViewById(r5)
            r18 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r18 = (im.bclpbkiauv.ui.hviews.MryTextView) r18
            android.view.View r0 = r1.findViewById(r4)
            r19 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r19 = (im.bclpbkiauv.ui.hviews.MryTextView) r19
            r0 = 2131297767(0x7f0905e7, float:1.8213488E38)
            android.view.View r0 = r1.findViewById(r0)
            r20 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r20 = (im.bclpbkiauv.ui.hviews.MryTextView) r20
            r0 = 2131297942(0x7f090696, float:1.8213843E38)
            android.view.View r0 = r1.findViewById(r0)
            r6 = r0
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagFlowLayout r6 = (im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagFlowLayout) r6
            r0 = 2131296434(0x7f0900b2, float:1.8210785E38)
            android.view.View r0 = r1.findViewById(r0)
            r5 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r5 = (im.bclpbkiauv.ui.hviews.MryTextView) r5
            android.view.View r0 = r1.findViewById(r3)
            r4 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r4 = (im.bclpbkiauv.ui.hviews.MryTextView) r4
            r0 = 2131297933(0x7f09068d, float:1.8213825E38)
            android.view.View r0 = r1.findViewById(r0)
            r21 = r0
            android.view.ViewStub r21 = (android.view.ViewStub) r21
            r0 = 2131297178(0x7f09039a, float:1.8212294E38)
            android.view.View r0 = r1.findViewById(r0)
            android.widget.RelativeLayout r0 = (android.widget.RelativeLayout) r0
            r9.rlLikeUsers = r0
            android.view.View r0 = r1.findViewById(r2)
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r0.setBackgroundColor(r2)
            r0 = 2131297936(0x7f090690, float:1.821383E38)
            android.view.View r0 = r1.findViewById(r0)
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r0.setBackgroundColor(r2)
            r0 = 2131297241(0x7f0903d9, float:1.8212421E38)
            android.view.View r0 = r1.findViewById(r0)
            androidx.recyclerview.widget.RecyclerView r0 = (androidx.recyclerview.widget.RecyclerView) r0
            r9.rvLikeUsers = r0
            com.bjz.comm.net.bean.RespFcListBean r0 = r9.mFcContentBean
            com.bjz.comm.net.bean.FcUserInfoBean r2 = r0.getCreatorUser()
            com.bjz.comm.net.bean.RespFcListBean r0 = r9.mFcContentBean
            long r22 = r0.getCreateAt()
            r0 = r25
            r3 = r1
            r1 = r2
            r14 = r3
            r2 = r22
            r22 = r15
            r15 = r4
            r4 = r7
            r10 = r5
            r5 = r18
            r11 = r6
            r6 = r20
            r16 = r7
            r7 = r19
            r23 = r10
            r10 = 1
            r8 = r28
            r0.bindUserInfo(r1, r2, r4, r5, r6, r7, r8)
            com.bjz.comm.net.bean.RespFcListBean r0 = r9.mFcContentBean
            r9.setTextView(r14, r0)
            r0 = 2131296632(0x7f090178, float:1.8211186E38)
            android.view.View r0 = r14.findViewById(r0)
            androidx.cardview.widget.CardView r0 = (androidx.cardview.widget.CardView) r0
            r1 = 2131297243(0x7f0903db, float:1.8212425E38)
            android.view.View r1 = r14.findViewById(r1)
            androidx.recyclerview.widget.RecyclerView r1 = (androidx.recyclerview.widget.RecyclerView) r1
            r2 = 2131297943(0x7f090697, float:1.8213845E38)
            android.view.View r2 = r14.findViewById(r2)
            im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.FcVideoPlayerView r2 = (im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.FcVideoPlayerView) r2
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            java.util.ArrayList r3 = r3.getMedias()
            if (r3 == 0) goto L_0x0175
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            java.util.ArrayList r3 = r3.getMedias()
            int r3 = r3.size()
            if (r3 <= 0) goto L_0x0175
            r3 = 0
            r0.setVisibility(r3)
            com.bjz.comm.net.bean.RespFcListBean r4 = r9.mFcContentBean
            java.util.ArrayList r4 = r4.getMedias()
            java.lang.Object r4 = r4.get(r3)
            r3 = r4
            com.bjz.comm.net.bean.FcMediaBean r3 = (com.bjz.comm.net.bean.FcMediaBean) r3
            int r4 = r3.getExt()
            if (r4 == r10) goto L_0x015a
            int r4 = r3.getExt()
            r5 = 3
            if (r4 != r5) goto L_0x012d
            goto L_0x015a
        L_0x012d:
            int r4 = r3.getExt()
            r5 = 2
            if (r4 != r5) goto L_0x0174
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            com.bjz.comm.net.utils.HttpUtils r5 = com.bjz.comm.net.utils.HttpUtils.getInstance()
            java.lang.String r5 = r5.getDownloadFileUrl()
            r4.append(r5)
            java.lang.String r5 = r3.getName()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r14.setTag(r4)
            r9.setVideoView(r2, r3, r12)
            r4 = 0
            r2.setVisibility(r4)
            goto L_0x0174
        L_0x015a:
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r5 = 1090519040(0x41000000, float:8.0)
            im.bclpbkiauv.messenger.utils.ShapeUtils$ShapeDrawable r4 = im.bclpbkiauv.messenger.utils.ShapeUtils.create(r4, r5)
            r0.setBackground(r4)
            com.bjz.comm.net.bean.RespFcListBean r4 = r9.mFcContentBean
            java.util.ArrayList r4 = r4.getMedias()
            r9.setPhotosView(r1, r4)
            r4 = 0
            r1.setVisibility(r4)
        L_0x0174:
            goto L_0x017a
        L_0x0175:
            r3 = 8
            r0.setVisibility(r3)
        L_0x017a:
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            java.util.ArrayList r3 = r3.getTopic()
            r9.setTopicsInfo(r11, r3)
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            boolean r3 = r3.isHasThumb()
            r15.setSelected(r3)
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            int r3 = r3.getThumbUp()
            if (r3 <= 0) goto L_0x019f
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            int r3 = r3.getThumbUp()
            java.lang.String r3 = java.lang.String.valueOf(r3)
            goto L_0x01a1
        L_0x019f:
            r3 = r22
        L_0x01a1:
            r15.setText(r3)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$3 r3 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$3
            r3.<init>(r15, r12)
            r15.setOnClickListener(r3)
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            int r3 = r3.getCommentCount()
            if (r3 <= 0) goto L_0x01bf
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            int r3 = r3.getCommentCount()
            java.lang.String r3 = java.lang.String.valueOf(r3)
            goto L_0x01c1
        L_0x01bf:
            r3 = r22
        L_0x01c1:
            r4 = r23
            r4.setText(r3)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$4 r3 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$4
            r5 = r11
            r11 = r27
            r3.<init>(r12, r11)
            r4.setOnClickListener(r3)
            com.bjz.comm.net.bean.RespFcListBean r3 = r9.mFcContentBean
            java.lang.String r3 = r3.getLocationName()
            com.bjz.comm.net.bean.RespFcListBean r6 = r9.mFcContentBean
            java.lang.String r6 = r6.getLocationCity()
            r7 = 0
            boolean r8 = android.text.TextUtils.isEmpty(r3)
            java.lang.String r10 = ""
            if (r8 != 0) goto L_0x022a
            boolean r8 = android.text.TextUtils.isEmpty(r6)
            if (r8 != 0) goto L_0x020c
            boolean r8 = android.text.TextUtils.equals(r3, r6)
            if (r8 != 0) goto L_0x020c
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r13 = "市"
            java.lang.String r13 = r6.replace(r13, r10)
            r8.append(r13)
            java.lang.String r13 = "·"
            r8.append(r13)
            r8.append(r3)
            java.lang.String r3 = r8.toString()
        L_0x020c:
            r8 = 2131297758(0x7f0905de, float:1.821347E38)
            if (r21 == 0) goto L_0x0223
            android.view.ViewParent r13 = r21.getParent()
            if (r13 == 0) goto L_0x0223
            android.view.View r13 = r21.inflate()
            android.view.View r8 = r13.findViewById(r8)
            r7 = r8
            im.bclpbkiauv.ui.hviews.MryTextView r7 = (im.bclpbkiauv.ui.hviews.MryTextView) r7
            goto L_0x022a
        L_0x0223:
            android.view.View r8 = r14.findViewById(r8)
            r7 = r8
            im.bclpbkiauv.ui.hviews.MryTextView r7 = (im.bclpbkiauv.ui.hviews.MryTextView) r7
        L_0x022a:
            if (r7 == 0) goto L_0x0258
            boolean r8 = android.text.TextUtils.isEmpty(r3)
            if (r8 == 0) goto L_0x0235
            r8 = 8
            goto L_0x0236
        L_0x0235:
            r8 = 0
        L_0x0236:
            r7.setVisibility(r8)
            boolean r8 = android.text.TextUtils.isEmpty(r3)
            if (r8 == 0) goto L_0x0240
            goto L_0x0241
        L_0x0240:
            r10 = r3
        L_0x0241:
            r7.setText(r10)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$5 r8 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$5
            r8.<init>(r12)
            r7.setOnClickListener(r8)
            goto L_0x0258
        L_0x024d:
            r22 = r15
            r10 = 1
            int r0 = r9.getItemViewType(r12)
            int r1 = ITEM_TYPE_BOTTOM
            if (r0 != r1) goto L_0x025c
        L_0x0258:
            r13 = r26
            goto L_0x03f3
        L_0x025c:
            r13 = r26
            android.view.View r14 = r13.itemView
            r0 = 2131297904(0x7f090670, float:1.8213766E38)
            android.view.View r0 = r14.findViewById(r0)
            r15 = r0
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView r15 = (im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView) r15
            r0 = 2131297157(0x7f090385, float:1.821225E38)
            android.view.View r0 = r14.findViewById(r0)
            r8 = r0
            android.widget.LinearLayout r8 = (android.widget.LinearLayout) r8
            im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r0 = im.bclpbkiauv.ui.actionbar.Theme.getCurrentTheme()
            boolean r0 = r0.isLight()
            r18 = 1082130432(0x40800000, float:4.0)
            if (r0 != 0) goto L_0x0290
            int r0 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            float r1 = (float) r1
            im.bclpbkiauv.messenger.utils.ShapeUtils$ShapeDrawable r0 = im.bclpbkiauv.messenger.utils.ShapeUtils.create(r0, r1)
            r8.setBackground(r0)
        L_0x0290:
            android.view.View r0 = r14.findViewById(r7)
            r7 = r0
            im.bclpbkiauv.ui.components.BackupImageView r7 = (im.bclpbkiauv.ui.components.BackupImageView) r7
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r7.setRoundRadius(r0)
            android.view.View r0 = r14.findViewById(r5)
            r19 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r19 = (im.bclpbkiauv.ui.hviews.MryTextView) r19
            android.view.View r0 = r14.findViewById(r4)
            r20 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r20 = (im.bclpbkiauv.ui.hviews.MryTextView) r20
            android.view.View r0 = r14.findViewById(r3)
            r6 = r0
            im.bclpbkiauv.ui.hviews.MryTextView r6 = (im.bclpbkiauv.ui.hviews.MryTextView) r6
            android.view.View r0 = r14.findViewById(r2)
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r0.setBackgroundColor(r1)
            com.bjz.comm.net.bean.FcUserInfoBean r1 = r27.getCreator()
            long r2 = r27.getCreateAt()
            r16 = 0
            r0 = r25
            r4 = r7
            r5 = r19
            r10 = r6
            r6 = r16
            r16 = r7
            r7 = r20
            r23 = r8
            r8 = r28
            r0.bindUserInfo(r1, r2, r4, r5, r6, r7, r8)
            boolean r0 = r27.isHasThumb()
            r10.setSelected(r0)
            int r0 = r27.getThumbUp()
            if (r0 <= 0) goto L_0x02f3
            int r0 = r27.getThumbUp()
            java.lang.String r0 = java.lang.String.valueOf(r0)
            goto L_0x02f5
        L_0x02f3:
            r0 = r22
        L_0x02f5:
            r10.setText(r0)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$6 r0 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$6
            r0.<init>(r10, r12, r11)
            r10.setOnClickListener(r0)
            r3 = 0
            r5 = -1
            r0 = r25
            r1 = r27
            r2 = r15
            r4 = r28
            r6 = r14
            r0.bindReplyView(r1, r2, r3, r4, r5, r6)
            java.util.ArrayList r7 = r27.getSubComment()
            if (r7 == 0) goto L_0x03dd
            int r0 = r7.size()
            if (r0 <= 0) goto L_0x03dd
            r23.removeAllViews()
            r0 = 0
            r8 = r0
        L_0x031e:
            int r0 = r7.size()
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = -2
            r3 = -1
            r4 = 16
            if (r8 >= r0) goto L_0x0381
            r0 = 2
            if (r8 != r0) goto L_0x0330
            r5 = r23
            goto L_0x0383
        L_0x0330:
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView r0 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView
            android.app.Activity r5 = r9.mContext
            r0.<init>(r5)
            r6 = r0
            r6.setGravity(r4)
            android.widget.LinearLayout$LayoutParams r0 = new android.widget.LinearLayout$LayoutParams
            r0.<init>(r3, r2)
            r5 = r0
            if (r8 == 0) goto L_0x0349
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            r5.topMargin = r0
        L_0x0349:
            r6.setLayoutParams(r5)
            android.app.Activity r0 = r9.mContext
            android.content.res.Resources r0 = r0.getResources()
            r2 = 2131099688(0x7f060028, float:1.7811736E38)
            int r0 = r0.getColor(r2)
            r6.setTextColor(r0)
            r6.setTextSize(r1)
            java.lang.Object r0 = r7.get(r8)
            r1 = r0
            com.bjz.comm.net.bean.FcReplyBean r1 = (com.bjz.comm.net.bean.FcReplyBean) r1
            r3 = 1
            r17 = 0
            r0 = r25
            r2 = r6
            r4 = r28
            r22 = r5
            r5 = r8
            r24 = r6
            r6 = r17
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView r0 = r0.bindReplyView(r1, r2, r3, r4, r5, r6)
            r5 = r23
            r5.addView(r0)
            int r8 = r8 + 1
            goto L_0x031e
        L_0x0381:
            r5 = r23
        L_0x0383:
            int r0 = r27.getSubComments()
            r6 = 2
            if (r0 <= r6) goto L_0x03d8
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView r0 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView
            android.app.Activity r6 = r9.mContext
            r0.<init>(r6)
            r0.setGravity(r4)
            android.widget.LinearLayout$LayoutParams r4 = new android.widget.LinearLayout$LayoutParams
            r4.<init>(r3, r2)
            r2 = r4
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            r2.topMargin = r3
            r0.setLayoutParams(r2)
            android.app.Activity r3 = r9.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131099735(0x7f060057, float:1.7811832E38)
            int r3 = r3.getColor(r4)
            r0.setTextColor(r3)
            r0.setTextSize(r1)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            int r3 = r27.getSubComments()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r1[r4] = r3
            java.lang.String r3 = "查看%d条回复>"
            java.lang.String r1 = java.lang.String.format(r3, r1)
            r0.setText(r1)
            r5.addView(r0)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$7 r1 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter$7
            r1.<init>(r12, r11)
            r5.setOnClickListener(r1)
        L_0x03d8:
            r0 = 0
            r5.setVisibility(r0)
            goto L_0x03f3
        L_0x03dd:
            r5 = r23
            int r0 = r5.getChildCount()
            if (r0 <= 0) goto L_0x03f3
            int r0 = r5.getVisibility()
            if (r0 != 0) goto L_0x03f3
            r5.removeAllViews()
            r0 = 8
            r5.setVisibility(r0)
        L_0x03f3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcDetailAdapter.onBindViewHolder(im.bclpbkiauv.ui.hui.adapter.SmartViewHolder, com.bjz.comm.net.bean.FcReplyBean, int):void");
    }

    private void bindUserInfo(FcUserInfoBean fcUserInfoBean, long createAt, BackupImageView ivUserAvatar, MryTextView tvUserName, MryTextView tvGender, MryTextView tvPublishTime, int position) {
        final FcUserInfoBean fcUserInfoBean2 = fcUserInfoBean;
        BackupImageView backupImageView = ivUserAvatar;
        MryTextView mryTextView = tvUserName;
        MryTextView mryTextView2 = tvGender;
        MryTextView mryTextView3 = tvPublishTime;
        final int i = position;
        if (fcUserInfoBean2 != null) {
            if (mryTextView2 != null) {
                if (fcUserInfoBean.getSex() != 0) {
                    boolean z = true;
                    if (fcUserInfoBean.getSex() != 1) {
                        z = false;
                    }
                    mryTextView2.setSelected(z);
                    String str = "";
                    if (fcUserInfoBean.getBirthday() > 0) {
                        int ageByBirthday = TimeUtils.getAgeByBirthday(new Date(((long) fcUserInfoBean.getBirthday()) * 1000));
                        if (ageByBirthday > 0) {
                            str = String.valueOf(ageByBirthday);
                        }
                        mryTextView2.setText(str);
                        mryTextView2.setCompoundDrawablePadding(ageByBirthday > 0 ? AndroidUtilities.dp(2.0f) : 0);
                    } else {
                        mryTextView2.setText(str);
                        mryTextView2.setCompoundDrawablePadding(0);
                    }
                    mryTextView2.setVisibility(0);
                } else {
                    mryTextView2.setVisibility(8);
                }
            }
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
            mryTextView3.setText(TimeUtils.fcFormat2Date(createAt));
            mryTextView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
            backupImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FcDetailAdapter.this.setAction(v, FcDetailAdapter.Index_click_avatar, i, fcUserInfoBean2);
                }
            });
            mryTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FcDetailAdapter.this.setAction(v, FcDetailAdapter.Index_click_avatar, i, fcUserInfoBean2);
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
            model.setStatusType(StatusType.STATUS_CONTRACT);
            tvContent.setVisibility(0);
            return;
        }
        tvContent.setVisibility(8);
    }

    private void setPhotosView(RecyclerView rlFcDetailPhotos, ArrayList<FcMediaBean> medias) {
        if (rlFcDetailPhotos != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rlFcDetailPhotos.getLayoutParams();
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
            rlFcDetailPhotos.setAdapter(new FcPhotosAdapter(medias, this.mContext, R.layout.item_friends_circle_img, this.screenWidth, new FcPhotosAdapter.OnPicClickListener(rlFcDetailPhotos) {
                private final /* synthetic */ RecyclerView f$1;

                {
                    this.f$1 = r2;
                }

                public final void onPicClick(View view, List list, int i) {
                    FcDetailAdapter.this.lambda$setPhotosView$0$FcDetailAdapter(this.f$1, view, list, i);
                }
            }, true));
        }
    }

    public /* synthetic */ void lambda$setPhotosView$0$FcDetailAdapter(RecyclerView rlFcDetailPhotos, View view, List dualist, int position1) {
        PhotoPreview photoPreview2 = this.photoPreview;
        if (photoPreview2 != null) {
            photoPreview2.show((View) rlFcDetailPhotos, position1, (List<?>) dualist);
        }
    }

    private void setVideoView(final FcVideoPlayerView rlFcDetailVideo, final FcMediaBean fcMediaBean, final int position) {
        if (rlFcDetailVideo != null) {
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
            rlFcDetailVideo.setListener(new FcVideoPlayerView.OnClickVideoContainerListener() {
                public void onLongClick() {
                    FcDetailAdapter.this.setAction(rlFcDetailVideo, FcDetailAdapter.Index_download_video, position, fcMediaBean.getName());
                }
            });
        }
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
                    stringBuilder.setSpan(new ForegroundColorSpan(FcDetailAdapter.this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), 0, 1, 18);
                    tv.setText(stringBuilder);
                }
                return tv;
            }
        });
    }

    private void setLikedUserView() {
        if (this.rvLikeUsers != null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mContext, 8);
            this.likeLayoutManager = gridLayoutManager;
            this.rvLikeUsers.setLayoutManager(gridLayoutManager);
            FcDetailLikedUserAdapter fcDetailLikedUserAdapter = new FcDetailLikedUserAdapter(this.mContext, new ArrayList(), R.layout.item_fc_detail_liked_user, true, this.mFcContentBean.getThumbUp(), this.listener);
            this.fcLikedUserAdapter = fcDetailLikedUserAdapter;
            this.rvLikeUsers.setAdapter(fcDetailLikedUserAdapter);
        }
    }

    private RichTextView bindReplyView(FcReplyBean model, RichTextView txt_comment, boolean isChild, int parentPosition, int childPosition, View itemView) {
        RichTextView txt_comment2;
        String commentUserName;
        final int i = parentPosition;
        final int i2 = childPosition;
        View view = itemView;
        if (txt_comment == null) {
            RichTextView txt_comment3 = new RichTextView(this.mContext);
            txt_comment3.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
            txt_comment2 = txt_comment3;
        } else {
            txt_comment2 = txt_comment;
        }
        txt_comment2.setClickable(false);
        txt_comment2.setLongClickable(false);
        txt_comment2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        new RichTextBuilder(this.mContext).setContent(model.getContent() == null ? "" : model.getContent()).setLinkColor(ContextCompat.getColor(this.mContext, R.color.color_FF09A4C9)).setAtColor(ContextCompat.getColor(this.mContext, R.color.color_FF09A4C9)).setTextView(txt_comment2).setListUser(model.getEntitys()).setNeedUrl(true).setSpanCreateListener(this.spanCreateListener).build();
        FcUserInfoBean creator = model.getCreator();
        if (creator != null) {
            String commentUserName2 = StringUtils.handleTextName(ContactsController.formatName(creator.getFirstName(), creator.getLastName()), 12);
            if (isChild) {
                SpannableStringBuilder headerStr = new SpannableStringBuilder();
                if (model.getReplayID() == this.mFcContentBean.getForumID()) {
                    headerStr.append(commentUserName2);
                    headerStr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_FF7A8391)), 0, commentUserName2.length(), 34);
                    headerStr.append(": ");
                } else {
                    headerStr.append(commentUserName2);
                    headerStr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_FF7A8391)), 0, commentUserName2.length(), 34);
                    headerStr.append(" ");
                    headerStr.append(LocaleController.getString("Reply", R.string.Reply));
                    headerStr.append(" ");
                    headerStr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), commentUserName2.length(), headerStr.length(), 33);
                    int StartIndex = headerStr.length();
                    FcUserInfoBean replayUser = model.getReplayUser();
                    headerStr.append(StringUtils.handleTextName(ContactsController.formatName(replayUser.getFirstName(), replayUser.getLastName()), 12));
                    headerStr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_999999)), StartIndex, headerStr.length(), 33);
                    headerStr.append(" : ");
                }
                CharSequence content = txt_comment2.getText();
                if (!TextUtils.isEmpty(headerStr) && !TextUtils.isEmpty(content)) {
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
                    stringBuilder.insert(0, headerStr, 0, headerStr.length());
                    txt_comment2.setText(stringBuilder);
                }
            }
            commentUserName = commentUserName2;
        } else {
            commentUserName = "";
        }
        final String finalCommentUserName = commentUserName;
        if (view != null) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    FcDetailAdapter.this.listener.onReplyClick(v, TextUtils.equals(finalCommentUserName, "") ? "" : finalCommentUserName, FcDetailAdapter.this.mFcContentBean, i, i2, true);
                    return true;
                }
            });
            final FcReplyBean fcReplyBean = model;
            final String str = finalCommentUserName;
            final int i3 = parentPosition;
            final int i4 = childPosition;
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (FcDetailAdapter.this.currentUserId != 0 && ((long) FcDetailAdapter.this.currentUserId) != fcReplyBean.getCreateBy() && FcDetailAdapter.this.listener != null) {
                        FcDetailAdapter.this.listener.onReplyClick(v, TextUtils.equals(str, "") ? "" : str, FcDetailAdapter.this.mFcContentBean, i3, i4, false);
                    }
                }
            });
        }
        txt_comment2.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                FcDetailAdapter.this.listener.onReplyClick(v, TextUtils.equals(finalCommentUserName, "") ? "" : finalCommentUserName, FcDetailAdapter.this.mFcContentBean, i, i2, true);
                return true;
            }
        });
        final FcReplyBean fcReplyBean2 = model;
        final String str2 = finalCommentUserName;
        final int i5 = parentPosition;
        final int i6 = childPosition;
        txt_comment2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (FcDetailAdapter.this.currentUserId != 0 && ((long) FcDetailAdapter.this.currentUserId) != fcReplyBean2.getCreateBy() && FcDetailAdapter.this.listener != null) {
                    FcDetailAdapter.this.listener.onReplyClick(v, TextUtils.equals(str2, "") ? "" : str2, FcDetailAdapter.this.mFcContentBean, i5, i6, false);
                }
            }
        });
        return txt_comment2;
    }

    /* access modifiers changed from: private */
    public void setAction(View v, int index2, int position, Object o) {
        FcItemActionClickListener fcItemActionClickListener = this.listener;
        if (fcItemActionClickListener != null) {
            fcItemActionClickListener.onAction(v, index2, position, o);
        }
    }

    public void setShowAtUser(boolean showAtUser) {
        this.isShowAtUser = showAtUser;
    }
}
