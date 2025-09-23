package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.common.R;
import com.ly.yin.domain.Comment;
import com.ly.yin.mapper.CommentMapper;
import com.ly.yin.service.ICommentService;
import com.ly.yin.vo.CommentVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl
        extends ServiceImpl<CommentMapper, Comment>
        implements ICommentService {
    /*  获得指定歌单id的评论列表*/
    @Override
    public List<CommentVO> songList(Integer songListId) {
        return baseMapper.songListComment(songListId);
    }

    @Override
    public R updateCommentUp(Integer id, Integer up) {
        return null;
    }

    /*  获得指定歌曲ID的评论列表*/
    @Override
    public List<CommentVO> getCommentOfSongId(Integer songId) {
        return baseMapper.songComment(songId);
    }
}
