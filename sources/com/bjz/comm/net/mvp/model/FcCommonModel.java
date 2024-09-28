package com.bjz.comm.net.mvp.model;

import com.android.tools.r8.annotations.SynthesizedClassMap;
import com.bjz.comm.net.UrlConstant;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.bean.IPResponse;
import com.bjz.comm.net.factory.ApiFactory;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.utils.RxHelper;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@SynthesizedClassMap({$$Lambda$15kGZKH6eZ4hpDsvHURgNsylfw8.class, $$Lambda$77dzvAKl1g9CDlxEuR3k6XzTbI.class, $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s.class, $$Lambda$ocxj43lUtA9VFMIsrwq9eDl1sCM.class})
public class FcCommonModel implements BaseFcContract.IFcCommModel {
    private static final String TAG = FcCommonModel.class.getSimpleName();

    public void unSubscribeTask() {
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(TAG);
    }

    @Deprecated
    public void getIpLocation(DataListener<IPResponse> listener) {
        Observable<IPResponse> observable = ApiFactory.getInstance().getApiCommon().getIpLocation(UrlConstant.GET_IP_LOCATION);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$ocxj43lUtA9VFMIsrwq9eDl1sCM r3 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((IPResponse) obj);
            }
        };
        listener.getClass();
        instance.sendCommRequest(str, observable, r3, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void getUploadAddr(int location, DataListener<BResponse<ArrayList<String>>> listener) {
        Observable<BResponse<ArrayList<String>>> observable = ApiFactory.getInstance().getApiCommon().getUploadAddr(location);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r3 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendRequest(str, observable, r3, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    public void uploadFile(String url, String formKey, String name, File file, DataListener<BResponse<FcMediaResponseBean>> listener) {
        Observable<BResponse<FcMediaResponseBean>> observable = ApiFactory.getInstance().getApiCommon().uploadFile(url, new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("name", name).addFormDataPart(formKey, file.getName(), RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file)).build());
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$mSGpaT9jJInhBATBTFCU4D9tp7s r5 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((BResponse) obj);
            }
        };
        listener.getClass();
        instance.sendCommRequest(str, observable, r5, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }

    private String guessMimeType(String path) {
        String contentTypeFor = null;
        try {
            contentTypeFor = URLConnection.getFileNameMap().getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            return "application/octet-stream";
        }
        return contentTypeFor;
    }

    public void downloadFile(String url, DataListener<ResponseBody> listener) {
        Observable<ResponseBody> observable = ApiFactory.getInstance().getApiCommon().downloadImg(url);
        RxHelper instance = RxHelper.getInstance();
        String str = TAG;
        listener.getClass();
        $$Lambda$15kGZKH6eZ4hpDsvHURgNsylfw8 r3 = new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onResponse((ResponseBody) obj);
            }
        };
        listener.getClass();
        instance.sendCommRequest(str, observable, r3, new Consumer() {
            public final void accept(Object obj) {
                DataListener.this.onError((Throwable) obj);
            }
        });
    }
}
