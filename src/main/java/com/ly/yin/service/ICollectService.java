package com.ly.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.domain.Collect;
import com.ly.yin.domain.Song;
import com.ly.yin.vo.CollectVO;

import java.util.List;

public interface ICollectService extends IService<Collect> {
    List<CollectVO> findByUserIdAndSongName(Integer userId,String songName);

    /* 用户端删除收藏的歌曲 */
    void deleteSong(Integer userId, Integer songId);

    boolean status(Collect collect);

    List<Song> detail(Integer userId);
}
