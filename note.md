## **`smp-eureka` (注册中心)**

- **角色**：通讯录 / 话务员。
- **作用**：所有微服务启动后都要来这里“报到”（注册）。
- **场景**：当 `smp-class` 想找 `smp-info` 要数据时，它不能直接连 IP，而是先问 Eureka：“`smp-info` 在哪？”，Eureka 会告诉它地址。这是微服务的核心。

## **`smp-config` (配置中心)**

- **角色**：档案管理员。
- **作用**：管理所有服务的配置文件（`application.yml` 或 `.properties`）。
- **场景**：你不需要去每个服务里改数据库密码，只需要在配置中心的文件夹（`config-dir`）里修改一次，所有服务都能读到。

这个配置文件定义了一个 **Spring Cloud Config Server（配置中心）**，它的作用是统一管理所有微服务的配置文件（比如数据库密码、Redis 地址等）。

### 1. Config 模块的工作原理

你可以把它想象成一个“档案室管理员”。

1. **启动时**：`smp-config`（管理员）启动，根据你配置的路径（`search-locations`），去硬盘里把所有配置文件（档案）都准备好。
2. **查询时**：其他服务（比如 `smp-class`）启动时，会先去问 `smp-eureka`（前台）："管理员在哪？"
3. **获取配置**：找到管理员后，`smp-class` 问："我是 class 服务，我是 dev 环境，把我的配置给我。"（http://config/class/dev）
4. **返回结果**：管理员从文件夹里找出 `class-dev.properties`，把里面的数据库 URL 返回给 `smp-class`。

## **`smp-gateway` (网关)**

- **角色**：大门保安 / 前台。
- **作用**：所有来自前端（网页/App）的请求，**必须**先经过这里。它负责路由转发（把请求转给对应的服务）、限流、跨域处理等。
- **场景**：前端请求 `http://gateway/api/class/get`，网关会把它转给 `smp-class` 服务。

使用JWT令牌登陆

核心就在于一个依赖：`spring-cloud-starter-netflix-zuul`

这个项目使用的网关技术并不是新一代的 *Spring Cloud Gateway*，而是上一代的经典霸主 **Netflix Zuul**。

这就解释了为什么没有配置文件也能转发——因为 **Zuul 默认自带“自动映射”功能**。

## **`smp-security` (认证授权服务)**

- **角色**：安检员。
- **作用**：负责登录认证、发令牌（Token）、权限校验。
- **场景**：用户登录时，请求会发到这里。如果用户名密码正确，它发一个 Token 给用户。其他服务（如 `smp-leave`）在处理请求前，会检查这个 Token 是否合法。

## **`smp-admin-server` (监控中心)**

- **角色**：监控室。
- **作用**：使用 Spring Boot Admin 技术，图形化展示所有微服务的健康状态、内存使用情况、日志级别等。

有了 **`smp-admin-server`**，打开浏览器访问它的页面，就能看到：

- **健康状态**：所有微服务是 UP（绿灯）还是 DOWN（红灯）。
- **JVM 监控**：内存占用、线程数量、垃圾回收情况（以图表形式展示）。
- **日志查看**：可以直接在网页上实时看其他服务的 Log，不用去翻服务器文件。
- **环境参数**：查看每个服务加载了哪些配置文件（Debug 神器）。

## **`smp-hystrix-dashboard` (熔断监控)**

- **角色**：电路保险丝仪表盘。
- **作用**：监控服务之间的调用成功率。如果某个服务（比如 `smp-info`）挂了或响应太慢，Hystrix 会切断连接（熔断），防止整个系统卡死，这个模块就是用来通过图表看这些数据的。

## **`smp-info` (基础信息服务)**

- **作用**：管理最基础的数据。
- **推测功能**：学生档案管理、教师信息管理、学院/专业信息管理。

## **`smp-class` (课程/班级服务)**

- **作用**：与“上课”相关。
- **推测功能**：课程表管理、班级管理、排课系统。

## **`smp-leave` (请假服务)**

- **作用**：日常事务流程。
- **推测功能**：学生发起请假申请、辅导员审批请假、销假记录。

## **`smp-room` (房产/宿舍服务)**

- **作用**：物理空间管理。
- **推测功能**：教室预约、宿舍分配、空闲教室查询。

## **`smp-statistics` (统计服务)**

- **作用**：数据分析。
- **推测功能**：统计全校请假人数、统计挂科率、生成可视化报表（给领导看的）。













## 数据库

### 1. 基本信息表

#### 1. 角色表 (role)

对应实体：`Role`

| **字段名**       | **类型** | **长度** | **约束**     | **说明**             |
| ---------------- | -------- | -------- | ------------ | -------------------- |
| **id**           | CHAR     | 36       | PK           | 主键，固定 UUID 长度 |
| **name**         | VARCHAR  | 50       | UK, Not Null | 角色名，唯一，非空   |
| **gmt_create**   | DATETIME | -        | Not Null     | 创建时间             |
| **gmt_modified** | DATETIME | -        | Not Null     | 更新时间             |

------

#### 2. 公寓信息表 (apartment)

对应实体：`Apartment`

| **字段名**       | **类型** | **长度** | **约束**     | **说明**           |
| ---------------- | -------- | -------- | ------------ | ------------------ |
| **id**           | CHAR     | 36       | PK           | 主键，UUID         |
| **name**         | VARCHAR  | 255      | UK, Not Null | 公寓名，唯一，非空 |
| **gmt_create**   | DATETIME | -        | Not Null     | 创建时间           |
| **gmt_modified** | DATETIME | -        | Not Null     | 更新时间           |

------

#### 3. 学生用户表 (student_user)

对应实体：`StudentUser`

> **注意**：`apartmentId` 是外键，关联 `apartment` 表。

