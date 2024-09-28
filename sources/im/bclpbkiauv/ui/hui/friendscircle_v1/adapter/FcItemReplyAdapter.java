package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RespFcListBean;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcItemActionClickListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.DataFormatUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.LinkMovementClickMethod;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanCreateListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextBuilder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView;
import java.util.Collection;

public class FcItemReplyAdapter extends BaseFcAdapter<FcReplyBean> {
    private long currentForumID;
    private final int currentUserId = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id;
    /* access modifiers changed from: private */
    public int itemPosition;
    /* access modifiers changed from: private */
    public final FcItemActionClickListener listener;
    private Context mContext;
    /* access modifiers changed from: private */
    public RespFcListBean model;
    private final SpanCreateListener spanCreateListener;

    public FcItemReplyAdapter(Context context, Collection<FcReplyBean> collection, int layoutId, boolean flag, long currentForumID2, int itemPosition2, RespFcListBean model2, FcItemActionClickListener listener2, SpanCreateListener spanCreateListener2) {
        super(collection, layoutId);
        this.mContext = context;
        this.flag = flag;
        this.currentForumID = currentForumID2;
        this.itemPosition = itemPosition2;
        this.model = model2;
        this.listener = listener2;
        this.spanCreateListener = spanCreateListener2;
    }

    /* access modifiers changed from: protected */
    public void onBindViewHolder(SmartViewHolder abrItem, FcReplyBean model2, final int position) {
        Spanned userName;
        abrItem.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        RichTextView txt_comment = (RichTextView) abrItem.itemView.findViewById(R.id.txt_comment);
        if (model2 != null) {
            final String userNameByid = DataFormatUtils.getUserNameByid(model2.getCreateBy());
            if (model2.getReplayID() == this.currentForumID) {
                userName = Html.fromHtml(this.mContext.getResources().getString(R.string.fc_detail_child_comment2, new Object[]{userNameByid}));
            } else {
                userName = Html.fromHtml(this.mContext.getResources().getString(R.string.fc_detail_child_comment3, new Object[]{userNameByid, DataFormatUtils.getUserNameByid(model2.getReplayUID())}));
            }
            new RichTextBuilder(this.mContext).setContent(model2.getContent() == null ? "" : model2.getContent()).setLinkColor(ContextCompat.getColor(this.mContext, R.color.color_5080B5)).setTextView(txt_comment).setListUser(model2.getEntitys()).setNeedUrl(true).setSpanCreateListener(this.spanCreateListener).build();
            CharSequence content = txt_comment.getText();
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(content)) {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
                stringBuilder.insert(0, userName, 0, userName.length());
                txt_comment.setText(stringBuilder);
            }
            txt_comment.setMovementMethod(LinkMovementClickMethod.getInstance());
            txt_comment.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    FcItemReplyAdapter.this.listener.onReplyClick(v, TextUtils.equals(userNameByid, "") ? "" : userNameByid, FcItemReplyAdapter.this.model, FcItemReplyAdapter.this.itemPosition, position, true);
                    return true;
                }
            });
            int i = this.currentUserId;
            if (i != 0 && ((long) i) != model2.getForumUser()) {
                txt_comment.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (FcItemReplyAdapter.this.listener != null) {
                            FcItemReplyAdapter.this.listener.onReplyClick(v, TextUtils.equals(userNameByid, "") ? "" : userNameByid, FcItemReplyAdapter.this.model, FcItemReplyAdapter.this.itemPosition, position, false);
                        }
                    }
                });
            }
        }
    }
}
