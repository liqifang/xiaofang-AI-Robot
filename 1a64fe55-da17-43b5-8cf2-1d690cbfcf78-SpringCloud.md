# ***SpringCloud***

> ps：本文档以黑马商城为例



## 1. 什么是微服务

微服务架构，首先是服务化，就是将单体架构中的功能模块从单体应用中拆分出来，独立部署为多个服务。同时要满足下面的一些特点：

- **单一职责**：一个微服务负责一部分业务功能，并且其核心数据不依赖于其它模块。
- **团队自治**：每个微服务都有自己独立的开发、测试、发布、运维人员，团队人员规模不超过10人（2张披萨能喂饱）
- **服务自治**：每个微服务都独立打包部署，访问自己独立的数据库。并且要做好服务隔离，避免对其它服务产生影响





## 2. 微服务拆分

服务拆分一定要考虑几个问题：

- 什么时候拆？

- 如何拆？

  

### 2.1 什么时候拆？

- 对于**大多数小型项目来说，一般是先采用单体架构**，随着用户规模扩大、业务复杂后**再逐渐拆分为微服务架构**。

- 而对于一些**大型项目**，在立项之初目的就很明确，为了长远考虑，在架构设计时就**直接选择微服务架构**。虽然前期投入较多，但后期就少了拆分服务的烦恼



### 2.2 怎么拆？

- **高内聚**：每个微服务的职责要尽量单一，包含的业务相互关联度高、完整度高。

- **低耦合**：每个微服务的功能要相对独立，尽量减少对其它微服务的依赖，或者依赖接口的稳定性要强。

  

**高内聚**首先是**单一职责，**但不能说一个微服务就一个接口，而是要保证微服务内部业务的完整性为前提。目标是当我们要修改某个业务时，最好就只修改当前微服务，这样变更的成本更低。

一旦微服务做到了高内聚，那么服务之间的**耦合度**自然就降低了。



### 2.3 拆分方式

- **横向**拆分

- **纵向**拆分

  所谓**纵向拆分**，就是按照项目的功能模块来拆分。

  而**横向拆分**，是看各个功能模块之间有没有公共的业务部分，如果有将其抽取出来作为通用服务。







## 3.服务的远程调用

在拆分过程中，我们发现购物车业务中需要查询商品信息，但商品信息查询的逻辑全部迁移到了`item-service`服务中，从而导致我们无法查询。

因此我们必须把原本的本地方法调用改造成**跨微服务的远程调用**（RPC，Remote Produce C all）。

![image-20250302115713577](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250302115713577.png)



### 3.1RestTemplate

Spring给我们提供了一个RestTemplate的API，可以方便的实现Http请求的发送。



要使用RestTemplate首先要将其注册为一个Bean

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RemoteCallConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

接着便可以修改业务代码，发送请求到`ietm-service`：

![image-20250302115645610](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250302115645610-1740887817593-1.png)







### 3.2注册中心

当我们在`cart-service`中使用`RestTemplate`手动发送Http请求时还存在很多问题：

- item-service这么多实例，cart-service如何知道每一个实例的地址？
- http请求要写url地址，`cart-service`服务到底该调用哪个实例呢？
- 如果在运行过程中，某一个`item-service`实例宕机，`cart-service`依然在调用该怎么办？
- 如果并发太高，`item-service`临时多部署了N台实例，`cart-service`如何知道新实例的地址？

为了解决以上问题，就可以使用**注册中心**





### 3.2.1注册中心原理

在微服务远程调用的过程中，包括两个角色：

- 服务提供者：提供接口供其它微服务访问，比如`item-service`
- 服务消费者：调用其它微服务提供的接口，比如`cart-service`

在大型微服务项目中，服务提供者的数量会非常多，为了管理这些服务就引入了**注册中心**的概念。注册中心、服务提供者、服务消费者三者间关系如下：

![image-20250302120636977](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250302120636977.png)







### 3.3 Nacos注册中心

目前开源的注册中心框架有很多，国内比较常见的有：

- Eureka：Netflix公司出品，目前被集成在SpringCloud当中，一般用于Java应用
- Nacos：Alibaba公司出品，目前被集成在SpringCloudAlibaba中，一般用于Java应用
- Consul：HashiCorp公司出品，目前集成在SpringCloud中，不限制微服务语言



本项目中使用的为**Nacos注册中心**

- 官方网站如下(点击图片可跳转)：

