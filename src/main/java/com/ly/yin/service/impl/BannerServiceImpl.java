package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.domain.Banner;
import com.ly.yin.mapper.BannerMapper;
import com.ly.yin.service.IBannerService;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl
        extends ServiceImpl<BannerMapper, Banner>
        implements IBannerService {

}
