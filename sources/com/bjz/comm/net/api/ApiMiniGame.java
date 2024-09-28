package com.bjz.comm.net.api;

import io.reactivex.Observable;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiMiniGame {
    @Streaming
    @GET
    @Headers({"RequestUrl:self"})
    Observable<ResponseBody> downloadZip(@Url String str);

    @GET("api/v1/public/gameList")
    Observable<String> getList(@QueryMap HashMap<String, Object> hashMap);
}
