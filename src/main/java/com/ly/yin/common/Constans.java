package com.ly.yin.common;

public class Constans {
    //项目的根路径
    public static String ASSETS_PATH = System.getProperty("user.dir");
    //用户头像存放路径
    public static String AVATOR_PATH = "img/avatorImages/";
    //项目的根路径 + 用户头像存放路径
    public static String AVATOR_IMAGES_PATH = ASSETS_PATH + "/" + AVATOR_PATH;
    // file: + 项目的根路径 + 用户头像存放路径
    public static String AVATOR_IMAGES_FILE_PATH = "file:" + AVATOR_IMAGES_PATH ;

    // 歌手图片相对路径
    public static String SINGERPIC_PATH = "img/singerPic/";
    // 完整存储路径
    public static String SINGERPIC_SAVE_PATH = ASSETS_PATH + "/" + SINGERPIC_PATH;
    // 文件协议路径
    public static String SINGERPIC_FILE_PATH = "file:" + SINGERPIC_SAVE_PATH;

    // 歌曲图片相对路径
    public static String SONG_PIC_PATH = "img/songPic/";
    // 存储路径
    public static String SONG_PIC_SAVE_PATH = ASSETS_PATH + "/" + SONG_PIC_PATH;
    // 映射路程
    public static String SONG_PIC_FILE_PATH = "file:" + SONG_PIC_SAVE_PATH;

    // 歌曲相对路径
    public static String SONG_PATH = "song/";
    // 存储路径
    public static String SONG_SAVE_PATH = ASSETS_PATH + "/" + SONG_PATH;
    // 映射路程
    public static String SONG_FILE_PATH = "file:" + SONG_SAVE_PATH;

    // 歌单相对路径
    public static String SONG_LIST_PATH = "img/songListPic/";
    // 存储路径
    public static String SONG_LIST_SAVE_PATH = ASSETS_PATH + "/" + SONG_LIST_PATH;
    // 映射路程
    public static String SONG_LIST_FILE_PATH = "file:" + SONG_LIST_SAVE_PATH;

    // 轮播图相对路径
    public static String SWIPER_PATH = "img/swiper/";
    // 存储路径
    public static String SWIPER_SAVE_PATH = ASSETS_PATH + "/" + SWIPER_PATH;
    // 映射路程
    public static String SWIPER_FILE_PATH = "file:" + SWIPER_SAVE_PATH;


    public static void main(String[] args){
        System.out.println(SONG_LIST_PATH);
        System.out.println(SONG_LIST_SAVE_PATH);
        System.out.println(SONG_LIST_FILE_PATH);
    }
}
