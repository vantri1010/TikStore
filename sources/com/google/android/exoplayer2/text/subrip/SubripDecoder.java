package com.google.android.exoplayer2.text.subrip;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubripDecoder extends SimpleSubtitleDecoder {
    private static final String ALIGN_BOTTOM_LEFT = "{\\an1}";
    private static final String ALIGN_BOTTOM_MID = "{\\an2}";
    private static final String ALIGN_BOTTOM_RIGHT = "{\\an3}";
    private static final String ALIGN_MID_LEFT = "{\\an4}";
    private static final String ALIGN_MID_MID = "{\\an5}";
    private static final String ALIGN_MID_RIGHT = "{\\an6}";
    private static final String ALIGN_TOP_LEFT = "{\\an7}";
    private static final String ALIGN_TOP_MID = "{\\an8}";
    private static final String ALIGN_TOP_RIGHT = "{\\an9}";
    static final float END_FRACTION = 0.92f;
    static final float MID_FRACTION = 0.5f;
    static final float START_FRACTION = 0.08f;
    private static final String SUBRIP_ALIGNMENT_TAG = "\\{\\\\an[1-9]\\}";
    private static final Pattern SUBRIP_TAG_PATTERN = Pattern.compile("\\{\\\\.*?\\}");
    private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+),(\\d+)";
    private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))\\s*-->\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))?\\s*");
    private static final String TAG = "SubripDecoder";
    private final ArrayList<String> tags = new ArrayList<>();
    private final StringBuilder textBuilder = new StringBuilder();

    public SubripDecoder() {
        super(TAG);
    }

    /* access modifiers changed from: protected */
    public SubripSubtitle decode(byte[] bytes, int length, boolean reset) {
        ArrayList<Cue> cues = new ArrayList<>();
        LongArray cueTimesUs = new LongArray();
        ParsableByteArray subripData = new ParsableByteArray(bytes, length);
        while (true) {
            String readLine = subripData.readLine();
            String currentLine = readLine;
            if (readLine == null) {
                break;
            } else if (currentLine.length() != 0) {
                try {
                    Integer.parseInt(currentLine);
                    boolean haveEndTimecode = false;
                    String currentLine2 = subripData.readLine();
                    if (currentLine2 == null) {
                        Log.w(TAG, "Unexpected end");
                        break;
                    }
                    Matcher matcher = SUBRIP_TIMING_LINE.matcher(currentLine2);
                    if (matcher.matches()) {
                        cueTimesUs.add(parseTimecode(matcher, 1));
                        if (!TextUtils.isEmpty(matcher.group(6))) {
                            haveEndTimecode = true;
                            cueTimesUs.add(parseTimecode(matcher, 6));
                        }
                        this.textBuilder.setLength(0);
                        this.tags.clear();
                        while (true) {
                            String readLine2 = subripData.readLine();
                            String currentLine3 = readLine2;
                            if (TextUtils.isEmpty(readLine2)) {
                                break;
                            }
                            if (this.textBuilder.length() > 0) {
                                this.textBuilder.append("<br>");
                            }
                            this.textBuilder.append(processLine(currentLine3, this.tags));
                        }
                        Spanned text = Html.fromHtml(this.textBuilder.toString());
                        String alignmentTag = null;
                        int i = 0;
                        while (true) {
                            if (i >= this.tags.size()) {
                                break;
                            }
                            String tag = this.tags.get(i);
                            if (tag.matches(SUBRIP_ALIGNMENT_TAG)) {
                                alignmentTag = tag;
                                break;
                            }
                            i++;
                        }
                        cues.add(buildCue(text, alignmentTag));
                        if (haveEndTimecode) {
                            cues.add((Object) null);
                        }
                    } else {
                        Log.w(TAG, "Skipping invalid timing: " + currentLine2);
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Skipping invalid index: " + currentLine);
                }
            }
        }
        Cue[] cuesArray = new Cue[cues.size()];
        cues.toArray(cuesArray);
        return new SubripSubtitle(cuesArray, cueTimesUs.toArray());
    }

    private String processLine(String line, ArrayList<String> tags2) {
        String line2 = line.trim();
        int removedCharacterCount = 0;
        StringBuilder processedLine = new StringBuilder(line2);
        Matcher matcher = SUBRIP_TAG_PATTERN.matcher(line2);
        while (matcher.find()) {
            String tag = matcher.group();
            tags2.add(tag);
            int start = matcher.start() - removedCharacterCount;
            int tagLength = tag.length();
            processedLine.replace(start, start + tagLength, "");
            removedCharacterCount += tagLength;
        }
        return processedLine.toString();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.Cue buildCue(android.text.Spanned r18, java.lang.String r19) {
        /*
            r17 = this;
            r0 = r19
            if (r0 != 0) goto L_0x000c
            com.google.android.exoplayer2.text.Cue r1 = new com.google.android.exoplayer2.text.Cue
            r11 = r18
            r1.<init>(r11)
            return r1
        L_0x000c:
            r11 = r18
            int r1 = r19.hashCode()
            java.lang.String r6 = "{\\an9}"
            java.lang.String r7 = "{\\an8}"
            java.lang.String r8 = "{\\an7}"
            java.lang.String r9 = "{\\an6}"
            java.lang.String r10 = "{\\an5}"
            java.lang.String r12 = "{\\an4}"
            java.lang.String r13 = "{\\an3}"
            java.lang.String r14 = "{\\an2}"
            java.lang.String r15 = "{\\an1}"
            r16 = -1
            r3 = 4
            r4 = 3
            r5 = 2
            r2 = 1
            switch(r1) {
                case -685620710: goto L_0x0078;
                case -685620679: goto L_0x0070;
                case -685620648: goto L_0x0068;
                case -685620617: goto L_0x0060;
                case -685620586: goto L_0x0058;
                case -685620555: goto L_0x0050;
                case -685620524: goto L_0x0048;
                case -685620493: goto L_0x003f;
                case -685620462: goto L_0x0037;
                default: goto L_0x0036;
            }
        L_0x0036:
            goto L_0x0080
        L_0x0037:
            boolean r1 = r0.equals(r6)
            if (r1 == 0) goto L_0x0036
            r1 = 5
            goto L_0x0081
        L_0x003f:
            boolean r1 = r0.equals(r7)
            if (r1 == 0) goto L_0x0036
            r1 = 8
            goto L_0x0081
        L_0x0048:
            boolean r1 = r0.equals(r8)
            if (r1 == 0) goto L_0x0036
            r1 = 2
            goto L_0x0081
        L_0x0050:
            boolean r1 = r0.equals(r9)
            if (r1 == 0) goto L_0x0036
            r1 = 4
            goto L_0x0081
        L_0x0058:
            boolean r1 = r0.equals(r10)
            if (r1 == 0) goto L_0x0036
            r1 = 7
            goto L_0x0081
        L_0x0060:
            boolean r1 = r0.equals(r12)
            if (r1 == 0) goto L_0x0036
            r1 = 1
            goto L_0x0081
        L_0x0068:
            boolean r1 = r0.equals(r13)
            if (r1 == 0) goto L_0x0036
            r1 = 3
            goto L_0x0081
        L_0x0070:
            boolean r1 = r0.equals(r14)
            if (r1 == 0) goto L_0x0036
            r1 = 6
            goto L_0x0081
        L_0x0078:
            boolean r1 = r0.equals(r15)
            if (r1 == 0) goto L_0x0036
            r1 = 0
            goto L_0x0081
        L_0x0080:
            r1 = -1
        L_0x0081:
            if (r1 == 0) goto L_0x0092
            if (r1 == r2) goto L_0x0092
            if (r1 == r5) goto L_0x0092
            if (r1 == r4) goto L_0x0090
            if (r1 == r3) goto L_0x0090
            r3 = 5
            if (r1 == r3) goto L_0x0090
            r1 = 1
            goto L_0x0094
        L_0x0090:
            r1 = 2
            goto L_0x0094
        L_0x0092:
            r1 = 0
        L_0x0094:
            int r3 = r19.hashCode()
            switch(r3) {
                case -685620710: goto L_0x00dd;
                case -685620679: goto L_0x00d5;
                case -685620648: goto L_0x00cd;
                case -685620617: goto L_0x00c5;
                case -685620586: goto L_0x00bd;
                case -685620555: goto L_0x00b4;
                case -685620524: goto L_0x00ac;
                case -685620493: goto L_0x00a4;
                case -685620462: goto L_0x009c;
                default: goto L_0x009b;
            }
        L_0x009b:
            goto L_0x00e5
        L_0x009c:
            boolean r3 = r0.equals(r6)
            if (r3 == 0) goto L_0x009b
            r3 = 5
            goto L_0x00e6
        L_0x00a4:
            boolean r3 = r0.equals(r7)
            if (r3 == 0) goto L_0x009b
            r3 = 4
            goto L_0x00e6
        L_0x00ac:
            boolean r3 = r0.equals(r8)
            if (r3 == 0) goto L_0x009b
            r3 = 3
            goto L_0x00e6
        L_0x00b4:
            boolean r3 = r0.equals(r9)
            if (r3 == 0) goto L_0x009b
            r3 = 8
            goto L_0x00e6
        L_0x00bd:
            boolean r3 = r0.equals(r10)
            if (r3 == 0) goto L_0x009b
            r3 = 7
            goto L_0x00e6
        L_0x00c5:
            boolean r3 = r0.equals(r12)
            if (r3 == 0) goto L_0x009b
            r3 = 6
            goto L_0x00e6
        L_0x00cd:
            boolean r3 = r0.equals(r13)
            if (r3 == 0) goto L_0x009b
            r3 = 2
            goto L_0x00e6
        L_0x00d5:
            boolean r3 = r0.equals(r14)
            if (r3 == 0) goto L_0x009b
            r3 = 1
            goto L_0x00e6
        L_0x00dd:
            boolean r3 = r0.equals(r15)
            if (r3 == 0) goto L_0x009b
            r3 = 0
            goto L_0x00e6
        L_0x00e5:
            r3 = -1
        L_0x00e6:
            if (r3 == 0) goto L_0x00fa
            if (r3 == r2) goto L_0x00fa
            if (r3 == r5) goto L_0x00fa
            if (r3 == r4) goto L_0x00f7
            r2 = 4
            if (r3 == r2) goto L_0x00f7
            r2 = 5
            if (r3 == r2) goto L_0x00f7
            r2 = 1
            r12 = r2
            goto L_0x00fc
        L_0x00f7:
            r2 = 0
            r12 = r2
            goto L_0x00fc
        L_0x00fa:
            r2 = 2
            r12 = r2
        L_0x00fc:
            com.google.android.exoplayer2.text.Cue r13 = new com.google.android.exoplayer2.text.Cue
            r4 = 0
            float r5 = getFractionalPositionForAnchorType(r12)
            r6 = 0
            float r8 = getFractionalPositionForAnchorType(r1)
            r10 = 1
            r2 = r13
            r3 = r18
            r7 = r12
            r9 = r1
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.subrip.SubripDecoder.buildCue(android.text.Spanned, java.lang.String):com.google.android.exoplayer2.text.Cue");
    }

    private static long parseTimecode(Matcher matcher, int groupOffset) {
        return 1000 * ((Long.parseLong(matcher.group(groupOffset + 1)) * 60 * 60 * 1000) + (Long.parseLong(matcher.group(groupOffset + 2)) * 60 * 1000) + (Long.parseLong(matcher.group(groupOffset + 3)) * 1000) + Long.parseLong(matcher.group(groupOffset + 4)));
    }

    static float getFractionalPositionForAnchorType(int anchorType) {
        if (anchorType == 0) {
            return START_FRACTION;
        }
        if (anchorType != 1) {
            return END_FRACTION;
        }
        return 0.5f;
    }
}
