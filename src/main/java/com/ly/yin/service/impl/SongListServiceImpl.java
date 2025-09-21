package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.common.Constants;
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
public class SongListServiceImpl
        extends ServiceImpl<SongListMapper, SongList>
    implements ISongListService
{
    /* 歌单分页查询 + 检索*/
    @Override
    public IPage<SongList> pageQuery(BaseQuery query) {
        //1. 构建分页查询   Mybatis支持分页有关操作，所以直接初始化一个Page对象
        IPage<SongList> page = new Page<>();
        //设置当前页码（一般为1，根据前端的切换页面改变传入的currentPage值）
        page.setCurrent(query.getCurrentPage());
        //设置每页记录数 不是setPage（）方法
        page.setSize(query.getPageSize());

        //2.构建查询参数
        LambdaQueryWrapper<SongList> wrapper = new LambdaQueryWrapper<>();
        //2.1 判断keyword(查询关键字)是否为空
        String keyword = query.getKeyword();
        boolean flag = keyword !=null && keyword.length() > 0;
        //2.2、如果 keyword 不为空
        // 加上select * from SongList where  Title like '%keyword% (模糊查询)
        //like()
        wrapper.like(flag,SongList::getTitle,keyword); //songList没有name属性，只有title属性

        //3.执行分页查询
        IPage<SongList> result = page(page,wrapper);
        return result;
    }

    /* 更新歌单图片 */
    @Override
    public void updateAvatar(Integer id, MultipartFile file) {
        // 1. 删除用来的图片
        //2. 新增图片 (copy)   java 内存 --> img磁盘
        //2.1 获取原始的文件名称
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf("."); //通过lastIndexOf(".")找到扩展名的起始后缀索引

        //2.2 获取原始文件名后缀，如：“.jpg ”
        String h = originalFilename.substring(i); //substring(i)截取字符串子串,截取从i开始到字符串末的子串

        //2.3、生成新的文件名   "xxx.jpg"
        String uuid = UUID.randomUUID().toString() +h;

        //2.4 拼接copy 文件路径       "D:/2025-06-code/YINYUE-20250616/img/songListPic/xxx.jpg"
        String fileName = Constants.SongList_PIC_SAVE_PATH + uuid;

        //2.5 拼接数据库字段 avator    "img/avatorImages/xxx.jpg"
        String avator = Constants.SongList_PIC_PATH + uuid;

        //2.6 copy   java 内存上传图片 --> img 磁盘
        try(InputStream in = file.getInputStream(); FileOutputStream out = new FileOutputStream(fileName);)
        {
//      允许用户以一行代码的形式，将输入流（InputStream）的内容复制到输出流（OutputStream）中，从而避免了传统的多行代码实现，大大简化了流的复制操作。
            IOUtils.copy(in,out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //3、更新数据库
        SongList songList = new SongList();
//        创建Consumer对象，并根据拼接数据库字段 avator和传入的行id，更新 ID 和头像路径
        songList.setId(id);
        songList.setPic(avator); //songList表中没有avator字段，但是一样有pic字段
        updateById(songList); //MyBatis-Plus 提供的通用方法，根据 ID 更新记录
    }

    /* 歌单搜索 （因为之前我们在管理员端写的方法是把分页查询和搜索写到一起了，但是我们这里只需要搜索功能，所以要单独写一个方法）*/
    @Override
    public List<SongList> SearchSongList(String keyword) {
        LambdaQueryWrapper<SongList> wrapper = new LambdaQueryWrapper<>();
        //等价于select * from song_list where title like '%keyword%'
        wrapper.like(SongList::getTitle,keyword);
        System.out.println(list(wrapper));
        return list(wrapper);
    }

    /* 歌单分类别分页查询 */
    @Override
    public IPage<SongList> stylePage(SongListQuery query) {
        //1. 构建分页查询对象
        IPage<SongList> page = new Page<>();
        //当前页码
        page.setCurrent(query.getCurrentPage());
        //每页记录数
        page.setSize(query.getPageSize());

        //2.构建查询参数  select * from song_list (where style like '%{style}%')
        LambdaQueryWrapper<SongList> wrapper = new LambdaQueryWrapper<>();
        //2.1 判断 style 是否为空
        String style = query.getStyle();
        boolean flag = style !=null && !style.equals("all");
        //2.2、如果 style 不为空 并且不为 all 就加上where style = ?
        //否则wrapper就是无参，直接进行全查询的分页
        wrapper.like(flag,SongList::getStyle,style);

        //3.分页查询
        IPage<SongList> result = page(page,wrapper);
        return result;
    }

}
