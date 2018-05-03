# Routes Generators

用于提供已绑定的 *handler method* 信息到路由树的映射。



最终目标: 提供一套方案使得能自动收集路由信息并生成 *JavaScript* 端的**请求发起客户端**。

## 文档

```java
@RestController
public class RoutesController extends AbstractRoutesGenerator {

	@RequestMapping("/routes")
	public String getRoutes() {
		return this.getRoutes();
	}

	@Override
	protected String getPrefix () {
		return "Demo";
	}

	@Override
	protected String getSuffix () {
		return "Controller";
	}

	@Override
	protected boolean isExcludeType (Class<?> beanType) {
		return BasicErrorController.class.equals(beanType);
	}
}
```

**Controller 定义**

```java
@RestController
@RequestMapping("/user")
public class DemoUserController {

    @RequestMapping("/demo")
	public String demo() {
		System.out.println("方法执行");
		return "demo";
	}
 	@RequestMapping("/demo2")
	public String demo2() {
		return "demo2";
	}

}
```

> 如果使用此项目下的 [domain-mapping](https://github.com/krunZhang/spring-extends/tree/master/domain-mapping) 扩展，则可以这样写:
>
> ```java
> @RestDomainMapping
> public class DemoUserController {
>
> 	public String demo() {
> 		System.out.println("方法执行");
> 		return "demo";
> 	}
>
> 	public String demo2() {
> 		return "demo2";
> 	}
>
> }
> ```

这个例子将会生成如下路由树:

```text
{
	"name": "@",
	"routes": [
		{
			"handlers": [
				{
					"name": "demo2",
					"url": "/user/demo2"
				}, {
					"name": "demo",
					"url": "/user/demo"
				}
			],
			"name": "user"
		}
	]
}
```

### 关于 `AbstractRoutesGenerator`

`AbstractRoutesGenerator` 是路由生成器的抽象基类。

它会对已登记的路由信息进行整合以生成路由树，路由结构为:

```typescript
Handler {
	name :string,			// 路由方法的名字，由 Controller 中对应的方法名所决定
    url :string				// 路由方法对应的地址
}

Route {
    name :string,			// 路由的名字，由类名得到
    handlers :Handler[],	// 路由下绑定的方法列表
    routes: Route[],		// 子路由
    path :string			// 路由的路径，这个属性用于构造树，不会传递到外界
}
```

对于 `DemoUserController` 这个类会生成如下路由:

```json
{
    name: '@',
    routes: [
        {
            name: 'user',
            path: '/user',
            handlers: [
                ...
            ]
        }
    ]
}
```

`Route.name` 属性是通过 `path` 属性转换而来，即去除前面的 `/` 字符。

而 `Route.path` 属性是通过类名转换而来：

在这个例子中，` getPrefix()` 和 `getSuffix()` 方法告知生成器需要对类名裁剪前缀 (`Demo`) 和后缀 (`Controller`)，然后生成器会将剩下的部分从驼峰命名法转换:

```text
DemoUserController -> User -> /user

另例子
DemoUserAuthController -> UserAuth -> /user/auth
```

## TODO

### v 1.1

* [ ] 提供 *JavaScript* 端的生成器

### v 1.0

* [x] 提供 `AbstractRoutesGenerator` 抽象基类实现。

## 更新

### v 1.0

提供 `AbstractRoutesGenerator` 抽象基类实现。

此扩展需要与 *JavaScript* 端生成器配合使用，因此此版本不提供 RELEASE 文件。