/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      MappingNameResolver.java
 * Date:    18-5-1 下午1:49
 * Author: krun
 */

package com.krun.spring.extend.mapping.resolver;

import com.krun.spring.extend.mapping.DomainMapping;
import com.krun.spring.extend.mapping.RestDomainMapping;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 *
 * 此接口用于提供从类或方法的信息中提取一个可用的路径的映射实现。
 *
 * @author krun
 * @date 2018/05/01
 */
public interface MappingNameResolver {

	/**
	 * 当类或方法没有被 {@link RequestMapping} 所修饰，或使用了该注解但没有填写
	 *  <code>path</code> 或 <code>value</code> 属性，那么就会调用此方法以通过类或方法的信息来
	 * 提取一个可用的路径。
	 * @param clazz 需要映射一个类的路径（以与其中的方法路径进行组合）时，此参数即该类；需要映射一个方法的路径时，此参数为该方法所在的类。
	 * @param method 映射类的路径时此参数为空；映射方法的路径时此参数为该方法。
	 * @param path 类或方法被这些注解( {@link DomainMapping}、{@link RestDomainMapping}、{@link RequestMapping}) 修饰时，若
	 *             该注解上的 <code>path</code> 或 <code>value</code> 属性值不为 NULL 时，则会被作为此参数传入，否则此参数置 NUll.
	 * @return 通过类或方法的信息所映射到的一个路径，请确保该映射方式的结果（在类和方法的路径组合后）是唯一的。
	 */
	String[] resolve(Class<?> clazz, @Nullable Method method, @Nullable String[] path);

}