| **字段名**              | **类型**      | **长度** | **约束**     | **说明**                                 |
| ----------------------- | ------------- | -------- | ------------ | ---------------------------------------- |
| **id**                  | CHAR          | 36       | PK           | 主键（通常与 User 表 ID 一致）           |
| **student_id**          | VARCHAR       | 255      | UK, Not Null | 学号，唯一，非空                         |
| **name**                | VARCHAR       | 255      | -            | (隐式)通常学生表也有姓名，但Entity未定义 |
| **sex**                 | BIT / TINYINT | 1        | -            | 性别 (1:男, 0:女)                        |
| **age**                 | INT           | -        | -            | 年龄                                     |
| **birthday**            | DATETIME      | -        | -            | 出生日期                                 |
| **id_card**             | CHAR          | 18       | UK           | 身份证号，唯一                           |
| **political_status**    | VARCHAR       | 255      | -            | 政治面貌                                 |
| **ethnic**              | VARCHAR       | 255      | -            | 民族                                     |
| **belong_counselor_id** | CHAR          | 36       | Not Null     | 所属辅导员 ID                            |
| **apartment_id**        | CHAR          | 36       | FK, Not Null | **外键** -> apartment.id                 |
| **room_num**            | VARCHAR       | 255      | Not Null     | 寝室号                                   |
| **bed_num**             | VARCHAR       | 255      | Not Null     | 床号                                     |
| **address**             | VARCHAR       | 255      | -            | 家庭地址                                 |
| **gmt_create**          | DATETIME      | -        | Not Null     | 创建时间                                 |
| **gmt_modified**        | DATETIME      | -        | Not Null     | 更新时间                                 |

------

#### 4. 用户表 (user)

对应实体：`User`

> **注意**：`roleId` 是外键。`studentUser` 使用了 `@PrimaryKeyJoinColumn`，意味着 `User` 和 `StudentUser` 共享同一个主键 ID（即是一对一关系，`StudentUser` 的 `id` 既是主键也是指向 `User` 的外键）。

| **字段名**       | **类型** | **长度** | **约束**     | **说明**                   |
| ---------------- | -------- | -------- | ------------ | -------------------------- |
| **id**           | CHAR     | 36       | PK           | 主键，UUID                 |
| **username**     | VARCHAR  | 255      | UK, Not Null | 用户名（账号），唯一，非空 |
| **password**     | VARCHAR  | 255      | Not Null     | 密码                       |
| **name**         | VARCHAR  | 255      | Not Null     | 真实姓名                   |
| **tel**          | CHAR     | 11       | -            | 电话号码                   |
| **email**        | VARCHAR  | 255      | -            | 邮箱                       |
| **role_id**      | CHAR     | 36       | FK, Not Null | **外键** -> role.id        |
| **gmt_create**   | DATETIME | -        | Not Null     | 创建时间                   |
| **gmt_modified** | DATETIME | -        | Not Null     | 更新时间                   |

------

#### SQL 建表语句 (参考 MySQL)

