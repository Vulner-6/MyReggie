package com.myreggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myreggie.entity.Employee;

public interface EmployeeService extends IService<Employee>
//意味着 EmployeeService 接口将继承 IService 接口中的方法，并用于操作 Employee 类型的数据，比如可以使用getOne()方法
{
}
