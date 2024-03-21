### 如何设计一个 RPC？
>  详细计划请移步： [https://www.yuque.com/yuanstack/pi0f3o](https://www.yuque.com/sylvanstack/pi0f3o)

### V1.0 - 框架基础与核心功能
- 目标：建立RPC框架的基础结构，实现基本的RPC调用。 
- 内容： 
  - RPC的基本概念介绍和原理图解析。 
  - 支持TCP/HTTP网络协议的基础RPC调用。 
  - 基本的JSON序列化协议实现。 
  - 简单的服务接口设计与元数据描述。 
- 成果：能进行基本RPC调用的框架原型。

#### 迭代一
- [X] xrpc-core
- [X] xrpc-demo
  - [X] xrpc-demo-api
  - [X] xrpc-demo-provider
  - [X] xrpc-demo-consumer
#### 迭代二
- [X] 异常场景处理
- [ ] 注解合并：@Service、@XProvider
- [X] 屏蔽 Objec 中 toString（）等方法
- [X] 返回值基本类型兼容处理
#### 迭代三
- [X] 支持方法重载（方法签名）
- [X] 支持接口多实现类调用
- [X] 支持类型转换（int -> long）
  - [X] 客户端 新 new 对象 -> LinkedHashMap
  - [X] 数组类型转化支持
  - [ ] List、Map 类型支持 
### V2.0 - 高级功能与性能优化
- 目标：引入高级功能（服务注册与发现、路由、负载均衡）和性能优化。 
- 内容： 
  - 基于Zookeeper实现服务注册与发现。 
  - 设计和实现服务路由与负载均衡机制。 
  - 实现服务的基础过滤器机制。 
  - 服务提供者与消费者的性能调优。 
- 成果：具备服务注册、发现、路由、负载均衡的高性能RPC框架。
#### 迭代 1
- [x] 负载均衡
  - [x] LoadBalancer
  - [x] Router
  - [x] Filter
- [ ] 静态注册中心
#### 迭代 2
- [ ] 注册中心
  - [ ] ZK 实现
#### 迭代 3
- [ ] 重构
