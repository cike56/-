package com.blog.common;

/*
*
* 以下三个方法同在一个线程
*   1.  `LocalCheekFilter`中的`doFilter`方法
    2.  `EmployeeController`中的`update`方法
    3.  `MyMetaObjectHandler`中的`updateFill`方法
*
*实现非admin用户无法对用户执行状态管理（启用或禁止）
*
* */

//3. 作用：基于 ThreadLocal 的封装工具类，用于保护和获取当前用户 id
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        //set设置当前线程的线程局部变量的值
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        //get返回当前线程所对应的线程局部变量的值
        return threadLocal.get();
    }
}
