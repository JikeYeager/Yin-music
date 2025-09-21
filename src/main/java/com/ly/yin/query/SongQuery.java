package com.ly.yin.query;

import lombok.Data;

@Data
//接受分页参数的对象
public class SongQuery extends BaseQuery {
    //单独创建一个歌曲查询对象的类就是因为歌曲查询要绑定对应的歌手，而在BaseQuery没有此参数
    private Integer singerId; //歌手id
}
