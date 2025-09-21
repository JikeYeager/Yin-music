package com.ly.yin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data  //帮我们生成get，set方法
@TableName("admin")  //表示是哪个表，这里是admin表
//后台管理员
public class Admin {
    @TableId(type = IdType.AUTO)
    //定义表中对应的字段
    private  Integer id;
    private  String name;
    private  String password;
}

//一般先写实体类，再写mapper，最后写controller