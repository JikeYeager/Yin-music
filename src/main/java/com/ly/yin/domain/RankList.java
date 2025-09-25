package com.ly.yin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("rank_list")    //评分表（作为用户表consumer和歌单表song_list的中间表）
public class RankList {
    @TableId(type = IdType.AUTO)
    private Integer id;        //主键
    private Integer songListId; //歌单id
    private Integer consumerId ; //用户id
    private Integer score;   //评分
}
