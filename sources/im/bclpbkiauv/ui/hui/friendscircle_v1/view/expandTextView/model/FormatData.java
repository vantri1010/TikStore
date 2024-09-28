package im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.model;

import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.expandViewModel.LinkType;
import java.util.List;

public class FormatData {
    private String formatedContent;
    private List<PositionData> positionDatas;

    public String getFormatedContent() {
        return this.formatedContent;
    }

    public void setFormatedContent(String formatedContent2) {
        this.formatedContent = formatedContent2;
    }

    public List<PositionData> getPositionDatas() {
        return this.positionDatas;
    }

    public void setPositionDatas(List<PositionData> positionDatas2) {
        this.positionDatas = positionDatas2;
    }

    public static class PositionData {
        private int end;
        private FCEntitysResponse fcEntitysResponse;
        private String selfAim;
        private String selfContent;
        private int start;
        private LinkType type;
        private String url;

        public PositionData(int start2, int end2, String url2, LinkType type2) {
            this.start = start2;
            this.end = end2;
            this.url = url2;
            this.type = type2;
        }

        public PositionData(int start2, int end2, String selfAim2, String selfContent2, LinkType type2) {
            this.start = start2;
            this.end = end2;
            this.selfAim = selfAim2;
            this.selfContent = selfContent2;
            this.type = type2;
        }

        public PositionData(int start2, int end2, String url2, LinkType type2, FCEntitysResponse fcEntitysResponse2) {
            this.start = start2;
            this.end = end2;
            this.url = url2;
            this.type = type2;
            this.selfAim = this.selfAim;
            this.selfContent = this.selfContent;
            this.fcEntitysResponse = fcEntitysResponse2;
        }

        public String getSelfAim() {
            return this.selfAim;
        }

        public void setSelfAim(String selfAim2) {
            this.selfAim = selfAim2;
        }

        public String getSelfContent() {
            return this.selfContent;
        }

        public void setSelfContent(String selfContent2) {
            this.selfContent = selfContent2;
        }

        public LinkType getType() {
            return this.type;
        }

        public void setType(LinkType type2) {
            this.type = type2;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url2) {
            this.url = url2;
        }

        public int getStart() {
            return this.start;
        }

        public void setStart(int start2) {
            this.start = start2;
        }

        public int getEnd() {
            return this.end;
        }

        public void setEnd(int end2) {
            this.end = end2;
        }

        public FCEntitysResponse getFcEntitysResponse() {
            return this.fcEntitysResponse;
        }

        public void setFcEntitysResponse(FCEntitysResponse fcEntitysResponse2) {
            this.fcEntitysResponse = fcEntitysResponse2;
        }

        public String toString() {
            return "PositionData{start=" + this.start + ", end=" + this.end + ", url='" + this.url + '\'' + ", type=" + this.type + ", selfAim='" + this.selfAim + '\'' + ", selfContent='" + this.selfContent + '\'' + ", fcEntitysResponse=" + this.fcEntitysResponse + '}';
        }
    }
}
