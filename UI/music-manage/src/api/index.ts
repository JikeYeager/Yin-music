import {deletes, get, getBaseURL, post} from './request'  //从request导入方法

//HttpManager封装了一系列方法
const HttpManager = {
    // 获取图片信息
    attachImageUrl: (url) => `${getBaseURL()}/${url}`,  //方法名：(参数列表) => 方法体
    // =======================> 管理员 API 完成
    // 是否登录成功  这里只是向后端发送http的post请求，具体验证在后端实现。 `admin/login`就是登录页面的路径
    adminLogin:({name,password}) => post(`admin/login`,{
        name,
        password
    }),
    // =======================> 用户 API 完成
    // 返回所有用户
    getAllUser: () => get(`consumer/list`),  //原来是'/user',我们前后端路径要一致，所以改为`consumer/list`
    //  25/6/23 分页查询用户 post请求需要传入参数
    getPageUser:({currentPage,pageSize,keyword}) => post(`consumer/page`,{
        currentPage,
        pageSize,
        keyword
    }),
    // 返回指定ID的用户
    getUserOfId: (id) => get(`user/detail?id=${id}`),
    // 删除用户
    // 6/25 神人，有封装好了的delete方法不用，用get方法deleteUser: (id) => get(`consumer/delete?{id}`),
    deleteUser: (id) => deletes(`consumer/delete/${id}`,{}),  //少写了’/‘导致报错了。。。
    //批量删除
    deleteAllUser:(ids) => deletes(`consumer/deleteAll?ids=${ids}`,null),
    // =======================> 收藏列表 API 完成
    // 返回的指定用户ID收藏列表
    getCollectionOfUserAndSongName: ({userId,songName}) => post(`collect/list`,{userId, songName}),
    // 删除收藏的歌曲
    // 6/27 仿写deleteCollection: (userId, songId) => deletes(`collect/delete?userId=${userId}&&songId=${songId}`),
    deleteCollection: (id) => deletes(`collect/delete/${id}`,{}),
    //批量删除收藏歌曲
    deleteAllCollection:(ids) => deletes(`collect/deleteAll?ids=${ids}`,null),


    // =======================> 评论列表 API 完成
    // 获得指定歌曲ID的评论列表
    getCommentOfSongId: ({resourceId ,type,keyword}) => get(`comment/song/detail`,{resourceId ,type,keyword}),
    // 获得指定歌单ID的评论列表
    getCommentOfSongListId: ({resourceId ,type,keyword}) => get(`comment/songList/detail`,{resourceId ,type,keyword}),
    // 删除评论
    deleteComment: (id) => get(`comment/delete?id=${id}`),

    // =======================> 歌手 API 完成
    // 返回所有歌手
    getAllSinger: () => get(`/singer/list`), // 6/22 修改路径

    //分页查询歌手
    getPageSinger:({currentPage,pageSize,keyword}) => post(`singer/page`,{
        currentPage,
        pageSize,
        keyword
    }),
    // 添加歌手或修改歌手  在后端根据id传入实现了区分添加和修改功能
    saveSinger: ({id, name, sex, birth, location, introduction}) => post(`singer/save`, {
        id,
        name,
        sex,
        birth,
        location,
        introduction
    }),
    // 更新歌手信息
    // updateSingerMsg: ({id, name, sex, birth, location, introduction}) => post(`singer/update`, {
    //     id,
    //     name,
    //     sex,
    //     birth,
    //     location,
    //     introduction
    // }),
    // 删除歌手
    // deleteSinger: (id) => deletes( url: 'singer /delete?${id}`)，
    deleteSinger: (id) => deletes(`singer/delete/${id}`,{}),

    // =======================> 歌曲 API  完成
    // 返回所有歌曲
    getAllSong: () => get(`/song/list`),
    //歌曲分页查询
    getSongPage: ({singerId,currentPage,pageSize,keyword}) => post(`song/page`,
            {singerId,
                currentPage,
                pageSize,
                keyword}),
    // 返回指定歌手ID的歌曲
    getSongOfSingerId: (id) => get(`song/singer/detail?singerId=${id}`),
    // 返回的指定用户ID收藏列表
    getSongOfId: (id) => get(`song/detail?id=${id}`),
    // 返回指定歌手名的歌曲
    getSongOfSingerName: (id) => get(`song/singerName/detail?name=${id}`),
    // 更新歌曲信息
    updateSongMsg: ({id, singerId, name, introduction, lyric}) => post(`song/update`, {
        id,
        singerId,
        name,
        introduction,
        lyric
    }),
    //更新歌曲文件
    updateSongUrl: (id) => `${getBaseURL()}/song/url/update?id=${id}`,
    //更新歌曲图片
    updateSongImg: (id) => `${getBaseURL()}/song/img/update?id=${id}`,
    //更新歌词文件
    updateSongLrc: (id) => `${getBaseURL()}/song/lrc/update?id=${id}`,
    // 删除歌曲
    // deleteSong: (id) => deletes(`song/delete?id=${id}`),  7/2
    deleteSong: (id) => deletes(`song/delete/${id}`,{}),
    // =======================> 歌单 API 完成
    // 添加歌单
    setSongList: ({title, introduction, style}) => post(`songList/add`, {title, introduction, style}),
    // 获取全部歌单  2025.6.20更新方法名为getAllSongList，路径修改为songList/list
    // getAllSongList: () => get(`songList`),
    getAllSongList: () => get(`songList/list`),
    //分页查询歌单信息
    getPageSongList:({currentPage,pageSize,keyword}) => post(`songList/page`,{
        currentPage,
        pageSize,
        keyword
    }),
    // 更新歌单信息
    updateSongListMsg: ({id, title, introduction, style}) => post(`songList/update`, {id, title, introduction, style}),
    // 删除歌单
    deleteSongList: (id) =>  deletes(`songList/delete/${id}`,{}),

    // =======================> 歌单歌曲 API 完成
    // 给歌单添加歌曲
    setListSong: ({songId,songListId}) => post(`listSong/add`, {songId,songListId}),
    // 返回歌单里指定歌单ID的歌曲
    getListSongOfSongId: (songListId) => get(`listSong/detail?songListId=${songListId}`),
    // 删除歌单里的歌曲
    deleteListSong: (songId) => get(`listSong/delete?songId=${songId}`)

}

export {HttpManager}
