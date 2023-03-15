package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.common.BaseContext;
import com.blog.common.Result;
import com.blog.entity.AddressBook;
import com.blog.exception.CustomException;
import com.blog.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

      //新增地址控制层，正常流程
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook={}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //把多条数据封装给list集合
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return Result.success(addressBooks);
    }

    //添加地址控制层
    @PostMapping
    public Result<AddressBook> addAddress(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }

    /*
    * 默认地址，按理说数据库中，有且仅有一条数据为默认地址，也就是`is_default`字段为 1
        -   如何保证整个表中的`is_default`字段只有一条为 1
        -   每次设置默认地址的时候，将当前用户所有地址的`is_default`字段设为 0，随后将当前地址的`is_default`字段设为 1
    * */
    @PutMapping("/default")
    public Result<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        //获取当前用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        //条件构造器
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        //条件：当前用户的地址
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        //将当前用户地址的is_default字段全部设为0
        queryWrapper.set(AddressBook::getIsDefault, 0);
        //执行更新操作
        addressBookService.update(queryWrapper);
        //随后再将当前地址的is_default字段设为1
        addressBook.setIsDefault(1);
        //再次执行更新操作
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    //1. 移动端结算界面数据回显
    @GetMapping("/default")
    public Result<AddressBook> defaultAddress() {
        //通过线程获取当前用户id
        Long userId = BaseContext.getCurrentId();
        log.info("userId={}", userId);
        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        //当前用户
        queryWrapper.eq(userId != null, AddressBook::getUserId, userId);
        //默认地址
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return Result.success(addressBook);
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            throw new CustomException("地址信息不存在");
        }
        return Result.success(addressBook);
    }

    @PutMapping
    public Result<String> updateAdd(@RequestBody AddressBook addressBook) {
        if (addressBook == null) {
            throw new CustomException("地址信息不存在，请刷新重试");
        }
        addressBookService.updateById(addressBook);
        return Result.success("地址修改成功");
    }

    @DeleteMapping()
    public Result<String> deleteAdd(@RequestParam("ids") Long id) {
        if (id == null) {
            throw new CustomException("地址信息不存在，请刷新重试");
        }
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            throw new CustomException("地址信息不存在，请刷新重试");
        }
        addressBookService.removeById(id);
        return Result.success("地址删除成功");
    }
}
