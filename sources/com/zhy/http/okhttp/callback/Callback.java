package com.zhy.http.okhttp.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Callback<T> {
    public static Callback CALLBACK_DEFAULT = new Callback() {
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            return null;
        }

        public void onError(Call call, Exception e, int id) {
        }

        public void onResponse(Object response, int id) {
        }
    };

    public abstract void onError(Call call, Exception exc, int i);

    public abstract void onResponse(T t, int i);

    public abstract T parseNetworkResponse(Response response, int i) throws Exception;

    public void onBefore(Request request, int id) {
    }

    public void onAfter(int id) {
    }

    public void inProgress(float progress, long total, int id) {
    }

    public boolean validateReponse(Response response, int id) {
        return response.isSuccessful();
    }
}
