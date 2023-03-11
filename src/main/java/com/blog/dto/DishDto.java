package com.blog.dto;


import com.blog.entity.Dish;
import com.blog.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/*
* -   因为 Dish 实体类不满足接收 flavor 参数，即需要导入 DishDto，用于封装页面提交的数据
  -   DTO，全称为`Data Transfer Object`，即数据传输对象，一般用于展示层与服务层之间的数据传输。
* */

@Data
public class DishDto extends Dish {
    //菜品口味
    private List<DishFlavor> flavors = new ArrayList<>();
    //菜品分类名称
    private String categoryName;

    private Integer copies;
}
