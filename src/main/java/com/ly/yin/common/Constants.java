package com.ly.yin.common;

//图片路径常量
public class Constants {
    /* 用户头像路径*/
    //1.项目根路径   D:\2025-06-code\YINYUE-20250616
    public static String ASSESTS_PATH = System.getProperty("user.dir");
    //2. 用户头像存放路径
    public static String AVATOR_PATH = "img/avatorImages/";
    //3. 项目的根路径 + 用户头像存放路径
    public static String AVATOR_IMAGES_PATH = ASSESTS_PATH + "/" + AVATOR_PATH;

    //4. file: + 项目的根路径 + 用户头像存放路径
    public static String AVATOR_IMAGES_FILE_PATH = "file:" + AVATOR_IMAGES_PATH;

    /* 歌手头像路径*/
    //1.歌手图片
    public static String SINGERPIC_PATH = "img/singerPic/";
    //2.歌手图片 存放路径
    public static String SINGERPIC_SAVE_PATH = ASSESTS_PATH + "/" + SINGERPIC_PATH;
    //3.歌手图片 映射路径
    public static String SINGERPIC_FILE_PATH = "file:" + SINGERPIC_SAVE_PATH ;

    /*歌曲图片路径*/
    public static String SONG_PIC_PATH = "img/songPic/";
    //2.歌手图片 存放路径
    public static String SONG_PIC_SAVE_PATH = ASSESTS_PATH + "/" + SONG_PIC_PATH;
    //3.歌手图片 映射路径
    public static String SONG_PIC_FILE_PATH = "file:" + SONG_PIC_SAVE_PATH ;

    /* 歌曲文件路径 */
    public static String SONG_PATH = "song/";
    //2.歌曲文件 存放路径
    public static String SONG_SAVE_PATH = ASSESTS_PATH + "/" + SONG_PATH;
    //3.歌曲文件 映射路径
    public static String SONG_FILE_PATH = "file:" + SONG_SAVE_PATH ;

    /* 歌单图片路径 */
    public static String SongList_PIC_PATH = "img/songListPic/";
    //2.歌手图片 存放路径
    public static String SongList_PIC_SAVE_PATH = ASSESTS_PATH + "/" + SongList_PIC_PATH;
    //3.歌手图片 映射路径
    public static String SongList_PIC_FILE_PATH = "file:" + SongList_PIC_SAVE_PATH ;

    /* 轮播图片路径 */
    //1.轮播图图片根目录  "img/swiper/"
    public static String SWIPER_PATH = "img/swiper/";
    //2.轮播图图片 存放路径
    public static String SWIPER_SAVE_PATH = ASSESTS_PATH + "/" + SWIPER_PATH;
    //3.歌手图片 映射路径
    public static String SWIPER_FILE_PATH = "file:" + SWIPER_SAVE_PATH ;
//    测试输出一下上面的路径是否正确
    public static void main(String[] args) {
        System.out.println(SONG_PIC_PATH);  //  img/singerPic/
        System.out.println(SONG_PIC_SAVE_PATH);//  D:\2025-06-code\YINYUE-20250616/img/singerPic/
        System.out.println(SONG_PIC_FILE_PATH);//  file:D:\2025-06-code\YINYUE-20250616/img/singerPic/
    }

}
