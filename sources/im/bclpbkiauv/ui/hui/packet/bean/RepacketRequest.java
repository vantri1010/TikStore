package im.bclpbkiauv.ui.hui.packet.bean;

import im.bclpbkiauv.javaBean.hongbao.UnifyBean;

public class RepacketRequest extends UnifyBean {
    private String fixedAmount;
    private String grantType;
    private String groups;
    private String groupsName;
    private Integer number;
    private String redType;

    public void setRedType(String redType2) {
        this.redType = redType2;
    }

    public void setGrantType(String grantType2) {
        this.grantType = grantType2;
    }

    public void setGroups(String groups2) {
        this.groups = groups2;
    }

    public void setGroupsName(String groupsName2) {
        this.groupsName = groupsName2;
    }

    public void setFixedAmount(String fixedAmount2) {
        this.fixedAmount = fixedAmount2;
    }

    public void setNumber(Integer number2) {
        this.number = number2;
    }
}
