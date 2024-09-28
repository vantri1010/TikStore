package com.litesuits.orm.db.model;

import com.litesuits.orm.db.annotation.Column;
import java.io.Serializable;

public class SQLiteColumn implements Serializable {
    private static final long serialVersionUID = 8822000632819424751L;
    @Column("cid")
    public long cid;
    @Column("dflt_value")
    public String dflt_value;
    @Column("name")
    public String name;
    @Column("notnull")
    public short notnull;
    @Column("pk")
    public short pk;
    @Column("type")
    public String type;

    public String toString() {
        return "Column [cid=" + this.cid + ", name=" + this.name + ", type=" + this.type + ", notnull=" + this.notnull + ", dflt_value=" + this.dflt_value + ", pk=" + this.pk + "]";
    }
}
