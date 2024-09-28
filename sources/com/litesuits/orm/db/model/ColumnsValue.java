package com.litesuits.orm.db.model;

import com.litesuits.orm.db.assit.Checker;
import java.util.HashMap;
import java.util.Map;

public class ColumnsValue {
    public String[] columns;
    private Map<String, Object> map = new HashMap();

    public ColumnsValue(Map<String, Object> map2) {
        if (!Checker.isEmpty((Map<?, ?>) map2)) {
            this.columns = new String[map2.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : map2.entrySet()) {
                this.columns[i] = entry.getKey();
                i++;
            }
            this.map = map2;
        }
    }

    public ColumnsValue(String[] columns2) {
        this.columns = columns2;
        for (String key : columns2) {
            this.map.put(key, (Object) null);
        }
    }

    public ColumnsValue(String[] columns2, Object[] values) {
        this.columns = columns2;
        if (values == null) {
            for (String key : columns2) {
                this.map.put(key, (Object) null);
            }
        } else if (columns2.length == values.length) {
            int i = 0;
            String[] arr$ = columns2;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                this.map.put(arr$[i$], values[i]);
                i$++;
                i++;
            }
        } else {
            throw new IllegalArgumentException("length of columns and values must be the same");
        }
    }

    public boolean checkColumns() {
        if (this.columns != null) {
            return true;
        }
        throw new IllegalArgumentException("columns must not be null");
    }

    public Object getValue(String key) {
        return this.map.get(key);
    }
}
