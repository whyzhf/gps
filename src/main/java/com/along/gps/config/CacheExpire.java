package com.along.gps.config;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author why
 * 20191021
 * 设置过期时间，单位 秒，默认60秒，如果不设置默认30分钟
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheExpire {
	/**
	 * expire time, default 60s
	 */
	@AliasFor("expire")
	long value() default 60L;

	/**
	 * expire time, default 60s
	 */
	@AliasFor("value")
	long expire() default 60L;

}