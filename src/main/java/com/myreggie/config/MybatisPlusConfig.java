package com.myreggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus 分页插件配置
 */
@Configuration  //表示这是一个Spring Boot配置类，它会在应用程序启动时被Spring容器加载。
public class MybatisPlusConfig
{
    @Bean   //这个注解用于定义一个Spring Bean，该Bean会被Spring容器管理
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor mybatisPlusInterceptor=new MybatisPlusInterceptor();
        // 添加 MyBatis Plus提供的分页插件，允许在查询数据库时进行分页操作
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
