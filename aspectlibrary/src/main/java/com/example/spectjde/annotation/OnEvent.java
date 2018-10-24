package com.example.spectjde.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by chenfeiyue on 17/6/30.
 * Description ：
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface OnEvent {
    String value() default "";
}
