package com.litesuits.orm.db.model;

import com.litesuits.orm.db.annotation.Column;
import java.io.Serializable;
import java.util.HashMap;

public class SQLiteTable implements Serializable {
    private static final long serialVersionUID = 6706520684759700566L;
    public HashMap<String, Integer> columns;
    public boolean isTableChecked;
    @Column("name")
    public String name;
    @Column("rootpage")
    public long rootpage;
    @Column("sql")
    public String sql;
    @Column("tbl_name")
    public String tbl_name;
    @Column("type")
    public String type;

    public String toString() {
        return "SQLiteTable{type='" + this.type + '\'' + ", name='" + this.name + '\'' + ", tbl_name='" + this.tbl_name + '\'' + ", rootpage=" + this.rootpage + ", sql='" + this.sql + '\'' + ", isTableChecked=" + this.isTableChecked + ", columns=" + this.columns + '}';
    }
}
