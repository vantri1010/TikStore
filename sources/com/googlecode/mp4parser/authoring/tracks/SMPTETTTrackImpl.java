package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.king.zxing.util.LogUtils;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SMPTETTTrackImpl extends AbstractTrack {
    public static final String SMPTE_TT_NAMESPACE = "http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt";
    XMLSubtitleSampleEntry XMLSubtitleSampleEntry = new XMLSubtitleSampleEntry();
    boolean containsImages;
    SampleDescriptionBox sampleDescriptionBox = new SampleDescriptionBox();
    private long[] sampleDurations;
    List<Sample> samples = new ArrayList();
    SubSampleInformationBox subSampleInformationBox = new SubSampleInformationBox();
    TrackMetaData trackMetaData = new TrackMetaData();

    static long toTime(String expr) {
        Matcher m = Pattern.compile("([0-9][0-9]):([0-9][0-9]):([0-9][0-9])([\\.:][0-9][0-9]?[0-9]?)?").matcher(expr);
        if (m.matches()) {
            String hours = m.group(1);
            String minutes = m.group(2);
            String seconds = m.group(3);
            String fraction = m.group(4);
            if (fraction == null) {
                fraction = ".000";
            }
            String fraction2 = fraction.replace(LogUtils.COLON, ".");
            return (long) (((double) ((Long.parseLong(hours) * 60 * 60 * 1000) + (Long.parseLong(minutes) * 60 * 1000) + (Long.parseLong(seconds) * 1000))) + (Double.parseDouble("0" + fraction2) * 1000.0d));
        }
        throw new RuntimeException("Cannot match " + expr + " to time expression");
    }

    public static String getLanguage(Document document) {
        return document.getDocumentElement().getAttribute("xml:lang");
    }

    public static long earliestTimestamp(Document document) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        NamespaceContext ctx = new TextTrackNamespaceContext((TextTrackNamespaceContext) null);
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(ctx);
        try {
            NodeList timedNodes = (NodeList) xpath.compile("//*[@begin]").evaluate(document, XPathConstants.NODESET);
            long earliestTimestamp = 0;
            for (int i = 0; i < timedNodes.getLength(); i++) {
                earliestTimestamp = Math.min(toTime(timedNodes.item(i).getAttributes().getNamedItem("begin").getNodeValue()), earliestTimestamp);
            }
            return earliestTimestamp;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static long latestTimestamp(Document document) {
        long end;
        XPathFactory xPathfactory = XPathFactory.newInstance();
        NamespaceContext ctx = new TextTrackNamespaceContext((TextTrackNamespaceContext) null);
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(ctx);
        try {
            try {
                NodeList timedNodes = (NodeList) xpath.compile("//*[@begin]").evaluate(document, XPathConstants.NODESET);
                long lastTimeStamp = 0;
                for (int i = 0; i < timedNodes.getLength(); i++) {
                    Node n = timedNodes.item(i);
                    String begin = n.getAttributes().getNamedItem("begin").getNodeValue();
                    if (n.getAttributes().getNamedItem("dur") != null) {
                        end = toTime(begin) + toTime(n.getAttributes().getNamedItem("dur").getNodeValue());
                    } else if (n.getAttributes().getNamedItem(TtmlNode.END) != null) {
                        end = toTime(n.getAttributes().getNamedItem(TtmlNode.END).getNodeValue());
                    } else {
                        throw new RuntimeException("neither end nor dur attribute is present");
                    }
                    lastTimeStamp = Math.max(end, lastTimeStamp);
                }
                return lastTimeStamp;
            } catch (XPathExpressionException e) {
                e = e;
                throw new RuntimeException(e);
            }
        } catch (XPathExpressionException e2) {
            e = e2;
            Document document2 = document;
            throw new RuntimeException(e);
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public SMPTETTTrackImpl(java.io.File... r32) throws java.io.IOException, javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, javax.xml.xpath.XPathExpressionException {
        /*
            r31 = this;
            r0 = r31
            r1 = r32
            r2 = 0
            r2 = r1[r2]
            java.lang.String r2 = r2.getName()
            r0.<init>(r2)
            com.googlecode.mp4parser.authoring.TrackMetaData r2 = new com.googlecode.mp4parser.authoring.TrackMetaData
            r2.<init>()
            r0.trackMetaData = r2
            com.coremedia.iso.boxes.SampleDescriptionBox r2 = new com.coremedia.iso.boxes.SampleDescriptionBox
            r2.<init>()
            r0.sampleDescriptionBox = r2
            com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry r2 = new com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry
            r2.<init>()
            r0.XMLSubtitleSampleEntry = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.samples = r2
            com.coremedia.iso.boxes.SubSampleInformationBox r2 = new com.coremedia.iso.boxes.SubSampleInformationBox
            r2.<init>()
            r0.subSampleInformationBox = r2
            int r2 = r1.length
            long[] r2 = new long[r2]
            r0.sampleDurations = r2
            javax.xml.parsers.DocumentBuilderFactory r2 = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            r3 = 1
            r2.setNamespaceAware(r3)
            javax.xml.parsers.DocumentBuilder r3 = r2.newDocumentBuilder()
            r4 = 0
            r6 = 0
            r7 = 0
        L_0x0046:
            int r8 = r1.length
            if (r7 < r8) goto L_0x0088
            com.googlecode.mp4parser.authoring.TrackMetaData r7 = r0.trackMetaData
            java.lang.String r8 = com.googlecode.mp4parser.util.Iso639.convert2to3(r6)
            r7.setLanguage(r8)
            com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry r7 = r0.XMLSubtitleSampleEntry
            java.lang.String r8 = "http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt"
            r7.setNamespace(r8)
            com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry r7 = r0.XMLSubtitleSampleEntry
            r7.setSchemaLocation(r8)
            boolean r7 = r0.containsImages
            if (r7 == 0) goto L_0x006a
            com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry r7 = r0.XMLSubtitleSampleEntry
            java.lang.String r8 = "image/png"
            r7.setAuxiliaryMimeTypes(r8)
            goto L_0x0071
        L_0x006a:
            com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry r7 = r0.XMLSubtitleSampleEntry
            java.lang.String r8 = ""
            r7.setAuxiliaryMimeTypes(r8)
        L_0x0071:
            com.coremedia.iso.boxes.SampleDescriptionBox r7 = r0.sampleDescriptionBox
            com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry r8 = r0.XMLSubtitleSampleEntry
            r7.addBox(r8)
            com.googlecode.mp4parser.authoring.TrackMetaData r7 = r0.trackMetaData
            r8 = 30000(0x7530, double:1.4822E-319)
            r7.setTimescale(r8)
            com.googlecode.mp4parser.authoring.TrackMetaData r7 = r0.trackMetaData
            r8 = 65535(0xffff, float:9.1834E-41)
            r7.setLayer(r8)
            return
        L_0x0088:
            r8 = r1[r7]
            com.coremedia.iso.boxes.SubSampleInformationBox$SubSampleEntry r9 = new com.coremedia.iso.boxes.SubSampleInformationBox$SubSampleEntry
            r9.<init>()
            com.coremedia.iso.boxes.SubSampleInformationBox r10 = r0.subSampleInformationBox
            java.util.List r10 = r10.getEntries()
            r10.add(r9)
            r10 = 1
            r9.setSampleDelta(r10)
            org.w3c.dom.Document r10 = r3.parse(r8)
            java.lang.String r11 = getLanguage(r10)
            if (r6 != 0) goto L_0x00a9
            r6 = r11
            goto L_0x00af
        L_0x00a9:
            boolean r12 = r6.equals(r11)
            if (r12 == 0) goto L_0x024e
        L_0x00af:
            r12 = r6
            javax.xml.xpath.XPathFactory r13 = javax.xml.xpath.XPathFactory.newInstance()
            com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl$TextTrackNamespaceContext r6 = new com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl$TextTrackNamespaceContext
            r14 = 0
            r6.<init>(r14)
            r14 = r6
            javax.xml.xpath.XPath r15 = r13.newXPath()
            r15.setNamespaceContext(r14)
            long r16 = latestTimestamp(r10)
            long[] r6 = r0.sampleDurations
            long r18 = r16 - r4
            r6[r7] = r18
            r18 = r16
            java.lang.String r4 = "/ttml:tt/ttml:body/ttml:div/@smpte:backgroundImage"
            javax.xml.xpath.XPathExpression r4 = r15.compile(r4)
            javax.xml.namespace.QName r5 = javax.xml.xpath.XPathConstants.NODESET
            java.lang.Object r5 = r4.evaluate(r10, r5)
            org.w3c.dom.NodeList r5 = (org.w3c.dom.NodeList) r5
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            java.util.HashSet r20 = new java.util.HashSet
            r20.<init>()
            r21 = r20
            r20 = 0
            r1 = r20
        L_0x00ec:
            r20 = r2
            int r2 = r5.getLength()
            if (r1 < r2) goto L_0x022d
            java.util.ArrayList r1 = new java.util.ArrayList
            r2 = r21
            r1.<init>(r2)
            r21 = r1
            r1 = r21
            java.util.List r1 = (java.util.List) r1
            java.util.Collections.sort(r1)
            r1 = 1
            java.util.Iterator r22 = r21.iterator()
        L_0x0109:
            boolean r2 = r22.hasNext()
            if (r2 != 0) goto L_0x01f2
            boolean r2 = r21.isEmpty()
            if (r2 != 0) goto L_0x01ce
            java.lang.String r2 = new java.lang.String
            r23 = r3
            java.io.FileInputStream r3 = new java.io.FileInputStream
            r3.<init>(r8)
            byte[] r3 = r0.streamToByteArray(r3)
            r2.<init>(r3)
            java.util.Set r3 = r6.entrySet()
            java.util.Iterator r3 = r3.iterator()
        L_0x012d:
            boolean r22 = r3.hasNext()
            if (r22 != 0) goto L_0x01a6
            r3 = r2
            java.util.ArrayList r22 = new java.util.ArrayList
            r22.<init>()
            r24 = r22
            r25 = r4
            java.util.List<com.googlecode.mp4parser.authoring.Sample> r4 = r0.samples
            r26 = r10
            com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl$1 r10 = new com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl$1
            r27 = r11
            r11 = r24
            r10.<init>(r3, r11)
            r4.add(r10)
            com.coremedia.iso.boxes.SubSampleInformationBox$SubSampleEntry$SubsampleEntry r4 = new com.coremedia.iso.boxes.SubSampleInformationBox$SubSampleEntry$SubsampleEntry
            r4.<init>()
            int r10 = com.coremedia.iso.Utf8.utf8StringLengthInBytes(r3)
            r24 = r12
            r28 = r13
            long r12 = (long) r10
            r4.setSubsampleSize(r12)
            java.util.List r10 = r9.getSubsampleEntries()
            r10.add(r4)
            java.util.Iterator r10 = r21.iterator()
        L_0x016a:
            boolean r12 = r10.hasNext()
            if (r12 != 0) goto L_0x0172
            goto L_0x01e4
        L_0x0172:
            java.lang.Object r12 = r10.next()
            java.lang.String r12 = (java.lang.String) r12
            java.io.File r13 = new java.io.File
            r22 = r3
            java.io.File r3 = r8.getParentFile()
            r13.<init>(r3, r12)
            r3 = r13
            r11.add(r3)
            com.coremedia.iso.boxes.SubSampleInformationBox$SubSampleEntry$SubsampleEntry r13 = new com.coremedia.iso.boxes.SubSampleInformationBox$SubSampleEntry$SubsampleEntry
            r13.<init>()
            r30 = r10
            r29 = r11
            long r10 = r3.length()
            r13.setSubsampleSize(r10)
            java.util.List r10 = r9.getSubsampleEntries()
            r10.add(r13)
            r3 = r22
            r11 = r29
            r10 = r30
            goto L_0x016a
        L_0x01a6:
            r25 = r4
            r26 = r10
            r27 = r11
            r24 = r12
            r28 = r13
            java.lang.Object r4 = r3.next()
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4
            java.lang.Object r10 = r4.getKey()
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
            java.lang.Object r11 = r4.getValue()
            java.lang.CharSequence r11 = (java.lang.CharSequence) r11
            java.lang.String r2 = r2.replace(r10, r11)
            r4 = r25
            r10 = r26
            r11 = r27
            goto L_0x012d
        L_0x01ce:
            r23 = r3
            r25 = r4
            r26 = r10
            r27 = r11
            r24 = r12
            r28 = r13
            java.util.List<com.googlecode.mp4parser.authoring.Sample> r2 = r0.samples
            com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl$2 r3 = new com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl$2
            r3.<init>(r8)
            r2.add(r3)
        L_0x01e4:
            int r7 = r7 + 1
            r1 = r32
            r4 = r18
            r2 = r20
            r3 = r23
            r6 = r24
            goto L_0x0046
        L_0x01f2:
            r23 = r3
            r25 = r4
            r26 = r10
            r27 = r11
            r24 = r12
            r28 = r13
            java.lang.Object r2 = r22.next()
            java.lang.String r2 = (java.lang.String) r2
            java.lang.String r3 = "."
            int r3 = r2.lastIndexOf(r3)
            java.lang.String r3 = r2.substring(r3)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r10 = "urn:dece:container:subtitleimageindex:"
            r4.<init>(r10)
            int r10 = r1 + 1
            r4.append(r1)
            r4.append(r3)
            java.lang.String r1 = r4.toString()
            r6.put(r2, r1)
            r1 = r10
            r3 = r23
            r4 = r25
            r10 = r26
            goto L_0x0109
        L_0x022d:
            r23 = r3
            r25 = r4
            r26 = r10
            r27 = r11
            r24 = r12
            r28 = r13
            r2 = r21
            org.w3c.dom.Node r3 = r5.item(r1)
            java.lang.String r3 = r3.getNodeValue()
            r2.add(r3)
            int r1 = r1 + 1
            r2 = r20
            r3 = r23
            goto L_0x00ec
        L_0x024e:
            r20 = r2
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "Within one Track all sample documents need to have the same language"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.tracks.SMPTETTTrackImpl.<init>(java.io.File[]):void");
    }

    /* access modifiers changed from: private */
    public byte[] streamToByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[8096];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            int read = input.read(buffer);
            int n = read;
            if (-1 == read) {
                return output.toByteArray();
            }
            output.write(buffer, 0, n);
        }
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSampleDurations() {
        long[] adoptedSampleDuration = new long[this.sampleDurations.length];
        for (int i = 0; i < adoptedSampleDuration.length; i++) {
            adoptedSampleDuration[i] = (this.sampleDurations[i] * this.trackMetaData.getTimescale()) / 1000;
        }
        return adoptedSampleDuration;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    public String getHandler() {
        return "subt";
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.subSampleInformationBox;
    }

    public void close() throws IOException {
    }

    private static class TextTrackNamespaceContext implements NamespaceContext {
        private TextTrackNamespaceContext() {
        }

        /* synthetic */ TextTrackNamespaceContext(TextTrackNamespaceContext textTrackNamespaceContext) {
            this();
        }

        public String getNamespaceURI(String prefix) {
            if (prefix.equals("ttml")) {
                return "http://www.w3.org/ns/ttml";
            }
            if (prefix.equals("smpte")) {
                return SMPTETTTrackImpl.SMPTE_TT_NAMESPACE;
            }
            return null;
        }

        public Iterator getPrefixes(String val) {
            return Arrays.asList(new String[]{"ttml", "smpte"}).iterator();
        }

        public String getPrefix(String uri) {
            if (uri.equals("http://www.w3.org/ns/ttml")) {
                return "ttml";
            }
            if (uri.equals(SMPTETTTrackImpl.SMPTE_TT_NAMESPACE)) {
                return "smpte";
            }
            return null;
        }
    }
}
