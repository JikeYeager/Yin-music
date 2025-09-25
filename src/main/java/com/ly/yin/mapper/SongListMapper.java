package com.ly.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.yin.domain.SongList;

import java.util.List;

public interface SongListMapper extends BaseMapper<SongList> {

    // 歌单搜索
    List<SongList> songListSearch(String keyword);

}