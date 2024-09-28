package com.litesuits.orm.db.model;

public class RelationKey {
    public String key1;
    public String key2;

    public boolean isOK() {
        return (this.key1 == null || this.key2 == null) ? false : true;
    }
}
