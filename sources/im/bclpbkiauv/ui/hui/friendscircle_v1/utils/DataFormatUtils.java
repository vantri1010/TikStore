package im.bclpbkiauv.ui.hui.friendscircle_v1.utils;

import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.RespFcListBean;
import im.bclpbkiauv.javaBean.fc.AllMsgBean;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import java.util.ArrayList;
import java.util.Iterator;

public class DataFormatUtils {
    public static ArrayList<FcReplyBean> formatFcReplylListBean(ArrayList<FcReplyBean> commentList) {
        ArrayList<FcReplyBean> parentListTemp = new ArrayList<>();
        ArrayList<FcReplyBean> childListTemp = new ArrayList<>();
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getSupID() == 0) {
                parentListTemp.add(commentList.get(i));
            } else {
                childListTemp.add(commentList.get(i));
            }
        }
        for (int i2 = 0; i2 < parentListTemp.size(); i2++) {
            long parentReplyId = parentListTemp.get(i2).getCommentID();
            ArrayList<FcReplyBean> Temp = new ArrayList<>();
            for (int j = 0; j < childListTemp.size(); j++) {
                if (parentReplyId == childListTemp.get(j).getSupID()) {
                    Temp.add(childListTemp.get(j));
                }
            }
            parentListTemp.get(i2).setSubComment(Temp);
        }
        commentList.clear();
        commentList.addAll(parentListTemp);
        return parentListTemp;
    }

    public static ArrayList<AllMsgBean> formatFcUnredReplyLike(ArrayList<FcReplyBean> replyList) {
        ArrayList<AllMsgBean> alllist = new ArrayList<>();
        Iterator<FcReplyBean> it = replyList.iterator();
        while (it.hasNext()) {
            FcReplyBean next = it.next();
        }
        return alllist;
    }

    public static void formatResponseFclistBean4DB(ArrayList<RespFcListBean> mRespFcListBeanList) {
        Iterator<RespFcListBean> it = mRespFcListBeanList.iterator();
        while (it.hasNext()) {
            RespFcListBean temp = it.next();
            temp.setForumID(temp.getForumID());
        }
    }

    public static String getUserNameByid(BaseFragment mBaseFragment, long userId) {
        TLRPC.User itemUser = mBaseFragment.getAccountInstance().getMessagesController().getUser(Integer.valueOf((int) userId));
        if (itemUser != null) {
            return StringUtils.handleTextName(ContactsController.formatName(itemUser.first_name, itemUser.last_name), 12);
        }
        return userId + "";
    }

    public static String getUserNameByid(BaseFmts mBaseFragment, long userId) {
        TLRPC.User itemUser = mBaseFragment.getAccountInstance().getMessagesController().getUser(Integer.valueOf((int) userId));
        if (itemUser != null) {
            return StringUtils.handleTextName(ContactsController.formatName(itemUser.first_name, itemUser.last_name), 12);
        }
        return userId + "";
    }

    public static String getUserNameByid(long userId) {
        TLRPC.User itemUser = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf((int) userId));
        if (itemUser != null) {
            return StringUtils.handleTextName(ContactsController.formatName(itemUser.first_name, itemUser.last_name), 12);
        }
        return userId + "";
    }
}
