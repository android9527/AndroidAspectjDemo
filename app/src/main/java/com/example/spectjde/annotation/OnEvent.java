package com.example.spectjde.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by chenfeiyue on 17/6/30.
 * Description ：
 */
@Target({METHOD})
@Retention(CLASS)
public @interface OnEvent {
    String value() default "";
}
