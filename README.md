## 基于Netty、Nacos、ProtoStuff实现的简易RPC框架

### 功能介绍

- 实现客户端和服务端的正常调用
- 增加心跳检测、解决TCP粘包半包问题
- 基于Nacos实现了服务注册发现等功能，实现了随机、轮询、一致性哈希等负载均衡算法
- 自定义注解实现服务端接口自动注册服务功能
- 提供序列化方案选择，有Java自带IO流序列化、Json序列化、Protobuf序列化

---

### 项目层级介绍

![image](D:\Download\FromBrowser\FromChrome\image.png)
