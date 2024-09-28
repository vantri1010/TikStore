package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.request.PostFormRequest;
import com.zhy.http.okhttp.request.RequestCall;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder> implements HasParamsable {
    private List<FileInput> files = new ArrayList();

    public RequestCall build() {
        return new PostFormRequest(this.url, this.tag, this.params, this.headers, this.files, this.id).build();
    }

    public PostFormBuilder files(String key, Map<String, File> files2) {
        for (String filename : files2.keySet()) {
            this.files.add(new FileInput(key, filename, files2.get(filename)));
        }
        return this;
    }

    public PostFormBuilder addFile(String name, String filename, File file) {
        this.files.add(new FileInput(name, filename, file));
        return this;
    }

    public static class FileInput {
        public File file;
        public String filename;
        public String key;

        public FileInput(String name, String filename2, File file2) {
            this.key = name;
            this.filename = filename2;
            this.file = file2;
        }

        public String toString() {
            return "FileInput{key='" + this.key + '\'' + ", filename='" + this.filename + '\'' + ", file=" + this.file + '}';
        }
    }

    public PostFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public PostFormBuilder addParams(String key, String val) {
        if (this.params == null) {
            this.params = new LinkedHashMap();
        }
        this.params.put(key, val);
        return this;
    }
}
