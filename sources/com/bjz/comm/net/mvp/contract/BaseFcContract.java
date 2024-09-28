package com.bjz.comm.net.mvp.contract;

import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.base.IBModel;
import com.bjz.comm.net.base.IBPresenter;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.FcBgBean;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.IPResponse;
import com.bjz.comm.net.bean.RequestReplyFcBean;
import com.bjz.comm.net.bean.RespFcAlbumListBean;
import com.bjz.comm.net.bean.RespFcLikesBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcReplyBean;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.bean.RespOthersFcListBean;
import java.io.File;
import java.util.ArrayList;
import okhttp3.ResponseBody;

public class BaseFcContract {

    public interface IFcCommItemModel extends IBModel {
        void doAddIgnoreUser(ArrayList<FcIgnoreUserBean> arrayList, DataListener<BResponse<ArrayList<FcIgnoreUserBean>>> dataListener);

        void doCancelFollowed(long j, DataListener<BResponseNoData> dataListener);

        void doCancelLike(long j, long j2, long j3, long j4, DataListener<BResponse<FcLikeBean>> dataListener);

        void doDeleteComment(long j, long j2, long j3, DataListener<BResponseNoData> dataListener);

        void doDeleteIgnoreUser(ArrayList<FcIgnoreUserBean> arrayList, DataListener<BResponse<ArrayList<FcIgnoreUserBean>>> dataListener);

        void doDeleteItem(long j, DataListener<BResponseNoData> dataListener);

        void doFollow(long j, DataListener<BResponseNoData> dataListener);

        void doIgnoreItem(long j, DataListener<BResponseNoData> dataListener);

        void doLike(long j, long j2, long j3, long j4, DataListener<BResponse<FcLikeBean>> dataListener);

        void doReply(RequestReplyFcBean requestReplyFcBean, DataListener<BResponse<FcReplyBean>> dataListener);

        void doSetItemPermission(long j, int i, DataListener<BResponseNoData> dataListener);

        void getFcBackgroundUrl(long j, DataListener<BResponse<FcBgBean>> dataListener);
    }

    public interface IFcCommItemPresenter extends IBPresenter {
        void doAddIgnoreUser(ArrayList<FcIgnoreUserBean> arrayList);

        void doCancelFollowed(long j, int i);

        void doCancelLike(long j, long j2, long j3, long j4, int i);

        void doDeleteComment(long j, long j2, long j3, int i, int i2);

        void doDeleteIgnoreUser(ArrayList<FcIgnoreUserBean> arrayList);

        void doDeleteItem(long j, int i);

        void doFollow(long j, int i);

        void doIgnoreItem(long j, int i);

        void doLike(long j, long j2, long j3, long j4, int i);

        void doReply(RequestReplyFcBean requestReplyFcBean, int i);

        void doSetItemPermission(long j, int i, int i2);

        void getFCBackground(long j);
    }

    public interface IFcCommItemView {
        void doAddIgnoreUserFailed(String str);

        void doAddIgnoreUserSucc(ArrayList<FcIgnoreUserBean> arrayList, String str);

        void doCancelFollowedFailed(String str);

        void doCancelFollowedSucc(long j, int i, String str);

        void doCancelLikeFailed(int i, String str);

        void doCancelLikeSucc(FcLikeBean fcLikeBean, long j, int i, String str);

        void doDeleteIgnoreUserFiled(String str);

        void doDeleteIgnoreUserSucc(ArrayList<FcIgnoreUserBean> arrayList, String str);

        void doDeleteItemFailed(String str);

        void doDeleteItemSucc(long j, int i, String str);

        void doDeleteReplySucc(long j, long j2, int i, int i2);

        void doFollowFailed(String str);

        void doFollowSucc(long j, int i, String str);

        void doIgnoreItemFailed(String str);

        void doIgnoreItemSucc(long j, int i, String str);

        void doLikeFailed(int i, String str);

        void doLikeSucc(FcLikeBean fcLikeBean, long j, int i, String str);

        void doReplyFailed(int i, boolean z, String str, String str2);

        void doReplySucc(FcReplyBean fcReplyBean, int i);

        void doSetItemPermissionFailed(String str);

        void doSetItemPermissionSucc(long j, int i, int i2, String str);

        void getFcBackgroundSucc(FcBgBean fcBgBean);

        void onError(String str);
    }

    public interface IFcCommModel extends IBModel {
        void downloadFile(String str, DataListener<ResponseBody> dataListener);

        @Deprecated
        void getIpLocation(DataListener<IPResponse> dataListener);

        void getUploadAddr(int i, DataListener<BResponse<ArrayList<String>>> dataListener);

        void uploadFile(String str, String str2, String str3, File file, DataListener<BResponse<FcMediaResponseBean>> dataListener);
    }

    public interface IFcCommPresenter extends IBPresenter {
        void downloadFile(String str, String str2, String str3);

        void getUploadAddr(int i, File file, DataListener<BResponse<FcMediaResponseBean>> dataListener);

        void uploadFile(File file, DataListener<BResponse<FcMediaResponseBean>> dataListener);
    }

    public interface IFcCommView {
        void getUploadUrlFailed(String str);

        void onDownloadFileError(String str);

        void onDownloadFileSucc(File file);

        void onError(String str);

        void onUploadFileError(String str);

