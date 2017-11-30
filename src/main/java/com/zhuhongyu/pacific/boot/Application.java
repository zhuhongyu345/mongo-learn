package com.zhuhongyu.pacific.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot 应用启动类
 * Created by zhuhongyu
 */
@ComponentScan(basePackages = "com.zhuhongyu.pacific")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("____________________________________");
    }

}
