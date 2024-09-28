package com.googlecode.mp4parser.srt;

import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
import com.king.zxing.util.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

public class SrtParser {
    public static TextTrackImpl parse(InputStream is) throws IOException {
        LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
        TextTrackImpl track = new TextTrackImpl();
        while (r.readLine() != null) {
            String timeString = r.readLine();
            String lineString = "";
            while (true) {
                String readLine = r.readLine();
                String s = readLine;
                if (readLine == null || s.trim().equals("")) {
                    long startTime = parse(timeString.split("-->")[0]);
                    long endTime = parse(timeString.split("-->")[1]);
                    LineNumberReader r2 = r;
                    TextTrackImpl.Line line = r5;
                    TextTrackImpl track2 = track;
                    List<TextTrackImpl.Line> subs = track.getSubs();
                    TextTrackImpl.Line line2 = new TextTrackImpl.Line(startTime, endTime, lineString);
                    subs.add(line);
                    r = r2;
                    track = track2;
                } else {
                    lineString = String.valueOf(lineString) + s + "\n";
                }
            }
            long startTime2 = parse(timeString.split("-->")[0]);
            long endTime2 = parse(timeString.split("-->")[1]);
            LineNumberReader r22 = r;
            TextTrackImpl.Line line3 = line2;
            TextTrackImpl track22 = track;
            List<TextTrackImpl.Line> subs2 = track.getSubs();
            TextTrackImpl.Line line22 = new TextTrackImpl.Line(startTime2, endTime2, lineString);
            subs2.add(line3);
            r = r22;
            track = track22;
        }
        return track;
    }

    private static long parse(String in) {
        String str = in;
        return (Long.parseLong(str.split(LogUtils.COLON)[0].trim()) * 60 * 60 * 1000) + (60 * Long.parseLong(str.split(LogUtils.COLON)[1].trim()) * 1000) + (1000 * Long.parseLong(str.split(LogUtils.COLON)[2].split(",")[0].trim())) + Long.parseLong(str.split(LogUtils.COLON)[2].split(",")[1].trim());
    }
}
