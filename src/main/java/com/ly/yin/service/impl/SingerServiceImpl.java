package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.common.Constants;
import com.ly.yin.domain.Singer;
import com.ly.yin.mapper.SingerMapper;
import com.ly.yin.query.BaseQuery;
import com.ly.yin.query.SingerQuery;
import com.ly.yin.service.ISingerService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class SingerServiceImpl
        extends ServiceImpl<SingerMapper, Singer>
    implements ISingerService{
    @Override
    public IPage<Singer> pageQuery(BaseQuery query) {
        //1. 构建分页查询   Mybatis支持分页有关操作，所以直接初始化一个Page对象
        IPage<Singer> page = new Page<>();
        //设置当前页码
        page.setCurrent(query.getCurrentPage());
        //设置每页记录数 不是setPage（）方法
        page.setSize(query.getPageSize());

        //2.构建查询参数
        LambdaQueryWrapper<Singer> wrapper = new LambdaQueryWrapper<>();
        //2.1 判断keyword(查询关键字)是否为空
        String keyword = query.getKeyword();
        boolean flag = keyword !=null && keyword.length() > 0;
        //2.2、如果 keyword 不为空
        // 加上select * from Singer where = name like '%keyword% (模糊查询)
        //like()
        wrapper.like(flag,Singer::getName,keyword);

        //3.执行分页查询
        IPage<Singer> result = page(page,wrapper);
        return result;
    }

    /*更新歌手图片*/
    @Override
    public void updateAvatar(Integer id, MultipartFile file) {
        //1.删除原来的图片
        //2.新增图片 (copy)   java 内存 --> img磁盘
        //2.1、获取原始的上传文件名称
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf("."); //通过lastIndexOf(".")找到扩展名的起始后缀索引

        //2.2 获取原始上传文件名后缀，如：“.jpg ”
        String h = originalFilename.substring(i); //substring(i)截取字符串子串,截取从i开始到字符串末的子串

        //2.3、生成新的文件名   "xxx.jpg"
/*  UUID.randomUUID().toString()可以在文件名前生成唯一标识符
     避免命名冲突
* */
        String uuid = UUID.randomUUID().toString() +h;

        //2.4 拼接copy 文件路径       "D:/2025-06-code/YINYUE-20250616/img/singerPic/xxx.jpg"
        String fileName = Constants.SINGERPIC_SAVE_PATH + uuid;

        //2.5 拼接数据库字段 pic    "img/singerPic/xxx.jpg"
        String pic = Constants.SINGERPIC_PATH + uuid;

        //2.6 copy   java 内存上传图片 --> img 磁盘
        try(InputStream in = file.getInputStream(); FileOutputStream out = new FileOutputStream(fileName);)
        {
//      允许用户以一行代码的形式，将输入流（InputStream）的内容复制到输出流（OutputStream）中，从而避免了传统的多行代码实现，大大简化了流的复制操作。
            IOUtils.copy(in,out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //3、更新数据库
        Singer singer = new Singer();
//        创建Consumer对象，并根据拼接数据库字段 pic和传入的行id，更新 ID 和头像路径
        singer.setId(id);
        singer.setPic(pic);
        updateById(singer); //MyBatis-Plus 提供的通用方法，根据 ID 更新记录
    }

    /* 歌手按性别分页查询 */
    @Override
    public IPage<Singer> sexPage(SingerQuery query) {
        //1. 构建分页查询对象
        IPage<Singer> page = new Page<>();
        //当前页码
        page.setCurrent(query.getCurrentPage());
        //每页记录数
        page.setSize(query.getPageSize());

        //2. 构建查询参数  select * from singer (where sex = ${sex})
        LambdaQueryWrapper<Singer> wrapper = new LambdaQueryWrapper<>();
        //2.1 判断 sex 是否为空
        Integer sex = query.getSex();
        boolean flag = sex !=null && !sex.equals(-1);
        //2.2、如果 sex 不为空 并且不为 -1 就加上where sex = ?
        //否则wrapper就是无参，直接进行全查询的分页
        if (flag) {
            wrapper.eq(Singer::getSex,sex);
        }
        //3.分页查询
        IPage<Singer> result = page(page,wrapper);
        return result;
    }
}
