import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
/*router路由的作用就是用来映射前端页面和路径之间的关系的，和后端@RequestMapping功能差不多，
都是做路径映射*/
const routes: Array<RouteRecordRaw> = [
  {
    path: '/Home',   //页面请求路径
    component: () => import('@/views/Home.vue'),  //跳转到path对应的页面
    meta: { title: '自述文件' },  //页面标题
    //子路由，可以进行拼接
    children: [
      {
        path: '/Info',  //主页面
        component: () => import('@/views/InfoPage.vue'),
        meta: { title: 'Info' }
      },
      {
        path: '/song',
        component: () => import('@/views/SongPage.vue'),
        meta: { title: 'Song' }
      },
      {
        path: '/singer',
        component: () => import('@/views/SingerPage.vue'),
        meta: { title: 'Singer' }
      },
      {
        path: '/SongList',
        component: () => import('@/views/SongListPage.vue'),
        meta: { title: 'SongList' }
      },
      {
        path: '/ListSong',
        component: () => import('@/views/ListSongPage.vue'),
        meta: { title: 'ListSong' }
      },
      {
        path: '/Comment',
        component: () => import('@/views/CommentPage.vue'),
        meta: { title: 'Comment' }
      },
      {
        path: '/Consumer',
        component: () => import('@/views/ConsumerPage.vue'),
        meta: { title: 'Consumer' }
      },
      {
        path: '/Collect',
        component: () => import('@/views/CollectPage.vue'),
        meta: { title: 'Collect' }
      }
    ]
  },
  {
    path: '/',   //路径为空的话，也就是 http://localhost:8080/ 时去登录页面
    component: () => import('@/views/Login.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
