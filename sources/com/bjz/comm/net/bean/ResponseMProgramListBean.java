package com.bjz.comm.net.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseMProgramListBean implements Serializable {
    private PageBean Page;
    private ArrayList<MiniProgramBean> Rows;

    public PageBean getPage() {
        return this.Page;
    }

    public void setPage(PageBean Page2) {
        this.Page = Page2;
    }

    public ArrayList<MiniProgramBean> getRows() {
        return this.Rows;
    }

    public void setRows(ArrayList<MiniProgramBean> Rows2) {
        this.Rows = Rows2;
    }

    public static class PageBean {
        private int CurrentPage;
        private int Limit;
        private int Offset;
        private String Order;
        private int PageCount;
        private int PageSize;
        private String Sort;
        private int TotalRows;

        public int getTotalRows() {
            return this.TotalRows;
        }

        public void setTotalRows(int TotalRows2) {
            this.TotalRows = TotalRows2;
        }

        public int getPageCount() {
            return this.PageCount;
        }

        public void setPageCount(int PageCount2) {
            this.PageCount = PageCount2;
        }

        public int getPageSize() {
            return this.PageSize;
        }

        public void setPageSize(int PageSize2) {
            this.PageSize = PageSize2;
        }

        public int getCurrentPage() {
            return this.CurrentPage;
        }

        public void setCurrentPage(int CurrentPage2) {
            this.CurrentPage = CurrentPage2;
        }

        public int getOffset() {
            return this.Offset;
        }

        public void setOffset(int Offset2) {
            this.Offset = Offset2;
        }

        public int getLimit() {
            return this.Limit;
        }

        public void setLimit(int Limit2) {
            this.Limit = Limit2;
        }

        public String getOrder() {
            return this.Order;
        }

        public void setOrder(String Order2) {
            this.Order = Order2;
        }

        public String getSort() {
            return this.Sort;
        }

        public void setSort(String Sort2) {
            this.Sort = Sort2;
        }
    }
}
