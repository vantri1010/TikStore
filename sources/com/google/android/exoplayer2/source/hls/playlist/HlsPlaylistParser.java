package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.util.Base64;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.king.zxing.util.LogUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HlsPlaylistParser implements ParsingLoadable.Parser<HlsPlaylist> {
    private static final String ATTR_CLOSED_CAPTIONS_NONE = "CLOSED-CAPTIONS=NONE";
    private static final String BOOLEAN_FALSE = "NO";
    private static final String BOOLEAN_TRUE = "YES";
    private static final String KEYFORMAT_IDENTITY = "identity";
    private static final String KEYFORMAT_PLAYREADY = "com.microsoft.playready";
    private static final String KEYFORMAT_WIDEVINE_PSSH_BINARY = "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed";
    private static final String KEYFORMAT_WIDEVINE_PSSH_JSON = "com.widevine";
    private static final String METHOD_AES_128 = "AES-128";
    private static final String METHOD_NONE = "NONE";
    private static final String METHOD_SAMPLE_AES = "SAMPLE-AES";
    private static final String METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC";
    private static final String METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR";
    private static final String PLAYLIST_HEADER = "#EXTM3U";
    private static final Pattern REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    private static final Pattern REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    private static final Pattern REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    private static final Pattern REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    private static final Pattern REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    private static final Pattern REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    private static final Pattern REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    private static final Pattern REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    private static final Pattern REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    private static final Pattern REGEX_IMPORT = Pattern.compile("IMPORT=\"(.+?)\"");
    private static final Pattern REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    private static final Pattern REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    private static final Pattern REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    private static final Pattern REGEX_KEYFORMATVERSIONS = Pattern.compile("KEYFORMATVERSIONS=\"(.+?)\"");
    private static final Pattern REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    private static final Pattern REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    private static final Pattern REGEX_MEDIA_TITLE = Pattern.compile("#EXTINF:[\\d\\.]+\\b,(.+)");
    private static final Pattern REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)\\s*(?:,|$)");
    private static final Pattern REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    private static final Pattern REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    private static final Pattern REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    private static final Pattern REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    private static final Pattern REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    private static final Pattern REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    private static final Pattern REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    private static final Pattern REGEX_VALUE = Pattern.compile("VALUE=\"(.+?)\"");
    private static final Pattern REGEX_VARIABLE_REFERENCE = Pattern.compile("\\{\\$([a-zA-Z0-9\\-_]+)\\}");
    private static final Pattern REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
    private static final String TAG_DEFINE = "#EXT-X-DEFINE";
    private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
    private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
    private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
    private static final String TAG_GAP = "#EXT-X-GAP";
    private static final String TAG_INDEPENDENT_SEGMENTS = "#EXT-X-INDEPENDENT-SEGMENTS";
    private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
    private static final String TAG_KEY = "#EXT-X-KEY";
    private static final String TAG_MEDIA = "#EXT-X-MEDIA";
    private static final String TAG_MEDIA_DURATION = "#EXTINF";
    private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
    private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
    private static final String TAG_PREFIX = "#EXT";
    private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
    private static final String TAG_START = "#EXT-X-START";
    private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
    private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
    private static final String TAG_VERSION = "#EXT-X-VERSION";
    private static final String TYPE_AUDIO = "AUDIO";
    private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
    private static final String TYPE_SUBTITLES = "SUBTITLES";
    private static final String TYPE_VIDEO = "VIDEO";
    private final HlsMasterPlaylist masterPlaylist;

    public HlsPlaylistParser() {
        this(HlsMasterPlaylist.EMPTY);
    }

    public HlsPlaylistParser(HlsMasterPlaylist masterPlaylist2) {
        this.masterPlaylist = masterPlaylist2;
    }

    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Queue<String> extraLines = new ArrayDeque<>();
        try {
            if (checkPlaylistHeader(reader)) {
                while (true) {
                    String readLine = reader.readLine();
                    String line2 = readLine;
                    if (readLine != null) {
                        line = line2.trim();
                        if (!line.isEmpty()) {
                            if (!line.startsWith(TAG_STREAM_INF)) {
                                if (line.startsWith(TAG_TARGET_DURATION) || line.startsWith(TAG_MEDIA_SEQUENCE) || line.startsWith(TAG_MEDIA_DURATION) || line.startsWith(TAG_KEY) || line.startsWith(TAG_BYTERANGE) || line.equals(TAG_DISCONTINUITY) || line.equals(TAG_DISCONTINUITY_SEQUENCE)) {
                                    break;
                                } else if (line.equals(TAG_ENDLIST)) {
                                    break;
                                } else {
                                    extraLines.add(line);
                                }
                            } else {
                                extraLines.add(line);
                                HlsMasterPlaylist parseMasterPlaylist = parseMasterPlaylist(new LineIterator(extraLines, reader), uri.toString());
                                Util.closeQuietly((Closeable) reader);
                                return parseMasterPlaylist;
                            }
                        }
                    } else {
                        Util.closeQuietly((Closeable) reader);
                        throw new ParserException("Failed to parse the playlist, could not identify any tags.");
                    }
                }
                extraLines.add(line);
                return parseMediaPlaylist(this.masterPlaylist, new LineIterator(extraLines, reader), uri.toString());
            }
            throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", uri);
        } finally {
            Util.closeQuietly((Closeable) reader);
        }
    }

    private static boolean checkPlaylistHeader(BufferedReader reader) throws IOException {
        int last = reader.read();
        if (last == 239) {
            if (reader.read() != 187 || reader.read() != 191) {
                return false;
            }
            last = reader.read();
        }
        int last2 = skipIgnorableWhitespace(reader, true, last);
        int playlistHeaderLength = PLAYLIST_HEADER.length();
        for (int i = 0; i < playlistHeaderLength; i++) {
            if (last2 != PLAYLIST_HEADER.charAt(i)) {
                return false;
            }
            last2 = reader.read();
        }
        return Util.isLinebreak(skipIgnorableWhitespace(reader, false, last2));
    }

    private static int skipIgnorableWhitespace(BufferedReader reader, boolean skipLinebreaks, int c) throws IOException {
        while (c != -1 && Character.isWhitespace(c) && (skipLinebreaks || !Util.isLinebreak(c))) {
            c = reader.read();
        }
        return c;
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x01ef  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0261  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist parseMasterPlaylist(com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser.LineIterator r46, java.lang.String r47) throws java.io.IOException {
        /*
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r13 = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r14 = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r15 = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r12 = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r11 = r3
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r16 = r5
            r17 = r6
        L_0x0035:
            boolean r5 = r46.hasNext()
            if (r5 == 0) goto L_0x0164
            java.lang.String r5 = r46.next()
            java.lang.String r8 = "#EXT"
            boolean r8 = r5.startsWith(r8)
            if (r8 == 0) goto L_0x004a
            r11.add(r5)
        L_0x004a:
            java.lang.String r8 = "#EXT-X-DEFINE"
            boolean r8 = r5.startsWith(r8)
            if (r8 == 0) goto L_0x0069
            java.util.regex.Pattern r6 = REGEX_NAME
            java.lang.String r6 = parseStringAttr(r5, r6, r2)
            java.util.regex.Pattern r7 = REGEX_VALUE
            java.lang.String r7 = parseStringAttr(r5, r7, r2)
            r2.put(r6, r7)
            r20 = r0
            r30 = r3
            r33 = r4
            goto L_0x015c
        L_0x0069:
            java.lang.String r8 = "#EXT-X-INDEPENDENT-SEGMENTS"
            boolean r8 = r5.equals(r8)
            if (r8 == 0) goto L_0x0074
            r17 = 1
            goto L_0x0035
        L_0x0074:
            java.lang.String r8 = "#EXT-X-MEDIA"
            boolean r8 = r5.startsWith(r8)
            if (r8 == 0) goto L_0x0087
            r12.add(r5)
            r20 = r0
            r30 = r3
            r33 = r4
            goto L_0x015c
        L_0x0087:
            java.lang.String r8 = "#EXT-X-STREAM-INF"
            boolean r8 = r5.startsWith(r8)
            if (r8 == 0) goto L_0x0156
            java.lang.String r8 = "CLOSED-CAPTIONS=NONE"
            boolean r8 = r5.contains(r8)
            r16 = r16 | r8
            java.util.regex.Pattern r8 = REGEX_BANDWIDTH
            int r8 = parseIntAttr(r5, r8)
            java.util.regex.Pattern r9 = REGEX_AVERAGE_BANDWIDTH
            java.lang.String r9 = parseOptionalStringAttr(r5, r9, r2)
            if (r9 == 0) goto L_0x00a9
            int r8 = java.lang.Integer.parseInt(r9)
        L_0x00a9:
            java.util.regex.Pattern r10 = REGEX_CODECS
            java.lang.String r10 = parseOptionalStringAttr(r5, r10, r2)
            java.util.regex.Pattern r7 = REGEX_RESOLUTION
            java.lang.String r7 = parseOptionalStringAttr(r5, r7, r2)
            if (r7 == 0) goto L_0x00db
            java.lang.String r6 = "x"
            java.lang.String[] r6 = r7.split(r6)
            r19 = 0
            r19 = r6[r19]
            int r19 = java.lang.Integer.parseInt(r19)
            r18 = 1
            r20 = r6[r18]
            int r20 = java.lang.Integer.parseInt(r20)
            if (r19 <= 0) goto L_0x00d2
            if (r20 > 0) goto L_0x00d6
        L_0x00d2:
            r19 = -1
            r20 = -1
        L_0x00d6:
            r6 = r19
            r29 = r20
            goto L_0x00e3
        L_0x00db:
            r19 = -1
            r20 = -1
            r6 = r19
            r29 = r20
        L_0x00e3:
            r19 = -1082130432(0xffffffffbf800000, float:-1.0)
            r30 = r3
            java.util.regex.Pattern r3 = REGEX_FRAME_RATE
            java.lang.String r3 = parseOptionalStringAttr(r5, r3, r2)
            if (r3 == 0) goto L_0x00f6
            float r19 = java.lang.Float.parseFloat(r3)
            r31 = r19
            goto L_0x00f8
        L_0x00f6:
            r31 = r19
        L_0x00f8:
            r32 = r3
            java.util.regex.Pattern r3 = REGEX_AUDIO
            java.lang.String r3 = parseOptionalStringAttr(r5, r3, r2)
            if (r3 == 0) goto L_0x010f
            if (r10 == 0) goto L_0x010f
            r33 = r4
            r4 = 1
            java.lang.String r4 = com.google.android.exoplayer2.util.Util.getCodecsOfType(r10, r4)
            r1.put(r3, r4)
            goto L_0x0111
        L_0x010f:
            r33 = r4
        L_0x0111:
            java.lang.String r4 = r46.next()
            java.lang.String r4 = replaceVariableReferences(r4, r2)
            boolean r5 = r0.add(r4)
            if (r5 == 0) goto L_0x014c
            int r5 = r13.size()
            java.lang.String r18 = java.lang.Integer.toString(r5)
            r19 = 0
            r21 = 0
            r27 = 0
            r28 = 0
            java.lang.String r20 = "application/x-mpegURL"
            r22 = r10
            r23 = r8
            r24 = r6
            r25 = r29
            r26 = r31
            com.google.android.exoplayer2.Format r5 = com.google.android.exoplayer2.Format.createVideoContainerFormat(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)
            r20 = r0
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r0 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl
            r0.<init>(r4, r5)
            r13.add(r0)
            goto L_0x014e
        L_0x014c:
            r20 = r0
        L_0x014e:
            r0 = r20
            r3 = r30
            r4 = r33
            goto L_0x0035
        L_0x0156:
            r20 = r0
            r30 = r3
            r33 = r4
        L_0x015c:
            r0 = r20
            r3 = r30
            r4 = r33
            goto L_0x0035
        L_0x0164:
            r20 = r0
            r30 = r3
            r33 = r4
            r19 = 0
            r0 = 0
        L_0x016d:
            int r3 = r12.size()
            if (r0 >= r3) goto L_0x02a8
            java.lang.Object r3 = r12.get(r0)
            java.lang.String r3 = (java.lang.String) r3
            int r5 = parseSelectionFlags(r3)
            java.util.regex.Pattern r6 = REGEX_URI
            java.lang.String r6 = parseOptionalStringAttr(r3, r6, r2)
            java.util.regex.Pattern r7 = REGEX_NAME
            java.lang.String r7 = parseStringAttr(r3, r7, r2)
            java.util.regex.Pattern r8 = REGEX_LANGUAGE
            java.lang.String r8 = parseOptionalStringAttr(r3, r8, r2)
            java.util.regex.Pattern r9 = REGEX_GROUP_ID
            java.lang.String r9 = parseOptionalStringAttr(r3, r9, r2)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r9)
            r42 = r12
            java.lang.String r12 = ":"
            r10.append(r12)
            r10.append(r7)
            java.lang.String r10 = r10.toString()
            java.util.regex.Pattern r12 = REGEX_TYPE
            java.lang.String r12 = parseStringAttr(r3, r12, r2)
            r21 = -1
            r43 = r11
            int r11 = r12.hashCode()
            r44 = r0
            r0 = -959297733(0xffffffffc6d2473b, float:-26915.615)
            r45 = r14
            r14 = 2
            if (r11 == r0) goto L_0x01e2
            r0 = -333210994(0xffffffffec239a8e, float:-7.911391E26)
            if (r11 == r0) goto L_0x01d8
            r0 = 62628790(0x3bba3b6, float:1.1028458E-36)
            if (r11 == r0) goto L_0x01ce
        L_0x01cd:
            goto L_0x01ec
        L_0x01ce:
            java.lang.String r0 = "AUDIO"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x01cd
            r0 = 0
            goto L_0x01ed
        L_0x01d8:
            java.lang.String r0 = "CLOSED-CAPTIONS"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x01cd
            r0 = 2
            goto L_0x01ed
        L_0x01e2:
            java.lang.String r0 = "SUBTITLES"
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto L_0x01cd
            r0 = 1
            goto L_0x01ed
        L_0x01ec:
            r0 = -1
        L_0x01ed:
            if (r0 == 0) goto L_0x0261
            r11 = 1
            if (r0 == r11) goto L_0x0241
            if (r0 == r14) goto L_0x01f8
            r14 = r45
            goto L_0x02a0
        L_0x01f8:
            java.util.regex.Pattern r0 = REGEX_INSTREAM_ID
            java.lang.String r0 = parseStringAttr(r3, r0, r2)
            java.lang.String r12 = "CC"
            boolean r12 = r0.startsWith(r12)
            if (r12 == 0) goto L_0x0211
            java.lang.String r12 = "application/cea-608"
            java.lang.String r14 = r0.substring(r14)
            int r14 = java.lang.Integer.parseInt(r14)
            goto L_0x021c
        L_0x0211:
            java.lang.String r12 = "application/cea-708"
            r14 = 7
            java.lang.String r14 = r0.substring(r14)
            int r14 = java.lang.Integer.parseInt(r14)
        L_0x021c:
            if (r4 != 0) goto L_0x0225
            java.util.ArrayList r18 = new java.util.ArrayList
            r18.<init>()
            r4 = r18
        L_0x0225:
            r23 = 0
            r25 = 0
            r26 = -1
            r21 = r10
            r22 = r7
            r24 = r12
            r27 = r5
            r28 = r8
            r29 = r14
            com.google.android.exoplayer2.Format r11 = com.google.android.exoplayer2.Format.createTextContainerFormat(r21, r22, r23, r24, r25, r26, r27, r28, r29)
            r4.add(r11)
            r14 = r45
            goto L_0x02a0
        L_0x0241:
            r25 = 0
            r26 = -1
            java.lang.String r23 = "application/x-mpegURL"
            java.lang.String r24 = "text/vtt"
            r21 = r10
            r22 = r7
            r27 = r5
            r28 = r8
            com.google.android.exoplayer2.Format r0 = com.google.android.exoplayer2.Format.createTextContainerFormat(r21, r22, r23, r24, r25, r26, r27, r28)
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r11 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl
            r11.<init>(r6, r0)
            r15.add(r11)
            r14 = r45
            goto L_0x02a0
        L_0x0261:
            java.lang.Object r0 = r1.get(r9)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x026e
            java.lang.String r11 = com.google.android.exoplayer2.util.MimeTypes.getMediaMimeType(r0)
            goto L_0x026f
        L_0x026e:
            r11 = 0
        L_0x026f:
            r34 = r11
            r36 = -1
            r37 = -1
            r38 = -1
            r39 = 0
            java.lang.String r33 = "application/x-mpegURL"
            r31 = r10
            r32 = r7
            r35 = r0
            r40 = r5
            r41 = r8
            com.google.android.exoplayer2.Format r11 = com.google.android.exoplayer2.Format.createAudioContainerFormat(r31, r32, r33, r34, r35, r36, r37, r38, r39, r40, r41)
            boolean r12 = isMediaTagMuxed(r13, r6)
            if (r12 == 0) goto L_0x0295
            r12 = r11
            r30 = r12
            r14 = r45
            goto L_0x02a0
        L_0x0295:
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r12 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl
            r12.<init>(r6, r11)
            r14 = r45
            r14.add(r12)
        L_0x02a0:
            int r0 = r44 + 1
            r12 = r42
            r11 = r43
            goto L_0x016d
        L_0x02a8:
            r44 = r0
            r43 = r11
            r42 = r12
            if (r16 == 0) goto L_0x02b6
            java.util.List r4 = java.util.Collections.emptyList()
            r0 = r4
            goto L_0x02b7
        L_0x02b6:
            r0 = r4
        L_0x02b7:
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist r18 = new com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist
            r3 = r18
            r4 = r47
            r5 = r43
            r6 = r13
            r7 = r14
            r8 = r15
            r9 = r30
            r10 = r0
            r19 = r43
            r11 = r17
            r21 = r42
            r12 = r2
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return r18
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser.parseMasterPlaylist(com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser$LineIterator, java.lang.String):com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist");
    }

    private static int parseSelectionFlags(String line) {
        int flags = 0;
        if (parseOptionalBooleanAttribute(line, REGEX_DEFAULT, false)) {
            flags = 0 | 1;
        }
        if (parseOptionalBooleanAttribute(line, REGEX_FORCED, false)) {
            flags |= 2;
        }
        if (parseOptionalBooleanAttribute(line, REGEX_AUTOSELECT, false)) {
            return flags | 4;
        }
        return flags;
    }

    private static HlsMediaPlaylist parseMediaPlaylist(HlsMasterPlaylist masterPlaylist2, LineIterator iterator, String baseUri) throws IOException {
        boolean hasEndTag;
        String segmentEncryptionIV;
        DrmInitData cachedDrmInitData;
        TreeMap<String, DrmInitData.SchemeData> currentSchemeDatas;
        DrmInitData.SchemeData schemeData;
        HlsMasterPlaylist hlsMasterPlaylist = masterPlaylist2;
        int playlistType = 0;
        long startOffsetUs = C.TIME_UNSET;
        long mediaSequence = 0;
        int version = 1;
        long targetDurationUs = C.TIME_UNSET;
        boolean hasIndependentSegmentsTag = hlsMasterPlaylist.hasIndependentSegments;
        boolean hasEndTag2 = false;
        HlsMediaPlaylist.Segment initializationSegment = null;
        HashMap<String, String> variableDefinitions = new HashMap<>();
        List<HlsMediaPlaylist.Segment> segments = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        TreeMap<String, DrmInitData.SchemeData> currentSchemeDatas2 = new TreeMap<>();
        int playlistDiscontinuitySequence = 0;
        int relativeDiscontinuitySequence = 0;
        long playlistStartTimeUs = 0;
        long segmentStartTimeUs = 0;
        long segmentByteRangeOffset = 0;
        long segmentByteRangeLength = -1;
        long segmentMediaSequence = 0;
        boolean hasGapTag = false;
        DrmInitData playlistProtectionSchemes = null;
        String encryptionKeyUri = null;
        String encryptionIV = null;
        DrmInitData cachedDrmInitData2 = null;
        String segmentTitle = "";
        long segmentDurationUs = 0;
        String encryptionScheme = null;
        boolean hasDiscontinuitySequence = false;
        while (true) {
            hasEndTag = hasEndTag2;
            if (!iterator.hasNext()) {
                break;
            }
            String line = iterator.next();
            boolean hasIndependentSegmentsTag2 = hasIndependentSegmentsTag;
            if (line.startsWith(TAG_PREFIX)) {
                tags.add(line);
            }
            if (line.startsWith(TAG_PLAYLIST_TYPE)) {
                String playlistTypeString = parseStringAttr(line, REGEX_PLAYLIST_TYPE, variableDefinitions);
                List<String> tags2 = tags;
                if ("VOD".equals(playlistTypeString)) {
                    playlistType = 1;
                } else if ("EVENT".equals(playlistTypeString)) {
                    playlistType = 2;
                }
                tags = tags2;
                hasEndTag2 = hasEndTag;
                hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
            } else {
                List<String> tags3 = tags;
                if (line.startsWith(TAG_START)) {
                    startOffsetUs = (long) (parseDoubleAttr(line, REGEX_TIME_OFFSET) * 1000000.0d);
                    tags = tags3;
                    hasEndTag2 = hasEndTag;
                    hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                    targetDurationUs = targetDurationUs;
                } else {
                    long targetDurationUs2 = targetDurationUs;
                    if (line.startsWith(TAG_INIT_SEGMENT)) {
                        String uri = parseStringAttr(line, REGEX_URI, variableDefinitions);
                        String byteRange = parseOptionalStringAttr(line, REGEX_ATTR_BYTERANGE, variableDefinitions);
                        if (byteRange != null) {
                            String[] splitByteRange = byteRange.split("@");
                            segmentByteRangeLength = Long.parseLong(splitByteRange[0]);
                            String str = byteRange;
                            if (splitByteRange.length > 1) {
                                segmentByteRangeOffset = Long.parseLong(splitByteRange[1]);
                            }
                        }
                        initializationSegment = new HlsMediaPlaylist.Segment(uri, segmentByteRangeOffset, segmentByteRangeLength);
                        segmentByteRangeOffset = 0;
                        segmentByteRangeLength = -1;
                        tags = tags3;
                        hasEndTag2 = hasEndTag;
                        hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                        targetDurationUs = targetDurationUs2;
                    } else if (line.startsWith(TAG_TARGET_DURATION)) {
                        targetDurationUs = ((long) parseIntAttr(line, REGEX_TARGET_DURATION)) * 1000000;
                        tags = tags3;
                        hasEndTag2 = hasEndTag;
                        hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                    } else if (line.startsWith(TAG_MEDIA_SEQUENCE)) {
                        mediaSequence = parseLongAttr(line, REGEX_MEDIA_SEQUENCE);
                        segmentMediaSequence = mediaSequence;
                        tags = tags3;
                        hasEndTag2 = hasEndTag;
                        hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                        targetDurationUs = targetDurationUs2;
                    } else if (line.startsWith(TAG_VERSION)) {
                        version = parseIntAttr(line, REGEX_VERSION);
                        tags = tags3;
                        hasEndTag2 = hasEndTag;
                        hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                        targetDurationUs = targetDurationUs2;
                    } else {
                        if (line.startsWith(TAG_DEFINE)) {
                            String importName = parseOptionalStringAttr(line, REGEX_IMPORT, variableDefinitions);
                            if (importName != null) {
                                String value = hlsMasterPlaylist.variableDefinitions.get(importName);
                                if (value != null) {
                                    variableDefinitions.put(importName, value);
                                }
                            } else {
                                variableDefinitions.put(parseStringAttr(line, REGEX_NAME, variableDefinitions), parseStringAttr(line, REGEX_VALUE, variableDefinitions));
                            }
                        } else if (line.startsWith(TAG_MEDIA_DURATION)) {
                            segmentTitle = parseOptionalStringAttr(line, REGEX_MEDIA_TITLE, "", variableDefinitions);
                            segmentDurationUs = (long) (parseDoubleAttr(line, REGEX_MEDIA_DURATION) * 1000000.0d);
                            tags = tags3;
                            hasEndTag2 = hasEndTag;
                            hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                            targetDurationUs = targetDurationUs2;
                        } else if (line.startsWith(TAG_KEY)) {
                            String method = parseStringAttr(line, REGEX_METHOD, variableDefinitions);
                            String keyFormat = parseOptionalStringAttr(line, REGEX_KEYFORMAT, KEYFORMAT_IDENTITY, variableDefinitions);
                            if (METHOD_NONE.equals(method)) {
                                currentSchemeDatas2.clear();
                                cachedDrmInitData2 = null;
                                encryptionKeyUri = null;
                                encryptionIV = null;
                                currentSchemeDatas = currentSchemeDatas2;
                            } else {
                                String encryptionIV2 = parseOptionalStringAttr(line, REGEX_IV, variableDefinitions);
                                if (!KEYFORMAT_IDENTITY.equals(keyFormat)) {
                                    if (encryptionScheme == null) {
                                        encryptionScheme = (METHOD_SAMPLE_AES_CENC.equals(method) || METHOD_SAMPLE_AES_CTR.equals(method)) ? C.CENC_TYPE_cenc : C.CENC_TYPE_cbcs;
                                    }
                                    if (KEYFORMAT_PLAYREADY.equals(keyFormat)) {
                                        schemeData = parsePlayReadySchemeData(line, variableDefinitions);
                                    } else {
                                        schemeData = parseWidevineSchemeData(line, keyFormat, variableDefinitions);
                                    }
                                    if (schemeData != null) {
                                        String encryptionIV3 = encryptionIV2;
                                        currentSchemeDatas = currentSchemeDatas2;
                                        currentSchemeDatas.put(keyFormat, schemeData);
                                        encryptionKeyUri = null;
                                        cachedDrmInitData2 = null;
                                        encryptionIV = encryptionIV3;
                                    } else {
                                        String encryptionIV4 = encryptionIV2;
                                        currentSchemeDatas = currentSchemeDatas2;
                                        encryptionKeyUri = null;
                                        encryptionIV = encryptionIV4;
                                    }
                                } else if (METHOD_AES_128.equals(method)) {
                                    encryptionIV = encryptionIV2;
                                    encryptionKeyUri = parseStringAttr(line, REGEX_URI, variableDefinitions);
                                    currentSchemeDatas = currentSchemeDatas2;
                                } else {
                                    encryptionIV = encryptionIV2;
                                    encryptionKeyUri = null;
                                    currentSchemeDatas = currentSchemeDatas2;
                                }
                            }
                            currentSchemeDatas2 = currentSchemeDatas;
                            tags = tags3;
                            hasEndTag2 = hasEndTag;
                            hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                            targetDurationUs = targetDurationUs2;
                            hlsMasterPlaylist = masterPlaylist2;
                        } else {
                            TreeMap<String, DrmInitData.SchemeData> currentSchemeDatas3 = currentSchemeDatas2;
                            if (line.startsWith(TAG_BYTERANGE)) {
                                String[] splitByteRange2 = parseStringAttr(line, REGEX_BYTERANGE, variableDefinitions).split("@");
                                segmentByteRangeLength = Long.parseLong(splitByteRange2[0]);
                                if (splitByteRange2.length > 1) {
                                    segmentByteRangeOffset = Long.parseLong(splitByteRange2[1]);
                                }
                                currentSchemeDatas2 = currentSchemeDatas3;
                                tags = tags3;
                                hasEndTag2 = hasEndTag;
                                hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                                targetDurationUs = targetDurationUs2;
                                hlsMasterPlaylist = masterPlaylist2;
                            } else if (line.startsWith(TAG_DISCONTINUITY_SEQUENCE)) {
                                hasDiscontinuitySequence = true;
                                playlistDiscontinuitySequence = Integer.parseInt(line.substring(line.indexOf(58) + 1));
                                currentSchemeDatas2 = currentSchemeDatas3;
                                tags = tags3;
                                hasEndTag2 = hasEndTag;
                                hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                                targetDurationUs = targetDurationUs2;
                                hlsMasterPlaylist = masterPlaylist2;
                            } else if (line.equals(TAG_DISCONTINUITY)) {
                                relativeDiscontinuitySequence++;
                                currentSchemeDatas2 = currentSchemeDatas3;
                                tags = tags3;
                                hasEndTag2 = hasEndTag;
                                hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                                targetDurationUs = targetDurationUs2;
                                hlsMasterPlaylist = masterPlaylist2;
                            } else if (line.startsWith(TAG_PROGRAM_DATE_TIME)) {
                                if (playlistStartTimeUs == 0) {
                                    playlistStartTimeUs = C.msToUs(Util.parseXsDateTime(line.substring(line.indexOf(58) + 1))) - segmentStartTimeUs;
                                    currentSchemeDatas2 = currentSchemeDatas3;
                                    tags = tags3;
                                    hasEndTag2 = hasEndTag;
                                    hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                                    targetDurationUs = targetDurationUs2;
                                    hlsMasterPlaylist = masterPlaylist2;
                                } else {
                                    currentSchemeDatas2 = currentSchemeDatas3;
                                }
                            } else if (line.equals(TAG_GAP)) {
                                hasGapTag = true;
                                currentSchemeDatas2 = currentSchemeDatas3;
                                tags = tags3;
                                hasEndTag2 = hasEndTag;
                                hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                                targetDurationUs = targetDurationUs2;
                                hlsMasterPlaylist = masterPlaylist2;
                            } else if (line.equals(TAG_INDEPENDENT_SEGMENTS)) {
                                hasIndependentSegmentsTag = true;
                                currentSchemeDatas2 = currentSchemeDatas3;
                                tags = tags3;
                                hasEndTag2 = hasEndTag;
                                targetDurationUs = targetDurationUs2;
                                hlsMasterPlaylist = masterPlaylist2;
                            } else if (line.equals(TAG_ENDLIST)) {
                                currentSchemeDatas2 = currentSchemeDatas3;
                                hasEndTag2 = true;
                                tags = tags3;
                                hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                                targetDurationUs = targetDurationUs2;
                                hlsMasterPlaylist = masterPlaylist2;
                            } else if (!line.startsWith("#")) {
                                if (encryptionKeyUri == null) {
                                    segmentEncryptionIV = null;
                                } else if (encryptionIV != null) {
                                    segmentEncryptionIV = encryptionIV;
                                } else {
                                    segmentEncryptionIV = Long.toHexString(segmentMediaSequence);
                                }
                                segmentMediaSequence++;
                                if (segmentByteRangeLength == -1) {
                                    segmentByteRangeOffset = 0;
                                }
                                if (cachedDrmInitData2 != null || currentSchemeDatas3.isEmpty()) {
                                    currentSchemeDatas2 = currentSchemeDatas3;
                                } else {
                                    DrmInitData.SchemeData[] schemeDatas = (DrmInitData.SchemeData[]) currentSchemeDatas3.values().toArray(new DrmInitData.SchemeData[0]);
                                    DrmInitData cachedDrmInitData3 = new DrmInitData(encryptionScheme, schemeDatas);
                                    if (playlistProtectionSchemes == null) {
                                        DrmInitData.SchemeData[] playlistSchemeDatas = new DrmInitData.SchemeData[schemeDatas.length];
                                        currentSchemeDatas2 = currentSchemeDatas3;
                                        int i = 0;
                                        while (true) {
                                            cachedDrmInitData = cachedDrmInitData3;
                                            if (i >= schemeDatas.length) {
                                                break;
                                            }
                                            playlistSchemeDatas[i] = schemeDatas[i].copyWithData((byte[]) null);
                                            i++;
                                            cachedDrmInitData3 = cachedDrmInitData;
                                            schemeDatas = schemeDatas;
                                        }
                                        playlistProtectionSchemes = new DrmInitData(encryptionScheme, playlistSchemeDatas);
                                        cachedDrmInitData2 = cachedDrmInitData;
                                    } else {
                                        currentSchemeDatas2 = currentSchemeDatas3;
                                        DrmInitData.SchemeData[] schemeDataArr = schemeDatas;
                                        cachedDrmInitData2 = cachedDrmInitData3;
                                    }
                                }
                                segments.add(new HlsMediaPlaylist.Segment(replaceVariableReferences(line, variableDefinitions), initializationSegment, segmentTitle, segmentDurationUs, relativeDiscontinuitySequence, segmentStartTimeUs, cachedDrmInitData2, encryptionKeyUri, segmentEncryptionIV, segmentByteRangeOffset, segmentByteRangeLength, hasGapTag));
                                segmentStartTimeUs += segmentDurationUs;
                                segmentDurationUs = 0;
                                segmentTitle = "";
                                if (segmentByteRangeLength != -1) {
                                    segmentByteRangeOffset += segmentByteRangeLength;
                                }
                                segmentByteRangeLength = -1;
                                hasGapTag = false;
                                hlsMasterPlaylist = masterPlaylist2;
                                tags = tags3;
                                hasEndTag2 = hasEndTag;
                                hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                                targetDurationUs = targetDurationUs2;
                            } else {
                                currentSchemeDatas2 = currentSchemeDatas3;
                            }
                        }
                        hlsMasterPlaylist = masterPlaylist2;
                        tags = tags3;
                        hasEndTag2 = hasEndTag;
                        hasIndependentSegmentsTag = hasIndependentSegmentsTag2;
                        targetDurationUs = targetDurationUs2;
                    }
                }
            }
        }
        String str2 = encryptionScheme;
        return new HlsMediaPlaylist(playlistType, baseUri, tags, startOffsetUs, playlistStartTimeUs, hasDiscontinuitySequence, playlistDiscontinuitySequence, mediaSequence, version, targetDurationUs, hasIndependentSegmentsTag, hasEndTag, playlistStartTimeUs != 0, playlistProtectionSchemes, segments);
    }

    private static DrmInitData.SchemeData parsePlayReadySchemeData(String line, Map<String, String> variableDefinitions) throws ParserException {
        if (!"1".equals(parseOptionalStringAttr(line, REGEX_KEYFORMATVERSIONS, "1", variableDefinitions))) {
            return null;
        }
        String uriString = parseStringAttr(line, REGEX_URI, variableDefinitions);
        return new DrmInitData.SchemeData(C.PLAYREADY_UUID, MimeTypes.VIDEO_MP4, PsshAtomUtil.buildPsshAtom(C.PLAYREADY_UUID, Base64.decode(uriString.substring(uriString.indexOf(44)), 0)));
    }

    private static DrmInitData.SchemeData parseWidevineSchemeData(String line, String keyFormat, Map<String, String> variableDefinitions) throws ParserException {
        if (KEYFORMAT_WIDEVINE_PSSH_BINARY.equals(keyFormat)) {
            String uriString = parseStringAttr(line, REGEX_URI, variableDefinitions);
            return new DrmInitData.SchemeData(C.WIDEVINE_UUID, MimeTypes.VIDEO_MP4, Base64.decode(uriString.substring(uriString.indexOf(44)), 0));
        } else if (!KEYFORMAT_WIDEVINE_PSSH_JSON.equals(keyFormat)) {
            return null;
        } else {
            try {
                return new DrmInitData.SchemeData(C.WIDEVINE_UUID, DownloadAction.TYPE_HLS, line.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new ParserException((Throwable) e);
            }
        }
    }

    private static int parseIntAttr(String line, Pattern pattern) throws ParserException {
        return Integer.parseInt(parseStringAttr(line, pattern, Collections.emptyMap()));
    }

    private static long parseLongAttr(String line, Pattern pattern) throws ParserException {
        return Long.parseLong(parseStringAttr(line, pattern, Collections.emptyMap()));
    }

    private static double parseDoubleAttr(String line, Pattern pattern) throws ParserException {
        return Double.parseDouble(parseStringAttr(line, pattern, Collections.emptyMap()));
    }

    private static String parseStringAttr(String line, Pattern pattern, Map<String, String> variableDefinitions) throws ParserException {
        String value = parseOptionalStringAttr(line, pattern, variableDefinitions);
        if (value != null) {
            return value;
        }
        throw new ParserException("Couldn't match " + pattern.pattern() + " in " + line);
    }

    private static String parseOptionalStringAttr(String line, Pattern pattern, Map<String, String> variableDefinitions) {
        return parseOptionalStringAttr(line, pattern, (String) null, variableDefinitions);
    }

    private static String parseOptionalStringAttr(String line, Pattern pattern, String defaultValue, Map<String, String> variableDefinitions) {
        Matcher matcher = pattern.matcher(line);
        String value = matcher.find() ? matcher.group(1) : defaultValue;
        if (variableDefinitions.isEmpty() || value == null) {
            return value;
        }
        return replaceVariableReferences(value, variableDefinitions);
    }

    private static String replaceVariableReferences(String string, Map<String, String> variableDefinitions) {
        Matcher matcher = REGEX_VARIABLE_REFERENCE.matcher(string);
        StringBuffer stringWithReplacements = new StringBuffer();
        while (matcher.find()) {
            String groupName = matcher.group(1);
            if (variableDefinitions.containsKey(groupName)) {
                matcher.appendReplacement(stringWithReplacements, Matcher.quoteReplacement(variableDefinitions.get(groupName)));
            }
        }
        matcher.appendTail(stringWithReplacements);
        return stringWithReplacements.toString();
    }

    private static boolean parseOptionalBooleanAttribute(String line, Pattern pattern, boolean defaultValue) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1).equals(BOOLEAN_TRUE);
        }
        return defaultValue;
    }

    private static Pattern compileBooleanAttrPattern(String attribute) {
        return Pattern.compile(attribute + "=(" + BOOLEAN_FALSE + LogUtils.VERTICAL + BOOLEAN_TRUE + SQLBuilder.PARENTHESES_RIGHT);
    }

    private static boolean isMediaTagMuxed(List<HlsMasterPlaylist.HlsUrl> variants, String mediaTagUri) {
        if (mediaTagUri == null) {
            return true;
        }
        for (int i = 0; i < variants.size(); i++) {
            if (mediaTagUri.equals(variants.get(i).url)) {
                return true;
            }
        }
        return false;
    }

    private static class LineIterator {
        private final Queue<String> extraLines;
        private String next;
        private final BufferedReader reader;

        public LineIterator(Queue<String> extraLines2, BufferedReader reader2) {
            this.extraLines = extraLines2;
            this.reader = reader2;
        }

        public boolean hasNext() throws IOException {
            String trim;
            if (this.next != null) {
                return true;
            }
            if (!this.extraLines.isEmpty()) {
                this.next = this.extraLines.poll();
                return true;
            }
            do {
                String readLine = this.reader.readLine();
                this.next = readLine;
                if (readLine == null) {
                    return false;
                }
                trim = readLine.trim();
                this.next = trim;
            } while (trim.isEmpty());
            return true;
        }

        public String next() throws IOException {
            if (!hasNext()) {
                return null;
            }
            String result = this.next;
            this.next = null;
            return result;
        }
    }
}
