package com.blog;

import com.blog.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务处理

/*
*
*
-   菜品数据是我们登录移动端之后的展示页面
-   所以每当我们访问首页的时候，都会调用数据库查询一遍菜品数据
-   对于这种需要频繁访问的数据，我们可以将其缓存到 Redis 中以减轻服务器的压力
*
-   移动端对应的菜品查看功能，是 DishController 中的 list 方法，此方法会根据前端提交的查询条件进行数据库查询操作
* （用户选择不同的菜品分类）。在高并发的情况下，频繁查询数据库会导致系统性能下降，服务端响应时间增长。
* 所以现在我们需要对此方法进行缓存优化，提高系统性能
*
-   但是还有存在一个问题，我们是将所有的菜品缓存一份，还是按照菜品 / 套餐分类，来进行缓存数据呢？
-   答案是后者，当我们点击某一个分类时，只需展示当前分类下的菜品，而其他分类的菜品数据并不需要展示，
* 所以我们在缓存的时候，根据菜品的分类，缓存多分数据，页面在查询时，点击某个分类，则查询对应分类下的菜品的缓存数据
*
-   具体实现思路如下
    1.  修改 DishController 中的 list 方法，先从 Redis 中获取分类对应的菜品数据，如果有，则直接返回；
    * 如果无，则查询数据库，并将查询到的菜品数据存入 Redis
    2.  修改 DishController 的 save、update 和 delete 方法，加入清理缓存的逻辑，
    * 避免产生脏数据（我们实际已经在后台修改 / 更新 / 删除了某些菜品，但由于缓存数据未被清理，未重新查询数据库，
    * 用户看到的还是我们修改之前的数据）
*
* */

@EnableCaching//开启redis缓存
public class ReggieApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");
    }
}
