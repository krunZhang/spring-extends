# Domain Mapping

此扩展项目用于简化路径到 *handler method* 的绑定。

## 文档

```java
@DomainMapping
public DemoController {}

@DomainMapping
public DemoFrontController extends DemoController {}

@DomainMapping
public DemoUserServiceController extends DemoFrontController {
    
    public String demo() {
        return "demo";
    }
    
    @ResponseBody
    public String demoTest() {
        return "demo test";
    }
    
}
```

这个例子将会生成如下路径:

```text
/demo/front/user-service/demo -> demo.html
/demo/front/user-service/demo-test -> "demo test"
```

其中 `DemoController` 将被称为根元素，其余 *Controller* 则是非根元素。

### 配置路径名称解析

首先需要配置路径名称解析，实现 *MappingNameResolver* 接口中的 *resolve* 方法即可:

```java
@Component
public MyNameResolver implements MappingNameResolver {
    
    @Override
     public String[] resolve(Class<?> clazz, Method method, String[] path) {
        // 此处根据传入参数返回一组路径。
    }
    
}
```

扫描类时，如果类带有 `@DomainMapping` 注解，则会调用此方法（*method* 参数为空）获取合适的路径值。需要注意的是，如果类上**同时**有 `@RequestMapping` 注解，则不会解析 `@DomainMapping` 注解。

扫描方法时，如果方法上没有 `@RequestMapping` 注解，则会调用此方法获取合适的路径值。如果方法上有 `@RequestMapping` 注解，则使用该注解的属性值集，但如果该注解的 *value* 或 *path* 属性值为空，则会调用此方法以获取合适的路径值。

如果一个类带有 `@DomainMapping` 注解，但是其中部分方法带有 `@RequsetMapping` 注解，也是可以正常工作的，它们的注解属性值集将会被合并，这在后面的例子中会详细说明。

### 使用默认的路径名称解析

包内带有一个简单的路径名称解析实现，仅需继承 `AbstractMappingNameResolver` 并实现两个方法即可:

```java
@Component
public class MyNameResolver extends AbstractMappingNameResolver {

	@Override // 前缀
	protected String getPrefix () {
		return "Demo";
	}
	@Override // 后缀
	protected String getSuffix () {
		return "Controller";
	}
}
```

这个默认实现遵循以下解析规则:

- 对于类名，如果后缀为 *Controller* 则会被去除。
  - 如果是根元素，则不会去除前缀，即 `DemoController` 将会产生 `/demo`, 如果直接命名为 `Controller`，则会产生 `/`；
  - 如果不是根元素，则会被去除前缀，即 `DemoFrontController extends DemoController` 将会产生 `/demo/front`。
  - 去除前后缀后剩下的内容将会被转换：`UserService -> user-service`
- 对于方法名，只会进行驼峰命名到连字符命名的转换。

> **注意!**
>
> 如果 `@DomainMapping` 的 *value* 或 *path* 属性具有一个以上的值，那么只会对其中的第一个值进行组装。
>
> 例如: 
>
> ```java
> @DomainMapping({"a", "b"})
> public DemoController {}
>
> @DomainMapping
> public DemoFrontController extends DemoController {
> 	
>     public String demo() {
>         return "demo";
>     }
>     
> }
> ```
>
> 那么只能得到 "/a/front/demo"。



根元素的判定规则如下:

- 不再继承任何带有 `@DomainMapping` 注解的类;
- 不再继承除 `Object` 以外的任何类 ;
- `@DomainMapping` 注解的 `root` 属性值不为空。

关于 `root` ，考虑以下例子:

```java
@DomainMapping
public DemoController {}

@DomainMapping(root = "/")
public DemoFrontController extends DemoController {}

@DomainMapping
public DemoUserServiceController extends DemoFrontController {}
```

对于 `DemoUserServiceController`，只会得到 `/user-service`。

如果是以下例子:

```java
@DomainMapping
public DemoController {}

@DomainMapping(root = "/a")
public DemoFrontController extends DemoController {}

@DomainMapping
public DemoUserServiceController extends DemoFrontController {}
```

那么 `DemoUserServiceController` 将会得到 `/a/user-service`。

> **注意！**
>
> `@DomainMapping` 只有 `root` 属性具有传递性，其他所有属性都只能用于当前类的路径生成。

### 注册`DomainMappingHandler`

在 *Spring-Boot* 中需要实现 *WebMvcRegistrations* 接口并实现 `getRequestMappingHandlerMapping` 方法 :

```java
@Configuration
public class WebMvcConfig implements WebMvcRegistrations {
	
    /* 确保当前环境下只有一个 RouteNameProvider 实现带有 @Component 注解。 */
	private MappingNameResolver nameResolver;

	public WebMvcConfig (MappingNameResolver nameResolver) {
		this.nameResolver = nameResolver;
	}

	@Override
	public RequestMappingHandlerMapping getRequestMappingHandlerMapping () {
		return new DomainMappingHandler(nameResolver);
	}
}
```

### 效果

`demo()` 方法因为没有带 `@RequestBody` 注解，那么其返回值将被视图解析器所使用，即该方法对应的路径的请求将得到（经过模板引擎处理后的） `demo.html` 的内容。

`demoTest()` 方法则会直接返回 `"demo test"` 字符串内容。

如果想让类中的合法方法可以直接返回内容则可以使用 `@RestDomainMapping` 注解。

### 与 `@RequestMapping` 注解混合使用

```java
@DomainMapping(
	method = RequestMethod.GET
)
public DemoUserServiceController {
    
    @RequestMapping(method = RequestMethod.POST)
    public String demo() {
        return "demo";
    }
    
    @ResponseBody
    public String demoTest() {
        return "demo test";
    }
    
    @RequestMapping({"/demo2", "demo/2"})
    @ResponseBody
    public String demo2() {
    	return "demo2";
    }
    
}
```

对于此例子，将会产生以下绑定:

```text
/demo-user-service/demo -> demo.html
/demo-user-service/demo-test -> "demo test"
/demo-user-service/demo2 -> "demo2"
/demo-user-service/demo/2 -> "demo2"
```

其中除了 `/demo-user-service/demo` 可以接受 `GET` 或 `POST` 外，其他路径都只能使用 `GET` 访问。

`@DomainMapping` 支持 `@RequestMapping` 所有的属性，因此可以灵活搭配。

## TODO

### v 1.1

* [x] 提供 `MappingNameResolver` 接口的抽象基类实现

### v 1.0

* [x] 提供 `@DomainMapping` 注解
* [x] 使被 `@DomainMapping` 修饰的类的公开非静态方法可以由方法名来绑定路径

## 更新