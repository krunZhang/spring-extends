/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      AbstractRequestConditionConverter.java
 * Date:    18-5-3 下午2:25
 * Author: krun
 */

package com.krun.spring.extend.route.convert;

import com.krun.spring.extend.route.Handler;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

/**
 * @author krun
 * @date 2018/05/03
 */
public abstract class AbstractRequestConditionConverter <E extends RequestCondition> {

	protected E condition;

	@SuppressWarnings("unchecked")
	public abstract void setCondition(RequestMappingInfo info);

	public abstract void setValuesToHandler(Handler handler);
	protected String[] getValues() {
		String[] values = values();
		return values.length == 0 ? null : values;
	}
	protected abstract String[] values ();

	public static AbstractRequestConditionConverter[] converters() {
		return new AbstractRequestConditionConverter[] {
				new MethodsConditionConverter(),
				new HeadersConditionConverter(),
				new ParamsConditionConverter(),
				new ProducesConditionConverter(),
				new ConsumesConditionConverter()
		};
	}

	protected static String[] getStrings (Set<NameValueExpression<String>> expressions) {
		String[]                         values      = new String[expressions.size()];
		NameValueExpression[]            es          = new NameValueExpression[expressions.size()];
		expressions.toArray(es);
		for (int i = 0; i < es.length; i++) {
			values[i] = (String) es[i].getName();
		}
		return values;
	}

}
