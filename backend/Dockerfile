# ======================================================================
# Todo 后端 Docker 镜像（Railway 部署用）
# 多阶段构建：构建层(Maven + Temurin JDK) + 运行层(Temurin JRE)
# 关键：必须用 eclipse-temurin 镜像，openjdk 官方镜像已被 Docker Hub 下架
# ======================================================================

# ---------- 阶段 1:构建 ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# 先只复制 pom.xml,缓存依赖层(加速重复构建)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# 复制源码并打包(跳过测试加速)
COPY src ./src
RUN mvn -B -DskipTests clean package

# 构建期校验:确保 jar 存在,否则报错提示(避免静默 COPY 失败)
RUN ls -la target/*.jar && \
    test -f target/todo-backend.jar && \
    echo ">>> [Dockerfile] 构建产物 OK: target/todo-backend.jar"

# ---------- 阶段 2:运行 ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

# 从构建阶段复制生成的 jar
# 注意:pom.xml 中 finalName=todo-backend,无版本号后缀,用精确匹配
COPY --from=builder /build/target/todo-backend.jar /app/app.jar

# 再次校验 jar 已成功复制
RUN ls -la /app/app.jar && \
    echo ">>> [Dockerfile] 运行时 jar 校验通过"

# 时区设置
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Railway 会注入 PORT 环境变量
# application.yml 中已配置 server.port=${SERVER_PORT:8080}
EXPOSE 8080

# JVM 容器化参数:适配 Railway 容器内存
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
