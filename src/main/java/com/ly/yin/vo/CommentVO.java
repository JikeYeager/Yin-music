package com.ly.yin.vo;

import lombok.Data;
import java.util.Date;

//view object

@Data
public class CommentVO {
    private Integer id;
    private Integer userId;
    private Integer songId;
    private Integer songListId;
    private String content;
    private Date createTime;
    private Integer type;
    private Integer up;

    private String username;
    private String avator;
}

