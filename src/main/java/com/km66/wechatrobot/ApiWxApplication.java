package com.km66.wechatrobot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@ServletComponentScan
public class ApiWxApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ApiWxApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

}
