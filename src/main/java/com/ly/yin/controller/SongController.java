package com.ly.yin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.yin.common.R;
import com.ly.yin.domain.Song;
import com.ly.yin.query.SongQuery;
import com.ly.yin.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/song")
public class SongController {
    @Autowired
    private ISongService songService; //实例化/声明songService类才能用

    @GetMapping("/list")
    public R list(){
        List<Song> list = songService.list();
        return R.success("查询成功",list);
    }
    /*歌曲分页查询*/
    @PostMapping("/page")
//    注意这里是SongQuery，而不是BaseQuery
    public R pageQuery(@RequestBody SongQuery query) {
        IPage<Song> page = songService.pageQuery(query);
        return R.success("分页查询成功",page);
    }
    /* 删除歌曲 */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id){
        songService.removeById(id);//removeById是封装好的，直接调用，无返回结果
        return R.success("删除成功");
    }
    /* 添加歌曲
    * */
    @PostMapping("/add")
//    public R addSong(@RequestBody Song song, MultipartFile file,MultipartFile lrcfile)
    //为什么不能打@RequestBody注解，因为会把所有参数封装到song里面，所以不可以打注解
    public R addSong(Song song, MultipartFile file,MultipartFile lrcfile)
    {
      //测试输出前端传入的数据
        System.out.println(song);  //song是歌曲名
        System.out.println(file.getOriginalFilename()); //歌曲文件
        System.out.println(lrcfile.getOriginalFilename()); //歌词文件
        //这里不同于singer的添加和修改，因为歌曲的上传还涉及文件的上传，所以需要自己再写一个实现方法
        songService.add(song,file,lrcfile);
        return R.success("歌手添加成功",null);
    }

    /*修改歌曲信息*/
    @PostMapping("/update")
    public R updateSong(@RequestBody Song song){
        System.out.println(song);
        songService.updateById(song);  //为什么这里一定使用的是id而不是songid呢？
        return R.success("歌曲信息修改成功");
    }
    /* 更新歌曲图片 */
    @PostMapping("/img/update")
    public R updateSongImg(@RequestParam("id") Integer id,@RequestParam("file") MultipartFile file){
        System.out.println(id);
        System.out.println(file);
        songService.updateSongImg(id,file);
        return R.success("歌曲图片更新成功");
    }

    /* 更新歌曲文件 */
    @PostMapping("/url/update")
    public R updateSongUrl(@RequestParam("id") Integer id,@RequestParam("file") MultipartFile file){
        System.out.println(id);
        System.out.println(file);
        songService.updateSongUrl(id,file);
        return R.success("歌曲文件更新成功");
    }

    /* 更新歌词 */
    @PostMapping("/lrc/update")
    public R updateSongLrc(@RequestParam("id") Integer id,
                           @RequestParam("file") MultipartFile file) {
        songService.updateSongLrc(id, file);
        return R.success("歌词更新成功");
    }

    /* 获取指定歌单id的歌曲 */
    @GetMapping("/listSong/{songListId}")
    public R listSong(@PathVariable Integer songListId){
            List<Song> list = songService.listSong(songListId);
        return R.success("指定歌单的歌曲查询成功",list);
    }

    /* 获取指定歌手ID的歌曲 */
    @GetMapping("/singer/{singerId}")
    public R getSongOfSingerId(@PathVariable Integer singerId){
        List<Song> list = songService.getSongOfSingerId(singerId);
        return R.success("对应歌手的歌曲查询成功喵~",list);
    }

}
