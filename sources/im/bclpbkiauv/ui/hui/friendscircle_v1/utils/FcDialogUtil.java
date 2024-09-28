package im.bclpbkiauv.ui.hui.friendscircle_v1.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.dialogs.FcDialog;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.hui.friendscircle_v1.base.BaseFcFragment;

public class FcDialogUtil {
    public static void chooseIsSetOtherFcItemPrivacyDialog(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("provicy_other_fc_item", R.string.provicy_other_fc_item);
        setDialog(fragment, title, LocaleController.getString("provicy_other_fc_item_sure", R.string.provicy_other_fc_item_sure), LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
    }

    public static void chooseIsDeleteMineItemDialog(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("fc_delete_mine", R.string.fc_delete_mine);
        setDialog(fragment, title, LocaleController.getString("fc_delete_mine_sure", R.string.fc_delete_mine_sure), LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
    }

    public static void showDeleteAlbumItemDialog(Object fragment, int urlType, boolean hasSameGroup, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String deleteTips;
        String content;
        Object obj = fragment;
        String deleteTips2 = LocaleController.getString("Delete", R.string.Delete);
        if (urlType == 2) {
            content = LocaleController.getString("fc_delete_fc_video", R.string.fc_delete_fc_video);
            deleteTips = deleteTips2;
        } else if (hasSameGroup) {
            content = LocaleController.getString("fc_delete_fc_multiple_pictures", R.string.fc_delete_fc_multiple_pictures);
            deleteTips = LocaleController.getString("DeleteAll", R.string.DeleteAll);
        } else {
            content = LocaleController.getString("fc_delete_fc_pictures", R.string.fc_delete_fc_pictures);
            deleteTips = deleteTips2;
        }
        if (obj instanceof BaseFragment) {
            setDialog((BaseFragment) obj, "", content, deleteTips, LocaleController.getString("Cancel", R.string.Cancel), Color.parseColor("#F74C31"), Color.parseColor("#3BBCFF"), OnConfirmClickListener, onDismissListener);
        } else if (obj instanceof BaseFmts) {
            setDialog((BaseFmts) obj, "", content, deleteTips, LocaleController.getString("Cancel", R.string.Cancel), Color.parseColor("#F74C31"), Color.parseColor("#3BBCFF"), OnConfirmClickListener, onDismissListener);
        } else if (obj instanceof BaseFcFragment) {
            setDialog((BaseFcFragment) obj, "", content, LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
        }
    }

    public static void chooseIsDeleteCommentDialog(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("fc_delete_comment", R.string.fc_delete_comment);
        setDialog(fragment, title, LocaleController.getString("fc_delete_comment_sure", R.string.fc_delete_comment_sure), LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
    }

    public static void chooseCancelFollowedDialog(Object fragment, boolean isFemale, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String tips;
        String title = LocaleController.getString("fc_cancel_followed", R.string.fc_cancel_followed);
        if (isFemale) {
            tips = LocaleController.getString("fc_cancel_followed_her_tips", R.string.fc_cancel_followed_her_tips);
        } else {
            tips = LocaleController.getString("fc_cancel_followed_him_tips", R.string.fc_cancel_followed_him_tips);
        }
        setDialog(fragment, title, tips, LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
    }

    public static void choosePrivacyAllFcDialog(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("fc_no_look_theyfc", R.string.fc_no_look_theyfc);
        setDialog(fragment, title, LocaleController.getString("fc_no_look_theyfc_sure", R.string.fc_no_look_theyfc_sure), LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
    }

    public static void isDeleteThisPic(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("image_select_tip", R.string.image_select_tip);
        String content = LocaleController.getString("friendscircle_publish_delete_photo", R.string.friendscircle_publish_delete_photo);
        if (fragment instanceof BaseFragment) {
            setDialog((BaseFragment) fragment, title, content, LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
        } else if (fragment instanceof BaseFmts) {
            setDialog((BaseFmts) fragment, title, content, LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
        } else if (fragment instanceof BaseFcFragment) {
            setDialog((BaseFcFragment) fragment, title, content, LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
        }
    }

    public static void isDeleteThisVideo(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("image_select_tip", R.string.image_select_tip);
        setDialog(fragment, title, LocaleController.getString("friendscircle_publish_delete_video", R.string.friendscircle_publish_delete_video), LocaleController.getString("OK", R.string.OK), LocaleController.getString("Cancel", R.string.Cancel), OnConfirmClickListener, onDismissListener);
    }

    public static void exitPublish(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("image_select_tip", R.string.image_select_tip);
        String content = LocaleController.getString("friendscircle_publish_exit_tip", R.string.friendscircle_publish_exit_tip);
        int btnColor = ContextCompat.getColor(ApplicationLoader.applicationContext, R.color.color_FF2ECEFD);
        setDialog(fragment, title, content, LocaleController.getString(R.string.PublishDoSave), LocaleController.getString(R.string.PublishDoNotSave), btnColor, btnColor, OnConfirmClickListener, onDismissListener);
    }

    public static void publishError(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("image_select_tip", R.string.image_select_tip);
        setDialog(fragment, title, LocaleController.getString("friendscircle_publish_fail", R.string.friendscircle_publish_fail), LocaleController.getString("OK", R.string.OK), (String) null, OnConfirmClickListener, onDismissListener);
    }

    public static void publishServerBusyError(Object fragment, FcDialog.OnConfirmClickListener OnConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        String title = LocaleController.getString("image_select_tip", R.string.image_select_tip);
        String content = LocaleController.getString("friendscircle_publish_server_busy", R.string.friendscircle_publish_server_busy);
        if (fragment instanceof BaseFragment) {
            setDialog((BaseFragment) fragment, title, content, LocaleController.getString("OK", R.string.OK), (String) null, OnConfirmClickListener, onDismissListener);
        } else if (fragment instanceof BaseFmts) {
            setDialog((BaseFmts) fragment, title, content, LocaleController.getString("OK", R.string.OK), (String) null, OnConfirmClickListener, onDismissListener);
        }
    }

    public static void setDialog(Object fragment, String title, String content, String confirmText, String cancelText, FcDialog.OnConfirmClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        setDialog(fragment, title, content, confirmText, cancelText, 0, 0, onConfirmClickListener, onDismissListener);
    }

    public static void setDialog(Object fragment, String title, String content, String confirmText, String cancelText, int confirmTextColor, int cancleTextColor, FcDialog.OnConfirmClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        Context context = null;
        if (fragment instanceof BaseFragment) {
            context = ((BaseFragment) fragment).getParentActivity();
        } else if (fragment instanceof BaseFmts) {
            context = ((BaseFmts) fragment).getParentActivity();
        } else if (fragment instanceof BaseFcFragment) {
            context = ((BaseFcFragment) fragment).getParentActivity();
        }
        if (context != null) {
            FcDialog dialog = new FcDialog(context);
            dialog.setCancelable(false);
            dialog.setTitle(title);
            dialog.setContent(content);
            dialog.setConfirmButtonColor(confirmTextColor);
            dialog.setCancelButtonColor(cancleTextColor);
            dialog.setOnConfirmClickListener(confirmText, onConfirmClickListener);
            dialog.setOnCancelClickListener(cancelText, (FcDialog.OnCancelClickListener) null);
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).showDialog(dialog, onDismissListener);
            } else if (fragment instanceof BaseFmts) {
                ((BaseFmts) fragment).showDialog(dialog, onDismissListener);
            } else if (fragment instanceof BaseFcFragment) {
                ((BaseFcFragment) fragment).showDialog(dialog, onDismissListener);
            }
        }
    }
}
