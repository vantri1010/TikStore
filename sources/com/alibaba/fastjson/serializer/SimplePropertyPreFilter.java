package com.alibaba.fastjson.serializer;

import java.util.HashSet;
import java.util.Set;

public class SimplePropertyPreFilter implements PropertyPreFilter {
    private final Class<?> clazz;
    private final Set<String> excludes;
    private final Set<String> includes;

    public SimplePropertyPreFilter(String... properties) {
        this((Class<?>) null, properties);
    }

    public SimplePropertyPreFilter(Class<?> clazz2, String... properties) {
        this.includes = new HashSet();
        this.excludes = new HashSet();
        this.clazz = clazz2;
        for (String item : properties) {
            if (item != null) {
                this.includes.add(item);
            }
        }
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Set<String> getIncludes() {
        return this.includes;
    }

    public Set<String> getExcludes() {
        return this.excludes;
    }

    public boolean apply(JSONSerializer serializer, Object source, String name) {
        if (source == null) {
            return true;
        }
        Class<?> cls = this.clazz;
        if (cls != null && !cls.isInstance(source)) {
            return true;
        }
        if (this.excludes.contains(name)) {
            return false;
        }
        if (this.includes.size() == 0 || this.includes.contains(name)) {
            return true;
        }
        return false;
    }
}
