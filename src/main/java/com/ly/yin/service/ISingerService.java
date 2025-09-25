package com.ly.yin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.domain.Singer;
import com.ly.yin.domain.Song;
import com.ly.yin.query.BaseQuery;
import com.ly.yin.query.SingerQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISingerService extends IService<Singer> {
    IPage<Singer> pageQuery(BaseQuery query);
    void updateAvatar(Integer id, MultipartFile file);
    List<Song> listSong(Integer SingerId);

    IPage<Singer> sexPage(SingerQuery query);

}
