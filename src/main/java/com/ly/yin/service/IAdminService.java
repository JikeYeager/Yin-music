package com.ly.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.domain.Admin;

public interface IAdminService
    extends IService<Admin>
{
    Admin login(Admin admin);
}
