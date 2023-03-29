package com.blog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringDataReadisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        redisTemplate.opsForValue().set("cike567","xuanwu");

        String cike567 = (String) redisTemplate.opsForValue().get("cike567");
        System.out.println(cike567);

        redisTemplate.opsForValue().set("age","18",20, TimeUnit.SECONDS);
        String age = (String) redisTemplate.opsForValue().get("age");
        System.out.println(age);

        //没有cike567则存入一条数据
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("cike567", "feiniao");
        System.out.println(aBoolean);
    }

    @Test
    public void testHash(){
        HashOperations<String ,String ,String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put("4204000400","name","Hades");
        hashOperations.put("4204000400","age","18");
        hashOperations.put("4204000400","hobby","LOL");

        //获取map集合
        Map<String ,String> map = hashOperations.entries("4204000400");
        Set<String > set = map.keySet();
        for (String key:
             set) {
            System.out.println(key+":"+map.get("4204000400"));
        }
        System.out.println("================================");

        //获取keys
        Set<String> keys = hashOperations.keys("4204000400");
        for (String key :
                keys) {
            System.out.println(key);
        }

        System.out.println("==================================");

        //获取values
        List<String > values = hashOperations.values("4204000400");
        for (String value :
                values) {
            System.out.println(value);
        }

    }

}
