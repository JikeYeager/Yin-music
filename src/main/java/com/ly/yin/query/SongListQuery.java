package com.ly.yin.query;

import lombok.Data;

@Data
//歌单分页查询对象
public class SongListQuery extends BaseQuery{
    //在继承基础分页查询上,新增歌单类型
    private String style;

}
