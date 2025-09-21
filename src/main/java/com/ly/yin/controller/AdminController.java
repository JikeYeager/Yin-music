package com.ly.yin.controller;

import com.ly.yin.common.R;
import com.ly.yin.domain.Admin;
import com.ly.yin.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
/*用来区分不同身份（类）下的同名方法或变量，在同一类内唯一确定，也就是不同类可以重名，
同一类不允许重名。本来就是用来区分不同类的同名，就不能用来区别同一类的同名方法/变量了*/
@RequestMapping("/admin")
public class AdminController {
    //注入service服务
    @Autowired
    private IAdminService adminService;

    @GetMapping("/list")
//    之前的版本，返回的类型定死了为列表，不够灵活
//    public List<Admin> list(){
//        return adminService.list();
//    }
    //这里专门在common里面定义了R类，作为统一的数据返回格式
    public R list(){
        List<Admin> list=adminService.list();
        return R.success("查询成功",list);
    }

    @PostMapping("/login") //与前端路由router里的admin/login路径一致
    public R login(@RequestBody Admin admin) //前端提交的数据给后端时是JSON格式，所以用@RequestBody接收
    {
        //用admin接收，用来查看接收结果
        System.out.println(admin);

        //1.实现登录逻辑： 通过调用service里面登录验证代码
        try {
            Admin dbAdmin=adminService.login(admin);   //login()方法在AdminServiceImpl中实现
            return R.success("登录成功",dbAdmin.getName());  //成功登录显示“登录成功”且返回用户名
        }catch (Exception e){
            e.printStackTrace();
            return R.error(e.getMessage()); //在前端输出错误信息
        }


//

    }


}
