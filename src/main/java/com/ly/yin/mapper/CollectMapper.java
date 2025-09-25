package com.ly.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.yin.domain.Collect;
import com.ly.yin.domain.Song;
import com.ly.yin.vo.CollectVO;

import java.util.List;

public interface CollectMapper extends BaseMapper<Collect> {
    //多表查询必须手动定义方法
    List<CollectVO> findByUserIdAndSongName(Integer userId, String songName);

    List<Song> findByUserId(Integer userId);
}
