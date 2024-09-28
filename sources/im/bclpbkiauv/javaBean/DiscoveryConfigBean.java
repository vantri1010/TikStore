package im.bclpbkiauv.javaBean;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class DiscoveryConfigBean {
    private List<DataBean> Data;
    private DataBean companyData;

    public DataBean getCompanyData(String compay) {
        List<DataBean> list;
        if (this.companyData == null && !TextUtils.isEmpty(compay) && (list = this.Data) != null && !list.isEmpty()) {
            int i = 0;
            while (true) {
                if (i < this.Data.size()) {
                    DataBean d = this.Data.get(i);
                    if (d != null && compay.equals(d.Compay)) {
                        this.companyData = this.Data.get(i);
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
        return this.companyData;
    }

    public List<DataBean> getData() {
        return this.Data;
    }

    public void setData(List<DataBean> Data2) {
        this.Data = Data2;
    }

    public static class DataBean {
        private String CompanyName;
        private int CompanyNo;
        /* access modifiers changed from: private */
        public String Compay;
        private List<GBean> G;
        private List<SBean> S;

        public int getCompanyNo() {
            return this.CompanyNo;
        }

        public void setCompanyNo(int CompanyNo2) {
            this.CompanyNo = CompanyNo2;
        }

        public String getCompay() {
            return this.Compay;
        }

        public void setCompay(String Compay2) {
            this.Compay = Compay2;
        }

        public String getCompanyName() {
            return this.CompanyName;
        }

        public void setCompanyName(String CompanyName2) {
            this.CompanyName = CompanyName2;
        }

        public List<GBean> getG() {
            List<GBean> list = this.G;
            if (list != null) {
                return list;
            }
            ArrayList arrayList = new ArrayList();
            this.G = arrayList;
            return arrayList;
        }

        public void setG(List<GBean> G2) {
            this.G = G2;
        }

        public List<SBean> getS() {
            List<SBean> list = this.S;
            if (list != null) {
                return list;
            }
            ArrayList arrayList = new ArrayList();
            this.S = arrayList;
            return arrayList;
        }

        public void setS(List<SBean> S2) {
            this.S = S2;
        }

        public static class GBean {
            private int No;
            private String Pic;
            private String Url;

            public int getNo() {
                return this.No;
            }

            public void setNo(int No2) {
                this.No = No2;
            }

            public String getPic() {
                return this.Pic;
            }

            public void setPic(String Pic2) {
                this.Pic = Pic2;
            }

            public String getUrl() {
                return this.Url;
            }

            public void setUrl(String Url2) {
                this.Url = Url2;
            }
        }

        public static class SBean {
            private String Logo;
            private int No;
            private String Title;
            private String Url;

            public int getNo() {
                return this.No;
            }

            public void setNo(int No2) {
                this.No = No2;
            }

            public String getTitle() {
                return this.Title;
            }

            public void setTitle(String Title2) {
                this.Title = Title2;
            }

            public String getUrl() {
                return this.Url;
            }

            public void setUrl(String Url2) {
                this.Url = Url2;
            }

            public String getLogo() {
                return this.Logo;
            }

            public void setLogo(String logo) {
                this.Logo = logo;
            }
        }
    }
}
