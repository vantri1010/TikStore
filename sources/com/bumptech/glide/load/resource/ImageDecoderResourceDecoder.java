package com.bumptech.glide.load.resource;

import android.graphics.ColorSpace;
import android.graphics.ImageDecoder;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.PreferredColorSpace;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.HardwareConfigState;
import java.io.IOException;

public abstract class ImageDecoderResourceDecoder<T> implements ResourceDecoder<ImageDecoder.Source, T> {
    private static final String TAG = "ImageDecoder";
    final HardwareConfigState hardwareConfigState = HardwareConfigState.getInstance();

    /* access modifiers changed from: protected */
    public abstract Resource<T> decode(ImageDecoder.Source source, int i, int i2, ImageDecoder.OnHeaderDecodedListener onHeaderDecodedListener) throws IOException;

    public final boolean handles(ImageDecoder.Source source, Options options) {
        return true;
    }

    public final Resource<T> decode(ImageDecoder.Source source, int requestedWidth, int requestedHeight, Options options) throws IOException {
        Options options2 = options;
        DecodeFormat decodeFormat = (DecodeFormat) options2.get(Downsampler.DECODE_FORMAT);
        DownsampleStrategy strategy = (DownsampleStrategy) options2.get(DownsampleStrategy.OPTION);
        final boolean isHardwareConfigAllowed = options2.get(Downsampler.ALLOW_HARDWARE_CONFIG) != null && ((Boolean) options2.get(Downsampler.ALLOW_HARDWARE_CONFIG)).booleanValue();
        final int i = requestedWidth;
        final int i2 = requestedHeight;
        final DecodeFormat decodeFormat2 = decodeFormat;
        final DownsampleStrategy downsampleStrategy = strategy;
        final PreferredColorSpace preferredColorSpace = (PreferredColorSpace) options2.get(Downsampler.PREFERRED_COLOR_SPACE);
        ImageDecoder.Source source2 = source;
        return decode(source, requestedWidth, i2, (ImageDecoder.OnHeaderDecodedListener) new ImageDecoder.OnHeaderDecodedListener() {
            public void onHeaderDecoded(ImageDecoder decoder, ImageDecoder.ImageInfo info, ImageDecoder.Source source) {
                boolean isP3Eligible = false;
                if (ImageDecoderResourceDecoder.this.hardwareConfigState.isHardwareConfigAllowed(i, i2, isHardwareConfigAllowed, false)) {
                    decoder.setAllocator(3);
                } else {
                    decoder.setAllocator(1);
                }
                if (decodeFormat2 == DecodeFormat.PREFER_RGB_565) {
                    decoder.setMemorySizePolicy(0);
                }
                decoder.setOnPartialImageListener(new ImageDecoder.OnPartialImageListener() {
                    public boolean onPartialImage(ImageDecoder.DecodeException e) {
                        return false;
                    }
                });
                Size size = info.getSize();
                int targetWidth = i;
                if (i == Integer.MIN_VALUE) {
                    targetWidth = size.getWidth();
                }
                int targetHeight = i2;
                if (i2 == Integer.MIN_VALUE) {
                    targetHeight = size.getHeight();
                }
                float scaleFactor = downsampleStrategy.getScaleFactor(size.getWidth(), size.getHeight(), targetWidth, targetHeight);
                int resizeWidth = Math.round(((float) size.getWidth()) * scaleFactor);
                int resizeHeight = Math.round(((float) size.getHeight()) * scaleFactor);
                if (Log.isLoggable(ImageDecoderResourceDecoder.TAG, 2)) {
                    Log.v(ImageDecoderResourceDecoder.TAG, "Resizing from [" + size.getWidth() + "x" + size.getHeight() + "] to [" + resizeWidth + "x" + resizeHeight + "] scaleFactor: " + scaleFactor);
                }
                decoder.setTargetSize(resizeWidth, resizeHeight);
                if (Build.VERSION.SDK_INT >= 28) {
                    if (preferredColorSpace == PreferredColorSpace.DISPLAY_P3 && info.getColorSpace() != null && info.getColorSpace().isWideGamut()) {
                        isP3Eligible = true;
                    }
                    decoder.setTargetColorSpace(ColorSpace.get(isP3Eligible ? ColorSpace.Named.DISPLAY_P3 : ColorSpace.Named.SRGB));
                } else if (Build.VERSION.SDK_INT >= 26) {
                    decoder.setTargetColorSpace(ColorSpace.get(ColorSpace.Named.SRGB));
                }
            }
        });
    }
}
