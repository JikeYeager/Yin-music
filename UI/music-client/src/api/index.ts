import { getBaseURL, get, post, deletes } from "./request";

const HttpManager = {
  // 获取图片信息
  attachImageUrl: (url) => url ? `${getBaseURL()}/${url}` : "https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png",
  // =======================> 用户 API 完成
  // 登录
  signIn: ({username,password}) => post(`consumer/login`, {username,password}),
  signInByemail: ({email,password})=>post(`consumer/email`, {email,password}),
  // 注册
  signUp: ({username,password,sex,phoneNum,email,birth,introduction,location}) => post(`consumer/add`, {username,password,sex,phoneNum,email,birth,introduction,location}),
  // 删除用户  与管理员端共用一个方法
  deleteUser: (id) => deletes(`consumer/delete/${id}`),
  // 更新用户信息
  updateUserMsg: ({id,username,sex,phoneNum,email,birth,introduction,location}) => post(`consumer/update`, {id,username,sex,phoneNum,email,birth,introduction,location}),
  updateUserPassword: ({id,username,oldPassword,password,confirmPassword}) => post(`consumer/updatePassword`, {id,username,oldPassword,password,confirmPassword}),
  // 返回指定ID的用户
  // getUserOfId: (id) => get(`user/detail?id=${id}`),
  getUserOfId: (id) => get(`consumer/detail/${id}`),
  // 更新用户头像          getBaseURL(): http://localhost:9999
  // uploadUrl: (userId) => `${getBaseURL()}/consumer/avatar/update?id=${userId}`,  因为我们在管理员端的时候就写过更新头像的方法了，所以这里我们不再写新的方法
  uploadUrl: (userId) => `${getBaseURL()}/consumer/avatar/${userId}`,

  // =======================> 歌单 API 完成
  // 获取全部歌单
  // getSongList: () => get("songList"),
  getAllSongList: () => get("songList/list"),  //7.17
  // 获取歌单类型  8.4
  // getSongListOfStyle: (style) => get(`songList/style/detail?style=${style}`),
  getSongListOfStyle:({style,pageSize,currentPage}) => post(`songList/style`,{style,pageSize,currentPage}),
  // 返回标题包含文字的歌单
  getSongListOfLikeTitle: (keywords) => get(`songList/likeTitle/detail?title=${keywords}`),
  // 返回歌单里指定歌单ID的歌曲
  // getSongOfSongListId: (songListId) => get(`listSong/detail?songListId=${songListId}`),
  getSongOfSongListId: (songListId) => get(`song/listSong/${songListId}`),

  // =======================> 歌手 API  完成
  // 返回所有歌手
  // getAllSinger: () => get("singer"),
  getAllSinger: () => get("singer/list"),  //7.17
  // 通过性别对歌手分类  8.5
  getSingerOfSex: ({sex,pageSize,currentPage}) => post(`singer/sex`,{sex,pageSize,currentPage}),

  // =======================> 收藏 API 完成
  // 返回的指定用户ID的收藏列表
  getCollectionOfUser: (userId) => get(`collect/detail?userId=${userId}`),
  // 添加收藏的歌曲 type: 0 代表歌曲， 1 代表歌单
  setCollection: ({userId,type,songId}) => post(`collect/add`,{userId,type,songId}),

  deleteCollection: (userId, songId) => deletes(`collect/delete/${userId}/${songId}`),

  isCollection: ({userId, type, songId}) => post(`collect/status`, {userId, type, songId}),

  // =======================> 评分 API 完成
  // 提交评分
  setRank: ({songListId,consumerId,score}) => post(`rankList/add`, {songListId,consumerId,score}),
  // 获取指定歌单的评分
  getRankOfSongListId: (songListId) => get(`rankList/avg?songListId=${songListId}`),
  // 获取指定用户的歌单评分 7.22
  getUserRank: (consumerId, songListId) => get(`/rankList/${consumerId}/${songListId}`),

  // =======================> 评论 API 完成
  // 添加评论
  setComment: ({userId,content,songId,songListId,type}) => post(`comment/add`, {userId,content,songId,songListId,type}),
  // 删除评论  7.26 把get方法换成delete方法规范一点
  deleteComment: (id) => deletes(`comment/delete/${id}`,{}),
  // 点赞
  setSupport: ({id,up}) => post(`comment/like`, {id,up}),
  // 返回所有评论  type= 1获取歌单评论，type=0获取歌曲评论
  getAllComment: (type, id) => {
    let url = "";
    if (type === 1) {
      url = `comment/songList/${id}`;
    } else if (type === 0) {
      url = `comment/song/${id}`;
    }
    return get(url);
  },

  // =======================> 歌曲 API
  // 返回指定歌曲ID的歌曲
  getSongOfId: (id) => get(`song/detail?id=${id}`),
  // 返回指定歌手ID的歌曲
  getSongOfSingerId: (id) => get(`song/singer/${id}`),
  // 返回指定歌手名的歌曲
  getSongOfSingerName: (keywords) => get(`song/singerName/detail?name=${keywords}`),
  // 下载音乐
  downloadMusic: (url) => get(url, { responseType: "blob" }),

  //======================> 点赞api的优化 避免有些是重复的点赞！新增数据表了得

  testAlreadySupport:({commentId,userId}) => post(`userSupport/test`, {commentId,userId}),

  deleteUserSupport:({commentId,userId}) => post(`userSupport/delete`, {commentId,userId}),

  insertUserSupport:({commentId,userId}) => post(`userSupport/insert`, {commentId,userId}),

  //======================> 搜索 api   8.3
  searchSong:(keyword) => get(`search/song/${keyword}`),
  SearchSongList:(keyword) => get(`search/songlist/${keyword}`),


  //获取所有的海报
  // getBannerList: () => get("banner/getAllBanner")
  getBannerList: () => get("banner/list")  // 7.17修改
};



export { HttpManager };
