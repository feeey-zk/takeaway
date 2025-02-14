package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 通过 Spring Boot 的配置绑定机制，将配置文件中的属性自动映射到该类的字段中
 */
//Spring 提供的注解，将该类注册为一个 Spring 管理的 Bean，可以通过依赖注入的方式在其他组件中使用这个类
@Component
//Spring Boot 提供的注解，用于将配置文件中的属性绑定到 Java Bean 中
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
