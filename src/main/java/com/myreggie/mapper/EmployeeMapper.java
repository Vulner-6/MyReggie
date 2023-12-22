package com.myreggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myreggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>  //继承自 BaseMapper ，并将泛型设置成导入的实体类 Employee
{
}
