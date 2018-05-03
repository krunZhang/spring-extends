/*
 * Copyright © 2018 krun, All Rights Reserved.
 * Project: SpringExtends
 * File:      Route.java
 * Date:    18-5-3 上午8:03
 * Author: krun
 */

package com.krun.spring.extend.route;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 路由结构，用以保存路由信息
 *
 * @author krun
 * @date 2018/05/03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Route {

	/**
	 * 路由名称
	 */
	String name;

	/**
	 * 路由路径，用于构造树，不传递到外界
	 */
	@JSONField(serialize = false)
	String path;

	/**
	 * 路由所包含的所有 beanType, 用于构造树，不传递到外界
	 */
	@JSONField(serialize = false)
	List<Class<?>> beanTypes;

	/**
	 * 路由所包含的方法，如果当前路由不是叶结点，则此属性为空
	 */
	List<Handler> handlers;

	/**
	 * 子路由
	 */
	List<Route> routes;

	/**
	 * 获取筛选后的路由树
	 * @param includeClasses 路由树内只允许含有此参数所配置的 beanTypes
	 * @return
	 */
	public List<Route> getRoutes(Class[] includeClasses) {

		List<Route> list = new LinkedList<>();

		if (routes == null) {
			return list;
		}

		for (Route r : routes) {
			if (!isIncludeClass(r, includeClasses)) {
				continue;
			}

			r.setRoutes(r.getRoutes(includeClasses));
			list.add(r);
		}

		return list;

	}

	/**
	 * 构造完毕后对 name 属性进行初始化并置空无效的子路由
	 */
	public void clean() {
		if (this.routes != null && this.routes.size() == 0) {
			this.routes = null;
		}

		if (this.name == null) {
			this.name = this.path.substring(1);
		}

		if (this.routes != null) {
			for (Route r : this.routes) {
				r.clean();
			}
		}
	}

	@Override
	public String toString () {
		return JSONObject.toJSONString(this);
	}

	/**
	 * 构造路由树
	 * @param route
	 */
	public void pushAsChild(Route route) {

		/* 检查子元素中是否有能匹配的节点 */

		if (routes != null) {
			for (Route child : routes) {
				if (!isMatch(child, route)) {
					continue;
				}

				/* 等价路由节点，即由同一个类所生成的路由 */
				if (isPathEquals(child, route)) {
					List<Handler> handlers = child.getHandlers();
					if (handlers == null) {
						handlers = new LinkedList<>();
						child.setHandlers(handlers);
					}
					/* 复制等价节点的路由方法信息 */
					handlers.addAll(route.getHandlers());

				} else {
					/*非等价路由，裁剪后加入子路由*/
					child.pushAsChild(splitPath(child, route));
				}

				return;
			}
		}

		if (routes == null) {
			routes = new LinkedList<>();
		}

		/* 添加为子元素 */
		Route child;
		if (isCurrentLevel(route)) {
			child = route;
		} else {
			child = split(route);
		}

		routes.add(child);

		/* 合并 beanType */

		if (beanTypes == null) {
			beanTypes = new LinkedList<>();
		}

		List<Class<?>> types = child.getBeanTypes();

		Iterator<Class<?>> iterator = types.iterator();
		Class<?> clazz;
		while (iterator.hasNext()) {
			clazz = iterator.next();
			if (beanTypes.contains(clazz)) {
				iterator.remove();
			}
		}

		beanTypes.addAll(types);

	}

	private static boolean isIncludeClass(Route route, Class[] classes) {
		if (classes.length == 0) {
			return true;
		}
		List<Class<?>> types = route.getBeanTypes();
		for (Class<?> clazz : classes) {
			for (Class<?> type : types) {
				if (type.equals(clazz)) {
					return true;
				}
			}
		}
		return false;
	}

	private static Route split(Route route) {
		String path = route.getPath();
		String p = path.substring(0, path.indexOf('/', 1));
		Route r = Route.builder().path(path.substring(p.length(), path.length())).beanTypes(route.getBeanTypes()).handlers(route.getHandlers()).build();
		route.setPath(p);
		route.setHandlers(null);
		route.pushAsChild(r);
		return route;
	}

	private static boolean isCurrentLevel(Route route) {
		return route.getPath().split("\\/").length == 2;
	}

	private static boolean isMatch(Route route, Route other) {
		return other.getPath().startsWith(route.getPath());
	}

	private static boolean isPathEquals(Route route, Route other) {
		return other.getPath().equals(route.getPath());
	}

	private static Route splitPath(Route route, Route other) {
		String path = other.getPath();
		other.setPath(path.substring(route.getPath().length(), path.length()));
		return other;
	}

}
