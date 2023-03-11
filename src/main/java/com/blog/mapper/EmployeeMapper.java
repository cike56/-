package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/*
* 2. 员工mapper
* */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}