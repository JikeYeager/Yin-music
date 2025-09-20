package com.ly.yin.common;

import lombok.Data;

//用于前后端交互使用的，后端可能返回字符串、列表等等数据类型，都是对于前端页面需要统一返回的结果.
@Data   //生成get和set方法
//Controller 响应结果类
public class R {
    //按照http来的，code=200成功，非200则失败
    private Integer code;
    //无论成功还是失败，都要有返回信息
    private String message;

    //返回数据
    private  Object data;

    //表示是否成功的状态量，true表成功，false表示失败
    private Boolean success;

    //因为我们一般是调用此类中的方法，一般不进行实例化，所以只用写一些静态方法

    //成功接入前端（无返回数据）
    public static R success(String message){
        R r=new R();
        r.success=true;
        r.message=message;
        r.code=200;  //成功的情况code=200
        return r;
    }

    //成功接入前端（有返回数据）
    public static R success(String message,Object data){
        R r=success(message);   //方法重载，调用无 data参数的success方法
        r.data=data;            //所以在上面的基础上补充data就可以了
        return r;
    }

    //一般连接错误也不会有什么返回数据，只有返回信息
    public static R error(String message){
        R r=new R();
        r.setCode(500); //定失败时code=500
        r.success=false;  //失败时状态量=false
        r.message=message;
        return r;
    }

    public static R error(String message,Integer code){
        R r=new R();
        r.code=code; //或者失败时手动传入code的数值
        r.success=false;  //失败时状态量=false
        r.message=message;
        return r;
    }
}
