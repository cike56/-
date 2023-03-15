package com.blog.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
* 统一结果集封装
*
* 这是因为要缓存的 JAVA 对象必须实现`Serializable`接口，因为 Spring 会先将对象序列化再存入 Redis，将缓存实体类继承`Serializable`
* */
@Data
public class Result<T> implements Serializable {
    private Integer code;//1:true 0:false
    private String msg;//error message
    private T data;//数据
    private Map map = new HashMap();//动态数据

    //这里提供了几个静态方法来返回一个Result对象
    //之前是直接用构造器的set方法也可以实现同样的功能
    //不过此种方式的复用性更高
    public static <T> Result<T> success(T obj){
        Result<T> r = new Result<>();
        r.setCode(1);
        r.setData(obj);
        return r;
    }

    public static <T> Result<T> error(String msg){
        Result<T> r = new Result<>();
        r.setCode(0);
        r.setMsg(msg);
        return r;
    }

    public Result<T> add(String key,String value){
        this.map.put(key,value);
        return this;
    }
}