package com.ly.yin.controller;

import com.ly.yin.common.R;
import com.ly.yin.domain.Banner;
import com.ly.yin.service.IBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banner")
public class BannerController {
    //注入service服务
    @Autowired
    private IBannerService bannerService;

    /* 全查询 */
    @GetMapping("/list")
//    之前的版本，返回的类型定死了为列表，不够灵活
//    public List<Admin> list(){
//        return adminService.list();
//    }
    //这里专门在common里面定义了R类，作为统一的数据返回格式
    public R list(){
        List<Banner> list=bannerService.list();
        return R.success("轮播图查询成功",list);
    }


}
