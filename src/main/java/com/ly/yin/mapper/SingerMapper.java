package com.ly.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.yin.domain.Singer;
import com.ly.yin.domain.Song;

import java.util.List;

public interface SingerMapper extends BaseMapper<Singer> {
    List<Song> listSong(Integer singerId);
}
