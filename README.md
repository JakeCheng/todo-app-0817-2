# Todo 全栈项目

> SpringBoot 2.7.x（源码兼容 JDK1.8，部署 JDK17） + Vue3 + Vite + Naive-UI 的待办管理全栈应用

## 项目结构

```
share-project-0817-2/
├── backend/                # 后端 Spring Boot 项目
│   ├── pom.xml
│   ├── Dockerfile          # 基于 eclipse-temurin:17
│   ├── .gitignore
│   └── src/main/
│       ├── java/com/example/todo/
│       │   ├── TodoApplication.java       # 主启动类
│       │   ├── config/WebMvcConfig.java   # 跨域 + 拦截器注册
│       │   ├── controller/                # 控制层
│       │   ├── dto/                       # 数据传输对象
│       │   ├── entity/                    # JPA 实体
│       │   ├── exception/                 # 全局异常处理
│       │   ├── interceptor/JwtInterceptor.java
│       │   ├── repository/                # JPA Repository
│       │   ├── service/                   # 业务层
│       │   └── util/                      # Result / JwtUtils / UserContext
│       └── resources/application.yml
└── frontend/               # 前端 Vue3 项目
    ├── package.json
    ├── vite.config.js
    ├── index.html
    ├── .gitignore
    └── src/
        ├── main.js
        ├── App.vue
        ├── api/            # axios 封装 + 接口模块
        ├── router/         # 路由 + 路由守卫
        └── views/          # 4 个页面组件
```

## 环境要求

| 项目       | 版本                                |
| ---------- | ----------------------------------- |
| JDK 源码   | 1.8（兼容）                         |
| 部署 JDK   | 17（Docker 镜像 eclipse-temurin:17）|
| Maven      | 3.6+                                |
| MySQL      | 5.7+ / 8.x                          |
| Node.js    | 16+                                 |
| pnpm       | 8+（推荐）                          |

## 数据库准备

> 仅需手动创建数据库，无需手动建表，JPA 会自动建表。

```sql
CREATE DATABASE IF NOT EXISTS todo_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
```

数据库账号：`admin`，密码为空（已在 `application.yml` 配置好）。

## 后端启动

```bash
cd backend
mvn clean package -DskipTests
java -jar target/todo-backend.jar
# 或开发模式
mvn spring-boot:run
```

后端服务地址：`http://127.0.0.1:8080`

健康检查：`GET http://127.0.0.1:8080/api/health`

### Docker 部署

```bash
cd backend
mvn clean package -DskipTests
docker build -t todo-backend:1.0.0 .
docker run -d -p 8080:8080 --name todo-backend todo-backend:1.0.0
```

## 前端启动

> 用户规则：禁止 AI 自行执行 pnpm 命令，请由用户手动执行。

```bash
cd frontend
pnpm install
pnpm dev      # 开发模式，默认 http://localhost:5173
pnpm build    # 打包到 dist/
pnpm preview  # 本地预览构建产物
```

后端接口基础地址在 [frontend/src/api/request.js](frontend/src/api/request.js) 中：

```js
const BASE_URL = 'http://127.0.0.1:8080'
```

部署时按需修改。

## 接口清单

### 用户模块（前缀 `/api/user`）

| 方法 | 路径       | 说明                 | 鉴权 |
| ---- | ---------- | -------------------- | ---- |
| POST | /register  | 注册（成功返回 token） | 否   |
| POST | /login     | 登录                 | 否   |
| POST | /password  | 修改密码（原+新）    | 否   |
| GET  | /info      | 获取当前用户信息     | 是   |
| PUT  | /info      | 修改个人信息         | 是   |

### Todo 模块（前缀 `/api/todo`，全部需鉴权）

| 方法   | 路径          | 说明              |
| ------ | ------------- | ----------------- |
| GET    | /             | 查询当前用户待办  |
| POST   | /             | 新增待办          |
| PUT    | /{id}         | 修改待办          |
| PUT    | /{id}/toggle  | 切换完成状态      |
| DELETE | /{id}         | 删除待办          |

### 统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

## 默认账号

无默认账号，请通过注册页面创建。注册成功后会自动登录并跳转主页。

## 联调说明

1. 启动 MySQL，创建数据库 `todo_db`
2. 启动后端（8080）
3. 启动前端（5173），浏览器访问 `http://localhost:5173`
4. 注册 → 自动登录 → Todo 主页
