## 基于Netty、Nacos、ProtoStuff实现的简易RPC框架

### 功能介绍

- 实现客户端和服务端的正常调用
- 增加心跳检测、解决TCP粘包半包问题
- 基于Nacos实现了服务注册发现等功能，实现了随机、轮询、一致性哈希等负载均衡算法
- 自定义注解实现服务端接口自动注册服务功能
- 提供序列化方案选择，有Java自带IO流序列化、Json序列化、Protobuf序列化

---

### 项目包层级介绍

***client包:***

- handler包：处理器
- loadBalancer包：负载均衡方案
- registery包：服务发现
- service包：调用的接口

***common包:***

- config包：扫描properties配置
- constant包：常量包
- message包：自定义消息协议包
- protocol包：序列化方案以及定长编码

***server包:***

- annotation包：自定义注解包
- service包：接口实现
