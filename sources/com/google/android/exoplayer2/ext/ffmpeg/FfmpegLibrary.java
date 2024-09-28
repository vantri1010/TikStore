package com.google.android.exoplayer2.ext.ffmpeg;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;

public final class FfmpegLibrary {
    private static native String ffmpegGetVersion();

    private static native boolean ffmpegHasDecoder(String str);

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.ffmpeg");
    }

    private FfmpegLibrary() {
    }

    public static String getVersion() {
        return ffmpegGetVersion();
    }

    public static boolean supportsFormat(String mimeType, int encoding) {
        String codecName = getCodecName(mimeType, encoding);
        return codecName != null && ffmpegHasDecoder(codecName);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.String getCodecName(java.lang.String r2, int r3) {
        /*
            int r0 = r2.hashCode()
            switch(r0) {
                case -2123537834: goto L_0x00b9;
                case -1606874997: goto L_0x00ae;
                case -1095064472: goto L_0x00a3;
                case -1003765268: goto L_0x0098;
                case -432837260: goto L_0x008e;
                case -432837259: goto L_0x0084;
                case -53558318: goto L_0x007a;
                case 187078296: goto L_0x0070;
                case 187094639: goto L_0x0065;
                case 1503095341: goto L_0x005a;
                case 1504470054: goto L_0x004e;
                case 1504578661: goto L_0x0043;
                case 1504619009: goto L_0x0037;
                case 1504831518: goto L_0x002c;
                case 1504891608: goto L_0x0020;
                case 1505942594: goto L_0x0014;
                case 1556697186: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x00c3
        L_0x0009:
            java.lang.String r0 = "audio/true-hd"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 7
            goto L_0x00c4
        L_0x0014:
            java.lang.String r0 = "audio/vnd.dts.hd"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 9
            goto L_0x00c4
        L_0x0020:
            java.lang.String r0 = "audio/opus"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 11
            goto L_0x00c4
        L_0x002c:
            java.lang.String r0 = "audio/mpeg"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x00c4
        L_0x0037:
            java.lang.String r0 = "audio/flac"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 14
            goto L_0x00c4
        L_0x0043:
            java.lang.String r0 = "audio/eac3"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 5
            goto L_0x00c4
        L_0x004e:
            java.lang.String r0 = "audio/alac"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 15
            goto L_0x00c4
        L_0x005a:
            java.lang.String r0 = "audio/3gpp"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 12
            goto L_0x00c4
        L_0x0065:
            java.lang.String r0 = "audio/raw"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 16
            goto L_0x00c4
        L_0x0070:
            java.lang.String r0 = "audio/ac3"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 4
            goto L_0x00c4
        L_0x007a:
            java.lang.String r0 = "audio/mp4a-latm"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x00c4
        L_0x0084:
            java.lang.String r0 = "audio/mpeg-L2"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 3
            goto L_0x00c4
        L_0x008e:
            java.lang.String r0 = "audio/mpeg-L1"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x00c4
        L_0x0098:
            java.lang.String r0 = "audio/vorbis"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 10
            goto L_0x00c4
        L_0x00a3:
            java.lang.String r0 = "audio/vnd.dts"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 8
            goto L_0x00c4
        L_0x00ae:
            java.lang.String r0 = "audio/amr-wb"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 13
            goto L_0x00c4
        L_0x00b9:
            java.lang.String r0 = "audio/eac3-joc"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 6
            goto L_0x00c4
        L_0x00c3:
            r0 = -1
        L_0x00c4:
            r1 = 0
            switch(r0) {
                case 0: goto L_0x00fb;
                case 1: goto L_0x00f8;
                case 2: goto L_0x00f8;
                case 3: goto L_0x00f8;
                case 4: goto L_0x00f5;
                case 5: goto L_0x00f2;
                case 6: goto L_0x00f2;
                case 7: goto L_0x00ee;
                case 8: goto L_0x00eb;
                case 9: goto L_0x00eb;
                case 10: goto L_0x00e7;
                case 11: goto L_0x00e4;
                case 12: goto L_0x00e1;
                case 13: goto L_0x00de;
                case 14: goto L_0x00db;
                case 15: goto L_0x00d8;
                case 16: goto L_0x00c9;
                default: goto L_0x00c8;
            }
        L_0x00c8:
            return r1
        L_0x00c9:
            r0 = 268435456(0x10000000, float:2.5243549E-29)
            if (r3 != r0) goto L_0x00d0
            java.lang.String r0 = "pcm_mulaw"
            return r0
        L_0x00d0:
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            if (r3 != r0) goto L_0x00d7
            java.lang.String r0 = "pcm_alaw"
            return r0
        L_0x00d7:
            return r1
        L_0x00d8:
            java.lang.String r0 = "alac"
            return r0
        L_0x00db:
            java.lang.String r0 = "flac"
            return r0
        L_0x00de:
            java.lang.String r0 = "amrwb"
            return r0
        L_0x00e1:
            java.lang.String r0 = "amrnb"
            return r0
        L_0x00e4:
            java.lang.String r0 = "opus"
            return r0
        L_0x00e7:
            java.lang.String r0 = "vorbis"
            return r0
        L_0x00eb:
            java.lang.String r0 = "dca"
            return r0
        L_0x00ee:
            java.lang.String r0 = "truehd"
            return r0
        L_0x00f2:
            java.lang.String r0 = "eac3"
            return r0
        L_0x00f5:
            java.lang.String r0 = "ac3"
            return r0
        L_0x00f8:
            java.lang.String r0 = "mp3"
            return r0
        L_0x00fb:
            java.lang.String r0 = "aac"
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary.getCodecName(java.lang.String, int):java.lang.String");
    }
}
