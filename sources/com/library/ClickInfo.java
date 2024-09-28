package com.library;

import java.util.List;

public class ClickInfo {
    public int mBottom;
    public List<DetailInfo> mDetailInfoList;
    public int mGroupId = -1;

    public ClickInfo(int bottom) {
        this.mBottom = bottom;
    }

    public ClickInfo(int bottom, List<DetailInfo> detailInfoList) {
        this.mBottom = bottom;
        this.mDetailInfoList = detailInfoList;
    }

    public static class DetailInfo {
        public int bottom;
        public int id;
        public int left;
        public int right;
        public int top;

        public DetailInfo(int id2, int left2, int right2, int top2, int bottom2) {
            this.id = id2;
            this.left = left2;
            this.right = right2;
            this.top = top2;
            this.bottom = bottom2;
        }
    }
}
