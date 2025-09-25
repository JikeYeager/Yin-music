export default {
  // state: {
  //   userId: "", // ID
  //   username: "", // 名字
  //   userPic: "", // 图片
  // },
  /* 7.24 修改user模块添加持久化（user.ts）*/
  state: {
    userId: localStorage.getItem('userId') || "", // 7.24 从localStorage初始化
    username: localStorage.getItem('username') || "",
    userPic: localStorage.getItem('userPic') || "",
  },
  getters: {
    userId: (state) => state.userId,
    username: (state) => state.username,
    userPic: (state) => state.userPic,
  },
  mutations: {
    setUserId: (state, userId) => {
      state.userId = userId;
      // 保存到localStorage
      if (userId) {
        localStorage.setItem('userId', userId);
      } else {
        localStorage.removeItem('userId');
      }
    },
    setUsername: (state, username) => {
      state.username = username;
      //
      if (username) {
        localStorage.setItem('username', username);
      } else {
        localStorage.removeItem('username');
      }
    },
    setUserPic: (state, userPic) => {
      state.userPic = userPic;
      //
      if (userPic) {
        localStorage.setItem('userPic', userPic);
      } else {
        localStorage.removeItem('userPic');
      }
    },
  },
};
