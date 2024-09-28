package com.litesuits.orm.db.model;

import com.litesuits.orm.db.utils.DataUtil;
import java.io.Serializable;
import java.lang.reflect.Field;

public class Property implements Serializable {
    private static final long serialVersionUID = 1542861322620643038L;
    public int classType = 0;
    public String column;
    public Field field;

    public Property(String column2, Field field2) {
        this.column = column2;
        this.field = field2;
        if (0 <= 0) {
            this.classType = DataUtil.getFieldClassType(field2);
        }
    }

    public Property(String column2, Field field2, int classType2) {
        this.column = column2;
        this.field = field2;
        if (classType2 <= 0) {
            this.classType = DataUtil.getFieldClassType(field2);
        }
        this.classType = classType2;
    }

    public String toString() {
        return "Property{column='" + this.column + '\'' + ", field=" + this.field + ", classType=" + this.classType + '}';
    }
}
