package com.myreggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 项目启动类
 *
 */
@EnableTransactionManagement   // 开启事务管理，用于操作多张数据表时使用
@ServletComponentScan    // 该注解使得过滤器生效
@Slf4j    // 该注解允许类中使用 log 记录日志
@SpringBootApplication
public class ReggieApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");
    }
}
