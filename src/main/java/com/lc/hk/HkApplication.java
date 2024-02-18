package com.lc.hk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.lc.hk.mapper")
@EnableScheduling
@SpringBootApplication
public class HkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HkApplication.class, args);
    }

}
