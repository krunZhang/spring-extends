/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      ParamsConditionConverter.java
 * Date:    18-5-3 下午2:38
 * Author: krun
 */

package com.krun.spring.extend.route.convert;

import com.krun.spring.extend.route.Handler;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**
 * @author krun
 * @date 2018/05/03
 */
public class ParamsConditionConverter extends AbstractRequestConditionConverter<ParamsRequestCondition> {

	@Override
	public void setCondition (RequestMappingInfo info) {
		condition = info.getParamsCondition();
	}
	@Override
	public void setValuesToHandler (Handler handler) {
		handler.setParams(getValues());
	}
	@Override
	protected String[] values () {
		return getStrings(condition.getExpressions());
	}
}
