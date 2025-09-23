package com.ly.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.common.R;
import com.ly.yin.domain.Comment;
import com.ly.yin.vo.CommentVO;

import java.util.List;

public interface ICommentService
        extends IService<Comment>
{
    List<CommentVO> songList(Integer songListId);

    R updateCommentUp(Integer id, Integer up);

    List<CommentVO> getCommentOfSongId(Integer songId);
}
