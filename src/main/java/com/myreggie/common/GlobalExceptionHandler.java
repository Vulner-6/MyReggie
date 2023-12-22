package com.myreggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 编写全局的异常处理，这样就不用在不同的代码块中使用 try catch 捕获异常了,可以直接抛出异常，这里只要编写对应的捕获方法，就都可以捕获
 */
// ControllerAdvice 注解设置要捕获的 controller
@ControllerAdvice(annotations = {RestControllerAdvice.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler
{
    /**
     * 异常处理方法
     */    // 捕获到对应的异常后，进行处理
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex)
    {
        // 日志记录错误信息
        log.error(ex.getMessage());
        // 错误信息中包含 Duplicate entry 字符串特征
        if(ex.getMessage().contains("Duplicate entry"))
        {
            String[] split=ex.getMessage().split(" "); //根据字符串之间的空格进行分割
            String msg=split[2]+"已存在！";
            return R.error(msg);
        }


        return R.error("未知错误！");
    }


    /**
     * 捕获自定义的异常，当在项目中抛出自定义的异常后，这里会捕获并处理
     */    // 捕获到对应的异常后，进行处理
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex)
    {
        // 日志记录错误信息
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }



}
