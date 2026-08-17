# ====== 阶段1: 构建 ======
# 使用 Maven 镜像编译源码（源码兼容 JDK 1.8，用 JDK 17 构建完全兼容）
FROM maven:3.8-eclipse-temurin-17 AS builder

WORKDIR /build

# 先单独复制 pom.xml，利用 Docker 缓存加速依赖下载
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并编译打包，跳过测试
COPY src ./src
RUN mvn clean package -DskipTests

# ====== 阶段2: 运行 ======
# 运行时只需要 JRE，镜像更小
FROM eclipse-temurin:17-jre

LABEL maintainer="todo-backend"

WORKDIR /app

# 从构建阶段复制产物
COPY --from=builder /build/target/todo-backend.jar app.jar

# 时区设置
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
