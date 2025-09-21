package com.ly.yin.query;

import lombok.Data;

@Data
public class SingerQuery extends BaseQuery{
    //sex在数据库中使用整型存储的，为了节约资源吧
    // 0女 1男 2组合  那么这里就用3来代指all
    private Integer sex;

}
