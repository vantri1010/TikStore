package com.stripe.android.net;

import com.stripe.android.util.StripeTextUtils;

public class RequestOptions {
    private final String mApiVersion;
    private final String mIdempotencyKey;
    private final String mPublishableApiKey;

    private RequestOptions(String apiVersion, String idempotencyKey, String publishableApiKey) {
        this.mApiVersion = apiVersion;
        this.mIdempotencyKey = idempotencyKey;
        this.mPublishableApiKey = publishableApiKey;
    }

    public String getApiVersion() {
        return this.mApiVersion;
    }

    public String getIdempotencyKey() {
        return this.mIdempotencyKey;
    }

    public String getPublishableApiKey() {
        return this.mPublishableApiKey;
    }

    public static RequestOptionsBuilder builder(String publishableApiKey) {
        return new RequestOptionsBuilder(publishableApiKey);
    }

    public static final class RequestOptionsBuilder {
        private String apiVersion;
        private String idempotencyKey;
        private String publishableApiKey;

        public RequestOptionsBuilder(String publishableApiKey2) {
            this.publishableApiKey = publishableApiKey2;
        }

        public RequestOptionsBuilder setPublishableApiKey(String publishableApiKey2) {
            this.publishableApiKey = publishableApiKey2;
            return this;
        }

        public RequestOptionsBuilder setIdempotencyKey(String idempotencyKey2) {
            this.idempotencyKey = idempotencyKey2;
            return this;
        }

        public RequestOptionsBuilder setApiVersion(String apiVersion2) {
            this.apiVersion = StripeTextUtils.isBlank(apiVersion2) ? null : apiVersion2;
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(this.apiVersion, this.idempotencyKey, this.publishableApiKey);
        }
    }
}
