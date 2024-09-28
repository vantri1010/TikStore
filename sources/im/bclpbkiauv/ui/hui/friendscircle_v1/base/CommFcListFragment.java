package im.bclpbkiauv.ui.hui.friendscircle_v1.base;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.SPConstant;
import com.bjz.comm.net.bean.FcBgBean;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcVersionBean;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcCommItemPresenter;
import com.bjz.comm.net.utils.HttpUtils;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.dialogs.FcDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.FcPageListRefreshListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPublishActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.FcDialogUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class CommFcListFragment extends BaseFcFragment implements BaseFcContract.IFcCommItemView, FcDoReplyDialog.OnFcDoReplyListener {
    private String TAG = getClass().getSimpleName();
    private FcCommMenuDialog dialogDeleteComment;
    private FcDoReplyDialog fcDoReplyDialog;
    private FcPageListRefreshListener fcPageListRefreshListener;
    public FcVersionBean fcVersionBean;
    protected RecyclerView.LayoutManager layoutManager;
    protected FcCommItemPresenter mCommItemPresenter;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mCommItemPresenter = new FcCommItemPresenter(this);
    }

    public void onDestroy() {
        super.onDestroy();
        FcDoReplyDialog fcDoReplyDialog2 = this.fcDoReplyDialog;
        if (fcDoReplyDialog2 != null) {
            fcDoReplyDialog2.onDestroy();
        }
        FcCommItemPresenter fcCommItemPresenter = this.mCommItemPresenter;
        if (fcCommItemPresenter != null) {
            fcCommItemPresenter.unSubscribeTask();
        }
    }

    public void setFcVersionBean(FcVersionBean fcVersionBean2) {
        this.fcVersionBean = fcVersionBean2;
    }

    public FcVersionBean getFcVersionBean() {
        return this.fcVersionBean;
    }

    public void setFcPageListRefreshListener(FcPageListRefreshListener fcPageListRefreshListener2) {
        this.fcPageListRefreshListener = fcPageListRefreshListener2;
    }

    public void startFcPublishActivity() {
        FcPageListRefreshListener fcPageListRefreshListener2 = this.fcPageListRefreshListener;
        if (fcPageListRefreshListener2 != null) {
            fcPageListRefreshListener2.startFcPublishActivity();
        } else {
            presentFragment(new FcPublishActivity());
        }
    }

    public void refreshDotStatus(int pageIndex) {
        FcPageListRefreshListener fcPageListRefreshListener2 = this.fcPageListRefreshListener;
        if (fcPageListRefreshListener2 != null) {
            fcPageListRefreshListener2.onRefreshed(pageIndex);
        }
    }

    /* access modifiers changed from: protected */
    public void showDeleteBottomSheet(final RespFcListBean model, final int itemPosition, final int replyPosition) {
        if (getParentActivity() != null && model.getComments().get(replyPosition).getCreateBy() == ((long) getUserConfig().getCurrentUser().id)) {
            List<String> list = new ArrayList<>();
            list.add(LocaleController.getString("Delete", R.string.Delete));
            FcCommMenuDialog fcCommMenuDialog = new FcCommMenuDialog(getParentActivity(), list, (List<Integer>) null, Color.parseColor("#FFFF4D3B"), (FcCommMenuDialog.RecyclerviewItemClickCallBack) new FcCommMenuDialog.RecyclerviewItemClickCallBack() {
                public void onRecyclerviewItemClick(int position) {
                    if (position == 0) {
                        CommFcListFragment.this.deleteReply(model, itemPosition, replyPosition);
                    }
                }
            }, 1);
            this.dialogDeleteComment = fcCommMenuDialog;
            if (fcCommMenuDialog.isShowing()) {
                this.dialogDeleteComment.dismiss();
            } else {
                this.dialogDeleteComment.show();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void deleteReply(RespFcListBean model, int itemPosition, int replyPosition) {
        FcDialogUtil.chooseIsDeleteCommentDialog(this, new FcDialog.OnConfirmClickListener(model, itemPosition, replyPosition) {
            private final /* synthetic */ RespFcListBean f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(View view) {
                CommFcListFragment.this.lambda$deleteReply$0$CommFcListFragment(this.f$1, this.f$2, this.f$3, view);
            }
        }, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$deleteReply$0$CommFcListFragment(RespFcListBean model, int itemPosition, int replyPosition, View dialog) {
        doDeleteComment(model, itemPosition, replyPosition);
    }

    /* access modifiers changed from: protected */
    public void showReplyFcDialog(String hint, long forumId, long forumUId, boolean isEnableAtUser, boolean isComment, boolean isRecommend, int requiredLevel) {
        if (this.fcDoReplyDialog == null) {
            FcDoReplyDialog fcDoReplyDialog2 = new FcDoReplyDialog(getParentActivity());
            this.fcDoReplyDialog = fcDoReplyDialog2;
            fcDoReplyDialog2.setListener(this);
        }
        this.fcDoReplyDialog.show(hint, forumId, isEnableAtUser, isComment);
    }

    public void onPause() {
        dismissFcDoReplyDialog();
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void dismissFcDoReplyDialog() {
        FcDoReplyDialog fcDoReplyDialog2 = this.fcDoReplyDialog;
        if (fcDoReplyDialog2 != null && fcDoReplyDialog2.isShowing()) {
            this.fcDoReplyDialog.saveUnPostContent();
            this.fcDoReplyDialog.dismiss();
        }
    }

    public void startFragment(BaseFragment baseFragment) {
        if (baseFragment != null) {
            presentFragment(baseFragment);
        }
    }

    /* access modifiers changed from: protected */
    public void setUserAvatarImg(BackupImageView backupImageView, long userId) {
        TLRPC.User mUser = getAccountInstance().getMessagesController().getUser(Integer.valueOf((int) userId));
        StringBuilder sb = new StringBuilder();
        sb.append("---------用户");
        sb.append(mUser == null);
        KLog.d(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("头像不显示 ");
        sb2.append(mUser == null);
        FileLog.d(sb2.toString());
        if (mUser != null) {
            backupImageView.setImage(ImageLocation.getForUser(mUser, false), "50_50", (Drawable) new AvatarDrawable(mUser, true), (Object) mUser);
        }
    }

    /* access modifiers changed from: protected */
    public void loadFcBackground(long id) {
        if (((long) getUserConfig().getCurrentUser().id) == id) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0);
            String backStr = sharedPreferences.getString("fc_bg" + getUserConfig().getCurrentUser().id, "");
            KLog.d("-------getBG    " + backStr + "   ' fc_bg" + getUserConfig().getCurrentUser().id);
            if (TextUtils.isEmpty(backStr)) {
                this.mCommItemPresenter.getFCBackground(id);
            } else {
                setFcBackground(backStr);
            }
        } else {
            this.mCommItemPresenter.getFCBackground(id);
        }
    }

    public void getFcBackgroundSucc(FcBgBean result) {
        if (result == null) {
            return;
        }
        if (TextUtils.isEmpty(result.getHomeBackground())) {
            setFcBackground("");
            return;
        }
        setFcBackground(HttpUtils.getInstance().getDownloadFileUrl() + result.getHomeBackground());
        if (getUserConfig().getCurrentUser().id == result.getUserID()) {
            saveFcBackground(HttpUtils.getInstance().getDownloadFileUrl() + result.getHomeBackground());
        }
    }

    /* access modifiers changed from: protected */
    public void saveFcBackground(String path) {
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0).edit();
        boolean commit = edit.putString("fc_bg" + getUserConfig().getCurrentUser().id, path).commit();
    }

    /* access modifiers changed from: protected */
    public void setFcBackground(String path) {
    }

    /* access modifiers changed from: protected */
    public void doFollow(int position, RespFcListBean mRespFcListBean) {
        this.mCommItemPresenter.doFollow(mRespFcListBean.getCreateBy(), position);
    }

    public void doFollowSucc(long userId, int position, String msg) {
        FcToastUtils.show((CharSequence) LocaleController.getString("friendscircle_attention_user_succ", R.string.friendscircle_attention_user_succ));
        doFollowAfterViewChange(position, true);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcFollowStatusUpdate, this.TAG, Long.valueOf(userId), true);
    }

    public void doFollowFailed(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_attention_user_fail", R.string.friendscircle_attention_user_fail) : msg);
    }

    /* access modifiers changed from: protected */
    public void doFollowAfterViewChange(int position, boolean isFollow) {
    }

    /* access modifiers changed from: protected */
    public void doCancelFollowed(int position, RespFcListBean mRespFcListBean) {
        this.mCommItemPresenter.doCancelFollowed(mRespFcListBean.getCreateBy(), position);
    }

    public void doCancelFollowedSucc(long userId, int position, String msg) {
        FcToastUtils.show((CharSequence) msg);
        doFollowAfterViewChange(position, false);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcFollowStatusUpdate, this.TAG, Long.valueOf(userId), false);
    }

    public void doCancelFollowedFailed(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_attention_user_cancel_fail", R.string.friendscircle_attention_user_cancel_fail) : msg);
    }

    /* access modifiers changed from: protected */
    public void doLike(int position, RespFcListBean model) {
        this.mCommItemPresenter.doLike(model.getForumID(), (long) getUserConfig().getCurrentUser().id, -1, -1, position);
    }

    public void doLikeSucc(FcLikeBean data, long forumID, int position, String msg) {
        FcToastUtils.show((CharSequence) LocaleController.getString("SOKULF1", R.string.SOKULF1));
        doLikeAfterViewChange(position, true, data);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcLikeStatusUpdate, this.TAG, data, true);
    }

    public void doLikeFailed(int position, String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail) : msg);
        doLikeAfterViewChange(position, false, (FcLikeBean) null);
    }

    /* access modifiers changed from: protected */
    public void doCancelLikeFc(int position, RespFcListBean mRespFcListBean) {
        this.mCommItemPresenter.doCancelLike(mRespFcListBean.getForumID(), mRespFcListBean.getCreateBy(), -1, -1, position);
    }

    public void doCancelLikeSucc(FcLikeBean data, long forumID, int position, String msg) {
        FcToastUtils.show((CharSequence) LocaleController.getString("SOKUUF1", R.string.SOKUUF1));
        doLikeAfterViewChange(position, false, data);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcLikeStatusUpdate, this.TAG, data, false);
    }

    public void doCancelLikeFailed(int position, String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_home_request_fail", R.string.friendscircle_home_request_fail) : msg);
        doLikeAfterViewChange(position, false, (FcLikeBean) null);
    }

    /* access modifiers changed from: protected */
    public void doLikeAfterViewChange(int position, boolean isLike, FcLikeBean data) {
    }

    /* access modifiers changed from: protected */
    public void doDeleteItem(int position, RespFcListBean mRespFcListBean) {
        this.mCommItemPresenter.doDeleteItem(mRespFcListBean.getForumID(), position);
    }

    public void doDeleteItemSucc(long forumID, int position, String msg) {
        FcToastUtils.show((CharSequence) LocaleController.getString("fc_delete_item_succ", R.string.fc_delete_item_succ));
        doDeleteItemAfterViewChange(forumID, position);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcIgnoreOrDeleteItem, this.TAG, Long.valueOf(forumID));
    }

    public void doDeleteItemFailed(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("fc_delete_item_failed", R.string.fc_delete_item_failed) : msg);
    }

    /* access modifiers changed from: protected */
    public void doDeleteItemAfterViewChange(long forumId, int position) {
    }

    /* access modifiers changed from: protected */
    public void setFcItemPermission(RespFcListBean mRespFcListBean, int status, int position) {
        this.mCommItemPresenter.doSetItemPermission(mRespFcListBean.getForumID(), status, position);
    }

    public void doSetItemPermissionSucc(long forumId, int permission, int position, String msg) {
        if (permission == 1) {
            FcToastUtils.show((CharSequence) LocaleController.getString("fc_set_fc_content_public_succ", R.string.fc_set_fc_content_public_succ));
        } else if (permission == 2) {
            FcToastUtils.show((CharSequence) LocaleController.getString("fc_set_fc_content_private_succ", R.string.fc_set_fc_content_private_succ));
        }
        doSetItemPermissionAfterViewChange(forumId, permission, position);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcPermissionStatusUpdate, this.TAG, Long.valueOf(forumId), Integer.valueOf(permission));
    }

    public void doSetItemPermissionFailed(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_set_fail", R.string.friendscircle_set_fail) : msg);
    }

    /* access modifiers changed from: protected */
    public void doSetItemPermissionAfterViewChange(long forumId, int permission, int position) {
    }

    /* access modifiers changed from: protected */
    public void doIgnoreItem(int position, RespFcListBean mRespFcListBean) {
        this.mCommItemPresenter.doIgnoreItem(mRespFcListBean.getForumID(), position);
    }

    public void doIgnoreItemSucc(long forumID, int position, String msg) {
        FcToastUtils.show((CharSequence) LocaleController.getString("friendscircle_hide_dynamic_success", R.string.friendscircle_hide_dynamic_success));
        doIgnoreItemAfterViewChange(forumID, position);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcIgnoreOrDeleteItem, this.TAG, Long.valueOf(forumID));
    }

    public void doIgnoreItemFailed(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_hide_dynamic_fail", R.string.friendscircle_hide_dynamic_fail) : msg);
    }

    /* access modifiers changed from: protected */
    public void doIgnoreItemAfterViewChange(long forumId, int position) {
    }

    /* access modifiers changed from: protected */
    public void doAddIgnoreUser(ArrayList<FcIgnoreUserBean> ignores) {
        this.mCommItemPresenter.doAddIgnoreUser(ignores);
    }

    public void doAddIgnoreUserSucc(ArrayList<FcIgnoreUserBean> ignores, String msg) {
        FcToastUtils.show((CharSequence) msg);
        doSetIgnoreUserAfterViewChange(true, ignores);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcIgnoreUser, this.TAG, ignores);
    }

    public void doAddIgnoreUserFailed(String msg) {
        FcToastUtils.show((CharSequence) msg);
    }

    private void doDeleteIgnoreUser(ArrayList<FcIgnoreUserBean> ignores) {
        this.mCommItemPresenter.doDeleteIgnoreUser(ignores);
    }

    public void doDeleteIgnoreUserSucc(ArrayList<FcIgnoreUserBean> ignores, String msg) {
        FcToastUtils.show((CharSequence) msg);
        doSetIgnoreUserAfterViewChange(false, ignores);
    }

    public void doDeleteIgnoreUserFiled(String msg) {
        FcToastUtils.show((CharSequence) msg);
    }

    /* access modifiers changed from: protected */
    public void doSetIgnoreUserAfterViewChange(boolean isIgnore, ArrayList<FcIgnoreUserBean> arrayList) {
    }

    public void doReplyFc(RequestReplyFcBean requestReplyFcBean, int replyParentPosition) {
        this.mCommItemPresenter.doReply(requestReplyFcBean, replyParentPosition);
    }

    public void doReplySucc(FcReplyBean data, int replyParentPosition) {
        if (data != null && data.getReplayID() == data.getForumID() && data.getReplayUID() == data.getForumUser()) {
            FcToastUtils.show((CharSequence) LocaleController.getString(R.string.friendscircle_home_comment_success));
        } else {
            FcToastUtils.show((CharSequence) LocaleController.getString(R.string.friendscircle_home_reply_success));
        }
        doReplySuccAfterViewChange(data, replyParentPosition);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcReplyItem, this.TAG, data);
    }

    public void doReplyFailed(int replyParentPosition, boolean isComment, String code, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            onError(msg);
        } else if (isComment) {
            onError(LocaleController.getString(R.string.friendscircle_home_comment_fail));
        } else {
            onError(LocaleController.getString(R.string.friendscircle_home_reply_fail));
        }
    }

    /* access modifiers changed from: protected */
    public void doReplySuccAfterViewChange(FcReplyBean data, int replyParentPosition) {
    }

    public void doDeleteComment(RespFcListBean model, int itemPosition, int replyPosition) {
        ArrayList<FcReplyBean> comments = model.getComments();
        if (comments == null || comments.size() <= 0) {
            int i = replyPosition;
            return;
        }
        FcReplyBean fcReplyBean = comments.get(replyPosition);
        if (fcReplyBean != null) {
            this.mCommItemPresenter.doDeleteComment(fcReplyBean.getCommentID(), fcReplyBean.getForumID(), fcReplyBean.getForumUser(), itemPosition, replyPosition);
        }
    }

    public void doDeleteReplySucc(long forumId, long commentId, int parentPosition, int childPosition) {
        FcToastUtils.show((CharSequence) LocaleController.getString(R.string.fc_delete_item_succ));
        doDeleteReplySuccAfterViewChange(forumId, commentId, parentPosition, childPosition);
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.fcDeleteReplyItem, this.TAG, Long.valueOf(forumId), Long.valueOf(commentId));
    }

    /* access modifiers changed from: protected */
    public void doDeleteReplySuccAfterViewChange(long forumId, long commentId, int parentPosition, int childPosition) {
    }

    public void onError(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_publish_server_busy", R.string.friendscircle_publish_server_busy) : msg);
    }

    public String getTAG() {
        return this.TAG;
    }
}
