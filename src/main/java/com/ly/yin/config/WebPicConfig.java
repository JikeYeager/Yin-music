package com.ly.yin.config;

//import org.apache.coyote.http11.Constants;

import com.ly.yin.common.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


    @Configuration
    public class WebPicConfig implements WebMvcConfigurer {

//        public static String ASSESTS_PATH = System.getProperty("user.dir");  //项目根路径
//        public static String AVATOR_IMAGES_FILE_PATH = "file:" + ASSESTS_PATH + "\\img\\avatorImages\\"; // 转义字符“\\”表示“\”


        //TODO 这个配置类的目的是什么  就是注册了一个类似于拦截器吧  看到对应的资源 会将其修改成相应的地址
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            //当我们遇到 “/img/avatorImages/**” 路径时，就会跳到 “Constants.AVATOR_IMAGES_FILE_PATH” 这个资源中
            //用户图片显示配置
            registry.addResourceHandler("/img/avatorImages/**")
                    .addResourceLocations(Constants.AVATOR_IMAGES_FILE_PATH);
            //歌手图片显示配置
            registry.addResourceHandler("/img/singerPic/**")
                    .addResourceLocations(Constants.SINGERPIC_FILE_PATH);
            //歌曲图片显示配置
            registry.addResourceHandler("/img/songPic/**")
                    .addResourceLocations(Constants.SONG_PIC_FILE_PATH);

            //歌曲文件播放配置
            registry.addResourceHandler("song/**")  //把“/”去掉后就可以运行了
                    .addResourceLocations(Constants.SONG_FILE_PATH);

            //歌单图片显示配置
            registry.addResourceHandler("/img/songListPic/**")
                    .addResourceLocations(Constants.SongList_PIC_FILE_PATH);

            //用户端轮播图片显示配置
            registry.addResourceHandler("/img/swiper/**")
                    .addResourceLocations(Constants.SWIPER_FILE_PATH);
                     //registry.addResourceHandler("/img/singerPic/**")
                     // .addResourceLocations(Constants.SINGER_PIC_PATH);

                     //registry.addResourceHandler("/img/songPic/**")
                     //.addResourceLocations(Constants.SONG_PIC_PATH);

                     // registry.addResourceHandler("/song/**")
                     //.addResourceLocations(Constants.SONG_PATH);

                     //  registry.addResourceHandler("/img/songListPic/**")
                     //.addResourceLocations(Constants.SONGLIST_PIC_PATH);

                     // registry.addResourceHandler("/img/swiper/**")
                     //.addResourceLocations(Constants.BANNER_PIC_PATH);
                     }
                     }

