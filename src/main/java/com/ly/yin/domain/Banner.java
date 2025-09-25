package com.ly.yin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//轮播图类
@Data  //帮我们生成get，set方法
@TableName("banner")  //表示是哪个表，这里是tp_music.banner表
public class Banner {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String pic; //轮播图片路径
}
