package com.myreggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //这个注解用于指定数据库表字段在插入数据时应该自动填充。在这里，createUser 字段将在插入数据时自动填充，通常用于记录创建用户的用户 ID 或其他相关信息
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //这个注解用于指定数据库表字段在插入和更新数据时都应该自动填充。在这里，updateUser 字段将在插入和更新数据时都自动填充，通常用于记录最后一次修改数据的用户 ID 或其他相关信息
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //这个注解用于指定数据库表字段在插入数据时应该自动填充。在这里，createUser 字段将在插入数据时自动填充，通常用于记录创建用户的用户 ID 或其他相关信息
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    //这个注解用于指定数据库表字段在插入和更新数据时都应该自动填充。在这里，updateUser 字段将在插入和更新数据时都自动填充，通常用于记录最后一次修改数据的用户 ID 或其他相关信息
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
