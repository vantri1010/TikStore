package im.bclpbkiauv.javaBean.fc;

import java.io.Serializable;

public class RequestFcReportBean implements Serializable {
    private long ForumID;
    private String ReportString;
    private long ReportType;
    private long UserID;

    public RequestFcReportBean() {
    }

    public RequestFcReportBean(long userID, long forumID, long reportType, String reportString) {
        this.UserID = userID;
        this.ForumID = forumID;
        this.ReportType = reportType;
        this.ReportString = reportString;
    }

    public long getUserID() {
        return this.UserID;
    }

    public void setUserID(long userID) {
        this.UserID = userID;
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public long getReportType() {
        return this.ReportType;
    }

    public void setReportType(long reportType) {
        this.ReportType = reportType;
    }

    public String getReportString() {
        return this.ReportString;
    }

    public void setReportString(String reportString) {
        this.ReportString = reportString;
    }
}
