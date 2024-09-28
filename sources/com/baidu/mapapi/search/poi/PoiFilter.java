package com.baidu.mapapi.search.poi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.king.zxing.util.LogUtils;
import java.util.HashMap;
import java.util.Map;

public final class PoiFilter implements Parcelable {
    public static final Parcelable.Creator<PoiFilter> CREATOR = new c();
    /* access modifiers changed from: private */
    public static Map<SortName, String> f = new HashMap();
    private String a = "";
    private String b = "";
    private String c = "";
    private String d = "";
    private String e = "";

    public static final class Builder {
        private String a;
        private String b;
        private String c;
        private String d;
        private String e;

        public Builder() {
            PoiFilter.f.put(SortName.HotelSortName.DEFAULT, "default");
            PoiFilter.f.put(SortName.HotelSortName.HOTEL_LEVEL, "level");
            PoiFilter.f.put(SortName.HotelSortName.HOTEL_PRICE, "price");
            PoiFilter.f.put(SortName.HotelSortName.HOTEL_DISTANCE, "distance");
            PoiFilter.f.put(SortName.HotelSortName.HOTEL_HEALTH_SCORE, "health_score");
            PoiFilter.f.put(SortName.HotelSortName.HOTEL_TOTAL_SCORE, "total_score");
            PoiFilter.f.put(SortName.CaterSortName.DEFAULT, "default");
            PoiFilter.f.put(SortName.CaterSortName.CATER_DISTANCE, "distance");
            PoiFilter.f.put(SortName.CaterSortName.CATER_PRICE, "price");
            PoiFilter.f.put(SortName.CaterSortName.CATER_OVERALL_RATING, "overall_rating");
            PoiFilter.f.put(SortName.CaterSortName.CATER_SERVICE_RATING, "service_rating");
            PoiFilter.f.put(SortName.CaterSortName.CATER_TASTE_RATING, "taste_rating");
            PoiFilter.f.put(SortName.LifeSortName.DEFAULT, "default");
            PoiFilter.f.put(SortName.LifeSortName.PRICE, "price");
            PoiFilter.f.put(SortName.LifeSortName.LIFE_COMMENT_RATING, "comment_num");
            PoiFilter.f.put(SortName.LifeSortName.LIFE_OVERALL_RATING, "overall_rating");
            PoiFilter.f.put(SortName.LifeSortName.DISTANCE, "distance");
        }

        public PoiFilter build() {
            return new PoiFilter(this.a, this.b, this.c, this.e, this.d);
        }

        public Builder industryType(IndustryType industryType) {
            int i = d.a[industryType.ordinal()];
            this.a = i != 1 ? i != 2 ? i != 3 ? "" : "life" : "cater" : "hotel";
            return this;
        }

        public Builder isDiscount(boolean z) {
            this.e = z ? "1" : "0";
            return this;
        }

        public Builder isGroupon(boolean z) {
            this.d = z ? "1" : "0";
            return this;
        }

        public Builder sortName(SortName sortName) {
            if (!TextUtils.isEmpty(this.a) && sortName != null) {
                this.b = (String) PoiFilter.f.get(sortName);
            }
            return this;
        }

        public Builder sortRule(int i) {
            this.c = i + "";
            return this;
        }
    }

    public enum IndustryType {
        HOTEL,
        CATER,
        LIFE
    }

    public interface SortName {

        public enum CaterSortName implements SortName {
            DEFAULT,
            CATER_PRICE,
            CATER_DISTANCE,
            CATER_TASTE_RATING,
            CATER_OVERALL_RATING,
            CATER_SERVICE_RATING
        }

        public enum HotelSortName implements SortName {
            DEFAULT,
            HOTEL_PRICE,
            HOTEL_DISTANCE,
            HOTEL_TOTAL_SCORE,
            HOTEL_LEVEL,
            HOTEL_HEALTH_SCORE
        }

        public enum LifeSortName implements SortName {
            DEFAULT,
            PRICE,
            DISTANCE,
            LIFE_OVERALL_RATING,
            LIFE_COMMENT_RATING
        }
    }

    protected PoiFilter(Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readString();
        this.c = parcel.readString();
        this.e = parcel.readString();
        this.d = parcel.readString();
    }

    PoiFilter(String str, String str2, String str3, String str4, String str5) {
        this.a = str;
        this.b = str2;
        this.c = str3;
        this.e = str4;
        this.d = str5;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(this.a)) {
            sb.append("industry_type:");
            sb.append(this.a);
            sb.append(LogUtils.VERTICAL);
        }
        if (!TextUtils.isEmpty(this.b)) {
            sb.append("sort_name:");
            sb.append(this.b);
            sb.append(LogUtils.VERTICAL);
        }
        if (!TextUtils.isEmpty(this.c)) {
            sb.append("sort_rule:");
            sb.append(this.c);
            sb.append(LogUtils.VERTICAL);
        }
        if (!TextUtils.isEmpty(this.e)) {
            sb.append("discount:");
            sb.append(this.e);
            sb.append(LogUtils.VERTICAL);
        }
        if (!TextUtils.isEmpty(this.d)) {
            sb.append("groupon:");
            sb.append(this.d);
            sb.append(LogUtils.VERTICAL);
        }
        if (!TextUtils.isEmpty(sb.toString())) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeString(this.b);
        parcel.writeString(this.c);
        parcel.writeString(this.e);
        parcel.writeString(this.d);
    }
}
