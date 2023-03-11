package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.DishDto;
import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.entity.Dish;
import com.blog.entity.DishFlavor;
import com.blog.exception.CustomException;
import com.blog.service.CategoryService;
import com.blog.service.DishFlavorService;
import com.blog.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info("接收到的数据为：{}", dishDto);
//        1. 新增菜品
        dishService.saveWithFlavor(dishDto);
        return Result.success("添加菜品成功");
    }



    /*
    * 菜品信息分页查询功能
    *
    * 多表联查
    *
    * 流程：
    *   1.  页面 (backend/page/food/list.html) 发送 ajax 请求，将分页查询参数(`page`、`pageSize`、`name`)，提交到服务端，获取分页数据
        2.  页面发送请求，请求服务端进行图片下载，用于页面图片展示
        *
        * 因为Dish里没有可以做分类的属性所以要借助DishDto类
        * 把 DishDto 看做是 Dish 类的基础上，增加了一个 categoryName 属性，到时候返回 DishDto
          具体实现思路就是，将查询出来的 dish 数据，赋给 dishDto，然后在根据 dish 数据中的 category_id，
    *     去菜品分类表中查询到 category_name，将其赋给 dishDto 没懂仔细看几遍
        *
        * 此处逻辑业务很麻烦，写个mapper接口用sql处理会简便很多，这都是后话，看以后的项目优化 todo
    * */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝 并忽略掉records（页面所有的记录）属性，把这个属性单独存在一个list里进行处理
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        //把刚才忽略掉的属性重新赋值
        List<Dish> records = pageInfo.getRecords();

        //此处的item指的是dish 1.功能是遍历records对象，并创建对象返回。  stream是java8新语法
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //为了让表不只有记录，重新拷贝一遍
            BeanUtils.copyProperties(item, dishDto);

            //多表查询
            Long categoryId = item.getCategoryId();  //dish里的分类id
            //根据分类管理里查询用的是dish的id
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            //把分类名赋值给dishDto返回给前端
            dishDto.setCategoryName(categoryName);
            return dishDto;
            //2.把对象转为集合返回给前端List<DishDto> list
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    //1. 菜品信息回显功能，需要我们先根据 id 来查询到对应的菜品信息才能回显
    @GetMapping("/{id}")
    public Result<DishDto> getByIdWithFlavor(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        log.info("查询到的数据为：{}", dishDto);
        return Result.success(dishDto);
    }

    //1. 表现层修改菜品
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        log.info("接收到的数据为：{}", dishDto);
        dishService.updateWithFlavor(dishDto);
        return Result.success("修改菜品成功");
    }

    //页面请求：`dish/list?categoryId=xxx`,
    // 添加菜品，与移动端共用
    @GetMapping("/list")
    public Result<List<DishDto>> get(Dish dish) {
        //条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据传进来的categoryId查询
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //只查询状态为1的菜品（在售菜品）
        queryWrapper.eq(Dish::getStatus, 1);
        //简单排下序，其实也没啥太大作用
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //获取查询到的结果作为返回值
        List<Dish> list = dishService.list(queryWrapper);
        log.info("查询到的菜品信息list:{}", list);
        //item就是list中的每一条数据，相当于遍历了
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            //创建一个dishDto对象
            DishDto dishDto = new DishDto();
            //将item的属性全都copy到dishDto里
            BeanUtils.copyProperties(item, dishDto);
            //由于dish表中没有categoryName属性，只存了categoryId
            Long categoryId = item.getCategoryId();
            //所以我们要根据categoryId查询对应的category
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //然后取出categoryName，赋值给dishDto
                dishDto.setCategoryName(category.getName());
            }
            //然后获取一下菜品id，根据菜品id去dishFlavor表中查询对应的口味，并赋值给dishDto
            Long itemId = item.getId();
            //条件构造器
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //条件就是菜品id
            lambdaQueryWrapper.eq(itemId != null, DishFlavor::getDishId, itemId);
            //根据菜品id，查询到菜品口味
            List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
            //赋给dishDto的对应属性
            dishDto.setFlavors(flavors);
            //并将dishDto作为结果返回
            return dishDto;
            //将所有返回结果收集起来，封装成List
        }).collect(Collectors.toList());
        return Result.success(dishDtoList);
    }

    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("status:{},ids:{}", status, ids);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ids != null, Dish::getId, ids);
        updateWrapper.set(Dish::getStatus, status);
        dishService.update(updateWrapper);
        return Result.success("批量操作成功");
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除的ids：{}", ids);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = dishService.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("删除列表中存在启售状态商品，无法删除");
        }
        dishService.removeByIds(ids);
        return Result.success("删除成功");
    }
}
