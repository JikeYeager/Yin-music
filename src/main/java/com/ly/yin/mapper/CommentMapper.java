package com.ly.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.yin.domain.Comment;
import com.ly.yin.vo.CommentVO;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    List<CommentVO> songListComment(Integer songListId);

    List<CommentVO> songComment(Integer songId);

}
