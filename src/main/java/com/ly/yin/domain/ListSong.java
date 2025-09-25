package com.ly.yin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("list_song") //歌单与歌曲的中间表
public class ListSong {
    @TableId(type = IdType.AUTO)
    private Integer id;  //主键id
    private Integer song_id; //歌曲id
    private Integer song_list_id; //歌单id
}
