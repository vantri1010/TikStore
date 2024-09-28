package com.google.android.exoplayer2.text.ttml;

import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder extends SimpleSubtitleDecoder {
    private static final String ATTR_BEGIN = "begin";
    private static final String ATTR_DURATION = "dur";
    private static final String ATTR_END = "end";
    private static final String ATTR_IMAGE = "backgroundImage";
    private static final String ATTR_REGION = "region";
    private static final String ATTR_STYLE = "style";
    private static final Pattern CELL_RESOLUTION = Pattern.compile("^(\\d+) (\\d+)$");
    private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
    private static final CellResolution DEFAULT_CELL_RESOLUTION = new CellResolution(32, 15);
    private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0f, 1, 1);
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final Pattern FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
    private static final Pattern PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    private static final Pattern PIXEL_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)px (\\d+\\.?\\d*?)px$");
    private static final String TAG = "TtmlDecoder";
    private static final String TTP = "http://www.w3.org/ns/ttml#parameter";
    private final XmlPullParserFactory xmlParserFactory;

    public TtmlDecoder() {
        super(TAG);
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            this.xmlParserFactory = newInstance;
            newInstance.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Couldn't create XmlPullParserFactory instance", e);
        }
    }

    /* access modifiers changed from: protected */
    public TtmlSubtitle decode(byte[] bytes, int length, boolean reset) throws SubtitleDecoderException {
        ByteArrayInputStream inputStream;
        ArrayDeque<TtmlNode> nodeStack;
        Map<String, TtmlStyle> globalStyles;
        TtsExtent ttsExtent;
        CellResolution cellResolution;
        FrameAndTickRate frameAndTickRate;
        Map<String, TtmlStyle> globalStyles2;
        FrameAndTickRate frameAndTickRate2;
        try {
            XmlPullParser xmlParser = this.xmlParserFactory.newPullParser();
            Map<String, TtmlStyle> globalStyles3 = new HashMap<>();
            Map<String, TtmlRegion> regionMap = new HashMap<>();
            Map<String, String> imageMap = new HashMap<>();
            regionMap.put("", new TtmlRegion((String) null));
            ByteArrayInputStream inputStream2 = new ByteArrayInputStream(bytes, 0, length);
            xmlParser.setInput(inputStream2, (String) null);
            ArrayDeque<TtmlNode> nodeStack2 = new ArrayDeque<>();
            int eventType = xmlParser.getEventType();
            FrameAndTickRate frameAndTickRate3 = DEFAULT_FRAME_AND_TICK_RATE;
            CellResolution cellResolution2 = DEFAULT_CELL_RESOLUTION;
            TtsExtent ttsExtent2 = null;
            TtmlSubtitle ttmlSubtitle = null;
            int unsupportedNodeDepth = 0;
            int eventType2 = eventType;
            while (eventType2 != 1) {
                TtmlNode parent = nodeStack2.peek();
                if (unsupportedNodeDepth == 0) {
                    String name = xmlParser.getName();
                    if (eventType2 == 2) {
                        String name2 = name;
                        if (TtmlNode.TAG_TT.equals(name2)) {
                            FrameAndTickRate frameAndTickRate4 = parseFrameAndTickRates(xmlParser);
                            cellResolution = parseCellResolution(xmlParser, DEFAULT_CELL_RESOLUTION);
                            ttsExtent = parseTtsExtent(xmlParser);
                            frameAndTickRate = frameAndTickRate4;
                        } else {
                            cellResolution = cellResolution2;
                            ttsExtent = ttsExtent2;
                            frameAndTickRate = frameAndTickRate3;
                        }
                        if (!isSupportedTag(name2)) {
                            Log.i(TAG, "Ignoring unsupported tag: " + xmlParser.getName());
                            unsupportedNodeDepth++;
                            TtmlNode ttmlNode = parent;
                            frameAndTickRate3 = frameAndTickRate;
                            globalStyles = globalStyles3;
                            inputStream = inputStream2;
                            cellResolution2 = cellResolution;
                            ttsExtent2 = ttsExtent;
                            int i = eventType2;
                            nodeStack = nodeStack2;
                        } else {
                            if (TtmlNode.TAG_HEAD.equals(name2)) {
                                String str = name2;
                                TtmlNode ttmlNode2 = parent;
                                frameAndTickRate2 = frameAndTickRate;
                                inputStream = inputStream2;
                                int i2 = eventType2;
                                globalStyles2 = globalStyles3;
                                nodeStack = nodeStack2;
                                parseHeader(xmlParser, globalStyles3, cellResolution, ttsExtent, regionMap, imageMap);
                            } else {
                                TtmlNode parent2 = parent;
                                frameAndTickRate2 = frameAndTickRate;
                                globalStyles2 = globalStyles3;
                                inputStream = inputStream2;
                                int i3 = eventType2;
                                nodeStack = nodeStack2;
                                try {
                                    TtmlNode node = parseNode(xmlParser, parent2, regionMap, frameAndTickRate2);
                                    nodeStack.push(node);
                                    if (parent2 != null) {
                                        parent2.addChild(node);
                                    }
                                } catch (SubtitleDecoderException e) {
                                    Log.w(TAG, "Suppressing parser error", e);
                                    unsupportedNodeDepth++;
                                    frameAndTickRate3 = frameAndTickRate2;
                                    cellResolution2 = cellResolution;
                                    ttsExtent2 = ttsExtent;
                                    globalStyles = globalStyles2;
                                }
                            }
                            frameAndTickRate3 = frameAndTickRate2;
                            cellResolution2 = cellResolution;
                            ttsExtent2 = ttsExtent;
                            globalStyles = globalStyles2;
                        }
                    } else {
                        TtmlNode parent3 = parent;
                        Map<String, TtmlStyle> globalStyles4 = globalStyles3;
                        inputStream = inputStream2;
                        String str2 = name;
                        int eventType3 = eventType2;
                        nodeStack = nodeStack2;
                        if (eventType3 == 4) {
                            parent3.addChild(TtmlNode.buildTextNode(xmlParser.getText()));
                            globalStyles = globalStyles4;
                        } else if (eventType3 == 3) {
                            if (xmlParser.getName().equals(TtmlNode.TAG_TT)) {
                                globalStyles = globalStyles4;
                                ttmlSubtitle = new TtmlSubtitle(nodeStack.peek(), globalStyles, regionMap, imageMap);
                            } else {
                                globalStyles = globalStyles4;
                            }
                            nodeStack.pop();
                        } else {
                            globalStyles = globalStyles4;
                        }
                    }
                } else {
                    globalStyles = globalStyles3;
                    inputStream = inputStream2;
                    int eventType4 = eventType2;
                    nodeStack = nodeStack2;
                    if (eventType4 == 2) {
                        unsupportedNodeDepth++;
                    } else if (eventType4 == 3) {
                        unsupportedNodeDepth--;
                    }
                }
                xmlParser.next();
                eventType2 = xmlParser.getEventType();
                byte[] bArr = bytes;
                int i4 = length;
                nodeStack2 = nodeStack;
                inputStream2 = inputStream;
                globalStyles3 = globalStyles;
            }
            return ttmlSubtitle;
        } catch (XmlPullParserException xppe) {
            throw new SubtitleDecoderException("Unable to decode source", xppe);
        } catch (IOException e2) {
            throw new IllegalStateException("Unexpected error when reading input.", e2);
        }
    }

    private FrameAndTickRate parseFrameAndTickRates(XmlPullParser xmlParser) throws SubtitleDecoderException {
        int frameRate = 30;
        String frameRateString = xmlParser.getAttributeValue(TTP, "frameRate");
        if (frameRateString != null) {
            frameRate = Integer.parseInt(frameRateString);
        }
        float frameRateMultiplier = 1.0f;
        String frameRateMultiplierString = xmlParser.getAttributeValue(TTP, "frameRateMultiplier");
        if (frameRateMultiplierString != null) {
            String[] parts = Util.split(frameRateMultiplierString, " ");
            if (parts.length == 2) {
                frameRateMultiplier = ((float) Integer.parseInt(parts[0])) / ((float) Integer.parseInt(parts[1]));
            } else {
                throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
            }
        }
        int subFrameRate = DEFAULT_FRAME_AND_TICK_RATE.subFrameRate;
        String subFrameRateString = xmlParser.getAttributeValue(TTP, "subFrameRate");
        if (subFrameRateString != null) {
            subFrameRate = Integer.parseInt(subFrameRateString);
        }
        int tickRate = DEFAULT_FRAME_AND_TICK_RATE.tickRate;
        String tickRateString = xmlParser.getAttributeValue(TTP, "tickRate");
        if (tickRateString != null) {
            tickRate = Integer.parseInt(tickRateString);
        }
        return new FrameAndTickRate(((float) frameRate) * frameRateMultiplier, subFrameRate, tickRate);
    }

    private CellResolution parseCellResolution(XmlPullParser xmlParser, CellResolution defaultValue) throws SubtitleDecoderException {
        String cellResolution = xmlParser.getAttributeValue(TTP, "cellResolution");
        if (cellResolution == null) {
            return defaultValue;
        }
        Matcher cellResolutionMatcher = CELL_RESOLUTION.matcher(cellResolution);
        if (!cellResolutionMatcher.matches()) {
            Log.w(TAG, "Ignoring malformed cell resolution: " + cellResolution);
            return defaultValue;
        }
        try {
            int columns = Integer.parseInt(cellResolutionMatcher.group(1));
            int rows = Integer.parseInt(cellResolutionMatcher.group(2));
            if (columns != 0 && rows != 0) {
                return new CellResolution(columns, rows);
            }
            throw new SubtitleDecoderException("Invalid cell resolution " + columns + " " + rows);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Ignoring malformed cell resolution: " + cellResolution);
            return defaultValue;
        }
    }

    private TtsExtent parseTtsExtent(XmlPullParser xmlParser) {
        String ttsExtent = XmlPullParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_TTS_EXTENT);
        if (ttsExtent == null) {
            return null;
        }
        Matcher extentMatcher = PIXEL_COORDINATES.matcher(ttsExtent);
        if (!extentMatcher.matches()) {
            Log.w(TAG, "Ignoring non-pixel tts extent: " + ttsExtent);
            return null;
        }
        try {
            return new TtsExtent(Integer.parseInt(extentMatcher.group(1)), Integer.parseInt(extentMatcher.group(2)));
        } catch (NumberFormatException e) {
            Log.w(TAG, "Ignoring malformed tts extent: " + ttsExtent);
            return null;
        }
    }

    private Map<String, TtmlStyle> parseHeader(XmlPullParser xmlParser, Map<String, TtmlStyle> globalStyles, CellResolution cellResolution, TtsExtent ttsExtent, Map<String, TtmlRegion> globalRegions, Map<String, String> imageMap) throws IOException, XmlPullParserException {
        do {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, "style")) {
                String parentStyleId = XmlPullParserUtil.getAttributeValue(xmlParser, "style");
                TtmlStyle style = parseStyleAttributes(xmlParser, new TtmlStyle());
                if (parentStyleId != null) {
                    for (String id : parseStyleIds(parentStyleId)) {
                        style.chain(globalStyles.get(id));
                    }
                }
                if (style.getId() != null) {
                    globalStyles.put(style.getId(), style);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, "region")) {
                TtmlRegion ttmlRegion = parseRegionAttributes(xmlParser, cellResolution, ttsExtent);
                if (ttmlRegion != null) {
                    globalRegions.put(ttmlRegion.id, ttmlRegion);
                }
            } else if (XmlPullParserUtil.isStartTag(xmlParser, TtmlNode.TAG_METADATA)) {
                parseMetadata(xmlParser, imageMap);
            }
        } while (!XmlPullParserUtil.isEndTag(xmlParser, TtmlNode.TAG_HEAD));
        return globalStyles;
    }

    private void parseMetadata(XmlPullParser xmlParser, Map<String, String> imageMap) throws IOException, XmlPullParserException {
        String id;
        do {
            xmlParser.next();
            if (XmlPullParserUtil.isStartTag(xmlParser, TtmlNode.TAG_IMAGE) && (id = XmlPullParserUtil.getAttributeValue(xmlParser, TtmlNode.ATTR_ID)) != null) {
                imageMap.put(id, xmlParser.nextText());
            }
        } while (!XmlPullParserUtil.isEndTag(xmlParser, TtmlNode.TAG_METADATA));
    }

    private TtmlRegion parseRegionAttributes(XmlPullParser xmlParser, CellResolution cellResolution, TtsExtent ttsExtent) {
        float position;
        float line;
        float height;
        float width;
        float line2;
        int lineAnchor;
        XmlPullParser xmlPullParser = xmlParser;
        TtsExtent ttsExtent2 = ttsExtent;
        String regionId = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_ID);
        if (regionId == null) {
            return null;
        }
        String regionOrigin = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_ORIGIN);
        if (regionOrigin != null) {
            Matcher originPercentageMatcher = PERCENTAGE_COORDINATES.matcher(regionOrigin);
            Matcher originPixelMatcher = PIXEL_COORDINATES.matcher(regionOrigin);
            if (originPercentageMatcher.matches()) {
                try {
                    float position2 = Float.parseFloat(originPercentageMatcher.group(1)) / 100.0f;
                    line = Float.parseFloat(originPercentageMatcher.group(2)) / 100.0f;
                    position = position2;
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Ignoring region with malformed origin: " + regionOrigin);
                    return null;
                }
            } else if (!originPixelMatcher.matches()) {
                Log.w(TAG, "Ignoring region with unsupported origin: " + regionOrigin);
                return null;
            } else if (ttsExtent2 == null) {
                Log.w(TAG, "Ignoring region with missing tts:extent: " + regionOrigin);
                return null;
            } else {
                try {
                    int width2 = Integer.parseInt(originPixelMatcher.group(1));
                    int height2 = Integer.parseInt(originPixelMatcher.group(2));
                    float position3 = ((float) width2) / ((float) ttsExtent2.width);
                    line = ((float) height2) / ((float) ttsExtent2.height);
                    position = position3;
                } catch (NumberFormatException e2) {
                    Log.w(TAG, "Ignoring region with malformed origin: " + regionOrigin);
                    return null;
                }
            }
            String regionExtent = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_EXTENT);
            if (regionExtent != null) {
                Matcher extentPercentageMatcher = PERCENTAGE_COORDINATES.matcher(regionExtent);
                Matcher extentPixelMatcher = PIXEL_COORDINATES.matcher(regionExtent);
                if (extentPercentageMatcher.matches()) {
                    try {
                        width = Float.parseFloat(extentPercentageMatcher.group(1)) / 100.0f;
                        height = Float.parseFloat(extentPercentageMatcher.group(2)) / 100.0f;
                    } catch (NumberFormatException e3) {
                        Log.w(TAG, "Ignoring region with malformed extent: " + regionOrigin);
                        return null;
                    }
                } else if (!extentPixelMatcher.matches()) {
                    Log.w(TAG, "Ignoring region with unsupported extent: " + regionOrigin);
                    return null;
                } else if (ttsExtent2 == null) {
                    Log.w(TAG, "Ignoring region with missing tts:extent: " + regionOrigin);
                    return null;
                } else {
                    try {
                        int extentWidth = Integer.parseInt(extentPixelMatcher.group(1));
                        int extentHeight = Integer.parseInt(extentPixelMatcher.group(2));
                        float width3 = ((float) extentWidth) / ((float) ttsExtent2.width);
                        height = ((float) extentHeight) / ((float) ttsExtent2.height);
                        width = width3;
                    } catch (NumberFormatException e4) {
                        Log.w(TAG, "Ignoring region with malformed extent: " + regionOrigin);
                        return null;
                    }
                }
                String displayAlign = XmlPullParserUtil.getAttributeValue(xmlPullParser, TtmlNode.ATTR_TTS_DISPLAY_ALIGN);
                if (displayAlign != null) {
                    String lowerInvariant = Util.toLowerInvariant(displayAlign);
                    char c = 65535;
                    int hashCode = lowerInvariant.hashCode();
                    if (hashCode != -1364013995) {
                        if (hashCode == 92734940 && lowerInvariant.equals("after")) {
                            c = 1;
                        }
                    } else if (lowerInvariant.equals(TtmlNode.CENTER)) {
                        c = 0;
                    }
                    if (c == 0) {
                        lineAnchor = 1;
                        line2 = line + (height / 2.0f);
                    } else if (c == 1) {
                        lineAnchor = 2;
                        line2 = line + height;
                    }
                    return new TtmlRegion(regionId, position, line2, 0, lineAnchor, width, 1, 1.0f / ((float) cellResolution.rows));
                }
                lineAnchor = 0;
                line2 = line;
                return new TtmlRegion(regionId, position, line2, 0, lineAnchor, width, 1, 1.0f / ((float) cellResolution.rows));
            }
            Log.w(TAG, "Ignoring region without an extent");
            return null;
        }
        Log.w(TAG, "Ignoring region without an origin");
        return null;
    }

    private String[] parseStyleIds(String parentStyleIds) {
        String parentStyleIds2 = parentStyleIds.trim();
        return parentStyleIds2.isEmpty() ? new String[0] : Util.split(parentStyleIds2, "\\s+");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.ttml.TtmlStyle parseStyleAttributes(org.xmlpull.v1.XmlPullParser r12, com.google.android.exoplayer2.text.ttml.TtmlStyle r13) {
        /*
            r11 = this;
            int r0 = r12.getAttributeCount()
            r1 = 0
        L_0x0005:
            if (r1 >= r0) goto L_0x020d
            java.lang.String r2 = r12.getAttributeValue(r1)
            java.lang.String r3 = r12.getAttributeName(r1)
            int r4 = r3.hashCode()
            r5 = 4
            r6 = -1
            r7 = 2
            r8 = 0
            r9 = 3
            r10 = 1
            switch(r4) {
                case -1550943582: goto L_0x0070;
                case -1224696685: goto L_0x0066;
                case -1065511464: goto L_0x005b;
                case -879295043: goto L_0x004f;
                case -734428249: goto L_0x0045;
                case 3355: goto L_0x003b;
                case 94842723: goto L_0x0031;
                case 365601008: goto L_0x0027;
                case 1287124693: goto L_0x001d;
                default: goto L_0x001c;
            }
        L_0x001c:
            goto L_0x007a
        L_0x001d:
            java.lang.String r4 = "backgroundColor"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 1
            goto L_0x007b
        L_0x0027:
            java.lang.String r4 = "fontSize"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 4
            goto L_0x007b
        L_0x0031:
            java.lang.String r4 = "color"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 2
            goto L_0x007b
        L_0x003b:
            java.lang.String r4 = "id"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 0
            goto L_0x007b
        L_0x0045:
            java.lang.String r4 = "fontWeight"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 5
            goto L_0x007b
        L_0x004f:
            java.lang.String r4 = "textDecoration"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 8
            goto L_0x007b
        L_0x005b:
            java.lang.String r4 = "textAlign"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 7
            goto L_0x007b
        L_0x0066:
            java.lang.String r4 = "fontFamily"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 3
            goto L_0x007b
        L_0x0070:
            java.lang.String r4 = "fontStyle"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x001c
            r3 = 6
            goto L_0x007b
        L_0x007a:
            r3 = -1
        L_0x007b:
            java.lang.String r4 = "TtmlDecoder"
            switch(r3) {
                case 0: goto L_0x01f4;
                case 1: goto L_0x01d2;
                case 2: goto L_0x01b0;
                case 3: goto L_0x01a7;
                case 4: goto L_0x0187;
                case 5: goto L_0x0177;
                case 6: goto L_0x0167;
                case 7: goto L_0x00e5;
                case 8: goto L_0x0082;
                default: goto L_0x0080;
            }
        L_0x0080:
            goto L_0x0209
        L_0x0082:
            java.lang.String r3 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r2)
            int r4 = r3.hashCode()
            switch(r4) {
                case -1461280213: goto L_0x00ad;
                case -1026963764: goto L_0x00a2;
                case 913457136: goto L_0x0098;
                case 1679736913: goto L_0x008e;
                default: goto L_0x008d;
            }
        L_0x008d:
            goto L_0x00b6
        L_0x008e:
            java.lang.String r4 = "linethrough"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x008d
            r6 = 0
            goto L_0x00b6
        L_0x0098:
            java.lang.String r4 = "nolinethrough"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x008d
            r6 = 1
            goto L_0x00b6
        L_0x00a2:
            java.lang.String r4 = "underline"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x008d
            r6 = 2
            goto L_0x00b6
        L_0x00ad:
            java.lang.String r4 = "nounderline"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x008d
            r6 = 3
        L_0x00b6:
            if (r6 == 0) goto L_0x00da
            if (r6 == r10) goto L_0x00d1
            if (r6 == r7) goto L_0x00c8
            if (r6 == r9) goto L_0x00bf
            goto L_0x00e3
        L_0x00bf:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setUnderline(r8)
            goto L_0x00e3
        L_0x00c8:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setUnderline(r10)
            goto L_0x00e3
        L_0x00d1:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setLinethrough(r8)
            goto L_0x00e3
        L_0x00da:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setLinethrough(r10)
        L_0x00e3:
            goto L_0x0209
        L_0x00e5:
            java.lang.String r3 = com.google.android.exoplayer2.util.Util.toLowerInvariant(r2)
            int r4 = r3.hashCode()
            switch(r4) {
                case -1364013995: goto L_0x011a;
                case 100571: goto L_0x0110;
                case 3317767: goto L_0x0106;
                case 108511772: goto L_0x00fc;
                case 109757538: goto L_0x00f1;
                default: goto L_0x00f0;
            }
        L_0x00f0:
            goto L_0x0123
        L_0x00f1:
            java.lang.String r4 = "start"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00f0
            r6 = 1
            goto L_0x0123
        L_0x00fc:
            java.lang.String r4 = "right"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00f0
            r6 = 2
            goto L_0x0123
        L_0x0106:
            java.lang.String r4 = "left"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00f0
            r6 = 0
            goto L_0x0123
        L_0x0110:
            java.lang.String r4 = "end"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00f0
            r6 = 3
            goto L_0x0123
        L_0x011a:
            java.lang.String r4 = "center"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x00f0
            r6 = 4
        L_0x0123:
            if (r6 == 0) goto L_0x015a
            if (r6 == r10) goto L_0x014f
            if (r6 == r7) goto L_0x0144
            if (r6 == r9) goto L_0x0139
            if (r6 == r5) goto L_0x012e
            goto L_0x0165
        L_0x012e:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_CENTER
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setTextAlign(r4)
            goto L_0x0165
        L_0x0139:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_OPPOSITE
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setTextAlign(r4)
            goto L_0x0165
        L_0x0144:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_OPPOSITE
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setTextAlign(r4)
            goto L_0x0165
        L_0x014f:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_NORMAL
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setTextAlign(r4)
            goto L_0x0165
        L_0x015a:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_NORMAL
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setTextAlign(r4)
        L_0x0165:
            goto L_0x0209
        L_0x0167:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            java.lang.String r4 = "italic"
            boolean r4 = r4.equalsIgnoreCase(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setItalic(r4)
            goto L_0x0209
        L_0x0177:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            java.lang.String r4 = "bold"
            boolean r4 = r4.equalsIgnoreCase(r2)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setBold(r4)
            goto L_0x0209
        L_0x0187:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)     // Catch:{ SubtitleDecoderException -> 0x0191 }
            r13 = r3
            parseFontSize(r2, r13)     // Catch:{ SubtitleDecoderException -> 0x0191 }
            goto L_0x0209
        L_0x0191:
            r3 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Failed parsing fontSize value: "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            com.google.android.exoplayer2.util.Log.w(r4, r5)
            goto L_0x0209
        L_0x01a7:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setFontFamily(r2)
            goto L_0x0209
        L_0x01b0:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r11.createIfNull(r13)
            int r3 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r2)     // Catch:{ IllegalArgumentException -> 0x01bc }
            r13.setFontColor(r3)     // Catch:{ IllegalArgumentException -> 0x01bc }
            goto L_0x0209
        L_0x01bc:
            r3 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Failed parsing color value: "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            com.google.android.exoplayer2.util.Log.w(r4, r5)
            goto L_0x0209
        L_0x01d2:
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r11.createIfNull(r13)
            int r3 = com.google.android.exoplayer2.util.ColorParser.parseTtmlColor(r2)     // Catch:{ IllegalArgumentException -> 0x01de }
            r13.setBackgroundColor(r3)     // Catch:{ IllegalArgumentException -> 0x01de }
            goto L_0x0209
        L_0x01de:
            r3 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Failed parsing background value: "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            com.google.android.exoplayer2.util.Log.w(r4, r5)
            goto L_0x0209
        L_0x01f4:
            java.lang.String r3 = r12.getName()
            java.lang.String r4 = "style"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0209
            com.google.android.exoplayer2.text.ttml.TtmlStyle r3 = r11.createIfNull(r13)
            com.google.android.exoplayer2.text.ttml.TtmlStyle r13 = r3.setId(r2)
        L_0x0209:
            int r1 = r1 + 1
            goto L_0x0005
        L_0x020d:
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseStyleAttributes(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlStyle):com.google.android.exoplayer2.text.ttml.TtmlStyle");
    }

    private TtmlStyle createIfNull(TtmlStyle style) {
        return style == null ? new TtmlStyle() : style;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.text.ttml.TtmlNode parseNode(org.xmlpull.v1.XmlPullParser r27, com.google.android.exoplayer2.text.ttml.TtmlNode r28, java.util.Map<java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlRegion> r29, com.google.android.exoplayer2.text.ttml.TtmlDecoder.FrameAndTickRate r30) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r30
            r4 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r6 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r8 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            java.lang.String r10 = ""
            r11 = 0
            r12 = 0
            int r13 = r27.getAttributeCount()
            r14 = 0
            com.google.android.exoplayer2.text.ttml.TtmlStyle r14 = r0.parseStyleAttributes(r1, r14)
            r15 = 0
        L_0x0025:
            if (r15 >= r13) goto L_0x00ea
            r24 = r13
            java.lang.String r13 = r1.getAttributeName(r15)
            r25 = r11
            java.lang.String r11 = r1.getAttributeValue(r15)
            r16 = -1
            int r17 = r13.hashCode()
            switch(r17) {
                case -934795532: goto L_0x0072;
                case 99841: goto L_0x0068;
                case 100571: goto L_0x005e;
                case 93616297: goto L_0x0052;
                case 109780401: goto L_0x0047;
                case 1292595405: goto L_0x003d;
                default: goto L_0x003c;
            }
        L_0x003c:
            goto L_0x007c
        L_0x003d:
            java.lang.String r1 = "backgroundImage"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x003c
            r1 = 5
            goto L_0x007d
        L_0x0047:
            java.lang.String r1 = "style"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x003c
            r1 = 3
            goto L_0x007d
        L_0x0052:
            java.lang.String r1 = "begin"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x003c
            r16 = 0
            r1 = 0
            goto L_0x007d
        L_0x005e:
            java.lang.String r1 = "end"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x003c
            r1 = 1
            goto L_0x007d
        L_0x0068:
            java.lang.String r1 = "dur"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x003c
            r1 = 2
            goto L_0x007d
        L_0x0072:
            java.lang.String r1 = "region"
            boolean r1 = r13.equals(r1)
            if (r1 == 0) goto L_0x003c
            r1 = 4
            goto L_0x007d
        L_0x007c:
            r1 = -1
        L_0x007d:
            if (r1 == 0) goto L_0x00d6
            r16 = r13
            r13 = 1
            if (r1 == r13) goto L_0x00cd
            r13 = 2
            if (r1 == r13) goto L_0x00c4
            r13 = 3
            if (r1 == r13) goto L_0x00b3
            r13 = 4
            if (r1 == r13) goto L_0x00a7
            r13 = 5
            if (r1 == r13) goto L_0x0093
            r1 = r29
            goto L_0x00c1
        L_0x0093:
            java.lang.String r1 = "#"
            boolean r1 = r11.startsWith(r1)
            if (r1 == 0) goto L_0x00a4
            r1 = 1
            java.lang.String r1 = r11.substring(r1)
            r11 = r1
            r1 = r29
            goto L_0x00e0
        L_0x00a4:
            r1 = r29
            goto L_0x00c1
        L_0x00a7:
            r1 = r29
            boolean r13 = r1.containsKey(r11)
            if (r13 == 0) goto L_0x00c1
            r10 = r11
            r11 = r25
            goto L_0x00e0
        L_0x00b3:
            r1 = r29
            java.lang.String[] r13 = r0.parseStyleIds(r11)
            int r0 = r13.length
            if (r0 <= 0) goto L_0x00c1
            r0 = r13
            r12 = r0
            r11 = r25
            goto L_0x00e0
        L_0x00c1:
            r11 = r25
            goto L_0x00e0
        L_0x00c4:
            r1 = r29
            long r4 = parseTimeExpression(r11, r3)
            r11 = r25
            goto L_0x00e0
        L_0x00cd:
            r1 = r29
            long r8 = parseTimeExpression(r11, r3)
            r11 = r25
            goto L_0x00e0
        L_0x00d6:
            r1 = r29
            r16 = r13
            long r6 = parseTimeExpression(r11, r3)
            r11 = r25
        L_0x00e0:
            int r15 = r15 + 1
            r0 = r26
            r1 = r27
            r13 = r24
            goto L_0x0025
        L_0x00ea:
            r1 = r29
            r25 = r11
            r24 = r13
            r15 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            if (r2 == 0) goto L_0x010b
            long r0 = r2.startTimeUs
            int r11 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r11 == 0) goto L_0x010b
            int r0 = (r6 > r15 ? 1 : (r6 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x0104
            long r0 = r2.startTimeUs
            long r6 = r6 + r0
        L_0x0104:
            int r0 = (r8 > r15 ? 1 : (r8 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x010b
            long r0 = r2.startTimeUs
            long r8 = r8 + r0
        L_0x010b:
            int r0 = (r8 > r15 ? 1 : (r8 == r15 ? 0 : -1))
            if (r0 != 0) goto L_0x0120
            int r0 = (r4 > r15 ? 1 : (r4 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x0116
            long r8 = r6 + r4
            goto L_0x0120
        L_0x0116:
            if (r2 == 0) goto L_0x0120
            long r0 = r2.endTimeUs
            int r11 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r11 == 0) goto L_0x0120
            long r8 = r2.endTimeUs
        L_0x0120:
            java.lang.String r15 = r27.getName()
            r16 = r6
            r18 = r8
            r20 = r14
            r21 = r12
            r22 = r10
            r23 = r25
            com.google.android.exoplayer2.text.ttml.TtmlNode r0 = com.google.android.exoplayer2.text.ttml.TtmlNode.buildNode(r15, r16, r18, r20, r21, r22, r23)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseNode(org.xmlpull.v1.XmlPullParser, com.google.android.exoplayer2.text.ttml.TtmlNode, java.util.Map, com.google.android.exoplayer2.text.ttml.TtmlDecoder$FrameAndTickRate):com.google.android.exoplayer2.text.ttml.TtmlNode");
    }

    private static boolean isSupportedTag(String tag) {
        return tag.equals(TtmlNode.TAG_TT) || tag.equals(TtmlNode.TAG_HEAD) || tag.equals(TtmlNode.TAG_BODY) || tag.equals(TtmlNode.TAG_DIV) || tag.equals(TtmlNode.TAG_P) || tag.equals(TtmlNode.TAG_SPAN) || tag.equals(TtmlNode.TAG_BR) || tag.equals("style") || tag.equals(TtmlNode.TAG_STYLING) || tag.equals(TtmlNode.TAG_LAYOUT) || tag.equals("region") || tag.equals(TtmlNode.TAG_METADATA) || tag.equals(TtmlNode.TAG_IMAGE) || tag.equals("data") || tag.equals(TtmlNode.TAG_INFORMATION);
    }

    private static void parseFontSize(String expression, TtmlStyle out) throws SubtitleDecoderException {
        Matcher matcher;
        String[] expressions = Util.split(expression, "\\s+");
        if (expressions.length == 1) {
            matcher = FONT_SIZE.matcher(expression);
        } else if (expressions.length == 2) {
            matcher = FONT_SIZE.matcher(expressions[1]);
            Log.w(TAG, "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
        } else {
            throw new SubtitleDecoderException("Invalid number of entries for fontSize: " + expressions.length + ".");
        }
        if (matcher.matches()) {
            String unit = matcher.group(3);
            char c = 65535;
            int hashCode = unit.hashCode();
            if (hashCode != 37) {
                if (hashCode != 3240) {
                    if (hashCode == 3592 && unit.equals("px")) {
                        c = 0;
                    }
                } else if (unit.equals("em")) {
                    c = 1;
                }
            } else if (unit.equals("%")) {
                c = 2;
            }
            if (c == 0) {
                out.setFontSizeUnit(1);
            } else if (c == 1) {
                out.setFontSizeUnit(2);
            } else if (c == 2) {
                out.setFontSizeUnit(3);
            } else {
                throw new SubtitleDecoderException("Invalid unit for fontSize: '" + unit + "'.");
            }
            out.setFontSize(Float.valueOf(matcher.group(1)).floatValue());
            return;
        }
        throw new SubtitleDecoderException("Invalid expression for fontSize: '" + expression + "'.");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c2, code lost:
        if (r11.equals("s") != false) goto L_0x00ee;
     */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x010f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long parseTimeExpression(java.lang.String r16, com.google.android.exoplayer2.text.ttml.TtmlDecoder.FrameAndTickRate r17) throws com.google.android.exoplayer2.text.SubtitleDecoderException {
        /*
            r0 = r16
            r1 = r17
            java.util.regex.Pattern r2 = CLOCK_TIME
            java.util.regex.Matcher r2 = r2.matcher(r0)
            boolean r3 = r2.matches()
            r6 = 5
            r7 = 4
            r8 = 3
            r9 = 2
            r10 = 1
            if (r3 == 0) goto L_0x007b
            java.lang.String r3 = r2.group(r10)
            long r10 = java.lang.Long.parseLong(r3)
            r12 = 3600(0xe10, double:1.7786E-320)
            long r10 = r10 * r12
            double r10 = (double) r10
            java.lang.String r9 = r2.group(r9)
            long r12 = java.lang.Long.parseLong(r9)
            r14 = 60
            long r12 = r12 * r14
            double r12 = (double) r12
            double r10 = r10 + r12
            java.lang.String r8 = r2.group(r8)
            long r12 = java.lang.Long.parseLong(r8)
            double r12 = (double) r12
            double r10 = r10 + r12
            java.lang.String r7 = r2.group(r7)
            r12 = 0
            if (r7 == 0) goto L_0x0047
            double r14 = java.lang.Double.parseDouble(r7)
            goto L_0x0048
        L_0x0047:
            r14 = r12
        L_0x0048:
            double r10 = r10 + r14
            java.lang.String r6 = r2.group(r6)
            if (r6 == 0) goto L_0x0059
            long r14 = java.lang.Long.parseLong(r6)
            float r14 = (float) r14
            float r15 = r1.effectiveFrameRate
            float r14 = r14 / r15
            double r14 = (double) r14
            goto L_0x005a
        L_0x0059:
            r14 = r12
        L_0x005a:
            double r10 = r10 + r14
            r14 = 6
            java.lang.String r14 = r2.group(r14)
            if (r14 == 0) goto L_0x0070
            long r12 = java.lang.Long.parseLong(r14)
            double r12 = (double) r12
            int r15 = r1.subFrameRate
            double r4 = (double) r15
            double r12 = r12 / r4
            float r4 = r1.effectiveFrameRate
            double r4 = (double) r4
            double r12 = r12 / r4
            goto L_0x0071
        L_0x0070:
        L_0x0071:
            double r10 = r10 + r12
            r4 = 4696837146684686336(0x412e848000000000, double:1000000.0)
            double r4 = r4 * r10
            long r4 = (long) r4
            return r4
        L_0x007b:
            java.util.regex.Pattern r3 = OFFSET_TIME
            java.util.regex.Matcher r2 = r3.matcher(r0)
            boolean r3 = r2.matches()
            if (r3 == 0) goto L_0x0120
            java.lang.String r3 = r2.group(r10)
            double r4 = java.lang.Double.parseDouble(r3)
            java.lang.String r11 = r2.group(r9)
            r12 = -1
            int r13 = r11.hashCode()
            r14 = 102(0x66, float:1.43E-43)
            if (r13 == r14) goto L_0x00e3
            r14 = 104(0x68, float:1.46E-43)
            if (r13 == r14) goto L_0x00d9
            r14 = 109(0x6d, float:1.53E-43)
            if (r13 == r14) goto L_0x00cf
            r14 = 3494(0xda6, float:4.896E-42)
            if (r13 == r14) goto L_0x00c5
            r14 = 115(0x73, float:1.61E-43)
            if (r13 == r14) goto L_0x00bc
            r9 = 116(0x74, float:1.63E-43)
            if (r13 == r9) goto L_0x00b1
        L_0x00b0:
            goto L_0x00ed
        L_0x00b1:
            java.lang.String r9 = "t"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x00b0
            r9 = 5
            goto L_0x00ee
        L_0x00bc:
            java.lang.String r13 = "s"
            boolean r13 = r11.equals(r13)
            if (r13 == 0) goto L_0x00b0
            goto L_0x00ee
        L_0x00c5:
            java.lang.String r9 = "ms"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x00b0
            r9 = 3
            goto L_0x00ee
        L_0x00cf:
            java.lang.String r9 = "m"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x00b0
            r9 = 1
            goto L_0x00ee
        L_0x00d9:
            java.lang.String r9 = "h"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x00b0
            r9 = 0
            goto L_0x00ee
        L_0x00e3:
            java.lang.String r9 = "f"
            boolean r9 = r11.equals(r9)
            if (r9 == 0) goto L_0x00b0
            r9 = 4
            goto L_0x00ee
        L_0x00ed:
            r9 = -1
        L_0x00ee:
            if (r9 == 0) goto L_0x010f
            if (r9 == r10) goto L_0x010a
            if (r9 == r8) goto L_0x0103
            if (r9 == r7) goto L_0x00fe
            if (r9 == r6) goto L_0x00f9
            goto L_0x0117
        L_0x00f9:
            int r6 = r1.tickRate
            double r6 = (double) r6
            double r4 = r4 / r6
            goto L_0x0117
        L_0x00fe:
            float r6 = r1.effectiveFrameRate
            double r6 = (double) r6
            double r4 = r4 / r6
            goto L_0x0117
        L_0x0103:
            r6 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r4 = r4 / r6
            goto L_0x0117
        L_0x010a:
            r6 = 4633641066610819072(0x404e000000000000, double:60.0)
            double r4 = r4 * r6
            goto L_0x0117
        L_0x010f:
            r6 = 4660134898793709568(0x40ac200000000000, double:3600.0)
            double r4 = r4 * r6
        L_0x0117:
            r6 = 4696837146684686336(0x412e848000000000, double:1000000.0)
            double r6 = r6 * r4
            long r6 = (long) r6
            return r6
        L_0x0120:
            com.google.android.exoplayer2.text.SubtitleDecoderException r3 = new com.google.android.exoplayer2.text.SubtitleDecoderException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Malformed time expression: "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            r3.<init>((java.lang.String) r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.text.ttml.TtmlDecoder.parseTimeExpression(java.lang.String, com.google.android.exoplayer2.text.ttml.TtmlDecoder$FrameAndTickRate):long");
    }

    private static final class FrameAndTickRate {
        final float effectiveFrameRate;
        final int subFrameRate;
        final int tickRate;

        FrameAndTickRate(float effectiveFrameRate2, int subFrameRate2, int tickRate2) {
            this.effectiveFrameRate = effectiveFrameRate2;
            this.subFrameRate = subFrameRate2;
            this.tickRate = tickRate2;
        }
    }

    private static final class CellResolution {
        final int columns;
        final int rows;

        CellResolution(int columns2, int rows2) {
            this.columns = columns2;
            this.rows = rows2;
        }
    }

    private static final class TtsExtent {
        final int height;
        final int width;

        TtsExtent(int width2, int height2) {
            this.width = width2;
            this.height = height2;
        }
    }
}
