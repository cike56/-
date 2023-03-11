package com.blog.exception;

public class CustomException extends RuntimeException{
    //3. 封装自定义异常类
    public CustomException(String msg){
        super(msg);
    }
}
