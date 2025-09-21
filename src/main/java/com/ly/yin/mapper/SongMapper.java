package com.ly.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.yin.domain.Song;

import java.util.List;

public interface SongMapper extends BaseMapper<Song> {
    List<Song> listSong(Integer songListId);

    List<Song> searchSong(String keyword);

    List<Song> getSongOfSingerId(Integer singerId);
}
