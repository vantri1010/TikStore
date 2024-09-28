package com.bjz.comm.net.api;

import com.bjz.comm.net.bean.BResponseBool;
import com.bjz.comm.net.bean.MiniProgramBean;
import com.bjz.comm.net.bean.ResponseMProgramListBean;
import io.reactivex.Observable;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

public interface ApiMiniProgram {
    @Streaming
    @GET("appletapi/base/download")
    Observable<ResponseBody> downloadWgt(@Query("name") String str);

    @GET("appletapi/app/all")
    Observable<BResponseBool<ResponseMProgramListBean>> getList(@QueryMap HashMap<String, Object> hashMap);

    @GET("appletapi/app/check")
    Observable<BResponseBool<MiniProgramBean>> queryInfo(@Query("ID") int i);
}
