package com.ly.yin.common;

//从pom.xml文件导入我们需要的缓存依赖
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/* 缓存工具类 */
public class CacheUtils {
    private static final Cache<String, String> cache =  Caffeine.newBuilder()
            .initialCapacity(10)  //初始容量
            .maximumSize(100)     //最大容量
            .expireAfterWrite(5, TimeUnit.MINUTES)  //验证码写入后五分钟失效
            .build();             //
    //创建一个私有构造方法
    private CacheUtils(){
    }
/**
 * 存储数据
 * @param key
 * @param value
 * 在邮箱发送验证码中key指邮箱，value指验证码
 */
public static void setKey(String key,String value){
    //以字典的形式存入{key：value}
    cache.put(key, value);
}

/**
 * 从缓存中读取数据
 * 根据key获取数据
 * @return
 */
public static String getKey(String key)
{
    return cache.getIfPresent(key);
}

}
