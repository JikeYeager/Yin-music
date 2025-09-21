package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.common.Constants;
import com.ly.yin.domain.Song;
import com.ly.yin.mapper.SongMapper;
import com.ly.yin.query.SongQuery;
import com.ly.yin.service.ISongService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class SongServiceImpl
        extends ServiceImpl<SongMapper, Song>
        implements ISongService {
    /* 歌曲分页查询 */
    @Override
    public IPage<Song> pageQuery(SongQuery query) {
        //1、构建分页查询
        IPage<Song> page = new Page<>();
        //当前页码
        page.setCurrent(query.getCurrentPage());
        //每页记录数
        page.setSize(query.getPageSize());
        //2、构建查询参数
        LambdaQueryWrapper<Song> wrapper = new LambdaQueryWrapper<>();
        //2.1、判断 keyword 是否为空
        String keyword = query.getKeyword();
        boolean flag = keyword != null && keyword.length() > 0;
        //2.2、如果 keyword 不为空 加上 name like'%keyword%'
        wrapper.like(flag, Song::getName, keyword);
        //2.3、加上singerId条件  singer_id =query.getSingerId()
        //我们要根据歌手id去找歌曲，singer_id是song表的外键
        wrapper.eq(Song::getSingerId, query.getSingerId());
        //3、执行分页查询
        IPage<Song> result = page(page, wrapper);
        return result;
    }

    /*添加歌曲
     * * @param song        歌曲对象
     * @param file          歌曲文件
     * @param lrcfile       歌词文件
     */
    @Override
    public void add(Song song, @RequestParam("file") MultipartFile file, @RequestParam("lrcfile") MultipartFile lrcfile) {
        // 1. 补全Song类没有传入参数的属性
        song.setCreateTime(new Date()); //创建时间默认为当前时间
        song.setUpdateTime(new Date()); //修改时间默认为当前时间
        // 歌曲路径 ，举例：'/song/'       +    '张杰-仰望星空'       有格式问题
//        String url = Constants.SONG_PATH + song.getName();


        //2. 歌词处理(歌词文件不存储，存在数据库字段)
//        使用try...catch...也是为了确保输入流和输出流在使用完毕后自动关闭，避免资源泄漏。
        if (lrcfile != null) {
            //若用户上传了 lrc 歌词文件，读取文件中内容转出字符串，覆盖歌词字段
            //用I/O流读出来再转成字符串，存入数据库中
            try (InputStream lrcIn = lrcfile.getInputStream()) {
                String lrc = IOUtils.toString(lrcIn, "UTF-8"); //按UTF-8编码将歌词文件转成字符串
                System.out.println(lrc); //在控制台打印歌词
                song.setLyric(lrc); //数据库更新歌词
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //3、歌曲文件路径
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        //3.2、获取原始文件名后缀   如：.mp3
        String h = originalFilename.substring(i);
        //3.3、生成新的文件名    如：孤勇者.mp3
        // 这里根据添加歌曲表单中输入的歌曲名字,不允许重新生成新的文件名
        String songName = song.getName() + h;
        //3.4、拼接copy 文件路径  D:\2025-06-code\YINYUE-20250616/song/孤勇者.mp3
        String fileName = Constants.SONG_SAVE_PATH + songName;
        // 3.5  7/11 上面更新数据库URL有格式问题 应当为“song/陈奕迅-孤勇者.mp3”
        String url = Constants.SONG_PATH + songName;
        song.setUrl(url); //在数据库更新歌曲路径
        //3.6、copy  java 内存上传图片 --> img 磁盘
        try (InputStream in = file.getInputStream(); FileOutputStream out = new FileOutputStream(fileName);) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //4、保存数据库
        save(song);

    }

    /* 更新歌曲图片 */
    @Override
    public void updateSongImg(Integer id, MultipartFile file) {
        // 1. 获取歌曲信息
        Song songid = getById(id);
        if (songid == null) throw new RuntimeException("歌曲不存在");
        //2. 处理图片文件
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        //2.2 获取原始上传文件名后缀，如：“.jpg ”
        String h = originalFilename.substring(i); //substring(i)截取字符串子串,截取从i开始到字符串末的子串
        //2.3、生成新的文件名   "xxx.jpg"
/*  UUID.randomUUID().toString()可以在文件名前生成唯一标识符
     避免命名冲突* */
        String uuid = UUID.randomUUID().toString() + h;
        //2.4  拼接copy 文件路径  D:/2025-06-code/YINYUE-20250616/img/songPic/xxx.jpg
        String fileName = Constants.SONG_PIC_SAVE_PATH + uuid;
        //2.5 拼接数据库字段 pic  img/songPic/xxx.jpg
        String pic = Constants.SONG_PIC_PATH + uuid;

        //2.6 copy   java 内存上传图片 --> img 磁盘
        try (InputStream in = file.getInputStream();
             FileOutputStream out = new FileOutputStream(fileName);) {
//      允许用户以一行代码的形式，将输入流（InputStream）的内容复制到输出流（OutputStream）中，从而避免了传统的多行代码实现，大大简化了流的复制操作。
            org.apache.tomcat.util.http.fileupload.IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //3、更新数据库
        Song song = new Song();
//   创建Song对象，并根据拼接数据库字段 pic和传入的行id，更新 ID 和头像路径
        song.setId(id);//更新数据库id
        song.setPic(pic);//更新数据库歌曲图片路径
        updateById(song); //MyBatis-Plus 提供的通用方法，根据 ID 更新记录
    }

    /* 更新歌曲文件 */
    @Override
    public void updateSongUrl(Integer id, MultipartFile file) {
        //1. 创建Song对象实例，判断该id到底有没有内容存在
        Song song = getById(id);
        if (song == null) throw new RuntimeException("歌曲不存在");

        //2. 更新歌曲文件
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String h = originalFilename.substring(i);
        //这里同样不创建新的文件名字，改为获取对应id的歌曲名字
        String fileName = Constants.SONG_SAVE_PATH + song.getName() + h;
        //2.5 拼接数据库字段 url  song/xxx.mp3
        String url = Constants.SONG_PATH + song.getName();
        //copy   java 内存上传图片 --> img 磁盘
        try (InputStream in = file.getInputStream();
             FileOutputStream out = new FileOutputStream(fileName)) {
            IOUtils.copy(in, out);

            // 更新URL路径
            song.setUrl(url);  //更新歌曲文件的URL
            song.setUpdateTime(new Date()); //更新修改时间为当前时间
            updateById(song); //根据id更新到数据库

        } catch (IOException e) {
            throw new RuntimeException("歌曲文件更新失败", e);
        }
    }

    /* 更新歌词 */
    @Override
    public void updateSongLrc(Integer id, MultipartFile file) {
        // 1. 创建Song对象实例，判断该id到底有没有内容存在
        Song song = getById(id);
        if (song == null) throw new RuntimeException("歌曲不存在");
        //2. 歌词处理(歌词文件不存储，存在数据库字段)
//        使用try...catch...也是为了确保输入流和输出流在使用完毕后自动关闭，避免资源泄漏。
        try (InputStream lrcIn = file.getInputStream()) {
            String lrc = IOUtils.toString(lrcIn, "UTF-8"); //按UTF-8编码将歌词文件转成字符串
            song.setLyric(lrc); //更新歌词到数据库
            song.setUpdateTime(new Date()); //更新修改时间为当前时间
            updateById(song); //根据id对应修改保存到数据库
        } catch (Exception e) {
            throw new RuntimeException("歌词更新失败", e);
        }
    }

    /* 根据指定歌单id获取歌曲 调用mapper的listSong方法*/
    @Override
    public List<Song> listSong(Integer songListId) {
        return baseMapper.listSong(songListId);
    }

    /* 歌曲 / 歌手 / 专辑 搜索功能的实现*/
    @Override
    public List<Song> searchSong(String keyword) {
        //模糊查询3个字段：歌手名，歌曲名，专辑名
        return baseMapper.searchSong(keyword);
    }

    /* 获取指定歌手ID的歌曲 */
    @Override
    public List<Song> getSongOfSingerId(Integer singerId) {
        return baseMapper.getSongOfSingerId(singerId);
    }
}
