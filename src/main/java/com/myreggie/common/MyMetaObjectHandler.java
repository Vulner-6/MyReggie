package com.myreggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component  //作用是将一个普通的Java类标识为Spring容器管理的Bean。Spring容器会自动扫描应用中所有带有@Component注解的类，并将它们初始化为Spring Bean，使其可以被容器管理。泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注，标识为一个Bean
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler
{
    /**
     * 当执行数据库插入操作时，下面代码会被调用，给公共字段自动赋值
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject)
    {
        log.info("公共字段自动填充[Insert]...");
        log.info(metaObject.toString());

        // 公共字段自动赋值
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        // 根据 ThreadLocal 获取到对应的员工id
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());

    }

    /**
     * 当执行数据库更新操作时，下面代码会被调用，给公共字段自动赋值
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject)
    {
        log.info("公共字段自动填充[Update]...");
        log.info(metaObject.toString());
        // 获取当前线程 id ，用于证明每一次的 http 请求，都是同一个新线程。
        long id =Thread.currentThread().getId();
        log.info("当前线程id:{}",id);

        metaObject.setValue("updateTime",LocalDateTime.now());
        // 根据 ThreadLocal 获取到对应的员工id
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
