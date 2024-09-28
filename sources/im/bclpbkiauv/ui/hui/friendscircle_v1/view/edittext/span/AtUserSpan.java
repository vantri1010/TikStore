package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\b\u0018\u00002\u00020\u00012\u00020\u0002B/\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\b\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\t\u0010\u001a\u001a\u00020\u0004HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001e\u001a\u00020\nHÆ\u0003J=\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#HÖ\u0003J\u0006\u0010$\u001a\u00020%J\t\u0010&\u001a\u00020\u0004HÖ\u0001J\u0010\u0010'\u001a\u00020!2\u0006\u0010(\u001a\u00020%H\u0016J\b\u0010)\u001a\u00020\u0006H\u0016R\u001a\u0010\t\u001a\u00020\nX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\b\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0011\"\u0004\b\u0015\u0010\u0013R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0011\"\u0004\b\u0019\u0010\u0013¨\u0006*"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/span/AtUserSpan;", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/span/DataBindingSpan;", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/span/DirtySpan;", "userID", "", "nickName", "", "userName", "showName", "accessHash", "", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V", "getAccessHash", "()J", "setAccessHash", "(J)V", "getNickName", "()Ljava/lang/String;", "setNickName", "(Ljava/lang/String;)V", "getShowName", "setShowName", "getUserID", "()I", "getUserName", "setUserName", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "getSpannedName", "Landroid/text/Spannable;", "hashCode", "isDirty", "text", "toString", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: AtUserSpan.kt */
public final class AtUserSpan implements DataBindingSpan, DirtySpan {
    private long accessHash;
    private String nickName;
    private String showName;
    private final int userID;
    private String userName;

    public static /* synthetic */ AtUserSpan copy$default(AtUserSpan atUserSpan, int i, String str, String str2, String str3, long j, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = atUserSpan.userID;
        }
        if ((i2 & 2) != 0) {
            str = atUserSpan.nickName;
        }
        String str4 = str;
        if ((i2 & 4) != 0) {
            str2 = atUserSpan.userName;
        }
        String str5 = str2;
        if ((i2 & 8) != 0) {
            str3 = atUserSpan.showName;
        }
        String str6 = str3;
        if ((i2 & 16) != 0) {
            j = atUserSpan.accessHash;
        }
        return atUserSpan.copy(i, str4, str5, str6, j);
    }

    public final int component1() {
        return this.userID;
    }

    public final String component2() {
        return this.nickName;
    }

    public final String component3() {
        return this.userName;
    }

    public final String component4() {
        return this.showName;
    }

    public final long component5() {
        return this.accessHash;
    }

    public final AtUserSpan copy(int i, String str, String str2, String str3, long j) {
        Intrinsics.checkParameterIsNotNull(str, "nickName");
        Intrinsics.checkParameterIsNotNull(str3, "showName");
        return new AtUserSpan(i, str, str2, str3, j);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AtUserSpan)) {
            return false;
        }
        AtUserSpan atUserSpan = (AtUserSpan) obj;
        return this.userID == atUserSpan.userID && Intrinsics.areEqual((Object) this.nickName, (Object) atUserSpan.nickName) && Intrinsics.areEqual((Object) this.userName, (Object) atUserSpan.userName) && Intrinsics.areEqual((Object) this.showName, (Object) atUserSpan.showName) && this.accessHash == atUserSpan.accessHash;
    }

    public int hashCode() {
        int i = this.userID * 31;
        String str = this.nickName;
        int i2 = 0;
        int hashCode = (i + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.userName;
        int hashCode2 = (hashCode + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.showName;
        if (str3 != null) {
            i2 = str3.hashCode();
        }
        long j = this.accessHash;
        return ((hashCode2 + i2) * 31) + ((int) (j ^ (j >>> 32)));
    }

    public AtUserSpan(int userID2, String nickName2, String userName2, String showName2, long accessHash2) {
        Intrinsics.checkParameterIsNotNull(nickName2, "nickName");
        Intrinsics.checkParameterIsNotNull(showName2, "showName");
        this.userID = userID2;
        this.nickName = nickName2;
        this.userName = userName2;
        this.showName = showName2;
        this.accessHash = accessHash2;
    }

    public final long getAccessHash() {
        return this.accessHash;
    }

    public final String getNickName() {
        return this.nickName;
    }

    public final String getShowName() {
        return this.showName;
    }

    public final int getUserID() {
        return this.userID;
    }

    public final String getUserName() {
        return this.userName;
    }

    public final void setAccessHash(long j) {
        this.accessHash = j;
    }

    public final void setNickName(String str) {
        Intrinsics.checkParameterIsNotNull(str, "<set-?>");
        this.nickName = str;
    }

    public final void setShowName(String str) {
        Intrinsics.checkParameterIsNotNull(str, "<set-?>");
        this.showName = str;
    }

    public final void setUserName(String str) {
        this.userName = str;
    }

    public final Spannable getSpannedName() {
        SpannableString spannableString = new SpannableString(this.showName);
        SpannableString $this$apply = spannableString;
        $this$apply.setSpan(new ForegroundColorSpan(Color.parseColor("#5080B5")), 0, $this$apply.length(), 33);
        return spannableString;
    }

    public boolean isDirty(Spannable text) {
        Intrinsics.checkParameterIsNotNull(text, "text");
        int spanStart = text.getSpanStart(this);
        int spanEnd = text.getSpanEnd(this);
        return spanStart >= 0 && spanEnd >= 0 && (Intrinsics.areEqual((Object) text.subSequence(spanStart, spanEnd).toString(), (Object) this.showName) ^ true);
    }

    public String toString() {
        return "User(userID=" + this.userID + ", nickName='" + this.nickName + "', userName='" + this.userName + "', showName='" + this.showName + "', accessHash=" + this.accessHash + ')';
    }
}
