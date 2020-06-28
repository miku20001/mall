package com.miku.user;

import com.miku.MikuUserApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MikuUserApplication.class)
public class testRedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedis(){

        this.redisTemplate.opsForValue().set("key2","value2");
        String var = this.redisTemplate.opsForValue().get("key1");

        System.out.printf("var = %s\n", var);
    }

    @Test
    public void testRedis2(){
        this.redisTemplate.opsForValue().set("key2","value2",5, TimeUnit.HOURS);
    }

    @Test
    public void testHash(){
        BoundHashOperations<String,Object,Object> hashOps = this.redisTemplate.boundHashOps("user");

        hashOps.put("name","jack");
        hashOps.put("age","21");

        //获取单个数据
        Object name = hashOps.get("name");
        System.out.println("name = "+name);

        //获取所有数据
        Map<Object,Object> map = hashOps.entries();
        for(Map.Entry<Object,Object> me : map.entrySet()){
            System.out.println(me.getKey()+":"+me.getValue());
        }

    }
}
