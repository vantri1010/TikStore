package im.bclpbkiauv.javaBean;

import android.os.Parcel;
import android.os.Parcelable;

public class AllTokenResponse implements Parcelable {
    public static final Parcelable.Creator<AllTokenResponse> CREATOR = new Parcelable.Creator<AllTokenResponse>() {
        public AllTokenResponse createFromParcel(Parcel in) {
            return new AllTokenResponse(in);
        }

        public AllTokenResponse[] newArray(int size) {
            return new AllTokenResponse[size];
        }
    };
    private String gametoken;
    private String momenttoken;

    public AllTokenResponse(String momenttoken2, String gametoken2) {
        this.momenttoken = momenttoken2;
        this.gametoken = gametoken2;
    }

    protected AllTokenResponse(Parcel in) {
        this.momenttoken = in.readString();
        this.gametoken = in.readString();
    }

    public String getMomenttoken() {
        return this.momenttoken;
    }

    public void setMomenttoken(String momenttoken2) {
        this.momenttoken = momenttoken2;
    }

    public String getGametoken() {
        return this.gametoken;
    }

    public void setGametoken(String gametoken2) {
        this.gametoken = gametoken2;
    }

    public String toString() {
        return "AllTokenResponse{momenttoken='" + this.momenttoken + '\'' + ", gametoken='" + this.gametoken + '\'' + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.momenttoken);
        dest.writeString(this.gametoken);
    }
}
