package com.ly.yin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@MapperScan("com.ly.yin.mapper")
public class YinApp {
    @GetMapping("/hello")
    public String hello(){
        return "hello Yin project...";
    }
    public static void main(String[] args){
        SpringApplication.run(YinApp.class);
    }
}
