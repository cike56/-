package com.blog.dto;

import com.blog.entity.Setmeal;
import com.blog.entity.SetmealDish;
import lombok.Data;
import java.util.List;

/*
* 2. 普通的 SetmealDish 类肯定是不够我们用的，这里还需要加上套餐内的具体菜品和套餐分类名称
* */

@Data
public class SetmealDto extends Setmeal {

    //添加的菜品
    private List<SetmealDish> setmealDishes;
    //分类名称
    private String categoryName;
}
