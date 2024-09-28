package com.bjz.comm.net.bean;

import java.util.List;

public class ResponseFcAttentionUsertBeanV1 {
    public List<Follows> Follows;
    public List<FcUserInfoBean> Users;

    public class Follows {
        public double CreateAt;
        public double CreateBy;
        public int EachOther;
        public double FollowID;
        public double FollowUID;
        public int ID;

        public Follows() {
        }

        public String toString() {
            return "Follows{ID=" + this.ID + ", FollowID=" + this.FollowID + ", FollowUID=" + this.FollowUID + ", CreateAt=" + this.CreateAt + ", CreateBy=" + this.CreateBy + ", EachOther=" + this.EachOther + '}';
        }
    }

    public String toString() {
        return "ResponseFcAttentionUsertBeanV1{Follows=" + this.Follows + ", Users=" + this.Users + '}';
    }
}
