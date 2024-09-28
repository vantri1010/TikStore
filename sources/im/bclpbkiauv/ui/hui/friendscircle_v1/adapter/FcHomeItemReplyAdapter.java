package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcUserInfoBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.TopicBean;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FCLinkSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StringUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanCreateListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.LinkSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextBuilder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView;
import java.util.Collection;

public class FcHomeItemReplyAdapter extends BaseFcAdapter<FcReplyBean> {
    private final int currentUserId;
    private final boolean isThemeLight;
    /* access modifiers changed from: private */
    public int itemPosition;
    /* access modifiers changed from: private */
    public final FcItemActionClickListener listener;
    private Context mContext;
    /* access modifiers changed from: private */
    public final int mGuid;
    /* access modifiers changed from: private */
    public RespFcListBean model;
    private int page = 0;
    private SpanCreateListener spanCreateListener = new SpanCreateListener() {
        public ClickAtUserSpan getCustomClickAtUserSpan(Context context, FCEntitysResponse FCEntitysResponse, int color, SpanAtUserCallBack spanClickCallBack) {
            return new FCClickAtUserSpan(FcHomeItemReplyAdapter.this.mGuid, FCEntitysResponse, color, new SpanAtUserCallBack() {
                public void onPresentFragment(BaseFragment baseFragment) {
                    if (FcHomeItemReplyAdapter.this.listener != null && baseFragment != null) {
                        FcHomeItemReplyAdapter.this.listener.onPresentFragment(baseFragment);
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

    public FcHomeItemReplyAdapter(Context context, Collection<FcReplyBean> collection, int layoutId, boolean flag, int itemPosition2, RespFcListBean model2, int page2, int mGuid2, FcItemActionClickListener listener2) {
        super(collection, layoutId);
        this.mContext = context;
        this.flag = flag;
        this.itemPosition = itemPosition2;
        this.model = model2;
        this.page = page2;
        this.listener = listener2;
        this.mGuid = mGuid2;
        this.currentUserId = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id;
        this.isThemeLight = Theme.getCurrentTheme().isLight();
    }

    /* access modifiers changed from: protected */
    public void onBindViewHolder(SmartViewHolder abrItem, FcReplyBean model2, int position) {
        SmartViewHolder smartViewHolder = abrItem;
        final int i = position;
        smartViewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        RichTextView txt_comment = (RichTextView) smartViewHolder.itemView.findViewById(R.id.txt_comment);
        if (this.isThemeLight) {
            txt_comment.setTextColor(this.mContext.getResources().getColor(R.color.color_FF838383));
        } else {
            txt_comment.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        }
        int commenterColor = this.isThemeLight ? this.mContext.getResources().getColor(R.color.color_FF313131) : Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
        int replyUserColor = this.isThemeLight ? this.mContext.getResources().getColor(R.color.color_FF838383) : Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
        new RichTextBuilder(this.mContext).setContent(model2.getContent() == null ? "" : model2.getContent()).setLinkColor(ContextCompat.getColor(this.mContext, R.color.color_FF09A4C9)).setAtColor(ContextCompat.getColor(this.mContext, R.color.color_FF09A4C9)).setTextView(txt_comment).setListUser(this.page == 3 ? model2.getEntitys() : null).setNeedUrl(true).setSpanCreateListener(this.spanCreateListener).build();
        FcUserInfoBean creator = model2.getCreator();
        if (creator != null) {
            final String commentUserName = StringUtils.handleTextName(ContactsController.formatName(creator.getFirstName(), creator.getLastName()), 12);
            SpannableStringBuilder headerStr = new SpannableStringBuilder();
            if (model2.getReplayID() == model2.getForumID()) {
                headerStr.append(commentUserName);
                headerStr.append(" : ");
                headerStr.setSpan(new ForegroundColorSpan(commenterColor), 0, headerStr.length(), 33);
                int i2 = commenterColor;
            } else {
                headerStr.append(commentUserName);
                headerStr.setSpan(new ForegroundColorSpan(commenterColor), 0, commentUserName.length(), 34);
                headerStr.append(" ");
                headerStr.append(LocaleController.getString("Reply", R.string.Reply));
                headerStr.append(" ");
                headerStr.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.color_FF2ECEFD)), commentUserName.length(), headerStr.length(), 33);
                int StartIndex = headerStr.length();
                FcUserInfoBean replayUser = model2.getReplayUser();
                headerStr.append(StringUtils.handleTextName(ContactsController.formatName(replayUser.getFirstName(), replayUser.getLastName()), 12));
                int i3 = commenterColor;
                headerStr.setSpan(new ForegroundColorSpan(replyUserColor), StartIndex, headerStr.length(), 33);
                headerStr.append(" : ");
            }
            CharSequence content = txt_comment.getText();
            if (!TextUtils.isEmpty(headerStr) && !TextUtils.isEmpty(content)) {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
                stringBuilder.insert(0, headerStr, 0, headerStr.length());
                txt_comment.setText(stringBuilder);
            }
            txt_comment.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    FcHomeItemReplyAdapter.this.listener.onReplyClick(v, commentUserName, FcHomeItemReplyAdapter.this.model, FcHomeItemReplyAdapter.this.itemPosition, i, true);
                    return true;
                }
            });
            int i4 = this.currentUserId;
            if (i4 != 0 && ((long) i4) != model2.getCreateBy()) {
                txt_comment.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (FcHomeItemReplyAdapter.this.listener != null) {
                            FcHomeItemReplyAdapter.this.listener.onReplyClick(v, commentUserName, FcHomeItemReplyAdapter.this.model, FcHomeItemReplyAdapter.this.itemPosition, i, false);
                        }
                    }
                });
                return;
            }
            return;
        }
    }
}
