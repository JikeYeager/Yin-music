package com.ly.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.yin.common.Constans;
import com.ly.yin.domain.Singer;
import com.ly.yin.domain.Song;
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
import java.util.List;
import java.util.UUID;

@Service
public class SingerServiceimpl extends ServiceImpl<SingerMapper, Singer> implements ISingerService {

    @Override
    public IPage<Singer> pageQuery(BaseQuery query) {
        //构建分页查询
        IPage<Singer> page = new Page<>();
        //当前页码
        page.setCurrent(query.getCurrentPage());
        //每页记录数
        page.setSize(query.getPageSize());
        //构建查询参数
        LambdaQueryWrapper<Singer> wrapper = new LambdaQueryWrapper<>();
        //判断 keyword 是否为空
        String keyword = query.getKeyword();
        boolean flag = keyword != null && keyword.length() > 0;
        //如果 keyword 不为空 加上 name like ‘%keyword%’
        wrapper.like(flag, Singer::getName, keyword);
        //执行分页查询
        IPage<Singer> result = page(page, wrapper);
        return result;
    }

    @Override
    public void updateAvatar(Integer id, MultipartFile file) {
        //1、获取原始的文件名称
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        //2、获取原始文件名后缀
        String h = originalFilename.substring(i);
        //3、生成新的文件名
        String uuid = UUID.randomUUID().toString() + h;
        //4、拼接copy文件路径
        String fileName = Constans.SINGERPIC_SAVE_PATH + uuid;
        //5、拼接数据库字段pic
        String pic = Constans.SINGERPIC_PATH + uuid;
        System.out.println(fileName);

        //6、copy java内存上传图片 --> img磁盘
        try(InputStream in = file.getInputStream();
            FileOutputStream out = new FileOutputStream(fileName)) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            throw new RuntimeException("歌手图片上传失败", e);
        }

        //7、更新数据库
        Singer singer = new Singer();
        singer.setId(id);
        singer.setPic(pic);
        updateById(singer);
    }

    @Override
    public List<Song> listSong(Integer singerId) {
        List<Song> list = baseMapper.listSong(singerId);
        return list;
    }

    @Override
    public IPage<Singer> sexPage(SingerQuery query){
        //1、构建分页查询
        IPage<Singer> page = new Page<>();
        //当前页码
        page.setCurrent(query.getCurrentPage());
        //每页记录数
        page.setSize(query.getPageSize());

        //2. 构建查询参数
        LambdaQueryWrapper<Singer> wrapper = new LambdaQueryWrapper<>();
        //2.判断 sex 是否为空 若为空/-1 则进行全查询
        Integer sex = query.getSex();
        boolean flag = sex != null && !sex.equals(-1);
        //2.2如果 sex 不为空 并且不为 -1 就加上 where sex = ？
        wrapper.like(flag,Singer::getSex,sex);

        //3. 执行分页查询
        IPage<Singer> result = page(page, wrapper);
        return result;



    }

    
}
