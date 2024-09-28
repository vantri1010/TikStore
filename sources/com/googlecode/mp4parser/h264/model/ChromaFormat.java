package com.googlecode.mp4parser.h264.model;

public class ChromaFormat {
    public static ChromaFormat MONOCHROME = new ChromaFormat(0, 0, 0);
    public static ChromaFormat YUV_420 = new ChromaFormat(1, 2, 2);
    public static ChromaFormat YUV_422 = new ChromaFormat(2, 2, 1);
    public static ChromaFormat YUV_444 = new ChromaFormat(3, 1, 1);
    private int id;
    private int subHeight;
    private int subWidth;

    public ChromaFormat(int id2, int subWidth2, int subHeight2) {
        this.id = id2;
        this.subWidth = subWidth2;
        this.subHeight = subHeight2;
    }

    public static ChromaFormat fromId(int id2) {
        ChromaFormat chromaFormat = MONOCHROME;
        if (id2 == chromaFormat.id) {
            return chromaFormat;
        }
        ChromaFormat chromaFormat2 = YUV_420;
        if (id2 == chromaFormat2.id) {
            return chromaFormat2;
        }
        ChromaFormat chromaFormat3 = YUV_422;
        if (id2 == chromaFormat3.id) {
            return chromaFormat3;
        }
        ChromaFormat chromaFormat4 = YUV_444;
        if (id2 == chromaFormat4.id) {
            return chromaFormat4;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    public int getSubWidth() {
        return this.subWidth;
    }

    public int getSubHeight() {
        return this.subHeight;
    }

    public String toString() {
        return "ChromaFormat{\nid=" + this.id + ",\n" + " subWidth=" + this.subWidth + ",\n" + " subHeight=" + this.subHeight + '}';
    }
}
