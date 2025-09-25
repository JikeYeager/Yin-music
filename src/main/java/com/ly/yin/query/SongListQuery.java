package com.ly.yin.query;

import lombok.Data;

@Data
//接受分页参数的对象
public class SongListQuery extends BaseQuery {
    private String style;
}

