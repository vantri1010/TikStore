package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.ErrorRequestCoordinator;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.signature.AndroidResourceSignature;
import com.bumptech.glide.util.Executors;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class RequestBuilder<TranscodeType> extends BaseRequestOptions<RequestBuilder<TranscodeType>> implements Cloneable, ModelTypes<RequestBuilder<TranscodeType>> {
    protected static final RequestOptions DOWNLOAD_ONLY_OPTIONS = ((RequestOptions) ((RequestOptions) ((RequestOptions) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)).priority(Priority.LOW)).skipMemoryCache(true));
    private final Context context;
    private RequestBuilder<TranscodeType> errorBuilder;
    private final Glide glide;
    private final GlideContext glideContext;
    private boolean isDefaultTransitionOptionsSet;
    private boolean isModelSet;
    private boolean isThumbnailBuilt;
    private Object model;
    private List<RequestListener<TranscodeType>> requestListeners;
    private final RequestManager requestManager;
    private Float thumbSizeMultiplier;
    private RequestBuilder<TranscodeType> thumbnailBuilder;
    private final Class<TranscodeType> transcodeClass;
    private TransitionOptions<?, ? super TranscodeType> transitionOptions;

    protected RequestBuilder(Glide glide2, RequestManager requestManager2, Class<TranscodeType> transcodeClass2, Context context2) {
        this.isDefaultTransitionOptionsSet = true;
        this.glide = glide2;
        this.requestManager = requestManager2;
        this.transcodeClass = transcodeClass2;
        this.context = context2;
        this.transitionOptions = requestManager2.getDefaultTransitionOptions(transcodeClass2);
        this.glideContext = glide2.getGlideContext();
        initRequestListeners(requestManager2.getDefaultRequestListeners());
        apply((BaseRequestOptions<?>) requestManager2.getDefaultRequestOptions());
    }

    protected RequestBuilder(Class<TranscodeType> transcodeClass2, RequestBuilder<?> other) {
        this(other.glide, other.requestManager, transcodeClass2, other.context);
        this.model = other.model;
        this.isModelSet = other.isModelSet;
        apply((BaseRequestOptions<?>) other);
    }

    private void initRequestListeners(List<RequestListener<Object>> requestListeners2) {
        for (RequestListener<Object> listener : requestListeners2) {
            addListener(listener);
        }
    }

    public RequestBuilder<TranscodeType> apply(BaseRequestOptions<?> requestOptions) {
        Preconditions.checkNotNull(requestOptions);
        return (RequestBuilder) super.apply(requestOptions);
    }

    public RequestBuilder<TranscodeType> transition(TransitionOptions<?, ? super TranscodeType> transitionOptions2) {
        this.transitionOptions = (TransitionOptions) Preconditions.checkNotNull(transitionOptions2);
        this.isDefaultTransitionOptionsSet = false;
        return this;
    }

    public RequestBuilder<TranscodeType> listener(RequestListener<TranscodeType> requestListener) {
        this.requestListeners = null;
        return addListener(requestListener);
    }

    public RequestBuilder<TranscodeType> addListener(RequestListener<TranscodeType> requestListener) {
        if (requestListener != null) {
            if (this.requestListeners == null) {
                this.requestListeners = new ArrayList();
            }
            this.requestListeners.add(requestListener);
        }
        return this;
    }

    public RequestBuilder<TranscodeType> error(RequestBuilder<TranscodeType> errorBuilder2) {
        this.errorBuilder = errorBuilder2;
        return this;
    }

    public RequestBuilder<TranscodeType> thumbnail(RequestBuilder<TranscodeType> thumbnailRequest) {
        this.thumbnailBuilder = thumbnailRequest;
        return this;
    }

    public RequestBuilder<TranscodeType> thumbnail(RequestBuilder<TranscodeType>... thumbnails) {
        if (thumbnails == null || thumbnails.length == 0) {
            return thumbnail((RequestBuilder) null);
        }
        RequestBuilder<TranscodeType> previous = null;
        for (int i = thumbnails.length - 1; i >= 0; i--) {
            RequestBuilder<TranscodeType> current = thumbnails[i];
            if (current != null) {
                if (previous == null) {
                    previous = current;
                } else {
                    previous = current.thumbnail(previous);
                }
            }
        }
        return thumbnail(previous);
    }

    public RequestBuilder<TranscodeType> thumbnail(float sizeMultiplier) {
        if (sizeMultiplier < 0.0f || sizeMultiplier > 1.0f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.thumbSizeMultiplier = Float.valueOf(sizeMultiplier);
        return this;
    }

    public RequestBuilder<TranscodeType> load(Object model2) {
        return loadGeneric(model2);
    }

    private RequestBuilder<TranscodeType> loadGeneric(Object model2) {
        this.model = model2;
        this.isModelSet = true;
        return this;
    }

    public RequestBuilder<TranscodeType> load(Bitmap bitmap) {
        return loadGeneric(bitmap).apply((BaseRequestOptions<?>) RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    public RequestBuilder<TranscodeType> load(Drawable drawable) {
        return loadGeneric(drawable).apply((BaseRequestOptions<?>) RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    public RequestBuilder<TranscodeType> load(String string) {
        return loadGeneric(string);
    }

    public RequestBuilder<TranscodeType> load(Uri uri) {
        return loadGeneric(uri);
    }

    public RequestBuilder<TranscodeType> load(File file) {
        return loadGeneric(file);
    }

    public RequestBuilder<TranscodeType> load(Integer resourceId) {
        return loadGeneric(resourceId).apply((BaseRequestOptions<?>) RequestOptions.signatureOf(AndroidResourceSignature.obtain(this.context)));
    }

    @Deprecated
    public RequestBuilder<TranscodeType> load(URL url) {
        return loadGeneric(url);
    }

    public RequestBuilder<TranscodeType> load(byte[] model2) {
        RequestBuilder<TranscodeType> result = loadGeneric(model2);
        if (!result.isDiskCacheStrategySet()) {
            result = result.apply((BaseRequestOptions<?>) RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
        }
        if (!result.isSkipMemoryCacheSet()) {
            return result.apply((BaseRequestOptions<?>) RequestOptions.skipMemoryCacheOf(true));
        }
        return result;
    }

    public RequestBuilder<TranscodeType> clone() {
        RequestBuilder<TranscodeType> result = (RequestBuilder) super.clone();
        result.transitionOptions = result.transitionOptions.clone();
        return result;
    }

    public <Y extends Target<TranscodeType>> Y into(Y target) {
        return into(target, (RequestListener) null, Executors.mainThreadExecutor());
    }

    /* access modifiers changed from: package-private */
    public <Y extends Target<TranscodeType>> Y into(Y target, RequestListener<TranscodeType> targetListener, Executor callbackExecutor) {
        return into(target, targetListener, this, callbackExecutor);
    }

    private <Y extends Target<TranscodeType>> Y into(Y target, RequestListener<TranscodeType> targetListener, BaseRequestOptions<?> options, Executor callbackExecutor) {
        Preconditions.checkNotNull(target);
        if (this.isModelSet) {
            Request request = buildRequest(target, targetListener, options, callbackExecutor);
            Request previous = target.getRequest();
            if (!request.isEquivalentTo(previous) || isSkipMemoryCacheWithCompletePreviousRequest(options, previous)) {
                this.requestManager.clear((Target<?>) target);
                target.setRequest(request);
                this.requestManager.track(target, request);
                return target;
            }
            if (!((Request) Preconditions.checkNotNull(previous)).isRunning()) {
                previous.begin();
            }
            return target;
        }
        throw new IllegalArgumentException("You must call #load() before calling #into()");
    }

    private boolean isSkipMemoryCacheWithCompletePreviousRequest(BaseRequestOptions<?> options, Request previous) {
        return !options.isMemoryCacheable() && previous.isComplete();
    }

    public ViewTarget<ImageView, TranscodeType> into(ImageView view) {
        Util.assertMainThread();
        Preconditions.checkNotNull(view);
        BaseRequestOptions baseRequestOptions = this;
        if (!baseRequestOptions.isTransformationSet() && baseRequestOptions.isTransformationAllowed() && view.getScaleType() != null) {
            switch (AnonymousClass1.$SwitchMap$android$widget$ImageView$ScaleType[view.getScaleType().ordinal()]) {
                case 1:
                    baseRequestOptions = baseRequestOptions.clone().optionalCenterCrop();
                    break;
                case 2:
                    baseRequestOptions = baseRequestOptions.clone().optionalCenterInside();
                    break;
                case 3:
                case 4:
                case 5:
                    baseRequestOptions = baseRequestOptions.clone().optionalFitCenter();
                    break;
                case 6:
                    baseRequestOptions = baseRequestOptions.clone().optionalCenterInside();
                    break;
            }
        }
        return (ViewTarget) into(this.glideContext.buildImageViewTarget(view, this.transcodeClass), (RequestListener) null, baseRequestOptions, Executors.mainThreadExecutor());
    }

    @Deprecated
    public FutureTarget<TranscodeType> into(int width, int height) {
        return submit(width, height);
    }

    public FutureTarget<TranscodeType> submit() {
        return submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public FutureTarget<TranscodeType> submit(int width, int height) {
        RequestFutureTarget<TranscodeType> target = new RequestFutureTarget<>(width, height);
        return (FutureTarget) into(target, target, Executors.directExecutor());
    }

    public Target<TranscodeType> preload(int width, int height) {
        return into(PreloadTarget.obtain(this.requestManager, width, height));
    }

    public Target<TranscodeType> preload() {
        return preload(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Deprecated
    public <Y extends Target<File>> Y downloadOnly(Y target) {
        return getDownloadOnlyRequest().into(target);
    }

    @Deprecated
    public FutureTarget<File> downloadOnly(int width, int height) {
        return getDownloadOnlyRequest().submit(width, height);
    }

    /* access modifiers changed from: protected */
    public RequestBuilder<File> getDownloadOnlyRequest() {
        return new RequestBuilder(File.class, this).apply((BaseRequestOptions<?>) DOWNLOAD_ONLY_OPTIONS);
    }

    /* renamed from: com.bumptech.glide.RequestBuilder$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;
        static final /* synthetic */ int[] $SwitchMap$com$bumptech$glide$Priority;

        static {
            int[] iArr = new int[Priority.values().length];
            $SwitchMap$com$bumptech$glide$Priority = iArr;
            try {
                iArr[Priority.LOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.HIGH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.IMMEDIATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[ImageView.ScaleType.values().length];
            $SwitchMap$android$widget$ImageView$ScaleType = iArr2;
            try {
                iArr2[ImageView.ScaleType.CENTER_CROP.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_INSIDE.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER.ordinal()] = 7;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.MATRIX.ordinal()] = 8;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    private Priority getThumbnailPriority(Priority current) {
        int i = AnonymousClass1.$SwitchMap$com$bumptech$glide$Priority[current.ordinal()];
        if (i == 1) {
            return Priority.NORMAL;
        }
        if (i == 2) {
            return Priority.HIGH;
        }
        if (i == 3 || i == 4) {
            return Priority.IMMEDIATE;
        }
        throw new IllegalArgumentException("unknown priority: " + getPriority());
    }

    private Request buildRequest(Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, BaseRequestOptions<?> requestOptions, Executor callbackExecutor) {
        return buildRequestRecursive(new Object(), target, targetListener, (RequestCoordinator) null, this.transitionOptions, requestOptions.getPriority(), requestOptions.getOverrideWidth(), requestOptions.getOverrideHeight(), requestOptions, callbackExecutor);
    }

    private Request buildRequestRecursive(Object requestLock, Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, RequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions2, Priority priority, int overrideWidth, int overrideHeight, BaseRequestOptions<?> requestOptions, Executor callbackExecutor) {
        RequestCoordinator parentCoordinator2;
        RequestCoordinator errorRequestCoordinator;
        if (this.errorBuilder != null) {
            RequestCoordinator errorRequestCoordinator2 = new ErrorRequestCoordinator(requestLock, parentCoordinator);
            errorRequestCoordinator = errorRequestCoordinator2;
            parentCoordinator2 = errorRequestCoordinator2;
        } else {
            Object obj = requestLock;
            errorRequestCoordinator = null;
            parentCoordinator2 = parentCoordinator;
        }
        Request mainRequest = buildThumbnailRequestRecursive(requestLock, target, targetListener, parentCoordinator2, transitionOptions2, priority, overrideWidth, overrideHeight, requestOptions, callbackExecutor);
        if (errorRequestCoordinator == null) {
            return mainRequest;
        }
        int errorOverrideWidth = this.errorBuilder.getOverrideWidth();
        int errorOverrideHeight = this.errorBuilder.getOverrideHeight();
        if (Util.isValidDimensions(overrideWidth, overrideHeight) && !this.errorBuilder.isValidOverride()) {
            errorOverrideWidth = requestOptions.getOverrideWidth();
            errorOverrideHeight = requestOptions.getOverrideHeight();
        }
        RequestBuilder<TranscodeType> requestBuilder = this.errorBuilder;
        ErrorRequestCoordinator errorRequestCoordinator3 = errorRequestCoordinator;
        errorRequestCoordinator3.setRequests(mainRequest, requestBuilder.buildRequestRecursive(requestLock, target, targetListener, errorRequestCoordinator3, requestBuilder.transitionOptions, requestBuilder.getPriority(), errorOverrideWidth, errorOverrideHeight, this.errorBuilder, callbackExecutor));
        return errorRequestCoordinator3;
    }

    /* JADX WARNING: type inference failed for: r35v0, types: [com.bumptech.glide.request.BaseRequestOptions<?>, com.bumptech.glide.request.BaseRequestOptions] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.bumptech.glide.request.Request buildThumbnailRequestRecursive(java.lang.Object r27, com.bumptech.glide.request.target.Target<TranscodeType> r28, com.bumptech.glide.request.RequestListener<TranscodeType> r29, com.bumptech.glide.request.RequestCoordinator r30, com.bumptech.glide.TransitionOptions<?, ? super TranscodeType> r31, com.bumptech.glide.Priority r32, int r33, int r34, com.bumptech.glide.request.BaseRequestOptions<?> r35, java.util.concurrent.Executor r36) {
        /*
            r26 = this;
            r11 = r26
            r15 = r27
            r14 = r30
            r13 = r32
            com.bumptech.glide.RequestBuilder<TranscodeType> r0 = r11.thumbnailBuilder
            if (r0 == 0) goto L_0x00b2
            boolean r1 = r11.isThumbnailBuilt
            if (r1 != 0) goto L_0x00a7
            com.bumptech.glide.TransitionOptions<?, ? super TranscodeType> r1 = r0.transitionOptions
            boolean r0 = r0.isDefaultTransitionOptionsSet
            if (r0 == 0) goto L_0x001b
            r1 = r31
            r23 = r1
            goto L_0x001d
        L_0x001b:
            r23 = r1
        L_0x001d:
            com.bumptech.glide.RequestBuilder<TranscodeType> r0 = r11.thumbnailBuilder
            boolean r0 = r0.isPrioritySet()
            if (r0 == 0) goto L_0x002e
            com.bumptech.glide.RequestBuilder<TranscodeType> r0 = r11.thumbnailBuilder
            com.bumptech.glide.Priority r0 = r0.getPriority()
            r18 = r0
            goto L_0x0034
        L_0x002e:
            com.bumptech.glide.Priority r0 = r11.getThumbnailPriority(r13)
            r18 = r0
        L_0x0034:
            com.bumptech.glide.RequestBuilder<TranscodeType> r0 = r11.thumbnailBuilder
            int r0 = r0.getOverrideWidth()
            com.bumptech.glide.RequestBuilder<TranscodeType> r1 = r11.thumbnailBuilder
            int r1 = r1.getOverrideHeight()
            boolean r2 = com.bumptech.glide.util.Util.isValidDimensions(r33, r34)
            if (r2 == 0) goto L_0x005c
            com.bumptech.glide.RequestBuilder<TranscodeType> r2 = r11.thumbnailBuilder
            boolean r2 = r2.isValidOverride()
            if (r2 != 0) goto L_0x005c
            int r0 = r35.getOverrideWidth()
            int r1 = r35.getOverrideHeight()
            r24 = r0
            r25 = r1
            goto L_0x0060
        L_0x005c:
            r24 = r0
            r25 = r1
        L_0x0060:
            com.bumptech.glide.request.ThumbnailRequestCoordinator r0 = new com.bumptech.glide.request.ThumbnailRequestCoordinator
            r0.<init>(r15, r14)
            r12 = r0
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r35
            r5 = r12
            r6 = r31
            r7 = r32
            r8 = r33
            r9 = r34
            r10 = r36
            com.bumptech.glide.request.Request r0 = r0.obtainRequest(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r1 = 1
            r11.isThumbnailBuilt = r1
            com.bumptech.glide.RequestBuilder<TranscodeType> r1 = r11.thumbnailBuilder
            r2 = r12
            r12 = r1
            r10 = r13
            r13 = r27
            r9 = r14
            r14 = r28
            r8 = r15
            r15 = r29
            r16 = r2
            r17 = r23
            r19 = r24
            r20 = r25
            r21 = r1
            r22 = r36
            com.bumptech.glide.request.Request r1 = r12.buildRequestRecursive(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            r3 = 0
            r11.isThumbnailBuilt = r3
            r2.setRequests(r0, r1)
            return r2
        L_0x00a7:
            r10 = r13
            r9 = r14
            r8 = r15
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()"
            r0.<init>(r1)
            throw r0
        L_0x00b2:
            r10 = r13
            r9 = r14
            r8 = r15
            java.lang.Float r0 = r11.thumbSizeMultiplier
            if (r0 == 0) goto L_0x00fb
            com.bumptech.glide.request.ThumbnailRequestCoordinator r0 = new com.bumptech.glide.request.ThumbnailRequestCoordinator
            r0.<init>(r8, r9)
            r12 = r0
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r35
            r5 = r12
            r6 = r31
            r7 = r32
            r8 = r33
            r9 = r34
            r13 = r10
            r10 = r36
            com.bumptech.glide.request.Request r14 = r0.obtainRequest(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            com.bumptech.glide.request.BaseRequestOptions r0 = r35.clone()
            java.lang.Float r1 = r11.thumbSizeMultiplier
            float r1 = r1.floatValue()
            com.bumptech.glide.request.BaseRequestOptions r15 = r0.sizeMultiplier(r1)
            com.bumptech.glide.Priority r7 = r11.getThumbnailPriority(r13)
            r0 = r26
            r1 = r27
            r4 = r15
            com.bumptech.glide.request.Request r0 = r0.obtainRequest(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r12.setRequests(r14, r0)
            return r12
        L_0x00fb:
            r13 = r10
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r35
            r5 = r30
            r6 = r31
            r7 = r32
            r8 = r33
            r9 = r34
            r10 = r36
            com.bumptech.glide.request.Request r0 = r0.obtainRequest(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.RequestBuilder.buildThumbnailRequestRecursive(java.lang.Object, com.bumptech.glide.request.target.Target, com.bumptech.glide.request.RequestListener, com.bumptech.glide.request.RequestCoordinator, com.bumptech.glide.TransitionOptions, com.bumptech.glide.Priority, int, int, com.bumptech.glide.request.BaseRequestOptions, java.util.concurrent.Executor):com.bumptech.glide.request.Request");
    }

    private Request obtainRequest(Object requestLock, Target<TranscodeType> target, RequestListener<TranscodeType> targetListener, BaseRequestOptions<?> requestOptions, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions2, Priority priority, int overrideWidth, int overrideHeight, Executor callbackExecutor) {
        Context context2 = this.context;
        GlideContext glideContext2 = this.glideContext;
        return SingleRequest.obtain(context2, glideContext2, requestLock, this.model, this.transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, targetListener, this.requestListeners, requestCoordinator, glideContext2.getEngine(), transitionOptions2.getTransitionFactory(), callbackExecutor);
    }
}