        void onUploadFileSucc(FcMediaResponseBean fcMediaResponseBean, String str);
    }

    public interface IFcPageAlbumListModel extends IBModel {
        void getAlbumList(int i, long j, int i2, DataListener<BResponse<ArrayList<RespFcAlbumListBean>>> dataListener);
    }

    public interface IFcPageAlbumListPresenter extends IBPresenter {
        void getAlbumList(int i, long j, int i2);
    }

    public interface IFcPageAlbumListView {
        void getAlbumListFailed(String str);

        void getAlbumListSucc(ArrayList<RespFcAlbumListBean> arrayList);
    }

    public interface IFcPageDetailModel extends IBModel {
        void getComments(long j, long j2, long j3, int i, DataListener<BResponse<RespFcReplyBean>> dataListener);

        void getDetail(long j, long j2, DataListener<BResponse<RespFcListBean>> dataListener);

        void getLikeUserList(long j, long j2, int i, DataListener<BResponse<RespFcLikesBean>> dataListener);

        void getReplyList(FcReplyBean fcReplyBean, long j, int i, DataListener<BResponse<RespFcReplyBean>> dataListener);
    }

    public interface IFcPageDetailPresenter extends IBPresenter {
        void getComments(long j, long j2, long j3, int i);

        void getDetail(long j, long j2);

        void getLikeUserList(long j, long j2, int i);

        void getReplyList(FcReplyBean fcReplyBean, int i, long j, int i2);
    }

    public interface IFcPageDetailView {
        void getCommentsFailed(String str);

        void getCommentsSucc(ArrayList<FcReplyBean> arrayList);

        void getDetailFailed(String str);

        void getDetailSucc(RespFcListBean respFcListBean);

        void getLikeUserListFiled(String str);

        void getLikeUserListSucc(RespFcLikesBean respFcLikesBean);

        void getReplyListFailed(FcReplyBean fcReplyBean, int i, String str);

        void getReplyListSucc(FcReplyBean fcReplyBean, int i, ArrayList<FcReplyBean> arrayList);

        void onError(String str);
    }

    public interface IFcPageFollowedModel extends IBModel {
        void getFcList(int i, long j, DataListener<BResponse<ArrayList<RespFcListBean>>> dataListener);
    }

    public interface IFcPageFollowedPresenter extends IBPresenter {
        void getFcList(int i, long j);
    }

    public interface IFcPageFollowedView {
        void getFcListFailed(String str);

        void getFcListSucc(ArrayList<RespFcListBean> arrayList);

        void onError(String str);
    }

    public interface IFcPageHomeModel extends IBModel {
        void getFcList(int i, long j, DataListener<BResponse<ArrayList<RespFcListBean>>> dataListener);
    }

    public interface IFcPageHomePresenter extends IBPresenter {
        void getFcList(int i, long j);
    }

    public interface IFcPageHomeView {
        void getFcListFailed(String str);

        void getFcListSucc(ArrayList<RespFcListBean> arrayList);

        void onError(String str);
    }

    public interface IFcPageMineModel extends IBModel {
        void getActionCount(long j, DataListener<BResponse<RespFcUserStatisticsBean>> dataListener);

        void getFCList(int i, long j, DataListener<BResponse<ArrayList<RespFcListBean>>> dataListener);

        void setFcBackground(String str, DataListener<BResponseNoData> dataListener);
    }

    public interface IFcPageMinePresenter extends IBPresenter {
        void getActionCount(long j);

        void getFCList(int i, long j);

        void setFcBackground(String str);
    }

    public interface IFcPageMineView {
        void getActionCountSucc(RespFcUserStatisticsBean respFcUserStatisticsBean);

        void getFCListFailed(String str);

        void getFCListSucc(ArrayList<RespFcListBean> arrayList);

        void onError(String str);

        void setFcBackgroundFailed(String str);

        void setFcBackgroundSucc(String str, String str2);
    }

    public interface IFcPageOthersModel extends IBModel {
        void checkIsFollowed(long j, DataListener<BResponse<Boolean>> dataListener);

        void getActionCount(long j, DataListener<BResponse<RespFcUserStatisticsBean>> dataListener);

        void getFCList(int i, long j, long j2, int i2, DataListener<BResponse<RespOthersFcListBean>> dataListener);
    }

    public interface IFcPageOthersPresenter extends IBPresenter {
        void checkIsFollowed(long j);

        void getActionCount(long j);

        void getFCList(int i, long j, long j2, int i2);
    }

    public interface IFcPageOthersView {
        void checkIsFollowedSucc(Boolean bool);

        void getActionCountSucc(RespFcUserStatisticsBean respFcUserStatisticsBean);

        void getFCListFailed(String str);

        void getFCListSucc(String str, RespOthersFcListBean respOthersFcListBean);

        void onError(String str);
    }

    public interface IFcPageRecommendModel extends IBModel {
        void getFcList(int i, long j, DataListener<BResponse<ArrayList<RespFcListBean>>> dataListener);
    }

    public interface IFcPageRecommendPresenter extends IBPresenter {
        void getFcList(int i, long j);
    }

    public interface IFcPageRecommendView {
        void getFcListFailed(String str);

        void getFcListSucc(ArrayList<RespFcListBean> arrayList);

        void onError(String str);
    }
}
