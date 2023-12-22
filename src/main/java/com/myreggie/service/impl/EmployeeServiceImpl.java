package com.myreggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myreggie.entity.Employee;
import com.myreggie.mapper.EmployeeMapper;
import com.myreggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service  // 继承自 mybatisplus 包中的 ServiceImpl,泛型一个填的映射文件，一个填的是实体类
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService
{
}
