package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcPhotosAdapter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.decoration.GridSpaceItemDecoration;
import im.bclpbkiauv.ui.hui.friendscircle_v1.glide.GlideUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcClickSpanListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.FcVideoPlayerView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageDetailActivity;
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

public class FcHomeAdapter extends BaseFcAdapter<RespFcListBean> {
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
    /* access modifiers changed from: private */
    public String TAG = FcHomeAdapter.class.getSimpleName();
    private final TLRPC.User currentUser;
    private int currentUserId;
    /* access modifiers changed from: private */
    public boolean isShowFollow = true;
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
    private FcCommMenuDialog othersFcOperateDialog;
    private FcCommMenuDialog ownFcOperateDialog;
    /* access modifiers changed from: private */
    public int pageIndex = 0;
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

    public FcHomeAdapter(Collection<RespFcListBean> collection, final Activity mContext2, int guid, int pageIndex2, FcItemActionClickListener listener2) {
        super(collection, R.layout.item_fc_text);
        this.mContext = mContext2;
        this.mGuid = guid;
        this.pageIndex = pageIndex2;
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
                FcHomeAdapter.this.setAction(rootView, FcHomeAdapter.Index_download_photo, position, path);
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

    private int getFooterSize() {
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
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(im.bclpbkiauv.ui.hui.adapter.SmartViewHolder r32, com.bjz.comm.net.bean.RespFcListBean r33, int r34) {
        /*
            r31 = this;
            r0 = r31
            r1 = r32
            r2 = r33
            r3 = r34
            int r4 = r0.getItemViewType(r3)
            int r5 = ITEM_TYPE_HEADER
            r6 = 1084227584(0x40a00000, float:5.0)
            r7 = 2131296851(0x7f090253, float:1.821163E38)
            java.lang.String r8 = "windowBackgroundWhite"
            java.lang.String r9 = "40_40"
            r10 = 1
            r11 = 0
            if (r4 != r5) goto L_0x0057
            android.view.View r4 = r1.itemView
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r4.setBackgroundColor(r5)
            android.view.View r4 = r1.itemView
            android.view.View r4 = r4.findViewById(r7)
            im.bclpbkiauv.ui.components.BackupImageView r4 = (im.bclpbkiauv.ui.components.BackupImageView) r4
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r4.setRoundRadius(r5)
            int r5 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r5 = im.bclpbkiauv.messenger.UserConfig.getInstance(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getCurrentUser()
            if (r5 == 0) goto L_0x004b
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = new im.bclpbkiauv.ui.components.AvatarDrawable
            r6.<init>((im.bclpbkiauv.tgnet.TLRPC.User) r5, (boolean) r10)
            im.bclpbkiauv.messenger.ImageLocation r7 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r5, r11)
            r4.setImage((im.bclpbkiauv.messenger.ImageLocation) r7, (java.lang.String) r9, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r5)
        L_0x004b:
            android.view.View r6 = r1.itemView
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$3 r7 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$3
            r7.<init>()
            r6.setOnClickListener(r7)
            goto L_0x03d7
        L_0x0057:
            int r4 = r0.getItemViewType(r3)
            int r5 = ITEM_TYPE_BOTTOM
            if (r4 != r5) goto L_0x006c
            android.view.View r4 = r1.itemView
            java.lang.String r5 = "windowBackgroundGray"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            r4.setBackgroundColor(r5)
            goto L_0x03d7
        L_0x006c:
            android.view.View r4 = r1.itemView
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r4.setBackgroundColor(r5)
            android.view.View r4 = r1.itemView
            android.view.View r5 = r4.findViewById(r7)
            im.bclpbkiauv.ui.components.BackupImageView r5 = (im.bclpbkiauv.ui.components.BackupImageView) r5
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r5.setRoundRadius(r6)
            r6 = 2131297869(0x7f09064d, float:1.8213695E38)
            android.view.View r6 = r4.findViewById(r6)
            im.bclpbkiauv.ui.hviews.MryTextView r6 = (im.bclpbkiauv.ui.hviews.MryTextView) r6
            r7 = 2131297821(0x7f09061d, float:1.8213598E38)
            android.view.View r7 = r4.findViewById(r7)
            im.bclpbkiauv.ui.hviews.MryTextView r7 = (im.bclpbkiauv.ui.hviews.MryTextView) r7
            r8 = 2131296428(0x7f0900ac, float:1.8210772E38)
            android.view.View r8 = r4.findViewById(r8)
            im.bclpbkiauv.ui.hviews.MryTextView r8 = (im.bclpbkiauv.ui.hviews.MryTextView) r8
            r12 = 2131296819(0x7f090233, float:1.8211565E38)
            android.view.View r12 = r4.findViewById(r12)
            android.widget.ImageView r12 = (android.widget.ImageView) r12
            r13 = 2131297767(0x7f0905e7, float:1.8213488E38)
            android.view.View r13 = r4.findViewById(r13)
            im.bclpbkiauv.ui.hviews.MryTextView r13 = (im.bclpbkiauv.ui.hviews.MryTextView) r13
            r14 = 2131297942(0x7f090696, float:1.8213843E38)
            android.view.View r14 = r4.findViewById(r14)
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagFlowLayout r14 = (im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout.TagFlowLayout) r14
            r15 = 2131296434(0x7f0900b2, float:1.8210785E38)
            android.view.View r15 = r4.findViewById(r15)
            im.bclpbkiauv.ui.hviews.MryTextView r15 = (im.bclpbkiauv.ui.hviews.MryTextView) r15
            r11 = 2131296429(0x7f0900ad, float:1.8210774E38)
            android.view.View r11 = r4.findViewById(r11)
            im.bclpbkiauv.ui.hviews.MryTextView r11 = (im.bclpbkiauv.ui.hviews.MryTextView) r11
            r10 = 2131297933(0x7f09068d, float:1.8213825E38)
            android.view.View r10 = r4.findViewById(r10)
            android.view.ViewStub r10 = (android.view.ViewStub) r10
            r1 = 2131297934(0x7f09068e, float:1.8213827E38)
            android.view.View r1 = r4.findViewById(r1)
            android.view.ViewStub r1 = (android.view.ViewStub) r1
            com.bjz.comm.net.bean.FcUserInfoBean r18 = r33.getCreatorUser()
            r19 = r1
            java.lang.String r1 = ""
            r20 = r10
            if (r18 == 0) goto L_0x021d
            if (r13 == 0) goto L_0x014c
            int r21 = r18.getSex()
            if (r21 == 0) goto L_0x0142
            int r10 = r18.getSex()
            r22 = r15
            r15 = 1
            if (r10 != r15) goto L_0x00fd
            r10 = 1
            goto L_0x00fe
        L_0x00fd:
            r10 = 0
        L_0x00fe:
            r13.setSelected(r10)
            int r10 = r18.getBirthday()
            if (r10 <= 0) goto L_0x0135
            java.util.Date r10 = new java.util.Date
            int r15 = r18.getBirthday()
            r23 = r14
            long r14 = (long) r15
            r24 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 * r24
            r10.<init>(r14)
            int r14 = im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils.getAgeByBirthday(r10)
            if (r14 <= 0) goto L_0x0122
            java.lang.String r15 = java.lang.String.valueOf(r14)
            goto L_0x0123
        L_0x0122:
            r15 = r1
        L_0x0123:
            r13.setText(r15)
            if (r14 <= 0) goto L_0x012f
            r15 = 1073741824(0x40000000, float:2.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            goto L_0x0130
        L_0x012f:
            r15 = 0
        L_0x0130:
            r13.setCompoundDrawablePadding(r15)
            r10 = 0
            goto L_0x013e
        L_0x0135:
            r23 = r14
            r13.setText(r1)
            r10 = 0
            r13.setCompoundDrawablePadding(r10)
        L_0x013e:
            r13.setVisibility(r10)
            goto L_0x0150
        L_0x0142:
            r23 = r14
            r22 = r15
            r10 = 8
            r13.setVisibility(r10)
            goto L_0x0150
        L_0x014c:
            r23 = r14
            r22 = r15
        L_0x0150:
            int r10 = r0.currentUserId
            if (r10 == 0) goto L_0x0192
            int r10 = r18.getUserId()
            int r15 = r0.currentUserId
            if (r10 != r15) goto L_0x0192
            im.bclpbkiauv.tgnet.TLRPC$User r10 = r0.currentUser
            if (r10 == 0) goto L_0x0192
            im.bclpbkiauv.ui.components.AvatarDrawable r10 = new im.bclpbkiauv.ui.components.AvatarDrawable
            im.bclpbkiauv.tgnet.TLRPC$User r15 = r0.currentUser
            r14 = 1
            r10.<init>((im.bclpbkiauv.tgnet.TLRPC.User) r15, (boolean) r14)
            im.bclpbkiauv.tgnet.TLRPC$User r14 = r0.currentUser
            r15 = 0
            im.bclpbkiauv.messenger.ImageLocation r14 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r14, r15)
            im.bclpbkiauv.tgnet.TLRPC$User r15 = r0.currentUser
            r5.setImage((im.bclpbkiauv.messenger.ImageLocation) r14, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r15)
            im.bclpbkiauv.tgnet.TLRPC$User r9 = r0.currentUser
            java.lang.String r9 = r9.first_name
            im.bclpbkiauv.tgnet.TLRPC$User r14 = r0.currentUser
            java.lang.String r14 = r14.last_name
            java.lang.String r9 = im.bclpbkiauv.messenger.ContactsController.formatName(r9, r14)
            r14 = 12
            java.lang.String r9 = im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils.handleTextName(r9, r14)
            r6.setText(r9)
            r10 = r7
            r27 = r8
            r25 = r11
            r17 = r12
            goto L_0x022d
        L_0x0192:
            com.bjz.comm.net.bean.AvatarPhotoBean r10 = r18.getPhoto()
            if (r10 == 0) goto L_0x01fe
            int r14 = r10.getSmallPhotoSize()
            int r15 = r10.getSmallLocalId()
            r25 = r11
            r17 = r12
            long r11 = r10.getSmallVolumeId()
            if (r14 == 0) goto L_0x01f6
            r26 = 0
            int r28 = (r11 > r26 ? 1 : (r11 == r26 ? 0 : -1))
            if (r28 == 0) goto L_0x01f6
            long r28 = r10.getAccess_hash()
            int r30 = (r28 > r26 ? 1 : (r28 == r26 ? 0 : -1))
            if (r30 == 0) goto L_0x01f6
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerUser r26 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerUser
            r26.<init>()
            r27 = r26
            r26 = r10
            int r10 = r18.getUserId()
            r28 = r14
            r14 = r27
            r14.user_id = r10
            r10 = r7
            r27 = r8
            long r7 = r18.getAccessHash()
            r14.access_hash = r7
            im.bclpbkiauv.messenger.ImageLocation r7 = new im.bclpbkiauv.messenger.ImageLocation
            r7.<init>()
            r8 = 2
            r7.dc_id = r8
            r7.photoPeer = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            r8.<init>()
            r7.location = r8
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r8 = r7.location
            r8.local_id = r15
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r8 = r7.location
            r8.volume_id = r11
            im.bclpbkiauv.ui.components.AvatarDrawable r8 = new im.bclpbkiauv.ui.components.AvatarDrawable
            r8.<init>()
            r5.setImage((im.bclpbkiauv.messenger.ImageLocation) r7, (java.lang.String) r9, (android.graphics.drawable.Drawable) r8, (java.lang.Object) r14)
            goto L_0x0207
        L_0x01f6:
            r27 = r8
            r26 = r10
            r28 = r14
            r10 = r7
            goto L_0x0207
        L_0x01fe:
            r27 = r8
            r26 = r10
            r25 = r11
            r17 = r12
            r10 = r7
        L_0x0207:
            java.lang.String r7 = r18.getFirstName()
            java.lang.String r8 = r18.getLastName()
            java.lang.String r7 = im.bclpbkiauv.messenger.ContactsController.formatName(r7, r8)
            r8 = 12
            java.lang.String r7 = im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils.handleTextName(r7, r8)
            r6.setText(r7)
            goto L_0x022d
        L_0x021d:
            r10 = r7
            r27 = r8
            r25 = r11
            r17 = r12
            r23 = r14
            r22 = r15
            r7 = 8
            r13.setVisibility(r7)
        L_0x022d:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$4 r7 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$4
            r7.<init>(r3, r2)
            r5.setOnClickListener(r7)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$5 r7 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$5
            r7.<init>(r3, r2)
            r6.setOnClickListener(r7)
            long r7 = r33.getCreateAt()
            java.lang.String r7 = im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils.fcFormat2Date(r7)
            r10.setText(r7)
            java.lang.String r7 = "windowBackgroundWhiteGrayText7"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r10.setTextColor(r7)
            r7 = r17
            r8 = 0
            r7.setVisibility(r8)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$6 r8 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$6
            r8.<init>(r3, r2)
            r7.setOnClickListener(r8)
            boolean r8 = r0.isShowFollow
            if (r8 == 0) goto L_0x029c
            long r8 = r33.getCreateBy()
            int r11 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r11 = im.bclpbkiauv.messenger.UserConfig.getInstance(r11)
            int r11 = r11.getClientUserId()
            long r11 = (long) r11
            int r14 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r14 == 0) goto L_0x029c
            r8 = r27
            r9 = 0
            r8.setVisibility(r9)
            boolean r9 = r33.isHasFollow()
            r8.setSelected(r9)
            boolean r9 = r33.isHasFollow()
            if (r9 == 0) goto L_0x028c
            java.lang.String r9 = "已关注"
            goto L_0x028e
        L_0x028c:
            java.lang.String r9 = "关注"
        L_0x028e:
            r8.setText(r9)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$7 r9 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$7
            r9.<init>(r2, r3)
            r8.setOnClickListener(r9)
            r9 = 8
            goto L_0x02a3
        L_0x029c:
            r8 = r27
            r9 = 8
            r8.setVisibility(r9)
        L_0x02a3:
            r0.setTextView(r4, r2)
            java.util.ArrayList r11 = r33.getMedias()
            if (r11 == 0) goto L_0x02dd
            java.util.ArrayList r11 = r33.getMedias()
            int r11 = r11.size()
            if (r11 <= 0) goto L_0x02dd
            java.util.ArrayList r11 = r33.getMedias()
            r12 = 0
            java.lang.Object r11 = r11.get(r12)
            com.bjz.comm.net.bean.FcMediaBean r11 = (com.bjz.comm.net.bean.FcMediaBean) r11
            int r14 = r0.getItemViewType(r3)
            int r15 = ITEM_TYPE_TEXT_PHOTOS
            if (r14 != r15) goto L_0x02d1
            java.util.ArrayList r14 = r33.getMedias()
            r0.setPhotosView(r4, r14)
            goto L_0x02de
        L_0x02d1:
            int r14 = r0.getItemViewType(r3)
            int r15 = ITEM_TYPE_TEXT_VIDEO
            if (r14 != r15) goto L_0x02de
            r0.setVideoView(r4, r11, r3)
            goto L_0x02de
        L_0x02dd:
            r12 = 0
        L_0x02de:
            java.util.ArrayList r11 = r33.getTopic()
            r14 = r23
            r0.setTopicsInfo(r14, r11)
            boolean r11 = r33.isHasThumb()
            r15 = r25
            r15.setSelected(r11)
            int r11 = r33.getThumbUp()
            java.lang.String r16 = "0"
            if (r11 <= 0) goto L_0x0301
            int r11 = r33.getThumbUp()
            java.lang.String r11 = java.lang.String.valueOf(r11)
            goto L_0x0303
        L_0x0301:
            r11 = r16
        L_0x0303:
            r15.setText(r11)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$8 r11 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$8
            r11.<init>(r15, r3, r2)
            r15.setOnClickListener(r11)
            int r11 = r33.getCommentCount()
            if (r11 <= 0) goto L_0x031c
            int r11 = r33.getCommentCount()
            java.lang.String r16 = java.lang.String.valueOf(r11)
        L_0x031c:
            r11 = r16
            r9 = r22
            r9.setText(r11)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$9 r11 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$9
            r11.<init>(r2)
            r9.setOnClickListener(r11)
            r11 = 0
            r16 = 0
            java.lang.String r12 = r33.getLocationName()
            r22 = r5
            java.lang.String r5 = r33.getLocationCity()
            r23 = r6
            r6 = 2131297179(0x7f09039b, float:1.8212296E38)
            if (r20 == 0) goto L_0x035c
            android.view.ViewParent r25 = r20.getParent()
            if (r25 == 0) goto L_0x035c
            r25 = r7
            android.view.View r7 = r20.inflate()
            android.view.View r6 = r7.findViewById(r6)
            android.widget.RelativeLayout r6 = (android.widget.RelativeLayout) r6
            r11 = 2131297758(0x7f0905de, float:1.821347E38)
            android.view.View r11 = r7.findViewById(r11)
            r7 = r11
            im.bclpbkiauv.ui.hviews.MryTextView r7 = (im.bclpbkiauv.ui.hviews.MryTextView) r7
            goto L_0x036d
        L_0x035c:
            r25 = r7
            android.view.View r6 = r4.findViewById(r6)
            android.widget.RelativeLayout r6 = (android.widget.RelativeLayout) r6
            r7 = 2131297758(0x7f0905de, float:1.821347E38)
            android.view.View r7 = r4.findViewById(r7)
            im.bclpbkiauv.ui.hviews.MryTextView r7 = (im.bclpbkiauv.ui.hviews.MryTextView) r7
        L_0x036d:
            boolean r11 = android.text.TextUtils.isEmpty(r12)
            if (r11 != 0) goto L_0x039f
            boolean r11 = android.text.TextUtils.isEmpty(r5)
            if (r11 != 0) goto L_0x039c
            boolean r11 = android.text.TextUtils.equals(r12, r5)
            if (r11 != 0) goto L_0x039c
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r27 = r8
            java.lang.String r8 = "市"
            java.lang.String r8 = r5.replace(r8, r1)
            r11.append(r8)
            java.lang.String r8 = "·"
            r11.append(r8)
            r11.append(r12)
            java.lang.String r12 = r11.toString()
            goto L_0x03a1
        L_0x039c:
            r27 = r8
            goto L_0x03a1
        L_0x039f:
            r27 = r8
        L_0x03a1:
            if (r7 == 0) goto L_0x03c3
            boolean r8 = android.text.TextUtils.isEmpty(r12)
            if (r8 == 0) goto L_0x03ac
            r11 = 8
            goto L_0x03ad
        L_0x03ac:
            r11 = 0
        L_0x03ad:
            r6.setVisibility(r11)
            boolean r8 = android.text.TextUtils.isEmpty(r12)
            if (r8 == 0) goto L_0x03b7
            goto L_0x03b8
        L_0x03b7:
            r1 = r12
        L_0x03b8:
            r7.setText(r1)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$10 r1 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$10
            r1.<init>(r3, r2)
            r7.setOnClickListener(r1)
        L_0x03c3:
            boolean r1 = r0.isShowReply
            if (r1 == 0) goto L_0x03cd
            r1 = r19
            r0.setReplyView(r4, r1, r2, r3)
            goto L_0x03cf
        L_0x03cd:
            r1 = r19
        L_0x03cf:
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$11 r8 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$11
            r8.<init>(r2)
            r4.setOnClickListener(r8)
        L_0x03d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter.onBindViewHolder(im.bclpbkiauv.ui.hui.adapter.SmartViewHolder, com.bjz.comm.net.bean.RespFcListBean, int):void");
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
        rlFcDetailPhotos.setNestedScrollingEnabled(false);
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
        rlFcDetailPhotos.setAdapter(new FcPhotosAdapter(medias, this.mContext, R.layout.item_friends_circle_img, this.screenWidth, new FcPhotosAdapter.OnPicClickListener(rlFcDetailPhotos) {
            private final /* synthetic */ RecyclerView f$1;

            {
                this.f$1 = r2;
            }

            public final void onPicClick(View view, List list, int i) {
                FcHomeAdapter.this.lambda$setPhotosView$0$FcHomeAdapter(this.f$1, view, list, i);
            }
        }, true));
    }

    public /* synthetic */ void lambda$setPhotosView$0$FcHomeAdapter(RecyclerView rlFcDetailPhotos, View view, List dualist, int position1) {
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
                FcHomeAdapter.this.setAction(rlFcDetailVideo, FcHomeAdapter.Index_download_video, position, fcMediaBean.getName());
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
                    stringBuilder.setSpan(new ForegroundColorSpan(FcHomeAdapter.this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), 0, 1, 18);
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
            if (r3 == 0) goto L_0x00ac
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
            if (r3 == 0) goto L_0x00a9
            if (r4 == 0) goto L_0x00a9
            r5 = 0
            r3.setNestedScrollingEnabled(r5)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$14 r6 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$14
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
            int r15 = r0.pageIndex
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
            if (r8 != 0) goto L_0x0086
            r5 = 8
        L_0x0086:
            r3.setVisibility(r5)
            java.lang.String r5 = "评论一下…"
            r4.setText(r5)
            android.app.Activity r5 = r0.mContext
            android.content.res.Resources r5 = r5.getResources()
            r8 = 2131099737(0x7f060059, float:1.7811836E38)
            int r5 = r5.getColor(r8)
            r4.setTextColor(r5)
            im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$15 r5 = new im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter$15
            r8 = r21
            r5.<init>(r8)
            r4.setOnClickListener(r5)
            goto L_0x00ae
        L_0x00a9:
            r8 = r21
            goto L_0x00ae
        L_0x00ac:
            r8 = r21
        L_0x00ae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.adapter.FcHomeAdapter.setReplyView(android.view.View, android.view.ViewStub, com.bjz.comm.net.bean.RespFcListBean, int):void");
    }

    /* access modifiers changed from: private */
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
                        FcHomeAdapter.this.setAction((View) null, FcHomeAdapter.Index_click_pop_delete, FcHomeAdapter.this.operatePosition, FcHomeAdapter.this.operateModel);
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
    public void popForOthersFc(RespFcListBean model, int position, final boolean isShowCancelFollow) {
        if (this.mContext != null) {
            this.operateModel = model;
            this.operatePosition = position;
            if (this.othersFcOperateDialog == null) {
                List<String> titles = new ArrayList<>();
                if (isShowCancelFollow) {
                    titles.add("取消关注");
                }
                titles.add(LocaleController.getString(R.string.provicy_other_fc_item));
                titles.add(LocaleController.getString(R.string.ReportChat));
                List<Integer> icons = new ArrayList<>();
                if (isShowCancelFollow) {
                    icons.add(Integer.valueOf(R.mipmap.fc_pop_cancel_followed));
                }
                icons.add(Integer.valueOf(R.mipmap.fc_pop_shield));
                icons.add(Integer.valueOf(R.mipmap.fc_pop_report));
                this.othersFcOperateDialog = new FcCommMenuDialog(this.mContext, titles, icons, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), (FcCommMenuDialog.RecyclerviewItemClickCallBack) new FcCommMenuDialog.RecyclerviewItemClickCallBack() {
                    public void onRecyclerviewItemClick(int index) {
                        String access$1100 = FcHomeAdapter.this.TAG;
                        KLog.d(access$1100, "onRecyclerviewItemClick operatePosition" + FcHomeAdapter.this.operatePosition);
                        if (isShowCancelFollow) {
                            if (index == 0) {
                                FcHomeAdapter.this.setAction((View) null, FcHomeAdapter.Index_click_pop_cancel_follow, FcHomeAdapter.this.operatePosition, FcHomeAdapter.this.operateModel);
                            } else if (index == 1) {
                                FcHomeAdapter.this.setAction((View) null, FcHomeAdapter.Index_click_pop_shield_item, FcHomeAdapter.this.operatePosition, FcHomeAdapter.this.operateModel);
                            } else if (index == 2) {
                                FcHomeAdapter.this.setAction((View) null, FcHomeAdapter.Index_click_pop_report, FcHomeAdapter.this.operatePosition, FcHomeAdapter.this.operateModel);
                            }
                        } else if (index == 0) {
                            FcHomeAdapter.this.setAction((View) null, FcHomeAdapter.Index_click_pop_shield_item, FcHomeAdapter.this.operatePosition, FcHomeAdapter.this.operateModel);
                        } else if (index == 1) {
                            FcHomeAdapter.this.setAction((View) null, FcHomeAdapter.Index_click_pop_report, FcHomeAdapter.this.operatePosition, FcHomeAdapter.this.operateModel);
                        }
                    }
                }, 1);
            }
            if (this.othersFcOperateDialog.isShowing()) {
                this.othersFcOperateDialog.dismiss();
            } else {
                this.othersFcOperateDialog.show();
            }
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
            fcItemActionClickListener.onPresentFragment(new FcPageDetailActivity(model, this.pageIndex, true));
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

    public void isShowReplyList(boolean isShowReply2) {
        this.isShowReply = isShowReply2;
    }

    public void isShowFollowBtn(boolean isShowFollow2) {
        this.isShowFollow = isShowFollow2;
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
