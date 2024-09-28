package com.bjz.comm.net.base;

import com.bjz.comm.net.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Converter;

public class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final TypeAdapter<T> adapter;
    private final Gson gson;

    MyGsonResponseBodyConverter(Gson gson2, TypeAdapter<T> adapter2) {
        this.gson = gson2;
        this.adapter = adapter2;
    }

    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        int code = 0;
        try {
            JSONObject object = new JSONObject(response);
            if (!object.isNull("State")) {
                code = object.optInt("State");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            value.close();
            throw th;
        }
        if (code != 400) {
            MediaType mediaType = value.contentType();
            T read = this.adapter.read(this.gson.newJsonReader(new InputStreamReader(new ByteArrayInputStream(response.getBytes()), mediaType != null ? mediaType.charset(UTF_8) : UTF_8)));
            value.close();
            return read;
        }
        throw new ApiException(code, "Token has expired.");
    }
}
