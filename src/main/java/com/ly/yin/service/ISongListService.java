package com.ly.yin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.domain.SongList;
import com.ly.yin.query.BaseQuery;
import com.ly.yin.query.SongListQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISongListService extends IService<SongList> {
    IPage<SongList> pageQuery(BaseQuery query);
    void updateAvatar(Integer id, MultipartFile file);

    IPage<SongList> stylePage(SongListQuery query);

    List<SongList> songListSearch(String keyword);
}