package com.litesuits.orm.db.model;

public enum ConflictAlgorithm {
    None(" "),
    Rollback(" OR ROLLBACK "),
    Abort(" OR ABORT "),
    Fail(" OR FAIL "),
    Ignore(" OR IGNORE "),
    Replace(" OR REPLACE ");
    
    private String algorithm;

    private ConflictAlgorithm(String algorithm2) {
        this.algorithm = algorithm2;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }
}
