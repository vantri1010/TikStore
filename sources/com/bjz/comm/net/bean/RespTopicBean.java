package com.bjz.comm.net.bean;

import java.util.ArrayList;
import java.util.List;

public class RespTopicBean {
    private List<Item> Topics;

    public static class Item {
        public int ID;
        public int Status;
        public String Subtitle;
        public int Tag;
        public int TopicID;
        public String TopicName;
        public int TypeID;
    }

    public List<Item> getTopics() {
        List<Item> list = this.Topics;
        return list == null ? new ArrayList() : list;
    }
}
