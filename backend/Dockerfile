# 源码兼容 JDK 1.8，部署运行使用 eclipse-temurin:17
# 说明：openjdk 镜像已从 Docker Hub 下架，改用 eclipse-temurin
FROM eclipse-temurin:17-jdk

LABEL maintainer="todo-backend"

WORKDIR /app

# 复制构建产物（先在 backend 目录执行 mvn clean package -DskipTests）
COPY target/todo-backend.jar app.jar

# 时区设置
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
