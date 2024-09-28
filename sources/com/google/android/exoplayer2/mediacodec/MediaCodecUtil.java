package com.google.android.exoplayer2.mediacodec;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MediaCodecUtil {
    private static final SparseIntArray AVC_LEVEL_NUMBER_TO_CONST;
    private static final SparseIntArray AVC_PROFILE_NUMBER_TO_CONST;
    private static final String CODEC_ID_AVC1 = "avc1";
    private static final String CODEC_ID_AVC2 = "avc2";
    private static final String CODEC_ID_HEV1 = "hev1";
    private static final String CODEC_ID_HVC1 = "hvc1";
    private static final String CODEC_ID_MP4A = "mp4a";
    private static final Map<String, Integer> HEVC_CODEC_STRING_TO_PROFILE_LEVEL;
    private static final SparseIntArray MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE;
    private static final Pattern PROFILE_PATTERN = Pattern.compile("^\\D?(\\d+)$");
    private static final RawAudioCodecComparator RAW_AUDIO_CODEC_COMPARATOR = new RawAudioCodecComparator();
    private static final String TAG = "MediaCodecUtil";
    private static final HashMap<CodecKey, List<MediaCodecInfo>> decoderInfosCache = new HashMap<>();
    private static int maxH264DecodableFrameSize = -1;

    private interface MediaCodecListCompat {
        int getCodecCount();

        MediaCodecInfo getCodecInfoAt(int i);

        boolean isSecurePlaybackSupported(String str, MediaCodecInfo.CodecCapabilities codecCapabilities);

        boolean secureDecodersExplicit();
    }

    public static class DecoderQueryException extends Exception {
        private DecoderQueryException(Throwable cause) {
            super("Failed to query underlying media codecs", cause);
        }
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        AVC_PROFILE_NUMBER_TO_CONST = sparseIntArray;
        sparseIntArray.put(66, 1);
        AVC_PROFILE_NUMBER_TO_CONST.put(77, 2);
        AVC_PROFILE_NUMBER_TO_CONST.put(88, 4);
        AVC_PROFILE_NUMBER_TO_CONST.put(100, 8);
        AVC_PROFILE_NUMBER_TO_CONST.put(110, 16);
        AVC_PROFILE_NUMBER_TO_CONST.put(122, 32);
        AVC_PROFILE_NUMBER_TO_CONST.put(244, 64);
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        AVC_LEVEL_NUMBER_TO_CONST = sparseIntArray2;
        sparseIntArray2.put(10, 1);
        AVC_LEVEL_NUMBER_TO_CONST.put(11, 4);
        AVC_LEVEL_NUMBER_TO_CONST.put(12, 8);
        AVC_LEVEL_NUMBER_TO_CONST.put(13, 16);
        AVC_LEVEL_NUMBER_TO_CONST.put(20, 32);
        AVC_LEVEL_NUMBER_TO_CONST.put(21, 64);
        AVC_LEVEL_NUMBER_TO_CONST.put(22, 128);
        AVC_LEVEL_NUMBER_TO_CONST.put(30, 256);
        AVC_LEVEL_NUMBER_TO_CONST.put(31, 512);
        AVC_LEVEL_NUMBER_TO_CONST.put(32, 1024);
        AVC_LEVEL_NUMBER_TO_CONST.put(40, 2048);
        AVC_LEVEL_NUMBER_TO_CONST.put(41, 4096);
        AVC_LEVEL_NUMBER_TO_CONST.put(42, 8192);
        AVC_LEVEL_NUMBER_TO_CONST.put(50, 16384);
        AVC_LEVEL_NUMBER_TO_CONST.put(51, 32768);
        AVC_LEVEL_NUMBER_TO_CONST.put(52, 65536);
        HashMap hashMap = new HashMap();
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL = hashMap;
        hashMap.put("L30", 1);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L60", 4);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L63", 16);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L90", 64);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L93", 256);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L120", 1024);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L123", 4096);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L150", 16384);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L153", 65536);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L156", 262144);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L180", 1048576);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L183", 4194304);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L186", 16777216);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H30", 2);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H60", 8);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H63", 32);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H90", 128);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H93", 512);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H120", 2048);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H123", 8192);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H150", 32768);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H153", 131072);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H156", 524288);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H180", 2097152);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H183", 8388608);
        HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H186", Integer.valueOf(ConnectionsManager.FileTypeVideo));
        SparseIntArray sparseIntArray3 = new SparseIntArray();
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE = sparseIntArray3;
        sparseIntArray3.put(1, 1);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(2, 2);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(3, 3);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(4, 4);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(5, 5);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(6, 6);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(17, 17);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(20, 20);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(23, 23);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(29, 29);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(39, 39);
        MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.put(42, 42);
    }

    private MediaCodecUtil() {
    }

    public static void warmDecoderInfoCache(String mimeType, boolean secure) {
        try {
            getDecoderInfos(mimeType, secure);
        } catch (DecoderQueryException e) {
            Log.e(TAG, "Codec warming failed", e);
        }
    }

    public static MediaCodecInfo getPassthroughDecoderInfo() throws DecoderQueryException {
        MediaCodecInfo decoderInfo = getDecoderInfo(MimeTypes.AUDIO_RAW, false);
        if (decoderInfo == null) {
            return null;
        }
        return MediaCodecInfo.newPassthroughInstance(decoderInfo.name);
    }

    public static MediaCodecInfo getDecoderInfo(String mimeType, boolean secure) throws DecoderQueryException {
        List<MediaCodecInfo> decoderInfos = getDecoderInfos(mimeType, secure);
        if (decoderInfos.isEmpty()) {
            return null;
        }
        return decoderInfos.get(0);
    }

    public static synchronized List<MediaCodecInfo> getDecoderInfos(String mimeType, boolean secure) throws DecoderQueryException {
        synchronized (MediaCodecUtil.class) {
            CodecKey key = new CodecKey(mimeType, secure);
            List<MediaCodecInfo> cachedDecoderInfos = decoderInfosCache.get(key);
            if (cachedDecoderInfos != null) {
                return cachedDecoderInfos;
            }
            MediaCodecListCompat mediaCodecList = Util.SDK_INT >= 21 ? new MediaCodecListCompatV21(secure) : new MediaCodecListCompatV16();
            ArrayList<MediaCodecInfo> decoderInfos = getDecoderInfosInternal(key, mediaCodecList, mimeType);
            if (secure && decoderInfos.isEmpty() && 21 <= Util.SDK_INT && Util.SDK_INT <= 23) {
                mediaCodecList = new MediaCodecListCompatV16();
                decoderInfos = getDecoderInfosInternal(key, mediaCodecList, mimeType);
                if (!decoderInfos.isEmpty()) {
                    Log.w(TAG, "MediaCodecList API didn't list secure decoder for: " + mimeType + ". Assuming: " + decoderInfos.get(0).name);
                }
            }
            if (MimeTypes.AUDIO_E_AC3_JOC.equals(mimeType)) {
                decoderInfos.addAll(getDecoderInfosInternal(new CodecKey(MimeTypes.AUDIO_E_AC3, key.secure), mediaCodecList, mimeType));
            }
            applyWorkarounds(mimeType, decoderInfos);
            List<MediaCodecInfo> unmodifiableDecoderInfos = Collections.unmodifiableList(decoderInfos);
            decoderInfosCache.put(key, unmodifiableDecoderInfos);
            return unmodifiableDecoderInfos;
        }
    }

    public static int maxH264DecodableFrameSize() throws DecoderQueryException {
        if (maxH264DecodableFrameSize == -1) {
            int result = 0;
            MediaCodecInfo decoderInfo = getDecoderInfo("video/avc", false);
            if (decoderInfo != null) {
                for (MediaCodecInfo.CodecProfileLevel profileLevel : decoderInfo.getProfileLevels()) {
                    result = Math.max(avcLevelToMaxFrameSize(profileLevel.level), result);
                }
                result = Math.max(result, Util.SDK_INT >= 21 ? 345600 : 172800);
            }
            maxH264DecodableFrameSize = result;
        }
        return maxH264DecodableFrameSize;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0034, code lost:
        if (r3.equals("hev1") != false) goto L_0x004c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.util.Pair<java.lang.Integer, java.lang.Integer> getCodecProfileAndLevel(java.lang.String r10) {
        /*
            r0 = 0
            if (r10 != 0) goto L_0x0004
            return r0
        L_0x0004:
            java.lang.String r1 = "\\."
            java.lang.String[] r1 = r10.split(r1)
            r2 = 0
            r3 = r1[r2]
            r4 = -1
            int r5 = r3.hashCode()
            r6 = 4
            r7 = 3
            r8 = 2
            r9 = 1
            switch(r5) {
                case 3006243: goto L_0x0041;
                case 3006244: goto L_0x0037;
                case 3199032: goto L_0x002e;
                case 3214780: goto L_0x0024;
                case 3356560: goto L_0x001a;
                default: goto L_0x0019;
            }
        L_0x0019:
            goto L_0x004b
        L_0x001a:
            java.lang.String r2 = "mp4a"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0019
            r2 = 4
            goto L_0x004c
        L_0x0024:
            java.lang.String r2 = "hvc1"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0019
            r2 = 1
            goto L_0x004c
        L_0x002e:
            java.lang.String r5 = "hev1"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L_0x0019
            goto L_0x004c
        L_0x0037:
            java.lang.String r2 = "avc2"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0019
            r2 = 3
            goto L_0x004c
        L_0x0041:
            java.lang.String r2 = "avc1"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0019
            r2 = 2
            goto L_0x004c
        L_0x004b:
            r2 = -1
        L_0x004c:
            if (r2 == 0) goto L_0x0061
            if (r2 == r9) goto L_0x0061
            if (r2 == r8) goto L_0x005c
            if (r2 == r7) goto L_0x005c
            if (r2 == r6) goto L_0x0057
            return r0
        L_0x0057:
            android.util.Pair r0 = getAacCodecProfileAndLevel(r10, r1)
            return r0
        L_0x005c:
            android.util.Pair r0 = getAvcProfileAndLevel(r10, r1)
            return r0
        L_0x0061:
            android.util.Pair r0 = getHevcProfileAndLevel(r10, r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecUtil.getCodecProfileAndLevel(java.lang.String):android.util.Pair");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: boolean} */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0054, code lost:
        if (r1.secure != r2) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0061, code lost:
        if (r1.secure == false) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0063, code lost:
        r16 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r3.add(com.google.android.exoplayer2.mediacodec.MediaCodecInfo.newInstance(r9, r4, r0, r18, false));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0070, code lost:
        r0 = e;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a6 A[SYNTHETIC, Splitter:B:46:0x00a6] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00c6 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.ArrayList<com.google.android.exoplayer2.mediacodec.MediaCodecInfo> getDecoderInfosInternal(com.google.android.exoplayer2.mediacodec.MediaCodecUtil.CodecKey r19, com.google.android.exoplayer2.mediacodec.MediaCodecUtil.MediaCodecListCompat r20, java.lang.String r21) throws com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException {
        /*
            r1 = r19
            r2 = r20
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0107 }
            r0.<init>()     // Catch:{ Exception -> 0x0107 }
            r3 = r0
            java.lang.String r0 = r1.mimeType     // Catch:{ Exception -> 0x0107 }
            r4 = r0
            int r0 = r20.getCodecCount()     // Catch:{ Exception -> 0x0107 }
            r5 = r0
            boolean r0 = r20.secureDecodersExplicit()     // Catch:{ Exception -> 0x0107 }
            r6 = r0
            r0 = 0
            r7 = r0
        L_0x0019:
            if (r7 >= r5) goto L_0x0106
            android.media.MediaCodecInfo r0 = r2.getCodecInfoAt(r7)     // Catch:{ Exception -> 0x0107 }
            r8 = r0
            java.lang.String r0 = r8.getName()     // Catch:{ Exception -> 0x0107 }
            r9 = r0
            r10 = r21
            boolean r0 = isCodecUsableDecoder(r8, r9, r6, r10)     // Catch:{ Exception -> 0x0104 }
            if (r0 == 0) goto L_0x00f8
            java.lang.String[] r11 = r8.getSupportedTypes()     // Catch:{ Exception -> 0x0104 }
            int r12 = r11.length     // Catch:{ Exception -> 0x0104 }
            r14 = 0
        L_0x0033:
            if (r14 >= r12) goto L_0x00f5
            r0 = r11[r14]     // Catch:{ Exception -> 0x0104 }
            r15 = r0
            boolean r0 = r15.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0104 }
            if (r0 == 0) goto L_0x00e9
            android.media.MediaCodecInfo$CodecCapabilities r0 = r8.getCapabilitiesForType(r15)     // Catch:{ Exception -> 0x009b }
            boolean r16 = r2.isSecurePlaybackSupported(r4, r0)     // Catch:{ Exception -> 0x009b }
            r17 = r16
            boolean r16 = codecNeedsDisableAdaptationWorkaround(r9)     // Catch:{ Exception -> 0x009b }
            r18 = r16
            if (r6 == 0) goto L_0x005b
            boolean r13 = r1.secure     // Catch:{ Exception -> 0x0057 }
            r2 = r17
            if (r13 == r2) goto L_0x0063
            goto L_0x005d
        L_0x0057:
            r0 = move-exception
            r16 = r5
            goto L_0x009e
        L_0x005b:
            r2 = r17
        L_0x005d:
            if (r6 != 0) goto L_0x0077
            boolean r13 = r1.secure     // Catch:{ Exception -> 0x0072 }
            if (r13 != 0) goto L_0x0077
        L_0x0063:
            r16 = r5
            r13 = r18
            r1 = 0
            com.google.android.exoplayer2.mediacodec.MediaCodecInfo r5 = com.google.android.exoplayer2.mediacodec.MediaCodecInfo.newInstance(r9, r4, r0, r13, r1)     // Catch:{ Exception -> 0x0070 }
            r3.add(r5)     // Catch:{ Exception -> 0x0070 }
            goto L_0x009a
        L_0x0070:
            r0 = move-exception
            goto L_0x009e
        L_0x0072:
            r0 = move-exception
            r16 = r5
            r1 = 0
            goto L_0x009e
        L_0x0077:
            r16 = r5
            r13 = r18
            r1 = 0
            if (r6 != 0) goto L_0x009a
            if (r2 == 0) goto L_0x009a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0070 }
            r5.<init>()     // Catch:{ Exception -> 0x0070 }
            r5.append(r9)     // Catch:{ Exception -> 0x0070 }
            java.lang.String r1 = ".secure"
            r5.append(r1)     // Catch:{ Exception -> 0x0070 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0070 }
            r5 = 1
            com.google.android.exoplayer2.mediacodec.MediaCodecInfo r1 = com.google.android.exoplayer2.mediacodec.MediaCodecInfo.newInstance(r1, r4, r0, r13, r5)     // Catch:{ Exception -> 0x0070 }
            r3.add(r1)     // Catch:{ Exception -> 0x0070 }
            return r3
        L_0x009a:
            goto L_0x00eb
        L_0x009b:
            r0 = move-exception
            r16 = r5
        L_0x009e:
            int r1 = com.google.android.exoplayer2.util.Util.SDK_INT     // Catch:{ Exception -> 0x0104 }
            r2 = 23
            java.lang.String r5 = "MediaCodecUtil"
            if (r1 > r2) goto L_0x00c6
            boolean r1 = r3.isEmpty()     // Catch:{ Exception -> 0x0104 }
            if (r1 != 0) goto L_0x00c6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0104 }
            r1.<init>()     // Catch:{ Exception -> 0x0104 }
            java.lang.String r2 = "Skipping codec "
            r1.append(r2)     // Catch:{ Exception -> 0x0104 }
            r1.append(r9)     // Catch:{ Exception -> 0x0104 }
            java.lang.String r2 = " (failed to query capabilities)"
            r1.append(r2)     // Catch:{ Exception -> 0x0104 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0104 }
            com.google.android.exoplayer2.util.Log.e(r5, r1)     // Catch:{ Exception -> 0x0104 }
            goto L_0x00eb
        L_0x00c6:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0104 }
            r1.<init>()     // Catch:{ Exception -> 0x0104 }
            java.lang.String r2 = "Failed to query codec "
            r1.append(r2)     // Catch:{ Exception -> 0x0104 }
            r1.append(r9)     // Catch:{ Exception -> 0x0104 }
            java.lang.String r2 = " ("
            r1.append(r2)     // Catch:{ Exception -> 0x0104 }
            r1.append(r15)     // Catch:{ Exception -> 0x0104 }
            java.lang.String r2 = ")"
            r1.append(r2)     // Catch:{ Exception -> 0x0104 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0104 }
            com.google.android.exoplayer2.util.Log.e(r5, r1)     // Catch:{ Exception -> 0x0104 }
            throw r0     // Catch:{ Exception -> 0x0104 }
        L_0x00e9:
            r16 = r5
        L_0x00eb:
            int r14 = r14 + 1
            r1 = r19
            r2 = r20
            r5 = r16
            goto L_0x0033
        L_0x00f5:
            r16 = r5
            goto L_0x00fa
        L_0x00f8:
            r16 = r5
        L_0x00fa:
            int r7 = r7 + 1
            r1 = r19
            r2 = r20
            r5 = r16
            goto L_0x0019
        L_0x0104:
            r0 = move-exception
            goto L_0x010a
        L_0x0106:
            return r3
        L_0x0107:
            r0 = move-exception
            r10 = r21
        L_0x010a:
            com.google.android.exoplayer2.mediacodec.MediaCodecUtil$DecoderQueryException r1 = new com.google.android.exoplayer2.mediacodec.MediaCodecUtil$DecoderQueryException
            r2 = 0
            r1.<init>(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.mediacodec.MediaCodecUtil.getDecoderInfosInternal(com.google.android.exoplayer2.mediacodec.MediaCodecUtil$CodecKey, com.google.android.exoplayer2.mediacodec.MediaCodecUtil$MediaCodecListCompat, java.lang.String):java.util.ArrayList");
    }

    private static boolean isCodecUsableDecoder(MediaCodecInfo info, String name, boolean secureDecodersExplicit, String requestedMimeType) {
        if (info.isEncoder() || (!secureDecodersExplicit && name.endsWith(".secure"))) {
            return false;
        }
        if (Util.SDK_INT < 21 && ("CIPAACDecoder".equals(name) || "CIPMP3Decoder".equals(name) || "CIPVorbisDecoder".equals(name) || "CIPAMRNBDecoder".equals(name) || "AACDecoder".equals(name) || "MP3Decoder".equals(name))) {
            return false;
        }
        if (Util.SDK_INT < 18 && "OMX.SEC.MP3.Decoder".equals(name)) {
            return false;
        }
        if ("OMX.SEC.mp3.dec".equals(name) && (Util.MODEL.startsWith("GT-I9152") || Util.MODEL.startsWith("GT-I9515") || Util.MODEL.startsWith("GT-P5220") || Util.MODEL.startsWith("GT-S7580") || Util.MODEL.startsWith("SM-G350") || Util.MODEL.startsWith("SM-G386") || Util.MODEL.startsWith("SM-T231") || Util.MODEL.startsWith("SM-T530") || Util.MODEL.startsWith("SCH-I535") || Util.MODEL.startsWith("SPH-L710"))) {
            return false;
        }
        if ("OMX.brcm.audio.mp3.decoder".equals(name) && (Util.MODEL.startsWith("GT-I9152") || Util.MODEL.startsWith("GT-S7580") || Util.MODEL.startsWith("SM-G350"))) {
            return false;
        }
        if (Util.SDK_INT < 18 && "OMX.MTK.AUDIO.DECODER.AAC".equals(name) && ("a70".equals(Util.DEVICE) || ("Xiaomi".equals(Util.MANUFACTURER) && Util.DEVICE.startsWith("HM")))) {
            return false;
        }
        if (Util.SDK_INT == 16 && "OMX.qcom.audio.decoder.mp3".equals(name) && ("dlxu".equals(Util.DEVICE) || "protou".equals(Util.DEVICE) || "ville".equals(Util.DEVICE) || "villeplus".equals(Util.DEVICE) || "villec2".equals(Util.DEVICE) || Util.DEVICE.startsWith("gee") || "C6602".equals(Util.DEVICE) || "C6603".equals(Util.DEVICE) || "C6606".equals(Util.DEVICE) || "C6616".equals(Util.DEVICE) || "L36h".equals(Util.DEVICE) || "SO-02E".equals(Util.DEVICE))) {
            return false;
        }
        if (Util.SDK_INT == 16 && "OMX.qcom.audio.decoder.aac".equals(name) && ("C1504".equals(Util.DEVICE) || "C1505".equals(Util.DEVICE) || "C1604".equals(Util.DEVICE) || "C1605".equals(Util.DEVICE))) {
            return false;
        }
        if (Util.SDK_INT < 24 && (("OMX.SEC.aac.dec".equals(name) || "OMX.Exynos.AAC.Decoder".equals(name)) && "samsung".equals(Util.MANUFACTURER) && (Util.DEVICE.startsWith("zeroflte") || Util.DEVICE.startsWith("zerolte") || Util.DEVICE.startsWith("zenlte") || "SC-05G".equals(Util.DEVICE) || "marinelteatt".equals(Util.DEVICE) || "404SC".equals(Util.DEVICE) || "SC-04G".equals(Util.DEVICE) || "SCV31".equals(Util.DEVICE)))) {
            return false;
        }
        if (Util.SDK_INT <= 19 && "OMX.SEC.vp8.dec".equals(name) && "samsung".equals(Util.MANUFACTURER) && (Util.DEVICE.startsWith("d2") || Util.DEVICE.startsWith("serrano") || Util.DEVICE.startsWith("jflte") || Util.DEVICE.startsWith("santos") || Util.DEVICE.startsWith("t0"))) {
            return false;
        }
        if (Util.SDK_INT <= 19 && Util.DEVICE.startsWith("jflte") && "OMX.qcom.video.decoder.vp8".equals(name)) {
            return false;
        }
        if (!MimeTypes.AUDIO_E_AC3_JOC.equals(requestedMimeType) || !"OMX.MTK.AUDIO.DECODER.DSPAC3".equals(name)) {
            return true;
        }
        return false;
    }

    private static void applyWorkarounds(String mimeType, List<MediaCodecInfo> decoderInfos) {
        if (MimeTypes.AUDIO_RAW.equals(mimeType)) {
            Collections.sort(decoderInfos, RAW_AUDIO_CODEC_COMPARATOR);
        }
    }

    private static boolean codecNeedsDisableAdaptationWorkaround(String name) {
        return Util.SDK_INT <= 22 && ("ODROID-XU3".equals(Util.MODEL) || "Nexus 10".equals(Util.MODEL)) && ("OMX.Exynos.AVC.Decoder".equals(name) || "OMX.Exynos.AVC.Decoder.secure".equals(name));
    }

    private static Pair<Integer, Integer> getHevcProfileAndLevel(String codec, String[] parts) {
        int profile;
        if (parts.length < 4) {
            Log.w(TAG, "Ignoring malformed HEVC codec string: " + codec);
            return null;
        }
        Matcher matcher = PROFILE_PATTERN.matcher(parts[1]);
        if (!matcher.matches()) {
            Log.w(TAG, "Ignoring malformed HEVC codec string: " + codec);
            return null;
        }
        String profileString = matcher.group(1);
        if ("1".equals(profileString)) {
            profile = 1;
        } else if ("2".equals(profileString)) {
            profile = 2;
        } else {
            Log.w(TAG, "Unknown HEVC profile string: " + profileString);
            return null;
        }
        Integer level = HEVC_CODEC_STRING_TO_PROFILE_LEVEL.get(parts[3]);
        if (level != null) {
            return new Pair<>(Integer.valueOf(profile), level);
        }
        Log.w(TAG, "Unknown HEVC level string: " + matcher.group(1));
        return null;
    }

    private static Pair<Integer, Integer> getAvcProfileAndLevel(String codec, String[] parts) {
        Integer profileInteger;
        Integer profileInteger2;
        if (parts.length < 2) {
            Log.w(TAG, "Ignoring malformed AVC codec string: " + codec);
            return null;
        }
        try {
            if (parts[1].length() == 6) {
                profileInteger = Integer.valueOf(Integer.parseInt(parts[1].substring(0, 2), 16));
                profileInteger2 = Integer.valueOf(Integer.parseInt(parts[1].substring(4), 16));
            } else if (parts.length >= 3) {
                profileInteger = Integer.valueOf(Integer.parseInt(parts[1]));
                profileInteger2 = Integer.valueOf(Integer.parseInt(parts[2]));
            } else {
                Log.w(TAG, "Ignoring malformed AVC codec string: " + codec);
                return null;
            }
            int profile = AVC_PROFILE_NUMBER_TO_CONST.get(profileInteger.intValue(), -1);
            if (profile == -1) {
                Log.w(TAG, "Unknown AVC profile: " + profileInteger);
                return null;
            }
            int level = AVC_LEVEL_NUMBER_TO_CONST.get(profileInteger2.intValue(), -1);
            if (level != -1) {
                return new Pair<>(Integer.valueOf(profile), Integer.valueOf(level));
            }
            Log.w(TAG, "Unknown AVC level: " + profileInteger2);
            return null;
        } catch (NumberFormatException e) {
            Log.w(TAG, "Ignoring malformed AVC codec string: " + codec);
            return null;
        }
    }

    private static int avcLevelToMaxFrameSize(int avcLevel) {
        if (avcLevel == 1 || avcLevel == 2) {
            return 25344;
        }
        switch (avcLevel) {
            case 8:
            case 16:
            case 32:
                return 101376;
            case 64:
                return 202752;
            case 128:
            case 256:
                return 414720;
            case 512:
                return 921600;
            case 1024:
                return 1310720;
            case 2048:
            case 4096:
                return 2097152;
            case 8192:
                return 2228224;
            case 16384:
                return 5652480;
            case 32768:
            case 65536:
                return 9437184;
            default:
                return -1;
        }
    }

    private static Pair<Integer, Integer> getAacCodecProfileAndLevel(String codec, String[] parts) {
        int profile;
        if (parts.length != 3) {
            Log.w(TAG, "Ignoring malformed MP4A codec string: " + codec);
            return null;
        }
        try {
            if (MimeTypes.AUDIO_AAC.equals(MimeTypes.getMimeTypeFromMp4ObjectType(Integer.parseInt(parts[1], 16))) && (profile = MP4A_AUDIO_OBJECT_TYPE_TO_PROFILE.get(Integer.parseInt(parts[2]), -1)) != -1) {
                return new Pair<>(Integer.valueOf(profile), 0);
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "Ignoring malformed MP4A codec string: " + codec);
        }
        return null;
    }

    private static final class MediaCodecListCompatV21 implements MediaCodecListCompat {
        private final int codecKind;
        private MediaCodecInfo[] mediaCodecInfos;

        public MediaCodecListCompatV21(boolean includeSecure) {
            this.codecKind = includeSecure;
        }

        public int getCodecCount() {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos.length;
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            ensureMediaCodecInfosInitialized();
            return this.mediaCodecInfos[index];
        }

        public boolean secureDecodersExplicit() {
            return true;
        }

        public boolean isSecurePlaybackSupported(String mimeType, MediaCodecInfo.CodecCapabilities capabilities) {
            return capabilities.isFeatureSupported("secure-playback");
        }

        private void ensureMediaCodecInfosInitialized() {
            if (this.mediaCodecInfos == null) {
                this.mediaCodecInfos = new MediaCodecList(this.codecKind).getCodecInfos();
            }
        }
    }

    private static final class MediaCodecListCompatV16 implements MediaCodecListCompat {
        private MediaCodecListCompatV16() {
        }

        public int getCodecCount() {
            return MediaCodecList.getCodecCount();
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            return MediaCodecList.getCodecInfoAt(index);
        }

        public boolean secureDecodersExplicit() {
            return false;
        }

        public boolean isSecurePlaybackSupported(String mimeType, MediaCodecInfo.CodecCapabilities capabilities) {
            return "video/avc".equals(mimeType);
        }
    }

    private static final class CodecKey {
        public final String mimeType;
        public final boolean secure;

        public CodecKey(String mimeType2, boolean secure2) {
            this.mimeType = mimeType2;
            this.secure = secure2;
        }

        public int hashCode() {
            int i = 1 * 31;
            String str = this.mimeType;
            return ((i + (str == null ? 0 : str.hashCode())) * 31) + (this.secure ? 1231 : 1237);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != CodecKey.class) {
                return false;
            }
            CodecKey other = (CodecKey) obj;
            if (!TextUtils.equals(this.mimeType, other.mimeType) || this.secure != other.secure) {
                return false;
            }
            return true;
        }
    }

    private static final class RawAudioCodecComparator implements Comparator<MediaCodecInfo> {
        private RawAudioCodecComparator() {
        }

        public int compare(MediaCodecInfo a, MediaCodecInfo b) {
            return scoreMediaCodecInfo(a) - scoreMediaCodecInfo(b);
        }

        private static int scoreMediaCodecInfo(MediaCodecInfo mediaCodecInfo) {
            String name = mediaCodecInfo.name;
            if (name.startsWith("OMX.google") || name.startsWith("c2.android")) {
                return -1;
            }
            if (Util.SDK_INT >= 26 || !name.equals("OMX.MTK.AUDIO.DECODER.RAW")) {
                return 0;
            }
            return 1;
        }
    }
}
