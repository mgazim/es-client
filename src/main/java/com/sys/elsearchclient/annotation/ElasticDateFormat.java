package com.sys.elsearchclient.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElasticDateFormat {

    String value() default BASIC_DATE_TIME;

    String BASIC_DATE      = "basic_date";
    String BASIC_DATE_TIME = "basic_date_time";
}
