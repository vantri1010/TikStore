package com.litesuits.orm.db.enums;

public enum Strategy {
    ROLLBACK(" ROLLBACK "),
    ABORT(" ABORT "),
    FAIL(" FAIL "),
    IGNORE(" IGNORE "),
    REPLACE(" REPLACE ");
    
    public String sql;

    private Strategy(String sql2) {
        this.sql = sql2;
    }

    public String getSql() {
        return this.sql;
    }
}
