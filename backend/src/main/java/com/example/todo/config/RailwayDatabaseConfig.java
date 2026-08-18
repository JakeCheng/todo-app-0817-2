package com.example.todo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Railway 数据库适配配置
 * <p>
 * 兼容本地开发与 Railway 部署：
 * - 本地：不设置任何环境变量，SpringBoot 用 application.yml 默认配置
 * - Railway：从 MYSQL_URL 环境变量解析（mysql://user:pass@host:port/db），
 *   自动转换为 jdbc:mysql:// 格式
 * <p>
 * 注：本类使用 @Primary 避免与 SpringBoot 自动配置的 DataSource 冲突
 */
@Configuration
public class RailwayDatabaseConfig {

    /**
     * 兜底：让 SpringBoot 走 application.yml 的 spring.datasource.*
     * 关键：当 MYSQL_URL 未设置时，这个 Bean 让自动配置生效
     */
    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 主数据源：当 Railway 注入的 MYSQL_URL 存在时，解析并覆盖默认配置
     */
    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        // Railway MySQL 插件注入的变量名（优先 MYSQL_URL，兼容 MYSQLURL）
        String mysqlUrl = System.getenv("MYSQL_URL");
        if (mysqlUrl == null || mysqlUrl.isEmpty()) {
            mysqlUrl = System.getenv("MYSQLURL");
        }
        // 用户在 Railway 控制台手动设置的变量名（推荐）
        if (mysqlUrl == null || mysqlUrl.isEmpty()) {
            mysqlUrl = System.getenv("DATABASE_URL");
        }

        if (StringUtils.hasText(mysqlUrl) && mysqlUrl.startsWith("mysql://")) {
            return buildFromRailwayUrl(mysqlUrl, properties);
        }

        // 本地开发：用 application.yml 默认值
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * 解析 Railway MySQL URL：mysql://user:pass@host:port/db
     * 转换为 jdbc 格式
     */
    private DataSource buildFromRailwayUrl(String rawUrl, DataSourceProperties properties) {
        try {
            URI uri = new URI(rawUrl);

            // 解析 user:password
            String userInfo = uri.getUserInfo();
            String username = "";
            String password = "";
            if (userInfo != null) {
                int idx = userInfo.indexOf(':');
                if (idx >= 0) {
                    username = userInfo.substring(0, idx);
                    password = userInfo.substring(idx + 1);
                } else {
                    username = userInfo;
                }
            }

            // 解析 host:port/db
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 3306 : uri.getPort();
            String db = uri.getPath();
            if (db != null && db.startsWith("/")) {
                db = db.substring(1);
            }
            if (db == null || db.isEmpty()) {
                db = "railway";
            }

            // 拼 jdbc URL
            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + db
                    + "?useUnicode=true&characterEncoding=utf8&useSSL=false"
                    + "&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
                    + "&createDatabaseIfNotExist=true";

            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            System.out.println(">>> [RailwayDatabaseConfig] 已从 MYSQL_URL 解析数据源: "
                    + host + ":" + port + "/" + db + " (user=" + username + ")");
            return ds;
        } catch (Exception e) {
            throw new IllegalArgumentException("MYSQL_URL 格式错误: " + rawUrl, e);
        }
    }
}
