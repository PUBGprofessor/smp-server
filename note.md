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

## **`smp-security` (认证授权服务)**

- **角色**：安检员。
- **作用**：负责登录认证、发令牌（Token）、权限校验。
- **场景**：用户登录时，请求会发到这里。如果用户名密码正确，它发一个 Token 给用户。其他服务（如 `smp-leave`）在处理请求前，会检查这个 Token 是否合法。

## **`smp-admin-server` (监控中心)**

- **角色**：监控室。
- **作用**：使用 Spring Boot Admin 技术，图形化展示所有微服务的健康状态、内存使用情况、日志级别等。

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
  `teacher_user_ud` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(教师)',
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
  `userId` char(36) NOT NULL COMMENT '逻辑外键：关联User表的ID(学生)',
  `student_classId` char(36) NOT NULL COMMENT '逻辑外键：关联student_class表的ID',
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
  `student_classId` char(36) NOT NULL COMMENT '逻辑外键：关联student_class表的ID',
  `gmt_create` datetime,
  `gmt_modified` datetime,
  -- 使用联合主键，确保一个学生在同一个班级里只能有一条记录
  PRIMARY KEY (`student_id`, `student_classId`)
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

