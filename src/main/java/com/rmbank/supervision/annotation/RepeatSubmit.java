package com.rmbank.supervision.annotation;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

/**
 * Created by wangh on 15/4/28.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {
    @Value("${RepeatSubmit.interval}")
    public int intervalDefault=0;
    public int interval() default intervalDefault;
    // 是不是有事务
    public boolean isTransactional() default false;
}
