package com.ly.yin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("song") //song表
public class Song {
    @TableId(type = IdType.AUTO)
    private  Integer id;
    private  Integer singerId;
    private  String name;
    private  String introduction;
    private  Date createTime;
    private  Date updateTime;
    private  String pic;
    private  String lyric;
    private  String url;
}
