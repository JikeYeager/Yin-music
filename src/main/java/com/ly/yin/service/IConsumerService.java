package com.ly.yin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.domain.Consumer;
import com.ly.yin.query.BaseQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

public interface IConsumerService
        extends IService<Consumer>
{
    //所有在需要在service层中实现的方法都要在interface中定义好，才能到Impl中实现
    IPage<Consumer> pageQuery(BaseQuery query);

    String updateAvatar(Integer id, MultipartFile file);

    Consumer login(Consumer consumer);

    Consumer loginByemail(Consumer consumer);

    void sendVerificationCode(String email);

    void resetPassword(HashMap<String, String> param);

    void updatePassword(HashMap<String, String> param);
}
