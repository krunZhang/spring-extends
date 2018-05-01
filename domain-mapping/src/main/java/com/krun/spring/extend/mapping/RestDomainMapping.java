/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      RestDomainMapping.java
 * Date:    18-5-1 下午2:02
 * Author: krun
 */

package com.krun.spring.extend.mapping;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用此注解标记一个类则等效于为该类同时使用下列注解:<br/>
 * {@link RestController}<br/>
 * {@link RequestMapping}<br/>
 *
 * 此时如果该类内的某个公开非静态方法未使用 {@link RequestMapping} 标记，则会用该方法的方法名为其生成一个路径，
 * 且该方法会被视为使用 {@link ResponseBody} 注解所修饰。
 * @author krun
 * @date 2018/05/01
 */
@Target (ElementType.TYPE)
@Retention (RetentionPolicy.RUNTIME)
@RestController
public @interface RestDomainMapping {

	String root() default "";
	String name() default "";
	@AliasFor ("path")
	String[] value() default {};
	@AliasFor("value")
	String[] path() default {};
	RequestMethod[] method() default {};
	String[] params() default {};
	String[] headers() default {};
	String[] consumes() default {};
	String[] produces() default {};

}
