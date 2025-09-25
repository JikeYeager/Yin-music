package com.ly.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.yin.domain.RankList;

public interface RankListMapper extends BaseMapper<RankList> {
//    Double selectAvgScoreBySongListId(Integer songListId);
    Double selectAvgScoreBySongListId(Integer songListId);
}
