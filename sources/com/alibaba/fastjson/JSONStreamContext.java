package com.alibaba.fastjson;

class JSONStreamContext {
    static final int ArrayValue = 1005;
    static final int PropertyKey = 1002;
    static final int PropertyValue = 1003;
    static final int StartArray = 1004;
    static final int StartObject = 1001;
    private final JSONStreamContext parent;
    private int state;

    public JSONStreamContext(JSONStreamContext parent2, int state2) {
        this.parent = parent2;
        this.state = state2;
    }

    public JSONStreamContext getParent() {
        return this.parent;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state2) {
        this.state = state2;
    }
}
