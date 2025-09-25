package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.common.Constans;
import com.ly.yin.domain.SongList;
import com.ly.yin.mapper.SongListMapper;
import com.ly.yin.query.BaseQuery;
import com.ly.yin.query.SongListQuery;
import com.ly.yin.service.ISongListService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class SongListServiceimpl extends ServiceImpl<SongListMapper, SongList> implements ISongListService {

    @Override
    public IPage<SongList> pageQuery(BaseQuery query) {
        IPage<SongList> page = new Page<>(query.getCurrentPage(), query.getPageSize());
        LambdaQueryWrapper<SongList> wrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(SongList::getTitle, query.getKeyword())
                    .or()
                    .like(SongList::getStyle, query.getKeyword());
        }

        return page(page, wrapper);
    }

    /* 更新歌单图片 */
    @Override
    public void updateAvatar(Integer id, MultipartFile file) {
        // 1. 处理文件名
        String originalFilename = file.getOriginalFilename();
        int suffixIndex = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(suffixIndex);
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 2. 拼接路径
        String storeFilePath = Constans.SONG_LIST_SAVE_PATH + newFileName;
        String dbSavePath = Constans.SONG_LIST_PATH + newFileName;

        // 3. 写入文件到磁盘
        try (InputStream in = file.getInputStream();
             FileOutputStream out = new FileOutputStream(storeFilePath)) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new RuntimeException("歌单图片上传失败", e);
        }

        // 4. 更新数据库
        SongList songList = new SongList();
        songList.setId(id);
        songList.setPic(dbSavePath);
        updateById(songList);
    }

    /*客户端按照类型分页查询歌单*/
    @Override
    public IPage<SongList> stylePage(SongListQuery query) {
        //1、构建分页查询
        IPage<SongList> page = new Page<>();
        //当前页码
        page.setCurrent(query.getCurrentPage());
        //每页记录数
        page.setSize(query.getPageSize());
        //2、构建查询参数
        LambdaQueryWrapper<SongList> wrapper = new LambdaQueryWrapper<>();
        //2.1、判断 keyword 是否为空
        String style = query.getStyle();
        boolean flag = style != null && !style.equals("all");
        //2.2、如果 style 不为空 并且不为 all 就加上 where style = ？
        wrapper.like(flag,SongList::getStyle,style);
        //3、执行分页查询
        IPage<SongList> result = page(page, wrapper);
        return result;
    }

    @Override
    public List<SongList> songListSearch(String keyword) {
        return baseMapper.songListSearch(keyword);
    }
}