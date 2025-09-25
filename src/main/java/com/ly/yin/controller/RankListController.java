package com.ly.yin.controller;

import com.ly.yin.common.R;
import com.ly.yin.domain.RankList;
import com.ly.yin.service.IRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rankList")
public class RankListController {
    @Autowired
    private IRankListService rankListService;

    /*1. 由用户id和歌单id唯一获取评分*/
    @GetMapping("/{consumerId}/{songListId}")
    public R findOne(@PathVariable Integer consumerId, @PathVariable Integer songListId){
        RankList rankList = rankListService.findOne(consumerId,songListId);
        return R.success("查询成功",rankList);
    }
    /*2. 获取整个歌单的评分（所有用户对此歌单的总分取平均值）*/
    @GetMapping("/avg")
    //注意不要使用@PathVariable，要用@RequestParam
    public R AllSongScore(@RequestParam Integer songListId){
        Double AllrankList = rankListService.AllSongScore(songListId);
        return R.success("查询成功",AllrankList);
    }
    /*3. 提交评分 */
    @PostMapping("/add")
    public R save(@RequestBody RankList rankList){
        rankListService.save(rankList);
        return R.success("评分成功");
    }


}
