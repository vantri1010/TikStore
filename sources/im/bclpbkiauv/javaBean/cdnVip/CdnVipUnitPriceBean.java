package im.bclpbkiauv.javaBean.cdnVip;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CdnVipUnitPriceBean {
    private List<UnitPriceBean> CdnPrice;

    public List<UnitPriceBean> getCdnPrice() {
        List<UnitPriceBean> list = this.CdnPrice;
        if (list != null) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        this.CdnPrice = arrayList;
        return arrayList;
    }

    public static class UnitPriceBean {
        private int level;
        private double price;

        public int getLevel() {
            return this.level;
        }

        public double getPrice() {
            return this.price;
        }

        public String getPriceStandard(int scale) {
            return new BigDecimal(this.price + "").divide(new BigDecimal("100")).setScale(2, RoundingMode.HALF_DOWN).toString();
        }
    }
}
