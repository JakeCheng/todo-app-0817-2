# 本地运行指南

> 本文档说明如何在本地从零启动前后端项目并完成联调。

## 一、环境准备

### 1. 必备软件

| 软件        | 版本要求          | 说明                                 |
| ----------- | ----------------- | ------------------------------------ |
| JDK         | 1.8（源码兼容）   | 编译运行后端                         |
| Maven       | 3.6+              | 后端构建                             |
| MySQL       | 5.7 / 8.x         | 数据存储                             |
| Node.js     | 16+               | 前端运行                             |
| pnpm        | 8+                | 前端包管理（推荐）                   |

### 2. 验证环境

```powershell
java -version
mvn -version
mysql --version
node -v
pnpm -v
```

## 二、数据库准备

### 1. 启动 MySQL 服务

Windows 服务中确认 MySQL 服务已启动，或执行：

```powershell
net start mysql
```

### 2. 创建数据库

使用任意 MySQL 客户端（Navicat / DBeaver / 命令行）执行：

```sql
CREATE DATABASE IF NOT EXISTS todo_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
```

> 账号：`admin`，密码为空。
> 表会由 JPA 自动创建，**无需手动建表**。

### 3. 验证连接

```powershell
mysql -uadmin todo_db
```

能进入即说明账号权限正常。

## 三、后端启动

### 1. 进入后端目录

```powershell
cd d:\VSCode_project\share-project-0817-2\backend
```

### 2. 编译打包

```powershell
mvn clean package -DskipTests
```

> 首次执行会下载依赖，耗时较长，请耐心等待。
> 构建成功后会在 `backend\target\` 下生成 `todo-backend.jar`。

### 3. 启动服务

**方式一：jar 启动（推荐生产）**

```powershell
java -jar target\todo-backend.jar
```

**方式二：Maven 启动（推荐开发热部署）**

```powershell
mvn spring-boot:run
```

### 4. 验证后端

浏览器访问健康检查接口：

```
http://127.0.0.1:8080/api/health
```

返回如下即说明后端启动成功：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "UP",
    "service": "todo-backend"
  }
}
```

### 5. 常见问题

| 问题                                  | 原因                              | 解决                                                              |
| ------------------------------------- | --------------------------------- | ----------------------------------------------------------------- |
| `Access denied for user 'admin'`      | MySQL 中不存在 admin 账号或无密码 | 创建账号：`CREATE USER 'admin'@'localhost' IDENTIFIED BY '';` 并授权 |
| `Communications link failure`         | MySQL 未启动 / 端口占用           | 确认 MySQL 服务运行，端口 3306 未被占用                          |
| 启动后表未创建                        | `ddl-auto` 配置问题               | 检查 `application.yml` 中 `spring.jpa.hibernate.ddl-auto: update` |
| 端口 8080 被占用                      | 其他程序占用                     | 修改 `application.yml` 中 `server.port`                           |
| `'dependencies.dependency.version' for mysql:mysql-connector-java:jar is missing` | SpringBoot 2.7.18 父 POM 已不再管理 `mysql-connector-java` 版本（MySQL 官方在 8.0.31 后改用新坐标 `com.mysql:mysql-connector-j`） | 在 `pom.xml` 中为 `mysql:mysql-connector-java` 显式指定 `<version>8.0.33</version>`（已在本项目 pom.xml 中配置，正常情况下无需再改） |

## 四、前端启动

### 1. 进入前端目录

```powershell
cd d:\VSCode_project\share-project-0817-2\frontend
```

### 2. 安装依赖

```powershell
pnpm install
```

> 首次安装耗时约 1-2 分钟。

### 3. 启动开发服务器

```powershell
pnpm dev
```

启动成功后控制台会输出：

```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://x.x.x.x:5173/
```

### 4. 访问应用

浏览器打开：

```
http://localhost:5173
```

会自动跳转到登录页。

### 5. 前端构建（可选，生产打包）

```powershell
pnpm build      # 打包到 dist/
pnpm preview    # 本地预览构建产物
```

## 五、联调验证

### 1. 注册流程

1. 在登录页点击「没有账号？去注册」
2. 填写用户名（3-32 位）、密码（6-32 位）、可选昵称邮箱
3. 点击「注册并登录」
4. 注册成功后**自动登录**并跳转 Todo 主页

### 2. Todo 操作

| 操作           | 方法                                |
| -------------- | ----------------------------------- |
| 新增待办       | 顶部输入框输入内容 → 点击「新增」或回车 |
| 标记完成/未完成 | 列表中点击「标记完成」按钮            |
| 删除待办       | 点击「删除」→ 确认弹窗              |

### 3. 数据隔离验证

1. 注册用户 A，新增几条待办
2. 退出登录，注册用户 B
3. 进入主页，只能看到 B 的待办（数据隔离生效）

### 4. 修改密码验证

1. 登录页点击「忘记密码」
2. 输入用户名 + 原始密码 + 新密码
3. 修改成功后跳转登录页，使用新密码登录

## 六、关闭服务

### 关闭前端

在终端按 `Ctrl + C`

### 关闭后端

- jar 启动方式：在终端按 `Ctrl + C`
- 或根据端口杀进程：

```powershell
# 查找占用 8080 的进程
netstat -ano | findstr :8080
# 终止进程（PID 替换为实际值）
taskkill /PID <PID> /F
```

## 七、配置修改说明

### 1. 修改数据库连接

文件：`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/todo_db?...
    username: admin
    password: ""
```

### 2. 修改后端端口

文件：`backend/src/main/resources/application.yml`

```yaml
server:
  port: 8080    # 修改此处
```

### 3. 修改前端请求后端地址

文件：`frontend/src/api/request.js`

```javascript
const BASE_URL = 'http://127.0.0.1:8080'    // 修改此处
```

> 若后端端口修改，前端此地址需同步修改。

### 4. 修改 JWT 密钥/有效期

文件：`backend/src/main/resources/application.yml`

```yaml
jwt:
  secret: todo-secret-key-2026-please-change-in-production-env
  expiration: 604800000    # 7 天，单位毫秒
```

## 八、快速启动清单

按顺序执行以下命令即可完成本地启动：

```powershell
# 1. 创建数据库（在 MySQL 客户端执行）
# CREATE DATABASE todo_db DEFAULT CHARSET utf8mb4;

# 2. 启动后端
cd d:\VSCode_project\share-project-0817-2\backend
mvn clean package -DskipTests
java -jar target\todo-backend.jar

# 3. 新开终端，启动前端
cd d:\VSCode_project\share-project-0817-2\frontend
pnpm install
pnpm dev

# 4. 浏览器访问 http://localhost:5173
```
