package com.myreggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myreggie.common.R;
import com.myreggie.entity.Employee;
import com.myreggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController   // 等同于 @Controller + @ResponseBody
@RequestMapping("/employee")
public class EmployeeController
{
    @Autowired    // 自动寻找名称为 EmployeeService 的 bean ，并获取其实例对象赋值给变量
    private EmployeeService employeeService;

    /**
     * 员工登录方法
     * @return
     */
    @PostMapping("/login")     //@RequestBody将传入请求的 JSON 或 XML 格式的数据（通常是前端通过 POST 请求发送的数据）转换为相应的 Java 对象
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee)
    // Employee 对象需要存在前端传来的字段变量，如前端参数 username，Employee必须有 username 变量。
    {
        // 将页面提交的密码password进行md5加密处理
        String password=employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据页面提交的用户名查询数据库,使用了 MyBatis-Plus 框架中的 LambdaQueryWrapper，用于构建数据库查询条件
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<Employee>();
        // 构建查询条件，用前端传来的 username 值，去数据库中查询
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(queryWrapper);

        // 若没查到用户，则返回登录失败信息
        if(emp==null)
        {
            return R.error("登录失败");
        }

        // 若密码错误，则返回登录失败信息
        if(!emp.getPassword().equals(password))
        {
            return R.error("登录失败");
        }

        // 判断员工状态是否已禁用
        if(emp.getStatus()==0)
        {
            return R.error("用户已禁用");
        }

        // 登录成功，将用户 ID 存放到 Session 中，并返回登录成功的结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request)
    {
        // 清理 session 中保存的当前员工的 id，这里的 employee 对应先前的 request.getSession().setAttribute("employee",emp.getId());
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest httpServletRequest,@RequestBody Employee employee)
    {
        log.info("新增员工，员工信息：{}",employee);

        // 设置新增员工的初始密码，并用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 由于使用了公共字段自动填充方案，所以这里的代码就不需要了
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        // 由于使用了公共字段自动填充方案，所以这里的代码就不需要了
        // 获得当前用户的 id
        //Long empId =(Long)httpServletRequest.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        // 保存数据至数据库
        employeeService.save(employee);
        return R.success("新增员工成功！");
    }

    /**
     * 员工信息的分页查询
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name)
    // 这里的 Page 类是 MybatisPlus 相关扩展包中自带的，里面包含的变量刚好是前端需要的，变量名都是匹配的。
    {
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        // 构造分页构造器
        Page pageInfo=new Page(page,pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper();
        // 添加过滤条件，只有当 name 不为空时，条件才会生效,相当于节省了 if else 的写法
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询，这里的 page() 与 Page 类之间存在关联，执行完查询后，pageInfo 变量应该会发生变化
        employeeService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据 id 修改员工信息
     */
    @PutMapping      // 已经在 /employee路径下了
    public R<String> update(HttpServletRequest httpServletRequest,@RequestBody Employee employee)
    {
        // 日志记录前端传过来的请求，方便调试
        log.info(employee.toString());
        // 获取当前线程 id ，用于证明每一次的 http 请求，都是同一个新线程。
        long id =Thread.currentThread().getId();
        log.info("当前线程id:{}",id);

        // 由于使用了公共字段自动填充方案，所以这里的代码就不需要了
        // 获取 session 中的 employee 值，当初存进去的应该是 id
        //Long empId=(Long)httpServletRequest.getSession().getAttribute("employee");
        // 修改员工信息
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据 id 查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")      // 表示接收 url 中的 id 部分，将其作为变量赋值
    public R<Employee> getById(@PathVariable Long id)
    {
        log.info("根据 id 查询员工信息...");
        Employee employee=employeeService.getById(id);
        if(employee!=null)
        {
            return R.success(employee);
        }
        return R.error("没有查到对应的员工信息");
    }

}
