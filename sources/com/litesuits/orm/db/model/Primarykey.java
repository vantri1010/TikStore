package com.litesuits.orm.db.model;

import com.litesuits.orm.db.enums.AssignType;
import java.lang.reflect.Field;

public class Primarykey extends Property {
    private static final long serialVersionUID = 2304252505493855513L;
    public AssignType assign;

    public Primarykey(Property p, AssignType assign2) {
        this(p.column, p.field, p.classType, assign2);
    }

    public Primarykey(String column, Field field, int classType, AssignType assign2) {
        super(column, field, classType);
        this.assign = assign2;
    }

    public boolean isAssignedBySystem() {
        return this.assign == AssignType.AUTO_INCREMENT;
    }

    public boolean isAssignedByMyself() {
        return this.assign == AssignType.BY_MYSELF;
    }
}
