package com.myreggie.common;

/**
 * 基于 ThreadLocal 封装工具类，用户保存和获取当前登录用户 id
 */
public class BaseContext
{
    // 工具类使用 static 修饰，看起来更专业
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id)
    {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId()
    {
        return threadLocal.get();
    }
}
