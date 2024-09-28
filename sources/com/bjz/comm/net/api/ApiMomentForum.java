package com.bjz.comm.net.api;

import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.BResponseNoData;
import com.bjz.comm.net.bean.FcBgBean;
import com.bjz.comm.net.bean.FcIgnoreUserBean;
import com.bjz.comm.net.bean.FcLikeBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.bean.FcVersionBean;
import com.bjz.comm.net.bean.RespFcAlbumListBean;
import com.bjz.comm.net.bean.RespFcIgnoreBean;
import com.bjz.comm.net.bean.RespFcLikesBean;
import com.bjz.comm.net.bean.RespFcListBean;
import com.bjz.comm.net.bean.RespFcReplyBean;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.bjz.comm.net.bean.RespOthersFcListBean;
import com.bjz.comm.net.bean.RespTopicBean;
import com.bjz.comm.net.bean.RespTopicTypeBean;
import com.bjz.comm.net.bean.ResponseFcAttentionUsertBeanV1;
import com.bjz.comm.net.bean.TokenRequest;
import io.reactivex.Observable;
import java.util.ArrayList;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiMomentForum {
    @GET("followsvc/check")
    Observable<BResponse<Boolean>> checkUserIsFollowed(@Query("FollowUID") long j);

    @GET("basesvc/user/version")
    Observable<BResponse<FcVersionBean>> checkVersion();

    @POST("commentsvc/delete")
    Observable<BResponseNoData> deleteReplyForum(@Body RequestBody requestBody);

    @POST("basesvc/ignore/add")
    Observable<BResponse<ArrayList<FcIgnoreUserBean>>> doAddIgnoreUser(@Body RequestBody requestBody);

    @POST("followsvc/delete")
    Observable<BResponseNoData> doCancelFollowed(@Body RequestBody requestBody);

    @POST("thumbsvc/delete")
    Observable<BResponse<FcLikeBean>> doCancelLike(@Body RequestBody requestBody);

    @POST("basesvc/ignore/delete")
    Observable<BResponse<ArrayList<FcIgnoreUserBean>>> doDeleteIgnoreUser(@Body RequestBody requestBody);

    @POST("forumsvc/delete")
    Observable<BResponseNoData> doDeleteItem(@Body RequestBody requestBody);

    @POST("followsvc/add")
    Observable<BResponseNoData> doFollow(@Body RequestBody requestBody);

    @POST("forumsvc/ignore")
    Observable<BResponseNoData> doIgnoreItem(@Body RequestBody requestBody);

    @POST("thumbsvc/add")
    Observable<BResponse<FcLikeBean>> doLike(@Body RequestBody requestBody);

    @POST("forumsvc/report/add")
    Observable<BResponseNoData> doReport(@Body RequestBody requestBody);

    @POST("forumsvc/permission")
    Observable<BResponseNoData> doSetItemPermission(@Body RequestBody requestBody);

    @GET("basesvc/user/statistics")
    Observable<BResponse<RespFcUserStatisticsBean>> getActionCount(@Query("UserID") long j);

    @GET("commentsvc/page")
    Observable<BResponse<RespFcReplyBean>> getComments(@Query("ForumID") long j, @Query("CommentID") long j2, @Query("ForumUser") long j3, @Query("Limit") int i, @Query("ReplayLimit") int i2);

    @GET("forumsvc/get")
    Observable<BResponse<RespFcListBean>> getDetail(@Query("ForumID") long j, @Query("ForumUser") long j2);

    @GET("basesvc/user/homebg")
    Observable<BResponse<FcBgBean>> getFCBackground(@Query("UserID") long j);

    @GET("basesvc/user/album")
    Observable<BResponse<ArrayList<RespFcAlbumListBean>>> getFcAlbumList(@Query("CreateBy") int i, @Query("ForumID") long j, @Query("Limit") int i2);

    @GET("topicsvc/page")
    Observable<BResponse<RespTopicBean>> getFcTopic(@Query("TopicTypeID") long j, @Query("TopicName") String str, @Query("TopicID") int i, @Query("Limit") int i2);

    @GET("topicsvc/type/all")
    Observable<BResponse<ArrayList<RespTopicTypeBean>>> getFcTopicList();

    @GET("followsvc/me")
    Observable<BResponse<ResponseFcAttentionUsertBeanV1>> getFollowedUserList(@Query("FollowID") int i, @Query("Limit") int i2, @Query("UserName") String str);

    @GET("forumsvc/follow")
    Observable<BResponse<ArrayList<RespFcListBean>>> getHomePageFollowList(@Query("Limit") int i, @Query("ForumID") long j);

    @GET("forumsvc/friend")
    Observable<BResponse<ArrayList<RespFcListBean>>> getHomePageFriendsList(@Query("Limit") int i, @Query("ForumID") long j);

    @GET("forumsvc/recommend")
    Observable<BResponse<ArrayList<RespFcListBean>>> getHomePageRecommendList(@Query("Limit") int i, @Query("ForumID") long j);

    @GET("thumbsvc/page")
    Observable<BResponse<RespFcLikesBean>> getLikeUserList(@Query("ForumID") long j, @Query("ThumbID") long j2, @Query("UpDown") long j3, @Query("Limit") int i);

    @GET("forumsvc/selfmain")
    Observable<BResponse<ArrayList<RespFcListBean>>> getMyFCList(@Query("Limit") int i, @Query("ForumID") long j);

    @GET("forumsvc/othermain")
    Observable<BResponse<RespOthersFcListBean>> getOthersFCList(@Query("Limit") int i, @Query("ForumID") long j, @Query("FriendID") long j2, @Query("RoundNum") int i2);

    @GET("commentsvc/replaypage")
    Observable<BResponse<RespFcReplyBean>> getReplyList(@Query("ForumID") long j, @Query("CommentID") long j2, @Query("SupID") long j3, @Query("SupUID") long j4, @Query("Limit") int i);

    @GET("tokensvc/get")
    Observable<BResponse<TokenRequest>> getToken(@Header("user-id") int i);

    @GET("basesvc/ignore/check")
    Observable<BResponse<RespFcIgnoreBean>> getUserIgnoreSetting(@Query("UserID") long j);

    @POST("forumsvc/add")
    Observable<BResponse<RespFcListBean>> publish(@Body RequestBody requestBody);

    @POST("commentsvc/add")
    Observable<BResponse<FcReplyBean>> replyForum(@Body RequestBody requestBody);

    @POST("basesvc/user/homebg")
    Observable<BResponseNoData> setFcBackground(@Body RequestBody requestBody);
}
