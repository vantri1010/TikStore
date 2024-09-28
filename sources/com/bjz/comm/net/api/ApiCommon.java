package com.bjz.comm.net.api;

import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.bean.IPResponse;
import io.reactivex.Observable;
import java.util.ArrayList;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiCommon {
    @Streaming
    @GET
    @Headers({"BaseUrl:self"})
    Observable<ResponseBody> downloadImg(@Url String str);

    @GET
    @Headers({"BaseUrl:self"})
    Observable<ResponseBody> getDiscoveryPageBannerData(@Url String str);

    @Deprecated
    @GET
    @Headers({"BaseUrl:self"})
    Observable<IPResponse> getIpLocation(@Url String str);

    @GET("basesvc/uploadurl")
    Observable<BResponse<ArrayList<String>>> getUploadAddr(@Query("Location") int i);

    @POST
    @Headers({"BaseUrl:self"})
    Observable<BResponse<FcMediaResponseBean>> uploadFile(@Url String str, @Body RequestBody requestBody);
}
