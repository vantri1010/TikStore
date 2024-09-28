package com.bjz.comm.net.api;

import com.bjz.comm.net.bean.ResponseAccessTokenBean;
import com.bjz.comm.net.bean.ResponseBaiduTranslateBean;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiTranslate {
    @POST
    Observable<ResponseAccessTokenBean> accessToken(@Url String str, @Query("grant_type") String str2, @Query("client_id") String str3, @Query("client_secret") String str4);

    @POST
    Observable<ResponseBaiduTranslateBean> translate(@Url String str, @Body RequestBody requestBody);

    @POST
    @Headers({"format:pcm", "rate:16000"})
    Observable<ResponseBaiduTranslateBean> translate(@Url String str, @Body RequestBody requestBody, @Query("cuid") String str2, @Query("token") String str3, @Query("dev_pid") int i, @Query("lan") long j);
}
