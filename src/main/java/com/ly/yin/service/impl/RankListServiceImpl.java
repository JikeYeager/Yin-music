package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.domain.RankList;
import com.ly.yin.mapper.RankListMapper;
import com.ly.yin.service.IRankListService;
import org.springframework.stereotype.Service;

@Service
public class RankListServiceImpl
        extends ServiceImpl<RankListMapper, RankList>
        implements IRankListService {

    /*由用户id和歌单id唯一获取评分*/
    @Override
    public RankList findOne(Integer consumerId, Integer songListId) {
        //0. 执行select * from rank_list where song_list_id= ? and consumer_id =?;
        //1. 创建查询对象
        LambdaQueryWrapper<RankList> wrapper = new LambdaQueryWrapper<>();
        //2.and consumer_id =?
        wrapper.eq(RankList::getConsumerId,consumerId);
        //3. where song_list_id= ?
        wrapper.eq(RankList::getSongListId,songListId);
        //4. 获取数据库查询结果
        return getOne(wrapper);
    }

    /*获取歌单的总评价（所有用户对某歌单的总分取平均值）*/
    @Override
    public Double AllSongScore(Integer songListId) {
        //0. 执行select avg(score) from rank_list where song_list_id= ? ;  使用到了聚合函数
        return baseMapper.selectAvgScoreBySongListId(songListId);
    }
}
