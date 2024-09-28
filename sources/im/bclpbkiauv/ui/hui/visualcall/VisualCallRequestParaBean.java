package im.bclpbkiauv.ui.hui.visualcall;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import java.io.Serializable;

@Table("visualcall_para")
public class VisualCallRequestParaBean implements Serializable {
    private int admin_id;
    private String app_id;
    private String gslb;
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    private String json;
    private String strId;
    private String token;
    private boolean video;

    public boolean isVideo() {
        return this.video;
    }

    public void setVideo(boolean video2) {
        this.video = video2;
    }

    public String getStrId() {
        return this.strId;
    }

    public void setStrId(String strId2) {
        this.strId = strId2;
    }

    public int getAdmin_id() {
        return this.admin_id;
    }

    public void setAdmin_id(int admin_id2) {
        this.admin_id = admin_id2;
    }

    public String getApp_id() {
        return this.app_id;
    }

    public void setApp_id(String app_id2) {
        this.app_id = app_id2;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token2) {
        this.token = token2;
    }

    public String getGslb() {
        return this.gslb;
    }

    public void setGslb(String gslb2) {
        this.gslb = gslb2;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json2) {
        this.json = json2;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }
}
