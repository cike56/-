package com.blog.common;

import com.blog.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 * 为了解决 username 不能重复，因为在建表的时候设定了 unique，
 * 只能存在唯一的 username，如果存入相同的 username 则会报错
 *
 * 利用报错信息： `Duplicate entry 'Kyle' for key 'employee.idx_username'` 对结果进行修改
 * 并拼接返回信息
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        //如果包含Duplicate entry，则说明有条目重复
        if (exception.getMessage().contains("Duplicate entry")) {
            //对字符串切片
            String[] split = exception.getMessage().split(" ");
            //字符串格式是固定的，所以这个位置必然是username
            String username = split[2];
            //拼串作为错误信息返回
            return Result.error(username + "已存在");
        }
        //如果是别的错误那我也没招儿了
        return Result.error("未知错误");
    }

    //4. 交给全局异常处理器处理`CustomerException`异常
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException exception) {
        log.error(exception.getMessage());
        return Result.error(exception.getMessage());
    }
}
