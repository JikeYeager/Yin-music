package com.ly.yin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.yin.domain.Song;
import com.ly.yin.query.SongQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISongService
        extends IService<Song> {
//    歌曲分页查询
    IPage<Song> pageQuery(SongQuery query);

    void add(Song song, MultipartFile file, MultipartFile lrcfile);

    void updateSongImg(Integer id, MultipartFile file);

    void updateSongUrl(Integer id, MultipartFile file);

    void updateSongLrc(Integer id, MultipartFile file);

    List<Song> listSong(Integer songListId);

    List<Song> searchSong(String keyword);

    List<Song> getSongOfSingerId(Integer singerId);
}
