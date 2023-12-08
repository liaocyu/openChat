package com.liaocyu.openChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 11:51
 * @description :
 */
@SpringBootApplication
@ServletComponentScan
public class openChatServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(openChatServerApplication.class , args);
    }
}
