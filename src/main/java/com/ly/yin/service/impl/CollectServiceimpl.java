package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.domain.Collect;
import com.ly.yin.domain.Song;
import com.ly.yin.mapper.CollectMapper;
import com.ly.yin.service.ICollectService;
import com.ly.yin.vo.CollectVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceimpl extends ServiceImpl<CollectMapper, Collect> implements ICollectService {

    /*1. */
    @Override
    public List<CollectVO> findByUserIdAndSongName(Integer userId, String songName) {
        return baseMapper.findByUserIdAndSongName(userId,songName);
    }

    /* 用户端删除收藏的歌曲 */
    @Override
    public void deleteSong(Integer userId, Integer songId) {
        /*执行SQL语句：delete from collect where user_id = ? and song_id = ?*/
        LambdaQueryWrapper<Collect> wrapper = new LambdaQueryWrapper<>();
        //where user_id = ?
        wrapper.eq(Collect::getUserId,userId);
        //and song_id = ?
        wrapper.eq(Collect::getSongId,songId);
        //delete from collect...
        remove(wrapper);  //mybatis-plus的内置方法，通过extends ServiceImpl<CollectMapper, Collect>就可以直接使用最基础的增删改查方法了

    }
    /*7. 获取收藏状态（已收藏/未收藏） */
    @Override
    public boolean status(Collect collect) {
        //执行SQL语句：select * from collect where user_id = ? and song_id = ? (and type = ?);
        LambdaQueryWrapper<Collect> wrapper = new LambdaQueryWrapper<>();
        //where user_id = ?
        wrapper.eq(Collect::getUserId,collect.getUserId());
        //and song_id = ?
        wrapper.eq(Collect::getSongId,collect.getSongId());
        //and type = ?
        wrapper.eq(Collect::getType,collect.getType());
        //获取查询结果
        Collect dbcollect = getOne(wrapper);
//        List<Collect> dbcollect2 = baseMapper.selectList(wrapper);
//        List<Collect> dbcollect3 = list(wrapper);

        //判断查询结果
        if (dbcollect == null)
            return false;
        else
            return true;
    }

    /*8. 获取指定用户id对应收藏的歌曲  （用户端个人页面查看）*/
    @Override
    public List<Song> detail(Integer userId){

        return baseMapper.findByUserId(userId);
    }
}
