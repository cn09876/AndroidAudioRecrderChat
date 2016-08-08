package com.words.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by uosim on 16/8/5.
 */


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SwCheck1
{
    public int value();
    String txt();
}
