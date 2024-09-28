package com.bjz.comm.net.api;

import com.bjz.comm.net.bean.HuanHuiUploadFileResponseBean;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiHuanHui {
    @POST("pub/upload")
    Observable<HuanHuiUploadFileResponseBean> uploadFile(@Header("Accept") String str, @Header("Accept-Encoding") String str2, @Body RequestBody requestBody);
}
