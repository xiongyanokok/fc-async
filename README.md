### 数据库脚本 ###
CREATE TABLE `async_req` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `application_name` varchar(100) NOT NULL DEFAULT '' COMMENT '应用名称',
  `sign` varchar(50) NOT NULL DEFAULT '' COMMENT '方法签名',
  `class_name` varchar(200) NOT NULL DEFAULT '' COMMENT '全路径类名称',
  `method_name` varchar(100) NOT NULL DEFAULT '' COMMENT '方法名称',
  `async_type` varchar(50) NOT NULL DEFAULT '' COMMENT '异步策略类型',
  `exec_status` tinyint NOT NULL DEFAULT '0' COMMENT '执行状态 0：初始化 1：执行失败 2：执行成功',
  `exec_count` int NOT NULL DEFAULT '0' COMMENT '执行次数',
  `param_json` longtext COMMENT '请求参数',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '业务描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_applocation_name` (`application_name`) USING BTREE,
  KEY `idx_exec_status` (`exec_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异步处理请求';

CREATE TABLE `async_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `async_id` bigint NOT NULL DEFAULT '0' COMMENT '异步请求ID',
  `error_data` longtext COMMENT '执行错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_async_id` (`async_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异步处理日志';



### 配置 ###

# 开关：默认关闭
async.enabled=true

# 数据源 druid 
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/fc_async?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true
spring.datasource.username=user
spring.datasource.password=xxxx
spring.datasource.filters=config
spring.datasource.connectionProperties=config.decrypt=true;config.decrypt.key=yyy
#静态地址
spring.resources.add-mappings=true
spring.resources.static-locations=classpath:/static/


# 以下配置都有默认值
# 核心线程数
async.executor.thread.corePoolSize=10
# 最大线程数
async.executor.thread.maxPoolSize=50
# 队列容量
async.executor.thread.queueCapacity=10000
# 活跃时间
async.executor.thread.keepAliveSeconds=600

# 执行成功是否删除记录：默认删除
async.exec.deleted=true
 
# 自定义队列名称前缀：默认应用名称
async.topic=应用名称
 
# 重试执行次数：默认5次
async.exec.count=5
 
# 重试最大查询数量
async.retry.limit=100

# 补偿最大查询数量
async.comp.limit=100



### 用法 ###
1，异步开关
async.enabled=true

2，在需要异步执行的方法加注解 （必须是spring代理方法）
@AsyncExec(type = AsyncExecEnum.SAVE_ASYNC, remark = "数据字典")

3，人工处理地址
http://localhost:8004/async/index.html



### 注意 ### 
kafka 和 job  需要自行配置实现

当然也可以替换掉这两个组件



