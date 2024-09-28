package com.bjz.comm.net.mvp.presenter;

import android.text.TextUtils;
import android.util.Log;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.UrlConstant;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.model.FcCommonModel;
import com.bjz.comm.net.mvp.presenter.FcCommonPresenter;
import com.bjz.comm.net.utils.FileUtils;
import com.bjz.comm.net.utils.RxHelper;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import okhttp3.ResponseBody;

public class FcCommonPresenter implements BaseFcContract.IFcCommPresenter {
    private String TAG = FcCommonPresenter.class.getSimpleName();
    BaseFcContract.IFcCommView mView;
    private FcCommonModel model = null;
    /* access modifiers changed from: private */
    public String uploadUrl;

    public FcCommonPresenter(BaseFcContract.IFcCommView view) {
        this.mView = view;
        if (0 == 0) {
            this.model = new FcCommonModel();
        }
    }

    public void unSubscribeTask() {
        this.model.unSubscribeTask();
        this.uploadUrl = null;
    }

    public void getUploadAddr(int location, final File file, final DataListener<BResponse<FcMediaResponseBean>> listener) {
        this.model.getUploadAddr(location, new DataListener<BResponse<ArrayList<String>>>() {
            public void onResponse(BResponse<ArrayList<String>> result) {
                if (result == null) {
                    FcCommonPresenter.this.mView.getUploadUrlFailed((String) null);
                } else if (!result.isState() || result.Data == null) {
                    FcCommonPresenter.this.mView.getUploadUrlFailed(result.Message);
                } else {
                    ArrayList<String> data = result.Data;
                    if (data.size() <= 0 || TextUtils.isEmpty(data.get(0))) {
                        FcCommonPresenter.this.mView.getUploadUrlFailed((String) null);
                        return;
                    }
                    FcCommonPresenter fcCommonPresenter = FcCommonPresenter.this;
                    String unused = fcCommonPresenter.uploadUrl = data.get(0) + UrlConstant.PUBLISH_FILE_UPLOAD_URL;
                    FcCommonPresenter.this.uploadFile(file, listener);
                }
            }

            public void onError(Throwable throwable) {
                FcCommonPresenter.this.mView.getUploadUrlFailed(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public void uploadFile(File file, DataListener<BResponse<FcMediaResponseBean>> listener) {
        if (TextUtils.isEmpty(this.uploadUrl)) {
            getUploadAddr(0, file, listener);
        } else if (listener == null) {
            this.model.uploadFile(this.uploadUrl, "file", FileUtils.getInstance().getPicName(file), file, new DataListener<BResponse<FcMediaResponseBean>>() {
                public void onResponse(BResponse<FcMediaResponseBean> result) {
                    if (result == null) {
                        FcCommonPresenter.this.mView.onUploadFileError((String) null);
                    } else if (result.isState()) {
                        FcCommonPresenter.this.mView.onUploadFileSucc((FcMediaResponseBean) result.Data, result.Message);
                    } else {
                        FcCommonPresenter.this.mView.onUploadFileError(result.Message);
                    }
                }

                public void onError(Throwable throwable) {
                    FcCommonPresenter.this.mView.onUploadFileError(RxHelper.getInstance().getErrorInfo(throwable));
                }
            });
        } else {
            this.model.uploadFile(this.uploadUrl, "file", FileUtils.getInstance().getPicName(file), file, listener);
        }
    }

    public void downloadFile(String url, final String dirPath, final String fileName) {
        if (BuildVars.LOG_VERSION) {
            Log.d("FcDownloadPic", "downloadFile ===>  , url = " + url + " , dirPath = " + dirPath + " , fileName = " + fileName);
        }
        this.model.downloadFile(url, new DataListener<ResponseBody>() {
            public void onResponse(ResponseBody result) {
                if (result != null) {
                    Observable.create(new ObservableOnSubscribe(result, dirPath, fileName) {
                        private final /* synthetic */ ResponseBody f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ String f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void subscribe(ObservableEmitter observableEmitter) {
                            FcCommonPresenter.AnonymousClass3.this.lambda$onResponse$0$FcCommonPresenter$3(this.f$1, this.f$2, this.f$3, observableEmitter);
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<File>() {
                        public void onSubscribe(Disposable d) {
                        }

                        public void onNext(File file) {
                            if (file != null) {
                                FcCommonPresenter.this.mView.onDownloadFileSucc(file);
                            } else {
                                FcCommonPresenter.this.mView.onDownloadFileError("文件保存异常");
                            }
                        }

                        public void onError(Throwable e) {
                            FcCommonPresenter.this.mView.onDownloadFileError("文件保存异常");
                        }

                        public void onComplete() {
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$onResponse$0$FcCommonPresenter$3(ResponseBody result, String dirPath, String fileName, ObservableEmitter emitter) throws Exception {
                emitter.onNext(FcCommonPresenter.this.saveFile(result, dirPath, fileName));
            }

            public void onError(Throwable throwable) {
                FcCommonPresenter.this.mView.onDownloadFileError(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    public File saveFile(ResponseBody response, String dirPath, String fileName) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;
        try {
            InputStream is2 = response.byteStream();
            long sum = 0;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            FileOutputStream fos2 = new FileOutputStream(file);
            while (true) {
                int read = is2.read(buf);
                int len = read;
                if (read == -1) {
                    break;
                }
                sum += (long) len;
                fos2.write(buf, 0, len);
            }
            fos2.flush();
            try {
                response.close();
                if (is2 != null) {
                    is2.close();
                }
            } catch (IOException e) {
            }
            try {
                fos2.close();
            } catch (IOException e2) {
            }
            return file;
        } catch (Throwable th) {
            try {
                response.close();
                if (is != null) {
                    is.close();
                }
            } catch (IOException e3) {
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }
}
