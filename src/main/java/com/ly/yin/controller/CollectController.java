package com.ly.yin.controller;

import com.ly.yin.common.R;
import com.ly.yin.domain.Collect;
import com.ly.yin.domain.Song;
import com.ly.yin.service.ICollectService;
import com.ly.yin.vo.CollectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collect")
public class CollectController {
    @Autowired
    private ICollectService collectService;

//    @GetMapping("/list")
//    public R list() {
//        List<Collect> list = collectService.list();
//        return R.success("查询成功", list);
//    }

    /*1. 收藏歌曲的搜索功能  根据用户id和歌曲名字搜索*/
    @PostMapping("/list")
    public R list(@RequestBody Map<String,Object> param) {
        // List<Collect> list =collectService.list();
        //用户id
        Integer userId = Integer.parseInt(param.get("userId").toString());
        //歌曲名字
        String songName = param.get("songName") == null ? "" : param.get("songName").toString();
        List<CollectVO> list = collectService.findByUserIdAndSongName(userId, songName);
        return R.success("查询成功", list);
    }

    /*2.获取指定用户id对应收藏的歌曲  （用户端个人页面查看）*/
    @GetMapping("/detail/{userId}")
    public R detail(@PathVariable Integer userId){
        List<Song> list = collectService.detail(userId);
        return R.success("查询成功",list);
    }

    /*3. 根据用户id删除收藏歌曲 */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        collectService.removeById(id);
        return R.success("删除成功");
    }

    /*4. 批量删除 */
    @DeleteMapping("/deleteAll")
    public R deleteAll(@RequestParam("ids") List<Integer> ids) {
        collectService.removeBatchByIds(ids);
        return R.success("批量删除成功");
    }



    /*后面的是用户端关于collect功能的实现*/

    /*5. 收藏歌曲/歌单 */
    @PostMapping("/add")
    public R add(@RequestBody Collect collect){
        collect.setCreateTime(new Date()); //创建时间需要默认值
        collectService.save(collect);
        //true表示已收藏，传递给前端进行判断
        return R.success("收藏成功喵~",true);
    }

    /*6. 删除指定用户的指定歌曲 */
    @DeleteMapping("/delete/{userId}/{songId}")
    public R deleteSongOrSongList(@PathVariable Integer userId,@PathVariable Integer songId){
        try {
            collectService.deleteSong(userId,songId);
            //false表示未收藏，传递给前端进行判断
            return R.success("取消收藏了喵~",false);
        }catch (Exception e){
            e.printStackTrace();;
            return R.error("取消收藏失败了喵，可能的错误：" + e.getMessage());
        }
    }

    /*7. 获取收藏状态（已收藏/未收藏） */
    @DeleteMapping("/status")
    public R status(@RequestBody Collect collect){
        boolean flag = collectService.status(collect);
        return R.success("获取收藏状态喵~", flag);
    }



}
