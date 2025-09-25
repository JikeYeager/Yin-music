package com.ly.yin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
//绑定collect表
@TableName("collect")
public class Collect {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer type;
    private Integer songId;
    private Integer songListId;
    private Date createTime;
}
