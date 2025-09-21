package com.ly.yin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("singer") //singer表
public class Singer {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer sex;
    private String pic;
    private Date birth;
    private String location;
    private String introduction;


}
