package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Employee;
import com.blog.mapper.EmployeeMapper;
import com.blog.service.EmployeeService;
import org.springframework.stereotype.Service;

/*
* 4. 员工功能实现类
* */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}