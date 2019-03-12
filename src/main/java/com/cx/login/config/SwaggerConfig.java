package com.cx.login.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfig {

    @Value("${swagger.show:true}")
    private boolean swaggerShow;

    @Bean
    public Docket customDocket() {
        Set<String> set = new HashSet<>();
        set.add("application/json");
        set.add("application/json");
        return new Docket(DocumentationType.SWAGGER_2).enable(swaggerShow).apiInfo(apiInfo()).produces(set);
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("机械之家", "https://manage.jipeicheng.cn/", "");
        return new ApiInfoBuilder().title("机配城运营支撑管理系统").description("API接口").contact(contact).version("1.0.0").build();
    }
}
