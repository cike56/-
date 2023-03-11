package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.entity.Employee;
import com.blog.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 登入功能
     *
     * @param request
     * @param employee
     * @return ----------------------------------------------------------------
     * @RequestBody 主要用于接收前端传递给后端的 json 字符串（请求体中的数据）
     * HttpServletRequest 作用：如果登录成功，将员工对应的 id 存到 session 一份，
     * 这样想获取一份登录用户的信息就可以随时获取出来
     *
     * <a href="http://localhost:8888/backend/page/login/login.html">登录</a>
     */
    //发送post请求
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1. 使用java内置工具类(DigestUtils)对密码进行加密(md5加密)
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 这部分就是MP(mybatis-plus)等值查询操作
        //注意两个对比的实体类
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        //3. 查询最新的一条数据,做判断,最后返回给前端
        Employee emp = employeeService.getOne(lqw);
        if (emp == null) {
            return Result.error("登陆失败");
        }
        if (!emp.getPassword().equals(password)) {
            return Result.error("登录失败");
        }
        if (emp.getStatus() == 0) {
            return Result.error("该用户已被禁用");
        }
        //4. 存个Session，只存个id就行了
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    /**
     * 登出功能
     *
     * @param request
     * @return 此功能删除session即可
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     * <p>
     * 只需要设置密码，创建时间和更新时间，创建人 ID 和修改人 ID
     *
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增的员工信息：{}", employee.toString());
        //设置默认密码为123456，并采用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // TODO 此处修改，但源码没添加上
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        //存入数据库
        employeeService.save(employee);

        return Result.success("添加员工成功");
    }

    //员工模糊查询并排序功能
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（当我们没有输入name时，就相当于查询所有了）
        wrapper.like(!(name == null || "".equals(name)), Employee::getName, name);
        //并对查询的结果进行降序排序，根据更新时间
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, wrapper);
        return Result.success(pageInfo);
    }

    /**
     * 通用的修改员工信息
     *
     * @param employee
     * @param request
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        log.info(employee.toString());
        //获取线程id
        long id1 = Thread.currentThread().getId();
        log.info("update的线程id为：{}", id1);
//        employeeService.updateById(employee);

        Long id = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(id);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return Result.success("员工信息修改成功");
    }


    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息..");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("未查询到该员工信息");
    }

}