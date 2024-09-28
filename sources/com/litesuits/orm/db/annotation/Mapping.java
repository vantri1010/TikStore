package com.litesuits.orm.db.annotation;

import com.litesuits.orm.db.enums.Relation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
    Relation value();
}
