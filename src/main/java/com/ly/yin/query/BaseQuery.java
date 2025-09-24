package com.ly.yin.query;

import lombok.Data;

/*专门接收分页查询的参数，有人会问为什么不写在domain中，
domain中的对象都是对应表的字段，而我们这里是要创建用来接收分页查询的参数，不是给表结构创建接收变量*/
@Data
//接受分页参数的对象 ,前两个参数是真分页的参数，最后一个参数是真搜索的参数
public class BaseQuery {
    //当前页码
    /*为什么使用Long类型？
    因为在service中实现分页查询时，调用的page.setCurrent、page.setSize方法的参数类型都是Long类型
    */
    private Long currentPage = 1L;
    //每页记录数（每页显示多少条记录）
    private Long pageSize = 10L;
    //查询关键字
    private String keyword;

}
