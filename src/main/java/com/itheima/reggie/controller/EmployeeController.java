package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    /*登录controller
    * */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//      1.将页面获取到的密码password进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//      2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//      3.如果没有查询到则返回登陆失败结果
        if (emp == null){
            return R.error("登陆失败");
        }
//      4.密码对比，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }
//      5.查看员工状态，如果已为禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return R.error("该账户已禁用");
        }
//      6.登录成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("Employee",emp.getId());
        return R.success(emp);
    }
    /*退出登录
    * */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("Employee");
        return R.success("退出成功");
    }
    @RequestMapping
    public R<String> save(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        log.info("当前获取到的对象为：{}",employee);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        Long nowloginuserid = (Long)httpServletRequest.getSession().getAttribute("Employee");

        employee.setCreateUser(nowloginuserid);
        employee.setUpdateUser(nowloginuserid);

        employeeService.save(employee);
        return R.success("新增员工成功！");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page pageinfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageinfo,lambdaQueryWrapper);
        return R.success(pageinfo);
    }
    @PutMapping
    public R<String> update(HttpServletRequest servletRequest,@RequestBody Employee employee){
        Long empid = (Long) servletRequest.getSession().getAttribute("Employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empid);
        employeeService.updateById(employee);
        return R.success("更新用户信息成功");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应的员工信息！");
    }
}
