package com.myreggie.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data    // @Data 注解可以简化 java 类的开发，自动提供 getter setter equal 等方法
public class R<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据，T表示泛型，变量 data 里可以存储任意类型的数据，让代码更有通用性

    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