[![image-20250302121707091](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250302121707091.png)](https://nacos.io/)



使用`Docker`将注册中心部署完成后可以访问http://192.168.150.101:8848/nacos/，注意将`192.168.150.101`替换为你自己的虚拟机IP地址。

首次访问会跳转到登录页，**账号密码都是nacos**

![image-20250302122146819](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250302122146819.png)







#### 3.3.1 服务注册

把`item-service`注册到Nacos，步骤如下：

- 引入依赖
- 配置Nacos地址
- 重启



##### 引入依赖

```XML
<!--nacos 服务注册发现-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```



##### 配置Nacos

在`item-service`的`application.yml`中添加nacos地址配置：

```YAML
spring:
  application:
    name: item-service # 服务名称
  cloud:
    nacos:
      server-addr: 192.168.150.101:8848 # nacos地址
```





##### 启动多个实例进行测试

![image-20250302123010856](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250302123010856.png)

访问nacos控制台，可以发现服务注册成功：

![img](https://b11et3un53m.feishu.cn/space/api/box/stream/download/asynccode/?code=NDY1MDY2NTZkMmFkZGY0NWRlMzY5YTRmNTgxNjU2ZWNfSXVhTE9seklvWnZrclIxazlBdWZUeWh4QUJOdWlNM0RfVG9rZW46WGNnaGJsQ09Kb09UYXl4U0FBNGNrQUN5bkplXzE3NDA4ODk4Mjg6MTc0MDg5MzQyOF9WNA)







#### 3.3.2 服务发现

服务的消费者要去nacos订阅服务，这个过程就是服务发现，步骤如下：

- 引入依赖
- 配置Nacos地址
- 发现并调用服务



##### 引入依赖

服务发现除了要引入nacos依赖以外，由于还需要负载均衡，因此要引入SpringCloud提供的LoadBalancer依赖。

我们在`cart-service`中的`pom.xml`中添加下面的依赖：

```XML
<!--nacos 服务注册发现-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

可以发现，这里Nacos的依赖于服务注册时一致，这个依赖中同时包含了服务注册和发现的功能。因为任何一个微服务都可以调用别人，也可以被别人调用，即可以是调用者，也可以是提供者。

因此，等一会儿`cart-service`启动，同样会注册到Nacos



##### 配置Nacos地址

在`cart-service`的`application.yml`中添加nacos地址配置：

```YAML
spring:
  cloud:
    nacos:
      server-addr: 192.168.150.101:8848
```



##### 发现服务并调用

接下来，服务调用者`cart-service`就可以去订阅`item-service`服务了。不过item-service有多个实例，而真正发起调用时只需要知道一个实例的地址。

因此，服务调用者必须利用负载均衡的算法，从多个实例中挑选一个去访问。常见的负载均衡算法有：

- 随机
- 轮询
- IP的hash
- 最近最少访问
- ...



另外，服务发现需要用到一个工具，DiscoveryClient，SpringCloud已经帮我们自动装配，我们可以直接注入使用：

![image-20250303092149256](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250303092149256.png)

接着就可以修改之前远程调用的方法了

![image-20250303092304730](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250303092304730.png)







### 3.4 OpenFeign

之前我们编写的远程调用代码太复杂了，为了再次简化调用方式我们就需要引入**OpenFeign**



#### 3.4.1 引入依赖

```XML
  <!--openFeign-->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
  </dependency>
  <!--负载均衡器-->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-loadbalancer</artifactId>
  </dependency>
```



#### 3.4.2 启用OpenFeign

![image-20250303092800958](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250303092800958.png)



#### 3.4.3 编写OpenFeign客户端

在`cart-service`中，定义一个新的接口，编写Feign客户端：

其中代码如下：

```Java
import com.hmall.cart.domain.dto.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface ItemClient {

    @GetMapping("/items")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);
}
```

这里只需要声明接口，无需实现方法。接口中的几个关键信息：

- `@FeignClient("item-service")` ：声明服务名称
- `@GetMapping` ：声明请求方式
- `@GetMapping("/items")` ：声明请求路径
- `@RequestParam("ids") Collection<Long> ids` ：声明请求参数
- `List<ItemDTO>` ：返回值类型



> [!TIP]
>
> OpenFeign接口的编写和Mybatis的接口编写方式类似



#### 3.4.4 使用

![image-20250303093035005](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250303093035005.png)







## 4.网关

### 4.1什么是网关？

顾明思议，网关就是**网络的关口**。数据在网络间传输，从一个网络传输到另一网络时就需要经过网关来做数据的**路由和转发以及数据安全的校验**。

当使用网关后，前端的请求不能直接访问微服务，而是要请求网关。

- 网关可以做安全控制，也就是登录身份校验，校验通过才放行
- 通过认证后，网关再根据请求判断应该访问哪个微服务，将请求转发过去

![image-20250309105621735](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250309105621735.png)



### 4.2网关的使用

#### 4.2.1引入依赖

```xml
<!--网关-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```



#### 4.2.2启动类

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

```



#### 4.2.3配置路由

在`hm-gateway`模块的`resources`目录新建一个`application.yaml`文件

```YAML
server:
  port: 8080
spring:
  application:
    name: gateway
  cloud:
    nacos:
      server-addr: 192.168.150.101:8848
    gateway:
      default-filters: # default-filters下的过滤器可以作用于所有路由
        - AddRequestHeader=key, value
      routes:
        - id: item # 路由规则id，自定义，唯一
          uri: lb://item-service # 路由的目标服务，lb代表负载均衡，会从注册中心拉取服务列表
          predicates: # 路由断言，判断当前请求是否符合当前规则，符合则路由到目标服务
            - Path=/items/**,/search/** # 这里是以请求路径作为判断规则
          filters:
          - AddRequestHeader=key, value # 逗号之前是请求头的key，逗号之后是value
        - id: cart
          uri: lb://cart-service
          predicates:
            - Path=/carts/**
        - id: user
          uri: lb://user-service
          predicates:
            - Path=/users/**,/addresses/**
        - id: trade
          uri: lb://trade-service
          predicates:
            - Path=/orders/**
        - id: pay
          uri: lb://pay-service
          predicates:
            - Path=/pay-orders/**
          
```



常见的路由属性有四个，其属性含义如下：

- `id`：路由的唯一标示
- `predicates`：路由断言，其实就是匹配条件
- `filters`：路由过滤条件，后面讲
- `uri`：路由目标地址，`lb://`代表负载均衡，从注册中心获取目标微服务的实例列表，并且负载均衡选择一个访问。

这里我们重点关注`predicates`，也就是路由断言。SpringCloudGateway中支持的断言类型有很多：

| **名称**   | **说明**                       | **示例**                                                     |
| :--------- | :----------------------------- | :----------------------------------------------------------- |
| After      | 是某个时间点后的请求           | - After=2037-01-20T17:42:47.789-07:00[America/Denver]        |
| Before     | 是某个时间点之前的请求         | - Before=2031-04-13T15:14:47.433+08:00[Asia/Shanghai]        |
| Between    | 是某两个时间点之前的请求       | - Between=2037-01-20T17:42:47.789-07:00[America/Denver], 2037-01-21T17:42:47.789-07:00[America/Denver] |
| Cookie     | 请求必须包含某些cookie         | - Cookie=chocolate, ch.p                                     |
| Header     | 请求必须包含某些header         | - Header=X-Request-Id, \d+                                   |
| Host       | 请求必须是访问某个host（域名） | - Host=**.somehost.org,**.anotherhost.org                    |
| Method     | 请求方式必须是指定方式         | - Method=GET,POST                                            |
| Path       | 请求路径必须符合指定规则       | - Path=/red/{segment},/blue/**                               |
| Query      | 请求参数必须包含指定参数       | - Query=name, Jack或者- Query=name                           |
| RemoteAddr | 请求者的ip必须是指定范围       | - RemoteAddr=192.168.1.1/24                                  |
| weight     | 权重处理                       |                                                              |



#### 4.2.4网关登录校验

通过路由中的过滤器，可以实现前端请求在经过controller前、后被过滤器相对应的方法处理加工以及拦截，通过编写恰当的业务逻辑来实现业务需求。

> [!TIP]
>
> 相关代码已在idea中实现







## 5.配置管理

到目前为止我们已经解决了微服务相关的几个问题：

- 微服务远程调用
- 微服务注册、发现
- 微服务请求路由、负载均衡
- 微服务登录用户信息传递

不过，现在依然还有几个问题需要解决：

- 网关路由在配置文件中写死了，如果变更必须重启微服务
- 某些业务配置在配置文件中写死了，每次修改都要重启服务
- 每个微服务都有很多重复的配置，维护成本高

这些问题都可以通过统一的**配置管理器服务**解决。而Nacos不仅仅具备注册中心功能，也具备配置管理的功能：

![image-20250309112652905](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250309112652905.png)

微服务共享的配置可以统一交给Nacos保存和管理，在Nacos控制台修改配置后，Nacos会将配置变更推送给相关的微服务，并且无需重启即可生效，实现配置热更新。

网关的路由同样是配置，因此同样可以基于这个功能实现动态路由功能，无需重启网关即可修改路由配置。



### 5.1添加共享配置

抽取可共享的配置文件后在nacos控制台中添加配置文件![image-20250309112958060](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250309112958060.png)



### 5.2拉取配置

接下来，我们要在微服务拉取共享配置。将拉取到的共享配置与本地的`application.yaml`配置合并，完成项目上下文的初始化。

不过，需要注意的是，读取Nacos配置是SpringCloud上下文（`ApplicationContext`）初始化时处理的，发生在项目的引导阶段。然后才会初始化SpringBoot上下文，去读取`application.yaml`。

也就是说引导阶段，`application.yaml`文件尚未读取，根本不知道nacos 地址，该如何去加载nacos中的配置文件呢？

SpringCloud在初始化上下文的时候会先读取一个名为`bootstrap.yaml`(或者`bootstrap.properties`)的文件，如果我们将nacos地址配置到`bootstrap.yaml`中，那么在项目引导阶段就可以读取nacos中的配置了。

![image-20250309113046516](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250309113046516.png)

因此，微服务整合Nacos配置管理的步骤如下：

1）引入依赖：

在cart-service模块引入依赖：

```XML
  <!--nacos配置管理-->
  <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
  </dependency>
  <!--读取bootstrap文件-->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-bootstrap</artifactId>
  </dependency>
```

2）新建bootstrap.yaml

在cart-service中的resources目录新建一个bootstrap.yaml文件

内容如下：

```YAML
spring:
  application:
    name: cart-service # 服务名称
  profiles:
    active: dev
  cloud:
    nacos:
      server-addr: 192.168.150.101 # nacos地址
      config:
        file-extension: yaml # 文件后缀名
        shared-configs: # 共享配置
          - dataId: shared-jdbc.yaml # 共享mybatis配置
          - dataId: shared-log.yaml # 共享日志配置
          - dataId: shared-swagger.yaml # 共享日志配置
```

3）修改application.yaml

由于一些配置挪到了bootstrap.yaml，因此application.yaml需要修改为：

```YAML
server:
  port: 8082
feign:
  okhttp:
    enabled: true # 开启OKHttp连接池支持
hm:
  swagger:
    title: 购物车服务接口文档
    package: com.hmall.cart.controller
  db:
    database: hm-cart
```

重启服务，发现所有配置都生效了。



### 5.3配置热更新

有很多的业务相关参数，将来可能会根据实际情况临时调整。例如购物车业务，购物车数量有一个上限，默认是10，现在这里购物车是写死的固定值，我们应该将其配置在配置文件中，方便后期修改。

但现在的问题是，即便写在配置文件中，修改了配置还是需要重新打包、重启服务才能生效。能不能不用重启，直接生效呢？

这就要用到Nacos的配置热更新能力了，分为两步：

- 在Nacos中添加配置
- 在微服务读取配置



#### 5.3.1在Nacos中添加配置

首先，我们在nacos中添加一个配置文件，将购物车的上限数量添加到配置中：

![image-20250309113317216](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250309113317216.png)

注意文件的dataId格式：

```Plain
[服务名]-[spring.active.profile].[后缀名]
```

文件名称由三部分组成：

- **`服务名`**：我们是购物车服务，所以是`cart-service`
- **`spring.active.profile`**：就是spring boot中的`spring.active.profile`，可以省略，则所有profile共享该配置
- **`后缀名`**：例如yaml

这里我们直接使用`cart-service.yaml`这个名称，则不管是dev还是local环境都可以共享该配置。

配置内容如下：

```YAML
hm:
  cart:
    maxAmount: 1 # 购物车商品数量上限
```



#### 5.3.2配置热更新

接着，我们在微服务中读取配置，实现配置热更新。

在`cart-service`中新建一个属性读取类：

```Java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "hm.cart")
public class CartProperties {
    private Integer maxAmount;
}
```

接着，在业务中使用该属性加载类：

![image-20250309113510052](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250309113510052.png)

当我们在nacos中修改配置后，无需重启服务，新的配置就生效了。







## 6.服务保护

常见的服务保护方案有很多，比如：

- 请求限流（增加一个限流器，**限制或控制**接口访问的并发流量，避免服务因流量激增而出现故障）
- 线程隔离（限定每个接口可以使用的资源范围，也就是将其“隔离”起来。）
- 服务熔断（通过**异常统计和熔断**以及**编写服务降级逻辑**避免拖慢其他接口的运行速度）

基于**sentinel**可以很方便的实现上述的三种操作。







### 6.1sentinel的下载和使用

下载sentinel相对应的jar包![image-20250311101116955](C:/Users/DELL/OneDrive/%E6%A1%8C%E9%9D%A2/Study/SpringCloud.assets/image-20250311101116955.png)

使用如下命令在控制台中启动

```Shell
java -Dserver.port=8090 -Dcsp.sentinel.dashboard.server=localhost:8090 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard.jar
```

访问[http://localhost:8090](http://localhost:8080)页面，就可以看到sentinel的控制台了



