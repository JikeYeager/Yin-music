package com.ly.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.domain.RankList;

public interface IRankListService extends IService<RankList> {
    RankList findOne(Integer consumerId, Integer songListId);

    Double AllSongScore(Integer songListId);
}
