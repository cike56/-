package com.blog;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Employee;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.DishMapper;
import com.blog.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReggieApplicationTests {

	@Autowired
	private MybatisPlusInterceptor mybatisPlusInterceptor;

	@Autowired
	private EmployeeMapper myEmployeeMapper;

	@Test
	void contextLoads() {
	}

//	2. ��ҳ��ѯ
	@Test
	void testSelectPage() {
		IPage<Employee> page = new Page<>(1, 3);
		myEmployeeMapper.selectPage(page, null);
		System.out.println("��ǰҳ��" + page.getCurrent());
		System.out.println("��ҳ����" + page.getSize());
		System.out.println("��ҳ��" + page.getPages());
		System.out.println("������" + page.getTotal());
		System.out.println(page.getRecords());
	}
}
