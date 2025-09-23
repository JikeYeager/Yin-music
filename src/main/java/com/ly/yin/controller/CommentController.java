package com.ly.yin.controller;

import com.ly.yin.common.R;
import com.ly.yin.domain.Comment;
import com.ly.yin.service.ICommentService;
import com.ly.yin.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {
    //注入service
    @Autowired
    private ICommentService commentService;

    /*1.获得指定歌曲ID的评论列表 传入参数没补全
    * getCommentOfSongId也未实现
    * */
    @GetMapping("/song/{songId}")
    public R getCommentOfSongId(@PathVariable Integer songId){
        //这里不使用分页查询，所以不用返回IPage<Comment>
        List<CommentVO> list = commentService.getCommentOfSongId(songId);
        return R.success("歌曲评论查询成功",list);
    }

//    /*2.获得指定歌单ID的评论列表 未完成*/
//    @GetMapping("/songList/detail")
//    public R getCommentOfSongListId(@PathVariable Integer id){
//        return null;
//    }


    /*3. 用户端获取歌单评论 */
    @GetMapping("/songList/{songListId}")
    public R songList(@PathVariable Integer songListId){
        List<CommentVO> list = commentService.songList(songListId);
        return R.success("歌单评论查询成功",list);
    }

    /*4. 用户端新增评论 */
    @PostMapping("/add")
    public R addComment(@RequestBody Comment comment){
        //为评论补充一下发表时间
        comment.setCreateTime(new Date());
        commentService.save(comment);
        return R.success("用户评论发表成功");
    }

    /*5. 用户端删除评论 */
    @DeleteMapping("/delete/{id}")
    public R delComment(@PathVariable Integer id){
        commentService.removeById(id);
        return R.success("用户删除评论成功");
    }

    /*6. 用户点赞 */
    @PostMapping("/like")
    public R setSupport(@RequestBody Map<String, Object> params){
        Integer id = (Integer) params.get("id");
        Integer up = (Integer) params.get("up");

        // 参数校验
        if (id == null || up == null) {
            return R.error("参数错误: id和up不能为空");
        }
        if (up < 0) {
            return R.error("参数错误: 点赞数不能为负数");
        }

        return commentService.updateCommentUp(id, up);
    }


}