```sql
-- 1. 创建角色表
CREATE TABLE `role` (
  `id` char(36) NOT NULL,
  `name` varchar(50) NOT NULL,
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_role_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 创建公寓表
CREATE TABLE `apartment` (
  `id` char(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_apartment_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 创建学生用户表
CREATE TABLE `student_user` (
  `id` char(36) NOT NULL COMMENT '通常与User表ID相同',
  `student_id` varchar(255) NOT NULL COMMENT '学号',
  `sex` bit(1) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `id_card` char(18) DEFAULT NULL,
  `political_status` varchar(255) DEFAULT NULL,
  `ethnic` varchar(255) DEFAULT NULL,
  `belong_counselor_id` char(36),
  `apartment_id` char(36) COMMENT '外键',
  `room_num` varchar(255),
  `bed_num` varchar(255),
  `address` varchar(255),
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_student_id` (`student_id`),
  UNIQUE KEY `UK_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 创建用户表
CREATE TABLE `user` (
  `id` char(36) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255),
  `tel` char(11),
  `email` varchar(255) DEFAULT NULL,
  `role_id` char(36) NOT NULL COMMENT '外键',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 2. 签到信息表

#### 1. 学生班级表 (student_class)

对应实体：`StudentClass`

| **字段名**        | **类型** | **长度** | **约束**     | **说明**                   |
| ----------------- | -------- | -------- | ------------ | -------------------------- |
| **id**            | CHAR     | 36       | PK           | 主键，UUID                 |
| **name**          | VARCHAR  | 255      | Not Null     | 群组名（班级名）           |
| **class_num**     | VARCHAR  | 255      | UK, Not Null | 班号（唯一，用于搜索加入） |
| **teacherUserId** | CHAR     | 36       | Not Null     | **外键** -> user.id (教师) |
| **gmt_create**    | DATETIME | -        | Not Null     | 创建时间                   |
| **gmt_modified**  | DATETIME | -        | Not Null     | 更新时间                   |

------

#### 2. 签到元数据表 (student_class_check_meta_data)

对应实体：`StudentClassCheckMetaData`

> **说明**：这张表存储某次签到的“规则”，比如老师发起了签到，规定了时间范围和位置。

| **字段名**         | **类型** | **长度** | **约束**     | **说明**                     |
| ------------------ | -------- | -------- | ------------ | ---------------------------- |
| **id**             | CHAR     | 36       | PK           | 主键，UUID                   |
| **start_time**     | DATETIME | -        | Not Null     | 签到开始时间                 |
| **end_time**       | DATETIME | -        | Not Null     | 签到结束时间                 |
| **longitude**      | DOUBLE   | -        | Not Null     | 老师发起时的经度             |
| **latitude**       | DOUBLE   | -        | Not Null     | 老师发起时的纬度             |
| **m**              | FLOAT    | -        | Not Null     | 允许的最大距离（米）         |
| **studentClassId** | CHAR     | 36       | FK, Not Null | **外键** -> student_class.id |
| **gmt_create**     | DATETIME | -        | Not Null     | 创建时间                     |
| **gmt_modified**   | DATETIME | -        | Not Null     | 更新时间                     |

------

#### 3. 学生签到记录表 (student_class_check)

对应实体：`StudentClassCheck`

> **说明**：这张表存储学生具体的签到行为。

| **字段名**                      | **类型** | **长度** | **约束**     | **说明**                     |
| ------------------------------- | -------- | -------- | ------------ | ---------------------------- |
| **id**                          | CHAR     | 36       | PK           | 主键，UUID                   |
| **userId**                      | CHAR     | 36       | Not Null     | **外键** -> user.id (学生)   |
| **studentClassId**              | CHAR     | 36       | FK, Not Null | **外键** -> student_class.id |
| **studentClassCheckMetaDataId** | CHAR     | 36       | FK, Not Null | **外键** -> 元数据表.id      |
| **check_time**                  | DATETIME | -        | Not Null     | 实际签到时间                 |
| **longitude**                   | DOUBLE   | -        | Not Null     | 学生签到时的经度             |
| **latitude**                    | DOUBLE   | -        | Not Null     | 学生签到时的纬度             |
| **gmt_create**                  | DATETIME | -        | Not Null     | 创建时间                     |
| **gmt_modified**                | DATETIME | -        | Not Null     | 更新时间                     |

------

#### 4. 班级-学生关联表 (student_class_user)

对应实体：`StudentClassUser`

> **说明**：这是一个多对多关系的中间表，使用了复合主键。

| **字段名**         | **类型** | **长度** | **约束** | **说明**                     |
| ------------------ | -------- | -------- | -------- | ---------------------------- |
| **studentId**      | CHAR     | 36       | PK, FK   | **外键** -> user.id (学生)   |
| **studentClassId** | CHAR     | 36       | PK, FK   | **外键** -> student_class.id |
| **gmt_create**     | DATETIME | -        | Not Null | 创建时间                     |
| **gmt_modified**   | DATETIME | -        | Not Null | 更新时间                     |

------

#### SQL 建表语句 (MySQL)

请在 `smp_class` 数据库中执行（如果你为 class 服务单独创建了数据库）。

```sql
-- 1. 创建学生班级表
CREATE TABLE `student_class` (
  `id` char(36) NOT NULL COMMENT '主键ID',
  `name` varchar(255) NOT NULL COMMENT '群组名/班级名',
  `class_num` varchar(255) NOT NULL COMMENT '班号(唯一)',
  `teacher_user_id` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(教师)',
  `gmt_create` datetime COMMENT '创建时间',
  `gmt_modified` datetime COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_class_num` (`class_num`)
  -- 已移除物理外键约束
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 创建签到元数据表
CREATE TABLE `student_class_check_meta_data` (
  `id` char(36) NOT NULL COMMENT '主键ID',
  `start_time` datetime NOT NULL COMMENT '签到开始时间',
  `end_time` datetime NOT NULL COMMENT '签到结束时间',
  `longitude` double COMMENT '经度',
  `latitude` double COMMENT '纬度',
  `m` float COMMENT '最大允许距离',
  `student_class_id` char(36) NOT NULL COMMENT '逻辑外键：关联student_class表的ID',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`)
  -- 已移除物理外键约束
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 创建学生签到记录表
CREATE TABLE `student_class_check` (
  `id` char(36) NOT NULL COMMENT '主键ID',
  `user_id` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(学生)',
  `student_class_id` char(36) NOT NULL COMMENT '逻辑外键：关联student_class表的ID',
  `student_classCheck_meta_data_id` char(36) NOT NULL COMMENT '逻辑外键：关联元数据表的ID',
  `check_time` datetime NOT NULL COMMENT '实际签到时间',
  `longitude` double ,
  `latitude` double,
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`)
  -- 已移除物理外键约束
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 创建班级-学生关联表 (多对多关系表)
CREATE TABLE `student_class_user` (
  `student_id` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(学生)',
  `student_class_id` char(36) NOT NULL COMMENT '逻辑外键：关联student_class表的ID',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  -- 使用联合主键，确保一个学生在同一个班级里只能有一条记录
  PRIMARY KEY (`student_id`, `student_class_id`)
  -- 已移除物理外键约束
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

```

### 3. 模块：smp-leave (请假服务)

#### 表名：`student_leave` (请假申请表)

> **对应实体**：`Leave`

| **字段名**       | **类型** | **长度** | **约束** | **说明**                                   |
| ---------------- | -------- | -------- | -------- | ------------------------------------------ |
| **id**           | CHAR     | 36       | PK       | 主键，UUID                                 |
| **userId**       | CHAR     | 36       | Not Null | **逻辑外键** -> 关联 User 表 ID (申请学生) |
| **startTime**    | DATETIME | -        | Not Null | 请假开始时间                               |
| **endTime**      | DATETIME | -        | Not Null | 请假结束时间                               |
| **leaveType**    | INT      | 11       | Not Null | 请假类型 (0:病假, 1:事假等 枚举索引)       |
| **reason**       | TEXT     | -        | Not Null | 请假原因                                   |
| **status**       | TINYINT  | 1        | -        | 审核状态 (1:通过, 0:拒绝, NULL:待审核)     |
| **gmt_create**   | DATETIME | -        | Not Null | 创建时间                                   |
| **gmt_modified** | DATETIME | -        | Not Null | 更新时间                                   |

#### 表名：`student_leave_reason` (审批/评论记录表)

> 对应实体：LeaveReason
>
> 注意：leaveId 是隐式字段，由 Leave 实体的 @JoinColumn 定义。

| **字段名**       | **类型** | **长度** | **约束** | **说明**                                      |
| ---------------- | -------- | -------- | -------- | --------------------------------------------- |
| **id**           | CHAR     | 36       | PK       | 主键，UUID                                    |
| **from_user_id** | CHAR     | 36       | Not Null | **逻辑外键** -> 关联 User 表 ID (评论/审批人) |
| **comment**      | TEXT     | -        | Not Null | 评论或审批意见                                |
| **leaveId**      | CHAR     | 36       | Not Null | **逻辑外键** -> 关联 student_leave 表 ID      |
| **gmt_create**   | DATETIME | -        | Not Null | 创建时间                                      |
| **gmt_modified** | DATETIME | -        | Not Null | 更新时间                                      |

------

### 4.. 模块：smp-room (查寝服务)

#### 表名：`student_room_check` (寝室签到记录表)

> **对应实体**：`StudentRoomCheck`

| **字段名**              | **类型** | **长度** | **约束** | **说明**                               |
| ----------------------- | -------- | -------- | -------- | -------------------------------------- |
| **id**                  | CHAR     | 36       | PK       | 主键，UUID                             |
| **userId**              | CHAR     | 36       | Not Null | **逻辑外键** -> 关联 User 表 ID (学生) |
| **checkTime**           | DATETIME | -        | Not Null | 实际打卡时间                           |
| **longitude**           | DOUBLE   | -        | Not Null | 经度                                   |
| **latitude**            | DOUBLE   | -        | Not Null | 纬度                                   |
| **filenameExtension**   | VARCHAR  | 255      | Not Null | 上传照片的文件后缀 (如 .jpg)           |
| **checkFaceSimilarity** | FLOAT    | -        | Not Null | 人脸比对相似度数值                     |
| **gmt_create**          | DATETIME | -        | Not Null | 创建时间                               |
| **gmt_modified**        | DATETIME | -        | Not Null | 更新时间                               |

#### 表名：`student_room_check_meta_data` (查寝配置元数据表)

> 对应实体：StudentRoomCheckMetaData
>
> 注意：使用复合主键 (id + belongCounselorId)。

| **字段名**            | **类型** | **长度** | **约束** | **说明**                               |
| --------------------- | -------- | -------- | -------- | -------------------------------------- |
| **id**                | CHAR     | 50       | PK       | **配置键** (对应 Java 中的 key 字段)   |
| **belongCounselorId** | CHAR     | 36       | PK       | **配置归属人** (关联 User 表辅导员 ID) |
| **value**             | TEXT     | -        | Not Null | 配置的具体值                           |
| **gmt_create**        | DATETIME | -        | Not Null | 创建时间                               |
| **gmt_modified**      | DATETIME | -        | Not Null | 更新时间                               |

#### 建表语句

```sql
-- 1. 请假申请表
CREATE TABLE `student_leave` (
  `id` char(36) NOT NULL COMMENT '主键ID',
  `user_id` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(申请学生)',
  `start_time` datetime NOT NULL COMMENT '请假开始时间',
  `end_time` datetime NOT NULL COMMENT '请假结束时间',
  `leave_type` int(11) NOT NULL COMMENT '请假类型(枚举值,如:0病假,1事假)',
  `reason` text COMMENT '请假原因',
  `status` tinyint(1) DEFAULT NULL COMMENT '审核状态(1:通过 0:未通过 NULL:待审核)',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 请假审批/评论记录表
CREATE TABLE `student_leave_reason` (
  `id` char(36) NOT NULL COMMENT '主键ID',
  `from_user_id` char(36) NOT NULL COMMENT '逻辑外键：关联User表(评论人/审批人)',
  `comment` text NOT NULL COMMENT '审批意见或评论内容',
  `leave_id` char(36) COMMENT '逻辑外键：关联student_leave表(隐式关联字段)',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 学生寝室签到/打卡记录表
CREATE TABLE `student_room_check` (
  `id` char(36) NOT NULL COMMENT '主键ID',
  `user_id` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(学生)',
  `check_time` datetime NOT NULL COMMENT '实际打卡时间',
  `longitude` double COMMENT '经度',
  `latitude` double COMMENT '纬度',
  `filename_extension` varchar(255) COMMENT '上传照片的扩展名',
  `check_face_similarity` float COMMENT '人脸比对相似度',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 查寝元数据配置表 (复合主键)
CREATE TABLE `student_room_check_meta_data` (
  `id` char(50) NOT NULL COMMENT '配置键(对应Java字段key)',
  `belong_counselor_id` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(辅导员)',
  `value` text NOT NULL COMMENT '配置值',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  -- 复合主键：确保同一个辅导员对同一个配置项只有一条记录
  PRIMARY KEY (`id`, `belong_counselor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


```





```sql
INSERT INTO `user` (
  `id`,
  `username`,
  `password`,
  `name`,
  `tel`,
  `email`,
  `role_id`,   -- 注意：如果你之前的建表语句里用的是 roleId，请这里也改成 roleId
  `gmt_create`,
  `gmt_modified`
)
VALUES (
  UUID(),             -- 自动生成一个 UUID
  'admin',            -- 用户名
  '123456',           -- 密码 (如果是 Spring Security，这里可能需要填加密后的字符串)
  '系统管理员',        -- 真实姓名
  '13800000000',      -- 电话 (可选)
  'admin@smp.com',    -- 邮箱 (可选)
  3,       -- 关联刚才插入的角色ID
  NOW(),
  NOW()
);
```





# 接口文档

## 1. 获取所有学生班级接口

### 接口信息
- **接口路径**: `GET /student_class_users`
- **接口描述**: 获取当前登录学生加入的所有班级信息
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数:无



### 响应示例
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": {
        "content": [
            {
                "user": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "studentUser": {
                        "id": "student_li",
                        "birthday": "2005-12-31T16:00:00.000+00:00",
                        "sex": true,
                        "age": 20,
                        "studentId": "20260001",
                        "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                        "idCard": "110101200601010001",
                        "politicalStatus": "群众",
                        "ethnic": "汉族",
                        "apartment": {
                            "id": "apt_001",
                            "name": "一号公寓",
                            "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                            "gmtModified": "2026-01-09T06:39:15.000+00:00"
                        },
                        "roomNum": "301",
                        "address": "北京市海淀区",
                        "bedNum": "1",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "gmtCreate": "2026-01-09T06:26:12.000+00:00",
                    "gmtModified": "2026-01-09T06:26:12.000+00:00"
                },
                "studentClass": {
                    "id": "1",
                    "name": "一班",
                    "classNum": "1",
                    "user": {
                        "id": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                        "name": "系统管理员",
                        "tel": "13800000000",
                        "email": "admin@smp.com",
                        "username": "admin",
                        "role": {
                            "id": "3",
                            "name": "辅导员",
                            "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                            "gmtModified": "2026-01-09T06:02:52.000+00:00"
                        },
                        "studentUser": null,
                        "gmtCreate": "2026-01-09T06:09:45.000+00:00",
                        "gmtModified": "2026-01-09T06:09:45.000+00:00"
                    },
                    "gmtCreate": "2026-01-11T07:00:44.000+00:00",
                    "gmtModified": "2026-01-11T07:00:45.000+00:00"
                },
                "gmtCreate": "2026-01-11T06:59:57.000+00:00",
                "gmtModified": "2026-01-11T06:59:58.000+00:00"
            }
        ],
        "pageable": {
            "sort": {
                "sorted": true,
                "unsorted": false,
                "empty": false
            },
            "offset": 0,
            "pageSize": 20,
            "pageNumber": 0,
            "unpaged": false,
            "paged": true
        },
        "totalPages": 1,
        "totalElements": 1,
        "last": true,
        "number": 0,
        "size": 20,
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "numberOfElements": 1,
        "first": true,
        "empty": false
    }
}{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "class123",
        "className": "计算机科学与技术1班",
        "teacherName": "张老师",
        "gmtCreate": "2023-01-01 10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "number": 0,
    "size": 20
  }
}
```


## 2. 加入班级接口

### 接口信息
- **接口路径**: `POST /join_class`
- **接口描述**: 学生加入指定班级
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数
| 参数名       | 类型   | 必填 | 描述 |
| ------------ | ------ | ---- | ---- |
| **classNum** | String | 是   | 班号 |

### 请求示例
```http
POST /join_class
Content-Type: application/x-www-form-urlencoded

classNum=CS202101
```


### 响应示例
```json
{
    "code": 400,
    "msg": "已经在这个班级了",
    "data": null
}
or
{
    "code": 201,
    "msg": "创建成功",
    "data": {
        "user": {
            "id": "student_li",
            "name": "李同学",
            "tel": "13800000002",
            "email": "li@smp.com",
            "username": "stu_li",
            "role": {
                "id": "1",
                "name": "学生",
                "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                "gmtModified": "2026-01-09T06:02:52.000+00:00"
            },
            "studentUser": {
                "id": "student_li",
                "birthday": "2005-12-31T16:00:00.000+00:00",
                "sex": true,
                "age": 20,
                "studentId": "20260001",
                "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                "idCard": "110101200601010001",
                "politicalStatus": "群众",
                "ethnic": "汉族",
                "apartment": {
                    "id": "apt_001",
                    "name": "一号公寓",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "roomNum": "301",
                "address": "北京市海淀区",
                "bedNum": "1",
                "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                "gmtModified": "2026-01-09T06:39:15.000+00:00"
            },
            "gmtCreate": "2026-01-09T06:26:12.000+00:00",
            "gmtModified": "2026-01-09T06:26:12.000+00:00"
        },
        "studentClass": {
            "id": "1",
            "name": "一班",
            "classNum": "1",
            "user": {
                "id": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                "name": "系统管理员",
                "tel": "13800000000",
                "email": "admin@smp.com",
                "username": "admin",
                "role": {
                    "id": "3",
                    "name": "辅导员",
                    "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                    "gmtModified": "2026-01-09T06:02:52.000+00:00"
                },
                "studentUser": null,
                "gmtCreate": "2026-01-09T06:09:45.000+00:00",
                "gmtModified": "2026-01-09T06:09:45.000+00:00"
            },
            "gmtCreate": "2026-01-11T07:00:44.000+00:00",
            "gmtModified": "2026-01-11T07:00:45.000+00:00"
        },
        "gmtCreate": "2026-01-11T07:30:10.035+00:00",
        "gmtModified": "2026-01-11T07:30:10.035+00:00"
    }
}
```


## 3. 学生退出班级接口

### 接口信息
- **接口路径**: `POST /quit_class`
- **接口描述**: 学生退出指定班级
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数
| 参数名         | 类型   | 必填 | 描述   |
| -------------- | ------ | ---- | ------ |
| studentClassId | String | 是   | 班级ID |

### 请求示例
```http
POST /quit_class
Content-Type: application/x-www-form-urlencoded

studentClassId=class123456
```


### 响应示例
```json
{
  "code": 204,
  "message": "no content"
}
```


## 注意事项
- 所有接口均需携带有效的认证信息
- 分页参数可选，不传时使用默认值
- 响应状态码遵循RESTful规范
- 错误情况会返回相应的错误码和消息

## 4. 新增请假信息接口文档

### 接口信息
- **接口路径**: `POST /leave`
- **接口描述**: 学生新增请假申请
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数
| 参数名    | 类型   | 必填 | 格式       | 描述               |
| --------- | ------ | ---- | ---------- | ------------------ |
| startTime | Date   | 是   | yyyy-MM-dd | 请假开始时间       |
| endTime   | Date   | 是   | yyyy-MM-dd | 请假结束时间       |
| reason    | String | 是   | -          | 请假原因           |
| leaveType | String | 是   | -          | 请假类型（枚举值） |

### 请假类型说明
- `SICK_LEAVE` - 病假
- `AFFAIR_LEAVE` - 事假
- `LEAVE_OF_ABSENCE` - 旷课假
- [ROOM_LEAVE](file://E:\VS%20code%20Project\Java\smp-server\smp-leave\src\main\java\top\itning\smp\smpleave\entity\LeaveType.java#L15-L15) - 寝室假
- [CLASS_LEAVE](file://E:\VS%20code%20Project\Java\smp-server\smp-leave\src\main\java\top\itning\smp\smpleave\entity\LeaveType.java#L11-L11) - 课假

### 请求示例
```http
POST /leave
Content-Type: application/x-www-form-urlencoded

startTime=2023-12-01&endTime=2023-12-03&reason=生病需要治疗&leaveType=SICK_LEAVE
```


### 响应示例
```json
{
    "code": 201,
    "msg": "创建成功",
    "data": {
        "id": "0db6c7bd-5a51-4fb5-ade2-6837b02913d2",
        "user": {
            "id": "student_li",
            "name": "李同学",
            "tel": "13800000002",
            "email": "li@smp.com",
            "username": "stu_li",
            "role": {
                "id": "1",
                "name": "学生",
                "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                "gmtModified": "2026-01-09T06:02:52.000+00:00"
            },
            "studentUser": {
                "id": "student_li",
                "birthday": "2005-12-31T16:00:00.000+00:00",
                "sex": true,
                "age": 20,
                "studentId": "20260001",
                "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                "idCard": "110101200601010001",
                "politicalStatus": "群众",
                "ethnic": "汉族",
                "apartment": {
                    "id": "apt_001",
                    "name": "一号公寓",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "roomNum": "301",
                "address": "北京市海淀区",
                "bedNum": "1",
                "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                "gmtModified": "2026-01-09T06:39:15.000+00:00"
            },
            "gmtCreate": "2026-01-09T06:26:12.000+00:00",
            "gmtModified": "2026-01-09T06:26:12.000+00:00"
        },
        "startTime": "2026-01-03T16:00:00.000+00:00",
        "endTime": "2026-01-06T15:59:59.000+00:00",
        "leaveType": "ROOM_LEAVE",
        "reason": "生病",
        "status": null,
        "leaveReasonList": null,
        "gmtCreate": "2026-01-11T07:43:43.842+00:00",
        "gmtModified": "2026-01-11T07:43:43.842+00:00"
    }
}
or
{
    "code": 400,
    "msg": "2026年01月04日至2026年01月06日您已经请过假了",
    "data": null
}
```


### 响应状态码
- `201 Created` - 请假申请创建成功
- `400 Bad Request` - 请求参数错误
- `401 Unauthorized` - 未登录或权限不足
- `403 Forbidden` - 权限不足（非学生身份）

### 注意事项
- 所有参数均为必填项
- 时间格式必须为 `yyyy-MM-dd`
- 请假类型必须为预定义的枚举值之一
- 仅学生身份可发起请假申请
- 请假申请创建后需等待辅导员审批

## 5. 学生获取请假信息接口文档

### 接口信息
- **接口路径**: `GET /studentLeaves`
- **接口描述**: 学生获取自己的请假信息列表
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数
| 参数名 | 类型    | 必填 | 默认值           | 描述     |
| ------ | ------- | ---- | ---------------- | -------- |
| page   | Integer | 否   | 0                | 页码     |
| size   | Integer | 否   | 20               | 每页数量 |
| sort   | String  | 否   | gmtModified,desc | 排序字段 |

### 响应示例
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": {
        "content": [
            {
                "id": "0db6c7bd-5a51-4fb5-ade2-6837b02913d2",
                "studentUser": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "birthday": "2005-12-31T16:00:00.000+00:00",
                    "sex": true,
                    "age": 20,
                    "studentId": "20260001",
                    "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                    "idCard": "110101200601010001",
                    "politicalStatus": "群众",
                    "ethnic": "汉族",
                    "apartment": {
                        "id": "apt_001",
                        "name": "一号公寓",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "roomNum": "301",
                    "bedNum": "1",
                    "address": "北京市海淀区",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "startTime": "2026-01-03T16:00:00.000+00:00",
                "endTime": "2026-01-06T15:59:59.000+00:00",
                "leaveType": "ROOM_LEAVE",
                "reason": "生病",
                "status": null,
                "leaveReasonList": [],
                "gmtCreate": "2026-01-11T07:43:44.000+00:00",
                "gmtModified": "2026-01-11T07:43:44.000+00:00"
            },
            {
                "id": "leave_003",
                "studentUser": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "birthday": "2005-12-31T16:00:00.000+00:00",
                    "sex": true,
                    "age": 20,
                    "studentId": "20260001",
                    "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                    "idCard": "110101200601010001",
                    "politicalStatus": "群众",
                    "ethnic": "汉族",
                    "apartment": {
                        "id": "apt_001",
                        "name": "一号公寓",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "roomNum": "301",
                    "bedNum": "1",
                    "address": "北京市海淀区",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "startTime": "2026-01-10T00:00:00.000+00:00",
                "endTime": "2026-01-12T10:00:00.000+00:00",
                "leaveType": "CLASS_LEAVE",
                "reason": "发烧感冒，头痛欲裂，无法上课",
                "status": null,
                "leaveReasonList": [],
                "gmtCreate": "2026-01-09T06:27:36.000+00:00",
                "gmtModified": "2026-01-09T06:27:36.000+00:00"
            },
            {
                "id": "leave_004",
                "studentUser": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "birthday": "2005-12-31T16:00:00.000+00:00",
                    "sex": true,
                    "age": 20,
                    "studentId": "20260001",
                    "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                    "idCard": "110101200601010001",
                    "politicalStatus": "群众",
                    "ethnic": "汉族",
                    "apartment": {
                        "id": "apt_001",
                        "name": "一号公寓",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "roomNum": "301",
                    "bedNum": "1",
                    "address": "北京市海淀区",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "startTime": "2026-01-01T00:00:00.000+00:00",
                "endTime": "2026-01-02T10:00:00.000+00:00",
                "leaveType": "ROOM_LEAVE",
                "reason": "家里有急事，需要回家一趟",
                "status": true,
                "leaveReasonList": [],
                "gmtCreate": "2026-01-09T06:27:36.000+00:00",
                "gmtModified": "2026-01-09T06:27:36.000+00:00"
            },
            {
                "id": "leave_001",
                "studentUser": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "birthday": "2005-12-31T16:00:00.000+00:00",
                    "sex": true,
                    "age": 20,
                    "studentId": "20260001",
                    "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                    "idCard": "110101200601010001",
                    "politicalStatus": "群众",
                    "ethnic": "汉族",
                    "apartment": {
                        "id": "apt_001",
                        "name": "一号公寓",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "roomNum": "301",
                    "bedNum": "1",
                    "address": "北京市海淀区",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "startTime": "2026-01-10T00:00:00.000+00:00",
                "endTime": "2026-01-12T10:00:00.000+00:00",
                "leaveType": "CLASS_LEAVE",
                "reason": "发烧感冒，头痛欲裂，无法上课",
                "status": null,
                "leaveReasonList": [],
                "gmtCreate": "2026-01-09T06:26:12.000+00:00",
                "gmtModified": "2026-01-09T06:26:12.000+00:00"
            },
            {
                "id": "leave_002",
                "studentUser": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "birthday": "2005-12-31T16:00:00.000+00:00",
                    "sex": true,
                    "age": 20,
                    "studentId": "20260001",
                    "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                    "idCard": "110101200601010001",
                    "politicalStatus": "群众",
                    "ethnic": "汉族",
                    "apartment": {
                        "id": "apt_001",
                        "name": "一号公寓",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "roomNum": "301",
                    "bedNum": "1",
                    "address": "北京市海淀区",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "startTime": "2026-01-01T00:00:00.000+00:00",
                "endTime": "2026-01-02T10:00:00.000+00:00",
                "leaveType": "ROOM_LEAVE",
                "reason": "家里有急事，需要回家一趟",
                "status": true,
                "leaveReasonList": [],
                "gmtCreate": "2026-01-09T06:26:12.000+00:00",
                "gmtModified": "2026-01-09T06:26:12.000+00:00"
            }
        ],
        "pageable": {
            "sort": {
                "unsorted": false,
                "sorted": true,
                "empty": false
            },
            "offset": 0,
            "pageNumber": 0,
            "pageSize": 20,
            "paged": true,
            "unpaged": false
        },
        "totalElements": 5,
        "totalPages": 1,
        "last": true,
        "number": 0,
        "size": 20,
        "sort": {
            "unsorted": false,
            "sorted": true,
            "empty": false
        },
        "numberOfElements": 5,
        "first": true,
        "empty": false
    }
}
```


### 响应状态码
- `200 OK` - 获取请假信息成功
- `401 Unauthorized` - 未登录或权限不足
- `403 Forbidden` - 权限不足（非学生身份）

### 注意事项
- 仅返回当前登录学生的请假记录
- 支持分页查询，默认每页20条记录
- 按修改时间倒序排列（最新的在前）
- 不需要额外的请求参数，自动获取当前登录学生信息



## 6. 检查是否允许打卡接口文档

### 接口信息
- **接口路径**: `GET /allow_check`
- **接口描述**: 检查当前时间学生是否允许进行寝室打卡
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数
| 参数名 | 类型 | 必填 | 描述                                   |
| ------ | ---- | ---- | -------------------------------------- |
| 无     | -    | -    | 无需额外参数，自动获取当前登录学生信息 |

### 响应示例
```json
{
    "code": 200,
    "msg": "查询成功",
    "data": true
}
```

### 响应状态码
- `200 OK` - 检查成功
- `401 Unauthorized` - 未登录或权限不足
- `403 Forbidden` - 权限不足（非学生身份）

### 注意事项
- 无需传递任何参数，系统自动获取当前登录学生信息
- 返回当前时间是否在允许打卡的时间范围内
- 用于前端判断是否显示打卡按钮

## 7. 获取学生的寝室打卡信息接口文档

### 接口信息
- **接口路径**: `GET /checks`
- **接口描述**: 获取当前登录学生的所有寝室打卡记录
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数
| 参数名 | 类型    | 必填 | 默认值         | 描述                       |
| ------ | ------- | ---- | -------------- | -------------------------- |
| page   | Integer | 否   | 0              | 页码                       |
| size   | Integer | 否   | 20             | 每页数量                   |
| sort   | String  | 否   | checkTime,desc | 排序字段（按打卡时间倒序） |

### 响应示例

```json
{
    "code": 200,
    "msg": "查询成功",
    "data": {
        "content": [
            {
                "id": "check_record_002",
                "user": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "studentUser": {
                        "id": "student_li",
                        "birthday": "2005-12-31T16:00:00.000+00:00",
                        "sex": true,
                        "age": 20,
                        "studentId": "20260001",
                        "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                        "idCard": "110101200601010001",
                        "politicalStatus": "群众",
                        "ethnic": "汉族",
                        "apartment": {
                            "id": "apt_001",
                            "name": "一号公寓",
                            "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                            "gmtModified": "2026-01-09T06:39:15.000+00:00"
                        },
                        "roomNum": "301",
                        "address": "北京市海淀区",
                        "bedNum": "1",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "gmtCreate": "2026-01-09T06:26:12.000+00:00",
                    "gmtModified": "2026-01-09T06:26:12.000+00:00"
                },
                "checkTime": "2026-01-09T06:27:36.000+00:00",
                "longitude": 116.4076,
                "latitude": 39.9041,
                "filenameExtension": "png",
                "checkFaceSimilarity": 0.85,
                "gmtCreate": "2026-01-09T06:27:36.000+00:00",
                "gmtModified": "2026-01-09T06:27:36.000+00:00"
            },
            {
                "id": "check_record_001",
                "user": {
                    "id": "student_li",
                    "name": "李同学",
                    "tel": "13800000002",
                    "email": "li@smp.com",
                    "username": "stu_li",
                    "role": {
                        "id": "1",
                        "name": "学生",
                        "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                        "gmtModified": "2026-01-09T06:02:52.000+00:00"
                    },
                    "studentUser": {
                        "id": "student_li",
                        "birthday": "2005-12-31T16:00:00.000+00:00",
                        "sex": true,
                        "age": 20,
                        "studentId": "20260001",
                        "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                        "idCard": "110101200601010001",
                        "politicalStatus": "群众",
                        "ethnic": "汉族",
                        "apartment": {
                            "id": "apt_001",
                            "name": "一号公寓",
                            "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                            "gmtModified": "2026-01-09T06:39:15.000+00:00"
                        },
                        "roomNum": "301",
                        "address": "北京市海淀区",
                        "bedNum": "1",
                        "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                        "gmtModified": "2026-01-09T06:39:15.000+00:00"
                    },
                    "gmtCreate": "2026-01-09T06:26:12.000+00:00",
                    "gmtModified": "2026-01-09T06:26:12.000+00:00"
                },
                "checkTime": "2026-01-08T06:27:36.000+00:00",
                "longitude": 116.4075,
                "latitude": 39.9043,
                "filenameExtension": "jpg",
                "checkFaceSimilarity": 0.98,
                "gmtCreate": "2026-01-09T06:27:36.000+00:00",
                "gmtModified": "2026-01-09T06:27:36.000+00:00"
            }
        ],
        "pageable": {
            "sort": {
                "sorted": true,
                "unsorted": false,
                "empty": false
            },
            "offset": 0,
            "pageNumber": 0,
            "pageSize": 20,
            "paged": true,
            "unpaged": false
        },
        "totalElements": 2,
        "last": true,
        "totalPages": 1,
        "number": 0,
        "size": 20,
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "numberOfElements": 2,
        "first": true,
        "empty": false
    }
}
```


### 响应状态码
- `200 OK` - 获取打卡信息成功
- `401 Unauthorized` - 未登录或权限不足
- `403 Forbidden` - 权限不足（非学生身份）

### 注意事项
- 无需传递额外参数，自动获取当前登录学生信息
- 支持分页查询，默认每页20条记录
- 按打卡时间倒序排列（最新的打卡记录在前）
- 仅返回当前登录学生的打卡记录





## 8. 学生打卡接口文档

### 接口信息
- **接口路径**: `POST /check`
- **接口描述**: 学生进行寝室打卡操作，需要上传照片和GPS位置信息
- **权限要求**: 学生登录 ([@MustStudentLogin](file://E:\VS%20code%20Project\Java\smp-server\smp-room\src\main\java\top\itning\smp\smproom\security\MustStudentLogin.java#L7-L12))

### 请求参数
| 参数名    | 类型          | 必填 | 描述         |
| --------- | ------------- | ---- | ------------ |
| file      | MultipartFile | 是   | 打卡照片文件 |
| longitude | double        | 是   | 打卡位置经度 |
| latitude  | double        | 是   | 打卡位置纬度 |

### 请求示例
```http
POST /check
Content-Type: multipart/form-data

file=<照片文件>&longitude=120.123456&latitude=30.123456
```


### 响应示例
```json
{
    "code": 201,
    "msg": "创建成功",
    "data": {
        "id": "2b021a98-f3b6-4850-a7dd-6d2d277ae962",
        "user": {
            "id": "student_li",
            "name": "李同学",
            "tel": "13800000002",
            "email": "li@smp.com",
            "username": "stu_li",
            "role": {
                "id": "1",
                "name": "学生",
                "gmtCreate": "2026-01-09T06:02:52.000+00:00",
                "gmtModified": "2026-01-09T06:02:52.000+00:00"
            },
            "studentUser": {
                "id": "student_li",
                "birthday": "2005-12-31T16:00:00.000+00:00",
                "sex": true,
                "age": 20,
                "studentId": "20260001",
                "belongCounselorId": "cbc6e729-ed21-11f0-9fe1-6c2408fbe0dd",
                "idCard": "110101200601010001",
                "politicalStatus": "群众",
                "ethnic": "汉族",
                "apartment": {
                    "id": "apt_001",
                    "name": "一号公寓",
                    "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                    "gmtModified": "2026-01-09T06:39:15.000+00:00"
                },
                "roomNum": "301",
                "address": "北京市海淀区",
                "bedNum": "1",
                "gmtCreate": "2026-01-09T06:39:15.000+00:00",
                "gmtModified": "2026-01-09T06:39:15.000+00:00"
            },
            "gmtCreate": "2026-01-09T06:26:12.000+00:00",
            "gmtModified": "2026-01-09T06:26:12.000+00:00"
        },
        "checkTime": "2026-01-11T09:37:46.875+00:00",
        "longitude": 1.0,
        "latitude": 1.0,
        "filenameExtension": "none",
        "checkFaceSimilarity": 1.0,
        "gmtCreate": "2026-01-11T09:37:46.908+00:00",
        "gmtModified": "2026-01-11T09:37:46.908+00:00"
    }
}
or
{
    "code": 400,
    "msg": "您今天已经打过卡了，不能重复打卡",
    "data": null
}
```


### 响应状态码
- `201 Created` - 打卡成功
- `400 Bad Request` - 请求参数错误（位置不在允许范围内等）
- `401 Unauthorized` - 未登录或权限不足
- `403 Forbidden` - 权限不足（非学生身份）
- `500 Internal Server Error` - 服务器内部错误

### 注意事项
- 需要上传照片文件和GPS经纬度坐标
- 系统会验证当前位置是否在允许打卡的地理围栏内
- 系统会验证当前时间是否在允许打卡的时间段内
- 仅学生身份可进行打卡操作



















```
"Authorization"
```

管理员："eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjgxNTcwNDAsImxvZ2luVXNlciI6IntcIm5hbWVcIjpcIuezu-e7n-euoeeQhuWRmFwiLFwidXNlcm5hbWVcIjpcImFkbWluXCIsXCJyb2xlXCI6e1wiaWRcIjpcIjNcIixcIm5hbWVcIjpcIui-heWvvOWRmFwiLFwiZ210Q3JlYXRlXCI6MTc2NzkzODU3MjAwMCxcImdtdE1vZGlmaWVkXCI6MTc2NzkzODU3MjAwMH0sXCJlbWFpbFwiOlwiYWRtaW5Ac21wLmNvbVwiLFwidGVsXCI6XCIxMzgwMDAwMDAwMFwifSIsImlzcyI6Iml0bmluZyJ9.BwuqlITgx8jmHVaMhaSRRjZ1kuv_hq6qE76K8rmgRoo"

学生：eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjgxNTcxMjcsImxvZ2luVXNlciI6IntcIm5hbWVcIjpcIuadjuWQjOWtplwiLFwidXNlcm5hbWVcIjpcInN0dV9saVwiLFwicm9sZVwiOntcImlkXCI6XCIxXCIsXCJuYW1lXCI6XCLlrabnlJ9cIixcImdtdENyZWF0ZVwiOjE3Njc5Mzg1NzIwMDAsXCJnbXRNb2RpZmllZFwiOjE3Njc5Mzg1NzIwMDB9LFwiZW1haWxcIjpcImxpQHNtcC5jb21cIixcInRlbFwiOlwiMTM4MDAwMDAwMDJcIn0iLCJpc3MiOiJpdG5pbmcifQ.APhmJ2KgzaU9D0Eyk0fl5lrifsaIzwyfRTA1ttuyxLo
