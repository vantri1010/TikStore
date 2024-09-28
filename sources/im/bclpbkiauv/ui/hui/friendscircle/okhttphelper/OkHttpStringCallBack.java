package im.bclpbkiauv.ui.hui.friendscircle.okhttphelper;

import android.content.Context;
import com.google.android.exoplayer2.util.Log;
import com.socks.library.KLog;
import com.zhy.http.okhttp.callback.StringCallback;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import java.net.UnknownHostException;
import okhttp3.Call;
import okhttp3.Request;

public class OkHttpStringCallBack extends StringCallback {
    private Context mContext = null;
    private AlertDialog progressDialog = null;

    public OkHttpStringCallBack(Context mContext2) {
        this.mContext = mContext2;
        if (mContext2 != null) {
            AlertDialog alertDialog = new AlertDialog(mContext2, 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCancel(false);
        }
    }

    public OkHttpStringCallBack() {
    }

    public void onBefore(Request request, int id) {
        try {
            if (this.mContext != null) {
                if (this.progressDialog == null) {
                    AlertDialog alertDialog = new AlertDialog(this.mContext, 3);
                    this.progressDialog = alertDialog;
                    alertDialog.setCanCancel(false);
                }
                this.progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAfter(int id) {
        try {
            if (this.progressDialog != null) {
                this.progressDialog.dismiss();
                this.progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.progressDialog = null;
        }
    }

    public void onError(Call call, Exception e, int id) {
        KLog.e("---------请求异常" + e.getMessage() + "   " + id);
        if (!(e instanceof UnknownHostException)) {
            e.printStackTrace();
        }
    }

    public void onResponse(String response, int id) {
        KLog.d("");
        Log.e("okhttp", "onResponse：complete");
    }

    public void inProgress(float progress, long total, int id) {
    }
}
