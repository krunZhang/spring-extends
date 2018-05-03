/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      ProducesConditionConverter.java
 * Date:    18-5-3 下午2:42
 * Author: krun
 */

package com.krun.spring.extend.route.convert;

import com.krun.spring.extend.route.Handler;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

/**
 * @author krun
 * @date 2018/05/03
 */
public class ProducesConditionConverter extends AbstractRequestConditionConverter<ProducesRequestCondition> {

	@Override
	public void setCondition (RequestMappingInfo info) {
		condition = info.getProducesCondition();
	}
	@Override
	public void setValuesToHandler (Handler handler) {
		handler.setProduces(getValues());
	}
	@Override
	protected String[] values () {
		Set<MediaType> producibleMediaTypes = condition.getProducibleMediaTypes();
		String[] values = new String[producibleMediaTypes.size()];
		MediaType[] types = new MediaType[producibleMediaTypes.size()];
		producibleMediaTypes.toArray(types);
		for (int i = 0; i < types.length; i++) {
			values[i] =types[i].toString();
		}
		return values;
	}
}
